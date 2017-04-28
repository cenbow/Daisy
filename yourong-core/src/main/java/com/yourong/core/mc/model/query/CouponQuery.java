package com.yourong.core.mc.model.query;

import org.apache.ibatis.type.Alias;

import com.yourong.common.domain.BaseQueryParam;
/**
 * 我的红包的查询条件
 * 
 * @author Administrator
 *
 */
@Alias("CouponQuery")
public class CouponQuery extends BaseQueryParam {

	private Integer status;// 优惠券状态 0-未领取 1-已领取，未使用 2-已使用 3-未领取，已过期 4-已领取，已过期
							// 5-使用中
	private Integer couponType;// 优惠券类型 1-现金券 2-收益券
	
	private Integer way; //1-web 2-app;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getCouponType() {
		return couponType;
	}

	public void setCouponType(Integer couponType) {
		this.couponType = couponType;
	}

	public Integer getWay() {
		return way;
	}

	public void setWay(Integer way) {
		this.way = way;
	}
	
	

}
