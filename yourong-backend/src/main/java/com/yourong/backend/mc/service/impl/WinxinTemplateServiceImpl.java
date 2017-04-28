package com.yourong.backend.mc.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import rop.thirdparty.com.alibaba.fastjson.JSONArray;
import rop.thirdparty.com.alibaba.fastjson.JSONObject;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.yourong.backend.mc.service.WeixinTemplateService;
import com.yourong.backend.sys.service.SysDictService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.Identities;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.weixin.Weixin;
import com.yourong.common.weixin.WeixinMenu;
import com.yourong.common.weixin.WeixinUtil;
import com.yourong.core.handle.AttachmentInfo;
import com.yourong.core.handle.AttachmentThread;
import com.yourong.core.handle.DebtAttachmentHandle;
import com.yourong.core.mc.manager.WinxinTemplateManager;
import com.yourong.core.mc.model.WinxinTemplate;
import com.yourong.core.sys.dao.SysDictMapper;
import com.yourong.core.sys.model.SysDict;

@Service
public class WinxinTemplateServiceImpl implements WeixinTemplateService {

	private static Logger logger = LoggerFactory
			.getLogger(WinxinTemplateServiceImpl.class);
	public static final List cache=new ArrayList();	
	@Autowired
	private WinxinTemplateManager winxinMenuManager;
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;
	@Autowired
	private SysDictService sysDictService;
	@Autowired
	private DebtAttachmentHandle debtAttachmentHandle;
	
	@Autowired
	private SysDictMapper sysDictMapper;
	public Integer deleteByPrimaryKey(Long id) {
		try {
			return winxinMenuManager.deleteByPrimaryKey(id);
		} catch (ManagerException e) {
			logger.error("删除模板信息失败,id=" + id, e);
		}
		return null;
	}

	public Integer insert(WinxinTemplate winxinMenu) {
		try {
			return winxinMenuManager.insert(winxinMenu);
		} catch (ManagerException e) {
			logger.error("插入微信模板失败,winxinMenu=" + winxinMenu, e);
		}
		return null;
	}


	public Integer updateByPrimaryKey(WinxinTemplate winxinMenu) {
		try {
			return winxinMenuManager.updateByPrimaryKey(winxinMenu);
		} catch (ManagerException e) {
			logger.error("更新区域信息失败,winxinMenu=" + winxinMenu, e);
		}
		return null;
	}

	public Integer updateByPrimaryKeySelective(WinxinTemplate winxinMenu) {
		try {
			return winxinMenuManager.updateByPrimaryKeySelective(winxinMenu);
		} catch (ManagerException e) {
			logger.error("更新微信模板失败,winxinMenu=" + winxinMenu, e);
		}
		return null;
	}

	public Integer deleteBymenuId(Long id) {
		try {
			return winxinMenuManager.deleteBymenuId(id);
		} catch (ManagerException e) {
			logger.error("删除菜单失败,id=" + id, e);
		}
		return null;
	}


	@Override
	public WinxinTemplate selectByPrimaryKey(Long id) {
		try {
			return winxinMenuManager.selectByPrimaryKey(id);
		} catch (ManagerException e) {
			logger.error("查询微信菜单失败,id=" + id, e);
		}
		return null;
	}


	public Page<WinxinTemplate> findByPage(Page<WinxinTemplate> pageRequest,
			Map<String, Object> map) {
		try {
			return winxinMenuManager.findByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("分页查询模板信息失败", e);
		}
		return null;
	}

	@Override
	public List<WinxinTemplate> showWeixinMenuTree() {
		try {
			return winxinMenuManager.getAllWeixinMenu();
		} catch (ManagerException e) {
			logger.error("分页查询部门信息失败", e);
		}	
		return null;
	}

	@Override
	public String getParentMenu() {
		try {
			return winxinMenuManager.getParentMenu();
		} catch (ManagerException e) {
			logger.error("分页查询微信菜单失败", e);
		}
		return null;
	}

	@Override
	public Integer batchDelete(long[] ids) {
		try {
			return winxinMenuManager.batchDelete(ids);
		} catch (ManagerException e) {
			logger.error("批量删除微信菜单失败,ids=" + ids, e);
		}
		return null;
	}

