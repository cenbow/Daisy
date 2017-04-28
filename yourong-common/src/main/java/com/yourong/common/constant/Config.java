package com.yourong.common.constant;

public class Config {

	/**
	 * oss key id
	 */
	public static String ossAccessKeyId = "LfpHr00gcIxuhxfe";
	/**
	 * oss key secret
	 */
	public static String ossAccessKeySecret = "ARyibWNAslko0IvRZJpcr353r3XMZ2";
	/**
	 * oss pic bucket
	 */
	public static String ossPicBucket = "yrimg-test";
	/**
	 * oss contract bucket
	 */
	public static String ossContractBucket = "yrcontract-test";
	/**
	 * oss pic url
	 */
	public static String ossPicUrl = "https://oss-cn-hangzhou.aliyuncs.com";

	/**
	 * 代收交易默认关闭时间
	 */
	public static String sinaPayDefaultOrderCloseTime = "15m";

	/**
	 * 公司用于新浪支付的内部会员id，记录流水等用，在会员表不存在
	 */
	public static String internalMemberId = "110800000000";
	/**
	 * 第一个会员的id
	 */
	public static String firstMemberId = null;

	/**
	 * 新浪存钱罐 基金的代码
	 */
	public static String finCodeSina = "000397";

	/**
	 * 充值手续费利
	 */
	public static String poundage;
	/**
	 * 充值最低手续费
	 */
	public static String minrecharge;
	/**
	 * 充值最小金额
	 */
	public static String minrechargeAmount;
	/**
	 * 提现最小金额
	 */
	public static String minwithdrawAmount;

	/**
	 * 提现手续费
	 */
	public static String freeWithdrawAmount;

	/**
	 * 投资赠送比例
	 */
	public static String investRecommendScale;

	/**
	 * 绑定邮箱活动id
	 */
	public static String activityIdForEmailBind;
	/**
	 * 实名认证活动id
	 */
	public static String activityIdForIdentityBind;
	/**
	 * 推荐投资活动id
	 */
	public static String activityIdForRecommendInvestment;
	/**
	 * 推荐注册活动id
	 */
	public static String activityIdForRecommendRegister;
	/**
	 * 完善个人信息活动id
	 */
	public static String activityIdForCompleteMemberInfo;
	/**
	 * 领压岁钱活动id
	 */
	public static String activityIdForGiftMoney;
	/**
	 * 小明和小刚的故事活动id
	 */
	public static String activityIdForXiaomingStory;
	/**
	 * 单笔大额投资活动id
	 */
	public static String activityIdForSingleInvest;
	/**
	 * 50元优惠券模板id
	 */
	public static String exchangeCouponTemplate50;
	/**
	 * 100元优惠券模板id
	 */
	public static String exchangeCouponTemplate100;
	/**
	 * 500元优惠券模板id
	 */
	public static String exchangeCouponTemplate500;
	/**
	 * 1000元优惠券模板id
	 */
	public static String exchangeCouponTemplate1000;

	/**
	 * 一马当先奖励人气值
	 */
	public static String firstInvestPopularity;
	/**
	 * 一鸣惊人奖励人气值
	 */
	public static String mostInvestPopularity;
	/**
	 * 一锤定音奖励人气值
	 */
	public static String lastInvestPopularity;
	/**
	 * 幸运女神奖励人气值
	 */
	public static String luckInvestPopularity;
	/**
	 * 一掷千金奖励人气值
	 */
	public static String mostAndLastInvestPopularity;
	
	/**
	 * 投资总额50000
	 */
	public static String investAmount1;
	/**
	 * 投资总额80000
	 */
	public static String investAmount2;
	/**
	 * 投资总额100000
	 */
	public static String investAmount3;
	/**
	 * 投资总额150000
	 */
	public static String investAmount4;
	/**
	 * 投资总额250000
	 */
	public static String investAmount5;
	/**
	 * 投资总额350000
	 */
	public static String investAmount6;
	/**
	 * 投资总额450000
	 */
	public static String investAmount7;
	/**
	 * 投资总额600000
	 */
	public static String investAmount8;
	/**
	 * 投资总额1000000
	 */
	public static String investAmount9;
	/**
	 * 投资总额1500000
	 */
	public static String investAmount10;
	/**
	 * 投资总额2000000
	 */
	public static String investAmount11;
	/**
	 * 土豪排行榜2期开始时间
	 */
	public static String rankingStartTime;
	/**
	 * 土豪排行榜2期结束时间
	 */
	public static String rankingEndTime;
	/**
	 * 土豪排行榜显示数量
	 */
	public static String rankingNum;
	
