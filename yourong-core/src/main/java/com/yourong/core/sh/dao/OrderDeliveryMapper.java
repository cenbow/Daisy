package com.yourong.core.sh.dao;

import org.apache.ibatis.annotations.Param;

import com.yourong.core.sh.model.OrderDelivery;

public interface OrderDeliveryMapper {

	int insertOrderDelivery(OrderDelivery orderDelivery);

	OrderDelivery queryDeliveryByMemberId(@Param("memberId")Long memberId);

	int updateOrderDelivery(OrderDelivery orderDelivery);
}
