package com.yourong.core.cms.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;

public class Banner extends AbstractBaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 主键 **/
	private Long id;

	/** banner标题 **/
	private String name;

	/** banner图片(jpg、png) **/
	private String image;

	/** 动态地图 **/
	private String imageBg;

	/** 链接 **/
	private String href;
	
	/** 是否可以分享 **/
	private Integer shareFlag;

	/** 分享标题 **/
	private String shareTitle;
	
	/** 分享标题 **/
	private String shareWord;

	/** 开始时间 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date startTime;

	/** 结束时间 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date endTime;

	/** 权重（值越大越靠前） **/
	private Integer weight;

	/** 效果（匹配js代码） **/
	private String effect;

	/** 新窗口打开（1:否 2:是），默认否 **/
	private Integer targetBlank;

	/** 类型（0-pc端，1-app端  ,2-m站） **/
	private Integer type;

	/** 创建者 **/
	private String createBy;

	/** 创建时间 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date createTime;

	/** 更新者 **/
	private String updateBy;

	/** 更新时间 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date updateTime;

	/** 备注信息 **/
	private String remark;

	/** 删除标记（1：未删除 -1：已删除） **/
	private Integer delFlag;

	/** 区域标志**/
	private Integer areaSign;
	
	/** 区域标志**/
	private Integer versionSign;
	
	/** 活动Id**/
	private Long activityId;
	
	/**banner状态**/
	private Integer bannerStatus;
	
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
		this.name = name == null ? null : name.trim();
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image == null ? null : image.trim();
	}

	public String getImageBg() {
		return imageBg;
	}

	public void setImageBg(String imageBg) {
		this.imageBg = imageBg;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href == null ? null : href.trim();
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

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public String getEffect() {
		return effect;
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}

	public Integer getTargetBlank() {
		return targetBlank;
	}

	public void setTargetBlank(Integer targetBlank) {
		this.targetBlank = targetBlank;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy == null ? null : createBy.trim();
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy == null ? null : updateBy.trim();
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
		this.remark = remark == null ? null : remark.trim();
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public Integer getAreaSign() {
		if(versionSign!=null&&type!=null&& type == 1 ){
			return versionSign;
		}
		return areaSign;
	}

	public void setAreaSign(Integer areaSign) {
		this.areaSign = areaSign;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public Integer getBannerStatus() {
		return bannerStatus;
	}

	public void setBannerStatus(Integer bannerStatus) {
		this.bannerStatus = bannerStatus;
	}

	/**
	 * @return the versionSign
	 */
	public Integer getVersionSign() {
		return versionSign;
	}

	/**
	 * @param versionSign the versionSign to set
	 */
	public void setVersionSign(Integer versionSign) {
		this.versionSign = versionSign;
	}

	/**
	 * @return the shareFlag
	 */
	public Integer getShareFlag() {
		return shareFlag;
	}

	/**
	 * @param shareFlag the shareFlag to set
	 */
	public void setShareFlag(Integer shareFlag) {
		this.shareFlag = shareFlag;
	}

	/**
	 * @return the shareTitle
	 */
	public String getShareTitle() {
		return shareTitle;
	}

	/**
	 * @param shareTitle the shareTitle to set
	 */
	public void setShareTitle(String shareTitle) {
		this.shareTitle = shareTitle;
	}

	/**
	 * @return the shareWord
	 */
	public String getShareWord() {
		return shareWord;
	}

	/**
	 * @param shareWord the shareWord to set
	 */
	public void setShareWord(String shareWord) {
		this.shareWord = shareWord;
	}
	
}