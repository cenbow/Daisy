/**
 * 
 */
package com.yourong.core.ic.model.query;

import com.yourong.common.domain.BaseQueryParam;

/**
 * @desc 活动专题页查询
 * @author zhanghao
 * 2016年7月20日下午5:58:48
 */
public class BannerQuery extends BaseQueryParam{


	/**
	 * banner类型
	 */
	private Integer bannerType;

	/**
	 * @return the bannerType
	 */
	public Integer getBannerType() {
		return bannerType;
	}

	/**
	 * @param bannerType the bannerType to set
	 */
	public void setBannerType(Integer bannerType) {
		this.bannerType = bannerType;
	}
	
}
