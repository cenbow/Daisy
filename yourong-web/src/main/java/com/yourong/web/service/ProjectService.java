package com.yourong.web.service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.CreateCollectTradeResult;
import com.yourong.core.fin.model.OverdueRepayLog;
import com.yourong.core.fin.model.biz.OverdueRepayLogBiz;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectExtra;
import com.yourong.core.ic.model.ProjectInterestBiz;
import com.yourong.core.ic.model.ProjectPackage;
import com.yourong.core.ic.model.ProjectPackageBiz;
import com.yourong.core.ic.model.ProjectPackageLinkModel;
import com.yourong.core.ic.model.ProjectPackageModel;
import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.ic.model.biz.DirectInterestForBorrow;
import com.yourong.core.ic.model.biz.ProjectForFront;
import com.yourong.core.ic.model.biz.ProjectForRewardDetail;
import com.yourong.core.ic.model.biz.ProjectInvestingDto;
import com.yourong.core.ic.model.biz.QuickProjectBiz;
import com.yourong.core.ic.model.query.ProjectBorrowQuery;
import com.yourong.core.ic.model.query.ProjectQuery;
import com.yourong.core.ic.model.query.TransferProjectQuery;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.HostingPayTrade;
import com.yourong.core.tc.model.biz.ProjectForMember;
import com.yourong.core.tc.model.query.CollectingProjectQuery;
import com.yourong.web.dto.CnGoldProjectDto;
import com.yourong.web.dto.MemberSessionDto;
import com.yourong.web.dto.PinYouProject;
import com.yourong.web.dto.ProjectInfoDto;

public interface ProjectService {

	/**
	 * 分页获取项目列表数据
	 * @param projectQuery
	 * @return
	 */
	public Map<String,Object> findProjectListByPage(ProjectQuery projectQuery);
	
	/**
	 * 根据编号查看项目详情
	 * @param id
	 * @return
	 */
	public ProjectInfoDto getProjectInfoById(Long id);
	
	/**
	 * 获得默认图片路径
	 * @param id
	 * @return
	 */
	public String getProjectImagePath(Long id);
	
	
	/**
	 * 获得项目可用余额
	 * @param id
	 * @return
	 */
	public BigDecimal getProjectBalanceById(Long id);

	/**
	 * 查询首页项目列表(固定8个)
	 * @return
	 */
	public ResultDO<ProjectForFront> findIndexProjectList(int default_show_number);
	
	/**
	 * 查询着陆页项目数据
	 * @return
	 */
	public ResultDO<ProjectForFront> findLandProjectList();
	/**
	 * 获得在首页显示的新手项目
	 * @return
	 */
	public ProjectForFront getIndexNoviceProject();
	
	/**
	 * 提供给众牛平台的项目数据
	 * @return
	 */
	public JSONObject getProjectListToZhongNiu();
	
	/**
	 * 提供给众牛平台的项目详情数据
	 * @return
	 */
	public JSONObject getProjectDetailToZhongNiu(Long projectId);
	
	/**
	 * 提供给品友平台正在投资中的项目数据
	 * @return
	 */
	public List<PinYouProject> queryInvestingProjectToPinYou();
	
	/**
	 * 获得投资中的项目数量
	 * @return
	 */
	public int queryInvestingProjectCount();
	
	/**
	 * 获得投资中的项目数量,纯项目校验
	 * @return
	 */
	public int isInvestingProjectByProject();
	
	
	/**
	 * 提供给金投网的数据接口
	 * @return
	 */
	public List<CnGoldProjectDto> queryCnGoldProjectList();
	
	/**
	 * 获取着陆页推荐项目
	 * 优先显示投资中项目，最近上线的投资中项目。
	 * 若没有投资中项目，显示最近上线的履约中项目
	 * @return
	 */
	public ProjectForFront findLandingRecommendProjectProject();
	
	/**
	 * 判断类目下是否存在项目
	 * @param projectType
	 * @return
	 */
	public boolean isExistProjectByType(String projectType,Integer instalment,String debtType);
	
	/**
	 * 获取新客项目
	 * @return
	 */
	public ResultDO<ProjectForFront> getNewCustomerProject();
	
	/**
	 * 获取着陆页推荐项目
	 * @return
	 */
	public ResultDO<ProjectForFront> getRecommendProject();
	
	/**
	 * 根据债权类型查询推荐项目
	 * @return
	 */
	public ResultDO<ProjectForFront> getRecommendProjectByGuaranty(Map<String, Object> paraMap);
	
	/**
	 * 判断是否为不可使用优惠券的项目类型
	 * @param projectGuarantyType   订单的项目类型
	 * @return
	 * @throws ManagerException
	 */
	public boolean isProjectOfCannotUseCoupon(Long projectId);

