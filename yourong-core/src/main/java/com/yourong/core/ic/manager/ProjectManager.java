package com.yourong.core.ic.manager;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.OverdueRepayLog;
import com.yourong.core.fin.model.biz.OverdueRepayLogBiz;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.DirectProjectBiz;
import com.yourong.core.ic.model.DirectSettlementBiz;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectBiz;
import com.yourong.core.ic.model.ProjectInterestBiz;
import com.yourong.core.ic.model.ProjectPackage;
import com.yourong.core.ic.model.ProjectPackageBiz;
import com.yourong.core.ic.model.biz.AfterHandlePreAuthTradeBiz;
import com.yourong.core.ic.model.biz.ProjectForDirectLottery;
import com.yourong.core.ic.model.biz.ProjectForFront;
import com.yourong.core.ic.model.biz.ProjectInvestingDto;
import com.yourong.core.ic.model.query.ProjectBorrowQuery;
import com.yourong.core.ic.model.query.ProjectQuery;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.mc.model.biz.ActivityLeadingSheep;
import com.yourong.core.os.biz.ProjectsPageOutPut;
import com.yourong.core.os.biz.ProjectsStatusOutPut;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.HostingPayTrade;
import com.yourong.core.tc.model.biz.ProjectForMember;
import com.yourong.core.tc.model.query.CollectingProjectQuery;

public interface ProjectManager {
	
	public Page<Project> findByPage(Page<Project> pageRequest, Map<String, Object> map) throws ManagerException;

	public int insert(Project record) throws ManagerException;

	public int insertSelective(Project record) throws ManagerException;
	
	/**
	 * 更新项目状态
	 * @param newStatus 新状态
	 * @param currentStatus 当前状态
	 * @param projectId  项目编号
	 * @return
	 * @throws ManagerException
	 */
	public int updateProjectStatus(int newStatus, int currentStatus, Long projectId) throws ManagerException;
	
	/**
	 * 根据编号获得项目信息
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public Project selectByPrimaryKey(Long id) throws ManagerException;
	
	/**
	 * 根据项目编号获得项目信息
	 * @param projectId
	 * @return
	 */
	public ProjectBiz findProjectById(Long projectId) throws ManagerException;
	
	/**
	 * 更新项目
	 * @param record
	 * @return
	 * @throws ManagerException
	 */
	public int updateByPrimaryKeySelective(Project record) throws ManagerException;

	/**
	 * 通过项目id和投资额获取年化收益
	 * @param investAmount 投资额
	 * @param projectId 项目编号
	 * @return
	 * @throws ManagerException
	 */
	public BigDecimal getAnnualizedRateByProjectIdAndInvestAmount(
			BigDecimal investAmount, Long projectId)throws ManagerException;
	
	/**
	 * 根据编号删除项目（逻辑删除）
	 * @param id
	 * @return
	 */
	public int deleteProjectById(Long id) throws ManagerException;

	/**
	 * 把项目状态置为投资中，提供给定时器处理的方法
	 * @return
	 * @throws ManagerException
	 */
	public int investingProjectTask() throws ManagerException;
	
	/**
	 * 把项目状态置为已截止，提供给定时器处理的方法
	 * @return
	 * @throws ManagerException
	 */
	int endSaleProjectTask() throws ManagerException;
	
	/**
	 * 把项目状态置为已还款，提供给定时器处理的方法
	 * @return
	 * @throws ManagerException
	 */
	int repaymentProjectTask() throws ManagerException;
	
	
	/**
	 * 项目列表
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	public Map<String,Object> findFrontProjectListByPage(ProjectQuery projectQuery) throws ManagerException;
	
	/**
	 * 项目列表,附加额外信息
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	public Page<ProjectForFront> findFrontProjectListExtraByPage(ProjectQuery projectQuery) throws ManagerException;
	
	/**
	 * 项目列表
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	public Page<ProjectForFront> p2pFindFrontProjectListByPage(ProjectQuery projectQuery) throws ManagerException;
	
	/**
	 * 检查项目名称或项目中期数是否存在
	 * @param name 项目名称
	 * @param namePeriod 项目名称中的期数
	 * @return
	 */
	public boolean checkNameExists(String name, String namePeriod) throws ManagerException;
	
	/**
	 * 检查用户名是否存在
	 * @param name
	 * @param namePeriod 项目名称中的期数
	 * @param id
	 * @return
	 */
	public boolean checkNameExists(String name, String namePeriod, Long id) throws ManagerException;
	
	/**
	 * 更新上线时间&销售截止时间
	 * @param onlineTime 上线时间
	 * @param saleEndTime 销售截止时间
	 * @param id 项目编号
	 * @return
	 * @throws ManagerException
	 */
	public int updateOnlineTimeAndEndDate(Timestamp onlineTime, Timestamp saleEndTime, Long id) throws ManagerException;
	
	/**
	 * 更新销售截止时间
	 * @param saleEndTime 销售截止时间
	 * @param id 项目编号
	 * @return
	 * @throws ManagerException
	 */
	public int updateEndDate(Timestamp saleEndTime, Long id) throws ManagerException;
	
