package com.yourong.api.service;

import com.google.common.base.Optional;
import com.yourong.api.dto.MemberSessionDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.common.domain.DynamicParamBuilder;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityLottery;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.mc.model.biz.*;
import com.yourong.core.mc.model.biz.ActivityForSubscription;
import com.yourong.core.mc.model.query.ActivityLotteryResultQuery;
import com.yourong.core.tc.model.Order;

import java.util.List;
import java.util.Map;

/**
 * 活动service
 * 
 * @author wangyanji
 *
 */
public interface ActivityLotteryService {

	/**
	 * 周年庆-瓜分红包
	 * 
	 * @param param
	 * @return
	 */
	public ResultDTO<Object> anniversaryGetRed(String param, String mobile);

	/**
	 * 获取活动
	 * 
	 * @param activityId
	 * @return
	 */
	public Activity getActivityById(Long activityId);
	
	/**
	 * 
	 * @Description:百万现金券活动显示余额
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月7日 上午11:58:12
	 */
	public Integer getMillionCouponFund();

	/**
	 * 
	 * @Description:获取活动（缓存）
	 * @param activityId
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月6日 下午2:11:19
	 */
	public Optional<Activity> getActivityByCache(Long activityId);

	/**
	 * 
	 * @Description:春节活动初始化
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月22日 下午2:30:59
	 */
	public Object springFestivalInit(Optional<MemberSessionDto> optOfMember);

	/**
	 * 
	 * @Description:春节许愿
	 * @param memberId
	 * @param messageTemplateId
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月30日 下午5:28:57
	 */
	public Object springFestivalMakeWish(Long memberId, Long messageTemplateId);

	/**
	 * 
	 * @Description:春节领券
	 * @param memberId
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月30日 下午5:28:57
	 */
	public Object springFestivalReceiveCoupon(Long memberId);

	/**
	 * 
	 * @Description:获取交易红包的分享码
	 * @param orderId
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月11日 下午1:52:56
	 */
	public Object getTransactionRedBagUrl(Long orderId, Long memberId);

	/**
	 * 
	 * @Description:领红包
	 * @param redBagCode
	 * @param mobile
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月11日 下午8:27:32
	 */
	public Object redBagReceive(String redBagCode, Long mobile);

	/**
	 * 
	 * @Description:红包初始化
	 * @param redBagCode
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月22日 下午5:01:44
	 */
	public Object redBagInit(String redBagCode);

	/**
	 * 
	 * @Description:支付后判断是否有活动
	 * @param order
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月13日 上午10:21:22
	 */
	public boolean hasActivityFlag(Order order, int activitySourceType);

	/**
	 * 
	 * @Description:五一四重礼初始化接口
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月18日 下午1:52:23
	 */
	public Object mayDay4GiftsInit(DynamicParamBuilder paramBuilder);

	/**
	 * 
	 * @Description:五一四重礼领券
	 * @param memberId
	 * @param couponAmount
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月18日 下午5:08:19
	 */
	public Object mayDay4GiftsReceive(DynamicParamBuilder paramBuilder);

	/**
	 * 
	 * @Description:红包适配页数据加载
	 * @param paramBuilder
	 * @return
	 * @author: wangyanji
	 * @time:2016年5月20日 上午11:04:51
	 */
	public Object popRedPackageMineInit(DynamicParamBuilder paramBuilder);

	/**
	 * 
	 * @Description:五一四重礼领券
	 * @param memberId
	 * @param couponAmount
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月18日 下午5:08:19
	 */
	public Object fellInLoveFor520Init(DynamicParamBuilder paramBuilder);

	/**
	 * 
	 * @Description:破二十亿活动初始化
	 * @param paramBuilder
	 * @return
	 * @author: wangyanji
	 * @time:2016年6月2日 下午7:15:12
	 */
	public Object break2BillionInit(DynamicParamBuilder paramBuilder);

	/**
	 * 
	 * @Description:破二十亿活动互动
	 * @param memberId
	 * @param activityPart
	 * @return
	 * @author: wangyanji
	 * @time:2016年6月2日 下午9:14:35
	 */
	public Object break2BillionReceive(DynamicParamBuilder paramBuilder);

