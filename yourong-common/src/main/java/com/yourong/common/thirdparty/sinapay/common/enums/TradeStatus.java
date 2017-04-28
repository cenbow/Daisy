package com.yourong.common.thirdparty.sinapay.common.enums;

/**
* <p>处理状态</p>
* @author Wallis Wang
* @version $Id: DealStatus.java, v 0.1 2014年5月15日 下午6:38:30 wangqiang Exp $
*/
public enum TradeStatus {
    /**
     * 等待买家付款
     */
    WAIT_PAY,
    /**
     * 买家已付款
     */
    PAY_FINISHED,
    /**
     * 交易成功
     */
    TRADE_SUCCESS,
    /**
     * 交易失败
     */
    TRADE_FAILED,
    /**
     * 交易结束
     */
    TRADE_FINISHED,
    /**
     * 交易关闭（合作方通过调用交易取消接口来关闭）
     */
    TRADE_CLOSED,
    /**
     * 交易不存在
     */
	TRADE_NOTEXITL,
	/**
	 * 代收冻结成功（商户通知）
	 */
	PRE_AUTH_APPLY_SUCCESS,
	/**
	 * 代收撤销成功（商户通知）
	 */
	PRE_AUTH_CANCELED,

}