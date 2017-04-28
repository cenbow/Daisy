/**
 * 
 */
package com.yourong.common.thirdparty.sinapay.member.exception;

/**
 * <p>会员网关调用失败
 * @author Your name
 * @version $Id: MemberGatewayInvokeFailureException.java, v 0.1 2014年5月21日 上午11:25:39 guoyongqiang Exp $
 */
public class MemberGatewayInvokeFailureException extends Exception {
    private static final long serialVersionUID = 1L;

    public MemberGatewayInvokeFailureException() {
        super();
    }

    public MemberGatewayInvokeFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberGatewayInvokeFailureException(String message) {
        super(message);
    }

    public MemberGatewayInvokeFailureException(Throwable cause) {
        super(cause);
    }

}
