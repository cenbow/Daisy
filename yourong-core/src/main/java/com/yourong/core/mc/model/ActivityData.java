package com.yourong.core.mc.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ActivityData implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 306312214390115336L;

	/****/
    private Long id;

   private Long activityId;
    
   private Integer dataType;
   
   private Integer dataGole;
   
   private Integer dataSilver;
   
   private Integer dataCopper;

    /**创建时间**/
    private Date createTime;

    /**更新时间**/
    private Date updateTime;
    
    
  private String remark;
  
  private Integer delFlag;

public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public Long getActivityId() {
	return activityId;
}

public void setActivityId(Long activityId) {
	this.activityId = activityId;
}

public Integer getDataType() {
	return dataType;
}

public void setDataType(Integer dataType) {
	this.dataType = dataType;
}

public Integer getDataGole() {
	return dataGole;
}

public void setDataGole(Integer dataGole) {
	this.dataGole = dataGole;
}

public Integer getDataSilver() {
	return dataSilver;
}

public void setDataSilver(Integer dataSilver) {
	this.dataSilver = dataSilver;
}

public Integer getDataCopper() {
	return dataCopper;
}

public void setDataCopper(Integer dataCopper) {
	this.dataCopper = dataCopper;
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

public String getRemark() {
	return remark;
}

public void setRemark(String remark) {
	this.remark = remark;
}

public Integer getDelFlag() {
	return delFlag;
}

public void setDelFlag(Integer delFlag) {
	this.delFlag = delFlag;
}
   
}