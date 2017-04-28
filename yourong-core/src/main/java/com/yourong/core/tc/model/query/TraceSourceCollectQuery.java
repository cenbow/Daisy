package com.yourong.core.tc.model.query;

import java.util.Date;
import java.util.List;

/**
 * Created by XR on 2016/9/13.
 */
public class TraceSourceCollectQuery {
    /**
     * 注册来源码
     */
    private String channelname;
    /**
     *注册来源码编号
     */
    private List<String> channeltrack;
    /**
     * 手机号
     */
    private String mobile;

    /**
     * 投资开始时间
     */
    private Date startdate;

    /**
     * 投资结束时间
     */
    private Date enddate;

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

    public String getChannelname() {
        return channelname;
    }

    public void setChannelname(String channelname) {
        this.channelname = channelname;
    }

    public List<String> getChanneltrack() {
        return channeltrack;
    }

    public void setChanneltrack(List<String> channeltrack) {
        this.channeltrack = channeltrack;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
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
