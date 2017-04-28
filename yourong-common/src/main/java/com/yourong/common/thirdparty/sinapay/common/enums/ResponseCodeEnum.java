package com.yourong.common.thirdparty.sinapay.common.enums;

/**
 * <p>响应码</p>
 * @author Wallis Wang
 * @version $Id: ResponseCodeEnum.java, v 0.1 2014年6月26日 下午4:53:06 wangqiang Exp $
 */
public enum ResponseCodeEnum {
    
    /**
     * 	提交成功，存在业务响应的以业务响应状态为准
     */
    APPLY_SUCCESS,
    /**
     * 	商户该接口授权已过期
     */
    AUTH_INVALID_DATE,
    /**
     * 	添加认证信息失败
     */
    ADD_VERIFY_FAIL,
    /**
     * 	支付或绑卡推进失败
     */
    ADVANCE_FAILED,
    /**
     * 	银行卡信息不存在
     */
    BANK_ACCOUNT_NOT_EXISTS,
    /**
     * 	绑定银行卡数量超限
     */
    BANK_ACCOUNT_TOO_MANY,
    /**
     * 	银行卡未认证
     */
    BANK_CARD_NOT_VERIFIED,
    /**
     * 	暂不支持该银行
     */
    
    BANK_CODE_NOT_SUPPORT,
    /**
     * 	银行卡未生效
     */
    BANK_CARD_NOT_EFFECT,
    /**
     * 	银行卡未签约
     */
    BANK_CARD_NOT_SIGN,
    /**
     * 	银行卡信息校验失败
     */
    BANK_INFO_VERIFY_FAILED,
    /**
     * 	绑卡失败
     */
    BIND_CARD_FAILED,
    /**
     * 	业务处理中，等待通知或查询
     */
    BIZ_PENDING,
    /**
     * 	余额不足
     */
    BLANCE_NOT_ENOUGH,
    /**
     * 	卡类型暂不支持
     */
    CARD_TYPE_NOT_SUPPORT,
    /**
     * 	证件号不存在，请提前实名认证
     */
    CERT_NOT_EXIST,
    /**
     * 	证件号不匹配
     */
    CERTNO_NOT_MATCHING,
    /**
     * 	用户标识信息重复
     */
    DUPLICATE_IDENTITY_ID,
    /**
     * 	冻结订单号重复
     */
    DUPLICATE_OUT_FREEZE_NO,
    /**
     * 	解冻订单号重复
     */
    DUPLICATE_OUT_UNFREEZE_NO,
    /**
     * 	重复的请求号
     */
    DUPLICATE_REQUEST_NO,
    /**
     * 	会员认证信息重复
     */
    DUPLICATE_VERIFY,
    /**
     * 	冻结余额失败
     */
    FREEZE_FUND_FAIL,
    /**
     * 	冻结余额处理中，请联系管理员
     */
    FREEZE_FUND_PROCESSING,
    /**
     * 	查询认证信息失败
     */
    GET_VERIFY_FAIL,
    /**
     * 	代付交易不允许退款
     */
    HOST_PAY_NOT_SUPPORT_REFUND,
    /**
     * 	不允许访问该类型的接口
     */
    ILLEGAL_ACCESS_SWITCH_SYSTEM,
    /**
     * 	参数校验未通过
     */
    ILLEGAL_ARGUMENT,
    /**
     * 	解密失败，请检查加密字段
     */
    ILLEGAL_DECRYPT,
    /**
     * 	用户标识信息中不存在该平台标志
     */
    ILLEGAL_INDETITY_PALTFORMTYPE,
    /**
     * 	非法的商户IP或域名
     */
    ILLEGAL_IP_OR_DOMAIN,
    /**
     * 	交易订单号不存在
     */
    ILLEGAL_OUTER_TRADE_NO,
    /**
     * 	服务接口不存在
     */
    ILLEGAL_SERVICE,
    /**
     * 	验签未通过
     */
    ILLEGAL_SIGN,
    /**
     * 	签名类型不正确
     */
    ILLEGAL_SIGN_TYPE,
    /**
     * 	用户卡信息有误
     */
    INCORRECT_CARD_INFORMATION,
    /**
     * 	超过可冻结金额
     */
    INSUFFICIENT_FREEZE_BALANCE,
    /**
     * 	超过可解冻金额
     */
    INSUFFICIENT_UNFREEZE_BALANCE,
    /**
     * 	用户不存在
     */
    MEMBER_ID_NOT_EXIST,
    /**
     * 	用户标识信息不存在
     */
    MEMBER_NOT_EXIST,
    /**
     * 	无相关银行卡信息
     */
    NO_BANK_CARD_INFO,
    /**
     * 	用户无基本账户信息或没有激活
     */
    NO_BASIC_ACCOUNT,
    /**
     * 	原冻结交易不存在
     */
    NO_FUND_ORIG_FREEEZE_TRADE,
    /**
     * 	该商户信息不存在
     */
    NO_SUCH_MERCHANT,
    /**
     * 	订单不存在
     */
    ORDER_NOT_EXIST,
    /**
     * 	其它错误
     */
    OTHER_ERROR,
    /**
     * 	请求参数不合法
     */
    PARAMETER_INVALID,
    /**
     * 	合作方Id不存在
     */
    PARTNER_ID_NOT_EXIST,
    /**
     *  	支付方式不支持
     */
    PAY_METHOD_NOT_SUPPORT,
    /**
     * 	订单批量支付付款人信息不一致
     */
    PAYER_INCONSISTENT,
    /**
     * 	重复支付
     */
    PAYMENT_DUPLIDATE,
    /**
     * 	支付失败
     */
    PAY_FAILED,
    /**
     * 	实名认证不通过
     */
    REALNAME_CONFIRM_FAILED,
    /**
     * 	实名不匹配
     */
    REAL_NAME_NOT_MATCHING,
    /**
     * 	请求方式不合法
     */
    REQUEST_METHOD_NOT_VALIDATE,
    /**
     * 	存钱罐账户开户失败
     */
    SAVING_POT_ACCOUNT_OPEN_FAILED,
    /**
     * 	系统内部错误
     */
    SYSTEM_ERROR,
    /**
     * 	交易金额修改不合法
     */
    TRADE_AMOUNT_MODIFIED,
    /**
     * 	交易关闭
     */
    TRADE_CLOSED,
    /**
     * 	交易调用失败
     */
    TRADE_FAILED,
    /**
     * 	交易号信息有误
     */
    TRADE_NO_MATCH_ERROR,
    /**
     * 用户银行卡信息不匹配
     */
    USER_BANK_ACCOUNT_NOT_MATCH	,
    /**
     * 	认证信息不存在
     */
    VERIFY_NOT_EXIST,
    /**
     * 	认证信息绑定超限
     */
    VERIFY_BINDED_OVERRUN,
    /**
     * 	安全卡当余额及存钱罐为0时才可解绑
     */
    UNBINDING_SECURITY_CARD_FORBIDDING,
    /**
     * 业务校验异常
     */
    BIZ_CHECK_EXCEPTION	,
    /**
     * 	上传文件失败
     */
    UPLOAD_FILE_FAIL,
    /**
     * 	文件摘要验证失败
     */
    CHECK_FILE_DIGEST_FAIL,
    /**
     * 	账户信息不存在
     */
    ACCOUNT_NOT_EXIST,
    /**
     * 	授权失败
     */
    AUTHORIZE_FAIL,
    /**
     * 	构建商户基本信息失败
     */
    MERCHANT_BUILD_FAIL,
    /**
     * 	商户审核请求失败
     */
    MERCHANT_SUBMIT_AUDIT_FAIL,
    /**
     * 商户审核请求信息不合法
     */
    MERCHANT_AUDIT_REQ_IS_EMPTY	,
    /**
     * 	短时间内禁止重复请求
     */
    FORBIDDEN_REPEAT_REQUEST,
    /**
     * 	审核处理中，请不要重复提交
     */
    AUDIT_PROCESSING,
    /**
     * 	审核已通过，请不要重复提交
     */
    AUDIT_SUCCESS,
    /**
     * 	审核驳回，请重新提交信息
     */
    AUDIT_REFUSED,
    /**
     * 	无任何审核信息
     */
    AUDIT_NOTHING,
    /**
     * 	绑银行卡失败
     */
    BINDING_BANK_CARD_FAILD,
    /**
     * 	文件不存在
     */
    FILE_NOT_FOUND,
    /**
     * 	开户失败
     */
    MERCHANT_OPEN_ACCOUNT_FAIL,
    /**
     * 开户参数不合法
     */
    MERCHANT_OPEN_REQ_ERROR	,
    /**
     * 	参与方信息不合法
     */
    ILLEGAL_PARTY_INFO,
    /**
     * 	鉴权未通过
     */
    INSPECT_FILTER_FAIL,
    /**
     * 	鉴权安全卡未通过
     */
    INSPECT_FILTER_SAFECARD_FAIL,
    /**
     * 	查询是否安全卡支持错误
     */
    QUERY_IS_SAFECARD_SUPPORT_FAIL,
    /**
     * 	无权限解绑手机
     */
    VALIDATE_UNBIND_VERIFYMOBILE_FAIL,
    /**
     * 	验证码错误
     */
    VERIFY_CODE_WRONG;

}