	/**
	 * 获得最大的推荐值
	 * @return
	 * @throws ManagerException
	 */
	public int findMaxRecommendWeight() throws ManagerException;
	
	/**
	 * 推荐项目
	 * @param id
	 * @param recommendWeight
	 * @return
	 * @throws ManagerException
	 */
	public int recommendProject(Long id, int recommendWeight) throws ManagerException;
	
	/**
	 * 重置项目排序权重
	 * @param recommendWeight
	 * @return
	 * @throws ManagerException
	 */
	public int resetRecommendWeight(int recommendWeight) throws ManagerException;
	
	
	public Page<Project> findProjectRecommendByPage(Page<Project> pageRequest, Map<String, Object> map) throws ManagerException;

	/**
	 * 查询首页投资中的项目列表
	 * @param num 数量
	 * @return
	 * @throws ManagerException
	 */
	public List<ProjectForFront> findIndexInvestingProjectList(Integer num,Integer investType) throws ManagerException;

	/**
	 * 通过项目id查询ProjectForFront
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 */
	public ProjectForFront getProjectForFrontByProjectId(Long projectId) throws ManagerException;
	
	/**
	 * 查询推荐项目列表
	 * @param num 数量
	 * @return
	 * @throws ManagerException
	 */
	public List<ProjectForFront> findRecommendProjectList(Integer num,Integer investType) throws ManagerException;
	
	/**
	 * 下架非投资中状态时的推荐项目
	 * @return
	 * @throws ManagerException
	 */
	public int cancelRecommendByNotInvestingState() throws ManagerException;
	
	/**
	 * 查询非投资中的首页项目列表
	 * @param num
	 * @return
	 * @throws ManagerException
	 */
	public List<ProjectForFront> findIndexNotInvestingProjectList(Integer num,Integer investType) throws ManagerException;
	
	/**
	 * 根据项目ID取消推荐
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public int cancelRecommendByProjectId(Long id) throws ManagerException;
	
	/**
	 * 查询不同放款状态的项目
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	public List<Long> findProjectByLoanStatus(Map<String, Object> map) throws ManagerException;
	
	/**
	 * 获取项目个数根据项目状态
	 * @param status
	 * @return
	 * @throws ManagerException
	 */
	public int findProjectCountByStatus(Integer status) throws ManagerException;
	
	/**
	 * 获得投资中的新手项目
	 * @param num
	 * @return
	 * @throws ManagerException
	 */
	public List<ProjectForFront> getNoviceProjectByInvesting(Integer num) throws ManagerException;
	
	/**
	 * 获得非投资状态的新手项目（已满额、已截止、已还款）
	 * @param num
	 * @return
	 * @throws ManagerException
	 */
	public List<ProjectForFront> getNoviceProjectByNotInvesting(Integer num) throws ManagerException;
	
	/**
	 * 根据债权id获取项目id
	 * @param debtId
	 * @return
	 * @throws ManagerException
	 */
	Long findProjectIdByDebtId(@Param("debtId")Long debtId) throws ManagerException;
	
	/**
	 * 提供给众牛平台的项目数据
	 * @return
	 * @throws ManagerException
	 */
	public List<Project> getProjectListToZhongNiu() throws ManagerException;
	
	/**
	 * 获得即将上线的项目
	 * @return
	 * @throws ManagerException
	 */
	public List<Project> findUpcomingProject() throws ManagerException;

	
	/**
	 * 获得APP最大的推荐值
	 * @return
	 * @throws ManagerException
	 */
	public int findMaxAppRecommendWeight() throws ManagerException;
	
	/**
	 * 推荐项目到App
	 * @param id
	 * @param recommendWeight
	 * @return
	 * @throws ManagerException
	 */
	public int recommendAppProject(Long id, int recommendWeight) throws ManagerException;
	
	/**
	 * 重置项目排序权重
	 * @param recommendWeight
	 * @return
	 * @throws ManagerException
	 */
	public int resetAppRecommendWeight(int recommendWeight) throws ManagerException;
	
	/**
	 * 根据项目ID取消推荐
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public int cancelAppRecommendByProjectId(Long id) throws ManagerException;
	
	/**
	 * 查询App推荐项目列表
	 * @param num 数量
	 * @return
	 * @throws ManagerException
	 */
	public List<ProjectForFront> findAppRecommendProjectList(Integer num) throws ManagerException;
	
	/**
	 * 查询APP首页展示项目
	 * @param num
	 * @param status
	 * @return
	 * @throws ManagerException
	 */
	public List<ProjectForFront> findAppIndexProjectList(Integer num, Integer status) throws ManagerException;

	
	/**
	 * 获得投资中的项目
	 * @return
	 */
	public List<ProjectForFront> queryInvestingProject() throws ManagerException;
	
	/**
	 * 获得投资中的项目,纯项目校验
	 * @return
	 */
	public List<Project> isInvestingProjectByProject() throws ManagerException;
	