	/**
	 * 数据保全API服务接口
	 */
	public static String preservationServicesUrl;
	/**
	 * 数据保全AppKey
	 */
	public static String preservationAppKey;
	/**
	 * 数据保全AppSecret
	 */
	public static String preservationAppSecret;
	/**
	 * 合同章前置url
	 */
	public static String preMaskUrl;
	/**
	 * 合同地址
	 */
	public static String prefixPath;
	
	/**
	 * 系统来源
	 */
	public static String fromSys;
	
	/**
	 * app首次投资活动ID
	 */
	public static String activityIdForAppFirstInvestment;
	
	/**
	 * 问卷网
	 */
	private static String questionSite;

	/**
	 * 问卷密钥
	 */
	private static String questionSecretKey;

	/**
	 * 问卷地址
	 */
	private static String questionAddr;

	/**
	 * 响应通知地址
	 */
	private static String questionNotify;

	/**
	 * 问卷赠送人气值
	 */
	private static String questionPopularity;
	
	/**
	 * 问卷结束时间
	 */
	private static String questionEndTime;
	/**
	 * 问卷projId
	 */
	private static String questionProjId;
	
	/***
	 * 邀请好友赠送比例
	 */
	public static String friendRecommendScale;
	
	/**
	 * 红色星期五活动id
	 */
	public static String activityIdForRedFriday;
	
	/**
	 * 上上签回调地址
	 */
	public static String bestSignReturnUrl;
	
	/**
	 * 上上签私钥
	 */
	public static String bestSignPem;
	
	/**
	 * 上上签域名地址
	 */
	public static String bestSignHost;
	
	/**
	 * 上上签开发者编号
	 */
	public static String bestSignMid;

	public static String getOssAccessKeyId() {
		return ossAccessKeyId;
	}

	public static void setOssAccessKeyId(String ossAccessKeyId) {
		Config.ossAccessKeyId = ossAccessKeyId;
	}

	public static String getOssAccessKeySecret() {
		return ossAccessKeySecret;
	}

	public static void setOssAccessKeySecret(String ossAccessKeySecret) {
		Config.ossAccessKeySecret = ossAccessKeySecret;
	}

	public static String getOssPicBucket() {
		return ossPicBucket;
	}

	public static void setOssPicBucket(String ossPicBucket) {
		Config.ossPicBucket = ossPicBucket;
	}

	public static String getOssContractBucket() {
		return ossContractBucket;
	}

	public static void setOssContractBucket(String ossContractBucket) {
		Config.ossContractBucket = ossContractBucket;
	}

	public static String getOssPicUrl() {
		return ossPicUrl;
	}

	public static void setOssPicUrl(String ossPicUrl) {
		Config.ossPicUrl = ossPicUrl;
	}

	public static String getSinaPayDefaultOrderCloseTime() {
		return sinaPayDefaultOrderCloseTime;
	}

	public static void setSinaPayDefaultOrderCloseTime(String sinaPayDefaultOrderCloseTime) {
		Config.sinaPayDefaultOrderCloseTime = sinaPayDefaultOrderCloseTime;
	}

	public static String getInternalMemberId() {
		return internalMemberId;
	}

	public static void setInternalMemberId(String internalMemberId) {
		Config.internalMemberId = internalMemberId;
	}

	public static String getFirstMemberId() {
		return firstMemberId;
	}

	public static String getFriendRecommendScale() {
		return friendRecommendScale;
	}

	public static void setFriendRecommendScale(String friendRecommendScale) {
		Config.friendRecommendScale = friendRecommendScale;
	}

