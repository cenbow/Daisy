package com.yourong.core.repayment.model;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.tc.model.HostingCollectTrade;

/**
 * 代收事件对象
 * Created by py on 2015/7/23.
 */
public class HostingCollectTradeListenerObject extends AbstractBaseObject {
    /**
     * 本地数据库查询代收对象
     */
    private HostingCollectTrade hostingCollectTrade;
    /**
     * 交易号
     */
    private String tradeNo;
    /**
     * 交易状态
     */
    private String tradeStatus;
    /**
     * 备注
     */
    private String remark;

    public HostingCollectTrade getHostingCollectTrade() {
        return hostingCollectTrade;
    }

    public void setHostingCollectTrade(HostingCollectTrade hostingCollectTrade) {
        this.hostingCollectTrade = hostingCollectTrade;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
