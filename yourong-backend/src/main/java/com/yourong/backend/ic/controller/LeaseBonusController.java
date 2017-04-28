package com.yourong.backend.ic.controller;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.yourong.backend.BaseController;
import com.yourong.backend.ic.service.LeaseBonusService;
import com.yourong.backend.ic.service.LeaseDetailService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.LeaseBonus;
import com.yourong.core.ic.model.LeaseDetail;

@Controller
@RequestMapping("leaseBonus")
public class LeaseBonusController extends BaseController {
	@Autowired
	private LeaseBonusService leaseBonusService;
	
	@Autowired
	private LeaseDetailService leaseDetailService;

	@RequestMapping(value = "index")
	@RequiresPermissions("leaseBonus:index")
	public String showLeaseBonusIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "/ic/leaseBonus/index";
	}

	@RequestMapping(value = "ajax")
	@RequiresPermissions("leaseBonus:ajax")
	@ResponseBody
	public Object showLeaseBonusPages(HttpServletRequest req, HttpServletResponse resp)
			throws ServletRequestBindingException {
		Map<String, Object> map = Maps.newHashMap();
		getFilterMapFromRequest(req, map);
		Page<LeaseBonus> pageRequest = new Page<LeaseBonus>();
		getPageInfoFromRequest(req, pageRequest);
		Page<LeaseBonus> pager = leaseBonusService.findByPage(pageRequest, map);
		return pager;
	}

	@RequestMapping(value = "save")
	@RequiresPermissions("leaseBonus:save")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "租赁分红模块", desc = "保存租赁")
	public Object saveLeaseBonus(@ModelAttribute LeaseBonus leaseBonus, HttpServletRequest req, HttpServletResponse resp) {
		int insertSelective;
		if (leaseBonus.getId() != null) {
//			leaseBonus.setBonusStatus(StatusEnum.LEASE_BONUS_WAIT_BONUS.getStatus());
//			leaseBonus.setBonusDate(DateUtils.getCurrentDate());
			insertSelective = leaseBonusService.updateByPrimaryKeySelective(leaseBonus);
		} else {
			insertSelective = leaseBonusService.insert(leaseBonus);
		}
		return insertSelective;
	}

	@RequestMapping(value = "show")
	@RequiresPermissions("leaseBonus:show")
	@ResponseBody
	public Object showLeaseBonus(HttpServletRequest req, HttpServletResponse resp) {
		long id = ServletRequestUtils.getLongParameter(req, "id", 0);
		LeaseBonus leaseBonus = leaseBonusService.selectByPrimaryKey(id);
		return leaseBonus;
	}

	@RequestMapping(value = "delete")
	@RequiresPermissions("leaseBonus:delete")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "租赁分红模块", desc = "删除租赁")
	public Object deleteLeaseBonus(HttpServletRequest req, HttpServletResponse resp) {
		long[] id = ServletRequestUtils.getLongParameters(req, "id[]");
		leaseBonusService.batchDelete(id);
		return "1";
	}
	
	@RequestMapping(value = "bonus")
	@RequiresPermissions("leaseBonus:bonus")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "租赁模块", desc = "结算租赁")
	public ResultDO<LeaseBonus> doLeaseBonus(@ModelAttribute LeaseDetail leaseDetail, HttpServletRequest req, HttpServletResponse resp) {
		try {
			//TODO TEST 结算接口完成之后删除  
//			ResultDO<LeaseBonus> resultDO = new ResultDO<LeaseBonus>();
//			LeaseBonus bonus = leaseBonusService.selectByPrimaryKey(leaseDetail.getLeaseBonusId());
//			bonus.setLeaseStatus(StatusEnum.LEASE_BONUS_BEEN_LEASED.getStatus());
//			if(leaseBonusService.updateByPrimaryKeySelective(bonus)>0){
//				BigDecimal unitRental = FormulaUtil.getUnitRental(leaseDetail.getTotalRental(), leaseDetail.getLeaseDays());
//				leaseDetail.setRental(unitRental);
//				leaseDetail.setBonusStatus(StatusEnum.LEASE_BONUS_DOING_BONUS.getStatus());
//				leaseDetailService.insertSelective(leaseDetail);
//				resultDO.setSuccess(true);
//			}else {
//				resultDO.setSuccess(false);
//			}
//			return resultDO;
			return leaseBonusService.doLeaseBonus(leaseDetail);
		} catch (Exception e) {
			logger.error("执行租赁分红发生异常，租赁分红id："+ leaseDetail.getLeaseBonusId(), e);
		}
		return null;
	}
}