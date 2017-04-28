/**
 * 
 */
package com.yourong.core.tc.model;

import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * @desc 合同签署信息
 * @author zhanghao
 * 2016年7月22日下午4:47:20
 */
public class ContractSignDto extends AbstractBaseObject{

	
	/** 订单号 **/
	private Long id;

	/** 订单id **/
	private Long orderId;
	
	/** 外部订单号 **/
	private String orderNo;

	/** 用户ID **/
	private Long memberId;

	/** 项目ID **/
	private Long projectId;

	/** 项目名称 **/
	private String projectName;
	
	/** 会员名称 **/
	private String memberName;
	
	/** 用户手机 **/
	private Long memberMoible;
	
	/**签署状态（0-初始化，1-未签署，2-已签署，3-已过期）**/
	private Integer signStatus;
	/** 合同生成时间 **/
	private Date  contractTime;
	/** 签署时间 **/
	private Date signTime;
	
    /**签署方式  0手动，1自动**/
    private Integer signWay;
    
    /**乙方签署状态**/
    private Integer secondStatus;
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
	 * @return the orderNo
	 */
	public String getOrderNo() {
		return orderNo;
	}
	/**
	 * @param orderNo the orderNo to set
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
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
	 * @return the projectId
	 */
	public Long getProjectId() {
		return projectId;
	}
	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}
	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	/**
	 * @return the memberName
	 */
	public String getMemberName() {
		return memberName;
	}
	/**
	 * @param memberName the memberName to set
	 */
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	/**
	 * @return the memberMoible
	 */
	public Long getMemberMoible() {
		return memberMoible;
	}
	/**
	 * @param memberMoible the memberMoible to set
	 */
	public void setMemberMoible(Long memberMoible) {
		this.memberMoible = memberMoible;
	}
	/**
	 * @return the signStatus
	 */
	public Integer getSignStatus() {
		return signStatus;
	}
	/**
	 * @param signStatus the signStatus to set
	 */
	public void setSignStatus(Integer signStatus) {
		this.signStatus = signStatus;
	}
	/**
	 * @return the contractTime
	 */
	public Date getContractTime() {
		return contractTime;
	}
	/**
	 * @param contractTime the contractTime to set
	 */
	public void setContractTime(Date contractTime) {
		this.contractTime = contractTime;
	}
	/**
	 * @return the signTime
	 */
	public Date getSignTime() {
		return signTime;
	}
	/**
	 * @param signTime the signTime to set
	 */
	public void setSignTime(Date signTime) {
		this.signTime = signTime;
	}
	/**
	 * @return the signWay
	 */
	public Integer getSignWay() {
		return signWay;
	}
	/**
	 * @param signWay the signWay to set
	 */
	public void setSignWay(Integer signWay) {
		this.signWay = signWay;
	}
	public Integer getSecondStatus() {
		return secondStatus;
	}
	public void setSecondStatus(Integer secondStatus) {
		this.secondStatus = secondStatus;
	}
}
