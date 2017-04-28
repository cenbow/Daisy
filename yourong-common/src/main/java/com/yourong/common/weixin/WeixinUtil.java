package com.yourong.common.weixin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import rop.thirdparty.com.alibaba.fastjson.JSONArray;

/**
 * @Author:chaisen
 * @date:2015年9月17日下午3:11:55
 */
public class WeixinUtil {
	
	private static  Logger logger = LoggerFactory.getLogger(WeixinUtil.class);
	@Autowired
	public static String APPID, APPSECRET;
	// http客户端
	public static DefaultHttpClient httpclient;
	
	static {
		httpclient = new DefaultHttpClient();
		httpclient = (DefaultHttpClient) HttpClientConnectionManager
				.getSSLInstance(httpclient); // 接受任何证书的浏览器客户端
		APPID = "wxded01b64a081b72b";// 你的APPID
		APPSECRET = "abb5a0addf478c91d67ba88363afb6f0"; // 你的APPSECRET
	}
	/**
	 * 创建菜单
	 */
	public static String createMenu1(String params, String accessToken)
			 {
		HttpPost httpost = HttpClientConnectionManager
				.getPostMethod("https://api.weixin.qq.com/cgi-bin/menu/create?access_token="
						+ accessToken);
		httpost.setEntity(new StringEntity(params, "UTF-8"));
		String result="";
		int resultCode=0;
		try {
			HttpResponse response = httpclient.execute(httpost);
			String jsonStr = EntityUtils.toString(response.getEntity(), "utf-8");
			JSONObject demoJson = new JSONObject(jsonStr);
			result=demoJson.getString("errmsg");
			resultCode=demoJson.getInt("errcode");
			if(resultCode==40054){
				result="url格式不正确！";
			}
			return result;
		}catch (IOException e) {
			logger.error("创建菜单出错, result = " + result, e);
		}
		return null;
	}
	
	/**
	 * 获取accessToken
	 */
	public static String getAccessToken(String appid, String secret)
			 {
		HttpGet get = HttpClientConnectionManager
				.getGetMethod("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
						+ appid + "&secret=" + secret);
		try {
			HttpResponse response = httpclient.execute(get);
			String jsonStr = EntityUtils.toString(response.getEntity(), "utf-8");
			JSONObject demoJson = new JSONObject(jsonStr);
			return demoJson.getString("access_token");
		}catch (IOException e) {
			logger.error("获取token出错", e);
		}
		return null;
	}

	/**
	 * 查询菜单
	 */
	public static String getMenuInfo(String accessToken) {
		HttpGet get = HttpClientConnectionManager
				.getGetMethod("https://api.weixin.qq.com/cgi-bin/menu/get?access_token="
						+ accessToken);
		String jsonStr="";
		try {
			HttpResponse response = httpclient.execute(get);
			jsonStr = EntityUtils.toString(response.getEntity(), "utf-8");
			return jsonStr;
		}catch (IOException e) {
			logger.error("获取菜单出错，jsonStr=" +jsonStr,e);
		}
		
		return null;
	}

	/**
	 * 删除菜单
	 */
	public static String deleteMenuInfo(String accessToken) throws Exception {
		HttpGet get = HttpClientConnectionManager
				.getGetMethod("https://api.weixin.qq.com/cgi-bin/menu/delete?access_token="
						+ accessToken);
		HttpResponse response = httpclient.execute(get);
		String jsonStr = EntityUtils.toString(response.getEntity(), "utf-8");
		JSONObject demoJson = new JSONObject(jsonStr);
		return demoJson.getString("errmsg");
	}

	/**
	 * 
	 * @param args
	 */

//从request中拿到xml并封装在map中
     public static Map parseXml(HttpServletRequest request) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        // 从request中取得输入流
        StringBuffer sb = new StringBuffer();
        InputStream is = request.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String s = "";
        while ((s = br.readLine()) != null) {
            sb.append(s);
        }
        String xml = sb.toString();
 
