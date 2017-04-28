package com.yourong.common.thirdparty.sinapay.member.domain.dto;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.enums.CharsetType;

public class RequestDto {

    /**
     * 参数编码字符集
     */
    @NotNull
    private CharsetType charsetType = CharsetType.UTF8;

    /**
     * 备注
     */
    private String      memo;

	/**
	 * 页面跳转同步返回页面路径
	 */
	private String redirectUrl;

	/**
	 * 完成后跳转
	 */
	private String returnUrl;

    public CharsetType getCharsetType() {
        return charsetType;
    }

    public void setCharsetType(CharsetType charsetType) {
        this.charsetType = charsetType;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RequestDto [charsetType=");
		builder.append(charsetType);
		builder.append(", memo=");
		builder.append(memo);
		builder.append(", redirectUrl=");
		builder.append(redirectUrl);
		builder.append(", returnUrl=");
		builder.append(returnUrl);
		builder.append("]");
		return builder.toString();
	}
}
