package com.yourong.common.constant;

/**
 * 常量类
 * 
 * @author py
 *
 */
public final class RedisConstant {
	/**
	 * redis seperater
	 */
	public static final String REDIS_SEPERATOR = ":";
	/***
	 * 注册时用户
	 * 
	 */
	public static final String REDIS_KEY_USER = "user";
	// 短信发送次数
	public static final String REDIS_FIELD_USER_SEND_SMS_COUNT = "smsCounts";
	// 短信验证次数
	public static final String REDIS_FIELD_USER_CHECK_SMS_COUNT = "checkCounts";
	// 语音发送次数
	public static final String REDIS_FIELD_USER_SEND_VOICE_COUNT = "voiceCounts";
	// 登录次数
	public static final String REDIS_FIELD_USER_LOGIN_COUNT = "loginCounts";
	// 未认证
	public static final String REDIS_FIELD_UNAUTH = "unAuth";
	// 微信解绑指令
	public static final String REDIS_FIELD_WEIXIN_UNBUNDLING_DIRECTIVE = "unbundling";

	/**
	 * redis key
	 */
	/**
	 * 会员
	 */
	public static final String REDIS_KEY_MEMBER = "member";

	public static final String REDIS_KEY_PLATFORM = "platform";

	public static final String REDIS_KEY_PROJECT = "project";

	public static final String REDIS_KEY_MEMBER_INVITECODE = "memberInviteCode";
	/**
	 * 活动
	 */
	public static final String REDIS_KEY_ACTIVITY = "activity";

	/**
	 * 进行中的活动列表
	 */
	public static final String REDIS_ACTIVITY_LIST = "progressActivity";

	/**
	 * 非会员中奖列表
	 */
	public static final String REDIS_KEY_ACTIVITY_OUTSIDER_MOBILE = "outsider";

	/**
	 * 活动参与者参加次数
	 */
	public static final String REDIS_KEY_ACTIVITY_PARTICIPATE = "participate";

	/**
	 * 活动基本信息
	 */
	public static final String REDIS_KEY_ACTIVITY_BASIC_INFO = "basicInfo";

	/**
	 * 活动有效奖项
	 */
	public static final String REDIS_KEY_ACTIVITY_LOTTERY_REWARDS = "rewards";

	/**
	 * 活动总共奖项
	 */
	public static final String REDIS_KEY_ACTIVITY_LOTTERY_REWARDS_TOTAL = "total";

	/**
	 * 邮箱退订
	 */
	public static final String REDIS_KEY_MEMBER_EMAIL_UNSUBSCRIBE = "emailUnsubscribe";

	/**
	 * 发送短信key
	 */
	public static final String REDIS_KEY_SENDMESSAGE = "sendMessage";
	/**
	 * 平台发送惠劵
	 */
	public static final String REDIS_KEY_ReceiveCoupon = "sendCoupon";

	/**
	 * redis field
	 */
	/**
	 * 存钱罐余额
	 */
	public static final String REDIS_FIELD_MEMBER_SINAPAY_BALANCE = "sinPayBalance";
	/**
	 * 成功充值笔数
	 */
	public static final String REDIS_FIELD_MEMBER_RECHARGE_COUNT = "rechargeCount";
	/**
	 * 成功充值总额
	 */
	public static final String REDIS_FIELD_MEMBER_RECHARGE_AMOUNT = "rechargeAmount";
	/**
	 * 成功提现笔数
	 */
	public static final String REDIS_FIELD_MEMBER_WITHDRAW_COUNT = "withdrawCount";
	/**
	 * 成功提现总额
	 */
	public static final String REDIS_FIELD_MEMBER_WITHDRAW_AMOUNT = "withdrawAmount";
	/**
	 * 累计投资总额
	 */
	public static final String REDIS_FIELD_MEMBER_TOTAL_INVEST_AMOUNT = "totalInvestAmount";
	/**
	 * 邮箱token
	 */
	public static final String REDIS_FIELD_MEMBER_EMAIL_TOKEN = "emailToken";

	/**
	 * 项目余额
	 */
	public static final String REDIS_FIELD_PROJECT_PROJECT_BALANCE = "projectBalance";

	/**
	 * 支付中项目
	 */
	public static final String REDIS_FIELD_PROJECT_PROJECT_PAYMENTING = "projectPaymenting";

	/**
	 * 会员头像
	 */
	public static final String REDIS_FIELD_MEMBER_AVATAR = "memberAvatar";

