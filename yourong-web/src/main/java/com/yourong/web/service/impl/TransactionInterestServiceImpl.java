package com.yourong.web.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.constant.Config;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.SinaPayEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.PayStatus;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeCode;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.repayment.manager.RepaymentManager;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.CalenderTransactionInterestDetail;
import com.yourong.core.tc.model.biz.TransactionForMemberCenter;
import com.yourong.core.tc.model.biz.TransactionInterestForCalendar;
import com.yourong.core.tc.model.biz.TransactionInterestForMember;
import com.yourong.core.tc.model.query.TransactionInterestQuery;
import com.yourong.web.dto.CalendarTransactionInterestDetailDto;
import com.yourong.web.dto.TransactionInterestDetailDto;
import com.yourong.web.dto.TransactionInterestForMemberDto;
import com.yourong.web.service.BaseService;
import com.yourong.web.service.TransactionInterestService;


/**
 * 交易本息业务类
 * @author Leon Ray
 * 2014年9月20日-下午6:39:24
 */
@Service
public class TransactionInterestServiceImpl extends BaseService implements TransactionInterestService {
    @Autowired
    private TransactionInterestManager transactionInterestManager;
    @Autowired
    private HostingCollectTradeManager hostingCollectTradeManager;
    @Autowired
    private SinaPayClient sinaPayClient;
    @Autowired
    private HostingPayTradeManager hostingPayTradeManager;
    @Autowired
    private TransactionManager myTransactionManager;
    @Autowired
    private BalanceManager balanceManager;
    @Autowired
    private CapitalInOutLogManager capitalInOutLogManager;
    @Autowired
    private ProjectManager projectManager;
    @Autowired
    private RepaymentManager repaymentManager;
    

	@Override
	public List<TransactionInterest> selectTransactionInterestByMemberID(
			Long memberID) {
		try {
			TransactionInterestQuery transactionQuery = new TransactionInterestQuery();
			transactionQuery.setMemberId(memberID);
			return transactionInterestManager.selectTransactionInterestsByQueryParams(transactionQuery);
		} catch (Exception e) {
			logger.error("通过会员id查询交易本息列表出异常，memberID=" + memberID);
		}
		return null;
	}


	@Override
	public ResultDO<?> afterHostingCollectTradeForPayInterestAndPrincipal(String tradeNo,
			String outTradeNo, String tradeStatus) {
		ResultDO<?> result = new ResultDO<Object>();
		try {
			result = transactionInterestManager.afterHostingCollectTradeForPayInterestAndPrincipal(tradeNo, outTradeNo, tradeStatus);
			if(result.isSuccess()) {
				//如果交易为交易成功状态
				if(TradeStatus.TRADE_FINISHED.name().equals(tradeStatus)) {
					// 创建平台收益权垫付代收交易
					HostingCollectTrade hostingCollectTrade = hostingCollectTradeManager.getByTradeNo(tradeNo);
					if(hostingCollectTrade!=null) {
						BigDecimal extraInterest = hostingCollectTrade.getPlatformAmount();
						//如果平台垫付总额大于0，则创建平台垫付代收还款交易
						if(extraInterest!=null &&extraInterest.doubleValue()>0) {
							// 判断有没有创建过平台收益权垫付代收交易
							if(hostingCollectTradeManager.haveHostingCollectTradeForPaltform(hostingCollectTrade.getId(), hostingCollectTrade.getTransactionInterestIds())) {
								logger.info(hostingCollectTrade.getTradeNo() + "已经创建了平台收益券代收");
								return result;
							}
							HostingCollectTrade collectTrade = new HostingCollectTrade();
							collectTrade.setPayMethod(SinaPayEnum.PAY_TYPE_BALANCE.getCode());
							collectTrade.setSourceId(hostingCollectTrade.getId());
							collectTrade.setTradeCloseTime(Config.sinaPayDefaultOrderCloseTime);
							collectTrade.setTradeStatus(SinaPayEnum.TRANSACTION_WAIT_PAY.getCode());
							collectTrade.setTradeTime(DateUtils.getCurrentDate());
							collectTrade.setPayStatus(PayStatus.PROCESSING.name());
							collectTrade.setAmount(extraInterest);
							collectTrade.setPayerId(Long.parseLong(Config.internalMemberId));
							collectTrade.setTradeNo(SerialNumberUtil.generateCollectTradeaNo());
							collectTrade.setType(TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType());
							collectTrade.setRemarks(hostingCollectTrade.getSourceId() + "平台代付收益券");
							collectTrade.setTransactionInterestIds(hostingCollectTrade.getTransactionInterestIds());
							if(hostingCollectTradeManager.insertSelective(collectTrade)>0) {
							sinaPayClient.createHostingCollectTrade(
									collectTrade.getTradeNo(), 
									TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getDesc(), 
									SerialNumberUtil.getBasicAccount(), 
									null, 
									AccountType.BASIC, 
									collectTrade.getAmount(), 
									IdType.EMAIL, 
									TradeCode.COLLECT_FROM_BORROWER,
									TypeEnum.COLLECT_TRADE_TYPE_NORMAL.getCode()
									);
//								if(result!=null && !result.isSuccess()) {
//									throw new Exception(result.getErrorMsg());
//								}
							}
						}
						//如果平台垫付总额为空或者小于0，则直接创建代付还本付息
						if(extraInterest==null || (extraInterest!=null &&extraInterest.doubleValue()==0)) {
							transactionInterestManager.processPayInterestAndPrincipal(tradeNo);
						}
					}
				}
			}
			return result;
		} catch (Exception e) {
			logger.error("代收完成后执行还本付息出错，tradeNo="+tradeNo, e);
			result.setSuccess(false);
			return result;
		}
	}


