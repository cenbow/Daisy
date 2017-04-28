package com.yourong.backend.utils;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.yourong.core.sys.manager.SysAreaManager;
import com.yourong.core.sys.manager.SysLogManager;
import com.yourong.core.sys.model.*;
import com.yourong.core.uc.model.ThirdCompany;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.collect.Lists;
import com.yourong.backend.cache.MyCacheManager;
import com.yourong.common.constant.Constant;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.Identities;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.SpringContextHolder;

/**
 * 系统工具类（包含字典服务，权限服务等）
 * @author ThinkGem
 * @version 2013-5-29
 */
public class SysServiceUtils {
	
	private static MyCacheManager myCacheManager = SpringContextHolder.getBean(MyCacheManager.class);

	private static SysLogManager sysLogManager = SpringContextHolder.getBean(SysLogManager.class);
	
	private static SysAreaManager sysAreaManager = SpringContextHolder.getBean(SysAreaManager.class);

	public static final String CACHE_DICT_MAP = "dictMap";
	
	private static final String WEB_ROOT_URL = "root_url_web";
	

	/**
	 * 获取所有可用菜单
	 * @return
	 */
	public static List<SysMenu> getAllMenu(){
		return myCacheManager.getAllMenu();
	}
	
	/**
	 * 获取树
	 * @return
	 */
	public static List<SysMenu> getTreeMenu(){
		return myCacheManager.getTreeMenu();
	}
	
	
	
	/**
	 * 获取数据字典标签
	 * @param value	value值
	 * @param groupName	组名
	 * @param defaultLabel	默认显示的标签值
	 * @return
	 */
	public static String getDictLabelByValue(String value, String groupName, String defaultLabel){
		if (StringUtils.isNotBlank(groupName) && StringUtils.isNotBlank(value)){
			for (SysDict dict : getDictList(groupName)){
				if (groupName.equals(dict.getGroupName()) && value.equals(dict.getValue())){
					return dict.getLabel();
				}
			}
		}
		return defaultLabel;
	}
	
	/**
	 * 获取数据字典标签
	 * @param key	value值
	 * @param groupName	组名
	 * @param defaultLabel	默认显示的标签值
	 * @return
	 */
	public static String getDictLabelByKey(String key, String groupName, String defaultLabel){
		if (StringUtils.isNotBlank(groupName) && StringUtils.isNotBlank(key)){
			for (SysDict dict : getDictList(groupName)){
				if (groupName.equals(dict.getGroupName()) && key.equals(dict.getKey())){
					return dict.getLabel();
				}
			}
		}
		return defaultLabel;
	}
	
	/**
	 * 获取数据字典value值
	 * @param key	键值	
	 * @param groupName	组名
	 * @param defaultValue	默认value
	 * @return
	 */
	public static String getDictValue(String key, String groupName, String defaultValue){
		if (StringUtils.isNotBlank(groupName) && StringUtils.isNotBlank(key)){
			for (SysDict dict : getDictList(groupName)){
				if (groupName.equals(dict.getGroupName()) && key.equals(dict.getKey())){
					return dict.getValue();
				}
			}
		}
		return defaultValue;
	}
	/**
	 * 获取数据字典对象
	 * @param key	键值	
	 * @param groupName	组名
	 * @return
	 */
	public static Object  getDictByGroupNameAndKey(String key,String groupName){
		if (StringUtils.isNotBlank(groupName) && StringUtils.isNotBlank(key)){
			for (SysDict dict : getDictList(groupName)){
				if (groupName.equals(dict.getGroupName()) && key.equals(dict.getKey())){
					return dict;
				}
			}
		}
		return null;
	}
	
	/**
	 * 通过组名获取数据字典列表
	 * @param groupName	组名
	 * @return
	 */
	public static List<SysDict> getDictList(String groupName){
		List<SysDict> dictList = myCacheManager.getListSysDictByGroupName(groupName);
		if (dictList == null){
			dictList = Lists.newArrayList();
		}
		return dictList;
	}
	
