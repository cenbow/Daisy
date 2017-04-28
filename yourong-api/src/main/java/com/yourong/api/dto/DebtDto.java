package com.yourong.api.dto;

import com.yourong.common.util.StringUtil;
import com.yourong.core.ic.model.DebtCollateral;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.DebtPledge;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberBaseBiz;

import java.util.List;

public class DebtDto {
	
	private Long id;
	
    /**公司债权编号**/
    private String serialNumber;

    /**原始债权编号**/
    private String originalDebtNumber;
    
    /**借款用途**/
    private String loanUse;

    /**保证措施**/
    private String safeguards;
    
    /** 质押物 */
	private DebtPledge debtPledge;
	
	/** 抵押物 */
	private DebtCollateral debtCollateral;
	
	/** 债权本息表 */
	private List<DebtInterest> debtInterests;

	/** 借款人信息 */
	private MemberBaseBiz borrowMemberBaseBiz;
	
	/** 出借人信息 */
	private Member lenderMember;
	
	/** 出借人信息,包含企业信息 */
	private MemberBaseBiz lenderMemberBaseBiz;
	
	/** 质押或抵押的具体类型 **/
	private String guarantyType;
	
	/** 类型code **/
	private String debtType;
	
	/**借款人类型（1:个人用户、2:企业用户）*/
	private Integer borrowerType;
	
	
	/**出借人类型（1:个人用户、2:企业用户）*/
	private Integer lenderType;
	
	private Integer instalment;
	
	/**
	 * 车
	 * @return
	 */
	public boolean getIsCar(){
		if(StringUtil.isNotBlank(guarantyType)){
			if(guarantyType.equals("car") || guarantyType.equals("newCar") || guarantyType.equals("equity")){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 新车融
	 * @return
	 */
	public boolean isNewCar(){
		if(StringUtil.isNotBlank(guarantyType)){
			if(guarantyType.equals("newCar") || guarantyType.equals("equity")){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 抵押
	 * @return
	 */
	public boolean getIsCollateral(){
		if(StringUtil.isNotBlank(debtType)){
			if(debtType.equals("collateral")){
				return true;
			}
		}
		return false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getOriginalDebtNumber() {
		return originalDebtNumber;
	}

	public void setOriginalDebtNumber(String originalDebtNumber) {
		this.originalDebtNumber = originalDebtNumber;
	}

	public String getLoanUse() {
		return loanUse;
	}

	public void setLoanUse(String loanUse) {
		this.loanUse = loanUse;
	}

	public String getSafeguards() {
		return safeguards;
	}

	public void setSafeguards(String safeguards) {
		this.safeguards = safeguards;
	}

	public DebtPledge getDebtPledge() {
		return debtPledge;
	}

	public void setDebtPledge(DebtPledge debtPledge) {
		this.debtPledge = debtPledge;
	}

	public DebtCollateral getDebtCollateral() {
		return debtCollateral;
	}

	public void setDebtCollateral(DebtCollateral debtCollateral) {
		this.debtCollateral = debtCollateral;
	}

	public List<DebtInterest> getDebtInterests() {
		return debtInterests;
	}

	public void setDebtInterests(List<DebtInterest> debtInterests) {
		this.debtInterests = debtInterests;
	}

	public MemberBaseBiz getBorrowMemberBaseBiz() {
		return borrowMemberBaseBiz;
	}

	public void setBorrowMemberBaseBiz(MemberBaseBiz borrowMemberBaseBiz) {
		this.borrowMemberBaseBiz = borrowMemberBaseBiz;
	}
	
	

	/**
	 * @return the lenderMemberBaseBiz
	 */
	public MemberBaseBiz getLenderMemberBaseBiz() {
		return lenderMemberBaseBiz;
	}

	/**
	 * @param lenderMemberBaseBiz the lenderMemberBaseBiz to set
	 */
	public void setLenderMemberBaseBiz(MemberBaseBiz lenderMemberBaseBiz) {
		this.lenderMemberBaseBiz = lenderMemberBaseBiz;
	}

	public Member getLenderMember() {
		return lenderMember;
	}

	public void setLenderMember(Member lenderMember) {
		this.lenderMember = lenderMember;
	}

	public String getGuarantyType() {
		return guarantyType;
	}

	public void setGuarantyType(String guarantyType) {
		this.guarantyType = guarantyType;
	}

	public String getDebtType() {
		return debtType;
	}

	public void setDebtType(String debtType) {
		this.debtType = debtType;
	}
	
	public Integer getBorrowerType() {
		return borrowerType;
	}

	public void setBorrowerType(Integer borrowerType) {
		this.borrowerType = borrowerType;
	}

	/**
	 * @return the lenderType
	 */
	public Integer getLenderType() {
		return lenderType;
	}

	/**
	 * @param lenderType the lenderType to set
	 */
	public void setLenderType(Integer lenderType) {
		this.lenderType = lenderType;
	}

	/**
	 * 借款人是否企业
	 * @return
	 */
	public boolean isEnterprise(){
		if(borrowerType != null && borrowerType == 2){
			return true;
		}
		return false;
	}
	
	/**
	 * 出借人是否企业
	 * @return
	 */
	public boolean isLenderEnterprise(){
		if(lenderType != null && lenderType == 2){
			return true;
		}
		return false;
	}
	
	/**
	 * 是否信用债权
	 */
	public boolean getIsCreditDebt(){
		if(getDebtType() != null && getDebtType().equals("credit")){
			return true;
		}
		return false;
	}
	
	public boolean getIsCarPayInDebt(){
		if(guarantyType != null && guarantyType.equals("carPayIn")){
			return true;
		}
		return false;
	}
	
	public boolean getIsCarBusinessDebt(){
		if(guarantyType != null && guarantyType.equals("carBusiness")){
			return true;
		}
		return false;
	}
	
	/**
	 * 是否为经营融
	 */
	public boolean getIsRunCompany(){
		if(guarantyType != null && guarantyType.equals("runCompany")){
			return true;
		}
		return false;
	}

	public Integer getInstalment() {
		return instalment;
	}

	public void setInstalment(Integer instalment) {
		this.instalment = instalment;
	}
}
