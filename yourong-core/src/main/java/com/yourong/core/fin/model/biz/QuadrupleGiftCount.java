package com.yourong.core.fin.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 用户四重礼获取次数
 * 
 * @author fuyili
 *
 *         创建时间:2015年8月14日上午9:27:28
 */
public class QuadrupleGiftCount extends AbstractBaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 一羊领头获取次数
	 */
	private Integer firstInvestCount;

	/**
	 * 一羊领头获取人气值总数
	 */
	private Integer firstInvestTotal;

	/**
	 * 一锤定音获取次数
	 */
	private Integer lastInvestCount;

	/**
	 * 一锤定音获取人气值总数
	 */
	private Integer lastInvestTotal;

	/**
	 * 一鸣惊人获取次数
	 */
	private Integer mostInvestCount;

	/**
	 * 一鸣惊人获取人气值总数
	 */
	private Integer mostInvestTotal;

	/**
	 * 一掷千金获取次数
	 */
	private Integer mostAndLastInvestCount;

	/**
	 * 一掷千金获取人气值总数
	 */
	private Integer mostAndLastInvestTotal;

	public Integer getMostAndLastInvestCount() {
		return mostAndLastInvestCount;
	}

	public void setMostAndLastInvestCount(Integer mostAndLastInvestCount) {
		this.mostAndLastInvestCount = mostAndLastInvestCount;
	}

	public Integer getMostAndLastInvestTotal() {
		return mostAndLastInvestTotal;
	}

	public void setMostAndLastInvestTotal(Integer mostAndLastInvestTotal) {
		this.mostAndLastInvestTotal = mostAndLastInvestTotal;
	}

	/**
	 * 幸运女神获取次数
	 */
	private Integer luckInvestCount;

	/**
	 * 幸运女神获取人气值总数
	 */
	private Integer luckInvestTotal;

	public Integer getFirstInvestCount() {
		return firstInvestCount;
	}

	public void setFirstInvestCount(Integer firstInvestCount) {
		this.firstInvestCount = firstInvestCount;
	}

	public Integer getLastInvestCount() {
		return lastInvestCount;
	}

	public void setLastInvestCount(Integer lastInvestCount) {
		this.lastInvestCount = lastInvestCount;
	}

	public Integer getMostInvestCount() {
		return mostInvestCount;
	}

	public void setMostInvestCount(Integer mostInvestCount) {
		this.mostInvestCount = mostInvestCount;
	}

	public Integer getLuckInvestCount() {
		return luckInvestCount;
	}

	public void setLuckInvestCount(Integer luckInvestCount) {
		this.luckInvestCount = luckInvestCount;
	}

	public Integer getFirstInvestTotal() {
		return firstInvestTotal;
	}

	public void setFirstInvestTotal(Integer firstInvestTotal) {
		this.firstInvestTotal = firstInvestTotal;
	}

	public Integer getLastInvestTotal() {
		return lastInvestTotal;
	}

	public void setLastInvestTotal(Integer lastInvestTotal) {
		this.lastInvestTotal = lastInvestTotal;
	}

	public Integer getMostInvestTotal() {
		return mostInvestTotal;
	}

	public void setMostInvestTotal(Integer mostInvestTotal) {
		this.mostInvestTotal = mostInvestTotal;
	}

	public Integer getLuckInvestTotal() {
		return luckInvestTotal;
	}

	public void setLuckInvestTotal(Integer luckInvestTotal) {
		this.luckInvestTotal = luckInvestTotal;
	}

}
