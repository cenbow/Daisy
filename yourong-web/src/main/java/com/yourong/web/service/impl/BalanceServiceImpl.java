package com.yourong.web.service.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.pay.BalancePayResponse;
import com.yourong.common.thirdparty.pay.PayMentService;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.web.BaseService;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.fin.manager.WithdrawLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.WithdrawFee;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.mc.manager.ActivityLotteryResultManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.manager.OrderManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.biz.MemberTransactionCapital;
import com.yourong.web.service.BalanceService;
import com.yourong.web.service.TransactionService;
import com.yourong.web.utils.SysServiceUtils;
/**
 * 资金余额
 * @author Administrator
 *
 */
@Service
public class BalanceServiceImpl extends BaseService implements BalanceService{
	
	@Autowired
	private BalanceManager balanceManager;
	
	@Autowired
	private PayMentService payMentService;
	
	@Autowired
	private CapitalInOutLogManager capitalInOutLogManager;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired 
	TransactionManager transactionManager;
	
	@Autowired
	TransactionInterestManager transactionInterestManager;
	
	@Autowired
	OrderManager orderManager;
	
	@Autowired 
	TransferProjectManager transferProjectManager;
	
	@Autowired
	private SysDictManager sysDictManager;
	
	@Autowired
    private WithdrawLogManager withdrawLogManager;
	
	@Autowired
	private HostingPayTradeManager hostingPayTradeManager;
	
	@Autowired
	private ActivityLotteryResultManager activityLotteryResultManager;

	/**
	 * 充值 回调接口
	 */
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void topUpFromThirdPay(long memberID,BigDecimal income,String sourceId) throws Exception {
		
			//新浪同步资金			
			BalancePayResponse queryBalance = payMentService.queryBalance(SerialNumberUtil.generateIdentityId(memberID));
			//更新余额表
			Balance balance = balanceManager.queryBalance(memberID,TypeEnum.BALANCE_TYPE_PIGGY);
			if(balance ==  null){
				balanceManager.insertBalance(TypeEnum.BALANCE_TYPE_PIGGY, 	queryBalance.getBalanceBigDecimal(),queryBalance.getAvailableBalanceBigDecimal(),memberID);
			}else{
				balanceManager.updateBalanceByID(queryBalance.getBalanceBigDecimal(),queryBalance.getAvailableBalanceBigDecimal(), balance.getId());				
			}		
			//记录流水
			capitalInOutLogManager.insert(memberID, TypeEnum.FINCAPITALINOUT_TYPE_RECHARGE, 
					income, BigDecimal.ZERO, queryBalance.getBalanceBigDecimal(), sourceId, "", TypeEnum.FINCAPITALINOUT_PAYACCOUNTTYPE_MAIN);
			
	}

	@Override
	public BigDecimal getBalanceByType(TypeEnum type) {
		try {
			Balance balance =balanceManager.queryBalance(-1L,
					type);
			if(balance!=null) {
				return balance.getAvailableBalance();
			}
		} catch (ManagerException e) {
			logger.error("查询余额（主要用于查询平台投资总额和平台收益利息总额）出异常, type=" + type);
		}
		return null;
	}

	@Override
	public Balance queryBalance(Long sourceID, TypeEnum type) {
		
		try {
			Balance balance =balanceManager.queryBalance(sourceID,type);
			return balance;
		} catch (ManagerException e) {
			logger.error("查询余额出异常, sourceID=" + sourceID);
		}
		return null;
	}

	@Override
	public void initBalance(Long sourceID, TypeEnum type) {
		
		Balance record =  new Balance();
		record.setBalance(BigDecimal.ZERO);
		record.setAvailableBalance(BigDecimal.ZERO);		
		record.setSourceId(sourceID);
		record.setBalanceType(type.getType());
		try {
			int i = balanceManager.insert(record);
		} catch (ManagerException e) {
			logger.error("初始化余额出异常, sourceID=" + sourceID);
		}
	}

	@Override
	public boolean balanceIsZero(Long memberID) {
		boolean result = false;
		try {
			Balance balance = balanceManager.synchronizedBalance(memberID, TypeEnum.BALANCE_TYPE_PIGGY);
			if (BigDecimal.ZERO.compareTo(balance.getAvailableBalance())==0){
				result  = true;
			}
		} catch (ManagerException e) {
			logger.error("同步化余额出异常, sourceID={}", memberID);
		}
		return result;
	}

	@Override
	public boolean isZeroMemberTotalAsset(Long memberId) {
		boolean result = false;
		try {
			result =  balanceManager.isZeroMemberTotalAsset(memberId);
		} catch (ManagerException e) {
			logger.error("判断会员所有资产是否为0 , memberId={}", memberId);
		}
		return result;
	}

