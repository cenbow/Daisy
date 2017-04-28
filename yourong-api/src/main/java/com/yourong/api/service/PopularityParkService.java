package com.yourong.api.service;

import com.yourong.api.dto.ResultDTO;

public interface PopularityParkService {

	
	ResultDTO<Object> getMemberCenter(Long memberId);

	//会员成长记录
	ResultDTO<Object> getGrowthSystem(Long memberId,Integer pageNo);

	//投资专享全部商品
	ResultDTO<Object> getInvestProductList(Long memberId);
	
	//投资专享全部商品
	ResultDTO<Object> getAllProductList(Long memberId,Integer goodsType);
	
	
	//商品详情
	ResultDTO<Object> getProductDetail(Long goodId,Long memberId);
	
	//兑换记录
	ResultDTO<Object> getExchangeRecord(Long memberId,Integer currentPage);

	//订单详情
	ResultDTO<Object> getExchangeOrderInfo(Long orderMainId,Long memberId);

	
	
}
