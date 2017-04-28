package com.yourong.api.service;

import java.util.List;

import com.yourong.api.dto.CalendarTransactionInterestDetailAPPDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.TransactionInterestDetail;
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
	public ResultDTO getTransactionInterestsByTransactionId(
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
	
	
	public Page<TransactionInterestForMember> queryTransactionInterestForMember(TransactionInterestQuery query);

	/**
	 * 
	 * @Description:根据日期获得用户还本付息详情
	 * @param memberId
	 * @param endDate
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月6日 下午3:00:35
	 */
	ResultDO<TransactionInterestDetail> getMemberInterestDetail(Long memberId, String endDate);
	

	/**
	 * 
	 * @Description:根据日期获得用户还本付息详情  重构
	 * @param memberId
	 * @param endDate
	 * @return
	 * @author: zhanghao
	 * @time:2016年6月14日 上午11:37:35
	 */
	ResultDO<CalendarTransactionInterestDetailAPPDto> getMemberInterestDetailNew(Long memberId, String endDate); 

}
