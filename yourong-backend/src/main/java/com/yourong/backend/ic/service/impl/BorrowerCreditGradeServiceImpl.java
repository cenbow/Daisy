package com.yourong.backend.ic.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yourong.backend.ic.service.BorrowerCreditGradeService;
import com.yourong.common.baidu.yun.utility.MessageDigestUtility;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.http.common.HttpRequest;
import com.yourong.common.http.common.HttpResponse;
import com.yourong.common.http.common.Method;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.util.sign.RSA;
import com.yourong.common.util.Base64;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.core.ic.manager.BorrowerCreditGradeManager;
import com.yourong.core.ic.model.BorrowerCreditGrade;
import com.yourong.core.ic.model.query.BorrowerCreditGradeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by alan.zheng on 2017/3/2.
 */
@Service
public class BorrowerCreditGradeServiceImpl implements BorrowerCreditGradeService {
    private static Logger logger = LoggerFactory
            .getLogger(BorrowerCreditGradeServiceImpl.class);
    @Autowired
    private BorrowerCreditGradeManager borrowerCreditGradeManager;

    @Override
    public Page<BorrowerCreditGrade> queryPageBorrowerCreditGrade(BorrowerCreditGradeQuery query) {
        return borrowerCreditGradeManager.queryPageBorrowerCreditGrade(query);
    }

    @Override
    public ResultDO<BorrowerCreditGrade> queryBorrowerCredit(Long memberId) {
        ResultDO<BorrowerCreditGrade> resultDO=new ResultDO<BorrowerCreditGrade>();
        BorrowerCreditGrade borrowerCreditGrade=borrowerCreditGradeManager.queryByBorrowerId(memberId);
        if (borrowerCreditGrade==null){
            return null;
        }
        try {
            String url= PropertiesUtil.getProperties("wex.open.api.server");
            String publickey=PropertiesUtil.getProperties("wex.rsa.publicKey");
            String merchantid=PropertiesUtil.getProperties("wex.merchant.id");
            String requestno= UUID.randomUUID().toString().replace("-","");
            String cert_no= Base64.encode(RSA.encryptByPublicKey(borrowerCreditGrade.getIdentityNumber().getBytes("UTF-8"),publickey));
            String phone_no=Base64.encode(RSA.encryptByPublicKey(borrowerCreditGrade.getBorrowerMobile().toString().getBytes("UTF-8"),publickey));
            String user_name=Base64.encode(RSA.encryptByPublicKey(borrowerCreditGrade.getBorrowerTrueName().getBytes("UTF-8"),publickey));
            String request_time= DateUtils.getStrFromDate(new Date(),"yyyyMMddHHmmss");
            String sign= sign(cert_no,merchantid,phone_no,user_name,requestno,request_time);
            Map<String,String> map=parameter(cert_no,phone_no,user_name,requestno,request_time,sign);
            HttpResponse res= request(url,map);
            String str= tostring(res.getInputStream());
            String redata= str.substring(5,str.indexOf("&sign"));
            byte[] databyte= Base64.decode(redata);
            String data=new String(databyte,"utf-8");
            JSONObject jsonObject = JSONObject.parseObject(data);
            if (jsonObject!=null&&jsonObject.containsKey("content")){
                JSONObject content= (JSONObject)jsonObject.get("content");
                if (content!=null&&content.containsKey("is_black_list")){
                    if ("Y".equals(content.get("is_black_list"))){
                        borrowerCreditGrade.setBlackLevel(1);
                    }else {
                        borrowerCreditGrade.setBlackLevel(0);
                    }
                }
                if (content!=null&&content.containsKey("black_reason")){
                    borrowerCreditGrade.setBlackReason((String) content.get("black_reason"));
                }else {
                    borrowerCreditGrade.setBlackReason(null);
                }
            }
            Date querydate=DateUtils.getDateFromString((String)jsonObject.get("response_time"),"yyyyMMddHHmmss");
            borrowerCreditGrade.setBlackQueryTime(querydate);
            borrowerCreditGrade.setBlackQueryTimeStr(DateUtils.getStrFromDate(querydate,"yyyy-MM-dd HH:mm:ss"));

            borrowerCreditGradeManager.updateBlackInfo(borrowerCreditGrade);

        } catch (Exception e) {
            logger.error("查询用户百度评级异常", e);
            resultDO.setSuccess(false);
            return resultDO;
        }
        resultDO.setResult(borrowerCreditGrade);
        return resultDO;
    }

    @Override
    public boolean updateCreditLevel(String creditLevel, Long borrowerId) {
        try {
            return borrowerCreditGradeManager.updateCreditLevel(creditLevel,borrowerId,new Date());
        } catch (Exception e) {
            logger.error("用户信用等级updateCreditLevel异常", e);
        }
        return false;
    }

