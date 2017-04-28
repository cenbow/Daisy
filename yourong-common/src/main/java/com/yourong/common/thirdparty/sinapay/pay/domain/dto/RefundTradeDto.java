package com.yourong.common.thirdparty.sinapay.pay.domain.dto;

import java.util.List;
import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.SplitArgs;

/**
 * <p>退款</p>
 * @version $Id: RefundTradeDto.java, v 0.1 2014年5月26日 上午10:35:43 wangqiang Exp $
 */
public class RefundTradeDto extends RequestDto {

    /**
     * 交易订单号
     * 必填   
     * <p>商户网站交易订单号，商户内部保证唯一</p>
     * 
     */
    @NotNull
    private String              outTradeNo;

    /**
     * 原始的商户网站唯一交易订单号
     * <p>原始的钱包合作商户网站唯一订单号（确保在合作伙伴系统中唯一）。同交易中的一致。</p>
     * 必填
     */
    @NotNull
    private String              origOuterTradeNo;

    /**
     * 退款金额
     * <p>支持部分退款，退款金额不能大于交易金额。单位为：RMB Yuan，精确到小数点后两位</p>
     * 必填
     */
    @NotNull
    private Money               refundAmount;

    /**
     * 摘要
     */
    private String              summary;
    
    
    @NotNull
    private String          userIp;

    /**
     * 分账信息列表
     * 
     */
    private List<SplitArgs>     splitList;

    /**
     * 扩展信息
     */
    private Map<String, String> extendParam;

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getOrigOuterTradeNo() {
        return origOuterTradeNo;
    }

    public void setOrigOuterTradeNo(String origOuterTradeNo) {
        this.origOuterTradeNo = origOuterTradeNo;
    }

    public Money getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Money refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<SplitArgs> getSplitList() {
        return splitList;
    }

    public void setSplitList(List<SplitArgs> splitList) {
        this.splitList = splitList;
    }

    public Map<String, String> getExtendParam() {
        return extendParam;
    }

    public void setExtendParam(Map<String, String> extendParam) {
        this.extendParam = extendParam;
    }

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RefundTradeDto [outTradeNo=");
		builder.append(outTradeNo);
		builder.append(", origOuterTradeNo=");
		builder.append(origOuterTradeNo);
		builder.append(", refundAmount=");
		builder.append(refundAmount);
		builder.append(", summary=");
		builder.append(summary);
		builder.append(", userIp=");
		builder.append(userIp);
		builder.append(", splitList=");
		builder.append(splitList);
		builder.append(", extendParam=");
		builder.append(extendParam);
		builder.append("]");
		return builder.toString();
	}
}
