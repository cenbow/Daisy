package com.yourong.core;

import java.math.BigDecimal;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.RefundTradeResult;
import com.yourong.common.util.SerialNumberUtil;

public class HostingRefundTradeTest extends BaseTest {
	@Autowired
    private SinaPayClient sinaPayClient;


	@Test
    @Rollback(false)
    public void createHostingRefund() {
		String refundNo = SerialNumberUtil.generateRefundTradeaNo();
		String ip="";
        try {
        	ResultDto<RefundTradeResult> resultDto = sinaPayClient.createHostingRefund(refundNo,"YRCT20160217100246603", new BigDecimal(1000), "退款测试",ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
