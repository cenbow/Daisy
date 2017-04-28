package com.yourong.common.constant;

import java.math.BigDecimal;

/**
 * 
 * @desc 活动常量类
 * @author wangyanji 2016年1月20日下午1:33:53
 */
public final class ActivityConstant {

	/**
	 * 红包分隔符
	 */
	public static final String redBagCodeSplit = ":";

	/**
	 * 默认活动key有效期
	 */
	public static final int activityKeyExpire = 60 * 60 * 24 * 30;

	/**
	 * 默认活动key短有效期
	 */
	public static final int activityKeyShortExpire = 60 * 20;

	/**
	 * 默认活动key一周有效期
	 */
	public static final int activityKeyWeekExpire = 60 * 60 * 24 * 7;

	/**
	 * 默认人气值备注
	 */
	public static final String popularityDesc = "点人气值";

	/**
	 * 现金券备注
	 */
	public static final String couponDesc = "元现金券";

	/**
	 * 收益券备注
	 */
	public static final String annualizedDesc = "收益券";
	
	/**
	 * 收益券 单位
	 */
	public static final String annualizedUnit = "%";

	/**
	 * 红包加密串默认长度
	 */
	public static final int redBagCodeLength = 64;

	/**
	 * 移动端投资送券活动
	 */
	public static final String ACTIVITY_MOBILE_INVEST_NAME = "【50元现金券，触“手”可得】";

	/**
	 * 移动端投资送券活动
	 */
	public static final BigDecimal ACTIVITY_MOBILE_INVEST_LIMIT = new BigDecimal(1000);

	/**
	 * 2016年三八妇女节
	 */
	public static final String WOMEN_DAY_NAME = "2016年三八妇女节";

	/**
	 * 破十亿庆祝活动
	 */
	public static final String ACTIVITY_BREAK_BILLION = "breakBillion";

	/**
	 * 破十亿庆祝活动
	 */
	public static final String ACTIVITY_BREAK_BILLION_NAME = "【十亿荣光 初心不忘】";

	/**
	 * 破十亿庆祝活动现金券总额
	 */
	public static final String ACTIVITY_BREAK_BILLION_COUPONAMOUNT_LIMIT = "500000";

	/**
	 * 投资分享红包活动
	 */
	public static final String ACTIVITY_REDPACKAGE_NAME = "【拼手气，抢红包】";

	/**
	 * 新人领红包奖励
	 */
	public static final String ACTIVITY_REDPACKAGE_REGISTER = "30元现金券";

	/**
	 * 十里春风送温暖活动
	 */
	public static final String ACTIVITY_SEND_SPRING_NAME = "【十里春风送温暖】";

	/**
	 * 十里春风送温暖短信文案
	 */
	public static final String ACTIVITY_SEND_SPRING_MSG = "尊敬的会员：十里春风送温暖，恭喜您获得一张50元现金券，限本周末移动端投资使用。更多福利等你来赚！回复TD退订。";

	/**
	 * 活动规则类型-红包
	 */
	public static final String ACTIVITY_RULE_TYPE_POPULARITY_RED_PACKAGE = "popularityRedPackage";

	/**
	 * 清明节活动
	 */
	public static final String ACTIVITY_QINGMING_NAME = "【伴你踏青】";

	/**
	 * 十里春风送温暖短信文案
	 */
	public static final String ACTIVITY_QINGMING_NAME_MSG = "清明踏青趣，投资暖人心。恭喜您获得一张88元现金券，领取后7天内有效。更多福利等你来！回复TD退订。";

	/**
	 * 投房有礼活动一重礼
	 */
	public static final String ACTIVITY_HOUSE1GIFT_NAME = "【投房有礼第一轮】";

	/**
	 * 投房有礼活动二重礼
	 */
	public static final String ACTIVITY_HOUSE2GIFT_NAME = "【投房有礼第二轮】";

	/**
	 * 投房有礼活动
	 */
	public static final String ACTIVITY_HOUSE2GIFT_LIST = "house2Gift";

	/**
	 * 微信周末活动
	 */
	public static final String ACTIVITY_WECHAT_INVEST = "wechatInvest";

	/**
	 * 微信周末活动
	 */
	public static final String ACTIVITY_WECHAT_INVEST_NAME = "【周末专属，无微不至第五期】";

	/**
	 * 庆国资战略入股有融网
	 */
	public static final String ACTIVITY_SASAC_INVEST_NAME = "【庆国资战略入股有融网】";

