package com.yourong.core.mc.model;

import java.util.Date;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.bsc.model.BscAttachment;
public class WinxinTemplate extends AbstractBaseObject{
	private static final long serialVersionUID = -6563351897480136314L;
	
	/**主键**/
    private Long id;
    
    /**规则名称**/
    private String name;
    
    /**类型**/
    private String type;
    
    /** */
    private String templateType;

    /**文字描述**/
    private String textDescribe;
    
    /**标题**/
    private String title;
    
    /**链接**/
    private String url;
    
    /**关键字**/
    private String keyword1;
    private String keyword2;
    private String keyword3;
    private String keyword4;
    private String keyword5;
    
    private String status;
    
    /**备注信息**/
    private String remarks;

    /**创建时间**/
    private Date createTime;

    /**更新时间**/
    private Date updateTime;


    /**删除标记**/
    private Integer delFlag;

    /** 附件信息 */
	private List<BscAttachment> bscAttachments;

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


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getTextDescribe() {
		return textDescribe;
	}


	public void setTextDescribe(String textDescribe) {
		this.textDescribe = textDescribe;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	

	public String getRemarks() {
		return remarks;
	}


	public void setRemarks(String remarks) {
		this.remarks = remarks;
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


	public Integer getDelFlag() {
		return delFlag;
	}


	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public List<BscAttachment> getBscAttachments() {
		return bscAttachments;
	}


	public void setBscAttachments(List<BscAttachment> bscAttachments) {
		this.bscAttachments = bscAttachments;
	}


	public String getTemplateType() {
		return templateType;
	}


	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}


	public String getKeyword1() {
		return keyword1;
	}


	public void setKeyword1(String keyword1) {
		this.keyword1 = keyword1;
	}


	public String getKeyword2() {
		return keyword2;
	}


	public void setKeyword2(String keyword2) {
		this.keyword2 = keyword2;
	}


	public String getKeyword3() {
		return keyword3;
	}


	public void setKeyword3(String keyword3) {
		this.keyword3 = keyword3;
	}


	public String getKeyword4() {
		return keyword4;
	}


	public void setKeyword4(String keyword4) {
		this.keyword4 = keyword4;
	}


	public String getKeyword5() {
		return keyword5;
	}


	public void setKeyword5(String keyword5) {
		this.keyword5 = keyword5;
	}
    
	
}
