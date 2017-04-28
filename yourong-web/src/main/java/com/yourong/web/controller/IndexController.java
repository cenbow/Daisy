package com.yourong.web.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.Exceptions;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.cache.RedisForProjectClient;
import com.yourong.core.cms.model.Banner;
import com.yourong.core.cms.model.CmsArticle;
import com.yourong.core.ic.model.ProjectPackage;
import com.yourong.core.ic.model.biz.ProjectForFront;
import com.yourong.web.dto.FinEaringDto;
import com.yourong.web.service.ArticleService;
import com.yourong.web.service.BannerService;
import com.yourong.web.service.MemberService;
import com.yourong.web.service.ProjectService;
import com.yourong.web.utils.ServletUtil;

@Controller
public class IndexController extends BaseController {
	@Autowired
	private ProjectService projectService;

	@Autowired
	private ArticleService articleService;

	@Autowired
	private BannerService bannerService;

	@Autowired
	private MemberService memberService;

	@RequestMapping(value = "/getVersion", method = RequestMethod.GET)
	@ResponseBody
	public Object getVersion(HttpSession session, HttpServletRequest request, Exception exception) {
		String versionInfo = PropertiesUtil.getStaticResourceVersion();
		if (StringUtil.isNumeric(versionInfo)) {
			return DateUtils.getStrFromDate(new Date(Long.valueOf(versionInfo)), DateUtils.TIME_PATTERN);
		}
		return versionInfo;
	}

	@RequestMapping("/404")
	public String error404() {
		return "/404";
	}

	@RequestMapping("/405")
	public String error405() {
		return "/error";
	}

	@RequestMapping("/500")
	public ModelAndView error500(HttpSession session,
			HttpServletRequest request, Exception exception) {
		ModelAndView model = new ModelAndView();
		this.logger.error("页面异常", exception);
		model.addObject("exception", Exceptions.getStackTraceAsString(exception));
		model.setViewName("/500");
		return model;
	}

	/**
	 * 首页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{inviteCod:(?!member|notify|robots|coupon)\\w{6}}")
	public ModelAndView index(@PathVariable String inviteCod,
			HttpServletRequest req, HttpServletResponse resp) {

		ModelAndView modelAndView = null;
		if (StringUtil.isNotBlank(inviteCod)
				&& RedisMemberClient.isExistMemberInviteCode(inviteCod)) {
			HttpSession session = getHttpSession(req);
			session.setAttribute(INVITECODE, inviteCod);
			if (ServletUtil.isMobile(req)) {
				String mUrl = PropertiesUtil.getMstationRootUrl();
				modelAndView = new ModelAndView();
				modelAndView.setViewName("redirect:" + mUrl + "/" + inviteCod);
				// 跳转M站
				return modelAndView;
			} else {
				// 跳转pc站
				modelAndView = showIndexProject("index");

			}
		} else {
			modelAndView = new ModelAndView();
			modelAndView.setViewName("/404");
		}
		return modelAndView;

	}

	/**
	 * 首页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/index")
	public ModelAndView index(HttpServletRequest req, HttpServletResponse resp) {
		boolean mobile = ServletUtil.isMobile(req);
		if (mobile) {
			ModelAndView modelAndView = new ModelAndView();
			String mUrl = PropertiesUtil.getMstationRootUrl();
			modelAndView.setViewName("redirect:" + mUrl);
			return modelAndView;
		}
		ModelAndView modelAndView = showIndexProject("index");
		return modelAndView;
	}

	@RequestMapping(value = "/")
	public ModelAndView show(HttpServletRequest req, HttpServletResponse resp) {
		String flag = ServletRequestUtils.getStringParameter(req, "fromMobile", "N");
		if ("Y".equals(flag)) {
			req.getSession().setAttribute(Constant.PC_INDEX_FROM_MOBIE, "Y");
		}
		boolean mobile = ServletUtil.isMobile(req);
		if (mobile && "N".equals(flag)) {
			ModelAndView modelAndView = new ModelAndView();
			String mUrl = PropertiesUtil.getMstationRootUrl();
			modelAndView.setViewName("redirect:" + mUrl);
			return modelAndView;
		}
		ModelAndView modelAndView = showIndexProject("index");
		return modelAndView;
	}

	// @RequestMapping(value = "/security/getToken")
	// @ResponseBody
	// public String getToken(HttpServletRequest req,
	// HttpServletResponse resp) {
	// return TokenHelper.generateRequestAndAddToken(req);
	// }

	/**
	 * 页面内容加载
	 * 
	 * @param viewName
	 *            路由
	 * @return
	 */
	private ModelAndView showIndexProject(String viewName) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(viewName);
		//加载资产包项目
		ResultDO<ProjectPackage> packageResult = projectService.getProjectPackageIndex();
		// 加载推荐项目列表
		int default_show_number = Constant.INDEX_DEFAULT_NUMBER;
		if(packageResult.getResult()!=null){
			default_show_number= Constant.INDEX_DEFAULT_NUMBER_NEW;
		}
		ResultDO<ProjectForFront> result = projectService
				.findIndexProjectList(default_show_number);
		//加载债权推荐项目列表
		ResultDO<ProjectForFront> debtResult = projectService
				.findIndexDebtProjectList();
		//加载直投推荐项目
		ResultDO<ProjectForFront> directResult = projectService
				.findIndexDirectProjectList();
		
