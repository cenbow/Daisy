package com.yourong.core.ic.manager;

import java.math.BigDecimal;
import java.util.Map;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.LeaseBonus;
import com.yourong.core.ic.model.biz.LeaseBonusForFront;

public interface LeaseBonusManager {
	int deleteByPrimaryKey(Long id) throws ManagerException;

	int insert(LeaseBonus record) throws ManagerException;

	int insertSelective(LeaseBonus record) throws ManagerException;

	LeaseBonus selectByPrimaryKey(Long id) throws ManagerException;

	int updateByPrimaryKeySelective(LeaseBonus record) throws ManagerException;

	int updateByPrimaryKey(LeaseBonus record) throws ManagerException;

	Page<LeaseBonus> findByPage(Page<LeaseBonus> pageRequest, Map<String, Object> map) throws ManagerException;

	int batchDelete(long[] ids) throws ManagerException;

	/** 根据项目查询租赁分红记录 **/
	LeaseBonus findLeaseBonusByProjectId(long projectId) throws ManagerException;

	/**
	 * 平台垫付租赁收益分红回调后处理流程
	 * @param tradeNo
	 * @param processStatus
	 * @return
	 * @throws Exception
	 */
	public ResultDO<LeaseBonus> afterLeaseBonusCollectNotifyProcess(String tradeNo, String processStatus) throws Exception;

    /**
     * 添加租赁结算,更新租赁分红数据
     * @param id
     * @return
     */
    int updateForLeaseSettlement(LeaseBonus leaseBonus)throws ManagerException;

	/**
	 * 前台页面数据
	 * 
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	Page<LeaseBonusForFront> findByFrontPage(BaseQueryParam baseQueryParam) throws ManagerException;

	/**
	 * 更新租赁分红状态
	 * 
	 * @param id
	 * @return
	 */
	int updateStatusByLeaseBonusId(long id) throws ManagerException;

	/**
	 * 删除租赁记录时更新租赁分红总额
	 * 
	 * @param totalIncome
	 * @param id
	 * @return
	 */
	int updateTotalIncomeByLeaseBonusId(BigDecimal totalIncome, long id) throws ManagerException;

	/**
	 * 编辑租赁记录时更新租赁分红总额
	 * 
	 * @param map
	 * @return
	 */
	int updateTotalIncomeForEditLeaseDetail(Map<String, Object> map) throws ManagerException;
	
	/**
	 * 通过项目查询租赁分红记录
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 */
	LeaseBonus getByProject(Long projectId) throws ManagerException;
	
}