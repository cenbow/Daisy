package com.yourong.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.yourong.common.constant.Config;

public class OSSUtil {
	/**
	 * 日志对象
	 */
	private static Logger logger = LoggerFactory.getLogger(OSSUtil.class);
	
	private static class InstanceHolder {
		private static final OSSClient instance = new OSSClient(Config.ossAccessKeyId, Config.ossAccessKeySecret);
	}

	public static final OSSClient getInstance() {
		return InstanceHolder.instance;
	}

	/**
	 * 将附件上传到阿里云oss
	 * @param key 文件目录，可通过ossutil的getkey获取不同类型的key值
	 * @param content 需要上传的流
	 * @param expiration（可空） 失效的时间，如要到2099-10-01失效，则传2099-10-01的date格式日期
	 * @return 返回的文件url，包含完整url
	 */
	public static String uploadAttachmentToOSS(String key, InputStream content, Date expiration, String bucketName) {

		try {
			// 初始化一个OSSClient
			OSSClient client = InstanceHolder.instance;
			// 创建上传Object的Metadata
			ObjectMetadata meta = new ObjectMetadata();
			// 必须设置ContentLength
			meta.setContentLength((long)content.available());
			
			client.putObject(bucketName, key, content,meta);
			if(expiration==null) {
				expiration = DateUtils.getDateTimeFromString(DateUtils.addDays(new Date(), 36500));
			}
			// 生成URL
			URL url = client.generatePresignedUrl(Config.ossPicBucket, key, expiration);
			return url.toString();
		} catch (IOException e) {
			logger.error("将附件上传到阿里云OSS出错, key = " + key, e);
		}
		return null;
	}
	

	/**
	 * 
	 * @param key 文件目录，可通过ossutil的getkey获取不同类型的key值
	 * @param filePath 需要上传的文件名
	 * @return 返回的文件url，包含完整url
	 */
	public static String uploadImageToOSS(String key, String filePath) {

		try {
			File file = new File(filePath);
			InputStream content;
			content = new FileInputStream(file);
			return uploadAttachmentToOSS(key, content, null, Config.ossPicBucket);
		} catch (IOException e) {
			logger.error("将附件上传到阿里云OSS出错, key = " + key, e);
		}
		return null;
	}
	
	/**
	 * 上传合同到OSS
	 * @param key
	 * @param filePath
	 * @return
	 */
	public static String uploadContractToOSS(String key, String filePath) {

		try {
			File file = new File(filePath);
			InputStream content;
			content = new FileInputStream(file);
			return uploadAttachmentToOSS(key, content, null, Config.ossContractBucket);
		} catch (IOException e) {
			logger.error("将附件上传到阿里云OSS出错, key = " + key, e);
		}
		return null;
	}
	
	/**
	 * 
	 * @param key 文件目录，可通过ossutil的getkey获取不同类型的key值
	 * @param filePath 需要上传的文件名
	 * @param expiration （可空） 失效的时间，如要到2099-10-01失效，则传2099-10-01的date格式日期
	 * @return 返回的文件url，包含完整url
	 */
	public static String uploadImageToOSS(String key, String filePath, Date expiration) {

		try {
			File file = new File(filePath);
			InputStream content;
			content = new FileInputStream(file);
			return uploadAttachmentToOSS(key, content, expiration, Config.ossPicBucket);
		} catch (IOException e) {
			logger.error("将附件上传到阿里云OSS出错, key = " + key, e);
		}
		return null;
	}

	/**
	 * 获取合同key
	 * @param memberId
	 * @param pFileName
	 * @param pDate
	 * @return
	 */
	public static String getContractKey(String memberId, String pFileName, Date pDate){
    	String lKey = "";
    	SimpleDateFormat lSdf = new SimpleDateFormat("yyyy-MM-dd");
    	String[] lDateFrags = lSdf.format(pDate).split("-");
    	lKey += memberId.subSequence(0, 6) + "/" + memberId.substring(6, 8) + "/" +
    			memberId.substring(8, 10) + "/" + memberId.substring(10, 12) + "/" +
    		lDateFrags[0] + "/" +lDateFrags[1] + "/" + lDateFrags[2] + "/" +pFileName;
    	return lKey;
    }
	
	/**
	 * 获取债权key
	 * @param debtId
	 * @param pFileName
	 * @param pDate
	 * @return
	 */
	public static String getDebtKey(String debtId, String pFileName, Date pDate){
    	String lKey = "";
    	SimpleDateFormat lSdf = new SimpleDateFormat("yyyy-MM-dd");
    	String[] lDateFrags = lSdf.format(pDate).split("-");
    	lKey += debtId.subSequence(0, 6) + "/" + debtId.substring(6, 8) + "/" +
    			debtId.substring(8, 10) + "/" +
    		lDateFrags[0] + "/" +lDateFrags[1] + "/" + lDateFrags[2] + "/" +pFileName;
    	return lKey;
    }
	
	
	/**
	 * 获取文章key
	 * @param categoryId //文章类型
	 * @param pFileName //图片名称
	 * @param pDate
	 * @return
	 */
	public static String getArticleKey(String categoryId, String pFileName, Date pDate){
    	String lKey = "";
    	SimpleDateFormat lSdf = new SimpleDateFormat("yyyy-MM-dd");
    	String[] lDateFrags = lSdf.format(pDate).split("-");
    	lKey += "article/" + categoryId + "/" + lDateFrags[0] + "/" +lDateFrags[1] + "/" + lDateFrags[2] + "/" +pFileName;
    	return lKey;
    }
	
