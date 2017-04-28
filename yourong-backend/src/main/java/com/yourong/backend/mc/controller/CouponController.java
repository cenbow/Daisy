package com.yourong.backend.mc.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.mc.service.CouponService;
import com.yourong.backend.sys.service.SysDictService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.sys.model.SysUser;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

@Controller
@RequestMapping("coupon")
public class CouponController extends BaseController {
	@Autowired
	private CouponService couponService;

	
	@Autowired
	private SysDictService sysDictService;

	@Autowired
	private MultipartResolver multipartResolver;

	/**
	 * 列表页面
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("coupon:index")
	public String showCouponTemplateIndex(HttpServletRequest req,
			HttpServletResponse resp) {
		return "/mc/coupon/index";
	}

	/**
	 * 列表数据
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
	@RequiresPermissions("coupon:ajax")
	@ResponseBody
	public Object showCouponDataPages(HttpServletRequest req,
			HttpServletResponse resp) throws ServletRequestBindingException {
		Page<Coupon> pageRequest = new Page<Coupon>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<Coupon> pager = couponService.findByPage(pageRequest, map);
		return pager;
	}

	/**
	 * 优惠券详情
	 * 
	 * @param id
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "detail")
	@RequiresPermissions("coupon:detail")
	@ResponseBody
	public Object showCouponDetail(@RequestParam("id") Long id,
			HttpServletRequest req, HttpServletResponse resp) {
		Coupon coupon = couponService.selectByPrimaryKey(id);
		return coupon;

	}

	/**
	 * 使用优惠券
	 * 
	 * @param id
	 * @param projectId
	 * @param transactionId
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "use")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "优惠券模块",desc = "使用优惠券")
	public Object useCoupon(@RequestParam("couponNo") String couponNo,
			@RequestParam("projectId") Long projectId,
			@RequestParam("transactionId") Long transactionId,
			HttpServletRequest req, HttpServletResponse resp) {
		int result = couponService.useCoupon(couponNo, projectId, transactionId);
		return result;
	}

	/**
	 * 可用优惠券
	 * 
	 * @param memberId
	 * @param couponType
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "usable")
	@ResponseBody
	public Object findUsableCouponsByMemberId(
			@RequestParam("memberId") Long memberId,
			@RequestParam("couponType") Integer couponType,
			@RequestParam("client") String client,
			@RequestParam("amountScope") BigDecimal amountScope,
			@RequestParam("daysScope") Integer daysScope,
			HttpServletRequest req, HttpServletResponse resp) {
		List<Coupon> result = couponService.findUsableCouponsByMemberId(
				memberId, couponType,client,amountScope,daysScope);
		return result;
	}

	/**
	 * 优惠券定时过期任务
	 * 
	 * @param memberId
	 * @param couponType
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "expire")
	@ResponseBody
	public Object expireCouponTask(HttpServletRequest req,
			HttpServletResponse resp) {
		Integer result = couponService.expireCouponTask();
		return result;
	}
	
	/**
	 * 优惠赠送页面
	 * @return
	 */
	@RequestMapping(value = "give")
	@RequiresPermissions("coupon:give")
	public String giveCoupons(){
		return "/mc/coupon/give";
	}
	