	/**
	 * 新浪存钱罐7日收益率
	 */
	public static final String REDIS_FIELD_PLATFORM_SINAPAY_SEVEN_DAYS_BONUS = "sevenDaysBonus";
	/**
	 * 平台注册会员数
	 */
	public static final String REDIS_FIELD_PLATFORM_MEMBER_COUNT = "memberCount";
	/**
	 * 平台注册会员数,
	 */
	public static final String REDIS_FIELD_PLATFORM_MEMBER_INCR_COUNT = "memberIncrCount";

	/**
	 * 会员完善详情信息
	 */
	public static final String REDIS_FIELD_MEMBER_INFO_COMPLETE_STATUS = "memberInfoCompleteStatus";

	/**
	 * 会员领取压岁钱key
	 */
	public static final String REDIS_KEY_PLATFORM_RECEIVE_GIFT_MONEY = "receiveGiftMoney";

	/**
	 * 项目交易明细Key
	 */
	public static final String TRANSACTION_DETAIL_FOR_PROJECT = "transactionDetailForProject";

	/**
	 * 会员昵称
	 */
	public static final String REDIS_FIELD_MEMBER_USERNAME = "memberUserName";

	/**
	 * 是否参与活动
	 */
	public static final String REDIS_FIELD_ACTIVITY_IS_PARTICIPATE = "isParticipate";

	/**
	 * AndroidApp下载次数key(区分user-agent)
	 */
	public static final String REDIS_KEY_YOURONGAPKDOWNLOAD = "yourongApk";

	/**
	 * AndroidApp下载次数key(总数)
	 */
	public static final String REDIS_KEY_YOURONGAPKDOWNLOADCOUNT = "yourongApk:Count";

	/**
	 * 红色星期五活动
	 */
	public static final String REDIS_FIELD_PLATFORM_RED_FRIDAY_COUNT = "redFridayCount";

	/**
	 * AndroidApp下载次数key(总数)
	 */
	public static final String REDIS_KEY_YOURONGAPKDOWNLOAD_COUNT = "Count";

	/**
	 * AndroidApp下载次数field(总数)
	 */
	public static final String REDIS_FIELD_YOURONGAPKDOWNLOAD_COUNT = "totalDownLoad";

	/**
	 * AndroidApp外网下载次数field(总数)
	 */
	public static final String REDIS_FIELD_YOURONGAPKDOWNLOAD_OTHERCOUNT = "otherDownLoad";

	/**
	 * 亿路上有你活动
	 */
	public static final String REDIS_KEY_ACTIVITY_YIROAD = "yiRoad";

	/**
	 * 亿路上有你亿举夺魁排行榜
	 */
	public static final String REDIS_KEY_ACTIVITY_YIROAD_LOTTERYLIST = "lotteryList";

	/**
	 * 亿路上有你亿举夺魁排行榜
	 */
	public static final String REDIS_KEY_ACTIVITY_YIROAD_RANKING = "rankingList";

	/**
	 * 亿路上有你亿举夺魁排行榜
	 */
	public static final String REDIS_KEY_ACTIVITY_YIROAD_SHARELIST = "shareList";

	/**
	 * 玩转人气值-人气值排行前十
	 */
	public static final String REDIS_KEY_ACTIVITY_POPULARITY_TOP10 = "popularityTopTen";

	/**
	 * 玩转人气值-人气值兑换记录
	 */
	public static final String REDIS_KEY_ACTIVITY_POPULARITY_EXCHANGE = "popularityExchange";

	/**
	 * 月度活动排行
	 */
	public static final String REDIS_KEY_ACTIVITY_MONTHLYRANK = "monthlyRank";

	/**
	 * 月度活动奇偶排行
	 */
	public static final String REDIS_KEY_ACTIVITY_MONTHLYRANK_WEEKLYLIST = "weeklyList";

	/**
	 * 月度活动奇偶排行上榜人员
	 */
	public static final String REDIS_KEY_ACTIVITY_MONTHLYRANK_WEEKLYLIST_TOPPER = "topper";
	/**
	 * 一羊领头专题页
	 */
	public static final String REDIS_KEY_ACTIVITY_LEADING_SHEEP = "leadingSheep";
	/**
	 * 一羊领头专题页-风云榜
	 */
	public static final String REDIS_KEY_ACTIVITY_LEADING_SHEEP_RANKLIST = "ranklist";

	/**
	 * 一羊领头人气值榜-风云榜
	 */
	public static final String REDIS_KEY_ACTIVITY_LEADING_SHEEP_GAINLIST = "gainlist";

	/**
	 * 着陆页
	 */
	public static final String REDIS_KEY_LANDING = "landing";
	/**
	 * 着陆页-新客项目
	 */
	public static final String REDIS_KEY_LANDING_NEWCUSTOMER = "newCustomer";

