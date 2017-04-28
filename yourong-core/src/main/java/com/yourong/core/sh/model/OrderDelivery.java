/**
 * 
 */
package com.yourong.core.sh.model;

import com.yourong.common.domain.AbstractBaseObject;

public class OrderDelivery extends AbstractBaseObject {

	private Long id;
	/**
     * 会员Id
     */
	private Long memberId;

    /**
     * 手机号
     */
    private String mobile;
    /**
     * 收货人
     */
    private String receiver;
    /**
     * 收货地址省
     */
    private String province;
    /**
     * 收货地址市
     */
    private String city;
    /**
     * 收货地址区
     */
    private String district;
    /**
     * 收货地址详细地址
     */
    private String address;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
    
}
