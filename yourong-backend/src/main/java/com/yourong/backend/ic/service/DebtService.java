package com.yourong.backend.ic.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.ic.model.DebtBiz;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.uc.model.MemberBaseBiz;

public interface DebtService {
	/** 删除 **/
	Integer deleteByPrimaryKey(Long id);//todo 

	/** 插入 **/
	Integer insert(Debt record);
	
	Integer insertSelective(Debt record);

	/** 更新有选择的 **/
	Integer updateByPrimaryKeySelective(Debt record);

	/** 查询根据主键 **/
	Debt selectByPrimaryKey(Long id);

	/** 更新根据主键 **/
	Integer updateByPrimaryKey(Debt record);

	/** 分页查询 **/
	Page<Debt> findByPage(Page<Debt> pageRequest, Map<String, Object> map);

	/** 批量删除 **/
	Integer batchDelete(long[] ids);
	
	DebtBiz getFullDebtInfoBySerialNumber(String serialNumber);

	MemberBaseBiz selectMemberBaseBiz(Long memberId);
	
	/**
	 * 获得公司债权编号&借款人
	 * @param serialNumber
	 * @return
	 * @throws ManagerException
	 */
	List<Map<String, String>> findSerialNumberAndMemberName(String serialNumber);
	
	public ResultDO<Debt> insertDebtInfo(DebtBiz debtBiz,String appPath) throws ManagerException;
	
	public ResultDO<Debt> updateDebtInfo(DebtBiz debtBiz,String appPath) throws ManagerException;
	
	public Integer deleteByDebtId(Long id) throws ManagerException;

	/**
	 * 紧急更新债权信息
	 * @param debtBiz
	 * @param appPath
	 * @return
	 * @throws ManagerException
	 */
	ResultDO<Debt> emergencyUpdateDebtInfo(DebtBiz debtBiz, String appPath) throws ManagerException;
	
	/**
	 * 债权信息查询
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	Page<Debt> findDebtInfoByPage(Page<Debt> pageRequest, Map<String, Object> map);
	
	/**
	 * 根据债权ID查询付息记录
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	Page<DebtInterest> showInterestRecord(Page<DebtInterest> pageRequest, Map<String, Object> map);
	
	/**
	 * 添加风控备注
	 * @param id
	 * @param newControlRemarks
	 * @return
	 * @throws ManagerException 
	 */
	public ResultDO<Object> addControlRemarks(Long id, String newControlRemarks) throws ManagerException;
	
	/**
	 * 根据债权id查询债权及项目状态
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public Debt findDebtAndProjectStatusByDebtId(Long id);

	/**
	 * 根据企业id查询用户信息
	 * @param enterpriseId
	 * @return
	 */
	MemberBaseBiz getMemberBaseBizByEnterpriseId(Long enterpriseId);
	/**
	 * 
	 * @Description:自动分期
	 * @param returnType
	 * @param interestFrom
	 * @param sDate
	 * @param eDate
	 * @return
	 * @author: chaisen
	 * @time:2016年5月4日 上午10:43:24
	 */
	List<DebtInterest> autoInterest(String returnType, int interestFrom, String sDate, String eDate);

}
