package com.yourong.api.controller;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rop.thirdparty.com.google.common.collect.Maps;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yourong.api.utils.APIPropertiesUtil;
import com.yourong.api.utils.SysServiceUtils;
import com.yourong.common.domain.OpenServiceResultDO;
import com.yourong.common.enums.OpenServiceResultCode;
import com.yourong.common.mongo.OpenServiceLog;
import com.yourong.common.thirdparty.sinapay.common.enums.CharsetType;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.CryptHelper;
import com.yourong.common.util.StringUtil;
import com.yourong.core.os.biz.FileBiz;

/**
 * 对外接口controller基类
 */
abstract class BaseServiceController {

	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(BaseServiceController.class);

	private final static String SIGN = "sign";

	private final static String SIGN_DICT_GROUP = "sign_key";

	private final static String ATTACHMENT_PREFIX = "attachment";

	private final static String ATTACHMENTS_PREFIX = "attachments";

	private final static String SIGN_SPLIT = "^";

	@SuppressWarnings("rawtypes")
	protected void preHandle(HttpServletRequest req, OpenServiceResultDO resultDO) {
		Map<String, String> params = null;
		try {
			String signKey = req.getHeader("Channel_Key");
			params = Collections3.transformRequestMap(req.getParameterMap());
			if (!validateSign(params)) {
				// 验证签名不通过
				logger.error("验证签名不通过, signKey={}", signKey);
				resultDO.setResultCodeByEnum(OpenServiceResultCode.CHECK_SIGN_ERROR);
				return;
			}
			// 解密
			String url = req.getRequestURL().toString();
			decryptPara(params, resultDO, signKey, url);
		} catch (Exception e) {
			logger.error("对外创建会员接口预处理报错 params={}", params, e);
			resultDO.setResultCodeByEnum(OpenServiceResultCode.SYSTEM_ERROR);
		}
	}

