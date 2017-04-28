/**
 * 
 */
package com.yourong.core.uc.model;

import java.math.BigDecimal;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年10月24日下午1:48:20
 */
public class VipDiscount extends AbstractBaseObject{
	

	/**id**/
    private Long id;

    /**会员等级**/
    private Integer vipLevel;
    
    
    /**等级优惠**/
    private BigDecimal rankPreferenceDiscount;
    
    /**vip相关优惠**/
    private String infomation;
    
    /**vip升级礼包**/
    private String levelUpGift;


	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}


	/**
	 * @return the vipLevel
	 */
	public Integer getVipLevel() {
		return vipLevel;
	}


	/**
	 * @param vipLevel the vipLevel to set
	 */
	public void setVipLevel(Integer vipLevel) {
		this.vipLevel = vipLevel;
	}


	/**
	 * @return the rankPreferenceDiscount
	 */
	public BigDecimal getRankPreferenceDiscount() {
		return rankPreferenceDiscount;
	}


	/**
	 * @param rankPreferenceDiscount the rankPreferenceDiscount to set
	 */
	public void setRankPreferenceDiscount(BigDecimal rankPreferenceDiscount) {
		this.rankPreferenceDiscount = rankPreferenceDiscount;
	}


	/**
	 * @return the infomation
	 */
	public String getInfomation() {
		return infomation;
	}


	/**
	 * @param infomation the infomation to set
	 */
	public void setInfomation(String infomation) {
		this.infomation = infomation;
	}


	public String getLevelUpGift() {
		return levelUpGift;
	}


	public void setLevelUpGift(String levelUpGift) {
		this.levelUpGift = levelUpGift;
	}
    
}
