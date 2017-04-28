package com.yourong.web.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import com.google.common.collect.Lists;
import com.yourong.common.constant.Config;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.fin.model.OverdueRepayLog;
import com.yourong.core.ic.model.DebtCollateral;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.LeaseDetail;
import com.yourong.core.ic.model.LotteryRuleAmountNumber;
import com.yourong.core.ic.model.ProjectInterest;
import com.yourong.core.ic.model.biz.ProjectForDirectLottery;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.uc.model.Enterprise;
import com.yourong.core.uc.model.MemberBaseBiz;

public class ProjectInfoDto {

	/** 项目id **/
	private Long id;

	/** 目前只有债权ID **/
	private Long debtId;

	/** 项目名称 **/
	private String name;

	/** 项目关键词 **/
	private String keyword;

	/** 项目描述 **/
	private String shortDesc;

	/** 收益类型 **/
	private String profitType;

	/** 风险等级 **/
	private String riskLevel;

	/** 投资总金额 **/
	private BigDecimal totalAmount;

	/** 起投金额 **/
	private BigDecimal minInvestAmount;

	/** 递增单位金额 **/
	private BigDecimal incrementAmount;

	/** 收益计算方式(1-阶梯收益， 2-利率随递增额递增) **/
	private Integer annualizedRateType;

	/** 最小收益 **/
	private BigDecimal minAnnualizedRate;

	/** 最大收益 **/
	private BigDecimal maxAnnualizedRate;

	/** 递增收益 **/
	private BigDecimal incrementAnnualizedRate;

	/** 项目开始日期(年月日) **/
	private Date startDate;

	/** 还款时间(年月日) **/
	private Date endDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	/**上线时间(年月日时分秒)**/
	private Date onlineTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	/**销售截止日期(年月日时分秒)**/
	private Date saleEndTime;

	/** 起息日，T+1则存1 **/
	private Integer interestFrom;

	/** 状态码(1:存盘,10:待审核,20:待发布,30:投资中,40:已暂停,50:已满额,60:已截止,70:已还款) **/
	private Integer status;

	/** 收益封顶 **/
	private BigDecimal maxInvestAmount;

	/** 附件信息 */
	private List<BscAttachment> bscAttachments;

	/** 借款人附件 **/
	private List<BscAttachment> borrowMemberAttachments = Lists.newArrayList();

	/** 原始债权人附件 **/
	private List<BscAttachment> lenderMemberAttachments = Lists.newArrayList();

	/** 质押或抵押物附件 **/
	private List<BscAttachment> collateralAttachments = Lists.newArrayList();

	/** 合同附件 **/
	private List<BscAttachment> contractAttachments = Lists.newArrayList();

	/** 合同附件名称 **/
	private List<String> contractCategoryName = Lists.newArrayList();

	/** 阶梯收益 **/
	List<ProjectInterest> projectInterestList;

	/** 项目描述，保障措施 **/
	private String description;

	/** 债权 **/
	private DebtDto debtDto;

	/** 就否新手项目 **/
	private Integer isNovice;

	/** 是否参与租赁分红 **/
	private Integer joinLease;

	/** 租赁结算列表 **/
	private List<LeaseDetail> leaseDetails;

	/** 删除标记 **/
	private Integer delFlag;

	/** 企业的项目信息 **/
	private EnterpriseProjectInfoDto enterpriseProjectInfoDto;

	/** 年化率 */
	private BigDecimal annualizedRate;
	
	/**
	 * 转让项目实际年化
	 */
	private BigDecimal transferAnnualizedRate;

	/** 项目形象图 **/
	private List<BscAttachment> signAttachments = Lists.newArrayList();

	/** 借款人为企业时显示的信息 **/
	private Enterprise enterprise;

	/** 借款人信息 */
	private MemberBaseBiz borrowMemberBiz;

	/** 借款类型 */
	private Integer borrowerType;

	/** 本息表 */
	private List<DebtInterest> interests;

	/** 担保方式（pledge-质押；collateral-抵押；credit-信用） **/
	private String securityType;

	/** 担保物的详细信息 */
	private DebtCollateral debtCollateral;
	
	/**销售完成时间*/
	private Date saleComplatedTime;
	
	private Integer investType;

	/**项目类型*/
	private String projectType;
	
	/**借款周期*/
	private Integer borrowPeriod;
	
	/**借款周期类型*/
	private Integer borrowPeriodType;
	
	/**是否提前还款**/
	private Integer prepayment;
	
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	/**提前还款时间**/
	private Date prepaymentTime;
	
	/**提前还款原因**/
	private String prepaymentRemark;
	