	/**
	 * 
	 * @Description:里程拉新初始化
	 * @param paramBuilder
	 * @return
	 * @author: wangyanji
	 * @time:2016年6月12日 下午4:34:28
	 */
	public Object inviteFriendInit(DynamicParamBuilder paramBuilder);

	/**
	 * 
	 * @Description:里程拉新领取
	 * @param paramBuilder
	 * @return
	 * @author: wangyanji
	 * @time:2016年6月12日 下午4:34:28
	 */
	public Object inviteFriendReceive(DynamicParamBuilder paramBuilder);

	/**
	 * 
	 * @Description:里程拉新助力榜
	 * @param paramBuilder
	 * @return
	 * @author: wangyanji
	 * @time:2016年6月12日 下午4:34:28
	 */
	public Object inviteFriendList(DynamicParamBuilder paramBuilder);
	/**
	 * 是否已领取
	 * @param id
	 * @param parseLong
	 * @return
	 * @throws Exception 
	 */
	public boolean isReceived(Long id, Long parseLong) throws Exception;
    /**
     * 领取红包
     * @param id
     * @return
     */
	//public ResultDTO<ActivityForAnniversary> receiveCelebrationA(Long id);
	/**
	 *  a轮初始化
	 * @param builder
	 * @return
	 */
	public Object celebrationActivityInit(DynamicParamBuilder builder)throws Exception;
	/**
	 * 领取红包
	 * @param paramBuilder
	 * @return
	 */
	public Object receiveCelebrationA(DynamicParamBuilder paramBuilder);
	/**
	 * @Description:风险测评
	 * @param paramBuilder
	 * @return
	 */
	public Object saveEvaluation(DynamicParamBuilder paramBuilder);
	/**
	 * 
	 * @Description:奥运初始化
	 * @param builder
	 * @return
	 * @author: chaisen
	 * @throws Exception 
	 * @time:2016年7月19日 下午2:02:39
	 */
	public Object olympicActivityInit(DynamicParamBuilder builder) throws Exception;
	/**
	 * 
	 * @Description:猜奖牌
	 * @param paramBuilder
	 * @return
	 * @author: chaisen
	 * @time:2016年7月19日 下午2:13:40
	 */
	public Object guessMedal(DynamicParamBuilder paramBuilder);
	/**
	 * 
	 * @Description:猜金牌
	 * @param paramBuilder
	 * @return
	 * @author: chaisen
	 * @time:2016年7月19日 下午2:13:53
	 */
	public Object guessGold(DynamicParamBuilder paramBuilder);
	/**
	 * 
	 * @Description:集奥运
	 * @param paramBuilder
	 * @return
	 * @author: chaisen
	 * @time:2016年7月19日 下午2:14:04
	 */
	public Object setOlympic(DynamicParamBuilder paramBuilder);
	
	//TODO临时处理，下个版本调整
	public ResultDTO<Object> signContract(DynamicParamBuilder builder);
	/**
	 * 新手六重礼
	 * @param builder
	 * @return
	 */
	public Object newSixGiftInit(DynamicParamBuilder builder);
	
	/**
	 * 
	 * @Description:加入战队
	 * @param paramBuilder
	 * @return
	 * @author: chaisen
	 * @time:2016年7月1日 下午12:52:26
	 */
	public Object joinJulyTeam(DynamicParamBuilder paramBuilder);
	/**
	 * 
	 * @Description:押注战队
	 * @param paramBuilder
	 * @return
	 * @author: chaisen
	 * @time:2016年7月1日 下午2:11:39
	 */
	public Object betJulyTeam(DynamicParamBuilder paramBuilder);
	