	@Override
	public ResultDO<TransactionInterest> getTransactionInterestsByTransactionId(ResultDO<TransactionForMemberCenter> resultMember,
			Long transactionId) {
		ResultDO<TransactionInterest> result = new ResultDO<TransactionInterest>();
		TransactionForMemberCenter transactionForMemberCenter = resultMember.getResult(); 
		try {
			TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
			transactionInterestQuery.setTransactionId(transactionId);
			List<TransactionInterest> transactionInterests= transactionInterestManager.selectTransactionInterestsByQueryParamsAndOverdue(transactionInterestQuery);
			
			transactionForMemberCenter.setTransactionInterests(transactionInterests);
        	Integer flag = 0;
        	/** 收益类型标记 0-正常，1-逾期，2-提前，3-既逾期又提前*/
            for(TransactionInterest transactionInterest :transactionInterests){
            	if(TypeEnum.REPAYMENT_TYPE_OVERDUE.getType()==transactionInterest.getPayType()&&0==flag){
            		flag=1;
            	}
            	if(TypeEnum.REPAYMENT_TYPE_PREREPAYMENT.getType()==transactionInterest.getPayType()&&0==flag
            			&&StatusEnum.TRANSACTION_INTEREST_ALL_PAYED.getStatus()==transactionInterest.getStatus()){
            		flag=2;
            	}
            	if(TypeEnum.REPAYMENT_TYPE_PREREPAYMENT.getType()==transactionInterest.getPayType()&&1==flag
            			&&StatusEnum.TRANSACTION_INTEREST_ALL_PAYED.getStatus()==transactionInterest.getStatus()){
            		flag=3;
            	}
            	if(TypeEnum.REPAYMENT_TYPE_PREREPAYMENT.getType()==transactionInterest.getPayType()
            			&&StatusEnum.TRANSACTION_INTEREST_ALL_PAYED.getStatus()==transactionInterest.getStatus()){
            		transactionInterest.setPayTime(transactionInterest.getTopayDate());
            	}
            }
           
            resultMember.getResult().setFlag(flag);
            Project project = projectManager.selectByPrimaryKey(transactionForMemberCenter.getProjectId());
            
            	if(project.isDirectProject()){
		 			transactionForMemberCenter.setProfitPeriod(project.getProfitPeriod());
					transactionForMemberCenter.setInterestFrom(project.getInterestFrom());
				}else{
					transactionForMemberCenter.setProfitPeriod(transactionForMemberCenter.getTotalDays()+"天");
				}
				//取交易本息实收利息
				transactionForMemberCenter.setTotalInterest(transactionForMemberCenter.getTotalRealPayInterest());
				
				/*if(StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()==project.getStatus()&&1==project.getPrepayment()){
					Map<String,Object> map = Maps.newHashMap();
					map.put("projectId", transactionForMemberCenter.getProjectId());
					map.put("transactionId", transactionForMemberCenter.getTransactionId());
					map.put("memberId", transactionForMemberCenter.getMemberId());
					TransactionInterest transactionInterest = transactionInterestManager.getPreTransactionInterest(map);
					transactionForMemberCenter.setProfitPeriod(DateUtils.daysOfTwo(project.getStartDate(),transactionInterest.getPayTime())+"天");
				}*/
	            
			
			result.setResultList(transactionInterests);
			return result;
		} catch (Exception e) {
			logger.error("通过交易id获取交易本息信息出错，transactionId="+transactionId, e);
			result.setSuccess(false);
			return result;
		}
	}


