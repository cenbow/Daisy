package com.yourong.core.mc.manager;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityLottery;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.mc.model.biz.*;
import com.yourong.core.mc.model.query.ActivityLotteryResultQuery;
import com.yourong.core.uc.model.Member;

/**
 * 活动抽奖manager
 * 
 * @author wangyanji
 *
 */
public interface ActivityLotteryManager {

	/**
	 * 新增用户抽奖次数
	 * 
	 * @param activityId
	 * @param memberId
	 * @param inputNum
	 */
	public void addMemberLotteryNumber(Long activityId, Long memberId, int inputNum, String jsonCondition) throws ManagerException;

	/**
	 * 获取用户指定活动的抽奖剩余次数
	 * 
	 * @param activityId
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public int getMemberRealLotteryNumber(Long activityId, Long memberId, String cycleConstraint) throws ManagerException;

	/**
	 * 获取用户指定活动的抽奖剩余次数
	 * 
	 * @param activityId
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public int showYiRoadShareFlag(Long activityId, Long memberId) throws ManagerException;

	/**
	 * 抽奖次数-1
	 * 
	 * @param activityId
	 * @param memberId
	 * @return 1 成功, 0 失败
	 * @throws ManagerException
	 */
	public int updateRealCount(Long activityId, Long memberId, String cycleConstraint) throws ManagerException;

	/**
	 * 抽奖
	 * 
	 * @param activityId
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public ActivityLotteryBiz drawLotteryByProbability(Long activityId, Long memberId) throws ManagerException;

	/**
	 * 显示最新中奖结果集
	 * 
	 * @param activityId
	 * @param rowNum
	 * @return
	 */
	public List<ActivityLotteryResult> queryNewLotteryResult(Long activityId, String remark, int rowNum) throws ManagerException;

	/**
	 * 亿路上有你活动增加用户抽奖次数
	 * 
	 * @param memberId
	 * @param transactionId
	 * @param investAmount
	 */
	public void yiRoadAddLotteryNumber(Long memberId, Long transactionId, BigDecimal investAmount);

	/**
	 * 亿路上有你活动增加分享抽奖次数
	 * 
	 * @param memberId
	 * @param transactionId
	 * @param investAmount
	 */
	public void yiRoadAddShareNumber(Long activityId, Long memberId) throws ManagerException;

	/**
	 * 判断是否在活动有效期
	 * 
	 * @param activityCode
	 * @return
	 */
	public ActivityBiz checkValidity(Long activityId) throws ManagerException;

	/**
	 * 直接插入一条抽奖记录
	 * 
	 * @param model
	 * @return
	 * @throws ManagerException
	 */
	public int insertActivityLottery(ActivityLottery model) throws ManagerException;

	/**
	 * 
	 * @Description:根据活动ID和会员ID查询抽奖记录
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2015年12月28日 下午3:11:48
	 */
	public List<ActivityLottery> queryLotteryByMemberAndActivity(Map<String, Object> map) throws ManagerException;

	/**
	 * 
	 * @Description:百万现金券活动
	 * @param memberId
	 * @param transactionId
	 * @param investAmount
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年1月4日 下午4:40:46
	 */
	public void sendMillionCoupon(Long memberId, Long transactionId, BigDecimal investAmount);

	/**
	 * 
	 * @Description:春节领券
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2015年12月31日 上午10:05:36
	 */
	public ResultDO<Object> springFestivalReceiveCoupon(Long memberId) throws ManagerException;

	/**
	 * 
	 * @Description:查询参与次数
	 * @param model
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月6日 上午9:36:55
	 */
	public int countNewLotteryResult(ActivityLotteryResult model) throws ManagerException;

	/**
	 * 
	 * @Description:生成红包连接（来自交易后产生的红包）
	 * @param activityName
	 * @param transactionId
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年1月10日 下午1:37:50
	 */
	public ResultDO<Object> createRedBagUrlFromTransaction(Activity activity, Long transactionId) throws ManagerException;

	/**
	 * 
	 * @Description:校验红包规则
	 * @param activityName
	 * @param t
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年1月13日 上午10:33:31
	 */
	public ResultDO<Object> checkRedBagRule(Activity activity, Long transactionId) throws ManagerException;

