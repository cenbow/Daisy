package com.yourong.core.fin.manager;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.fin.model.OverdueLog;
import com.yourong.core.fin.model.OverdueRepayLog;
import com.yourong.core.ic.model.ProjectInterestBiz;

public interface OverdueRepayLogManager {

	public OverdueLog getOverdueLogByInterestId(Long interestId) throws ManagerException;

	public int insertSelective(OverdueLog overdueLog) throws ManagerException;


	/**
	 * 
	 * @Description:查询逾期记录
	 * @param overdueMap
	 * @return
	 * @author: chaisen
	 * @time:2016年1月27日 下午1:44:34
	 */
	public OverdueLog selectOverdueByProjectId(Map<String, Object> overdueMap) throws ManagerException;

	/**
	 * 
	 * @Description:保存逾期记录
	 * @param project
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月29日 下午2:38:12
	 */
	public int saveOverdueLog(ProjectInterestBiz project) throws ManagerException;

	/**
	 * 
	 * @Description:TODO
	 * @param overdueMap
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月1日 上午9:38:19
	 */
	public int findOverdueByProjectIdTotalCount(Map<String, Object> overdueMap) throws ManagerException;

	/**
	 * 
	 * @Description:保存逾期还款记录
	 * @param overdueRepayLog
	 * @return
	 * @author: chaisen
	 * @time:2016年2月1日 下午1:44:58
	 */
	public int saveOverdueRepayLog(ProjectInterestBiz project)throws ManagerException;
	/**
	 * 
	 * @Description:统计逾期还款记录数
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月23日 上午11:14:00
	 */
	public int countOverdueRepayLogByProjectId(Long projectId)throws ManagerException;

	int updateByPrimaryKeySelective(OverdueRepayLog overdueRepayLog) throws ManagerException;
	
	/**
	 * @Description:保存线上垫资还款记录
	 * @param projectId
	 * @param repayDate
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年2月23日 下午3:49:37
	 */
	public OverdueRepayLog saveRecordOnline(Long projectId,Date repayDate)throws ManagerException;

	
    OverdueRepayLog selectByPrimaryKey(Long id)throws ManagerException;

    /**
     * @Description:更新逾期还款状态  根据逾期记录id
     * @param status
     * @param status2
     * @param id
     * @author: fuyili
     * @time:2016年2月29日 下午3:22:14
     */
	public int updateStatusByOverdueId(int beforeStatus, int afterStatus, Long overdueId)throws ManagerException;

	/**
	 * @Description:更新逾期还款状态 根据主键
	 * @param status
	 * @param status2
	 * @param sourceId
	 * @author: fuyili
	 * @time:2016年2月29日 下午3:45:19
	 */
	public int updateStatusById(int beforeStatus, int afterStatus, Long id)throws ManagerException;
	/**
	 * 
	 * @Description:查询逾期结算记录
	 * @param id
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年5月25日 下午3:09:15
	 */
	public List<OverdueRepayLog> getOverdueRepayListByProjectId(Long id)throws ManagerException;
	/**
	 * 
	 * @Description:催收中的结算记录
	 * @param id
	 * @return
	 * @author: chaisen
	 * @time:2016年5月27日 下午1:57:42
	 */
	public OverdueRepayLog getRepayLogByProjectIdCollect(Long id)throws ManagerException;
	
	/**
	 * @Description:根据逾期借款记录计算滞纳金
	 * @param overdueRepayId
	 * @return
	 * @throws ManagerExcepiton
	 * @author: fuyili
	 * @time:2016年6月5日 上午9:10:59
	 */
	public Map<String, Object> getLateFeeByOverdueRepayLog(OverdueRepayLog overdueRepayLog,BigDecimal overdueFeeRate) throws ManagerException;

	/**
	 * @Description:查询未还款的逾期结算记录
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年6月5日 上午11:26:24
	 */
	public OverdueRepayLog getOverdueRepayByStatus(Long projectId)throws ManagerException;
	
	/**
	 * @Description:逾期结算记录下是否存在未归还的逾期记录
	 * @param overdueRepayId
	 * @return
	 * @author: fuyili
	 * @time:2016年6月6日 上午11:51:08
	 */
	public int getUnreturnCountByOverdueRepayId(Long overdueRepayId) throws ManagerException;

	/**
	 * 查询用户逾期未还期数
	 * @param memberId
	 * @return
	 */
	public int queryOverdueCountByBorrowerId(Long memberId);

	/**
	 * 查询用户逾期未还金额
	 * @param memberId
	 * @return
	 */
	public BigDecimal queryOverdueAmountByBorrowerId(Long memberId);
}
