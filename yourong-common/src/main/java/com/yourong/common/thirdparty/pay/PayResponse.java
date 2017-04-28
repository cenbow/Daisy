package com.yourong.common.thirdparty.pay;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 资金托管平台返回结果集
 * 
 * @author Administrator
 *
 *
 */
public class PayResponse extends AbstractBaseObject {

    /**
     * 
     */
    private static final long serialVersionUID = 7291505960565684042L;
    /**
     * 响应时间(非空) 发起响应时间，格式yyyyMMddhhmmss 非空 20140101120401
     */
    private String response_time;
    /**
     * 合作者身份ID(非空) 签约合作方的钱包唯一用户号。 非空 2000001159940003
     */
    private String partner_id;
    /**
     * 参数编码字符集(非空) 商户网站使用的编码格式，如utf-8、gbk、gb2312等。 非空 UTF-8
     */
    private String _input_charset;
    /**
     * 签名(非空)
     */
    private String sign;
    /**
     * 签名方式(非空) 签名方式支持RSA、MD5。建议使用MD5
     */
    private String sign_type;
    /**
     * 签名版本号(可空) 签名密钥版本，默认1.0
     */
    private String sign_version;
    /**
     * 响应码(非空) 参见附录
     */
    private String response_code;
    /**
     * 响应信息(可空) 参见附录
     */
    private String response_message;
    /**
     * 备注(可空) 说明信息，原文返回。客户可根据需要存放需要在响应时带回的信息。
     */
    private String memo;

    public String getResponse_time() {
	return response_time;
    }

    public void setResponse_time(String response_time) {
	this.response_time = response_time;
    }

    public String getPartner_id() {
	return partner_id;
    }

    public void setPartner_id(String partner_id) {
	this.partner_id = partner_id;
    }

    public String get_input_charset() {
	return _input_charset;
    }

    public void set_input_charset(String _input_charset) {
	this._input_charset = _input_charset;
    }

    public String getSign() {
	return sign;
    }

    public void setSign(String sign) {
	this.sign = sign;
    }

    public String getSign_type() {
	return sign_type;
    }

    public void setSign_type(String sign_type) {
	this.sign_type = sign_type;
    }

    public String getSign_version() {
	return sign_version;
    }

    public void setSign_version(String sign_version) {
	this.sign_version = sign_version;
    }

    public String getResponse_code() {
	return response_code;
    }

    public void setResponse_code(String response_code) {
	this.response_code = response_code;
    }

    public String getResponse_message() {
	return response_message;
    }

    public void setResponse_message(String response_message) {
	this.response_message = response_message;
    }

    public String getMemo() {
	return memo;
    }

    public void setMemo(String memo) {
	this.memo = memo;
    }

}
