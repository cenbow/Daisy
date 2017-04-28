package com.yourong.core.tc.model.biz;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.StringUtil;

public class TransactionForFirstInvestAct implements Serializable {

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

    /**投资金额**/
    private BigDecimal totalInvest;
    
    
    private Integer number;
    
    private Date dayTime;
    
    

	public Date getDayTime() {
		return dayTime;
	}

	public void setDayTime(Date dayTime) {
		this.dayTime = dayTime;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
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

	public BigDecimal getTotalInvest() {
		return totalInvest;
	}

	public void setTotalInvest(BigDecimal totalInvest) {
		this.totalInvest = totalInvest;
	}
	
	public String getTotalInvestFormat() {
		if(totalInvest!=null){
			return FormulaUtil.getFormatPrice(totalInvest);
		}
		return null;
	}

}