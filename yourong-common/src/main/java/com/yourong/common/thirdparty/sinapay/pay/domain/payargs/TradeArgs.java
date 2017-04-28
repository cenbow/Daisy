package com.yourong.common.thirdparty.sinapay.pay.domain.payargs;

import java.util.List;
import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;

/**
 * <p>交易参数</p>
 * @author Wallis Wang
 * @version $Id: TradeArgs.java, v 0.1 2014年5月14日 下午1:56:33 wangqiang Exp $
 */
public class TradeArgs {
    /**
     * 交易订单号
     * 必填
     */
    @NotNull
    private String              tradeNo;

    /**
     * 收款人标识
     * 必填
     */
    @NotNull
    private String              payeeId;

    /**
     * 标识类型
     * 必填
     */
    @NotNull
    private IdType              idType;

    /**
     * 账户类型
     * 
     */
    private AccountType         accountType;

    /**
     * 金额
     * 必填
     */
    @NotNull
    private Money               money;

    /**
     * 分账信息列表
     */
    private List<SplitArgs>     splitArgs;

    /**
     * 备注
     */
    private String              remark;

    /**
     * 扩展信息
     */
    private Map<String, String> extendParams;

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(String payeeId) {
        this.payeeId = payeeId;
    }

    public IdType getIdType() {
        return idType;
    }

    public void setIdType(IdType idType) {
        this.idType = idType;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Money getMoney() {
        return money;
    }

    public void setMoney(Money money) {
        this.money = money;
    }

    public List<SplitArgs> getSplitArgs() {
        return splitArgs;
    }

    public void setSplitArgs(List<SplitArgs> splitArgs) {
        this.splitArgs = splitArgs;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Map<String, String> getExtendParams() {
        return extendParams;
    }

    public void setExtendParams(Map<String, String> extendParams) {
        this.extendParams = extendParams;
    }
}
