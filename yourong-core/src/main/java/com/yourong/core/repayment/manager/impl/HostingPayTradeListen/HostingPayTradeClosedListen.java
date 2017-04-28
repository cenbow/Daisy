package com.yourong.core.repayment.manager.impl.HostingPayTradeListen;

import com.google.common.eventbus.Subscribe;
import com.yourong.common.eventbus.EventListener;
import com.yourong.core.repayment.model.HostingPayTradeListenerObject;
import org.springframework.stereotype.Component;

/**
 * 代付记录关闭，处理业务逻辑
 * Created by Administrator on 2015/7/28.
 */
@Component
public class HostingPayTradeClosedListen implements EventListener<HostingPayTradeListenerObject> {

    @Override
    @Subscribe
    public void handle(HostingPayTradeListenerObject hostingPayTradeListenerObject) {

    }
}
