package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.fin.model.OverdueRepayLog;
import com.yourong.core.ic.model.DebtCollateral;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.ProjectInterest;
import com.yourong.core.uc.model.Enterprise;
import com.yourong.core.uc.model.MemberBaseBiz;
import com.yourong.api.dto.EnterpriseProjectInfoDto;

public class ProjectDetailDto {
	private Long id;
	/**项目描述**/
    private String shortDesc;
    
    /**项目描述，保障措施**/
    private String description;
    
    /**债权**/
    private DebtDto debtDto;
    
    /**借款人附件**/
	private List<BscAttachment> borrowMemberAttachments = Lists.newArrayList();
	
	/**原始债权人附件**/
	private List<BscAttachment> lenderMemberAttachments = Lists.newArrayList();
	
	/**质押或抵押物附件**/
	private List<BscAttachment> collateralAttachments = Lists.newArrayList();
	
	/** p2p-合同附件 **/
	private List<BscAttachment> contractAttachments = Lists.newArrayList();
	
	/** p2p-项目形象图 **/
	private List<BscAttachment> signAttachments = Lists.newArrayList();
	
	/** p2p-合同附件名称 **/
	private List<String> contractCategoryName = Lists.newArrayList();
	
	/**p2p-企业的项目信息**/
    private EnterpriseProjectInfoDto enterpriseProjectInfoDto;
    
    /** P2P-担保物的详细信息 */
	private DebtCollateral debtCollateral;
	
	/** p2p-项目开始日期(年月日) **/
	private Date startDate;

	/** p2p-还款时间(年月日) **/
	private Date endDate;
	
	/** p2p-本息表 */
	private List<DebtInterest> interests;
	
	/** p2p-借款人信息 */
	private MemberBaseBiz borrowMemberBiz;
	
	/** p2p-借款人为企业时显示的信息 **/
	private Enterprise enterprise;
	
	/** p2p-阶梯收益 **/
	private List<ProjectInterest> projectInterestList;
	
	/**状态码(1:存盘,10:待审核,20:待发布,30:投资中,40:已暂停,50:已满额,60:已截止,70:已还款)**/
    private Integer status;
    
    /**借款人类型（1：个人；2-企业）**/
    private Integer borrowerType;
    
    /**项目类型code**/
    private String projectType;
    
    /** 担保方式（pledge-质押；collateral-抵押；credit-信用） **/
	private String securityType;
	
	/**项目描述，保障措施**/
    private String p2pDescription;
    
    private Integer investType;

	private String openPlatformKey;
    
    /**逾期结算记录**/
	private List<OverdueRepayLog> overdueRepayBiz;

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getShortDesc() {
		return shortDesc;
	}

	public List<OverdueRepayLog> getOverdueRepayBiz() {
		return overdueRepayBiz;
	}

	public void setOverdueRepayBiz(List<OverdueRepayLog> overdueRepayBiz) {
		this.overdueRepayBiz = overdueRepayBiz;
	}

	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}

	public String getDescription() {
		if(description != null){
			return formatDescription(description);
		}
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public void setBorrowMemberAttachments(
			List<BscAttachment> borrowMemberAttachments) {
		this.borrowMemberAttachments = borrowMemberAttachments;
	}

	public List<BscAttachment> getLenderMemberAttachments() {
		return lenderMemberAttachments;
	}

	public void setLenderMemberAttachments(
			List<BscAttachment> lenderMemberAttachments) {
		this.lenderMemberAttachments = lenderMemberAttachments;
	}

	public List<BscAttachment> getCollateralAttachments() {
		return collateralAttachments;
	}

	public void setCollateralAttachments(List<BscAttachment> collateralAttachments) {
		this.collateralAttachments = collateralAttachments;
	}
	
	
	
	private String formatDescription(String description){
		StringBuffer sb = new StringBuffer();
		Document doc = Jsoup.parse(description);
        Elements trs = doc.select("table").select("tr");
        for(int i = 0;i<trs.size();i++){
            Elements tds = trs.get(i).select("td");
            for(int j = 0;j<tds.size();j++){
                String text = tds.get(j).text();
                if(j==0){
                	sb.append("<h3>").append(text).append("</h3>");
                }else{
                	sb.append("<p>").append(text).append("</p>");
                }
            }
        }
        return sb.toString();
	}

	public EnterpriseProjectInfoDto getEnterpriseProjectInfoDto() {
		return enterpriseProjectInfoDto;
	}

	public void setEnterpriseProjectInfoDto(EnterpriseProjectInfoDto enterpriseProjectInfoDto) {
		this.enterpriseProjectInfoDto = enterpriseProjectInfoDto;
	}

	public List<BscAttachment> getContractAttachments() {
		return contractAttachments;
	}

	public void setContractAttachments(List<BscAttachment> contractAttachments) {
		this.contractAttachments = contractAttachments;
	}

	public List<BscAttachment> getSignAttachments() {
		return signAttachments;
	}

	public void setSignAttachments(List<BscAttachment> signAttachments) {
		this.signAttachments = signAttachments;
	}

	public List<String> getContractCategoryName() {
		return contractCategoryName;
	}

	public void setContractCategoryName(List<String> contractCategoryName) {
		this.contractCategoryName = contractCategoryName;
	}

	public DebtCollateral getDebtCollateral() {
		return debtCollateral;
	}

	public void setDebtCollateral(DebtCollateral debtCollateral) {
		this.debtCollateral = debtCollateral;
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

	public List<DebtInterest> getInterests() {
		return interests;
	}

	public void setInterests(List<DebtInterest> interests) {
		this.interests = interests;
	}

	public MemberBaseBiz getBorrowMemberBiz() {
		return borrowMemberBiz;
	}

	public void setBorrowMemberBiz(MemberBaseBiz borrowMemberBiz) {
		this.borrowMemberBiz = borrowMemberBiz;
	}

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public List<ProjectInterest> getProjectInterestList() {
		return projectInterestList;
	}

	public void setProjectInterestList(List<ProjectInterest> projectInterestList) {
		this.projectInterestList = projectInterestList;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getBorrowerType() {
		return borrowerType;
	}

	public void setBorrowerType(Integer borrowerType) {
		this.borrowerType = borrowerType;
	}

	public boolean isCompany(){
		if (this.borrowerType == TypeEnum.MEMBER_TYPE_COMPANY.getType()){
			return true;
		}
		return false;
	}
	
	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
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

	public String getSecurityType() {
		return securityType;
	}

	public void setSecurityType(String securityType) {
		this.securityType = securityType;
	}

	public String getP2pDescription() {
		return p2pDescription;
	}

	public void setP2pDescription(String p2pDescription) {
		this.p2pDescription = p2pDescription;
	}

	public Integer getInvestType() {
		return investType;
	}

	public void setInvestType(Integer investType) {
		this.investType = investType;
	}

	public String getOpenPlatformKey() {
		return openPlatformKey;
	}

	public void setOpenPlatformKey(String openPlatformKey) {
		this.openPlatformKey = openPlatformKey;
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
