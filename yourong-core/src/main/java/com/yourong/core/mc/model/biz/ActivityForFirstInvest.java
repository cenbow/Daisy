package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.StringUtil;

/**
 * 一马当先活动专题
 * 
 * @author Leon Ray 2014年12月12日-下午2:35:02
 */
public class ActivityForFirstInvest extends AbstractBaseObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 会员id
	 */
	private Long memberId;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 手机号
	 */
	private Long mobile;

	/**
	 * 用户头像url
	 */
	private String avatars;

	/**
	 * 人气值数量
	 */
	private int popularityNum;

	/**
	 * 项目id
	 */
	private Long projectId;

	/**
	 * 项目名称
	 */
	private String projectName;

	/**
	 * 项目状态
	 */
	private Integer status;

	/**
	 * 交易额
	 */
	private BigDecimal investAmount;
	
	/**
	 * 交易时间
	 */
	private Date transactionTime;
	
	/**
	 * 项目上线时间
	 */
	private Date onlineTime;
	
	/**
	 * 项目的投资的总笔数
	 */
    private Integer investCount;

    private String firstInvestInterval;
    
    /**人气值备注**/
    private String remark;

	public Date getTransactionTime() {
		return transactionTime;
	}

	public Date getOnlineTime() {
		return onlineTime;
	}

	public void setFirstInvestInterval(String firstInvestInterval) {
		this.firstInvestInterval = firstInvestInterval;
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

	public void setUsername(String username) {
		this.username = username;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public int getPopularityNum() {
		return popularityNum;
	}

	public void setPopularityNum(int popularityNum) {
		this.popularityNum = popularityNum;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName.substring(0, projectName.indexOf("期") + 1);
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getAvatars() {
		return avatars;
	}

	public void setAvatars(String avatars) {
		this.avatars = avatars;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getInvestAmount() {
		return FormulaUtil.getFormatPrice(investAmount);
	}

	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}

	public void setTransactionTime(Date transactionTime) {
		this.transactionTime = transactionTime;
	}

	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}

	public Integer getInvestCount() {
		return investCount;
	}

	public void setInvestCount(Integer investCount) {
		this.investCount = investCount;
	}
	
	/**
	 * 用户抢标时间
	 */
	public String getFirstInvestInterval(){
		StringBuffer timeStr = new StringBuffer();
		if (transactionTime != null && onlineTime != null) {
			long diff = DateUtils.getTimeIntervalSencond(onlineTime, transactionTime);
			long hh, mm, ss;
			hh = diff / 3600;
			mm = (diff / 60) % 60;
			ss = diff % 60;
			if (hh > 0) {
				timeStr.append(hh + "小时");
			}
			if (mm > 0) {
				timeStr.append(mm + "分钟");
			}
			if (ss > 0) {
				timeStr.append(ss + "秒");
			}
		}
		if(timeStr.toString().length()>0){
			return timeStr.toString();
		}else{
			return "0";
		}
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
