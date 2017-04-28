/**
 * 
 */
package com.yourong.core.ic.model.biz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.DateUtils;

/**
 * @author wanglei
 * 推荐快投项目业务展示对象
 *
 */
public class QuickProjectBiz extends AbstractBaseObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 项目id **/
	private Long id;
	
	/** 项目名称 **/
	private String name;
	
	/** 项目简述 **/
	private String shortDesc;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	/**上线时间(年月日时分秒)**/
	private Date onlineTime;
	
	
	/** 投资总金额 **/
	private BigDecimal totalAmount;
	/** 年华收益率 **/
	private BigDecimal annualizedRate;
	
	/** 投资周期 **/
	private Integer borrowPeriod;

	/** 投资周期单位 **/
	private Integer borrowPeriodType;
	
	/** 项目状态 **/
	private Integer status;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	/**抽奖结束时间(年月日时分秒)**/
	private Date lotteryEndTime;

	/** 奖池总额 **/
	private BigDecimal extraAmount;
	

	/** 上线倒计时 **/
	private Integer onlineCountDown;
	
	/** 抽奖结束倒计时 **/
	private Integer lotteryEndCountDown;
	
	/** 项目缩略图 **/
	private String thumbnail;
	
	
	/**
	 * 中奖名单
	 */
	private List<LotteryRewardBiz> lotteryRewardList;
	
	/** 人气值奖励 开始时间 **/
	private String popularityStratDate;
	
	/** 人气值奖励  结束时间 **/
	private String popularityEndDate;
	
	/** 人气值 **/
	private String popularity; 
	
	/** 人气值开关 **/
	private boolean popularityFlag;
	
	/** 项目名 期数 **/
	private String prefixProjectName;
	
	/** 项目名  **/
	private String suffixProjectName;
	
	/** 项目加息时 所加利率 **/
	private BigDecimal addRate;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getShortDesc() {
		return shortDesc;
	}
	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}
	
	
	public Date getOnlineTime() {
		return onlineTime;
	}
	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getAnnualizedRate() {
		return annualizedRate;
	}
	public void setAnnualizedRate(BigDecimal annualizedRate) {
		this.annualizedRate = annualizedRate;
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

	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getLotteryEndTime() {
		return lotteryEndTime;
	}
	public void setLotteryEndTime(Date lotteryEndTime) {
		this.lotteryEndTime = lotteryEndTime;
	}

	public BigDecimal getExtraAmount() {
		return extraAmount;
	}
	public void setExtraAmount(BigDecimal extraAmount) {
		this.extraAmount = extraAmount;
	}
	public Integer getOnlineCountDown() {
		return onlineCountDown;
	}
	public void setOnlineCountDown(Integer onlineCountDown) {
		this.onlineCountDown = onlineCountDown;
	}
	public Integer getLotteryEndCountDown() {
		return lotteryEndCountDown;
	}
	public void setLotteryEndCountDown(Integer lotteryEndCountDown) {
		this.lotteryEndCountDown = lotteryEndCountDown;
	}
	
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	public List<LotteryRewardBiz> getLotteryRewardList() {
		return lotteryRewardList;
	}
	public void setLotteryRewardList(List<LotteryRewardBiz> lotteryRewardList) {
		this.lotteryRewardList = lotteryRewardList;
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
	
	public String getPrefixProjectName() {
		if (StringUtils.isNotBlank(name)&& name.contains("期")) {
			return name.substring(0, name.indexOf("期") + 1);
		} else {
			return "";
		}
	}

	public String getSuffixProjectName() {
		if (StringUtils.isNotBlank(name)&&name.contains("期")) {
			return name.substring(name.indexOf("期") + 1);
		} else {
			return name;
		}
	}
	
	
	
	public BigDecimal getAddRate() {
		return addRate;
	}
	public void setAddRate(BigDecimal addRate) {
		this.addRate = addRate;
	}
	
	
}
