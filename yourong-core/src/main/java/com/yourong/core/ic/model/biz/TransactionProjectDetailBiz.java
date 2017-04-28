package com.yourong.core.ic.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by XR on 2016/12/15.
 */
public class TransactionProjectDetailBiz extends AbstractBaseObject {
    /**转让项目ID**/
    private Long id;
    /**
     * 转让价格(已经获得+手续费)
     */
    private BigDecimal transferAmount;

    /**
     * 剩余本金
     */
    private BigDecimal residualPrincipal;

    /**
     * 可认购本金 (转让本金)
     */
    private BigDecimal subscriptionPrincipal;

    /**转让开始时间**/
    private Date transferStartDate;

    /**转让结束时间**/
    private Date transferEndDate;

    /**转让项目名称*/
    private String transferName;

    /**折价**/
    private BigDecimal discount;

    /**已转金额**/
    private BigDecimal transferedAmount;

    /**
     * 已经获得
     */
    private BigDecimal totalIncome;

    /**状态（30：投资中，50：已满额，51：转让中，52：已转让,70：已还款,80：流标中,90：流标,转让失败）**/
    private Integer status;

    /**备注**/
    private String remarks;

    /**转让手续费率 */
    private BigDecimal transferRate;

    /**转让手续费 */
    private BigDecimal transferFee;

    /**
     * 0-未失败，1-流标，2-撤销
     */
    private Integer failFlag;

    /**
     * 流标时间
     */
    private Date failTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTransferAmount() {
        if (totalIncome==null){
            return transferFee;
        }
        if (transferFee==null){
            return totalIncome;
        }
        return totalIncome.add(transferFee);
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    public BigDecimal getResidualPrincipal() {
        return residualPrincipal;
    }

    public void setResidualPrincipal(BigDecimal residualPrincipal) {
        this.residualPrincipal = residualPrincipal;
    }

    public BigDecimal getSubscriptionPrincipal() {
        return subscriptionPrincipal;
    }

    public void setSubscriptionPrincipal(BigDecimal subscriptionPrincipal) {
        this.subscriptionPrincipal = subscriptionPrincipal;
    }

    public Date getTransferStartDate() {
        return transferStartDate;
    }

    public void setTransferStartDate(Date transferStartDate) {
        this.transferStartDate = transferStartDate;
    }

    public Date getTransferEndDate() {
        return transferEndDate;
    }

    public void setTransferEndDate(Date transferEndDate) {
        this.transferEndDate = transferEndDate;
    }

    public String getTransferName() {
        return transferName;
    }

    public void setTransferName(String transferName) {
        this.transferName = transferName;
    }

    public BigDecimal getDiscount() {
        if (transferedAmount==null){
            return transferedAmount;
        }
        if (totalIncome==null){
            if (transferFee!=null){
                transferedAmount.subtract(transferFee);
            }
            return transferedAmount;
        }
        if (transferFee==null){
            if (totalIncome!=null){
                transferedAmount.subtract(totalIncome);
            }
            return transferedAmount;
        }
        return transferedAmount.subtract(totalIncome).subtract(transferFee);
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getTransferedAmount() {
        return transferedAmount;
    }

    public void setTransferedAmount(BigDecimal transferedAmount) {
        this.transferedAmount = transferedAmount;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public BigDecimal getTransferRate() {
        return transferRate;
    }

    public void setTransferRate(BigDecimal transferRate) {
        this.transferRate = transferRate;
    }

    public BigDecimal getTransferFee() {
        return transferFee;
    }

    public void setTransferFee(BigDecimal transferFee) {
        this.transferFee = transferFee;
    }

    public Integer getFailFlag() {
        return failFlag;
    }

    public void setFailFlag(Integer failFlag) {
        this.failFlag = failFlag;
    }

    public Date getFailTime() {
        return failTime;
    }

    public void setFailTime(Date failTime) {
        this.failTime = failTime;
    }
}
