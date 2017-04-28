package com.yourong.core.mc.model.biz;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.StringUtil;

public class ActivityForMemberInfo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4296157459652549655L;

	/**用户ID**/
    private Long memberId;
    
    private String username;
    
    private Long mobile;
    /**用户头像**/
    private String avatars;

    
    private Date happenTime;
    
    private String rewardInfo;
    
    private BigDecimal totalInvestAmount;
    
    
    
    


	public BigDecimal getTotalInvestAmount() {
		return totalInvestAmount;
	}

	public void setTotalInvestAmount(BigDecimal totalInvestAmount) {
		this.totalInvestAmount = totalInvestAmount;
	}

	/**
	 * @return the rewardInfo
	 */
	public String getRewardInfo() {
		return rewardInfo;
	}

	/**
	 * @param rewardInfo the rewardInfo to set
	 */
	public void setRewardInfo(String rewardInfo) {
		this.rewardInfo = rewardInfo;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getUsername() {
		return StringUtil.maskUserNameOrMobile(username, mobile);
	}
	
	public String getUsernames() {
		if(username!=null&&username.contains("*")){
			return username;
		}
		if(mobile!=null&&mobile.toString().contains("*")){
			return mobile.toString();
		}
		return StringUtil.maskUserNameOrMobile(username, mobile);
	}

	public void setUsername(String username) {
		this.username = username;
	}


	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public String getAvatars() {
		return avatars;
	}

	public void setAvatars(String avatars) {
		this.avatars = avatars;
	}

	/**
	 * @return the happenTime
	 */
	public Date getHappenTime() {
		return happenTime;
	}

	/**
	 * @param happenTime the happenTime to set
	 */
	public void setHappenTime(Date happenTime) {
		this.happenTime = happenTime;
	}

	
}