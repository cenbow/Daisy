package com.yourong.web.utils;

import com.yourong.common.enums.DebtEnum;
import com.yourong.common.util.SpringContextHolder;
import com.yourong.core.ic.model.ProjectExtra;
import com.yourong.web.service.ProjectService;

/**
 * 项目util
 * 
 * @author fuyili 2015年4月16日上午10:24:03
 */
public class ProjectUtils {
	public static final String CAR_PROJECT_NAME = "车有融项目";
	public static final String BUY_CAR_PROJECT_NAME = "购车融项目";
	private static ProjectService projectService = SpringContextHolder.getBean(ProjectService.class);

	/**
	 * 判断是否有投资中的项目
	 */
	public boolean isInvestingProject() {
		int projectCount = projectService.queryInvestingProjectCount();
		if (projectCount > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断是否有投资中的项目,纯项目校验
	 */
	public boolean isInvestingProjectByProject() {
		int projectCount = projectService.isInvestingProjectByProject();
		if (projectCount > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断类目下是否存在项目
	 * @param projectType
	 * @return
	 */
	public boolean isExistEquityProject(){
		return projectService.isExistProjectByType("equity",0,DebtEnum.DEBT_TYPE_CREDIT.getCode());
	}
	
	
	/**
	 * 判断类目下是否存在项目
	 * @param projectType
	 * @return
	 */
	public boolean isExistCarPayInProject(){
		return projectService.isExistProjectByType("carPayIn",0,DebtEnum.DEBT_TYPE_CREDIT.getCode());
	}
	
	/**
	 * 判断类目下是否存在项目
	 * @param projectType
	 * @return
	 */
	public boolean isExistCarBusinessProject(){
		return projectService.isExistProjectByType("carBusiness",0,DebtEnum.DEBT_TYPE_CREDIT.getCode());
	}
	
	/**
	 * 判断类目下是否存在项目
	 * @param projectType
	 * @return
	 */
	public boolean isExistBuyCarProject(){
		return projectService.isExistProjectByType("car",1,DebtEnum.DEBT_TYPE_COLLATERAL.getCode());
	}
	
	/**
	 * 判断类目下是否存在项目
	 * @param projectType
	 * @return
	 */
	public boolean isExistRunCompanyProject(){
		return projectService.isExistProjectByType("runCompany",0,DebtEnum.DEBT_TYPE_CREDIT.getCode());
	}
	
	/**
	 * 判断是否为不可使用优惠券的项目类型（目前包括carPayIn、carBusiness）
	 * @return
	 */
	public boolean isProjectOfCannotUseCoupon(Long projectId){
		return projectService.isProjectOfCannotUseCoupon(projectId);
	}
	
	/**
	 * 判断是否为不可使用收益券的项目（目前包括购车分期）
	 * @return
	 */
	public boolean isProjectOfCannotUseProfitCoupon(Long projectId){
		return projectService.isProjectOfCannotUseProfitCoupon(projectId);
	}
	
	/**
	 * 
	 */
	public String getProjectTypeName(String key, String groupName, String defaultLabel,Long projectId){
		String labelName = SysServiceUtils.getDictLabelByKey(key, groupName, defaultLabel);
		if(CAR_PROJECT_NAME.equals(labelName)){
			boolean buyCar = projectService.isBuyCar(projectId);
			if(buyCar){
				return labelName = BUY_CAR_PROJECT_NAME;
			}
		}
		return labelName;
	}
	
	/**
	 * 
	 * @Description:获取特殊项目标识
	 * @param projectId
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月1日 下午3:52:19
	 */
	public Integer getProjectActivitySign(Long projectId) {
		return projectService.getProjectActivitySign(projectId);
	}
	/**
	 * @Description:根据项目类型获取投资中的项目个数
	 * @param projectType
	 * @param investTypeCode
	 * @return
	 * @author: fuyili
	 * @time:2016年2月4日 上午10:53:26
	 */
	public int getInvestProjectCountByProjectType(String projectType,String investTypeCode){
		return projectService.getInvestProjectCountByProjectType(projectType,investTypeCode);
	}
	
	/**
	 * 获取交易列表的收益状态（1、预期收益 30、40、50、51、60、80、90  2.52、70获得收益）
	 */
	public boolean getTransactionListProfitStatus(Long projectId){
		return projectService.getTransactionListProfitStatus(projectId);
	}
	/**
	 * 
	 * @Description:获取用户 借款的项目数
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年3月11日 下午5:59:44
	 */
	public int getBorrowerCount(Long memberId){
		return projectService.countCurrentBorrowerByMemberId(memberId);
	}
	
	public boolean isExistDirectProject(){
		boolean flag = false;
		if(projectService.getDirectProjectCount()>0){
			flag = true;
		}
		return flag ;
	}
	
	public boolean isQuickLottery(Long projectId){
		boolean flag = false;
		ProjectExtra pe=projectService.getProjectActivitySignByProjectId(projectId);
		if(pe!=null&&pe.getActivitySign()!=null){
		if(pe.getActivitySign()!=null&&pe.getActivitySign()==2){
			flag = true;
			}
		}
		return flag ;
	}
	
}
