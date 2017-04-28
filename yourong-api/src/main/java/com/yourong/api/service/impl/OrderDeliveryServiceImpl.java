package com.yourong.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.api.service.OrderDeliveryService;
import com.yourong.core.sh.manager.ShopOrderManager;
import com.yourong.core.sh.model.OrderDelivery;
@Service
public class OrderDeliveryServiceImpl implements OrderDeliveryService{

	@Autowired
	private ShopOrderManager shopOrderManager;
	
	@Override
	public OrderDelivery queryOrderDelivery(Long memberId) {
		return shopOrderManager.queryOrderDelivery(memberId);
	}

}
