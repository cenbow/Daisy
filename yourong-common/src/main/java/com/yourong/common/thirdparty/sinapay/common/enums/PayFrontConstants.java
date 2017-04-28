package com.yourong.common.thirdparty.sinapay.common.enums;

public class PayFrontConstants {

	/**
     * 代收-其它（如需要使用，请联系运营申请，新浪支付将会开设单独的中间账户）
     */
    public static final String COLLECTION_OTHER = "1000";
    /**
     * 代收投资金
     */
    public static final String COLLECTION_INVESTMENT = "1001";

    /**
     * 代收还款金
     */
    public static final String COLLECTION_REPAYMENT  = "1002";

    /**
     * 代付-其他（如需要使用，请联系运营申请，新浪支付将会开设单独的中间账户）
     */
    public static final String PAY_OTHER              = "2000";
    /**
     * 代付借款金
     */
    public static final String PAY_LOAN              = "2001";

    /**
     * 代付（本金/收益）金
     */
    public static final String PAY_PRINCIPAL_INCOME  = "2002";
}
