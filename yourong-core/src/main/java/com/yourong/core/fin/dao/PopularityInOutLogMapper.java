package com.yourong.core.fin.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.core.fin.model.PopularityInOutLog;
import com.yourong.core.fin.model.query.PopularityInOutLogQuery;
import com.yourong.core.mc.model.biz.ActivityForFirstInvest;
import com.yourong.core.mc.model.biz.ActivityForSingleInvest;
import com.yourong.core.mc.model.biz.ActivityLeadingSheepProject;
import com.yourong.core.mc.model.biz.ActivityLeadingSheepRanks;

@Repository
public interface PopularityInOutLogMapper {

    int insertSelective(PopularityInOutLog popularityInOutLog);

    PopularityInOutLog selectByPrimaryKey(@Param("id") Long id);

	List<PopularityInOutLog> selectPopularityInOutLogForGain(BaseQueryParam query);
	long selectPopularityInOutLogForGainCount(BaseQueryParam query);

	List<PopularityInOutLog> selectPopularityInOutLogForExchange(BaseQueryParam query);
	long selectPopularityInOutLogForExchangeCount(BaseQueryParam query);

	List<ActivityForFirstInvest> selectActivityForFirstInvestList(
			PopularityInOutLogQuery query);

	List<ActivityForSingleInvest> selectActivityForSingleInvest(PopularityInOutLogQuery query);
	
	/**
	 * 人气值排行TOP10
	 * @param topNum
	 * @return
	 */
	List<PopularityInOutLog> findTopPopularityMember(@Param("topNum")int topNum);
	
	List<PopularityInOutLog> findLastExchangeCoupon();
	
	/**
	 * 根据备注和用户id查询用户是否获取过该类型的人气值
	 * @param remark
	 * @param memberId
	 * @return
	 */
	int findPopularityBySourceIdAndRemark(@Param("remark") String remark,@Param("memberId")Long memberId);
	
	/**
	 * 最近一个一羊领头（一鸣惊人、幸运女神、一锤定音）的项目
	 * @param remark
	 * @return
	 */
	ActivityLeadingSheepProject findLastLeadingSheepProject(@Param("remark")String remark);
	
	/**
	 * 获取过一羊领头（一鸣惊人、幸运女神、一锤定音）的总人数
	 */
	Integer findGetLeadingSheepMemeberCount(@Param("remark")String remark);
	
	/**
	 * 获取一羊领头等最多的前3位用户
	 * @param remark
	 * @return
	 */
	List<ActivityLeadingSheepRanks> findLeadingSheepRanks(@Param("remark")String remark);

	/**
	 * 任务赚取人气值（邀请好友、签到） 
	 * @param memberId
	 * @param remark
	 * @return
	 */
	Integer findTaskGetTotalPopByMemberId(@Param("memberId")Long memberId);
	
	/**
	 * 投资赚人气值（投资四重礼）
	 * @param memberId
	 * @param remark
	 * @return
	 */
	Integer findInvestGetTotalPopByMemberId(@Param("memberId")Long memberId,@Param("remark")String remark);
	
	/**
	 * 活动赚人气值（除去四重礼）
	 * @param memberId
	 * @param remark
	 * @return
	 */
	Integer findActivityGetTotalPopByMemberId(@Param("memberId")Long memberId,@Param("remark")String remark);
	
	/**
	 * 获取用户人气值流水
	 * @param query
	 * @return
	 */
	List<PopularityInOutLog> selectPopularityInOutLog(BaseQueryParam query);
	
	long selectPopularityInOutLogCount(BaseQueryParam query);
	
	/**
	 * 获取用户获取四重礼的次数
	 * @param memberId
	 * @param remark
	 * @return
	 */
	Integer getQuadrupleGiftCountByMemberId(@Param("memberId")Long memberId,@Param("remark")String remark);
	
	/**
	 * 获取用户四重礼的赚取人气值总数
	 * @param memberId
	 * @param remark
	 * @return
	 */
	Integer findQuadrupleGiftTotalPopByMemberId(@Param("memberId")Long memberId,@Param("remark")String remark);
	
	/**
	 * @Description:获取五重礼送人气值列表
	 * @author: fuyili
	 * @time:2015年12月11日 下午5:32:30
	 */
	List<ActivityForFirstInvest> selectQuintupleGiftList(PopularityInOutLogQuery query);
	/**
	 * 
	 * @Description:五重礼过滤p2p
	 * @param remark
	 * @return
	 * @author: chaisen
	 * @time:2016年6月8日 下午12:00:59
	 */
	ActivityLeadingSheepProject p2pFindLastLeadingSheepProject(@Param("remark")String remark);
	
	/**
	 * 获取用户当天的人气值总收益
	 * @param memberId
	 * @param remark
	 * @return
	 */
	Integer getMemberPopCountByDate(@Param("map") Map<String, Object> map);
	
	/**
	 * 人气值是否发放过
	 * @param remark
	 * @param sourceId
	 * @return
	 */
	Integer countInvestBySourceId(@Param("remark")String remark,@Param("sourceId")Long sourceId);
	
	
	Integer updatePopularityByMemberId(@Param("remark")String remark,@Param("memberId")Long memberId,@Param("beforeRemark")String beforeRemark,@Param("sourceId")Long sourceId);

	/**
	 * 查询时间之前人气值总收入
	 * @param memberid
	 * @param time
     * @return
     */
	BigDecimal queryPopularityTotalIncome(@Param("memberid")Long memberid, @Param("time")Date time);

	/**
	 * 查询时间之前人气值总支出
	 * @param memberid
	 * @param time
     * @return
     */
	BigDecimal queryPopularityTotalOutlay(@Param("memberid")Long memberid, @Param("time")Date time);
}