	/**
	 * 显示最新中奖结果集和项目名称
	 * 
	 * @param activityId
	 * @param rowNum
	 * @return
	 */
	public List<ActivityLotteryResult> queryNewLotteryResultAndProject(Long activityId, String remark, int rowNum) throws ManagerException;

	/**
	 * 
	 * @Description:红包加密
	 * @param activityName
	 * @param transactionId
	 * @return
	 * @author: wangyanji
	 * @throws ManagerException
	 * @throws UnsupportedEncodingException
	 * @time:2016年1月10日 下午10:57:03
	 */
	public String encryptRedBag(Long activityId, Long transactionId) throws ManagerException;
	
	/**
	 * 
	 * @Description:签到额外奖励
	 * @param member
	 * @author: wangyanji
	 * @time:2016年3月16日 上午11:25:48
	 */
	public void prizeByCheck(Member member);

	/**
	 * 
	 * @Description:自定义查询抽奖表
	 * @param queryMap
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年4月19日 上午11:39:00
	 */
	public ActivityLottery selectByMemberActivity(Map<String, Object> queryMap) throws ManagerException;

	/**
	 * 
	 * @Description:锁行
	 * @param model
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年6月12日 上午9:58:43
	 */
	public ActivityLottery getRecordForLock(Long id) throws ManagerException;
	/**
	 * 庆A轮领取红包
	 * @param 
	 * @return
	 */
	public ResultDO<ActivityForAnniversary> receiveCelebrationA(Long memberId);
	/**
	 * 
	 * @Description:猜奖牌
	 * @param memberId
	 * @param medalType
	 * @return
	 * @author: chaisen
	 * @time:2016年7月12日 上午9:48:20
	 */
	public ResultDO<ActivityForOlympicReturn> guessMedal(Long memberId, int medalType);
	/**
	 * 
	 * @Description:猜金牌
	 * @param memberId
	 * @param popularityValue
	 * @param goldNumber
	 * @return
	 * @author: chaisen
	 * @time:2016年7月12日 上午9:48:39
	 */
	public ResultDO<ActivityForOlympicReturn> guessGold(Long memberId, int popularityValue, int goldNumber);
	/**
	 * 
	 * @Description:竞猜总人数
	 * @param activityId
	 * @param cycleConstraint
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年7月12日 上午11:53:01
	 */
	public int countActivityLotteryByActivityId(Long activityId, String cycleConstraint) throws ManagerException;

	/**
	 * 
	 * @Description:奖牌奇偶数竞猜记录
	 * @param memberId
	 * @param activityId
	 * @return
	 * @author: chaisen
	 * @time:2016年7月12日 下午12:00:11
	 */
	public List<ActivityForOlympicGuess> getGuessMedalRecord(Long memberId, Long activityId);
	/**
	 * 
	 * @Description:金牌竞猜记录
	 * @param memberId
	 * @param activityId
	 * @param olympicDate
	 * @param startTime
	 * @return
	 * @author: chaisen
	 * @time:2016年7月12日 下午2:28:23
	 */
	List<ActivityForOlympicGuess> getGuessGoldRecord(Long memberId, Long activityId, ActivityForOlympicDate olympicDate, Date startTime);
	/**
	 * 
	 * @Description:集奥运
	 * @param memberId
	 * @param puzzle
	 * @return
	 * @author: chaisen
	 * @time:2016年7月12日 下午3:07:17
	 */
	public ResultDO<ActivityForOlympicReturn> setOlympic(Long memberId, String puzzle);
	/**
	 * 
	 * @Description:吉祥物拼图剩余数量
	 * @param activityId
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年7月8日 下午3:43:40
	 */
	public ActivityForOlympic getPuzzleRemind(Long activityId, Long memberId, ActivityForOlympicDate olympicDate,ActivityForOlympic model);
	/**
	 * 
	 * @Description:校验
	 * @param activityLottery
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年7月19日 下午2:46:02
	 */
	public ActivityLottery checkExistLottery(ActivityLottery activityLottery) throws ManagerException;
	/**
	 * 
	 * @Description:更新
	 * @param activityLottery
	 * @return
	 * @author: chaisen
	 * @time:2016年7月19日 下午2:48:59
	 */
	public int updateByActivityAndMember(ActivityLottery activityLottery)throws ManagerException;
	/**
	 * 
	 * @Description:实物大奖列表
	 * @param activityId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年7月19日 下午2:52:25
	 */
	List<ActivityForOlympicGuess> getRealPrize(Long activityId) throws ManagerException;
	/**
	 * 
	 * @Description:TODO
	 * @param activityId
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年7月20日 下午1:49:00
	 */
	Integer getMedalType(Long activityId, Long memberId);
	/**
	 * 
	 * @Description:更新抽奖次数
	 * @param activityLottery
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年7月22日 上午10:40:11
	 */
	public int updateRealCount(ActivityLottery activityLottery) throws ManagerException;
	
