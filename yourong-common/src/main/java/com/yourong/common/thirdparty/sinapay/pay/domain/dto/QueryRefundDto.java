package com.yourong.common.thirdparty.sinapay.pay.domain.dto;

import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.enums.IdType;

/**
 * <p>退款查询</p>
 * @author Wallis Wang
 * @version $Id: QueryRefundDto.java, v 0.1 2014年6月5日 下午1:04:53 wangqiang Exp $
 */
public class QueryRefundDto extends PageQuery {

    /**
     * 
     */
    private static final long   serialVersionUID = -422413542038459914L;

    /**
     * 用户标识信息
     * 必填
     * <p>商户系统用户id(字母或数字)</p>
     */
    private String              identityId;

    /**
     * 用户标识类型
     * 必填
     * <p>ID的类型，包括UID、MOBILE、EMAIL</p>
     */
    private IdType              identityType;

    /**
     * 退款订单号
     * <p>商户网站交易订单号，商户内部保证唯一</p>
     */
    private String              outTradeNo;

    /**
     * 开始时间
     * <p>格式类似20131117020101</p>
     */
    private String              startTime;

    /**
     * 结束时间
     * <p>格式类似20131117020101</p>
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
		builder.append("QueryRefundDto [identityId=");
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
