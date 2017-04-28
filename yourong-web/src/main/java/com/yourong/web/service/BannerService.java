package com.yourong.web.service;

import java.util.List;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.Banner;
import com.yourong.core.cms.model.biz.BannerFroAreaBiz;

public interface BannerService {

	List<Banner> findPcOnlineBanner(String areaSign);

	List<Banner> findAppOnlineBanner();

	/**
	 * 根据页面区域获取banner
	 * 
	 * @param type
	 * @param areaCode
	 * @param rowStart
	 * @param rowLength
	 * @return
	 */
	ResultDO<BannerFroAreaBiz> showBannerByArea(int type, String areaCode,
			Integer rowStart, Integer rowLength);

	/**
	 * 分页获取活动列表
	 */
	Page<BannerFroAreaBiz> findActivityBannerByPage(BaseQueryParam queryParam);
}
