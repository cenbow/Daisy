/**
 * 
 */
package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.biz.OverduePopularityBiz;


/**
 * @desc 人气值乐园
 * @author zhanghao
 * 2016年10月20日上午10:27:53
 */
public class PopularityParkDto extends AbstractBaseObject{

	/**用户ID**/
    private Long memberId;
    
    /**vip等级**/
    private Integer vipLevel;
    
    private Integer score;
    
    /**用户昵称**/
    private String username;
    
    /**距离下一等级仍需分数**/
    private BigDecimal needSncreaseScore;
    
    /**头像**/
    private String avatars;
    
    private BigDecimal popularity;
    
     /**投资专享**/
    private List<GoodsForAppDto> investmentList;
    
    /**虚拟卡券**/
    private List<GoodsForAppDto> virtualCardList;
    
    /**实物**/
    private List<GoodsForAppDto> physicalList;

	/**
	 * 双节特惠
	 */
    private List<GoodsForAppDto> doubleList;

    /**投资专享**/
    private List<GoodsForAppDto> investmentListAll;
    
    /**虚拟卡券**/
    private List<GoodsForAppDto> virtualCardListAll;
    
    /**实物**/
    private List<GoodsForAppDto> physicalListAll;

	/**
	 * 双节特惠
	 */
    private List<GoodsForAppDto> doubleListAll;
    
    /**某种类型，全部**/
    private List<GoodsForAppDto> goodsListAll;


    private boolean levelUpFlag;
    
    private Page<ShopOrderDetilDto> shopPage;

	/**
	 * 过期人气值
	 */
	private OverduePopularityBiz overduePopularity;

	/**
	 * @return the memberId
	 */
	public Long getMemberId() {
		return memberId;
	}

	/**
	 * @param memberId the memberId to set
	 */
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	/**
	 * @return the vipLevel
	 */
	public Integer getVipLevel() {
		return vipLevel;
	}

	/**
	 * @param vipLevel the vipLevel to set
	 */
	public void setVipLevel(Integer vipLevel) {
		this.vipLevel = vipLevel;
	}

	/**
	 * @return the needSncreaseScore
	 */
	public BigDecimal getNeedSncreaseScore() {
		return needSncreaseScore;
	}

	/**
	 * @param needSncreaseScore the needSncreaseScore to set
	 */
	public void setNeedSncreaseScore(BigDecimal needSncreaseScore) {
		this.needSncreaseScore = needSncreaseScore;
	}

	/**
	 * @return the avatars
	 */
	public String getAvatars() {
		return avatars;
	}

	/**
	 * @param avatars the avatars to set
	 */
	public void setAvatars(String avatars) {
		this.avatars = avatars;
	}

	/**
	 * @return the popularity
	 */
	public BigDecimal getPopularity() {
		return popularity;
	}

	/**
	 * @param popularity the popularity to set
	 */
	public void setPopularity(BigDecimal popularity) {
		this.popularity = popularity;
	}

	/**
	 * @return the investmentList
	 */
	public List<GoodsForAppDto> getInvestmentList() {
		return investmentList;
	}

	/**
	 * @param investmentList the investmentList to set
	 */
	public void setInvestmentList(List<GoodsForAppDto> investmentList) {
		this.investmentList = investmentList;
	}

	/**
	 * @return the virtualCardList
	 */
	public List<GoodsForAppDto> getVirtualCardList() {
		return virtualCardList;
	}

	/**
	 * @param virtualCardList the virtualCardList to set
	 */
	public void setVirtualCardList(List<GoodsForAppDto> virtualCardList) {
		this.virtualCardList = virtualCardList;
	}

	/**
	 * @return the physicalList
	 */
	public List<GoodsForAppDto> getPhysicalList() {
		return physicalList;
	}

	/**
	 * @param physicalList the physicalList to set
	 */
	public void setPhysicalList(List<GoodsForAppDto> physicalList) {
		this.physicalList = physicalList;
	}

	public List<GoodsForAppDto> getDoubleList() {
		return doubleList;
	}

	public void setDoubleList(List<GoodsForAppDto> doubleList) {
		this.doubleList = doubleList;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the levelUpFlag
	 */
	public boolean isLevelUpFlag() {
		return levelUpFlag;
	}

	/**
	 * @param levelUpFlag the levelUpFlag to set
	 */
	public void setLevelUpFlag(boolean levelUpFlag) {
		this.levelUpFlag = levelUpFlag;
	}

	/**
	 * @return the investmentListAll
	 */
	public List<GoodsForAppDto> getInvestmentListAll() {
		return investmentListAll;
	}

	/**
	 * @param investmentListAll the investmentListAll to set
	 */
	public void setInvestmentListAll(List<GoodsForAppDto> investmentListAll) {
		this.investmentListAll = investmentListAll;
	}

	/**
	 * @return the virtualCardListAll
	 */
	public List<GoodsForAppDto> getVirtualCardListAll() {
		return virtualCardListAll;
	}

	/**
	 * @param virtualCardListAll the virtualCardListAll to set
	 */
	public void setVirtualCardListAll(List<GoodsForAppDto> virtualCardListAll) {
		this.virtualCardListAll = virtualCardListAll;
	}

	/**
	 * @return the physicalListAll
	 */
	public List<GoodsForAppDto> getPhysicalListAll() {
		return physicalListAll;
	}

	/**
	 * @param physicalListAll the physicalListAll to set
	 */
	public void setPhysicalListAll(List<GoodsForAppDto> physicalListAll) {
		this.physicalListAll = physicalListAll;
	}

	public List<GoodsForAppDto> getDoubleListAll() {
		return doubleListAll;
	}

	public void setDoubleListAll(List<GoodsForAppDto> doubleListAll) {
		this.doubleListAll = doubleListAll;
	}

	/**
	 * @return the shopPage
	 */
	public Page<ShopOrderDetilDto> getShopPage() {
		return shopPage;
	}

	/**
	 * @param shopPage the shopPage to set
	 */
	public void setShopPage(Page<ShopOrderDetilDto> shopPage) {
		this.shopPage = shopPage;
	}

	/**
	 * @return the score
	 */
	public Integer getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(Integer score) {
		this.score = score;
	}

	public OverduePopularityBiz getOverduePopularity() {
		return overduePopularity;
	}

	public void setOverduePopularity(OverduePopularityBiz overduePopularity) {
		this.overduePopularity = overduePopularity;
	}

	public List<GoodsForAppDto> getGoodsListAll() {
		return goodsListAll;
	}

	public void setGoodsListAll(List<GoodsForAppDto> goodsListAll) {
		this.goodsListAll = goodsListAll;
	}
	
	
}
