package com.yourong.api.controller;


import com.google.common.collect.Maps;
import com.yourong.api.dto.WithdrawLogDto;
import com.yourong.api.service.MemberService;
import com.yourong.api.service.RechargeLogWithdrawLogService;
import com.yourong.api.service.TokenService;
import com.yourong.api.service.WeiXinService;
import com.yourong.api.service.ActivitySubscribeService;
import com.yourong.api.utils.APIPropertiesUtil;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.weixin.TextMsg;
import com.yourong.common.weixin.util.MessageUtil;
import com.yourong.common.weixin.util.SignUtil;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.constant.Constant;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.StringUtil;
import com.yourong.core.mc.model.ActivitySubscribe;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberToken;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by py on 2015/4/10.
 */
@Controller
@RequestMapping(value = "weixin")
public class WeixinController  extends  BaseController{
    public static final String ROOT_URL_WEB = PropertiesUtil.getProperties("root_url_web");
    @Autowired
    private TokenService tokenService;

    @Autowired
    private RechargeLogWithdrawLogService rechargeLogWithdrawLogService;
    
    @Autowired
    private WeiXinService weiXinService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ActivitySubscribeService activitySubscribeService;

    public static  String url = "";

     public static String WEIXIN_YOURONG="";

    @RequestMapping(
            method = {RequestMethod.GET}
    )
    @ResponseBody
    public void bind(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String echostr = request.getParameter("echostr");
      if(isLegal(request)){
            PrintWriter writer = response.getWriter();
            writer.write(echostr);
            writer.flush();
            writer.close();
        }
    }

    @RequestMapping(
            method = {RequestMethod.POST}
    )
    @ResponseBody
    public  void  process(HttpServletRequest request,HttpServletResponse response) throws Exception {
      String content =  !this.isLegal(request) ? "" : this.processRequest(request,response);
      renderJson(response, content);
    }
    private String getToken() {
        return APIPropertiesUtil.getProperties("weixin.api.token");
    }
   private String getAppId() {
        return APIPropertiesUtil.getProperties("weixin.api.appID");
    }
    private String getAESKey() {
        return "";
    }
    protected boolean isLegal(HttpServletRequest request) {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
       return SignUtil.checkSignature(this.getToken(), signature, timestamp, nonce);
    }


