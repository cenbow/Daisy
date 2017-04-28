/**
 * 
 */
package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.constant.Config;
import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.StringUtil;
import com.yourong.core.bsc.model.BscAttachment;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年10月20日上午10:39:04
 */
public class GoodsForAppDto extends AbstractBaseObject{
	
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
     * 商品图片
     */
    private String image;
    /**
     * 商品图片
     */
    private List<BscAttachment> imageList;
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
     * 购买商品需要等级
     */
    private Integer levelNeed;
    /**
     * 充值类型1-直冲，2-卡密
     */
    private Integer rechargeType;
    /**
     * 排序
     */
    private Long sort;
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 折扣价格
     */
    private BigDecimal discountPrice;
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
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 删除标记
     */
    private Integer delFlag;
    
    /**
     * 人气值
     */
    private Integer popularity;
    
    /**
     * vip等级
     */
    private Integer memberVip;
    
    /**
     * 是否投资过
     */
    private boolean investFlag;
    
    

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

    public Long getSort() {
        return sort;
    }

    public void setSort(Long sort) {
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

	/**
	 * @return the imageList
	 */
	public List<BscAttachment> getImageList() {
		return imageList;
	}

	/**
	 * @param imageList the imageList to set
	 */
	public void setImageList(List<BscAttachment> imageList) {
		this.imageList = imageList;
	}

	/**
	 * @return the discountPrice
	 */
	public BigDecimal getDiscountPrice() {
		return discountPrice;
	}

	/**
	 * @param discountPrice the discountPrice to set
	 */
	public void setDiscountPrice(BigDecimal discountPrice) {
		this.discountPrice = discountPrice;
	}

	/**
	 * @return the levelNeed
	 */
	public Integer getLevelNeed() {
		return levelNeed;
	}

	/**
	 * @param levelNeed the levelNeed to set
	 */
	public void setLevelNeed(Integer levelNeed) {
		this.levelNeed = levelNeed;
	}

	/**
	 * @return the rechargeType
	 */
	public Integer getRechargeType() {
		return rechargeType;
	}

	/**
	 * @param rechargeType the rechargeType to set
	 */
	public void setRechargeType(Integer rechargeType) {
		this.rechargeType = rechargeType;
	}

	

	/**
	 * @return the popularity
	 */
	public Integer getPopularity() {
		return popularity;
	}

	/**
	 * @param popularity the popularity to set
	 */
	public void setPopularity(Integer popularity) {
		this.popularity = popularity;
	}

	/**
	 * @return the memberVip
	 */
	public Integer getMemberVip() {
		return memberVip;
	}

	/**
	 * @param memberVip the memberVip to set
	 */
	public void setMemberVip(Integer memberVip) {
		this.memberVip = memberVip;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		if(image != null){
				return Config.ossPicUrl+image;
		}
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return the investFlag
	 */
	public boolean isInvestFlag() {
		return investFlag;
	}

	/**
	 * @param investFlag the investFlag to set
	 */
	public void setInvestFlag(boolean investFlag) {
		this.investFlag = investFlag;
	}
	
	public Integer getSurplusInventor() {
	        if (freezeInventor==null){
	            return inventor;
	        }else {
	            return inventor-freezeInventor;
	        }
	}
	
	public String getGoodStatusFlag(){
		if(popularity==null||discountPrice==null||inventor==null||memberVip==null){
			return "0";
		}

		if(this.getSurplusInventor()<1){
			return "4";//库存不够
		}
		if(popularity < discountPrice.intValue()){
			return "1";//人气值不足
		}
		if(!investFlag&&goodsType!=TypeEnum.GOODS_TYPE_FOR_INVEST.getType()){
			return "2";//未投资过
		}
		if(levelNeed!=null){
			if(memberVip<levelNeed){
				return "3";//vip等级不够
			}
		}
		return "0";
	}
	
	public Integer getAvailInventor(){
		return inventor-freezeInventor;
	}
	
}
