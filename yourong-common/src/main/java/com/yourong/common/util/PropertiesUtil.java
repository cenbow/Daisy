package com.yourong.common.util;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.jcabi.manifests.Manifests;

public  final  class PropertiesUtil {
	private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);	
	private static Properties loadProperties;
	private static String configFilePath = "config.properties";
	/**静态资源版本号 */
	private static String STATIC_RESOURCE_VERSION = null;
	
	private PropertiesUtil(){	    
	}
	public static String getProperties(String key) {			
		String tempName = "";
		try {
			if(loadProperties == null){
			    	Resource resource = new ClassPathResource(configFilePath);
				loadProperties = PropertiesLoaderUtils.loadProperties(resource);
			}			
			tempName = loadProperties.getProperty(key);
		} catch (IOException e) {
			logger.error("获取配置文件config.properties失败", e);
		}
		return tempName;
	}
	/**
	 * 上传文件根目录
	 * @return
	 */
	public static String getUploadDirectory(){
		return PropertiesUtil.getProperties("upload.directory");
	}
	/**
	 * 是否签名加密
	 * @return
	 * author: pengyong
	 * 下午4:36:21
	 */
	public static boolean isAppNoEncrption(){
		if("true".equalsIgnoreCase(PropertiesUtil.getProperties("app.not.need.encrption"))){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 是否开发
	 * @return
	 * author: pengyong
	 * 下午4:36:21
	 */
	public static boolean isDev(){
		if("true".equalsIgnoreCase(PropertiesUtil.getProperties("isDev"))){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 静态资源版本
	 * @return
	 */	
	public static String getStaticResourceVersion(){
		if(STATIC_RESOURCE_VERSION == null){
			try {

				Manifests.append(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getServletContext());

				String version = Manifests.read("App-Version-static");
				STATIC_RESOURCE_VERSION = version;
			} catch (Exception e) {
				logger.error("获取静态资源版本异常", e);
				STATIC_RESOURCE_VERSION = String.valueOf(DateUtils.getCurrentDateTime().getTime());
			}
			return STATIC_RESOURCE_VERSION;
		}else{
			return 	STATIC_RESOURCE_VERSION;
		}		
	}
	
	/**
	 * 获得支持可上传的文件类型
	 * @return
	 */
	public static String[] getUploadFileExts(){
		String exts =  PropertiesUtil.getProperties("uploadFileExts");
		if(StringUtil.isNotBlank(exts)){
			return exts.split(",");
		}
		return null;
	}
	
	/**
	 * 资源文件开发环境
	 * @return
	 */
	public static boolean resourceDev(){
		if("true".equalsIgnoreCase(PropertiesUtil.getProperties("resourceDev"))){
			return true;
		}else{
			return false;
		}
	}
	public static  int getMybatisCacheDB(){
		return Integer.valueOf(PropertiesUtil.getProperties("redis.cache.mybatis.index"));
	}
	
	/**
	 * 获得API URL
	 * @return
	 */
	public static String getApiRootUrl(){
		String url= PropertiesUtil.getProperties("root_url");
		if(StringUtil.isBlank(url)){
			url = "http://www.yrw.com";
		}
		return url;
	}
	
	/**
	 * 获得API URL
	 * @return
	 */
	public static String getMstationRootUrl(){
		String url= PropertiesUtil.getProperties("mRoot_url");
		if(StringUtil.isBlank(url)){
			url = "http://m.yrw.com";
		}
		return url;
	}
	
	/**
	 * 对象缓存所在的数据库索引
	 * @return
	 */
	public static int getObjectCacheDBIndex(){
		return Integer.valueOf(PropertiesUtil.getProperties("redis.cache.object.index"));
	}
	
	
	/**
	 * 百度推送Android访问令牌
	 * @return
	 */
	public static String getBaiduPushAndroidApiKey(){
		return getProperties("baidu.push.android.api.key");
	}
	
	/**
	 * 百度推送Android安全密钥
	 * @return
	 */
	public static String getBaiduPushAndroidSecretKey(){
		return getProperties("baidu.push.android.secret.key");
	}
	
	/**
	 * 百度推送IOS访问令牌
	 * @return
	 */
	public static String getBaiduPushIOSApiKey(){
		return getProperties("baidu.push.ios.api.key");
	}
	
	/**
	 * 百度推送IOS安全密钥
	 * @return
	 */
	public static String getBaiduPushIOSSecretKey(){
		return getProperties("baidu.push.ios.secret.key");
	}
	
	/**
	 * 百度推送IOS声音名称
	 * @return
	 */
	public static String getBaiduPushMsgSoundName(){
		String soundName = getProperties("baidu.push.msg.sound.name");
		if(StringUtil.isNotBlank(soundName)){
			return soundName;
		}
		return "sms-received1.caf";
	}
	
	/**
	 * Baidu IOS应用推送时使用
	 * 1：开发状态
	 * 2：生产状态
	 * @return
	 */
	public static int getBaiduPushDeployStatus(){
		if(isDev()){
			return 1;
		}
		return 2;
	}
	
	/**
	 * web url
	 * @return
	 */
	public static String getWebRootUrl(){
		String url= PropertiesUtil.getProperties("root_url_web");
		if(StringUtil.isBlank(url)){
			url = "http://www.yrw.com";
		}
		return url;
	}
	
	/**
	 * 完善信息活动ID
	 * @return
	 */
	public static Long getActivityCompletedMemberInfoId(){
		String completedMemberInfoId = PropertiesUtil.getProperties("activity.completedMemberInfo");
		return Long.parseLong(completedMemberInfoId);
	}
	
	/**
	 * 绑定邮箱活动ID
	 * @return
	 */
	public static Long getActivityBindEmailId(){
		String bindEmailId = PropertiesUtil.getProperties("activity.bindEmail");
		return Long.parseLong(bindEmailId);
	}
	
	/**
	 * 实名验证活动ID
	 * @return
	 */
	public static Long getActivityVerifyTrueNameId(){
		String verifyTrueNameId = PropertiesUtil.getProperties("activity.verifyTrueName");
		return Long.parseLong(verifyTrueNameId);
	}
	
	/**
	 * 生日专题开始时间
	 * @return
	 */
	public static Date getBirthdayActivity(){
		String birthdayActivity = PropertiesUtil.getProperties("activity.birthday.startTime");
		return DateUtils.getDateFromString(birthdayActivity);
	}
	
	/**
	 * 生日50元优惠券
	 * @return
	 */
	public static Long getCoupon50ActvityId(){
		String coupon50ActvityId = getProperties("activity.birthday.coupon50ActvityId");
		return Long.parseLong(coupon50ActvityId);
	}
	
	/**
	 * 生日1%优惠券
	 * @return
	 */
	public static Long getCoupon001ActvityId(){
		String coupon001ActvityId = getProperties("activity.birthday.coupon001ActvityId");
		return Long.parseLong(coupon001ActvityId);
	}
	
	/**
	 * 需要返回上一级的特殊URL
	 * @return
	 */
	public static String[] getUrlArray(){
		String url = PropertiesUtil.getProperties("req_url_array");
		if(StringUtil.isNotBlank(url)){
			return url.split(",");
		}
		return null;
	}
	/**
	 * 微信appId
	 * @return
	 */
	public static String getWeixinAppID(){
		return getProperties("weixin.api.appID");
	}
	/**
	 * 微信appsecret
	 * @return
	 */
	public static String getWeixinAppsecret(){
		return getProperties("weixin.api.appsecret");
	}
	
	
	/**
	 * aliyun url
	 * @return
	 */
	public static String getAliyunUrl(){
		String url= PropertiesUtil.getProperties("oss.ossPicUrl");
		if(StringUtil.isBlank(url)){
			url = "https://oss-cn-hangzhou.aliyuncs.com";
		}
		return url;
	}
	
	/**
	 * 生日50元优惠券
	 * @return
	 */
	public static Long getCouponTemplateId50(){
		String coupon50ActvityId = getProperties("activity.birthday.couponTemplateId50");
		return Long.parseLong(coupon50ActvityId);
	}
	
	/**
	 * 生日1%优惠券
	 * @return
	 */
	public static Long getCouponTemplateId001(){
		String coupon001ActvityId = getProperties("activity.birthday.couponTemplateId001");
		return Long.parseLong(coupon001ActvityId);
	}
	/**
	 *  黑名单 线程暂停时间
	 * @return
	 */
	public static Long theadSuspend(){
		String theadSupend = getProperties("thead.suspend");
		return Long.parseLong(theadSupend);
	}

}
