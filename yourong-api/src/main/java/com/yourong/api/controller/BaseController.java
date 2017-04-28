package com.yourong.api.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.api.dto.MemberSessionDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.utils.ServletUtil;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.DynamicParamBuilder;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.AES;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.StringUtil;
import com.yourong.core.uc.model.Member;

/**
 * controller基类
 * Created by py on 2015/3/16.
 */
abstract class BaseController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String MEMBER_ID = "api_memberID";
    private static final String REQUEST_SOURCE = "request_source";

    /**
     * 统一异常处理
     *
     * @param request
     * @param ex
     * @return author: pengyong	 *
     */
    @ExceptionHandler
    public String exp(HttpServletRequest request, Exception ex) {
        logger.error("页面请求异常：URL" + request.getRequestURI(), ex);
        request.setAttribute("exceptions", ex);
        return "/500";
    }

    protected ResultDTO<?> validateResult(ResultDTO<?> resultObject, BindingResult result) {
        int errorCount = result.getErrorCount();
        if (errorCount > 0) {
            resultObject.setResultCodeList(formatListObjectErrorType(result.getAllErrors()));
            resultObject.setIsError();
        } else {
            resultObject.setIsSuccess();
        }
        return resultObject;
    }

    protected void converterResultDTO(ResultDTO<Object> result, ResultDO<Object> resultDO) {
        if (resultDO.isError()) {
            result.setResultCodeList(resultDO.getResultCodeList());
        } else {
            result.setResult(resultDO.getResult());
        }
    }

    protected Long getMemberID(HttpServletRequest request) {
        Long memberID =  (Long) request.getAttribute(MEMBER_ID);//APP获取ID
        if(memberID == null){//M站获取ID
            MemberSessionDto member = getMember();
            memberID = member == null ? 0L : member.getId();
         }
         return memberID;
    }
    
    
    protected Long getMemberIdAppToHtml(HttpServletRequest req){
    	String isMsite = req.getParameter("isMsite");
		if (StringUtil.isNotBlank(isMsite)) {
			if("Y".equals(isMsite)){
				String encryptionId = req.getParameter("encryptionId");
				if (StringUtil.isBlank(encryptionId)) {
					return 0L;
				}
				return Long.valueOf(encryptionId);
			}	
		}
    	String isNeedYRWtoken = req.getParameter("isNeedYRWtoken");
		if (StringUtil.isBlank(isNeedYRWtoken)) {
			return 0L;
		}
		String encryptionId = req.getParameter("encryptionId");
		if (StringUtil.isBlank(encryptionId)) {
			return 0L;
		}
		List<String> encryptionCode = AES.getInstance().tokenDecrypt(encryptionId);
		if (Collections3.isEmpty(encryptionCode)) {
			return 0L;
		} else {
			Long memberId = Long.valueOf(encryptionCode.get(0));
			return memberId;
		}
    }

    protected List<ResultCode> formatListObjectErrorType(List<ObjectError> erros) {
        List<ResultCode> resultCodeList = Lists.newArrayList();
        for (ObjectError error : erros) {
            ResultCode resultCode = ResultCode.getResultCodeByCode(error.getDefaultMessage());
            if (resultCode != null) {
                if (!resultCodeList.contains(resultCode)) {
                    resultCodeList.add(resultCode);
                }
            }else{
                resultCodeList.add(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
            }
        }
        return resultCodeList;
    }

    /**
     * 请求来源
     *
     * @param request
     * @return
     */
    protected Integer getRequestSource(HttpServletRequest request) {
        Integer rid = (Integer) request.getAttribute(REQUEST_SOURCE);
        if (rid == null) {
            rid = -1;
        }
        return rid;
    }
    
    public void render(HttpServletResponse response,String contentType, String content) {
		try {
			String encoding = "UTF-8";
			String fullContentType = contentType + ";charset=" + encoding;
			response.setContentType(fullContentType);
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			response.getWriter().write(content);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void renderJson(HttpServletResponse response, String jsonString) {
		render(response,"application/json", jsonString);
	}
	
	protected HttpSession getHttpSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			session = request.getSession();
		}
		return session;
	}
	
	protected void setResponseHeaders(HttpServletResponse response) {
		response.setContentType("image/png");
		response.setHeader("Cache-Control", "no-cache, no-store");
		response.setHeader("Pragma", "no-cache");
		long time = System.currentTimeMillis();
		response.setDateHeader("Last-Modified", time);
		response.setDateHeader("Date", time);
		response.setDateHeader("Expires", time);
	}
	
	/**
	 * 从session获取member
	 */
	protected MemberSessionDto getMember() {
		return ServletUtil.getUserDO();
	}
	
	/**
	 * 更新登录会员信息
	 * @param request
	 * @param memberSession
	 */
	protected void updateMemberSessionDto(HttpServletRequest request,MemberSessionDto memberSession){
		HttpSession session = request.getSession(false);
		if (session == null) {
			session = request.getSession();
		}
		session.removeAttribute(Constant.CURRENT_USER);
		session.setAttribute(Constant.CURRENT_USER, memberSession);
	}

	/**
	 * 构造参数map，格式为“args_paraName_1_string_value”才能放入map
	 * 
	 * @param req
	 * @return
	 */
	protected DynamicParamBuilder buildArgs2(HttpServletRequest req) {
		List valueList = Lists.newArrayList();
		// 初始化memberId
		Long memberId = 0l;
		String loginSource = req.getParameter("loginSource");
		if (String.valueOf(TypeEnum.MEMBER_LOGIN_SOURCE_ANDROID.getType()).equals(loginSource)) {
			String token = req.getParameter("token");
			List<String> encryptionCode = AES.getInstance().tokenDecrypt(token);
			if (Collections3.isNotEmpty(encryptionCode) && StringUtil.isNumeric(encryptionCode.get(0))) {
				memberId = Long.valueOf(encryptionCode.get(0));
			}
		} else if (String.valueOf(TypeEnum.MEMBER_LOGIN_SOURCE_IOS.getType()).equals(loginSource)) {
			String mKey = req.getParameter("mKey");
			if (StringUtil.isNotBlank(mKey)) {
				memberId = Long.valueOf(mKey);
			}
		} else {
			memberId = getMember() != null ? getMember().getId() : 0l;
		}
		// 构建参数
		DynamicParamBuilder paramBuilder = new DynamicParamBuilder();
		if (memberId > 0l) {
			paramBuilder.setMemberId(memberId);
		}
		Map<String, Object> kayAndValueMap = Maps.newHashMap();
		paramBuilder.setParamValueList(valueList);
		paramBuilder.setParamMap(kayAndValueMap);
		// 没有其他入参则直接返回
		Map<String, String[]> parameterMap = req.getParameterMap();
		if (MapUtils.isEmpty(parameterMap)) {
			return paramBuilder;
		}
		String[] invokeParameters = parameterMap.get("invokeParameters");
		if (invokeParameters == null || invokeParameters.length < 1) {
			return paramBuilder;
		}
		// 解析...
		Map<Integer, Object> valueMap = Maps.newTreeMap();
		String[] parameters = new String[] {};
		parameters = invokeParameters[0].split("&");
		for (String singleParameter : parameters) {
			if (!singleParameter.startsWith("args")) {
				continue;
			}
			String[] temp = singleParameter.split("_", 5);
			if (temp == null || temp.length != 5) {
				continue;
			}
			// 暂时不支持数组
			Object o = paramBuilder.conventStringToTargetClass(temp[3], temp[4]);
			valueMap.put(Integer.valueOf(temp[2]), o);
			kayAndValueMap.put(temp[1], o);
		}
		valueList.addAll(paramBuilder.buildObjects(valueMap));
		return paramBuilder;
	}

	/**
	 * 构造参数map，格式为“args_paraName_1_string_value”才能放入map
	 * 
	 * @param req
	 * @return
	 */
	protected DynamicParamBuilder buildArgs(HttpServletRequest req) {
		List valueList = Lists.newArrayList();
		// 初始化memberId
		Long memberId = 0l;
		String loginSource = req.getParameter("loginSource");
		if (String.valueOf(TypeEnum.MEMBER_LOGIN_SOURCE_ANDROID.getType()).equals(loginSource)
				|| String.valueOf(TypeEnum.MEMBER_LOGIN_SOURCE_IOS.getType()).equals(loginSource)) {
			String token = req.getParameter("token");
			List<String> encryptionCode = AES.getInstance().tokenDecrypt(token);
			if (Collections3.isNotEmpty(encryptionCode) && StringUtil.isNumeric(encryptionCode.get(0))) {
				memberId = Long.valueOf(encryptionCode.get(0));
			}
		} else {
			memberId = getMember() != null ? getMember().getId() : 0l;
		}
		// 构建参数
		DynamicParamBuilder paramBuilder = new DynamicParamBuilder();
		if (memberId > 0l) {
			paramBuilder.setMemberId(memberId);
		}
		Map<String, Object> kayAndValueMap = Maps.newHashMap();
		paramBuilder.setParamValueList(valueList);
		paramBuilder.setParamMap(kayAndValueMap);
		// 没有其他入参则直接返回
		Map<String, String[]> parameterMap = req.getParameterMap();
		if (MapUtils.isEmpty(parameterMap)) {
			return paramBuilder;
		}
		String[] invokeParameters = parameterMap.get("invokeParameters");
		if (invokeParameters == null || invokeParameters.length < 1) {
			return paramBuilder;
		}
		// 解析...
		Map<Integer, Object> valueMap = Maps.newTreeMap();
		String[] parameters = new String[] {};
		parameters = invokeParameters[0].split("&");
		for (String singleParameter : parameters) {
			if (!singleParameter.startsWith("args")) {
				continue;
			}
			String[] temp = singleParameter.split("_", 5);
			if (temp == null || temp.length != 5) {
				continue;
			}
			// 暂时不支持数组
			Object o = paramBuilder.conventStringToTargetClass(temp[3], temp[4]);
			valueMap.put(Integer.valueOf(temp[2]), o);
			kayAndValueMap.put(temp[1], o);
		}
		valueList.addAll(paramBuilder.buildObjects(valueMap));
		return paramBuilder;
	}
	
	protected DynamicParamBuilder buildArgs3(HttpServletRequest req) throws ServletRequestBindingException {
		String method = ServletRequestUtils.getStringParameter(req, "invokeMethod");
		List valueList = Lists.newArrayList();
		// 初始化memberId
		Long memberId = 0l;
		String loginSource = req.getParameter("loginSource");
		if (String.valueOf(TypeEnum.MEMBER_LOGIN_SOURCE_ANDROID.getType()).equals(loginSource)
				|| String.valueOf(TypeEnum.MEMBER_LOGIN_SOURCE_IOS.getType()).equals(loginSource)) {
			String token = req.getParameter("token");
			List<String> encryptionCode = AES.getInstance().tokenDecrypt(token);
			if (Collections3.isNotEmpty(encryptionCode) && StringUtil.isNumeric(encryptionCode.get(0))) {
				memberId = Long.valueOf(encryptionCode.get(0));
			}
		} else {
			memberId = getMember() != null ? getMember().getId() : 0l;
		}
		// 构建参数
		DynamicParamBuilder paramBuilder = new DynamicParamBuilder();
		if (memberId > 0l) {
			paramBuilder.setMemberId(memberId);
		}
		Map<String, Object> kayAndValueMap = Maps.newHashMap();
		paramBuilder.setParamValueList(valueList);
		paramBuilder.setParamMap(kayAndValueMap);
		// 没有其他入参则直接返回
		Map<String, String[]> parameterMap = req.getParameterMap();
		if (MapUtils.isEmpty(parameterMap)) {
			return paramBuilder;
		}
		String[] invokeParameters = parameterMap.get("invokeParameters");
		if (invokeParameters == null || invokeParameters.length < 1) {
			return paramBuilder;
		}
		// 解析...
		Map<Integer, Object> valueMap = Maps.newTreeMap();
		String[] parameters = new String[] {};
		if("setOlympic".equals(method)){
			parameters = invokeParameters[0].split("&");
		}else{
			parameters = invokeParameters[0].split(",");
		}
		for(int i=0;i<parameters.length;i++){
			if (!parameters[i].startsWith("args")) {
				continue;
			}
			String[] temp = parameters[i].split("_", 5);
			if (temp == null || temp.length != 5) {
				continue;
			}
			// 暂时不支持数组
			Object o = paramBuilder.conventStringToTargetClass(temp[3], temp[4]);
			valueMap.put(Integer.valueOf(temp[2]), o);
			kayAndValueMap.put(temp[1], o);
		}
		/*for (String singleParameter : parameters) {
			if (!singleParameter.startsWith("args")) {
				continue;
			}
			String[] temp = singleParameter.split("_", 5);
			if (temp == null || temp.length != 5) {
				continue;
			}
			// 暂时不支持数组
			Object o = paramBuilder.conventStringToTargetClass(temp[3], temp[4]);
			valueMap.put(Integer.valueOf(temp[2]), o);
			kayAndValueMap.put(temp[1], o);
		}*/
		valueList.addAll(paramBuilder.buildObjects(valueMap));
		return paramBuilder;
	}

	/**
	 * 
	 * @Description:获取真实IP
	 * @param request
	 * @return
	 * @author: wangyanji
	 * @time:2016年3月28日 上午10:15:41
	 */
	protected String getIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (StringUtil.isBlank(ip)) {
			ip = request.getRemoteAddr();
		}
		// 如果是负载均衡，取第一个IP
		if (StringUtil.isNotBlank(ip) && ip.indexOf(",") > -1) {
			String[] arr = ip.split(",");
			ip = arr[0];
		}
		return ip;
	}
	
	protected Optional<Member> encryptionMemberFromAppC(HttpServletRequest req, ModelAndView model) {
		try {
			String loginSource = req.getHeader("loginSource");
			if (StringUtil.isBlank(loginSource)) {
				loginSource = "3";
				model.addObject("loginSource", loginSource);
				MemberSessionDto memberDto = getMember();
				if (memberDto == null) {
					return Optional.fromNullable(null);
				}
				Long memberId = memberDto.getId();
				Member member = ServletUtil.getMemberInfoByMemberId(memberId);
				return Optional.fromNullable(member);
			} else {
				model.addObject("loginSource", loginSource);
			}
			String isNeedYRWtoken = req.getParameter("isNeedYRWtoken");
			if (StringUtil.isBlank(isNeedYRWtoken)) {
				return Optional.fromNullable(null);
			}
			String encryptionId = req.getParameter("encryptionId");
			if (StringUtil.isBlank(encryptionId)) {
				return Optional.fromNullable(null);
			}
			List<String> encryptionCode = AES.getInstance().tokenDecrypt(encryptionId);
			if (Collections3.isEmpty(encryptionCode)) {
				return Optional.fromNullable(null);
			} else {
				Long memberId = Long.valueOf(encryptionCode.get(0));
				Member member =ServletUtil.getMemberInfoByMemberId(memberId);
				if (member != null) {
					model.addObject("memberId", memberId);
				}
				return Optional.fromNullable(member);
			}
		} catch (Exception e) {
			return Optional.fromNullable(null);
		}
	}

	/**
	 * 
	 * @Description:判断本地是否存在vm文件，动态路由需要校验
	 * @param req
	 * @param prePath
	 * @param vmName
	 * @return
	 * @author: wangyanji
	 * @time:2016年7月7日 下午2:37:29
	 */
	protected boolean isExistsVM(HttpServletRequest req, String prePath, String vmName) {
		try {
			boolean existsFlag = false;
			String path = req.getSession().getServletContext().getRealPath("/WEB-INF") + "/vm/" + prePath + "/" + vmName + ".vm";
			File vmFile = new File(path);
			existsFlag = vmFile.exists();
			logger.info("判断本地是否存在vm文件:path={}, existsFlag={}", path, existsFlag);
			return existsFlag;
		} catch(Exception e) {
			logger.error("判断本地是否存在vm文件报错！vmName={}", vmName, e);
		}
		return false;
	}
}