	/**
	 * 查询所有省市区	type=0 查询所有，type=2查询所有省，type=3 查询所有市，type=4 查询所有区
	 * @param type
	 * @return
	 */
	public static List<SysArea> getAllAreaList(int type){
		List<SysArea> areaList = Lists.newArrayList();
		if(type==0) {
			areaList = myCacheManager.getAllArea();
		} else {
			areaList = myCacheManager.getSysAreasByType();
		} 
		return areaList;
	}
	
	/**
	 * 获取所有有效的角色
	 * @return
	 */
	public static List<SysRole> getAllSysRole(){
		return myCacheManager.getAllSysRole();
	}
	


	/**
	 *  获取当前登录的账号， (以后会扩展 获取SysUser对象)
	 * @return
	 */
	public static String  getCurrentLoginUser(){
		Subject subject = SecurityUtils.getSubject();
		String name = "";
		if(subject != null && subject.isAuthenticated() ){
			name = subject.getPrincipal().toString();
		}
		return  name;
	}
	public static SysUser getCurrentLoginUserInfo(){
		SysUser  sysUser = null;
		Subject subject = SecurityUtils.getSubject();
		if(subject != null && subject.isAuthenticated() ){
			sysUser =  (SysUser) subject.getSession().getAttribute(Constant.CURRENT_USER);
		}
		return sysUser;
	}

	/**
	 * 	 写入业务日志到数据库
	 * @param moduleName  模块名称  长度  256 超过256 自动截取
	 * @param desc  具体日志   长度 1000 ， 超过1000 自动截取
	 */
	public static   void  writeLogger(String moduleName,String desc ){
		SysLog  sysLog = new SysLog();
		sysLog.setModuleName(moduleName);
		sysLog.setModuleDesc(desc);
		Subject subject = SecurityUtils.getSubject();
		sysLog.setRemoteAddr(subject.getSession().getHost());
		SysUser sysUser = null;
		if(subject != null && subject.isAuthenticated() ){
			sysUser =  (SysUser) subject.getSession().getAttribute(Constant.CURRENT_USER);
			sysLog.setOperateName(sysUser.getName());
			sysLog.setOperateId(sysUser.getId());
		}
		sysLogManager.insertSelective(sysLog);
	}



	/**
	 * 随机函数
	 * @return
	 */
	public static Long getRandom(){
		return Identities.randomLong();
	}
	
	/**
	 * 获得静态资源版本
	 */
	public static String getStaticResourceVersion(){
		return PropertiesUtil.getStaticResourceVersion();
	}
	
	/**
	 * 后台获取前台url
	 * @return
	 */
	public static String getWebRootURL(){
		return PropertiesUtil.getProperties(WEB_ROOT_URL);
	}
	
	/**
	 * 
	 * @Description:获取第三方垫付公司
	 * @return
	 * @author: chaisen
	 * @time:2016年1月7日 下午4:05:08
	 */
	public static List<ThirdCompany> getThirdCompanyList(){
		List<ThirdCompany> companyList = myCacheManager.getThirdCompanyList();
		List<ThirdCompany> newList = Lists.newArrayList();
		if (companyList == null){
			companyList = Lists.newArrayList();
		}else{
			for(ThirdCompany company :companyList){
				ThirdCompany thirdCompany=new ThirdCompany();
				thirdCompany.setValue(company.getMemberId().toString());
				thirdCompany.setLabel(company.getCompanyName());
				newList.add(thirdCompany);
			}
		}
		return newList;
	}
	
	/**
	 * 根据code获得上级parentids
	 * @param code
	 * @return
	 * @throws ManagerException 
	 * @throws NumberFormatException 
	 */
	public static List<Object> getParentIdsByCode(String code) throws Exception{
		//因vm原因，这里code必须设置为String类型 
		List list = sysAreaManager.getParentIdsByCode(Long.parseLong(code));
		return list;
	}
	
	
	public static List<SysDict> getDictListKey(String groupName){
		List<SysDict> dictList = myCacheManager.getListSysDictByGroupName(groupName);
		if (dictList == null){
			dictList = Lists.newArrayList();
		}
		List<SysDict> newList= Lists.newArrayList();
		if(Collections3.isNotEmpty(dictList)){
			for(SysDict dict:dictList){
				SysDict newDict=new SysDict();
				newDict.setKey(dict.getValue());
				newDict.setValue(dict.getKey());
				newList.add(newDict);
			}
		}
		return newList;
	}
}
