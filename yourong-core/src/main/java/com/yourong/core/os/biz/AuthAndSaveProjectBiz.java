package com.yourong.core.os.biz;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.yourong.common.domain.ValidAnnotationForOpen;

public class AuthAndSaveProjectBiz extends ValidAnnotationForOpen {

	/**
	 * 渠道商
	 */
	@NotNull
	private String channelKey;

	/**
	 * 外部业务号
	 */
	@NotNull
	private String outBizNo;

	/**
	 * 借款人IP
	 */
	@NotNull
	private String ip;
	/**
	 * 借款人手机号
	 */
	@NotNull
	private Long mobile;
	/**
	 * 借款人姓名
	 */
	@NotNull
	private String trueName;
	/**
	 * 借款人身份证件号码
	 */
	@NotNull
	private String identityNumber;
	/**
	 * 借款金额
	 */
	@NotNull
	private BigDecimal totalAmount;
	/**
	 * 年化利率
	 */
	@NotNull
	private BigDecimal annualizedRate;
	/**
	 * 借款周期
	 */
	@NotNull
	private Integer borrowPeriod;
	/**
	 * 借款周期类型
	 */
	@NotNull
	private Integer borrowPeriodType;
	/**
	 * 借款人收入
	 */
	private Integer borrowerIncome;

	/**
	 * 借款人职业
	 */
	private String borrowerJob;

	/**
	 * 借款人具体身份信息
	 */
	private String borrowerBasicInfo;

	/**
	 * 项目缩略图
	 */
	private FileBiz attachmentThumbnail;
	/**
	 * 项目形象图
	 */
	private FileBiz attachmentImage;
	/**
	 * 个人相关图片
	 */
	private List<FileBiz> attachmentsPersonal;
	/**
	 * 借款人相关(身份证正反面)图片
	 */
	@NotNull
	private List<FileBiz> attachmentsBorrower;
	/**
	 * 合同相关图片
	 */
	private List<FileBiz> attachmentsContract;
	/**
	 * 法律相关图片
	 */
	private List<FileBiz> attachmentsLegal;
	/**
	 * 征信相关图片
	 */
	private List<FileBiz> attachmentsCredit;
	/**
	 * 其他相关图片
	 */
	private List<FileBiz> attachmentsOther;
	/**
	 * 商品sku
	 */
	private String sku;

	public String getOutBizNo() {
		return outBizNo;
	}

	public void setOutBizNo(String outBizNo) {
		this.outBizNo = outBizNo;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public String getIdentityNumber() {
		return identityNumber;
	}

	public void setIdentityNumber(String identityNumber) {
		this.identityNumber = identityNumber;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
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

	public Integer getBorrowerIncome() {
		return borrowerIncome;
	}

	public void setBorrowerIncome(Integer borrowerIncome) {
		this.borrowerIncome = borrowerIncome;
	}

	public FileBiz getAttachmentThumbnail() {
		return attachmentThumbnail;
	}

	public void setAttachmentThumbnail(FileBiz attachmentThumbnail) {
		this.attachmentThumbnail = attachmentThumbnail;
	}

	public FileBiz getAttachmentImage() {
		return attachmentImage;
	}

	public void setAttachmentImage(FileBiz attachmentImage) {
		this.attachmentImage = attachmentImage;
	}

	public List<FileBiz> getAttachmentsPersonal() {
		return attachmentsPersonal;
	}

	public void setAttachmentsPersonal(List<FileBiz> attachmentsPersonal) {
		this.attachmentsPersonal = attachmentsPersonal;
	}

	public List<FileBiz> getAttachmentsBorrower() {
		return attachmentsBorrower;
	}

	public void setAttachmentsBorrower(List<FileBiz> attachmentsBorrower) {
		this.attachmentsBorrower = attachmentsBorrower;
	}

	public List<FileBiz> getAttachmentsContract() {
		return attachmentsContract;
	}

	public void setAttachmentsContract(List<FileBiz> attachmentsContract) {
		this.attachmentsContract = attachmentsContract;
	}

	public List<FileBiz> getAttachmentsLegal() {
		return attachmentsLegal;
	}

	public void setAttachmentsLegal(List<FileBiz> attachmentsLegal) {
		this.attachmentsLegal = attachmentsLegal;
	}

	public List<FileBiz> getAttachmentsCredit() {
		return attachmentsCredit;
	}

	public void setAttachmentsCredit(List<FileBiz> attachmentsCredit) {
		this.attachmentsCredit = attachmentsCredit;
	}

	public List<FileBiz> getAttachmentsOther() {
		return attachmentsOther;
	}

	public void setAttachmentsOther(List<FileBiz> attachmentsOther) {
		this.attachmentsOther = attachmentsOther;
	}

	public String getBorrowerJob() {
		return borrowerJob;
	}

	public void setBorrowerJob(String borrowerJob) {
		this.borrowerJob = borrowerJob;
	}

	public String getBorrowerBasicInfo() {
		return borrowerBasicInfo;
	}

	public void setBorrowerBasicInfo(String borrowerBasicInfo) {
		this.borrowerBasicInfo = borrowerBasicInfo;
	}

	public BigDecimal getAnnualizedRate() {
		return annualizedRate;
	}

	public void setAnnualizedRate(BigDecimal annualizedRate) {
		this.annualizedRate = annualizedRate;
	}

	public String getChannelKey() {
		return channelKey;
	}

	public void setChannelKey(String channelKey) {
		this.channelKey = channelKey;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}
}
