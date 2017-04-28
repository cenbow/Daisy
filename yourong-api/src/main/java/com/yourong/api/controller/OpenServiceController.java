package com.yourong.api.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.api.service.OpenService;
import com.yourong.common.domain.OpenServiceResultDO;
import com.yourong.core.os.biz.AuthAndSaveProjectBiz;
import com.yourong.core.os.biz.ProjectsPageOutPut;
import com.yourong.core.os.biz.ProjectsPageQuery;
import com.yourong.core.os.biz.ProjectsStatusOutPut;
import com.yourong.core.os.biz.ProjectsStatusQuery;
import com.yourong.core.os.biz.SynStatusBiz;
import com.yourong.core.os.biz.TokenBiz;
import com.yourong.core.os.biz.TokenOutPut;

@Controller
@RequestMapping(value = "openService")
public class OpenServiceController extends BaseServiceController {

	private static final Logger logger = LoggerFactory.getLogger(OpenServiceController.class);

	@Autowired
	private OpenService openService;

	/**
	 * 
	 * @Description:对外创建会员接口
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月18日 下午1:06:44
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/authMemberSaveProject", method = RequestMethod.POST, headers = { "Open-Version=1.0.0" })
	@ResponseBody
	public Object authMemberSaveProject(HttpServletRequest req, HttpServletResponse resp) {
		// 构造返回实体类
		OpenServiceResultDO<AuthAndSaveProjectBiz> resultDO = null;
		try {
			resultDO = new OpenServiceResultDO<AuthAndSaveProjectBiz>(OpenServiceResultDO.DEFULT_SUCCESS, AuthAndSaveProjectBiz.class);
		} catch (Exception e) {
			return new OpenServiceResultDO(OpenServiceResultDO.DEFULT_ERROR);
		}
		// 预处理
		preHandle(req, resultDO);
		if (!resultDO.isSuccess()) {
			return resultDO;
		}
		// 业务处理
		return openService.authMemberSaveProject(resultDO.getResult());
	}

	/**
	 * 
	 * @Description:同步标的状态接口
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月18日 下午1:06:44
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/synProjectStatus", method = RequestMethod.POST, headers = { "Open-Version=1.0.0" })
	@ResponseBody
	public Object synProjectStatus(HttpServletRequest req, HttpServletResponse resp) {
		// 构造返回实体类
		OpenServiceResultDO<SynStatusBiz> resultDO = null;
		try {
			resultDO = new OpenServiceResultDO<SynStatusBiz>(OpenServiceResultDO.DEFULT_SUCCESS, SynStatusBiz.class);
		} catch (Exception e) {
			return new OpenServiceResultDO(OpenServiceResultDO.DEFULT_ERROR);
		}
		// 预处理
		preHandle(req, resultDO);
		if (!resultDO.isSuccess()) {
			return resultDO;
		}
		// 业务处理
		return openService.synProjectStatus(resultDO.getResult());
	}

	@RequestMapping(value = "/getDataQueryToken", method = RequestMethod.GET)
	@ResponseBody
	public TokenOutPut getToken(@ModelAttribute TokenBiz tokenBiz, HttpServletRequest req, HttpServletResponse resp) {
		return openService.getDataQueryToken(tokenBiz);
	}

	/**
	 * 
	 * @Description:获取满标数据
	 * @param projectForWDZJBiz
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2017年3月17日
	 */
	@RequestMapping(value = "/getOverPros", method = RequestMethod.GET)
	@ResponseBody
	public ProjectsPageOutPut getOverPros(@ModelAttribute ProjectsPageQuery projectsPageQuery,
			HttpServletRequest req, HttpServletResponse resp) {
		return openService.getOverPros(projectsPageQuery);
	}

	/**
	 * 
	 * @Description:获取投资中数据
	 * @param projectForWDZJBiz
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2017年3月17日
	 */
	@RequestMapping(value = "/getInvestingPros", method = RequestMethod.GET)
	@ResponseBody
	public ProjectsPageOutPut getInvestingPros(@ModelAttribute ProjectsPageQuery projectsPageQuery,
			HttpServletRequest req, HttpServletResponse resp) {
		return openService.getInvestingPros(projectsPageQuery);
	}

	/**
	 * 
	 * @Description:获取项目状态
	 * @param projectForWDZJBiz
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2017年3月17日
	 */
	@RequestMapping(value = "/getProsStatus", method = RequestMethod.GET)
	@ResponseBody
	public ProjectsStatusOutPut getProsStatus(@ModelAttribute ProjectsStatusQuery projectsStatusQuery,
			HttpServletRequest req, HttpServletResponse resp) {
		return openService.getProsStatus(projectsStatusQuery);
	}
}
