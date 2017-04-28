package com.yourong.web.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.model.query.PopularityInOutLogQuery;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.mc.model.biz.ActivityForFirstInvest;
import com.yourong.core.mc.model.biz.ActivityLeadingSheep;
import com.yourong.core.mc.model.biz.ActivityLeadingSheepProject;
import com.yourong.core.mc.model.biz.ActivityLeadingSheepRanks;
import com.yourong.core.mc.model.biz.ActivityLeadingSheepRanksAndCount;
import com.yourong.core.tc.dao.TransactionMapper;
import com.yourong.core.tc.model.Transaction;
import com.yourong.web.service.ActivityLeadingSheepService;
import com.yourong.web.utils.ServletUtil;
import com.yourong.web.utils.SysServiceUtils;

@Service
public class ActivityLeadingSheepServiceImpl implements
		ActivityLeadingSheepService {
	private static Logger logger = LoggerFactory
			.getLogger(ActivityLeadingSheepServiceImpl.class);
	@Autowired
	private ProjectManager projectManager;

	@Autowired
	private PopularityInOutLogManager popularityInOutLogManager;
	@Autowired
	private TransactionMapper transactionMapper;

	@Override
	public List<ActivityLeadingSheep> getProjectForLeadingSheeps() {
		try {
			List<ActivityLeadingSheep> leadingSheepList = projectManager
					.getProjectForLeadingSheeps();
			for (ActivityLeadingSheep activityLeadingSheep : leadingSheepList) {
				for (ActivityLeadingSheepProject project : activityLeadingSheep
						.getActivityLeadingSheepProjects()) {
					if (project.getMemberId() != null) {
						project.setAvatars(ServletUtil
								.getMemberAvatarById(project.getMemberId()));
						project.setUsername(ServletUtil
								.getMemberUserName(project.getMemberId()));
					}
					if (project.getId() != null) {
						if (project.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
							project.setProgress(SysServiceUtils
									.getProjectNumberProgress(
											project.getTotalAmount(),
											project.getId()));
						}
					}

				}
			}
			return leadingSheepList;
		} catch (Exception e) {
			logger.error("获取一羊领头专题页数据失败", e);
		}
		return null;
	}

	/**
	 * 查询一马当先等活动人气值列表
	 */
	@Override
	public List<ActivityForFirstInvest> selectLeadingSheepList(
			PopularityInOutLogQuery popularityInOutLogQuery) {
		try {
			String remarkKey = "";
			if (RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST
					.getRemarks().equals(popularityInOutLogQuery.getRemark())) {
				remarkKey = RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST
						.getCode();
			}
			if (RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST
					.getRemarks().equals(popularityInOutLogQuery.getRemark())) {
				remarkKey = RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST
						.getCode();
			}
			if (RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST
					.getRemarks().equals(popularityInOutLogQuery.getRemark())) {
				remarkKey = RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST
						.getCode();
			}
			if (RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST
					.getRemarks().equals(popularityInOutLogQuery.getRemark())) {
				remarkKey = RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST
						.getCode();
			}
			if (RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST
					.getRemarks().equals(popularityInOutLogQuery.getRemark())) {
				remarkKey = RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST
						.getCode();
			}

			String key = RedisConstant.REDIS_KEY_ACTIVITY
					+ RedisConstant.REDIS_SEPERATOR
					+ RedisConstant.REDIS_KEY_ACTIVITY_LEADING_SHEEP
					+ RedisConstant.REDIS_SEPERATOR
					+ RedisConstant.REDIS_KEY_ACTIVITY_LEADING_SHEEP_GAINLIST
					+ remarkKey;
			boolean isExit = RedisManager.isExitByObjectKey(key);
			List<ActivityForFirstInvest> investList = null;
			if (isExit) {
				investList = (List<ActivityForFirstInvest>) RedisManager
						.getObject(key);
			} else {
				investList = popularityInOutLogManager
						.selectActivityForFirstInvestList(popularityInOutLogQuery);
				for (ActivityForFirstInvest invest : investList) {
					if (RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_FIRST_INVEST
							.getRemarks().equals(
									popularityInOutLogQuery.getRemark())) {
						// 一羊领头
						Transaction transaction = transactionMapper
								.selectFirstTransactionByProject(invest
										.getProjectId());
						if (transaction != null) {
							invest.setTransactionTime(transaction
									.getTransactionTime());
						}
					}
					if (RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_INVEST
							.getRemarks().equals(
									popularityInOutLogQuery.getRemark())) {
						Transaction transaction = transactionMapper
								.selectMostTransactionByProject(invest
										.getProjectId());
						if (transaction != null) {
							invest.setInvestAmount(transaction
									.getInvestAmount());
						}
					}
					if (RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LAST_INVEST
							.getRemarks().equals(
									popularityInOutLogQuery.getRemark())) {
						Transaction transaction = transactionMapper
								.selectLastTransactionByProject(invest
										.getProjectId());
						if (transaction != null) {
							invest.setInvestAmount(transaction
									.getInvestAmount());// 一锤定音的投资额
						}
					}
					if (RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_MOST_AND_LAST_INVEST
							.getRemarks().equals(
									popularityInOutLogQuery.getRemark())) {
						Transaction transaction = transactionMapper
								.selectLastTransactionByProject(invest
										.getProjectId());
						if (transaction != null) {
							invest.setInvestAmount(transaction
									.getInvestAmount());// 一掷千金的投资额
						}
					}
					if (RemarksEnum.TRANSACTION_ADD_POPULARITY_BALANCE_BY_LUCK_INVEST
							.getRemarks().equals(
									popularityInOutLogQuery.getRemark())) {
						Integer investCount = transactionMapper
								.getTransactionMemberCountByProject(invest
										.getProjectId());
						if (investCount != null) {
							invest.setInvestCount(investCount);// 项目总的投资笔数
						}
					}
					invest.setAvatars(ServletUtil.getMemberAvatarById(invest
							.getMemberId()));
				}
				RedisManager.putObject(key, investList);
				RedisManager.expireObject(key, 600);
			}
			return investList;
		} catch (ManagerException e) {
			logger.error("查询一马当先等活动人气值列表失败，popularityInOutLogQuery={}",
					popularityInOutLogQuery, e);
		}
		return null;
	}

	/**
	 * 查询一羊领头风云榜
	 */
	@Override
	public List<ActivityLeadingSheepRanksAndCount> selectLeadingSheepRanksAndCount() {
		try {
			String key = RedisConstant.REDIS_KEY_ACTIVITY
					+ RedisConstant.REDIS_SEPERATOR
					+ RedisConstant.REDIS_KEY_ACTIVITY_LEADING_SHEEP
					+ RedisConstant.REDIS_SEPERATOR
					+ RedisConstant.REDIS_KEY_ACTIVITY_LEADING_SHEEP_RANKLIST;
			boolean isExit = RedisManager.isExitByObjectKey(key);
			List<ActivityLeadingSheepRanksAndCount> list = null;
			if (isExit) {
				list = (List<ActivityLeadingSheepRanksAndCount>) RedisManager
						.getObject(key);
			} else {
				list = popularityInOutLogManager
						.getActivityLeadingSheepRanksAndCount();
				if (Collections3.isNotEmpty(list)) {
					for (ActivityLeadingSheepRanksAndCount ranksAndCount : list) {
						if (ranksAndCount != null
								&& Collections3.isNotEmpty(ranksAndCount
										.getRanks()))
							for (ActivityLeadingSheepRanks rank : ranksAndCount
									.getRanks()) {
								rank.setUsername(ServletUtil
										.getMemberUserName(rank.getMemberId()));// 获取用户名
								rank.setAvatars(ServletUtil
										.getMemberAvatarById(rank.getMemberId()));// 获取用户头像
							}
					}
					RedisManager.putObject(key, list);
					RedisManager.expireObject(key, 43200);
				}
			}
			return list;
		} catch (ManagerException e) {
			logger.error("查询一羊领头风云榜失败", e);
		}
		return null;
	}
	
	/**
	 * @desc 获取支持一锤定音的项目
	 * @return
	 * @author fuyili
	 * @time 2015年12月21日 上午9:37:59
	 *
	 */
	@Override
	public ActivityLeadingSheep getLastProjectsForQuintupleGift() {
		try {
			ActivityLeadingSheep leadingSheep = projectManager.getLastProjects();
			for (ActivityLeadingSheepProject project : leadingSheep.getActivityLeadingSheepProjects()) {
				if (project.getMemberId() != null) {
					project.setAvatars(ServletUtil.getMemberAvatarById(project.getMemberId()));
					project.setUsername(ServletUtil.getMemberUserName(project.getMemberId()));
				}
				if (project.getId() != null) {
					if (project.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
						project.setProgress(SysServiceUtils.getProjectNumberProgress(project.getTotalAmount(), project.getId()));
					}
				}

			}
			return leadingSheep;
		} catch (Exception e) {
			logger.error("获取一羊领头专题页数据失败", e);
		}
		return null;
	}
}
