/**
 * 
 */
package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.DateUtils;
import com.yourong.core.bsc.model.BscAttachment;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年10月26日下午1:37:13
 */
public class ShopOrderDetilDto extends AbstractBaseObject{
	
	/**
     * 订单id
     */
    private Long orderId;
	
	/**
     * 产品名称
     */
    private String goodsName;
    
    
    /**
     * 状态 1-待发放 2-已发放 3-已取消
     */
    private Integer status;
    
    /**
     * 订单类型 1-投资专享 2-虚拟卡券 3-超值实物
     */
    private Integer orderType;
    
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 商品图片
     */
    private String image;
    /**
     * 商品图片
     */
    private List<BscAttachment> imageList;
    
    /**
     * 金额(所需人气值)
     */
    private String amount;
    
    /**
     * 收货人
     */
    private String receiver;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 收货地址省市区
     */
    private String areaFullName;
    /**
     * 收货地址详细地址
     */
    private String address;
    
    /**
     * 发货信息备注(可展示给客户用)
     */
    private String sendRemark;
    
    /**
     * 充值类型1-直冲，2-卡密
     */
    private Integer rechargeType;
    
    /**
     * 充值账号
     */
    private String rechargeCard;

	/**
	 * @return the orderId
	 */
	public Long getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	/**
	 * @return the goodsName
	 */
	public String getGoodsName() {
		return goodsName;
	}

	/**
	 * @param goodsName the goodsName to set
	 */
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @return the createTime
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime the createTime to set
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public String getCreateTimeStr(){
		if (createTime != null) {
			return DateUtils.formatDatetoString(createTime,
					DateUtils.DATE_FMT_3);
		}
		return null;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return the imageList
	 */
	public List<BscAttachment> getImageList() {
		return imageList;
	}

	/**
	 * @param imageList the imageList to set
	 */
	public void setImageList(List<BscAttachment> imageList) {
		this.imageList = imageList;
	}

	/**
	 * @return the amount
	 */
	public String getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
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
	 * @return the sendRemark
	 */
	public String getSendRemark() {
		return sendRemark;
	}

	/**
	 * @param sendRemark the sendRemark to set
	 */
	public void setSendRemark(String sendRemark) {
		this.sendRemark = sendRemark;
	}

	/**
	 * @return the rechargeType
	 */
	public Integer getRechargeType() {
		return rechargeType;
	}

	/**
	 * @param rechargeType the rechargeType to set
	 */
	public void setRechargeType(Integer rechargeType) {
		this.rechargeType = rechargeType;
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
	 * @return the orderType
	 */
	public Integer getOrderType() {
		return orderType;
	}

	/**
	 * @param orderType the orderType to set
	 */
	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

}
