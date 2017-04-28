package com.yourong.common.thirdparty.sinapay.pay.domain.dto;

/**
 * <p>分页查询</p>
 * @author Wallis Wang
 * @version $Id: PageQuery.java, v 0.1 2014年5月26日 上午11:15:15 wangqiang Exp $
 */
public class PageQuery extends RequestDto {

    /**
     * 
     */
    private static final long serialVersionUID = 4020264840958310114L;

    /**
     * 页号
     * <p>页号，从1开始，默认为1</p>
     */
    private int pageNo;

    /**
     * 每页大小
     * <p>每页记录数，默认20</p>
     */
    private int pageSize;

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PageQuery [pageNo=");
		builder.append(pageNo);
		builder.append(", pageSize=");
		builder.append(pageSize);
		builder.append("]");
		return builder.toString();
	}

}
