package com.yourong.core.repayment.model;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.TradeItem;
import com.yourong.core.tc.model.HostingPayTrade;

/**
 * 代付事件对象
 * Created by py on 2015/7/23.
 */
public class HostingPayTradeListenerObject extends AbstractBaseObject {
    private HostingPayTrade hostingPayTrade;
    private TradeItem tradeItem;

    public TradeItem getTradeItem() {
        return tradeItem;
    }

    public void setTradeItem(TradeItem tradeItem) {
        this.tradeItem = tradeItem;
    }

    public HostingPayTrade getHostingPayTrade() {
        return hostingPayTrade;
    }

    public void setHostingPayTrade(HostingPayTrade hostingPayTrade) {
        this.hostingPayTrade = hostingPayTrade;
    }
}
