package com.yourong.common.enums;

public enum MessageEnum {
	COUPON_EXPIRED("001","优惠券过期"),
	UPDATE_PASSWORD("002","修改密码"),
	BIND_BAND_CARD("003","绑定银行卡"),
	INTEREST("004","利息到账"),
	CAPITAL("005","本息到账"),
	SINA_WALLET_INCOME("006","新浪存钱罐收益"),
	SIGN_IN("007","签到"),
	REG_SINA("008","开通新浪存钱罐"),
	BIND_EMAIL("009","绑定邮箱"),
	PERFECT_INFORMATION("010","完善资料"),
	EXCHANGE_POPULARITY("011","人气值兑换"),
	INVESTMENT_SUCCESS("012","投资成功"),
	WITHDRAWALS_SUCCESS("013","提现成功"),
	WITHDRAWALS_ERROR("014","提现失败"),
	PROJECT_ACTIVITIES("015","项目活动"),
	INVESTMENT_FAIL("016","投资失败"),
	QUESTIONNAIRE("017","问卷调查"),
	PROJECT_NOTICE("018","项目预告"),
	WITHDRAWALS_REFUSE("019","提现被拒绝"),
	BIND_WEIXIN("020","绑定微信"),
	APPLICATION("021","非常规活动"),
	GIVE_POPULARITY("022","人工派发人气值"),
	QUICK_PAYMENT("023","开通快捷支付"),
	SAFE_BANK_CARD("024","升级为安全卡"),
	DELETE_BANK_CARD("025","删除银行卡"),
	LEASE_BONUS("026","租赁分红"),
	RECHARGE_SUCCESS("027","充值成功"),
	RED_FRIDAY("028","红色星期五"),
	YIROAD_LOTTERY("029","抽奖活动"),
	APP_FIRST_RECHARGE("030","手当直充"),
	BIRTHDAY("031","生日专题"),
	MID_AUTUMN("032","中秋活动"),
	//新手任务
	FOLLOW_WECHAT("033","关注微信"),
	INVEST_PROJECT("034","投资项目"),
	USE_APP("035","体验APP"),
	SPEGINE_COMMON("036","活动引擎通用站内信"),

	P2P_UNRAISE("041","P2P-支付成功，但尚未募集完成"),
	P2P_RAISED("042","P2P-支付成功，募集完成"),
	P2P_RAISE_FAIL("043", "P2P-项目流标"),
	P2P_REPAYMENT_PRINCIPAL("044", "P2P-本息到账"),
	P2P_REPAYMENT_INTEREST("045", "P2P-利息到账"),
	P2P_PRINCIPAL_INTEREST_MEMBER("046", "P2P-还本付息提醒借款人"),
	
	
	FIVE_PRIZE("047","五重礼信息APP推送")	,
	
	NOTICE_AUDIT("048","直投项目满额通知风控")	,
	NOTICE_LOAN("049","风控审核通过，通知放款")	,
	
	BRIGHT_OLYMPIC("051","亮奥运奖励短信通知")	,
	
	BRIGHT_OLYMPIC_MAIL("052","亮奥运奖励站内信通知")	,
	
	SET_OLYMPIC("053","集奥运实物大奖短信通知")	,
	
	CONTRACT_UNSIGN("054","合同未签署"),

	CONTRACT_COUPON_EXPIRRE("055","优惠券到期提醒"),
	
	JULY_TEAM("050","组团大作战获胜通知"),
	
	TRANSFER_APPLAY("059","转让申请"),
	
	TRANSFER_INVEST("056","投资转让项目"),
	
	TRANSFER_RAISE("057","转让项目募集完成"),
	
	TRANSFER_SUCCESS("058","成功投资转让项目"),
	
	//TRANSFER_FAILED("060","转让失败"),
	
	//TRANSFER_RAISEFAILED("061","转让项目募集未完成"),
	
	TRANSFER_TRANSUCCESS("062","转让成功"),
	
	
	APP_QUICK_SUCCESS("601","快投有奖开奖结果"),

	APP_QUICK_TIME_OUT("602","快投有奖募集时间超过奖励期限"),
	
	APP_QUICK_FAIL("603","快投有奖募集失败"),
	
	APP_COUPON_TIME_OUT("604","优惠券即将过期"),
	
	APP_DIRECT_PROJECT_SUCCESS("605","直投项目募集成功"),
	
	APP_DIRECT_PROJECT_FAIL("606","直投项目募集失败"),
	
	APP_AUTO_INVEST_SUCCESS("609","自动投标成功"),
	
	APP_TRANSFER_SUCCESS("610","转让成功通知"),
	
	//APP_TRANSFER_FAIL("611","转让失败通知"),
	
	//APP_TRANSFER_RAISE_SUCCESS("612","转让募集成功通知"),
	
	//APP_TRANSFER_RAISE_FAIL("613","转让募集失败通知"),
	
	APP_PAY_PRINCIPAL("614","还本付息本息到账"),
	
	APP_PAY_INTEREST("615","利息到账"),
	
	APP_FIVE_REWARD("616","五重礼"),
	
	QUICK_REWARD_NOTICE("063","快投项目上线通知"),
	;
	

	private String code;
	private String desc;

	MessageEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
}
