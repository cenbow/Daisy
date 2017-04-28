package com.yourong.core.fin.manager.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.yourong.common.util.DateUtils;
import com.yourong.core.fin.model.biz.OverduePopularityBiz;
import com.yourong.core.sys.dao.SysDictMapper;
import com.yourong.core.sys.model.SysDict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rop.thirdparty.com.google.common.collect.Lists;

import com.yourong.common.annotation.RedisCacheAnnotation;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.StringUtil;
import com.yourong.core.fin.dao.PopularityInOutLogMapper;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.PopularityInOutLog;
import com.yourong.core.fin.model.biz.PopularityMemberBiz;
import com.yourong.core.fin.model.biz.QuadrupleGiftCount;
import com.yourong.core.fin.model.query.PopularityInOutLogQuery;
import com.yourong.core.mc.model.biz.ActivityForFirstInvest;
import com.yourong.core.mc.model.biz.ActivityForSingleInvest;
import com.yourong.core.mc.model.biz.ActivityLeadingSheepRanks;
import com.yourong.core.mc.model.biz.ActivityLeadingSheepRanksAndCount;
import com.yourong.core.tc.dao.TransactionMapper;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.uc.manager.MemberManager;

@Component
public class PopularityInOutLogManagerImpl implements PopularityInOutLogManager {

	@Autowired
	private PopularityInOutLogMapper popularityInOutLogMapper;

	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private TransactionMapper transactionMapper;

	@Autowired
	private MemberManager memberManager;

	@Autowired
	private SysDictMapper sysDictMapper;