	/**
	 * 
	 * @param sort
	 * @param recommendType
	 * @return
	 */
	public Project getProjectBySortIndex(int sort, int recommendType) throws ManagerException;
	
	/**
	 * 查询符合创建绿狗合同的项目
	 * @param paramMap
	 * @return
	 * @throws ManagerException
	 */
	public List<Project> queryProjectFromLvGou(Map<String, Object> paramMap) throws ManagerException;
	
	/**
	 * 获取着陆页推荐项目
	 * @return
	 */
	public ProjectForFront findLandingRecommendProject() throws ManagerException;
	
	/**
	 * 判断类目下是否存在项目
	 * @param projectType
	 * @param debtType 
	 * @param instalment 
	 * @return
	 * @throws ManagerException
	 */
	public boolean isExistProjectByType(String projectType, Integer instalment, String debtType) throws ManagerException;
	
	/**
	 * 获取显示一羊领头专题页的项目
	 */
	public List<ActivityLeadingSheep> getProjectForLeadingSheeps() throws ManagerException;
	
	/**
	 * 
	 * @return
	 * @throws ManagerException
	 */
	public Project getSortFirstAppRecommendProject() throws ManagerException;
	
	/**
	 * 
	 * @return
	 * @throws ManagerException
	 */
	public Project p2pGetSortFirstAppRecommendProject() throws ManagerException;
	
	/**
	 * 
	 * @return
	 * @throws ManagerException
	 */
	public Project getLatestOnLineProject() throws ManagerException;
	
	/**
	 * 
	 * @return
	 * @throws ManagerException
	 */
	public Project p2pGetLatestOnLineProject() throws ManagerException;
	
	/**
	 * 
	 * @return
	 * @throws ManagerException
	 */
	public Project getFinishProject() throws ManagerException;
	
	/**
	 * 
	 * @return
	 * @throws ManagerException
	 */
	public Project p2pGetFinishProject() throws ManagerException;
	
	/**
	 * 
	 * @return
	 * @throws ManagerException
	 */
	public Project getNoviceProject() throws ManagerException;
	
	/**
	 * 
	 * @return
	 * @throws ManagerException
	 */
	public Project p2pGetNoviceProject() throws ManagerException;
	
	/**
	 * 获得首页新客项目
	 * @param num
	 * @return
	 */
	public List<ProjectForFront> findIndexNoviceProject(Integer num) throws ManagerException;
	
	/**
	 * 根据债权类型查询推荐项目
	 * @param paraMap
	 * @return
	 * @throws ManagerException
	 */
	public List<ProjectForFront> getRecommendProjectByGuaranty(Map<String, Object> paraMap) throws ManagerException;
	
	/**
	 * 判断是否为不可使用优惠券的项目类型（目前包括carPayIn、carBusiness）
	 * @param projectGuarantyType   订单的项目类型
	 * @return
	 * @throws ManagerException
	 */
	public boolean isProjectOfCannotUseCoupon(String projectGuarantyType) throws ManagerException;
	
	/**
	 * 获得可设置成预告的项目
	 * @param projectName
	 * @return
	 */
	public List<Project> queryProjectFromNotice(String projectName) throws ManagerException;
	
	/**
	 * 判断项目类型为不能使用收益券的项目类型
	 * @param compareGuarantyType
	 * @param instalment
	 * @return
	 * @throws ManagerException
	 */
	public boolean projectOfCanotUseProfitCoupon(String compareGuarantyType,int instalment) throws ManagerException;
	
	/**
	 * 判断是否为不可使用优惠券的项目类型
	 * @return
	 * @throws ManagerException
	 */
	public boolean isProjectOfCannotUseCoupon(Long projectId)throws ManagerException;

	/**
	 * 判断是否为不可使用收益券的项目类型
	 * @return
	 * @throws ManagerException
	 */
	boolean isProjectOfCannotUseProfitCoupon(Long projectId)throws ManagerException;
	
	/**
	 * 是否为购车分期项目
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 */
	public boolean isBuyCar(Long projectId)throws ManagerException;
	
	/**
	 * 根据企业id获取履约中的项目
	 * @param enterpriseId
	 * @return
	 */
	public List<ProjectInvestingDto> getFullProjectsByEnterpriseId(Long enterpriseId)throws ManagerException ;
	
	/**
	 * 获取企业已还款借款项目总额
	 * @param enterpriseId
	 * @param status
	 * @return
	 */
	public BigDecimal getRepaymentTotalAmountByEnterpriseId(Long enterpriseId)throws ManagerException ;
	
	/**
	 * 获取企业已借款项目总额
	 * @param enterpriseId
	 * @param status
	 * @return
	 */
	public BigDecimal getCurrentTotalAmountByEnterpriseId(Long enterpriseId)throws ManagerException ;
	
	/**
	 * 获取企业历史借款项目总额
	 * @param enterpriseId
	 * @param status
	 * @return
	 */
	public BigDecimal getHistoryTotalAmountByEnterpriseId(Long enterpriseId)throws ManagerException ;
	
	/**
	 * 获取将要还款的增加可用余额
	 * @return
	 */
	public List<Project> repaymentProjectAddAvailableCredit()throws ManagerException;
	
