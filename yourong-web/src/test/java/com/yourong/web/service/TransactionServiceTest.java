/**
 * 
 */
package com.yourong.web.service;

import java.math.BigDecimal;

import org.apache.commons.httpclient.HttpException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yourong.common.thirdparty.sinapay.pay.exceptions.PayFrontException;
import com.yourong.web.BaseWebControllerTest;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年9月8日下午4:04:19
 */
public class TransactionServiceTest extends BaseWebControllerTest{

	
	
	@Autowired
    private  TransactionService transactionService;
    
    
    @Test
    public void test() throws PayFrontException, HttpException{
    	
    	Long memberId=110800001560L;
    	Long transactionId=888801002211L;
    	BigDecimal transferAmount=new BigDecimal(4981.32);
    	transactionService.transferToProject(memberId, transactionId,  transferAmount);
    	
    }
    
}
