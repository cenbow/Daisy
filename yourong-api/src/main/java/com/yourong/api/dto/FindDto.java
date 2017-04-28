/**
 * 
 */
package com.yourong.api.dto;

import java.util.List;

import com.yourong.core.cms.model.Banner;
import com.yourong.core.cms.model.biz.BannerFroAreaBiz;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年5月16日上午9:25:39
 */
public class FindDto {

	/** 用户ID**/
	private Long memberId;
	
	/** 人气值总额 **/
	private int popularity;
	
	/** 今日人气值总额 **/
	private int popularityForCheck;
	
	/** 推荐好友数量 **/
	private int recommendNum;
	
	private boolean  checked;
	
	private boolean  birth;
	
	private String realName;
	
	private Integer quickLotteryNum;

	List<BannerFroAreaBiz> activityBannerList;
	
	List<Icon> iconList;
	
	private Integer unreadMessage;
	
	/**
	 * @return the memberIds
	 */
	public Long getMemberId() {
		return memberId;
	}

	/**
	 * @param memberId the memberId to set
	 */
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	/**
	 * @return the popularity
	 */
	public int getPopularity() {
		return popularity;
	}

	/**
	 * @param popularity the popularity to set
	 */
	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}

	/**
	 * @return the popularityForCheck
	 */
	public int getPopularityForCheck() {
		return popularityForCheck;
	}

	/**
	 * @param popularityForCheck the popularityForCheck to set
	 */
	public void setPopularityForCheck(int popularityForCheck) {
		this.popularityForCheck = popularityForCheck;
	}

	/**
	 * @return the recommendNum
	 */
	public int getRecommendNum() {
		return recommendNum;
	}

	/**
	 * @param recommendNum the recommendNum to set
	 */
	public void setRecommendNum(int recommendNum) {
		this.recommendNum = recommendNum;
	}

	/**
	 * @return the checked
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * @param checked the checked to set
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	/**
	 * @return the activityBannerList
	 */
	public List<BannerFroAreaBiz> getActivityBannerList() {
		return activityBannerList;
	}


	public Integer getQuickLotteryNum() {
		return quickLotteryNum;
	}

	public void setQuickLotteryNum(Integer quickLotteryNum) {
		this.quickLotteryNum = quickLotteryNum;
	}
	/**
	 * @param activityBannerList the activityBannerList to set
	 */
	public void setActivityBannerList(List<BannerFroAreaBiz> activityBannerList) {
		this.activityBannerList = activityBannerList;
	}

	/**
	 * @return the birth
	 */
	public boolean isBirth() {
		return birth;
	}

	/**
	 * @param birth the birth to set
	 */
	public void setBirth(boolean birth) {
		this.birth = birth;
	}

	/**
	 * @return the iconList
	 */
	public List<Icon> getIconList() {
		return iconList;
	}

	/**
	 * @param iconList the iconList to set
	 */
	public void setIconList(List<Icon> iconList) {
		this.iconList = iconList;
	}

	/**
	 * @return the realName
	 */
	public String getRealName() {
		return realName;
	}

	/**
	 * @param realName the realName to set
	 */
	public void setRealName(String realName) {
		this.realName = realName;
	}

	/**
	 * @return the unreadMessage
	 */
	public Integer getUnreadMessage() {
		return unreadMessage;
	}

	/**
	 * @param unreadMessage the unreadMessage to set
	 */
	public void setUnreadMessage(Integer unreadMessage) {
		this.unreadMessage = unreadMessage;
	}
	
}
