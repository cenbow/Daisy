package com.yourong.common.thirdparty.sinapay.pay.domain.dto;

import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeCode;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.PayArgsBase;

/**
 * <p>代收</p>
 * @author Wallis Wang
 * @version $Id: CreateCollectTradeDto.java, v 0.1 2014年5月15日 下午7:23:07 wangqiang Exp $
 */
public class CreateCollectTradeDto extends RequestDto {

    /**
     * 
     */
    private static final long   serialVersionUID = 1364822578781423159L;

    /**
     * 房金所系统交易订单号，对于外部必须唯一。
     * <p>商户网站交易订单号，商户内部保证唯一</p>
     * 必填
     */
    @NotNull
    private String              outTradeNo;

    /**
     * 交易码
     * <p>商户网站代收交易业务码</p>
     */
    private TradeCode           outTradeCode;

    /**
     * 摘要信息。
     * 必填
     */
    @NotNull
    private String              summary;

    /**
     * 交易关闭时间
     * <pre>
     * 1、设置未付款交易的超时时间,一旦超时，该笔交易就会自动被关闭。
     * 2、取值范围：1m～15d m-分钟，h-小时，d-天，不接受小数点，如1.5d，可转换为36h
     * </pre>
     */
    private String              tradeCloseTime;

    /**
     * 扩展信息
     */
    private Map<String, String> extendParam;

    /**
     * 付款用户ID
     * 必填
     */
    @NotNull
    private String              payerId;

    /**
     * 标识类型
     * 必填
     */
    @NotNull
    private IdType              payerIdentityType;

    /**
     * 付款用户ip地址
     */
    private String              payerIp;

    /**
     * 收款时，用户支付参数
     * <p>只支持网银支付、余额、快捷、线下支付</p>
     * 必填
     */
    @NotNull
    private PayArgsBase         payMethod;

	/**
	 * 代收冻结交易传pre_auth，其它场景不传该参数
	 */
	private String collectTradeType;

	/**
	 * 支付失败后是否可以再次支付
	 */
	private String canRepayOnFailed;

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public TradeCode getOutTradeCode() {
        return outTradeCode;
    }

    public void setOutTradeCode(TradeCode outTradeCode) {
        this.outTradeCode = outTradeCode;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTradeCloseTime() {
        return tradeCloseTime;
    }

    public void setTradeCloseTime(String tradeCloseTime) {
        this.tradeCloseTime = tradeCloseTime;
    }

    public Map<String, String> getExtendParam() {
        return extendParam;
    }

    public void setExtendParam(Map<String, String> extendParam) {
        this.extendParam = extendParam;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public IdType getPayerIdentityType() {
        return payerIdentityType;
    }

    public void setPayerIdentityType(IdType payerIdentityType) {
        this.payerIdentityType = payerIdentityType;
    }

    public String getPayerIp() {
        return payerIp;
    }

    public void setPayerIp(String payerIp) {
        this.payerIp = payerIp;
    }

    public PayArgsBase getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(PayArgsBase payMethod) {
        this.payMethod = payMethod;
    }

	public String getCollectTradeType() {
		return collectTradeType;
	}

	public void setCollectTradeType(String collectTradeType) {
		this.collectTradeType = collectTradeType;
	}

	public String getCanRepayOnFailed() {
		return canRepayOnFailed;
	}

	public void setCanRepayOnFailed(String canRepayOnFailed) {
		this.canRepayOnFailed = canRepayOnFailed;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CreateCollectTradeDto [outTradeNo=");
		builder.append(outTradeNo);
		builder.append(", outTradeCode=");
		builder.append(outTradeCode);
		builder.append(", summary=");
		builder.append(summary);
		builder.append(", tradeCloseTime=");
		builder.append(tradeCloseTime);
		builder.append(", extendParam=");
		builder.append(extendParam);
		builder.append(", payerId=");
		builder.append(payerId);
		builder.append(", payerIdentityType=");
		builder.append(payerIdentityType);
		builder.append(", payerIp=");
		builder.append(payerIp);
		builder.append(", payMethod=");
		builder.append(payMethod);
		builder.append(", collectTradeType=");
		builder.append(collectTradeType);
		builder.append(", canRepayOnFailed=");
		builder.append(canRepayOnFailed);
		builder.append("]");
		return builder.toString();
	}
}