	/**
	 * 
	 * @Description:红包剩余数量
	 * @param rb
	 * @param templateId
	 * @return
	 * @author: chaisen
	 * @time:2016年7月2日 上午9:22:35
	 */
	public Integer getRemindCouponNumber(ActivityLottery rb, Long templateId,int groupType);
	/**
	 * 
	 * @Description:统计押注记录
	 * @param activityId
	 * @param string
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年7月4日 下午5:22:20
	 */
	public Integer countBetTotal(Long activityId, String cycleConstraint) throws ManagerException;
	
	/**
	 * 
	 * @Description:加入战队
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年6月30日 下午4:19:04
	 */
	public ResultDO<ActivityForJulyRetrun> julyTeamJoin(Long memberId);
	/**
	 * 
	 * @Description:押注战队
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年6月30日 下午6:07:39
	 */
	public ResultDO<ActivityForJulyRetrun> betJulyTeam(Long memberId,int popularityValue,int groupType);
	/**
	 * 
	 * @Description:七月战队  领取红包
	 * @param memberId
	 * @param couponAmount
	 * @return
	 * @author: chaisen
	 * @time:2016年7月1日 下午2:27:07
	 */
	public ResultDO<ActivityForJulyRetrun> receiveJulyTeamCoupon(Long memberId, int couponAmount);
	/**
	 * 
	 * @Description:活动校验信息
	 * @param queryMap
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年11月2日 下午4:51:20
	 */

	public List<ActivityLottery> selectActivityLotteryByMemberId(Long memberId, String remark) throws ManagerException;
	/**
	 * 
	 * @Description:统计抽奖次数
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年11月7日 下午2:40:35
	 */
	public Integer getQuickLotteryNumber(Long memberId);
	/**
	 * 
	 * @Description:领取红包
	 * @param memberId
	 * @param couponAmount
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年10月17日 下午2:17:48
	 */
	public ResultDO<ActivityForAnniversaryRetrun> receiveCouponAnniversary(Long memberId, int couponAmount,int type) throws ManagerException;
	/**
	 * 
	 * @Description:消耗人气值领取奖品
	 * @param memberId
	 * @param couponAmount
	 * @param chip
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年10月19日 下午9:22:23
	 */
	public ResultDO<ActivityForAnniversaryRetrun> receiveRewardAnniversary(
			Long memberId, int popularValue, int chip) throws ManagerException;
	
	
	/**
	 * 
	 * @Description:签到送现金券
	 * @param memberId
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年12月13日 下午2:07:12
	 */
	public void signSendCoupon(Long memberId) throws ManagerException;
	/**
	 * 
	 * @Description:双旦抢红包
	 * @param memberId
	 * @param type
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年12月6日 上午10:39:52
	 */
	public ResultDO<ActivityForDouble> doubleReceiveCoupon(Long memberId) throws ManagerException;

	/**
	 * 鸡年新年活动抢红包
	 * @param memberId
	 * @return
	 * @throws ManagerException
     */
	public ResultDO<ActivityForNewYear> newYearLuckyMoney(Long memberId,Long templateId) throws ManagerException;

	/**
	 * 鸡年新年活动登录送福袋
	 * @param memberId
	 * @return
	 * @throws ManagerException
     */
	public ResultDO<ActivityForNewYear> newYearLuckyBag(Long memberId) throws ManagerException;

