package com.yourong.core.sh.model;

import com.yourong.common.domain.AbstractBaseObject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by XR on 2016/10/19.
 */
public class Goods extends AbstractBaseObject {
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
    private Integer rechargeAmount;
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
     * 剩余数量
     */
    private Integer surplusInventor;
    /**
     * 是否自动增加库存 1-是，-1否
     */
    private Integer autoInventory;
    /**
     * 自动增加库存数量
     */
    private Integer autoInventoryCount;
    /**
     * 已兑换数量
     */
    private Integer exchange;
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
     * 购买商品需要等级
     */
    private Integer levelNeed;
    /**
     * 充值类型1-直冲，2-卡密
     */
    private Integer rechargeType;
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
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 删除标记
     */
    private Integer delFlag;
    /**
     * 会员打折信息
     */
    private String discountInfo;
    /**
     * 定时上架时间
     */
    private Date shelvesTime;
    /**
     * 定时下架时间
     */
    private Date offShelvesTime;
    
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

    public Integer getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(Integer rechargeAmount) {
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

    public Integer getSurplusInventor() {
        if (freezeInventor==null){
            return inventor;
        }else {
            return inventor-freezeInventor;
        }
    }

    public void setSurplusInventor(Integer surplusInventor) {
        this.surplusInventor = surplusInventor;
    }

    public Integer getAutoInventory() {
        return autoInventory;
    }

    public void setAutoInventory(Integer autoInventory) {
        this.autoInventory = autoInventory;
    }

    public Integer getAutoInventoryCount() {
        return autoInventoryCount;
    }

    public void setAutoInventoryCount(Integer autoInventoryCount) {
        this.autoInventoryCount = autoInventoryCount;
    }

    public Integer getExchange() {
        return exchange;
    }

    public void setExchange(Integer exchange) {
        this.exchange = exchange;
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

    public Integer getLevelNeed() {
        return levelNeed;
    }

    public void setLevelNeed(Integer levelNeed) {
        this.levelNeed = levelNeed;
    }

    public Integer getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(Integer rechargeType) {
        this.rechargeType = rechargeType;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

	public String getDiscountInfo() {
		return discountInfo;
	}

	public void setDiscountInfo(String discountInfo) {
		this.discountInfo = discountInfo;
	}

	public Date getShelvesTime() {
		return shelvesTime;
	}

	public void setShelvesTime(Date shelvesTime) {
		this.shelvesTime = shelvesTime;
	}

	public Date getOffShelvesTime() {
		return offShelvesTime;
	}

	public void setOffShelvesTime(Date offShelvesTime) {
		this.offShelvesTime = offShelvesTime;
	}
	
    
}