	@Override
	public ResultDO<WinxinTemplate> insertWeixinInfo(WinxinTemplate winxin, String appPath) throws ManagerException {
		ResultDO<WinxinTemplate> resultDO = new ResultDO<WinxinTemplate>();
		try {
				// 保存首次回复信息
				//winxinMenuManager.updateWeixin();
				int result = winxinMenuManager.insertSelective(winxin);
				if (result > 0) {
					if (Collections3.isNotEmpty(winxin.getBscAttachments())) {
						if(winxin.getBscAttachments()!=null){
							AttachmentInfo info = new AttachmentInfo();
							info.setKeyId(winxin.getId().toString());
							info.setBscAttachments(winxin.getBscAttachments());
							info.setAppPath(appPath);
							info.setOperation(AttachmentInfo.SAVE);
							taskExecutor.execute(new AttachmentThread(debtAttachmentHandle, info));
						}
					}
				}
		} catch (ManagerException e) {
			logger.error("插入微信信息失败,winxin=" + winxin, e);
			throw new ManagerException(e);
		}
		return resultDO;
	}

	@Override
	public WinxinTemplate queryInfobyId(long id) {
		try {
			return winxinMenuManager.queryInfobyId(id);
		} catch (ManagerException e) {
			logger.error("失败,id=" + id, e);
		}
		return null;
	}

	@Override
	public ResultDO<WinxinTemplate> updateWeixinInfo(WinxinTemplate winxin, String appPath) throws ManagerException {
		ResultDO<WinxinTemplate> resultDO = new ResultDO<WinxinTemplate>();
		try {
			if (winxin != null) {
				int result = winxinMenuManager.updateByPrimaryKeySelective(winxin);
				if (result > 0) {
					AttachmentInfo info = new AttachmentInfo();
					info.setKeyId(winxin.getId().toString());
					info.setBscAttachments(winxin.getBscAttachments());
					info.setAppPath(appPath);
					info.setOperation(AttachmentInfo.UPDATE);
					taskExecutor.execute(new AttachmentThread(debtAttachmentHandle, info));
					
				} else {
					//resultDO.setResultCode(ResultCode.DEBT_UPDATE_FAIL_ERROR);
				}
			}
		} catch (ManagerException e) {
			logger.error("更新模板信息失败,debtBiz=" + winxin, e);
			throw new ManagerException(e);
		}
		return resultDO;
	}

	@Override
	public Integer updateWeixin() {
		try {
			return winxinMenuManager.updateWeixin();
		} catch (ManagerException e) {
			logger.error("失败", e);
		}
		return null;
	}

	@Override
	public WinxinTemplate getMenu() {
		String accessToken="";
		String json="";
		try {
			accessToken = WeixinUtil.getAccessToken(PropertiesUtil.getWeixinAppID(), PropertiesUtil.getWeixinAppsecret());
			json=WeixinUtil.getMenuInfo(accessToken);
		} catch (Exception e) {
			logger.error("失败", e);
		}
		WinxinTemplate winxinMenu=new WinxinTemplate();
		if(json!=null&&json.length()>8){
			winxinMenu.setRemarks(json.substring(8, json.length()-1));
		}
		return winxinMenu;
		}

