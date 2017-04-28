
package com.yourong.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.bestsign.sdk.BestSignSDK;
import cn.bestsign.sdk.domain.vo.params.ReceiveUser;
import cn.bestsign.sdk.domain.vo.params.SendUser;
import cn.bestsign.sdk.domain.vo.result.AutoSignbyCAResult;
import cn.bestsign.sdk.domain.vo.result.CertificateApplyResult;
import cn.bestsign.sdk.domain.vo.result.Continfo;
import cn.bestsign.sdk.domain.vo.result.QueryUserImageUserInfoResult;
import cn.bestsign.sdk.domain.vo.result.UploadUserImageResult;
import cn.bestsign.sdk.integration.Constants.CA_TYPE;
import cn.bestsign.sdk.integration.Constants.CERT_IDENT_TYPE;
import cn.bestsign.sdk.integration.Constants.CONTRACT_NEEDVIDEO;
import cn.bestsign.sdk.integration.Constants.DEVICE_TYPE;
import cn.bestsign.sdk.integration.Constants.USER_TYPE;
import cn.bestsign.sdk.integration.Logger.DEBUG_LEVEL;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yourong.common.constant.Config;
import com.yourong.common.enums.StatusEnum;

public class BestSignUtil {

	private static final Logger logger = LoggerFactory.getLogger(BestSignUtil.class);

	private static final String mid = Config.bestSignMid;

	private static final String pem = Config.bestSignPem;

	private static String host = Config.bestSignHost;

	private static BestSignSDK sdk = null;

	private static Continfo[] lastContinfoList = null;
	
	static {
		sdk = BestSignSDK.getInstance(mid, pem, host);
		sdk.setLogDir("/mnt/logs/yourong/bestSign/");
		sdk.setDebugLevel(DEBUG_LEVEL.INFO);
	}
	
	


	/**
	 * 
	 * @Description:用户图片上传接口
	 * @throws Exception
	 * @ imgType 支持jpg和png两种
	 * @author: zhanghao
	 * @time:2016年7月5日 下午16:22:27
	 */
	public static UploadUserImageResult uploadUserSign(String usermobile,String imgType,String filePath,String imgName,String username) throws Exception {
		//outputInfo();
		/*String usermobile = "18800000002";
		String username = "测试签章第三方有限公司";
		byte[] image = getResource("D:/mnt/data/sign/333.jpg");
		String imgName = "333.jpg";
		String imgType = "jpg";*/
		//File imageFile = new File(filePath);
		byte[] image = getResource(filePath);
		USER_TYPE usertype = USER_TYPE.ENTERPRISE;
		logger.info("用户图片上传接口: usermobile={}, username={}", usermobile, username );
		UploadUserImageResult result = sdk.uploaduserimage(null, usermobile, imgType, image, imgName, username, usertype);
		logger.info("用户图片上传接口结果:{}", dump(result));
		return result;
	} 

	/**
	 * 
	 * @Description:查询用户图片
	 * @throws Exception
	 * @author: zhanghao
	 * @time:2016年7月5日 下午16:22:27
	 */
	public static Map<String, QueryUserImageUserInfoResult> queryUserSign(String useraCount ) throws Exception {
		outputInfo();
		/*String useraCount = "18800000002";
		String sealName = "contract";
*/		logger.info("用户图片上传接口: useraCount={}, sealName={}", useraCount);
		Map<String, QueryUserImageUserInfoResult> resultMap = sdk.queryuserimage(useraCount);
	/*	QueryUserImageUserInfoResult info = resultMap.get("peruserinfo");
		QueryUserImageUserInfoResult infoC = resultMap.get("companyuser");*/
		logger.info(resultMap.toString());
		return resultMap;
	}	

