package com.yourong.core.repayment.manager.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.BatchTradeNotifyMode;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeCode;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.TradeArgs;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.ic.dao.ProjectMapper;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.repayment.manager.RepaymentManager;
import com.yourong.core.tc.dao.HostingCollectTradeMapper;
import com.yourong.core.tc.dao.TransactionInterestMapper;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.HostingPayTrade;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.TransactionInterestForPay;

/**
 *    还本付息
 * Created by py on 2015/7/20.
 */
@Component
public class RepaymentManagerImpl implements RepaymentManager{

    private Logger logger = LoggerFactory.getLogger(RepaymentManagerImpl.class);

    @Autowired
    private TransactionInterestMapper transactionInterestMapper;
    @Autowired
    private HostingCollectTradeMapper hostingCollectTradeMapper;

    @Autowired
    private HostingCollectTradeManager hostingCollectTradeManager;
    @Autowired
    private SinaPayClient sinaPayClient;
    @Autowired
    private HostingPayTradeManager hostingPayTradeManager;
    @Autowired
    private TransactionManager myTransactionManager;
    @Autowired
    private DebtManager debtManager;
    @Autowired
    private BalanceManager balanceManager;
    @Autowired
    private CapitalInOutLogManager capitalInOutLogManager;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private TransactionInterestManager transactionInterestManager;

    @Override
    public void payInterestAndPrincipal(List<TransactionInterestForPay> hostingCollectTradeForPayInterestAndPrincipal ) throws  Exception {
        if (Collections3.isEmpty(hostingCollectTradeForPayInterestAndPrincipal))
            return;
        Map<Long,String> map = Maps.newHashMap();
        for (TransactionInterestForPay transactionInterestForPay:hostingCollectTradeForPayInterestAndPrincipal) {
            String transactionInterestIds = transactionInterestForPay.getTransactionInterestIds();
            Long projectid = transactionInterestForPay.getProjectId();
            //先查待收表， 债权人的钱 是否到账
            HostingCollectTrade collectTrade = hostingCollectTradeMapper.queryAlreadyHostingCollectTradeForLender(projectid, TypeEnum.HOSTING_PAY_TRADE_RETURN.getType(),transactionInterestIds);
            //只有债权人（借款人）代收和平台收益券垫付代收都成功，才继续执行
            //判断债权人代收是否成功和平台代收收益劵是否成功
            if (isJudgeCollectMoney(collectTrade)   &&  isJudgeCollectMoneyForPlaForm(transactionInterestForPay)) {
                logger.info("准备发起代付的项目ID={},代收号={}",projectid,collectTrade.getTradeNo());
                map.put(projectid, collectTrade.getTradeNo());
            }
        }
        if (Collections3.isEmpty(map.entrySet())) {
            return;
        }
        //更新交易本息表记录
        updateTransactionInterest(map);
        //发起代付给投资人
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
         createThirdPayProcessPayInterestAndPrincipal(map);
        stopWatch.stop();
         logger.info("发起代付给投资者---总共有{}个项目,耗时={}",map.size(),stopWatch.getTime());
    }

    /***
     *   更新交易本息表记录
     * @param map
     * @throws ManagerException
     */
    private void updateTransactionInterest(Map<Long,String> map) throws ManagerException {
        for (Map.Entry<Long,String> e: map.entrySet()) {
            String  tradeNo = (String) e.getValue();
            Long projectId = (Long) e.getKey();
            transactionInterestManager.updateStatusToPayingForPayInterest(StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus(),
                    StatusEnum.TRANSACTION_INTEREST_PAYING.getStatus(),	projectId,tradeNo);
        }
    }

    /**
     * 判断 原始债权人资金代收是否成功
     * @param collectTrade
     * @return
     */
    private boolean isJudgeCollectMoney(HostingCollectTrade collectTrade) {
        boolean result = false;
        if (collectTrade != null
                && TypeEnum.HOSTING_PAY_TRADE_RETURN.getType() == collectTrade.getType()
                && TradeStatus.TRADE_FINISHED.name().equalsIgnoreCase(collectTrade.getTradeStatus())) {
            result = true;
        }
        return result;
    }

