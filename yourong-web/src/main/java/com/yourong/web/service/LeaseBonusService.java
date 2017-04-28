package com.yourong.web.service;

import java.util.List;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.LeaseBonusDetail;
import com.yourong.core.ic.model.LeaseDetail;
import com.yourong.core.ic.model.biz.LeaseBonusForFront;
import com.yourong.core.ic.model.biz.LeaseDetailBiz;
import com.yourong.core.ic.model.query.LeaseBonusDetailQuery;

public interface LeaseBonusService {
	/**
	 * 分页获取租赁分红列表
	 * 
	 * @param memberId
	 * @param couponType
	 * @param status
	 * @return
	 */
	Page<LeaseBonusForFront> findLeaseBonusesByPage(BaseQueryParam baseQueryParam);

	/**
	 * 分红明细分页列表
	 * 
	 * @param leaseBonusDetailQuery
	 * @return
	 */
	Page<LeaseBonusDetail> findBonusDetailByPage(LeaseBonusDetailQuery leaseBonusDetailQuery);

	/**
	 * 租赁分红代付回调处理流程
	 * 
	 * @param tradeStatus
	 * @param tradeNo
	 * @return
	 */
	ResultDO<?> afterLeaseBonusHostingPay(String tradeStatus, String tradeNo, String outTradeNo) throws Exception;

	/**
	 * 租赁记录列表
	 * 
	 * @param projectId
	 * @return
	 */
	List<LeaseDetail> findLeaseDetailByPage(Long projectId);

	/**
	 * 根据租赁结算id获取租赁结算明细
	 * 
	 * @param leaseDetailId
	 * @return
	 */
	LeaseDetailBiz findLeaseDetailBiz(LeaseBonusDetailQuery leaseBonusDetailQuery);

}
