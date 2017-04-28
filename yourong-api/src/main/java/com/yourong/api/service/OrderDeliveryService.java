package com.yourong.api.service;

import com.yourong.core.sh.model.OrderDelivery;

/**
 * 查询人气值乐园发放兑换商品用户地址
 * @author 
 *
 */
public interface OrderDeliveryService {

	public OrderDelivery queryOrderDelivery(Long memberId);

}