    protected String processRequest(HttpServletRequest request,HttpServletResponse response)throws IOException {
        Map reqMap = MessageUtil.parseXml(request, this.getToken(), this.getAppId(), this.getAESKey());
        String fromUserName = (String)reqMap.get("FromUserName");
        String toUserName = (String)reqMap.get("ToUserName");
        String msgType = (String)reqMap.get("MsgType");
        String content = (String)reqMap.get("Content");
        logger.debug("收到消息,消息类型:{}", msgType);
        String result;
        String e;
        if(msgType.equals("event")) {
        	String eventKey = (String)reqMap.get("EventKey");
            result = (String)reqMap.get("Event");
            e = (String)reqMap.get("Ticket");
            if(StringUtil.isNotBlank(e)) {

            }
            //首次关注
            if(result.equals("subscribe")) {
            	//return followTips(fromUserName, toUserName);
                //带参数二维码关注记录
                if (!StringUtils.isEmpty(eventKey)){
                    String uniqueStr=fromUserName+"-"+eventKey;
                    ActivitySubscribe activitySubscribe= activitySubscribeService.querySubscribeByOpenIdAndUniqueStr(fromUserName,uniqueStr);
                    if (activitySubscribe==null){
                        ActivitySubscribe insert=new ActivitySubscribe();
                        insert.setOpenId(fromUserName);
                        insert.setUniqueStr(uniqueStr);
                        insert.setActivityName(eventKey);
                        insert.setCreateTime(new Date());
                        activitySubscribeService.insertSubscribe(insert);
                    }
                }
            	return weiXinService.firstConcern(fromUserName, toUserName);
            } else if(result.equals("unsubscribe")) {

            } else {
                if(result.equals("CLICK")) {
                    String generate = clientEvent(eventKey, fromUserName, toUserName);
                    return generate;
                } else if(result.equals("VIEW")) {

                } else if(result.equals("LOCATION")) {

                }
            }
        } else if(msgType.equals("text")) {
        	if(StringUtil.isNotBlank(content)){
        		if(content.equals("解绑")){
        			MemberToken memberToken = tokenService.selectByWeixinID(fromUserName);
        			if(memberToken != null){
        				return weiXinService.unbundlingWeiXinStep1(fromUserName, toUserName, memberToken);
        			}
        		}else if(content.equals("1") || content.equals("2") || content.equals("确认") || content.equals("取消")){
        			MemberToken memberToken = tokenService.selectByWeixinID(fromUserName);
        			if(memberToken != null){
        				return weiXinService.unbundlingWeiXinStep2(fromUserName, toUserName, content, memberToken);
        			}
        		}else if(weiXinService.keyWords(content).contains(content)){
        			//关键字回复不需要绑定
        			//MemberToken memberToken = tokenService.selectByWeixinID(fromUserName);
        			//if(memberToken != null){
        				return weiXinService.unbundlingWeiXinStep3(fromUserName, toUserName, content);
        			//}
        		}else if(content.startsWith("yrtz")){
        			Date timeDate=null;
        			String date=null;
        			String temp[]=content.split("yrtz");
        			if(temp.length>0){
        				   date=temp[1];
        				try {
        					timeDate=DateUtils.getDateFromStrings(date, DateUtils.DATE_FMT_0);
        					System.out.println(timeDate);
        				} catch (Exception ex) {
        					return weiXinService.responseTransferCustomerService(fromUserName, toUserName);
        				}
        			}else{
        				return weiXinService.responseTransferCustomerService(fromUserName, toUserName);
        			}
        			MemberToken memberToken = tokenService.selectByWeixinID(fromUserName);
        			Long memberId=1l;
        			if(memberToken!=null){
        				memberId=memberToken.getMemberId();
        			}
        			//if(memberToken!=null){
        				//if(memberService.isAuthIdentityAndPhone(memberToken.getMemberId())){
            	    		return weiXinService.queryMemberInvest(memberId, fromUserName, toUserName,timeDate,date);
            	    	//}else{
            	    	//	return authIdentityMsg(fromUserName, toUserName);
            	    	//}
        			//}else{
        			//	return weiXinService.registerOrBind(fromUserName, toUserName,memberToken);
        			//}
        			
        		}else if(content.startsWith("yrhk")){
        			int pageSize=0;
        			String temp[]=content.split("_");
        			String param=temp[1];
        			if(param!=null){
        				pageSize=Integer.parseInt(param);
        				if(pageSize>30){
        					return weiXinService.responseTransferCustomerService(fromUserName, toUserName);
        				}
        				return weiXinService.queryInvestAmountList(fromUserName, toUserName,pageSize);
        			}
        		}else{
        			return weiXinService.responseTransferCustomerService(fromUserName, toUserName);
        		}
        		
        	}
        } else if(msgType.equals("image")) {
        	return weiXinService.responseTransferCustomerService(fromUserName, toUserName);
        } else {
            String url2;
            if(msgType.equals("voice")) {
            	return weiXinService.responseTransferCustomerService(fromUserName, toUserName);
            } else if(msgType.equals("video")) {
            	return weiXinService.responseTransferCustomerService(fromUserName, toUserName);
            } else if(msgType.equals("location")) {
            	return weiXinService.responseTransferCustomerService(fromUserName, toUserName);
            } else if(msgType.equals("link")) {
            	return weiXinService.responseTransferCustomerService(fromUserName, toUserName);
            }
        }

        result ="" ;
        return result;
    }

    /**
     * 账号绑定     *
     * @param fromUserName
     * @param toUserName
     * @return
     */
    private String registerOrBind(String fromUserName, String toUserName,Member member) {

        TextMsg msg = new TextMsg();
        if (member==null){
            msg.add(" 抱歉，您尚未绑定微信服务号！ ");
            msg.addln();
            msg.add(" 已有有融网账户，请<a href='"+ROOT_URL_WEB+"/security/mLogin/?openId="+fromUserName+"'>点击这里</a>绑定。");
            msg.addln();
            msg.add(" 新用户请<a href='"+ROOT_URL_WEB+"/security/mRegister/?openId="+fromUserName+"'>点击这里</a>注册并绑定");
        }else {
            String name = StringUtil.isNotBlank(member.getUsername())?member.getUsername():StringUtil.maskMobile(member.getMobile());
            msg.add("亲爱的").add(name).add(", 您已经成功绑定微信服务。\n\t点击左下角键盘回复文本【解绑】可解除账户绑定。");
            msg.addln();
        }
        msg.setFromUserName( toUserName);
        msg.setToUserName(fromUserName);
        return msg.toXml();
    }