	/**
	 * 
	 * @Description:CFCA认证
	 * @throws Exception
	 * @author: zhanghao
	 * @time:2016年7月5日 下午16:22:27
	 */
	public static CertificateApplyResult handleCFCACertificate(String name,String linkMobile,String address,String province,String city,String linkIdCode) throws Exception {
		outputInfo();
		CA_TYPE caType = CA_TYPE.CFCA;
		String password = "123456";
		/*String name = "张皓";
		String linkMobile = "18966154581";
		String linkIdCode = "140411199210235610";*/
		logger.info("CFCA认证接口: name={}, linkMobile={}", name, linkMobile);
		CertificateApplyResult result = sdk.certificateApply(caType, name, password, linkMobile, null, address, province, city, linkIdCode, CERT_IDENT_TYPE.PERSONAL_ID_CARD);
		logger.info("CFCA认证结果: result={}, cerNo={}, msg={}", result.getIsResult(), result.getCerNo(), result.getMsg());
		dump(result);
		return result;
		//CFCA认证结果: result=true, cerNo=CFCA-33-20160629165229399, msg=ok
	}

	/**
	 * 
	 * @Description:浙江CA认证
	 * @throws Exception
	 * @author: zhanghao
	 * @time:2016年7月5日 下午16:22:27
	 */
	public static CertificateApplyResult handleCACertificate(String enterpriseName,String linkMan,String linkMobile,String address,
			String province,String city,String linkIdCode,String regisNo,String orgNo,String taxNo) throws Exception {
		outputInfo();
		CA_TYPE caType = CA_TYPE.ZJCA;
		String password = "123456";
		/*	String name = "测试签章小融有限公司";
		String linkMobile = "18800000000";
		String linkIdCode = "230882198706084910";*/
		logger.info("CA认证接口: enterpriseName={}, linkMan={},linkMobile={},address={},province={},city={},linkIdCode={},regisNo={},orgNo={},taxNo={}", 
				enterpriseName, linkMan,linkMobile,address,province,city,linkIdCode,regisNo,orgNo,taxNo);
		CertificateApplyResult result = sdk.certificateApply(caType, enterpriseName, password, linkMan, linkMobile, null, address, province, city,
				linkIdCode, regisNo, orgNo, taxNo,CERT_IDENT_TYPE.PERSONAL_ID_CARD);
		logger.info("CA认证结果: result={}, cerNo={}, msg={}", result.getIsResult(), result.getCerNo(), result.getMsg());
		dump(result);
		return result;
		//ZJCA-11-2016062314302399   18966154581  甲方
		//ZJCA-11-2016062314302399    18800000000   小融
		//ZJCA-11-2016062314302399   18800000001
	}

	/**
	 * 
	 * @Description:发送合同
	 * @return
	 * @throws Exception
	 * @author: zhanghao
	 * @time:2016年7月5日 下午16:22:27
	 */
	public static Continfo[] createContract(String filePath,ReceiveUser firstPart,ReceiveUser secondPart,SendUser senduser,String fileName) throws Exception {
		byte[] fileData = getResource(filePath);
		//ReceiveUser firstPart = new ReceiveUser(null, "测试签章陈林琳有限公司", "17712122121", USER_TYPE.ENTERPRISE, CONTRACT_NEEDVIDEO.NONE, false);
		// * {"Signimagetype":0,"mobile":"17712122121","name":"陈林琳","needvideo":0,"usertype":1}
		// * {"Signimagetype":0,"mobile":"18800000000","name":"颜星嘉","needvideo":0,"usertype":1}
		//ReceiveUser secondPart = new ReceiveUser(null, "颜星嘉", "18800000001", USER_TYPE.PERSONAL, CONTRACT_NEEDVIDEO.NONE, false);
		ReceiveUser[] userlist = { firstPart, secondPart };
		//SendUser senduser = new SendUser("", "小融网络", "13358115822", 99, true, USER_TYPE.ENTERPRISE, false,
		//		"【有融网】标题", "");
		Continfo[] lastContinfoList = sdk.sjdsendcontractdocUpload(userlist, senduser, fileData,fileName);

		// {"response":{"info":{"code":100000},
//"content":{"contlist":[{"continfo":{"signid":"14666773425714IQK2","docid":"1466677342571AXA41","email":"13658566325","sendemail":"100044535@qq.com","vatecode":"","needVideo":null}},
		// {"continfo":{"signid":"14666773425714IQK2","docid":"1466677342571AXA41","email":"13575785519","sendemail":"100044535@qq.com","vatecode":"","needVideo":null}}]}}}
		// docId = 1466677342571AXA41
		return lastContinfoList;
	}
	