	/**
	 * 判断是否为不可使用收益券的项目类型
	 * @param projectGuarantyType   订单的项目类型
	 * @return
	 * @throws ManagerException
	 */
	boolean isProjectOfCannotUseProfitCoupon(Long projectId);
	
	/**
	 * 是否为购车分期
	 * @param projectId
	 * @return
	 */
	boolean isBuyCar(Long projectId);
	
	/**
	 * 根据企业id获取履约中的项目
	 * @param enterpriseId
	 * @return
	 */
	List<ProjectInvestingDto> getInvestingProjectsByEnterpriseId(Long enterpriseId);
	
	/**
	 * 
	 * @Description:获取特殊项目标识
	 * @param projectId
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月1日 下午3:51:32
	 */
	public int getProjectActivitySign(Long projectId);

	/**
	 * 
	 * @Description:新手首次投资项目推荐
	 * @return
	 * @author: wangyanji
	 * @time:2016年2月26日 上午11:30:36
	 */
	public ResultDO<Object> firstInvestProject();
	
	/**
	 * 根据memberid 获取借款中心数据
	 * @Description:TODO
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年1月26日 下午2:48:45
	 */
	public ResultDO getBorrowCenterData(Long memberId);
	/**
	 * 
	 * @Description:分页查询借款列表
	 * @param query
	 * @return
	 * @author: chaisen
	 * @time:2016年1月26日 下午6:11:00
	 */
	public Page<ProjectInterestBiz> getBorrowList(ProjectBorrowQuery query);
	
	/**
	 * 
	 * @Description:查询项目本息记录
	 * @param query
	 * @return
	 * @author: chaisen
	 * @time:2016年1月26日 下午7:39:02
	 */
	public List<DirectInterestForBorrow> selectInterestByProjectId(Long projectId);
	/**
	 * 
	 * @Description:逾期还款记录
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年1月26日 下午8:01:29
	 */
	public List<OverdueRepayLogBiz> getOverdueByProjectId(Long projectId,int type);
	/**
	 * 
	 * @Description:获取滞纳金，逾期利息，逾期本金
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年2月2日 下午2:13:00
	 */
	public ResultDO getOverdueInfo(Long projectId);
	
	/**
	 * @Description:根据项目id获取项目详情
	 * @param projectId
	 * @return
	 * @author: fuyili
	 * @time:2016年1月26日 下午1:20:29
	 */
	ProjectInfoDto getAllProjectInfo(Long projectId,Long memberId);
	
	/**
	 * 查询条件查询会员中心募集中的项目
	 * @param 
	 * @return
	 */
	Page<ProjectForMember> selectCollectProjectForMember(CollectingProjectQuery query);

	/**
	 * 查询用户是否有投资募集中的项目
	 * @param 
	 * @return
	 */
	public int collectingProject(Long memberId);
	
	/**
	 * 查询用户募集中的项目的最后一笔
	 * @param 
	 * @return
	 */
	public Long selectCollectProjectDescTransactionTime(Long memberId);
	/**
	 * 查询用户是否有投资募集中的项目,对应的投资总额
	 * @param 
	 * @return
	 */
	public BigDecimal selectCollectProjectForMemberInvestAmount(Long memberId);
	
	/**
	 * 用户点击、已阅，募集中的项目
	 * @param 
	 * @return
	 */
	public void clickCollectingProject(Map<String, Object> map) ;



	Project selectByPrimaryKey(Long id);
	

	/**
	 * @Description:根据项目类型查询投资中的项目总数
	 * @param projectType
	 * @param investTypeCode
	 * @return
	 * @author: fuyili
	 * @time:2016年2月4日 上午10:52:26
	 */
	public int getInvestProjectCountByProjectType(String projectType,String investTypeCode);
	/**
	 * 
	 * @Description:逾期还款列表
	 * @param query
	 * @return
	 * @author: chaisen
	 * @time:2016年2月17日 下午1:55:29
	 */
	public Page<ProjectInterestBiz> getBorrowOverdueList(ProjectBorrowQuery query);
	/**
	 * 
	 * @Description:流标
	 * @param query
	 * @return
	 * @author: chaisen
	 * @time:2016年2月17日 下午3:29:42
	 */
	public Page<ProjectInterestBiz> getBorrowLabelList(ProjectBorrowQuery query);
	
	/**
	 * 
	 * @Description:募集中的项目详情
	 * @param transactionId
	 * @return
	 * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年2月18日 上午10:15:48
	 */
	public ResultDO<ProjectForMember> selectCollectProjectDetail(Long transactionId,Long memberId);
	
