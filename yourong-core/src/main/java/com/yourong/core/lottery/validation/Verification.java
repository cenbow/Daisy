package com.yourong.core.lottery.validation;

import com.yourong.core.lottery.model.RuleBody;

/**
 * 抽奖资格校验
 * @author wangyanji
 *
 */
public interface Verification {

	/**
	 * 校验接口
	 * @param model
	 * @return true 允许抽奖/false 不允许
	 * @throws Exception 
	 */
	public boolean validate(RuleBody model) throws Exception;
	
	/**
	 * 抽奖前的准备
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public void prepare(RuleBody model) throws Exception;
}
