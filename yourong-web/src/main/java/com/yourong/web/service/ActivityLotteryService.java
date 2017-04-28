package com.yourong.web.service;

import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityLottery;
import com.yourong.core.mc.model.biz.ActivityForAnniversary;
import com.yourong.core.mc.model.biz.ActivityForFiveBillionRetrun;
import com.yourong.core.mc.model.biz.ActivityForJulyRetrun;
import com.yourong.core.mc.model.biz.ActivityForRankListBiz;
import com.yourong.core.mc.model.biz.ActivityHistoryBiz;
import com.yourong.core.mc.model.biz.ActivityLotteryBiz;
import com.yourong.core.mc.model.biz.ActivityLotteryResultBiz;
import com.yourong.core.mc.model.biz.ActivityForWomensDay;
import com.yourong.core.mc.model.biz.ActivityForInviteFriend;
import com.yourong.core.mc.model.biz.ActivityForLabor;
import com.yourong.core.mc.model.biz.ActivityForInviteFriendDetail;
import com.yourong.core.mc.model.query.ActivityLotteryResultQuery;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.query.TransactionQuery;
import com.yourong.web.dto.MemberSessionDto;

/**
 * 活动service
 * 
 * @author wangyanji
 *
 */
public interface ActivityLotteryService {

	/**
	 * 获取活动ID
	 * 
	 * @param activityName
	 * @return
	 */
	public Long getActivityIdByPropertiesName(String activityName);

	/**
	 * 亿路上有你抽奖
	 * 
	 * @param activityId
	 * @param memberId
	 * @return
	 */
	public ActivityLotteryBiz yiRoadDrawLottery(Long memberId);

	/**
	 * 亿路上有你显示最新中奖结果集
	 * 
	 * @param activityId
	 * @param rowNum
	 * @return
	 */
	public List<ActivityLotteryResultBiz> yiRoadNewLotteryResult();

	/**
	 * 亿路上有你显示当前用户的剩余抽奖次数
	 * 
	 * @param memberId
	 * @return
	 */
	public int yiRoadNewLotteryCount(Long memberId);

	/**
	 * 亿路上有你当天是否分享
	 * 
	 * @param memberId
	 * @return
	 */
	public int showYiRoadShareFlag(Long memberId);

	/**
	 * 亿路上有你亿举夺魁排行榜
	 * 
	 * @return
	 */
	public ActivityForRankListBiz yiRoadRankList();

	/**
	 * 获取下载次数
	 * 
	 * @return
	 */
	public int getDownTotalCount();

	/**
	 * 亿起分享赠送人气值
	 * 
	 * @param memberId
	 * @return 0,失败, 1,生成
	 */
	public ActivityLotteryBiz yiRoadShare(Long memberId);

	/**
	 * 亿路上有你分享滚屏
	 * 
	 * @return
	 */
	public List<ActivityLotteryResultBiz> yiRoadShareList();

	/**
	 * 获取活动对象
	 * 
	 * @param id
	 * @return
	 */
	public Activity getActivityId(Long id);

	/**
	 * 月度活动奇偶排行
	 * 
	 * @return
	 */
	public ActivityForRankListBiz weeklyRank();

	/**
	 * 周年庆-奖励大放送初始化
	 * 
	 * @return
	 */
	public ResultDO<ActivityForAnniversary> anniversaryPrizeInit(Long memberId);

	/**
	 * 周年庆-奖励大放送初始化-领取
	 * 
	 * @return
	 */
	public ResultDO<ActivityForAnniversary> anniversaryReceivePrize(Long memberId, Long activityId);

	/**
	 * 周年庆-奖励大放送初始化-巅峰1小时
	 * 
	 * @return
	 */
	public ResultDO<ActivityForRankListBiz> anniversaryOneHourList();

	/**
	 * 周年庆-幸运二十五宫格
	 * 
	 * @param memberId
	 * @param gridIndex
	 * @return
	 */
	public ResultDO<ActivityForAnniversary> anniversaryTwentyFiveGrid(Long memberId, Integer chip);

