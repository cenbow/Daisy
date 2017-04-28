package com.yourong.common.thirdparty.pay.sina;


/**
 * 新浪支付回调基础参数
 * @author Administrator
 *
 */
public class SinaPayNotifyBaseParmas {
	
	/**
	 * 通知类型(非空)
	 * 订单处理结果通知类型
	 */
	private String notify_type;
	/**
	 * 通知编号(非空)
	 * 32位长，随机生成
	 */
	private String notify_id;
	/**
	 * 参数编码字符集(非空)
	 * 商户网站使用的编码格式，如utf-8、gbk、gb2312等。	非空	UTF-8
	 */
	private String _input_charset;
	/**
	 * 通知时间
	 */
	private String notify_time;
	/**
	 * 签名(非空)
	 */
	private String sign;
	/**
	 * 签名方式(非空)
	 * 签名方式支持RSA、MD5。建议使用MD5
	 */
	private String sign_type;
	/**
	 * 接口版本号(非空)
	 * 接口版本号，默认1.0
	 */
	private String version;
	/**
	 * 备注(可空)
	 * 说明信息，原文返回。客户可根据需要存放需要在响应时带回的信息。
	 */
	private String memo;
	/**
	 * 返回错误码
	 */
	private String error_code;
	
	/**
	 * 返回错误原因
	 */
	private String error_message;

	public String getNotify_type() {
		return notify_type;
	}

	public void setNotify_type(String notify_type) {
		this.notify_type = notify_type;
	}

	public String getNotify_id() {
		return notify_id;
	}

	public void setNotify_id(String notify_id) {
		this.notify_id = notify_id;
	}

	public String get_input_charset() {
		return _input_charset;
	}

	public void set_input_charset(String _input_charset) {
		this._input_charset = _input_charset;
	}

	public String getNotify_time() {
		return notify_time;
	}

	public void setNotify_time(String notify_time) {
		this.notify_time = notify_time;
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getError_code() {
		return error_code;
	}

	public void setError_code(String error_code) {
		this.error_code = error_code;
	}

	public String getError_message() {
		return error_message;
	}

	public void setError_message(String error_message) {
		this.error_message = error_message;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SinaPayNotifyBaseParmas [notify_type=");
		builder.append(notify_type);
		builder.append(", notify_id=");
		builder.append(notify_id);
		builder.append(", _input_charset=");
		builder.append(_input_charset);
		builder.append(", notify_time=");
		builder.append(notify_time);
		builder.append(", sign=");
		builder.append(sign);
		builder.append(", sign_type=");
		builder.append(sign_type);
		builder.append(", version=");
		builder.append(version);
		builder.append(", memo=");
		builder.append(memo);
		builder.append(", error_code=");
		builder.append(error_code);
		builder.append(", error_message=");
		builder.append(error_message);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_input_charset == null) ? 0 : _input_charset.hashCode());
		result = prime * result
				+ ((error_code == null) ? 0 : error_code.hashCode());
		result = prime * result
				+ ((error_message == null) ? 0 : error_message.hashCode());
		result = prime * result + ((memo == null) ? 0 : memo.hashCode());
		result = prime * result
				+ ((notify_id == null) ? 0 : notify_id.hashCode());
		result = prime * result
				+ ((notify_time == null) ? 0 : notify_time.hashCode());
		result = prime * result
				+ ((notify_type == null) ? 0 : notify_type.hashCode());
		result = prime * result + ((sign == null) ? 0 : sign.hashCode());
		result = prime * result
				+ ((sign_type == null) ? 0 : sign_type.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SinaPayNotifyBaseParmas other = (SinaPayNotifyBaseParmas) obj;
		if (_input_charset == null) {
			if (other._input_charset != null)
				return false;
		} else if (!_input_charset.equals(other._input_charset))
			return false;
		if (error_code == null) {
			if (other.error_code != null)
				return false;
		} else if (!error_code.equals(other.error_code))
			return false;
		if (error_message == null) {
			if (other.error_message != null)
				return false;
		} else if (!error_message.equals(other.error_message))
			return false;
		if (memo == null) {
			if (other.memo != null)
				return false;
		} else if (!memo.equals(other.memo))
			return false;
		if (notify_id == null) {
			if (other.notify_id != null)
				return false;
		} else if (!notify_id.equals(other.notify_id))
			return false;
		if (notify_time == null) {
			if (other.notify_time != null)
				return false;
		} else if (!notify_time.equals(other.notify_time))
			return false;
		if (notify_type == null) {
			if (other.notify_type != null)
				return false;
		} else if (!notify_type.equals(other.notify_type))
			return false;
		if (sign == null) {
			if (other.sign != null)
				return false;
		} else if (!sign.equals(other.sign))
			return false;
		if (sign_type == null) {
			if (other.sign_type != null)
				return false;
		} else if (!sign_type.equals(other.sign_type))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
	
}
