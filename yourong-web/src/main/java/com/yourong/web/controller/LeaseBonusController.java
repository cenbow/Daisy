package com.yourong.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.LeaseDetail;
import com.yourong.core.ic.model.biz.LeaseBonusForFront;
import com.yourong.core.ic.model.biz.LeaseDetailBiz;
import com.yourong.core.ic.model.query.LeaseBonusDetailQuery;
import com.yourong.web.service.LeaseBonusService;

@Controller
@RequestMapping("leaseBonus")
public class LeaseBonusController extends BaseController {
	@Autowired
	private LeaseBonusService leaseBonusService;

	@RequestMapping(value = "leaseBonusList", method = RequestMethod.GET)
	public Object getLeaseBonusDataPage(@ModelAttribute("baseQueryParam") BaseQueryParam baseQueryParam) {
		baseQueryParam.setPageSize(8);
		ModelAndView mView = new ModelAndView();
		mView.setViewName("/activity/leaseBonusList");
		Page<LeaseBonusForFront> leaseBonusPage = new Page<LeaseBonusForFront>();
		leaseBonusPage = leaseBonusService.findLeaseBonusesByPage(baseQueryParam);
		mView.addObject("leaseBonusPage", leaseBonusPage);
		return mView;
	}

	/**
	 * 租赁分红-租赁结算明细
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/bonusDetail")
	@ResponseBody
	public Object bonusDetail(@ModelAttribute("leaseBonusDetailQuery") LeaseBonusDetailQuery leaseBonusDetailQuery,
			HttpServletRequest req, HttpServletResponse resp) {
		leaseBonusDetailQuery.setPageSize(48);
		LeaseDetailBiz leaseDetailBiz = leaseBonusService.findLeaseDetailBiz(leaseBonusDetailQuery);
		return leaseDetailBiz;
	}

	/**
	 * 租赁分红-租赁记录
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/leaseDetail")
	@ResponseBody
	public Object leaseDetail(@ModelAttribute("projectId") Long projectId, HttpServletRequest req,
			HttpServletResponse resp) {
		List<LeaseDetail> bonusDetail = leaseBonusService.findLeaseDetailByPage(projectId);
		return bonusDetail;
	}
}
