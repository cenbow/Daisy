package com.yourong.core.ic.manager;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.ic.model.DebtBiz;
import com.yourong.core.ic.model.DebtInterest;

public interface DebtManager {
	/**
	 * 单个删除债权
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public int deleteByPrimaryKey(Long id) throws ManagerException;

	/**
	 * 插入债权信息
	 * @param record
	 * @return
	 * @throws ManagerException
	 */
	public int insert(Debt record) throws ManagerException;

	public int insertSelective(Debt record) throws ManagerException;

	public Debt selectByPrimaryKey(Long id) throws ManagerException;

	public int updateByPrimaryKeySelective(Debt record) throws ManagerException;
	
	/**
	 * 紧急修改更新字段 
	 * @param record
	 * @return
	 * @throws ManagerException
	 */
	public int emergencyUpdateByPrimaryKeySelective(Debt record) throws ManagerException;

	public int updateByPrimaryKey(Debt record) throws ManagerException;

	public Page<Debt> findByPage(Page<Debt> pageRequest, Map<String, Object> map)
			throws ManagerException;

	/**
	 * 批量删除债权
	 * @param ids
	 * @return
	 * @throws ManagerException
	 */
	public int batchDelete(long[] ids) throws ManagerException;
	
	
	/**
	 * 根据公司债权编号获取债权信息
	 * @param serialNumber
	 * @return
	 */
	public Debt findDebtBySerialNumber(String serialNumber) throws ManagerException;
	
	/**
	 * 根据公司债权编号获取债权完整信息
	 * @param serialNumber
	 * @return
	 * @throws ManagerException
	 */
	public DebtBiz getFullDebtInfoBySerialNumber(String serialNumber) throws ManagerException;

	
	/**
	 * 获得公司债权编号&借款人
	 * @param serialNumber
	 * @return
	 * @throws ManagerException
	 */
	public List<Map<String, String>> findSerialNumberAndMemberName(String serialNumber) throws ManagerException;
	
	/**
	 * 根据编号更新债权状态
	 * @param status
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public int updateDebtStatusById(Integer status, Long id) throws ManagerException;
	
	/**
	 * 根据主键删除（逻辑）
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public int deleteByDebtId(Long id) throws ManagerException;
	
	public int findDebtByOriginalDebtNumber(String originalDebtNumber) throws ManagerException;
	
	/**
	 * 根据编号获得债权业务信息
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public DebtBiz getFullDebtInfoById(Long id) throws ManagerException;
	
	/**
	 * 根据用户姓名或用户手机模糊查询对应债权 
	 * @param map
	 * @return
	 */
	List<Long> findDebtIdByMemberInfo(@Param("map")Map<String, Object> map) throws ManagerException;
	
	/**
	 * 根据借款人姓名模糊查询对应债权 
	 * @param map
	 * @return
	 */
	List<Long> findDebtIdByMemberName(@Param("map")Map<String, Object> map) throws ManagerException;
	
	/**
	 * 
	 * @param debtId
	 * @return
	 */
	public DebtBiz getDebtInfoToZhongNiu(Long debtId) throws ManagerException;
	
	/**
	 * 债权信息查询
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	Page<Debt> findDebtInfoByPage(Page<Debt> pageRequest, Map<String, Object> map) throws ManagerException;

	/**
	 * /根据主键修改风控备注
	 * @param record
	 * @return
	 * @throws ManagerException
	 */
	public int updateControlRemarksById(Debt record) throws ManagerException;
	
	/**
	 * 根据债权id查询债权及项目状态
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	Debt findDebtAndProjectStatusByDebtId(Long id)throws ManagerException;
	
	 /**
     * 根据项目id获取债权
     * @return
     */
	public Debt selectEnterpriseIdByProjectId(Long projectId)throws ManagerException;
	
	/**
	 * 统计用户出借人身份下的债权数量
	 * @param memberId
	 * @return
	 */
	public boolean countDebtByLenderId(Long memberId)throws ManagerException;
	/**
	 * 
	 * @Description:自动分期
	 * @param returnType
	 * @param interestFrom
	 * @param sDate
	 * @param eDate
	 * @return
	 * @author: chaisen
	 * @time:2016年5月4日 上午10:59:17
	 */
	public List<DebtInterest> autoInterest(String returnType, int interestFrom, String sDate, String eDate);
}
