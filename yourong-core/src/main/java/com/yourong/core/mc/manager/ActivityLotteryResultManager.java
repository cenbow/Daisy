package com.yourong.core.mc.manager;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.biz.LotteryRewardBiz;
import com.yourong.core.ic.model.biz.ProjectForLevel;
import com.yourong.core.ic.model.biz.ProjectForRewardMember;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.mc.model.ActivityLotteryResultNew;
import com.yourong.core.mc.model.biz.ActivityForFiveBillionRetrun;
import com.yourong.core.mc.model.biz.ActivityForJulyBet;
import com.yourong.core.mc.model.biz.ActivityForMemberInfo;
import com.yourong.core.mc.model.biz.ActivityForOlympic;
import com.yourong.core.mc.model.biz.ActivityGrabResultBiz;
import com.yourong.core.mc.model.biz.ActivityForInviteFriendDetail;


/**
 * 活动抽奖结果manager
 * @author wangyanji
 *
 */
public interface ActivityLotteryResultManager {

	/**
	 * 自定义中奖结果查询
	 * @param activityId
	 * @param memberId
	 * @param inputNum
	 */
	public List<ActivityLotteryResult> getLotteryResultBySelective(ActivityLotteryResult model) throws ManagerException;
	
	/**
	 * 
	 * @Description:按奖励类型汇总结果
	 * @param model
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年6月11日 下午9:38:55
	 */
	public int sumRewrdsByMemberActivity(ActivityLotteryResult model) throws ManagerException;

	/**
	 * 
	 * @Description:根据奖励类型排行
	 * @param model
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年6月11日 下午6:37:07
	 */
	public List<ActivityLotteryResult> topByRewardType(ActivityLotteryResult model) throws ManagerException;

	/**
	 * 
	 * @Description:里程拉新好友助力榜
	 * @param activityId
	 * @param memberId
	 * @param startNo
	 * @return
	 * @author: wangyanji
	 * @time:2016年6月12日 下午4:05:44
	 */
	public List<ActivityLotteryResult> inviteFriendList(Long activityId, Long memberId, int startNo, Date startTime, Date endTime)
			throws ManagerException;

	/**
	 * 
	 * @Description:里程拉新好友助力榜统计
	 * @param activityId
	 * @param memberId
	 * @param startNo
	 * @return
	 * @author: wangyanji
	 * @time:2016年6月12日 下午4:05:44
	 */
	public Integer inviteFriendListCount(Long activityId, Long memberId, Date startTime, Date endTime) throws ManagerException;
	/**
	 * 
	 * @Description:统计人气值
	 * @param activityResult
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年6月24日 上午11:11:39
	 */
	public int sumRewrdsByMemberActivityRewardId(ActivityLotteryResult activityResult)throws ManagerException;
	/**
	 * 
	 * @Description:统计活动之前记录
	 * @param activityResult
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年6月27日 下午6:12:06
	 */
	public int countRewrdsByMemberActivityRewardId(ActivityLotteryResult activityResult)throws ManagerException;
	/**
	 * 
	 * @Description:亮奥运
	 * @param activityId
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年7月11日 下午3:50:10
	 */
	public String getBrightOlympicEveryDay(Long activityId, Long memberId)throws ManagerException;
	/**
	 * 
	 * @Description:TODO
	 * @param model
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年7月8日 上午11:52:05
	 */
	public int countLotteryResultByMemberIdAndRemark(ActivityLotteryResult model) throws ManagerException;

	/**
	 * 
	 * @Description:亮奥运奖励领取情况
	 * @param activity
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年7月14日 下午12:57:03
	 */
	public ActivityForOlympic ifBrightOlympicReceived(Activity activity, Long memberId,ActivityForOlympic model) throws ManagerException;
	/**
	 * 
	 * @Description:猜奥运逻辑处理
	 * @author: chaisen
	 * @throws Exception 
	 * @time:2016年7月14日 下午2:08:05
	 */
	public void guessOlympicSendCoupon() throws Exception;
	/**
	 * 
	 * @Description:保存数据
	 * @param betResult
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年7月19日 下午2:35:17
	 */
	public int insertSelective(ActivityLotteryResult model) throws ManagerException;
	
	/**
	 * 
	 * @Description:统计上轮翻倍人数
	 * @param activityId
	 * @param startTime
	 * @param endTime
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年7月4日 下午5:33:20
	 */
	public Integer countLastBetTotal(Long activityId,String remark) throws ManagerException;
	
