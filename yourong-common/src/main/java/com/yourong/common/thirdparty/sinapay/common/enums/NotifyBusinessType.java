package com.yourong.common.thirdparty.sinapay.common.enums;

/**
 * 6.17	通知业务类型
 * @author Leon Ray
 * 2016年7月19日-下午1:44:07
 */
public enum NotifyBusinessType {

    /**
     * 交易结果通知
     */
	trade_status_sync,
    /**
     * 交易退款结果通知
     */
	refund_status_sync,
	/**
	 * 充值结果通知
	 */
	deposit_status_sync,
	/**
	 * 出款结果通知
	 */
	withdraw_status_sync,
	/**
	 * 批量交易结果通知
	 */
	batch_trade_status_sync,
	/**
	 * 审核结果通知
	 */
	audit_status_sync,
	/**
	 * 批次结算文件处理结果通知
	 */
	batch_file_settle_sync;
}
