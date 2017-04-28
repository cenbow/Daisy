package com.yourong.common.thirdparty.sinapay.common.enums;

/**
 * <p>结果类型，成功还是失败</p>
 * @author Wallis Wang
 * @version $Id: ResultType.java, v 0.1 2014年5月16日 下午6:55:06 wangqiang Exp $
 */
public enum ResultType {

    /**
     * 成功
     */
    T(true),
    /**
     * 失败
     */
    F(false);

    private boolean result;

    private ResultType(boolean result) {
        this.result = result;
    }

    public boolean result() {
        return result;
    }

}
