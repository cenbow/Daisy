package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.List;

import com.yourong.core.cms.model.CmsArticle;

public class IndexDto {
	private List<IndexProjectDto> projectList;
	private List<BannerDto> bannerList;
	
	private List<IconDto> iconList ;
	private BigDecimal paltformTotalInvest = BigDecimal.ZERO;
	private Long paltformMembers = 0L;
	private Integer friends = 0;
	
	private String shareContent;
	private String title;
	private String shareUrl;
	 /**唯一标识用户的短链接url**/
    private String shortUrl;
	
	private List<CmsArticle> articles;
	
	private CmsArticle noticeArticles;
	
	private String platTotalInvest;
	
	private String platMembers;
	
	/**
	 * App首页广告
	 */
	private List<BannerDto> appIndexAdList;
	
	/**
	 * App首页弹层
	 */
	private List<BannerDto> appIndexPopupList;
	
	/**
	 * 是否签到
	 */
	private boolean  checked;
	
	/**
	 * 是否签到
	 */
	private boolean  birth;
	
	
	public List<IndexProjectDto> getProjectList() {
		return projectList;
	}
	public void setProjectList(List<IndexProjectDto> projectList) {
		this.projectList = projectList;
	}
	public List<BannerDto> getBannerList() {
		return bannerList;
	}
	public void setBannerList(List<BannerDto> bannerList) {
		this.bannerList = bannerList;
	}
	public BigDecimal getPaltformTotalInvest() {
		return paltformTotalInvest;
	}
	public void setPaltformTotalInvest(BigDecimal paltformTotalInvest) {
		this.paltformTotalInvest = paltformTotalInvest;
	}
	public Long getPaltformMembers() {
		return paltformMembers;
	}
	public void setPaltformMembers(Long paltformMembers) {
		this.paltformMembers = paltformMembers;
	}
	public Integer getFriends() {
		return friends;
	}
	public void setFriends(Integer friends) {
		this.friends = friends;
	}
	public String getShareContent() {
		return shareContent;
	}
	public void setShareContent(String shareContent) {
		this.shareContent = shareContent;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getShareUrl() {
		return shareUrl;
	}
	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
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
	 * @return the articles
	 */
	public List<CmsArticle> getArticles() {
		return articles;
	}
	/**
	 * @param articles the articles to set
	 */
	public void setArticles(List<CmsArticle> articles) {
		this.articles = articles;
	}
	/**
	 * @return the noticeArticles
	 */
	public CmsArticle getNoticeArticles() {
		return noticeArticles;
	}
	/**
	 * @param noticeArticles the noticeArticles to set
	 */
	public void setNoticeArticles(CmsArticle noticeArticles) {
		this.noticeArticles = noticeArticles;
	}
	/**
	 * @return the platTotalInvest
	 */
	public String getPlatTotalInvest() {
		return platTotalInvest;
	}
	/**
	 * @param platTotalInvest the platTotalInvest to set
	 */
	public void setPlatTotalInvest(String platTotalInvest) {
		this.platTotalInvest = platTotalInvest;
	}
	/**
	 * @return the platMembers
	 */
	public String getPlatMembers() {
		return platMembers;
	}
	/**
	 * @param platMembers the platMembers to set
	 */
	public void setPlatMembers(String platMembers) {
		this.platMembers = platMembers;
	}
	
	
	
	public List<IconDto> getIconList() {
		return iconList;
	}
	public void setIconList(List<IconDto> iconList) {
		this.iconList = iconList;
	}
	
	
	public List<BannerDto> getAppIndexAdList() {
		return appIndexAdList;
	}
	public void setAppIndexAdList(List<BannerDto> appIndexAdList) {
		this.appIndexAdList = appIndexAdList;
	}
	
	public boolean isBirth() {
		return birth;
	}
	public void setBirth(boolean birth) {
		this.birth = birth;
	}
	public List<BannerDto> getAppIndexPopupList() {
		return appIndexPopupList;
	}
	public void setAppIndexPopupList(List<BannerDto> appIndexPopupList) {
		this.appIndexPopupList = appIndexPopupList;
	}
	public String getShortUrl() {
		return shortUrl;
	}
	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}
	
}
