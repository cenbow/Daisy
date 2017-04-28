package com.yourong.core;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import rop.thirdparty.com.google.common.collect.Maps;

import com.google.common.collect.Lists;
import com.yourong.common.thirdparty.sinapay.common.enums.CharsetType;
import com.yourong.common.util.CryptHelper;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;

public class OpenServiceTest extends BaseTest {

	@Test
	public void openServiceTest() throws InterruptedException, ExecutionException, ParseException, IOException, GeneralSecurityException {

		final CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("http://m.yrw.com/openService/authMemberSaveProject");
		//HttpPost httpPost = new HttpPost("http://118.178.89.80/openService/authMemberSaveProject");
		//HttpPost httpPost = new HttpPost("http://60.191.67.98:10030/openService/authMemberSaveProject");
		httpPost.addHeader("Open-Version", "1.0.0");
		httpPost.addHeader("Channel_key", "jimistore");
		List<BasicNameValuePair> params = Lists.newArrayList();
		String dateline = DateUtils.formatDatetoString(DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND);
		params.add(new BasicNameValuePair("ip", CryptHelper.aesEncrypt("192.168.0.19", "jms20161111")));
		params.add(new BasicNameValuePair("mobile", CryptHelper.aesEncrypt("13575785566", "jms20161111")));
		params.add(new BasicNameValuePair("trueName", CryptHelper.aesEncrypt("支欣然", "jms20161111")));
		params.add(new BasicNameValuePair("identityNumber", CryptHelper.aesEncrypt("340304199209108380", "jms20161111")));
		params.add(new BasicNameValuePair("outBizNo", CryptHelper.aesEncrypt("jms" + dateline, "jms20161111")));
		params.add(new BasicNameValuePair("totalAmount", CryptHelper.aesEncrypt("6010.00", "jms20161111")));
		params.add(new BasicNameValuePair("borrowPeriod", CryptHelper.aesEncrypt("6", "jms20161111")));
		params.add(new BasicNameValuePair("borrowPeriodType", CryptHelper.aesEncrypt("2", "jms20161111")));
		params.add(new BasicNameValuePair("annualizedRate", CryptHelper.aesEncrypt("11.14", "jms20161111")));
		params.add(new BasicNameValuePair("attachmentThumbnail",
				"{fileSize:879394,fileUrl:\"" + CryptHelper.aesEncrypt("http://api.test.jimistore.com:8001/201611/e5a771a2-21ce-4cab-953f-548dd852f79d.jpg", "jms20161111") + "\",name:\"WOCa8qNgQL\",suffix:\".jpg\"}"));
		params.add(new BasicNameValuePair("attachmentImage",
				"{fileSize:879394,fileUrl:\"" + CryptHelper.aesEncrypt("http://api.test.jimistore.com:8001/201611/e5a771a2-21ce-4cab-953f-548dd852f79d.jpg", "jms20161111") + "\",name:\"WOCa8qNgQL\",suffix:\".jpg\"}"));
		params.add(new BasicNameValuePair("attachmentsPersonal", ""));
		params.add(new BasicNameValuePair(
				"attachmentsBorrower",
				"[{fileSize:879394,fileUrl:\"" + CryptHelper.aesEncrypt("http://api.test.jimistore.com:8001/201611/e5a771a2-21ce-4cab-953f-548dd852f79d.jpg", "jms20161111") + "\",name:\"WOCa8qNgQL\",suffix:\".jpg\"},{fileSize: 879394,fileUrl:\"" + CryptHelper.aesEncrypt("http://api.test.jimistore.com:8001/201611/e5a771a2-21ce-4cab-953f-548dd852f79d.jpg", "jms20161111") + "\",name:\"WOCa8qNgQL\",suffix:\".jpg\"}]"));
		params.add(new BasicNameValuePair(
				"attachmentsContract",
				"[{fileSize:879394,fileUrl:\"" + CryptHelper.aesEncrypt("http://api.test.jimistore.com:8001/201611/e5a771a2-21ce-4cab-953f-548dd852f79d.jpg", "jms20161111") + "\",name:\"WOCa8qNgQL\",suffix:\".jpg\"},{fileSize: 879394,fileUrl:\"" + CryptHelper.aesEncrypt("http://api.test.jimistore.com:8001/201611/e5a771a2-21ce-4cab-953f-548dd852f79d.jpg", "jms20161111") + "\",name:\"WOCa8qNgQL\",suffix:\".jpg\"}]"));
		params.add(new BasicNameValuePair("attachmentsLegal", ""));
		params.add(new BasicNameValuePair("attachmentsCredit", ""));
		params.add(new BasicNameValuePair("attachmentsOther", ""));
		String signData = buildSign(params);
		params.add(new BasicNameValuePair("sign", CryptHelper.md5(signData, CharsetType.UTF8.charset())));
		UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, "UTF-8");
		httpPost.setEntity(urlEncodedFormEntity);
		logger.info("Executing request..." + httpPost.getURI());
		CloseableHttpResponse response = httpclient.execute(httpPost);
		try {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				logger.info("--------------------------------------");
				logger.info("Response content: " + EntityUtils.toString(entity, "UTF-8"));
				logger.info("--------------------------------------");
			}
		} finally {
			response.close();
		}
	}
	
	/**
	 * 闪贷测试
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws ParseException
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	@Test
	public void openServiceTestShanDai() throws InterruptedException, ExecutionException, ParseException, IOException, GeneralSecurityException {
		final CloseableHttpClient httpclient = HttpClients.createDefault();
		//HttpPost httpPost = new HttpPost("http://60.191.67.98:10005/yourong-api/openService/authMemberSaveProject");
		//HttpPost httpPost = new HttpPost("http://192.168.0.195:8082/yourong-api/openService/authMemberSaveProject");
		HttpPost httpPost = new HttpPost("http://localhost:8082/yourong-api/openService/authMemberSaveProject");
		httpPost.addHeader("Open-Version", "1.0.0");
		httpPost.addHeader("Channel_key", "shandai");
		List<BasicNameValuePair> params = Lists.newArrayList();
		String dateline = DateUtils.formatDatetoString(DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND);
		params.add(new BasicNameValuePair("ip", CryptHelper.aesEncrypt("192.168.0.19", "jms20161102")));
		params.add(new BasicNameValuePair("mobile", CryptHelper.aesEncrypt("13575185569", "jms20161102")));
		params.add(new BasicNameValuePair("trueName", CryptHelper.aesEncrypt("谭宏旺", "jms20161102")));
		params.add(new BasicNameValuePair("identityNumber", CryptHelper.aesEncrypt("422823198511163376", "jms20161102")));
		params.add(new BasicNameValuePair("outBizNo", CryptHelper.aesEncrypt("jms" + dateline, "jms20161102")));
		params.add(new BasicNameValuePair("totalAmount", CryptHelper.aesEncrypt("5988.00", "jms20161102")));
		params.add(new BasicNameValuePair("borrowPeriod", CryptHelper.aesEncrypt("6", "jms20161102")));
		params.add(new BasicNameValuePair("borrowPeriodType", CryptHelper.aesEncrypt("2", "jms20161102")));
		params.add(new BasicNameValuePair("annualizedRate", CryptHelper.aesEncrypt("10.18", "jms20161102")));
		params.add(new BasicNameValuePair("attachmentThumbnail",
				"{fileSize:879394,fileUrl:\"" + CryptHelper.aesEncrypt("http://api.test.jimistore.com:8001/201611/e5a771a2-21ce-4cab-953f-548dd852f79d.jpg", "jms20161102") + "\",name:\"WOCa8qNgQL\",suffix:\".jpg\"}"));
		params.add(new BasicNameValuePair("attachmentImage",
				"{fileSize:879394,fileUrl:\"" + CryptHelper.aesEncrypt("http://api.test.jimistore.com:8001/201611/e5a771a2-21ce-4cab-953f-548dd852f79d.jpg", "jms20161102") + "\",name:\"WOCa8qNgQL\",suffix:\".jpg\"}"));
		params.add(new BasicNameValuePair("attachmentsPersonal", ""));
		params.add(new BasicNameValuePair(
				"attachmentsBorrower",
				"[{fileSize:879394,fileUrl:\"" + CryptHelper.aesEncrypt("http://api.test.jimistore.com:8001/201611/e5a771a2-21ce-4cab-953f-548dd852f79d.jpg", "jms20161102") + "\",name:\"WOCa8qNgQL\",suffix:\".jpg\"},{fileSize: 879394,fileUrl:\"" + CryptHelper.aesEncrypt("http://api.test.jimistore.com:8001/201611/e5a771a2-21ce-4cab-953f-548dd852f79d.jpg", "jms20161102") + "\",name:\"WOCa8qNgQL\",suffix:\".jpg\"}]"));
		params.add(new BasicNameValuePair(
				"attachmentsContract",
				"[{fileSize:879394,fileUrl:\"" + CryptHelper.aesEncrypt("http://api.test.jimistore.com:8001/201611/e5a771a2-21ce-4cab-953f-548dd852f79d.jpg", "jms20161102") + "\",name:\"WOCa8qNgQL\",suffix:\".jpg\"},{fileSize: 879394,fileUrl:\"" + CryptHelper.aesEncrypt("http://api.test.jimistore.com:8001/201611/e5a771a2-21ce-4cab-953f-548dd852f79d.jpg", "jms20161102") + "\",name:\"WOCa8qNgQL\",suffix:\".jpg\"}]"));
		params.add(new BasicNameValuePair("attachmentsLegal", ""));
		params.add(new BasicNameValuePair("attachmentsCredit", ""));
		params.add(new BasicNameValuePair("attachmentsOther", ""));
		String signData = buildSign(params);
		params.add(new BasicNameValuePair("sign", CryptHelper.md5(signData, CharsetType.UTF8.charset())));
		UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, "UTF-8");
		httpPost.setEntity(urlEncodedFormEntity);
		logger.info("Executing request..." + httpPost.getURI());
		System.out.println();
		CloseableHttpResponse response = httpclient.execute(httpPost);
		try {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				logger.info("Response content: " + EntityUtils.toString(entity, "UTF-8"));
			}
		} finally {
			response.close();
		}
	}
	
	

	// @Test
	public void openServiceTest1() throws InterruptedException, ExecutionException, ParseException, IOException, GeneralSecurityException {

		final CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("http://127.0.0.1:8081/yourong-api/openService/authMemberSaveProject");
		//HttpPost httpPost = new HttpPost("http://60.191.67.98:10030/openService/authMemberSaveProject");
		httpPost.addHeader("Open-Version", "1.0.0");
		httpPost.addHeader("Channel_key", "jimistore");
		List<BasicNameValuePair> params = Lists.newArrayList();
		String dateline = DateUtils.formatDatetoString(DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN_MILLISECOND);
		params.add(new BasicNameValuePair("ip", "192.168.0.19"));
		params.add(new BasicNameValuePair("mobile", "13575785576"));
		params.add(new BasicNameValuePair("trueName", "胡坤"));
		params.add(new BasicNameValuePair("identityNumber", "330100000000000000"));
		params.add(new BasicNameValuePair("outBizNo", "jms" + dateline));
		params.add(new BasicNameValuePair("totalAmount", "6000.00"));
		params.add(new BasicNameValuePair("borrowPeriod", "6"));
		params.add(new BasicNameValuePair("borrowPeriodType", "2"));
		params.add(new BasicNameValuePair("annualizedRate", "11.14"));
		params.add(new BasicNameValuePair("attachmentThumbnail",
				"{fileSize:879394,fileUrl:\"" + "static/upload/api/2016/10/WOCa8qNgQL.jpg" + "\",name:\"WOCa8qNgQL\",suffix:\".jpg\"}"));
		params.add(new BasicNameValuePair("attachmentImage",
				"{fileSize:879394,fileUrl:\"" + "static/upload/api/2016/10/WOCa8qNgQL.jpg" + "\",name:\"WOCa8qNgQL\",suffix:\".jpg\"}"));
		params.add(new BasicNameValuePair("attachmentsPersonal", ""));
		params.add(new BasicNameValuePair(
				"attachmentsBorrower",
				"[{fileSize:879394,fileUrl:\"" + "static/upload/api/2016/10/WOCa8qNgQL.jpg" + "\",name:\"WOCa8qNgQL\",suffix:\".jpg\"},{fileSize: 879394,fileUrl:\"" + "static/upload/api/2016/10/WOCa8qNgQL.jpg" + "\",name:\"WOCa8qNgQL\",suffix:\".jpg\"}]"));
		params.add(new BasicNameValuePair(
				"attachmentsContract",
				"[{fileSize:879394,fileUrl:\"" + "static/upload/api/2016/10/WOCa8qNgQL.jpg" + "\",name:\"WOCa8qNgQL\",suffix:\".jpg\"},{fileSize: 879394,fileUrl:\"" + "static/upload/api/2016/10/WOCa8qNgQL.jpg" + "\",name:\"WOCa8qNgQL\",suffix:\".jpg\"}]"));
		params.add(new BasicNameValuePair("attachmentsLegal", ""));
		params.add(new BasicNameValuePair("attachmentsCredit", ""));
		params.add(new BasicNameValuePair("attachmentsOther", ""));
		String signData = buildSign(params);
		params.add(new BasicNameValuePair("sign", CryptHelper.md5(signData, CharsetType.UTF8.charset())));
		UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, "UTF-8");
		httpPost.setEntity(urlEncodedFormEntity);
		logger.info("Executing request..." + httpPost.getURI());
		CloseableHttpResponse response = httpclient.execute(httpPost);
		try {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				logger.info("--------------------------------------");
				logger.info("Response content: " + EntityUtils.toString(entity, "UTF-8"));
				logger.info("--------------------------------------");
			}
		} finally {
			response.close();
		}
	}
	
	//@Test
	public void openServiceTest2() throws InterruptedException, ExecutionException, ParseException, IOException, GeneralSecurityException {

		final CloseableHttpClient httpclient = HttpClients.createDefault();
		//HttpPost httpPost = new HttpPost("http://127.0.0.1:8081/yourong-api/openService/synProjectStatus");
		HttpPost httpPost = new HttpPost("http://60.191.67.98:10030/openService/synProjectStatus");
		httpPost.addHeader("Open-Version", "1.0.0");
		httpPost.addHeader("Channel_key", "jimistore");
		List<BasicNameValuePair> params = Lists.newArrayList();
		params.add(new BasicNameValuePair("outBizNo", "jms20161110173724308"));
		String signData = buildSign(params);
		params.add(new BasicNameValuePair("sign", CryptHelper.md5(signData, CharsetType.UTF8.charset())));
		UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, "UTF-8");
		httpPost.setEntity(urlEncodedFormEntity);
		logger.info("Executing request..." + httpPost.getURI());
		CloseableHttpResponse response = httpclient.execute(httpPost);
		try {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				logger.info("--------------------------------------");
				logger.info("Response content: " + EntityUtils.toString(entity, "UTF-8"));
				logger.info("--------------------------------------");
			}
		} finally {
			response.close();
		}
	}

	private static String buildSign(List<BasicNameValuePair> params) {
		Map<String, String> dataMap = Maps.newHashMap();
		for (BasicNameValuePair kv : params) {
			dataMap.put(kv.getName(), kv.getValue());
		}
		List<String> keys = new ArrayList<String>(dataMap.keySet());
		Collections.sort(keys);
		StringBuffer prestr = new StringBuffer();
		String key = "";
		String value = "";
		for (int i = 0; i < keys.size(); i++) {
			key = (String) keys.get(i);
			value = (String) dataMap.get(key);
			if ("null".equals(value) || StringUtil.isBlank(value) || key.equalsIgnoreCase("sign") || key.startsWith("attachments")
					|| key.startsWith("attachment")) {
				continue;
			}
			prestr.append(value).append("^");
		}
		if (prestr.length() < 1) {
			return "";
		}
		return prestr.deleteCharAt(prestr.length() - 1).toString();
	}

	public static void main(String[] args) throws GeneralSecurityException {
		System.out.println(CryptHelper.aesDecrypt("b64c37483fbff9ee0ec2d5ee27662caabd35cbca3371d18747868d446f54be65", "jms20161111"));
//		String thiscode = CryptHelper.aesEncrypt("http://api.test.jimistore.com:8001/201611/e5a771a2-21ce-4cab-953f-548dd852f79d.jpg",
//				"jms20161102");
//		System.out.println(thiscode);
//		String dec = CryptHelper.aesEncrypt("http://api.test.jimistore.com:8001/201611/e5a771a2-21ce-4cab-953f-548dd852f79d.jpg", "UTF-8",
//				"jms20161102");
//		System.out.println(dec);
//		System.out
//				.println("54ae90868753ee46439bc62c973f7021efc146ec010ed842a98097783e698de550e9761bf9c4b46e9115837302c2a0b03641bfdee339a2b882ef979ee5fb3c4d042694a256811fcfdc1cc8ed411f4d18e96b053edb79e6ce693fb3c1d4c12d54");
//		String thiscode2 = CryptUtil
//				.getAesEncrypt(
//						"http://api.test.jimistore.comhttp://api.test.jimistore.comhttp://api.test.jimistore.comhttp://api.test.jimistore.comhttp://api.test.jimistore.comhttp://api.test.jimistore.comhttp://api.test.jimistore.comhttp://api.test.jimistore.comhttp://api.test.jimistore.comhttp://api.test.jimistore.comhttp://api.test.jimistore.comhttp://api.test.jimistore.comhttp://api.test.jimistore.comhttp://api.test.jimistore.comhttp://api.test.jimistore.comhttp://api.test.jimistore.com",
//				"jms20161102");
//		System.out.println(thiscode2);
//		String code = "c3dc0708c92df87f1d81e9ab861eff66";
//		System.out.println(code);
//		String ora = null;
//		try {
//			ora = CryptHelper.aesDecrypt(code, CharsetType.UTF8.charset(), "jms20161102");
//		} catch (GeneralSecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(ora);
	}
}
