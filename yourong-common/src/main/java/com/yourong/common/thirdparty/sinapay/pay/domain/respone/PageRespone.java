package com.yourong.common.thirdparty.sinapay.pay.domain.respone;

/**
 * <p>分页查询</p>
 * @author Your name
 * @version $Id: PageQuery.java, v 0.1 2014年5月7日 下午5:51:55 wangqiang Exp $
 */
public class PageRespone extends PayResponse {

    /**
     * 页号
     */
    private String pageNo;

    /**
     * 每页大小
     */
    private String pageSize;

    /**
     * 总计录数
     */
    private String totalItem;

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(String totalItem) {
        this.totalItem = totalItem;
    }

}
