package com.yourong.core.mc.model.biz;

import java.util.Date;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 里程拉新
 * 
 * @author wangyanji
 */
public class ActivityForFriendShip extends AbstractBaseObject{

	private Date startTime;

	private Integer activityStatus;

	private Integer totalKm;

	private Integer packages;

	private Integer boxes;

	private String receiveReturnStr;

	private List<ActivityLotteryResultBiz> topList;

	private List<ActivityLotteryResultBiz> friendList;

	private Integer friendListCount;

	private Integer rewardType;

	private Integer totalPop;

	private Integer totalCoupon;

	private String avatar;

	/******************** 活动参数 *******************************/

	private Integer firstInvest;

	private Long firstInvestCoupon;

	private Integer firstInvestCouponValue;

	private Integer bindWeChat;

	private Integer bindApp;
	
	private Integer improveMemberInfo;
	
	private Integer bindEmail;

	private List<Integer> packagesRange;

	private List<List<Long>> boxesRange;

	private List<String> boxesStr;

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Integer getFirstInvest() {
		return firstInvest;
	}

	public void setFirstInvest(Integer firstInvest) {
		this.firstInvest = firstInvest;
	}

	public Integer getBindWeChat() {
		return bindWeChat;
	}

	public void setBindWeChat(Integer bindWeChat) {
		this.bindWeChat = bindWeChat;
	}

	public Integer getBindApp() {
		return bindApp;
	}

	public void setBindApp(Integer bindApp) {
		this.bindApp = bindApp;
	}

	public Integer getImproveMemberInfo() {
		return improveMemberInfo;
	}

	public void setImproveMemberInfo(Integer improveMemberInfo) {
		this.improveMemberInfo = improveMemberInfo;
	}

	public Integer getBindEmail() {
		return bindEmail;
	}

	public void setBindEmail(Integer bindEmail) {
		this.bindEmail = bindEmail;
	}

	public Long getFirstInvestCoupon() {
		return firstInvestCoupon;
	}

	public void setFirstInvestCoupon(Long firstInvestCoupon) {
		this.firstInvestCoupon = firstInvestCoupon;
	}

	public Integer getFirstInvestCouponValue() {
		return firstInvestCouponValue;
	}

	public void setFirstInvestCouponValue(Integer firstInvestCouponValue) {
		this.firstInvestCouponValue = firstInvestCouponValue;
	}

	public Integer getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(Integer activityStatus) {
		this.activityStatus = activityStatus;
	}

	public Integer getPackages() {
		return packages;
	}

	public void setPackages(Integer packages) {
		this.packages = packages;
	}

	public Integer getBoxes() {
		return boxes;
	}

	public void setBoxes(Integer boxes) {
		this.boxes = boxes;
	}

	public List<ActivityLotteryResultBiz> getTopList() {
		return topList;
	}

	public void setTopList(List<ActivityLotteryResultBiz> topList) {
		this.topList = topList;
	}

	public Integer getTotalKm() {
		return totalKm;
	}

	public void setTotalKm(Integer totalKm) {
		this.totalKm = totalKm;
	}

	public List<Integer> getPackagesRange() {
		return packagesRange;
	}

	public void setPackagesRange(List<Integer> packagesRange) {
		this.packagesRange = packagesRange;
	}

	public List<List<Long>> getBoxesRange() {
		return boxesRange;
	}

	public void setBoxesRange(List<List<Long>> boxesRange) {
		this.boxesRange = boxesRange;
	}

	public String getReceiveReturnStr() {
		return receiveReturnStr;
	}

	public void setReceiveReturnStr(String receiveReturnStr) {
		this.receiveReturnStr = receiveReturnStr;
	}

	public List<String> getBoxesStr() {
		return boxesStr;
	}

	public void setBoxesStr(List<String> boxesStr) {
		this.boxesStr = boxesStr;
	}

	public Integer getRewardType() {
		return rewardType;
	}

	public void setRewardType(Integer rewardType) {
		this.rewardType = rewardType;
	}

	public Integer getTotalPop() {
		return totalPop;
	}

	public void setTotalPop(Integer totalPop) {
		this.totalPop = totalPop;
	}

	public Integer getTotalCoupon() {
		return totalCoupon;
	}

	public void setTotalCoupon(Integer totalCoupon) {
		this.totalCoupon = totalCoupon;
	}

	public List<ActivityLotteryResultBiz> getFriendList() {
		return friendList;
	}

	public void setFriendList(List<ActivityLotteryResultBiz> friendList) {
		this.friendList = friendList;
	}

	public Integer getFriendListCount() {
		return friendListCount;
	}

	public void setFriendListCount(Integer friendListCount) {
		this.friendListCount = friendListCount;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
}
