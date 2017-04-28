package com.yourong.backend.sys.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yourong.backend.BaseController;
import com.yourong.backend.sys.service.SysUserService;
import com.yourong.backend.utils.SysServiceUtils;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.util.CryptHelper;
import com.yourong.core.sys.model.SysUser;

@Controller
public class IndexController extends BaseController {
	private static Logger log = LoggerFactory.getLogger(IndexController.class);
	
	@Autowired
	private SysUserService sysUserService;

	
	@RequestMapping("/404")
	public String error404() {
		return "404";
	}
	/**
	 * 前端开发用
	 * @return
	 */
	@RequestMapping("/dev")
	public String dev() {
		return "dev/demo";
	}

	@RequestMapping("/test")
	public String test() {
		return "dev/test";
	}


	@RequestMapping("/500")
	public ModelAndView error500(HttpSession session, HttpServletRequest request,Exception ex) {
		ModelAndView model =new ModelAndView();
		model.addObject("ex", ex);
		model.setViewName("500");
		return model;
	}
	@RequestMapping("/noPermission")
	public ModelAndView errorNoPermission(HttpSession session, HttpServletRequest request,Exception ex) {
		ModelAndView model =new ModelAndView();
		model.addObject("ex", ex);
		model.setViewName("noPermission");
		return model;
	}
	@RequestMapping(value = "/personal", method = RequestMethod.GET)
	public String personal() {
		return "personal";
	}

	/**
	 * 首页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String showIndex() {
		return "index";
	}

	/**
	 * 登录页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		return "login";
	}
	
	/**
	 * 登录
	 * 
	 * @return
	 */
	@RequestMapping(value = "/logined")
	public String logined(HttpSession session, HttpServletRequest request,final RedirectAttributes redirectAttributes) {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		Subject currentUser = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(username,
				CryptHelper.encryptByase(password));
		token.setRememberMe(false);
		String resultPageURL = "redirect:/login";
		try {
			log.debug("对用户[" + username + "]进行登录验证..验证开始");
			currentUser.login(token);
			log.debug("对用户[" + username + "]进行登录验证..验证通过");

			resultPageURL = "redirect:/index";
		} catch (UnknownAccountException uae) {
			log.debug("对用户[" + username + "]进行登录验证..验证未通过,未知账户");
			session.setAttribute("message_login", "未知账户");
		} catch (IncorrectCredentialsException ice) {
			log.debug("对用户[" + username + "]进行登录验证..验证未通过,错误的凭证");
			session.setAttribute("message_login", "密码不正确");
		} catch (LockedAccountException lae) {
			log.debug("对用户[" + username + "]进行登录验证..验证未通过,账户已锁定");
			session.setAttribute("message_login", "账户已锁定");
		} catch (ExcessiveAttemptsException eae) {
			log.debug("对用户[" + username + "]进行登录验证..验证未通过,错误次数过多");
			session.setAttribute("message_login", "用户名或密码错误次数过多");
		} catch (AuthenticationException ae) {
			// 通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景
			log.debug("对用户[" + username + "]进行登录验证..验证未通过,堆栈轨迹如下");			
			session.setAttribute("message_login", "用户名或密码不正确");			
		}
		// 验证是否登录成功
		if (currentUser.isAuthenticated()) {
			log.info("登录系统成功-"+username);
			 Subject subject = SecurityUtils.getSubject();
			 String remoteIP = request.getRemoteAddr();
			// sysUserService.updateLoginIPandDate(record);
			 SysUser user = sysUserService.selectByLoginName(token.getUsername());
			 subject.getSession().setAttribute(Constant.CURRENT_USER, user);
			SysServiceUtils.writeLogger("用户管理","登陆成功");
		} else {
			token.clear();
		}
				
		return resultPageURL;
	}

	/**
	 * 登录
	 * 
	 * @return
	 */
	@RequestMapping(value = "/loginApi", method = RequestMethod.GET)
	@ResponseBody
	public Object loginPost(HttpServletRequest req, HttpServletResponse resp) {
		req.getHeader("Referer");
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		Subject currentUser = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(username, CryptHelper.encryptByase(password));
		token.setRememberMe(false);
		ResultDO rDO = new ResultDO();
		HttpSession session = getHttpSession(req);
		try {
			log.debug("对用户[" + username + "]进行登录验证..验证开始");
			currentUser.login(token);
			log.debug("对用户[" + username + "]进行登录验证..验证通过");
		} catch (UnknownAccountException uae) {
			log.debug("对用户[" + username + "]进行登录验证..验证未通过,未知账户");
			session.setAttribute("message_login", "未知账户");
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		} catch (IncorrectCredentialsException ice) {
			log.debug("对用户[" + username + "]进行登录验证..验证未通过,错误的凭证");
			session.setAttribute("message_login", "密码不正确");
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		} catch (LockedAccountException lae) {
			log.debug("对用户[" + username + "]进行登录验证..验证未通过,账户已锁定");
			session.setAttribute("message_login", "账户已锁定");
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		} catch (ExcessiveAttemptsException eae) {
			log.debug("对用户[" + username + "]进行登录验证..验证未通过,错误次数过多");
			session.setAttribute("message_login", "用户名或密码错误次数过多");
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		} catch (AuthenticationException ae) {
			// 通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景
			log.debug("对用户[" + username + "]进行登录验证..验证未通过,堆栈轨迹如下");
			session.setAttribute("message_login", "用户名或密码不正确");
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		// 验证是否登录成功
		if (currentUser.isAuthenticated()) {
			log.info("登录系统成功-" + username);
			Subject subject = SecurityUtils.getSubject();
			String remoteIP = req.getRemoteAddr();
			// sysUserService.updateLoginIPandDate(record);
			SysUser user = sysUserService.selectByLoginName(token.getUsername());
			subject.getSession().setAttribute(Constant.CURRENT_USER, user);
			SysServiceUtils.writeLogger("用户管理", "登陆成功");
		} else {
			token.clear();
		}

		return rDO;
	}

	@RequestMapping(value = "/loginout")
	public String loginOut(HttpSession session,HttpServletRequest request) {
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			SysServiceUtils.writeLogger("用户管理","退出成功");
			subject.logout(); 			
			if (log.isDebugEnabled()) {
				//log.debug("用户" + username + "退出登录");
			}
		}
		return "redirect:/login";
	}


	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String showHome() {
		return "dev/test";
	}

}
