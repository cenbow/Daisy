package com.yourong.core.repayment.manager.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.constant.Config;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.SinaPayEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.PayStatus;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeCode;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.CreateCollectTradeResult;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.OverdueLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.repayment.manager.CollectCreditorPrincipalAndInterestManager;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.biz.TransactionInterestForPay;
import com.yourong.core.uc.manager.ThirdCompanyManager;
import com.yourong.core.uc.model.ThirdCompany;

/**
 * 代收债权人 的本金利息， 还有平台贴补的利息
 * Created by py on 2015/7/21.
 */
@Component
public class CollectCreditorPrincipalAndInterestManagerImpl  implements CollectCreditorPrincipalAndInterestManager {
    @Resource
    private TransactionInterestManager transactionInterestManager;

    @Resource
    private HostingCollectTradeManager hostingCollectTradeManager;

    @Resource
    private SinaPayClient sinaPayClient;

    @Autowired
    private OverdueLogManager overdueLogManager;

    @Autowired
    private ThirdCompanyManager thirdCompanyManager;

	@Autowired
	private SysDictManager sysDictManager;
	
	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private ProjectManager projectManager;

    private  Logger logger = LoggerFactory.getLogger(CollectCreditorPrincipalAndInterestManagerImpl.class);

    /**
     * 代收本地原始债权人和平台本息金额
     */
    @Override
    public  List<TransactionInterestForPay> createHostingCollectTradeForPayInterestAndPrincipal() {
        try {
        	//1.正常还款的本息记录
            List<TransactionInterestForPay> transactionInterestForPays = transactionInterestManager.selectAllProjectToBePaidTransactionInterests();
    //    	List<TransactionInterestForPay> transactionInterestForPays = Lists.newArrayList();
            if(Collections3.isEmpty(transactionInterestForPays)) {
            	logger.warn("[还本付息]-[创建代收]--今日无正常还款的项目");
            } 
//            else {
//            	logger.warn("[还本付息]-[创建代收]--今日正常还款的项目总数为：{}", transactionInterestForPays.size());
//            }
            //2.提前还款
            List<TransactionInterestForPay> transactionInterestForPrePays = transactionInterestManager.selectAllProjectToBePrePaidTransactionInterests();
            if(Collections3.isEmpty(transactionInterestForPrePays)) {
            	logger.warn("[还本付息]-[创建代收]--今日无提前还款的项目");
            } 
//            else {
//            	logger.warn("[还本付息]-[创建代收]--今日提前还款的项目总数为：{}", transactionInterestForPrePays.size());
//            }
            transactionInterestForPays.addAll(transactionInterestForPrePays);
            //3.逾期还款
            List<TransactionInterestForPay> transactionInterestForOverPays = transactionInterestManager.selectAllProjectToBeOverPaidTransactionInterests();
            if(Collections3.isEmpty(transactionInterestForOverPays)) {
            	logger.warn("[还本付息]-[创建代收]--今日无逾期还款的项目");
            } 
//            else {
//            	logger.warn("[还本付息]-[创建代收]--今日逾期还款的项目总数为：{}", transactionInterestForOverPays.size());
//            }
            transactionInterestForPays.addAll(transactionInterestForOverPays);
            
            //计算每个项目 债权人要还的本息 和 平台垫付的利息
            List<TransactionInterestForPay> transactionInterestForPayLists = buildTransactionInterestForPayLists(transactionInterestForPays);
            if (Collections3.isEmpty(transactionInterestForPayLists)) {
            	logger.warn("[还本付息]-[创建代收]--今日无还款的项目");
                return Lists.newArrayList();
            }
            //待收交易号
            //  List<String> tranNos = Lists.newArrayList();
            for (TransactionInterestForPay transactionInterestForPay : transactionInterestForPayLists) {
                try {
                        //插入代收表， 债权人应付的本息
                        ResultDto<CreateCollectTradeResult> result = insertCreditorHostingCollectTrade(transactionInterestForPay);
                        if (result != null && !result.isSuccess()) {
                            logger.info("[还本付息]-[创建代收]--向债权人或者借款人发起代收失败: projectId={}", transactionInterestForPay.getProjectId());
//                            collectTradeError(result);
                        }
                        //插入代收表， 平台应付的利息
                        result = insertPlatformHostingCollectTrade(transactionInterestForPay);
                        if (result != null && !result.isSuccess()) {
                            logger.info("[还本付息]-[创建代收]--向平台发起代收失败: projectId={}",transactionInterestForPay.getProjectId());
//                            collectTradeError(result);
                        }
                    } catch (Exception e) {
                        logger.error("[还本付息]-[创建代收]--代收原始债权人和平台本息金额发生异常", e);
                    }
            }
            return transactionInterestForPayLists;
        } catch (Exception e) {
            logger.error("[还本付息]-[创建代收]--代收原始债权人和平台本息金额发生异常", e);
        }
        return  Lists.newArrayList();
    }



//    /**
//     * 代收发起后，记录交易号
//     * @param result
//     * @throws com.yourong.common.exception.ManagerException
//     */
//    private void collectTradeSucess(ResultDto<CreateCollectTradeResult> result,List<String> tradeNos) throws ManagerException {
//        if (result.isSuccess() &&result.getModule()==null ){
//            return;
//        }
//        String tradeNo = result.getModule().getTradeNo();
//        tradeNos.add(tr)
//    }
    /**
     * 代收失败
     * @param result
     * @throws com.yourong.common.exception.ManagerException
     */
    @SuppressWarnings("unused")
	private void collectTradeError(ResultDto<CreateCollectTradeResult> result) throws ManagerException {
        if (result.isError() &&result.getModule()==null ){
            return;
        }
        String tradeNo = result.getModule().getTradeNo();
        String error = result.getErrorMsg();
        if (result.getModule().getPayStatus().equals(PayStatus.FAILED.name())){
            String remak = tradeNo + ":交易失败,失败原因:" + error;
            //将代收记录置为失败
            hostingCollectTradeManager.updateTradeStatus(
                    hostingCollectTradeManager.getByTradeNo(tradeNo),
                    SinaPayEnum.TRANSACTION_TRADE_FAILED.getCode(),
                    remak
            );
        }
    }


