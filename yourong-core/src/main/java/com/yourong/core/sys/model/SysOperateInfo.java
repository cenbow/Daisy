package com.yourong.core.sys.model;

import java.io.Serializable;
import java.util.Date;

public class SysOperateInfo implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5369216980320858089L;

	/**编号**/
    private Long id;

    /**来源id**/
    private Long sourceId;

    /**操作人id**/
    private Long operateId;
    
    /**操作时间**/
    private Date operateTime;

    /**操作表类型(1,项目表)**/
    private Integer operateTableType;
   
    /**操作内容**/
    private String operateMsg;
    
    /**操作编码**/
    private String operateCode;
    
    /**创建时间**/
    private Date createTime;

    /**更新时间**/
    private Date updateTime;

    /**备注**/
    private String remarks;

    /**删除标记**/
    private Integer delFlag;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public Long getOperateId() {
		return operateId;
	}

	public void setOperateId(Long operateId) {
		this.operateId = operateId;
	}

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public Integer getOperateTableType() {
		return operateTableType;
	}

	public void setOperateTableType(Integer operateTableType) {
		this.operateTableType = operateTableType;
	}

	public String getOperateMsg() {
		return operateMsg;
	}

	public void setOperateMsg(String operateMsg) {
		this.operateMsg = operateMsg;
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
		this.remarks = remarks;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public String getOperateCode() {
		return operateCode;
	}

	public void setOperateCode(String operateCode) {
		this.operateCode = operateCode;
	}
    
    
}