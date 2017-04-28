package com.yourong.core.repayment.manager.impl.HostingPayTradeFinishedListen;

import com.google.common.eventbus.Subscribe;
import com.yourong.common.eventbus.AsyncEventListener;
import com.yourong.common.util.DateUtils;
import com.yourong.core.common.MessageClient;
import com.yourong.core.repayment.model.HostingPayTradeFinishedListenerObject;
import com.yourong.core.tc.model.TransactionInterest;
import org.springframework.stereotype.Component;

/**
 * 代付成功后发送消息通知
 * Created by  on 2015/7/29.
 */
@Component
public class HostingPaySucessSendMessageListen implements AsyncEventListener<HostingPayTradeFinishedListenerObject> {

    @Override
    @Subscribe
    public void handle(HostingPayTradeFinishedListenerObject hostingPayTradeFinishedListenerObject) {
        TransactionInterest transactionInterest = hostingPayTradeFinishedListenerObject.getTransactionInterest();
        //发送利息本金支付通知
        MessageClient.sendMsgForInterestOrCapital(DateUtils.getCurrentDate(), transactionInterest.getTransactionId(), transactionInterest.getRealPayInterest(), transactionInterest.getRealPayPrincipal(), transactionInterest.getMemberId());
    }
}