	@Override
	public ResultDO<TransactionInterestForCalendar> getInterestCalendar(
			Long memberId) {
		ResultDO<TransactionInterestForCalendar> result = new ResultDO<TransactionInterestForCalendar>();
		try {
			List<TransactionInterest> transactionInterests= this.selectTransactionInterestByMemberID(memberId);
			//将TransactionInterest转换成TransactionInterestForCalendar
			if(Collections3.isNotEmpty(transactionInterests)) {
				List<TransactionInterestForCalendar> transactionInterestForCalendars = Lists.newArrayList();
				Map<String, TransactionInterestForCalendar> map = Maps.newHashMap();
				for (TransactionInterest transactionInterest : transactionInterests) {
					this.getInterestCalendarDetail(transactionInterest, map, memberId);
				}
				for (Entry<String, TransactionInterestForCalendar> key : map.entrySet()) {
					transactionInterestForCalendars.add(key.getValue());
				}
				result.setResultList(transactionInterestForCalendars);
			}
			return result;
		} catch (Exception e) {
			logger.error("获取收益日历中本息记录出错，memberId="+memberId, e);
			result.setSuccess(false);
			return result;
		}
	}
	
	private void getInterestCalendarDetail(TransactionInterest transactionInterest,Map<String, TransactionInterestForCalendar> map,Long memberId){
		if(transactionInterest.getPayType()==TypeEnum.REPAYMENT_TYPE_PREREPAYMENT.getType()&&
				transactionInterest.getStatus()==1){//处理提前还款类型
			String preDateStr = DateUtils.formatDatetoString(transactionInterest.getEndDate(), DateUtils.DATE_FMT_3);
			if(map.containsKey(preDateStr)){
				TransactionInterestForCalendar transactionInterestForCalendar = map.get(preDateStr);
				//transactionInterestForCalendar.setTotalPayableInterest(transactionInterestForCalendar.getTotalPayableInterest().add(transactionInterest.getRealPayInterest()));
				//transactionInterestForCalendar.setTotalPayablePrincipal(transactionInterestForCalendar.getTotalPayablePrincipal().add(transactionInterest.getRealPayPrincipal()));
				//transactionInterestForCalendar.setNum(transactionInterestForCalendar.getNum()+1);
				transactionInterestForCalendar.setPreNum(transactionInterestForCalendar.getPreNum()+1);
			} else {
				TransactionInterestForCalendar transactionInterestForCalendar = new TransactionInterestForCalendar();
				transactionInterestForCalendar.setMemberId(memberId);
				transactionInterestForCalendar.setNum(0);
				transactionInterestForCalendar.setNoNum(0);
				transactionInterestForCalendar.setPreNum(1);
				transactionInterestForCalendar.setOverNum(0);
				transactionInterestForCalendar.setOverNoPayNum(0);
				transactionInterestForCalendar.setPayDate(preDateStr);
				transactionInterestForCalendar.setTotalPayableInterest(BigDecimal.ZERO);
				transactionInterestForCalendar.setTotalPayablePrincipal(BigDecimal.ZERO);
				//transactionInterestForCalendar.setTotalPayableInterest(transactionInterest.getRealPayInterest());
				//transactionInterestForCalendar.setTotalPayablePrincipal(transactionInterest.getRealPayPrincipal());
				map.put(preDateStr, transactionInterestForCalendar);
			}
			
		}
		//  逾期前用end_date，逾期后用payTime
		if(transactionInterest.getPayType()==TypeEnum.REPAYMENT_TYPE_OVERDUE.getType()&&transactionInterest.getStatus()!=1&&transactionInterest.getStatus()!=5){//处理逾期未还款类型
			String overEndDateStr = DateUtils.formatDatetoString(transactionInterest.getEndDate(), DateUtils.DATE_FMT_3);
			if(map.containsKey(overEndDateStr)){
				TransactionInterestForCalendar transactionInterestForCalendar = map.get(overEndDateStr);
				//transactionInterestForCalendar.setTotalPayableInterest(transactionInterestForCalendar.getTotalPayableInterest().add(transactionInterest.getRealPayInterest()));
				//transactionInterestForCalendar.setTotalPayablePrincipal(transactionInterestForCalendar.getTotalPayablePrincipal().add(transactionInterest.getRealPayPrincipal()));
				//transactionInterestForCalendar.setNum(transactionInterestForCalendar.getNum()+1);
				transactionInterestForCalendar.setOverNoPayNum(transactionInterestForCalendar.getOverNoPayNum()+1);
			} else {
				TransactionInterestForCalendar transactionInterestForCalendar = new TransactionInterestForCalendar();
				transactionInterestForCalendar.setMemberId(memberId);
				transactionInterestForCalendar.setNum(0);
				transactionInterestForCalendar.setNoNum(0);
				transactionInterestForCalendar.setPreNum(0);
				transactionInterestForCalendar.setOverNum(0);
				transactionInterestForCalendar.setOverNoPayNum(1);
				transactionInterestForCalendar.setPayDate(overEndDateStr);
				transactionInterestForCalendar.setTotalPayableInterest(BigDecimal.ZERO);
				transactionInterestForCalendar.setTotalPayablePrincipal(BigDecimal.ZERO);
				//transactionInterestForCalendar.setTotalPayableInterest(transactionInterest.getRealPayInterest());
				//transactionInterestForCalendar.setTotalPayablePrincipal(transactionInterest.getRealPayPrincipal());
				map.put(overEndDateStr, transactionInterestForCalendar);
			}
		}
		
		//  逾期前用end_date，逾期后用payTime
		if(transactionInterest.getPayType()==TypeEnum.REPAYMENT_TYPE_OVERDUE.getType()&&transactionInterest.getStatus()==1){//处理逾期还款类型
			String overDateStr = DateUtils.formatDatetoString(transactionInterest.getEndDate(), DateUtils.DATE_FMT_3);
			if(map.containsKey(overDateStr)){
				TransactionInterestForCalendar transactionInterestForCalendar = map.get(overDateStr);
			    //transactionInterestForCalendar.setTotalPayableInterest(transactionInterestForCalendar.getTotalPayableInterest().add(transactionInterest.getRealPayInterest()));
				//transactionInterestForCalendar.setTotalPayablePrincipal(transactionInterestForCalendar.getTotalPayablePrincipal().add(transactionInterest.getRealPayPrincipal()));
				//transactionInterestForCalendar.setNum(transactionInterestForCalendar.getNum()+1);
				transactionInterestForCalendar.setOverNum(transactionInterestForCalendar.getOverNum()+1);
			} else {
				TransactionInterestForCalendar transactionInterestForCalendar = new TransactionInterestForCalendar();
				transactionInterestForCalendar.setMemberId(memberId);
				transactionInterestForCalendar.setNum(0);
				transactionInterestForCalendar.setNoNum(0);
				transactionInterestForCalendar.setPreNum(0);
				transactionInterestForCalendar.setOverNum(1);
				transactionInterestForCalendar.setOverNoPayNum(0);
				transactionInterestForCalendar.setPayDate(overDateStr);
				transactionInterestForCalendar.setTotalPayableInterest(BigDecimal.ZERO);
				transactionInterestForCalendar.setTotalPayablePrincipal(BigDecimal.ZERO);
				//transactionInterestForCalendar.setTotalPayableInterest(transactionInterest.getRealPayInterest());
				//transactionInterestForCalendar.setTotalPayablePrincipal(transactionInterest.getRealPayPrincipal());
				map.put(overDateStr, transactionInterestForCalendar);
			}
		}
		
		//正常已还 或者垫资已还
		if((transactionInterest.getPayType()==TypeEnum.REPAYMENT_TYPE_NORMAL.getType()||transactionInterest.getPayType()==TypeEnum.REPAYMENT_TYPE_LOANREPAYMENT.getType())
				&&transactionInterest.getStatus()==1){
			String endDateStr = DateUtils.formatDatetoString(transactionInterest.getEndDate(), DateUtils.DATE_FMT_3);
			if(map.containsKey(endDateStr)) {
				TransactionInterestForCalendar transactionInterestForCalendar = map.get(endDateStr);
				transactionInterestForCalendar.setTotalPayableInterest(transactionInterestForCalendar.getTotalPayableInterest().add(transactionInterest.getPayableInterest()));
				transactionInterestForCalendar.setTotalPayablePrincipal(transactionInterestForCalendar.getTotalPayablePrincipal().add(transactionInterest.getPayablePrincipal()));
				transactionInterestForCalendar.setNum(transactionInterestForCalendar.getNum()+1);
			} else {
				TransactionInterestForCalendar transactionInterestForCalendar = new TransactionInterestForCalendar();
				transactionInterestForCalendar.setMemberId(memberId);
				transactionInterestForCalendar.setNum(1);
				transactionInterestForCalendar.setNoNum(0);
				transactionInterestForCalendar.setPreNum(0);
				transactionInterestForCalendar.setOverNum(0);
				transactionInterestForCalendar.setOverNoPayNum(0);
				transactionInterestForCalendar.setPayDate(endDateStr);
				transactionInterestForCalendar.setTotalPayableInterest(transactionInterest.getPayableInterest());
				transactionInterestForCalendar.setTotalPayablePrincipal(transactionInterest.getPayablePrincipal());
				map.put(endDateStr, transactionInterestForCalendar);
			}
		}
		
		//正常未还  或者垫资未还
		if((transactionInterest.getPayType()==TypeEnum.REPAYMENT_TYPE_NORMAL.getType()||transactionInterest.getPayType()==TypeEnum.REPAYMENT_TYPE_LOANREPAYMENT.getType())
				&&transactionInterest.getStatus()!=1&&transactionInterest.getStatus()!=5){
			String endDateStr = DateUtils.formatDatetoString(transactionInterest.getEndDate(), DateUtils.DATE_FMT_3);
			if(map.containsKey(endDateStr)) {
				TransactionInterestForCalendar transactionInterestForCalendar = map.get(endDateStr);
				//transactionInterestForCalendar.setTotalPayableInterest(transactionInterestForCalendar.getTotalPayableInterest().add(transactionInterest.getPayableInterest()));
				//transactionInterestForCalendar.setTotalPayablePrincipal(transactionInterestForCalendar.getTotalPayablePrincipal().add(transactionInterest.getPayablePrincipal()));
				transactionInterestForCalendar.setNoNum(transactionInterestForCalendar.getNoNum()+1);
			} else {
				TransactionInterestForCalendar transactionInterestForCalendar = new TransactionInterestForCalendar();
				transactionInterestForCalendar.setMemberId(memberId);
				transactionInterestForCalendar.setNum(0);
				transactionInterestForCalendar.setNoNum(1);
				transactionInterestForCalendar.setPreNum(0);
				transactionInterestForCalendar.setOverNum(0);
				transactionInterestForCalendar.setOverNoPayNum(0);
				transactionInterestForCalendar.setPayDate(endDateStr);
				transactionInterestForCalendar.setTotalPayableInterest(transactionInterest.getPayableInterest());
				transactionInterestForCalendar.setTotalPayablePrincipal(transactionInterest.getPayablePrincipal());
				map.put(endDateStr, transactionInterestForCalendar);
			}
		}
	}
	


