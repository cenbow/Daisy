package com.yourong.core.ic.manager;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.ProjectInterestBiz;

public interface DebtInterestManager {
	/**
	 * 单个删除债权本息
	 * 
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	int deleteByPrimaryKey(Long id) throws ManagerException;

	/**
	 * 插入债权本息信息
	 * 
	 * @param record
	 * @return
	 * @throws ManagerException
	 */
	int insert(DebtInterest record) throws ManagerException;

	int insertSelective(DebtInterest record) throws ManagerException;

	DebtInterest selectByPrimaryKey(Long id) throws ManagerException;

	int updateByPrimaryKeySelective(DebtInterest record) throws ManagerException;

	int updateByPrimaryKey(DebtInterest record) throws ManagerException;

	Page<DebtInterest> findByPage(Page<DebtInterest> pageRequest, Map<String, Object> map)
			throws ManagerException;

	/**
	 * 批量删除债权本息
	 * 
	 * @param ids
	 * @return
	 * @throws ManagerException
	 */
	int batchDelete(int[] ids) throws ManagerException;
	/**
	 * 根据债权id获取债权本息信息
	 * @param debtId
	 * @return
	 */
	List<DebtInterest> findInterestsByDebtId(Long debtId) throws ManagerException;

	/**
	 * 定时更新债权本息还本付息记录状态
	 * @return
	 * @throws ManagerException
	 */
	int updateStatusForPayInterestAndPrincipal() throws ManagerException;
	
	/**
	 * 根据项目获取债权本息期数
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	int findPeriodsByProjectId(Map<String, Object> map) throws ManagerException;
	
	/**
	 * 根据项目id获取债权本息数据
	 * @param projectId
	 * @return
	 */
	List<DebtInterest> findDebtInterestByProjectId(Long projectId) throws ManagerException;

	int findDPeriodsByProjectId(Map<String, Object> map)throws ManagerException;
	/**
	 * 
	 * @Description:查询债权本息
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月12日 上午9:56:59
	 */
	List<DebtInterest> findInterestsByProjectId(Long projectId) throws ManagerException;

	Page<DebtInterest> findInterestsByProjectId(Page<DebtInterest> pageRequest,Map<String, Object> map)throws ManagerException;
	/**
	 * 
	 * @Description:还款日期
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月27日 上午9:53:46
	 */
	DebtInterest selectOverdueDyasByProjectId(Long projectId)throws ManagerException;
	
	/**
	 * 
	 * @Description:逾期本金和利息
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月27日 上午9:55:11
	 */
	DebtInterest selectOverdueAmountByProjectId(Map<String, Object> map) throws ManagerException;
	/**
	 * 
	 * @Description:代还本金和利息统计
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月26日 下午4:58:24
	 */
	DebtInterest selectWaitPayAmountByBorrowerId( Map<String, Object> map) throws ManagerException;
	/**
	 * 
	 * @Description:已还本金
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月27日 下午3:14:30
	 */
	DebtInterest selectWaitPayAmountByProjectId(Map<String, Object> map)throws ManagerException;
	/**
	 * 
	 * @Description:根据条件查询本金和利息
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月28日 下午5:38:26
	 */
	ProjectInterestBiz selectOverduePayAmountByProjectId(Map<String, Object> map)throws ManagerException;

	int findInterestsByProjectIdTotalCount(Map<String, Object> interestmap)throws ManagerException;
	/**
	 * 
	 * @Description:
	 * @param interestmap
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月1日 下午2:28:16
	 */
	List<ProjectInterestBiz> getOverdueInfoListByMap(Map<String, Object> map)throws ManagerException;
	
	/**
	 * @Description:根据项目id和结束时间获取债权本息记录
	 * @param projectId
	 * @param endDate
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年2月19日 下午3:59:36
	 */
	DebtInterest findDebtInterestByEndDateAndProjectId(Long projectId,Date endDate)throws ManagerException;
	/**
	 * 
	 * @Description:TODO
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月23日 下午3:08:22
	 */
	DebtInterest selectNOPayAmountByMemberId(Long memberId)throws ManagerException;
	
	/**
	 * @Description:根据项目id获取项目本息记录 
	 * @param projectId
	 * @return
	 * @author: fuyili
	 * @time:2016年3月3日 下午4:58:45
	 */
	List<DebtInterest> findProjectInterestsByProjectId(Long projectId)throws ManagerException;
	/**
	 * 
	 * @Description:查询本息记录 默认排序
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年3月4日 上午11:46:53
	 */
	List<DebtInterest> findInterestlistByProjectId(Long projectId) throws ManagerException;
	/**
	 * 
	 * @Description:TODO
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年3月9日 上午10:05:05
	 */
	DebtInterest selectWaitPayAmountByMemberId(Long memberId) throws ManagerException;
	 /**
     * @Description:根据项目获取债权本息状态
     * @return
     * @author: zhanghao
     * @time:2016年4月7日 上午9:40:05
     */
	public DebtInterest findStatusByProjectId(Map<String, Object> map)throws ManagerException;
	
	/**
	 * @Description:更新项目本息状态
	 * @param 项目本息id
	 * @param beforeStatus
	 * @param afterStatus
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年5月24日 下午3:07:42
	 */
	int updateStatusById(Long id,int beforeStatus,int afterStatus)throws ManagerException;
	
	/**
	 * @Description:
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年5月24日 下午10:52:00
	 */
	int getCountUnReturnByProjectId(Long projectId) throws ManagerException;
	
	/**
	 * 
	 * @Description:逾期记录
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年5月30日 下午4:20:42
	 */
	public List<DebtInterest> findOverdueInterestsByProjectId(Long projectId)throws ManagerException;
	/**
	 * 
	 * @Description:待支付本息记录
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年6月1日 上午9:39:35
	 */
	List<DebtInterest> findWaitPayInterestsByProjectId(Long projectId) throws ManagerException;
	
	/**
	 * @Description:产生普通逾期
	 * @throws ManagerException
	 * @author: fuyili
	 * @return 
	 * @time:2016年6月1日 下午2:01:00
	 */
	public void generateGeneralOverdue()throws ManagerException;
	
	/**
	 * @Description:产生垫资逾期
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年6月1日 下午2:22:33
	 */
	public void generateUnderWriteOverdue()throws ManagerException;

	/**
	 * @Description:项目本息还本付息成功后更新
	 * @param id
	 * @param realPayInterest
	 * @param realPayPrincipal
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年6月18日 下午5:27:36
	 */
	int updateRealPayForPrincipalAndInterestSuccess(Long id, BigDecimal realPayInterest, BigDecimal realPayPrincipal)
			throws ManagerException;
}
