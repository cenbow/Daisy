package com.yourong.core.lottery.manager;

import java.math.BigDecimal;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.lottery.model.RuleForRedPackage;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.biz.ActivityBiz;
import com.yourong.core.mc.model.biz.ActivityForRedBag;
import com.yourong.core.tc.model.Transaction;

public interface RedPackageManager {

	/**
	 * 
	 * @Description:校验红包规则
	 * @param rule
	 * @param transaction
	 * @param rDO
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年3月17日 下午2:59:51
	 */
	public ResultDO<Object> checkRedPackage(RuleForRedPackage rule, Transaction transaction, ResultDO<Object> rDO, int triggle)
			throws Exception;

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

	/**
	 * 
	 * @Description:领取人气值红包
	 * @param mobile
	 * @param transactionId
	 * @return
	 * @author: wangyanji
	 * @time:2016年3月21日 下午3:14:01
	 */
	public ActivityForRedBag receivePopularityRedPackage(Long mobile, Transaction transaction, ActivityBiz activityBiz,
			RuleForRedPackage rule);

}
