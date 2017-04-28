package com.yourong.core.ic.model.biz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

public class ProjectForReward extends AbstractBaseObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9193339820515134532L;

	/** 项目id **/
	private Long id;


	/** 项目名称 **/
	private String name;


	private Integer status;

	private Integer level1;
	private Integer level2;
	private Integer level3;
	private Integer level4;
	private Integer level5;
	private Integer level6;
	
	private Date receivedTime;
    
    private String rewardInfo;
    
    private String progress;
    
    
    private List<ProjectForLevel> projectForLevel;
    
    
    private BigDecimal totalRewardAmount;
    
    private BigDecimal popularity;
    
	private BigDecimal totalCash;

    private boolean overDate=true;
    
    

	public boolean isOverDate() {
		return overDate;
	}


	public void setOverDate(boolean overDate) {
		this.overDate = overDate;
	}


	public Date getReceivedTime() {
		return receivedTime;
	}


	public void setReceivedTime(Date receivedTime) {
		this.receivedTime = receivedTime;
	}


	public BigDecimal getTotalRewardAmount() {
		return totalRewardAmount;
	}


	public void setTotalRewardAmount(BigDecimal totalRewardAmount) {
		this.totalRewardAmount = totalRewardAmount;
	}




	public BigDecimal getPopularity() {
		return popularity;
	}


	public void setPopularity(BigDecimal popularity) {
		this.popularity = popularity;
	}


	public String getProgress() {
		return progress;
	}


	public void setProgress(String progress) {
		this.progress = progress;
	}


	public List<ProjectForLevel> getProjectForLevel() {
		return projectForLevel;
	}


	public void setProjectForLevel(List<ProjectForLevel> projectForLevel) {
		this.projectForLevel = projectForLevel;
	}


	public String getRewardInfo() {
		return rewardInfo;
	}


	public void setRewardInfo(String rewardInfo) {
		this.rewardInfo = rewardInfo;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Integer getStatus() {
		return status;
	}


	public void setStatus(Integer status) {
		this.status = status;
	}







	public Integer getLevel1() {
		return level1;
	}


	public void setLevel1(Integer level1) {
		this.level1 = level1;
	}


	public Integer getLevel2() {
		return level2;
	}


	public void setLevel2(Integer level2) {
		this.level2 = level2;
	}


	public Integer getLevel3() {
		return level3;
	}


	public void setLevel3(Integer level3) {
		this.level3 = level3;
	}


	public Integer getLevel4() {
		return level4;
	}


	public void setLevel4(Integer level4) {
		this.level4 = level4;
	}


	public Integer getLevel5() {
		return level5;
	}


	public void setLevel5(Integer level5) {
		this.level5 = level5;
	}


	public Integer getLevel6() {
		return level6;
	}


	public void setLevel6(Integer level6) {
		this.level6 = level6;
	}




	public String getPrefixProjectName() {
		if (name.contains("期")) {
			return name.substring(0, name.indexOf("期") + 1);
		} else {
			return "";
		}
	}

	public BigDecimal getTotalCash() {
		return totalCash;
	}

	public void setTotalCash(BigDecimal totalCash) {
		this.totalCash = totalCash;
	}	

	
	
	
}
