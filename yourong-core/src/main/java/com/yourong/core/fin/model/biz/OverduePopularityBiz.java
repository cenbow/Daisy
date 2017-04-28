package com.yourong.core.fin.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by alan.zheng on 2017/2/20.
 */
public class OverduePopularityBiz extends AbstractBaseObject {
    /**
     * 过期人气值数值
     */
    private Integer overduePopularity;
    /**
     * 是否显示
     */
    private boolean show;
    /**
     * 是否显示规则
     */
    private boolean showRule;
    /**
     * 获得时间
     */
    private Date incomeTime;
    /**
     * 支出时间
     */
    private Date outlayTime;

    public Integer getOverduePopularity() {
        return overduePopularity;
    }

    public void setOverduePopularity(Integer overduePopularity) {
        this.overduePopularity = overduePopularity;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public boolean isShowRule() {
        return showRule;
    }

    public void setShowRule(boolean showRule) {
        this.showRule = showRule;
    }

    public Date getIncomeTime() {
        return incomeTime;
    }

    public void setIncomeTime(Date incomeTime) {
        this.incomeTime = incomeTime;
    }

    public Date getOutlayTime() {
        return outlayTime;
    }

    public void setOutlayTime(Date outlayTime) {
        this.outlayTime = outlayTime;
    }
}
