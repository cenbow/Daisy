package com.yourong.backend.sh.dto;

import com.yourong.common.domain.AbstractBaseObject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by XR on 2016/10/19.
 */
public class GoodsDto extends AbstractBaseObject {
    /**
     * id
     */
    private Long id;
    /**
     * 产品名称
     */
    private String goodsName;
    /**
     * 产品描述
     */
    private String goodsDes;
    /**
     * 产品类型 1-投资专享 2-虚拟卡券 3-超值实物
     */
    private Integer goodsType;
    /**
     * 优惠券模板id
     */
    private Long sourceId;
    /**
     * 充值金额
     */
    private Long rechargeAmount;
    /**
     * 标签
     */
    private Integer tags;
    /**
     * 总库存
     */
    private Integer inventor;
    /**
     * 冻结库存
     */
    private Integer freezeInventor;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 价格
     */
    private BigDecimal price;
    /**
     * 折扣
     */
    private Float discount;
    /**
     * 状态 1-下架 2-上架
     */
    private Integer status;
    /**
     * 状态修改时间
     */
    private Date statusTime;
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

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsDes() {
        return goodsDes;
    }

    public void setGoodsDes(String goodsDes) {
        this.goodsDes = goodsDes;
    }

    public Integer getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(Integer goodsType) {
        this.goodsType = goodsType;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(Long rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public Integer getTags() {
        return tags;
    }

    public void setTags(Integer tags) {
        this.tags = tags;
    }

    public Integer getInventor() {
        return inventor;
    }

    public void setInventor(Integer inventor) {
        this.inventor = inventor;
    }

    public Integer getFreezeInventor() {
        return freezeInventor;
    }

    public void setFreezeInventor(Integer freezeInventor) {
        this.freezeInventor = freezeInventor;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(Date statusTime) {
        this.statusTime = statusTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
