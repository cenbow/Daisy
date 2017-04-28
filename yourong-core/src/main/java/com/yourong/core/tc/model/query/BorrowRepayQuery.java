package com.yourong.core.tc.model.query;

import java.util.Date;

/**
 * Created by XR on 2016/9/10.
 */
public class BorrowRepayQuery {
    private String borrowName;
    private String borrowMobile;
    private Long borrowId;
    private Date endDate;

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

    public String getBorrowName() {
        return borrowName;
    }

    public void setBorrowName(String borrowName) {
        this.borrowName = borrowName;
    }

    public String getBorrowMobile() {
        return borrowMobile;
    }

    public void setBorrowMobile(String borrowMobile) {
        this.borrowMobile = borrowMobile;
    }

    public Long getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(Long borrowId) {
        this.borrowId = borrowId;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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
}