	/**
	 * 
	 * @Description:yrw平台自动签署
	 * @throws Exception
	 * @author: zhanghao
	 * @time:2016年7月5日 下午16:22:27
	 */
	public static AutoSignbyCAResult yrwSign(String signId,String mobile) throws Exception {
		// 自动签这份合同
		//String signId = "1467617615565KRW82";
		//String email = "18800000002";
		int pagenum = 3;
		float signX = 0.2f;
		float signY = 0.8f;
		boolean openflag = true;

		AutoSignbyCAResult result = sdk.AutoSignbyCA(signId, mobile, pagenum, signX, signY, openflag);
		// {"code":100000,"docID":"1466677342571AXA41","fmid":"9d1f03ea49a14d049ddacf7a2e28aae2","fsdId":"14666773425714IQK2","returnurl":"","vmsg":"已完成签字"}
		System.out.println(result);
		return result;
	}

	/**
	 * 
	 * @Description:自动签署
	 * @throws Exception
	 * @author: zhanghao
	 * @time:2016年7月5日 下午16:22:27
	 */
	public static AutoSignbyCAResult autoSign(String signId,String mobile,int pagenum,float signX,float signY) throws Exception {
		// 
		// TODO 当手动签署的人是企业用户的场景，是否需要调用公章签署的api
		// 自动签这份合同
		/*String signId = "1493272520394ULN12";
		String mobile = "18652842856";
		int pagenum = 5;
		float signX = 0.38f;
		float signY = 0.05f;*/
		logger.info("平台自动签署: signId={}, mobile={}",signId, mobile);
		
		boolean openflag = true;
		AutoSignbyCAResult result = sdk.AutoSignbyCA(signId, mobile, pagenum, signX, signY, openflag);
		// {"response":{"info":{"code":100000},"content":{"code":"100000","fsdId":"14666773425714IQK2","fmid":"5bccd7b6ed574df7acc12275ddb4ad4c","docID":"1466677342571AXA41","returnurl":"","vmsg":"已完成签字"}}}
		System.out.println(result);
		return result;
	}

	/**
	 * 
	 * @Description:手动签署
	 * @throws Exception
	 * @author: zhanghao
	 * @time:2016年7月5日 下午16:22:27
	 */
	public static String handSign(String signId,String mobile,String docId,String returnUrl,int type,int pagenum,float signx,float signy ) throws Exception {
		/*String signId = "1467618749960ICGC2";
		String fsid = "1467618749960BSSP1";*/
		//String email = "15136133072";
	
		//String returnurl = "http://notify.yourongwang.com:10066/notify/trade?isNeedToken=True";
		//DEVICE_TYPE typedevice = DEVICE_TYPE.PC;    1-pc  2-mobile
		boolean openflagString = true;
		DEVICE_TYPE typeDevice = DEVICE_TYPE.PC;
		if(type==StatusEnum.CONTRACT_SIGN_WAY_MOBILE.getStatus()){
			typeDevice = DEVICE_TYPE.MOBILE;
		}
		String result = sdk.getSignPageSignimagePc(signId, mobile, pagenum, signx, signy, returnUrl, typeDevice, openflagString);
		System.out.println(result);
		return result;
		//http://notify.yourongwang.com:10066/notify/trade?code=100000&signID=1467190561227ERD92
	}

