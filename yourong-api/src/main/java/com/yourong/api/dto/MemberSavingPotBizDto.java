package com.yourong.api.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.fin.model.biz.BonusBiz;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by py on 2015/4/1.
 */
public class MemberSavingPotBizDto extends AbstractBaseObject {

    /**
     * 存钱罐账户
     */
    private String sinaMemberId;

    /**
     * 存钱罐余额
     */
    private BigDecimal savingPotBalance;

    /**
     * 存钱罐可用余额
     */
    private BigDecimal savingPotavailableBalance;

    /**
     * 昨日收益
     */
    private BigDecimal totalBonus;

    /**
     * 昨日收益
     */
    private BigDecimal yesterdayBonus;

    /**
     * 近一周收益
     */
    private BigDecimal lastWeekBonus;

    /**
     * 近一月收益
     */
    private BigDecimal lastMonthBonus;

    /**
     * 近一周收益列表
     */
    @JSONField(serialize=false)
    List<BonusBiz> lastWeekBonusLists;

    /**
     * 近一月收益列表
     */
    @JSONField(serialize=false)
    List<BonusBiz> lastMonthBonusLists;
    
    /***可用余额**/
	private String popularityAvailableBalance;


    public String getSinaMemberId() {
        return sinaMemberId;
    }

    public void setSinaMemberId(String sinaMemberId) {
        this.sinaMemberId = sinaMemberId;
    }

    public BigDecimal getSavingPotBalance() {
        return savingPotBalance;
    }

    public void setSavingPotBalance(BigDecimal savingPotBalance) {
        this.savingPotBalance = savingPotBalance;
    }

    public BigDecimal getSavingPotavailableBalance() {
        return savingPotavailableBalance;
    }

    public void setSavingPotavailableBalance(BigDecimal savingPotavailableBalance) {
        this.savingPotavailableBalance = savingPotavailableBalance;
    }


    public BigDecimal getTotalBonus() {
        return totalBonus;
    }

    public void setTotalBonus(BigDecimal totalBonus) {
        this.totalBonus = totalBonus;
    }

    public BigDecimal getYesterdayBonus() {
        return yesterdayBonus;
    }

    public void setYesterdayBonus(BigDecimal yesterdayBonus) {
        this.yesterdayBonus = yesterdayBonus;
    }

    public BigDecimal getLastWeekBonus() {
        return lastWeekBonus;
    }

    public void setLastWeekBonus(BigDecimal lastWeekBonus) {
        this.lastWeekBonus = lastWeekBonus;
    }

    public BigDecimal getLastMonthBonus() {
        return lastMonthBonus;
    }

    public void setLastMonthBonus(BigDecimal lastMonthBonus) {
        this.lastMonthBonus = lastMonthBonus;
    }

    public List<BonusBiz> getLastWeekBonusLists() {
        return lastWeekBonusLists;
    }

    public void setLastWeekBonusLists(List<BonusBiz> lastWeekBonusLists) {
        this.lastWeekBonusLists = lastWeekBonusLists;
    }

    public List<BonusBiz> getLastMonthBonusLists() {
        return lastMonthBonusLists;
    }

    public void setLastMonthBonusLists(List<BonusBiz> lastMonthBonusLists) {
        this.lastMonthBonusLists = lastMonthBonusLists;
    }

	public String getPopularityAvailableBalance() {
		return popularityAvailableBalance;
	}

	public void setPopularityAvailableBalance(String popularityAvailableBalance) {
		this.popularityAvailableBalance = popularityAvailableBalance;
	}

}
