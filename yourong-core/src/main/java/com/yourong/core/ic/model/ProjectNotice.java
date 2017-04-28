package com.yourong.core.ic.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;

public class ProjectNotice extends AbstractBaseObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5786778043421524009L;

	/**项目id**/
    private Long id;

    /**项目id**/
    private Long projectId;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    /**预告开始时间**/
    private Date startTime;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    /**预告结束时间**/
    private Date endTime;

    /**排序，同在预告时间内，值越小越靠前**/
    private Integer sort;

    /**状态码(1:正常  2:暂停预告 3:预告结束 )**/
    private Integer status;

    /**预告信息**/
    private String noticeMessage;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    /**创建时间**/
    private Date createTime;

    /**更新时间**/
    private Date updateTime;

    /**备注**/
    private String remarks;

    /**删除标记**/
    private Integer delFlag;
    
    /**项目名称**/
    private String projectName;
    
    /**首页显示**/
    private Integer indexShow;
    
    /**上线时间**/
    private Date onlineTime;
    
    /**预告是否通知**/
	private Integer noticeNotice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getNoticeMessage() {
        return noticeMessage;
    }

    public void setNoticeMessage(String noticeMessage) {
        this.noticeMessage = noticeMessage == null ? null : noticeMessage.trim();
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
    
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Integer getIndexShow() {
		return indexShow;
	}

	public void setIndexShow(Integer indexShow) {
		this.indexShow = indexShow;
	}
	
	public Date getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}

	public Integer getNoticeNotice() {
		return noticeNotice;
	}

	public void setNoticeNotice(Integer noticeNotice) {
		this.noticeNotice = noticeNotice;
	}

}