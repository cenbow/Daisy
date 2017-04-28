package com.yourong.core.mc.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 2017年春季活动
 * 
 * @author zhanghao
 */
public class ActivityForSpringComeTotal extends AbstractBaseObject{

	 /**
     * 累计投资额
     */
    private String totalAmount;
    
    /**
     * 奖励优惠券id
     */
    private String rewardTemplateIds;

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getRewardTemplateIds() {
		return rewardTemplateIds;
	}

	public void setRewardTemplateIds(String rewardTemplateIds) {
		this.rewardTemplateIds = rewardTemplateIds;
	}
	
}
