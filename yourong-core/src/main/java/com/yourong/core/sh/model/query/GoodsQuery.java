package com.yourong.core.sh.model.query;

/**
 * Created by XR on 2016/10/20.
 */
public class GoodsQuery {
    /**
     * 商品类型
     */
    private Integer goodsType;
    /**
     * 当前页码
     */
    private int currentPage = 1;
    /**
     * 查询起始行
     */
    private int startRow = 0;

    /**
     * 每页显示的条数
     */
    private int pageSize = 10;

    
    /** 查询数量 **/
	private Integer limitSize;
   
    public Integer getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(Integer goodsType) {
        this.goodsType = goodsType;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

	/**
	 * @return the limitSize
	 */
	public Integer getLimitSize() {
		return limitSize;
	}

	/**
	 * @param limitSize the limitSize to set
	 */
	public void setLimitSize(Integer limitSize) {
		this.limitSize = limitSize;
	}
    
}
