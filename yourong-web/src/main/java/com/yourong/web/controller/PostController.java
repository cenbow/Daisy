package com.yourong.web.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.biz.ProjectForDirectLottery;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.model.biz.TraceSourceCollectBiz;
import com.yourong.core.tc.model.query.TraceSourceCollectQuery;
import com.yourong.web.service.SysDictService;
import com.yourong.web.service.TransactionService;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.yourong.web.dto.MemberSessionDto;
import com.yourong.web.dto.NoviceTaskDto;
import com.yourong.web.service.MemberService;
import com.yourong.web.service.ProjectService;

/**
 * 关于
 * @author Administrator
 *
 */
@Controller
@RequestMapping("post")
public class PostController extends BaseController{

	@Autowired
	private MemberService memberService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private SysDictService sysDictService;
	
	@Autowired
	private ProjectManager projectManager;
	/**
	 * 安全保障
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/subject/safety.html")
	public ModelAndView securityPage(
			HttpServletRequest req, 
			HttpServletResponse resp
			) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/post/securityPage");
		return model;
	}

	
	/**
	 * 合作伙伴
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/subject/partner.html")
	public ModelAndView partnerPage(
			HttpServletRequest req, 
			HttpServletResponse resp
			) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/post/partner");
		return model;
	}
	
	/**
	 * 帮助中心-常见问题
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/help/question.html")
	public ModelAndView questionPage(
			HttpServletRequest req, 
			HttpServletResponse resp
			) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/post/question");
		return model;
	}	
	
	/**
	 * 帮助中心-联系客服
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/help/contact.html")
	public ModelAndView contactPage(
			HttpServletRequest req, 
			HttpServletResponse resp
			) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/post/contact");
		return model;
	}	
	/**
	 * 名词解释
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/help/wiki.html")
	public ModelAndView wikiPage(
			HttpServletRequest req, 
			HttpServletResponse resp
			) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/post/wiki");
		return model;
	}	
	/**
	 * 新手指引
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/help/guide.html")
	public ModelAndView guidePage(
			HttpServletRequest req, 
			HttpServletResponse resp
			) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/post/guide");
		return model;
	}	
	
	   
 
	/**
	 * 新闻详情
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/article/news/detail")
	public ModelAndView newsDetail(
			HttpServletRequest req, 
			HttpServletResponse resp
			) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/post/newsDetail");
		return model;
	}	
	
	/**
	 * 友情链接
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/friendlyLink")
	public ModelAndView friendlyLink(
			HttpServletRequest req, 
			HttpServletResponse resp
			) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/post/friendlyLink");
		return model;
	}


	/**
	 * 专题页
	 *
	 * @return
	 */
	@RequestMapping(value = "/topics/{landingName:^[0-9a-zA-Z_]+$}")
	public String posting(@PathVariable("landingName")String landingName) {
		return "/post/topics/"+landingName;
	}
	
	/**
	 * 新手六重礼
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/newSixGift/index")
	public ModelAndView newSixGift(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		MemberSessionDto user = getMember();
		if (user != null) {
			List<NoviceTaskDto> noviceTaskList = memberService.getNoviceTaskList(user.getId());
			model.addObject("noviceTaskList", noviceTaskList);
		}
		model.setViewName("/post/newSixGift");
		return model;
	}

	/**
	 * 
	 * @Description:新手六重礼初始化
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2016年2月26日 下午1:36:42
	 */
	@RequestMapping(value = "/newSixGift/init", method = RequestMethod.POST)
	@ResponseBody
	public Object breakBillionInit(HttpServletRequest req, HttpServletResponse resp) {
		return projectService.firstInvestProject();
	}

