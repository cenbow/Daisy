package com.yourong.core.tc.model.biz;

import com.yourong.common.enums.StatusEnum;
import com.yourong.common.util.DateUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 转让项目biz
 * Created by XR on 2016/12/14.
 */
public class TransactionProjectBiz {
    /**
     * 交易id
     */
    private Long transferId;
    /**
     * 交易最新的转让项目id
     */
    private Long transferProjectId;
    /**
     * 项目名称
     */
    private String transferName;
    /**
     * 原始项目id
     */
    private Long projectId;
    /**
     * 原始项目名称
     */
    private String projectName;
    /**
     * 投资金额
     */
    private BigDecimal totalAmount;
    /**
     * 已转本金
     */
    private BigDecimal transferredAmount;
    /**
     * 累计收益
     */
    private BigDecimal totalIncome;
    /**
     * 交易剩余投资额
     */
    private BigDecimal transferResidualPrincipal;
    /**
     * 剩余本金
     */
    private BigDecimal residualPrincipal;
    /**
     * 剩余期限
     */
    private String residualTime;
    /**
     * 本次转让进度
     */
    private BigDecimal transferSchedule;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 状态描述
     */
    private String statusDes;
    /**
     * 转让项目到期时间
     */
    private Date transferEndDate;
    /**
     * 转让项目状态
     */
    private Integer transferStatus;

    /**
     * 部分转让全部转让标书 1 部分转让 2 全部转让
     */
    private Integer transferInvestStatus;

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public Long getTransferProjectId() {
        return transferProjectId;
    }

    public void setTransferProjectId(Long transferProjectId) {
        this.transferProjectId = transferProjectId;
    }

    public String getTransferName() {
        return transferName;
    }

    public void setTransferName(String transferName) {
        this.transferName = transferName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTransferredAmount() {
        if (totalAmount==null){
            return null;
        }
        if (transferResidualPrincipal==null){
            return null;
        }
        return totalAmount.subtract(transferResidualPrincipal);
    }

    public void setTransferredAmount(BigDecimal transferredAmount) {
        this.transferredAmount = transferredAmount;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTransferResidualPrincipal() {
        return transferResidualPrincipal;
    }

    public void setTransferResidualPrincipal(BigDecimal transferResidualPrincipal) {
        this.transferResidualPrincipal = transferResidualPrincipal;
    }

    public BigDecimal getResidualPrincipal() {
        return residualPrincipal;
    }

    public void setResidualPrincipal(BigDecimal residualPrincipal) {
        this.residualPrincipal = residualPrincipal;
    }

    public String getResidualTime() {
        if (transferEndDate!=null){
            int hour=DateUtils.getTimeIntervalHours(new Date(),transferEndDate);
            if (hour<1){
                int mins= DateUtils.getTimeIntervalMins(new Date(),transferEndDate);
                if (mins<1){
                    return "1分钟";
                }
                return mins+"分钟";
            }
            return hour+"小时";
        }
        return null;
    }

    public void setResidualTime(String residualTime) {
        this.residualTime = residualTime;
    }

    public BigDecimal getTransferSchedule() {
        if (residualPrincipal==null){
            return new BigDecimal(0);
        }
        if (transferResidualPrincipal==null){
            return residualPrincipal;
        }
        return residualPrincipal.subtract(transferResidualPrincipal);
    }

    public void setTransferSchedule(BigDecimal transferSchedule) {
        this.transferSchedule = transferSchedule;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getTransferEndDate() {
        return transferEndDate;
    }

    public void setTransferEndDate(Date transferEndDate) {
        this.transferEndDate = transferEndDate;
    }

    public String getStatusDes() {
        if(status==null||status==StatusEnum.TRANSACTION_COMPLETE.getStatus()
                ||status==StatusEnum.TRANSACTION_LOSE.getStatus()
                ){
            return "已结束";
        }
        if(transferStatus==StatusEnum.TRANSACTION_TRANSFER_STATUS_TRANSFERING.getStatus()){
            return "转让中";
        }
        if(transferStatus==StatusEnum.TRANSACTION_TRANSFER_STATUS_NO.getStatus()||
                transferStatus==StatusEnum.TRANSACTION_TRANSFER_STATUS_PART_TRANSFERED.getStatus()||
                status==StatusEnum.TRANSACTION_TRANSFER_STATUS_NO.getStatus()
                ){
            return "持有中";
        }
        return "已结束";
    }

    public void setStatusDes(String statusDes) {
        this.statusDes = statusDes;
    }

    public Integer getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(Integer transferStatus) {
        this.transferStatus = transferStatus;
    }

    public Integer getTransferInvestStatus() {
        return transferInvestStatus;
    }

    public void setTransferInvestStatus(Integer transferInvestStatus) {
        this.transferInvestStatus = transferInvestStatus;
    }
}
