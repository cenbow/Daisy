package com.yourong.core.tc.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Preservation implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3281709159614287112L;

	/**ID**/
    private Long id;

    /**数据保全id**/
    private Long preservationId;

    /**交易号**/
    private Long transactionId;

    /**附件id**/
    private Long attachmentId;

    /**保全时间**/
    private Date preservationTime;

    /**保全状态， -1-失败，1-成功**/
    private Integer successFlag;

    /**备注**/
    private String remarks;

    /**删除标记**/
    private Integer delFlag;

    /**创建时间**/
    private Date createTime;

    /**更新时间**/
    private Date updateTime;

    /**保全链接*/
    private String preservationLink;
    
    /**链接时效*/
    private Date linkExpireTime;
    
    /**extend*/
    /**保全文件路径（绝对路径+文件名+后缀名，如D:\\交易合同.pdf）*/
    private String contractPath;
    /**保全标题*/
    private String contractTitle;
    /**保全人身份证号*/
    private String identityNumber;
    /**保全人姓名*/
    private String identiferTrueName;
    /**保全人ID*/
    private Long memberId;
    /**投资金额*/
    private BigDecimal investAmount;
    /**保全人邮箱（选填）*/
    private String memberEmail;
    /**保全人手机号*/
    private String memberPhone;
    /**系统来源*/
    private String fromSys;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPreservationId() {
        return preservationId;
    }

    public void setPreservationId(Long preservationId) {
        this.preservationId = preservationId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public Date getPreservationTime() {
        return preservationTime;
    }

    public void setPreservationTime(Date preservationTime) {
        this.preservationTime = preservationTime;
    }

    public Integer getSuccessFlag() {
        return successFlag;
    }

    public void setSuccessFlag(Integer successFlag) {
        this.successFlag = successFlag;
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

	public String getPreservationLink() {
		return preservationLink;
	}

	public void setPreservationLink(String preservationLink) {
		this.preservationLink = preservationLink;
	}

	public Date getLinkExpireTime() {
		return linkExpireTime;
	}

	public void setLinkExpireTime(Date linkExpireTime) {
		this.linkExpireTime = linkExpireTime;
	}

	public String getContractPath() {
		return contractPath;
	}

	public void setContractPath(String contractPath) {
		this.contractPath = contractPath;
	}

	public String getContractTitle() {
		return contractTitle;
	}

	public void setContractTitle(String contractTitle) {
		this.contractTitle = contractTitle;
	}

	public String getIdentityNumber() {
		return identityNumber;
	}

	public void setIdentityNumber(String identityNumber) {
		this.identityNumber = identityNumber;
	}

	public String getIdentiferTrueName() {
		return identiferTrueName;
	}

	public void setIdentiferTrueName(String identiferTrueName) {
		this.identiferTrueName = identiferTrueName;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public BigDecimal getInvestAmount() {
		return investAmount;
	}

	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}

	public String getMemberEmail() {
		return memberEmail;
	}

	public void setMemberEmail(String memberEmail) {
		this.memberEmail = memberEmail;
	}

	public String getMemberPhone() {
		return memberPhone;
	}

	public void setMemberPhone(String memberPhone) {
		this.memberPhone = memberPhone;
	}

	public String getFromSys() {
		return fromSys;
	}

	public void setFromSys(String fromSys) {
		this.fromSys = fromSys;
	}
}