	/**
	 * 
	 * @Description:领取现金券
	 * @param paramBuilder
	 * @return
	 * @author: chaisen
	 * @time:2016年7月2日 上午9:17:26
	 */
	public Object receiveJulyTeamCoupon(DynamicParamBuilder paramBuilder);
	/**
	 * 
	 * @Description:七月战队初始化
	 * @param builder
	 * @return
	 * @author: chaisen
	 * @throws Exception 
	 * @time:2016年7月4日 下午8:38:11
	 */
	public Object julyTeamInit(DynamicParamBuilder builder) throws Exception;
	/**
	 * 
	 * @Description:数据频道
	 * @param builder
	 * @return
	 * @author: chaisen
	 * @time:2016年10月11日 上午9:56:17
	 */
	public Object dataChannelInit(DynamicParamBuilder builder);

	public Object realTimeData();
	/**
	 * 
	 * @Description:周年庆专题页
	 * @param builder
	 * @return
	 * @author: chaisen
	 * @time:2016年10月28日 下午12:41:06
	 */
	public Object anniversarySpecial(DynamicParamBuilder builder);
	/**
	 * 
	 * @Description:周年庆倒计时
	 * @param builder
	 * @return
	 * @author: chaisen
	 * @time:2016年10月12日 上午11:09:19
	 */
	public Object anniversaryLastInit(DynamicParamBuilder builder);
	/**
	 * 
	 * @Description:领取红包
	 * @param paramBuilder
	 * @return
	 * @author: chaisen
	 * @time:2016年10月12日 下午12:53:55
	 */
	public Object receiveAnniversaryCoupon(DynamicParamBuilder paramBuilder);
	/**
	 * 
	 * @Description:周年庆初始化
	 * @param builder
	 * @return
	 * @author: chaisen
	 * @time:2016年10月21日 上午9:28:33
	 */
	public Object anniversaryInit(DynamicParamBuilder builder);
	
	/**
	 * 
	 * @Description:周年庆领取现金券
	 * @param paramBuilder
	 * @return
	 * @author: chaisen
	 * @time:2016年10月21日 上午9:37:52
	 */
	public Object receiveCouponAnniversary(DynamicParamBuilder paramBuilder);
	
	
    /**
     * 
     * @Description:翻牌赢取豪礼
     * @param paramBuilder
     * @return
     * @author: chaisen
     * @time:2016年10月21日 上午9:40:02
     */
	public Object receiveRewardAnniversary(DynamicParamBuilder paramBuilder);
	/**
	 * 
	 * @Description:周年庆公益初始化
	 * @param builder
	 * @return
	 * @author: chaisen
	 * @time:2016年11月15日 上午11:00:46
	 */
	public Object publicWelfareInit(DynamicParamBuilder builder);
	
	/**
	 * 
	 * @Description:支持公益获得人气值
	 * @param paramBuilder
	 * @return
	 * @author: chaisen
	 * @time:2016年11月15日 上午11:21:18
	 */
	public Object supportWelfare(DynamicParamBuilder paramBuilder);
	/**
	 * 
	 * @Description:十月活动初始化
	 * @return
	 * @author: chaisen
	 * @time:2016年10月11日 上午9:56:28
	 */
	public Object octoberInit(DynamicParamBuilder builder);
	/**
	 * 
	 * @Description:双旦初始化
	 * @param builder
	 * @return
	 * @author: chaisen
	 * @time:2016年12月9日 下午1:37:24
	 */
	public Object doubleDanInit(DynamicParamBuilder builder);
	/**
	 * 
	 * @Description:双旦领取红包
	 * @param paramBuilder
	 * @return
	 * @author: chaisen
	 * @time:2016年12月9日 下午1:42:05
	 */
	public Object doubleReceiveCoupon(DynamicParamBuilder paramBuilder);
	
	public Object getQuickRewardRule();

	/**
	 * 查询活动抽奖数据
	 * @param paraMap
	 * @return
	 */
	public ActivityLottery selectByMemberActivity(Map<String, Object> paraMap);

	/**
	 * 鸡年新年活动抢红包
	 * @param paramBuilder
	 * @return
     */
	public Object newYearLuckyMoney(DynamicParamBuilder paramBuilder);

