package com.yourong.core.lottery.draw;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;
import com.yourong.common.cache.RedisActivityClient;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.RandomUtils;
import com.yourong.core.lottery.LotteryBase;
import com.yourong.core.lottery.model.PopularityRedBag;
import com.yourong.core.lottery.model.RuleBody;
import com.yourong.core.lottery.validation.impl.VerificationByPopularityRedBag;
import com.yourong.core.mc.manager.ActivityLotteryPretreatManager;

/**
 * 生成人气值红包类
 * 
 * @author wangyanji
 *
 */
@Component
public class DrawByPopularityRedBagCreate extends LotteryBase {

	private static final Logger logger = LoggerFactory.getLogger(DrawByPopularityRedBagCreate.class);

	@Autowired
	private VerificationByPopularityRedBag verificationByPopularityRedBag;

	@Autowired
	private ActivityLotteryPretreatManager activityLotteryPretreatManager;

	@Override
	public boolean validate(RuleBody model, String validateType) throws Exception {
		if (Optional.of(model).isPresent()) {
			if (TypeEnum.ACTIVITY_LOTTERY_VALIDATE_POPULARITYREDBAG.getCode().equals(validateType)) {
				return verificationByPopularityRedBag.validate(model);
			}
		}
		return false;
	}

	@Override
	protected Object loadRewards(RuleBody rb) throws Exception {
		return null;
	}

	@Override
	public Object drawLottery(RuleBody model, Object inputCode, String validateType) throws Exception {
		try {
			PopularityRedBag redBag = (PopularityRedBag) (model.getVerificationObj());
			// 定义随机数结果
			List<Integer> randomList = null;
			// 先按照红包规则分类
			if (redBag.getArithmetic().equals(TypeEnum.REDPACKAGE_POPULARITY_RANDOM_SUBTRACT.getCode())) {
				// 定额随机量人气值红包
				randomList = quotaTotalAmountAndSplitNum(redBag);
			}
			if (Collections3.isNotEmpty(randomList)) {
				// push一组红包到redis
				Long retNum = RedisActivityClient.rpushRedBagValue(redBag.getSourceId(), randomList, ActivityConstant.activityKeyExpire);
				logger.info("交易号transactionId={}, 生成红包个数={}", redBag.getSourceId(), retNum);
				redBag.setFinalSplitNum(retNum);
				// 增加已生成红包总数
				//RedisActivityClient.incrPopularityRedBag(redBag.getSourceId(), retNum, ActivityConstant.activityKeyExpire);
			}
			return redBag;
		} catch (Exception e) {
			logger.error("生成人气值红包程序失败, memberId={}, activityId={}", model.getMemberId(), model.getActivityId(), e);
			throw e;
		}
	}

	/**
	 * 
	 * @Description:
	 * @param redBag
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月8日 上午10:33:50
	 */
	private List<Integer> quotaTotalAmountAndSplitNum(PopularityRedBag redBag) {
		// 确定分享数额
		int[] randomSplitNum = redBag.getSplitNum();
		int splitNum = RandomUtils.getRandomNumberByRange(randomSplitNum[0], randomSplitNum[1]);
		// 确定红包总额
		int amount = getRedBagAmount(redBag.getRedBagAmount(), redBag.getTotalAmount());
		if (splitNum < 1 && amount < splitNum) {
			return null;
		}
		// 计算随机数
		return RandomUtils.calcByQuotaTotalAmountAndSplitNum(amount, splitNum, 1);
	}

	/**
	 * 
	 * @Description:获得最终红包总额
	 * @param preRedBagAmount
	 * @param calcRule
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月8日 上午11:04:55
	 */
	private int getRedBagAmount(BigDecimal preRedBagAmount, String calcRule) {
		if (calcRule.startsWith("%")) {
			String[] ruleValueStr = calcRule.split("%");
			BigDecimal ruleValue = new BigDecimal(ruleValueStr[ruleValueStr.length - 1]);
			return preRedBagAmount.multiply(ruleValue).divide(new BigDecimal(100)).intValue();
		}
		return 0;
	}

}
