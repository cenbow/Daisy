package com.yourong.core.mc.manager;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.mc.model.biz.ActivityBiz;
import com.yourong.core.tc.model.Transaction;

/**
 * 
 * @desc 交易后活动相关业务处理
 * @author wangyanji 2015年12月22日下午5:03:12
 */
public interface ActivityAfterTransactionManager {

	/**
	 * 
	 * @Description:交易后活动相关业务处理入口
	 * @param transaction
	 * @author: wangyanji
	 * @time:2015年12月22日 下午5:10:18
	 */
	public void afterTransactionEntry(Transaction transaction);

	/**
	 * 
	 * @Description:直投审核通过以后活动相关业务处理入口
	 * @param transaction
	 * @author: wangyanji
	 * @time:2015年12月22日 下午5:10:18
	 */
	public void afterDirectProjectAuditEntry(Transaction transaction);

	/**
	 * 
	 * @Description:创建人气值红包
	 * @param ruleJson
	 * @param rewardJson
	 * @param transaction
	 * @return
	 * @author: wangyanji
	 * @time:2016年3月16日 下午5:34:05
	 */
	public ResultDO<Object> popularityRedPackage(ActivityBiz activityBiz, Transaction transaction);
	
	public void fiveRitesTest(Transaction transaction,int type);

	void olympicTest(Transaction transaction);
	/**
	 * 补发五重礼
	 * @param projectId
	 * @return
	 */
	ResultDO<Object> sendFiveRites(Long projectId);

	public void octoberNational(Transaction transaction);

	void thirtyGift(Transaction transaction);

	void directProjectLottery(Transaction transaction);

	void anniversary(Transaction transaction);

	void october(Transaction transaction);

	void directSendReward(Transaction transaction);

	void sumTotalFirstInvestAmount(Transaction transaction);

	/**
	 * @param transaction
	 */
	void fiveBillionLuck(Transaction transaction);

	void dayDropGold(Transaction transaction);
	
	/**
	 * 60亿活动，抢标五重礼的奖励翻6倍
	 * @return
	 * @throws Exception
	 */
	public BigDecimal sixBillionActivity(Date transDate,String popularValue) throws Exception;

}
