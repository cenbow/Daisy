package com.yourong.common.thirdparty.sinapay.pay.domain.result;

import java.io.Serializable;

/**
 * <p>分页查询</p>
 * @author Your name
 * @version $Id: PageQuery.java, v 0.1 2014年5月7日 下午5:51:55 wangqiang Exp $
 */
public class PageResult implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1457593751290477250L;

    /**
     * 页号
     */
    private int               pageNo;

    /**
     * 每页大小
     */
    private int               pageSize;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