	/**
	 * 获取项目状态为30支付中，且可用余额为0的项目
	 * @return
	 */
	public List<Project> selectPaymentingProject()throws ManagerException;


	/**
	 * @Description: 获取支持一锤定音的项目
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2015年12月21日 上午9:24:23
	 */
	ActivityLeadingSheep getLastProjects() throws ManagerException;
	
	/**
	 * 
	 * @Description:查询特殊项目列表
	 * @param map
	 * @param projectStatus
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年1月6日 上午10:47:49
	 */
	public List<ProjectForFront> selectExtraProject(Map<String, Object> map, int[] projectStatus) throws ManagerException;

	/**
	 * 
	 * @Description:获取首次投资推荐项目
	 * @param num
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年2月26日 下午1:06:17
	 */
	public List<ProjectForFront> getFirstInvestProject(Integer num) throws ManagerException;
	/**
	 * 
	 * @Description:分页查询项目列表
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月4日 下午5:58:42
	 */
	Page<DirectProjectBiz> selectLoanForPagin(Page<DirectProjectBiz> pageRequest, Map<String, Object> map) throws ManagerException;
	
	
	/**
	 * @Description:直投项目分页获取数据
	 * @return
	 * @author: fuyili
	 * @time:2015年12月30日 下午7:31:39
	 */
	public Page<DirectProjectBiz> directFindByPage(Page<DirectProjectBiz> pageRequest,Map<String, Object> map)throws ManagerException;
/**
	 * 
	 * @Description:分页查询还本付息列表
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月6日 上午9:59:49
	 */
	public Page<ProjectInterestBiz> findRepayInterestForPagin(Page<ProjectInterestBiz> pageRequest, Map<String, Object> map)throws ManagerException;

	ProjectInterestBiz findPayPrincipalAndInterestByProject(ProjectInterestBiz interestBiz) throws ManagerException;

	public ProjectInterestBiz selectInterestInfoByPrimaryKey(Long id)throws ManagerException;

	public BigDecimal getThirdAccountMoney(Long memberId) throws ManagerException;

	public ProjectInterestBiz getFlagInfo(Long id)throws ManagerException;
	/**
	 * 
	 * @Description:获取逾期还款记录
	 * @param id
	 * @return
	 * @author: chaisen
	 * @time:2016年1月7日 下午8:15:37
	 */
	public ProjectInterestBiz getOverdueInfo(Long id)throws ManagerException;
	
	
	/**
	 * @Description:添加直投项目
	 * @param project
	 * @return
	 * @author: fuyili
	 * @time:2016年1月6日 上午9:13:06
	 */
	public Project insertDirectProject(DirectProjectBiz directProjectBiz)throws ManagerException;
	
	/**
	 * @Description:根据项目编号或者名称查询项目
	 * @param map
	 * @return
	 * @author: fuyili
	 * @time:2016年1月7日 下午8:12:50
	 */
	public int findDirectProjectByProjectNameOrCode(Map<String, Object> map)throws ManagerException;

	/***
	 *   计算项目本息数据
	 * @param project
	 * @param date  起息时间
	 * @return
	 */
	public List<DebtInterest> calculateInterest(Project project,Date date);
	/**
	 * 
	 * @Description:管理费记录
	 * @param pageRequest
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年1月11日 下午5:30:07
	 */
	public Page<ProjectInterestBiz> selectManageFeeForPagin(Page<ProjectInterestBiz> pageRequest, Map<String, Object> map)throws ManagerException;
	/**
	 * @Description:查询直投项目详情
	 * @param id
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年1月7日 下午8:49:04
	 */
	public DirectProjectBiz findDirectProjectBizById(Long id)throws ManagerException;
	
	/**
	 * 
	 * @Description:查询项目信息
	 * @param id
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月12日 上午10:14:37
	 */
	public ProjectInterestBiz showProject(Long id)throws ManagerException;
	
	/**
	 * @Description:更新直投项目
	 * @param map
	 * @return
	 * @author: fuyili
	 * @time:2016年1月12日 上午11:12:20
	 */
	public int updateDirectProjectSelective(DirectProjectBiz directProjectBiz)throws ManagerException;

	/**
	 * openPlatformKey可更新为null
	 * @param directProjectBiz
	 * @return
	 * @throws ManagerException
     */
	public int updateDirectProjectSelectiveWithNull(DirectProjectBiz directProjectBiz)throws ManagerException;
	/**
	 * 
	 * @Description:查询逾期管理
	 * @param pageRequest
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年1月20日 下午2:14:39
	 */
	public Page<ProjectInterestBiz> findOverdueList(Page<ProjectInterestBiz> pageRequest, Map<String, Object> map)throws ManagerException;
	
	/**
	 * 
	 * @Description:垫资记录是否已存在
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月26日 上午10:55:52
	 */
	public boolean checkIsExistUnderwriteLog(Long interestId)throws ManagerException;
	
