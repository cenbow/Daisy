package com.yourong.core.ic.model;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.DateUtils;
import com.yourong.core.bsc.model.BscAttachment;

public class CollectionProcess extends AbstractBaseObject{
	
	 private static final long serialVersionUID = -6788851483909460435L;
    /****/
    private Long id;

    /**逾期结算id**/
    private Long overdueRepayId;
    
    /**催收日期**/
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date collectTime;
    
    /**催收形式**/
    private String collectForm;
    
    /**催收结果 1:成功，2：失败 **/
    private Integer collectStatus;
    
    /**下次催收时间**/
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date nextCollectTime;

    /**下次催收形式**/
    private String nextCollectForm;
    
    /**详细结果**/
    private String collectDetail;

   
    /**备注**/
    private String remarks;

    /****/
    private Date updateTime;

    /****/
    private Date createTime;

    /**-1:删除，1：正常**/
    private Integer delFlag;
    
    
    /** 附件信息 */
	private List<BscAttachment> bscAttachments;
	
	private List<CollectionProcess> collectList;
	
	private Integer overday;
	
	private Integer days;
	
	private Integer nextDays;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public Integer getNextDays() {
		return nextDays;
	}

	public void setNextDays(Integer nextDays) {
		this.nextDays = nextDays;
	}

	public Long getOverdueRepayId() {
		return overdueRepayId;
	}

	public Integer getOverday() {
		return overday;
	}

	public void setOverday(Integer overday) {
		this.overday = overday;
	}

	public void setOverdueRepayId(Long overdueRepayId) {
		this.overdueRepayId = overdueRepayId;
	}

	public Date getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(Date collectTime) {
		this.collectTime = collectTime;
	}

	public String getCollectForm() {
		return collectForm;
	}

	public void setCollectForm(String collectForm) {
		this.collectForm = collectForm;
	}

	public Integer getCollectStatus() {
		return collectStatus;
	}

	public void setCollectStatus(Integer collectStatus) {
		this.collectStatus = collectStatus;
	}

	public Date getNextCollectTime() {
		return nextCollectTime;
	}

	public void setNextCollectTime(Date nextCollectTime) {
		this.nextCollectTime = nextCollectTime;
	}

	public String getNextCollectForm() {
		return nextCollectForm;
	}

	public void setNextCollectForm(String nextCollectForm) {
		this.nextCollectForm = nextCollectForm;
	}

	public String getCollectDetail() {
		return collectDetail;
	}

	public void setCollectDetail(String collectDetail) {
		this.collectDetail = collectDetail;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public List<BscAttachment> getBscAttachments() {
		return bscAttachments;
	}

	public void setBscAttachments(List<BscAttachment> bscAttachments) {
		this.bscAttachments = bscAttachments;
	}

	public List<CollectionProcess> getCollectList() {
		return collectList;
	}

	public void setCollectList(List<CollectionProcess> collectList) {
		this.collectList = collectList;
	}
	
	
	public String getCollectTimeStr(){
		if (collectTime != null) {
			return DateUtils.getStrFromDate(collectTime, DateUtils.DATE_FMT_3);
		}
		return null;
	}
	
	public String getNextCollectTimeStr(){
		if (nextCollectTime != null) {
			return DateUtils.getStrFromDate(nextCollectTime, DateUtils.DATE_FMT_3);
		}
		return null;
	}

}