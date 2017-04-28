package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.Date;

import ch.qos.logback.core.net.ssl.SSL;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;

/**
 * 一羊领头专题页项目类
 * 
 * @author fuyili
 *
 *         创建时间:2015年7月7日下午2:19:28
 */
public class ActivityLeadingSheepProject extends AbstractBaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 项目id
	 */
	private Long id;
	/**
	 * 项目名称
	 */
	private String name;
	/**
	 * 项目状态
	 */
	private Integer status;
	/**
	 * 项目投资总额
	 */
	private BigDecimal totalAmount;
	/**
	 * 项目缩略图
	 */
	private String thumbnail;
	/**
	 * 项目进度条
	 */
	private String progress;
	/**
	 * 项目上线时间
	 */
	private Date onlineTime;

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
	 * 用户头像
	 */
	private String avatars;

	public String getAvatars() {
		return avatars;
	}

	public void setAvatars(String avatars) {
		this.avatars = avatars;
	}

	/**
	 * 是否是预告
	 */
	private boolean isNotice;

	/**
	 * 第一笔交易时间
	 */
	private Date transactionTime;

	/**
	 * 交易额
	 */
	private BigDecimal investAmount;

	/**
	 * 项目总共交易几笔
	 */
	private Integer investCount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public boolean isNotice() {
		return isNotice;
	}

	public void setNotice(boolean isNotice) {
		this.isNotice = isNotice;
	}

	public void setTransactionTime(Date transactionTime) {
		this.transactionTime = transactionTime;
	}

	/**
	 * 获取抢标用时
	 */
	public String getFirstInvestInterval() {
		StringBuffer timeStr = new StringBuffer();
		if (transactionTime != null && onlineTime != null) {
//			long diff = DateUtils.getTimeIntervalSencond(onlineTime, transactionTime);
//			long hh, mm, ss;
//			hh = diff / 3600;
//			mm = (diff / 60) % 60;
//			ss = diff % 60;
//			if (hh > 0) {
//				timeStr.append(hh + "小时");
//			}
//			if ((hh > 0 && mm == 0) || hh > 0) {
//				timeStr.append(mm + "分钟");
//			}
//			if ((mm > 0 && ss == 0) || ss > 0) {
//				timeStr.append(ss + "秒");
//			}
			long diff = DateUtils.getTimeIntervalSencond(onlineTime, transactionTime);
			if(diff>0){
				timeStr.append(diff+"秒");
			}
		}
		return timeStr.toString();
	}

	public String getInvestAmount() {
		return FormulaUtil.getFormatPrice(investAmount);
	}

	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}

	public Integer getInvestCount() {
		return investCount;
	}

	public void setInvestCount(Integer investCount) {
		this.investCount = investCount;
	}

	public String getPrefixProjectName() {
		if (name.contains("期")) {
			return name.substring(0, name.indexOf("期") + 1);
		} else {
			return "";
		}
	}

	public String getSuffixProjectName() {
		if (name.contains("期")) {
			return name.substring(name.indexOf("期") + 1);
		} else {
			return name;
		}
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Date getOnlineTime() {
		return onlineTime;
	}
}
