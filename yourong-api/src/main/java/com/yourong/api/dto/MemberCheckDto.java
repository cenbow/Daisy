package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员签到对象
 * @author Leon Ray
 * 2015年3月18日-下午5:23:50
 */
public class MemberCheckDto {
    /**签到时间**/
    private Date checkDate;

    /**签到所或人气值**/
    private BigDecimal gainPopularity;
    
    /**倍数**/
    private int popularityDouble = 1;
    
    private Integer popularity;

	public Date getCheckDate() {
		return checkDate;
	}
	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}
	public BigDecimal getGainPopularity() {
		return gainPopularity;
	}
	public void setGainPopularity(BigDecimal gainPopularity) {
		this.gainPopularity = gainPopularity;
	}
	public int getPopularityDouble() {
		return popularityDouble;
	}
	public void setPopularityDouble(int popularityDouble) {
		this.popularityDouble = popularityDouble;
	}
	public Integer getPopularity() {
		return popularity;
	}
	public void setPopularity(Integer popularity) {
		this.popularity = popularity;
	}
   
}