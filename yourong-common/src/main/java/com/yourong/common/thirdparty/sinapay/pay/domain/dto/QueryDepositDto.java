package com.yourong.common.thirdparty.sinapay.pay.domain.dto;

import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;

/**
 * <p>充值</p>
 * @author Wallis Wang
 * @version $Id: QueryDepositDto.java, v 0.1 2014年5月26日 上午11:22:50 wangqiang Exp $
 */
public class QueryDepositDto extends PageQuery {

    /**
     * 
     */
    private static final long   serialVersionUID = 5804684308207377896L;

    /**
     * 外部系统用户ID
     * 必填
     * <p>商户系统用户id(字母或数字)</p>
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
     * 交易订单号
     */
    private String              outTradeNo;

    /**
     * 开始时间
     */
    private String              startTime;

    /**
     * 结束时间
     */
    private String              endTime;

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

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Map<String, String> getExtendParam() {
        return extendParam;
    }

    public void setExtendParam(Map<String, String> extendParam) {
        this.extendParam = extendParam;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QueryDepositDto [identityId=");
		builder.append(identityId);
		builder.append(", identityType=");
		builder.append(identityType);
		builder.append(", outTradeNo=");
		builder.append(outTradeNo);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append(", extendParam=");
		builder.append(extendParam);
		builder.append("]");
		return builder.toString();
	}
}
