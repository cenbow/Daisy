package com.yourong.core.fin.manager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.PopularityInOutLog;
import com.yourong.core.fin.model.biz.OverduePopularityBiz;
import com.yourong.core.fin.model.biz.PopularityMemberBiz;
import com.yourong.core.fin.model.biz.QuadrupleGiftCount;
import com.yourong.core.fin.model.query.PopularityInOutLogQuery;
import com.yourong.core.mc.model.biz.ActivityForFirstInvest;
import com.yourong.core.mc.model.biz.ActivityForSingleInvest;
import com.yourong.core.mc.model.biz.ActivityLeadingSheepRanksAndCount;

public interface PopularityInOutLogManager {

	/**
	 * 插入人气值流水
	 * 
	 * @param record
	 * @return
	 * @throws ManagerException
	 */
	public int insertSelective(PopularityInOutLog record)
			throws ManagerException;

	/**
	 * 获取人气值流水对象
	 * 
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	public PopularityInOutLog getPopularityInOutLogById(Long id)
			throws ManagerException;

	/**
	 * 插入充值流水
	 * 
	 * @param referralId
	 * @param type
	 * @param income
	 * @param outlay
	 * @param availableBalance
	 * @param sourceId
	 * @param remarks
	 */
	public int insert(Long referralId, TypeEnum type, BigDecimal income,
			BigDecimal outlay, BigDecimal availableBalance, Long sourceId,
			String remarks) throws ManagerException;

	/**
	 * 查询获取记录
	 * 
	 * @param query
	 * @return
	 * @throws ManagerException
	 */
	public Page<PopularityInOutLog> selectPopularityInOutLogForGain(
			BaseQueryParam query) throws ManagerException;

	/**
	 * 查询兑换记录
	 * 
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public Page<PopularityInOutLog> selectPopularityInOutLogForExchange(
			BaseQueryParam query) throws ManagerException;

	/**
	 * 查询一马当先等活动人气值列表
	 * 
	 * @param query
	 * @return
	 * @throws ManagerException
	 */
	public List<ActivityForFirstInvest> selectActivityForFirstInvestList(
			PopularityInOutLogQuery query) throws ManagerException;

	/**
	 * 单笔投资活动列表
	 * 
	 * @param query
	 * @return
	 * @throws ManagerException
	 */
	public List<ActivityForSingleInvest> selectActivityForSingleInvest(
			PopularityInOutLogQuery query) throws ManagerException;

	/**
	 * 人气值排行TOP10
	 * 
	 * @param topNum
	 * @return
	 */
	List<PopularityInOutLog> findTopPopularityMember(int topNum)
			throws ManagerException;

	/**
	 * 最近兑换现金券记录
	 * 
	 * @return
	 * @throws ManagerException
	 */
	List<PopularityInOutLog> findLastExchangeCoupon() throws ManagerException;

	/**
	 * 查询该用户某一类型的人气值是否赠送过
	 */
	int findPopularityByMemberIdAndRemark(String remark, Long memberId)
			throws ManagerException;

	/**
	 * 查询一羊领头等的总共获取的人数及获取最多的前3位用户
	 */
	List<ActivityLeadingSheepRanksAndCount> getActivityLeadingSheepRanksAndCount()
			throws ManagerException;

	/**
	 * 扣人气值
	 */
	boolean reducePopularity(Long senderId, Long memberId, TypeEnum type,
			BigDecimal outLay, String remarks) throws ManagerException;

	/**
	 * 兑换扣减的人气值（兑换现金券、兑换收益券、兑换流量等）
	 */
	boolean rechargeReducePopularity(Long senderId, Long memberId,
			TypeEnum type, BigDecimal outLay, String remarks)
			throws ManagerException;

	/**
	 * 获取用户分别通过任务、投资、活动赚取的人气值
	 * 
	 * @param memberId
	 * @return
	 */
	PopularityMemberBiz findTotalPopByMemberId(Long memberId)
			throws ManagerException;

	/**
	 * 获取用户人气值流水
	 * 
	 * @param query
	 * @return
	 * @throws ManagerException
	 */
	Page<PopularityInOutLog> selectPopularityInOutLog(BaseQueryParam query)
			throws ManagerException;

	/**
	 * 获取用户获取五重礼的次数
	 * 
	 * @param memberId
	 * @return
	 */
	QuadrupleGiftCount getQuadrupleGiftCountByMemberId(Long memberId)
			throws ManagerException;

	/**
	 * 扣除提现手续费用
	 * 
	 * @param memberId
	 * @param sourceId
	 * @return
	 * @throws ManagerException
	 */
	public boolean reduceWithdrawalsFee(Long memberId, Long sourceId,
			Integer withdrawFees) throws ManagerException;

	/**
	 * @Description:平台五重礼每一种获取的总次数和人气值总数
	 * @return
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2015年12月11日 下午4:54:51
	 */
	QuadrupleGiftCount getQuintupleGiftCount() throws ManagerException;
	
	
	/**
	 * @Description:获取五重礼送人气值用户列表
	 * @param query
	 * @return
	 * @author: fuyili
	 * @time:2015年12月11日 下午5:34:04
	 */
	List<ActivityForFirstInvest> selectQuintupleGiftList(PopularityInOutLogQuery query)throws ManagerException;
	
	/**
	 * @Description:获取用户当天的人气值总收益
	 * @param map
	 * @return
	 * @author: zhangaho
	 * @time:2016年5月20日 上午11:00:04
	 */
	public Integer getMemberPopCountByDate(Map<String, Object> map) throws ManagerException ;
	/**
	 * 
	 * @param projectId 统计五重礼人气值是否发放过
	 * @param transactionAddPopularityBalanceByLastInvest
	 * @return
	 * @throws ManagerException
	 */
	public int countInvestBySourceId(Long projectId)throws ManagerException ;

	/**
	 * 查询用户过期人气值
	 * @param memberid
     * @return
     */
	public OverduePopularityBiz queryOverduePopularity(Long memberid) throws ManagerException ;
}
