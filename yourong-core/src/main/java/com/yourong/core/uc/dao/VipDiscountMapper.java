/**
 * 
 */
package com.yourong.core.uc.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yourong.core.uc.model.VipDiscount;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年10月24日下午1:56:36
 */
@Repository
public interface VipDiscountMapper {
	
	
	VipDiscount getVipDiscountByVip(@Param("vipLevel")Integer vipLevel);

}
