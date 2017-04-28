package com.yourong.api.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yourong.core.co.model.CompanyManage;
import com.yourong.core.co.model.CompanyProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.yourong.api.service.ArticleService;
import com.yourong.api.service.BalanceService;
import com.yourong.api.service.BannerService;
import com.yourong.api.service.CouponService;
import com.yourong.api.service.MemberService;
import com.yourong.api.service.OrderService;
import com.yourong.api.service.ProjectService;
import com.yourong.api.service.TransactionService;
import com.yourong.api.service.AboutService;
import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sms.SmsMobileSend;
import com.yourong.common.util.StringUtil;
import com.yourong.core.cms.model.Banner;
import com.yourong.core.cms.model.CmsArticle;
import com.yourong.core.ic.model.biz.ProjectForFront;
import com.yourong.core.ic.model.query.ProjectQuery;

/**
 * m 站首页
 */
@Controller
public class MStationController extends BaseController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private BannerService bannerService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AboutService aboutService;

    protected static final String INVITECODE = "inviteCode_shortURL";

    @Autowired
    private MemberService memberService;
    @Autowired
    private SmsMobileSend smsMobileSend;

    private static final String REGISTER_MOBILE = "Register_mobile";


    /**
     * 移动端首页
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "/mIndex")
    public ModelAndView showMobile(HttpServletRequest req, HttpServletResponse resp) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("mobile/index");
        // 加载推荐项目列表
        ResultDO<ProjectForFront> result = projectService.findIndexProjectList();
        modelAndView.addObject("projectList", result.getResultList());
        // 加载网站公告
        Map<String, Object> map = Maps.newHashMap();
        map.put("categoryId", 1);// 公告
        List<CmsArticle> articles = articleService.selectHomeNotice(map);
        modelAndView.addObject("articles", articles);

        // 新手项目
        ProjectForFront noviceProject = projectService.getIndexNoviceProject();
        modelAndView.addObject("noviceProject", noviceProject);
        // 加载banner
        List<Banner> banners = bannerService.findMOnlineBanner();// banner
        modelAndView.addObject("banners", banners);

        return modelAndView;
    }

    /**
     * 移动端首页，带邀请码
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "/{inviteCod:(?!member|notify|robots|coupon)\\w{6}}")
	public ModelAndView mIndex(@PathVariable String inviteCod,HttpServletRequest req,
                        HttpServletResponse resp) {
    	if(StringUtil.isNotBlank(inviteCod) && RedisMemberClient.isExistMemberInviteCode(inviteCod)){
    		req.getSession().setAttribute(INVITECODE, inviteCod);
    	}
    	return showMobile(req, resp);
    }

    /**
     * 移动端默认首页
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "/")
   	public ModelAndView defaultIndex(HttpServletRequest req, HttpServletResponse resp) {
       	return showMobile(req, resp);
    }

    /**
     * 通用提示
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/mstation/tips")
    public ModelAndView commonTips(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/commonTips");
        return model;
    }


    /************************YourongMobile***************************/
    /**
     * 项目列表
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "products/m/list-{projectType}-{statusCode}-{currentPage}.html")
    public String findProjectListMobile(@ModelAttribute("projectQuery") ProjectQuery query, HttpServletRequest req, HttpServletResponse resp) {
        searchPre(query);
     /*   Page<ProjectForFront> projectList = projectService.findProjectListByPage(query);
        req.setAttribute("projectList", projectList);*/
        if (StringUtil.isBlank(query.getProjectType())) {
            query.setProjectType("all");
        }
        req.setAttribute("query", query);
        return "/mobile/products/list";
    }
    /**
     * 项目详情
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "products/detail-{pid}.html")
    public ModelAndView detail(@PathVariable String pid, HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/products/detail");
        model.addObject("pid", pid);
        return model;
    }
    /**
     * 搜索的前置条件
     *
     * @param query
     */
    private void searchPre(ProjectQuery query) {
        String statusCode = query.getStatusCode();
        String guarantyType = query.getProjectType();
        //加这些判断主要是防止“错误数据”，以及前台Tab项的切换
        if (statusCode != null) {
            if (!statusCode.equals("investing") && !statusCode.equals("performance") && !statusCode.equals("repayment")) {
                query.setStatus(null);
                query.setStatusCode("all");
            }
        }
        if (guarantyType != null) {
            if (!guarantyType.equals("car") && !guarantyType.equals("house") && !guarantyType.equals("newCar")) {
                query.setProjectType(null);
            }
        }
        query.setPageSize(5);
		query.setQuerySource("mStation");
    }

    /**
     * 下载着陆页
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/landing/downLoading")
    public ModelAndView downLoading(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/landing/download_B");
        return model;
    }

    /**
     * 推广着陆页C
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/landing/registerC")
    public ModelAndView registerC(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/landing/registerC");
        return model;
    }

    /**
     * 推广着陆页D
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/landing/registerD")
    public ModelAndView registerD(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/landing/registerD");
        return model;
    }

    /**
     * 注册着陆页G
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/landing/registerG")
    public ModelAndView registerG(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/landing/registerG");
        return model;
    }

    /**
     * 注册着陆页
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/landing/registerH")
    public ModelAndView registerH(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/landing/registerH");
        return model;
    }
    /**
     * 下载着陆页E
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/landing/downloadE")
    public ModelAndView downloadE(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/landing/downloadE");
        return model;
    }
    /**
     * 下载着陆页F
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/landing/downloadF")
    public ModelAndView downloadF(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/landing/downloadF");
        return model;
    }

    /**
     * 下载着陆页G
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/landing/downloadG")
    public ModelAndView downloadG(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/landing/downloadG");
        return model;
    }
    /**
     * 邀新有奖着陆页
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/landing/rewardForNew")
    public ModelAndView rewardForNew(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/landing/rewardForNew");
        return model;
    }

    /**
     * 关于有融
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("mstation/aboutYR")
    public ModelAndView aboutYR(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        Integer source = getRequestSource(req);
        model.setViewName("/mobile/member/aboutYR");
        model.addObject("source", source);
        return model;
    }
    /**
     * 安全保障
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("mstation/safetyAssurance")
    public ModelAndView safetyAssurance(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/safetyAssurance");
        return model;
    }
    /**
     *
     * @Description:测评页面
     * @param req
     * @param resp
     * @return
     * @author: chaisen
     * @time:2016年6月15日 下午1:52:13
     */
    @RequestMapping("/questionNaire")
    public ModelAndView questionnaire(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/member/questionnaire");
        return model;
    }
    /**
     *
     * @Description:上上签专题页移动端
     * @param req
     * @param resp
     * @return
     * @time:2016年7月25日 下午11:14:13
     */
    @RequestMapping("/post/signUpgrade")
    public ModelAndView signUpgrade(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/post/signUpgrade");
        return model;
    }
    /**
     *
     * @Description:2016上半年运营报告
     * @param req
     * @param resp
     * @return
     * @time:2016年9月9日 下午11:14:13
     */
    @RequestMapping("/post/operateReport2016")
    public ModelAndView operateReport2016(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/post/operateReport2016");
        return model;
    }
    /**
     *
     * @Description:转让专题页移动端
     * @param req
     * @param resp
     * @return
     * @time:2016年9月24日 下午1:54:13
     */
    @RequestMapping("/post/transferMarket")
    public ModelAndView transferMarket(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/post/transferMarket");
        return model;
    }
    /**
     *
     * @Description:人气值乐园专题页移动端
     * @param req
     * @param resp
     * @return
     * @time:2016年10月31日 下午1:54:13
     */
    @RequestMapping("/post/popularityPark")
    public ModelAndView popularityPark(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/post/popularityPark");
        return model;
    }

    /**
     * @param req
     * @param resp
     * @return
     * @Description:快投有奖介绍页面移动端
     * @time:2016年11月09日 下午2:00:00
     */
    @RequestMapping("/post/directActivatorTips")
    public ModelAndView directActivatorTips(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/post/directActivatorTips");
        return model;
    }

    /**
     * 推广着陆页
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/landing/sixgiftA")
    public ModelAndView sixgiftA(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/landing/sixgiftA");
        return model;
    }


    /**
     * 推广着陆页
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/landing/sixgiftB")
    public ModelAndView sixgiftB(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/landing/sixgiftB");
        return model;
    }

    /**
     * 推广着陆页
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/landing/sixgift568")
    public ModelAndView sixgift568(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/landing/sixgift568");
        return model;
    }

    /**
     * 推广着陆页
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/landing/sixgiftC")
    public ModelAndView sixgiftC(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/landing/sixgiftC");
        return model;
    }

    /**
     * 推广着陆页
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/landing/exceed50")
    public ModelAndView exceed50(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/landing/exceed50");
        return model;
    }

    /**
     * 2017第一季度运营报告
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/operate2017/firstQuarter")
    public ModelAndView firstQuarter(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/post/operate2017/firstQuarter");
        return model;
    }

    /**
     * 有融成长接口请求
     * @param req
     * @param resp
     * @return
     */
    @ResponseBody
    @RequestMapping("/about/event/ajax")
    public ResultDO<List<CompanyProfile>> eventAjax(
            HttpServletRequest req,
            HttpServletResponse resp
    ) {
        ResultDO<List<CompanyProfile>> resultDO = aboutService.queryCompanyProfile();
        return resultDO;
    }

    /**
     * 有融公司管理层接口请求
     * @param req
     * @param resp
     * @return
     */
    @ResponseBody
    @RequestMapping("/about/manage/ajax")
    public ResultDO<List<CompanyManage>> manageAjax(
            HttpServletRequest req,
            HttpServletResponse resp
    ) {
        ResultDO<List<CompanyManage>> resultDO = aboutService.queryCompanyManage();
        return resultDO;
    }

    /**
     * 合作伙伴
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/mstation/cooperator")
    public ModelAndView cooperator(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/cooperator");
        return model;
    }

    /**
     * 资质荣誉
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/mstation/honor")
    public ModelAndView honor(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/honor");
        return model;
    }


}
