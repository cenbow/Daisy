package com.yourong.api.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.yourong.common.constant.Constant;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.yourong.api.cache.MyCacheManager;
import com.yourong.api.dto.MemberInfoDto;
import com.yourong.api.service.MemberInfoService;
import com.yourong.api.service.MemberService;
import com.yourong.api.service.ProjectService;
import com.yourong.api.service.SysAreaService;
import com.yourong.common.thirdparty.sinapay.common.enums.BankCode;
import com.yourong.common.thirdparty.sinapay.common.enums.RechargeBankCode;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.SpringContextHolder;
import com.yourong.common.util.StringUtil;
import com.yourong.core.sys.model.SysArea;
import com.yourong.core.sys.model.SysDict;

import javax.servlet.http.HttpServletRequest;

/**
 * 系统工具类（包含字典服务，权限服务等）
 * @author ThinkGem
 * @version 2013-5-29
 */

public class SysServiceUtils {
	
	private static MyCacheManager myCacheManager = SpringContextHolder.getBean(MyCacheManager.class);
	
	private static ProjectService projectService = SpringContextHolder.getBean(ProjectService.class);
	
	private static MemberService memberService = SpringContextHolder.getBean(MemberService.class);
	
	private static SysAreaService sysAreaService = SpringContextHolder.getBean(SysAreaService.class);
	
	private static MemberInfoService memberInfoService = SpringContextHolder.getBean(MemberInfoService.class);
	public static final String CACHE_DICT_MAP = "dictMap";
	private static final String ROOT_URL = "root_url";
	private static final String MROOT_URL = "mRoot_url";
	
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
	 * @param value	value值
	 * @param groupName	组名
	 * @param defaultValue	默认显示的标签值
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
			
		}
		return areaList;
	}
	
	/**
	 * 根据code获得上级parentids
	 * @param code
	 * @return
	 */
	public static List<Object> getParentIdsByCode(String code){
		//因vm原因，这里code必须设置为String类型 
		List list = sysAreaService.getParentIdsByCode(Long.parseLong(code));
		return list;
	}
	
	/**
	 * 获得项目图片路径
	 * @param id
	 * @return
	 */
	public static String getProjectImagePath(Long id){
		return projectService.getProjectImagePath(id);
	}
	
	/**
	 * 项目可用余额
	 * @param id
	 * @return
	 */
	public static BigDecimal getProjectBalance(Long id){
		BigDecimal balance =  projectService.getProjectBalanceById(id);
		return balance;
	}
	
	/**
	 * 带百分符的项目进度
	 * @param totalAmount
	 * @param id
	 * @return
	 */
	public static String getProjectProgress(BigDecimal totalAmount, Long id){
		return getProjectNumberProgress(totalAmount, id)+"%";
	}
	
	/**
	 * 项目进度
	 * @param totalAmount
	 * @param id
	 * @return
	 */
	public static String getProjectNumberProgress(BigDecimal totalAmount, Long id){
		String  progress = "0";
		BigDecimal availableBalance = projectService.getProjectBalanceById(id);
		if(availableBalance != null){
			if(availableBalance.compareTo(BigDecimal.ZERO) <= 0){
				progress = "100";
			}else if(availableBalance.compareTo(totalAmount) == 0){
				progress = "0";
			}else{
				progress = new DecimalFormat("###.##").format((totalAmount.subtract(availableBalance)).divide(totalAmount,4,RoundingMode.HALF_UP).multiply(new BigDecimal("100")));
			}
		}
		return progress;
	}
	
	/**
	 * 提现页面   所有银行code
	 * @return
	 * author: pengyong
	 * 下午2:32:18
	 */
	public static BankCode[] getBankCode(){
		return BankCode.values();
	}
	
	
	/**
	 * 取指定银行emun
	 * @return
	 * author: pengyong	 
	 * *
	 */
	public static BankCode getBankCode(String code){
		return BankCode.valueOf(code);
	}
	
	
	/**
	 * 充值显示 充值银行  
	 * @return
	 * author: pengyong
	 *
	 */
	public static RechargeBankCode[] getAllRechargeBankCode(){
		return RechargeBankCode.values();
	}
	/**
	 * 获取配置url
	 * @return
	 * author: pengyong
	 * 下午4:32:10
	 */
	public static String getRootURL(){
		return PropertiesUtil.getProperties(ROOT_URL);
	}


	/**
	 * 获取WEB配置url
	 * @return
	 * author: pengyong
	 * 下午4:32:10
	 */
	public static String getRootWebURL(){
		return PropertiesUtil.getProperties("root_url_web");
	}
	/**
	 * 获取是否开发环境
	 * @return
	 * author: pengyong
	 * 下午4:32:10
	 */
	public static boolean isDev(){		
		return PropertiesUtil.isDev();
	}
	
	/**
	 * 获得静态资源版本
	 */
	public static String getStaticResourceVersion(){
		return PropertiesUtil.getStaticResourceVersion();
	}	
	
	/**
	 * 资源文件开发环境
	 * @return
	 */
	public static boolean resourceDev(){
		return PropertiesUtil.resourceDev();
	}
	
	/**
	 * 获得向上取整的项目进度
	 * @param totalAmount
	 * @param id
	 * @return
	 */
	public static Integer getProgressCeil(BigDecimal totalAmount, Long id){
		String progress = getProjectNumberProgress(totalAmount, id);
		Double cp = Math.ceil(Double.parseDouble(progress));
		return cp.intValue();
	}
	
	/**
	 * 获得用户昵称
	 * @param id
	 * @return
	 */
	public String getMemberUserName(Long id){
		return memberService.getFormatMemberUserName(id);
	}
	
	/**
	 * 获取数据字典标签
	 * @param groupName	组名
	 * @return
	 */
	public static SysDict getSysDictByKey(String key, String groupName){
		SysDict value= null;
		if (StringUtils.isNotBlank(groupName) && StringUtils.isNotBlank(key)){
			for (SysDict dict : getDictList(groupName)){
				if (groupName.equals(dict.getGroupName()) && key.equals(dict.getKey())){
					return dict;
				}
			}
		}
		return value;
	}

	public static boolean isMstation(HttpServletRequest request){
		boolean result = true;
		return result;
	}
	
	public static String getMRootURL(){
		return PropertiesUtil.getProperties(MROOT_URL);
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
	 * 获取黑名单
	 * @return
	 */
	public static  Set<Long>   getBlackMemberId(){
		Set<Long> memberIds = Sets.newHashSet();
		List<SysDict> dictList = getDictList(Constant.BLACK_MEMBERIDS_GROUP);
		for (SysDict dict : dictList){
			memberIds.add(Long.valueOf(dict.getValue()));
		}
		return memberIds;
	}

	/**
	 * 
	 * @Description:获取微信appId
	 * @return
	 * @author: wangyanji
	 * @time:2016年1月29日 上午9:43:25
	 */
	public static String getWeixinAppID() {
		return PropertiesUtil.getWeixinAppID();
	}
	
	/**
	 * 
	 * @Description:获取测评分数
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年6月6日 上午11:27:39
	 */
	public static Integer getEvaluation(Long memberId){
		MemberInfoDto memberInfoDto=memberInfoService.getMemberInfoByMemberId(memberId);
		if(memberInfoDto!=null){
			return memberInfoDto.getEvaluationScore();
		}
		return 0;
	}
	
	//提现手续费值，从数据字典获取
		public static Integer getWithdrawalFees(){
			String fees = getDictValue("withdrawalFees", "withdrawal_fees", "0");
			if(StringUtil.isNotBlank(fees)){
				return Integer.parseInt(fees);
			}
			return 0;
		}
}