    /**
     * 判断平台的代收收益劵是否成功，如果没有，也返回成功
     * @param transactionInterestForPay
     * @return
     */
    private boolean isJudgeCollectMoneyForPlaForm(TransactionInterestForPay transactionInterestForPay) {
        String transactionInterestIds = transactionInterestForPay.getTransactionInterestIds();
        Long projectid = transactionInterestForPay.getProjectId();
        BigDecimal totalPayInterestAndPrincipalForPlatform = transactionInterestForPay.getTotalPayInterestAndPrincipalForPlatform();
        if (totalPayInterestAndPrincipalForPlatform == null || BigDecimal.ZERO.compareTo(totalPayInterestAndPrincipalForPlatform) == 0) {
            logger.info("项目：projectId={}  不用同步平台收益券代收", transactionInterestForPay.getProjectId());
            return true;
        }
        HostingCollectTrade collectTrade = hostingCollectTradeMapper.queryAlreadyHostingCollectTradeForPalform(projectid, TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType(), transactionInterestIds);
        if (collectTrade != null
                && TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType() == collectTrade.getType()
                && TradeStatus.TRADE_FINISHED.name().equalsIgnoreCase(collectTrade.getTradeStatus())) {
            return true;
        }
        return false;
    }




    public  void createThirdPayProcessPayInterestAndPrincipal(Map<Long,String> map) throws Exception {
        for (Map.Entry<Long,String> e: map.entrySet()) {
            String  tradeNo = (String) e.getValue();
            logger.info("发起代付的项目ID={},代收号={}",e.getKey(),tradeNo);
            processPayInterestAndPrincipal(tradeNo);
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ResultDO<HostingCollectTrade> processPayInterestAndPrincipal(String tradeNo) throws Exception  {
    	ResultDO<HostingCollectTrade> result = new ResultDO<HostingCollectTrade>();
    	HostingCollectTrade collectTradeUnlock = hostingCollectTradeManager.getByTradeNo(tradeNo);
    	if(collectTradeUnlock!=null) {
    		HostingCollectTrade collectTradeForLock = hostingCollectTradeManager.getByIdForLock(collectTradeUnlock.getId());
    		//判断该笔借款人代收是否关联平台代收
    		BigDecimal extraInterest = transactionInterestMapper.sumExtraInterest(collectTradeForLock.getProjectId());
    		if(extraInterest!=null && extraInterest.doubleValue()>0) {
    			 HostingCollectTrade PlatformCollectTrade = hostingCollectTradeMapper.queryAlreadyHostingCollectTradeForPalform(collectTradeForLock.getProjectId(), TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType(), collectTradeForLock.getTransactionInterestIds());
    		        if (PlatformCollectTrade != null
    		                && !TradeStatus.TRADE_FINISHED.name().equalsIgnoreCase(PlatformCollectTrade.getTradeStatus())) {
    		        	result.setResultCode(ResultCode.REPAYMENT_PLATFORM_COLLECT_NOT_TRADE_FINISHED);
    		        	logger.info("【还本付息】项目ID={},借款人代收号={}的平台垫付代收={状态还未变成TRADE_FINISHED",collectTradeForLock.getProjectId(),collectTradeForLock.getTradeNo(), PlatformCollectTrade.getTradeNo());
    		            return result;
    		        }
	}
	        List<TransactionInterest> transactionInterests = transactionInterestMapper.selectPayingTransactionInterestsByTradeNo(collectTradeForLock.getTradeNo(), StatusEnum.TRANSACTION_INTEREST_PAYING.getStatus());
	        if(Collections3.isNotEmpty(transactionInterests)) {
	            List<TradeArgs> tradeList = Lists.newArrayList();
	            String batchPayNo = SerialNumberUtil.generateBatchPayTradeaNo(tradeNo);
	            String summary = "代收交易:"+collectTradeForLock.getTradeNo()+"还本付息";
	            for (TransactionInterest transactionInterest : transactionInterests) {
	                // 本地创建代付交易hostingPayTrade
	                //判断这笔本息有没有代付过
	                if (hostingPayTradeManager.haveHostPayTradeForPayInterestAndPrincipal( transactionInterest.getId(),                  TypeEnum.HOSTING_PAY_TRADE_RETURN.getType())) {
	                	logger.info("[还本付息]，本息id={}已经代付过，无需重复代付",transactionInterest.getId());
	                    continue;
	                }
	                HostingPayTrade hostingPayTrade = new HostingPayTrade();
	                Integer payTrade = createHostingPayTrade(transactionInterest, batchPayNo, hostingPayTrade);
	                if(payTrade ==null || payTrade == 0){
	                   logger.info("插入代付失败，batchPayNo={}，transactionInterestID ={},tradeNo={}",batchPayNo,transactionInterest.getId(),transactionInterest.getTradeNo());
	                    continue;
	                }
	                //组建批量代付参数
	                TradeArgs tradeArgs = new TradeArgs();
	                tradeArgs.setAccountType(AccountType.SAVING_POT);
	                tradeArgs.setIdType(IdType.UID);
	                tradeArgs.setMoney(new Money(hostingPayTrade.getAmount()));
	                tradeArgs.setPayeeId(SerialNumberUtil.generateIdentityId(hostingPayTrade.getPayeeId()));
	                tradeArgs.setRemark(hostingPayTrade.getSummary());
	                tradeArgs.setTradeNo(hostingPayTrade.getTradeNo());
	                tradeList.add(tradeArgs);
	                if(tradeList.size()==100) {
	                    //当批量代付满300笔时提交代付请求
	                    try {
	                        sinaPayClient.createBatchPayTrade(batchPayNo, summary,collectTradeForLock.getPayerIp(),tradeList, TradeCode.PAY_TO_INVESTOR, BatchTradeNotifyMode.single_notify);
	                    } catch (Exception e) {
	                        logger.error("代付发生异常,batchPayNo ={}",batchPayNo,e);
	                    }
	                    batchPayNo = SerialNumberUtil.generateBatchPayTradeaNo(tradeNo);
	                    tradeList.clear();
	                }
	            }
	            if(Collections3.isNotEmpty(tradeList) && tradeList.size()<100) {
	                try {
	                    sinaPayClient.createBatchPayTrade(batchPayNo, summary,collectTradeForLock.getPayerIp(), tradeList, TradeCode.PAY_TO_INVESTOR, BatchTradeNotifyMode.single_notify);
	                } catch (Exception e) {
	                    logger.error("代收成功处理还本付息代付发生异常",e);
	                }
	            }
	        }
    	}
    	return result;
    }

    private Integer createHostingPayTrade(TransactionInterest transactionInterest, String batchPayNo, HostingPayTrade hostingPayTrade) throws ManagerException {
    	//加入添加滞纳金的代付
        BigDecimal totalAmount = transactionInterest.getPayableInterest().add(transactionInterest.getPayablePrincipal()).add(transactionInterest.getOverdueFine()).setScale(2, BigDecimal.ROUND_HALF_UP);
        hostingPayTrade.setAmount(totalAmount);
        hostingPayTrade.setBatchPayNo(batchPayNo);
        hostingPayTrade.setPayeeId(transactionInterest.getMemberId());
        hostingPayTrade.setRemarks(RemarksEnum.HOSTING_PAY_TRADE_FOR_INTEREST_AND_PRINCIPAL.getRemarks()+"本息id："+transactionInterest.getId());
        hostingPayTrade.setSourceId(transactionInterest.getId());
//		hostingPayTrade.setSummary(summary);
        hostingPayTrade.setTradeNo(SerialNumberUtil.generatePayTradeaNo(transactionInterest.getId()));
        hostingPayTrade.setTradeStatus(TradeStatus.WAIT_PAY.name());
        hostingPayTrade.setType(TypeEnum.HOSTING_PAY_TRADE_RETURN.getType());
        return hostingPayTradeManager.insertSelective(hostingPayTrade);
    }

	@Override
	public Integer updateInterestStatus(
			List<TransactionInterestForPay> hostingCollectTradeForPayInterestAndPrincipal)
			throws Exception {
		 if (Collections3.isNotEmpty(hostingCollectTradeForPayInterestAndPrincipal)) {
			 for (TransactionInterestForPay transactionInterestForPay:hostingCollectTradeForPayInterestAndPrincipal) {
				 String transactionInterestIds = transactionInterestForPay.getTransactionInterestIds();
				 Long projectid = transactionInterestForPay.getProjectId();
				 //先查待收表， 债权人的钱 是否到账
				 HostingCollectTrade collectTrade = hostingCollectTradeMapper.queryAlreadyHostingCollectTradeForLender(projectid, TypeEnum.HOSTING_PAY_TRADE_RETURN.getType(),transactionInterestIds);
				 if(collectTrade!=null) {
					 transactionInterestManager.updateStatusToPayingForPayInterest(StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus(),
							 StatusEnum.TRANSACTION_INTEREST_PAYING.getStatus(),	collectTrade.getProjectId(),collectTrade.getTradeNo());
				 }
			 }
		 }
			return null;
	}

}