	/**
	 * 
	 * @Description:统计还款中的项目个数
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月26日 下午3:06:07
	 */
	public int selectPayingTotalCount( Map<String, Object> map)throws ManagerException;
	/**
	 * 
	 * @Description:分页查询借款列表
	 * @param query
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月26日 下午6:12:46
	 */
	public Page<ProjectInterestBiz> getBorrowList(ProjectBorrowQuery query)throws ManagerException;


	
	/**
	 * @Description:直投项目更新上线和销售截止时间
	 * @param onlineTime
	 * @param saleEndTime
	 * @param id
	 * @param statuses
	 * @return
	 * @author: fuyili
	 * @time:2016年1月25日 下午4:56:37
	 */
	public int updateOnlineAEndTimeById(Timestamp onlineTime,Timestamp saleEndTime, Long id)throws ManagerException;
	
	/**
	 * @Description:直投项目更新销售截止时间
	 * @param saleEndTime
	 * @param id
	 * @param statuses
	 * @return
	 * @author: fuyili
	 * @time:2016年1月25日 下午4:58:00
	 */
    public int updateSaleEndTimeById(Timestamp saleEndTime, Long id)throws ManagerException;
    /**
     * 
     * @Description:逾期还款记录
     * @param projectId
     * @return
     * @throws ManagerException
     * @author: chaisen
     * @time:2016年1月28日 下午1:25:20
     */
	public List<OverdueRepayLog> getOverdueRepayLogRecordByProjectId(Long projectId)throws ManagerException;
	/**
	 * 
	 * @Description:获取滞纳金
	 * @param projectId
	 * @param repayDate
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年1月28日 下午4:34:55
	 */
	public ProjectInterestBiz getOverdueAmount(Long projectId, String repayDate)throws ManagerException;
	
	/**
	 * @Description:查询条件查询会员中心募集中的项目
	 * @param map
	 * @return
	 * @author: zhanghao
	 * @time:2016年1月27日 上午15:23:20
	 */
	public Page<ProjectForMember> selectCollectProjectForMember(CollectingProjectQuery query) throws ManagerException;
	
	/**
	 * @Description:查询会员是否有募集中的项目
	 * @param map
	 * @return
	 * @author: zhanghao
	 * @time:2016年1月28日 上午14:17:20
	 */
	public 	int collectingProject(CollectingProjectQuery query) throws ManagerException ;
	
	/**
	 * @Description:查询会员是否有募集中的项目，对应的投资总额
	 * @param map
	 * @return
	 * @author: zhanghao
	 * @time:2016年3月10日 上午11:39:20
	 */
	public BigDecimal selectCollectProjectForMemberInvestAmount(CollectingProjectQuery query)throws ManagerException;

	/**
	 * @Description:查询会员是否有募集中的项目，最后一笔投资
	 * @param map
	 * @return
	 * @author: zhanghao
	 * @time:2016年3月16日 下午16:08:20
	 */
	public ProjectForMember selectCollectProjectDescTransactionTime(CollectingProjectQuery query)throws ManagerException;
	
	
	/**
	 * 
	 * @Description:获取滞纳金，逾期利息，逾期本金
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月2日 下午2:20:01
	 */
	public  Map<String, Object>  getOverdueInfoByProjectId(Long projectId)throws ManagerException;

	/**
	 * 计算逾期收益
	 * @param project
	 * @param investAmount
	 * @param currentDate
	 * @param extraAnnualizedRate
	 * @return
	 */
	BigDecimal invertExpectEarnings(Project project, BigDecimal investAmount, Date currentDate, BigDecimal extraAnnualizedRate);
	
	/**
	 * @Description:根据项目类型获取投资中的项目个数
	 * @param projectType
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年2月4日 上午9:41:35
	 */
	int getInvestProjectCountByProjectType(String projectType,String investTypeCode)throws ManagerException;
	
	/**
	 * @Description:查询状态为流标中的项目
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年2月18日 上午9:49:40
	 */
	List<Project> findLoseProjects()throws ManagerException;
	
	/**
	 * 
	 * @Description:逾期列表
	 * @param query
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月17日 下午1:57:08
	 */
	public Page<ProjectInterestBiz> getBorrowOverdueList(ProjectBorrowQuery query)throws ManagerException;
	/**
	 * 
	 * @Description:流标
	 * @param query
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月17日 下午3:30:48
	 */
	public Page<ProjectInterestBiz> getBorrowLabelList(ProjectBorrowQuery query)throws ManagerException;

	/**
	 * @Description:定时任务设置项目状态为已流标
	 * @return
	 * @author: fuyili
	 * @time:2016年2月18日 上午11:01:05
	 */
	public int autoLoseProject()throws ManagerException;
	
	/**
	 * 
	 * @Description:募集中的项目详情
	 * @param transactionId
	 * @return
	 * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年2月18日 上午10:15:48
	 */
	public ProjectForMember selectCollectProjectDetail(Long transactionId,Long memberId) throws ManagerException ;

	/**
	 * 直投项目，改变项目状态
	 * @return
	 * @throws ManagerException
	 */
	public int repaymentDirectProjectTask() throws ManagerException;
	