	/**
	 * 
	 * @Description:查询第三方合同签署信息
	 * @throws Exception
	 * @author: zhanghao
	 * @time:2016年7月5日 下午16:22:27
	 */
	public static JSONArray queryContractInfo(String signId ) throws Exception {
		//String signId = "14695342339259DW92";
		Map<String, Object> map = sdk.contractInfo(signId);
		String result =  (String) map.get("response");
		JSONObject  jasonObject = JSONObject.parseObject(result);
		Map res = (Map)jasonObject;
		JSONObject result1 =   (JSONObject) res.get("response");
		Map res1 = (Map)result1;
		JSONObject result2 = (JSONObject)  res1.get("content");
		Map res2 = (Map)result2;
		JSONArray jsonArray = (JSONArray) res2.get("userlist");
		return jsonArray;
		
	}


	private static HostnameVerifier hv = new HostnameVerifier() {   
        public boolean verify(String urlHostName, SSLSession session) {   
            System.out.println("Warning: URL Host: " + urlHostName + " vs. "   
                               + session.getPeerHost());   
            return true;   
        }   
    };   
	
	/**
	 * 
	 * @Description:合同下载链接
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2016年6月26日 下午9:08:31
	 */
    public static String getContractDownURL(String signId) throws Exception {
		//String signId = "14666773425714IQK2";
		String fileUrl = sdk.getContractDownloadURL(signId);
		//fileUrl ="http://www.yrw.com/download/apk";
		//https://www.ssqsign.com/openpage/contractDownload.page?mid=680ca993799a48b2b9df2f5a2d61f05c&sign=VKt0iUBWtkiOOr9VW1qEVam7SbvOvJTNTHV0rD0vCId%2F5FXhoVQM1UtWu02DN%2BQ%2Fn%2F2h4HeZn8BLsnOJ%2FIrYYDHK4azmyOo7Soouob65u4n5yxi1%2FZJbDq3%2BewnCK8Zgph3okWRoId%2Bd6aAz5jHwEttIYUC9dIUYJIeReKa5OH0%3D&fsdid=1468561905952IX4S2&status=3
		//String fileUrl = "https://www.ssqsign.com/openpage/contractDownload.page?mid=E0000000000000000001&fsdid=14152550911203VT42&status=3&sign=K3vfCiE6GvI3TF7nR4yum4hrhHkK97MX%2Fgvlq2B1vaHtHDTgxwG0ThQKXeCteZCR7DiTi2EnJpKzaYF32YsTrJHhy7xQKU90Fnw%2FmoSylfmr7gcen%2FppqLGI871JC5rJZyLXK4t%2FlmUaHyHBuWo35lkjBtZfZMWFlmeSw47DjCM%3D";
		
		return fileUrl;
	}
	
