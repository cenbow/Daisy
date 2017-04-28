package com.yourong.core.sys.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 网站顶部公告
 * @author zhanghao
 *
 */
public class WebInfo {

	/** 开始时间 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date startDate;
	
	/** 结束时间 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date endDate;   
	
	/** 状态：0-关、1：开**/
	private Integer webClient;

	/** 状态：0-关、1：开 **/
	private Integer mobClient;
	
	/** 链接 **/
	private String href;	
	
	/**公告内容**/
    private String content;

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	
	/**
	 * @return the webClient
	 */
	public Integer getWebClient() {
		return webClient;
	}

	/**
	 * @param webClient the webClient to set
	 */
	public void setWebClient(Integer webClient) {
		this.webClient = webClient;
	}

	/**
	 * @return the mobClient
	 */
	public Integer getMobClient() {
		return mobClient;
	}

	/**
	 * @param mobClient the mobClient to set
	 */
	public void setMobClient(Integer mobClient) {
		this.mobClient = mobClient;
	}

	/**
	 * @return the href
	 */
	public String getHref() {
		return href;
	}

	/**
	 * @param href the href to set
	 */
	public void setHref(String href) {
		this.href = href;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	

}