package com.yourong.api.service;

import java.util.List;

import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.Banner;
import com.yourong.core.cms.model.biz.BannerFroAreaBiz;
import com.yourong.core.ic.model.query.BannerQuery;

public interface BannerService {
	
	List<Banner> findPcOnlineBanner();
	
	List<Banner> findAppOnlineBanner();
	
	List<Banner> findAppFindBanner();
	
	List<Banner> findAppOnlineBannerForVersion(Integer version);
	
	List<Banner> findMOnlineBanner();
	
	public List<Banner> findAppActivityBanner();
	
	public Page<BannerFroAreaBiz> findAppActivityBannerByPage(BannerQuery bannerQuery) ;

	/**
	 * app首页广告
	 * @return
	 */
	List<Banner> findAppIndexAd();
	
	/**
	 * app首页弹层
	 * @return
	 */
	List<Banner> findAppIndexPopup();

}
