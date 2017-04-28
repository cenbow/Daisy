package com.yourong.api.service;


import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.yourong.api.dto.IndexDataDto;
import com.yourong.api.dto.IndexDto;
import com.yourong.api.dto.ProjectDetailDto;
import com.yourong.api.dto.ProjectInfoDto;
import com.yourong.api.dto.ProjectListDto;
import com.yourong.api.dto.ProjectPackageListDto;
import com.yourong.api.dto.ProjectPageDto;
import com.yourong.api.dto.RecommendProjectDto;
import com.yourong.api.dto.RepaymentPlanDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.ic.model.ProjectPackage;
import com.yourong.core.ic.model.ProjectPackageLinkModel;
import com.yourong.core.ic.model.biz.ProjectForFront;
import com.yourong.core.ic.model.biz.ProjectInvestingDto;
import com.yourong.core.ic.model.biz.TransferProjectBiz;
import com.yourong.core.ic.model.query.ProjectQuery;
import com.yourong.core.ic.model.query.TransferProjectQuery;

public interface ProjectService {

	/**
	 * 分页获取项目列表数据
	 * @param projectQuery
	 * @return
	 */
	public ProjectPageDto<ProjectListDto> queryPagingProject(ProjectQuery projectQuery);
	
	/**
	 * 分页获取项目列表数据,提供给旧版app过滤直投数据
	 * @param projectQuery
	 * @return
	 */
	public ProjectPageDto<ProjectListDto> p2pQueryPagingProject(ProjectQuery projectQuery);
	
	
	/**
	 * 获得项目收益
	 * @param pid
	 * @return
	 */
	public ResultDTO getProjectInterestList(Long pid, Long memberId, int projectCategory, Long transferId);
	
	/**
	 * 根据编号查看项目详情
	 * @param id
	 * @return
	 */
	public ResultDTO<ProjectInfoDto> getProjectInfoById(Long id);
	
	/**
	 * 根据项目编号获得项目明细
	 * @param id
	 * @return
	 */
	public ProjectDetailDto  getProjectDetail(Long id);
	
	/**
	 * 项目合同资料图片
	 * @param id
	 * @return
	 */
	public List<BscAttachment> getProjectContractImage(Long id);
	
	
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
	public ResultDO<ProjectForFront> findIndexProjectList();
	
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
	 * 查询首页接口数据
	 * @return
	 */
	public ResultDTO<IndexDataDto> queryIndexData();
	
	/**
	 * 分页获取项目列表数据
	 * @param projectQuery
	 * @return
	 */
	//public Page<ProjectForFront> findProjectListByPage(ProjectQuery projectQuery);
	
	/**
	 * 查询首页接口数据
	 * @return
	 */
	public ResultDTO<IndexDto> queryIndexData2(Long memberId , Integer version);
	
	
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
	 * 根据项目编号获得直投项目明细
	 * @param id
	 * @return
	 */
	public ProjectDetailDto  p2pProjectDetail(Long id,Long memberId);
	/**
	 * p2p-项目合同资料图片
	 * @param id
	 * @return
	 */
	public ProjectDetailDto  getP2pProjectContractImage(Long id,Long memberId);
	/**
	 * p2p-项目合同资料图片
	 * @param id
	 * @return
	 */
	public ProjectDetailDto  p2pProjectrepaymentPlan(Long id);
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
	 * 
	 * @Description:获取精选项目
	 * @param memberId
	 * @param date
	 * @return
	 * @author: zhanghao
	 * @time:2016年5月16日 上午11:45:03
	 */
	public List<RecommendProjectDto> getRecommendProject(Integer num) ;
	/**
	 * p2p-项目还款计划
	 * @param id
	 * @return
	 */
	public RepaymentPlanDto p2pProjectrepaymentPlanJosn(Long id);
	
	
	/**
	 * 分页获取转让项目列表数据
	 * @param projectQuery
	 * @return
	 */
	public ProjectPageDto<ProjectListDto> queryTransferProject(TransferProjectQuery transferProjectQuery);
	
	
	public TransferProjectBiz transferProjectDetail(Long id);
	
	/**
	 * 转让项目还款计划
	 * @param id
	 * @return
	 */
	public ResultDTO<Object>  transferProjectrepaymentPlan(Long id);
	

	/**
	 * 直投抽奖页面
	 * @param id
	 * @return
	 */
	public Object directRewardDetail(Long id);

	/**
	 * 
	 * @Description:加载快投推荐项目
	 * @return
	 * @author: wangyanji
	 * @time:2017年1月10日
	 */
	public ProjectListDto getRecommendQuickProject();
	/**
	 * 查询资产包项目列表
	 * @param id
	 * @return
	 */
	public ProjectPackageListDto<ProjectPackage>  queryAllProjectPackageList();
	/**
	 * 查询资产包项目列表
	 * @param id
	 * @return
	 */
	public Page<ProjectPackage>  queryProjectPackageList(ProjectQuery projectQuery);
	/**
	 * 获取资产包数据
	 * @param projectPackageId
	 * @return
	 */
	public ProjectPackageListDto<ProjectPackageLinkModel> findProjectPackageList(Long projectPackageId);
	/**
	 * 获取资产包实体
	 * @param projectPackageId
	 * @return
	 */
	public ProjectPackage getProjectPackage(Long projectPackageId);
	
	public String getMinRewardLimit();
	/**
	 * 统计销售完成的资产包数量
	 * @param status
	 * @return
	 */
	public int countCompletedProjectPackage(Integer status);
}
