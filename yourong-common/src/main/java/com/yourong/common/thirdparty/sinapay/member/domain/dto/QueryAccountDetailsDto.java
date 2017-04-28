/**
 * 
 */
package com.yourong.common.thirdparty.sinapay.member.domain.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;

/**
 * <p>微钱包的某类资金账户
 * @version 0.1 2014年5月20日 下午12:02:06
 */
public class QueryAccountDetailsDto extends RequestDto implements Serializable {

    private static final long   serialVersionUID = 1L;

    /**
     * 用户标识信息
     * 必填
     */
    @NotNull
    private String              identityId;

    /**
     * 用户标识类型
     * 必填
     */
    @NotNull
    private IdType              identityType;

    /**
     * 账户类型
     */
    private AccountType         accountType;

    /**
     * 开始时间
     * 必填
     */
    @NotNull
    private Date                startTime;

    /**
     * 结束时间
     * 必填
     */
    @NotNull
    private Date                endTime;

    /**
     * 页号
     */
    private String              pageNo;

    /**
     * 每页大小
     */
    private String              pageSize;

    /**
     * 扩展信息
     */
    private Map<String, String> extendParam;

    public String getIdentityId() {
        return identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public IdType getIdentityType() {
        return identityType;
    }

    public void setIdentityType(IdType identityType) {
        this.identityType = identityType;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Map<String, String> getExtendParam() {
        return extendParam;
    }

    public void setExtendParam(Map<String, String> extendParam) {
        this.extendParam = extendParam;
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

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QueryAccountDetailsDto [identityId=");
		builder.append(identityId);
		builder.append(", identityType=");
		builder.append(identityType);
		builder.append(", accountType=");
		builder.append(accountType);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append(", pageNo=");
		builder.append(pageNo);
		builder.append(", pageSize=");
		builder.append(pageSize);
		builder.append(", extendParam=");
		builder.append(extendParam);
		builder.append("]");
		return builder.toString();
	}
}