	public static void setFirstMemberId(String firstMemberId) {
		Config.firstMemberId = firstMemberId;
	}

	public static String getFinCodeSina() {
		return finCodeSina;
	}

	public static void setFinCodeSina(String finCodeSina) {
		Config.finCodeSina = finCodeSina;
	}

	public static String getPoundage() {
		return poundage;
	}

	public static void setPoundage(String poundage) {
		Config.poundage = poundage;
	}

	public static String getMinrecharge() {
		return minrecharge;
	}

	public static void setMinrecharge(String minrecharge) {
		Config.minrecharge = minrecharge;
	}

	public static String getMinrechargeAmount() {
		return minrechargeAmount;
	}

	public static void setMinrechargeAmount(String minrechargeAmount) {
		Config.minrechargeAmount = minrechargeAmount;
	}

	public static String getMinwithdrawAmount() {
		return minwithdrawAmount;
	}

	public static void setMinwithdrawAmount(String minwithdrawAmount) {
		Config.minwithdrawAmount = minwithdrawAmount;
	}

	public static String getInvestRecommendScale() {
		return investRecommendScale;
	}

	public static void setInvestRecommendScale(String investRecommendScale) {
		Config.investRecommendScale = investRecommendScale;
	}

	public static String getActivityIdForEmailBind() {
		return activityIdForEmailBind;
	}

	public static void setActivityIdForEmailBind(String activityIdForEmailBind) {
		Config.activityIdForEmailBind = activityIdForEmailBind;
	}

	public static String getActivityIdForIdentityBind() {
		return activityIdForIdentityBind;
	}

	public static void setActivityIdForIdentityBind(String activityIdForIdentityBind) {
		Config.activityIdForIdentityBind = activityIdForIdentityBind;
	}

	public static String getActivityIdForRecommendInvestment() {
		return activityIdForRecommendInvestment;
	}

	public static void setActivityIdForRecommendInvestment(String activityIdForRecommendInvestment) {
		Config.activityIdForRecommendInvestment = activityIdForRecommendInvestment;
	}

	public static String getActivityIdForRecommendRegister() {
		return activityIdForRecommendRegister;
	}

	public static void setActivityIdForRecommendRegister(String activityIdForRecommendRegister) {
		Config.activityIdForRecommendRegister = activityIdForRecommendRegister;
	}

	public static String getActivityIdForCompleteMemberInfo() {
		return activityIdForCompleteMemberInfo;
	}

	public static void setActivityIdForCompleteMemberInfo(String activityIdForCompleteMemberInfo) {
		Config.activityIdForCompleteMemberInfo = activityIdForCompleteMemberInfo;
	}

	public static String getActivityIdForGiftMoney() {
		return activityIdForGiftMoney;
	}

	public static void setActivityIdForGiftMoney(String activityIdForGiftMoney) {
		Config.activityIdForGiftMoney = activityIdForGiftMoney;
	}

	public static String getActivityIdForXiaomingStory() {
		return activityIdForXiaomingStory;
	}

	public static String getActivityIdForSingleInvest() {
		return activityIdForSingleInvest;
	}

	public static void setActivityIdForSingleInvest(String activityIdForSingleInvest) {
		Config.activityIdForSingleInvest = activityIdForSingleInvest;
	}

	public static void setActivityIdForXiaomingStory(String activityIdForXiaomingStory) {
		Config.activityIdForXiaomingStory = activityIdForXiaomingStory;
	}

	public static String getExchangeCouponTemplate50() {
		return exchangeCouponTemplate50;
	}

	public static void setExchangeCouponTemplate50(String exchangeCouponTemplate50) {
		Config.exchangeCouponTemplate50 = exchangeCouponTemplate50;
	}

	public static String getExchangeCouponTemplate100() {
		return exchangeCouponTemplate100;
	}

	public static void setExchangeCouponTemplate100(String exchangeCouponTemplate100) {
		Config.exchangeCouponTemplate100 = exchangeCouponTemplate100;
	}

	public static String getExchangeCouponTemplate500() {
		return exchangeCouponTemplate500;
	}

