package com.yourong.core.tc.model.biz;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.enums.StatusEnum;
import com.yourong.core.tc.model.Order;

/**
 * 
 * @desc 使用存钱罐余额发起代收交易biz
 * @author wangyanji 2016年7月20日下午6:23:03
 */
public class TradeBiz extends AbstractBaseObject {

	/**
	 * 订单
	 */
	private Order order;

	/**
	 * 是否委托支付
	 */
	private Integer isWithholdAuthority;

	/**
	 * 是否现金券全额支付
	 */
	private boolean isAllCashCouponPay = false;

	/**
	 * 重定向链接
	 */
	private String redirectUrl;

	/**
	 * 是否第一次请求
	 */
	private boolean isNotFirstRequest = false;

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public Integer getIsWithholdAuthority() {
		return isWithholdAuthority;
	}

	public void setIsWithholdAuthority(Integer isWithholdAuthority) {
		this.isWithholdAuthority = isWithholdAuthority;
	}

	public void setIsWithholdAuthority(boolean isWithholdAuthority) {
		if (isWithholdAuthority) {
			this.isWithholdAuthority = StatusEnum.WITHHOLD_AUTHORITY_FLAG_Y.getStatus();
		} else {
			this.isWithholdAuthority = StatusEnum.WITHHOLD_AUTHORITY_FLAG_N.getStatus();
		}
	}

	public boolean isAllCashCouponPay() {
		return isAllCashCouponPay;
	}

	public void setAllCashCouponPay(boolean isAllCashCouponPay) {
		this.isAllCashCouponPay = isAllCashCouponPay;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public boolean isNotFirstRequest() {
		return isNotFirstRequest;
	}

	public void setNotFirstRequest(boolean isNotFirstRequest) {
		this.isNotFirstRequest = isNotFirstRequest;
	}

}
