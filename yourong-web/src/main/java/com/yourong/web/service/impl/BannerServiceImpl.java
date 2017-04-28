package com.yourong.web.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.core.cms.manager.BannerManager;
import com.yourong.core.cms.model.Banner;
import com.yourong.core.cms.model.biz.BannerFroAreaBiz;
import com.yourong.web.service.BannerService;

@Service
public class BannerServiceImpl implements BannerService {
	@Autowired
	private BannerManager bannerManager;

	private static final Logger logger = LoggerFactory
			.getLogger(BannerServiceImpl.class);

	@Override
	public List<Banner> findPcOnlineBanner(String areaSign) {
		try {
			List<BannerFroAreaBiz> adList = bannerManager.showBannerByArea(
					TypeEnum.BANNER_CHANNEL_TYPE_PC.getType(), areaSign, 0, 99);
			List<Banner> retList = new ArrayList<Banner>();
			if (CollectionUtils.isNotEmpty(adList)) {
				BeanCopyUtil.copy(adList, retList);
			}
			return retList;
		} catch (Exception e) {
			logger.error("Pc获取banner失败", e);
		}
		return null;
	}

	@Override
	public List<Banner> findAppOnlineBanner() {
		try {
			List<BannerFroAreaBiz> adList = bannerManager.showBannerByArea(
					TypeEnum.BANNER_CHANNEL_TYPE_APP.getType(), "home", 0, 99);
			List<Banner> retList = new ArrayList<Banner>();
			if (CollectionUtils.isNotEmpty(adList)) {
				BeanCopyUtil.copy(adList, retList);
			}
			return retList;
		} catch (Exception e) {
			logger.error("App获取banner失败", e);
		}
		return null;
	}

	@Override
	public ResultDO<BannerFroAreaBiz> showBannerByArea(int type,
			String areaCode, Integer rowStart, Integer rowLength) {
		ResultDO<BannerFroAreaBiz> res = new ResultDO<BannerFroAreaBiz>();
		try {
			List<BannerFroAreaBiz> adList = bannerManager.showBannerByArea(
					type, areaCode, rowStart, rowLength);
			if (CollectionUtils.isNotEmpty(adList)) {
				res.setResultList(adList);
			} else {
				res.setSuccess(false);
			}
		} catch (Exception e) {
			logger.error("查询活动赚取人气值列表失败", e);
			res.setSuccess(false);
		}
		return res;
	}

	/**
	 * 获取活动集合页分页列表
	 * 
	 * @param queryParam
	 * @return
	 */
	@Override
	public Page<BannerFroAreaBiz> findActivityBannerByPage(
			BaseQueryParam queryParam) {
		try {
			return bannerManager.findActivityBannerByPage(queryParam,
					"activityCenterPage");
		} catch (Exception e) {
			logger.error("查询活动集合页分页数据失败", e);
		}
		return null;

	}
}
