package com.yourong.core.fin.manager;

import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.biz.PayPrincipalInterestBiz;

public interface PayPrincipalInterestManager {
	/**
	 * 托管还本付息列表
	 * 
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	public Page<PayPrincipalInterestBiz> findByPage(
			Page<PayPrincipalInterestBiz> pageRequest, Map<String, Object> map)
			throws ManagerException;

	/**
	 * 根据项目获取还本付息信息,债权
	 * 
	 * @param project
	 * @return
	 * @throws ManagerException
	 */
	public void findPayPrincipalAndInterestByProject(
			PayPrincipalInterestBiz payPrincipalInterestBiz)
			throws ManagerException;

	/**
	 * 还款本息数据统计根据还款状态
	 * 
	 * @param status
	 * @return
	 */
	PayPrincipalInterestBiz findTotalPrincipalAndInterestByStatus(
			Map<String, Object> map)throws ManagerException;
	
	/**
	 * 根据项目获取还本付息信息，直投项目
	 * 
	 * @param project
	 * @return
	 * @throws ManagerException
	 */
	public void findPayPrincipalAndInterestByDirectProject(
			PayPrincipalInterestBiz payPrincipalInterestBiz)
			throws ManagerException;
	
	/**
	 * 距离到期日3天的项目 发送邮件
	 * @param map
	 * @return
	 */
	Integer sendMailThree2EndDateProject() throws ManagerException;
	
	/**
	 * 距离到期日3天的直投项目 发送邮件
	 * @param map
	 * @return
	 */
	Integer sendMailThree2EndDateDirectProject() throws ManagerException;
	
	/**
	 * 距离到期日1天的项目 发送短信
	 * @param map
	 * @return
	 */
	Integer sendSmsOne2EndDateProject() throws ManagerException;
	
	/**
	 * 距离到期日1天的直投项目 短信通知借款人
	 * @param map
	 * @return
	 */
	Integer sendSmsOne2EndDateDirectProject() throws ManagerException;
	
	/**
	 * 距离到期日3天的直投项目 短信通知借款人
	 * @param map
	 * @return
	 */
	Integer sendSmsThree2EndDateDirectProject() throws ManagerException;
	

}
