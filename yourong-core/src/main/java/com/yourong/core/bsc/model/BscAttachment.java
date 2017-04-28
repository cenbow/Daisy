package com.yourong.core.bsc.model;

import java.io.Serializable;
import java.util.Date;

public class BscAttachment implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1327567009708644337L;

	/****/
	private Long id;

	/** 模块名称 contract-合同附件 debt-债权附件 **/
	private String module;

	/** 类目id，暂时没用，预留 **/
	private Integer catId;

	/** 文件名字 **/
	private String fileName;

	/** 文件url **/
	private String fileUrl;

	/** 文件大小 **/
	private Long fileSize;

	/** 文件后缀名 **/
	private String fileExt;

	/** 上传时间 **/
	private Date uploadTime;

	/** 状态， 1-可用，0-不可用 **/
	private Integer status;

	/** 排序 **/
	private Integer listOrder;

	/****/
	private String fileDesc;

	/** 存储方式：1-本地 2-OSS 3-又拍云 **/
	private String storageWay;

	/** 删除状态， -1-删除，1-未删除 **/
	private Integer delFlag;

	/** key_id bsc_attrachment_index表 */
	private String keyId;

	/** create_time bsc_attrachment_index表 */
	private Date createTime;

	/** attrachment_index_id bsc_attrachment_index表 */
	private Integer attachmentIndexId;

	/** extend */
	/** 保全ID */
	private Long preservationId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module == null ? null : module.trim();
	}

	public Integer getCatId() {
		return catId;
	}

	public void setCatId(Integer catId) {
		this.catId = catId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName == null ? null : fileName.trim();
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl == null ? null : fileUrl.trim();
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileExt() {
		return fileExt;
	}

	public void setFileExt(String fileExt) {
		this.fileExt = fileExt == null ? null : fileExt.trim();
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getListOrder() {
		return listOrder;
	}

	public void setListOrder(Integer listOrder) {
		this.listOrder = listOrder;
	}

	public String getFileDesc() {
		return fileDesc;
	}

	public void setFileDesc(String fileDesc) {
		this.fileDesc = fileDesc == null ? null : fileDesc.trim();
	}

	public String getStorageWay() {
		return storageWay;
	}

	public void setStorageWay(String storageWay) {
		this.storageWay = storageWay == null ? null : storageWay.trim();
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BscAttachment [id=");
		builder.append(id);
		builder.append(", module=");
		builder.append(module);
		builder.append(", catId=");
		builder.append(catId);
		builder.append(", fileName=");
		builder.append(fileName);
		builder.append(", fileUrl=");
		builder.append(fileUrl);
		builder.append(", fileSize=");
		builder.append(fileSize);
		builder.append(", fileExt=");
		builder.append(fileExt);
		builder.append(", uploadTime=");
		builder.append(uploadTime);
		builder.append(", status=");
		builder.append(status);
		builder.append(", listOrder=");
		builder.append(listOrder);
		builder.append(", fileDesc=");
		builder.append(fileDesc);
		builder.append(", storageWay=");
		builder.append(storageWay);
		builder.append(", delFlag=");
		builder.append(delFlag);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((catId == null) ? 0 : catId.hashCode());
		result = prime * result + ((delFlag == null) ? 0 : delFlag.hashCode());
		result = prime * result
				+ ((fileDesc == null) ? 0 : fileDesc.hashCode());
		result = prime * result + ((fileExt == null) ? 0 : fileExt.hashCode());
		result = prime * result
				+ ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result
				+ ((fileSize == null) ? 0 : fileSize.hashCode());
		result = prime * result + ((fileUrl == null) ? 0 : fileUrl.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((listOrder == null) ? 0 : listOrder.hashCode());
		result = prime * result + ((module == null) ? 0 : module.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result
				+ ((storageWay == null) ? 0 : storageWay.hashCode());
		result = prime * result
				+ ((uploadTime == null) ? 0 : uploadTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BscAttachment other = (BscAttachment) obj;
		if (catId == null) {
			if (other.catId != null)
				return false;
		} else if (!catId.equals(other.catId))
			return false;
		if (delFlag == null) {
			if (other.delFlag != null)
				return false;
		} else if (!delFlag.equals(other.delFlag))
			return false;
		if (fileDesc == null) {
			if (other.fileDesc != null)
				return false;
		} else if (!fileDesc.equals(other.fileDesc))
			return false;
		if (fileExt == null) {
			if (other.fileExt != null)
				return false;
		} else if (!fileExt.equals(other.fileExt))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (fileSize == null) {
			if (other.fileSize != null)
				return false;
		} else if (!fileSize.equals(other.fileSize))
			return false;
		if (fileUrl == null) {
			if (other.fileUrl != null)
				return false;
		} else if (!fileUrl.equals(other.fileUrl))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (listOrder == null) {
			if (other.listOrder != null)
				return false;
		} else if (!listOrder.equals(other.listOrder))
			return false;
		if (module == null) {
			if (other.module != null)
				return false;
		} else if (!module.equals(other.module))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (storageWay == null) {
			if (other.storageWay != null)
				return false;
		} else if (!storageWay.equals(other.storageWay))
			return false;
		if (uploadTime == null) {
			if (other.uploadTime != null)
				return false;
		} else if (!uploadTime.equals(other.uploadTime))
			return false;
		return true;
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getAttachmentIndexId() {
		return attachmentIndexId;
	}

	public void setAttachmentIndexId(Integer attachmentIndexId) {
		this.attachmentIndexId = attachmentIndexId;
	}

	public Long getPreservationId() {
		return preservationId;
	}

	public void setPreservationId(Long preservationId) {
		this.preservationId = preservationId;
	}

}