package com.yourong.core.ic.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.ic.model.DirectSettlementBiz;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectInterestBiz;
import com.yourong.core.ic.model.biz.AfterHandlePreAuthTradeBiz;
import com.yourong.core.ic.model.biz.ProjectForFront;
import com.yourong.core.ic.model.biz.ProjectInvestingDto;
import com.yourong.core.ic.model.query.ProjectBorrowQuery;
import com.yourong.core.ic.model.query.ProjectQuery;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.mc.model.biz.ActivityLeadingSheepProject;
import com.yourong.core.os.biz.ProjectForOpen;
import com.yourong.core.os.biz.ProjectStatus;
import com.yourong.core.os.biz.ProjectsPageOutPut;
import com.yourong.core.tc.model.biz.ProjectForMember;
import com.yourong.core.tc.model.query.CollectingProjectQuery;
//@CacheNamespace(implementation=com.yourong.common.cache.MybatisRedisCache.class)
public interface ProjectMapper {
	
    @Delete({
        "delete from ic_project",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @Options(flushCache=true)
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into ic_project (debt_id, ",
        "project_type, name, ",
        "keyword, short_desc, ",
        "profit_type, risk_level, ",
        "total_amount, min_invest_amount, max_invest_amount",
        "increment_amount, annualized_rate_type, ",
        "min_annualized_rate, max_annualized_rate, ",
        "increment_annualized_rate, start_date, ",
        "end_date, online_time, ",
        "sale_end_time, interest_from, ",
        "status, publish_id, ",
        "publish_time,  ",
        "audit_time, audit_message, ",
        "recommend, recommend_weight, ",
        "create_time, update_time, ",
        "remarks, del_flag,description,thumbnail,is_novice,join_lease)",
        "values (#{id,jdbcType=BIGINT}, #{debtId,jdbcType=BIGINT}, ",
        "#{projectType,jdbcType=VARCHAR}, #{name,jdbcType=VARCHAR}, ",
        "#{keyword,jdbcType=VARCHAR}, #{shortDesc,jdbcType=VARCHAR}, ",
        "#{profitType,jdbcType=VARCHAR}, #{riskLevel,jdbcType=VARCHAR}, ",
        "#{totalAmount,jdbcType=DECIMAL}, #{minInvestAmount,jdbcType=DECIMAL}, ",
        "#{maxInvestAmount,jdbcType=DECIMAL},",
        "#{incrementAmount,jdbcType=DECIMAL}, #{annualizedRateType,jdbcType=INTEGER}, ",
        "#{minAnnualizedRate,jdbcType=DECIMAL}, #{maxAnnualizedRate,jdbcType=DECIMAL}, ",
        "#{incrementAnnualizedRate,jdbcType=DECIMAL}, #{startDate,jdbcType=TIMESTAMP}, ",
        "#{endDate,jdbcType=TIMESTAMP}, #{onlineTime,jdbcType=TIMESTAMP}, ",
        "#{saleEndTime,jdbcType=TIMESTAMP}, #{interestFrom,jdbcType=INTEGER}, ",
        "#{status,jdbcType=INTEGER}, #{publishId,jdbcType=BIGINT}, ",
        "#{publishTime,jdbcType=TIMESTAMP}, #{auditId,jdbcType=BIGINT}, ",
        "#{auditTime,jdbcType=TIMESTAMP}, #{auditMessage,jdbcType=VARCHAR}, ",
        "#{recommend,jdbcType=INTEGER}, #{recommendWeight,jdbcType=INTEGER}, ",
        "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP}, ",
        "#{remarks,jdbcType=VARCHAR}, #{delFlag,jdbcType=INTEGER}, ",
        "#{description,jdbcType=VARCHAR},#{thumbnail,jdbcType=VARCHAR}, ",
        "#{isNovice,jdbcType=INTEGER},#{joinLease,jdbcType=INTEGER}) ",
        "#{onlineNotice,jdbcType=INTEGER},#{noticeNotice,jdbcType=INTEGER}) ",
    })
    @Options(flushCache=true)
    int insert(Project record);

    int insertSelective(Project record);

    @Select({
        "select",
        "id, debt_id, original_project_number, project_type, name, annualized_rate, keyword, short_desc, profit_type, risk_level, ",
        "total_amount, min_invest_amount, max_invest_amount, increment_amount, annualized_rate_type, min_annualized_rate, ",
        "max_annualized_rate, increment_annualized_rate, start_date, end_date, online_time, ",
        "sale_end_time, interest_from, status, publish_id, publish_time, audit_id, audit_time, ",
        "audit_message, recommend, recommend_weight, create_time, update_time, remarks, ",
        "del_flag,description,thumbnail,is_novice,join_lease,app_recommend,app_recommend_weight,online_notice,notice_notice,borrower_id, ",
        "invest_type, manage_fee_rate,overdue_fee_rate,borrower_id,borrower_type,enterprise_id,borrow_period,borrow_period_type,security_type,instalment,sale_complated_time,borrow_detail, ",
        "prepayment,prepayment_time,prepayment_remark, guarantee_fee_rate,risk_fee_rate,late_fee_rate,introducer_fee_rate,introducer_id,transfer_flag,",
        "transfer_after_interest,open_platform_key,package_flag",
        "from ic_project",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    Project selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Project record);

    @Update({
        "update ic_project",
        "set debt_id = #{debtId,jdbcType=BIGINT},",
          "project_type = #{projectType,jdbcType=VARCHAR},",
          "name = #{name,jdbcType=VARCHAR},",
          "keyword = #{keyword,jdbcType=VARCHAR},",
          "short_desc = #{shortDesc,jdbcType=VARCHAR},",
          "profit_type = #{profitType,jdbcType=VARCHAR},",
          "risk_level = #{riskLevel,jdbcType=VARCHAR},",
          "total_amount = #{totalAmount,jdbcType=DECIMAL},",
          "min_invest_amount = #{minInvestAmount,jdbcType=DECIMAL},",
          "max_invest_amount = #{maxInvestAmount,jdbcType=DECIMAL},",
          "increment_amount = #{incrementAmount,jdbcType=DECIMAL},",
          "annualized_rate_type = #{annualizedRateType,jdbcType=INTEGER},",
          "min_annualized_rate = #{minAnnualizedRate,jdbcType=DECIMAL},",
          "max_annualized_rate = #{maxAnnualizedRate,jdbcType=DECIMAL},",
          "increment_annualized_rate = #{incrementAnnualizedRate,jdbcType=DECIMAL},",
          "start_date = #{startDate,jdbcType=TIMESTAMP},",
          "end_date = #{endDate,jdbcType=TIMESTAMP},",
          "online_time = #{onlineTime,jdbcType=TIMESTAMP},",
          "sale_end_time = #{saleEndTime,jdbcType=TIMESTAMP},",
          "interest_from = #{interestFrom,jdbcType=INTEGER},",
          "status = #{status,jdbcType=INTEGER},",
          "publish_id = #{publishId,jdbcType=BIGINT},",
          "publish_time = #{publishTime,jdbcType=TIMESTAMP},",
          "audit_id = #{auditId,jdbcType=BIGINT},",
          "audit_time = #{auditTime,jdbcType=TIMESTAMP},",
          "audit_message = #{auditMessage,jdbcType=VARCHAR},",
          "recommend = #{recommend,jdbcType=INTEGER},",
          "recommend_weight = #{recommendWeight,jdbcType=INTEGER},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP},",
          "remarks = #{remarks,jdbcType=VARCHAR},",
          "del_flag = #{delFlag,jdbcType=INTEGER},",
          "description = #{description,jdbcType=VARCHAR}",
          "thumbnail = #{thumbnail,jdbcType=VARCHAR}",
          "is_novice = #{isNovice,jdbcType=INTEGER}",
          "join_lease = #{joinLease,jdbcType=INTEGER}",
          "online_notice = #{onlineNotice,jdbcType=INTEGER}",
          "notice_notice = #{noticeNotice,jdbcType=INTEGER}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @Options(flushCache=true)
    int updateByPrimaryKey(Project record);

//    Page<Project> findByPage(Page<Project> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") int[] ids);

    List<Project> selectForPagin(@Param("map") Map<String, Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String, Object> map);
    
    @Update({
    	" update ic_project set status = #{status,jdbcType=INTEGER}, update_time=now()  where id = #{id,jdbcType=BIGINT} and status = #{currentStatus,jdbcType=INTEGER}"
    })
    @Options(flushCache=true)
    int updateProjectStatus(@Param("status")int statusCode, @Param("currentStatus") int currentStatus, @Param("id")Long projectId);
    
    @Update({
    	" update ic_project set del_flag = -1, update_time=now()  where id = #{id,jdbcType=BIGINT}"
    })
    @Options(flushCache=true)
    int deleteProjectById(Long id);
    
    @Update({
    	" update ic_project set status= 30, update_time=now() where status = 20 and online_time <= now()"
    })
    @Options(flushCache=true)
    int investingProjectTask();
    
    @Update({
    	" update ic_project set status= 60 , update_time=now()  where status in (30,40) and sale_end_time <= now()"
    })
    @Options(flushCache=true)
    int endSaleProjectTask();
    
    @Update({
    	" update ic_project set status= 70, update_time=now()  where status > 20 and status < 70 and end_date <= curdate()     and   invest_type =1"
    })
    @Options(flushCache=true)
    int repaymentProjectTask();

    @Update({
            " update ic_project set status= 70 , update_time=now() where status =52  and end_date <= curdate()     and   invest_type =2"
    })
    @Options(flushCache=true)
    int repaymentDirectProjectTask();

    @Update({
    	" update ic_project set online_time= #{onlineTime,jdbcType=TIMESTAMP}, sale_end_time=#{saleEndTime,jdbcType=TIMESTAMP}, update_time=now() where status = 20 and id = #{id,jdbcType=BIGINT}"
    })
    @Options(flushCache=true)
    int updateOnlineTimeAndEndDate(@Param("onlineTime")Timestamp onlineTime, @Param("saleEndTime")Timestamp saleEndTime, @Param("id")Long id);
    
    @Update({
    	" update ic_project set sale_end_time=#{saleEndTime,jdbcType=TIMESTAMP} , update_time=now() where status = 30 and id = #{id,jdbcType=BIGINT}"
    })
    @Options(flushCache=true)
    int updateEndDate(@Param("saleEndTime")Timestamp saleEndTime, @Param("id")Long id);
    
    /**
     * 检查项目名称或项目中期数是否存在
     * 
     * @param name
     * @param namePeriod
     * @param id
     * @return
     */
    Project checkNameExists(@Param("name")String name, @Param("namePeriod")String namePeriod, @Param("id")Long id);
    
    Integer findMaxRecommendWeight();
    
  /*  @Update({
    	" update ic_project set recommend_weight = #{recommendWeight, jdbcType=INTEGER}, recommend = 1 , update_time=now() where id=#{id,jdbcType=BIGINT}"
    })
    @Options(flushCache=true)
    int recommendProject(@Param("id")Long id, @Param("recommendWeight")int recommendWeight);*/
    /**
     * 
     * @Description:推介项目到pc
     * @param projectId
     * @param recommendWeight
     * @return
     * @author: chaisen
     * @time:2016年4月29日 上午11:33:56
     */
    public int recommendProject(@Param("projectId")Long projectId, @Param("recommendWeight")int recommendWeight);
/*    @Update({
    	" update ic_project set recommend_weight = recommend_weight + 1, update_time=now() where recommend = 1 and recommend_weight >= #{recommendWeight, jdbcType=INTEGER}"
    })
    @Options(flushCache=true)
    int resetRecommendWeight(@Param("recommendWeight")int recommendWeight);*/
    
    
    List<Project> selectProjectRecommendForPagin(@Param("map") Map<String, Object> map);

    int selectProjectRecommendForPaginTotalCount(@Param("map") Map<String, Object> map);
    
    
    @Update({
    	"update ic_project set recommend=0,app_recommend=0 , update_time=now() where (recommend=1 or app_recommend=1) and status > 30"
    })
    @Options(flushCache=true)
    int cancelRecommendByNotInvestingState();
    
    @Update({
    	"update ic_project set recommend=0, update_time=now()  where id=#{id,jdbcType=BIGINT}"
    })
    @Options(flushCache=true)
    int cancelRecommendByProjectId(Long id);
    /**********************************************WEB START**********************************************/
    
    List<ProjectForFront> selectForPaginFront(@Param("projectQuery")ProjectQuery projectQuery);
    
    List<ProjectForFront> p2pSelectForPaginFront(@Param("projectQuery")ProjectQuery projectQuery);

    int selectForPaginTotalCountFront(@Param("projectQuery")ProjectQuery projectQuery);
    
    int p2pSelectForPaginTotalCountFront(@Param("projectQuery")ProjectQuery projectQuery);
    
    /**查询投资中的项目列表**/
	List<ProjectForFront> findIndexInvestingProjectList(@Param("map")Map<String, Object> map);
	/**通过项目id查询ProjectForFront**/
	ProjectForFront getProjectForFrontByProjectId(@Param("id")Long projectId);
	
	List<ProjectForFront> findRecommendProjectList(@Param("map")Map<String, Object> map);
    
    /**查询非投资中的首页项目列表**/
	List<ProjectForFront> findIndexNotInvestingProjectList(@Param("map")Map<String, Object> map);
	
	/**
	 * 获得投资中的新手项目
	 * @param num
	 * @return
	 * @throws ManagerException
	 */
	public List<ProjectForFront> getNoviceProjectByInvesting(@Param("num")Integer num);
	
	/**
	 * 获得非投资状态的新手项目（已满额、已截止、已还款）
	 * @param num
	 * @return
	 * @throws ManagerException
	 */
	public List<ProjectForFront> getNoviceProjectByNotInvesting(@Param("num")Integer num);
    /**********************************************WEB END**********************************************/
	
	/**查询不同放款状态的项目**/
	List<Long> findProjectByLoanStatus(@Param("map")Map<String, Object> map);	
	
	/**获取项目个数根据项目状态**/
	int findProjectCountByStatus(@Param("status") Integer status);
	
	/**根据债权id获取项目id*/
	Long findProjectIdByDebtId(@Param("debtId")Long debtId);
	
	/**提供给众牛平台的项目数据**/
	List<Project> getProjectListToZhongNiu();
	
	/**获得即将上线的项目**/
	List<Project> findUpcomingProject();	


	/**
	 * 获得APP最大的推荐值
	 * @return
	 * @throws ManagerException
	 */
	public Integer findMaxAppRecommendWeight();
	
	/**
	 * 推荐项目到App
	 * @param projectId
	 * @param recommendWeight
	 * @return
	 * @throws ManagerException
	 */
	public int recommendAppProject(@Param("projectId")Long projectId, @Param("recommendWeight")int recommendWeight);
	
	/**
	 * 重置项目排序权重
	 * @param recommendWeight
	 * @return
	 * @throws ManagerException
	 */
	public int resetAppRecommendWeight(@Param("recommendWeight")int recommendWeight);
	/**
	 * 
	 * @Description:重置项目排序权重 pc
	 * @param recommendWeight
	 * @return
	 * @author: chaisen
	 * @time:2016年4月29日 上午11:19:46
	 */
	public int resetRecommendWeight(@Param("recommendWeight")int recommendWeight);
	
	/**
	 * 根据项目ID取消推荐
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 */
	public int cancelAppRecommendByProjectId(@Param("projectId")Long projectId);
	
	/**
	 * 查找App端推荐的项目
	 * @param num
	 * @return
	 */
	public List<ProjectForFront> findAppRecommendProjectList(@Param("num")Integer num);
	
	/**
	 * 查询APP首页展示项目
	 * @param num
	 * @param status
	 * @return
	 * @throws ManagerException
	 */
	public List<ProjectForFront> findAppIndexProjectList(@Param("num")Integer num, @Param("status")Integer status);
	

	/**
	 * 获得正在投资中的项目
	 * @return
	 */
	List<ProjectForFront> queryInvestingProject();
	
	
	/**
	 * 获得正在投资中的项目,纯项目校验
	 * @return
	 */
	List<Project> isInvestingProjectByProject();
	
	/**
	 * 
	 * @param sort
	 * @param recommendType
	 * @return
	 */
	public Project getProjectBySortIndex(@Param("sort")int sort, @Param("recommendType")int recommendType);
	
	/**
	 * 查询没有推送绿狗的项目
	 * @param map
	 * @return
	 */
	public List<Project> queryProjectFromLvGou(@Param("map")Map<String, Object> map);
	
	/**
	 * 获取着陆页推荐项目
	 * @return
	 */
	public ProjectForFront findLandingRecommendProject();
	
	/**
	 * 判断类目下是否存在项目
	 * @param projectType
	 * @return
	 */
	public Integer isExistProjectByType(@Param("projectType")String projectType,@Param("instalment")Integer instalment,@Param("debtType")String debtType);
	
	/**
	 * 获取投资中且0投资的项目 
	 * @param map
	 * @return
	 */
	public List<ActivityLeadingSheepProject> findInvestingAndFirstInvestProjects(@Param("limitSize")Integer limitSize);
	
	/**
	 * 获取支持一鸣惊人的项目
	 * @return
	 */
	public List<ActivityLeadingSheepProject> findSupportMostInvestProject();
	
	/**
	 *  获取可以一锤定音、幸运女神的项目
	 */
	public List<ActivityLeadingSheepProject> findInvestingProjectForLeadingSheep();
	
	/**
	 * 根据排序规则获得APP推荐的项目
	 * @return
	 */
	public Project getSortFirstAppRecommendProject();
	
	/**
	 * 根据排序规则获得APP推荐的项目
	 * @return
	 */
	public Project p2pGetSortFirstAppRecommendProject();
	
	/**
	 * 获得新上线的项目
	 * @return
	 */
	public Project getLatestOnLineProject();
	
	/**
	 * 获得新上线的项目
	 * @return
	 */
	public Project p2pGetLatestOnLineProject();
	
	/**
	 * 获得非投资状态的项目
	 * @return
	 */
	public Project getFinishProject();
	
	/**
	 * 获得非投资状态的项目
	 * @return
	 */
	public Project p2pGetFinishProject();
	
	/**
	 * 获得新客项目
	 * @return
	 */
	public Project getNoviceProject();
	
	/**
	 * 获得新客项目
	 * @return
	 */
	public Project p2pGetNoviceProject();
	
	/**
	 * 获得推荐首页新客项目
	 * @param num
	 * @return
	 */
	public List<ProjectForFront> findIndexNoviceProject(@Param("num")Integer num);
    /**
	 * 获得没有推荐 首页新客项目
	 * @param num
	 * @return
	 */
	public List<ProjectForFront> findIndexNoviceProjectNoRecommend(@Param("num")Integer num);
	
	/**
	 * 根据债权类型查询推荐项目
	 * @param map
	 * @return
	 */
	public List<ProjectForFront> getRecommendProjectByGuaranty(@Param("map")Map<String, Object> map);
	
	/**
	 * 获得可设置成预告的项目
	 * @param projectName
	 * @return
	 */
	public List<Project> queryProjectFromNotice(@Param("projectName")String projectName);
	
	/**
	 * 根据企业id获取履约中的项目
	 * @param enterpriseId
	 * @return
	 */
	public List<ProjectInvestingDto> getFullProjectsByEnterpriseId(@Param("enterpriseId") Long enterpriseId);
	
	/**
	 * 根据状态获取企业借款项目总额
	 * @param enterpriseId
	 * @param status
	 * @return
	 */
	public BigDecimal getTotalAmountByEnterpriseIdAndStatus(@Param("enterpriseId") Long enterpriseId,@Param("status") int[] status);
	
	/**
	 * 获取将要还款的项目
	 * @return
	 */
	public List<Project> selectToRepaymentProject();
	
	/**
	 *  获取可以一掷千金的项目
	 */
	public List<ActivityLeadingSheepProject> findLastAndMostProjectForLeadingSheep();

	/**
	 * 获取项目状态为30，可用余额为0的的项目
	 * @return
	 */
	public List<Project> selectPaymentingProject();

	
	/**
	 * 
	 * @Description:查询特殊项目列表
	 * @param map
	 * @param projectStatus
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月6日 上午10:52:57
	 */
	public List<ProjectForFront> selectExtraProject(@Param("map")Map<String, Object> map, @Param("projectStatus")int[] projectStatus);
	
	/**
	 * 
	 * @Description:获取首次投资推荐项目
	 * @param num
	 * @return
	 * @author: wangyanji
	 * @time:2016年2月26日 下午12:01:04
	 */
	public List<ProjectForFront> getFirstInvestProject(@Param("num") Integer num);
	/**
	 * 
	 * @Description:分页查询放款列表
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年1月11日 下午8:41:40
	 */
	public List<Project> selectLoanForPagin(@Param("map") Map<String, Object> map);
	 /**
	  * 
	  * @Description:放款列表统计
	  * @param map
	  * @return
	  * @author: chaisen
	  * @time:2016年1月11日 下午8:41:56
	  */
	 int selectLoanForPaginTotalCount(@Param("map") Map<String, Object> map);
	
	
	/**
	 * @Description:直投项目分页获取数据
	 * @return
	 * @author: fuyili
	 * @time:2015年12月30日 下午7:31:39
	 */
	public List<Project> directSelectForPagin(@Param("map")Map<String, Object> map);
	
	/**
	 * @Description:直投项目分页获取数据
	 * @return
	 * @author: fuyili
	 * @time:2015年12月30日 下午7:32:11
	 */
	public int directSelectForPaginTotalCount(@Param("map")Map<String, Object> map);
	
	/**
	 * @Description:添加直投项目
	 * @param project
	 * @return
	 * @author: fuyili
	 * @time:2016年1月6日 上午9:13:06
	 */
	public int insertDirectProject(Project project);
	
	/**
	 * @Description:根据项目编号或者名称查询项目
	 * @param map
	 * @return
	 * @author: fuyili
	 * @time:2016年1月7日 下午8:12:50
	 */
	public int findDirectProjectByProjectNameOrCode(@Param("map")Map<String, Object> map);

	/**
	 * 
	 * @Description:管理费页面记录数
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年1月11日 下午5:35:40
	 */
	public int selectManageFeeForPaginTotalCount(@Param("map") Map<String, Object> map);
	/**
	 * 
	 * @Description:管理费分页
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年1月11日 下午5:36:16
	 */
	List<Project> selectManageFeeForPagin(@Param("map") Map<String, Object> map);
	
	/**
	 * @Description:更新直投项目
	 * @param map
	 * @return
	 * @author: fuyili
	 * @time:2016年1月12日 上午11:12:20
	 */
	public int updateDirectProjectSelective(Project project);

	/**
	 * openPlatformKey可更新为null
	 * @param project
	 * @return
     */
	public int updateDirectProjectSelectiveWithNull(Project project);
	/**
	 * 
	 * @Description:逾期管理记录数
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年1月20日 下午5:55:07
	 */
	public int selectOverdueForPaginTotalCount(@Param("map") Map<String, Object> map);
	/**
	 * 
	 * @Description:逾期管理列表
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年1月20日 下午5:56:04
	 */
	List<ProjectInterestBiz> selectOverdueForPagin(@Param("map") Map<String, Object> map);
	
	/**
	 * 
	 * @Description:统计还款中的项目个数
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年1月26日 下午3:03:40
	 */
	public int selectPayingTotalCount(@Param("map") Map<String, Object> map);
	
	/**
	 * 
	 * @Description:借款列表
	 * @param projectQuery
	 * @return
	 * @author: chaisen
	 * @time:2016年1月26日 下午7:15:28
	 */
	List<ProjectInterestBiz> selectForPaginBorrow(@Param("projectQuery")ProjectBorrowQuery projectQuery);
	/**
	 * 
	 * @Description:统计借款列表
	 * @param projectQuery
	 * @return
	 * @author: chaisen
	 * @time:2016年2月29日 下午2:49:15
	 */
    int selectForPaginTotalCountBorrow(@Param("projectQuery")ProjectBorrowQuery projectQuery);
	
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
	public int updateOnlineAEndTimeById(@Param("onlineTime")Timestamp onlineTime, @Param("saleEndTime")Timestamp saleEndTime, @Param("id")Long id,@Param("statuses")int[] statuses);
	
	/**
	 * @Description:直投项目更新销售截止时间
	 * @param saleEndTime
	 * @param id
	 * @param statuses
	 * @return
	 * @author: fuyili
	 * @time:2016年1月25日 下午4:58:00
	 */
    public int updateSaleEndTimeById(@Param("saleEndTime")Timestamp saleEndTime, @Param("id")Long id,@Param("statuses")int[] statuses);
    
    /**
	 * 查询条件查询会员中心募集中的项目
	 * @param 
	 * @author: zhanghao
	 * @return
	 */
	List<ProjectForMember> selectCollectProjectForMember(CollectingProjectQuery query);
	
	
	/**
	 *  查询条件查询会员中心募集中的项目个数
	 * @param transactionQuery
	 * @author: zhanghao
	 * @return
	 */
	int selectCollectProjectForMemberCount(CollectingProjectQuery query);

	/**
	 * 查询条件查询会员中心募集中的项目
	 * @param 
	 * @author: zhanghao
	 * @return
	 */
	List<ProjectForMember> selectCollectProjectForMemberCounting(CollectingProjectQuery query);
	
	
	
	/**
	 * 查询条件查询会员中心募集中的项目,对应的投资总额
	 * @param 
	 * @author: zhanghao
	 * @return
	 */
	public BigDecimal selectCollectProjectForMemberInvestAmount(CollectingProjectQuery query);
	/**
	 * 查询条件查询会员中心募集中的项目,最后一笔投资
	 * @param 
	 * @author: zhanghao
	 * @return
	 */
	ProjectForMember  selectCollectProjectDescTransactionTime (CollectingProjectQuery query);
	
	/**
	 * 
	 * @Description:web 借款列表
	 * @param projectQuery
	 * @return
	 * @author: chaisen
	 * @time:2016年2月17日 下午2:15:28
	 */
	List<ProjectInterestBiz> selectOverdueList(@Param("projectQuery")ProjectBorrowQuery projectQuery);
	
	
	/**
	 * 获取募集中的项目详情
	 * @param transactionId
	 * @return
	 */
	ProjectForMember selectCollectProjectDetail(@Param("transactionId")Long transactionId,@Param("memberId")Long memberId);

	/**
	 * @Description:查询项目根据项目状态
	 * @param status
	 * @return
	 * @author: fuyili
	 * @time:2016年2月18日 上午9:48:47
	 */
	List<Project> findProjectsByStatus(@Param("status")Integer status);
	/**
	 * 
	 * @Description:已还清的借款
	 * @param projectQuery
	 * @return
	 * @author: chaisen
	 * @time:2016年3月1日 下午6:20:26
	 */
	List<ProjectInterestBiz> selectForPaginHasPayoffBorrow(@Param("projectQuery")ProjectBorrowQuery projectQuery);
	
	/**
	 * 
	 * @Description:还款中的借款
	 * @param projectQuery
	 * @return
	 * @author: chaisen
	 * @time:2016年3月2日 上午9:32:27
	 */
	List<ProjectInterestBiz> selectForPaginPayingBorrow(@Param("projectQuery")ProjectBorrowQuery projectQuery);
	
	/**
	 * 
	 * @Description:还款中的借款统计
	 * @param projectQuery
	 * @return
	 * @author: chaisen
	 * @time:2016年3月2日 上午10:32:06
	 */
	 int selectForPaginCountPayingBorrow(@Param("projectQuery")ProjectBorrowQuery projectQuery);
	 /**
	  * 
	  * @Description:逾期记录数统计
	  * @param projectQuery
	  * @return
	  * @author: chaisen
	  * @time:2016年3月2日 下午1:06:28
	  */
	 int selectCountOverdueList(@Param("projectQuery")ProjectBorrowQuery projectQuery);
	 /**
	  * 
	  * @Description:统计用户借款项目数
	  * @param borrowerId
	  * @return
	  * @author: chaisen
	  * @time:2016年3月11日 下午3:14:10
	  */
	 public int countCurrentBorrowerByMemberId(@Param("borrowerId")Long borrowerId);
	 /**
	  * 
	  * @Description:募集中统计
	  * @param projectQuery
	  * @return
	  * @author: chaisen
	  * @time:2016年3月16日 下午3:48:01
	  */
	 int selectForPaginBorrowCount(@Param("projectQuery")ProjectBorrowQuery projectQuery);
	 /**
	  * 
	  * @Description:已还清的借款统计
	  * @param projectQuery
	  * @return
	  * @author: chaisen
	  * @time:2016年3月16日 下午4:06:51
	  */
	 int selectForPaginHasPayoffBorrowCount(@Param("projectQuery")ProjectBorrowQuery projectQuery);
	 
	 /**
	  * @Description:获取直投项目个数
	  * @return
	  * @author: fuyili
	  * @time:2016年3月23日 下午6:57:15
	  */
	 int getDirectProjectCount();
	 /**
	  * 
	  * @Description:等本等息项目信息 -最近上线
	  * @param 无
	  * @return
	  * @author: zhanghao
	  * @time:2016年4月11日 下午5:23:51
	  */
	 Project averageCapitalMethod();
	 /**
	  * 
	  * @Description:等本等息项目信息 -履约中
	  * @param 无
	  * @return
	  * @author: zhanghao
	  * @time:2016年4月11日 下午5:06:51
	  */
	 Project averageCapitalMethoding();
	 /**
	  * 
	  * @Description:等本等息项目信息 -预告中
	  * @param 无
	  * @return
	  * @author: zhanghao
	  * @time:2016年4月22日 下午1:06:51
	  */
	 Project averageCapitalMethodNoticing();
	 /**
	  * 
	  * @Description:直投项目营收结算
	  * @param map
	  * @return
	  * @author: chaisen
	  * @time:2016年5月6日 下午4:23:23
	  */
	 List<DirectSettlementBiz> selectForPaginInterestSettlement(@Param("map") Map<String, Object> map);
	 /**
	  * 
	  * @Description:直投项目营收结算统计
	  * @param map
	  * @return
	  * @author: chaisen
	  * @time:2016年5月6日 下午4:23:39
	  */
	 int selectForPaginTotalInterestSettlementCount(@Param("map") Map<String, Object> map);
	 
	 /**
	  * @Description:是否为借款人
	  * @return
	  * @author: zhanghao	
	  * @time:2016年5月6日 上午9:44:15
	  */
	 Integer ifBorrower(@Param("memberId") Long memberId);
	 /**
	  * 
	  * @param enterpriseID
	  * @return
	  */
	 int countProjectByEnterpriseId(@Param("enterpriseId")Long enterpriseId);
	 
	 /**
	  * @Description:推荐项目
	  * @return
	  * @author: zhanghao	
	  * @time:2016年5月16日 上午11:44:15
	  */
	 List<Project> getRecommendProject(@Param("num") Integer num);
	 
	 /**获得投资中的债权项目**/
	 List<Project> findInvestingDebtProject(@Param("map") Map<String, Object> map);	
	 /**
	  * 
	  * @Description:逾期管理统计
	  * @param map
	  * @return
	  * @author: chaisen
	  * @time:2016年5月25日 上午11:14:27
	  */
	int selectNormalOverdueForPaginTotalCount(@Param("map") Map<String, Object> map);
	/**
	 * 
	 * @Description:逾期管理列表
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年5月25日 上午11:14:31
	 */
	List<ProjectInterestBiz> selectNormalOverdueForPagin(@Param("map") Map<String, Object> map);
	 
	 /**
	  * @Description:查询提前还款管理列表
      * @return
	  * @author: zhanghao
	  * @time:2016年5月25日  下午14:29:47
	 */
	 List<Project> findEarlyRepaymentProject(@Param("map") Map<String, Object> map);	 
	 
	 /**
	  * @Description:查询提前还款管理列表
      * @return
	  * @author: zhanghao
	  * @time:2016年5月25日  下午14:29:47
	 */
	 int findEarlyRepaymentProjectCount(@Param("map") Map<String, Object> map);
	 /**
	  * 
	  * @Description:一羊领头过滤p2p
	  * @param limitSize
	  * @return
	  * @author: chaisen
	  * @time:2016年6月8日 上午11:55:58
	  */
	 public List<ActivityLeadingSheepProject> p2pFindInvestingAndFirstInvestProjects(@Param("limitSize")Integer limitSize);
	 /**
	  * 
	  * @Description:一鸣惊人过滤p2p
	  * @return
	  * @author: chaisen
	  * @time:2016年6月8日 下午1:41:27
	  */
	 List<ActivityLeadingSheepProject> p2pFindSupportMostInvestProject();
	 /**
	  * 
	  * @Description:一锤定音过滤p2p
	  * @return
	  * @author: chaisen
	  * @time:2016年6月8日 下午1:45:25
	  */
	 List<ActivityLeadingSheepProject> p2pFindInvestingProjectForLeadingSheep();
	 /**
	  * 
	  * @Description:一掷千金过滤p2p
	  * @return
	  * @author: chaisen
	  * @time:2016年6月8日 下午1:47:12
	  */
	 List<ActivityLeadingSheepProject> p2pFindLastAndMostProjectForLeadingSheep();
	 
	 /**
	  * 
	  * @Description 获得非新手投资项目列表，按上线时间的早晚顺序排列
	  * @param isNovice
	  * @param status
	  * @return
	  * @author luwenshan
	  * @time 2016年8月17日 下午9:56:40
	  */
	 List<Project> findNoviceInvestingProject(@Param("isNovice")Integer isNovice, @Param("status")Integer status);
	 
	 /**
	  * 
	  * @Description:获取推介的快投有奖项目
	  * @return
	  * @author: chaisen
	  * @time:2016年11月3日 上午10:48:37
	  */
	 List<ProjectForFront> findQuickInvestRecommendProject();
	 /**
	  * 
	  * @Description:中奖项目信息
	  * @param recodr
	  * @return
	  * @author: chaisen
	  * @time:2016年11月3日 下午2:09:07
	  */
	 List<ProjectForFront> findQuickInvestLotteryProject(ActivityLotteryResult recodr);

	int countLotteryResultByProjectId(ActivityLotteryResult modelResult);
	
	/**
	 * 查询当天还款的借款人项目信息
	 * 
	 * @param investType
	 * @return
	 * @throws Exception
	 * @author: luwenshan
	 * @time:2016年12月9日 下午5:08:32
	 */
	 List<Project> selectTodayPayBorrowerProject(@Param("investType")Integer investType);
	 
	List<AfterHandlePreAuthTradeBiz> queryAfterHandlePreAuthTrade();
	
	 
	 List<ProjectForFront> getRecommendProjectByInvestDay(@Param("projectQuery")ProjectQuery projectQuery);

	int selectForPaginTotalCountFrontInvest(@Param("projectQuery")ProjectQuery projectQuery);

	List<ProjectForFront> selectForPaginFrontInvest(@Param("projectQuery")ProjectQuery projectQuery);
	 
	/**
	 * 
	 * @Description:获取推荐快投项目ID
	 * @return
	 * @author: wangyanji
	 * @time:2017年1月10日
	 */
	Long getRecommendQuickProjectId();

	/**
	 * 
	 * @Description:获取推荐快投项目
	 * @param projectId
	 * @return
	 * @author: wangyanji
	 * @time:2017年1月10日
	 */
	ProjectForFront getRecommendQuickProject(@Param("projectId") Long projectId);
    /**
     * 平台成功履约的项目总数=待放款+已放款	+已还款
     * @return
     */
	@Select({
    	" select count(1) from ic_project where `status` in (51,52,70)"
    })
    @Options(flushCache=true)
	int  getProjectTotalCount();
	
	@Update({
		"  update  ic_project  set package_flag = #{packageFlag} where id= #{projectId}"
	})
	public int updateProjectPakcageFlag(@Param("packageFlag") Integer packageFlag,@Param("projectId") Long projectId);
	/**
	 * 查询借款人已还清项目数
	 * @param borrowerId
	 * @return
     */
	int queryPayOffCountByBorrowerId(@Param("borrowerId")Long borrowerId);
	/**
	 * 
	 * @Description:查询满标项目列表总数
	 * @param map
	 * @return
	 * @author: wangyanji
	 * @time:2017年3月17日
	 */
	ProjectsPageOutPut getOverProsCount(@Param("map") Map<String, Object> map);

	/**
	 * 
	 * @Description:查询满标项目列表
	 * @param map
	 * @return
	 * @author: wangyanji
	 * @time:2017年3月16日
	 */
	List<ProjectForOpen> getOverPros(@Param("map") Map<String, Object> map);

	/**
	 * 
	 * @Description:查询投资中项目列表总数
	 * @param map
	 * @return
	 * @author: wangyanji
	 * @time:2017年3月17日
	 */
	ProjectsPageOutPut getInvestingProsCount(@Param("map") Map<String, Object> map);

	/**
	 * 
	 * @Description:查询投资中项目列表
	 * @param map
	 * @return
	 * @author: wangyanji
	 * @time:2017年3月16日
	 */
	List<ProjectForOpen> getInvestingPros(@Param("map") Map<String, Object> map);

	/**
	 * 
	 * @Description:TODO
	 * @param ids
	 * @return
	 * @author: wangyanji
	 * @time:2017年3月17日
	 */
	List<ProjectStatus> getProsStatus(@Param("ids") List<Long> ids);
}