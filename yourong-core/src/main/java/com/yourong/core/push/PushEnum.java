package com.yourong.core.push;

/**
 * 推送消息类型枚举
 *
 */
public enum PushEnum {
	PROJECT_ONLINE(0,"项目上线"),//项目上线
	PROJECT_NOTICE(1,"项目预告"),//项目预告
	CREATE_ORDER(2,"创建订单"),//创建订单
	PAY_ORDER(3,"支付订单"),//支付订单
	RECHARGE(4,"充值"),//充值
	CANCEL_ORDER(5,"取消订单"),//取消订单
	WITHDRAW_FAILED(6,"提现失败"),//提现失败
	MEMBER_EXIT(7,"用户退出"),//用户退出
	WITHDRAW_SUCCESS(8,"提现成功"),//提现成功
	MEMBER_UPDATE_PASSWORD(9,"修改密码"),//用户修改密码或者换手机号码
	TRANSACTION_INTEREST(10,"还本付息"),//还本付息
	OTHER(-1,"其它"),//其它
	BIRTHDAY(11,"生日专题"),//生日
	
	DIY(16,"自定义"),//自定义
	AUTO_INVEST(609,"自动投标"),//自动投标
	PAY_PRINCIPAL(614,"还本付息本息到账"),
	PAY_INTEREST(615,"利息到账"),
	
	TRANSFER_SUCCESS(610,"转让成功通知"),//转让成功
	FIVE_PRIZE(47,"五重礼信息APP推送"),
	;
	
	private int index;
	private String desc;
	PushEnum(int index, String desc){
		this.index = index;
		this.desc = desc;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public static PushEnum getEnumByIndex(int index) {
		for (PushEnum e : PushEnum.values()) {
			if (e.getIndex() == index) {
				return e;
			}
		}
		return null;
	}
}
