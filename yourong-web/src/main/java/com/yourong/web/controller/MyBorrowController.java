package com.yourong.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.TypeEnum;
import com.yourong.core.fin.model.OverdueRepayLog;
import com.yourong.core.fin.model.biz.OverdueRepayLogBiz;
import com.yourong.core.ic.model.ProjectInterestBiz;
import com.yourong.core.ic.model.biz.DirectInterestForBorrow;
import com.yourong.core.ic.model.query.ProjectBorrowQuery;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.web.service.ProjectService;

/**
 * 
 * @desc 我的借款
 * @author chaisen 2016年1月26日下午2:19:03
 */
@Controller	
@RequestMapping("myBorrow")
public class MyBorrowController extends BaseController {

	@Autowired
	private ProjectService projectService;

	/**
	 * 
	 * @Description:我的借款
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年2月2日 上午10:11:05
	 */
	@RequestMapping(value = "borrowMoney")
	public ModelAndView borrowMoney(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		Long memberId = getMember().getId();
		ResultDO result = projectService.getBorrowCenterData(memberId);
		model.addObject("data", result.getResult());
		model.setViewName("/member/borrowMoney/borrowMoney");
		return model;
	}

	/**
	 * 
	 * @Description:获取滞纳金，逾期利息，逾期本金
	 * @param req
	 * @param resp
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年2月2日 下午2:12:02
	 */
	@RequestMapping(value = "getAmount")
	public ModelAndView getOverdueInfo(HttpServletRequest req, HttpServletResponse resp, @RequestParam("projectId") Long projectId) {
		ModelAndView model = new ModelAndView();
		ResultDO result = projectService.getOverdueInfo(projectId);
		model.addObject("data", result.getResult());
		model.setViewName("/member/borrowMoney/borrowMoney");
		return model;
	}

	/**
	 * 
	 * @Description:借款列表(还款中、已还款、募集中)
	 * @param req
	 * @param resp
	 * @param query
	 * @return
	 * @author: chaisen
	 * @time:2016年1月26日 下午6:55:58
	 */
	@RequestMapping(value = "/borrow/list", method = RequestMethod.GET)
	public ModelAndView getBorrowList(HttpServletRequest req, HttpServletResponse resp, ProjectBorrowQuery query) {
		ModelAndView mv = new ModelAndView();
		Long memberId = getMember().getId();
		query.setBorrowerId(memberId);
		ResultDO<ProjectInterestBiz> result = new ResultDO<ProjectInterestBiz>();
		//募集中
		if (query.getType() == 1) {
			mv.setViewName("member/borrowMoney/raisingList");
			result.setPage(projectService.getBorrowRaisingList(query));
		//已还款
		}else if(query.getType() == 2){
			mv.setViewName("member/borrowMoney/hasPayBorrowList");
			result.setPage(projectService.getHasPayoffBorrow(query));
		//还款中
		} else {
			mv.setViewName("member/borrowMoney/borrowList");
			result.setPage(projectService.getPayingBorrowList(query));
		}
		mv.addObject("result", result.getPage());
		return mv;
	}

	/**
	 * 
	 * @Description:逾期列表
	 * @param req
	 * @param resp
	 * @param query
	 * @return
	 * @author: chaisen
	 * @time:2016年2月17日 下午1:44:01
	 */
	@RequestMapping(value = "/borrow/overdueList", method = RequestMethod.GET)
	public ModelAndView getBorrowOverdueList(HttpServletRequest req, HttpServletResponse resp, ProjectBorrowQuery query) {
		ModelAndView mv = new ModelAndView();
		Long memberId = getMember().getId();
		query.setBorrowerId(memberId);
		mv.setViewName("member/borrowMoney/overdueList");
		ResultDO<ProjectInterestBiz> result = new ResultDO<ProjectInterestBiz>();
		result.setPage(projectService.getBorrowOverdueList(query));
		mv.addObject("result", result.getPage());
		return mv;
	}

	/**
	 * 
	 * @Description:流标列表
	 * @param req
	 * @param resp
	 * @param query
	 * @return
	 * @author: chaisen
	 * @time:2016年2月17日 下午3:29:08
	 */
	@RequestMapping(value = "/borrow/labelList", method = RequestMethod.GET)
	public ModelAndView getBorrowLabelList(HttpServletRequest req, HttpServletResponse resp, ProjectBorrowQuery query) {
		ModelAndView mv = new ModelAndView();
		Long memberId = getMember().getId();
		query.setBorrowerId(memberId);
		query.setType(4);
		mv.setViewName("member/borrowMoney/labelList");
		ResultDO<ProjectInterestBiz> result = new ResultDO<ProjectInterestBiz>();
		result.setPage(projectService.getBorrowLabelList(query));
		mv.addObject("result", result.getPage());
		return mv;
	}

	/**
	 * 
	 * @Description:获取项目本息记录
	 * @param req
	 * @param resp
	 * @param pageSize
	 * @return
	 * @author: chaisen
	 * @time:2016年1月26日 下午7:35:59
	 */
	@RequestMapping(value = "/interest/list")
	@ResponseBody
	public ResultDO<DirectInterestForBorrow> getInterestList(@RequestParam("projectId") Long projectId) {
		List<DirectInterestForBorrow> list = projectService.selectInterestByProjectId(projectId);
		ResultDO<DirectInterestForBorrow> result = new ResultDO<DirectInterestForBorrow>();
		result.setResultList(list);
		return result;
	}

