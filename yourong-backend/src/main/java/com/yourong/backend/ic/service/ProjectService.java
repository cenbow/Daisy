package com.yourong.backend.ic.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.OverdueRepayLog;
import com.yourong.core.fin.model.ProjectFee;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.ic.model.DirectProjectBiz;
import com.yourong.core.ic.model.DirectSettlementBiz;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectBiz;
import com.yourong.core.ic.model.ProjectInterestBiz;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.HostingPayTrade;

public interface ProjectService {
	
	Page<Project> findByPage(Page<Project> pageRequest, Map<String, Object> map);
	
	/**
	 * 更新项目状态
	 * @param newStatus 新状态
	 * @param currentStatus 当前状态
	 * @param projectId  项目编号
	 * @return
	 * @throws ManagerException
	 */
	public int updateProjectStatus(int newStatus, int currentStatus, Long projectId);
	
	/**
	 * 添加项目
	 * @param project
	 * @return
	 * @throws ManagerException
	 */
	public ResultDO addProject(Project project) throws ManagerException;
	
	/**
	 * 根据项目编号获得项目信息
	 * @param projectId
	 * @return
	 */
	public ProjectBiz findProjectById(Long projectId);
	
	/**
	 * 更新项目
	 * @param project
	 * @return
	 * @throws ManagerException
	 */
	public ResultDO updateProject(Project project) throws ManagerException;
	
	
	/**
	 * 根据编号删除项目（逻辑删除）
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public int deleteProjectById(Long id) throws ManagerException;
	
	
	/**
	 * 待审项目
	 * @param id 项目编号
	 * @param auditId 审核员编号
	 * @return
	 * @throws ManagerException
	 */
	public int waitReviewProject(Long id, Long auditId) throws ManagerException;
	

	/**
	 * 审核项目
	 * @param id 项目编号
	 * @param auditId 审核员编号
	 * @param msg 审核内容
	 * @return
	 * @throws ManagerException
	 */
	public int reviewProject(Long id, Long auditId, String msg) throws ManagerException;
	
	/**
	 * 发布项目
	 * @param id 项目编号
	 * @param auditId 审核员编号
	 * @param msg 审核内容
	 * @return
	 * @throws ManagerException
	 */
	public int releaseProject(Long id, Long auditId, String msg) throws ManagerException;
	
	/**
	 * 暂停项目
	 * @param id 项目编号
	 * @param auditId 审核员编号
	 * @param msg 审核内容
	 * @return
	 * @throws ManagerException
	 */
	public int stopProject(Long id, Long auditId, String msg) throws ManagerException;
	
	/**
	 * 恢复项目
	 * @param id 项目编号
	 * @param auditId 审核员编号
	 * @param msg 审核内容
	 * @throws ManagerException
	 * @return
	 */
	public int startProject(Long id, Long auditId, String msg) throws ManagerException;
	
	/**
	 * 回退到存盘状态
	 * @param id
	 * @param auditId
	 * @param msg
	 * @throws ManagerException
	 * @return
	 */
	public int fallbackSaveStatus(Long id, Long auditId, String msg) throws ManagerException;
	
	/**
	 * 检查项目名称或项目中期数是否存在
	 * @param name 项目名称
	 * @param namePeriod 项目名称中的期数
	 * @return
	 */
	public boolean checkNameExists(String name, String namePeriod);
	
	/**
	 * 检查用户名是否存在
	 * @param name 项目名称
	 * @param namePeriod 项目名称中的期数
	 * @param id
	 * @return
	 */
	public boolean checkNameExists(String name, String namePeriod, Long id);
	
	/**
	 * 更新上线时间&销售截止时间
	 * @param onlineTime 上线时间
	 * @param saleEndTime 销售截止时间
	 * @param id 项目编号
	 * @return
	 * @throws ManagerException
	 */
	public ResultDO updateOnlineTimeAndEndDate(Date onlineTime, Date saleEndTime, Long id);
	
	/**
	 * 更新销售截止时间
	 * @param saleEndTime 销售截止时间
	 * @param id 项目编号
	 * @return
	 */
	public ResultDO updateEndDate(Date saleEndTime, Long id);
	
	/**
	 * 获取项目个数根据项目状态
	 * @param status
	 * @return
	 */
	public int findProjectCountByStatus(Integer status);

	/**
	 * 紧急修改项目
	 * @param project
	 * @return
	 * @throws ManagerException
	 */
	ResultDO emergencyUpdateProject(Project project) throws ManagerException;
	
	/**
	 * 添加项目备注
	 * @param id
	 * @param newControlRemarks
	 * @return
	 * @throws ManagerException 
	 */
	public ResultDO<Object> addControlRemarks(Long id, String newControlRemarks) throws ManagerException;
	/**
	 * 分页查询放款管理
	 * @Description:TODO
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月4日 上午10:39:42
	 */
	Page<DirectProjectBiz> selectLoanForPagin(Page<DirectProjectBiz> pageRequest, Map<String, Object> map);
	/**
	 * 
	 * @Description:根据主键id查询项目
	 * @param id
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月4日 下午5:59:08
	 */
	ProjectInterestBiz selectProjectInfoById(Long id);
	/**
	 * 
	 * @Description:放款，保存审核信息
	 * @param project
	 * @return
	 * @author: chaisen
	 * @time:2016年1月5日 下午3:55:03
	 */
	public ResultDO saveLoanInfo(Project project);
	/**
	 * 
	 * @Description:分页查询还本付息列表
	 * @param pageRequest
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年1月6日 上午9:55:37
	 */
	Page<ProjectInterestBiz> findRepayInterestForPagin(Page<ProjectInterestBiz> pageRequest, Map<String, Object> map);
	/**
	 * 
	 * @Description:获取垫资代付信息
	 * @param id
	 * @return
	 * @author: chaisen
	 * @time:2016年1月6日 下午5:19:03
	 */
	ProjectInterestBiz selectInterestInfoByPrimaryKey(Long id);
	/**
	 * 
	 * @Description:垫资代付
	 * @param project
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @throws Exception 
	 * @time:2016年1月7日 下午2:02:01
	 */
	public ResultDO saveRepayment(ProjectInterestBiz project) throws  Exception;

