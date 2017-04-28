/**
 * 
 */
package com.yourong.core.fin.model;

import com.yourong.core.uc.model.Member;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年5月3日上午10:11:13
 */
public class ManagementFeeAgreement {

	/** 用户ID **/
	private Long memberId;
	
	/** 用户信息 **/
	private Member member;
    
    /**交易号**/
    private Long id;

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
	 * @return the member
	 */
	public Member getMember() {
		return member;
	}

	/**
	 * @param member the member to set
	 */
	public void setMember(Member member) {
		this.member = member;
	}

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
    
}