	/**
	 * 
	 * @Description:下载合同文件
	 * @throws Exception
	 * @author: zhanghao
	 * @time:2016年7月18日 下午9:08:31
	 */
	public static String getContractDown(String signId,Long transactionId) throws Exception {
		//String signId = "14666773425714IQK2";
		String fileUrl = sdk.getContractDownloadURL(signId);
		//fileUrl ="http://www.yrw.com/download/apk";
		//https://www.ssqsign.com/openpage/contractDownload.page?mid=680ca993799a48b2b9df2f5a2d61f05c&sign=VKt0iUBWtkiOOr9VW1qEVam7SbvOvJTNTHV0rD0vCId%2F5FXhoVQM1UtWu02DN%2BQ%2Fn%2F2h4HeZn8BLsnOJ%2FIrYYDHK4azmyOo7Soouob65u4n5yxi1%2FZJbDq3%2BewnCK8Zgph3okWRoId%2Bd6aAz5jHwEttIYUC9dIUYJIeReKa5OH0%3D&fsdid=1468561905952IX4S2&status=3
		//String fileUrl = "https://www.ssqsign.com/openpage/contractDownload.page?mid=E0000000000000000001&fsdid=14152550911203VT42&status=3&sign=K3vfCiE6GvI3TF7nR4yum4hrhHkK97MX%2Fgvlq2B1vaHtHDTgxwG0ThQKXeCteZCR7DiTi2EnJpKzaYF32YsTrJHhy7xQKU90Fnw%2FmoSylfmr7gcen%2FppqLGI871JC5rJZyLXK4t%2FlmUaHyHBuWo35lkjBtZfZMWFlmeSw47DjCM%3D";
		//String fileUrl = "https://www.ssqsign.com/openpage/contractDownload.page?mid=680ca993799a48b2b9df2f5a2d61f05c&sign=VKt0iUBWtkiOOr9VW1qEVam7SbvOvJTNTHV0rD0vCId%2F5FXhoVQM1UtWu02DN%2BQ%2Fn%2F2h4HeZn8BLsnOJ%2FIrYYDHK4azmyOo7Soouob65u4n5yxi1%2FZJbDq3%2BewnCK8Zgph3okWRoId%2Bd6aAz5jHwEttIYUC9dIUYJIeReKa5OH0%3D&fsdid=1468561905952IX4S2&status=3";
		
		URL url = new URL(fileUrl);
		 // 下载网络文件
	    int bytesum = 0;
	    int byteread = 0;
	    	HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
	    	if ( conn instanceof HttpsURLConnection ) {
				((HttpsURLConnection)conn).setHostnameVerifier(new HostnameVerifier() {
					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});
				TrustManager[] tm = {new X509TrustManager() {
					@Override
					public void checkClientTrusted(X509Certificate[] chain,
							String authType) throws CertificateException {
						// TODO Auto-generated method stub
					}

					@Override
					public void checkServerTrusted(X509Certificate[] chain,
							String authType) throws CertificateException {
						// TODO Auto-generated method stub
					}

					@Override
					public X509Certificate[] getAcceptedIssuers() {
						// TODO Auto-generated method stub
						return null;
					}
				}};
				SSLContext sslContext = SSLContext.getInstance("SSL");
		        sslContext.init(null, tm, new java.security.SecureRandom());
		        SSLSocketFactory ssf = sslContext.getSocketFactory();
		        ((HttpsURLConnection)conn).setSSLSocketFactory(ssf);
			}
	    	conn.setRequestMethod("GET");
			configureConnection(conn);
			conn.connect();
	    	
			//String saveDirUrl = "D:\\temporary";//本机测试
			String saveDirUrl = Config.prefixPath+ FileInfoUtil.getContractFolder(transactionId.toString(), DateUtils.getCurrentDate());  
			
			File dir = new File(saveDirUrl);
			if (!dir.exists()) {
				try {
					dir.mkdirs();
				} catch (Exception e) {
					throw new Exception("创建目录 \"" + saveDirUrl + "\" 失败");
				}
			}
	        InputStream inStream = conn.getInputStream();
	        FileOutputStream fs = new FileOutputStream(saveDirUrl+transactionId+".zip");
	        byte[] buffer = new byte[1204];
	        int length;
	        while ((byteread = inStream.read(buffer)) != -1) {
	            bytesum += byteread;
	            System.out.println(bytesum);
	            fs.write(buffer, 0, byteread);
	        }
		return saveDirUrl;
	}