	/**
	 * 优惠券赠送
	 */
	@RequestMapping(value = "toGiveCoupon")
	@RequiresPermissions("coupon:toGiveCoupon")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "优惠券模块",desc = "后台赠送优惠券")
	public Object toGiveCoupon(HttpServletRequest req,
			HttpServletResponse resp,@RequestParam("mobiles")String mobiles,
			@RequestParam("couponTemplateId") Long couponTemplateId){
		SysUser userInfo = getCurrentLoginUserInfo();
		String[] mobileStrs = mobiles.split(",");
		int size = mobileStrs.length;
		Long[] mobileLongs = new Long[size];
		for (int i=0;i<size;i++) {
			mobileLongs[i] = Long.valueOf(mobileStrs[i]);
		}
		Map<String, Object> results = couponService.giveCoupon(mobileLongs, couponTemplateId,userInfo.getId());
		return results;
	}
	
	/*人气值赠送*/
	@RequestMapping(value = "toGivePopularity")
	@RequiresPermissions("coupon:toGivePopularity")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "优惠券模块",desc = "后台赠送人气值")
	public Object toGivePopularity(HttpServletRequest req,
			HttpServletResponse resp,@RequestParam("mobile")Long mobile,@RequestParam("income")BigDecimal income,@RequestParam("remarks") String remarks){
		SysUser userInfo = getCurrentLoginUserInfo();
		boolean result = couponService.givePopularity(mobile, income, remarks, userInfo.getId());
		return result;
	}
	
	
	/**
	 * 优惠券监控参数设置页面
	 * @return
	 */
	@RequestMapping(value = "monitorcoupon")
	@RequiresPermissions("coupon:monitorcoupon")
	public String monitorcoupon(){
		return "/mc/coupon/monitorcoupon";
	}
	
	
	/**
	 *获取优惠券监控参数
	 * 
	 * @return
	 * @throws ServletRequestBindingException 
	 */
	@RequestMapping(value = "getParam")
	@ResponseBody
	public Object getParam(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		
		//SysServiceUtils.getDictValue("couponmobile", "couponinfor", "");
		
		
		//List<SysDict> results= sysDictService.findByGroupName("couponinfor");
		
		Page<SysDict> pageRequest = new Page<SysDict>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		map.put("groupName", "couponinfor");
		Page<SysDict> results = sysDictService.findByPage(pageRequest,map);		 
		/*Map<String, String> results = null;
		results.put("couponmobile", SysServiceUtils.getDictValue("couponmobile", "couponinfor", ""));
		results.put("coupontemplateid",  SysServiceUtils.getDictValue("coupontemplateid", "couponinfor", ""));
		results.put("couponnum",  SysServiceUtils.getDictValue("couponmobile", "couponinfor", ""));*/
			
		
		return results;
	}
	
	 @RequestMapping(value = "saveMoParam")
	    @RequiresPermissions("coupon:saveMoParam")
	    @ResponseBody
	    @LogInfoAnnotation(moduleName = "优惠券模块",desc = "更改监控参数")
	    public Object saveSysDict(@ModelAttribute SysDict sysDict, HttpServletRequest req, HttpServletResponse resp) {
	        
	       	int insertSelective = sysDictService.updateByPrimaryKeySelective(sysDict); 
	         
	       return "1";		 
	    }
	 @RequestMapping(value = "showMoParam")
	    @RequiresPermissions("coupon:showMoParam")
	    @ResponseBody
	    public Object showMoParam(HttpServletRequest req, HttpServletResponse resp) {
	        long id =  ServletRequestUtils.getIntParameter(req, "id", 0); 
	        SysDict sysDict = sysDictService.selectByPrimaryKey(id);	 
	        	return sysDict;		 
	    }
	
	 /**
		 * 优惠券解锁页面
		 * @return
		 */
		@RequestMapping(value = "unlockcoupon")
		@RequiresPermissions("coupon:unlockcoupon")
		public String  unlockcoupon(){
			return "/mc/coupon/unlockcoupon";
		}
		
		@RequestMapping(value="unlocked")
		@RequiresPermissions("coupon:unlocked")
		@ResponseBody
		@LogInfoAnnotation(moduleName="优惠券模块",desc = "优惠券解锁")
		public Object unlockedCoupon(String couponNo){
		    	//优惠券解锁
		    	return couponService.unlockedCouponByCouponNo(couponNo);
		}
		
		/**
		 *查询符合条件的优惠券
		 * 
		 * @return
		 * @throws ServletRequestBindingException 
		 */
		@RequestMapping(value = "unlockQuery")
		@RequiresPermissions("coupon:unlockQuery")
		@ResponseBody
		public Object unlockQuery(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
			Page<Coupon> pageRequest = new Page<Coupon>();
			Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
			Page<Coupon> pager= couponService.getCouponByMember(pageRequest,map);
			return pager;
		}

	/**
	 * 发放优惠券
	 * @param req
	 * @param resp
     * @return
     */
	@RequestMapping(value = "sendCoupon")
	public Object sendCoupon(Model model,HttpServletRequest req, HttpServletResponse resp){
		ResultDO<Integer> rDO = new ResultDO<Integer>();
		Long templateId=null;
		SysUser userInfo = getCurrentLoginUserInfo();
		MultipartHttpServletRequest multipartRequest = null;

		if (StringUtils.isEmpty(req.getParameter("couponTemplateId"))){
			model.addAttribute("code","2");
			return "redirect:/coupon/sendResult";
		}
		templateId=Long.parseLong(req.getParameter("couponTemplateId"));
		Long mobile=Long.parseLong(req.getParameter("mobile"));
		try {
			if(multipartResolver.isMultipart(req)){
				String appPath = req.getSession().getServletContext().getRealPath("/");
				multipartRequest = (MultipartHttpServletRequest) req;
				Map<String,MultipartFile> fileMap = multipartRequest.getFileMap();


				File fileDir = new File(appPath + PropertiesUtil.getUploadDirectory());
				if (!fileDir.exists() && !fileDir.isDirectory()) {
					fileDir.mkdir();
				}
				String relativePath = getFileSavePath();

				for(Map.Entry<String,MultipartFile> en:fileMap.entrySet()){
					MultipartFile multipartFile = en.getValue();
					//文件保存
					File file = FileInfoUtil.writeFile(multipartFile.getInputStream(), multipartFile.getOriginalFilename(), appPath+relativePath);
					couponService.sendCoupon(file,templateId,mobile,userInfo.getId());
				}
				model.addAttribute("code","1");
				return "redirect:/coupon/sendResult";
			}else{
				rDO.isError();
				model.addAttribute("code","-1");
				return "redirect:/coupon/sendResult";
			}
		} catch (IOException e) {
			model.addAttribute("code","-1");
			return "redirect:/coupon/sendResult";
		}finally {
			if(multipartRequest != null){
				multipartResolver.cleanupMultipart(multipartRequest);
			}
		}
	}

	@RequestMapping(value = "/sendResult")
	public String sendResult(Model model,String code){
		if ("-1".equals(code)){
			model.addAttribute("msg","优惠券发送异常");
		}
		if ("1".equals(code)){
			model.addAttribute("msg","优惠券正在发送中");
		}
		if ("2".equals(code)){
			model.addAttribute("msg","请填写优惠券模板id");
		}
		return "/mc/coupon/sendresult";
	}

	/**
	 * 文件保存地址
	 * @return
	 */
	private String getFileSavePath(){
		StringBuffer sb = new StringBuffer();
		sb.append(ConfigUtil.getInstance().getUploadDirectory());
		sb.append(File.separator);
		sb.append(DateUtils.getYear(DateUtils.getCurrentDate()));
		sb.append(File.separator);
		sb.append(DateUtils.getMonth(DateUtils.getCurrentDate()));
		return sb.toString();
	}
	
}