	/**
	 * 周年庆-幸运二十五宫格中奖结果
	 * 
	 * @return
	 */
	public ResultDO<ActivityLotteryResultBiz> anniversaryTwentyFiveGridResult();

	/**
	 * 周年庆-分享链接
	 * 
	 * @param transactionId
	 * @return
	 */
	public ResultDO<ActivityForAnniversary> anniversaryShareUrl(Long transactionId, Long memberId);

	/**
	 * 获取活动信息（缓存）
	 * 
	 * @param activityId
	 * @return
	 */
	public Optional<Activity> getActivityByCache(Long activityId);

	/**
	 * 
	 * @Description:圣诞节&元旦初始化
	 * @param optOfMember
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月20日 下午1:39:24
	 */
	public Object doubleDanInit(Optional<MemberSessionDto> optOfMember);

	/**
	 * 
	 * @Description:圣诞领券
	 * @param memeberId
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月28日 下午2:00:50
	 */
	public Object doubleDanReceiveChristmas(Long memberId);

	/**
	 * 
	 * @Description:元旦领券
	 * @param memberId
	 * @param giftIndex
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月26日 上午11:55:21
	 */
	public Object doubleDanReceiveNewYear(Long memberId, int giftIndex);

	/**
	 * 
	 * @Description:领取神秘新年礼
	 * @param memberId
	 * @return
	 * @author: wangyanji
	 * @time:2015年11月26日 下午3:23:00
	 */
	public Object doubleDanReceiveScretGift(Long memberId);

	/**
	 * 
	 * @Description:百万现金券页面初始化
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月5日 下午3:40:19
	 */
	public Object millionFundInit();

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
	 * @param transactionId
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月11日 下午1:52:56
	 */
	public Object getTransactionRedBagUrl(Long transactionId, Long memberId);

	/**
	 * 
	 * @Description:获取红包规则
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月11日 下午7:43:05
	 */
	public Object getRedBagRule();

	/**
	 * 
	 * @Description:处理交易后的活动业务
	 * @param t
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月28日 下午2:07:08
	 */
	public void activityAfterTransaction(Transaction t);

	/**
	 * 
	 * @Description:破十亿活动初始化
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月5日 下午3:40:19
	 */
	public Object breakBillionInit();

	/**
	 * 
	 * @Description:获取新手任务列表
	 * @return
	 * @author: wangyanji
	 * @time:2016年2月26日 下午3:30:20
	 */
	public ResultDO<ActivityHistoryBiz> getNewerMissionList();

	/**
	 * 
	 * @Description:投房有礼两重礼
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月5日 下午1:38:38
	 */
	public Object house2GiftsInit();

	/**
	 * 
	 * @Description:五一四重礼初始化接口
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月18日 下午1:52:23
	 */
	public Object mayDay4GiftsInit(Optional<MemberSessionDto> optMember);

	/**
	 * 
	 * @Description:五一四重礼领券
	 * @param memberId
	 * @param couponAmount
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月18日 下午5:08:19
	 */
	public Object mayDay4GiftsReceive(Long memberId, int couponAmount);

	/**
	 * 
	 * @Description:520活动初始化
	 * @param optMember
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月29日 下午4:16:04
	 */
	public Object fellInLoveFor520Init(Optional<MemberSessionDto> optMember);
	
	/**
	 * 
	 * @Description:破二十亿活动初始化
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月5日 下午3:40:19
	 */
	public Object break2BillionInit(Optional<MemberSessionDto> optMember);

	/**
	 * 
	 * @Description:破二十亿活动互动
	 * @param memberId
	 * @param activityPart
	 * @author: wangyanji
	 * @time:2016年6月2日 上午10:00:26
	 */
	public Object break2BillionReceive(Long memberId, int activityPart);

