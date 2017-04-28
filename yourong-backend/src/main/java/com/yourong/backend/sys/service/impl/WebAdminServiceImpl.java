/**
 * 
 */
package com.yourong.backend.sys.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.yourong.backend.sys.service.WebAdminService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.DateUtils;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.sys.model.WebInfo;

/**
 * @author zhanghao
 *
 */
@Service
public class WebAdminServiceImpl implements WebAdminService {
	private static Logger logger = LoggerFactory
			.getLogger(WebAdminServiceImpl.class);

	@Autowired
	private SysDictManager sysDictManager;

	@Override
	public Object saveWebInfo(WebInfo webInfo) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		rDO.setSuccess(true);
		try {
			if(Optional.fromNullable(webInfo).isPresent()){
				
				Map<String, Object> webNotice = Maps.newHashMap();
				//Maps.newHashMap()//支持泛型、
				webNotice.put("content", webInfo.getContent());
				//webNotice.put("href", webInfo.getHref());
				Date endDate =webInfo.getEndDate();
				Date startDate =webInfo.getStartDate();
				webNotice.put("endDate", (endDate!=null&&StringUtils.isNotBlank(endDate.toString())?DateUtils.toString(endDate,"yyyy-MM-dd HH:mm"):""));//因为存入varchar类型，固提前做格式修改
				webNotice.put("startDate",(startDate!=null&&StringUtils.isNotBlank(startDate.toString())?DateUtils.toString(startDate,"yyyy-MM-dd HH:mm"):""));
		
				this.updateSysdict("web_notice", webNotice);
		
				// head_off_method app_is_head_off_sina_method   控制移动端是否开启公告功能
				//Map<String, Object> webNoticeApp = Maps.newHashMap();
				//webNoticeApp.put("head_off_method", (webInfo.getMobClient()!=null?"Y":"N"));
				//this.updateSysdict("app_is_head_off_sina_method", webNoticeApp);
		
				// is_redirect is_handle_sina_pay   控制Web端是否开启公告功能
				Map<String, Object> webNoticeWeb = Maps.newHashMap();
				webNoticeWeb.put("is_redirect", webInfo.getHref());
				//webNoticeWeb.put("is_redirect", (webInfo.getWebClient()!=null?"Y":"N"));
				this.updateSysdict("is_handle_sina_pay", webNoticeWeb);
			
			}
		} catch (ManagerException e) {
			logger.info("保存网站公告异常" ,  e);
			rDO.setSuccess(false);
			return rDO;
		}
		return rDO;
	}

	/**
	 * 对应数据字典的groupName，自动批量更新
	 *
	 */
	private void updateSysdict(String groupName, Map<String, Object> map) throws ManagerException{

		SysDict sysDict = new SysDict();
		sysDict.setGroupName(groupName);
		/*if(("is_handle_sina_pay").equals(groupName)){
			sysDict.setRemarks(map.get("is_redirect").toString());
			//map.remove("remarks");
		}*/
		for (Entry<String, Object> entry : map.entrySet()) {
			sysDict.setKey(entry.getKey());//数据字典Key值
			//更新链接时，更新备注字段
			if("is_redirect".equals(entry.getKey())&&"is_handle_sina_pay".equals(groupName) ){
				sysDict.setRemarks(entry.getValue().toString());
			}else{
				sysDict.setValue(entry.getValue().toString());//数据字典数据值
			}
			sysDictManager.updateByGroupNameAndKey(sysDict);
		}
	}
}