	/**
	 * 除夕抢压岁钱
	 * @param memberId
	 * @return
	 * @throws ManagerException
     */
	public ResultDO<Activity> newYearGrab(Long memberId) throws ManagerException;

	/**
	 * 查询抽奖表活动数据
	 * @param activityId
	 * @return
     */
	public List<ActivityLottery> queryActivityLotteryByActivityId(Long activityId);

	/**
	 * 查询抽奖表活动数据条数
	 * @param activityId
	 * @return
     */
	public int queryCountByActivityId(Long activityId);

	/**
	 * 元宵活动
	 * @return
	 * @throws ManagerException
     */
	public ResultDO<Activity> lanternFestival() throws ManagerException;

	/**
	 * 新品特惠
	 * @return
	 * @throws ManagerException
     */
	public ResultDO<Activity> newPreferential() throws ManagerException;

	/**
	 * 情人节签到送优惠券
	 * @param memberId
	 * @return
	 * @throws ManagerException
     */
	public ResultDO<Activity> valentineDay(Long memberId) throws ManagerException;

	/**
	 * 38节签到送券
	 * @param memberId
	 * @return
	 * @throws ManagerException
     */
	public ResultDO<Activity> womensDaySign(Long memberId) throws ManagerException;

	/**
	 * 38节登录礼包
	 * @param memberId
	 * @return
	 * @throws ManagerException
     */
	public ResultDO<ActivityForWomensDay> womensDayBag(Long memberId) throws ManagerException;

	/**
	 * 38节数据
	 * @param memberid
	 * @return
     */
	public ResultDO<ActivityForWomensDay> womensDayData(Long memberid);


	/**
	 * 领取福袋
	 * @param memberId
	 * @param couponAmount
	 * @return
	 */
	public ResultDO<ActivityForFiveBillionRetrun> receiveLuckBag(Long memberId,
			int couponAmount)throws ManagerException;

	/**福临双全抽奖
	 * @param 
	 * @return
	 */
	public ResultDO<ActivityForFiveBillionRetrun> lotteryLuckBoth(Long memberId,int type)throws ManagerException;

	/**
	 * @param activityLotteryResultQuery
	 * @return
	 * @throws ManagerException
	 */
	Page<ActivityForFiveBillionRetrun> activityLotteryResultListByPage(
			ActivityLotteryResultQuery activityLotteryResultQuery)
			throws ManagerException;

	/**
	 * 订阅号活动
	 * @param memberid
	 * @return
     */
	public ResultDO<Activity> subscription(Long memberid);

	/**
	 * 订阅号数据
	 * @param memberid
	 * @return
     */
	public ResultDO<ActivityForSubscription> subscriptionData(Long memberid);

	public ResultDO<Object> receiveCouponGold(Long memberId, int couponAmount,String way);

	/**
	 * 邀请好友数据
	 * @param memberId
	 * @return
     */
	public ResultDO<ActivityForInviteFriend> inviteFriendData(Long memberId);

	/**
	 * 邀请好友新用户送券
	 * @param memberId
	 * @return
     */
	public ResultDO<ActivityForInviteFriend> inviteFriendNewUser(Long memberId);

	/**
	 * 送花活动领取
	 * @param memberId
	 * @return
     */
	public ResultDO<Activity> receiveFlowers(Long memberId);

	/**
	 * 送花活动初始数据
	 * @param memberId
	 * @return
     */
	public ResultDO<ActivityForSubscription> flowersData(Long memberId);
	
	/**
	 * 60亿活动送优惠券
	 * @param member
	 * @throws Exception
	 */
	public void sixBillionActAfterLogin(Member member) throws Exception;

	public ResultDO<Object> springComingReceiveCoupon(Long memberId, Long templateId);

	public ResultDO<Object> springComingInit(Long memberId);

	/**
	 * 51活动签到
	 * @param memberId
	 * @return
	 */
	public ResultDO<Activity> laborSign(Long memberId);

	/**
	 * 51活动领取专属510礼包
	 * @param memberId
	 * @return
	 */
	public ResultDO<Activity> laborGift(Long memberId);

	/**
	 * 51活动数据
	 * @param memberId
	 * @return
	 */
	public ResultDO<ActivityForLabor> laborInit(Long memberId);
}