	/**
	 * @desc 获取微信菜单
	 * @return
	 * @author chaisen
	 * @time 2015年12月2日 下午3:01:10
	 **/
	@Override
	public List<Weixin> getWeixinMenu() { 
	 List<Weixin> list = Lists.newArrayList();
	 List<Weixin> newList = Lists.newArrayList();
		if(cache.size()>0){
			list=(List<Weixin>) cache.get(0);
		  return list;
	    }
	  String accessToken = WeixinUtil.getAccessToken(PropertiesUtil.getWeixinAppID(), PropertiesUtil.getWeixinAppsecret());
	  String json=WeixinUtil.getMenuInfo(accessToken);
	  String temp="";
	  if(json!=null&&json.length()>18){
		  temp=json.substring(18, json.length()-2);
	  }
	  JSONArray jsonChannelIds = JSONArray.parseArray(temp);
       List<Weixin> menuList = Lists.newArrayList();
       menuList=JSON.parseArray(jsonChannelIds.toJSONString(), Weixin.class);
       for(Weixin wx:menuList){
    	   wx.setPid("PID"); 
    	   wx.setId("PID"+Identities.randomNumberLength(5));
    	   wx.setText(wx.getName());
    	   String pid=wx.getId();
    	   if(wx.getSub_button()!=null){
        		 List<Weixin> childist = Lists.newArrayList();
        		 childist =getWeixinChildMenu(wx.getSub_button(),pid);
        		 newList.addAll(childist);
        	 }
    	   newList.add(wx);
       }
       
       if(cache.size()==0){
      	 cache.clear();
	         cache.add(newList);
       }
       newList=(List<Weixin>) cache.get(0);
		return newList;
		}
	/**
	 * 
	 * @Description:获取子菜单
	 * @param list
	 * @param pid
	 * @return
	 * @author: chaisen
	 * @time:2015年12月11日 上午10:19:21
	 */
	public List<Weixin> getWeixinChildMenu( List<Weixin> list,String pid) {
		 List<Weixin> childist = Lists.newArrayList();
		 for(Weixin w:list){
			 w.setText(w.getName());
			 w.setPid(pid);
			 w.setId(Identities.randomNumberLength(5));
			 childist.add(w);
		 }
		 return childist;
	}
	/**
	 * @desc 保存微信菜单
	 * @param weixin
	 * @return
	 * @author chaisen
	 * @time 2015年12月2日 下午3:12:44
	 **/
	@Override
	public List<Weixin> saveWeixinMenu(Weixin weixin) {
		List<Weixin> list = Lists.newArrayList();
		List<Weixin> newList = Lists.newArrayList();
		list=(List<Weixin>) cache.get(0);
		if(weixin.getKey().isEmpty()){
			weixin.setKey(null);
		}
		 //新增
		 if(weixin.getId().isEmpty()){
			 newList=this.addWeixin(list,weixin);
	    //修改
		 }else{
			 newList=this.updateWeixin(list,weixin);
		 }
		 cache.clear();
	    cache.add(newList);
		return newList;
	}

	/**
	 * @Description:TODO
	 * @param list
	 * @param weixin
	 * @return
	 * @author: chaisen
	 * @time:2015年12月2日 下午3:18:12
	 */
	private List<Weixin> updateWeixin(List<Weixin> list, Weixin weixin) {
		List<Weixin> newList = Lists.newArrayList();
		 for(Weixin wx:list){
			 if(wx.getId().equals(weixin.getId())){
				 wx.setText(weixin.getName());
				 wx.setName(weixin.getName());
				 wx.setUrl(weixin.getUrl());
			 }
			 newList.add(wx);
		 }
		return newList;
	}

	/**
	 * @Description:TODO
	 * @param list
	 * @param weixin
	 * @author: chaisen
	 * @return 
	 * @time:2015年12月2日 下午3:15:46
	 */
	private  List<Weixin> addWeixin(List<Weixin> list, Weixin weixin) {
		List<Weixin> tempList = Lists.newArrayList();
		weixin.setId(Identities.randomNumberLength(5));
		weixin.setText(weixin.getName());
		tempList.add(weixin);
		list.addAll(tempList);
		return list;
		
	}

	/**
	 * @desc 发布微信菜单
	 * @param jsonStr
	 * @return
	 * @author chaisen
	 * @time 2015年12月2日 下午3:22:15
	 **/
	@Override
	public ResultDO<WeixinMenu> pushWeixinMenu(String jsonStr) {
		ResultDO<WeixinMenu> result = new ResultDO<WeixinMenu>();
		jsonStr=jsonStr.replace("children", "sub_button");
		JSONArray jsonArray = JSONArray.parseArray(jsonStr);
   	    List<WeixinMenu> listMenu = Lists.newArrayList();
   	    listMenu=JSON.parseArray(jsonArray.toJSONString(), WeixinMenu.class);
   	    Object objectMenu=JSONArray.toJSON(listMenu);
        String first="{\"button\":";
        String end="}";
        String temp=first+objectMenu.toString()+end;
        String accessToken = WeixinUtil.getAccessToken(PropertiesUtil.getWeixinAppID(), PropertiesUtil.getWeixinAppsecret());
		 String res = WeixinUtil.createMenu1(temp.toString(), accessToken);
		 if(res.equals("ok")){
			 result.setSuccess(true);
			 return result;
		 }else{
			 result.setSuccess(false);
			 return result;
		 }
	}