	/**
	 * @Description:垫资还款
	 * @param projectId
	 * @return
	 * @author: fuyili
	 * @time:2016年2月23日 上午11:42:18
	 */
	public ResultDO<HostingCollectTrade> toUnderWriteRepay(Long projectId, String payerIp) throws Exception;
	/**
	 * 调用新浪接口远程创建垫资还款代收
	 * @param order
	 * @return
	 */
	public ResultDto<CreateCollectTradeResult> createSinpayHostingCollectTradeForRepay(HostingCollectTrade collectTrade, String payerIp)
			throws Exception;

	/**
	 * 垫资还款代收回调后续处理业务
	 * @param tradeNo
	 * @param outTradeNo
	 * @param tradeStatus
	 * @return
	 */
	public ResultDO<List<HostingPayTrade>> afterOverdueRepayCollectNotify(String tradeNo,
			String outTradeNo, String tradeStatus)  throws Exception ;

	/**
	 * 调用新浪接口远程创建垫资代付
	 * @param hostingPayTrade
	 * @return
	 * @throws Exception
	 */
	public void createSinpayHostingPayTradeForRepay(List<HostingPayTrade> hostingPayTrade)  throws Exception ;

	/**
	 * 垫资还款代付回调后续处理业务
	 * @param tradeStatus
	 * @param tradeNo
	 * @param outTradeNo
	 * @return
	 * @throws Exception
	 */
	public ResultDO<?> afterOverdueRepayHostingPay(String tradeStatus,
			String tradeNo, String outTradeNo)  throws Exception ;

	/**
	 * @Description:首页债权推荐项目
	 * @return
	 * @author: fuyili
	 * @time:2016年2月26日 上午11:06:55
	 */
	ResultDO<ProjectForFront> findIndexDebtProjectList();

	/**
	 * @Description:首页直投推荐项目
	 * @return
	 * @author: fuyili
	 * @time:2016年2月26日 上午11:07:19
	 */
	ResultDO<ProjectForFront> findIndexDirectProjectList();
	

	/**
	 * @Description:首页转让推荐项目
	 * @return
	 * @author: zhanghao
	 * @time:2016年9月21日 下午14:07:19
	 */
	ResultDO<ProjectForFront> findTransferProjectList();
	
	/**
	 * 
	 * @Description:募集中
	 * @param query
	 * @return
	 * @author: chaisen
	 * @time:2016年2月25日 下午4:47:37
	 */
	public Page<ProjectInterestBiz> getBorrowRaisingList(ProjectBorrowQuery query);
	/**
	 * 
	 * @Description:获取逾期记录
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年2月29日 上午11:51:14
	 */
	public ResultDO<OverdueRepayLogBiz> getOverdueRecordListByProjectId(Long projectId);
	/**
	 * 获取逾期本金，利息，滞纳金
	 * @Description:TODO
	 * @return
	 * @author: chaisen
	 * @time:2016年2月29日 上午11:54:27
	 */
	public ResultDO<OverdueRepayLogBiz> getUnderWriteAmountInfo(Long projectId);
	/**
	 * 
	 * @Description:已还清的借款
	 * @param query
	 * @return
	 * @author: chaisen
	 * @time:2016年3月1日 下午6:25:08
	 */
	public Page<ProjectInterestBiz> getHasPayoffBorrow(ProjectBorrowQuery query);
	/**
	 * 
	 * @Description:还款中的借款
	 * @param query
	 * @return
	 * @author: chaisen
	 * @time:2016年3月2日 上午9:35:49
	 */
	public Page<ProjectInterestBiz> getPayingBorrowList(ProjectBorrowQuery query);
	
	
	/**
	 * @Description:获取交易列表的收益状态
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年3月8日 上午11:23:27
	 */
	boolean getTransactionListProfitStatus(Long projectId);
	/**
	 * 
	 * @Description:统计登录用户的 借款项目数
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年3月11日 下午3:27:06
	 */
	public int countCurrentBorrowerByMemberId(Long memberId);
	
	/**
	  * @Description:获取直投项目个数
	  * @return
	  * @author: fuyili
	  * @time:2016年3月23日 下午6:57:15
	  */
	 int getDirectProjectCount();
	 
		/**
		 * @Description:获取当前项目借款人是否为企业
		 * @param projectId
		 * @return
		 * @throws ManagerException
		 * @author: zhanghao	
		 * @time:2016年4月14日 下午17:11:27
		 */
	 boolean isBorrowerTypeEnterprise(Long projectId);
	
	/**
	 * 等本等息项目信息
	 * @return
	 */
	
	public ResultDO averageCapitalMethod();
	
	/**
	 * @Description:保证金归还代收notify回调
	 * @param tradeNo
	 * @param outTradeNo
	 * @param tradeStatus
	 * @return
	 * @throws Exception
	 * @author: fuyili
	 * @time:2016年5月27日 上午11:43:07
	 */
	public ResultDO<HostingPayTrade> afterGuaranteeFeeCollectNotify(String tradeNo, String outTradeNo, String tradeStatus)
			throws Exception;

