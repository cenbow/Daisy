package com.yourong.backend.ic.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.DirectProjectBiz;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectPackage;
import com.yourong.core.ic.model.ProjectPackageLinkModel;
import com.yourong.core.ic.model.ProjectPackageModel;
import com.yourong.core.ic.model.biz.TransferProjectPageBiz;
import com.yourong.core.ic.model.query.TransferProjectPageQuery;
import com.yourong.core.sys.model.SysDict;

public interface DirectProjectService {

	/**
	 * @Description:直投项目分页获取数据
	 * @return
	 * @author: fuyili
	 * @time:2015年12月30日 下午7:31:39
	 */
	public Page<DirectProjectBiz> directFindByPage(Page<DirectProjectBiz> pageRequest,Map<String, Object> map);

	/**
	 * @Description:添加直投项目
	 * @param directProjectBiz
	 * @param appPath
	 * @return
	 * @author: fuyili
	 * @time:2016年1月7日 下午8:47:00
	 */
	public ResultDO<DirectProjectBiz> insertDirectProjectInfo(DirectProjectBiz directProjectBiz, String appPath)throws Exception;
	
	/**
	 * @Description:查询项目详情
	 * @param id
	 * @return
	 * @author: fuyili
	 * @time:2016年1月7日 下午8:47:18
	 */
	public DirectProjectBiz findDirectProjectBizById(Long id);

	/**
	 * @Description:编辑项目
	 * @param directProjectBiz
	 * @param appPath
	 * @return
	 * @author: fuyili
	 * @time:2016年1月12日 上午10:19:25
	 */
	public ResultDO<DirectProjectBiz> editDirectProjectInfo(DirectProjectBiz directProjectBiz, String appPath,String action)throws Exception;

	/**
	 * @Description:紧急修改项目
	 * @param directProjectBiz
	 * @param appPath
	 * @return
	 * @author: fuyili
	 * @time:2016年1月12日 下午5:24:49
	 */
	public ResultDO<DirectProjectBiz> emergencyPartDirectProjectInfo(DirectProjectBiz directProjectBiz, String appPath);
	
	/**
	 * @Description:恢复项目
	 * @param id
	 * @param auditId
	 * @param msg
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年1月20日 下午3:38:54
	 */
	public int startProject(Long id,Long auditId,String msg) ;
	
	/**
	 * @Description:暂停项目
	 * @param id
	 * @param auditId
	 * @param msg
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年1月20日 下午3:39:09
	 */
	public int stopProject(Long id, Long auditId, String msg) ;
	
	/**
	 * @Description:上线审核
	 * @param id
	 * @param auditId
	 * @param msg
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年1月20日 下午5:37:40
	 */
	public ResultDO<Project> reviewProject(Long id, Long auditId, String msg) throws Exception;
	
	/**
	 * 回退到存盘状态
	 * @param id
	 * @param auditId
	 * @param msg
	 * @throws ManagerException
	 * @return
	 */
	public ResultDO<Project> fallbackSaveStatus(Long id, Long auditId, String msg) throws ManagerException;
	
	/**
	 * @Description:提交待审
	 * @param id
	 * @param auditId
	 * @return
	 * @author: fuyili
	 * @time:2016年1月20日 下午8:05:36
	 */
	public int waitReviewProject(Long id, Long auditId);

	/**
	 * @Description:风控审核
	 * @param id
	 * @param auditId
	 * @return
	 * @author: fuyili
	 * @time:2016年1月20日 下午11:17:39
	 */
	public ResultDO<Project> riskReviewProjectSuccess(Long id, Long auditId,String msgs)throws Exception;

	/**
	 * @Description:删除项目
	 * @param id
	 * @return
	 * @author: fuyili
	 * @time:2016年1月25日 下午2:08:00
	 */
	int deleteProjectById(Long id)throws ManagerException;

	/**
	 * @Description:修改上线和销售截止时间
	 * @param onlineTime
	 * @param saleEndTime
	 * @param id
	 * @return
	 * @author: fuyili
	 * @time:2016年1月25日 下午4:20:41
	 */
	ResultDO<Object> updateOnlineTimeAndEndDate(Date onlineTime, Date saleEndTime, Long id);

	/**
	 * @Description:修改销售截止时间
	 * @param saleEndTime
	 * @param id
	 * @return
	 * @author: fuyili
	 * @time:2016年1月25日 下午4:21:08
	 */
	ResultDO<Object> updateEndDate(Date saleEndTime, Long id);
	
	/**
	 * @Description:风控审核失败
	 * @param id
	 * @param auditId
	 * @param msgs
	 * @return
	 * @author: fuyili
	 * @time:2016年2月4日 下午1:21:11
	 */
	ResultDO<Project> riskReviewProjectFail(Long id, Long auditId,String msgs)throws ManagerException;

	/**
	 * @Description:项目流标
	 * @param id
	 * @return
	 * @author: fuyili
	 * @time:2016年2月18日 下午5:42:40
	 */
	public ResultDO<Project> loseProject(Long id);

	/**
	 * 查询转让费率
	 * @return
     */
	public List<SysDict> findTransferRate();

	public Page<TransferProjectPageBiz> queryPageTransferProjectPageBiz(TransferProjectPageQuery query);
	/**
	 * @Description:直投资产包项目分页获取数据
	 * @return
	 * @author: tanjianlin
	 * @time:2015年12月30日 下午7:31:39
	 */
	public Page<ProjectPackage> directPackagePage(Page<ProjectPackage> pageRequest,Map<String, Object> map,ProjectPackage projectPackage);
	/**
	 * 添加项目编号到资产包内
	 * @param originalProjectNumbers
	 * @return
	 */
	public int addProjectNumber(ProjectPackageModel packageModel)throws Exception;
	/**
	 * 获取缺省资产包名称
	 * @return
	 */
    public  ProjectPackage getDefualtProjectPackage();
	/**
	 * 添加项目编号到资产包内
	 * @param originalProjectNumbers
	 * @return
	 */
	public int batchDelete(String ids);
	
	/**
	 * 获取资产包内容
	 * @param projectPackageId
	 * @return
	 */
	public ProjectPackageModel getPackage(Long projectPackageId);
	/**
	 * 审核资产包上线
	 * @return
	 */
	public int auditProjectFromPackage(Long projectPackageId);
	/**
	 * 获取资产包内的所有项目列表
	 * @param projectPackageId
	 * @return
	 */
	public List<ProjectPackageLinkModel> getProjectPackageList(Long projectPackageId); 

}
