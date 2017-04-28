package com.yourong.common.thirdparty.sinapay.member.common;

/**
 * <p>一些常量</p>
 * @author Wallis Wang
 * @version $Id: Constants.java, v 0.1 2014年6月6日 下午4:19:15 wangqiang Exp $
 */
public class Constants {

    public static final String ANGLE_BRACKETS                = "^";

    public static final String UNDER_LINE                    = "_";

    public static final String VERTICAL_LINE                 = "|";

    public static final String DOLLAR                        = "$";

    public static final String WAVY_LINES                    = "~";

    public static final String COMMA                         = ",";

    //默认接口版本
    public static final String DEFAULT_SERVICE_VERSION       = "1.0";

    //默认签名版本号
    public static final String DEFAULT_SIGN_VERSION          = "1.0";

    //加密版本号
    public static final String DEFAULT_ENCRYPT_VERSION       = "1.0";

    //创建激活会员 
    public static final String SERVICE_CREATE_ACTIVE_MEMBER  = "create_activate_member";

    //设置实名信息
    public static final String SERVICE_SET_REAL_NAME         = "set_real_name";

    //绑定银行卡
    public static final String SERVICE_BINDING_BANK_CARD     = "binding_bank_card";
    //绑定银行卡 推进,验证 短信
    public static final String SERVICE_BINDING_BANK_ADVICE_CARD     = "binding_bank_card_advance";

    //解绑银行卡
    public static final String SERVICE_UNBINDING_BANK_CARD   = "unbinding_bank_card";

    //查询银行卡
    public static final String SERVICE_QUERY_BANK_CARD       = "query_bank_card";

    //查询余额
    public static final String SERVICE_QUERY_BALANCE         = "query_balance";

    //查询收支明细
    public static final String SERVICE_QUERY_ACCOUNT_DETAILS = "query_account_details";

    //冻结余额
    public static final String SERVICE_BALANCE_FREEZE        = "balance_freeze";

    //解冻余额
    public static final String SERVICE_BALANCE_UNFREEZE      = "balance_unfreeze";
   //认证信息
    public static final String SERVICE_BINDING_VERIFY      = "binding_verify";
    //解绑认证信息
    public static final String SERVICE_UNBINDING_VERIFY    = "unbinding_verify";
    
    //查询认证信息
    public static final String SERVICE_QUERY_VERIFY      = "query_verify";  

    //请求的MD5的KEY
    public static final String MD5_REQUEST_KEY               = "1234567890qwertyuiopasdfghjklzxc";

    //响应的MD5的KEY
    public static final String MD5_RESPONSE_KEY              = "1234567890qwertyuiopasdfghjklzxc";

    //会员网关地址
    public static final String MEMBER_URL                    = "http://10.65.209.29:8082/mgs/service.do";
    
    //认证不存在
    public static final String VERIFY_NOT_EXIST  = "VERIFY_NOT_EXIST";

    //查询基金股份
    public static final String QUERY_FUND_YIELD      = "query_fund_yield";

    //sina页面展示用户信息
	public static final String SHOW_MEMBER_INFOS_SINA = "show_member_infos_sina";
    
    //委托扣款重定向
    public static final String HANDLE_WITHHOLD_AUTHORITY  = "handle_withhold_authority";
    
    //修改委托扣款重定向
    public static final String MODIFY_WITHHOLD_AUTHORITY  = "modify_withhold_authority";
    
    //解除委托扣款重定向
    public static final String RELIEVE_WITHHOLD_AUTHORITY  = "relieve_withhold_authority";
    
    //查看用户是否委托扣款
    public static final String QUERY_WITHHOLD_AUTHORITY  = "query_withhold_authority";
    
    //新浪委托扣款授权，单笔无限额
    public static final String WITHHOLD_AUTHORITY_QUOTA = "++";
    
    //新浪委托扣款授权，日累计无限额
    public static final String WITHHOLD_AUTHORITY_DAYQUOTA = "++";

	// 设置支付密码
	public static final String SET_PAY_PASSWORD = "set_pay_password";

	// 修改支付密码
	public static final String MODIFY_PAY_PASSWORD = "modify_pay_password";

	// 找回支付密码
	public static final String FIND_PAY_PASSWORD = "find_pay_password";

	// 查询是否设置支付密码
	public static final String QUERY_IS_SET_PAY_PASSWORD = "query_is_set_pay_password";
	
	//查询冻结、解冻结果
	public static final String QUERY_CTRL_RESULT = "query_ctrl_result";
	
	//资产包五重礼限制
    public static final String PACKAGE_FIVE_AMOUNT_LIMIT = "package_five_amount_limit";
		
		

}
