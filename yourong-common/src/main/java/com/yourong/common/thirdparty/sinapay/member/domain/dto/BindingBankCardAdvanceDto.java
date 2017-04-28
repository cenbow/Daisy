package com.yourong.common.thirdparty.sinapay.member.domain.dto;

import java.io.Serializable;
import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;

/**
 * 绑定银行卡推进
 * Created by Administrator on 2014/9/23.
 */
public class BindingBankCardAdvanceDto extends RequestDto implements Serializable {


    /**
	 * 
	 */
	private static final long serialVersionUID = 590840987127930610L;

	/**
     * 扩展信息
     */
    private Map<String, String> extendParam;
    
    /**
     * ticket有效期为15分钟，只能使用一次
     */
    @NotNull
    private String ticket;
    
    /**
     * 短信验证码
     */
    @NotNull
    private String validCode;
    
    /**
     * 会员IP
     */
    private String clientIp;

    public Map<String, String> getExtendParam() {
        return extendParam;
    }

    public void setExtendParam(Map<String, String> extendParam) {
        this.extendParam = extendParam;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getValidCode() {

        return validCode;
    }

    public void setValidCode(String validCode) {
        this.validCode = validCode;
    }

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	
}