	public static void setExchangeCouponTemplate500(String exchangeCouponTemplate500) {
		Config.exchangeCouponTemplate500 = exchangeCouponTemplate500;
	}

	public static String getExchangeCouponTemplate1000() {
		return exchangeCouponTemplate1000;
	}

	public static void setExchangeCouponTemplate1000(String exchangeCouponTemplate1000) {
		Config.exchangeCouponTemplate1000 = exchangeCouponTemplate1000;
	}

	public static String getFreeWithdrawAmount() {
		return freeWithdrawAmount;
	}

	public static void setFreeWithdrawAmount(String freeWithdrawAmount) {
		Config.freeWithdrawAmount = freeWithdrawAmount;
	}

	public static String getFirstInvestPopularity() {
		return firstInvestPopularity;
	}

	public static void setFirstInvestPopularity(String firstInvestPopularity) {
		Config.firstInvestPopularity = firstInvestPopularity;
	}

	public static String getMostInvestPopularity() {
		return mostInvestPopularity;
	}

	public static void setMostInvestPopularity(String mostInvestPopularity) {
		Config.mostInvestPopularity = mostInvestPopularity;
	}

	public static String getLastInvestPopularity() {
		return lastInvestPopularity;
	}

	public static void setLastInvestPopularity(String lastInvestPopularity) {
		Config.lastInvestPopularity = lastInvestPopularity;
	}

	public static String getLuckInvestPopularity() {
		return luckInvestPopularity;
	}

	public static void setLuckInvestPopularity(String luckInvestPopularity) {
		Config.luckInvestPopularity = luckInvestPopularity;
	}

	public static String getInvestAmount1() {
		return investAmount1;
	}

	public static void setInvestAmount1(String investAmount1) {
		Config.investAmount1 = investAmount1;
	}

	public static String getInvestAmount2() {
		return investAmount2;
	}

	public static void setInvestAmount2(String investAmount2) {
		Config.investAmount2 = investAmount2;
	}

	public static String getInvestAmount3() {
		return investAmount3;
	}

	public static void setInvestAmount3(String investAmount3) {
		Config.investAmount3 = investAmount3;
	}

	public static String getInvestAmount4() {
		return investAmount4;
	}

	public static void setInvestAmount4(String investAmount4) {
		Config.investAmount4 = investAmount4;
	}

	public static String getInvestAmount5() {
		return investAmount5;
	}

	public static void setInvestAmount5(String investAmount5) {
		Config.investAmount5 = investAmount5;
	}

	public static String getInvestAmount6() {
		return investAmount6;
	}

	public static void setInvestAmount6(String investAmount6) {
		Config.investAmount6 = investAmount6;
	}

	public static String getInvestAmount7() {
		return investAmount7;
	}

	public static void setInvestAmount7(String investAmount7) {
		Config.investAmount7 = investAmount7;
	}

	public static String getInvestAmount8() {
		return investAmount8;
	}

	public static void setInvestAmount8(String investAmount8) {
		Config.investAmount8 = investAmount8;
	}

	public static String getInvestAmount9() {
		return investAmount9;
	}

	public static void setInvestAmount9(String investAmount9) {
		Config.investAmount9 = investAmount9;
	}

	public static String getInvestAmount10() {
		return investAmount10;
	}

	public static void setInvestAmount10(String investAmount10) {
		Config.investAmount10 = investAmount10;
	}

	public static String getInvestAmount11() {
		return investAmount11;
	}

	public static void setInvestAmount11(String investAmount11) {
		Config.investAmount11 = investAmount11;
	}

	public static String getRankingStartTime() {
		return rankingStartTime;
	}

	public static void setRankingStartTime(String rankingStartTime) {
		Config.rankingStartTime = rankingStartTime;
	}

	public static String getRankingEndTime() {
		return rankingEndTime;
	}

	public static void setRankingEndTime(String rankingEndTime) {
		Config.rankingEndTime = rankingEndTime;
	}

	public static String getRankingNum() {
		return rankingNum;
	}

	public static void setRankingNum(String rankingNum) {
		Config.rankingNum = rankingNum;
	}

