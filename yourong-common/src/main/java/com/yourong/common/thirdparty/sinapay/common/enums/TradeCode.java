package com.yourong.common.thirdparty.sinapay.common.enums;

/**
 * 交易业务码
 * 用于收款/付款时映射到第三方支付机构交易码
 * Created by Pandazki on 5/1/14.
 */
public enum TradeCode {

    //收款相关
    /**
     * 收 投资人 投资款
     */
    COLLECT_FROM_INVESTOR(PayFrontConstants.COLLECTION_INVESTMENT),
    /**
     * 收 平台 现金券
     */
    COLLECT_FROM_PLATFORM_CASHCOUPON(PayFrontConstants.COLLECTION_INVESTMENT), 
    /**
     * 收 借款人（出借人） 还款
     */
    COLLECT_FROM_BORROWER(PayFrontConstants.COLLECTION_REPAYMENT),
    /**
     * 收 平台 收益券
     */
    COLLECT_FROM_PLATFORM_PROFITCOUPON(PayFrontConstants.COLLECTION_REPAYMENT),
    /**
     * 收 借款人 逾期还款
     */
    COLLECT_FROM_BORROWER_FOR_OVERDUE_REPAY(PayFrontConstants.COLLECTION_REPAYMENT),
    /**
     * 收  租赁分红
     */
    COLLECT_FROM_PLATFORM_FOR_LEASEBONUS(PayFrontConstants.COLLECTION_REPAYMENT),
    /**
     * 收  平台 保证金
     */
    COLLECT_FROM_PLATFORM_FOR_GUARANTEEFEE(PayFrontConstants.COLLECTION_REPAYMENT),
    
    
    //付款相关
    /**
     * 付 借款人(出借人)借款  （即放款给借款人（出借人），批付时包括平台管理费）
     */
    PAY_TO_BORROWER(PayFrontConstants.PAY_LOAN),
    /**
     * 付 平台 管理费（放款时部分作为管理费）
     */
    PAY_TO_PLATFORM_FOR_MANAGERFEE(PayFrontConstants.PAY_LOAN),
    /**
     * 投资专用账户直接代付业务
     */
    PAY_FOR_DIRECT_FROM_INVEST_ACCOUNT(PayFrontConstants.PAY_LOAN),
    /**
     * 付 投资人 收益（包含本金、利息）
     */
    PAY_TO_INVESTOR(PayFrontConstants.PAY_PRINCIPAL_INCOME),
    /**
     * 付 垫资人 (包含垫付本金、利息、滞纳金）
     */
    PAY_TO_UNDERWRITER(PayFrontConstants.PAY_PRINCIPAL_INCOME),
    /**
     * 付 投资人 租赁分红
     */
    PAY_TO_INVESTOR_FOR_LEASEBONUS(PayFrontConstants.PAY_PRINCIPAL_INCOME),
    /**
     * 还款专用账户直接代付业务
     */
    PAY_FOR_DIRECT_FROM_REPAYMENT_ACCOUNT(PayFrontConstants.PAY_PRINCIPAL_INCOME),
    /**
     * 付 借款人 项目保证金
     */
	PAY_TO_BORROWER_FOR_GUARANTEE_FEE(PayFrontConstants.PAY_PRINCIPAL_INCOME),
	/**
	 * 付 转让项目收款
	 */
	PAY_TO_TRANSFER_FOR_TRANSACTION_AMOUNT(PayFrontConstants.PAY_LOAN);

    /**
     * 外部业务码
     */
    private String bizCode;

    private TradeCode(String bizCode) {
        this.bizCode = bizCode;
    }

    public String bizCode() {
        return bizCode;
    }

    public static TradeCode ofBizCode(String bizCode) {
        TradeCode[] codes = TradeCode.values();
        for (TradeCode code : codes) {
            if (code.bizCode().equals(bizCode)) {
                return code;
            }
        }
        return null;
    }

}
