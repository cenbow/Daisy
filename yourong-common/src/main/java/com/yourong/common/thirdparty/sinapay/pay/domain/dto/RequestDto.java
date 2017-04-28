package com.yourong.common.thirdparty.sinapay.pay.domain.dto;

import java.io.Serializable;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.enums.CharsetType;
import com.yourong.common.thirdparty.sinapay.pay.core.common.Aliases;

/**
 * <p>基本请求参数 </p>
 * @author Wallis Wang
 * @version $Id: BaseRequestDTO.java, v 0.1 2014年5月9日 下午5:54:56 wangqiang Exp $
 */
public class RequestDto implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1960826442811693020L;

    /**
     * 参数编码字符集
     * 必填
     */
    @NotNull
    @Aliases("_input_charset")
    private CharsetType       inputCharset     = CharsetType.UTF8;

    /**
     * 系统异步回调通知地址
     */
    private String            notifyUrl;

    /**
     * 页面跳转同步返回页面路径
     */
    private String            returnUrl;

    /**
     * 备注
     */
    private String            memo;

    public CharsetType getInputCharset() {
        return inputCharset;
    }

    public void setInputCharset(CharsetType inputCharset) {
        this.inputCharset = inputCharset;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RequestDto [inputCharset=");
		builder.append(inputCharset);
		builder.append(", notifyUrl=");
		builder.append(notifyUrl);
		builder.append(", returnUrl=");
		builder.append(returnUrl);
		builder.append(", memo=");
		builder.append(memo);
		builder.append("]");
		return builder.toString();
	}
}
