package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 60亿活动
 * @author zhengqj
 *
 */
public class ActivityForSixBillion extends AbstractBaseObject {
	/**
	 * 收益券模板id集合
	 */
	private List<Long> templateId;
	
	/**
	 * 人气值翻倍倍率
	 */
	private BigDecimal turnTimes;
	

	public List<Long> getTemplateId() {
		return templateId;
	}

	public void setTemplateId(List<Long> templateId) {
		this.templateId = templateId;
	}

	public BigDecimal getTurnTimes() {
		return turnTimes;
	}

	public void setTurnTimes(BigDecimal turnTimes) {
		this.turnTimes = turnTimes;
	}
}