	/**
	 * 微信周末活动
	 */
	public static final String ACTIVITY_WECHAT_INVEST_MSG = "周末专属，无微不至，微信投资赢取现金券大礼。恭喜您获得一张{0}和{1}，领取后7天内有效，更多好礼等你来！回复TD退订。";

	/**
	 * 五一四重礼活动
	 */
	public static final String ACTIVITY_MAYDAY_4GIFTS_NAME = "【四重壕礼 嗨翻五一】";

	/**
	 * 五一四重礼活动key
	 */
	public static final String ACTIVITY_MAYDAY_4GIFTS_KEY = "mayDay4Gifts";

	/**
	 * 手气最佳
	 */
	public static final String ACTIVITY_ROLE_TOP = "redPackageTop";

	/**
	 * 520活动
	 */
	public static final String ACTIVITY_FELL_IN_LOVE_NAME = "【我们相爱吧】";

	/**
	 * 520活动key
	 */
	public static final String ACTIVITY_FELL_IN_LOVE_KEY = "fellInLove";

	/**
	 * 破二十亿庆祝活动
	 */
	public static final String ACTIVITY_2_BREAK_BILLION = "break2Billion";

	/**
	 * 破二十亿庆祝活动抽奖列表
	 */
	public static final String ACTIVITY_2_BREAK_BILLION_LOTTERY = "break2BillionLottery";

	/**
	 * 破二十亿庆祝活动
	 */
	public static final String ACTIVITY_2_BREAK_BILLION_NAME = "【悬赏20亿冲榜达人】";

	/**
	 * 破二十亿庆祝活动PART1
	 */
	public static final String ACTIVITY_2_BREAK_BILLION_PART1 = "投资领券";

	/**
	 * 破二十亿庆祝活动PART2
	 */
	public static final String ACTIVITY_2_BREAK_BILLION_PART2 = "每日抽奖";

	/**
	 * 破二十亿庆祝活动PART3
	 */
	public static final String ACTIVITY_2_BREAK_BILLION_PART3 = "破亿红包";
	
	/**
	 * 友情岁月
	 */
	public static final String ACTIVITY_FRIEND_SHIP_YEARS = "【友情岁月】";

	/**
	 * 友情岁月key
	 */
	public static final String ACTIVITY_FRIEND_SHIP_KEY = "friendShip";

	/**
	 * 友情岁月topKey
	 */
	public static final String ACTIVITY_FRIEND_SHIP_TOP_KEY = "friendShipTop";
	
	/**
	 * 融光焕发 庆a活动
	 */
	public static final String ACTIVITY_CELEBRATE_A = "celebrateA";
	/**
	 * 融光焕发 庆a活动
	 */
	public static final String ACTIVITY_CELEBRATE_A_NAME = "【“融”光焕发】";
	

	/**
	 * 五重礼
	 */
	public static final String FIVE_RITES = "五重礼";

	/**
	 * 五重礼-KEY
	 */
	public static final String FIVE_RITES_KEY = "fiveRitesKey";

	/**
	 * 邀请好友
	 */
	public static final String INVITATION_FRIENDS = "邀请好友";

	/**
	 * 邀请好友-KEY
	 */
	public static final String INVITATION_FRIENDS_KEY = "refferal";
	
	/**
	 * 玩转奥运,我是冠军
	 */
	public static final String ACTIVITY_PLAY_OLYMPIC = "玩转奥运，我是冠军";
	/**
	 * 玩转奥运 -KEY
	 */
	public static final String ACTIVITY_PLAY_OLYMPIC_KEY = "playOlympicKey";
	/**
	 * 亮奥运
	 */
	public static final String ACTIVITY_PLAY_BRIGHT_OLYMPIC = "brightOlympic";
	/**
	 * 竞猜奖牌
	 */
	public static final String ACTIVITY_OLYMPIC_GUESS_MEDAL = "guessMedal";
	/**
	 * 竞猜金牌
	 */
	public static final String ACTIVITY_OLYMPIC_GUESS_GOLE = "guessGold";
	/**
	 * 奥运拼图
	 */
	public static final String ACTIVITY_OLYMPIC_SET_OLYMPIC = "setOlympic";
	/**
	 * 实物大奖
	 */
	public static final String ACTIVITY_OLYMPIC_REAL_PRIZE = "realPrize";
	/**
	 * 玩转奥运
	 */
	public static final String ACTIVITY_OLYMPIC_NAME = "【玩转奥运】";
	/**
	 * 玩转奥运-亮奥运
	 */
	public static final String ACTIVITY_OLYMPIC_BRIGHT_NAME = "【玩转奥运-亮奥运】";
	/**
	 * 玩转奥运-集奥运
	 */
	public static final String ACTIVITY_OLYMPIC_SET_NAME = "【玩转奥运-集奥运】";
	/**
	 * 玩转奥运-猜奥运
	 */
	public static final String ACTIVITY_OLYMPIC_GUESS_NAME = "【玩转奥运-猜奥运】";
	/**
	 * 喝彩中国，闪耀里约
	 */
	public static final String ACTIVITY_CELEBRATE_OLYMPIC_NAME = "【喝彩中国喜迎G20】";
	
