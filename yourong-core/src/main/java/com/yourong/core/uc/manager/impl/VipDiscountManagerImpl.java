/**
 * 
 */
package com.yourong.core.uc.manager.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.yourong.common.enums.MemberLogEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.uc.dao.VipDiscountMapper;
import com.yourong.core.uc.manager.MemberBehaviorLogManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.manager.MemberVipManager;
import com.yourong.core.uc.manager.VipDiscountManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberVip;
import com.yourong.core.uc.model.VipDiscount;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年10月24日下午1:49:19
 */
@Component
public class VipDiscountManagerImpl implements VipDiscountManager{
	
	private Logger logger = LoggerFactory.getLogger(VipDiscountManagerImpl.class);
	
	@Autowired
	private VipDiscountMapper vipDiscountMapper;
	
	@Autowired
	private MemberVipManager memberVipManager;
	
	@Autowired
	private CouponManager couponManager;
	
	@Autowired
	private MemberManager memberManager;
	
	@Autowired
	private MemberBehaviorLogManager memberBehaviorLogManager;
	
	@Override
	public VipDiscount getVipDiscountByVip(Integer vipLevel) {
		try{
			return vipDiscountMapper.getVipDiscountByVip(vipLevel);
		}catch(Exception e){
			logger.error("获取vpi福利异常,vipLevel={}",vipLevel,e);
		}
		return null;
		
		
	}
	
	/**
	 * 生日送券
	 * 
	 * @param query
	 * @return
	 * @throws ManagerException
	 */
	@Override
	public void birthGoSign(Long memberId,Integer type){
		try {
			Member member = memberManager.selectByPrimaryKey(memberId);
			MemberVip  memberVip = memberVipManager.selectRecentMemberVipByMemberId(memberId);
			VipDiscount vipDis =this.getVipDiscountByVip(memberVip.getVipLevel());		
			String mapJson =vipDis.getInfomation();
			
			Map map = JSON.parseObject(mapJson);
			if(map.containsKey("cash")&&map.containsKey("income")){
				String cashId = map.get("cash").toString();
				String incomeId = map.get("income").toString();
				
				Coupon cashC = couponManager.receiveCoupon(memberId, -1L, Long.parseLong(cashId), -1L);
				if (cashC == null) {
					// 现金券赠送失败;
					logger.error("现金券赠送失败，接口返回Coupon=null!, memberId={}, templateId={}", memberId,
							 cashId);
				}
				Coupon incomeC = couponManager.receiveCoupon(memberId, -1L, Long.parseLong(incomeId), -1L);
				if (incomeC == null) {
					// 收益券赠送失败;
					logger.error("收益券赠送失败，接口返回Coupon=null!, memberId={}, templateId={}", memberId,
							 incomeId);
				}
			}
			
			memberBehaviorLogManager.logInsert(memberId, memberId.toString(), MemberLogEnum.MEMBER_BEHAVIOR_TYPE_BIRTH_SIGN.getType(),
					type, "");
			
		} catch(Exception e){
			logger.error("生日签到处理异常,memberId={}",memberId,e);
		}
	}
		

}
