package com.yourong.common.thirdparty.sinapay.pay.domain.dto;

import java.util.List;
import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.pay.core.common.Constants;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.BankPayArgs;

/**
 * <p>支付DTO</p>
 * <pre>如果交易状态是“等待付款”的情况下支付接口才执行成功，否则将失败</pre>
 * @author Wallis Wang
 * @version $Id: PayTradeDto.java, v 0.1 2014年5月21日 下午1:11:34 wangqiang Exp $
 */
public class PayTradeDto extends RequestDto {

    /**
     * 接口
     * 必填  (调用方不用写)
     */
    @NotNull
    private String              service = Constants.PAY_HOSTING_TRADE;

    /**
     *支付请求号 
     *必填
     *<p>商户网站支付订单号，商户内部保证唯一</p>
     */
    @NotNull
    private String              outPayNo;

    /**
     * 商户网站唯一交易订单号集合
     * 必填
     * <p>钱包合作商户网站唯一订单号（确保在合作伙伴系统中唯一）。总数不超过十笔</p>
     */
    @NotNull
    private List<String>        outerTradeNoList;

    /**
     * 扩展信息
     */
    private Map<String, String> extendParam;

    /**
     * 付款用户ip地址
     * <p>用户在商户平台发起支付时候的IP地址，公网IP，不是内网IP</p>
     */
    private String              payerIp;

    /**
     * 支付方式 
     * 必填
     */
    @NotNull
	private BankPayArgs payMethod;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getOutPayNo() {
        return outPayNo;
    }

    public void setOutPayNo(String outPayNo) {
        this.outPayNo = outPayNo;
    }

    public List<String> getOuterTradeNoList() {
        return outerTradeNoList;
    }

    public void setOuterTradeNoList(List<String> outerTradeNoList) {
        this.outerTradeNoList = outerTradeNoList;
    }

    public Map<String, String> getExtendParam() {
        return extendParam;
    }

    public void setExtendParam(Map<String, String> extendParam) {
        this.extendParam = extendParam;
    }

    public String getPayerIp() {
        return payerIp;
    }

    public void setPayerIp(String payerIp) {
        this.payerIp = payerIp;
    }

	public BankPayArgs getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(BankPayArgs payMethod) {
		this.payMethod = payMethod;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PayTradeDto [service=");
		builder.append(service);
		builder.append(", outPayNo=");
		builder.append(outPayNo);
		builder.append(", outerTradeNoList=");
		builder.append(outerTradeNoList);
		builder.append(", extendParam=");
		builder.append(extendParam);
		builder.append(", payerIp=");
		builder.append(payerIp);
		builder.append(", payMethod=");
		builder.append(payMethod);
		builder.append("]");
		return builder.toString();
	}

}
