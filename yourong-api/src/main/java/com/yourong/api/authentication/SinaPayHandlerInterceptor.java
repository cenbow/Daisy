package com.yourong.api.authentication;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.utils.SysServiceUtils;
import com.yourong.common.constant.Constant;
import com.yourong.common.enums.ResultCode;
import com.yourong.core.sys.model.SysDict;

public class SinaPayHandlerInterceptor extends HandlerInterceptorAdapter {
	
	private static final String IS_HEAD_OFF_SINA_METHOD = "app_is_head_off_sina_method";
    private static final String HEAD_OFF_METHOD = "head_off_method";

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		HandlerMethod method = (HandlerMethod)handler;
		String requestName = method.getMethod().getName();
		if(checkRequestMethod(requestName)){
			SysDict sysDict = SysServiceUtils.getSysDictByKey(HEAD_OFF_METHOD, IS_HEAD_OFF_SINA_METHOD);
			if(sysDict != null && sysDict.getValue().equals("Y")){
				ResultDTO<Object> resultDto = new ResultDTO<Object>();
				ResultCode resultCode = ResultCode.getResultCodeByCode("-101");
				resultCode.setMsg(sysDict.getDescription());
				resultDto.setResultCode(resultCode);
	            String jsonString = JSON.toJSONString(resultDto);
	            response.setCharacterEncoding("UTF-8");
	            response.setContentType("application/json; charset=utf-8");
	            ServletOutputStream outputStream = response.getOutputStream();
	            outputStream.write(jsonString.getBytes(Constant.DEFAULT_CHARSET));
	            return false;
			}
		}
		return super.preHandle(request, response, handler);
	}

	
	/**
	 * 需要拦截的方式
	 * saveMember 注册会员
	   authIdentity 实名验证
	   bingQuickBankCard 绑定银行卡 ,向第三方支付发送绑定银行卡请求
	   checkCodeAndSaveBankCard 绑定银行卡  , 发送验证码 到第三方支付 验证
	   saveBankCard  添加银行卡
	   deleteBankCardByID 解绑银行卡
	   rechargeOnBankCard 会员充值.绑卡充值 1,发送请求到第三方支付
	   rechargeOnBankCardCheck  会员充值.绑卡充值 2,用户验证码和ticket
	   withdrawSubmit 提现
	   PayOrderOnAmount 余额支付
	   PayOrderOnQuickPayment 绑卡支付
	   createOrder 创建订单
	 * @param name
	 * @return
	 */
	private boolean checkRequestMethod(String name){
		boolean checkFlag = false;
		String headOffMethod[] = {"saveMember","authIdentity","bingQuickBankCard","checkCodeAndSaveBankCard","saveBankCard","deleteBankCardByID"
				,"rechargeOnBankCard","rechargeOnBankCardCheck","withdrawSubmit","PayOrderOnAmount","PayOrderOnQuickPayment", "createOrder"};
		for(String m : headOffMethod){
			if(m.indexOf(name) >= 0){//这里不要用equals,因考虑多个版本方法命名问题
				checkFlag = true;
				break;
			}
		}
		return checkFlag;
	}
}
