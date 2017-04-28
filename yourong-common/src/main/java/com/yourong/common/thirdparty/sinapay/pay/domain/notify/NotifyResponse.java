package com.yourong.common.thirdparty.sinapay.pay.domain.notify;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.pay.core.common.Ignore;

/**
 * <p>异步通知响应</p>
 * @author Wallis Wang
 * @version $Id: NotifyResponse.java, v 0.1 2014年6月20日 上午11:00:11 wangqiang Exp $
 */
public class NotifyResponse {

    /**
     * 通知ID
     */
    @NotNull
    private String notifyId;

    /**
     * 通知时间
     */
    @NotNull
    private String notifyTime;

    /**
     * 通知类型
     */
    @NotNull
    private String notifyType;

    /**
     * 字符集
     */
    @Expose
    @SerializedName("_input_charset")
    @NotNull
    private String inputCharset;

    /**
     * 通知版本
     */
    @NotNull
    private String version;

    /**
     * 签名内容
     */
    @Ignore
    private String sign;

    /**
     * 签名类型
     */
    @Ignore
    private String signType;

    /**
     * 备注
     */
    private String memo;

    /**
     * 错误原因
     */
    private String errorCode;

    /**
     * 错误码
     */
    private String errorMessage;

    /**
     * 响应CODE
     */
    private String responseCode;

    /**
     * 响应MESSAGE
     */
    private String responseMessage;

    public String getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId;
    }

    public String getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(String notifyTime) {
        this.notifyTime = notifyTime;
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public String getInputCharset() {
        return inputCharset;
    }

    public void setInputCharset(String inputCharset) {
        this.inputCharset = inputCharset;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
    
    
}
