package com.yourong.core.repayment.manager.impl.HostingPayTradeListen;

import com.yourong.common.eventbus.EventListener;
import com.yourong.core.repayment.model.HostingPayTradeListenerObject;

/**
 * 代付记录， 在第三方支付接口，查询没有记录
 * Created by Administrator on 2015/7/28.
 */
public class HostingPayTradeIsNotExitListen implements EventListener<HostingPayTradeListenerObject> {
    @Override
    public void handle(HostingPayTradeListenerObject hostingPayTradeListenerObject) {

    }
}
