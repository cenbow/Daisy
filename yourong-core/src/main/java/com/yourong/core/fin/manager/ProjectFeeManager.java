package com.yourong.core.fin.manager;

import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.ProjectFee;
import com.yourong.core.ic.model.Project;
	
public interface ProjectFeeManager {
	/**
	 * 
	 * @Description:根据项目id查询管理费信息
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月12日 下午1:28:59
	 */
	public ProjectFee getManageMentFeeByProjectId(Long projectId)
			throws ManagerException;

	/**
	 * 
	 * @Description:插入记录
	 * @param record
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月12日 下午1:29:04
	 */
	int insertSelective(ProjectFee record) throws ManagerException;

	/**
	 * 
	 * @Description:保存管理费
	 * @param project
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月12日 下午1:36:21
	 */
	int saveManagementFee(Project project) throws ManagerException;

	/**
	 * 
	 * @Description:管理费分页
	 * @param map
	 * @return
	 * @author: zhanghao
	 * @time:2016年2月16日 下午3:58:16
	 */
	public Page<ProjectFee> selectManageFeeForPagin(
			Page<ProjectFee> pageRequest, Map<String, Object> map)
			throws ManagerException;
	
	/**
	 * 
	 * @Description:根据项目id和服务费类型  更新服务费
	 * @param projectId
	 * @param feeType (1：项目管理费 2：风险金 3：保证金  4：介绍费)
	 * @param feeStatus (1:待收取，2：收取中，3 已收取 ，4 归还中 ，5 已归还)
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年5月30日 上午9:22:13
	 */
	public int updateProjectFeeByProjectId(Long projectId, int feeType,int feeStatus)throws ManagerException;
	
	/**
	 * 
	 * @Description:插入服务费记录
	 * @param projectId
	 * @param feeType
	 * @param memberId 收取人id
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年5月30日 上午9:40:59
	 */
	int insertProjectFee(Long projectId,int feeType,Long memberId) throws ManagerException;
	
	/**
	 * 
	 * @Description:通过projectId，feeType 查询服务费
	 * @param projectId
	 * @param feeType
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年5月30日 上午11:07:16
	 */
	public ProjectFee getProjectFeeByProjectIdType(Long projectId,int feeType)
			throws ManagerException;

}
