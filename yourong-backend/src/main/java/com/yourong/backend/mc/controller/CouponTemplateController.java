package com.yourong.backend.mc.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yourong.backend.mc.service.CouponTemplateSMSService;
import com.yourong.core.mc.model.CouponTemplateSMS;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.mc.service.CouponTemplatePrintService;
import com.yourong.backend.mc.service.CouponTemplateService;
import com.yourong.backend.utils.SysServiceUtils;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.CouponEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.mc.model.CouponTemplatePrint;
import com.yourong.core.sys.model.SysUser;

@Controller
@RequestMapping("couponTemplate")
public class CouponTemplateController extends BaseController {
	@Autowired
	private CouponTemplateService couponTemplateService;

	@Autowired
	private CouponTemplatePrintService couponTemplatePrintService;

	@Autowired
	private CouponTemplateSMSService couponTemplateSMSService;

	/**
	 * 列表页面
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("couponTemplate:index")
	public String showCouponTemplateIndex(HttpServletRequest req,
			HttpServletResponse resp) {
		return "/mc/couponTemplate/index";
	}

	/**
	 * 列表数据
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
	@RequiresPermissions("couponTemplate:ajax")
	@ResponseBody
	public Object showCouponTemplatePages(HttpServletRequest req,
			HttpServletResponse resp) throws ServletRequestBindingException {
		Page<CouponTemplate> pageRequest = new Page<CouponTemplate>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<CouponTemplate> pager = couponTemplateService.findByPage(
				pageRequest, map);
		return pager;
	}

	/**
	 * 保存
	 * @param couponTemplate
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "save")
	@RequiresPermissions("couponTemplate:save")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "优惠券模板模块",desc = "保存优惠券模板")
	public ResultDO<CouponTemplate> saveCouponTemplate(
			@ModelAttribute("couponTemplate") CouponTemplate couponTemplate,
			HttpServletRequest req, HttpServletResponse resp) {
		ResultDO<CouponTemplate> result = new ResultDO<CouponTemplate>();
		if(couponTemplate.getWebScope()==null){
			couponTemplate.setWebScope(0);
		}
		if(couponTemplate.getWapScope()==null){
			couponTemplate.setWapScope(0);
		}
		if(couponTemplate.getAppScope()==null){
			couponTemplate.setAppScope(0);
		}
		if(couponTemplate.getCouponType()!=null&&couponTemplate.getCouponType()==1){
			couponTemplate.setExtraInterestType(0);
		}
		if(couponTemplate.getExtraInterestDay()==null){
			couponTemplate.setExtraInterestDay(0);
		}
		if (CouponEnum.COUPONTEMPLATE_VAILD_CALC_TYPE_FOREVER.getCode().equals(
				couponTemplate.getVaildCalcType().toString())) {
			couponTemplate.setDays(null);
			couponTemplate.setStartDate(null);
			couponTemplate.setEndDate(null);
		}
		if (CouponEnum.COUPONTEMPLATE_VAILD_CALC_TYPE_TIMEZONE.getCode()
				.equals(couponTemplate.getVaildCalcType().toString())) {
			couponTemplate.setDays(null);
		}
		if (CouponEnum.COUPONTEMPLATE_VAILD_CALC_TYPE_DAYS.getCode().equals(
				couponTemplate.getVaildCalcType().toString())) {
			couponTemplate.setStartDate(null);
			couponTemplate.setEndDate(null);
		}
		if(couponTemplate.getExtraInterestDay()!=null&&couponTemplate.getExtraInterestDay()>0){
			if(couponTemplate.getExtraInterestDay()>couponTemplate.getDaysScope()){
				result.setResultCode(ResultCode.COUPON_TEMPLATE_EXTRA_DAY_ERROR);
				return result;
			}
		}
		if (couponTemplate.getId() != null) {
			result = couponTemplateService.update(couponTemplate);
		} else {
			SysUser user = getCurrentLoginUserInfo();
			couponTemplate.setCreateBy(user.getId());
			couponTemplate.setStatus(StatusEnum.COUPONTEMPLATE_STATUS_SAVE
					.getStatus());
			result = couponTemplateService.insertCouponTemplate(couponTemplate);
		}
		return result;
	}

	/**
	 * 删除优惠券模板
	 * @param id
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "delete")
	@RequiresPermissions("couponTemplate:delete")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "优惠券模板模块",desc = "删除优惠券模板")
	public ResultDO<CouponTemplate> deleteCouponTemplate(
			@RequestParam("id") Long id, HttpServletRequest req,
			HttpServletResponse resp) {
		ResultDO<CouponTemplate> result = new ResultDO<CouponTemplate>();
		if (id != null) {
			result = couponTemplateService.deleteByCouponTemplateId(id);
		} else {
			result.setSuccess(false);
		}
		return result;
	}

	/**
	 * 编辑显示数据
	 * @param id
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "show")
	@RequiresPermissions("couponTemplate:show")
	@ResponseBody
	public Object showCouponTemplate(@RequestParam("id") Long id,
			HttpServletRequest req, HttpServletResponse resp) {
		CouponTemplate couponTemplate = couponTemplateService
				.selectByPrimaryKey(id);
		return couponTemplate;
	}

	/**
	 * 印刷优惠券
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "print")
	@RequiresPermissions("couponTemplate:print")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "优惠券模板模块",desc = "印刷优惠券")
	public ResultDO<CouponTemplate> printCouponTemplate(
			@ModelAttribute("couponTemplatePrint") CouponTemplatePrint couponTemplatePrint,
			HttpServletRequest req, HttpServletResponse resp) {
		ResultDO<CouponTemplate> result = new ResultDO<CouponTemplate>();
		try {

			SysUser user = SysServiceUtils.getCurrentLoginUserInfo();
			couponTemplatePrint.setCreateBy(user.getId());
			result = couponTemplateService
					.printCouponTemplate(couponTemplatePrint);

		} catch (Exception e) {
			result.setSuccess(false);
		}
		return result;
	}

	/**
	 * 印刷详情
	 * @param couponTemplateId
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "printAjax")
	@RequiresPermissions("couponTemplate:printAjax")
	@ResponseBody
	public Object showPrintDatePages(
			@RequestParam("couponTemplateId") Long couponTemplateId,
			HttpServletRequest req, HttpServletResponse resp)
			throws ServletRequestBindingException {
		Page<CouponTemplatePrint> pageRequest = new Page<CouponTemplatePrint>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		map.put("couponTemplateId", couponTemplateId);
		Page<CouponTemplatePrint> pager = couponTemplatePrintService
				.findByPage(pageRequest, map);
		return pager;
	}

	@ResponseBody
	@RequestMapping(value = "top")
	@RequiresPermissions("couponTemplate:top")
	public Object top(@RequestParam(value = "couponTemplateId",defaultValue = "0") Long couponTemplateId){
		Map<String,Object> map=new HashMap<>();
		Date sorttime =new Date();
		if (couponTemplateId>0){
			if (couponTemplateService.updateSort(sorttime,couponTemplateId)>0){
				map.put("code",1);
				map.put("msg","置顶成功");
				return map;
			}
		}
		map.put("code",-1);
		map.put("msg","置顶失败");
		return map;
	}

	@ResponseBody
	@RequestMapping(value = "canceltop")
	@RequiresPermissions("couponTemplate:top")
	public Object canceltop(@RequestParam(value = "couponTemplateId",defaultValue = "0") Long couponTemplateId){
		Map<String,Object> map=new HashMap<>();
		Date sorttime=null;
		if (couponTemplateId>0){
			if (couponTemplateService.updateSort(sorttime,couponTemplateId)>0){
				map.put("code",1);
				map.put("msg","取消置顶成功");
				return map;
			}
		}
		map.put("code",-1);
		map.put("msg","取消置顶失败");
		return map;
	}

	@ResponseBody
	@RequestMapping(value = "sms")
	@RequiresPermissions("couponTemplate:couponTemplateSMS")
	public ResultDO<CouponTemplateSMS> couponTemplateSMS(@RequestParam(value = "templateid",defaultValue = "0")Long templateid){
		ResultDO<CouponTemplateSMS> result = new ResultDO<CouponTemplateSMS>();
		CouponTemplateSMS couponTemplateSMS= couponTemplateSMSService.queryByTemplateId(templateid);
		if (couponTemplateSMS==null){
			result.setSuccess(false);
			return result;
		}
		result.setResult(couponTemplateSMS);
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "savesms")
	@RequiresPermissions("couponTemplate:saveCouponTemplateSMS")
	public Object saveCouponTemplateSMS(CouponTemplateSMS couponTemplateSMS){
		Map<String,Object> map=new HashMap<>();
		if (couponTemplateSMS.getTemplateId()==null){
			map.put("code",-1);
			map.put("msg","保存失败");
			return map;
		}
		if (couponTemplateSMS.getId()!=null&&couponTemplateSMS.getId()>0){    //更新
			couponTemplateSMS.setUpdateTime(new Date());
		}else {
			couponTemplateSMS.setCreateTime(new Date());
			couponTemplateSMS.setDelFlag(1);
		}
		if (couponTemplateSMS.getStatus()==null){
			couponTemplateSMS.setStatus(0);
		}else {
			couponTemplateSMS.setStatus(1);
		}
		if (couponTemplateSMSService.saveCouponTemplateSMS(couponTemplateSMS)){
			map.put("code",1);
			map.put("msg","保存成功");
			return map;
		}
		map.put("code",-1);
		map.put("msg","保存失败");
		return map;
	}

}
