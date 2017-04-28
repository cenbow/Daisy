package com.yourong.common.thirdparty.sinapay.member.domain.result;

import java.util.List;

/**
 * <p>资金明细</p>
 * @author Wallis Wang
 * @version $Id: AccountDetail.java, v 0.1 2014年6月5日 下午3:18:55 wangqiang Exp $
 */
public class AccountDetail {

    /**
     * 收支明细列表 
     */
    private List<StatementEntry> detailList;

    /**
     * 总收入
     */
    private String               totalIncome;

    /**
     * 总支出
     */
    private String               totalPayout;

    public List<StatementEntry> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<StatementEntry> detailList) {
        this.detailList = detailList;
    }

    public String getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(String totalIncome) {
        this.totalIncome = totalIncome;
    }

    public String getTotalPayout() {
        return totalPayout;
    }

    public void setTotalPayout(String totalPayout) {
        this.totalPayout = totalPayout;
    }
}
