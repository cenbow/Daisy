package com.yourong.web.dto;

import com.yourong.core.tc.model.Order;

public class OrderPayDto {

	private Order order;

	private TransactionDto transactionDto;

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public TransactionDto getTransactionDto() {
		return transactionDto;
	}

	public void setTransactionDto(TransactionDto transactionDto) {
		this.transactionDto = transactionDto;
	}

}
