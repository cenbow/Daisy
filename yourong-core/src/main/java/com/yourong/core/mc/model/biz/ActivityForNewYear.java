package com.yourong.core.mc.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 新年活动
 * Created by alan.zheng on 2016/12/26.
 */
public class ActivityForNewYear extends AbstractBaseObject {
    /**
     * 福袋优惠券模板id集合
     */
    private String luckyBagTemplateIds;
    /**
     * 喜领压岁钱优惠券模板id集合
     */
    private String luckyMoneyTemplateIds;

    /**
     * 领取金额
     */
    private BigDecimal luckyMoneyAmount;

    public String getLuckyBagTemplateIds() {
        return luckyBagTemplateIds;
    }

    public void setLuckyBagTemplateIds(String luckyBagTemplateIds) {
        this.luckyBagTemplateIds = luckyBagTemplateIds;
    }

    public String getLuckyMoneyTemplateIds() {
        return luckyMoneyTemplateIds;
    }

    public void setLuckyMoneyTemplateIds(String luckyMoneyTemplateIds) {
        this.luckyMoneyTemplateIds = luckyMoneyTemplateIds;
    }

    public BigDecimal getLuckyMoneyAmount() {
        return luckyMoneyAmount;
    }

    public void setLuckyMoneyAmount(BigDecimal luckyMoneyAmount) {
        this.luckyMoneyAmount = luckyMoneyAmount;
    }
}
