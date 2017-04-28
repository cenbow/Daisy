package com.yourong.common.thirdparty.sinapay.common.enums;

/**
 * <p>异常信息</p>
 * @author Wallis Wang
 * @version $Id: ErrorCode.java, v 0.1 2014年5月16日 下午3:22:45 wangqiang Exp $
 */
public enum ErrorCode {

    PAY_FRONT_EXCEPTION("PAY_FRONT_EXCEPTION", "支付网关前置异常 "),
    ORDER_NOT_EXIST("ORDER_NOT_EXIST", "订单不存在 ");

    private String errorCode;

    private String errorMsg;

    private ErrorCode(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public String code() {
        return errorCode;
    }

    public String msg() {
        return errorMsg;
    }
}