    @Override
    public boolean saveBorrowerCreditGrade(Long borrowerId) {
        try {
            return borrowerCreditGradeManager.saveBorrowerCreditGrade(borrowerId);
        } catch (Exception e) {
            logger.error("用户信用等级saveBorrowerCreditGrade异常", e);
        }
        return false;
    }

    @Override
    public BorrowerCreditGrade queryByBorrowerId(Long borrowerId) {
        try {
            return borrowerCreditGradeManager.queryByBorrowerId(borrowerId);
        } catch (Exception e) {
            logger.error("用户信用等级queryByBorrowerId异常", e);
        }
        return null;
    }

    /**
     * 请求签名
     * @param cert_no
     * @param phone_no
     * @param user_name
     * @param requestno
     * @param request_time
     * @return
     */
    private String sign(String cert_no,String merchantid,String phone_no,String user_name,String requestno,String request_time){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("cert_no=");
        stringBuilder.append(cert_no);
        stringBuilder.append("&");
        stringBuilder.append("channel_code=BD10101");
        stringBuilder.append("&");
        stringBuilder.append("partner_id=");
        stringBuilder.append(merchantid);
        stringBuilder.append("&");
        stringBuilder.append("phone_no=");
        stringBuilder.append(phone_no);
        stringBuilder.append("&");
        stringBuilder.append("platform_type=PC");
        stringBuilder.append("&");
        stringBuilder.append("request_no=");
        stringBuilder.append(requestno);
        stringBuilder.append("&");
        stringBuilder.append("request_time=");
        stringBuilder.append(request_time);
        stringBuilder.append("&");
        stringBuilder.append("service=credit_check_back_list_get");
        stringBuilder.append("&");
        stringBuilder.append("user_name=");
        stringBuilder.append(user_name);
        stringBuilder.append("&");
        stringBuilder.append("version=1.0");
        String md5Salt= PropertiesUtil.getProperties("wex.md5Salt");
        stringBuilder.append(md5Salt);
        String sign= MessageDigestUtility.toMD5Hex(stringBuilder.toString());
        return sign;
    }

    /**
     * 封装请求参数
     * @param cert_no
     * @param phone_no
     * @param user_name
     * @param requestno
     * @param request_time
     * @return
     */
    private Map<String,String> parameter(String cert_no,String phone_no,String user_name,String requestno,String request_time,String sign){
        Map<String,String> map=new HashMap<>();
        try {
            map.put("cert_no", URLEncoder.encode(URLEncoder.encode(cert_no,"UTF-8"),"UTF-8"));
            map.put("channel_code",URLEncoder.encode(URLEncoder.encode("BD10101","UTF-8"),"UTF-8"));
            map.put("partner_id",URLEncoder.encode(URLEncoder.encode("200007699877","UTF-8"),"UTF-8"));
            map.put("phone_no",URLEncoder.encode(URLEncoder.encode(phone_no,"UTF-8"),"UTF-8"));
            map.put("platform_type",URLEncoder.encode(URLEncoder.encode("PC","UTF-8"),"UTF-8"));
            map.put("request_no",URLEncoder.encode(URLEncoder.encode(requestno,"UTF-8"),"UTF-8"));
            map.put("request_time",URLEncoder.encode(URLEncoder.encode(request_time,"UTF-8"),"UTF-8"));
            map.put("service",URLEncoder.encode(URLEncoder.encode("credit_check_back_list_get","UTF-8"),"UTF-8"));
            map.put("sign",URLEncoder.encode(URLEncoder.encode(sign,"UTF-8"),"UTF-8"));
            map.put("sign_type",URLEncoder.encode(URLEncoder.encode("MD5","UTF-8"),"UTF-8"));
            map.put("user_name",URLEncoder.encode(URLEncoder.encode(user_name,"UTF-8"),"UTF-8"));
            map.put("version",URLEncoder.encode(URLEncoder.encode("1.0","UTF-8"),"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 处理请求
     * @param map
     * @return
     */
    private static HttpResponse request(String url,Map<String, String> map){
        HttpRequest httpRequest=new HttpRequest(url);
        if (!map.isEmpty()){
            for (String key:map.keySet()) {
                httpRequest.addParam(key,map.get(key));
            }
        }
        httpRequest.setMethod(Method.POST);
        HttpResponse httpResponse=null;
        try {
            httpResponse= httpRequest.exeute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return httpResponse;
    }

    /**
     * 解析InputStream
     * @param stream
     * @return
     */
    private static String tostring(InputStream stream){
        StringBuffer bs = new StringBuffer();
        try {
            BufferedReader buffer = new BufferedReader(new InputStreamReader(stream,"utf-8"));
            String l = null;
            while((l=buffer.readLine())!=null){
                bs.append(l);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bs.toString();
    }
}
