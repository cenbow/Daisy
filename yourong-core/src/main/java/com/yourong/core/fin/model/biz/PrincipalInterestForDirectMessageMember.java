/**
 * 
 */
package com.yourong.core.fin.model.biz;

import java.math.BigDecimal;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年3月16日上午9:26:52
 */
public class PrincipalInterestForDirectMessageMember extends AbstractBaseObject{

	/**应付本金**/
    private BigDecimal principal;

    /**应付利息**/
    private BigDecimal interest;
    
    /* 项目个数 */
	private Integer projectNum;
	
	/**P2P项目借款人ID**/
    private Long memberId;

	/**
	 * @return the principal
	 */
	public BigDecimal getPrincipal() {
		return principal;
	}

	/**
	 * @param principal the principal to set
	 */
	public void setPrincipal(BigDecimal principal) {
		this.principal = principal;
	}

	/**
	 * @return the interest
	 */
	public BigDecimal getInterest() {
		return interest;
	}

	/**
	 * @param interest the interest to set
	 */
	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}

	/**
	 * @return the projectNum
	 */
	public Integer getProjectNum() {
		return projectNum;
	}

	/**
	 * @param projectNum the projectNum to set
	 */
	public void setProjectNum(Integer projectNum) {
		this.projectNum = projectNum;
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
	
    
    
}
