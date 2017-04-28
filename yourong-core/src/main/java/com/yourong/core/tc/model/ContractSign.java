/**
 * 
 */
package com.yourong.core.tc.model;

import java.util.Date;


/**
 * @desc 合同签署类
 * @author zhanghao
 * 2016年7月6日下午2:27:21
 */
public class ContractSign implements Cloneable {

	 /****/
    private Long id;

    /**第三方合同ID**/
    private String signId;
    
    /**第三方文档ID**/
    private String docId;

    /**附件id**/
    private Long attachmentId;
    
    /**交易id**/
    private Long transactionId;
    
    /**手机号，即第三方账户**/
    private Long mobile;
    
    /**来源ID  用户ID 企业ID**/
    private Long sourceId;
    
    /**类型（1-甲方，2-乙方，3-第三方，即小融）**/
    private Integer type;
    
    /**签署状态（0-初始化状态，未签署，1-签署中，2-签署成功，3-签署失败）**/
    private Integer status;
    
    /**备注**/
    private String remark;

    /**删除标记**/
    private Integer delFlag;
    
    /**创建时间**/
    private Date createTime;
    
    /**更新时间**/
    private Date updateTime;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the signId
	 */
	public String getSignId() {
		return signId;
	}

	/**
	 * @param signId the signId to set
	 */
	public void setSignId(String signId) {
		this.signId = signId;
	}

	/**
	 * @return the docId
	 */
	public String getDocId() {
		return docId;
	}

	/**
	 * @param docId the docId to set
	 */
	public void setDocId(String docId) {
		this.docId = docId;
	}

	/**
	 * @return the attachmentId
	 */
	public Long getAttachmentId() {
		return attachmentId;
	}

	/**
	 * @param attachmentId the attachmentId to set
	 */
	public void setAttachmentId(Long attachmentId) {
		this.attachmentId = attachmentId;
	}

	/**
	 * @return the transactionId
	 */
	public Long getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}
	
	/**
	 * @return the mobile
	 */
	public Long getMobile() {
		return mobile;
	}

	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	/**
	 * @return the sourceId
	 */
	public Long getSourceId() {
		return sourceId;
	}

	/**
	 * @param sourceId the sourceId to set
	 */
	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * @return the delFlag
	 */
	public Integer getDelFlag() {
		return delFlag;
	}

	/**
	 * @param delFlag the delFlag to set
	 */
	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}
	
	
	
	/**
	 * @return the createTime
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime the createTime to set
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the updateTime
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * @param updateTime the updateTime to set
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Object clone() {   
		ContractSign o = null;
        try {   
        	o = (ContractSign) super.clone();
            return o;   
        } catch (CloneNotSupportedException e) {   
            return null;   
        }   
    }   
    
	
}
