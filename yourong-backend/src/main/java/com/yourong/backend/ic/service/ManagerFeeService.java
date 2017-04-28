/**
 * 
 */
package com.yourong.backend.ic.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.fin.model.ProjectFee;
import com.yourong.core.fin.model.ManagementFeeAgreement;
import com.yourong.core.tc.model.biz.ContractBiz;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年4月29日下午4:19:35
 */
public interface ManagerFeeService {

	public ProjectFee selectBorrowerInformation(Long projectId);
	
	public List<ManagementFeeAgreement> selectAgreementInformation(Long projectId);
	
	
	/**
	 * P2P查看合同
	 * @param memberId 
	 * @param annualizedRate 
	 * @return
	 */
	public ResultDO<ContractBiz> p2pViewContract(Long transactionId);
	
	/**
	 * 下载合同
	 * @param memberId 
	 * @param annualizedRate 
	 * @return
	 */
	public String agreementDown(Long projectId,HttpServletResponse resp);
	
	
	
}
