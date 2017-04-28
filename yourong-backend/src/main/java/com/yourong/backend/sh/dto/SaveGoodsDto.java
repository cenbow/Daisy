package com.yourong.backend.sh.dto;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.bsc.model.BscAttachment;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * Created by XR on 2016/10/20.
 */
public class SaveGoodsDto extends AbstractBaseObject {
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
     * 购买商品需要等级
     */
    private Integer levelNeed;
    /**
     * 充值类型
     */
    private Integer rechargeType;
    /**
     * 是否自动增加库存 1-是，-1否
     */
    private Integer autoInventory;
    /**
     * 自动增加库存数量
     */
    private Integer autoInventoryCount;
    /**
     * 会员打折信息
     */
    private String discountInfo;
    /**
     * 定时上架时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date shelvesTime;
    /**
     * 定时下架时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date offShelvesTime;
    /**
     * 附件信息
     */
    private List<BscAttachment> bscAttachments;

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

    public List<BscAttachment> getBscAttachments() {
        return bscAttachments;
    }

    public void setBscAttachments(List<BscAttachment> bscAttachments) {
        this.bscAttachments = bscAttachments;
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
