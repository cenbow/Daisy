package com.yourong.core.fin.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yourong.core.fin.model.ProjectFee;
@Repository
public interface ProjectFeeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProjectFee record);

    int insertSelective(ProjectFee record);

    ProjectFee selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProjectFee record);

    int updateByPrimaryKey(ProjectFee record);

	ProjectFee getManageMentFeeByProjectId(Long projectId);
	
	/**
	 * 
	 * @Description:管理费页面记录数
	 * @param map
	 * @return
	 * @author: zhanghao
	 * @time:2016年1月11日 下午3:58:16
	 */
	public int selectManageFeeForPaginTotalCount(@Param("map") Map<String, Object> map);
	/**
	 * 
	 * @Description:管理费分页
	 * @param map
	 * @return
	 * @author: zhanghao
	 * @time:2016年2月16日 下午3:58:16
	 */
	List<ProjectFee> selectManageFeeForPagin(@Param("map") Map<String, Object> map);
	
	/**
	 * @Description:收取之后更新管理费状态和实际收取值
	 * @param afterStatus
	 * @param beforeStatus
	 * @param realManagementFee
	 * @param projectId
	 * @return
	 * @author: fuyili
	 * @time:2016年3月9日 下午8:54:53
	 */
	public int updateAfterGatherFee(@Param("afterStatus") int afterStatus, @Param("beforeStatus") int beforeStatus,
			@Param("realManagementFee") BigDecimal realManagementFee, @Param("projectId") Long projectId);
	
	/**
	 * @Description:更新状态根据项目id
	 * @param afterStatus
	 * @param beforeStatus
	 * @param projectId
	 * @return
	 * @author: fuyili
	 * @time:2016年3月10日 上午11:53:06
	 */
	public int updateStatus(@Param("afterStatus") int afterStatus, @Param("beforeStatus") int beforeStatus, @Param("projectId") Long projectId);
	
	/**
	 * 根据projectId更新服务费
	 * @Description feeType 1：项目管理费 2：风险金 3：保证金
	 * @param onlineTime
	 * @param saleEndTime
	 * @param id
	 * @param statuses
	 * @return
	 * @author: chaisen
	 * @time:2016年5月30日 上午9:10:49
	 */
	public int updateProjectFeeByProjectId(@Param("projectId")Long projectId, @Param("feeType")int feeType, @Param("feeStatus")int feeStatus);
	/**
	 * 
	 * @Description:通过projectId，feeType 查询服务费
	 * @param projectId
	 * @param feeType
	 * @return
	 * @author: chaisen
	 * @time:2016年5月30日 上午11:08:29
	 */
	ProjectFee getProjectFeeByProjectIdType(@Param("projectId")Long projectId, @Param("feeType")int feeType);
	
	/**
	 * @Description:费用归还后状态修改
	 * @param afterStatus
	 * @param beforeStatus
	 * @param id
	 * @return
	 * @author: fuyili
	 * @time:2016年6月7日 下午2:59:09
	 */
	int updateAfterReturnFee(@Param("afterStatus") int afterStatus, @Param("beforeStatus") int beforeStatus,@Param("id") Long id);
	
}


