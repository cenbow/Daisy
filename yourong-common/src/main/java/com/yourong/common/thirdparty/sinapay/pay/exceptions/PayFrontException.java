package com.yourong.common.thirdparty.sinapay.pay.exceptions;

/**
 * <p>钱包前置异常</p>
 * @author Wallis Wang
 * @version $Id: PayFrontException.java, v 0.1 2014年5月16日 上午11:44:59 wangqiang Exp $
 */
public class PayFrontException extends Exception {

    private static final long serialVersionUID = -636885681437783835L;

    public PayFrontException() {
        super();
    }

    public PayFrontException(String message, Throwable cause) {
        super(message, cause);
    }

    public PayFrontException(String message) {
        super(message);
    }

    public PayFrontException(Throwable cause) {
        super(cause);
    }
}