		//加载转让推荐项目
		ResultDO<ProjectForFront> transferResult = projectService
				.findTransferProjectList();
		modelAndView.addObject("packageEntity", packageResult.getResult());
		modelAndView.addObject("projectList", result.getResultList());
		modelAndView.addObject("debtProjectList", debtResult.getResultList());
		modelAndView.addObject("directProjectList", directResult.getResultList());
		modelAndView.addObject("transferProjectList", transferResult.getResultList());
		
		// 加载网站公告
		Map<String, Object> map = Maps.newHashMap();
		map.put("categoryId", 1);// 公告
		List<CmsArticle> articles = articleService.selectHomeNotice(map);
		modelAndView.addObject("articles", articles);

		// 新手项目
		// ProjectForFront noviceProject =
		// projectService.getIndexNoviceProject();
		// modelAndView.addObject("noviceProject", noviceProject);

		String areaSign = "home";
		List<Banner> banners = bannerService.findPcOnlineBanner(areaSign);// banner
		modelAndView.addObject("banners", banners);

		// 加载七日年化收益
		List<FinEaringDto> finEarings = memberService.getfin();
		if (Collections3.isNotEmpty(finEarings)) {
			FinEaringDto finEaring = finEarings.get(0);
			modelAndView.addObject("finEaring", finEaring);
		}
		// try {
		// String nodejsKey =
		// CryptCode.encryptToMD5(DateUtils.getCurrentDate().getTime() +
		// WebPropertiesUtil.getMD5Key());
		// modelAndView.addObject("nodejsKey",nodejsKey);
		// }catch (Exception e){
		// }
		return modelAndView;
	}

	/**
	 * SEO 短链接
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/seoyer1")
	public String seoyer1(HttpServletRequest req, HttpServletResponse resp) {
		return "redirect:/?trackid=SEO_SY_rwpt";
	}

	/**
	 * SEO 短链接
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/seoyer2")
	public String seoyer2(HttpServletRequest req, HttpServletResponse resp) {
		return "redirect:/landing/seasonFour/register?trackid=SEO_Z50_rwpt";
	}

	/**
	 * SEO 短链接
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/seoyer3")
	public String seoyer3(HttpServletRequest req, HttpServletResponse resp) {
		return "redirect:/landing/seasonFour/register?trackid=SEO_SY_Z50";
	}

	/**
	 * SEO 短链接
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/seoyer4")
	public String seoyer4(HttpServletRequest req, HttpServletResponse resp) {
		return "redirect:/activity/investHouse?trackid=SEO_tfyx";
	}

	/**
	 * SEO 短链接
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/seoyer5")
	public String seoyer5(HttpServletRequest req, HttpServletResponse resp) {
		return "redirect:/activity/birthdayGift/?trackid=SEO_birthday";
	}

	/**
	 * SEO 短链接
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/seoyer6")
	public String seoyer6(HttpServletRequest req, HttpServletResponse resp) {
		return "redirect:/?trackid=SEO_SY";
	}

	/**
	 * SEO 短链接
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/seoyer7")
	public String seoyer7(HttpServletRequest req, HttpServletResponse resp) {
		return "redirect:/activity/quadrupleGift?trackid=SEO_qb4cl";
	}

	@RequestMapping(value = "/getTransactionHistory", method = RequestMethod.GET)
	@ResponseBody
	public Set<String> getTransactionHistory(HttpServletRequest req,
			HttpServletResponse resp) {
		Set<String> forIndex = RedisForProjectClient
				.getTransactionDetailForIndex(0, 10);
		return forIndex;
	}

}
