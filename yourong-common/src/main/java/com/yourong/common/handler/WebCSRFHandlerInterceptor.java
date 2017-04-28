package com.yourong.common.handler;

import java.util.List;

/**
 * @desc web CSRF拦截器
 * @author fuyili 2015年12月4日下午12:50:18
 */
public class WebCSRFHandlerInterceptor extends BaseCSRFHandlerInterceptor {
	private List<String> excludeUrls;
	private List<String> onceTokenValidateUrls;

	@Override
	public List<String> getExcludeUrls() {
		return excludeUrls;
	}

	@Override
	public List<String> getOnceTokenValidateUrls() {
		return onceTokenValidateUrls;
	}

	public void setExcludeUrls(List<String> excludeUrls) {
		this.excludeUrls = excludeUrls;
	}

	public void setOnceTokenValidateUrls(List<String> onceTokenValidateUrls) {
		this.onceTokenValidateUrls = onceTokenValidateUrls;
	}

}
