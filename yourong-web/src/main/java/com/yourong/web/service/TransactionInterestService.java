package com.yourong.web.service;

import java.util.List;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.TransactionForMemberCenter;
import com.yourong.core.tc.model.biz.TransactionInterestForCalendar;
import com.yourong.core.tc.model.biz.TransactionInterestForMember;
import com.yourong.core.tc.model.query.TransactionInterestQuery;


public interface TransactionInterestService {

	
	/**
	 * 查询会员所有的收益记录
	 * @param memberID
	 * @return
	 */
	public List<TransactionInterest> selectTransactionInterestByMemberID(Long memberID);

	/**
	 * 代收完成后执行还本付息
	 * @param tradeNo
	 * @param outTradeNo
	 * @param tradeStatus
	 * @return
	 * @throws Exception
	 */
	public ResultDO<?> afterHostingCollectTradeForPayInterestAndPrincipal(String tradeNo,
			String outTradeNo, String tradeStatus) ;

	/**
	 * 通过交易id获取交易本息信息
	 * @param transactionId
	 * @return
	 */
	public ResultDO<TransactionInterest> getTransactionInterestsByTransactionId(ResultDO<TransactionForMemberCenter> result,
			Long transactionId);

	/**
	 * 获取收益日历中本息记录
	 * @param memberId
	 * @return
	 */
	public ResultDO<TransactionInterestForCalendar> getInterestCalendar(
			Long memberId);

	/**
	 * 平台收益权垫付代收回调
	 * @param tradeNo
	 * @param outTradeNo
	 * @param tradeStatus
	 * @return
	 */
	public ResultDO<?> afterHostingCollectTradeCouponBForPayInterestAndPrincipal(
			String tradeNo, String outTradeNo, String tradeStatus);

	/**
	 * 根据日期获得用户还本付息详情
	 * @param memberId
	 * @param endDate
	 * @return
	 */
	public ResultDO getMemberInterestDetail(Long memberId, String endDate);

	/**
	 *  查看用户还本付息明细
	 * @param query
	 * @return
	 */
	public ResultDO  getTransactionInterestDetailForMember(TransactionInterestQuery query);
	
}
