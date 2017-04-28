package com.yourong.common.thirdparty.sinapay.common.enums;

/**
 * 批次状态
 * @author Leon Ray
 * 2016年7月19日-下午1:37:07
 */
public enum BatchTradeStatus {
    /**
     * 待处理(系统不会异步通知)
     */
	WAIT_PROCESS,
    /**
     * 处理中(系统不会异步通知)
     */
	PROCESSING,
    /**
     * 处理结束(系统会异步通知)
     */
	FINISHED,

}