	/**
	 * 
	 * @Description:里程拉新初始化
	 * @param optMember
	 * @return
	 * @author: wangyanji
	 * @time:2016年6月11日 下午3:16:56
	 */
	public Object inviteFriendInit(Optional<MemberSessionDto> optMember);

	/**
	 * 
	 * @Description:里程拉新领取
	 * @param memberId
	 * @param rewardType
	 * @return
	 * @author: wangyanji
	 * @time:2016年6月12日 上午9:35:39
	 */
	public Object inviteFriendReceive(Long memberId, int rewardType);

	/**
	 * 
	 * @Description:里程拉新助力榜
	 * @param memberId
	 * @param startNo
	 * @return
	 * @author: wangyanji
	 * @time:2016年6月12日 上午9:35:39
	 */
	public Object inviteFriendList(Long memberId, int startNo);
	/**
	 *  庆a活动领取红包
	 * @param id
	 * @return
	 */
	public ResultDO<ActivityForAnniversary> receiveCelebrationA(Long id);

	boolean isReceived(Long memberId, Long activityId) throws Exception;
	/**
	 * 初始化a轮活动
	 * @param 
	 * @return
	 * @throws Exception
	 */
	public Object initCelebration(Optional<MemberSessionDto> optMember)throws Exception;
	/**
	 * 
	 * @Description:玩转奥运初始化
	 * @param fromNullable
	 * @return
	 * @author: chaisen
	 * @throws Exception 
	 * @time:2016年7月11日 下午2:23:34
	 */
	public Object olympicInit(Optional<MemberSessionDto> fromNullable) throws Exception;
	/**
	 * 
	 * @Description:猜奖牌
	 * @param id
	 * @param medalType
	 * @return
	 * @author: chaisen
	 * @time:2016年7月12日 上午9:11:42
	 */
	public Object guessMedal(Long memberId, int medalType);
	/**
	 * 
	 * @Description:猜金牌
	 * @param id
	 * @param popularityValue
	 * @param goldNumber
	 * @return
	 * @author: chaisen
	 * @time:2016年7月12日 上午9:11:53
	 */
	public Object guessGold(Long memberId, int popularityValue, int goldNumber);
	/**
	 * 
	 * @Description:集奥运
	 * @param memberId
	 * @param puzzle
	 * @return
	 * @author: chaisen
	 * @time:2016年7月12日 下午3:05:27
	 */
	public Object setOlympic(Long memberId, String puzzle);
	
	/**
	 * 
	 * @Description:七月战队初始化
	 * @param optMember
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年6月29日 下午5:08:04
	 */
	public Object julyTeamInit(Optional<MemberSessionDto> optMember)throws Exception;
	/**
	 * 
	 * @Description:加入战队
	 * @param id
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年6月30日 下午4:17:09
	 */
	public ResultDO<ActivityForJulyRetrun> julyTeamJoin(Long id);
	/**
	 * 
	 * @Description:押注
	 * @param id
	 * @param popularityValue
	 * @return
	 * @author: chaisen
	 * @time:2016年6月30日 下午6:06:09
	 */
	public Object betJulyTeam(Long id, int popularityValue,int groupType);
	/**
	 * 
	 * @Description:七月战队 领取红包
	 * @param id
	 * @param couponAmount
	 * @return
	 * @author: chaisen
	 * @time:2016年7月1日 下午2:25:30
	 */
	public ResultDO<ActivityForJulyRetrun> receiveJulyTeamCoupon(Long id, int couponAmount);
	/**
	 * 
	 * @Description:平台数据频道
	 * @return
	 * @author: chaisen
	 * @time:2016年8月26日 下午4:45:24
	 */
	public Object dataChannelInit();