	@Override
	public int insertSelective(PopularityInOutLog popularityInOutLog) throws ManagerException {
		try {
			return popularityInOutLogMapper.insertSelective(popularityInOutLog);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public PopularityInOutLog getPopularityInOutLogById(Long id) throws ManagerException {
		try {
			return popularityInOutLogMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insert(Long referralId, TypeEnum type, BigDecimal income, BigDecimal outlay, BigDecimal availableBalance, Long sourceId,
			String remarks) throws ManagerException {
		try {
			PopularityInOutLog popularityInOutLog = new PopularityInOutLog();
			popularityInOutLog.setBalance(availableBalance);
			popularityInOutLog.setIncome(income);
			popularityInOutLog.setMemberId(referralId);
			popularityInOutLog.setOutlay(outlay);
			popularityInOutLog.setRemark(remarks);
			popularityInOutLog.setSourceId(sourceId);
			popularityInOutLog.setType(type.getType());
			return this.insertSelective(popularityInOutLog);
		} catch (Exception e) {
			throw new ManagerException(e);
		}

	}

	@Override
	public Page<PopularityInOutLog> selectPopularityInOutLogForGain(BaseQueryParam query) throws ManagerException {
		try {
			List<PopularityInOutLog> popularityInOutLogs = popularityInOutLogMapper.selectPopularityInOutLogForGain(query);
			long count = popularityInOutLogMapper.selectPopularityInOutLogForGainCount(query);
			Page<PopularityInOutLog> page = new Page<PopularityInOutLog>();
			page.setData(popularityInOutLogs);
			page.setiDisplayLength(query.getPageSize());
			page.setPageNo(query.getCurrentPage());
			page.setiTotalDisplayRecords(count);
			page.setiTotalRecords(count);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<PopularityInOutLog> selectPopularityInOutLogForExchange(BaseQueryParam query) throws ManagerException {
		try {
			List<PopularityInOutLog> popularityInOutLogs = popularityInOutLogMapper.selectPopularityInOutLogForExchange(query);
			long count = popularityInOutLogMapper.selectPopularityInOutLogForExchangeCount(query);
			Page<PopularityInOutLog> page = new Page<PopularityInOutLog>();
			page.setData(popularityInOutLogs);
			page.setiDisplayLength(query.getPageSize());
			page.setPageNo(query.getCurrentPage());
			page.setiTotalDisplayRecords(count);
			page.setiTotalRecords(count);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ActivityForFirstInvest> selectActivityForFirstInvestList(PopularityInOutLogQuery query) throws ManagerException {
		try {
			return popularityInOutLogMapper.selectActivityForFirstInvestList(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ActivityForSingleInvest> selectActivityForSingleInvest(PopularityInOutLogQuery query) throws ManagerException {
		try {
			return popularityInOutLogMapper.selectActivityForSingleInvest(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 获取人气值top10的用户 存放到缓存
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PopularityInOutLog> findTopPopularityMember(int topNum) throws ManagerException {
		try {
			String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR
					+ RedisConstant.REDIS_KEY_ACTIVITY_POPULARITY_TOP10;
			List<PopularityInOutLog> list = Lists.newArrayList();
			boolean isExit = RedisManager.isExitByObjectKey(key);
			if (isExit) {
				list = (List<PopularityInOutLog>) RedisManager.getObject(key);
			} else {
				list = popularityInOutLogMapper.findTopPopularityMember(topNum);
				RedisManager.putObject(key, list);
				RedisManager.expireObject(key, 604800);
			}
			return list;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 获取最近人气值兑换的用户 存放到缓存
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PopularityInOutLog> findLastExchangeCoupon() throws ManagerException {
		try {
			String key = RedisConstant.REDIS_KEY_ACTIVITY + RedisConstant.REDIS_SEPERATOR
					+ RedisConstant.REDIS_KEY_ACTIVITY_POPULARITY_EXCHANGE;
			List<PopularityInOutLog> list = Lists.newArrayList();
			boolean isExit = RedisManager.isExitByObjectKey(key);
			if (isExit) {
				list = (List<PopularityInOutLog>) RedisManager.getObject(key);
			} else {
				list = popularityInOutLogMapper.findLastExchangeCoupon();
				RedisManager.putObject(key, list);
				RedisManager.expireObject(key, 604800);
			}
			return list;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int findPopularityByMemberIdAndRemark(String remark, Long memberId) throws ManagerException {
		try {
			return popularityInOutLogMapper.findPopularityBySourceIdAndRemark(remark, memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<ActivityLeadingSheepRanksAndCount> getActivityLeadingSheepRanksAndCount() throws ManagerException {
		List<ActivityLeadingSheepRanksAndCount> rList = Lists.newArrayList();
		String[] remarks = { RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST.getRemarks(),
				RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getRemarks(),
				RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getRemarks(),
				RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST.getRemarks(),
				RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST.getRemarks() };
		for (String remark : remarks) {
			ActivityLeadingSheepRanksAndCount ranksAndCount = new ActivityLeadingSheepRanksAndCount();
			Integer totalMembers = popularityInOutLogMapper.findGetLeadingSheepMemeberCount(remark);
			if (totalMembers != null) {
				ranksAndCount.setTotalMembers(totalMembers);
			}
			List<ActivityLeadingSheepRanks> ranks = popularityInOutLogMapper.findLeadingSheepRanks(remark);
			if (Collections3.isNotEmpty(ranks)) {
				ranksAndCount.setRanks(ranks);
			}
			rList.add(ranksAndCount);
		}
		return rList;
	}

	/**
	 * 扣人气值
	 */
	@Override
	public boolean reducePopularity(Long senderId, Long memberId, TypeEnum type, BigDecimal outLay, String remarks) throws ManagerException {
		boolean result = false;
		try {
			Balance balance = balanceManager.reducePopularityBalance(outLay, memberId);
			int flag = this.insert(memberId, type, null, outLay, balance.getBalance(), senderId, remarks);
			if (flag > 0) {
				result = true;
			}
		} catch (ManagerException e) {
			throw new ManagerException(e);
		}
		return result;
	}

	/**
	 * 兑换扣人气值
	 */
	@Override
	public boolean rechargeReducePopularity(Long senderId, Long memberId, TypeEnum type, BigDecimal outLay, String remarks)
			throws ManagerException {
		boolean result = false;
		try {
			result = this.reducePopularity(senderId, memberId, type, outLay, remarks);
			if (result) {
				// 增加平台兑换人气值总额
				balanceManager.incrExchangePlatformTotalPoint(outLay);
			}
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 获取用户分别通过任务、投资、活动赚取的人气值
	 */
	@Override
	public PopularityMemberBiz findTotalPopByMemberId(Long memberId) throws ManagerException {
		try {
			String remark = "(\'" + RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST.getRemarks() + "\','"
					+ RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getRemarks() + "\',\'"
					+ RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST.getRemarks() + "\',\'"
					+ RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getRemarks() + "\')";
			PopularityMemberBiz biz = new PopularityMemberBiz();
			int investTotalPop = popularityInOutLogMapper.findInvestGetTotalPopByMemberId(memberId, remark);
			int taskTotalPop = popularityInOutLogMapper.findTaskGetTotalPopByMemberId(memberId);
			int activityTotalPop = popularityInOutLogMapper.findActivityGetTotalPopByMemberId(memberId, remark);
			biz.setInvestTotalPop(investTotalPop);
			biz.setTastTotalPop(taskTotalPop);
			biz.setActivityTotalPop(activityTotalPop);
			return biz;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 用户人气值流水
	 * 
	 * @param query
	 * @return
	 * @throws ManagerException
	 */

	@Override
	public Page<PopularityInOutLog> selectPopularityInOutLog(BaseQueryParam query) throws ManagerException {
		try {
			List<PopularityInOutLog> popularityInOutLogs = popularityInOutLogMapper.selectPopularityInOutLog(query);
			long count = popularityInOutLogMapper.selectPopularityInOutLogCount(query);
			Page<PopularityInOutLog> page = new Page<PopularityInOutLog>();
			page.setData(popularityInOutLogs);
			page.setiDisplayLength(query.getPageSize());
			page.setPageNo(query.getCurrentPage());
			page.setiTotalDisplayRecords(count);
			page.setiTotalRecords(count);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 获取用户获取五重礼的次数
	 */
	@Override
	public QuadrupleGiftCount getQuadrupleGiftCountByMemberId(Long memberId) throws ManagerException {
		try {
			QuadrupleGiftCount count = new QuadrupleGiftCount();
			int firstInvestCount = popularityInOutLogMapper.getQuadrupleGiftCountByMemberId(memberId,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST.getRemarks());
			int firstInvestTotal = popularityInOutLogMapper.findQuadrupleGiftTotalPopByMemberId(memberId,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST.getRemarks());
			int mostInvestCount = popularityInOutLogMapper.getQuadrupleGiftCountByMemberId(memberId,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getRemarks());
			int mostInvestTotal = popularityInOutLogMapper.findQuadrupleGiftTotalPopByMemberId(memberId,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getRemarks());
			int luckInvestCount = popularityInOutLogMapper.getQuadrupleGiftCountByMemberId(memberId,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST.getRemarks());
			int luckInvestTotal = popularityInOutLogMapper.findQuadrupleGiftTotalPopByMemberId(memberId,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST.getRemarks());
			int lastInvestCount = popularityInOutLogMapper.getQuadrupleGiftCountByMemberId(memberId,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getRemarks());
			int lastInvestTotal = popularityInOutLogMapper.findQuadrupleGiftTotalPopByMemberId(memberId,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getRemarks());
			int mostAndLastInvestCount = popularityInOutLogMapper.getQuadrupleGiftCountByMemberId(memberId,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST.getRemarks());
			int mostAndLastInvestTotal = popularityInOutLogMapper.findQuadrupleGiftTotalPopByMemberId(memberId,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST.getRemarks());
			count.setFirstInvestCount(firstInvestCount);
			count.setFirstInvestTotal(firstInvestTotal);
			count.setMostInvestCount(mostInvestCount);
			count.setMostInvestTotal(mostInvestTotal);
			count.setLuckInvestCount(luckInvestCount);
			count.setLuckInvestTotal(luckInvestTotal);
			count.setLastInvestCount(lastInvestCount);
			count.setLastInvestTotal(lastInvestTotal);
			count.setMostAndLastInvestCount(mostAndLastInvestCount);
			count.setMostAndLastInvestTotal(mostAndLastInvestTotal);
			return count;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public boolean reduceWithdrawalsFee(Long memberId, Long sourceId, Integer withdrawFee) throws ManagerException {
		boolean result = false;
		try {
			Balance balance = balanceManager.reduceWithdrawalsFee(memberId, sourceId, withdrawFee);
			if (balance != null) {
				result = true;
			}
		} catch (ManagerException e) {
			throw new ManagerException(e);
		}
		return result;
	}

	/**
	 * 统计平台用户获取五重礼的总次数
	 */
	@Override
	@RedisCacheAnnotation(key = RedisConstant.REDIS_KEY_LANDING + RedisConstant.REDIS_SEPERATOR
			+ RedisConstant.REDIS_KEY_LANDING_QUINTUPLEGIFT_COUNT, expire = 7 * 24 * 60 * 60)
	public QuadrupleGiftCount getQuintupleGiftCount() throws ManagerException {
		try {
			QuadrupleGiftCount count = new QuadrupleGiftCount();
			int firstInvestCount = popularityInOutLogMapper.getQuadrupleGiftCountByMemberId(-1L,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST.getRemarks());
			int firstInvestTotal = popularityInOutLogMapper.findQuadrupleGiftTotalPopByMemberId(-1L,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST.getRemarks());
			int mostInvestCount = popularityInOutLogMapper.getQuadrupleGiftCountByMemberId(-1L,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getRemarks());
			int mostInvestTotal = popularityInOutLogMapper.findQuadrupleGiftTotalPopByMemberId(-1L,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getRemarks());
			int luckInvestCount = popularityInOutLogMapper.getQuadrupleGiftCountByMemberId(-1L,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST.getRemarks());
			int luckInvestTotal = popularityInOutLogMapper.findQuadrupleGiftTotalPopByMemberId(-1L,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST.getRemarks());
			int lastInvestCount = popularityInOutLogMapper.getQuadrupleGiftCountByMemberId(-1L,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getRemarks());
			int lastInvestTotal = popularityInOutLogMapper.findQuadrupleGiftTotalPopByMemberId(-1L,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getRemarks());
			int mostAndLastInvestCount = popularityInOutLogMapper.getQuadrupleGiftCountByMemberId(-1L,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST.getRemarks());
			int mostAndLastInvestTotal = popularityInOutLogMapper.findQuadrupleGiftTotalPopByMemberId(-1L,
					RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST.getRemarks());
			count.setFirstInvestCount(firstInvestCount);
			count.setFirstInvestTotal(firstInvestTotal);
			count.setMostInvestCount(mostInvestCount);
			count.setMostInvestTotal(mostInvestTotal);
			count.setLuckInvestCount(luckInvestCount);
			count.setLuckInvestTotal(luckInvestTotal);
			count.setLastInvestCount(lastInvestCount);
			count.setLastInvestTotal(lastInvestTotal);
			count.setMostAndLastInvestCount(mostAndLastInvestCount);
			count.setMostAndLastInvestTotal(mostAndLastInvestTotal);
			return count;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * @desc 获取五重礼送人气值用户列表
	 * @param query
	 * @return
	 * @author fuyili
	 * @time 2015年12月14日 下午1:10:17
	 */
	@Override
	@RedisCacheAnnotation(key = RedisConstant.REDIS_KEY_LANDING + RedisConstant.REDIS_SEPERATOR
			+ RedisConstant.REDIS_KEY_LANDING_QUINTUPLEGIFT_GAIN_LIST, expire = 1 * 24 * 60 * 60)
	public List<ActivityForFirstInvest> selectQuintupleGiftList(PopularityInOutLogQuery query) throws ManagerException {
		List<ActivityForFirstInvest> investList = popularityInOutLogMapper.selectQuintupleGiftList(query);
		for (ActivityForFirstInvest invest : investList) {
			if (RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST.getRemarks().equals(invest.getRemark())) {
				// 一羊领头
				Transaction transaction = transactionMapper.selectFirstTransactionByProject(invest.getProjectId());
				if (transaction != null) {
					invest.setTransactionTime(transaction.getTransactionTime());
				}
			}
			if (RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getRemarks().equals(invest.getRemark())) {
				Transaction transaction = transactionMapper.selectMostTransactionByProject(invest.getProjectId());
				if (transaction != null) {
					invest.setInvestAmount(transaction.getInvestAmount());
				}
			}
			if (RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getRemarks().equals(invest.getRemark())) {
				Transaction transaction = transactionMapper.selectLastTransactionByProject(invest.getProjectId());
				if (transaction != null) {
					invest.setInvestAmount(transaction.getInvestAmount());// 一锤定音的投资额
				}
			}
			if (RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST.getRemarks().equals(invest.getRemark())) {
				Transaction transaction = transactionMapper.selectLastTransactionByProject(invest.getProjectId());
				if (transaction != null) {
					invest.setInvestAmount(transaction.getInvestAmount());// 一掷千金的投资额
				}
			}
			if (RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST.getRemarks().equals(invest.getRemark())) {
				Integer investCount = transactionMapper.getTransactionMemberCountByProject(invest.getProjectId());
				if (investCount != null) {
					invest.setInvestCount(investCount);// 项目总的投资笔数
				}
			}
			if (StringUtil.isNotBlank(memberManager.getMemberAvatar(invest.getMemberId()))) {
				invest.setAvatars(memberManager.getMemberAvatar(invest.getMemberId()));
			}
		}
		return investList;
	}
	
	@Override
	public Integer getMemberPopCountByDate(Map<String, Object> map) throws ManagerException {
		try {
			return popularityInOutLogMapper.getMemberPopCountByDate(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int countInvestBySourceId(Long projectId)
			throws ManagerException {
		try {
			String remark = "(\'" + RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST.getRemarks() + "\','"
					+ RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST.getRemarks() + "\',\'"
					+ RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST.getRemarks() + "\',\'"
					+ RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST.getRemarks() + "\')";
			return popularityInOutLogMapper.countInvestBySourceId(remark,projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public OverduePopularityBiz queryOverduePopularity(Long memberid) throws ManagerException {
		OverduePopularityBiz overduePopularityBiz=new OverduePopularityBiz();
		if (memberid==null){
			return overduePopularityBiz;
		}
		try {
			SysDict clearDict=new SysDict();
			Map clearmap = Maps.newHashMap();
			clearmap.put("groupName","popularity_key");
			clearmap.put("key","clear_key");
			clearDict = sysDictMapper.findByGroupNameAndKey(clearmap);
			if (clearDict==null){
				return null;
			}
			BigDecimal income=null;
			BigDecimal outlay=null;
			String[] str= clearDict.getValue().split(",");
			String[] t= str[0].split("_");
			overduePopularityBiz.setShow(false);
			overduePopularityBiz.setShowRule(false);
			if (DateUtils.getYear(new Date())==Integer.parseInt(t[0])&&DateUtils.getMonth(new Date())==Integer.parseInt(t[1])){
				overduePopularityBiz.setShow(true);
				overduePopularityBiz.setIncomeTime(DateUtils.getDateFromString("2015-12-31 23:59:59","yyyy-MM-dd HH:mm:ss"));
				overduePopularityBiz.setOutlayTime(DateUtils.getDateFromString(""+t[0]+"-"+t[1]+"-"+t[2]+" 23:59:59","yyyy-MM-dd HH:mm:ss"));
				income=popularityInOutLogMapper.queryPopularityTotalIncome(memberid, DateUtils.getDateFromString("2015-12-31 23:59:59","yyyy-MM-dd HH:mm:ss"));
				outlay=popularityInOutLogMapper.queryPopularityTotalOutlay(memberid,DateUtils.getDateFromString(""+t[0]+"-"+t[1]+"-"+t[2]+" 23:59:59","yyyy-MM-dd HH:mm:ss"));
			}
			String[] t2= str[1].split("_");
			if (DateUtils.getMonth(new Date())==Integer.parseInt(t2[0])){
				overduePopularityBiz.setShowRule(true);
				overduePopularityBiz.setShow(true);
				int ly= DateUtils.getYear(new Date())-1;
				overduePopularityBiz.setIncomeTime(DateUtils.getDateFromString(""+ly+"-"+t2[0]+"-"+t2[1]+" 23:59:59","yyyy-MM-dd HH:mm:ss"));
				overduePopularityBiz.setOutlayTime(DateUtils.getDateFromString(""+DateUtils.getYear(new Date())+"-"+t2[0]+"-"+t2[1]+" 23:59:59","yyyy-MM-dd HH:mm:ss"));
				income=popularityInOutLogMapper.queryPopularityTotalIncome(memberid, DateUtils.getDateFromString(""+ly+"-12-31 23:59:59","yyyy-MM-dd HH:mm:ss"));
				outlay=popularityInOutLogMapper.queryPopularityTotalOutlay(memberid,
						DateUtils.getDateFromString(""+DateUtils.getYear(new Date())+"-"+t2[0]+"-"+t2[1]+" 23:59:59","yyyy-MM-dd HH:mm:ss"));
			}
			BigDecimal result= null;
			if (income==null){
				income=new BigDecimal(0);
			}
			if (outlay==null){
				outlay=new BigDecimal(0);
			}
			try {
				result = income.subtract(outlay);
			} catch (Exception e) {
				result=new BigDecimal(0);
			}
			if (result.compareTo(BigDecimal.ZERO)<0){
				overduePopularityBiz.setOverduePopularity(0);
			}else {
				overduePopularityBiz.setOverduePopularity(result.intValue());
			}
			return overduePopularityBiz;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
}