	public static void unzipFile(String directory, String saveDirUrl,Long transactionId) throws Exception
	{
	    try
	    {
	    	File zip = new File(saveDirUrl+transactionId+".zip");
	        ZipInputStream zis = new ZipInputStream(new FileInputStream(zip));
	        ZipEntry ze = zis.getNextEntry();
	        File parent = new File(directory);
	        if (!parent.exists() && !parent.mkdirs())
	        {
	            throw new Exception("创建解压目录 \"" + parent.getAbsolutePath() + "\" 失败");
	        }
	        
	        while (ze != null)
	        {
	        	String pdfName = ze.getName();
	        	if(!pdfName.contains("contract")){
	        		ze = zis.getNextEntry();
	        		continue ;
	        	}
	            String name = transactionId+"已签署.pdf";
	            File child = new File(parent, name);
	                FileOutputStream output = new FileOutputStream(child);
	            byte[] buffer = new byte[10240];
	            int bytesRead = 0;
	            while ((bytesRead = zis.read(buffer)) > 0)
	            {
	                output.write(buffer, 0, bytesRead);
	            }
	            output.flush();
	            output.close();
	                ze = zis.getNextEntry();
	            }
	            zis.close();
	    }
	    catch (IOException e)
	    {
	    }
	}
	
	
	private static void configureConnection(HttpURLConnection conn) {
		StringBuilder userAgentBuilder = new StringBuilder();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded;charset=utf-8");
	
		String sysName = getLocalHostSysName();
		String sysVersion = getLocalHostSysVersion();
		String sysArch = getLocalHostSysArch();
		String sysLangVersion = getLocalSysLangVersion();
		userAgentBuilder.append("BCCS_SDK/3.0 (");
		if (sysName !=null ) {userAgentBuilder.append(sysName).append("; ");}
		if (sysVersion !=null ) {userAgentBuilder.append(sysVersion).append("; ");}
		if (sysArch !=null ) {userAgentBuilder.append(sysArch);}
		userAgentBuilder.append(") ").append("JAVA/").append(sysLangVersion).
		append(" (Baidu Push Server SDK V.2.0.0)");
		conn.setRequestProperty("User-Agent",userAgentBuilder.toString());
	}
    
    private static String getLocalHostSysName () {
		String sysName = null;
		try {
			sysName = System.getProperty("os.name");
			return sysName;
		} catch (SecurityException e){
			return null;
		}
	}
	
	private static String getLocalHostSysArch () {
		String sysArch = null;
		try {
			sysArch = System.getProperty("os.arch");
			return sysArch;
		} catch (SecurityException e){
			return null;
		}
	}
	
	private static String getLocalHostSysVersion () {
		String sysVersion = null;
		try {
			sysVersion = System.getProperty("os.version");
			return sysVersion;
		} catch (SecurityException e){
			return null;
		}
	}
	
	private static String getLocalSysLangVersion () {
		String sysLanguageVersion = null;
		try {
			sysLanguageVersion = System.getProperty("java.version");
			return sysLanguageVersion;
		} catch (SecurityException e){
			return null;
		}
	}
        
	
	/**
	 * 
	 * @Description:合同预览
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2016年6月26日 下午9:12:13
	 */
	public static String viewContract(String signId,String docId) throws Exception {
		/*String signId = "14666773425714IQK2";
		String docId = "1466677342571AXA41";*/
		String url = sdk.getContractViewURL(signId, docId);
		System.out.println(url);
		return url;
	}

	private static byte[] getResource(String path) throws IOException {
		File e = new File(path);
		InputStream s = new FileInputStream(e);
		ArrayList<byte[]> bufferList = new ArrayList<byte[]>();
		byte[] buffer = new byte[4096];
		int read = 0;
		int total = 0;
		while ((read = s.read(buffer)) > 0) {
			byte[] b = new byte[read];
			System.arraycopy(buffer, 0, b, 0, read);
			bufferList.add(b);
			total += read;
		}
		s.close();

		byte[] result = new byte[total];
		int pos = 0;
		for (int i = 0; i < bufferList.size(); i++) { 
			byte[] b = bufferList.get(i);
			System.arraycopy(b, 0, result, pos, b.length);
			pos += b.length;
		}
		return result;
	}

	private static String dump(Object value) {
		return JSONObject.toJSONString(value);
	}

	private static void outputInfo() {
		logger.info("上上签接口调用 mid={}, host={}", mid, host);
	}
	//创建一份新合同用来进行测试
	private static Continfo[] createContract() throws Exception {
		byte[] fileData = getDemoPdf();
		ReceiveUser[] userlist = {new ReceiveUser("1234567@qq.com", "Test1", "18658177193", USER_TYPE.PERSONAL, CONTRACT_NEEDVIDEO.NONE, false)};
		SendUser senduser = new SendUser("22345678@163.com", "Test2", "18658177193", 3, false, USER_TYPE.PERSONAL, false, "title", "");
		lastContinfoList = sdk.sjdsendcontractdocUpload(userlist, senduser, fileData);
		return lastContinfoList;
	}
	private static byte[] getDemoPdf() throws IOException {
		return getResource("f:/test.pdf");
	}
	
