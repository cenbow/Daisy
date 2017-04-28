package com.yourong.common.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 绿狗证据托管
 * 
 * @author wangyanji
 *
 */
public class LvGouUtil {
	/**
	 * 日志对象
	 */
	private static Logger logger = LoggerFactory.getLogger(LvGouUtil.class);

	/**
	 * 项目信息推送(电子协议信息，用户信息，投资项目信息)
	 * 
	 * @param name 投资人投资的项目名称
	 * @param no 项目编号
	 * @param users 投资该项目的所有投资人用户名和身份证后六位
	 * @param contract 投资人投资该项目的电子合同pdf一份。(模拟文件上传)
	 * @param contractno 电子合同编号
	 * @param contractname 电子合同名称
	 * @param endtime 过期日期(时间戳,单位秒)
	 * @param financing 融资金额(单位元)
	 * @param duration 融资期限(单位月)
	 * @return 0是成功，1是重复，2是失败
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String contractAdd(String name, String no, String users,
			String contract, String contractno, String contractname,
			int endtime, String financing, String duration) {

		String url = PropertiesUtil.getProperties("lvgou.contractAddUrl");
				
		Map paraMap = new HashMap();
		paraMap.put("name", name);
		paraMap.put("no", no);
		paraMap.put("users", users);
		paraMap.put("contractno", contractno);
		paraMap.put("contractname", contractname);
		paraMap.put("endtime", endtime);
		paraMap.put("financing", financing);
		paraMap.put("duration", duration);
		logger.debug("绿狗推送项目参数：url={},name={},no={},users={},contractno={},contractname={},endtime={},financing={},duration={}", url, name, no, users, contractno, contractname, endtime, financing, duration);
		try {
			Map finalMap = encrypt(paraMap);
			return HttpUtil.doPostFile(url, finalMap, "contract", contract);
		} catch (Exception e) {
			logger.error("项目信息推送失败!, contractno = " + contractno, e);
			return "2";
		}
	}

	/**
	 * 转让协议推送
	 * 
	 * @param no 项目编号
	 * @param protocolname 协议名称
	 * @param protocolno 协议编号
	 * @param contract 投资人投资该项目的电子合同pdf一份。(模拟文件上传)
	 * @param from 转让人
	 * @param to 接收人(用英文冒号分割用户名和用户idcard如test1:888888)
	 * @param endtime 过期日期(时间戳,单位秒如1406858984)
	 * @param start 协议转让发起人
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String agreementAdd(String no, String protocolname, String protocolno,
			String contract, String from, String to, int endtime, String start) {
		
		String url = PropertiesUtil.getProperties("lvgou.agreementAdd");
		
		Map paraMap = new HashMap();
		paraMap.put("no", no);
		paraMap.put("protocolname", protocolname);
		paraMap.put("protocolno", protocolno);
		paraMap.put("contract", contract);
		paraMap.put("from", from);
		paraMap.put("to", to);
		paraMap.put("endtime", endtime);
		paraMap.put("start", start);
		try {
			Map finalMap = encrypt(paraMap);
			return HttpUtil.doPost(url, finalMap);
		} catch (UnsupportedEncodingException e) {
			logger.error("转让协议推送失败!, protocolno = " + protocolno, e);
			return "2";
		}
	}

	/**
	 * 项目状态推送(坏账或者逾期)
	 * 
	 * @param no 项目编号
	 * @param status 状态0=>无坏账或无逾期,1=>是坏账或逾期
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String projectstatusUpdate(String no, String status) {

		String url = PropertiesUtil.getProperties("lvgou.projectstatusUpdate");
		
		Map paraMap = new HashMap();
		paraMap.put("no", no);
		paraMap.put("status", status);
		try {
			Map finalMap = encrypt(paraMap);
			return HttpUtil.doPost(url, finalMap);
		} catch (UnsupportedEncodingException e) {
			logger.error("项目状态推送失败!, no = " + no, e);
			return "2";
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map encrypt(Map params) throws UnsupportedEncodingException {
		String signKey =  PropertiesUtil.getProperties("lvgou.signKey");
		String partnerid = PropertiesUtil.getProperties("lvgou.partnerid");
		TreeMap sortedMap = new TreeMap(params);
		sortedMap.put("partnerid", partnerid);
		Set<Map.Entry<String, Object>> mapSet = sortedMap.entrySet();

		String signStr = "";
		for (Map.Entry<String, Object> param : mapSet) {
			String key = param.getKey().trim();
			String val = String.valueOf(param.getValue()).trim();
			signStr += (key + "=" + val + "&");
			if ("partnerid".equals(key))
				continue;
			String value = authcode(val);
			param.setValue(value);
		}

		signStr = signStr.substring(0, signStr.length() - 1);
		String sign = DigestUtils.md5Hex(
				(signStr + signKey).getBytes("UTF-8")).toLowerCase();
		sortedMap.put("sign", sign);
		return sortedMap;
	}

	@SuppressWarnings("unused")
	private String authcode(String value) throws UnsupportedEncodingException {
		String encryptKey = PropertiesUtil.getProperties("lvgou.encryptKey");
		if (encryptKey == null || "".equals(encryptKey.trim())) {
			return "";
		}
		String key = DigestUtils.md5Hex(encryptKey.getBytes("UTF-8"))
				.toLowerCase();
		int key_length = key.length();

		String string = DigestUtils.md5Hex((value + key).getBytes("UTF-8"))
				.toLowerCase().substring(0, 8)
				+ value;
		byte[] bytes = string.getBytes("UTF-8");
		int string_length = bytes.length;
		int[] rndkey = new int[256];
		int[] box = new int[256];

		byte[] result = new byte[string_length];

		for (int i = 0; i <= 255; i++) {
			rndkey[i] = key.codePointAt(i % key_length);
			box[i] = i;
		}
		for (int i = 0, j = 0; i < 256; i++) {
			j = (j + box[i] + rndkey[i]) % 256;
			int tmp = box[i];
			box[i] = box[j];
			box[j] = tmp;
			int a = 0;
			for (i = 0, j = 0; i < string_length; i++) {
				a = (a + 1) % 256;
				j = (j + box[a]) % 256;
				tmp = box[a];
				box[a] = box[j];
				box[j] = tmp;
				byte ch = (byte) (bytes[i] ^ (box[(box[a] + box[j]) % 256]));
				result[i] = ch;
			}
			String tmpStr = new String(Base64.encodeBase64(result), "UTF-8")
					.replace("=", "");
			return tmpStr;
		}
		return null;
	}
}
