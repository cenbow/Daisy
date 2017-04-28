package com.yourong.core.sh.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by XR on 2016/10/21.
 */
public class OrderBiz extends AbstractBaseObject {
    /**
     * id
     */
    private Long id;
    /**
     * 姓名
     */
    private String trueName;
    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 订单类型 1-投资专享 2-虚拟卡券 3-超值实物
     */
    private Integer orderType;
    /**
     * 商品id
     */
    private Long goodsId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品类型
     */
    private Integer goodsType;
    /**
     * 充值类型1-直冲，2-卡密
     */
    private Integer rechargeType;
    /**
     * 充值账号
     */
    private String rechargeCard;
    /**
     * 收货地址详细地址
     */
    private String address;
    /**
     * 收货人
     */
    private String receiver;
    /**
     * 手机号
     */
    private Long memberMobile;
    /**
     * 收货手机号
     */
    private Long takeMobile;
    /**
     * 发货信息备注(可展示给客户用)
     */
    private String sendRemark;
    /**
     * 备注
     */
    private String remark;
    /**
     * 发放时间
     */
    private Date sendTime;
    /**
     * 状态 1-待发放 2-已发放 3-已取消
     */
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(Integer goodsType) {
        this.goodsType = goodsType;
    }

    public Integer getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(Integer rechargeType) {
        this.rechargeType = rechargeType;
    }

    public String getRechargeCard() {
        return rechargeCard;
    }

    public void setRechargeCard(String rechargeCard) {
        this.rechargeCard = rechargeCard;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Long getMemberMobile() {
        return memberMobile;
    }

    public void setMemberMobile(Long memberMobile) {
        this.memberMobile = memberMobile;
    }

    public Long getTakeMobile() {
        return takeMobile;
    }

    public void setTakeMobile(Long takeMobile) {
        this.takeMobile = takeMobile;
    }

    public String getSendRemark() {
        return sendRemark;
    }

    public void setSendRemark(String sendRemark) {
        this.sendRemark = sendRemark;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