	/**
	 * 喝彩中国，闪耀里约 key
	 */
	public static final String ACTIVITY_CELEBRATE_OLYMPIC_KEY = "celebrateOlympic";
	
	/**
	 * 提现手续费
	 */
	public static final String WITH_DRAW_NAME = "提现手续费";
	
	
	/**
	 * 七月战队
	 */
	public static final String ACTIVITY_JULY_TEAM = "【组团大作战】";
	/**
	 * 七月战队-key
	 */
	public static final String ACTIVITY_JULY_TEAM_KEY = "julyTeam";
	
	/**
	 * 骄阳似火-key
	 */
	public static final String ACTIVITY_JULY_TEAM_JYSH_KEY = "julyTeamJYSH";
	/**
	 * 骄阳似火 
	 */
	public static final String ACTIVITY_JULY_TEAM_JYSH_NAME = "【五仁月饼队】";
	/**
	 * 清凉一夏-key
	 */
	public static final String ACTIVITY_JULY_TEAM_QLYX_KEY = "julyTeamQLYX";
	/**
	 * 清凉一夏
	 */
	public static final String ACTIVITY_JULY_TEAM_QLYX_NAME = "【冰皮月饼队】";
	/**
	 * 组团大作战
	 */
	public static final String ACTIVITY_JULY_TEAM_ZTDZZ_NAME = "【组团大作战】";
	/**
	 * 消暑红包
	 */
	public static final String ACTIVITY_JULY_TEAM_XSHB_NAME = "团圆红包";
	/**
	 * 获胜战队第一名短信文案
	 */
	public static final String ACTIVITY_JULY_TEAM_NAME_MSG = "恭喜成为今日PK之王，您将获得258元元祖雪月饼提货券一张，客服会在3个工作日内与您联系，记得保持手机畅通！回复TD退订。";
	/**
	 * 获胜队伍
	 */
	public static final String ACTIVITY_JULY_TEAM_SUCCESS_TEAM = "successTeam";
	
	/**
	 * 国庆活动
	 */
	public static final String ACTIVITY_NATIONAL_OCTOBER_NAME = "【喜破三十亿，携手庆十一】";
	/**
	 * 国庆活动key
	 */
	public static final String ACTIVITY_NATIONAL_OCTOBER_KEY = "nationalOctober";
	
	/**
	 * 人气值乐园9宫格
	 */
	public static final String ACTIVITY_PLAY_POPULARITY_PARK_NINE = "【人气值乐园九宫格活动】";
	
	/**
	 * 周年庆倒计时活动
	 */
	public static final String ACTIVITY_FOUR_CHANGE_NAME = "【周年庆倒计时】";
	/**
	 * 周年庆倒计时活动 key 
	 */
	public static final String ACTIVITY_FOUR_CHANGE_KEY = "fourWeatherChange";
	
	
	public static final String ACTIVITY_CELEBRATEGIFT_NAME = "【周年庆豪礼相送】";
	
	public static final String ACTIVITY_CELEBRATEGIFT_KEY = "celebrategiftKey";
	
	
	public static final String DIRECT_WINNER_LOTTERY_KEY="winnerLottery";
	
	public static final String DIRECT_LOSER_LOTTERY_KEY="popularity";
	
	public static final String ACTIVITY_DIRECT_CATALYZER = "直投满标悬赏抽奖规则";
	
	public static final String DIRECT_COUNT_LOTTERY_KEY="countLottery";
	
	public static final String DIRECT_QUICK_LOTTERY_MSG = "【有融网】{0}·快投有奖开奖啦~快去查看吧！回复TD退订。";
	
	public static final String DIRECT_QUICK_REWARD_SEND = "快投奖励发放";
	/**
	 * 四季变换，有你相伴
	 */
	public static final String ACTIVITY_ANNIVERSARY_CELEBRATE_NAME = "【四季变换，有你相伴】";
	
