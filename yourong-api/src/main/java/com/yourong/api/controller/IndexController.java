package com.yourong.api.controller;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.yourong.api.dto.IndexDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.MemberService;
import com.yourong.api.service.PaymentPlatformService;
import com.yourong.api.service.ProjectService;
import com.yourong.api.service.XmlAnalyticalService;
import com.yourong.api.utils.APIPropertiesUtil;
import com.yourong.api.utils.SysServiceUtils;
import com.yourong.common.enums.MemberLogEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.Exceptions;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.uc.model.Member;

/**
 * Created by py on 2015/4/23.
 */
@Controller
public class IndexController extends  BaseController {
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private  XmlAnalyticalService xmlAnalyticalService;
	
	@Autowired
	private PaymentPlatformService paymentPlatformService;
	
    public static final  Map  maps = Maps.newHashMap();
    static {
   
        maps.put("api.server.version", APIPropertiesUtil.getProperties("api.server.version").split(","));
        maps.put("ios.url",APIPropertiesUtil.getProperties("ios.url"));
        maps.put("android.url","http://www.yrw.com/download/apk");
        maps.put("lastInvestReward", APIPropertiesUtil.getProperties("lastInvestReward"));//一锤定音奖励
        maps.put("mostInvestReward", APIPropertiesUtil.getProperties("mostInvestReward"));//一鸣惊人奖励
        
        maps.put("bothlmInvestReward", APIPropertiesUtil.getProperties("bothlmInvestReward"));//一掷千金送人气值点数
        
        maps.put("withdrawalFees", APIPropertiesUtil.getProperties("withdrawalFees"));//提现手续费
        maps.put("isOpenFees", APIPropertiesUtil.getProperties("isOpenFees"));//是否开启提现手续费
        maps.put("isOpenBankTips", APIPropertiesUtil.getProperties("isOpenBankTips"));//是否开启银行卡提示
        maps.put("isOpenCoupons", APIPropertiesUtil.getProperties("isOpenCoupons"));//是否开启兑换现金券
        
        
        
        
        //Collections.unmodifiableMap(maps);
    }
    @RequestMapping(value = "/getVersion")
    @ResponseBody
    public ResultDTO<Object> getVersion(HttpServletRequest req,   HttpServletResponse resp) throws Exception {
        ResultDTO resultDTO = new ResultDTO();
        

        maps.put("isBirth", false);
        //用户生日提醒闪屏
        Long memberId = getMemberID(req);
        if (memberId != null) {
			Member member = memberService.selectByPrimaryKey(memberId);
			if (member != null) {
				if(member.getBirthday() != null){
					int status = getBirthdayStatus(member.getBirthday());
					if(status == 1){
						maps.put("avatars", member.getAvatars());//个人头像
						maps.put("trueName", member.getTrueName());//真实姓名
						maps.put("isBirth", true);
					}
				}
			}
		}
        
        String androidIsUpgrade =SysServiceUtils.getDictValue("androidIsUpgrade", "update_configuration", "");
        String  iosIsUpgrade = SysServiceUtils.getDictValue("iosIsUpgrade", "update_configuration", "");
        if(!"true".equals(androidIsUpgrade)){
        	androidIsUpgrade = "false";
        }
        if(!"true".equals(iosIsUpgrade)){
        	iosIsUpgrade = "false";
        }
        maps.put("android.isUpgrade",androidIsUpgrade);
        maps.put("ios.isUpgrade",iosIsUpgrade);
        
        String androidVersion =SysServiceUtils.getDictValue("androidVersion", "update_configuration", "");
        String  iosVersion = SysServiceUtils.getDictValue("iosVersion", "update_configuration", "");
        maps.put("android.version", androidVersion);
        maps.put("ios.version", iosVersion);
        
        String version = ServletRequestUtils.getStringParameter(req, "huluwa", "");
        if(StringUtils.isNotBlank(version)){
        	androidVersion = version;
        	iosVersion = version;
        }
        maps.put("android.context", xmlAnalyticalService.XmlAnalytical("1", androidVersion));
        maps.put("ios.context", xmlAnalyticalService.XmlAnalytical("2", iosVersion));
        
        
        //根据字典获取不同手机类型下的启动广告图片
        Map  launchAdvImgUrlmaps = Maps.newHashMap(); //getDictList
        
        //launchAdvImgUrlmaps.put("launchAdvImgUrl", SysServiceUtils.getDictList("launchAdvImgUrl"));
        String aLiYun =PropertiesUtil.getAliyunUrl();
        String iPhone6p = SysServiceUtils.getDictValue("iPhone6p", "launchAdvImgUrl", "");
        String iPhone6 = SysServiceUtils.getDictValue("iPhone6", "launchAdvImgUrl", "");
        String iPhone5 = SysServiceUtils.getDictValue("iPhone5", "launchAdvImgUrl", "");
        String iPhone4 = SysServiceUtils.getDictValue("iPhone4", "launchAdvImgUrl", "");
        String android = SysServiceUtils.getDictValue("android", "launchAdvImgUrl", "");
        
        String link = SysServiceUtils.getDictValue("link", "launchAdvImgUrl", "");
        String appTitle = SysServiceUtils.getDictValue("appTitle", "launchAdvImgUrl", "");
        
        launchAdvImgUrlmaps.put("iPhone6p",(StringUtil.isNotBlank(iPhone6p)?aLiYun+iPhone6p:iPhone6p));
        launchAdvImgUrlmaps.put("iPhone6", (StringUtil.isNotBlank(iPhone6)?aLiYun+iPhone6:iPhone6));
        launchAdvImgUrlmaps.put("iPhone5", (StringUtil.isNotBlank(iPhone5)?aLiYun+iPhone5:iPhone5));
        launchAdvImgUrlmaps.put("iPhone4", (StringUtil.isNotBlank(iPhone4)?aLiYun+iPhone4:iPhone4));
        launchAdvImgUrlmaps.put("android", (StringUtil.isNotBlank(android)?aLiYun+android:android));
        launchAdvImgUrlmaps.put("link", (StringUtil.isNotBlank(link)?link:link));
        launchAdvImgUrlmaps.put("appTitle", (StringUtil.isNotBlank(appTitle)?appTitle:appTitle));
        
        maps.put("launchAdvImgUrl",launchAdvImgUrlmaps);
        
        String httpUrl = SysServiceUtils.getDictValue("httpUrl", "httpUrl", "");
		maps.put("httpUrl", httpUrl);
		
		String firstHttpUrl = SysServiceUtils.getDictValue("firstHttpUrl", "httpUrl", "");
		maps.put("firstHttpUrl", firstHttpUrl);
		
		String secondHttpUrl = SysServiceUtils.getDictValue("secondHttpUrl", "httpUrl", "");
		maps.put("secondHttpUrl", secondHttpUrl);
		
		String sesame = SysServiceUtils.getDictValue("sesame", "sesame", "N");
		maps.put("sesame", sesame);
        
		// 记录打包时间戳
		String timer = PropertiesUtil.getStaticResourceVersion();
		if (StringUtil.isNotBlank(timer) && StringUtil.isNumeric(timer)) {
			try {
				String newTimer = DateUtils.getStrFromDate(new Date(Long.valueOf(timer)), DateUtils.TIME_PATTERN);
				timer = newTimer;
			} catch (Exception e) {
				logger.error("App-Version-static时间戳转换失败 timer={}", timer, e);
			}
		}
		maps.put("App-Version-static", timer);
        resultDTO.setResult(maps);
		// 后台日志打印当前服务器IP
		logger.info("当前服务器地址={}", req.getRemoteAddr());
         return  resultDTO;
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
    public ModelAndView error500(HttpServletRequest request,Exception ex) {
        ModelAndView model =new ModelAndView();
        this.logger.error("页面异常", ex);
        model.addObject("exception", Exceptions.getStackTraceAsString(ex));
        model.setViewName("/500");
        request.getSession().invalidate();
        return model;
    }
    
    /**
	 * 查询首页Banner&推荐项目
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryIndexData", method = RequestMethod.POST, headers = {"Accept-Version=1.0.0"})
	@ResponseBody
	public ResultDTO queryIndexData(HttpServletRequest req, HttpServletResponse resp){
		return projectService.queryIndexData();
	}
	
	@RequestMapping(value = "queryIndexData", method = RequestMethod.POST, headers = {"Accept-Version=1.3.0"})
	@ResponseBody
	public ResultDTO queryIndexData3(HttpServletRequest req, HttpServletResponse resp){
		Long memberId = getMemberID(req);
		ResultDTO<IndexDto>  result = projectService.queryIndexData2(memberId,130);
		return result;
	}
	
	@RequestMapping(value = "queryIndexData", method = RequestMethod.POST, headers = {"Accept-Version=1.4.0"})
	@ResponseBody
	public ResultDTO queryIndexData4(HttpServletRequest req, HttpServletResponse resp){
		Long memberId = getMemberID(req);
		ResultDTO<IndexDto>  result = projectService.queryIndexData2(memberId,140);
		return result;
	}
	
	/**
	 * 网站声明
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/agreement/declare.html")
    public String declare(HttpServletRequest req, HttpServletResponse resp) {
        return "/agreement/declare";
    }
	
	/**
	 * 快捷支付
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/agreement/quickPay.html")
    public String quickPay(HttpServletRequest req, HttpServletResponse resp) {
        return "/agreement/quickPay";
    }
	
	/**
	 * 有融网服务协议
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/agreement/register.html")
    public String register(HttpServletRequest req, HttpServletResponse resp) {
        return "/agreement/register";
    }
	
	/**
	 * 存钱罐服务协议
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/agreement/sinaPay.html")
    public String sinaPay(HttpServletRequest req, HttpServletResponse resp) {
        return "/agreement/sinaPay";
    }
	
	/**
	 * 银行卡限额
	 * @return
	 */
	@RequestMapping(value = "/getBankQuota")
    @ResponseBody
	public ResultDTO getBankQuota(){
		return paymentPlatformService.selectAllPaymentPlatform();
	}
	
	public int getBirthdayStatus(Date birthday){
		Date date = DateUtils.getCurrentDate();
		int c_month = DateUtils.getMonth(date);
		int c_day = DateUtils.getDate(date);
		int b_month = DateUtils.getMonth(birthday);
		int b_day = DateUtils.getDate(birthday);
		if(c_month < b_month){
			return 0;//生日还未到
		}else if(c_month == b_month && c_day == b_day){
			return 1;//今天生日
		}else{
			return -1;//生日已经过了
		}
	}
	
}