	/**
	 * @Description:流标创建退款
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年2月22日 下午5:07:06
	 */
	public void loseProject(Long projectId) throws ManagerException;
	
	/**
	 * 
	 * @Description:根据 projectId 和repayDate 获取滞纳金
	 * @param projectId
	 * @param repayDate
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月24日 上午9:25:47
	 */
	BigDecimal getLateFeesByProjectIdandRepaydate(Long projectId, Date repayDate) throws ManagerException;
	/**
	 * 
	 * @Description:web 募集中列表
	 * @param query
	 * @return
	 * @author: chaisen
	 * @time:2016年2月25日 下午4:49:51
	 */
	public Page<ProjectInterestBiz> getBorrowRaisingList(ProjectBorrowQuery query)throws ManagerException;
	/**
	 * 
	 * @Description:获取逾期本金、利息，滞纳金
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年2月29日 上午11:57:23
	 */
	public OverdueRepayLogBiz getUnderWriteAmountInfoByProjectId(Long projectId)throws ManagerException;
	/**
	 * 
	 * @Description:已还清的借款
	 * @param query
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年3月1日 下午6:26:47
	 */
	public Page<ProjectInterestBiz> getHasPayoffBorrow(ProjectBorrowQuery query)throws ManagerException;
	/**
	 * 
	 * @Description:还款中的借款
	 * @param query
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年3月2日 上午9:33:31
	 */
	public Page<ProjectInterestBiz> getPayingBorrowList(ProjectBorrowQuery query) throws ManagerException;
	/**
	 * 
	 * @Description:还款中的借款统计
	 * @param query
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年3月2日 上午10:34:43
	 */
	public int selectForPaginCountPayingBorrow(ProjectBorrowQuery query)throws ManagerException;

	/**
	 * @Description:获取交易列表的收益状态
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年3月8日 上午11:23:27
	 */
	boolean getTransactionListProfitStatus(Long projectId) throws ManagerException;
	/**
	 * 
	 * @Description:还款中的借款
	 * @param query
	 * @return
	 * @author: chaisen
	 * @time:2016年3月11日 上午11:06:24
	 */
	public List<ProjectInterestBiz> selectForPaginPayingBorrow(ProjectBorrowQuery query)throws ManagerException;
	/**
	 * 
	 * @Description:统计登录用户的 借款项目数
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年3月11日 下午3:29:52
	 */
	public int countCurrentBorrowerByMemberId(Long memberId)throws ManagerException;
	
	/**
	 * @Description:垫资还款代收回调后续处理业务
	 * @param tradeNo
	 * @param outTradeNo
	 * @param tradeStatus
	 * @return
	 * @author: fuyili
	 * @time:2016年3月15日 下午6:45:06
	 */
	public ResultDO<List<HostingPayTrade>> afterOverdueRepayCollectNotify(String tradeNo, String outTradeNo, String tradeStatus)throws Exception;
	/**
	 * 调用新浪接口远程创建垫资代付
	 * @param hostingPayTrade
	 * @return
	 * @throws Exception
	 */
	public void createSinpayHostingPayTradeForRepay(List<HostingPayTrade> hostingPayTrade)  throws Exception ;
	
	
	/**
	 * 垫资还款代付回调后续处理业务
	 * 
	 * @param tradeStatus
	 * @param tradeNo
	 * @param outTradeNo
	 * @return
	 * @throws Exception
	 */
	public ResultDO<?> afterOverdueRepayHostingPay(String tradeStatus, String tradeNo, String outTradeNo) throws Exception;
	
	/**
	 * @Description:垫资代付异常处理接口，根据代收号发起批量代付
	 * @param collectTradeNo
	 * @return
	 * @throws Exception
	 * @author: fuyili
	 * @time:2016年3月23日 下午2:56:30
	 */
	public ResultDO<?> createHostPayForOverdueRepayByCollectTradeNo(String collectTradeNo) throws Exception;
	
	 /**
	  * @Description:获取直投项目个数
	  * @return
	  * @author: fuyili
	  * @time:2016年3月23日 下午6:57:15
	  */
	 int getDirectProjectCount()throws ManagerException;
	/**
	 * 等本等息项目信息 -最近上线
	 * @param 
	 * @return
	 * @throws ManagerException
	 */
	public Project averageCapitalMethod() throws ManagerException;
	/**
	 * 等本等息项目信息 -履约中
	 * @param 
	 * @return
	 * @throws ManagerException
	 */
	public Project averageCapitalMethoding() throws ManagerException;
	/**
	 * 等本等息项目信息 -预告中
	 * @param 
	 * @return
	 * @throws ManagerException
	 */
	public Project averageCapitalMethodNoticing() throws ManagerException;
	/**
	 * 
	 * @Description:直投项目营收结算
	 * @param pageRequest
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年5月6日 下午4:33:11
	 */
	public Page<DirectSettlementBiz> findDirectSettlementByPage(Page<DirectSettlementBiz> pageRequest, Map<String, Object> map)throws ManagerException;
	/**
	 * 
	 * @Description:推荐项目
	 * @return
	 * @author: zhanghao
	 * @time:2016年5月16日 下午14:03:11
	 */
	public List<Project> getRecommendProject(Integer num) throws ManagerException;
	/**
	 * 
	 * @Description:投资中的债权项目
	 * @return
	 * @author: zhanghao
	 * @time:2016年5月16日 下午15:52:11
	 */
	public List<Project> findInvestingDebtProject(Map<String, Object> map) throws ManagerException;


