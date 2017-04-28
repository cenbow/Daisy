/**
 * 
 */
package com.yourong.core.uc.manager.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.enums.StatusEnum;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.DateUtils;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.manager.CouponTemplateManager;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.sh.manager.GoodsManager;
import com.yourong.core.sh.model.Goods;
import com.yourong.core.uc.dao.MemberVipMapper;
import com.yourong.core.uc.manager.MemberVipManager;
import com.yourong.core.uc.manager.VipDiscountManager;
import com.yourong.core.uc.model.MemberVip;
import com.yourong.core.uc.model.VipDiscount;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年10月20日上午10:01:43
 */
@Component

public class MemberVipManagerImpl implements MemberVipManager{                                                                              
	
	private Logger logger = LoggerFactory.getLogger(MemberVipManagerImpl.class);
	
	@Autowired
    private MemberVipMapper memberVipMapper;

	@Autowired
	private VipDiscountManager vipDiscountManager;
	
	@Autowired
	private GoodsManager goodsManager;
	
	@Autowired
	private CouponTemplateManager couponTemplateManager;
	
	@Autowired
	private CouponManager couponManager;

	@Autowired
	private SysDictManager sysDictManager;
	/**
	 * 切分商品的折扣信息
	 */
	static final String DISCOUNT_SPLIT= ",";
	/**
	 * 切分会员等级的和折扣
	 */
	static final String LEVEL_SPLIT= "-";

