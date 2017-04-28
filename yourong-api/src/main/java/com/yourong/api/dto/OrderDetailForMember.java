package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class OrderDetailForMember {
	
	/**订单编号**/
	private Long orderId;
	/** 内部交易号，由前台生成 **/
	private String orderNo;
	/**项目编号**/
	@JSONField(name="pid")
	private Long projectId;
	/**项目名称**/
	@JSONField(name="name")
	private String projectName;
	/** 预期收益 **/
	private BigDecimal expectAmount;
	/** 投资额 **/
	private BigDecimal investAmount;
	/** 年化收益率 **/
	private BigDecimal annualizedRate;
	/** 1:待支付 2：已支付，投资失败 3：已支付，投资成功 4：支付失败 **/
	private Integer status;
	/** 收益天数 **/
	private int profitDays;
	/****/
	private Date orderTime;
	/** 项目到期日 **/
	private Date endDate;
}