	/**
	 * @desc TODO
	 * @param id
	 * @return
	 * @author chaisen
	 * @time 2015年12月2日 下午3:30:09
	 **/
	@Override
	public List<Weixin> delWeixinMenu(String id) {
		List<Weixin> newList = Lists.newArrayList();
		 List<Weixin> list = Lists.newArrayList();
		  if(cache.size()>0){
			  list=(List<Weixin>) cache.get(0);
		  }
	       for(Weixin wx:list){
	    	   if(wx.getId().equals(id)){
	    		   continue;
	    	   }
	    	   newList.add(wx);
	    	 }
	         cache.clear();
	         cache.add(newList);
		 return newList;
	}

	@Override
	public List<Weixin> getWeixinMenuList() throws ManagerException { 
		List<SysDict>  dictList=sysDictService.findByGroupName ("weixinMenu");
		List<Weixin> list = Lists.newArrayList();
		if(Collections3.isEmpty(dictList)){
			 String accessToken = WeixinUtil.getAccessToken(PropertiesUtil.getWeixinAppID(), PropertiesUtil.getWeixinAppsecret());
			  String json=WeixinUtil.getMenuInfo(accessToken);
			  String temp="";
			  if(json!=null&&json.length()>18){
				  temp=json.substring(18, json.length()-2);
			  }
			  JSONArray jsonChannelIds = JSONArray.parseArray(temp);
		      List<Weixin> menuList = Lists.newArrayList();
		      menuList=JSON.parseArray(jsonChannelIds.toJSONString(), Weixin.class);
		      for(Weixin wx:menuList){
		    	  SysDict sysDict=new SysDict();
		    	  sysDict.setGroupName("weixinMenu");
		    	  sysDict.setLabel(wx.getName());
		    	  sysDict.setKey("parentNode");
		    	  sysDict.setValue("parentNode");
		    	  sysDict.setSort(1);
		    	  sysDict.setStatus(2);
		    	  sysDictService.insertSelective(sysDict);
		    	  if(wx.getSub_button()!=null){
		    		  for(Weixin bean:wx.getSub_button()){
		    			  SysDict dict=new SysDict();
			    		  dict.setLabel(bean.getName());
			    		  if(bean.getKey()!=null){
			    			  dict.setKey(bean.getKey());  
			    		  }else{
			    			  dict.setKey("0"); 
			    		  }
			    		  dict.setRemarks(bean.getUrl());
			    		  dict.setDescription(bean.getType());
			    		  dict.setStatus(1);
			    		  dict.setSort(0);
			    		  dict.setValue(sysDict.getId().toString());
			    		  sysDictService.insert(dict);
		    		  }
		    		  
		    	  }
		      }
		}else{
			for(SysDict dict:dictList){
				Weixin wx=new Weixin();
				wx.setId(dict.getId().toString());
				wx.setPid("0");
				wx.setName(dict.getLabel());
				wx.setKey(dict.getKey());
				wx.setStatus(dict.getStatus().toString());
				list.add(wx);
				List<SysDict>  childList=sysDictMapper.findByValue(dict.getId().toString());
				if(Collections3.isNotEmpty(childList)){
					for(SysDict sys:childList){
						Weixin wxC=new Weixin();
						wxC.setPid(dict.getId().toString());
						wxC.setId(sys.getId().toString());
						wxC.setName(sys.getLabel());
						wxC.setUrl(sys.getRemarks());
						wxC.setKey(sys.getKey());
						wxC.setType(sys.getDescription());
						wxC.setStatus(sys.getStatus().toString());
						list.add(wxC);
					}
				}
			}
			
		}
		return list;
	}

	@Override
	public Weixin selectByweixinId(long id) {
		SysDict sysDict=sysDictService.selectByPrimaryKey(id);
		Weixin wx=new Weixin();
		if(sysDict!=null){
			wx.setName(sysDict.getLabel());
			wx.setId(sysDict.getId().toString());
			wx.setKey(sysDict.getKey());
			wx.setUrl(sysDict.getRemarks());
			wx.setType(sysDict.getDescription());
			wx.setSort(sysDict.getSort());
		}
		return wx;
	}

