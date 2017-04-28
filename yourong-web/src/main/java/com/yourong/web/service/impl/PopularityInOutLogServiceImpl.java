package com.yourong.web.service.impl;

import java.math.BigDecimal;
import java.util.List;

import com.yourong.core.fin.model.biz.OverduePopularityBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.common.constant.Config;
import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.model.PopularityInOutLog;
import com.yourong.core.fin.model.biz.PopularityMemberBiz;
import com.yourong.core.fin.model.biz.QuadrupleGiftCount;
import com.yourong.core.fin.model.query.PopularityInOutLogQuery;
import com.yourong.core.mc.model.biz.ActivityForFirstInvest;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.web.service.PopularityInOutLogService;
import com.yourong.web.utils.ServletUtil;

/**
 * 
 * @author fuyili
 *
 *         创建时间:2015年5月11日下午2:11:06
 */
@Service
public class PopularityInOutLogServiceImpl implements PopularityInOutLogService {

	private static Logger logger = LoggerFactory.getLogger(PopularityInOutLogServiceImpl.class);

	@Autowired
	private PopularityInOutLogManager popularityInOutLogManager;

	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private TransactionManager transactionManager;

	@Override
	public ResultDO<PopularityInOutLog> giveQuestionPopularity(Long memberId, String projId) {
		ResultDO<PopularityInOutLog> result = new ResultDO<PopularityInOutLog>();
		try {
			// 判断用户是否已赠送过该人气值
			int recordNum = popularityInOutLogManager.findPopularityByMemberIdAndRemark(
					RemarksEnum.QUESTION_ADD_POPULARITY_BALANCE.getRemarks() + "(问卷编号：" + projId + ")", memberId);
			if (recordNum > 0) {
				result.setResultCode(ResultCode.QUESTION_POPULARITY_RECEIVED);
				return result;
			}
			BigDecimal value = new BigDecimal(Config.getQuestionPopularity());
			// 调用赠送人气值接口
			// Balance balance =
			// balanceManager.increaseBalance(TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY,
			// value, memberId);
			// popularityInOutLogManager.insert(memberId,
			// TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY, value, null,
			// balance.getAvailableBalance(), memberId,
			// RemarksEnum.QUESTION_ADD_POPULARITY_BALANCE.getRemarks()
			// + "(问卷编号：" + projId + ")");

			transactionManager.givePopularity(memberId, memberId, TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY, value, "(问卷编号：" + projId + ")");

			MessageClient.sendMsgForQuestionnaire(memberId);
		} catch (ManagerException e) {
			result.setSuccess(false);
			logger.error("问卷调查赠送人气值失败,memberId=", memberId, e);
		}
		return result;
	}

	/**
	 * 获取用户分别通过任务、投资、活动赚取的人气值
	 */
	@Override
	public PopularityMemberBiz findTotalPopByMemberId(Long memberId) {
		try {
			return popularityInOutLogManager.findTotalPopByMemberId(memberId);
		} catch (ManagerException e) {
			logger.error("获取用户分别通过任务、投资、活动赚取的人气值失败,memberId={}", memberId, e);
		}
		return null;
	}

	/**
	 * 获取用户人气值流水
	 */
	@Override
	public Page<PopularityInOutLog> getPopularityInOutLog(BaseQueryParam query) {
		try {
			return popularityInOutLogManager.selectPopularityInOutLog(query);
		} catch (ManagerException e) {
			logger.error("获取用户人气值流水,query={}", query, e);
		}
		return null;
	}

	/**
	 * 获取用户获取五重礼的次数
	 */
	@Override
	public QuadrupleGiftCount getQuadrupleGiftCountByMemberId(Long memberId) {
		try {
			return popularityInOutLogManager.getQuadrupleGiftCountByMemberId(memberId);
		} catch (ManagerException e) {
			logger.error("获取用户获取五重礼的次数,memberId={}", memberId, e);
		}
		return null;
	}

	/**
	 * 平台五重礼每一种获取总次数和人气值总数
	 */
	@Override
	public QuadrupleGiftCount getQuintupleGiftCount() {
		try {
			return popularityInOutLogManager.getQuintupleGiftCount();
		} catch (ManagerException e) {
			logger.error("平台五重礼每一种获取的总次数和人气值总数失败", e);
		}
		return null;
	}

	/**
	 * @desc 获取五重礼送人气值用户列表
	 * @param query
	 * @return
	 * @author fuyili
	 * @time 2015年12月14日 下午1:12:42
	 *
	 */
	@Override
	public List<ActivityForFirstInvest> selectQuintupleGiftMemberList(PopularityInOutLogQuery query) {
		List<ActivityForFirstInvest> list = null;
		try {
			list = popularityInOutLogManager.selectQuintupleGiftList(query);
		} catch (ManagerException e) {
			logger.error("获取五重礼送人气值用户列表失败，popularityInOutLogQuery={}",query, e);
		}
		return list;
	}

	@Override
	public OverduePopularityBiz queryOverduePopularity(Long memberid) {
		try {
			return popularityInOutLogManager.queryOverduePopularity(memberid);
		} catch (ManagerException e) {
			logger.error("查询用户过期人气值异常，memberId={}",memberid, e);
		}
		return null;
	}
}