	BigDecimal getThirdAccountMoney(Long memberId);

	ProjectInterestBiz getFlagInfo(Long id);
	/**
	 * 
	 * @Description:获取逾期还款记录
	 * @param id
	 * @return
	 * @author: chaisen
	 * @time:2016年1月7日 下午8:13:57
	 */
	ProjectInterestBiz getOverdueInfo(Long id);
	/**
	 * 
	 * @Description:逾期还款标记
	 * @param project
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年1月7日 下午8:52:37
	 */
	public ResultDO saveRepayFlagInterest(ProjectInterestBiz project) throws Exception;
	/**
	 * 
	 * @Description:管理费记录
	 * @param pageRequest
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年1月11日 下午5:28:30
	 */
	Page<ProjectFee> selectManageFeeForPagin(Page<ProjectFee> pageRequest, Map<String, Object> map);
	/**
	 * 
	 * @Description:查询还款记录-项目基本信息
	 * @param id
	 * @return
	 * @author: chaisen
	 * @time:2016年1月12日 上午10:12:17
	 */
	ProjectInterestBiz showProject(Long id);
	/**
	 * 
	 * @Description:根据项目id查询本息
	 * @param pageRequest
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年1月12日 上午10:43:15
	 */
	Page<ProjectInterestBiz> showProjectInterest(Page<ProjectInterestBiz> pageRequest, Map<String, Object> map);
	/**
	 * 
	 * @Description:分页查询逾期管理页面
	 * @param pageRequest
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年1月20日 下午1:54:27
	 */
	Page<ProjectInterestBiz> findOverdueList(Page<ProjectInterestBiz> pageRequest, Map<String, Object> map);
	
	/**
	 * 
	 * @Description:同步第三方账户余额
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年1月26日 下午1:22:48
	 */
	Object synchronizedBalance() throws ManagerException;
	/**
	 * 
	 * @Description:查询逾期还款记录
	 * @param pageRequest
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年1月28日 下午1:17:07
	 */
	Page<OverdueRepayLog> getOverdueRecord(Page<OverdueRepayLog> pageRequest, Map<String, Object> map);
	/**
	 * 
	 * @Description:获取滞纳金根据projectId
	 * @param projectId
	 * @param repayDate
	 * @return
	 * @author: chaisen
	 * @time:2016年1月28日 下午4:31:49
	 */
	ProjectInterestBiz getOverdueAmount(Long projectId, String repayDate);
	/**
	 * 计算总额
	 * @Description:TODO
	 * @param payableAmount
	 * @param overdueFine
	 * @return
	 * @author: chaisen
	 * @time:2016年2月1日 下午1:17:28
	 */
	BigDecimal getPayableAmount(BigDecimal payableAmount, BigDecimal overdueFine);
	/**
	 * 
	 * @Description:本息记录
	 * @param pageRequest
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年3月9日 下午5:02:10
	 */
	Page<ProjectInterestBiz> findProjectInterest(Page<ProjectInterestBiz> pageRequest, Map<String, Object> map);
	
	
	void loseProject(Long projectId);
	
	/**
	 * @Description:垫资还款代付异常处理接口，根据代收号发起批量代付
	 * @param collectTradeNo
	 * @return
	 * @throws Exception
	 * @author: fuyili
	 * @time:2016年3月23日 下午2:54:52
	 */
	public ResultDO<?> createHostPayForOverdueRepayByCollectTradeNo(String collectTradeNo);
	/**
	 * 
	 * @Description:置为存盘
	 * @param id
	 * @param id2
	 * @param msg
	 * @return
	 * @author: chaisen
	 * @time:2016年4月29日 下午2:28:38
	 */
	ResultDO setSaveProject(Long id, Long memberId, String msg);

	ResultDO<Debt> getSerialNumber(Long projectId);
	/**
	 * 
	 * @Description:直投项目营收结算分页
	 * @param pageRequest
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年5月6日 下午4:30:06
	 */
	Page<DirectSettlementBiz> findDirectSettlementByPage(Page<DirectSettlementBiz> pageRequest, Map<String, Object> map);
	/**
	 * 
	 * @Description:TODO
	 * @param pageRequest
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年5月25日 上午11:09:17
	 */
	Page<ProjectInterestBiz> findNormalOverdueList(Page<ProjectInterestBiz> pageRequest, Map<String, Object> map);
	
	/**
	 * @Description:项目保证金归还创建代收
	 * @param project
	 * @throws Exception
	 * @author: fuyili
	 * @time:2016年5月28日 下午1:17:23
	 */
	public ResultDO<HostingCollectTrade> createCollectTradeForGuaranteeFee(Long projectId);

	/**
	 * @Description:根据本地代收记录创建归还保证金代付
	 * @param projectId
	 * @return
	 * @author: fuyili
	 * @time:2016年7月28日 下午5:42:26
	 */
	ResultDO<HostingPayTrade> createPayTradeByCollectTradeNoForGuaranteeFee(String projectId);

	SysDict selectByGroupNameAndValue(String channelBusiness);
	
	/**
	 * 垫资还款标记
	 * 
	 * @param project
	 * @return
	 * @throws Exception
	 */
	public ResultDO<ProjectInterestBiz> toUnderWriteRepay(ProjectInterestBiz project) throws Exception;
	
}
