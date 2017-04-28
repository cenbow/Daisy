/**
 * 
 */
package com.yourong.core.sh.model;

import java.math.BigDecimal;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年10月25日上午10:21:42
 */
public class OrderForCreat extends AbstractBaseObject {

	/**
     * 会员Id
     */
	private Long memberId;
	 /**
     * 商品Id
     */
    private Long goodId;
    
    /**
     * 产品类型 1-投资专享 2-虚拟卡券 3-超值实物
     */
    private Integer goodsType;
    
    /**
     * 商品数量
     */
    private Integer goodsCount;
    
    /**
     * 金额(所需人气值)
     */
    private BigDecimal amount;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 收货人
     */
    private String receiver;
    /**
     * 收货地址省市区
     */
    private String areaFullName;
    /**
     * 收货地址详细地址
     */
    private String address;
    /**
     * 充值账户
     */
    private String rechargeCard;
    
    
	/**
	 * @return the memberId
	 */
	public Long getMemberId() {
		return memberId;
	}
	/**
	 * @param memberId the memberId to set
	 */
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	
	/**
	 * @return the goodId
	 */
	public Long getGoodId() {
		return goodId;
	}
	/**
	 * @param goodId the goodId to set
	 */
	public void setGoodId(Long goodId) {
		this.goodId = goodId;
	}
	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}
	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	/**
	 * @return the receiver
	 */
	public String getReceiver() {
		return receiver;
	}
	/**
	 * @param receiver the receiver to set
	 */
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	/**
	 * @return the areaFullName
	 */
	public String getAreaFullName() {
		return areaFullName;
	}
	/**
	 * @param areaFullName the areaFullName to set
	 */
	public void setAreaFullName(String areaFullName) {
		this.areaFullName = areaFullName;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the goodsType
	 */
	public Integer getGoodsType() {
		return goodsType;
	}
	/**
	 * @param goodsType the goodsType to set
	 */
	public void setGoodsType(Integer goodsType) {
		this.goodsType = goodsType;
	}
	/**
	 * @return the rechargeCard
	 */
	public String getRechargeCard() {
		return rechargeCard;
	}
	/**
	 * @param rechargeCard the rechargeCard to set
	 */
	public void setRechargeCard(String rechargeCard) {
		this.rechargeCard = rechargeCard;
	}
	/**
	 * @return the goodsCount
	 */
	public Integer getGoodsCount() {
		return goodsCount;
	}
	/**
	 * @param goodsCount the goodsCount to set
	 */
	public void setGoodsCount(Integer goodsCount) {
		this.goodsCount = goodsCount;
	}
	
}
