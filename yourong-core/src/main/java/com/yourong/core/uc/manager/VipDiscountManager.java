/**
 * 
 */
package com.yourong.core.uc.manager;

import com.yourong.core.uc.model.VipDiscount;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年10月24日下午1:49:08
 */
public interface VipDiscountManager {
	
	public VipDiscount getVipDiscountByVip(Integer vipLevel);
	
	public void birthGoSign(Long memberId,Integer type);

}