    /**
     *	计算每个项目 债权人要还的本息 和 平台垫付的利息
     * @param transactionInterestForPays
     * @return
     */
    private List<TransactionInterestForPay> buildTransactionInterestForPayLists( List<TransactionInterestForPay> transactionInterestForPays) throws Exception{
        if(Collections3.isNotEmpty(transactionInterestForPays)) {
            Map<Long,List<TransactionInterestForPay>> transactionInterestForPayMaps = Maps.newHashMap();
            //按照项目来分组
            for (TransactionInterestForPay transactionInterestForPay : transactionInterestForPays) {
                Long projectId = transactionInterestForPay.getProjectId();
                transactionInterestForPay.setIsAdvances(Boolean.FALSE);
                // 一般情况 借款人不可能不存在，如果存在 应该是脏数据
                Long payerId = transactionInterestForPay.getPayerId();
                if (payerId == null){
                    logger.warn("[还本付息]-[创建代收]--项目：projectId={},借款人不存在",projectId);
                    continue;
                }
				// 渠道商项目借款人取固定账户
//				Project project = projectManager.selectByPrimaryKey(projectId);
//				if (project == null) {
//					logger.warn("[还本付息]-[创建代收]--项目：projectId={},项目不能存在", projectId);
//					continue;
//				}
				if (StringUtil.isNotBlank(transactionInterestForPay.getOpenPlatformKey())) {
					// 从字典表找对应渠道合作商资金账户
					SysDict dict = sysDictManager.findByGroupNameAndKey("channel_business", transactionInterestForPay.getOpenPlatformKey());
					if (dict != null && StringUtil.isNotBlank(dict.getValue()) && StringUtil.isNumeric(dict.getValue())) {
						payerId = Long.valueOf(dict.getValue());
						transactionInterestForPay.setPayerId(payerId);
					}
				}
                //查询该项目 借款是否之前逾期
                int i = overdueLogManager.countOverdueRecordByProjectId(projectId);
                //如果改项目有逾期记录。 则不进行还本付息
                // 查询第三方垫资
                ThirdCompany thirdCompanyId = thirdCompanyManager.getThirdCompanyId(transactionInterestForPay.getInterestId(),projectId);
                if (i > 0  &&  thirdCompanyId == null ) {
                    logger.info("[还本付息]-[创建代收]--项目：projectId={},项目有逾期记录, 则不进行还本付息",projectId);
                    continue;
                }
                if (thirdCompanyId != null){
                    payerId = thirdCompanyId.getMemberId();
                    transactionInterestForPay.setPayerId(payerId);
                    transactionInterestForPay.setIsAdvances(Boolean.TRUE);
                    logger.info("[还本付息]-[创建代收]--项目：projectId={},该项目 由第三方垫付，垫付人ID{}",projectId,payerId);
                }


                List<TransactionInterestForPay> transactionInterestForPayLists  = Lists.newArrayList();
                transactionInterestForPayLists.add(transactionInterestForPay);
                if(transactionInterestForPayMaps.containsKey(projectId)) {
                    transactionInterestForPayMaps.get(projectId).add(transactionInterestForPay);
                } else {
                    transactionInterestForPayMaps.put(projectId, transactionInterestForPayLists);
                }
            }
            List<TransactionInterestForPay> transactionInterestForPayLists  = Lists.newArrayList();
            if(transactionInterestForPayMaps!=null && transactionInterestForPayMaps.size()>0) {
                for (Long projectId : transactionInterestForPayMaps.keySet()) {
                	logger.info("[还本付息]-[创建代收]--项目：projectId={},开始计算原始债权人还款代收金额和平台垫付收益券代收金额",projectId);
                    List<TransactionInterestForPay> transactionInterestForPayList = transactionInterestForPayMaps.get(projectId);
                    if(Collections3.isNotEmpty(transactionInterestForPayList)) {
                        //原始债权人还款总额
                        BigDecimal totalPayInterestAndPrincipalForLender = BigDecimal.ZERO;
                        //平台垫付收益券总额
                        BigDecimal totalPayInterestAndPrincipalForPlatform = BigDecimal.ZERO;
                        StringBuffer transactionInterestIds = new StringBuffer();
                        for (TransactionInterestForPay transactionInterestForPay : transactionInterestForPayList) {
                            // 应付利息 只是针对投资者，  原始债权人还款总额 应该要减去使用收益劵产生的利息
                            //平台垫付  项目加息总额
                        	totalPayInterestAndPrincipalForPlatform = totalPayInterestAndPrincipalForPlatform.add(transactionInterestForPay.getExtraProjectInterest());
                        	//平台垫付收益券总额
                            totalPayInterestAndPrincipalForPlatform = totalPayInterestAndPrincipalForPlatform.add(transactionInterestForPay.getExtraInterest());
                            //原始债权人要还利息    应该要减去使用收益劵产生的利息（收益劵是平台代付、项目加息也是平台代付）
                            BigDecimal bigDecimal = transactionInterestForPay.getPayableInterest().subtract(transactionInterestForPay.getExtraInterest())
                            		.subtract(transactionInterestForPay.getExtraProjectInterest());
                            totalPayInterestAndPrincipalForLender = totalPayInterestAndPrincipalForLender.add(bigDecimal);
                            //原始债权人要还本金
                            totalPayInterestAndPrincipalForLender = totalPayInterestAndPrincipalForLender.add(transactionInterestForPay.getPayablePrincipal());
                            //原始债权人要还的滞纳金
                            totalPayInterestAndPrincipalForLender = totalPayInterestAndPrincipalForLender.add(transactionInterestForPay.getOverdueFine());
                            transactionInterestIds.append(transactionInterestForPay.getTransactionInterestId().toString());
                            transactionInterestIds.append(StringUtil.CARET);
                        }
                        TransactionInterestForPay interestForPay = new TransactionInterestForPay();
                        interestForPay.setProjectId(projectId);
                        interestForPay.setPayerId(transactionInterestForPayList.get(0).getPayerId());
                        interestForPay.setTotalPayInterestAndPrincipalForLender(totalPayInterestAndPrincipalForLender.setScale(2, BigDecimal.ROUND_HALF_UP));
                        interestForPay.setTotalPayInterestAndPrincipalForPlatform(totalPayInterestAndPrincipalForPlatform.setScale(2, BigDecimal.ROUND_HALF_UP));
                        interestForPay.setTransactionInterestIds(transactionInterestIds.substring(0, transactionInterestIds.lastIndexOf(StringUtil.CARET)));
                        logger.info("[还本付息]-[创建代收]--项目：projectId={},计算原始债权人还款代收金额和平台垫付收益券代收金额完成，totalPayInterestAndPrincipalForLender={},totalPayInterestAndPrincipalForPlatform={}",projectId,totalPayInterestAndPrincipalForLender,totalPayInterestAndPrincipalForPlatform);
                        transactionInterestForPayLists.add(interestForPay);
                    }
                }
                //借款人账户资金余额不足，则不进行代收
                for (int i = 0;i<transactionInterestForPayLists.size();i++) {
					//查询借款人账户余额
                	TransactionInterestForPay transactionInterestForPay = transactionInterestForPayLists.get(i);
                	Long payerId = transactionInterestForPay.getPayerId();
                	Balance balance=new Balance();
        			try{
        				balance = balanceManager.synchronizedBalance(payerId, TypeEnum.BALANCE_TYPE_PIGGY);
        			}catch(Exception e){
        				logger.error("[还本付息]-[创建代收]--代收前查询借款人存钱罐余额错误",e);
        				balance = balanceManager.queryBalance(payerId, TypeEnum.BALANCE_TYPE_PIGGY);
        			}
        			if(balance.getAvailableBalance().compareTo(transactionInterestForPay.getTotalPayInterestAndPrincipalForLender())<0){
                		transactionInterestForPayLists.remove(transactionInterestForPay);
                		//TODO 发送短信等通知借款人余额不足
                		logger.info("[还本付息]-[创建代收]--借款人账户资金不足，不创建代收，projectId={}",transactionInterestForPay.getProjectId());
                	}
				}
            }
            return transactionInterestForPayLists;
        }
        return null;
    }