	@Override
	public ResultDO getMemberCenterData(Long memberId) {
		ResultDO result = new ResultDO();
		try {
			 Map<String, Object> balanceMap = Maps.newHashMap();
			 Balance balance = queryBalance(memberId, TypeEnum.BALANCE_TYPE_PIGGY);
			 //投资数据
			 MemberTransactionCapital capital = transactionService.getMemberTransactionCapital(memberId);
			 //待收本金
			 BigDecimal receivablePrincipal = BigDecimal.ZERO;
			 //待收收益
	  	     BigDecimal receivableInterest = BigDecimal.ZERO;
	  	     //已收收益
	  	     BigDecimal receivedInterest = BigDecimal.ZERO;
	  	     //累计投资
	  	     BigDecimal investTotal = BigDecimal.ZERO;
	  	     //滞纳金
	  	     BigDecimal overdueFine = BigDecimal.ZERO;
	  	     //其他收益
	  	     BigDecimal otherIncome = BigDecimal.ZERO;
			 if(capital != null){
				 receivablePrincipal = capital.getReceivablePrincipal();
				 receivableInterest = capital.getReceivableInterest();
				 receivedInterest = capital.getReceivedInterest();
				 investTotal = capital.getFinishedInvestTotal().add(capital.getSubsistingInvestTotal());
				 overdueFine = capital.getOverdueFine();
			 }
	  	 	 //资产总计＝存钱罐余额＋待收本金＋待收收益
	  	 	 BigDecimal totalAssets = balance.getBalance().add(receivablePrincipal).add(receivableInterest);
	  	 	 
	  	 	 //资产总计
	  	 	 balanceMap.put("totalAssets", totalAssets);
	  	 	 //存钱罐余额
			 balanceMap.put("balance", balance.getBalance());
			 //存钱罐可用余额
			 balanceMap.put("availableBalance", balance.getAvailableBalance());
			 balanceMap.put("investTotal", investTotal);//累计投资本金
			 balanceMap.put("investNum", capital.getFinishedInvestNum()+capital.getSubsistingInvestNum());//累计投资本金
			 balanceMap.put("receivedPrincipal", capital.getReceivedPrincipal());//已收本金
			 balanceMap.put("finishedInvestNum", capital.getFinishedInvestNum());//已收本金笔数
			 balanceMap.put("receivablePrincipal", receivablePrincipal);//待收本金
			 balanceMap.put("subsistingInvestNum", capital.getSubsistingInvestNum());//待收本金笔数
			 balanceMap.put("investTotalInterest", receivedInterest.add(receivableInterest));//累计利息收益
			 balanceMap.put("interestTotalNum", capital.getReceivedInterestNum()+capital.getReceivableInterestNum());//累计利息收益笔数
			 balanceMap.put("receivedInterest", receivedInterest);//已收收益
			 balanceMap.put("receivedInterestNum", capital.getReceivedInterestNum());//已收收益笔数
			 balanceMap.put("receivableInterest", receivableInterest);//待收收益
			 balanceMap.put("receivableInterestNum", capital.getReceivableInterestNum());//待收收益笔数
			 balanceMap.put("totalInvestNum", capital.getTotalInvestNum());//投资总笔数
			 
			// 转让总收款
			Map totalTransferAmountMap = hostingPayTradeManager.getTotalTransferGetAmount(memberId);
			BigDecimal totalTransferAmount = new BigDecimal(totalTransferAmountMap.get("amount").toString()).setScale(
					2, BigDecimal.ROUND_HALF_UP);
			BigDecimal totalTransferAmountNum = new BigDecimal(totalTransferAmountMap.get("num").toString());
			Map totalTransferFeeMap = hostingPayTradeManager.getTotalTransferFeeAmount(memberId);
			BigDecimal totalTransferFee = new BigDecimal(totalTransferFeeMap.get("amount").toString()).setScale(2,
					BigDecimal.ROUND_HALF_UP);
			BigDecimal totalTransferFeeNum = new BigDecimal(totalTransferFeeMap.get("num").toString());
			BigDecimal totalPrincipal = transferProjectManager.getTotalTransferPrincipal(memberId).setScale(2,
					BigDecimal.ROUND_HALF_UP);
			balanceMap.put("totalTransferAmount", totalTransferAmount);
			balanceMap.put("totalTransferAmountNum", totalTransferAmountNum);
			balanceMap.put("totalPrincipal", totalPrincipal);
			balanceMap.put("totalDiscount", totalPrincipal.subtract(totalTransferAmount).subtract(totalTransferFee));
			balanceMap.put("totalTransferFee", totalTransferFee);
			balanceMap.put("totalTransferFeeNum", totalTransferFeeNum);
			 
			 //累计收益 = 待收收益  + 存钱罐收益 + 已收收益
	  	     //存钱罐收益
	  	     BigDecimal savingPotEarning = queryBalance(memberId, TypeEnum.BALANCE_TYPE_MEMBER_TOTAL_PIGGY).getBalance();
	  	     //租赁分红
	  	     BigDecimal totalMemberLeaseBonusAmounts = transactionManager.totalMemberLeaseBonusAmounts(memberId);
	  	     //快投有奖现金奖励
	  	     ActivityLotteryResult modelResultAmount = new ActivityLotteryResult();
			 modelResultAmount.setMemberId(memberId);
			 BigDecimal quickRewardCash = hostingPayTradeManager.totalMemberPayAmountByType(memberId, TypeEnum.HOSTING_PAY_TRADE_QUICK_REWARD.getType());//快投发放的现金奖励
			 //其他收益 = 租赁分红+滞纳金+快投有奖现金奖励
			 otherIncome = totalMemberLeaseBonusAmounts.add(overdueFine).add(quickRewardCash);
	  	     //使用现金券
	  	     BigDecimal totalMemberUsedCouponAmount = transactionManager.totalMemberUsedCouponAmount(memberId);
	  	     //累计收益
	  	     BigDecimal totalEarnings = savingPotEarning.add(receivableInterest).add(receivedInterest).add(otherIncome).add(totalMemberUsedCouponAmount);   	     
	  	     balanceMap.put("totalEarnings", totalEarnings.setScale(2, BigDecimal.ROUND_HALF_UP)); //总共赚取
	  	     balanceMap.put("interestEarning", receivedInterest.add(receivableInterest));//利息收益
	  	     //balanceMap.put("totalMemberExtraInterest",transactionInterestManager.totalMemberExtraInterest(memberId));//收益券获得的利息
	  	     balanceMap.put("totalMemberExtraInterest",transactionInterestManager.totalMemberExtraInterestT(memberId));//收益券获得的利息
	  	     balanceMap.put("savingPotEarning", savingPotEarning);//存钱罐收益
	  	     balanceMap.put("otherIncome",otherIncome);//租赁分红
	  	     balanceMap.put("totalMemberUsedCouponAmount",totalMemberUsedCouponAmount);//使用现金券
	  	     balanceMap.put("totalMemberUsedCouponNum",transactionManager.totalMemberUsedCouponNum(memberId));//使用现金券笔数
	  	     balanceMap.put("freezeBalance", balance.getBalance().subtract(balance.getAvailableBalance()));//冻结余额
	  	     balanceMap.put("waitingPaymentOrder", orderManager.getNoPayOrdeCount(memberId));//待支付订单
	  	     result.setResult(balanceMap);
		} catch (ManagerException e) {
			logger.error("会员中心数据异常={}", memberId, e);
			result.setResultCode(ResultCode.ERROR);
		}
		return result;
	}

