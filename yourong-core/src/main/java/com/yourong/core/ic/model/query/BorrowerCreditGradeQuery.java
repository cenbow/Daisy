package com.yourong.core.ic.model.query;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * Created by alan.zheng on 2017/3/1.
 */
public class BorrowerCreditGradeQuery extends AbstractBaseObject {
    /**
     * 商品类型
     */
    private Long borrowerId;
    /**
     * 借款人姓名
     */
    private String borrowerTrueName;
    /**
     * 综合等级
     */
    private String creditLevel;
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

    public Long getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(Long borrowerId) {
        this.borrowerId = borrowerId;
    }

    public String getBorrowerTrueName() {
        return borrowerTrueName;
    }

    public void setBorrowerTrueName(String borrowerTrueName) {
        this.borrowerTrueName = borrowerTrueName;
    }

    public String getCreditLevel() {
        return creditLevel;
    }

    public void setCreditLevel(String creditLevel) {
        this.creditLevel = creditLevel;
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

    public Integer getLimitSize() {
        return limitSize;
    }

    public void setLimitSize(Integer limitSize) {
        this.limitSize = limitSize;
    }
}
