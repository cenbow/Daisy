/**
 * 
 */
package com.yourong.core.uc.manager;

import java.math.BigDecimal;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.uc.model.MemberVip;
import com.yourong.core.uc.model.VipDiscount;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年10月20日上午10:01:34
 */
public interface MemberVipManager {
	
	
	public MemberVip selectRecentMemberVipByMemberId(Long memberId) throws ManagerException ;
	
	public BigDecimal getGoodValueByMemberVip(Long memberId,Long goodId) throws ManagerException ;
	
	/**
	 * 校验客户升级条件
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public Boolean isLevelUp(Long memberId)throws ManagerException;
	
	public Page<MemberVip> queryMemberVipList(BaseQueryParam query) throws ManagerException;
	
	/**
	 * 获取用户VIP等级
	 * @param memberId
	 * @return
	 */
	public Integer getMemberVip(Long memberId);
	
	public void memberLevelUpHandle();
	
	public Integer selectMemberVipNewByMemberId(Long memberId) throws ManagerException ;

	/**
	 * 获取vip等级对应的折扣信息
	 * @param vipLevel
	 * @return
	 */
	public VipDiscount getVipDiscountByVip(Integer vipLevel);
	/**
	 * 根据商品的折扣信息和会员等级计算折扣信息，获取不到返回1
	 * @param goodsDiscountInfo 商品折扣信息
	 * @param vipLevel 会员等级
	 * @return
	 */
	public BigDecimal getGoodsDiscountByInfo(String goodsDiscountInfo,Integer vipLevel);

}
