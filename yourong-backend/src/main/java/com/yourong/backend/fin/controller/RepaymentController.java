package com.yourong.backend.fin.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yourong.backend.BaseController;
import com.yourong.backend.ic.service.ProjectService;
import com.yourong.backend.tc.service.TransactionService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.ProjectBiz;
import com.yourong.core.repayment.manager.SynchronizedHostingCollectTradeManager;
import com.yourong.core.repayment.manager.SynchronizedHostingPayTradeManager;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.HostingPayProjectMember;

/**
 * 还本付息，项目详情
 * Created by  on 2015/7/28.
 */
@Controller
@RequestMapping("repayment")
public class RepaymentController extends BaseController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private HostingCollectTradeManager hostingCollectTradeManager;
    @Autowired
    private HostingPayTradeManager hostingPayTradeManager;
    @Autowired
    private SynchronizedHostingCollectTradeManager synchronizedHostingCollectTradeManager;
    @Autowired
    private SynchronizedHostingPayTradeManager synchronizedHostingPayTradeManager;

    /**
     * 项目投资还本付息详情
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "projectInversts")
    public ModelAndView projectInverstsIndex(HttpServletRequest req, HttpServletResponse resp ,@RequestParam("projectId") Long projectId) {
        ModelAndView modelAndView =new ModelAndView();
        ProjectBiz projectById = projectService.findProjectById(projectId);
        modelAndView.setViewName("/fin/repayment/index");
        modelAndView.addObject("project",projectById);
        return modelAndView;
    }
    /**
     * 项目代收记录
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "hostingCollectTradeList")
    @ResponseBody
    public Object hostingCollectTradeList(HttpServletRequest req, HttpServletResponse resp,@RequestParam("projectId") Long projectId) throws ServletRequestBindingException {
        Page<HostingCollectTrade> pageRequest = new Page<HostingCollectTrade>();
        try {
            List<HostingCollectTrade> hostingCollectTrades = hostingCollectTradeManager.selectHostingCollectTradesBySourceId(projectId);
            int size = hostingCollectTrades.size();
            pageRequest.setiDisplayLength(size);
            pageRequest.setData(hostingCollectTrades);
            pageRequest.setPageNo(1);
            pageRequest.setiTotalRecords(size);
            pageRequest.setiTotalDisplayRecords(size);
            pageRequest.setiDisplayStart(0);
        } catch (ManagerException e) {
            logger.error("查询代收记录异常", e);
        }
        return pageRequest;
    }
    /**
     * 项目投资记录
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "hostingPayTradeList")
    @ResponseBody
    public Object hostingPayTradeList(HttpServletRequest req, HttpServletResponse resp ,@RequestParam("projectId") Long projectId) {
        Page<HostingPayProjectMember> pageRequest = new Page<HostingPayProjectMember>();
        try {
            List<HostingPayProjectMember> mapList = hostingPayTradeManager.selectProjectInverstAndHostingPayTrade(projectId);
            int size = mapList.size();
            pageRequest.setiDisplayLength(size);
            pageRequest.setData(mapList);
            pageRequest.setPageNo(1);
            pageRequest.setiTotalRecords(size);
            pageRequest.setiTotalDisplayRecords(size);
            pageRequest.setiDisplayStart(0);
        } catch (ManagerException e) {
            logger.error("查询代付记录异常", e);
        }
        return pageRequest ;
    }

    /**
     * 同步代收
     * @param req
     * @param resp
     * @return
     */
    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "sycnCollect")
    @ResponseBody
    public Object synchronizedHostingCollectTrade(HttpServletRequest req, HttpServletResponse resp) {
    	ResultDO retDO = new ResultDO();
    	try {
    		synchronizedHostingCollectTradeManager.synchronizedHostingCollectTrade();
    		retDO.setSuccess(true);
    	}catch(Exception e) {
    		logger.info("同步代收异常", e);
    		retDO.setSuccess(false);
    	}
    	return retDO ;
    }
    
    /**
     * 同步代付
     * @param req
     * @param resp
     * @return
     */
    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "sycnPay")
    @ResponseBody
    public Object synchronizedHostingPayTrade(HttpServletRequest req, HttpServletResponse resp) {
    	ResultDO retDO = new ResultDO();
    	try {
    		synchronizedHostingPayTradeManager.synchronizedHostingPayTrade();
    		retDO.setSuccess(true);
    	}catch(Exception e) {
    		logger.info("同步代收异常", e);
    		retDO.setSuccess(false);
    	}
    	return retDO ;
    }
}
