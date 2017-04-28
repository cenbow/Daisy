/**
 * 
 */
package com.yourong.api.dto;

import java.util.List;

import com.yourong.common.pageable.Page;
import com.yourong.core.tc.model.biz.TransactionForMemberCenter;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年7月13日上午9:53:21
 */
public class ContractSignDto {

	/** 签署方式 **/
	private int signWay;


	/** 未签署合同信息 **/
	private Page<UnSignContractDto> unSingList;


	/**
	 * @return the signWay
	 */
	public int getSignWay() {
		return signWay;
	}


	/**
	 * @param signWay the signWay to set
	 */
	public void setSignWay(int signWay) {
		this.signWay = signWay;
	}


	/**
	 * @return the unSingList
	 */
	public Page<UnSignContractDto> getUnSingList() {
		return unSingList;
	}


	/**
	 * @param unSingList the unSingList to set
	 */
	public void setUnSingList(Page<UnSignContractDto> unSingList) {
		this.unSingList = unSingList;
	}
	
}