	private Boolean isOverdue=false;
	
	 /**折价**/
    private BigDecimal discount;
    
    /*项目类型*/
    private Integer projectCategory;
    
    /*是否可以转让*/
    private Integer transferFlag;
    
	/**起息日后X个自然日可以转让**/
	private Integer transferAfterInterest;
	
	
	/** 原项目年化率 */
	private BigDecimal originalAnnualizedRate;
	
	/**转让价格**/
    private BigDecimal transferAmount;
    

    /**转让结束时间**/
    private Date transferEndDate;

    /**服务器时间**/
    private Date currentDate;
    
    /**单位转让金额**/
    private BigDecimal unitTransferAmount;
    
    /**单位认购金额**/
    private BigDecimal unitSubscriptionAmount;
    
    /*转让项目ID*/
    private Long transferId;
    
    /**项目缩略图**/
    private String thumbnail;
    
    /**是否开启快投有奖**/
    private Boolean isQuickLottery=false;
    
    /** 快投结束时间  **/
    private Date lotteryEndTime;
    
	/**
	 * 不需要限制首投校验
	 */
	private Boolean noCheckNovice = false;
    
	/**特殊业务标识*/
    private Integer extraType;
    
    /**加息数值*/
    private String addRate;
    
    
    /** 奖池总额 **/
	private BigDecimal extraAmount;
	
	
	/** 抽奖结束倒计时 **/
	private Integer lotteryEndCountDown;
	
	/** 人气值奖励 开始时间 **/
	private String popularityStratDate;
	
	/** 人气值奖励  结束时间 **/
	private String popularityEndDate;
	
	/** 人气值 **/
	private String popularity; 
	
	/** 人气值开关 **/
	private boolean popularityFlag;
	
	
	/** 上线倒计时 **/
	private Integer onlineCountDown;
	
	
	
    
    private BigDecimal addRateBigDecimal;
	
	public Boolean getIsOverdue() {
		return isOverdue;
	}

	public void setIsOverdue(Boolean isOverdue) {
		this.isOverdue = isOverdue;
	}
	
	public Boolean getIsQuickLottery() {
		return isQuickLottery;
	}

	public void setIsQuickLottery(Boolean isQuickLottery) {
		this.isQuickLottery = isQuickLottery;
	}

	/**逾期结算记录(催收成果)**/
	private List<OverdueRepayLog> overdueRepayBiz;
	
	/**逾期结算记录(催收中)**/
	private OverdueRepayLog overdue;
	
	/** 优惠券 */
	private List<Coupon> coupons;
	
	
	private ProjectForDirectLottery directLotteryDetail;

	private String openPlatformKey;
	
	private List<LotteryRuleAmountNumber> lotteryRuleList;

	/**
	 * 借款人信用等级
	 */
	private String borrowerCreditLevel;

	/**
	 * 借款人信用等级描述
	 */
	private String borrowerCreditLevelDes;

	/**
	 * 已还清项目数
	 */
	private Integer payOffCount;

	/**
	 * 逾期未还期数
	 */
	private Integer overdueCount;

	/**
	 * 逾期未还金额
	 */
	private BigDecimal overdueAmount;
	
	

	public List<LotteryRuleAmountNumber> getLotteryRuleList() {
		return lotteryRuleList;
	}

	public void setLotteryRuleList(List<LotteryRuleAmountNumber> lotteryRuleList) {
		this.lotteryRuleList = lotteryRuleList;
	}

	public ProjectForDirectLottery getDirectLotteryDetail() {
		return directLotteryDetail;
	}

