package com.yourong.core.fin.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.fin.model.OverdueRepayLog;
@Repository
public interface OverdueRepayLogMapper {
	
    int insertSelective(OverdueRepayLog record);

    OverdueRepayLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OverdueRepayLog record);

    OverdueRepayLog getOverdueLogByInterestId(Long interestId);
    /**
     * 
     * @Description:TODO 查询逾期还款记录
     * @param projectId
     * @return
     * @author: chaisen
     * @time:2016年1月28日 下午1:27:40
     */
	List<OverdueRepayLog> getOverdueRepayLogRecordByProjectId(@Param("projectId") Long projectId);
	/**
	 * 
	 * @Description:通过条件查询逾期记录
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年1月27日 下午1:40:58
	 */
	OverdueRepayLog selectOverdueByProjectId(@Param("map") Map<String, Object> map);
	
	int findOverdueByProjectIdTotalCount(@Param("map") Map<String, Object> map);

	/**
	 * 
	 * @Description:统计逾期还款记录数
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年2月23日 上午11:16:15
	 */
	int countOverdueRepayLogByProjectId(@Param("projectId") Long projectId);


	/**
	 * @Description:更新逾期还款状态，根据逾期记录id
	 * @param beforeStatus
	 * @param afterStatus
	 * @param overdueId
	 * @return
	 * @author: fuyili
	 * @time:2016年2月29日 下午3:23:51
	 */
	int updateStatusByOverdueId(@Param("beforeStatus")int beforeStatus, @Param("afterStatus")int afterStatus, @Param("overdueId")Long overdueId);

	/**
	 * @Description:更新逾期还款状态，根据主键
	 * @param beforeStatus
	 * @param afterStatus
	 * @param id
	 * @return
	 * @author: fuyili
	 * @time:2016年2月29日 下午3:53:15
	 */
	int updateStatusById(@Param("beforeStatus")int beforeStatus, @Param("afterStatus")int afterStatus, @Param("id")Long id);
	
	/**
	 * 
	 * @Description:查询逾期结算记录
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年5月25日 下午2:35:51
	 */
	List<OverdueRepayLog> getRepayLogByProjectId(@Param("projectId") Long projectId);
	/**
	 * 
	 * @Description:催收中
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年5月27日 下午1:55:23
	 */
	OverdueRepayLog getRepayLogByProjectIdCollect(@Param("projectId") Long projectId);
	
	/**
	 * 
	 * @Description:逾期结算记录
	 * @param projectId
	 * @param type
	 * @return
	 * @author: chaisen
	 * @time:2016年5月30日 上午10:18:45
	 */
	List<OverdueRepayLog> getOverdueRepayLogRecordByProjectIdAndType(@Param("projectId") Long projectId,@Param("type") int  type);
	/**
	 * 
	 * @Description：统计逾期还款记录数
	 * @param projectId
	 * @param type
	 * @return
	 * @author: chaisen
	 * @time:2016年5月30日 下午2:10:32
	 */
	int countOverdueRepayLogByProjectIdAndtype(@Param("projectId") Long projectId,@Param("type") int  type);
	/**
	 * 
	 * @Description:滞纳金
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年6月1日 上午11:49:27
	 */
	BigDecimal totalOverdueFine(@Param("projectId")Long projectId);
	
	/**
	 * @Description:查询未还款的普通逾期结算记录 
	 * @param projectId
	 * @return
	 * @author: fuyili
	 * @time:2016年6月3日 上午11:01:37
	 */
	OverdueRepayLog getOverdueRepayByStatus(@Param("projectId")Long projectId);
	
	/**
	 * @Description:获取逾期结算记录锁
	 * @param projectId
	 * @return
	 * @author: fuyili
	 * @time:2016年6月3日 上午11:30:50
	 */
	OverdueRepayLog getForLock(@Param("id")Long id);
	
	/**
	 * @Description:查询逾期结算记录下是否存在未还款的逾期记录 
	 * @param overdueRepayId
	 * @return
	 * @author: fuyili
	 * @time:2016年6月6日 上午11:45:52
	 */
	int getUnreturnCountByOverdueRepayId(@Param("overdueRepayId")Long overdueRepayId);
	
	/**
	 * 
	 * @Description:普通逾期结算记录
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年6月16日 下午3:56:12
	 */
	List<OverdueRepayLog> getCommonOverdueRepayLogRecord(@Param("projectId") Long projectId)throws ManagerException;

	/**
	 * 查询用户逾期未还期数
	 * @param memberId
	 * @return
     */
	int queryOverdueCountByBorrowerId(@Param("borrowerId")Long memberId);

	/**
	 * 查询用户逾期未还金额
	 * @param memberId
	 * @return
     */
	Map queryOverdueAmountByBorrowerId(@Param("borrowerId")Long memberId);
}