package com.yourong.api.service;

import com.yourong.common.domain.OpenServiceResultDO;
import com.yourong.core.os.biz.AuthAndSaveProjectBiz;
import com.yourong.core.os.biz.OpenServiceOutPut;
import com.yourong.core.os.biz.ProjectsPageOutPut;
import com.yourong.core.os.biz.ProjectsPageQuery;
import com.yourong.core.os.biz.ProjectsStatusOutPut;
import com.yourong.core.os.biz.ProjectsStatusQuery;
import com.yourong.core.os.biz.SynStatusBiz;
import com.yourong.core.os.biz.SynStatusOutPut;
import com.yourong.core.os.biz.TokenBiz;
import com.yourong.core.os.biz.TokenOutPut;

public interface OpenService {

	/**
	 * 
	 * @Description:认证会员并创建项目
	 * @param input
	 * @return
	 * @author: wangyanji
	 * @time:2016年11月8日 上午10:57:00
	 */
	public OpenServiceResultDO<OpenServiceOutPut> authMemberSaveProject(AuthAndSaveProjectBiz input);

	/**
	 * 
	 * @Description:根据外部业务编号获取标的状态
	 * @param outBizNo
	 * @author: wangyanji
	 * @time:2016年11月10日 下午6:10:28
	 */
	public OpenServiceResultDO<SynStatusOutPut> synProjectStatus(SynStatusBiz synStatusBiz);

	/**
	 * 
	 * @Description:获取数据对接token
	 * @param tokenBiz
	 * @return
	 * @author: wangyanji
	 * @time:2017年3月15日
	 */
	public TokenOutPut getDataQueryToken(TokenBiz tokenBiz);

	/**
	 * 
	 * @Description:查询满标项目列表
	 * @param projectForWDZJBiz
	 * @return
	 * @author: wangyanji
	 * @time:2017年3月16日
	 */
	public ProjectsPageOutPut getOverPros(ProjectsPageQuery projectsPageQuery);

	/**
	 * 
	 * @Description:查询投资中项目列表
	 * @param projectForWDZJBiz
	 * @return
	 * @author: wangyanji
	 * @time:2017年3月16日
	 */
	public ProjectsPageOutPut getInvestingPros(ProjectsPageQuery projectsPageQuery);

	/**
	 * 
	 * @Description:查询项目状态
	 * @param projectsStatusQuery
	 * @return
	 * @author: wangyanji
	 * @time:2017年3月17日
	 */
	public ProjectsStatusOutPut getProsStatus(ProjectsStatusQuery projectsStatusQuery);
}
