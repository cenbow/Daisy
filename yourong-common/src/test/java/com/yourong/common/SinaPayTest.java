package com.yourong.common;

import org.junit.Before;
import org.junit.Test;

import com.yourong.common.enums.SinaPayEnum;
import com.yourong.common.thirdparty.pay.CarListResponse;
import com.yourong.common.thirdparty.pay.PayMentService;
import com.yourong.common.thirdparty.pay.PayResponse;
import com.yourong.common.thirdparty.pay.sina.SinaPayConfig;
import com.yourong.common.thirdparty.pay.sina.SinaPayService;

public class SinaPayTest {
    private 	PayMentService  payMentService;
    @Before
    public void initPayService(){

	/*
	 * sinaPay.version=1.0 sinaPay.partnerId=200003670082
	 * sinaPay.inputCharset=utf-8 sinaPay.signType=MD5
	 * sinaPay.signVersion=1.0 sinaPay.notifyUrl=00000000
	 * sinaPay.returnUrl=00000000
	 * sinaPay.memberGate=https://testgate.pay.sina.com.cn/mgs/gateway.do
	 * sinaPay.orderGate=https://testgate.pay.sina.com.cn/mas/gateway.do
	 * sinaPay.md5Key=1234567890qwertyuiopasdfghjklzxc
	 */
	SinaPayConfig.setInputCharset("utf-8");
	SinaPayConfig.setVersion("1.0"); 
	SinaPayConfig.setPartnerId("200003670082");
	SinaPayConfig.setSignType("MD5");
	SinaPayConfig.setSignVersion("1.0");	
	SinaPayConfig.setMemberGate("https://testgate.pay.sina.com.cn/mgs/gateway.do");
	SinaPayConfig.setOrderGate("https://testgate.pay.sina.com.cn/mas/gateway.do");
	SinaPayConfig.setMd5Key("1234567890qwertyuiopasdfghjklzxc");	
	payMentService = new SinaPayService();
    } 
   // @Test
    public void queryBankCardTest(){
	String name =  "10000";
	CarListResponse queryBankCard = payMentService.queryBankCard(name);
	System.out.println(queryBankCard.getCard_list());
    }
    @Test
    public void createActivateMemberTest(){
	String name= "1232131";
	PayResponse res = payMentService.createActivateMember(name, SinaPayEnum.MEMBER_1.getCode(), "192.168.0.60", null);
	System.out.println(res.getResponse_message());
    }
    
    //@Test
    public void setRealNameTest(){
	String filePath = "D:/key/rsa_public.crt";
	String realName = "oyK8oPNAPIP4thxzybBVkkCe8qkB5bnZmmZQgOSe4Fb%2FfNm9ZGePsZ83iOYCTzbqe4RtH65mNFBxPtMNH1RnbpzcQMW7h8L5FlFoz5ls3USPhrJmAaBxvm6r2qEhD1cVy2P53oG6375ta4S71aIF6F8UsShfIl9YQG0eUnFIKas%3D";
	String memberid = "10003";
	String certificateNumber = "mj%2FCURKW5tA8nU%2F8gspqKi2hJAiubYToBVMUCSLWL44BaQNyYSqTmSW5HneYpuYdHhvicciRGBh9yvcnmwKIrSx%2Fkpi5lH4%2FQWAm%2BqZo%2BbEfeSw4RgRjkoDpJ0MA5H%2FK7RDWBTO%2BzNOmkQMQkFW8jJfmDjfpSIIcdGAMWfPVrZA%3D";
	String certificate = SinaPayEnum.CERTIFICATE_IC.getCode();
	PayResponse setRealName = payMentService.setRealName(memberid, realName, certificate,certificateNumber);
	System.out.println(setRealName.getResponse_message());
    }
    
    
    

}