	public Page<ProjectInterestBiz> findNormalOverdueList(Page<ProjectInterestBiz> pageRequest, Map<String, Object> map)throws ManagerException;

	/**
	 * @Description:保证金归还代收notify回调后处理
	 * @param tradeNo
	 * @param outTradeNo
	 * @param tradeStatus
	 * @return
	 * @author: fuyili
	 * @time:2016年5月27日 上午11:43:55
	 */
	public ResultDO<HostingPayTrade> afterGuaranteeFeeCollectNotify(String tradeNo, String outTradeNo,
			String tradeStatus)throws Exception;

	/**
	 * @Description:保险金归还代付给借款人
	 * @param hostingPayTrades
	 * @author: fuyili
	 * @time:2016年5月27日 下午4:34:17
	 */
	public void createSinpayHostingPayTradeForGuaranteeFee(HostingPayTrade hostingPayTrades);

	/**
	 * @Description:保险金归还代付给借款人notify回调后处理
	 * @param tradeStatus
	 * @param tradeNo
	 * @param outTradeNo
	 * @return
	 * @author: fuyili
	 * @time:2016年5月27日 下午4:47:20
	 */
	public ResultDO<?> afterGuaranteeFeeHostingPay(String tradeStatus, String tradeNo, String outTradeNo)throws Exception;
	/**
	 * @Description:项目保证金归还创建代收
	 * @param project
	 * @throws Exception
	 * @author: fuyili
	 * @time:2016年5月28日 下午1:17:23
	 */
	public ResultDO<HostingCollectTrade> createCollectTradeForGuaranteeFee(Project project)throws Exception;

	/**
	 * 
	 * @Description:代收完成/撤销后续业务
	 * @param projectId
	 * @param reAuthFlag
	 *            : true 如果存在代收成功交易状态的重新发起代收完成/撤销, false 不发起
	 * @throws ManagerException
	 * @author: wangyanji
	 * @return
	 * @time:2016年5月26日 下午2:10:45
	 */
	public ResultDO<Project> afterHandlePreAuthTrade(Long projectId, boolean reAuthFlag) throws Exception;

	/**
	 * 
	 * @Description:逾期结算记录
	 * @param projectId
	 * @param type
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年5月30日 上午10:19:46
	 */
	public List<OverdueRepayLog> getOverdueRepayLogRecordByProjectIdAndType(Long projectId,int type)throws ManagerException;
	
	/**
	 * 
	 * @Description:代收完成接口
	 * @param rDO
	 * @param reAuthFlag
	 * @param freezeList
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年5月27日 上午9:45:56
	 */
	public void finishPreAuthTrade(ResultDO<Project> rDO, List<HostingCollectTrade> freezeList) throws ManagerException;
	
	/**
	 * 
	 * @Description:代收撤销接口
	 * @param rDO
	 * @param reAuthFlag
	 * @param freezeList
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年5月27日 上午9:45:56
	 */
	public void cancelPreAuthTrade(ResultDO<Project> rDO, List<HostingCollectTrade> freezeList) throws ManagerException;
	/**
	 * 
	 * @Description:逾期还款详情
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年5月31日 上午10:35:18
	 */
	public OverdueRepayLogBiz getOverdueAmountInfoByProjectId(Long projectId)throws ManagerException;
	
	/**
	 * 
	 * @Description:债权封装计算预期收益
	 * @param project
	 * @param investAmount
	 * @param extraAnnualizedRate
	 * @return
	 * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年6月13日 想下午14:27:18
	 */
	public BigDecimal expectAmount(Project project, BigDecimal investAmount, BigDecimal extraAnnualizedRate) throws ManagerException;
	/**
	 * 
	 * @Description:滞纳金
	 * @param overdueRepayLog
	 * @param overdueFeeRate
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年6月20日 下午5:59:02
	 */
	//BigDecimal getLateFeeByProjectId(OverdueRepayLog overdueRepayLog, BigDecimal overdueFeeRate) throws ManagerException;


	OverdueRepayLog getLateFeeByProjectId(OverdueRepayLog overdueRepayLog) throws ManagerException;	
	
	/**
	 * @Description:创建本地代付【归还保险金】
	 * @param collectTrade
	 * @param project
	 * @return
	 * @throws Exception
	 * @author: fuyili
	 * @time:2016年7月28日 下午7:51:21
	 */
	public HostingPayTrade createLocalHostPayForGuaranteeFee(HostingCollectTrade collectTrade,Project project) throws Exception;
	