	/**
	 * 
	 * @Description:垫资还款记录
	 * @param req
	 * @param resp
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年1月26日 下午8:00:22
	 */
	@RequestMapping(value = "/overdue/list")
	@ResponseBody
	public ResultDO<OverdueRepayLogBiz> getOverdueList(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam("projectId") Long projectId) {
		List<OverdueRepayLogBiz> list = projectService.getOverdueByProjectId(projectId,TypeEnum.OVERDUE_SETTLEMENT_TYPE_UNDERWRITE.getType());
		ResultDO<OverdueRepayLogBiz> result = new ResultDO<OverdueRepayLogBiz>();
		result.setResultList(list);
		return result;
	}
	/**
	 * 
	 * @Description:逾期还款记录
	 * @param req
	 * @param resp
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年5月27日 下午3:54:04
	 */
	@RequestMapping(value = "/overdueComm/list")
	@ResponseBody
	public ResultDO<OverdueRepayLogBiz> getOverdueCommList(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam("projectId") Long projectId) {
		List<OverdueRepayLogBiz> list = projectService.getCommonOverdueRepayLogRecord(projectId);
		ResultDO<OverdueRepayLogBiz> result = new ResultDO<OverdueRepayLogBiz>();
		result.setResultList(list);
		return result;
	}

	/**
	 * @Description:垫资还款
	 * @param req
	 * @param resp
	 * @param projectId
	 * @return
	 * @author: fuyili
	 * @time:2016年2月23日 上午11:33:43
	 */
	@RequestMapping(value = "toUnderwriteRepay", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<?> toUnderwriteRepay(HttpServletRequest req, HttpServletResponse resp, @RequestParam("projectId") Long projectId) {
		ResultDO<HostingCollectTrade> result = new ResultDO<HostingCollectTrade>();
		try {
			String payerIp = getIp(req);
			result = projectService.toUnderWriteRepay(projectId, payerIp);
			if(result.isSuccess()) {
				// 创建远程垫付还款代收
				projectService.createSinpayHostingCollectTradeForRepay(result.getResult(), payerIp);
			}
		} catch (Exception e) {
			logger.error("垫资还款失败，projectId={}", projectId, e);
		}
		return result;
	}
	/**
	 * 
	 * @Description:获取垫资还款详情
	 * @param req
	 * @param resp
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年2月29日 上午11:40:00
	 */
	@RequestMapping(value = "/underwrite/detail")
	@ResponseBody
	public ResultDO<OverdueRepayLogBiz> getUnderwriteDetail(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam("projectId") Long projectId) {
		ResultDO<OverdueRepayLogBiz> result = projectService.getUnderWriteAmountInfo(projectId);
		 if (result != null&&result.getResult()!=null) {
			 result.getResult().setOverdueList(projectService.getOverdueRecordListByProjectId(projectId).getResultList());
		 }
		return result;
	}
	/**
	 * 
	 * @Description:获取逾期还款详情
	 * @param req
	 * @param resp
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年5月30日 下午3:24:16
	 */
	@RequestMapping(value = "/overdue/detail")
	@ResponseBody
	public ResultDO<OverdueRepayLogBiz> getOverdueDetail(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam("projectId") Long projectId) {
		ResultDO<OverdueRepayLogBiz> result = projectService.getOverdueAmountInfo(projectId);
		 if (result != null&&result.getResult()!=null) {
			 result.getResult().setOverdueList(projectService.geOverdueDebtInterestListByProjectId(projectId).getResultList());
		 }
		return result;
	}
	
	/**
	 * @Description:普通逾期还款
	 * @param req
	 * @param resp
	 * @param projectId
	 * @return
	 * @author: fuyili
	 * @time:2016年6月4日 下午5:38:05
	 */
	@RequestMapping(value = "toOverdueRepay", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<?> toOverdueRepay(HttpServletRequest req, HttpServletResponse resp, @RequestParam("projectId") Long projectId) {
		ResultDO<OverdueRepayLog> result = new ResultDO<OverdueRepayLog>();
		try {
			String payerIp = getIp(req);
			result = projectService.toOverdueRepay(projectId, payerIp);
		} catch (Exception e) {
			logger.error("普通逾期还款失败，projectId={}", projectId, e);
		}
		return result;
	}
	/**
	 * 
	 * @Description:项目费用详情
	 * @param req
	 * @param resp
	 * @param projectId
	 * @return
	 * @author: chaisen
	 * @time:2016年6月12日 下午5:11:20
	 */
	@RequestMapping(value = "/borrow/feeDetail")
	@ResponseBody
	public ResultDO<ProjectInterestBiz> getBorrowFeeDetail(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam("projectId") Long projectId) {
		ResultDO<ProjectInterestBiz> result = projectService.getProjectFeeDetail(projectId);
		return result;
	}
}
