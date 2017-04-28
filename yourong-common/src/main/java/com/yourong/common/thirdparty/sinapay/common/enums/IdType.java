package com.yourong.common.thirdparty.sinapay.common.enums;

/**
 * <p>账户ID类型</p>
 * @author Wallis Wang
 * @version $Id: IdType.java, v 0.1 2014年5月14日 上午11:49:03 wangqiang Exp $
 */
public enum IdType {
    /**
     * 商户用户ID
     */
    UID,
    /**
     * 钱包绑定手机号
     */
    MOBILE,
    /**
     * 钱包绑定邮箱
     */
    EMAIL,
    /**
     * 平台类型 ，余额查询  
     */
    MEMBER_ID;
}