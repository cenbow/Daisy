package com.yourong.backend.mc.service;
import java.util.List;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.weixin.Weixin;
import com.yourong.common.weixin.WeixinMenu;
import com.yourong.core.mc.model.WinxinTemplate;

public interface WeixinTemplateService {
	Integer deleteByPrimaryKey(Long id);

    Integer insert(WinxinTemplate winxinMenu);

    WinxinTemplate selectByPrimaryKey(Long id);

    Integer updateByPrimaryKeySelective(WinxinTemplate winxinMenu);

    Page<WinxinTemplate> findByPage(Page<WinxinTemplate> pageRequest, Map<String, Object> map);

    Integer deleteBymenuId(Long id);

	/**
	 * @return
	 */
	List<WinxinTemplate> showWeixinMenuTree();
	String getParentMenu();

	/**
	 * @param id
	 */
	Integer batchDelete(long[] id);

	/**
	 * @param winxin
	 * @param appPath
	 * @return
	 */
	ResultDO<WinxinTemplate> insertWeixinInfo(WinxinTemplate winxin,
			String appPath)throws ManagerException;

	/**
	 * @param id
	 * @return
	 */
	WinxinTemplate queryInfobyId(long id);

	/**
	 * @param winxin
	 * @param appPath
	 * @return
	 */
	ResultDO<WinxinTemplate> updateWeixinInfo(WinxinTemplate winxin,
			String appPath)throws ManagerException;

	/**
	 * @return 
	 * 
	 */
	Integer updateWeixin();

	/**
	 * @return
	 */
	WinxinTemplate getMenu();

	/**
	 * @Description:TODO
	 * @return
	 * @author: chaisen
	 * @time:2015年12月2日 下午3:00:55
	 */
	java.util.List<Weixin> getWeixinMenu();

	/**
	 * @Description:TODO
	 * @param weixin
	 * @return
	 * @author: chaisen
	 * @time:2015年12月2日 下午3:12:32
	 */
	java.util.List<Weixin> saveWeixinMenu(Weixin weixin);

	/**
	 * @Description:TODO
	 * @param jsonStr
	 * @return
	 * @author: chaisen
	 * @time:2015年12月2日 下午3:22:04
	 */
	ResultDO<WeixinMenu> pushWeixinMenu(String jsonStr);

	/**
	 * @Description:TODO
	 * @param id
	 * @return
	 * @author: chaisen
	 * @time:2015年12月2日 下午3:30:00
	 */
	java.util.List<Weixin> delWeixinMenu(String id);

	List<Weixin> getWeixinMenuList() throws ManagerException;

	Weixin selectByweixinId(long id);

	ResultDO<Weixin> updateWeixin(Weixin weixin);

	ResultDO<Weixin> insertWeixin(Weixin weixin);

	ResultDO<Weixin> pushWeixin();
	
}