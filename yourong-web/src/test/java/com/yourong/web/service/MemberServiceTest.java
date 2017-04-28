package com.yourong.web.service;

import java.math.BigDecimal;

import org.apache.commons.httpclient.HttpException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yourong.common.thirdparty.sinapay.SinaCashDeskClient;
import com.yourong.common.thirdparty.sinapay.pay.exceptions.PayFrontException;
import com.yourong.web.BaseWebControllerTest;
import com.yourong.web.utils.WebPropertiesUtil;

/**
 * Created by peng.yong on 2016/1/20.
 */
public class MemberServiceTest  extends BaseWebControllerTest {

    @Autowired
    private  MemberService memberService;
    
    @Autowired
    private SinaCashDeskClient sinaCashDeskClient;
    
    @Test
    public void selectMemberByIDTest() throws PayFrontException, HttpException{
//        Member member = memberService.selectByPrimaryKey(110800000201L);
    	
//    	String str = sinaCashDeskClient.createHostingDeposit("20160808195749", 110800001503l, "192.168.0.60", new BigDecimal(500), WebPropertiesUtil.getSinaCashDeskReturnUrl());
//    	String str = sinaCashDeskClient.createHostingWithdraw("20160808194234", 110800001503l, "192.168.0.60", new BigDecimal(500), WebPropertiesUtil.getSinaCashDeskReturnUrl());
    }
    
}
