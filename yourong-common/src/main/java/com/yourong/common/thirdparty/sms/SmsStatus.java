package com.yourong.common.thirdparty.sms;



public enum SmsStatus {
	
    SMSSTATUS2(-2,"客户端","客户端异常"),
	SMSSTATUS9000(-9000,"ss","数据格式错误,数据超出数据库允许范围"),
	SMSSTATUS9001(-9001,"客户端,所有业务","序列号格式错误"),
	SMSSTATUS9002(-9002,"客户端,所有业务","密码格式错误"),	
	SMSSTATUS9003(-9003,"客户端,所有业务","客户端Key格式错误"),
	SMSSTATUS9004(-9004,"客户端,转发业务","设置转发格式错误"),
	SMSSTATUS9005(-9005,"客户端,企业注册业务","公司地址格式错误"),
	SMSSTATUS9006(-9006,"客户端,企业注册业务","企业中文名格式错误"),
	SMSSTATUS9007(-9007,"客户端,企业注册业务","企业中文名简称格式错误"),
	SMSSTATUS9008(-9008,"客户端,企业注册业务","邮件地址格式错误"),
	SMSSTATUS9009(-9009,"客户端,企业注册业务","企业英文名格式错误"),
	SMSSTATUS9010(-9010,"客户端,企业注册业务","企业英文名简称格式错误"),
	SMSSTATUS9011(-9011,"客户端,企业注册业务","传真格式错误"),
	SMSSTATUS9012(-9012,"客户端,企业注册业务","联系人格式错误"),
	SMSSTATUS9013(-9013,"客户端,企业注册业务","联系电话"),
	SMSSTATUS9014(-9014,"客户端,企业注册业务","邮编格式错误"),
	SMSSTATUS9015(-9015,"客户端,密码修改业务","新密码格式错误"),
	SMSSTATUS9016(-9016,"客户端,发送业务","发送短信包大小超出范围"),
	SMSSTATUS9017(-9017,"客户端,发送业务","发送短信内容格式错误"),
	SMSSTATUS9018(-9018,"客户端,发送业务","发送短信扩展号格式错误"),
	SMSSTATUS9019(-9019,"客户端,发送业务","发送短信优先级格式错误"),
	SMSSTATUS9020(-9020,"客户端,发送业务","发送短信手机号格式错误"),
	SMSSTATUS9021(-9021,"客户端,发送业务","发送短信定时时间格式错误"),
	SMSSTATUS9022(-9022,"客户端,发送业务","发送短信唯一序列值错误"),
	SMSSTATUS9023(-9023,"客户端,充值业务","充值卡号格式错误"),
	SMSSTATUS9024(-9024,"客户端,充值业务","充值密码格式错误"),
	SMSSTATUS9025(-9025,"客户端","客户端请求sdk5超时（需确认）"),
	STATUS0(0,"服务器端","成功"),
	SMSSTATUS1(-1,"服务器端","系统异常"),
	SMSSTATUS101(-101,"服务器端","命令不被支持"),
	SMSSTATUS102(-102,"服务器端","RegistryTransInfo删除信息失败（转接）"),
	SMSSTATUS103(-103,"服务器端","RegistryInfo更新信息失败（序列号相关注册）"),
	SMSSTATUS104(-104,"服务器端","请求超过限制"),
	SMSSTATUS111(-111,"服务器端","企业注册失败"),
	SMSSTATUS117(-117,"服务器端","发送短信失败"),
	SMSSTATUS118(-118,"服务器端","接收MO失败"),
	SMSSTATUS119(-119,"服务器端","接收Report失败"),
	SMSSTATUS120(-120,"服务器端","修改密码失败"),
	SMSSTATUS122(-122,"服务器端","号码注销失败"),
	SMSSTATUS110(-110,"服务器端","号码注册激活失败"),
	SMSSTATUS123(-123,"服务器端","查询单价失败"),
	SMSSTATUS124(-124,"服务器端","查询余额失败"),
	SMSSTATUS125(-125,"服务器端","设置MO转发失败"),
	SMSSTATUS126(-126,"服务器端","路由信息失败"),
	SMSSTATUS127(-127,"服务器端","计费失败0余额"),
	SMSSTATUS128(-128,"服务器端","计费失败余额不足"),
	SMSSTATUS1100(-1100,"服务器端","序列号错误,序列号不存在内存中,或尝试攻击的用户"),
	SMSSTATUS1103(-1103,"服务器端","序列号Key错误"),
	SMSSTATUS1102(-1102,"服务器端","序列号密码错误"),
	SMSSTATUS1104(-1104,"服务器端","路由失败，请联系系统管理员"),
	SMSSTATUS1105(-1105,"服务器端","注册号状态异常, 未用 1"),
	SMSSTATUS1107(-1107,"服务器端","注册号状态异常, 停用 3"),
	SMSSTATUS1108(-1108,"服务器端","注册号状态异常, 停止 5"),
	SMSSTATUS113(-113,"服务器端","充值失败"),
	SMSSTATUS1131(-1131,"服务器端","充值卡无效"),
	SMSSTATUS1132(-1132,"服务器端","充值密码无效"),
	SMSSTATUS1133(-1133,"服务器端","充值卡绑定异常"),
	SMSSTATUS1134(-1134,"服务器端","充值状态无效"),
	SMSSTATUS1135(-1135,"服务器端","充值金额无效"),
	SMSSTATUS190(-190,"服务器端","数据操作失败"),
	SMSSTATUS1901(-1901,"服务器端","数据库插入操作失败"),
	SMSSTATUS1902(-1902,"服务器端","数据库更新操作失败"),
	SMSSTATUS1903(-1903,"服务器端","数据库删除操作失败");
    

	private String msg;

	private int code;

	private String codeStr;
	
    private SmsStatus(int code, String codeStr, String msg) {
		this.code = code;
		this.codeStr = codeStr;
		this.msg = msg;
	}
    
    public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getCodeStr() {
		return codeStr;
	}

	public void setCodeStr(String codeStr) {
		this.codeStr = codeStr;
	}

	public static SmsStatus getSmsStatusByCode(int code) {
		for (SmsStatus result : SmsStatus.values()) {
			if (result.getCode() == code) {
				return result;
			}
		}
		return SmsStatus.SMSSTATUS1;
	}

}
