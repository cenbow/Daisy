package com.yourong.core.mc.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 2017年春季活动
 * 
 * @author zhanghao
 */
public class ActivityForSpringCome extends AbstractBaseObject{

	 /**
     * 投资送券id集合
     */
    private String investTemplateIds;
    

	public String getInvestTemplateIds() {
		return investTemplateIds;
	}

	public void setInvestTemplateIds(String investTemplateIds) {
		this.investTemplateIds = investTemplateIds;
	}
	
}
