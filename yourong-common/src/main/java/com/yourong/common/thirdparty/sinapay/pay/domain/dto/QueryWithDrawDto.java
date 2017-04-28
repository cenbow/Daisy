package com.yourong.common.thirdparty.sinapay.pay.domain.dto;

import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;

/**
 * <p>提现查询</p>
 * @author Wallis Wang
 * @version $Id: QueryWithDrawDto.java, v 0.1 2014年5月26日 上午11:28:40 wangqiang Exp $
 */
public class QueryWithDrawDto extends PageQuery {

    /**
     * 
     */
    private static final long   serialVersionUID = -5677763548266164679L;

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
     * 交易订单号
     */
    private String              outTradeNo;

    /**
     * 开始时间
     * <p>数字串，一共14位格式为：年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]</p>
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
		builder.append("QueryWithDrawDto [identityId=");
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
