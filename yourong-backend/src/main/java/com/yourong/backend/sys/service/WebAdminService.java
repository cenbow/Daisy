/**
 * 
 */
package com.yourong.backend.sys.service;

import com.yourong.core.sys.model.WebInfo;

/**
 * @author zhanghao
 *
 */
public interface WebAdminService {

	/**
	 * 保存公告
	 * @param customMessage
	 * @return
	 */
	public Object saveWebInfo(WebInfo webInfo);
	
	
	
}
