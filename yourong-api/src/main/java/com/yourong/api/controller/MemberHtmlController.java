package com.yourong.api.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.HtmlTokenService;
import com.yourong.common.util.AES;
import com.yourong.common.util.Identities;
import com.yourong.core.uc.model.MemberHtmlToken;

@Controller
@RequestMapping("security/memberHtml")
public class MemberHtmlController extends BaseController {
	
	@Autowired
	private HtmlTokenService htmlTokenService;
	
	/**
	 *  html登陆,app端通过token换取htmlToken
	 * @param form
	 * @param result
	 * @param request
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "getHtmlToken",method = RequestMethod.POST,headers = {"Accept-Version=1.0.0"})
	@ResponseBody
	public ResultDTO<Object> logining(HttpServletRequest req, HttpServletResponse resp) {
		ResultDTO<Object> resultRe = new ResultDTO<Object>();

		//根据app端加密token,获取用户id
		Long memberId = getMemberID(req);
		String encodeBase64String = generateHtmlTokenAndSave(memberId);
		/*
			String encodeBase64String = generateEncryTokenAndSaveToken(id, form.getDevice(), form.getEquipment(), form.getLoginSource(),form.getChannelId());
			//登陆成功后，清空登陆次数
			RedisMemberClient.removeMemberLoginCount(form.getUsername());
			// 登录成功后，清空短信发送次数
            RedisMemberClient.removeMemberMobileSMSCount(resultDO.getResult().getMobile());
            RedisMemberClient.removeMemberMobileVoiceCount(resultDO.getResult().getMobile());
			resultRe.setIsSuccess();
			Map map = Maps.newHashMap();
			Member member = resultDO.getResult();
			if(StringUtil.isNotBlank(member.getAvatars())){
				member.setAvatars(PropertiesUtil.getAliyunUrl()+"/"+member.getAvatars());
			}
			MemberDto memberDto= BeanCopyUtil.map(member, MemberDto.class);
		    map.put("token",encodeBase64String);
			map.put("member",memberDto);
			map.put("title","有融网");
			map.put("shareContent","我正在用有融网APP投资理财，快和我一起来吧！");
			map.put("shareUrl",PropertiesUtil.getMstationRootUrl()+"/mstation/landing/inviteRegister?inviteCode_shortURL="+memberDto.getShortUrl());
			SPParameter  parameter =new SPParameter();
			parameter.setMemberId(member.getId());
			SPEngine.getSPEngineInstance().run(parameter);*/
		// 前台 根据登陆次数显示验证码
		//resultRe.setResult(count);
		resultRe.setResult(encodeBase64String);
		return resultRe;
	}
	
	
	/**
	 * 生成Token,并且保存到数据库
	 * @param id
	 * @param device
	 * @param equipment
	 * @param type
	 * @return
	 */
	private String generateHtmlTokenAndSave(Long memberId){
		String token = Identities.uuid2();
		MemberHtmlToken memberHtmlToken = new MemberHtmlToken();
		memberHtmlToken.setMemberId(memberId);
		memberHtmlToken.setToken(token);
		//逻辑删除旧htmltoken
		htmlTokenService.deleteHtmlMemberTokenByMemberID(memberId);
		//插入新htmltoken
		htmlTokenService.insertSelective(memberHtmlToken);
		
		//用户id+token令牌+时间，生成唯一token
		String encodeBase64String =AES.getInstance().encryptToken(memberId,token);
		return encodeBase64String;
	}
	

}
