package com.yourong.core.fin.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yourong.core.fin.model.OverdueLog;
@Repository
public interface OverdueLogMapper {
	
    int insertSelective(OverdueLog record);

    OverdueLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OverdueLog record);

	OverdueLog getOverdueLogByInterestId(Long interestId);

	List<OverdueLog> getOverdueLogByProjectId(Long projectId);
	/**
	 * 
	 * @Description:通过条件查询逾期记录
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年1月27日 下午1:40:58
	 */
	OverdueLog selectOverdueByProjectId(@Param("map") Map<String, Object> map);
	
	int findOverdueByProjectIdTotalCount(@Param("map") Map<String, Object> map);
	/**
	 * 
	 * @Description: 逾期日期
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年1月28日 下午6:01:48
	 */
	OverdueLog selectOverdueDyasByProjectId(@Param("projectId") Long projectId);
	/**
	 * 
	 * @Description:统计逾期记录
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年2月1日 下午3:26:00
	 */
	int countOverdueRecordByProjectId(@Param("projectId") Long projectId);
	/**
	 * 
	 * @Description:TODO
	 * @param status
	 * @return
	 * @author: chaisen
	 * @time:2016年2月23日 下午1:37:00
	 */
	int countOverdueRecordByProjectIdandStatus(@Param("status") Integer status,@Param("projectId") Long projectId);
	/**
	 * 
	 * @Description:统计逾期记录数  
	 * @param interestId
	 * @return
	 * @author: chaisen
	 * @time:2016年2月26日 下午5:26:44
	 */
	int countOverdueRecordByInterestId(@Param("map") Map<String, Object> map);
	
	/**
	 * @Description:根据项目id和当前还款时间 更新垫资逾期记录状态
	 * @param beforeStatus
	 * @param afterStatus
	 * @param repayDate
	 * @param projectId
	 * @return
	 * @author: fuyili
	 * @time:2016年2月26日 下午2:17:34
	 */
	int updateStatusByProjectId(@Param("beforeStatus") Integer beforeStatus, @Param("afterStatus") Integer afterStatus,
			@Param("repayDate") Date repayDate, @Param("projectId") Long projectId,@Param("remarks")String remarks);
	/**
	 * 
	 * @Description:最近一期还款时间
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年3月1日 下午6:50:25
	 */
	OverdueLog selectOverdueRecordByProjectId(@Param("projectId") Long projectId);
	/**
	 * @Description:更新最近一笔逾期记录的滞纳金
	 * @param lateFees
	 * @param repayDate
	 * @return
	 * @author: fuyili
	 * @time:2016年2月29日 下午12:09:43
	 */
	int updateOverdueFine( @Param("overdueFine") BigDecimal overdueFine, @Param("status") Integer status,@Param("projectId") Long projectId);

	/**
	 * @Description:更新逾期记录状态 根据逾期记录id
	 * @param beforeStatus
	 * @param afterStatus
	 * @param id
	 * @return
	 * @author: fuyili
	 * @time:2016年2月29日 下午2:49:25
	 */
	int updateForOverdueRepaySuccess(@Param("beforeStatus")int beforeStatus, @Param("afterStatus")int afterStatus,@Param("id")  Long id);
	/**
	 * 
	 * @Description:获取逾期本金和利息
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年3月3日 下午3:42:49
	 */
	OverdueLog selectOverdueAmountByProjectId(@Param("projectId") Long projectId);
	/**
	 * 
	 * @Description:逾期本息
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年3月4日 上午10:54:30
	 */
	OverdueLog selectOverdueLogPrincipalByProjectId(@Param("projectId") Long projectId);
	/**
	 * 
	 * @Description:获取逾期本金
	 * @param projectId
	 * @param endDate
	 * @return 
	 * @author: chaisen
	 * @time:2016年3月7日 下午4:23:26
	 */
	OverdueLog selectOverduePayAmountByProjectId(@Param("projectId") Long projectId,@Param("startDate") Date startDate);
	/**
	 * 
	 * @Description:查询逾期本金和利息 根据memberId
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年3月9日 上午9:41:21
	 */
	OverdueLog selectOverduePayAmountByMemberId(@Param("memberId") Long memberId);
	
	/**
	 * 
	 * @Description:最近一期还款时间
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年3月11日 上午9:27:13
	 */
	OverdueLog selectOverdueDateByProjectId(@Param("projectId") Long projectId);
	
	int getOverdueLogByProjectIdAndType(@Param("type") Integer type,@Param("projectId") Long projectId);
	/**
	 * 
	 * @Description:统计逾期或垫资
	 * @param type
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年5月30日 下午4:42:01
	 */
	int getOverdueLogByInterestIdAndtype(@Param("interestId") Long interestId,@Param("type") Integer type);
	/**
	 * 
	 * @Description:垫资逾期记录
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年5月31日 上午10:01:49
	 */
	List<OverdueLog> selectInterestsByProjectId(@Param("projectId") Long projectId);
	/**
	 * 
	 * @Description:统计逾期的记录
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年5月31日 下午4:33:15
	 */
	int getCountOverdueRecord(@Param("interestId") Long interestId);
	
	/**
	 * 
	 * @Description:根据interestId 统计逾期记录
	 * @param interestId
	 * @param type
	 * @return
	 * @author: chaisen
	 * @time:2016年6月1日 上午10:25:46
	 */
	int getCountOverdueRecordByInterestId(@Param("interestId") Long interestId,@Param("type") Integer type);

	/**
	 * @Description:根据逾期记录id更新逾期状态
	 * @param beforeStatus
	 * @param afterStatus
	 * @param id
	 * @return
	 * @author: fuyili
	 * @time:2016年6月5日 下午3:19:20
	 */
	int updateStatusForOverdueRepay(@Param("beforeStatus")int beforeStatus, @Param("afterStatus")int afterStatus,@Param("id")  Long id);
	
	/**
	 * @Description:根据项目本息查找对应逾期记录
	 * @param interestId
	 * @param type
	 * @return
	 * @author: fuyili
	 * @time:2016年6月6日 上午11:34:52
	 */
	OverdueLog selectByInterestId(@Param("interestId")Long interestId);
	
	/***
	 * 
	 * @Description:逾期记录数
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年6月13日 下午3:21:21
	 */
	int getOverdueLogByProjectIdAndStatus(@Param("projectId") Long projectId);
	/**
	 * 
	 * @Description:垫资逾期记录数
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年6月20日 下午5:40:08
	 */
	int countUnderOverdueByProjectId(@Param("projectId") Long projectId);
	
	OverdueLog selectNormalOverduePayAmountByProjectId(@Param("projectId") Long projectId,@Param("startDate") Date startDate);
	/**
	 * 
	 * @Description:垫资逾期
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年6月21日 下午8:07:53
	 */
	OverdueLog selectUnderOverdueAmountByProjectId(@Param("projectId") Long projectId);
	/**
	 * 
	 * @Description:逾期记录
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年6月22日 上午11:55:33
	 */
	int getCountOverdueRecordByProjectId(@Param("projectId") Long projectId);
	
	/**
	 * @Description:根据项目id和status获取垫资开始时间
	 * @param status
	 * @param projectId
	 * @return
	 * @author: fuyili
	 * @time:2016年7月1日 下午3:12:16
	 */
	OverdueLog getStartDateByProjectIdandStatus(@Param("status") Integer status,@Param("projectId") Long projectId);
	
	/**
	 * @Description:根据项目id和status获取垫资记录
	 * @param status
	 * @param projectId
	 * @return
	 * @author: fuyili
	 * @time:2016年7月19日 下午2:00:21
	 */
	List<OverdueLog> getIdsByProjectIdandStatus(@Param("status") Integer status,@Param("projectId") Long projectId);
	
	/**
	 * @Description:LockForUpdate
	 * @param id
	 * @return
	 * @author: fuyili
	 * @time:2016年7月19日 下午2:08:29
	 */
	OverdueLog getLockForUpdate(@Param("id") Long id );
	
	/**
	 * 
	 * @Description:是否项目存在逾期未还记录
	 * @param projectId
	 * @return
	 * @author: zhanghao
	 * @time:2016年9月8日 下午3:26:00
	 */
	int isOverDueUnrepayment(@Param("projectId") Long projectId);
	
	/**
	 * 查询本息已还，但是垫资还款未还的最近一期垫资还款记录
	 * 
	 * @param projectId
	 * @return
	 */
	OverdueLog selectOverdueInfoByProjectId(@Param("projectId") Long projectId);
	
}