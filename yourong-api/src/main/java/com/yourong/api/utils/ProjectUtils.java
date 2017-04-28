package com.yourong.api.utils;

import com.yourong.api.service.MemberService;
import com.yourong.api.service.ProjectService;
import com.yourong.common.util.SpringContextHolder;
import com.yourong.core.uc.model.Member;

/**
 * 项目util
 * 
 * @author fuyili 2015年4月16日上午10:24:03
 */
public class ProjectUtils {
	private static ProjectService projectService = SpringContextHolder.getBean(ProjectService.class);

	private static MemberService memberService = SpringContextHolder.getBean(MemberService.class);

	/**
	 * 判断是否有投资中的项目
	 */
//	public boolean isInvestingProject() {
//		int projectCount = projectService.queryInvestingProjectCount();
//		if (projectCount > 0) {
//			return true;
//		}
//		return false;
//	}
	
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
	
	public boolean isMember(Long mobile) {
		Member m = memberService.selectByMobile(mobile);
		if (m == null) {
			return false;
		}
		return true;
	}
}
