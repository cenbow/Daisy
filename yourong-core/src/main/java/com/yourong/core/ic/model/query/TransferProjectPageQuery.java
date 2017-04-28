package com.yourong.core.ic.model.query;

import com.yourong.common.domain.AbstractBaseObject;

import java.util.Date;
import java.util.List;

/**
 * Created by XR on 2016/12/14.
 */
public class TransferProjectPageQuery extends AbstractBaseObject {
    /**
     * 转让项目编号
     */
    private Long id;
    /**
     * 转让项目名称
     */
    private String transferName;
    /**
     * 转让人
     */
    private String transferMember;
    /**状态（30：投资中，50：已满额，51：转让中，52：已转让,70：已还款,80：流标中,90：流标,转让失败）**/
    private List<Integer> status;
    /**
     * 原始项目名称
     */
    private String projectName;
    /**
     * 原始借款人
     */
    private String borrowName;
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
    
    /**
     * 转让结束时间--起始时间(用于前台查询)
     */
    private Date transferEndDateStart;
    
    /**
     *  转让结束时间--截止时间(用于前台查询)
     */
    private Date transferEndDateEnd;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransferName() {
        return transferName;
    }

    public void setTransferName(String transferName) {
        this.transferName = transferName;
    }

    public String getTransferMember() {
        return transferMember;
    }

    public void setTransferMember(String transferMember) {
        this.transferMember = transferMember;
    }

    public List<Integer> getStatus() {
        return status;
    }

    public void setStatus(List<Integer> status) {
        this.status = status;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getBorrowName() {
        return borrowName;
    }

    public void setBorrowName(String borrowName) {
        this.borrowName = borrowName;
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
    
	public Date getTransferEndDateStart() {
		return transferEndDateStart;
	}

	public void setTransferEndDateStart(Date transferEndDateStart) {
		this.transferEndDateStart = transferEndDateStart;
	}

	public Date getTransferEndDateEnd() {
		return transferEndDateEnd;
	}

	public void setTransferEndDateEnd(Date transferEndDateEnd) {
		this.transferEndDateEnd = transferEndDateEnd;
	}
}
