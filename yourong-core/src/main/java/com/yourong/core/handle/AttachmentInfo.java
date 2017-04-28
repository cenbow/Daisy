package com.yourong.core.handle;

import java.util.List;

import com.yourong.core.bsc.model.BscAttachment;

public class AttachmentInfo {
	public static final String SAVE = "SAVE";
	public static final String UPDATE = "UPDATE";
	public static final String EMERGENCY_UPDATE = "EMERGENCY_UPDATE";//紧急修改
	
	private String keyId;
	private List<BscAttachment> bscAttachments;
	private String operation;
	private String appPath;
	private AttachMentType attachmentType;
	public static enum AttachMentType {
	    /**
	     * 债权
	     */
	    DEBT,
	    /**
	     * 项目
	     */
	    PROJECT,
		/**
		 * 人气值商品
		 */
	    GOODS,
		/**
		 * 文章
		 */
		ARTICLE,
		/**
		 * 公司管理层
		 */
		COMPANYMANAGE,
	}
	
	public String getKeyId() {
		return keyId;
	}
	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}
	
	public List<BscAttachment> getBscAttachments() {
		return bscAttachments;
	}
	public void setBscAttachments(List<BscAttachment> bscAttachments) {
		this.bscAttachments = bscAttachments;
	}
	
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public String getAppPath() {
		return appPath;
	}
	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}
	public AttachMentType getAttachmentType() {
		return attachmentType;
	}
	public void setAttachMentType(AttachMentType attachmentType) {
		this.attachmentType = attachmentType;
	}
	
}
