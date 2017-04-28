/**
 * 
 */
package com.yourong.backend.jobs;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.collect.Maps;
import com.yourong.backend.utils.SysServiceUtils;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年1月13日下午3:24:02
 */
public class WebMaintenAnnounceTask {
	private static final Logger logger = LoggerFactory
			.getLogger(WebMaintenAnnounceTask.class);

	@Autowired
	private SysDictManager sysDictManager;
	
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("网站维护公告开关定时器执行开始");
					webMaintenAnnounce();
					logger.info("网站维护公告开关定时器执行结束");
				} catch (Exception e) {
					logger.error("网站维护公告开关定时器执行出现异常", e);
				}
			}
		});
	}

	public void webMaintenAnnounce() throws ManagerException {
		
		String start = SysServiceUtils.getDictValue("startDate",  
				"web_notice", "");
		String end = SysServiceUtils.getDictValue("endDate",  
				"web_notice", "");
		
		
		if(StringUtil.isNotBlank(start)&&StringUtil.isNotBlank(end)){
			Date startDate = DateUtils.getDateTimeFromString(start+":00");
			Date endDate = DateUtils.getDateTimeFromString(end+":00");
			Date now = new Date(); 
			
			Map<String, Object> webNoticeApp = Maps.newHashMap();
			Map<String, Object> webNoticeWeb = Maps.newHashMap();
			
			if(DateUtils.isDateBetween(now,startDate,endDate)){
				webNoticeApp.put("head_off_method", "Y");
				webNoticeWeb.put("is_redirect", "Y");
			}else{
				webNoticeApp.put("head_off_method", "N");
				webNoticeWeb.put("is_redirect", "N");
			}
			this.updateSysdict("app_is_head_off_sina_method", webNoticeApp);
			this.updateSysdict("is_handle_sina_pay", webNoticeWeb);
		}
		
		
	}

	
	/**
	 * 对应数据字典的groupName，自动批量更新
	 *
	 */
	private void updateSysdict(String groupName, Map<String, Object> map) throws ManagerException{

		SysDict sysDict = new SysDict();
		sysDict.setGroupName(groupName);
		for (Entry<String, Object> entry : map.entrySet()) {
			sysDict.setKey(entry.getKey());//数据字典Key值
			sysDict.setValue(entry.getValue().toString());//数据字典数据值
			sysDictManager.updateByGroupNameAndKey(sysDict);
		}
	}
	
}
