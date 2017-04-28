package com.yourong.common.thirdparty.sinapay.pay.core.common;

/**
 * <p>一些常用的常量</p>
 * @author Wallis Wang
 * @version $Id: Constants.java, v 0.1 2014年5月16日 下午2:41:04 wangqiang Exp $
 */
public class Constants {

    public static final String NOTIFY_PROCESS_SUCCESS          = "success";

    public static final String NOTIFY_PROCESS_FAILED           = "failed";

    public static final String FORM_REQEUST_METHOD             = "post";

    public static final String FORM_ELEMENT_OUTER              = "<form></form>";

    public static final String ANGLE_BRACKETS                  = "^";

    public static final String UNDER_LINE                      = "_";

    public static final String VERTICAL_LINE                   = "|";

    public static final String DOLLAR                          = "$";

    public static final String WAVY_LINES                      = "~";

    public static final String COMMA                           = ",";
    
    // 充值关闭时间
    public static final String DEPOSIT_CLOSE_TIME              = "12h";
    
    // 提现关闭时间
    public static final String WITHDRAW_CLOSE_TIME             = "15m";
    
    // 提现方式
    public static final String WITHDRAW_MODE                   = "CASHDESK";

    // 代收接口
    public static final String CREATE_HOSTING_COLLECT_TRADE    = "create_hosting_collect_trade";

	// 代收完成接口
	public static final String FINISH_PRE_AUTH_TRADE           = "finish_pre_auth_trade";

	// 代收撤销接口
	public static final String CANCEL_PRE_AUTH_TRADE           = "cancel_pre_auth_trade";

    // 单笔代付
    public static final String CREATE_SINGLE_HOSTING_PAY_TRADE = "create_single_hosting_pay_trade";

    // 批量代付
    public static final String CREATE_BATCH_HOSTING_PAY_TRADE  = "create_batch_hosting_pay_trade";

    // 支付
    public static final String PAY_HOSTING_TRADE               = "pay_hosting_trade";

    // 退款
    public static final String REFUND_HOSTING_TRADE            = "create_hosting_refund";

    // 交易查询
    public static final String QUERY_HOSTING_TRADE             = "query_hosting_trade";

    // 充值
    public static final String CREATE_HOSTING_DEPOSIT          = "create_hosting_deposit";

    // 支付查询
    public static final String QUERY_PAY_RESULT                = "query_pay_result";

    // 充值查询
    public static final String QUERY_HOSTING_DEPOSIT           = "query_hosting_deposit";

    // 提现
    public static final String CREATE_HOSTING_WITHDRAW         = "create_hosting_withdraw";

    // 提现查询
    public static final String QUERY_HOSTING_WITHDRAW          = "query_hosting_withdraw";

    // 退款查询
    public static final String QUERY_REFUND                    = "query_hosting_refund";
    
    //查询基金股份
    public static final String QUERY_FUND_YIELD      = "query_fund_yield";

    //advance_hosting_pay
    public  static  final  String ADVANCE_HOSTING_PAY = "advance_hosting_pay";

    //托管交易批次查询
    public  static  final  String QUERY_HOSTING_BATCH_TRADE = "query_hosting_batch_trade";

}