	/**
	 * 首次投资推荐项目
	 */
	public static final String REDIS_KEY_FIRST_INVEST = RedisConstant.REDIS_KEY_LANDING + RedisConstant.REDIS_SEPERATOR + "firstInvest";

	/**
	 * 新手任务最新动态
	 */
	public static final String REDIS_KEY_NEWER_MISSION_LIST = RedisConstant.REDIS_KEY_LANDING + RedisConstant.REDIS_SEPERATOR
			+ "newerMissionList";

	/**
	 * 着陆页-推荐项目
	 */
	public static final String REDIS_KEY_LANDING_RECOMMEND = "recommend";
	/**
	 * 交易模块
	 */
	public static final String REDIS_KEY_INDEX_TRANCTION = "tranction";
	/**
	 * 交易历史记录
	 */
	public static final String REDIS_KEY_INDEX_HISTROY = "data";

	/**
	 * 交易四重礼
	 */
	public static final String REDIS_KEY_QUADRUPLEGIFT = "quadruplegift";

	/**
	 * 周年庆-25宫格
	 */
	public static final String REDIS_KEY_ACTIVITY_ANNIVERSARY = "anniversary";

	/**
	 * 周年庆-25宫格
	 */
	public static final String REDIS_KEY_ACTIVITY_ANNIVERSARY_25GRID_LIST = "25GridList";

	/**
	 * 巅峰1小时
	 */
	public static final String REDIS_KEY_ACTIVITY_ANNIVERSARY_1HOUR_LIST = "oneHour";

	/**
	 * 百万现金券活动标识
	 */
	public static final String REDIS_KEY_ACTIVITY_MILLIONCOUPON_NAME = "millionCoupon";

	/**
	 * 百万现金券活动总金额
	 */
	public static final String REDIS_KEY_ACTIVITY_MILLIONCOUPON_FUND = "fund";


	/**
	 * 微信
	 */
	public static final String REDIS_KEY_WEIXIN = "weixin";

	/**
	 * 微信jsApi:accessToken
	 */
	public static final String REDIS_KEY_WEIXIN_ACCESSTOKEN = "accessToken";

	/**
	 * 微信jsApi:ticket
	 */
	public static final String REDIS_KEY_WEIXIN_TICKET = "ticket";

	/**
	 * 着陆页-五重礼每一种利获取统计
	 */
	public static final String REDIS_KEY_LANDING_QUINTUPLEGIFT_COUNT = "quintuplegiftCount";
	/**
	 * 着陆页-五重礼获取用户列表
	 */
	public static final String REDIS_KEY_LANDING_QUINTUPLEGIFT_GAIN_LIST = "quintuplegiftGainList";

	/**
	 * 春节活动
	 */
	public static final String REDIS_KEY_ACTIVITY_SPRING = "springFestival";

	/**
	 * 春节活动-返利列表
	 */
	public static final String REDIS_KEY_ACTIVITY_SPRING_REBATELIST = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR
			+ RedisConstant.REDIS_KEY_ACTIVITY_SPRING + RedisConstant.REDIS_SEPERATOR + "rebateList";

	/**
	 * 春节活动-许愿列表
	 */
	public static final String REDIS_KEY_ACTIVITY_SPRING_WISHLIST = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR
			+ RedisConstant.REDIS_KEY_ACTIVITY_SPRING + RedisConstant.REDIS_SEPERATOR + "wishList";

	/**
	 * 春节活动-如意列表
	 */
	public static final String REDIS_KEY_ACTIVITY_SPRING_WISHESPROLIST = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR
			+ RedisConstant.REDIS_KEY_ACTIVITY_SPRING + RedisConstant.REDIS_SEPERATOR + "wishesProList";

	/**
	 * 红包
	 */
	public static final String REDIS_KEY_ACTIVITY_REDBAG = "redBag";

	/**
	 * 春节活动-兑换人气值返利
	 */
	public static final String REDIS_KEY_ACTIVITY_REBATE = "rebate";

	/**
	 * 红包用户领取记录
	 */
	public static final String REDIS_KEY_ACTIVITY_REDBAG_MOBILE = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR
			+ RedisConstant.REDIS_KEY_ACTIVITY_REDBAG + RedisConstant.REDIS_SEPERATOR + "mobile";

	/**
	 * 红包总数
	 */
	public static final String REDIS_KEY_ACTIVITY_REDBAG_TOTALNUM = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR
			+ RedisConstant.REDIS_KEY_ACTIVITY_REDBAG + RedisConstant.REDIS_SEPERATOR + "totalNum";

