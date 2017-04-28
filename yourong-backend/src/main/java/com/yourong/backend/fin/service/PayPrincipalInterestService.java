package com.yourong.backend.fin.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.biz.PayPrincipalInterestBiz;


public interface PayPrincipalInterestService {
	/**
	 * 获取还本付息管理列表 
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public Page<PayPrincipalInterestBiz> findByPage(Page<PayPrincipalInterestBiz> pageRequest, Map<String, Object> map);
	
	/**
	 * 还款本息数据统计根据还款状态
	 * @param status
	 * @return
	 */
	public PayPrincipalInterestBiz findTotalPrincipalAndInterestByStatus(Map<String, Object> map);
}
