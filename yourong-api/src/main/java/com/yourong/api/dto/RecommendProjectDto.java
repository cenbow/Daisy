/**
 * 
 */
package com.yourong.api.dto;

import java.math.BigDecimal;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.common.constant.Config;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.StringUtil;

/**
 * @desc 推荐项目对象
 * @author zhanghao
 * 2016年5月16日下午2:06:39
 */
public class RecommendProjectDto {

	/**项目id**/
	@JSONField(name="pid")
    private Long id;
    
    /**项目名称**/
    private String name;
    
    /**最小收益**/
    private BigDecimal minAnnualizedRate;

    /**最大收益**/
    private BigDecimal maxAnnualizedRate;
    
    /**剩余收益日**/
    private Integer incomeDays;
    
    /**可投余额**/
    private BigDecimal availableBalance;
    
    /**项目缩略图**/
    private String thumbnail;
    
    /**投资类型（1-债权；2-直投）**/
    private Integer investType;
    
    /**借款周期**/
    private Integer borrowPeriod;

    /**借款周期类型（1-日；2-月；3-年）**/
    private Integer borrowPeriodType;
    
    
    private Integer ResidualIncomeDays;

    private boolean isDirectProject;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the minAnnualizedRate
	 */
	public BigDecimal getMinAnnualizedRate() {
		return minAnnualizedRate;
	}

	/**
	 * @param minAnnualizedRate the minAnnualizedRate to set
	 */
	public void setMinAnnualizedRate(BigDecimal minAnnualizedRate) {
		this.minAnnualizedRate = minAnnualizedRate;
	}

	/**
	 * @return the maxAnnualizedRate
	 */
	public BigDecimal getMaxAnnualizedRate() {
		return maxAnnualizedRate;
	}

	/**
	 * @param maxAnnualizedRate the maxAnnualizedRate to set
	 */
	public void setMaxAnnualizedRate(BigDecimal maxAnnualizedRate) {
		this.maxAnnualizedRate = maxAnnualizedRate;
	}

	/**
	 * @return the incomeDays
	 */
	public Integer getIncomeDays() {
		return incomeDays;
	}

	/**
	 * @param incomeDays the incomeDays to set
	 */
	public void setIncomeDays(Integer incomeDays) {
		this.incomeDays = incomeDays;
	}

	/**
	 * @return the availableBalance
	 */
	public BigDecimal getAvailableBalance() {
		return availableBalance;
	}

	/**
	 * @param availableBalance the availableBalance to set
	 */
	public void setAvailableBalance(BigDecimal availableBalance) {
		this.availableBalance = availableBalance;
	}

	/**
	 * @return the thumbnail
	 */
	public String getThumbnail() {
		if(thumbnail != null){
			if(getId() != null && getId() >= 989800156L){
				return Config.ossPicUrl+StringUtil.getFilePath(thumbnail,"300");
			}else{
				return Config.ossPicUrl+thumbnail;
			}
		}
		return null;
	}

	/**
	 * @param thumbnail the thumbnail to set
	 */
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	/**
	 * @return the investType
	 */
	public Integer getInvestType() {
		return investType;
	}

	/**
	 * @param investType the investType to set
	 */
	public void setInvestType(Integer investType) {
		this.investType = investType;
	}

	/**
	 * @return the borrowPeriod
	 */
	public Integer getBorrowPeriod() {
		return borrowPeriod;
	}

	/**
	 * @param borrowPeriod the borrowPeriod to set
	 */
	public void setBorrowPeriod(Integer borrowPeriod) {
		this.borrowPeriod = borrowPeriod;
	}

	/**
	 * @return the borrowPeriodType
	 */
	public Integer getBorrowPeriodType() {
		return borrowPeriodType;
	}

	/**
	 * @param borrowPeriodType the borrowPeriodType to set
	 */
	public void setBorrowPeriodType(Integer borrowPeriodType) {
		this.borrowPeriodType = borrowPeriodType;
	}

	/**
	 * @return the residualIncomeDays
	 */
	public Integer getResidualIncomeDays() {
		return ResidualIncomeDays;
	}

	/**
	 * @param residualIncomeDays the residualIncomeDays to set
	 */
	public void setResidualIncomeDays(Integer residualIncomeDays) {
		ResidualIncomeDays = residualIncomeDays;
	}
	
	public boolean getIsDirectProject() {
		if(ProjectEnum.PROJECT_TYPE_DIRECT.getType()==getInvestType()){
			return true;
		}
		return false;
	}

	public void setDirectProject(boolean isDirectProject) {
		this.isDirectProject = isDirectProject;
	}
	
	public String getProfitPeriod() {
		if(this.borrowPeriod!=null&&this.borrowPeriodType!=null){
//			return this.borrowPeriod.toString()+(this.borrowPeriodType==TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType()?"天":
//				(this.borrowPeriodType==TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_MONTH.getType()?"个月":"年"));
			
			String borrowPeriodResult = this.borrowPeriod.toString();
			if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType()==borrowPeriodType){
				return borrowPeriodResult + "天";
			}
			if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_MONTH.getType()==borrowPeriodType){
				return borrowPeriodResult + "个月";
			}
			if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_YEAR.getType()==borrowPeriodType){
				return borrowPeriodResult + "年";
			}
			if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_WEEK.getType()==borrowPeriodType){
				return borrowPeriodResult + "周";
			}
		}
		return "";
	}

}
