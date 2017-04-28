package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.ic.model.biz.ProjectForFront;

/**
 * 2015年春节活动
 * 
 * @author wangyanji
 */
public class ActivityForSpringFestival extends AbstractBaseObject {

	private Long activityId;

	/**
	 * 开始时间
	 */
	private Date startTime;

	/**
	 * 结束时间
	 */
	private Date endTime;

	/**
	 * 活动状态
	 */
	private Integer activityStatus;

	/**
	 * 除夕领取开始时间
	 */
	private Date receiveStartTime;

	/**
	 * 除夕领取结束时间
	 */
	private Date receiveEndTime;

	/**
	 * 是否许愿
	 */
	private boolean hasMakeWish;

	/**
	 * 是否领券
	 */
	private boolean hasReceiveCoupon;

	/**
	 * 兑换返人气值
	 */
	private List<ActivityLotteryResultBiz> rechargePopularityList;

	/**
	 * 许愿列表
	 */
	private List<ActivityLotteryResultBiz> wishList;

	/**
	 * 新春专享投资列表
	 */
	private List<ActivityLotteryResultBiz> sprProInvestList;

	/**
	 * 现金券模板
	 */
	private Long couponTemplateId;

	/**
	 * 如意项目返利1
	 */
	private BigDecimal wishesProLevel1;

	/**
	 * 如意项目返利投金额
	 */
	private Integer wishesProInvest;

	/**
	 * 如意项目返利2
	 */
	private BigDecimal wishesProLevel2;

	/**
	 * 领取压岁钱的人数
	 */
	private Integer receiveCouponNum;

	/**
	 * 如意推荐项目
	 */
	private ProjectForFront recommendProject;

	private String totalNum;

	private String claimNum;

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getReceiveStartTime() {
		return receiveStartTime;
	}

	public void setReceiveStartTime(Date receiveStartTime) {
		this.receiveStartTime = receiveStartTime;
	}

	public boolean isHasMakeWish() {
		return hasMakeWish;
	}

	public void setHasMakeWish(boolean hasMakeWish) {
		this.hasMakeWish = hasMakeWish;
	}

	public boolean isHasReceiveCoupon() {
		return hasReceiveCoupon;
	}

	public void setHasReceiveCoupon(boolean hasReceiveCoupon) {
		this.hasReceiveCoupon = hasReceiveCoupon;
	}

	public List<ActivityLotteryResultBiz> getRechargePopularityList() {
		return rechargePopularityList;
	}

	public void setRechargePopularityList(List<ActivityLotteryResultBiz> rechargePopularityList) {
		this.rechargePopularityList = rechargePopularityList;
	}

	public List<ActivityLotteryResultBiz> getWishList() {
		return wishList;
	}

	public void setWishList(List<ActivityLotteryResultBiz> wishList) {
		this.wishList = wishList;
	}

	public List<ActivityLotteryResultBiz> getSprProInvestList() {
		return sprProInvestList;
	}

	public void setSprProInvestList(List<ActivityLotteryResultBiz> sprProInvestList) {
		this.sprProInvestList = sprProInvestList;
	}

	public Integer getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(Integer activityStatus) {
		this.activityStatus = activityStatus;
	}

	public Date getReceiveEndTime() {
		return receiveEndTime;
	}

	public void setReceiveEndTime(Date receiveEndTime) {
		this.receiveEndTime = receiveEndTime;
	}

	public Long getCouponTemplateId() {
		return couponTemplateId;
	}

	public void setCouponTemplateId(Long couponTemplateId) {
		this.couponTemplateId = couponTemplateId;
	}

	public BigDecimal getWishesProLevel1() {
		return wishesProLevel1;
	}

	public void setWishesProLevel1(BigDecimal wishesProLevel1) {
		this.wishesProLevel1 = wishesProLevel1;
	}

	public Integer getWishesProInvest() {
		return wishesProInvest;
	}

	public void setWishesProInvest(Integer wishesProInvest) {
		this.wishesProInvest = wishesProInvest;
	}

	public BigDecimal getWishesProLevel2() {
		return wishesProLevel2;
	}

	public void setWishesProLevel2(BigDecimal wishesProLevel2) {
		this.wishesProLevel2 = wishesProLevel2;
	}

	public Integer getReceiveCouponNum() {
		return receiveCouponNum;
	}

	public void setReceiveCouponNum(Integer receiveCouponNum) {
		this.receiveCouponNum = receiveCouponNum;
	}

	public ProjectForFront getRecommendProject() {
		return recommendProject;
	}

	public void setRecommendProject(ProjectForFront recommendProject) {
		this.recommendProject = recommendProject;
	}

	public String getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(String totalNum) {
		this.totalNum = totalNum;
	}

	public String getClaimNum() {
		return claimNum;
	}

	public void setClaimNum(String claimNum) {
		this.claimNum = claimNum;
	}

}