	/**
	 * 获取banner的key
	 * @param pFileName
	 * @param pDate
	 * @return
	 */
	public static String getBannerKey(String pFileName, Date pDate){
    	String lKey = "";
    	SimpleDateFormat lSdf = new SimpleDateFormat("yyyy-MM-dd");
    	String[] lDateFrags = lSdf.format(pDate).split("-");
    	lKey += "banner/" + lDateFrags[0] + "/" +lDateFrags[1] + "/" + lDateFrags[2] + "/" +pFileName;
    	return lKey;
    }
	
	
	/**
	 * 获取项目key
	 * @param projectId
	 * @param pFileName
	 * @param pDate
	 * @return
	 */
	public static String getProjectKey(String projectId, String pFileName, Date pDate){
    	String lKey = "";
    	SimpleDateFormat lSdf = new SimpleDateFormat("yyyy-MM-dd");
    	String[] lDateFrags = lSdf.format(pDate).split("-");
    	lKey += projectId.subSequence(0, 5) + "/" + projectId.substring(5, 7) + "/" +
    			projectId.substring(7, 9) + "/" +
    		lDateFrags[0] + "/" +lDateFrags[1] + "/" + lDateFrags[2] + "/" +pFileName;
    	return lKey;
    }

	/**
	 * 获取项目key
	 * @param goodsid
	 * @param pFileName
	 * @param pDate
	 * @return
	 */
	public static String getGoodsKey(String goodsid, String pFileName, Date pDate){
		String lKey = "";
		SimpleDateFormat lSdf = new SimpleDateFormat("yyyy-MM-dd");
		String[] lDateFrags = lSdf.format(pDate).split("-");
		lKey +=  goodsid+
				lDateFrags[0] + "/" +lDateFrags[1] + "/" + lDateFrags[2] + "/" +pFileName;
		return lKey;
	}

	/**
	 * 获取项目key
	 * @param manageId
	 * @param pFileName
	 * @param pDate
	 * @return
	 */
	public static String getManageKey(String manageId, String pFileName, Date pDate){
		String lKey = "";
		SimpleDateFormat lSdf = new SimpleDateFormat("yyyy-MM-dd");
		String[] lDateFrags = lSdf.format(pDate).split("-");
		lKey +=  manageId+
				lDateFrags[0] + "/" +lDateFrags[1] + "/" + lDateFrags[2] + "/" +pFileName;
		return lKey;
	}
	
	/**
	 * 获取会员头像Key
	 * @param memberId
	 * @param pFileName
	 * @param pDate
	 * @return
	 */
	public static String getAvatarKey(String memberId, String pFileName, Date pDate){
		String lKey = "";
    	SimpleDateFormat lSdf = new SimpleDateFormat("yyyy-MM-dd");
    	String[] lDateFrags = lSdf.format(pDate).split("-");
    	lKey += memberId.subSequence(0, 5) + "/" + memberId.substring(5, 7) + "/" +
    			memberId.substring(7, 9) + "/" +
    		lDateFrags[0] + "/" +lDateFrags[1] + "/" + lDateFrags[2] + "/" +pFileName;
    	return lKey;
	}
	
	
	/**
	 * 获取不包含前缀的url路径，用于存储到附件表
	 * @param url
	 */
	public static String getSimpleImageUrl(String url) {
		String tmpUrl = url.substring(url.indexOf(Config.ossPicBucket)-1);
		return tmpUrl.substring(0, tmpUrl.indexOf("?"));
	}
	
	/**
	 * 获取包含前缀的url路径，用于页面展示
	 * @param url
	 */
	public static String getFullImageUrl(String simpleUrl) {
		return Config.ossPicBucket+simpleUrl;
	}
	
	/**
	 * 获取合同下载地址，默认1个小时后失效
	 * @param key
	 * @param expiration
	 * @return
	 */
	public static String getContractDownloadUrl(String key, Date expiration) {
		if(expiration==null) {
			expiration = DateUtils.addHour(new Date(), 1);
		}
		// 生成URL
		// 初始化一个OSSClient
		OSSClient client = InstanceHolder.instance;
		URL url = client.generatePresignedUrl(Config.ossContractBucket, key, expiration);
		return url.toString();
	}
	
	
	public static void main(String[] args) throws Exception {
//		File file = new File("D:\\Documents\\Pictures\\gst.jpg");
//		InputStream content;
//		content = new FileInputStream(file);
//		String url = uploadAttachmentToOSS(getDebtKey("9999000015", "gst1.jpg", new Date()), content, DateUtils.getDateTimeFromString(DateUtils.addDays(new Date(), 365)), pic_bucket);
//		System.out.println("url="+url);
		System.out.println(getContractDownloadUrl("110800/00/01/03/2014/10/21/"+"888800000163.pdf", null));
		System.out.println(getArticleKey("12", "dafdaf.jpg", DateUtils.getCurrentDate()));
	}
}