	@Override
	public ResultDO<Weixin> updateWeixin(Weixin weixin) {
		int i=0;
		ResultDO<Weixin> result = new ResultDO<Weixin>();
		SysDict dict=sysDictService.selectByPrimaryKey(Long.parseLong(weixin.getId()));
		//增加
		if(weixin.getStatus().equals("2")){
			if(dict==null){
				return result;
			}
			dict.setLabel(weixin.getName());
			dict.setDescription(weixin.getType());
			dict.setSort(weixin.getSort());
			dict.setKey(weixin.getKey());
			//增加的一级
			if(dict.getStatus()==2){
				SysDict sd=new SysDict();
				sd.setLabel(weixin.getName());
				if(weixin.getKey()==null){
					sd.setKey("0");
				}else{
					sd.setKey(weixin.getKey());
				}
				sd.setRemarks(weixin.getUrl());
				sd.setValue(dict.getId().toString());
				sd.setStatus(1);
				sd.setDescription(weixin.getType());
				sysDictService.insert(sd);
			}else{
				dict.setId(null);
				sysDictService.insert(dict);
			}
			
		}else{
			if(dict!=null){
				dict.setLabel(weixin.getName());
				//更新的父节点
				if(dict.getValue().equals("parentNode")){ 
					dict.setKey("parentNode");
					dict.setValue("parentNode");
					dict.setGroupName("weixinMenu");
				}else{
					if(weixin.getType().equals("view")){
						dict.setKey("0");
					}else{
						dict.setKey(weixin.getKey());
					}
					dict.setValue(dict.getValue());
					dict.setDescription(weixin.getType());
					dict.setRemarks(weixin.getUrl());
					dict.setSort(weixin.getSort());
				}
			}
			i=sysDictService.updateByPrimaryKeySelective(dict);
		}
		
		
		if(i>0){
			result.setSuccess(true);
		}
		return result;
	}

	@Override
	public ResultDO<Weixin> insertWeixin(Weixin weixin) {
		SysDict sysDict=new SysDict();
		sysDict.setLabel(weixin.getName());
		sysDictService.insert(sysDict);
		return null;
	}

	@Override
	public ResultDO<Weixin> pushWeixin() {
		ResultDO<Weixin> result = new ResultDO<Weixin>();
		List<SysDict>  dictList=sysDictService.findByGroupName ("weixinMenu");
		List<WeixinMenu> listMenu = Lists.newArrayList();
		if(Collections3.isEmpty(dictList)){
			return result;
		}
		for(SysDict dict:dictList){
			List<WeixinMenu> child = Lists.newArrayList();
			WeixinMenu wxP=new WeixinMenu();
			wxP.setName(dict.getLabel());
			List<SysDict>  childList=sysDictMapper.findByValue(dict.getId().toString());
			if(Collections3.isNotEmpty(childList)){
				for(SysDict sys:childList){
					WeixinMenu wxC=new WeixinMenu();
					wxC.setKey(sys.getKey());
					wxC.setName(sys.getLabel());
					wxC.setType(sys.getDescription());
					wxC.setUrl(sys.getRemarks());
					child.add(wxC);
				}
				wxP.setSub_button(child);
				
			}
			listMenu.add(wxP);
		}
		//jsonStr=jsonStr.replace("children", "sub_button");
		//JSONArray jsonArray = JSONArray.parseArray(jsonStr);
   	   // listMenu=JSON.parseArray(jsonArray.toJSONString(), WeixinMenu.class);
   	    Object objectMenu=JSONArray.toJSON(listMenu);
        String first="{\"button\":";
        String end="}";
        String temp=first+objectMenu.toString()+end;
        String accessToken = WeixinUtil.getAccessToken(PropertiesUtil.getWeixinAppID(), PropertiesUtil.getWeixinAppsecret());
		 String res = WeixinUtil.createMenu1(temp.toString(), accessToken);
		 if(res.equals("ok")){
			 result.setSuccess(true);
			 return result;
		 }else{
			 result.setSuccess(false);
			 return result;
		 }
	}

}