	@Override
	public ResultDO<?> afterHostingCollectTradeCouponBForPayInterestAndPrincipal(
			String tradeNo, String outTradeNo, String tradeStatus) {
		ResultDO<?> result = new ResultDO();
		try {
			result = transactionInterestManager.afterHostingCollectTradeCouponBForPayInterestAndPrincipal(tradeNo, outTradeNo, tradeStatus);
			if(result.isSuccess()) {
				if(TradeStatus.TRADE_FINISHED.name().equals(tradeStatus)) {
					HostingCollectTrade hostingCollectTrade = hostingCollectTradeManager.getByTradeNo(tradeNo);
					if(hostingCollectTrade!=null) {
						transactionInterestManager.processPayInterestAndPrincipal(hostingCollectTradeManager.getById(hostingCollectTrade.getSourceId()).getTradeNo());
					}
				}
			}
			return result;
		} catch (Exception e) {
			logger.error("平台收益权垫付代收回调发生异常，tradeNo：" +tradeNo, e);
			result.setSuccess(false);
			return result;
		}
	}


	@Override
	public ResultDO getMemberInterestDetail(Long memberId, String endDate) {
		ResultDO<TransactionInterestDetailDto> result = new ResultDO<TransactionInterestDetailDto>();
		if(StringUtil.isBlank(endDate)){
			result.setSuccess(false);
		}
		try {
			TransactionInterestDetailDto detailDto = new TransactionInterestDetailDto();
			CalendarTransactionInterestDetailDto calendarTransactionInterestDetailDto = new CalendarTransactionInterestDetailDto();
			Map<String, Object> map = Maps.newHashMap();
			map.put("memberId",memberId);
			map.put("endDate",endDate);
			
			List<CalenderTransactionInterestDetail> calenderTransactionInterestDetailLists= transactionInterestManager.getMemberInterestDetailByDateNew(map);
			BigDecimal totalAmount = BigDecimal.ZERO;
			BigDecimal topayTotalAmount = BigDecimal.ZERO;
			BigDecimal repaidTotalAmount = BigDecimal.ZERO;
			for(CalenderTransactionInterestDetail cal: calenderTransactionInterestDetailLists){
				
				if(cal.getStatus()==1){
					totalAmount = totalAmount.add(cal.getRealPayInterest()).add(cal.getRealPayPrincipal());
					repaidTotalAmount = repaidTotalAmount.add(cal.getRealPayInterest()).add(cal.getRealPayPrincipal());
					if(cal.getPayType()==TypeEnum.REPAYMENT_TYPE_PREREPAYMENT.getType()){
						cal.setPayTime(cal.getTopayDate());
					}
				}else{
					totalAmount = totalAmount.add(cal.getPayableInterest()).add(cal.getPayablePrincipal());
					topayTotalAmount = topayTotalAmount.add(cal.getPayableInterest()).add(cal.getPayablePrincipal());
					if(TypeEnum.TRANSACTION_INTEREST_PAY_TYPE_OVERDUE.getType()==cal.getPayType()){
						Integer days = DateUtils.daysBetween(cal.getEndDate(), DateUtils.getCurrentDate());
						cal.setOverDays(days);
					}
				}
				
				
			}
			totalAmount = totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
			topayTotalAmount = topayTotalAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
			repaidTotalAmount = repaidTotalAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
			calendarTransactionInterestDetailDto.setTotalAmount(totalAmount);
			calendarTransactionInterestDetailDto.setTopayTotalAmount(topayTotalAmount);
			calendarTransactionInterestDetailDto.setRepaidTotalAmount(repaidTotalAmount);
			calendarTransactionInterestDetailDto.setNum(calenderTransactionInterestDetailLists.size());
			calendarTransactionInterestDetailDto.setTransactionInterestList(calenderTransactionInterestDetailLists);
			detailDto.setCalendarTransactionInterestDetailDto(calendarTransactionInterestDetailDto);
			result.setResult(detailDto);
		} catch (ManagerException e) {
			logger.error("根据日期获得用户还本付息详情发生异常" , e);
			result.setSuccess(false);
		}
		return result;
	}


	@Override
	public ResultDO getTransactionInterestDetailForMember(TransactionInterestQuery query) {
		ResultDO result = new ResultDO();
		try {
			//List<TransactionInterestForMember> detailList = transactionInterestManager.getTransactionInterestDetailForMember(query);
			List<TransactionInterestForMember> detailList = transactionInterestManager.getTransactionInterestDetailForMemberFilterP2P(query);
			if(Collections3.isNotEmpty(detailList)){
				List<TransactionInterestForMemberDto> detailDtoList =BeanCopyUtil.mapList(detailList, TransactionInterestForMemberDto.class);
				result.setResult(detailDtoList);
			}
		} catch (ManagerException e) {
			logger.error("查看用户还本付息明细发生异常" , e);
			result.setSuccess(false);
		}
		return result;
	}
	
}