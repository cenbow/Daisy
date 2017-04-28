package com.yourong.common.thirdparty.sinapay.common.enums;

/**
 * <p>会员类型</p>
 * @author Wallis Wang
 * @version $Id: MemberType.java, v 0.1 2014年6月6日 下午3:24:48 wangqiang Exp $
 */
public enum MemberType {

    /**
     * 个人
     */
    PERSONAL(1),
    /**
     * 企业
     */
    ENTERPRISE(2);

    private int type;

    private MemberType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
