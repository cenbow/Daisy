/**
 * 
 */
package com.yourong.common.thirdparty.sinapay.member.exception;

/**
 * 返回值中缺失了指定key的值。
 * @author guoyongqiang
 * @version $Id: ExpectedValueLostException.java, v 0.1 2014年5月21日 下午5:39:31 guoyongqiang Exp $
 */
public class ExpectedValueLostException extends MemberGatewayInvokeFailureException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     * @param key 期望有值的key
     */
    public ExpectedValueLostException(String key) {
        super("期望的值丢失:" + key);
    }

}
