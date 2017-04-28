package com.yourong.core.tc.manager;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.tc.model.biz.ContractBiz;

/**
 * 合同相关mananger
 * @author Leon Ray
 * 2014年10月21日-下午1:43:31
 */
public interface ContractManager {

	
	/**
	 * 保存合同
	 * @param transactionId
	 * @return
	 * @throws ManagerException
	 */
	public BscAttachment saveContract(Long transactionId, String fromSys) throws ManagerException;

	/**
	 * 获取预览合同信息
	 * @param memberId
	 * @param projectId
	 * @param investAmount
	 * @param annualizedRate 
	 * @return
	 * @throws ManagerException
	 */
	public ContractBiz getPreviewContract(Long memberId, Long projectId,
			BigDecimal investAmount, BigDecimal annualizedRate, Date orderTime) throws ManagerException;

	/**
	 * 查看合同
	 * @param transactionId
	 * @return
	 * @throws ManagerException
	 */
	public ContractBiz getViewContract(Long transactionId) throws ManagerException;

	/**
	 * 创建合同页
	 * @param htmlPath
	 * @param vmTemPath
	 * @param params
	 */
	public void createHtmlByVelocity(String htmlPath, String vmTemPath, Map<String, Object> params);
	
	/**
	 * 创建合同PDF
	 * @param htmlPath
	 * @param path
	 * @param fileName
	 * @param attachment
	 */
	public void generatePdfContract(String htmlPath, String path, String fileName, BscAttachment attachment);
	
}