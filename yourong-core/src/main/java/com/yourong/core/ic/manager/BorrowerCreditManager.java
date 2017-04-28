package com.yourong.core.ic.manager;

import java.math.BigDecimal;
import java.util.List;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.ic.model.BorrowerCredit;

/**
 * 
 * @Description 借款人授信额度信息manager
 * @author luwenshan
 * @time 2016年12月9日 下午3:57:23
 */
public interface BorrowerCreditManager {
	

    /**
     * 
     * @Description 根据借款人信息查询借款人授信额度信息
     * @param borrowerId 借款人ID
     * @param borrowerType 借款人类型
     * @param openPlatformKey 渠道商类型
     * @return
     * @author luwenshan
     * @time 2016年11月24日 上午11:59:24
     */
    public BorrowerCredit selectByBorrower(Long borrowerId, Integer borrowerType, String openPlatformKey) throws ManagerException;
    
    /**
     * 
     * @Description 根据借款人信息修改借款人授信额度信息
     * @param borrowerCredit
     * @return
     * @author luwenshan
     * @time 2016年11月24日 上午11:57:55
     */
    public int updateByBorrower(BorrowerCredit borrowerCredit) throws ManagerException;
    
    /**
	 * 
	 * @Description 保存借款人授信额度信息
	 * @param borrowerCredit
	 * @return
	 * @author luwenshan
	 * @time 2016年11月24日 上午11:57:43
	 */
    public int saveBorrower(BorrowerCredit borrowerCredit) throws ManagerException;
	
    /**
     * 根据借款人信息获取个人用户或企业用户的存续量
     * 
     * @param borrowerId
     * @param borrowerType
     * @param investType
     * @return
     */
    public BigDecimal getMemberOrEnterprisePayablePrincipal(Long borrowerId, Integer borrowerType, Integer investType) throws ManagerException;
    
    /**
     * 根据借款人信息获取渠道商用户的存续量
     * 
     * @param openPlatformKey
     * @param investType
     * @return
     */
    public BigDecimal getChannelPayablePrincipal(String openPlatformKey, Integer investType) throws ManagerException;
    
    /**
     * 
     * 增加直投项目借款人存续量
     * 
     * @param borrowerId
     * @param borrowerType
     * @param openPlatformKey
     * @param projectTotalAmount
     */
    public void increaseBorrowerCredit(Long borrowerId, Integer borrowerType, String openPlatformKey, BigDecimal projectTotalAmount) throws ManagerException;
    
    /**
     * 
     * 减少直投项目借款人存续量
     * 
     * @param borrowerId
     * @param borrowerType
     * @param openPlatformKey
     * @param projectTotalAmount
     */
    public void reduceBorrowerCredit(Long borrowerId, Integer borrowerType, String openPlatformKey, BigDecimal projectTotalAmount) throws ManagerException;
    
    /**
     * 根据借款人信息汇总个人用户或企业用户的存续量信息
     * 
     * @param borrowerCredit
     * @return
     */
    public List<BorrowerCredit> getMemberOrEnterpriseBorrowerCredit(Long borrowerId, Integer borrowerType, Integer investType) throws ManagerException;
    
    /**
     * 根据借款人信息汇总渠道商用户的存续量
     * 
     * @param borrowerCredit
     * @return
     */
    public List<BorrowerCredit> getChannelBorrowerCredit(String openPlatformKey, Integer investType) throws ManagerException;
	
}
