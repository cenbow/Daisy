package com.yourong.core.cms.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 
 * @author wanglei
 *
 */
public class CmsIcon extends AbstractBaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/** 主键 **/
	private Long id;

	/** icon标题 **/
	private String name;

	/** icon图片(jpg、png) **/
	private String image;
	
	/** 链接 **/
	private String href;
	
	/** 备注信息 **/
	private String remark;
	
	/** 权重（值越大越靠前） **/
	private Integer weight;
	
	/**icon状态**/
	private Integer iconStatus;
	
	/** 删除标记（1：未删除 -1：已删除） **/
	private Integer delFlag;
	
	/** 更新者 **/
	private String updateBy;

	/** 更新时间 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date updateTime;
	
	

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

	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}

	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}

	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	

	public Integer getWeight() {
		return weight;
	}
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	
	
	public Integer getIconStatus() {
		return iconStatus;
	}
	public void setIconStatus(Integer iconStatus) {
		this.iconStatus = iconStatus;
	}

	public Integer getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	
	
	
	
}
