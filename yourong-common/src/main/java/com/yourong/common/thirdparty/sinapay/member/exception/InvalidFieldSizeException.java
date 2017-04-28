/**
 * 
 */
package com.yourong.common.thirdparty.sinapay.member.exception;

/**
 * 返回值中指定参数的值无效。
 * @author guoyongqiang
 * @version $Id: ExpectedValueLostException.java, v 0.1 2014年5月21日 下午5:39:31 guoyongqiang Exp $
 */
public class InvalidFieldSizeException extends MemberGatewayInvokeFailureException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     * @param key 参数名
     * @param value 参数返回值
     */
    public InvalidFieldSizeException(String key, String value, int expected, int actual) {
        super("无效的值:" + key + " - " + actual);//FIXME 需要完善
    }

}