	/**
	 * 抢除夕压岁钱
	 * @param paramBuilder
	 * @return
	 */
	public Object grab(DynamicParamBuilder paramBuilder);

	/**
	 * 查询除夕抢压岁钱结果
	 * @param activityId
	 * @return
     */
	public List<ActivityGrabResultBiz> queryGrabResult(Long activityId);

	/**
	 * 查询抽奖表活动数据条数
	 * @param activityId
	 * @return
     */
	public Integer queryCountByActivityId(Long activityId);

	/**
	 * 查询用户是否可以抢红包
	 * @param memberId
	 * @param activityId
     * @return
     */
	public boolean queryGrabStatus(Long memberId,Long activityId);

	/**
	 * 元宵活动
	 * @return
	 */
	public ResultDO<Activity> lanternFestival();

	/**
	 * 38节签到
	 * @param paramBuilder
	 * @return
     */
	public ResultDTO<Activity> goddessSign(DynamicParamBuilder paramBuilder);

	/**
	 * 38节数据
	 * @param memberId
	 * @return
     */
	public ResultDTO<ActivityForWomensDay> womensDayInit(Long memberId);

	/**
	 * 领取礼包
	 * @param paramBuilder
	 * @return
     */
	public ResultDTO<Activity> womensDayBag(DynamicParamBuilder paramBuilder);

	/**
	 * 订阅号活动领取
	 * @param paramBuilder
	 * @return
     */
	public ResultDTO<Activity> subscription(DynamicParamBuilder paramBuilder);

	/**
	 * 订阅号活动数据
	 * @param memberId
	 * @return
     */
	public ResultDO<ActivityForSubscription> subscriptionData(Long memberId);

	/**
	 * @param builder
	 * @return
	 */
	public Object fiveBillionIndex(DynamicParamBuilder builder);
	
	/**
	 * 领取福袋
	 * @param paramBuilder
	 * @return
	 */
	public Object receiveLuckBag(DynamicParamBuilder paramBuilder);
	
	/**
	 * 50亿抽奖
	 * @param paramBuilder
	 * @return
	 */
	public Object lotteryLuckBoth(DynamicParamBuilder paramBuilder);

	/**
	 * @param query
	 * @return
	 */
	
	public Object myLotteryRecord(DynamicParamBuilder paramBuilder);
	/**
	 * 
	 * @param builder
	 * @return
	 */
	public Object dayDropGoldInit(DynamicParamBuilder builder);
	/**
	 * 天降金喜领取现金券
	 * @param paramBuilder
	 * @return
	 */
	public Object receiveCouponGold(DynamicParamBuilder paramBuilder);
	
	/**
	 * 
	 * @param builder
	 * @return
	 */
	public Object springComingInit(DynamicParamBuilder builder);
	/**
	 * 好春来领取现金券
	 * @param paramBuilder
	 * @return
	 */
	public Object springComingActivity(DynamicParamBuilder paramBuilder);

	/**
	 * 邀请好友活动数据
	 * @param paramBuilder
	 * @return
     */
	public ResultDTO<ActivityForInviteFriend> inviteFriendData(DynamicParamBuilder paramBuilder);

	/**
	 * 邀请好友详情
	 * @param paramBuilder
	 * @return
     */
	public ResultDTO<Page<ActivityForInviteFriendDetail>> inviteFriendDetail(DynamicParamBuilder paramBuilder);
	/**
	 * 送花活动领取
	 * @param paramBuilder
	 * @return
	 */
	public ResultDTO<Activity> receiveFlowers(DynamicParamBuilder paramBuilder);

	/**
	 * 送花活动初始数据
	 * @param memberId
	 * @return
	 */
	public ResultDTO<ActivityForSubscription> flowersData(Long memberId);

	/**
	 * 51活动数据
	 * @param memberId
	 * @return
	 */
	public ResultDTO<ActivityForLabor> laborData(Long memberId);

	/**
	 * 51活动领取礼包
	 * @param paramBuilder
	 * @return
	 */
	public ResultDTO<Activity> receiveLabor(DynamicParamBuilder paramBuilder);
}