	/** 
	 * @Description:获取当前用户押注记录
	 * @param activityResult
	 * @return
	 * @author: chaisen
	 * @time:2016年6月30日 上午10:36:53
	 */
	public List<ActivityForJulyBet> getBetRecordList(ActivityLotteryResult activityResult)throws ManagerException;

	Integer countBetLotteryResult(Long activityId, Long memberId)
			throws ManagerException;
	/**
	 * 
	 * @Description:获取各等奖对应的个数
	 * @param modelResult
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年11月4日 下午1:43:33
	 */
	public List<ProjectForLevel> getRewardLevelByProjectId(ActivityLotteryResult modelResult) throws ManagerException;

	public BigDecimal sumRewardInfoByProjectId(ActivityLotteryResult modelResult,int rewardType) throws ManagerException;
	/**
	 * 
	 * @Description:是否领取过
	 * @param activityId
	 * @param remark
	 * @return
	 * @author: chaisen
	 * @time:2016年10月18日 下午6:08:36
	 */
	public boolean isReceived(Long activityId,Long memberId, String remark);
	
	/**
	 * 
	 * @Description:获取各等奖对应的个数
	 * @param modelResult
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年11月4日 下午1:43:33
	 */
	public List<ProjectForRewardMember> findProjectRewardByProjectId(ActivityLotteryResult modelResult) throws ManagerException;

	public List<ActivityLotteryResult> getActivityLotteryResultByProject(ActivityLotteryResult recrod) throws ManagerException;
	
	
	public List<ActivityLotteryResult> sumRewardInfoByMemberId(ActivityLotteryResult lotteryResult) throws ManagerException;
	
	/**
	 * 
	 * @Description:查询快投奖励最高的金额
	 * @param remark
	 * @return
	 * @author: wangyanji
	 * @time:2017年1月10日
	 */
	public ActivityLotteryResult getMaxRewardForQuickProject(String remark) throws ManagerException;

	/**
	 * 获取指定快投项目中奖名单
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 */
	public List<LotteryRewardBiz> getProjectLotteryReward(Long projectId)
			throws ManagerException;

	/**
	 * 获取快投中奖 历史名单
	 * @return
	 * @throws ManagerException 
	 */
	public List<LotteryRewardBiz> getLotteryReward(int rowNum) throws ManagerException;

	

	public List<ActivityLotteryResult> getLotteryResultBySelectiveAndLotteryStatus(ActivityLotteryResult model)throws ManagerException;

	/**
	 * 查询活动中奖纪录
	 * @param activityId
	 * @return
     */
	public List<ActivityGrabResultBiz> queryActivityGrabResult(Long activityId) throws ManagerException;

	/**
	 * 查询活动用户是否已经中奖
	 * @param activityId
	 * @param memberId
     * @return
     */
	public boolean queryLotteryResultByActivityAndMemberId(Long activityId,Long memberId);
	
	/**
	 * 查询用户可用抽奖次数
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	int queryAvailableLotteryNum(Long memberId) throws ManagerException;
	
	/**
	 * 查询快投奖励金额
	 * @param model
	 * @return
	 * @throws ManagerException 
	 */
	public ActivityLotteryResult sumRewardInfoResultByProjectId(
			ActivityLotteryResult model) throws ManagerException;

	/** 查询中奖结果
	 * @param id
	 * @param memberId
	 * @param string
	 * @return
	 * @throws ManagerException 
	 */
	public ActivityLotteryResult queryLotteryResultByRemark(Long activityId,
			Long memberId, String string) throws ManagerException;

	/**
	 * @param memberId
	 * @param activityId
	 * @param param
	 * @return
	 * @throws ManagerException
	 */
	List<ActivityForFiveBillionRetrun> getMyLotteryRecord(Long memberId,
			Long activityId, String param) throws ManagerException;

	/**
	 * @param activityId
	 * @param param
	 * @return
	 * @throws ManagerException
	 */
	List<ActivityForMemberInfo> getLotteryRecord(Long activityId, String param)
			throws ManagerException;

	public List<ActivityForMemberInfo>  getFirstTotalInvestAmount(ActivityLotteryResultNew totalNew);

	/**
	 * 查询用户好友邀请详情
	 * @param memberId
	 * @return
     */
	Page<ActivityForInviteFriendDetail> queryInviteFriendDetail(Long memberId,Integer pageNo);
}