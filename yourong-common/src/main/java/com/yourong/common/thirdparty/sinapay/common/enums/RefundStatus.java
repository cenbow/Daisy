package com.yourong.common.thirdparty.sinapay.common.enums;

/**
 * <p>退款</p>
 * @author Wallis Wang
 * @version $Id: RefundStatus.java, v 0.1 2014年6月24日 下午4:05:56 wangqiang Exp $
 */
public enum RefundStatus {
    /**
     * 等待退款（处理中）
     */
    WAIT_REFUND,
    /**
     * 已扣款（处理中）
     */
    PAY_FINISHED,
    /**
     * 退款成功
     */
    SUCCESS,
    /**
     * 退款失败
     */
    FAILED
}