	/**
	 * 
	 * @Description:对外接口验签
	 * @param parameterMap
	 * @return
	 * @author: wangyanji
	 * @time:2016年11月2日 下午4:17:07
	 */
	private boolean validateSign(Map<String, String> params) {
		// 判断是否要aes解密
		if (!APIPropertiesUtil.getProperties("openServiceNeedDecrypt").equals("true")) {
			params.remove(SIGN);
			return true;
		}
		// 将request中参数转换成map
		logger.info("第三方回调入参：" + params);
		String sign = params.get(SIGN);
		if (StringUtil.isBlank(sign)) {
			return false;
		}
		params.remove(SIGN);
		String validDataAbstract = buildSign(params);
		if (CryptHelper.md5(validDataAbstract, CharsetType.UTF8.charset()).equals(sign)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @Description:入参转化成实体类
	 * @param params
	 * @param t
	 * @return
	 * @author: wangyanji
	 * @time:2016年11月3日 上午11:02:23
	 */
	private void decryptPara(Map<String, String> params, OpenServiceResultDO<?> resultDO, String signKey, String url) {
		if (resultDO.getResult() == null) {
			logger.error("获取实体类失败 ObjectClass={}, params={}", resultDO.getResult());
			resultDO.setResultCodeByEnum(OpenServiceResultCode.SYSTEM_ERROR);
			return;
		}
		Map<String, Object> decryptPara = null;
		// 先AES解密
		try {
			decryptPara = decryptByMap(params, signKey);
		} catch (Exception e) {
			logger.error("外部接口入参解密失败 params={}, decryptPara={}", params, decryptPara, e);
			resultDO.setResultCodeByEnum(OpenServiceResultCode.DECRYPT_ERROR);
			return;
		}
		try {
			// 放入渠道商
			decryptPara.put("channelKey", signKey);
			// 记录日志
			OpenServiceLog.openServicelog(url, signKey, params.get("outBizNo").toString(), decryptPara.toString());
			BeanUtils.populate(resultDO.getResult(), decryptPara);
		} catch (Exception e) {
			logger.error("入参解析失败 params={}, decryptPara={}", params, decryptPara, e);
			resultDO.setResultCodeByEnum(OpenServiceResultCode.INPUT_TRANSFER_ERROR);
			return;
		}
	}
    
	/**
	 * 
	 * @Description:生成需要签名的摘要
	 * @param params
	 * @return
	 * @author: wangyanji
	 * @time:2016年11月3日 上午10:37:11
	 */
	private String buildSign(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		StringBuffer prestr = new StringBuffer();
		String key = "";
		String value = "";
		for (int i = 0; i < keys.size(); i++) {
			key = (String) keys.get(i);
			value = (String) params.get(key);
			if ("null".equals(value) || StringUtil.isBlank(value) || key.equalsIgnoreCase(SIGN) || key.startsWith(ATTACHMENT_PREFIX)
					|| key.startsWith(ATTACHMENTS_PREFIX)) {
				continue;
			}
			prestr.append(value).append(SIGN_SPLIT);
		}
		if (prestr.length() < 1) {
			return "";
		}
		return prestr.deleteCharAt(prestr.length() - 1).toString();
	}

	/**
	 * 
	 * @Description:遍历Map，解密value，附件类入参不解密
	 * @param params
	 * @return
	 * @throws GeneralSecurityException
	 * @author: wangyanji
	 * @time:2016年11月3日 下午1:23:32
	 */
	private Map<String, Object> decryptByMap(Map<String, String> params, String signKey) throws Exception {
		// 判断是否要aes解密
		boolean needDecrypt = false;
		if (APIPropertiesUtil.getProperties("openServiceNeedDecrypt").equals("true")) {
			needDecrypt = true;
		}
		String signValue = SysServiceUtils.getDictValue(signKey, SIGN_DICT_GROUP, "");
		if (StringUtil.isBlank(signValue)) {
			logger.error("找不到签名秘钥 signValue={}", signValue);
			throw new GeneralSecurityException("私钥不存在");
		}
		Map<String, Object> decryptPara = Maps.newHashMap();
		for (String key : params.keySet()) {
			if ("null".equals(params.get(key)) || StringUtil.isBlank(params.get(key)) || key.equalsIgnoreCase(SIGN)) {
				continue;
			} else if (key.startsWith(ATTACHMENTS_PREFIX)) {
				// 解密附件数组地址
				List<FileBiz> fArrBiz = null;
				try {
					fArrBiz = JSON.parseArray(params.get(key), FileBiz.class);
				} catch (JSONException e) {
					logger.error("Json转化报错 key={} value={}", key, params.get(key));
					throw e;
				}
				for (FileBiz file : fArrBiz) {
					if (needDecrypt) {
						try {
							file.setFileUrl(CryptHelper.aesDecrypt(file.getFileUrl(), CharsetType.UTF8.charset(), signValue));
						} catch (Exception e) {
							logger.error("解密报错 key={} fileUrl={}", key, file.getFileUrl());
							throw e;
						}
					}
				}
				decryptPara.put(key, fArrBiz);
			} else if (key.startsWith(ATTACHMENT_PREFIX)) {
				// 解密附件地址
				FileBiz fBiz = JSON.parseObject(params.get(key), FileBiz.class);
				if (needDecrypt) {
					try {
						fBiz.setFileUrl(CryptHelper.aesDecrypt(fBiz.getFileUrl(), CharsetType.UTF8.charset(), signValue));
					} catch (Exception e) {
						logger.error("解密报错 key={} fileUrl={}", key, fBiz.getFileUrl());
						throw e;
					}
				}
				decryptPara.put(key, fBiz);
			} else {
				if (needDecrypt) {
					try {
						decryptPara.put(key, CryptHelper.aesDecrypt(params.get(key), CharsetType.UTF8.charset(), signValue));
					} catch (Exception e) {
						logger.error("解密报错 key={} value={}", key, params.get(key));
						throw e;
					}
				} else {
					decryptPara.put(key, params.get(key));
				}
			}
		}
		return decryptPara;
	}
}