	@Override
	public MemberVip selectRecentMemberVipByMemberId(Long memberId) throws ManagerException {
		try {
			MemberVip memberVip = memberVipMapper.selectRecentMemberVipByMemberId(memberId);
				
				if(memberVip==null){
					MemberVip memberVipTemp = new MemberVip();
					memberVipTemp.setMemberId(memberId);
					memberVipTemp.setVipLevel(0);
					memberVipTemp.setMonth(DateUtils.getStrFromDate(DateUtils.getCurrentDate(),DateUtils.DATE_FMT_14));
					memberVipTemp.setScore(new BigDecimal(0));
					memberVipTemp.setIncreasedScore(new BigDecimal(0));
					memberVipTemp.setNeedSncreaseScore(new BigDecimal(10));
					return memberVipTemp;
				}
				
			return memberVip;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	public Integer getMemberVip(Long memberId){
		Integer vipLevel = 0;
		try {
			String key = RedisConstant.MEMBER_VIP_LEVLE+RedisConstant.REDIS_SEPERATOR +memberId ;
			boolean isExit = RedisManager.isExitByObjectKey(key);
			if(isExit){
				vipLevel = (Integer) RedisManager.getObject(key);
			}else{
				MemberVip memberVip = this.selectRecentMemberVipByMemberId(memberId);
				if(memberVip!=null){
					vipLevel =  memberVip.getVipLevel();
					RedisManager.putObject(key, vipLevel);
					RedisManager.expireObject(key, 7 * 24 * 60 * 60 );
				}
			}
		} catch (ManagerException e) {
			logger.error("获取用户vip等级异常,memberId={}" , memberId);
		}
		return vipLevel;
	}
	
	@Override
	public BigDecimal getGoodValueByMemberVip(Long memberId,Long goodId) throws ManagerException {
		BigDecimal goodValue = BigDecimal.ZERO;
		try {
			Goods goods =  goodsManager.queryGoodsById(goodId);
			goodValue = goods.getPrice();
			if(goods.getGoodsType()!=TypeEnum.GOODS_TYPE_DOUBLE.getType()){
				Integer vipLevel = this.getMemberVip(memberId);
				BigDecimal discount = getGoodsDiscountByInfo(goods.getDiscountInfo(),vipLevel);
				//如果根据商品填写的折扣信息没有取到折扣，则根据会员等级取折扣
				if(discount.compareTo(BigDecimal.ONE)==0){
					if(goods.getGoodsType()==TypeEnum.GOODS_TYPE_FOR_INVEST.getType()){
						CouponTemplate couponTemplate = couponTemplateManager.selectByPrimaryKey(goods.getSourceId());
						//现金券或者现金抵扣券
						if(couponTemplate.getCouponType()==1 || couponTemplate.getCouponType()==3){
							MemberVip memberVip = this.selectRecentMemberVipByMemberId(memberId);
							VipDiscount vipDiscount = vipDiscountManager.getVipDiscountByVip(memberVip.getVipLevel());
							discount = vipDiscount.getRankPreferenceDiscount();
						}
					}
				}
				goodValue = goodValue.multiply(discount).setScale(0, BigDecimal.ROUND_HALF_UP);
				return goodValue;
			}
			
			//双节特惠商品
			if (goods.getGoodsType()==TypeEnum.GOODS_TYPE_DOUBLE.getType()){
				SysDict sysDict= sysDictManager.findByGroupNameAndKey("goods_tags",goods.getTags().toString());
				if (sysDict!=null){
					BigDecimal discount= null;
					try {
						discount = new BigDecimal(sysDict.getDescription());
					} catch (Exception e) {
						logger.error("人气值商品元宵节折扣获取异常,goodId={}",goodId);
						return goodValue;
					}
					goodValue = goodValue.multiply(discount).multiply(new BigDecimal(0.1)).setScale(0, BigDecimal.ROUND_HALF_UP);
					return goodValue;
				}
			}
			return goodValue;
		} catch (Exception e) {
			logger.error("根据用户VIP等级获取商品折扣后价格异常,memberId={},goodId={}" , memberId,goodId);
		}
		return goodValue;
	}
	
	@Override
	public Boolean isLevelUp(Long memberId)throws ManagerException{
		
		MemberVip memberVip = this.selectRecentMemberVipByMemberId(memberId);
		if(memberVip == null){
			return false;
		}
		Integer year = Integer.parseInt(memberVip.getMonth().substring(0, 4)) ;
		if(year != DateUtils.getYear(DateUtils.getCurrentDate())){
			return false;
		}
		Integer month = Integer.parseInt(memberVip.getMonth().substring(4, 6)) ;
		if(month!=DateUtils.getMonth(DateUtils.getCurrentDate())-1){
			return false;
		}
		if(memberVip.getVipLevel()<=memberVip.getLastVipLevel()){
			return false;
		}
		return true;
	}
	
	
	/**
	 * 成长记录
	 * 
	 * @param query
	 * @return
	 * @throws ManagerException
	 */

	@Override
	public Page<MemberVip> queryMemberVipList(BaseQueryParam query) throws ManagerException {
		try {
			List<MemberVip> memberVip = memberVipMapper.selectMemberVipList(query);
			long count = memberVipMapper.selectMemberVipListCount(query);
			Page<MemberVip> page = new Page<MemberVip>();
			page.setData(memberVip);
			page.setiDisplayLength(query.getPageSize());
			page.setPageNo(query.getCurrentPage());
			page.setiTotalDisplayRecords(count);
			page.setiTotalRecords(count);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	/** 每次查询数量 **/
	static final int numConstant = 3000;

	static final int startConstant = 0;
	
	@Override
	public void memberLevelUpHandle(){
		
		int startNum = startConstant;
		int num = numConstant;
		int size = numConstant;
		Map<String, Object> map = Maps.newHashMap();
		for (int i = 0; size > 0; i++) {
			map.put("startNum", startNum + i * num);
			map.put("num", num);
			List<MemberVip>  memberVipList = memberVipMapper
					.memberLevelUpHandle(map);
			size = memberVipList.size();
			this.doHandle(memberVipList);
		}
	}
	
	private void doHandle(List<MemberVip>  memberVipList){
		
			for(MemberVip m :memberVipList){
				try{
					String key = RedisConstant.MEMBER_LEVEL_UP_GIFT+RedisConstant.REDIS_SEPERATOR +m.getMemberId() ;
					boolean isExit = RedisManager.isExitByObjectKey(key);
					if(isExit){
						continue;
					}
					
					Integer year = Integer.parseInt(m.getMonth().substring(0, 4)) ;
					if(year != DateUtils.getYear(DateUtils.getCurrentDate())){
						continue;
					}
					Integer month = Integer.parseInt(m.getMonth().substring(4, 6)) ;
					if(month!=DateUtils.getMonth(DateUtils.getCurrentDate())-1){
						continue;
					}
					if(m.getVipLevel()<=m.getLastVipLevel()){
						continue;
					}
					for(int i=1;m.getVipLevel()>=m.getLastVipLevel()+i;i++){
						//支持升多级
						this.doHandleForLevel(m.getLastVipLevel()+i, m.getMemberId());
					}
					
					RedisManager.putObject(key, true);
					RedisManager.expireObject(key, 2160000);
					
				}catch(Exception e){
					logger.error("会员升级赠送礼包异常,memberId={},vip={}" ,m.getMemberId(),m.getVipLevel());
				}
				
				
			}
	}

	private void doHandleForLevel(Integer vipLevel,Long memberId) throws ManagerException{
		
		VipDiscount vipDis =vipDiscountManager.getVipDiscountByVip(vipLevel);		
		String mapJson = vipDis.getLevelUpGift();
		Map map = JSON.parseObject(mapJson);
		if(map.containsKey("cash")){
			String cashId = map.get("cash").toString();
			Coupon cashC = couponManager.receiveCoupon(memberId, -1L, Long.parseLong(cashId), -1L);
			if (cashC == null) {
				// 现金券赠送失败;
				logger.error("会员升级现金券赠送失败，接口返回Coupon=null!, memberId={}, templateId={}", memberId,
						 cashId);
			}
		}
		if(map.containsKey("income")){
			String incomeId = map.get("income").toString();
			Coupon incomeC = couponManager.receiveCoupon(memberId, -1L, Long.parseLong(incomeId), -1L);
			if (incomeC == null) {
				// 收益券赠送失败;
				logger.error("会员升级收益券赠送失败，接口返回Coupon=null!, memberId={}, templateId={}", memberId,
						 incomeId);
			}
			
		}
	}

	@Override
	public Integer selectMemberVipNewByMemberId(Long memberId) throws ManagerException {
		try {
			return memberVipMapper.selectMemberVipNewByMemberId(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public VipDiscount getVipDiscountByVip (Integer vipLevel){
		try{
			VipDiscount vipDiscount=vipDiscountManager.getVipDiscountByVip(vipLevel);
			return vipDiscount;
		}catch(Exception e){
			logger.error("查询用户VIP折扣异常，vipLevel：" + vipLevel, e);
			return null;
		}
		
	}


	@Override
	public BigDecimal getGoodsDiscountByInfo(String goodsDiscountInfo,Integer vipLevel) {
		if(StringUtils.isBlank(goodsDiscountInfo)){
			return  BigDecimal.ONE;
		}
		try{
			String[] discountInfos = goodsDiscountInfo.split(DISCOUNT_SPLIT);
			for(String discountInfo : discountInfos){
				String[] infoArray = discountInfo.split(LEVEL_SPLIT);
				//判断会员等级是否在此折扣区间
				if(Integer.valueOf(infoArray[0])<=vipLevel&&Integer.valueOf(infoArray[1])>=vipLevel){
					return new BigDecimal(infoArray[2]).divide(new BigDecimal("10"), 3, BigDecimal.ROUND_UP);
				}
			}
		}catch(Exception e){
			logger.error("获取折扣异常，折扣信息：" + goodsDiscountInfo+",vipLevel:"+vipLevel, e);
		}
		return  BigDecimal.ONE;
	}
	
	
}
