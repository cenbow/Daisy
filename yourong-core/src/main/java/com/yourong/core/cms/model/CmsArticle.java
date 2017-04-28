package com.yourong.core.cms.model;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.yourong.core.bsc.model.BscAttachment;
import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;

public class CmsArticle  extends AbstractBaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3162463328284042234L;

	/** 编号 **/
	private Long id;

	/** 栏目编号 **/
	private Long categoryId;
	/** 栏目名称 **/
	private String categoryName;

	/**
	 * 类别
	 */
	private Integer genre;

	/** 标题 **/
	private String title;

	/** 文章链接 **/
	private String link;

	/** 标题颜色 **/
	private String color;

	/** 文章图片 **/
	private String image;

	/**
	 * 精选图片
	 */
	private String chosenImage;

	/** 关键字 **/
	private String keywords;

	/** 描述、摘要 **/
	private String description;

	/** 权重，越大越靠前 **/
	private Integer weight;

	/** 权重期限 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date weightTime;

	/** 点击数 **/
	private Integer hits;

	/** 推荐位，多选 **/
	private String posid;

	/** 文章来源 **/
	private String copyfrom;

	/** 相关文章 **/
	private String relation;

	/** 是否允许评论(0:否 1:是) **/
	private Integer allowComment;

	/** 备注信息 **/
	private String remarks;

	/** 创建者 **/
	private String createBy;

	/** 创建时间 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createTime;

	public Integer getPublishState() {
		return publishState;
	}

	public void setPublishState(Integer publishState) {
		this.publishState = publishState;
	}

	public Date getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}

	/** 更新者 **/
	private String updateBy;

	/** 更新时间 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date updateTime;

	/** 删除标记(0:未删除 -1:已删除) **/
	private Integer delFlag;

	/** 发布状态(0:未发布  1:已发布) **/
	private Integer publishState;
	
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    /**上线时间(年月日时分秒)**/
    private Date onlineTime;
	
	
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    /**结束时间(年月日时分秒)**/
    private Date endTime;
	
	
	/** 文章内容 **/
	private byte[] content;
	
	private String contentHtml;
	/**
	 * 文章图片附件
	 */
	private List<BscAttachment> commonAttachments;

	/**
	 * 精选图片附件
	 */
	private List<BscAttachment> chosenAttachments;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getGenre() {
		return genre;
	}

	public void setGenre(Integer genre) {
		this.genre = genre;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title == null ? null : title.trim();
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link == null ? null : link.trim();
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color == null ? null : color.trim();
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image == null ? null : image.trim();
	}

	public String getChosenImage() {
		return chosenImage;
	}

	public void setChosenImage(String chosenImage) {
		this.chosenImage = chosenImage;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords == null ? null : keywords.trim();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description == null ? null : description.trim();
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Date getWeightTime() {
		return weightTime;
	}

	public void setWeightTime(Date weightTime) {
		this.weightTime = weightTime;
	}

	public Integer getHits() {
		return hits;
	}

	public void setHits(Integer hits) {
		this.hits = hits;
	}

	public String getPosid() {
		return posid;
	}

	public void setPosid(String posid) {
		this.posid = posid == null ? null : posid.trim();
	}

	public String getCopyfrom() {
		return copyfrom;
	}

	public void setCopyfrom(String copyfrom) {
		this.copyfrom = copyfrom == null ? null : copyfrom.trim();
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation == null ? null : relation.trim();
	}

	public Integer getAllowComment() {
		return allowComment;
	}

	public void setAllowComment(Integer allowComment) {
		this.allowComment = allowComment;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks == null ? null : remarks.trim();
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

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getContentHtml() {
		if(this.getContent() != null){
			return new String(this.getContent());
		}
		return null;
	}

	public void setContentHtml(String contentHtml) {
		this.contentHtml = contentHtml;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CmsArticle [id=");
		builder.append(id);
		builder.append(", categoryId=");
		builder.append(categoryId);
		builder.append(", categoryName=");
		builder.append(categoryName);
		builder.append(", title=");
		builder.append(title);
		builder.append(", link=");
		builder.append(link);
		builder.append(", color=");
		builder.append(color);
		builder.append(", image=");
		builder.append(image);
		builder.append(", keywords=");
		builder.append(keywords);
		builder.append(", description=");
		builder.append(description);
		builder.append(", weight=");
		builder.append(weight);
		builder.append(", weightTime=");
		builder.append(weightTime);
		builder.append(", hits=");
		builder.append(hits);
		builder.append(", posid=");
		builder.append(posid);
		builder.append(", copyfrom=");
		builder.append(copyfrom);
		builder.append(", relation=");
		builder.append(relation);
		builder.append(", allowComment=");
		builder.append(allowComment);
		builder.append(", remarks=");
		builder.append(remarks);
		builder.append(", createBy=");
		builder.append(createBy);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", updateBy=");
		builder.append(updateBy);
		builder.append(", updateTime=");
		builder.append(updateTime);  
		builder.append(", delFlag=");
		builder.append(delFlag);
		builder.append(", onlineTime=");
		builder.append(onlineTime);
		builder.append(", publishState=");
		builder.append(publishState);
		builder.append(", content=");
		builder.append(Arrays.toString(content));
		builder.append(", contentHtml=");
		builder.append(contentHtml);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((allowComment == null) ? 0 : allowComment.hashCode());
		result = prime * result
				+ ((categoryId == null) ? 0 : categoryId.hashCode());
		result = prime * result
				+ ((categoryName == null) ? 0 : categoryName.hashCode());
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + Arrays.hashCode(content);
		result = prime * result
				+ ((contentHtml == null) ? 0 : contentHtml.hashCode());
		result = prime * result
				+ ((copyfrom == null) ? 0 : copyfrom.hashCode());
		result = prime * result
				+ ((createBy == null) ? 0 : createBy.hashCode());
		result = prime * result
				+ ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((delFlag == null) ? 0 : delFlag.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((hits == null) ? 0 : hits.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result
				+ ((keywords == null) ? 0 : keywords.hashCode());
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + ((posid == null) ? 0 : posid.hashCode());
		result = prime * result
				+ ((relation == null) ? 0 : relation.hashCode());
		result = prime * result + ((remarks == null) ? 0 : remarks.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result
				+ ((updateBy == null) ? 0 : updateBy.hashCode());
		result = prime * result
				+ ((updateTime == null) ? 0 : updateTime.hashCode());
		result = prime * result + ((weight == null) ? 0 : weight.hashCode());
		result = prime * result
				+ ((weightTime == null) ? 0 : weightTime.hashCode());
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
		CmsArticle other = (CmsArticle) obj;
		if (allowComment == null) {
			if (other.allowComment != null)
				return false;
		} else if (!allowComment.equals(other.allowComment))
			return false;
		if (categoryId == null) {
			if (other.categoryId != null)
				return false;
		} else if (!categoryId.equals(other.categoryId))
			return false;
		if (categoryName == null) {
			if (other.categoryName != null)
				return false;
		} else if (!categoryName.equals(other.categoryName))
			return false;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (!Arrays.equals(content, other.content))
			return false;
		if (contentHtml == null) {
			if (other.contentHtml != null)
				return false;
		} else if (!contentHtml.equals(other.contentHtml))
			return false;
		if (copyfrom == null) {
			if (other.copyfrom != null)
				return false;
		} else if (!copyfrom.equals(other.copyfrom))
			return false;
		if (createBy == null) {
			if (other.createBy != null)
				return false;
		} else if (!createBy.equals(other.createBy))
			return false;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
			return false;
		if (delFlag == null) {
			if (other.delFlag != null)
				return false;
		} else if (!delFlag.equals(other.delFlag))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (hits == null) {
			if (other.hits != null)
				return false;
		} else if (!hits.equals(other.hits))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (image == null) {
			if (other.image != null)
				return false;
		} else if (!image.equals(other.image))
			return false;
		if (keywords == null) {
			if (other.keywords != null)
				return false;
		} else if (!keywords.equals(other.keywords))
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
		if (posid == null) {
			if (other.posid != null)
				return false;
		} else if (!posid.equals(other.posid))
			return false;
		if (relation == null) {
			if (other.relation != null)
				return false;
		} else if (!relation.equals(other.relation))
			return false;
		if (remarks == null) {
			if (other.remarks != null)
				return false;
		} else if (!remarks.equals(other.remarks))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (updateBy == null) {
			if (other.updateBy != null)
				return false;
		} else if (!updateBy.equals(other.updateBy))
			return false;
		if (updateTime == null) {
			if (other.updateTime != null)
				return false;
		} else if (!updateTime.equals(other.updateTime))
			return false;
		if (weight == null) {
			if (other.weight != null)
				return false;
		} else if (!weight.equals(other.weight))
			return false;
		if (weightTime == null) {
			if (other.weightTime != null)
				return false;
		} else if (!weightTime.equals(other.weightTime))
			return false;
		return true;                  
	}
	
	public String getCreateDateStr(){
		if(createTime!=null){
			return DateUtils.formatDatetoString(createTime, "yyyy.MM.dd");
		}
		return null;
	}
	
	public String getCreateDate2Str(){
		if(createTime!=null){
			return DateUtils.formatDatetoString(createTime, "yyyy-MM-dd");
		}
		return null;
	}
	
	public String getSubTitle(){
		if(title!=null){
			if(title.length()>30){
				return title.substring(0, 30)+"...";
			}else{
				return title;
			}
		}
		return null;
	}

	public String getFirstSentence() {
		if(this.getContent()!=null){
			String content = StringUtil.filterHtml(new String(this.getContent())).trim();
			if(content.length()>80){
				return content.substring(0,80);
			}else{
				return content;
			}
		}else{
			return "";
		}
	}
	
	public String getSentence() {
		if(this.getContent()!=null){
			String content = StringUtil.filterHtml(new String(this.getContent())).trim();
			content = content.replace("&nbsp;", " ");
			return content;
		}else{
			return "";
		}
	}

	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	public String getEndTimeStr(){
		if(endTime!=null){
			return DateUtils.formatDatetoString(endTime, DateUtils.TIME_PATTERN);
		}
		return null;
	}
	
	public String getOnlineTimeStr() {
		if(onlineTime!=null){
			return DateUtils.formatDatetoString(onlineTime, DateUtils.TIME_PATTERN);
		}
		return null;
	}
	
	public String getCreateTimeStr(){
		if(createTime!=null){
			return DateUtils.formatDatetoString(createTime,DateUtils.TIME_PATTERN);
		}
		return null;
	}

	public List<BscAttachment> getCommonAttachments() {
		return commonAttachments;
	}

	public void setCommonAttachments(List<BscAttachment> commonAttachments) {
		this.commonAttachments = commonAttachments;
	}

	public List<BscAttachment> getChosenAttachments() {
		return chosenAttachments;
	}

	public void setChosenAttachments(List<BscAttachment> chosenAttachments) {
		this.chosenAttachments = chosenAttachments;
	}
}