package com.yourong.common.thirdparty.sinapay.pay.domain.respone;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.pay.core.common.Ignore;

/**
 * <p>响应结果</p>
 * @author Wallis Wang
 * @version $Id: BasicResponse.java, v 0.1 2014年5月15日 下午6:58:42 wangqiang Exp $
 */
public class PayResponse {

    /**
     * 请求时间
     * 发起请求时间，格式yyyyMMddhhmmss(20140101120401)
     */
    @NotNull
    private String responseTime;

    /**
     * 合作者身份ID 
     */
    private String partnerId;

    /**
     * 参数编码字符集
     */
    @Expose
    @SerializedName("_input_charset")
    @NotNull
    private String inputCharset;

    /**
     * 签名
     */
    @Ignore
    @NotNull
    private String sign;

    /**
     * 签名方式
     */
    @Ignore
    @NotNull
    private String signType;

    /**
     * 签名版本号
     */
    @Ignore
    private String signVersion;

    /**
     * 响应码
     */
    private String responseCode;

    /**
     * 响应信息
     */
    private String responseMessage;

    /**
     * 备注
     */
    private String memo;

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getInputCharset() {
        return inputCharset;
    }

    public void setInputCharset(String inputCharset) {
        this.inputCharset = inputCharset;
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

    public String getSignVersion() {
        return signVersion;
    }

    public void setSignVersion(String signVersion) {
        this.signVersion = signVersion;
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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