	public static String getPreservationServicesUrl() {
		return preservationServicesUrl;
	}

	public static void setPreservationServicesUrl(String preservationServicesUrl) {
		Config.preservationServicesUrl = preservationServicesUrl;
	}

	public static String getPreservationAppKey() {
		return preservationAppKey;
	}

	public static void setPreservationAppKey(String preservationAppKey) {
		Config.preservationAppKey = preservationAppKey;
	}

	public static String getPreservationAppSecret() {
		return preservationAppSecret;
	}

	public static void setPreservationAppSecret(String preservationAppSecret) {
		Config.preservationAppSecret = preservationAppSecret;
	}

	public static String getPreMaskUrl() {
		return preMaskUrl;
	}

	public static void setPreMaskUrl(String preMaskUrl) {
		Config.preMaskUrl = preMaskUrl;
	}

	public static String getPrefixPath() {
		return prefixPath;
	}

	public static void setPrefixPath(String prefixPath) {
		Config.prefixPath = prefixPath;
	}

	public static String getActivityIdForAppFirstInvestment() {
		return activityIdForAppFirstInvestment;
	}

	public static void setActivityIdForAppFirstInvestment(
			String activityIdForAppFirstInvestment) {
		Config.activityIdForAppFirstInvestment = activityIdForAppFirstInvestment;
	}

	public static String getQuestionSite() {
		return questionSite;
	}

	public static void setQuestionSite(String questionSite) {
		Config.questionSite = questionSite;
	}

	public static String getQuestionSecretKey() {
		return questionSecretKey;
	}

	public static void setQuestionSecretKey(String questionSecretKey) {
		Config.questionSecretKey = questionSecretKey;
	}

	public static String getQuestionAddr() {
		return questionAddr;
	}

	public static void setQuestionAddr(String questionAddr) {
		Config.questionAddr = questionAddr;
	}

	public static String getQuestionNotify() {
		return questionNotify;
	}

	public static void setQuestionNotify(String questionNotify) {
		Config.questionNotify = questionNotify;
	}

	public static String getQuestionPopularity() {
		return questionPopularity;
	}

	public static void setQuestionPopularity(String questionPopularity) {
		Config.questionPopularity = questionPopularity;
	}

	public static String getQuestionEndTime() {
		return questionEndTime;
	}

	public static void setQuestionEndTime(String questionEndTime) {
		Config.questionEndTime = questionEndTime;
	}

	public static String getQuestionProjId() {
		return questionProjId;
	}

	public static void setQuestionProjId(String questionProjId) {
		Config.questionProjId = questionProjId;
	}

	public static String getFromSys() {
		return fromSys;
	}

	public static void setFromSys(String fromSys) {
		Config.fromSys = fromSys;
	}

	public static String getActivityIdForRedFriday() {
		return activityIdForRedFriday;
	}

	public static void setActivityIdForRedFriday(String activityIdForRedFriday) {
		Config.activityIdForRedFriday = activityIdForRedFriday;
	}

	public static String getMostAndLastInvestPopularity() {
		return mostAndLastInvestPopularity;
	}

	public static void setMostAndLastInvestPopularity(
			String mostAndLastInvestPopularity) {
		Config.mostAndLastInvestPopularity = mostAndLastInvestPopularity;
	}

	public static String getBestSignReturnUrl() {
		return bestSignReturnUrl;
	}
	public static void setBestSignReturnUrl(String bestSignReturnUrl) {
		Config.bestSignReturnUrl = bestSignReturnUrl;
	}

	public static String getBestSignPem() {
		return bestSignPem;
	}

	public static void setBestSignPem(String bestSignPem) {
		Config.bestSignPem = bestSignPem;
	}

	public static String getBestSignHost() {
		return bestSignHost;
	}

	public static void setBestSignHost(String bestSignHost) {
		Config.bestSignHost = bestSignHost;
	}

	public static String getBestSignMid() {
		return bestSignMid;
	}

	public static void setBestSignMid(String bestSignMid) {
		Config.bestSignMid = bestSignMid;
	}
	
}
