package com.yourong.core.fin.manager;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.fin.model.OverdueLog;
import com.yourong.core.ic.model.ProjectInterestBiz;


public interface OverdueLogManager {
	
	public  OverdueLog  getOverdueLogByInterestId(Long interestId) throws ManagerException;

	public int insertSelective(OverdueLog overdueLog) throws ManagerException;

	public List<OverdueLog> getOverdueLogByProjectId(Long projectId)throws ManagerException;
	/**
	 * 
	 * @Description:查询逾期记录
	 * @param overdueMap
	 * @return
	 * @author: chaisen
	 * @time:2016年1月27日 下午1:44:34
	 */
	public OverdueLog selectOverdueByProjectId(Map<String, Object> overdueMap)throws ManagerException;
	/**
	 * 
	 * @Description:保存逾期记录
	 * @param project
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月29日 下午2:38:12
	 */
	public int saveOverdueLog(ProjectInterestBiz project)throws ManagerException;
	/**
	 * 
	 * @Description:
	 * @param overdueMap
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月1日 上午9:38:19
	 */
	public int findOverdueByProjectIdTotalCount(Map<String, Object> overdueMap)throws ManagerException;
	/**
	 * 
	 * @Description: 更新记录
	 * @param record
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月1日 下午2:36:24
	 */
	int updateByPrimaryKeySelective(OverdueLog record) throws ManagerException;
	/**
	 * 
	 * @Description:通过projectId 统计逾期未还记录
	 * @param interestId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月1日 下午3:20:39
	 */
	int countOverdueRecordByProjectId(Long projectId)throws ManagerException;
	
	/**
	 * @Description:插入逾期记录
	 * @param interestId 项目本息id
	 * @param type 逾期记录类型
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年2月18日 下午3:25:03
	 */
	int insertOverdue(Long interestId, int type) throws ManagerException;
	/**
	 * 
	 * @Description:统计逾期未还记录数
	 * @param status
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月23日 下午1:42:26
	 */
	int countOverdueNotpay(Long projectId) throws ManagerException;
	/**
	 * 
	 * @Description:统计逾期还款中记录数
	 * @param status
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月23日 下午1:43:20
	 */
	int countOverduePaying(Long projectId) throws ManagerException;

	/**
	 * @Description:根据还款日期和项目id 修改垫资记录状态
	 * @param beforeStatus
	 * @param afterStatus
	 * @param repayDate
	 * @param remark
	 * @return
	 * @author: fuyili
	 * @time:2016年2月26日 下午2:07:30
	 */
	public int updateStatus(int beforeStatus, int afterStatus, Date repayDate, Long projectId,String remark) throws ManagerException;

	/**
	 * @Description:更新最近一笔逾期记录的滞纳金
	 * @param lateFees
	 * @param repayDate
	 * @author: fuyili
	 * @time:2016年2月29日 下午12:08:53
	 */
	public int updateOverdueFine(BigDecimal overdueFine, Integer status,Long projectId)throws ManagerException;
	/**
	 * 
	 * @Description:统计逾期记录数
	 * @param interestId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月26日 下午5:29:30
	 */
	int countOverdueRecordByInterestId(Map<String, Object> map) throws ManagerException;
	
	public OverdueLog selectByPrimaryKey(Long id)throws ManagerException;
	
	/**
	 * @Description:更新状态  根据逾期记录id
	 * @param status
	 * @param status2
	 * @param id
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年2月29日 下午2:45:27
	 */
	public int updateForOverdueRepaySuccess(int beforeStatus, int afterStatus, Long id)throws ManagerException;
	/**
	 * 
	 * @Description:查询逾期本金和利息 根据memberId
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年3月9日 上午9:43:05
	 */
	public  OverdueLog  selectOverduePayAmountByMemberId(Long memberId) throws ManagerException;
	/**
	 * 
	 * @Description:项目是否有逾期
	 * @param id
	 * @param status
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年5月27日 上午10:12:18
	 */
	public int getOverdueLogByProjectIdAndType(Long id, int status)throws ManagerException;
	
	/**
	 * 
	 * @Description:统计逾期或垫资记录数
	 * @param interestId
	 * @param type
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年5月30日 下午4:44:02
	 */
	int getOverdueLogByInterestIdAndtype(Long interestId, int type) throws ManagerException;
	/**
	 * 
	 * @Description:垫资逾期记录
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年5月31日 上午10:02:53
	 */
	public List<OverdueLog> selectInterestsByProjectId(Long projectId)throws ManagerException;
	/**
	 * 
	 * @Description:逾期记录
	 * @param interestId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年5月31日 下午4:35:47
	 */
	int getCountOverdueRecord(Long interestId) throws ManagerException;
	
	/**
	 * 
	 * @Description:根据interestId统计逾期
	 * @param interestId
	 * @param type
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年6月1日 上午10:27:08
	 */
	public int getCountOverdueRecordByInterestId(Long interestId, int type)throws ManagerException;

	/**
	 * @Description:更新逾期记录状态
	 * @param beforeStatus
	 * @param afterStatus
	 * @param id
	 * @return
	 * @author: fuyili
	 * @time:2016年6月5日 下午3:21:21
	 */
	public int updateStatusForOverdueRepay(int beforeStatus, int afterStatus, Long id)throws ManagerException;

	
	/**
	 * @Description:根据项目本息查找对应逾期记录
	 * @param interestId
	 * @param type
	 * @return
	 * @author: fuyili
	 * @time:2016年6月6日 上午11:34:52
	 */
	OverdueLog selectByInterestId(Long interestId)throws ManagerException;

	/**
	 * @Description:垫资还款的开始时间
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年7月1日 下午3:14:40
	 */
	OverdueLog getUnderwriteStartDateByProjectIdandStatus(Long projectId) throws ManagerException;
	
	
	/**
	 * @Description:根据项目id获取未还垫资记录
	 * @param projectId
	 * @return
	 * @author: fuyili
	 * @time:2016年7月19日 下午2:00:21
	 */
	List<OverdueLog> getIdsByProjectIdandStatus(Long projectId)throws ManagerException;

	/**
	 * @Description:LockForUpdate
	 * @param id
	 * @return
	 * @author: fuyili
	 * @time:2016年7月19日 下午2:08:29
	 */
	OverdueLog getLockForUpdate(Long id )throws ManagerException;
	
	/**
	 * 
	 * @Description:是否项目存在逾期未还记录
	 * @param projectId
	 * @return
	 * @author: zhanghao
	 * @time:2016年9月8日 下午3:26:00
	 */
	public boolean isOverDueUnrepayment(Long projectId) throws ManagerException;
}
