package com.yourong.common.thirdparty.sinapay.pay.domain.result;

/**
 * <p>支付结果返回对象</p>
 * @author Wallis Wang
 * @version $Id: ResultDto.java, v 0.1 2014年5月15日 下午6:47:33 wangqiang Exp $
 * @param <T>
 */
public class PayFrontResult<T> {

    /**
     * 结果对象
     */
    private T      module;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 备注
     */
    private String memo;

    public T getModule() {
        return module;
    }

    public void setModule(T module) {
        this.module = module;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
