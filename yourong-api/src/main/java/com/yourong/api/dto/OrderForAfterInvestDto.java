package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.constant.Constant;
import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.core.tc.model.Order;

/**
 * 
 * @desc 订单支付以后的业务类，原业务类OrderForAfterInvestbiz在core层，不便于修改
 * @author zhanghao 2016年5月17日上午10:28:05
 */
public class OrderForAfterInvestDto extends AbstractBaseObject {

	private Order order;

	/** 交易号 **/
	private Long transactionId;

	/** P2P收益周期 **/
	private String profitPeriod;

	/** 投资类型（1-债权；2-直投） **/
	private Integer investType;

	/** 收益天数 **/
	private int profitDays;

	/** 收益类型 **/
	private String profitType;

	/**
	 * 是否发送红包标记
	 */
	private boolean hasRedPackage;

	/**
	 * 项目前置名
	 */
	private String prefixProjectName;
	
	/**
	 * 推荐项目
	 */
	List<RecommendProjectDto> recommendProjectList;
	/**
	 * 当前项目余额
	 */
	private BigDecimal availableBalance;
	
	/**
	 * 预期收益
	 */
	private BigDecimal expectAmount;
	
	/**
	 * 募集进度
	 */
	private String progress;
	/**
	 * 	首次还款日期
	 */
	private Date endDate;
	
	/**
	 * 	签署方式
	 */
	private Integer  signWay;

	/** 项目类型 **/
	private Integer projectCategory;
	
	/**
	 * 转让本金
	 */
	private BigDecimal transferPrincipal;
	
	/** 剩余天数 **/
	private int residualDays;
	
	private Integer lotteryNumber;
	
	private Long projectId;
	
	private Integer quickRewardFlag;
	
	private String extraName;
	
	
	
	public String getExtraName() {
		return extraName;
	}

	public void setExtraName(String extraName) {
		this.extraName = extraName;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public boolean isHasRedPackage() {
		return hasRedPackage;
	}

	public void setHasRedPackage(boolean hasRedPackage) {
		this.hasRedPackage = hasRedPackage;
	}

	public String getProfitPeriod() {
		return profitPeriod;
	}

	public void setProfitPeriod(String profitPeriod) {
		this.profitPeriod = profitPeriod;
	}

	public Integer getInvestType() {
		return investType;
	}

	public void setInvestType(Integer investType) {
		this.investType = investType;
	}

	public int getProfitDays() {
		return profitDays;
	}

	public void setProfitDays(int profitDays) {
		this.profitDays = profitDays;
	}

	public String getProfitType() {
		return profitType;
	}

	public void setProfitType(String profitType) {
		this.profitType = profitType;
	}

	public String getPrefixProjectName() {
		return prefixProjectName;
	}

	public void setPrefixProjectName(String prefixProjectName) {
		this.prefixProjectName = prefixProjectName;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * @return the recommendProjectList
	 */
	public List<RecommendProjectDto> getRecommendProjectList() {
		return recommendProjectList;
	}

	/**
	 * @param recommendProjectList the recommendProjectList to set
	 */
	public void setRecommendProjectList(
			List<RecommendProjectDto> recommendProjectList) {
		this.recommendProjectList = recommendProjectList;
	}

	/**
	 * @return the availableBalance
	 */
	public BigDecimal getAvailableBalance() {
		return availableBalance;
	}

	/**
	 * @param availableBalance the availableBalance to set
	 */
	public void setAvailableBalance(BigDecimal availableBalance) {
		this.availableBalance = availableBalance;
	}

	public BigDecimal getExpectAmount() {
		return expectAmount;
	}

	public void setExpectAmount(BigDecimal expectAmount) {
		this.expectAmount = expectAmount;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * 结束时间格式化
	 * @return
	 */
	public String getEndDateStr() {
		if(endDate!=null){
			return DateUtils.formatDatetoString(endDate, DateUtils.DATE_FMT_3);
		}
		return "";
	}

	/**
	 * @return the signWay
	 */
	public Integer getSignWay() {
		return signWay;
	}

	/**
	 * @param signWay the signWay to set
	 */
	public void setSignWay(Integer signWay) {
		this.signWay = signWay;
	}
	
	/**
	 * 
	 * @Description:合同名称
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月14日 上午11:14:33
	 */
	public String getContractTitle() {
		if(projectCategory==TypeEnum.PROJECT_CATEGORY_NORMAL.getType()){
			if (2 == this.investType) {
				return Constant.CONTRACT_P2P_TILE+"范本";
			}
			return Constant.CONTRACT_DEBT_TILE+"范本";
		}

		if(projectCategory==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
			if (2 == this.investType) {
				return Constant.CONTRACT_TRANSFER_P2P_TILE+"范本";
			}
			return Constant.CONTRACT_TRANSFER_DEBT_TILE+"范本";
		}
		return "";
	}

	/**
	 * @return the projectCategory
	 */
	public Integer getProjectCategory() {
		return projectCategory;
	}

	/**
	 * @param projectCategory the projectCategory to set
	 */
	public void setProjectCategory(Integer projectCategory) {
		this.projectCategory = projectCategory;
	}

	/**
	 * @return the transferPrincipal
	 */
	public BigDecimal getTransferPrincipal() {
		return transferPrincipal;
	}

	/**
	 * @param transferPrincipal the transferPrincipal to set
	 */
	public void setTransferPrincipal(BigDecimal transferPrincipal) {
		this.transferPrincipal = transferPrincipal;
	}

	/**
	 * @return the residualDays
	 */
	public int getResidualDays() {
		return residualDays;
	}

	/**
	 * @param residualDays the residualDays to set
	 */
	public void setResidualDays(int residualDays) {
		this.residualDays = residualDays;
	}

	public Integer getLotteryNumber() {
		return lotteryNumber;
	}

	public void setLotteryNumber(Integer lotteryNumber) {
		this.lotteryNumber = lotteryNumber;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Integer getQuickRewardFlag() {
		return quickRewardFlag;
	}

	public void setQuickRewardFlag(Integer quickRewardFlag) {
		this.quickRewardFlag = quickRewardFlag;
	}
	
	
	
}
