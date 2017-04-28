package com.yourong.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.yourong.api.service.BannerService;
import com.yourong.common.constant.Config;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.core.cms.manager.BannerManager;
import com.yourong.core.cms.model.Banner;
import com.yourong.core.cms.model.biz.BannerFroAreaBiz;
import com.yourong.core.ic.model.query.BannerQuery;

@Service
public class BannerServiceImpl implements BannerService{
	@Autowired
	private BannerManager bannerManager;

	private static final Logger logger = LoggerFactory.getLogger(BannerServiceImpl.class);
	
	@Override
	public List<Banner> findPcOnlineBanner() {
		try {
			List<BannerFroAreaBiz> adList = bannerManager.showBannerByArea(TypeEnum.BANNER_CHANNEL_TYPE_PC.getType(), "home", 0, 99);
			List<Banner> retList = new ArrayList<Banner>();
			if(CollectionUtils.isNotEmpty(adList)) {
				BeanCopyUtil.copy(adList, retList);
			}
			return retList;
		} catch (Exception e) {
			logger.error("Pc获取banner失败", e);
		}
		return null;
	}

	public List<Banner> findAppOnlineBanner() {
		Map<String, Object> map = Maps.newHashMap();
		try {
			map.put("type", TypeEnum.BANNER_CHANNEL_TYPE_APP.getType());
			List<Banner> adList = bannerManager.findOnlineBanner(map);
			return adList;
		} catch (Exception e) {
			logger.error("APP获取banner失败", e);
		}
		return null;
	}
	
	
	public List<Banner> findAppOnlineBannerForVersion(Integer version) {
		Map<String, Object> map = Maps.newHashMap();
		try {
			map.put("type", TypeEnum.BANNER_CHANNEL_TYPE_APP.getType());
			map.put("areaSign", version);
			List<Banner> adList = bannerManager.findOnlineBanner(map);
			return adList;
		} catch (Exception e) {
			logger.error("APP获取banner失败", e);
		}
		return null;
	}
	
	public List<Banner> findAppActivityBanner() {
		Map<String, Object> map = Maps.newHashMap();
		try {
			map.put("type", TypeEnum.BANNER_CHANNEL_TYPE_APP_ACTIVITY.getType());
			List<Banner> adList = bannerManager.findOnlineBanner(map);
			return adList;
		} catch (Exception e) {
			logger.error("APP获取活动banner失败", e);
		}
		return null;
	}
	
	public List<Banner> findAppFindBanner() {
		Map<String, Object> map = Maps.newHashMap();
		try {
			map.put("type", TypeEnum.BANNER_CHANNEL_TYPE_APP_FIND.getType());
			List<Banner> adList = bannerManager.findOnlineBanner(map);
			return adList;
		} catch (Exception e) {
			logger.error("APP获取发现页banner失败", e);
		}
		return null;
	}
	
	public List<Banner> findMOnlineBanner() {
		Map<String, Object> map = Maps.newHashMap();
		try {//
			map.put("type", TypeEnum.BANNER_CHANNEL_TYPE_M.getType());
			List<Banner> adList = bannerManager.findOnlineBanner(map);
			return adList;
		} catch (Exception e) {
			logger.error("M站获取banner失败", e);
		}
		return null;
	}
	
	/**
	 * 获取活动集合页分页列表
	 * 
	 * @param queryParam
	 * @return
	 */
	@Override
	public Page<BannerFroAreaBiz> findAppActivityBannerByPage(BannerQuery bannerQuery) {
		try {
			bannerQuery.setBannerType(TypeEnum.BANNER_CHANNEL_TYPE_APP_ACTIVITY.getType());	
			Page<BannerFroAreaBiz> bannerPage =  bannerManager.findAppActivityBannerByPage(bannerQuery);
			List<BannerFroAreaBiz> bannerList= bannerPage.getData();
			for(BannerFroAreaBiz ban :bannerList){
				ban.setImage(Config.ossPicUrl + ban.getImage());
			}
			return bannerPage;
		} catch (Exception e) {
			logger.error("查询app活动集合页数据失败", e);
		}
		return null;

	}

	
	@Override
	public List<Banner> findAppIndexAd() {
		Map<String, Object> map = Maps.newHashMap();
		try {
			map.put("type", TypeEnum.BANNER_CHANNEL_TYPE_APP_INDEX_AD.getType());
			List<Banner> appAdList = bannerManager.findOnlineBanner(map);
			return appAdList;
		} catch (Exception e) {
			logger.error("APP获取banner失败", e);
		}
		return null;
	}
	
	
	@Override
	public List<Banner> findAppIndexPopup() {
		Map<String, Object> map = Maps.newHashMap();
		try {
			map.put("type", TypeEnum.BANNER_CHANNEL_TYPE_APP_INDEX_POPUP.getType());
			List<Banner> appAdList = bannerManager.findOnlineBanner(map);
			return appAdList;
		} catch (Exception e) {
			logger.error("APP获取banner失败", e);
		}
		return null;
	}

}
