package com.yourong.backend.ic.controller;

import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.yourong.backend.BaseController;
import com.yourong.backend.ic.service.LeaseDetailService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.LeaseDetail;

@Controller
@RequestMapping("leaseDetail")
public class LeaseDetailController extends BaseController {
	@Autowired
	private LeaseDetailService leaseDetailService;

	@RequestMapping(value = "index")
	@RequiresPermissions("leaseDetail:index")
	public String showLeaseDetailIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "/leaseDetail/index";
	}

	@RequestMapping(value = "ajax")
	@RequiresPermissions("leaseDetail:ajax")
	@ResponseBody
	public Object showLeaseDetailPages(@RequestParam("leaseBonusId") Long leaseBonusId, HttpServletRequest req,
			HttpServletResponse resp) throws ServletRequestBindingException {
		Map<String, Object> map = Maps.newHashMap();
		getFilterMapFromRequest(req, map);
		Page<LeaseDetail> pageRequest = new Page<LeaseDetail>();
		getPageInfoFromRequest(req, pageRequest);
		map.put("leaseBonusId", leaseBonusId);
		Page<LeaseDetail> pager = leaseDetailService.findByPage(pageRequest, map);
		return pager;
	}

	@RequestMapping(value = "save")
	@RequiresPermissions("leaseDetail:save")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "租赁记录模块", desc = "保存租赁记录")
	public ResultDO<LeaseDetail> saveLeaseDetail(@ModelAttribute LeaseDetail leaseDetail,
			@ModelAttribute("oldTotalRental") BigDecimal oldTotalRental,
			@ModelAttribute("newTotalRental") BigDecimal newTotalRental, HttpServletRequest req,
			HttpServletResponse resp) throws ManagerException {
		ResultDO<LeaseDetail> leaseBonus = new ResultDO<LeaseDetail>();
		if(leaseDetail.getRental().compareTo(BigDecimal.ZERO)<=0){
			leaseBonus.setResultCode(ResultCode.LEASE_LEASE_RENTAL_NOT_MORE_ZERO_ERROR);
			return leaseBonus;
		}
		if (leaseDetail.getId() != null) {
			leaseBonus = leaseDetailService.updateByPrimaryKeySelective(leaseDetail,oldTotalRental,newTotalRental);
		} else {
			leaseBonus = leaseDetailService.insertSelective(leaseDetail);
		}
		return leaseBonus;
	}

	@RequestMapping(value = "show")
	@RequiresPermissions("leaseDetail:show")
	@ResponseBody
	public Object showLeaseDetail(HttpServletRequest req, HttpServletResponse resp) {
		long id = ServletRequestUtils.getLongParameter(req, "id", 0);
		LeaseDetail leaseDetail = leaseDetailService.selectByPrimaryKey(id);
		return leaseDetail;
	}

	@RequestMapping(value = "delete")
	@RequiresPermissions("leaseDetail:delete")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "租赁记录模块", desc = "删除租赁记录")
	public Object deleteLeaseDetail(@RequestParam("id") Long id, @RequestParam("leaseBonusId") Long leaseBonusId,
			@RequestParam("rental") BigDecimal rental, HttpServletRequest req, HttpServletResponse resp)
			throws ManagerException {
		int result = leaseDetailService.deleteByPrimaryKey(id, leaseBonusId, rental);
		return result;
	}
}