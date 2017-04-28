package com.yourong.core.sh.model.query;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.domain.BaseQueryParam;

import java.util.Date;

/**
 * Created by XR on 2016/10/21.
 */
public class ShopOrderQuery extends AbstractBaseObject {
    /**
     * 用户姓名
     */
    private String trueName;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 商品类型
     */
    private Integer goodsType;
    /**
     * 兑换查询开始时间
     */
    private Date startTime;
    /**
     * 兑换查询结束时间
     */
    private Date endTime;
    /**
     * 商品名称
     */
    private String goodName;
    /**
     * 收货人联系方式
     */
    private String takeMobile;
    /**
     * 订单状态
     */
    private Integer status;
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

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(Integer goodsType) {
        this.goodsType = goodsType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public String getTakeMobile() {
        return takeMobile;
    }

    public void setTakeMobile(String takeMobile) {
        this.takeMobile = takeMobile;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