	/**
	 * @Description:保证金代付给借款人
	 * @param hostingPayTrades
	 * @throws Exception
	 * @author: fuyili
	 * @time:2016年5月27日 下午4:33:13
	 */
	void createSinpayHostingPayTradeForGuaranteeFee(HostingPayTrade hostingPayTrades) throws Exception;

	/**
	 * @Description:保证金归还代付notify回调
	 * @param tradeStatus
	 * @param tradeNo
	 * @param outTradeNo
	 * @return
	 * @author: fuyili
	 * @time:2016年5月27日 下午4:46:05
	 */
	public ResultDO<?> afterGuaranteeFeeHostingPay(String tradeStatus, String tradeNo, String outTradeNo)throws Exception;
	/**
	 * 
	 * @Description:获取逾期本息记录
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年5月30日 下午4:23:40
	 */
	public ResultDO<OverdueRepayLogBiz> geOverdueDebtInterestListByProjectId(Long projectId);
	/**
	 * 
	 * @Description:逾期详情
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年5月31日 上午10:31:34
	 */
	public ResultDO<OverdueRepayLogBiz> getOverdueAmountInfo(Long projectId);

	/**
	 * @Description:普通逾期还款
	 * @param overdueRepayId
	 * @param payerIp
	 * @return
	 * @author: fuyili
	 * @time:2016年6月4日 下午5:40:15
	 */
	public ResultDO<OverdueRepayLog> toOverdueRepay(Long overdueRepayId, String payerIp)throws Exception;
	/**
	 * 
	 * @Description:项目费用详情
	 * @param projectId
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年6月12日 下午5:12:11
	 */
	public ResultDO<ProjectInterestBiz> getProjectFeeDetail(Long projectId);
	/**
	 * 
	 * @Description:普通逾期还款记录
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年6月16日 下午3:53:40
	 */
	public List<OverdueRepayLogBiz> getCommonOverdueRepayLogRecord(Long projectId);

	/**
	 * @Description:处理转让项目代付回调
	 * @param tradeStatus
	 * @param tradeNo
	 * @param outTradeNo
	 * @return
	 * @throws Exception
	 * @author: fuyili
	 * @time:2016年9月23日 上午10:18:03
	 */
	public ResultDO<?> afterHostPayForTransferSuccess(String tradeStatus, String tradeNo, String outTradeNo) throws Exception;
	
	/**
	 * @Description:账户中心-我的投资-转让记录
	 * @param transferProjectQuery
	 * @return
	 * @throws Exception
	 * @author: fuyili
	 * @time:2016年9月24日 下午12:46:48
	 */
	public Page<TransferProject> findTransferProjectForMemberCenter(TransferProjectQuery transferProjectQuery);
	/**
	 * 
	 * @Description:直投抽奖
	 * @param projectId
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年10月31日 下午1:48:19
	 */
	public ResultDO<Object> directProjectLottery(Long projectId, Long memberId, int type);
	/**
	 * 
	 * @Description:快投抽奖专题页
	 * @param user
	 * @return
	 * @author: chaisen
	 * @time:2016年11月2日 下午4:05:59
	 */
	public Object quickLotteryProject(MemberSessionDto user);
	/**
	 * 
	 * @Description:奖励详情
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年11月3日 下午9:20:15
	 */
	public ResultDO<ProjectForRewardDetail> getrewardDetail(Long projectId);
	/**
	 * 
	 * @Description:统计次数
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年11月7日 下午2:39:05
	 */
	public Integer getQuickLotteryNumber(Long memberId);

	public ProjectExtra getProjectActivitySignByProjectId(Long projectId);

	/**
	 * 获取推荐的快投项目
	 * @return
	 */
	public QuickProjectBiz getRecommendQuickProject();

	/**
	 * 获取资产实体信息
	 * @param projectQuery
	 * @return
	 */
	public ProjectPackage findProjectPackage(Long projectPackageId);
	
	/**
	 * 根据资产包ID获取资产项目列表信息
	 * @param projectPackageId
	 * @return
	 */
	public List<ProjectPackageLinkModel> getProjectPackageLinkModelList(Long projectPackageId);
	/**
	 * 获取未满标的最大资产包信息列表
	 * @return
	 */
	public ResultDO<ProjectPackage> getProjectPackageIndex();
	
	/**
	 * 查询资产项目分页信息
	 * @param query
	 * @return
	 */
	public List<ProjectPackage> ProjectPackageList(Integer status);

	/**
	 * 获取所有已售罄的项目列表
	 * @return
	 */
	public List<ProjectPackage> getAllCompletedProjectPackageList();

	/**
	 * 获取无重礼限制条件
	 */

	public String getMinRewardLimit();
	
}
