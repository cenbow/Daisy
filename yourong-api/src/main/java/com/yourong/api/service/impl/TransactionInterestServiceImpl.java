package com.yourong.api.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.api.dto.CalendarTransactionInterestDetailAPPDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.dto.TransactionInterestDto;
import com.yourong.api.service.TransactionInterestService;
import com.yourong.common.constant.Config;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.SinaPayEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
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
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.CalenderTransactionInterestDetail;
import com.yourong.core.tc.model.biz.TransactionInterestDetail;
import com.yourong.core.tc.model.biz.TransactionInterestDetailForCalendar;
import com.yourong.core.tc.model.biz.TransactionInterestForCalendar;
import com.yourong.core.tc.model.biz.TransactionInterestForMember;
import com.yourong.core.tc.model.query.TransactionInterestQuery;


/**
 * 交易本息业务类
 * @author Leon Ray
 * 2014年9月20日-下午6:39:24
 */
@Service
public class TransactionInterestServiceImpl  implements TransactionInterestService {
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

	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());


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
	public ResultDTO getTransactionInterestsByTransactionId(
			Long transactionId) {
		ResultDTO result = new ResultDTO();
		try {
			TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
			transactionInterestQuery.setTransactionId(transactionId);
			List<TransactionInterest> transactionInterests= transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
			List<TransactionInterestDto> list = BeanCopyUtil.mapList(transactionInterests, TransactionInterestDto.class);
			result.setResult(list);
			return result;
		} catch (Exception e) {
			logger.error("通过交易id获取交易本息信息出错，transactionId="+transactionId, e);
			result.setResultCode(ResultCode.ERROR);
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
						transactionInterestForCalendar.setPayDate(endDateStr);
						transactionInterestForCalendar.setTotalPayableInterest(transactionInterest.getPayableInterest());
						transactionInterestForCalendar.setTotalPayablePrincipal(transactionInterest.getPayablePrincipal());
						map.put(endDateStr, transactionInterestForCalendar);
					}
					
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
	public Page<TransactionInterestForMember> queryTransactionInterestForMember(
			TransactionInterestQuery query) {
		try {
			return transactionInterestManager.queryTransactionInterestForMember(query);
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		return new Page<TransactionInterestForMember>();
	}

	@Override
	public ResultDO<TransactionInterestDetail> getMemberInterestDetail(Long memberId, String endDate) {
		ResultDO<TransactionInterestDetail> result = new ResultDO<TransactionInterestDetail>();
		if (StringUtil.isBlank(endDate)) {
			result.setSuccess(false);
		}
		try {
			// TODO 根据memberId+endDate缓存（endDate < 当前时间）
			Map<String, Object> map = Maps.newHashMap();
			map.put("memberId",memberId);
			map.put("endDate",endDate);
			List<TransactionInterestDetailForCalendar> detailList = transactionInterestManager.getMemberInterestDetailByDate(map);
			if (Collections3.isNotEmpty(detailList)) {
				TransactionInterestDetail detailDto = new TransactionInterestDetail();
				BigDecimal totalAmount = new BigDecimal(0);
				for (TransactionInterestDetailForCalendar detail : detailList) {
					Integer num = transactionInterestManager.getTotalInterestNum(memberId, detail.getTransactionId());
					if (num <= 1) {
						detail.setCurrentNum(1);
						detail.setTotalNum(1);
					} else {
						int cnum = transactionInterestManager.getCurrentInterestNum(memberId, detail.getTransactionId(), endDate);
						detail.setCurrentNum(cnum);
						detail.setTotalNum(num);
					}
					totalAmount = totalAmount.add(detail.getTotalPayableInterest());
					totalAmount = totalAmount.add(detail.getTotalPayablePrincipal());
				}
				detailDto.setDetailList(detailList);
				detailDto.setTotalAmount(totalAmount);
				detailDto.setNum(detailList.size());
				result.setResult(detailDto);
			}
		} catch (ManagerException e) {
			logger.error("根据日期获得用户还本付息详情发生异常", e);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return result;
	}
	
	@Override
	public ResultDO<CalendarTransactionInterestDetailAPPDto> getMemberInterestDetailNew(Long memberId, String endDate) {
		ResultDO<CalendarTransactionInterestDetailAPPDto> result = new ResultDO<CalendarTransactionInterestDetailAPPDto>();
		if (StringUtil.isBlank(endDate)) {
			result.setSuccess(false);
		}
		try {
			// TODO 根据memberId+endDate缓存（endDate < 当前时间）
			Map<String, Object> map = Maps.newHashMap();
			map.put("memberId",memberId);
			map.put("endDate",endDate);
			List<CalenderTransactionInterestDetail> calenderTransactionInterestDetail = transactionInterestManager.getMemberInterestDetailByDateNew(map);
			if (Collections3.isNotEmpty(calenderTransactionInterestDetail)) {
				CalendarTransactionInterestDetailAPPDto calendarTransactionInterestDetailAPPDto = new CalendarTransactionInterestDetailAPPDto();
				BigDecimal totalAmount = new BigDecimal(0);
				for (CalenderTransactionInterestDetail cal : calenderTransactionInterestDetail) {
					
					if(cal.getStatus()==1){
						cal.setRealPayInterest(cal.getRealPayInterest());
						cal.setRealPayPrincipal(cal.getRealPayPrincipal());
						cal.setPayTime(cal.getPayTime());
						totalAmount = totalAmount.add(cal.getRealPayInterest()).add(cal.getRealPayPrincipal());
					}else{
						cal.setRealPayInterest(cal.getPayableInterest());
						cal.setRealPayPrincipal(cal.getPayablePrincipal());
						cal.setPayTime(cal.getEndDate());
						totalAmount = totalAmount.add(cal.getPayableInterest()).add(cal.getPayablePrincipal());
						if(TypeEnum.TRANSACTION_INTEREST_PAY_TYPE_OVERDUE.getType()==cal.getPayType()){
							Integer days = DateUtils.daysBetween(cal.getEndDate(), DateUtils.getCurrentDate());
							cal.setOverDays(days);
						}
					}
				}
				calendarTransactionInterestDetailAPPDto.setCalenderTransactionInterestDetail(calenderTransactionInterestDetail);
				calendarTransactionInterestDetailAPPDto.setTotalAmount(totalAmount);
				calendarTransactionInterestDetailAPPDto.setNum(calenderTransactionInterestDetail.size());
				result.setResult(calendarTransactionInterestDetailAPPDto);
			}
		} catch (ManagerException e) {
			logger.error("根据日期获得重构后用户还本付息详情，发生异常", e);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return result;
	}

}