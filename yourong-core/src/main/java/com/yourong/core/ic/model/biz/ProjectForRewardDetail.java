package com.yourong.core.ic.model.biz;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 第几天（day）各等级奖项的金额详情
 * 
 */
public class ProjectForRewardDetail extends AbstractBaseObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9193339820515134532L;

	private String proportion;
	
	//1等奖金额
	private BigDecimal level1Amount;
	
	//2等奖金额
	private BigDecimal level2Amount;
	
	//3等奖金额
	private BigDecimal level3Amount;
	
	//4等奖金额
	private BigDecimal level4Amount;
	
	//5等奖金额
	private BigDecimal level5Amount;
	
	//6等奖金额
	private BigDecimal level6Amount;
	
	private Date time;
	
	//第几天
	private Integer day;
	
	

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public String getProportion() {
		return proportion;
	}

	public void setProportion(String proportion) {
		this.proportion = proportion;
	}

	public BigDecimal getLevel1Amount() {
		return level1Amount;
	}

	public void setLevel1Amount(BigDecimal level1Amount) {
		this.level1Amount = level1Amount;
	}

	public BigDecimal getLevel2Amount() {
		return level2Amount;
	}

	public void setLevel2Amount(BigDecimal level2Amount) {
		this.level2Amount = level2Amount;
	}

	public BigDecimal getLevel3Amount() {
		return level3Amount;
	}

	public void setLevel3Amount(BigDecimal level3Amount) {
		this.level3Amount = level3Amount;
	}

	public BigDecimal getLevel4Amount() {
		return level4Amount;
	}

	public void setLevel4Amount(BigDecimal level4Amount) {
		this.level4Amount = level4Amount;
	}

	public BigDecimal getLevel5Amount() {
		return level5Amount;
	}

	public void setLevel5Amount(BigDecimal level5Amount) {
		this.level5Amount = level5Amount;
	}

	public BigDecimal getLevel6Amount() {
		return level6Amount;
	}

	public void setLevel6Amount(BigDecimal level6Amount) {
		this.level6Amount = level6Amount;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

    
   
	
}