    /**
     * 我的资产
     * @return
     */
    private String myBalance(Long memberId,String fromUserName, String toUserName){
    	if(memberService.isAuthIdentityAndPhone(memberId)){
    		return weiXinService.queryMemberBalance(memberId, fromUserName, toUserName);
    	}else{
    		return authIdentityMsg(fromUserName, toUserName);
    	}
    }

    /**
     * 我的项目
     * @return
     */
    private String myProject(Long memberId,String fromUserName, String toUserName){
    	if(memberService.isAuthIdentityAndPhone(memberId)){
    		 return weiXinService.queryMemberTransaction(memberId, fromUserName, toUserName);
     	}else{
     		return authIdentityMsg(fromUserName, toUserName);
     	}
    }

    /**
     * 我的优惠
     * @return
     */
    private String myPreferential(Long memberId,String fromUserName, String toUserName){
		 return weiXinService.queryMemberCoupon(memberId, fromUserName, toUserName);
    }
    
    /**
     * 我的提现
     * @param memberId
     * @param fromUserName
     * @param toUserName
     * @return
     */
    private String queryMemberWithDraw(Long memberId, String fromUserName, String toUserName){
    	if(memberService.isAuthIdentityAndPhone(memberId)){
   		 return weiXinService.queryMemberWithDraw(memberId,fromUserName,toUserName);
    	}else{
    		return authIdentityMsg(fromUserName, toUserName);
    	}
    }
    
    /**
     * 未实名验证提示
     * @param fromUserName
     * @param toUserName
     * @return
     */
    private String authIdentityMsg(String fromUserName, String toUserName){
    	TextMsg msg = new TextMsg();
    	msg.add("您尚未开通新浪支付托管账户存钱罐功能，请登录网站开通。");
    	msg.setFromUserName( toUserName);
        msg.setToUserName(fromUserName);
        return msg.toXml();
    }

    /**
     * 关注提示语
     * @param fromUserName
     * @param toUserName
     * @return
     */
    private String followTips(String fromUserName, String toUserName){
    	TextMsg msg = new TextMsg();
    	msg.add("您好，欢迎关注有融网微信服务号！");
    	msg.addln();
    	msg.add("愿您每一天都早安/午安/晚安！");
    	msg.addln();
    	msg.add("<a href='http://eqxiu.com/s/SGPZcFTG'>关于有融</a>");
    	msg.setFromUserName( toUserName);
        msg.setToUserName(fromUserName);
    	return msg.toXml();
    }

    /**
     * 点击事件处理
     * @param eventKey
     * @param fromUserName
     * @param toUserName
     * @return
     */
    private String clientEvent(String eventKey, String fromUserName, String toUserName){
    	String content = "";
        MemberToken memberToken = tokenService.selectByWeixinID(fromUserName);
        Member member = null;
        String trueName ="";
        if (memberToken==null){
            content =  weiXinService.registerOrBind(fromUserName,toUserName,memberToken);
            return content;
        }
        Long memberId = memberToken.getMemberId();
    	switch(eventKey){
    		case "register_bind":
                content =  weiXinService.registerOrBind(fromUserName,toUserName,memberToken);
    			break;
    		case "my_balance":
    			content = myBalance(memberId,fromUserName, toUserName);
                break;
            case "my_projects":
    			content = myProject(memberId,fromUserName, toUserName);
    			break;
    		case "my_coupons":
    			content = myPreferential(memberId,fromUserName, toUserName);
    			break;
    		case "my_withdrawals":
                content =  queryMemberWithDraw(memberId,fromUserName,toUserName);
    			break;
    	}
    	return content;
    }

}