	public void setDirectLotteryDetail(ProjectForDirectLottery directLotteryDetail) {
		this.directLotteryDetail = directLotteryDetail;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDebtId() {
		return debtId;
	}

	public void setDebtId(Long debtId) {
		this.debtId = debtId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getPrepaymentTime() {
		return prepaymentTime;
	}

	public void setPrepaymentTime(Date prepaymentTime) {
		this.prepaymentTime = prepaymentTime;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public OverdueRepayLog getOverdue() {
		return overdue;
	}

	public void setOverdue(OverdueRepayLog overdue) {
		this.overdue = overdue;
	}

	public String getShortDesc() {
		return shortDesc;
	}

	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}

	public String getProfitType() {
		return profitType;
	}

	public void setProfitType(String profitType) {
		this.profitType = profitType;
	}

	public String getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}

	public String getPrepaymentRemark() {
		return prepaymentRemark;
	}

	public void setPrepaymentRemark(String prepaymentRemark) {
		this.prepaymentRemark = prepaymentRemark;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getMinInvestAmount() {
		return minInvestAmount;
	}

	public void setMinInvestAmount(BigDecimal minInvestAmount) {
		this.minInvestAmount = minInvestAmount;
	}

	public BigDecimal getIncrementAmount() {
		return incrementAmount;
	}

	public void setIncrementAmount(BigDecimal incrementAmount) {
		this.incrementAmount = incrementAmount;
	}

	public Integer getAnnualizedRateType() {
		return annualizedRateType;
	}

	public void setAnnualizedRateType(Integer annualizedRateType) {
		this.annualizedRateType = annualizedRateType;
	}

	public BigDecimal getMinAnnualizedRate() {
		return minAnnualizedRate;
	}

	public void setMinAnnualizedRate(BigDecimal minAnnualizedRate) {
		this.minAnnualizedRate = minAnnualizedRate;
	}

	public BigDecimal getMaxAnnualizedRate() {
		return maxAnnualizedRate;
	}

	public void setMaxAnnualizedRate(BigDecimal maxAnnualizedRate) {
		this.maxAnnualizedRate = maxAnnualizedRate;
	}

	public BigDecimal getIncrementAnnualizedRate() {
		if (incrementAnnualizedRate == null) {
			return BigDecimal.ZERO;
		}
		return incrementAnnualizedRate;
	}

	public void setIncrementAnnualizedRate(BigDecimal incrementAnnualizedRate) {
		this.incrementAnnualizedRate = incrementAnnualizedRate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}

	public Date getSaleEndTime() {
		return saleEndTime;
	}

	public void setSaleEndTime(Date saleEndTime) {
		this.saleEndTime = saleEndTime;
	}

	public Integer getInterestFrom() {
		return interestFrom;
	}

	public void setInterestFrom(Integer interestFrom) {
		this.interestFrom = interestFrom;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<BscAttachment> getBscAttachments() {
		return bscAttachments;
	}

	public void setBscAttachments(List<BscAttachment> bscAttachments) {
		this.bscAttachments = bscAttachments;
	}

	public DebtDto getDebtDto() {
		return debtDto;
	}

	public void setDebtDto(DebtDto debtDto) {
		this.debtDto = debtDto;
	}

	public List<BscAttachment> getBorrowMemberAttachments() {
		return borrowMemberAttachments;
	}

	public void setBorrowMemberAttachments(List<BscAttachment> borrowMemberAttachments) {
		this.borrowMemberAttachments = borrowMemberAttachments;
	}

	public List<BscAttachment> getLenderMemberAttachments() {
		return lenderMemberAttachments;
	}

	public void setLenderMemberAttachments(List<BscAttachment> lenderMemberAttachments) {
		this.lenderMemberAttachments = lenderMemberAttachments;
	}

	public List<BscAttachment> getCollateralAttachments() {
		return collateralAttachments;
	}

	public void setCollateralAttachments(List<BscAttachment> collateralAttachments) {
		this.collateralAttachments = collateralAttachments;
	}

	public List<BscAttachment> getContractAttachments() {
		return contractAttachments;
	}

	public void setContractAttachments(List<BscAttachment> contractAttachments) {
		this.contractAttachments = contractAttachments;
	}

	public List<String> getContractCategoryName() {
		return contractCategoryName;
	}

	public void setContractCategoryName(List<String> contractCategoryName) {
		this.contractCategoryName = contractCategoryName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getIsNovice() {
		return isNovice;
	}

	public void setIsNovice(Integer isNovice) {
		this.isNovice = isNovice;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}
	
	public Integer getPrepayment() {
		return prepayment;
	}

	public void setPrepayment(Integer prepayment) {
		this.prepayment = prepayment;
	}

	public boolean isDirectProject(){
		if (this.investType == ProjectEnum.PROJECT_TYPE_DIRECT.getType()){
			return true;
		}
		return false;
	}
	/**
	 * 收益总天数
	 * 
	 * @return
	 */
	public Integer getEarningsTotalDays() {
		int day = DateUtils.getIntervalDays(getStartDate(), getEndDate()) + 1 - getInterestFrom();
		return day > 0 ? day : 0;
	}

	/**
	 * 获得预告项目的收益总天数
	 * 
	 * @return
	 */
	public Integer getNoticeProjectEarningsTotalDays() {
		if(isDirectProject()){
			return getDirectProjectEarningDays();
		}else{
			int day = DateUtils.getIntervalDays(DateUtils.formatDate(getOnlineTime(), DateUtils.DATE_FMT_3), getEndDate()) + 1
					- getInterestFrom();
			return day > 0 ? day : 0;
		}
	}

	/**
	 * 剩余收益天数
	 * 
	 * @return
	 */
	public Integer getEarningsDays() {
		int day = 0;
		if (ProjectEnum.PROJECT_TYPE_DIRECT.getType() == investType) {
			day = getDirectProjectEarningDays();
		} else {
			day = DateUtils.getIntervalDays(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), getEndDate()) + 1
					- getInterestFrom();
		}
		return day > 0 ? day : 0;
	}

	/**
	 * 根据状态显示剩余收益天数或收益总天数
	 * 
	 * @return
	 */
	public Integer getEarningsDaysByStatus() {
		if (isNotice()) {// 预告
			return getNoticeProjectEarningsTotalDays();
		}
		if (status == StatusEnum.PROJECT_STATUS_INVESTING.getStatus() || status == StatusEnum.PROJECT_STATUS_STOP.getStatus()) {
			return getEarningsDays();
		}
		return getEarningsTotalDays();
	}

	public List<ProjectInterest> getProjectInterestList() {
		return projectInterestList;
	}

	public void setProjectInterestList(List<ProjectInterest> projectInterestList) {
		this.projectInterestList = projectInterestList;
	}

	public BigDecimal getMaxInvestAmount() {
		if (maxInvestAmount == null) {
			return BigDecimal.ZERO;
		}
		return maxInvestAmount;
	}

	public void setMaxInvestAmount(BigDecimal maxInvestAmount) {
		this.maxInvestAmount = maxInvestAmount;
	}

	public String getPrefixProjectName() {
		if (name.contains("期")) {
			return name.substring(0, name.indexOf("期") + 1);
		} else {
			return "";
		}
	}

	public String getSuffixProjectName() {
		if (name.contains("期")) {
			return name.substring(name.indexOf("期") + 1);
		} else {
			return name;
		}
	}

	/**
	 * 是否可投资
	 * 
	 * @return
	 */
	public boolean isActive() {
		// 投资中
		if (status != StatusEnum.PROJECT_STATUS_INVESTING.getStatus()) {
			return false;
		}
		return true;
	}

	/**
	 * 是否暂停
	 * 
	 * @return
	 */
	public boolean isStop() {
		if (status == StatusEnum.PROJECT_STATUS_STOP.getStatus()) {
			return true;
		}
		return false;
	}

	public String getTransactionText() {
		if(projectCategory == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
			if (status == StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()) {
				return "累计赚取";
			}else{
				return "预期赚取";
			}
		}
		if (status == StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()) {
			return "累计获得收益";
		}
		return "预期总收益";
	}

	/**
	 * 根据状态获得按钮文本
	 * 
	 * @return
	 */
	public String getButtonText() {
		
		if(projectCategory == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
			if(status == StatusEnum.TRANSFER_PROJECT_STATUS_INVESTING.getStatus()
					){
				return "立即认购";
			}else if(status == StatusEnum.TRANSFER_PROJECT_STATUS_LOAN.getStatus()){
				return "履约中";
			}else if(status == StatusEnum.TRANSFER_PROJECT_STATUS_REPAYMENT.getStatus()){
				return "已还款";
			}else if(status == StatusEnum.TRANSFER_PROJECT_STATUS_LOSE.getStatus()){
				return "流标";
			}
		}
		
		
		if (ProjectEnum.PROJECT_TYPE_DEBT.getType() == investType) {
			if (status == StatusEnum.PROJECT_STATUS_INVESTING.getStatus()) {
				return "立即投资";
			} else if (status == StatusEnum.PROJECT_STATUS_STOP.getStatus()) {
				return StatusEnum.PROJECT_STATUS_STOP.getDesc();
			} else if (status == StatusEnum.PROJECT_STATUS_FULL.getStatus() || status == StatusEnum.PROJECT_STATUS_END.getStatus()) {
				return "履约中";
			} else if (status == StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()) {
				return "已还款";
			}
		} else if (ProjectEnum.PROJECT_TYPE_DIRECT.getType() == investType) {
			if (getStatus() == StatusEnum.PROJECT_STATUS_INVESTING.getStatus()) {
				return "立即投资";
			} else if (getStatus() == StatusEnum.PROJECT_STATUS_FULL.getStatus()
					|| (getStatus() == StatusEnum.PROJECT_STATUS_LOSING.getStatus() && saleComplatedTime != null )
					||getStatus() == StatusEnum.PROJECT_STATUS_AUTH_PASS.getStatus()) {
				return "已投满";
			} else if (getStatus() == StatusEnum.PROJECT_STATUS_STOP.getStatus()) {
				return StatusEnum.PROJECT_STATUS_STOP.getDesc();
			} else if (getStatus() == StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus()
					||getStatus() == StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus()
					) {
				return "履约中";
			} else if (getStatus() == StatusEnum.PROJECT_STATUS_END.getStatus() 
					||(getStatus() == StatusEnum.PROJECT_STATUS_LOSING.getStatus() && saleComplatedTime == null )) {
				return StatusEnum.PROJECT_STATUS_END.getDesc();
			} else if (getStatus() == StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()) {
				return StatusEnum.PROJECT_STATUS_REPAYMENT.getDesc();
			} else if (getStatus() == StatusEnum.PROJECT_STATUS_LOSE.getStatus()) {
				return StatusEnum.PROJECT_STATUS_LOSE.getDesc();
			}
		}
		return "";
	}

	/**
	 * 详情页面seo描述，最长限制为200
	 * 
	 * @return
	 */
	public String getSeoDesc() {
		if (StringUtil.isNotBlank(getShortDesc()) && getShortDesc().length() > 200) {
			return getShortDesc().substring(0, 200);
		}
		return getShortDesc();
	}

	/**
	 * 是否新手项目
	 * 
	 * @return
	 */
	public boolean isNoviceProject() {
		if (isNovice == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 是否是预告
	 * 
	 * @return
	 */
	public boolean isNotice() {
		if (status >= StatusEnum.PROJECT_STATUS_INVESTING.getStatus()) {
			return false;
		}
		return true;
	}

	/**
	 * 预告是否就绪
	 * 
	 * @return
	 */
	public boolean noticeIsReady() {
		int sencond = getNoticeEndTime();
		float _sencond = (float) sencond / 3600;
		if (_sencond > 48) {
			return false;
		}
		return true;
	}

	/**
	 * 预告的结束时间（秒）
	 * 
	 * @return
	 * 
	 */
	public int getNoticeEndTime() {
		return DateUtils.getTimeIntervalSencond(DateUtils.getCurrentDate(), getOnlineTime());
	}

	public Integer getJoinLease() {
		return joinLease;
	}

	public boolean isJoinLeaseBoolean() {
		if (joinLease == null) {
			return false;
		}
		if (joinLease == 1) {
			return true;
		}
		return false;
	}

	public void setJoinLease(Integer joinLease) {
		this.joinLease = joinLease;
	}

	public List<LeaseDetail> getLeaseDetails() {
		return leaseDetails;
	}

	public void setLeaseDetails(List<LeaseDetail> leaseDetails) {
		this.leaseDetails = leaseDetails;
	}

	/**
	 * 获取第一期的收益天数
	 */

	public Integer getFirstRealEarningDays() {
		if (ProjectEnum.PROJECT_TYPE_DEBT.getType() == investType) {
			if (debtDto != null) {
				interests = debtDto.getDebtInterests();
			}
			if (Collections3.isNotEmpty(interests)) {
				for (DebtInterest debtInterest : interests) {
					// 开始时间小于这期债权的收益时间，则不计交易本息
					Date startInterestDate = DateUtils.formatDate(DateUtils.addDate(DateUtils.getCurrentDate(), getInterestFrom()),
							DateUtils.DATE_FMT_3);
					if (!startInterestDate.after(debtInterest.getEndDate())) {
						return DateUtils.daysOfTwo(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3),
								debtInterest.getEndDate())
								+ 1 - getInterestFrom();
					}
				}
			}
		}
		return 0;
	}

	/**
	 * 获取第一期的债权收益天数
	 */
	public Integer getFirstDebtEarningDays() {
		if (ProjectEnum.PROJECT_TYPE_DEBT.getType() == investType) {
			if (debtDto != null) {
				interests = debtDto.getDebtInterests();
			}
			if (Collections3.isNotEmpty(interests)) {
				for (DebtInterest debtInterest : interests) {
					// 开始时间小于这期债权的收益时间，则不计交易本息
					Date startInterestDate = DateUtils.formatDate(DateUtils.addDate(DateUtils.getCurrentDate(), getInterestFrom()),
							DateUtils.DATE_FMT_3);
					if (!startInterestDate.after(debtInterest.getEndDate())) {
						return DateUtils.daysOfTwo(debtInterest.getStartDate(), debtInterest.getEndDate()) + 1;
					}
				}
			}
		}
		return 0;
	}

	/**
	 * 获取收益期数
	 */
	public Integer getEarningPeriod() {
		Integer period = 0;
		if (ProjectEnum.PROJECT_TYPE_DEBT.getType() == investType) {
			if (debtDto != null) {
				interests = debtDto.getDebtInterests();
			}
			if (Collections3.isNotEmpty(interests)) {
				for (DebtInterest debtInterest : interests) {
					// 开始时间小于这期债权的收益时间，则不计交易本息
					Date startInterestDate = DateUtils.formatDate(DateUtils.addDate(DateUtils.getCurrentDate(), getInterestFrom()),
							DateUtils.DATE_FMT_3);
					if (!startInterestDate.after(debtInterest.getEndDate())) {
						period = period + 1;
					}
				}
				return period;
			}
		}else if(ProjectEnum.PROJECT_TYPE_DIRECT.getType() == investType){
			if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_MONTH.getType() == borrowPeriodType||
					TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_WEEK.getType() == borrowPeriodType){
				return borrowPeriod;
			}else if (TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_YEAR.getType() == borrowPeriodType){
				return borrowPeriod * 12;
			}
		}
		return period;
	}

	/**
	 * 判断本息表显示方式（每一期都会归还本金和利息）的项目
	 */
	public boolean getIsAvgPrincipalAndInterest() {
		Set<String> set = new HashSet<String>();
		set.add(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode());
		if (set.contains(profitType)) {
			return true;
		}
		return false;

	}

	public EnterpriseProjectInfoDto getEnterpriseProjectInfoDto() {
		return enterpriseProjectInfoDto;
	}

	public void setEnterpriseProjectInfoDto(EnterpriseProjectInfoDto enterpriseProjectInfoDto) {
		this.enterpriseProjectInfoDto = enterpriseProjectInfoDto;
	}

	public BigDecimal getAnnualizedRate() {
		return annualizedRate;
	}

	public void setAnnualizedRate(BigDecimal annualizedRate) {
		this.annualizedRate = annualizedRate;
	}

	public List<BscAttachment> getSignAttachments() {
		return signAttachments;
	}

	public void setSignAttachments(List<BscAttachment> signAttachments) {
		this.signAttachments = signAttachments;
	}

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public MemberBaseBiz getBorrowMemberBiz() {
		return borrowMemberBiz;
	}

	public void setBorrowMemberBiz(MemberBaseBiz borrowMemberBiz) {
		this.borrowMemberBiz = borrowMemberBiz;
	}

	public Integer getBorrowerType() {
		return borrowerType;
	}

	public void setBorrowerType(Integer borrowerType) {
		this.borrowerType = borrowerType;
	}

	public List<DebtInterest> getInterests() {
		return interests;
	}

	public void setInterests(List<DebtInterest> interests) {
		this.interests = interests;
	}

	public String getSecurityType() {
		return securityType;
	}

	public void setSecurityType(String securityType) {
		this.securityType = securityType;
	}

	public DebtCollateral getDebtCollateral() {
		return debtCollateral;
	}

	public void setDebtCollateral(DebtCollateral debtCollateral) {
		this.debtCollateral = debtCollateral;
	}

	public String getInterestFromStr() {
		String t = "T（募集完成日）+" + getInterestFrom();
		return t;
	}

	public Integer getInvestType() {
		return investType;
	}

	public void setInvestType(Integer investType) {
		this.investType = investType;
	}

	public Date getSaleComplatedTime() {
		return saleComplatedTime;
	}

	public void setSaleComplatedTime(Date saleComplatedTime) {
		this.saleComplatedTime = saleComplatedTime;
	}
	
	/**
	 * 获取产品类型：房屋、车辆
	 */
	public String getProjectTypeGroupName(){
		
		
		if(borrowerType!=null&&investType!=null&&ProjectEnum.PROJECT_TYPE_DIRECT.getType()==investType){
			if(borrowerType==2){
				return "企业";
			}else if(borrowerType==4){
				return "主体";
			}else{
				return "个人";
			}
		}
		String groupName = "车辆";
		if(projectType.equals("house") || projectType.equals("houseRecord")){
			groupName = "房屋";
		}
		if(projectType.equals("carCompany") ){
			groupName = "999";
		}
		return groupName;
	}

	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	/**
	 * 获取直投项目的收益周期
	 */
	public String getFormatProfitPeriodType(){
		String profitPeriod = "";
		if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType()==borrowPeriodType){
			profitPeriod = "天";
		}
		if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_MONTH.getType()==borrowPeriodType){
			profitPeriod = "个月";
		}
		if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_YEAR.getType()==borrowPeriodType){
			profitPeriod = "年";
		}
		if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_WEEK.getType()==borrowPeriodType){
			profitPeriod = "周";
		}
		return profitPeriod;
	}

	public Integer getBorrowPeriod() {
		return borrowPeriod;
	}

	public void setBorrowPeriod(Integer borrowPeriod) {
		this.borrowPeriod = borrowPeriod;
	}

	public Integer getBorrowPeriodType() {
		return borrowPeriodType;
	}

	public void setBorrowPeriodType(Integer borrowPeriodType) {
		this.borrowPeriodType = borrowPeriodType;
	}
	
	/**
	 * 获取直投项目的收益天数
	 */
	public int  getDirectProjectEarningDays(){
		Date currendDate = DateUtils.getCurrentDate();
        int days = 0;
        if (TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType()==borrowPeriodType) {
            days = borrowPeriod;
        }
        //借款周期类型月
        if (TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_MONTH.getType()==borrowPeriodType) {
            Date peroidEndDate = DateUtils.addMonth(currendDate, borrowPeriod);
            days = DateUtils.getIntervalDays(currendDate, peroidEndDate);
        }
        //借款周期类型年
        if (TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_YEAR.getType()==borrowPeriodType) {
        	Date peroidEndDate = DateUtils.addYearsApart(currendDate, borrowPeriod);
            days = DateUtils.getIntervalDays(currendDate, peroidEndDate);
        }
		// 借款周期类型周
		if (TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_WEEK.getType() == borrowPeriodType) {
			days = borrowPeriod.intValue() * 7;
		}
        return days;
    }

	public List<OverdueRepayLog> getOverdueRepayBiz() {
		return overdueRepayBiz;
	}

	public void setOverdueRepayBiz(List<OverdueRepayLog> overdueRepayBiz) {
		this.overdueRepayBiz = overdueRepayBiz;
	}

	public List<Coupon> getCoupons() {
		return coupons;
	}

	public void setCoupons(List<Coupon> coupons) {
		this.coupons = coupons;
	}

	/**
	 * @return the discount
	 */
	public BigDecimal getDiscount() {
		return discount;
	}

	/**
	 * @param discount the discount to set
	 */
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
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
	 * @return the transferFlag
	 */
	public Integer getTransferFlag() {
		return transferFlag;
	}

	/**
	 * @param transferFlag the transferFlag to set
	 */
	public void setTransferFlag(Integer transferFlag) {
		this.transferFlag = transferFlag;
	}

	/**
	 * @return the transferAfterInterest
	 */
	public Integer getTransferAfterInterest() {
		return transferAfterInterest;
	}

	/**
	 * @param transferAfterInterest the transferAfterInterest to set
	 */
	public void setTransferAfterInterest(Integer transferAfterInterest) {
		this.transferAfterInterest = transferAfterInterest;
	}


	/**
	 * @return the originalAnnualizedRate
	 */
	public BigDecimal getOriginalAnnualizedRate() {
		return originalAnnualizedRate;
	}

	/**
	 * @param originalAnnualizedRate the originalAnnualizedRate to set
	 */
	public void setOriginalAnnualizedRate(BigDecimal originalAnnualizedRate) {
		this.originalAnnualizedRate = originalAnnualizedRate;
	}

	/**
	 * @return the transferAmount
	 */
	public BigDecimal getTransferAmount() {
		return transferAmount;
	}

	/**
	 * @param transferAmount the transferAmount to set
	 */
	public void setTransferAmount(BigDecimal transferAmount) {
		this.transferAmount = transferAmount;
	}


	/**
	 * @return the transferEndDate
	 */
	public Date getTransferEndDate() {
		return transferEndDate;
	}

	/**
	 * @param transferEndDate the transferEndDate to set
	 */
	public void setTransferEndDate(Date transferEndDate) {
		this.transferEndDate = transferEndDate;
	}

	/**
	 * @return the currentDate
	 */
	public Date getCurrentDate() {
		return currentDate;
	}

	/**
	 * @param currentDate the currentDate to set
	 */
	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	/**
	 * @return the unitTransferAmount
	 */
	public BigDecimal getUnitTransferAmount() {
		return unitTransferAmount;
	}

	/**
	 * @param unitTransferAmount the unitTransferAmount to set
	 */
	public void setUnitTransferAmount(BigDecimal unitTransferAmount) {
		this.unitTransferAmount = unitTransferAmount;
	}

	/**
	 * @return the unitSubscriptionAmount
	 */
	public BigDecimal getUnitSubscriptionAmount() {
		return unitSubscriptionAmount;
	}

	/**
	 * @param unitSubscriptionAmount the unitSubscriptionAmount to set
	 */
	public void setUnitSubscriptionAmount(BigDecimal unitSubscriptionAmount) {
		this.unitSubscriptionAmount = unitSubscriptionAmount;
	}


	/**
	 * @return the transferId
	 */
	public Long getTransferId() {
		return transferId;
	}

	/**
	 * @param transferId the transferId to set
	 */
	public void setTransferId(Long transferId) {
		this.transferId = transferId;
	}

	/**
	 * @return the thumbnail
	 */
	public String getThumbnail() {
		if(projectCategory!=null){
			if(projectCategory==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
				return Config.ossPicUrl+thumbnail;
			}
		}
		if(thumbnail != null){
			if(getId() != null && getId() >= 989800156L){
				return Config.ossPicUrl+StringUtil.getFilePath(thumbnail,"300");
			}else{
				return Config.ossPicUrl+thumbnail;
			}
		}
		return thumbnail;
	}

	/**
	 * @param thumbnail the thumbnail to set
	 */
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getOpenPlatformKey() {
		return openPlatformKey;
	}

	public void setOpenPlatformKey(String openPlatformKey) {
		this.openPlatformKey = openPlatformKey;
	}

	public Boolean getNoCheckNovice() {
		return noCheckNovice;
	}

	public void setNoCheckNovice(Boolean noCheckNovice) {
		this.noCheckNovice = noCheckNovice;
	}

	public Integer getExtraType() {
		return extraType;
	}

	public void setExtraType(Integer extraType) {
		this.extraType = extraType;
	}

	public String getAddRate() {
		return addRate;
	}

	public void setAddRate(String addRate) {
		this.addRate = addRate;
	}

	public BigDecimal getAddRateBigDecimal() {
		return addRateBigDecimal;
	}

	public void setAddRateBigDecimal(BigDecimal addRateBigDecimal) {
		this.addRateBigDecimal = addRateBigDecimal;
	}

	/**
	 * @return the transferAnnualizedRate
	 */
	public BigDecimal getTransferAnnualizedRate() {
		return transferAnnualizedRate;
	}

	/**
	 * @param transferAnnualizedRate the transferAnnualizedRate to set
	 */
	public void setTransferAnnualizedRate(BigDecimal transferAnnualizedRate) {
		this.transferAnnualizedRate = transferAnnualizedRate;
	}

	
	public Date getLotteryEndTime() {
		return lotteryEndTime;
	}
	public void setLotteryEndTime(Date lotteryEndTime) {
		this.lotteryEndTime = lotteryEndTime;
	}

	public Integer getLotteryEndCountDown() {
		return lotteryEndCountDown;
	}
	public void setLotteryEndCountDown(Integer lotteryEndCountDown) {
		this.lotteryEndCountDown = lotteryEndCountDown;
	}

	
	public BigDecimal getExtraAmount() {
		return extraAmount;
	}
	public void setExtraAmount(BigDecimal extraAmount) {
		this.extraAmount = extraAmount;
	}

	public String getPopularityStratDate() {
		return popularityStratDate;
	}
	public void setPopularityStratDate(String popularityStratDate) {
		this.popularityStratDate = popularityStratDate;
	}

	public String getPopularityEndDate() {
		return popularityEndDate;
	}
	public void setPopularityEndDate(String popularityEndDate) {
		this.popularityEndDate = popularityEndDate;
	}

	public String getPopularity() {
		return popularity;
	}
	public void setPopularity(String popularity) {
		this.popularity = popularity;
	}

	public boolean getPopularityFlag() {
		return popularityFlag;
	}
	public void setPopularityFlag(boolean popularityFlag) {
		this.popularityFlag = popularityFlag;
	}

	public Integer getOnlineCountDown() {
		return onlineCountDown;
	}
	public void setOnlineCountDown(Integer onlineCountDown) {
		this.onlineCountDown = onlineCountDown;
	}

	public String getBorrowerCreditLevel() {
		return borrowerCreditLevel;
	}

	public void setBorrowerCreditLevel(String borrowerCreditLevel) {
		this.borrowerCreditLevel = borrowerCreditLevel;
	}

	public String getBorrowerCreditLevelDes() {
		return borrowerCreditLevelDes;
	}

	public void setBorrowerCreditLevelDes(String borrowerCreditLevelDes) {
		this.borrowerCreditLevelDes = borrowerCreditLevelDes;
	}

	public Integer getPayOffCount() {
		return payOffCount;
	}

	public void setPayOffCount(Integer payOffCount) {
		this.payOffCount = payOffCount;
	}

	public Integer getOverdueCount() {
		return overdueCount;
	}

	public void setOverdueCount(Integer overdueCount) {
		this.overdueCount = overdueCount;
	}

	public BigDecimal getOverdueAmount() {
		return overdueAmount;
	}

	public void setOverdueAmount(BigDecimal overdueAmount) {
		this.overdueAmount = overdueAmount;
	}
}