	/**
	 * 红包认领数
	 */
	public static final String REDIS_KEY_ACTIVITY_REDBAG_CLAIMNUM = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR
			+ RedisConstant.REDIS_KEY_ACTIVITY_REDBAG + RedisConstant.REDIS_SEPERATOR + "claimNum";

	/**
	 * 单组红包金额数组
	 */
	public static final String REDIS_KEY_ACTIVITY_REDBAG_VALUES = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR
			+ RedisConstant.REDIS_KEY_ACTIVITY_REDBAG + RedisConstant.REDIS_SEPERATOR + "values";

	/**
	 * 单组红包信息字符串
	 */
	public static final String REDIS_KEY_ACTIVITY_REDBAG_INFO = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR
			+ RedisConstant.REDIS_KEY_ACTIVITY_REDBAG + RedisConstant.REDIS_SEPERATOR + "info";

	/**
	 * 单组红包状态-未抢完
	 */
	public static final String REDIS_VALUE_ACTIVITY_REDBAG_WAITING = "waiting";

	/**
	 * 单组红包状态-已抢完
	 */
	public static final String REDIS_VALUE_ACTIVITY_REDBAG_EMPTY = "empty";

	/**
	 * 参加活动条件
	 */
	public static final String REDIS_KEY_ACTIVITY_OBTAIN_CONDITIONS = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR
			+ "obtainCondition" + RedisConstant.REDIS_SEPERATOR;

	/**
	 * 活动rule条件
	 */
	public static final String REDIS_KEY_ACTIVITY_RULE_CONDITIONS = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR
			+ "ruleCondition" + RedisConstant.REDIS_SEPERATOR;

	
	/**
	 * 用户投资的募集中项目
	 */
	public static final String REDIS_KEY_COLLECTING_PROJECT = "collectingProject";
	
	/**
	 * 项目投资人数
	 */
	public static final String REDIS_FIELD_PROJECT_INVEST_MEMBER = "investMember";
	/**
	 * 项目最高投资额
	 */
	public static final String REDIS_FIELD_PROJECT_MOST_INVEST_AMOUNT = "mostInvestAmount";
	/**
	 * 项目总收益
	 */
	public static final String REDIS_FIELD_PROJECT_TOTAL_AMOUNT = "totalAmount";
	/**
	 * 平台累计投资破亿时间戳记录
	 */
	public static final String REDIS_PLATFORM_TOTAL_INVEST_TIME = "platform" + RedisConstant.REDIS_SEPERATOR + "totalInvest";
	/**
	 *滞纳金
	 */
	public static final String REDIS_KEY_TRANSACTION_LATEFEE = "lateFee";
	/**
	 * 一鸣惊人- key
	 */
	public static final String REDIS_KEY_MOST_INVEST = "mostInvest";
	/**
	 * 一锤定音- key
	 */
	public static final String REDIS_KEY_LAST_INVEST = "lastInvest";
	/**
	 * 一掷千金- key
	 */
	public static final String REDIS_KEY_MOST_LAST_INVEST = "mostLastInvest";
	/**
	 * 幸运女神- key
	 */
	public static final String REDIS_KEY_LUCK_INVEST = "luckInvest";
	
	/**
	 * 一鸣惊人- name
	 */
	public static final String REDIS_KEY_MOST_INVEST_NAME = "一鸣惊人";
	/**
	 * 一锤定音- name
	 */
	public static final String REDIS_KEY_LAST_INVEST_NAME = "一锤定音";
	/**
	 * 一掷千金- name
	 */
	public static final String REDIS_KEY_MOST_LAST_INVEST_NAME = " 一掷千金";
	/**
	 * 幸运女神- name
	 */
	public static final String REDIS_KEY_LUCK_INVEST_NAME = "幸运女神";
	
	
	public static final String TRANSACTION_INVERESTLIST_FOR_PROJECT = "transactionInvestForProject";

	/**
	 * 玩转奥运
	 */
	public static final String ACTIVITY_PLAY_OLYMPIC_KEY = "playOlympicKey";
	/**
	 * 当天投资笔数
	 */
	public static final String ACTIVITY_PLAY_OLYMPIC_CURRENT_TRANSACTION_TOTALINVEST = "playOlympicKey" + RedisConstant.REDIS_SEPERATOR + "totalInvest";
	/**
	 * 第四张拼图数量
	 */
	public static final String ACTIVITY_PLAY_OLYMPIC_CURRENT_FOURPUZZLE= "playOlympicKey" + RedisConstant.REDIS_SEPERATOR + "fourPuzzle";
	
