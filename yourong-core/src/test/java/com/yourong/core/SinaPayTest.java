package com.yourong.core;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.BatchTradeNotifyMode;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeCode;
import com.yourong.common.thirdparty.sinapay.member.domain.result.AccountDetail;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.TradeArgs;
import com.yourong.common.util.SerialNumberUtil;

/**
 * Created by py on 2015/7/16.
 */
public class SinaPayTest extends  BaseTest {
    @Autowired
    private SinaPayClient sinaPayClient;

    //@Test
    public void queryHostingBatchTrade(){
        String batchNo="YRBP20150722000248366";
    }

    //@Test	
    public void queryBalanceTest(){
        Long id = 110800000244L;
        try {
            ResultDto<?> resultDto = sinaPayClient.queryBalance(id);
            System.out.println(resultDto.getModule());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //@Test
    public void createBatchPayTradeTest(){
        String batchPayNo = SerialNumberUtil.generateBatchPayTradeaNo();
        List tradeList = Lists.newArrayList();
        TradeArgs tradeArgs = new TradeArgs();
        tradeArgs.setAccountType(AccountType.SAVING_POT);
        tradeArgs.setIdType(IdType.UID);
        tradeArgs.setMoney(new Money("10.0"));
        tradeArgs.setPayeeId(SerialNumberUtil.generateIdentityId(110800000244L));
        tradeArgs.setRemark("test批量代付");
        String generateCollectTradeaNo = SerialNumberUtil.generateCollectTradeaNo();
        System.out.println(generateCollectTradeaNo);
        tradeArgs.setTradeNo(generateCollectTradeaNo);
        String ip="";
        tradeList.add(tradeArgs);
        try {
            ResultDto<?> batchPayTrade = sinaPayClient.createBatchPayTrade(batchPayNo,"测试批量代付",ip,tradeList, TradeCode.PAY_TO_INVESTOR, BatchTradeNotifyMode.single_notify);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   // @Test
    public void queryAccountDetailsTest(){
        Long id = 810800001332L;
        Date start = new Date();
             //   DateUtils.getDateFromString(DateUtils.DATE_FMT_3,"2015-09-08");
        Date end =  new Date();
             //   DateUtils.getDateFromString(DateUtils.DATE_FMT_3,"2015-09-09");
        try {
            ResultDto<AccountDetail> resultDto = sinaPayClient.queryAccountDetails(id,start,end );
            AccountDetail module = resultDto.getModule();
            System.out.println(module.getTotalIncome());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 
     * @Description:冻结、解冻结果  
     * @author: chaisen
     * @time:2016年7月28日 上午9:51:48
     */
    @Test
    public void queryCtrlResultTest(){
        String outFreezeNo = SerialNumberUtil.generateFreezeOutCtrlNo();
        System.out.println(outFreezeNo);
        Long memberId=110800001472L;
        BigDecimal amount=new BigDecimal(10000);
        String summary="1";
        try {
           // ResultDto<?> batchPayTrade = sinaPayClient.queryCtrlResult(batchPayNo);
        	 //ResultDto<?> result =sinaPayClient.freezeBalance(memberId, outFreezeNo, amount, summary);
        	 sinaPayClient.queryCtrlResult("YRDJ20160801183647410");
        	//sinaPayClient.unfreezeBalance(memberId, outFreezeNo, outUnFreezeNo, amount, summary);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
