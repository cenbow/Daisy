package com.yourong.core.cms.model;

import java.io.Serializable;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;
import org.springframework.format.annotation.DateTimeFormat;

public class CmsCategory extends AbstractBaseObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5285248743390634670L;

	/**编号**/
    private Long id;

    /**父级编号**/    
    private String parentId;

    /**栏目模块(article:文章类型 link:链接类型)**/
    private String module;

    /**栏目名称**/
    private String name;

    /**栏目图片**/
    private String image;

    /**链接**/
    private String href;

    /**目标**/
    private String target;

    /**描述**/
    private String description;

    /**关键字**/
    private String keywords;

    /**排序（升序）**/
    private Integer sort;

    /**是否在导航中显示(0:否 1:是)**/
    private Integer inMenu;

    /**是否在分类页中显示列表(0:否 1:是)**/
    private Integer inList;

    /**是否允许评论(0:否 1:是)**/
    private Integer allowComment;

    /**是否需要审核(0:否 1:是)**/
    private Integer isAudit;

    /**创建时间**/
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date createTime;

    /**更新时间**/
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date updateTime;

    /**备注信息**/
    private String remarks;

    /**删除标记(0:不删除 -1:删除)**/
    private Integer delFlag;
    
    /**栏目树是否展开(true:展开；false:缩进)非数据库字段*/
    private boolean open;
    
    /**父节点名称  非数据库字段*/
    private String parentName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId == null ? null : parentId.trim();
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module == null ? null : module.trim();
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

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href == null ? null : href.trim();
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target == null ? null : target.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords == null ? null : keywords.trim();
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getInMenu() {
        return inMenu;
    }

    public void setInMenu(Integer inMenu) {
        this.inMenu = inMenu;
    }

    public Integer getInList() {
        return inList;
    }

    public void setInList(Integer inList) {
        this.inList = inList;
    }

    public Integer getAllowComment() {
        return allowComment;
    }

    public void setAllowComment(Integer allowComment) {
        this.allowComment = allowComment;
    }

    public Integer getIsAudit() {
        return isAudit;
    }

    public void setIsAudit(Integer isAudit) {
        this.isAudit = isAudit;
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

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CmsCategory [id=");
		builder.append(id);
		builder.append(", parentId=");
		builder.append(parentId);
		builder.append(", module=");
		builder.append(module);
		builder.append(", name=");
		builder.append(name);
		builder.append(", image=");
		builder.append(image);
		builder.append(", href=");
		builder.append(href);
		builder.append(", target=");
		builder.append(target);
		builder.append(", description=");
		builder.append(description);
		builder.append(", keywords=");
		builder.append(keywords);
		builder.append(", sort=");
		builder.append(sort);
		builder.append(", inMenu=");
		builder.append(inMenu);
		builder.append(", inList=");
		builder.append(inList);
		builder.append(", allowComment=");
		builder.append(allowComment);
		builder.append(", isAudit=");
		builder.append(isAudit);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", updateTime=");
		builder.append(updateTime);
		builder.append(", remarks=");
		builder.append(remarks);
		builder.append(", delFlag=");
		builder.append(delFlag);
		builder.append(", open=");
		builder.append(open);
		builder.append(", parentName=");
		builder.append(parentName);
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
				+ ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((delFlag == null) ? 0 : delFlag.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((href == null) ? 0 : href.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + ((inList == null) ? 0 : inList.hashCode());
		result = prime * result + ((inMenu == null) ? 0 : inMenu.hashCode());
		result = prime * result + ((isAudit == null) ? 0 : isAudit.hashCode());
		result = prime * result
				+ ((keywords == null) ? 0 : keywords.hashCode());
		result = prime * result + ((module == null) ? 0 : module.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (open ? 1231 : 1237);
		result = prime * result
				+ ((parentId == null) ? 0 : parentId.hashCode());
		result = prime * result
				+ ((parentName == null) ? 0 : parentName.hashCode());
		result = prime * result + ((remarks == null) ? 0 : remarks.hashCode());
		result = prime * result + ((sort == null) ? 0 : sort.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result
				+ ((updateTime == null) ? 0 : updateTime.hashCode());
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
		CmsCategory other = (CmsCategory) obj;
		if (allowComment == null) {
			if (other.allowComment != null)
				return false;
		} else if (!allowComment.equals(other.allowComment))
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
		if (href == null) {
			if (other.href != null)
				return false;
		} else if (!href.equals(other.href))
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
		if (inList == null) {
			if (other.inList != null)
				return false;
		} else if (!inList.equals(other.inList))
			return false;
		if (inMenu == null) {
			if (other.inMenu != null)
				return false;
		} else if (!inMenu.equals(other.inMenu))
			return false;
		if (isAudit == null) {
			if (other.isAudit != null)
				return false;
		} else if (!isAudit.equals(other.isAudit))
			return false;
		if (keywords == null) {
			if (other.keywords != null)
				return false;
		} else if (!keywords.equals(other.keywords))
			return false;
		if (module == null) {
			if (other.module != null)
				return false;
		} else if (!module.equals(other.module))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (open != other.open)
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		if (parentName == null) {
			if (other.parentName != null)
				return false;
		} else if (!parentName.equals(other.parentName))
			return false;
		if (remarks == null) {
			if (other.remarks != null)
				return false;
		} else if (!remarks.equals(other.remarks))
			return false;
		if (sort == null) {
			if (other.sort != null)
				return false;
		} else if (!sort.equals(other.sort))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (updateTime == null) {
			if (other.updateTime != null)
				return false;
		} else if (!updateTime.equals(other.updateTime))
			return false;
		return true;
	}
}