	/**
	 * 
	 * @Description 获得非新手投资项目列表，按上线时间的早晚顺序排列
	 * @param isNovice
	 * @param status
	 * @return
	 * @throws Exception
	 * @author luwenshan
	 * @time 2016年8月17日 下午9:57:57
	 */
	 List<Project> getNoviceInvestingProject(Integer isNovice, Integer status) throws Exception;
	 
	 /**
	  * @Description 通过收益率计算项目收益
	  * @author zhanghao
	  * @time 2016年8月24日 上午11:52:57
	  * */
	 public BigDecimal invertExpectEarningsByRate(Project project, BigDecimal investAmount, Date date, BigDecimal annualizedRate) ;
	 /**
	  * 
	  * @Description:直投抽奖
	  * @param projectId
	  * @param memberId
	  * @return
	  * @author: chaisen
	 * @throws ManagerException 
	 * @throws Exception 
	  * @time:2016年10月31日 下午1:49:50
	  */
	public ResultDO<Object> directProjectLottery(Long projectId, Long memberId, int type) throws  Exception;
	 /**
	  * 
	  * @Description:直投抽奖
	  * @param transactionId
	  * @param memberId
	  * @return
	  * @author: zhanghao
	 * @throws ManagerException 
	 * @throws Exception 
	  * @time:2016年11月11日 下午1:49:50
	  */
	public ResultDO<Object> directProjectLotteryByTransactionId(Long transactionId, Long memberId) throws Exception;
	
	public ResultDO<Object> directProjectLotteryByTransactionIdType(Long transactionId, Long memberId,int type) throws Exception;
	
	/**
	 * 
	 * @Description:快投有奖项目列表
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年11月3日 上午10:52:17
	 */
	public List<ProjectForFront> findQuickInvestRecommendProject() throws ManagerException;
	/**
	 * 
	 * @Description:项目中奖信息
	 * @param modelResult
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年11月3日 下午2:11:52
	 */
	public List<ProjectForFront> findQuickInvestLotteryProject(ActivityLotteryResult modelResult) throws ManagerException;

	public int countLotteryResultByProjectId(ActivityLotteryResult modelResult) throws ManagerException;
	
	/**
	 * 抽奖详情
	 * @Description:抽奖详情
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年11月3日 下午5:23:32
	 */
	public ProjectForDirectLottery getDirectLotteryDetailByProjectId(Long projectId);

	/**
	 * 查询需要直投项目发起代收完成/撤销数据
	 * @return
     */
	public List<AfterHandlePreAuthTradeBiz> queryAfterHandlePreAuthTrade();

	BigDecimal invertExtraInterest(Project project, BigDecimal investAmount,
			Date date, BigDecimal extraAnnualizedRate);
	 /**
	  * 
	  * @Description:查询推介项目
	  * @param number
	  * @param startDay
	  * @param endDay
	  * @return
	  * @throws ManagerException
	  * @author: chaisen
	  * @time:2016年11月8日 下午4:53:42
	  */
	 public List<ProjectForFront> getRecommendProjectByInvestDay(int number, String investArea ,int type) throws ManagerException;

	 public Page<ProjectForFront> findFrontInvestProjectListByPage(ProjectQuery projectQuery) throws ManagerException;

	/**
	 * 
	 * @Description:获取推荐快投项目ID
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2017年1月10日
	 */
	public Long getRecommendQuickProjectId() throws ManagerException;

	/**
	 * 
	 * @Description:获取推荐快投项目
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2017年1月10日
	 */
	public ProjectForFront getRecommendQuickProject(Long projectId) throws ManagerException;
	
	
	
	public List<DebtInterest> calculateInterestForProjectDetail(Project project,Date date,BigDecimal couponAnnualized,Long investAmount);
	/**
	 * 查询借款人已还清项目数
	 * @param borrowerId
	 * @return
     */
	public Integer queryPayOffCountByBorrowerId(Long borrowerId);
	/**
	 * @Description:直投项目分页获取数据
	 * @return
	 * @author: fuyili
	 * @time:2015年12月30日 下午7:31:39
	 */
	public Page<ProjectPackage> directPackagePage(Page<ProjectPackage> pageRequest,Map<String, Object> map,ProjectPackage projectPackage)throws ManagerException;
	
	/**
	 * 计算项目所在的资产包进度
	 * @throws ManagerException
	 */
	public void computerProjectPackageProgress(Long projectId);

	/**
	 * 
	 * @Description:查询满标项目列表
	 * @param map
	 * @return
	 * @author: wangyanji
	 * @time:2017年3月16日
	 */
	public ProjectsPageOutPut getOverPros(Map<String, Object> map) throws ManagerException;

	/**
	 * 
	 * @Description:查询投资中项目列表
	 * @param map
	 * @return
	 * @author: wangyanji
	 * @time:2017年3月16日
	 */
	public ProjectsPageOutPut getInvestingPros(Map<String, Object> map) throws ManagerException;

	/**
	 * 
	 * @Description:查询项目状态
	 * @param ids
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2017年3月17日
	 */
	public ProjectsStatusOutPut getProsStatus(List<Long> ids) throws ManagerException;
}