	public Object realTimeData();
	/**
	 * 
	 * @Description:周年庆活动
	 * @param fromNullable
	 * @return
	 * @author: chaisen
	 * @throws Exception 
	 * @time:2016年10月17日 下午1:30:46
	 */
	public Object anniversaryInit(Optional<MemberSessionDto> fromNullable) throws Exception;
	/**
	 * 
	 * @Description:领取红包
	 * @param memberId
	 * @param type
	 * @param couponAmount
	 * @return
	 * @author: chaisen
	 * @time:2016年10月17日 下午2:28:22
	 */
	public Object receiveCouponAnniversary(Long memberId, int type, int couponAmount);
	/**
	 * 
	 * @Description:消耗人气值领取奖品
	 * @param id
	 * @param chip
	 * @param couponAmount
	 * @return
	 * @author: chaisen
	 * @time:2016年10月19日 下午9:20:20
	 */
	public Object receiveRewardAnniversary(Long id, int chip, int popularValue);
	/**
	 * 
	 * @Description:十月活动初始化
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年10月9日 下午1:11:28
	 */
	public Object octoberInit(Optional<MemberSessionDto> fromNullable) ;
	/**
	 * 
	 * @Description:双旦活动初始化
	 * @param fromNullable
	 * @return
	 * @author: chaisen
	 * @time:2016年12月6日 上午10:18:27
	 */
	public Object doubleInit(Optional<MemberSessionDto> fromNullable);
	/**
	 * 
	 * @Description:双旦抢红包
	 * @param id
	 * @param type
	 * @return
	 * @author: chaisen
	 * @time:2016年12月6日 上午10:37:07
	 */
	public Object doubleReceiveCoupon(Long memberId);
	
	
	

	/**
	 * 查询活动抽奖数据
	 * @param paraMap
	 * @return
     */
	public ActivityLottery selectByMemberActivity(Map<String, Object> paraMap);

	/**
	 * 鸡年新年活动抢红包
	 * @param memberId
	 * @return
     */
	public Object newYearLuckyMoney(Long memberId,Long templateId);

	/**
	 * 元宵活动
	 * @return
     */
	public ResultDO<Activity> lanternFestival();

	/**
	 * 
	 * @param memberId
	 * @param couponAmount
	 * @return
	 */
	public Object receiveLuckBag(Long memberId, int couponAmount);

	/**
	 * @param id
	 * @return
	 */
	public Object lotteryLuckBoth(Long memberId,int type);

	/**
	 * @param 50亿初始化
	 * @return
	 */
	public Object fiveBillionInit(Optional<MemberSessionDto> fromNullable);

	/**
	 * @param query
	 * @return
	 */
	public Page<ActivityForFiveBillionRetrun> myLotteryRecord(
			ActivityLotteryResultQuery query);

	/**
	 * 38节活动数据
	 * @return
     */
	public ResultDO<ActivityForWomensDay> womensDayData(Long memberId);

	/**
	 * 38节领取礼包
	 * @param memberId
	 * @return
     */
	public ResultDO<ActivityForWomensDay> womensDayBag(Long memberId);
	/**
	 * 天降金喜初始化
	 * @param fromNullable
	 * @return
	 */
	public Object dayDropInit(Optional<MemberSessionDto> fromNullable);

	public Object receiveCouponGold(Long id, int couponAmount);
	
	public Object springComingActivity(Long id, Long templateId);
	
	/**
	 * 好春来初始化
	 * @param fromNullable
	 * @return
	 */
	public ResultDO<Object> springComingInit(Optional<MemberSessionDto> fromNullable);
	

	/**
	 * 邀请好友数据
	 * @param memberId
	 * @return
     */
	public ResultDO<ActivityForInviteFriend> inviteFriendData(Long memberId);

	/**
	 * 邀请好友详情
	 * @param memberId
	 * @return
     */
	public Page<ActivityForInviteFriendDetail> inviteFriendDetail(Long memberId,Integer pageNo);

	/**
	 * 51活动数据
	 * @param memberId
	 * @return
	 */
	public ResultDO<ActivityForLabor> laborInit(Long memberId);

	/**
	 * 51活动领取礼包
	 * @param memberId
	 * @return
	 */
	public ResultDO<Activity> receiveLabor(Long memberId);
}
