package com.yourong.common.thirdparty.sinapay.common.enums;

/**
 * <p>charset类型</p>
 * @author Wallis Wang
 * @version $Id: CharsetType.java, v 0.1 2014年5月13日 下午3:06:21 wangqiang Exp $
 */
public enum CharsetType {

    UTF8("utf-8"), GBK("gbk"), GB2312("gb2312");

    private String charset;

    private CharsetType(String charset) {
        this.charset = charset;
    }

    public String charset() {
        return charset;
    }
}