	/**
	 * 第四张拼图
	 */
	public static final String ACTIVITY_PLAY_OLYMPIC_FOUR = "fourPuzzleOlympic";
	
	/**
	 *  用户修改新手机号：新手机号的验证码放入redis的key
	 */
	public static final String REDIS_KEY_MODIFY_MOBILE = "modify_mobile" + RedisConstant.REDIS_SEPERATOR;
	
	/**
	 * 用户对当前项目是否自动投标过(110800000215_member_auto_invest_current_project:989800009)
	 */
	public static final String MEMBER_CURRENT_PROJECT_AUTO_INVEST_FLAG = RedisConstant.REDIS_SEPERATOR + "member_auto_invest_current_project" + RedisConstant.REDIS_SEPERATOR;
	
	/**
	 * 用户当天是否已自动投标过(110800000215_member_auto_invest_current_day:2016-08-17)
	 */
	public static final String MEMBER_CURRENT_DAY_AUTO_INVEST_FLAG = RedisConstant.REDIS_SEPERATOR + "member_auto_invest_current_day" + RedisConstant.REDIS_SEPERATOR;
	
	
	public static final String ACTIVITY_SETP_TEAM_CURRENT_COUPONAMOUNT = "setpTeamKey" + RedisConstant.REDIS_SEPERATOR + "couponAmount";
	
	
	/**
	 * 人气值乐园-9宫格
	 */
	public static final String REDIS_KEY_ACTIVITY_POPULARITY_PARK_NINE = "popularityParkNine";

	/**
	 * 人气值乐园-9宫格
	 */
	public static final String REDIS_KEY_ACTIVITY_POPULARITY_PARK_NINE_LIST = "popularityParkNineList";
	
	/**
	 * 
	 */
	public static final String ACTIVITY_COUPONAMOUNT = "fourWeatherChangAmount";
	
	
	public static final String ACTIVITY_SEND_COUPON_TOTAL = "activitySendCouponTotal";
	
	
	public static final String DIRECT_LOTTERY_KEY_ACTIVITY = "directLottery";
	
	public static final String DIRECT_LOTTERY_KEY_ACTIVITY_COUPON = "directLotteryCoupon";

	public static final String DIRECT_LOTTERY_KEY_ACTIVITY_FAIL = "directLotteryFail";
	
	public static final String MEMBER_LEVEL_UP_GIFT ="memberLevelUp_gift";
	
	
	public static final String ACTIVITY_ANNERCELEBRATE_CURRENT_COUPONAMOUNT = "annercelebrateKey" + RedisConstant.REDIS_SEPERATOR + "couponAmount";
	
	
	public static final String ACTIVITY_DOUBLE_DAN_INVESTAMOUNT = "doubleActivityInvestAmount";
	
	public static final String ACTIVITY_DOUBLE_DAN_TOTAL_RED = "doubleTotalRed";
	
	/**
	 * 限制用户注册IP
	 */
	public static final String REDIS_KEY_USER_RESTRICT_IP ="register:web:";

	/**
	 * 除夕抢压岁钱数量key
	 */
	public static final String REDIS_KEY_GRAB ="grab";

	/**
	 * 除夕抢压岁钱数量field
	 */
	public static final String REDIS_KEY_GRAB_COUNT ="grabCount";
	/**
	 * 除夕成功抢压岁钱的用户数量
	 */
	public static final String REDIS_KEY_GRAB_MEMBER ="grabMember";

	/**
	 * 除夕抢压岁钱中奖结果
	 */
	public static final String REDIS_KEY_GRAB_RESULT ="grabResult";
	
	
	public static final String POPULAR_PARK_GOOD_INVEST_ALL = "popularParkGoodInvestAll";
	
	public static final String POPULAR_PARK_GOOD_Double_ALL = "popularParkGoodDoubleAll";
	
	public static final String POPULAR_PARK_GOOD_ALL = "popularParkGoodAll";

	
	/**
	 * 新注册未投资的用户当天是否已发送短信(newRegisterMemberSendMsgFlag)
	 */
	public static final String NEW_REGISTER_MEMBER_SEND_MSG_FLAG = "newRegisterMemberSendMsgFlag";
	
	
	public static final String ACTIVITY_DAY_DROPGOLD_TOTAL_RED = "dayDropTotalRed";
	
	/**
	 * 第三方数据对接
	 */
	public static final String OPEN_SERVICE_DATA_QUERY_TOKEN = "openservice" + RedisConstant.REDIS_SEPERATOR
			+ "dataquery" + RedisConstant.REDIS_SEPERATOR;
	
	
	public static final String MEMBER_VIP_LEVLE = "memberVipLevel";
}
