/**
 * 
 */
package com.yourong.core.uc.model;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.DateUtils;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年10月19日下午3:29:24
 */
public class MemberVip extends AbstractBaseObject{
	
	private static final long serialVersionUID = 5746379109990570174L;

	/****/
    private Long id;
	
    /**用户ID**/
    private Long memberId;
    
    /**月份**/
    private String month;
    
    /**分数**/
    private BigDecimal score;
    
    /**vip等级**/
    private Integer vipLevel;
    
    /**上月分数**/
    private BigDecimal lastScore;
    
    /**上月vip等级**/
    private Integer lastVipLevel;
    
    /**环比增长分数**/
    private BigDecimal increasedScore;
    
    /**距离下一等级仍需分数**/
    private BigDecimal needSncreaseScore;
    
    /**更新时间**/
    private Date updateTime;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

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
	 * @return the month
	 */
	public String getMonth() {
		return month;
	}

	/**
	 * @param month the month to set
	 */
	public void setMonth(String month) {
		this.month = month;
	}

	/**
	 * @return the score
	 */
	public BigDecimal getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(BigDecimal score) {
		this.score = score;
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
	 * @return the lastScore
	 */
	public BigDecimal getLastScore() {
		return lastScore;
	}

	/**
	 * @param lastScore the lastScore to set
	 */
	public void setLastScore(BigDecimal lastScore) {
		this.lastScore = lastScore;
	}

	/**
	 * @return the lastVipLevel
	 */
	public Integer getLastVipLevel() {
		return lastVipLevel;
	}

	/**
	 * @param lastVipLevel the lastVipLevel to set
	 */
	public void setLastVipLevel(Integer lastVipLevel) {
		this.lastVipLevel = lastVipLevel;
	}

	/**
	 * @return the increasedScore
	 */
	public BigDecimal getIncreasedScore() {
		return increasedScore;
	}

	/**
	 * @param increasedScore the increasedScore to set
	 */
	public void setIncreasedScore(BigDecimal increasedScore) {
		this.increasedScore = increasedScore;
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
	 * @return the updateTime
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * @param updateTime the updateTime to set
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	public String getUpdateTimeStr(){
		if (updateTime != null) {
			return DateUtils.formatDatetoString(updateTime,
					DateUtils.TIME_PATTERN);
		}
		return null;
	}
    
    
}
