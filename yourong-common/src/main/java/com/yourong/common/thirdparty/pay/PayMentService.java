package com.yourong.common.thirdparty.pay;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付接口
 * @author Administrator
 *
 */
public interface PayMentService {
    /**
     * 创建激活会员
     * @param name 会员ID 字母或数字 (非空)
     * @param memberType 会员类型(非空)
     * @param clientIp 请求会员IP
     * @param map 扩展信息  (空)
     * @return
     */
    PayResponse createActivateMember(String name ,String memberType, String clientIp, Map<String,String> map);
    /**
     * 设置实名信息
     * @return
     */   
     public PayResponse setRealName(String memberid,String realName,String certificate,String certificateNumber);       
       /***
        *  绑定认证信息 
        * @param memberid 会员ID (非空)
        * @param verifyType 认证类型 (非空)
        * @param verifyEntity 认证内容 (非空)
        * @param map  扩展信息 (可空)
        * @return 
        */
    public  PayResponse  bindingVerify(String memberid,String verifyType,String verifyEntity,Map<String,String> map);
    /**
     *  解除认证信息
     * @param memberid 会员ID (非空)
     * @param verifyType 认证类型 (非空)
     * @return
     */
    public PayResponse  unbindingVerify(String memberid,String verifyType);  
    /**
     * 
     * @param memberid 会员ID (非空)
     * @param verifyType 认证类型 (非空)
     * @param isMask 是否掩码
     * @return
     */
    public  VerifyEntityResponse  queryVerify(String memberid,String verifyType, String isMask);
    /**
     * 查询绑定银行卡
     * @return
     */
    public CarListResponse  queryBankCard(String name);
    /***
     *  查询余额
     * @param memberid
     * @return
     */    
    public BalancePayResponse queryBalance(String memberid);
    /**
     * 绑定银行卡
     * @return
     */   
    public BlanCadeResponse  bindingBankCard(String memberid, String bank_code,
    		String bank_account_no,String account_name);
        
    /**
     * 解除已绑定银行卡
     * @return
     */   
    public PayResponse  unBindingBankCard(String memberid, 	String card_id);
    
    
    /**
     * 
     * @return
     */
    public PayResponse balanceFreeze(String outFreezeNo,String memberid); 
    
    
       
    /**
     * 交易订单的 方法
     * @return
     */    
    public  PayResponse  createHostingCollectTrade(
    		String outTradeNo,
    		String outTradeCode,
    		String summary,
    		String tradeCloseTime,
    		String payerId,
    		String payerIdentityType,
    		String payMethod
    		);
 
   
    
//    PayResponse  create_single_hosting_pay_trade();
//    
//    PayResponse  create_batch_hosting_pay_trade();
//    
//    PayResponse  pay_hosting_trade();
//    
//    PayResponse  query_pay_result();
//    
//    PayResponse  query_hosting_trade();
//    
//    PayResponse  query_hosting_refund();
//    
//    PayResponse  create_hosting_deposit();
//    
//    PayResponse  query_hosting_deposit();
//    

//    
//    PayResponse  query_hosting_withdraw();
//   
    /**
     * 会员充值的一些基本参数 (参数不能为空)
     * @param outTradeNo 交易订单号		
     * @param memberid  会员ID
     * @param amount   金额     * 
     * @param payMethod 支付方式
     * @param blankCode 银行代码
     * @param returnUrl 返回页面
     * @param notifyUrl 通知页面
     * 
     * @return
     */
    public Map<?, ?> hostingDepositBaseParam(String outTradeNo,String memberid,
	    BigDecimal amount,String payMethod ,String blankCode,
	    String returnUrl ,String notifyUrl);
  
    /**
     * 会员提现接口
     * @param out_trade_no 交易订单号
     * @param identity_id   会员ID
     * @param account_type 收款人账号类型	
     * @param amount  金额 
     * @param card_id 银行卡号 已经绑定的银行卡
     * @return
     */
    public PayResponse  createHostingWithdraw(String out_trade_no,String identity_id,String account_type,String amount,String card_id,String notifyUrl);
    
}