        // 读取输入流
        Document document = null;
        try {
            document = DocumentHelper.parseText(xml);
        } catch (DocumentException e1) {
            e1.printStackTrace();
        }
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();
        // 遍历所有子节点
        for (Element e : elementList) {
            // 对于CDATA区域内的内容，XML解析程序不会处理，而是直接原封不动的输出。
            map.put(e.getName(), e.getText());
        }
        return map;
    }
     public static String process(HttpServletRequest request,HttpServletResponse response) throws Exception{
         @SuppressWarnings("unchecked")
         Map<String, String> map = parseXml(request);
         String result = "";
         String msgType = map.get("MsgType");
         String respContent = "";
         //文本消息
         /*if (msgType.equals("text")) {
            respContent = TulingRobot.robot(map.get("Content"));
            TextMessage textMessage = Map2Bean.parseText(map,respContent);
            result = Bean2ResponseXML.textMessageToXml(textMessage);
        }*/
        //图片消息
       if (msgType.equals("image")) {
            respContent = "";
            return null;
        }
        //声音消息
        else if (msgType.equals("voice")) {
            respContent = "";
            return null;
        }
        //视频消息
        else if (msgType.equals("video")) {
            respContent = "";
            return null;
        }
        //地理位置
        else if (msgType.equals("location")) {
            respContent = "";
            return null;
        }
        //事件类型
        else if (msgType.equals("event")) {/*
            String eventType = map.get("Event");
            //订阅
            if (eventType.equals("subscribe")) {
                respContent = "欢迎订阅我的公众号！";
                TextMessage textMessage = Map2Bean.parseText(map,respContent);
                result = Bean2ResponseXML.textMessageToXml(textMessage);
            }
            //取消订阅
            else if (eventType.equals("unsubscribe")) {
                // TODO
                return null;
            }
            //点击菜单
            else if (eventType.equals("CLICK")) {
                // TODO 
                return null;
            }
        */}
        return result;
    }
     public static void main(String args[]){
         String s = "[{\"name\":\"我的账户\",\"sub_button\":[{\"type\":\"click\",\"name\":\"账户绑定\",\"key\":\"M1001\"},{\"type\":\"click\",\"name\":\"我的资产\",\"key\":\"M1002\"}]},{\"type\":\"click\",\"name\":\"我的资产\",\"key\":\"M2001\"},{\"type\":\"click\",\"name\":\"其它\",\"key\":\"M3001\"}]";
         String aa= "[{\"name\":\"我的账户\",\"sub_button\":[{\"type\":\"click\",\"name\":\"注册绑定\",\"key\":\"register_bind\",\"sub_button\":[]},{\"type\":\"click\",\"name\":\"我的资产\",\"key\":\"my_balance\",\"sub_button\":[]},{\"type\":\"click\",\"name\":\"我的项目\",\"key\":\"my_projects\",\"sub_button\":[]},{\"type\":\"click\",\"name\":\"我的优惠\",\"key\":\"my_coupons\",\"sub_button\":[]},{\"type\":\"click\",\"name\":\"我的提现\",\"key\":\"my_withdrawals\",\"sub_button\":[]}]},{\"name\":\"认识有融\",\"sub_button\":[{\"type\":\"view\",\"name\":\"理财项目\",\"url\":\"https://www.yrw.com/products/list-all-all-investing-1.html\",\"sub_button\":[]},{\"type\":\"view\",\"name\":\"关于有融\",\"url\":\"http://eqxiu.com/s/SGPZcFTG\",\"sub_button\":[]},{\"type\":\"view\",\"name\":\"安全保障\",\"url\":\"https://www.yrw.com/post/subject/safety.html\",\"sub_button\":[]},{\"type\":\"view\",\"name\":\"新浪存钱罐\",\"url\":\"https://www.yrw.com/activity/sinapay\",\"sub_button\":[]},{\"type\":\"view\",\"name\":\"APP下载\",\"url\":\"hteApp\",\"sub_button\":[]}]},{\"name\":\"周年庆\",\"sub_button\":[{\"type\":\"view\",\"name\":\"答谢会\",\"url\":\"htttingPreHeat\",\"sub_button\":[]},{\"type\":\"view\",\"name\":\"相聚11·19\",\"url\":\"htw.eptnqi\",\"sub_button\":[]},{\"type\":\"view\",\"name\":\"员工祝福\",\"url\":\"http://v.qq.com/page/f/x/5/f01719elkx5.html\",\"sub_button\":[]}]}]";
         JSONArray jsonChannelIds = JSONArray.parseArray(aa);
         List<Weixin> posLogList = Lists.newArrayList();
         posLogList=JSON.parseArray(jsonChannelIds.toJSONString(), Weixin.class);
         
       
     }

}
 
