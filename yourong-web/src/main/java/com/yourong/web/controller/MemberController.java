package com.yourong.web.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.constant.Constant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.member.common.Constants;
import com.yourong.common.thirdparty.sms.SmsMobileSend;
import com.yourong.common.util.CryptHelper;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.RegexUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.core.fin.model.biz.CapitalInOutForMemberCenter;
import com.yourong.core.fin.model.query.CapitalQuery;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.engine.SPEngine;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.uc.model.AutoInvestSet;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberCheck;
import com.yourong.core.uc.model.MemberInfo;
import com.yourong.core.uc.model.MemberNotifySettings;
import com.yourong.core.upload.model.ImageConfig;
import com.yourong.core.upload.util.UploadUtil;
import com.yourong.web.cache.MyCacheManager;
import com.yourong.web.dto.AutoInvestSetDto;
import com.yourong.web.dto.MemberDto;
import com.yourong.web.dto.MemberForm;
import com.yourong.web.dto.MemberInfoDto;
import com.yourong.web.dto.MemberNotifySettingsDto;
import com.yourong.web.dto.MemberSessionDto;
import com.yourong.web.service.BalanceService;
import com.yourong.web.service.CapitalInOutLogService;
import com.yourong.web.service.CouponService;
import com.yourong.web.service.MemberBankCardService;
import com.yourong.web.service.MemberInfoService;
import com.yourong.web.service.MemberNotifySettingsService;
import com.yourong.web.service.MemberService;
import com.yourong.web.service.OrderService;
import com.yourong.web.service.ProjectService;
import com.yourong.web.service.TransactionService;
import com.yourong.web.utils.ServletUtil;

