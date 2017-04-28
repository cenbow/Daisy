package com.yourong.core.mc.manager;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityData;
import com.yourong.core.mc.model.ActivityGroup;
import com.yourong.core.mc.model.ActivityMessage;
import com.yourong.core.mc.model.biz.ActivityForJulyBet;
import com.yourong.core.mc.model.biz.ActivityForJulyHistory;
import com.yourong.core.tc.model.biz.TransactionForFirstInvestAct;

public interface ActivityGroupManager {

	/**
	 * 
	 * @Description:插入活动留言
	 * @param activityMessage
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2015年12月30日 下午1:30:11
	 */
	public Integer insert(String actvityName, Long memberId, Long messageTemplateId) throws ManagerException;

	/**
	 * 
	 * @Description:根据活动取最新的留言记录
	 * @param activityId
	 * @param rowNum
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2015年12月30日 下午1:31:50
	 */
	public List<ActivityMessage> selectRankByActivityId(Long activityId, int rowNum) throws ManagerException;

	/**
	 * 
	 * @Description:判断用户是否加入过战队
	 * @param activityId
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年6月30日 上午9:13:27
	 */
	public boolean checkGroupByActivityIdAndMemberId(Long activityId, Long memberId) throws ManagerException;
	/**
	 * 
	 * @Description:根据活动id和用户id获取分组信息
	 * @param activityId
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年6月30日 上午11:27:07
	 */
	public ActivityGroup getCurrentMemberGroupBy(Long activityId, Long memberId)throws ManagerException;
	/**
	 * 
	 * @Description:当前用户所属战队
	 * @param activityId
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年6月30日 上午11:35:44
	 */
	public int getGroupTypeByMemberId(Long activityId, Long memberId)throws ManagerException;
	/**
	 * 
	 * @Description:获取双方当天投资总额
	 * @param activity
	 * @param groupType
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年6月30日 下午1:50:17
	 */
	public BigDecimal getTotalAmountByMemberIdAndActivityGroupType(Activity activity, int groupType)throws ManagerException;
	/**
	 * 
	 * @Description:用户所属战队投资排行前十
	 * @param groupType
	 * @param id
	 * @param specialTime
	 * @param specialTime2
	 * @return
	 * @author: chaisen
	 * @time:2016年7月2日 上午10:44:57
	 */
	public List<TransactionForFirstInvestAct> selectTopTenInvestByGroupType(int groupType, Long id, Date specialTime, Date specialTime2);
	/**
	 * 
	 * @Description:所在战队红包剩余数量
	 * @param activity
	 * @param groupType
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年7月2日 上午11:33:06
	 */
	public ActivityForJulyBet getRemindCoupon(Activity activity, int groupType)throws ManagerException;
	/**
	 * 
	 * @Description:统计战队投资总额奖励人气值
	 * @author: chaisen
	 * @throws Exception 
	 * @time:2016年7月4日 上午10:34:27
	 */
	public void totalTeamInvestAmount()throws Exception;
	/**
	 * 
	 * @Description:获取pk历史记录
	 * @return
	 * @author: chaisen
	 * @time:2016年7月4日 下午6:45:39
	 */
	public List<ActivityForJulyHistory> getPKforJulyHistory(Long activityId);
	/**
	 * 
	 * @Description:获取红包初始化数量
	 * @param activity
	 * @return
	 * @author: chaisen
	 * @time:2016年7月5日 上午11:43:58
	 */
	public ActivityForJulyBet initRemindCoupon(Activity activity);
	/**
	 * 
	 * @Description:获取
	 * @param activityId
	 * @param groupType
	 * @return
	 * @author: chaisen
	 * @time:2016年7月8日 下午5:07:55
	 */
	public BigDecimal getTotalAmountByActivityId(Long activityId, int groupType);
	/**
	 * 
	 * @Description:获取胜利战队
	 * @param activityId
	 * @return
	 * @author: chaisen
	 * @time:2016年7月8日 下午5:39:02
	 */
	public int getSuccessTeam(Long activityId);

	public ActivityData selectActivityDateByActivityId(Long activityId, String dateStrFromDate) throws ManagerException;
	


}