	@Override
	public BigDecimal getWithdrawFee(BigDecimal withdrawAmount,Long memberId) {
		BigDecimal freeWithdrawAmount=BigDecimal.ZERO;
		Optional<Activity> withActivity = LotteryContainer.getInstance()
				.getActivityByName(ActivityConstant.WITH_DRAW_NAME);
		if (!withActivity.isPresent()) {
			return new BigDecimal(SysServiceUtils.getWithdrawalFees());
		}
	    Activity activity=withActivity.get();
	    try {
	    if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
	    	SysDict sysDict=sysDictManager.findByGroupNameAndKey("withdrawal_fees", "withdrawalFees");
	    	WithdrawFee withdrawFee=new WithdrawFee();
	    	if(sysDict!=null){
	    		withdrawFee=JSON.parseObject(sysDict.getRemarks(),WithdrawFee.class);
	    		if(withdrawAmount.compareTo(withdrawFee.getWithDrawAmount().get(0))<0){
	    			freeWithdrawAmount=withdrawFee.getPopularValue();
	    		}else if(withdrawAmount.compareTo(withdrawFee.getWithDrawAmount().get(1))>=0){
	    			freeWithdrawAmount=BigDecimal.ZERO;
	    		}else{
	    			int i=withdrawLogManager.countWithDrawFree(memberId,DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_6));
	    			if(i>withdrawFee.getSecond()){
	    				freeWithdrawAmount=withdrawFee.getPopularValue();
	    			}
	    		}
	    	}
	    }else{
	    	freeWithdrawAmount=new BigDecimal(SysServiceUtils.getWithdrawalFees());
	    }
	    } catch (ManagerException e) {
			logger.error("获取提现手续费异常={}", memberId);
		}
		return freeWithdrawAmount;
	}
}