@Controller
@RequestMapping("member")
public class MemberController extends BaseController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MyCacheManager myCacheManager;

    @Autowired
    private MemberNotifySettingsService memberNotifySettingsService;

    @Autowired
    private MemberBankCardService memberBankCardService;

    @Autowired
    private Map<String, List<ImageConfig>> imagesConfig;

    @Autowired
    private MemberInfoService memberInfoService;

    @Autowired
    private CouponService couponService;
    
    @Autowired
    private BalanceService balanceService;
    
    @Autowired
    private TransactionService transactionService;
    @Autowired
	private OrderService orderService;
    
    @Autowired
    private CapitalInOutLogService capitalInOutLogService;
    
    @Autowired
    private ProjectService projectService;

    @Autowired
	private SmsMobileSend smsMobileSend;

    /**
     * 第三方支付开通 打开页面
     *
     * @return
     */
    @RequestMapping("sinapay")
    public ModelAndView registerSinapay(HttpServletRequest req) {
        ModelAndView model = new ModelAndView();
        model.addObject("id", getMember().getId());
        model.setViewName("member/login/register-sinapay");
        return model;
    }

    /**
     * 第三方支付开通 提交表单
     *
     * @param memberDto
     * @param result
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "authIdentity",method = RequestMethod.POST)
    @ResponseBody
    public ResultDO<Object> authIdentity(
            @ModelAttribute("form") MemberDto memberDto, BindingResult result,
            HttpServletRequest req, HttpServletResponse resp) {
        memberDto.setMobile(getMember().getMobile());
        memberDto.setIp(getIp(req));
        ResultDO<Object> resultDO = new ResultDO<Object>();
        try {
            resultDO = memberService.authIdentity(memberDto);
        } catch (Exception e) {
            logger.error("第三方支付开通异常", e);
            resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
            return resultDO;
        }
        if (resultDO.isSuccess()) {
        	SPParameter  parameter =new SPParameter();
			parameter.setMemberId(memberDto.getId());
			SPEngine.getSPEngineInstance().run(parameter);
            if (getMember(req) != null) {
            	Date birthday = DateUtils.getBirthdayByIdentity(memberDto.getIdentityNumber());
                MemberSessionDto memberSessionDto = getMember(req);
                memberSessionDto.setIdentityNumber(memberDto.getIdentityNumber());
                memberSessionDto.setTrueName(memberDto.getTrueName());
                memberSessionDto.setBirthday(birthday);
                updateMemberSessionDto(req, memberSessionDto);
            }
        }
        return resultDO;
    }
    /**
     *开通新浪支付存钱罐wap
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/landing/mSinapay")
    public ModelAndView mRegisterSinapay(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.addObject("id", getMember().getId());
        model.setViewName("/landing/mRegister-sinapay");
        return model;
    }
    /**
     *注册后成功页wap
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/landing/mRegisterSuccess")
    public ModelAndView mRegisterSuccess(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView modelAndView = new ModelAndView();
        if (ServletUtil.isVerifyTrueName()) {
            modelAndView.setViewName("landing/mRegister-reward");
            Coupon coupon = couponService.getCouponByMemberIdAndActivity(getMember().getId(), 1L);
            modelAndView.addObject("coupon", coupon);
        } else {
            modelAndView.setViewName("forward:/landing/mRegister-sinapay");
        }
        return modelAndView;
    }
    /**
     *开通新浪支付存钱罐wap(第二个)
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/landing/mSinapayA")
    public ModelAndView mRegisterSinapay_A(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.addObject("id", getMember().getId());
        model.setViewName("/landing/mRegister-sinapay_A");
        return model;
    }
    /**
     *注册后成功页wap（第二个）
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/landing/mRegisterSuccessA")
    public ModelAndView mRegisterSuccess_A(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView modelAndView = new ModelAndView();
        if (ServletUtil.isVerifyTrueName()) {
            modelAndView.setViewName("landing/mRegister-reward_A");
            Coupon coupon = couponService.getCouponByMemberIdAndActivity(getMember().getId(), 1L);
            modelAndView.addObject("coupon", coupon);
        } else {
            modelAndView.setViewName("forward:/landing/mRegister-sinapay_A");
        }
        return modelAndView;
    }


    /**
     * 注册成功显示页面
     *
     * @return
     */
    @RequestMapping("registerSucess")
    public ModelAndView registerReward() {
        ModelAndView modelAndView = new ModelAndView();
//        if (ServletUtil.isVerifyTrueName()) {
            modelAndView.setViewName("member/login/register-reward");
            Coupon coupon = couponService.getCouponByMemberIdAndActivity(getMember().getId(), 1L);
            modelAndView.addObject("coupon", coupon);
//        } else {
//            modelAndView.setViewName("forward:/member/sinapay");
//        }
        return modelAndView;
    }

    //会员中心
    @RequestMapping("home")
    public String home() {
        return "forward:/memberBalance/home";
    }

    /**
     * 原密码修改 -修改用户密码
     *
     * @param req
     * @param resp
     * @return
     * @throws ServletRequestBindingException
     */
    @RequestMapping(value = "updatePassword",method = RequestMethod.POST)
    @ResponseBody
    public ResultDO<Object> updatePassword(@Valid
                                           @ModelAttribute("form") MemberForm form, BindingResult bingResult,
                                           HttpServletRequest req, HttpServletResponse resp)
            throws ServletRequestBindingException {
        Long id = getMember().getId();
        ResultDO<Object> result = new ResultDO<Object>();
        validateResult(result, bingResult);
        if (!result.isSuccess()) {
            return result;
        }
        if (!StringUtil.equals(form.getNewPassword(), form.getCheckNewPassword(), true)) {

            result.setResultCode(ResultCode.MEMBER_RE_PASSWORD_ERROR);
            return result;
        }
        String oldPassword = form.getOldPassword();
        String newPassword = form.getNewPassword();
        result = memberService.updatePassword(id, oldPassword, newPassword);
        //修改密码成功， 清空session
        if (result.isSuccess()) {
            cleanSession(req, resp);
        }
        return result;
    }

    @RequestMapping(value = "update/password/page")
    public String getUpdatePage() {
        return "/member/changePassword";
    }


    /**
     * 消息通知配置
     *
     * @return
     */
    @RequestMapping(value = "memberNotifySettings")
    public String getMemberNotifySettingsPage() {
        return "/member/memberNotifySettings";
    }

    /**
     * 更新客户消息通知状态
     *
     * @param req
     * @param resp
     * @return
     * @throws ServletRequestBindingException
     */
    @RequestMapping(value = "updateMemberNotifySettings")
    @ResponseBody
    public ResultDO<MemberNotifySettings> updateMemberNotifySettings(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
        ResultDO<MemberNotifySettings> result = new ResultDO<MemberNotifySettings>();
        Long memberId = getMember().getId();
        Map<Integer, Map<Integer, Integer>> map = getNotifySettings(req);
        try {
            result = memberNotifySettingsService.updateNotifySettingsStatus(map, memberId);
        } catch (ManagerException e) {
            result.setResultCode(ResultCode.MEMBER_UPDATE_NOTIFY_SETTINGS_ERROR);
        }
        return result;
    }

    /**
     * 获得订阅配置项
     *
     * @param req
     * @return
     */
    private Map<Integer, Map<Integer, Integer>> getNotifySettings(HttpServletRequest req) {
        Map<Integer, Map<Integer, Integer>> map = Maps.newHashMap();
        // 通知类型 通知方式
        List<SysDict> notifyTypeSysDictList = myCacheManager.getListSysDictByGroupName("notify_type");
        List<SysDict> notifyWaySysDictList = myCacheManager.getListSysDictByGroupName("notify_way");
        for (SysDict notifyType : notifyTypeSysDictList) {
            Map<Integer, Integer> xmap = Maps.newHashMap();
            for (SysDict notifyWay : notifyWaySysDictList) {
                Integer id = ServletRequestUtils.getIntParameter(req, notifyType.getKey() + "_" + notifyWay.getKey(), Constant.DISABLE);
                xmap.put(Integer.parseInt(notifyWay.getValue()), id);
            }
            map.put(Integer.parseInt(notifyType.getValue()), xmap);
        }
        return map;
    }


    /**
     * 发送邮箱认证链接
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "/to/bind/email", method = RequestMethod.POST)
    @ResponseBody
    public ResultDO<Member> toBindEmail(HttpServletRequest req,
                                        HttpServletResponse resp) {
        Long memberId = getMember().getId();
        String email = req.getParameter("email");
        ResultDO<Member> result = memberService.toBindEmail(memberId, email);
        return result;
    }

    /**
     * 安全认证页面
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "security")
    public ModelAndView security(HttpServletRequest req,
                                 HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/member/security");
        return model;
    }

    /**
     * 会员退出
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "logout")
    public String logout(HttpServletRequest req, HttpServletResponse resp) {

        String path = cleanSession(req, resp);
        // 拼接跳转页面路径
        String basePath = req.getScheme() + "://" + req.getServerName() + ":"
                + req.getServerPort() + path + "/";
        return "redirect:" + basePath;

    }

    /**
     * 清空session里所有的值
     *
     * @param req
     * @param resp
     * @return author: pengyong
     * 上午9:19:49
     */
    private String cleanSession(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession httpSession = getHttpSession(req);
        // 清除session
        Enumeration<String> em = httpSession.getAttributeNames();
        while (em.hasMoreElements()) {
            req.getSession().removeAttribute(em.nextElement().toString());
        }
        httpSession.removeAttribute(Constant.CURRENT_USER);
        httpSession.invalidate();
        String path = req.getContextPath();
        setResponseHeaders(resp);
        return path;
    }


    /**
     * 个人资料页面
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "profile")
    public ModelAndView profile(HttpServletRequest req,
                                HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/member/profile");
        Long memberId = getMember().getId();
        MemberInfoDto infoDto = memberInfoService.getMemberInfoByMemberId(memberId);
        model.addObject("memberInfo", infoDto);

        return model;
    }

    /**
     *  消息订阅页面
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "msgfeed")
    public ModelAndView msgfeed(HttpServletRequest req,
                                HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();

        List<MemberNotifySettingsDto> memberNotifySettingsList = memberNotifySettingsService.getCheckedNotifySettings(getMember().getId());
        model.setViewName("/member/msgfeed");
        model.addObject("memberNotifySettingsList", memberNotifySettingsList);
        return model;
    }
    
    /**
     *  我的消息
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "messages")
    public ModelAndView myMessages(HttpServletRequest req,
                                    HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/member/message");
        return model;
    }
    
    /**
     *  邮箱绑定成功页面
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "emailBinded")
    public ModelAndView emailBinded(HttpServletRequest req,
                                    HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/member/emailBinded");
        return model;
    }

    /**
     * 保存用户详细信息
     *
     * @param memberInfoDto
     * @param bindingResult
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "saveMemberInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResultDO<MemberInfo> saveOrUpdateMemberInfo(@Valid @ModelAttribute("memberInfoDto") MemberInfoDto memberInfoDto, BindingResult bindingResult,
                                                       HttpServletRequest req, HttpServletResponse resp) {
        Long memberId = getMember().getId();
        memberInfoDto.setMemberId(memberId);
        ResultDO<MemberInfo> result = new ResultDO<MemberInfo>();
        try {
            result = memberInfoService.saveOrUpdateMemberInfoByMemberId(memberInfoDto);
        } catch (ManagerException e) {
            logger.error("保存用户详细信息失败,memberId:" + memberInfoDto.getMemberId());
        }
        return result;
    }

    /**
     * 保存用户昵称
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "saveUserName", method = RequestMethod.POST)
    @ResponseBody
    public ResultDO<Member> saveUserName(HttpServletRequest req, HttpServletResponse resp) {
        ResultDO<Member> result = new ResultDO<Member>();
        String userName = ServletRequestUtils.getStringParameter(req, "userName", null);
        if (StringUtils.isNotEmpty(userName)) {
            Long memberId = getMember().getId();
            result = memberService.saveUserName(memberId, userName);
            if (result.isSuccess()) {
                MemberSessionDto memberSession = getMember();
                memberSession.setUsername(userName);
                updateMemberSessionDto(req, memberSession);
            }
        } else {
            result.setResultCode(ResultCode.ERROR);
        }
        return result;
    }


    /**
     * 保存用户头像
     *
     * @param req
     * @param resp
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "saveAvatar", method = RequestMethod.POST)
    @ResponseBody
    public ResultDO<Object> saveAvatar(HttpServletRequest req, HttpServletResponse resp) {
        //TODO 待重构
        ResultDO<Object> result = new ResultDO<Object>();
        try {
            Long id = getMember().getId();//110800000129L;
            int x = Math.round(ServletRequestUtils.getFloatParameter(req, "x", 0));
            int y = Math.round(ServletRequestUtils.getFloatParameter(req, "y", 0));
            int w = Math.round(ServletRequestUtils.getFloatParameter(req, "w", 0));
            int h = Math.round(ServletRequestUtils.getFloatParameter(req, "h", 0));
            String avatars = ServletRequestUtils.getStringParameter(req, "avatars", null);
            String appPath = req.getSession().getServletContext().getRealPath("/");
            String filePath = appPath + avatars;
            String newPath = getNewFile(filePath);
            
            File file = new File(filePath);
            if(!file.exists()){//如果本地文件不存在，则去aliyun获取图片
            	String ossPicUrl = ServletRequestUtils.getStringParameter(req, "ossPicUrl", null);
            	FileUtils.copyURLToFile(new URL(ossPicUrl), file);
            }
            Thumbnails.of(filePath).sourceRegion(x, y, w, h).size(w, h).keepAspectRatio(false).toFile(newPath);
            UploadUtil.doScaleAndAddWatemarkImage(new File(newPath), "avatar", imagesConfig);
            memberService.saveAvatar(id, newPath);
        } catch (Exception ex) {
            result.setSuccess(false);
        }
        return result;
    }


    private String getNewFile(String filePath) {
        String path = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        String name = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
        return path + "avatar_" + name;
    }

    
    /**
	 * 用户签到
	 * @param req
	 * @param resps
	 * @return
	 */
	@RequestMapping(value = "check", method = RequestMethod.GET)
	@ResponseBody
	public ResultDO<MemberCheck> memberCheck(HttpServletRequest req,
			HttpServletResponse resps) {
		Long memberId = getMember().getId();
		Integer loginSource = getMember().getLoginSource();
		if(loginSource==null) {
			loginSource = TypeEnum.MEMBER_LOGIN_SOURCE_PC.getType();
		}
		ResultDO<MemberCheck> result = null;
		try {
			result = memberService.memberCheck(memberId, loginSource);
		} catch (DuplicateKeyException mysqlE) { //重复签到不打印日志
			logger.error("会员重复签到！memberId={}", memberId);
		} catch (Exception e) {
			logger.error("用户签到发生异常", e);
		}
		return result;
	}
	
	/**
	 * 新浪存钱罐查询验证
	 */
	 @RequestMapping(value = "auditMemberInfos")
	 public ModelAndView auditSavingPot(){
		 MemberSessionDto userName = ServletUtil.getUserDO();
		 Long memberId= userName.getId();
		 ModelAndView mv = new ModelAndView();
		 mv.setViewName("/member/authSavingPotPage");
		 try {
			String auditHtml = memberService.getAuditMemberInfosForm(memberId);
			mv.addObject("auditHtml", auditHtml);
		} catch (Exception e) {
			logger.error("新浪存钱罐查询验证生成签名异常",e);
			mv.setViewName("/error");  
		}
		 return mv;
	 }
	 @RequestMapping(value="capitalInOutLogList", method = RequestMethod.POST)
	 @ResponseBody
	 public Map<String, Object> capitalInoutLogPage(@ModelAttribute("capitalQuery")CapitalQuery capitalQuery){
		 Map<String, Object> map = Maps.newHashMap();
		 capitalQuery.setMemberId(getMember().getId());
		 Page<CapitalInOutForMemberCenter> capitalPage = capitalInOutLogService.findCapitalPageForAcountCenter(capitalQuery);
		 map.put("capitalPage", capitalPage);
		 map.put("capitalQuery", capitalQuery);
		 return map;
	 }
	 
	 @RequestMapping(value="capitalInOutLog")
	 public String capitalInOutLog(){
		 return "/member/capitalInOutLog";
	 }
	 
	 /**
	  * 新手任务状态
	  * @param req
	  * @param resp
	  * @return
	  */
	 @RequestMapping(value = "getNoviceTaskStatus",method = RequestMethod.POST)
	 @ResponseBody
	 public ResultDO getNoviceTaskStatus(HttpServletRequest req, HttpServletResponse resp){
		 return memberService.getNoviceTaskStatus(getMember().getId());
	 }
    /**
     *  我的借款
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "borrowMoney")
    public ModelAndView borrowMoney(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        Long memberId = getMember().getId();
        ResultDO result = projectService.getBorrowCenterData(memberId);
    	model.addObject("data", result.getResult());
        model.setViewName("/member/borrowMoney/borrowMoney");
        return model;
    }
    /**
     * 
     * @Description:保存风险测评结果
     * @param req
     * @param resp
     * @return
     * @author: chaisen
     * @time:2016年6月3日 下午5:56:56
     */
    @RequestMapping(value = "saveEvaluation", method = RequestMethod.POST)
    @ResponseBody
    public ResultDO<MemberInfo>saveEvaluationMemberInfo(HttpServletRequest req, HttpServletResponse resp) {
        ResultDO<MemberInfo> result = new ResultDO<MemberInfo>();
        MemberSessionDto member = getMember();
        int evaluationScore=ServletRequestUtils.getIntParameter(req, "evaluationScore", 0);
		//登录认证
		if (member == null) {
			result.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
			return result;
		}
		if(evaluationScore==0){
			result.setResultCode(ResultCode.EVALUATION_SCORE_RESULT);
			return result;
		}
		Long memberId = getMember().getId();
        try {
            result = memberInfoService.UpdateMemberInfoByMemberId(memberId,evaluationScore);
        } catch (ManagerException e) {
            logger.error("测评失败,memberId:" + memberId);
        }
        return result;
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
        model.setViewName("/member/questionnaire");
        return model;
    }
    
    /**
     * 更新用户签署方式
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "saveSignWay", method = RequestMethod.POST)
    @ResponseBody
    public ResultDO<Member> saveSignWay(HttpServletRequest req, HttpServletResponse resp) {
        ResultDO<Member> result = new ResultDO<Member>();
        int signWay = ServletRequestUtils.getIntParameter(req, "signWay", 0);
        if(signWay==1||signWay==0){
        	Long memberId = getMember().getId();
            result = memberService.saveSignWay(memberId, signWay);
            return result;
        }   
        result.setResultCode(ResultCode.ERROR);
        return result;
    }
    /**
     *  我的资料-上上签
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "eSignature")
    public ModelAndView eSignature(HttpServletRequest req,
                                   HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/member/eSignature");
        return model;
    }
    /**
     *  我的资料-上上签
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "transit")
    public ModelAndView transit(HttpServletRequest req,
                                   HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/order/transit");
        return model;
    }

    
    /**
	 * 委托授权扣款操作(1开通/2关闭)
	 * 
	 * @return
	 */
    @RequestMapping(value = "handWithholdAuthority", method = RequestMethod.POST)
    @ResponseBody
	public ResultDO<String> handWithholdAuthority(HttpServletRequest req, HttpServletResponse resp) {
    	int handleType = ServletRequestUtils.getIntParameter(req, "type", 0);
    	return this.memberService.handWithholdAuthority(getMember().getId(), handleType);
	}
    
    /**
	 * 查询并同步是否开通委托扣款
	 * 
	 */
    @RequestMapping(value = "queryWithholdAuthority", method = RequestMethod.POST)
    @ResponseBody
	public Object queryWithholdAuthority(HttpServletRequest req, HttpServletResponse resp) {
    	return memberService.synWithholdAuthority(getMember().getId());
	}
    
	/**
	 * 
	 * @Description:设置支付密码
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2016年7月15日 下午5:50:36
	 */
	@RequestMapping(value = "handlePayPassword", method = RequestMethod.POST)
	@ResponseBody
	public Object handlePayPassword(HttpServletRequest req, HttpServletResponse resp) {
		int handleType = ServletRequestUtils.getIntParameter(req, "handleType", 0);
		return memberService.handlePayPassword(getMember().getId(), handleType);
	}
	
	/**
	 * 
	 * @Description:同步设置支付密码状态
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2016年7月15日 下午5:50:36
	 */
	@RequestMapping(value = "synPayPasswordFlag", method = RequestMethod.POST)
	@ResponseBody
	public Object synPayPasswordFlag(HttpServletRequest req, HttpServletResponse resp) {
		MemberSessionDto member = getMember();
		ResultDO<Boolean> rDO = new ResultDO<Boolean>();
		if (member.getPayPasswordFlag() == StatusEnum.SET_PAY_SUCCESS_FLAG_Y.getStatus()) {
			rDO.setResult(true);
			return rDO;
		}
		return memberService.synPayPasswordFlag(getMember().getId());
	}

    /**
     *  我的存钱罐-委托支付
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "delegatePay")
    public ModelAndView delegatePayPage(HttpServletRequest req,
                                    HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/member/delegatePay");
        return model;
    }
    /**
     *  我的存钱罐-委托支付
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "paymentPassword")
    public ModelAndView paymentPassword(HttpServletRequest req,
                                        HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/member/paymentPassword");
        return model;
    }
    /**
     *  支付相关的页面中转跳转
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "pageRelay")
    public ModelAndView pageRelay(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/member/pageRelay");
        return model;
    }

    /**
     *  订单支付中转页面
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "payRelay")
    public ModelAndView payRelay(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/member/payRelay");
        return model;
    }

    /**
     *  用户中心修改手机号码
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "modifyMobile")
    public ModelAndView modifyMobile(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/member/modifyMobile");
        return model;
    }
    
    /**
     * 验证码方式修改手机号(新手机号验证码校验并修改手机号)
     *
     * @param newMobile
     * @param req
     * @param resp
     * @return
     * @throws Exception
     */
	@RequestMapping(value = "modifyMobileByCaptcha", method = RequestMethod.POST)
	@ResponseBody
	public Object modifyMobileByCaptcha(@ModelAttribute("newMobile") Long newMobile, HttpServletRequest req, HttpServletResponse resp) {
		ResultDO<Object> resultDO = new ResultDO<>();
		MemberSessionDto member = getMember();
		try {
			// 获取手机号码有误
			if (!RegexUtils.checkMobile(newMobile+"")) {
				resultDO.setResultCode(ResultCode.MEMBER_MOBILE_ERROR);
				logger.info(resultDO.getResultCode().getMsg());
				return resultDO;
			}
			
			String newMobileCaptcha = ServletRequestUtils.getStringParameter(req, "captcha");
			if (req.getSession().getAttribute(Constant.SMS_CONTENT_CODE) == null) {
				resultDO.setResultCode(ResultCode.ERROR_SYSTEM_REQUEST_PARAM_DECODE);
				logger.info("未获取到session中的验证码！");
				return resultDO;
			}
			String sessionMobileCaptcha = (String) req.getSession().getAttribute(Constant.SMS_CONTENT_CODE);
			
			// 检验手机验证码的验证次数
			boolean checkSmsCodeCounts = checkSmsCodeCounts(newMobile);
			if (checkSmsCodeCounts) {
				resultDO.setSuccess(false);
				resultDO.setResultCode(ResultCode.ERROR);
				return resultDO;
			}
			// 获得session中的手机号和验证码
			List<String> newMobileCaptchaInfos = Splitter.on(Constants.ANGLE_BRACKETS).splitToList(sessionMobileCaptcha);
			Long sessionMobile = null;
			String sessionCaptcha = null;
	    	if (!newMobileCaptchaInfos.isEmpty()) {
	    		sessionMobile = Long.valueOf(newMobileCaptchaInfos.get(0));
	    		sessionCaptcha = newMobileCaptchaInfos.get(1);
	    	}
	    	if (sessionMobile == null || sessionCaptcha == null) {
				resultDO.setResultCode(ResultCode.ERROR_SYSTEM_REQUEST_PARAM_DECODE);
				logger.info("未获取到session中的验证码！");
				return resultDO;
	    	}
			// 核对手机号
	    	if (!newMobile.equals(sessionMobile)) {
				resultDO.setResultCode(ResultCode.MEMBER_CHECK_MOBILE_CODE_ERROR);
				logger.info(resultDO.getResultCode().getMsg());
				return resultDO;
	    	}
			// 核对验证码
			if (StringUtil.equals(newMobileCaptcha, sessionCaptcha, true)) {
				// 手机号和验证码进行加密
				String newMobileCaptcharAes = CryptHelper.encryptByase(newMobile + Constants.ANGLE_BRACKETS + newMobileCaptcha);
				resultDO = this.memberService.modifyMobileByOldMobile(member.getId(), newMobile, member.getMobile());
				// 清空session
				if (resultDO.isSuccess()) {
					// 清空session
					cleanSession(req, resp);
					// 重新把加密后的手机号和验证码放如session中
					req.getSession().setAttribute(Constant.MEMBER_MODIFY_MOBILE_CAPTCHA_AES, newMobileCaptcharAes);
					// 新手机号验证码存入redis中
					RedisManager.putString(RedisConstant.REDIS_KEY_MODIFY_MOBILE + newMobile, newMobileCaptcha);
//					RedisManager.expireObject(Constant.MEMBER_MODIFY_MOBILE_CAPTCHA, 1200);
				}
			} else {
				resultDO.setResultCode(ResultCode.MEMBER_CAPTCHA_ERROR);
			}
		} catch (ServletRequestBindingException ex) {
			logger.error("验证码方式修改手机号异常，mobile="+ member.getMobile() +",newMobile=" + newMobile, ex);
		}
		return resultDO;
	}
	
	/**
	 * 身份信息修改手机号，验证信息
	 * 
	 * @param newMobile
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "validateMemberInfo", method = RequestMethod.POST)
	@ResponseBody
	public Object validateMemberInfo(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		// 核对验证码
		MemberSessionDto member = getMember();
		// 姓名
		String trueName = ServletRequestUtils.getStringParameter(req, "trueName");
		// 身份证号码
		String identityNumber = ServletRequestUtils.getStringParameter(req, "identityNumber");
		// 登录密码
		String password = ServletRequestUtils.getStringParameter(req, "password");
		return this.memberService.validateMemberInfo(member.getMobile(), trueName, identityNumber, password);
	}

    /**
     * 身份信息修改手机号
     *
     * @param newMobile
     * @param req
     * @param resp
     * @return
     * @throws Exception
     */
	@RequestMapping(value = "modifyMobileByIdentity", method = RequestMethod.POST)
	@ResponseBody
	public Object modifyMobileByIdentity(@ModelAttribute("newMobile") Long newMobile, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ResultDO<Object> resultDO = new ResultDO<>();
		// 获取手机号码有误
		if (!RegexUtils.checkMobile(newMobile+"")) {
			resultDO.setResultCode(ResultCode.MEMBER_MOBILE_ERROR);
			logger.info(resultDO.getResultCode().getMsg());
			return resultDO;
		}
		String newMobileCaptcha = ServletRequestUtils.getStringParameter(req, "captcha");
		if (req.getSession().getAttribute(Constant.SMS_CONTENT_CODE) == null) {
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM_REQUEST_PARAM_DECODE);
			logger.info("未获取到session中的验证码！");
			return resultDO;
		}
		String sessionMobileCaptcha = (String) req.getSession().getAttribute(Constant.SMS_CONTENT_CODE);
		
		// 检验手机验证码的验证次数
		boolean checkSmsCodeCounts = checkSmsCodeCounts(newMobile);
		if (checkSmsCodeCounts) {
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.info("验证码验证次数超过限制次数！");
			return resultDO;
		}
		// 获得session中的手机号和验证码
		List<String> newMobileCaptchaInfos = Splitter.on(Constants.ANGLE_BRACKETS).splitToList(sessionMobileCaptcha);
		Long sessionMobile = null;
		String sessionCaptcha = null;
    	if (!newMobileCaptchaInfos.isEmpty()) {
    		sessionMobile = Long.valueOf(newMobileCaptchaInfos.get(0));
    		sessionCaptcha = newMobileCaptchaInfos.get(1);
    	}
    	if (sessionMobile == null || sessionCaptcha == null) {
    		resultDO.setResultCode(ResultCode.ERROR_SYSTEM_REQUEST_PARAM_DECODE);
			logger.info("未获取到session中的验证码！");
			return resultDO;
    	}
		// 核对手机号
    	if (!newMobile.equals(sessionMobile)) {
			resultDO.setResultCode(ResultCode.MEMBER_CHECK_MOBILE_CODE_ERROR);
			logger.info(resultDO.getResultCode().getMsg());
			return resultDO;
    	}
		// 核对验证码
		if (StringUtil.equals(newMobileCaptcha, sessionCaptcha, true)) {
			MemberSessionDto member = getMember();
			// 姓名
			String trueName = ServletRequestUtils.getStringParameter(req, "trueName");
			// 身份证号码
			String identityNumber = ServletRequestUtils.getStringParameter(req, "identityNumber");
			resultDO = this.memberService.modifyMobileByIdentity(member.getMobile(), newMobile, trueName, identityNumber);
			if (resultDO.isSuccess()) {
				// 修改手机号后清空session
				cleanSession(req, resp);
				
				// 加密后的手机号、身份证号码、姓名放入session中
				String sessionMemberModifyInfo = CryptHelper.encryptByase(newMobile + Constants.ANGLE_BRACKETS + identityNumber+ Constants.ANGLE_BRACKETS + trueName);
				req.getSession().setAttribute(Constant.MEMBER_MODIFY_MOBILE_IDENTITY_NAME, sessionMemberModifyInfo);
			}
		} else {
			resultDO.setResultCode(ResultCode.MEMBER_CAPTCHA_ERROR);
		}
		return resultDO;
	}
	
    // 检查手机的验证次数
	private boolean checkSmsCodeCounts(long mobile) {
		boolean result = false;
		// 记录验证次数
		RedisMemberClient.setUserCheckCount(mobile);
		// 如果验证次数大于5次
		Integer userCheckCount = RedisMemberClient.getUserCheckCount(mobile);
		if (userCheckCount >= Constant.USER_MEMBER_COUNTS) {
			result = true;
		}
		return result;
	}
    
    /**
     * 
     * @Description:自动投标设置初始化页面
     * @param req
     * @param resp
     * @return
     * @author: chaisen
     * @time:2016年8月16日 上午11:28:15
     */
    @RequestMapping(value = "queryAutoInvest", method = RequestMethod.POST)
    @ResponseBody
	public Object queryAutoInvest(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMember().getId();
		 ResultDO<AutoInvestSet> result = new ResultDO<AutoInvestSet>();
		 result= memberService.queryAutoInvest(memberId);
		 return result;
	}
    /**
     * 
     * @Description:自动投标设置
     * @param req
     * @param resp
     * @return
     * @author: chaisen
     * @time:2016年8月16日 上午11:37:13
     */
    @RequestMapping(value = "autoInvestSet", method = RequestMethod.POST)
    @ResponseBody
    public Object autoInvestOpen(HttpServletRequest req, HttpServletResponse resp) {
        ResultDO<AutoInvestSet> result = new ResultDO<AutoInvestSet>();
        if(getMember()==null){
        	result.setResultCode(ResultCode.ACTIVITY_REPORT_IS_NOT_LOGIN);
        	return result;
        }
        Long memberId = getMember().getId();
        int investFlag = ServletRequestUtils.getIntParameter(req, "investFlag", 0);
        return memberService.autoInvestSet(memberId,investFlag);
    }
    /**
     * 
     * @Description:保存投标设置信息
     * @param autoInvestDto
     * @param bindingResult
     * @param req
     * @param resp
     * @return
     * @author: chaisen
     * @time:2016年8月17日 上午9:35:12
     */
    @RequestMapping(value = "saveAutoInvestSet", method = RequestMethod.POST)
    @ResponseBody
    public ResultDO<AutoInvestSet> saveAutoInvestSet(@Valid @ModelAttribute("autoInvestDto") AutoInvestSetDto autoInvestDto, BindingResult bindingResult,
                                                       HttpServletRequest req, HttpServletResponse resp) {
        Long memberId = getMember().getId();
        autoInvestDto.setMemberId(memberId);
        ResultDO<AutoInvestSet> result = new ResultDO<AutoInvestSet>();
        try {
            result = memberService.saveAutoInvestSetByMemberId(autoInvestDto);
        } catch (ManagerException e) {
            logger.error("保存投标设置信息失败,autoInvestDto:" + autoInvestDto.getMemberId());
        }
        return result;
    }
    
    /**
     * 获取用户信息新浪展示页
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "showMemberInfosSina", method = RequestMethod.POST)
    @ResponseBody
    public ResultDO<Object> showMemberInfosSina(HttpServletRequest req, HttpServletResponse resp) {
        ResultDO<Object> result = new ResultDO<Object>();
        Long memberId = getMember().getId();
        if(memberId<0){
        	result.setResultCode(ResultCode.ERROR);
        	return result;
        }
        result = memberService.showMemberInfosSina(memberId);
        return result;
    }
    /**
     *  自动投标
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "autoInvest")
    public ModelAndView autoInvest(HttpServletRequest req,
                                   HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/member/autoInvest");
        return model;
    }
}