	public static String getContractArbitrationURL(String mobile,String signId){
		try{
			//获取公证存证页面URL
			Map<String, Object> result = sdk.getContractArbitrationURL(signId, USER_TYPE.PERSONAL, "", mobile);
			JSONObject result1 =JSONArray.parseObject(result.get("response").toString());
			result1 = JSONArray.parseObject(result1.get("response").toString());
			result1 = JSONArray.parseObject(result1.get("content").toString());
			return result1.getString("url");
		}catch(Exception e){
			logger.info("获取上上签保全URL出错{}" + e);
		}
		return null;
	}
	
	public static void main(String[]args) throws Exception{
		String pem="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJDPD/xTiAWankvT7WQI1nB/a+5u7wWNWrjsFLQqFMEVXUP1cvCe2h99E8jMehuqKuZTSWy21sfDSdC1BhFFHIinHGxG37YKL56soLLUKqD3sND3FEs0gdDEAcOg2YzaHZf6OwJiY3NnPOM/o9/kvWcSalab2Q+zNmeo9wzuKcWFAgMBAAECgYBv2dBSKT8ufPB6R5bcpsrkGDgI8lzjX/zMS2Xuh3aCcXsZq/P9EeYPXnAysGY9CiKax4g2Vb/uitRwRfMK0eQoK95fGH5L1SnjHfj0DGD1w8ea90jDrvyvx/sS3ArwuZ45vtgw+xmV2dNV+sGd8ZTu5ErGnUd8JLcVV7+JzEbF4QJBAMLQLzdmhiK++delo7g1+C++uNEnzR8XSR8KjyNCEA3oblaL48HK8eZvGDAaMuvygz1Fztj6TH2lVnzyFEt3wykCQQC+Sk/ZPPRQGExkfPuxZpJfuvUC53Sp0pKVtCrxKN9s3BLH0WiENj/+yu6L6UshH87S6gYvhLEdFWsimTW6MXb9AkAvyXmLkW6d31LR/yOl8DctHw+e3rCGS+P35VMvRulBQB6wxCfeRbYI3H+GbSbIkfh3c0RGHT/eIkyQ4aDw0jPhAkBNLezG6n8ZAVHyq/KV2ElkBHsdi2z4+Aw2JdYI7A/6oiQH4XpnrW01VW99VckADVuglgSeP05qwmYqqUNuWmOtAkAwGbR6fD6v2fHVVUNGZn57w2xHApTuwaG1Dv/KNNn4GR7Ez2Ajt6C2rCK+tUIfRSLciQ0i8i+e6o3zkjirGQDU";
		String host="https://www.bestsign.info";
		String mid="680ca993799a48b2b9df2f5a2d61f05c";
		sdk = BestSignSDK.getInstance(mid, pem, host);
		sdk.setLogDir("/mnt/logs/yourong/bestSign/");
		sdk.setDebugLevel(DEBUG_LEVEL.INFO);
		//
		// TODO 当手动签署的人是企业用户的场景，是否需要调用公章签署的api
		// 自动签这份合同
		String signId = "1493274077212QSXO2";
		String mobile = "18767146132";
		int pagenum = 5;
		float signX = 0.38f;
		float signY = 0.05f;
		logger.info("平台自动签署: signId={}, mobile={}",signId, mobile);

		boolean openflag = true;
		AutoSignbyCAResult result = sdk.AutoSignbyCA(signId, mobile, pagenum, signX, signY, openflag);
		// {"response":{"info":{"code":100000},"content":{"code":"100000","fsdId":"14666773425714IQK2","fmid":"5bccd7b6ed574df7acc12275ddb4ad4c","docID":"1466677342571AXA41","returnurl":"","vmsg":"已完成签字"}}}
		System.out.println(result);
	}
	
}