	/**
	 * 四季变换，有你相伴 key 
	 */
	public static final String ACTIVITY_ANNIVERSARY_CELEBRATE_KEY = "anniversaryCelebrate";
	
	
	public static final String ACTIVITY_ANNIVERSARY_CELEBRATE_GIFT_NAME = "【四季变换，有你相伴，翻牌赢豪礼】";
	
	
	public static final String ACTIVITY_PUBLIC_WELFARE_NAME = "【有融我心，爱在行动】";
	
	
	public static final String ACTIVITY_PUBLIC_WELFARE_KEY = "publicWelfare";
	
	
	public static final String ACTIVITY_ANNIVERSARY_CELEBRATE_SEND = "四季变换，有你相伴！恭喜您获得一张{0}元现金券，领取后7天内有效。有融二周年庆，更多福利等你来！回复TD退订";
	/**
	 * 金秋狂欢季，领走iPhone7
	 */
	public static final String ACTIVITY_OCTOBER_NAME = "【双12狂欢季，领走iPhone7】";
	/**
	 * 金秋狂欢季，领走iPhone7 key
	 */
	public static final String ACTIVITY_OCTOBER_KEY = "octoberActivity";
	
	
	public static final String ACTIVITY_SIGN_SEND_COUPON_NAME = "【签到送现金券】";
	
	public static final String ACTIVITY_SIGN_SEND_COUPON_KEY = "signSendCoupon";
	
	
	public static final String ACTIVITY_DOUBLE_NAME = "【双旦狂欢惠】";

	public static final String ACTIVITY_NEWYEAR = "【鸡年新年活动】";

	public static final String ACTIVITY_GRABBAG = "【除夕抢压岁钱】";

	public static final String ACTIVITY_LANTERNFESTIVAL = "【元宵邂逅情人节】";

	public static final String ACTIVITY_WOMENSDAY = "【女神节，绽放你的美】";
	public static final String ACTIVITY_NEWPREFERENTIAL = "【新品特惠】";
	public static final String ACTIVITY_SUBSCRIPTION = "【订阅号】";

	
	public static final String ACTIVITY_DOUBLE_KEY = "doubleActivityKey";

	
	public static final String CMS_CATEGORY_NOTICE="网站公告";
	
	public static final String CMS_CATEGORY_MEDIA="媒体报道";
	
	public static final String CMS_CATEGORY_PLATFORM="平台动态";

	public static final String ACTIVITY_NEWYEAR_KEY = "newyearActivityKey";

	public static final String ACTIVITY_GRABBAG_KEY = "grabbagActivityKey";
	
	
	public static final String ACTIVITY_FIVEBILLION_NAME = "【福临50亿】";
	
	
	public static final String ACTIVITY_FIVEBILLION_KEY = "fiveBillionActivitykey";
	
	
	public static final String ACTIVITY_FIVEBILLION_MYLOTTERY_KEY = "fiveBillionMyLotteryKey";

	public static final String ACTIVITY_LANTERNFESTIVAL_KEY = "lanternFestivalActivityKey";

	public static final String ACTIVITY_WOMENSDAY_KEY = "womensDayActivityKey";
	public static final String ACTIVITY_SUBSCRIPTION_KEY = "subscriptionActivityKey";
	
	
	public static final String ACTIVITY_DAY_DROP_GOLD_NAME = "【天降金喜】";
	
	
	public static final String ACTIVITY_DAY_DROP_GOLD_KEY = "dayDropGoldKey";

	public static final String ACTIVITY_INVITEFRIENDS_NAME = "【邀请好友】";
	public static final String ACTIVITY_FLOWERS_NAME = "【女神送花】";

	public static final String ACTIVITY_INVITEFRIENDS_KEY = "invitefriendskey";


	public static final String ACTIVITY_FLOWERS_KEY = "flowersKey";

	/*庆60亿，抢标奖励翻6倍*/
	public static final String ACTIVITY_SIXBILLION_NAME = "【庆60亿】";
	public static final String ACTIVITY_SIXBILLION_KEY = "sixBillionActivitykey";

	public static final String SPRING_ACTIVITY_TRANSACTION = "【好春来活动-投资送券】";

	public static final String SPRING_ACTIVITY_FIRST_INVEST = "【好春来活动-首投送券】";

	public static final String SPRING_ACTIVITY_TOTAL_INVEST = "【好春来活动-累计送券】";

	public static final String ACTIVITY_LABOR_NAME = "【劳动最光荣】";

	public static final String ACTIVITY_LABOR_KEY = "laborKey";
}
