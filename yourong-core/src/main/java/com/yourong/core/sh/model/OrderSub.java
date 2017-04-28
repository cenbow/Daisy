package com.yourong.core.sh.model;

import com.yourong.common.domain.AbstractBaseObject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by XR on 2016/10/18.
 */
public class OrderSub extends AbstractBaseObject {
    /**
     * id
     */
    private Long id;
    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 订单类型 1-投资专享 2-虚拟卡券 3-超值实物
     */
    private Integer orderType;
    /**
     * 产品id
     */
    private Long goodsId;
    /**
     * 优惠券模板id
     */
    private Long sourceId;
    /**
     * 充值账号
     */
    private String rechargeCard;
    /**
     * 充值金额
     */
    private Integer rechargeAmount;
    /**
     * 充值类型1-直冲，2-卡密
     */
    private Integer rechargeType;
    /**
     * 商品数量
     */
    private Integer goodsCount;
    /**
     * 金额(所需人气值)
     */
    private BigDecimal amount;
    /**
     * 创建时间
     */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getRechargeCard() {
        return rechargeCard;
    }

    public void setRechargeCard(String rechargeCard) {
        this.rechargeCard = rechargeCard;
    }

    public Integer getRechargeAmount() {
        return rechargeAmount;
    }

    public Integer getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(Integer rechargeType) {
        this.rechargeType = rechargeType;
    }

    public void setRechargeAmount(Integer rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public Integer getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(Integer goodsCount) {
        this.goodsCount = goodsCount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