    /**
     * 插入原始债权人还款
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public  ResultDto<CreateCollectTradeResult> insertCreditorHostingCollectTrade(TransactionInterestForPay transactionInterestForPay) throws Exception {
        BigDecimal totalPayInterestAndPrincipalForLender = transactionInterestForPay.getTotalPayInterestAndPrincipalForLender();
        Long projectId = transactionInterestForPay.getProjectId();
        Long payerId = transactionInterestForPay.getPayerId();
        BigDecimal totalPayInterestAndPrincipalForPlatform = transactionInterestForPay.getTotalPayInterestAndPrincipalForPlatform();
        ResultDto<CreateCollectTradeResult> result = new ResultDto<CreateCollectTradeResult>();
        try {
            // 判断是否已经代收过原始债权人的还款
            if(hostingCollectTradeManager.haveHostingCollectTradeForLender(projectId, transactionInterestForPay.getTransactionInterestIds())) {
                logger.info("[还本付息]-[创建代收]--项目：projectId=" + projectId + "还本付息已经创建过原始债权人代收了");
                result.setSuccess(true);
                return result;
            }
            logger.info("[还本付息]-[创建代收]--项目：projectId=" + projectId + "，开始创建本地借款人代收");
            HostingCollectTrade collectTrade = new HostingCollectTrade();
            buildBaseCollectTrade(collectTrade, projectId);
            collectTrade.setAmount(totalPayInterestAndPrincipalForLender);
            collectTrade.setPayerId(payerId);
            collectTrade.setTradeNo(SerialNumberUtil.generateCollectTradeaNo());
            collectTrade.setType(TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getType());
            collectTrade.setTransactionInterestIds(transactionInterestForPay.getTransactionInterestIds());
            collectTrade.setPlatformAmount(totalPayInterestAndPrincipalForPlatform);
            if (transactionInterestForPay.getIsAdvances()){
                collectTrade.setSummary("[项目"+projectId+"]创建垫资人还本付息代收");
            }else {
            	//项目XX2016-11-11还本付息
                collectTrade.setSummary("[项目"+projectId+"]创建"+RemarksEnum.HOSTING_COLLECT_TRADE_FOR_LENDER.getRemarks());
            }
			String ip = null;
			SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
			if (dict != null) {
				ip = dict.getValue();
			}
			collectTrade.setPayerIp(ip);
            if(hostingCollectTradeManager.insertSelective(collectTrade)>0) {
            	 logger.info("[还本付息]-[创建代收]--项目：projectId=" + projectId + "，创建本地借款人代收成功，开始创建新浪远程借款人代收");
                ResultDto<CreateCollectTradeResult> hostingCollectTrade = null;
                try {
                    hostingCollectTrade  = sinaPayClient.createHostingCollectTrade(
                            collectTrade.getTradeNo(),
                            collectTrade.getSummary(),
                            SerialNumberUtil.generateIdentityId(payerId),
                            ip,
                            AccountType.SAVING_POT,
                            collectTrade.getAmount(),
                            IdType.UID,
                            TradeCode.COLLECT_FROM_BORROWER,
                            TypeEnum.COLLECT_TRADE_TYPE_NORMAL.getCode()
                    );
                }catch (Exception e){
                    logger.error("[还本付息]-[创建代收]--发起新浪代收异常：projectId=" + projectId + ", payerId=" + payerId, e);
                }
                logger.info("[还本付息]-[创建代收]--项目：projectId=" + projectId + "，创建新浪远程借款人代收完成");
                return hostingCollectTrade;
            }
            return null;
        } catch (Exception e) {
            logger.error("[还本付息]-[创建代收]--插入原始债权人还款和平台垫付代收交易出错：projectId=" + projectId + ", payerId=" + payerId, e);
            throw e;
        }
    }
    /**
     * 插入平台原始应付的利息
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public synchronized ResultDto<CreateCollectTradeResult> insertPlatformHostingCollectTrade(TransactionInterestForPay transactionInterestForPay) throws Exception {
        ResultDto<CreateCollectTradeResult> result = new ResultDto<CreateCollectTradeResult>();
        BigDecimal totalPayInterestAndPrincipalForPlatform = transactionInterestForPay.getTotalPayInterestAndPrincipalForPlatform();
        if(totalPayInterestAndPrincipalForPlatform==null || BigDecimal.ZERO.compareTo(totalPayInterestAndPrincipalForPlatform)==0){
            logger.info("[还本付息]-[创建代收]--项目：projectId={}  不用平台收益券代收",transactionInterestForPay.getProjectId());
            result.setSuccess(true);
            return result;
        }
        if(hostingCollectTradeManager.haveHostingCollectTradeForPaltform(transactionInterestForPay.getProjectId(), transactionInterestForPay.getTransactionInterestIds())) {
            logger.info("[还本付息]-[创建代收]--项目：projectId={}  已经创建了平台收益券代收",transactionInterestForPay.getProjectId());
            result.setSuccess(true);
            return result;
        }
		String ip = null;
		SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
		if (dict != null) {
			ip = dict.getValue();
		}
		logger.info("[还本付息]-[创建代收]--项目：projectId={}  开始创建平台收益券本地代收",transactionInterestForPay.getProjectId());
        HostingCollectTrade collectTrade = new HostingCollectTrade();
        collectTrade.setPayMethod(SinaPayEnum.PAY_TYPE_BALANCE.getCode());
        collectTrade.setSourceId(transactionInterestForPay.getProjectId());
        collectTrade.setTradeCloseTime(Config.sinaPayDefaultOrderCloseTime);
        collectTrade.setTradeStatus(SinaPayEnum.TRANSACTION_WAIT_PAY.getCode());
        collectTrade.setTradeTime(DateUtils.getCurrentDate());
        collectTrade.setPayStatus(PayStatus.PROCESSING.name());
        collectTrade.setAmount(totalPayInterestAndPrincipalForPlatform);
        collectTrade.setPayerId(Long.parseLong(Config.internalMemberId));
        collectTrade.setProjectId(transactionInterestForPay.getProjectId());
        collectTrade.setTradeNo(SerialNumberUtil.generateCollectTradeaNo());
        collectTrade.setType(TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType());
        collectTrade.setSummary("[项目"+transactionInterestForPay.getProjectId()+"]创建平台垫付收益券还款代收");
        collectTrade.setTransactionInterestIds(transactionInterestForPay.getTransactionInterestIds());
		collectTrade.setPayerIp(ip);
        if(hostingCollectTradeManager.insertSelective(collectTrade)>0) {
        	logger.info("[还本付息]-[创建代收]--项目：projectId={}  创建平台收益券本地代收完成，开始创建平台收益券远程代收",transactionInterestForPay.getProjectId());
            ResultDto<CreateCollectTradeResult> hostingCollectTrade = null;
            try {
                   hostingCollectTrade = sinaPayClient.createHostingCollectTrade(
                        collectTrade.getTradeNo(),
                        collectTrade.getSummary(),
                        SerialNumberUtil.getBasicAccount(),
                        ip,
                        AccountType.BASIC,
                        collectTrade.getAmount(),
                        IdType.EMAIL,
                        TradeCode.COLLECT_FROM_PLATFORM_PROFITCOUPON,
                        TypeEnum.COLLECT_TRADE_TYPE_NORMAL.getCode()
                );
                   logger.info("[还本付息]-[创建代收]--项目：projectId={}  创建平台收益券远程代收完成",transactionInterestForPay.getProjectId());
                return hostingCollectTrade;
                
            }catch (Exception e){
                logger.error("[还本付息]-[创建代收]--项目：projectId="+transactionInterestForPay.getProjectId()+"创建平台收益券远程代收异常：", e);
            }
        }
        return  null;
    }
    private void buildBaseCollectTrade(HostingCollectTrade collectTrade, Long projectId) {
        collectTrade.setPayMethod(SinaPayEnum.PAY_TYPE_BALANCE.getCode());
        collectTrade.setSourceId(projectId);
        collectTrade.setProjectId(projectId);
        collectTrade.setTradeCloseTime(Config.sinaPayDefaultOrderCloseTime);
        collectTrade.setTradeStatus(SinaPayEnum.TRANSACTION_WAIT_PAY.getCode());
        collectTrade.setTradeTime(DateUtils.getCurrentDate());
        collectTrade.setPayStatus(PayStatus.PROCESSING.name());
    }


}