	/**
	 * 直投项目专题页
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/aboutP2P")
	public ModelAndView aboutP2P(
			HttpServletRequest req,
			HttpServletResponse resp
	) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/post/aboutP2P");
		return model;
	}


	/**
	 * 新安全保障v3改版
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/subject/securityGuarantee")
	public ModelAndView securityGuarantee(
			HttpServletRequest req,
			HttpServletResponse resp
	) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/post/securityGuarantee");
		return model;
	}

	/**
	 * 风险评估
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/questionnaire")
	public ModelAndView questionnaire(
			HttpServletRequest req,
			HttpServletResponse resp
	) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/post/questionnaire");
		return model;
	}
	/**
	 * 上上签专题页
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/signUpgrade")
	public ModelAndView signUpgrade(
			HttpServletRequest req,
			HttpServletResponse resp
	) {
		ModelAndView model = new ModelAndView();
		model.setViewName("post/signUpgrade");
		return model;
	}
	/**
	 * 转让专题页
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/transferMarket")
	public ModelAndView transferMarket(
			HttpServletRequest req,
			HttpServletResponse resp
	) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/post/transferMarket");
		return model;
	}

	/**
	 * 支付升级专题页
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/payUpgrade")
	public ModelAndView payUpgrade(
			HttpServletRequest req,
			HttpServletResponse resp
	) {
		ModelAndView model = new ModelAndView();
		model.setViewName("/post/payUpgrade");
		return model;
	}
	/**
	 * 自动投标专题页
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/autoInvestSubject")
	public ModelAndView autoInvestSubject(
			HttpServletRequest req,
			HttpServletResponse resp
	) {
		ModelAndView model = new ModelAndView();
		model.setViewName("post/autoInvestSubject");
		return model;
	}
	/**
	 * 2016上半年运营报告
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/operateReport2016")
	public ModelAndView operateReport2016(
			HttpServletRequest req,
			HttpServletResponse resp
	) {
		ModelAndView model = new ModelAndView();
		model.setViewName("post/operateReport2016");
		return model;
	}

	/**
	 * 2016下半年运营报告
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/reportLatter2016")
	public ModelAndView reportLatter2016(
			HttpServletRequest req,
			HttpServletResponse resp
	) {
		ModelAndView model = new ModelAndView();
		model.setViewName("post/reportLatter2016");
		return model;
	}

	@RequestMapping(value = "/channel")
	public String channelcollect(HttpServletRequest request, Model model, String mobile, String startdate,String enddate,@RequestParam(value = "currpage",defaultValue = "1") Integer currentPage){
		TraceSourceCollectQuery query=new TraceSourceCollectQuery();
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
		if (!StringUtils.isEmpty(startdate)){
			try {
				Date sdate = simpleDateFormat.parse(startdate);
				query.setStartdate(sdate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (!StringUtils.isEmpty(enddate)){
			try {
				Date edate = simpleDateFormat.parse(enddate);
				query.setEnddate(edate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		query.setMobile(mobile);
		query.setPageSize(50);
		query.setCurrentPage(currentPage);
		Page<TraceSourceCollectBiz> page=new Page<>();
		String channelname=(String) request.getSession().getAttribute("CHANNELNAME");
		if (StringUtils.isEmpty(channelname)){
			return "redirect:/post/channellogin";
		}
		SysDict sysDict=new SysDict();
		try {
			sysDict=sysDictService.findByGroupNameAndKey("channel_group",channelname);
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		String trackstr= sysDict.getRemarks();
		String[] tracks=trackstr.split(",");
		List<String> tks=new ArrayList<>();
		for (String t:tracks) {
			tks.add(t);
		}
		query.setChanneltrack(tks);
		query.setChannelname(sysDict.getDescription());
		page= transactionService.queryByPageCollectByTraceSource(query);
		model.addAttribute("page",page);
		model.addAttribute("query",query);
		return "post/channelcollect";
	}

	@RequestMapping(value = "/channellogin")
	public String channellogin(Model model, @RequestParam(required = false,defaultValue = "0") String error){
		if (error.equals("1")){
			model.addAttribute("error","输入渠道和密匙");
		}
		if (error.equals("2")){
			model.addAttribute("error","渠道和密匙错误");
		}
		return "post/channellogin";
	}

	@RequestMapping(value = "/channellogining")
	public String channellogining(HttpServletRequest request, Model model, @RequestParam(value = "channelname") String channelname, @RequestParam(value = "password",required = false)String pwd){
		if (StringUtils.isEmpty(pwd)||StringUtils.isEmpty(channelname)){
			model.addAttribute("error",1);  //输入渠道和密匙
			return "redirect:/post/channellogin";
		}
		SysDict sysDict=new SysDict();
		try {
			sysDict=sysDictService.findByGroupNameAndKey("channel_group",channelname);
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		if (sysDict!=null&&pwd.equals(sysDict.getValue())){
			request.getSession().setAttribute("CHANNELNAME",channelname);
			return "redirect:/post/channel";
		}
		model.addAttribute("error",2);   //渠道和密匙错误
		return "redirect:/post/channellogin";
	}

	/**
	 * 2016数据中心
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/dataCenter")
	public ModelAndView dataCenter(
			HttpServletRequest req,
			HttpServletResponse resp
	) {
		ModelAndView model = new ModelAndView();
		model.setViewName("post/dataCenter");
		return model;
	}

	/**
	 * 周年庆专题页
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/popularityPark")
	public ModelAndView popularityPark(
			HttpServletRequest req,
			HttpServletResponse resp
	) {
		ModelAndView model = new ModelAndView();
		model.setViewName("post/popularityPark");
		return model;
	}
	/**
	 *
	 * @Description:快投有奖
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年11月2日 下午3:59:15
	 */
	@RequestMapping("/directActivator")
	public ModelAndView quickLotterySpecial(HttpServletRequest req,HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		model.setViewName("post/directActivator");
		return model;
	}
	/**
	 *
	 * @Description:快投抽奖专题页
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年11月2日 下午4:04:19
	 */
	@RequestMapping(value = "/quickLottery/init", method = RequestMethod.POST)
	@ResponseBody
	public Object quickLotteryInit(HttpServletRequest req, HttpServletResponse resp) {
		MemberSessionDto user = getMember();
		return projectService.quickLotteryProject(user);
	}

    /**
     * 2017第一季度运营报告
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/operate2017/firstQuarter")
    public ModelAndView operate2017First(
            HttpServletRequest req,
            HttpServletResponse resp
    ) {
        ModelAndView model = new ModelAndView();
        model.setViewName("post/operate2017First");
        return model;
    }

}
