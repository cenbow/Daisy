package com.yourong.common.thirdparty.sinapay.pay.domain.dto;

import java.util.List;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.enums.BatchTradeNotifyMode;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeCode;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.TradeArgs;

/**
 * <p>批量代付DTO</p>
 * @author Wallis Wang
 * @version $Id: BatchHostPayTrade.java, v 0.1 2014年5月21日 上午11:08:22 wangqiang Exp $
 */
public class CreateBatchPayTradeDto extends RequestDto {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2400497901214784316L;
	/**
     * 支付请求号
     * <p>商户网站支付订单号，商户内部保证唯一</p>
     * 必填
     */
    @NotNull
    private String          outPayNo;
    /**
     * 交易码
     * 要求批量交易的交易码必须统一，发起时会使用该交易码而不是每个交易订单内独立的交易码
     * 必填
     */
    @NotNull
    private TradeCode       outTradeCode;

    /**
     * 支付交易清单
     * 必填
     */
    @NotNull
    private List<TradeArgs> tradeList;

    /**
     * 摘要信息。
     * 必填
     */
    @NotNull
    private String          summary;
    
    @NotNull
    private String          userIp;
    
    /**
     * 通知方式
     *	single_notify: 交易逐笔通知
	 *	batch_notify: 批量通知
     */
    private BatchTradeNotifyMode notifyMethod;

    public String getOutPayNo() {
        return outPayNo;
    }

    public void setOutPayNo(String outPayNo) {
        this.outPayNo = outPayNo;
    }

    public TradeCode getOutTradeCode() {
        return outTradeCode;
    }

    public void setOutTradeCode(TradeCode outTradeCode) {
        this.outTradeCode = outTradeCode;
    }

    public List<TradeArgs> getTradeList() {
        return tradeList;
    }

    public void setTradeList(List<TradeArgs> tradeList) {
        this.tradeList = tradeList;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public BatchTradeNotifyMode getNotifyMethod() {
    	return notifyMethod;
    }
    
    public void setNotifyMethod(BatchTradeNotifyMode batchNotify) {
    	this.notifyMethod = batchNotify;
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
		builder.append("CreateBatchPayTradeDto [outPayNo=");
		builder.append(outPayNo);
		builder.append(", outTradeCode=");
		builder.append(outTradeCode);
		builder.append(", tradeList=");
		builder.append(tradeList);
		builder.append(", summary=");
		builder.append(summary);
		builder.append(", userIp=");
		builder.append(userIp);
		builder.append(", notifyMethod=");
		builder.append(notifyMethod);
		builder.append("]");
		return builder.toString();
	}


}
