package com.yourong.core.sh.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * Created by XR on 2016/10/28.
 */
public class OrderRechargeBiz extends AbstractBaseObject {
    private Long orderId;
    /**
     * 状态 1-待发放 2-已发放 3-已取消 4-处理中 5-充值失败
     */
    private Integer status;
    /**
     * 产品id
     */
    private Long goodsId;
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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
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

    public void setRechargeAmount(Integer rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public Integer getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(Integer rechargeType) {
        this.rechargeType = rechargeType;
    }

    public Integer getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(Integer goodsCount) {
        this.goodsCount = goodsCount;
    }
}
