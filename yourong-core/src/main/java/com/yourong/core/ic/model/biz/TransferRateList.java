/**
 * 
 */
package com.yourong.core.ic.model.biz;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年12月20日下午2:36:42
 */
public class TransferRateList extends AbstractBaseObject{

	private static final long serialVersionUID = 1L;
	
	/**起始天数**/
    private Integer startDay;
	
	 /**截止天数**/
    private Integer endDay;
    
    private String rate;

	private String desc;

	/**
	 * @return the startDay
	 */
	public Integer getStartDay() {
		return startDay;
	}

	/**
	 * @param startDay the startDay to set
	 */
	public void setStartDay(Integer startDay) {
		this.startDay = startDay;
	}

	/**
	 * @return the endDay
	 */
	public Integer getEndDay() {
		return endDay;
	}

	/**
	 * @param endDay the endDay to set
	 */
	public void setEndDay(Integer endDay) {
		this.endDay = endDay;
	}

	/**
	 * @return the rate
	 */
	public String getRate() {
		return rate;
	}

	/**
	 * @param rate the rate to set
	 */
	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
