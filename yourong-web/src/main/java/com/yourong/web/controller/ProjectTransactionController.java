package com.yourong.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.core.tc.model.biz.TransactionForProject;
import com.yourong.core.tc.model.query.TransactionQuery;
import com.yourong.web.dto.TransactionForProjectDto;
import com.yourong.web.service.TransactionService;

@Controller
@RequestMapping("projectTransaction")
public class ProjectTransactionController {
	@Autowired
	private TransactionService transactionService;
	/**
	 * 项目详情页投资记录接口
	 * @param req
	 * @param resp
	 * @param projectId
	 * @return
	 */
	@RequestMapping(value = "/detail/transactions")
    public String getProjectDetailTransactions(
    		HttpServletRequest req, 
    		HttpServletResponse resp    		
    		)  {
		Long projectId = ServletRequestUtils.getLongParameter(req, "projectId",0L);		
		if(projectId == null || projectId == 0L){
			return "/404";
		}		
    	TransactionQuery transactionQuery = new TransactionQuery();
    	transactionQuery.setPageSize(0);
    	transactionQuery.setProjectId(projectId);
    	List<TransactionForProject> page = transactionService.selectTransactionForProjectsForPage(transactionQuery);
    	req.setAttribute("orderList", page);
    	return "/transaction/transactionsDetail";
    }
	
	
	/**
	 * 转让项目详情页投资记录接口
	 * @param req
	 * @param resp
	 * @param projectId
	 * @return
	 */
	@RequestMapping(value = "/detail/transferTransactions")
    public String getTransferProjectDetailTransactions(
    		HttpServletRequest req, 
    		HttpServletResponse resp    		
    		)  {
		Long projectId = ServletRequestUtils.getLongParameter(req, "projectId",0L);		
		if(projectId == null || projectId == 0L){
			return "/404";
		}		
    	TransactionQuery transactionQuery = new TransactionQuery();
    	transactionQuery.setPageSize(0);
    	transactionQuery.setProjectId(projectId);
    	List<TransactionForProject> page = transactionService.getTransferProjectDetailTransactions(transactionQuery);
    	req.setAttribute("tranList", page);
    	return "/transaction/transferTransDetail";
    }
	
//	@RequestMapping("/queryTransactionDetail")
//	@ResponseBody
//	public Object getTransactionDetailByProjectId(HttpServletRequest req, HttpServletResponse resp){
//		Long projectId = ServletRequestUtils.getLongParameter(req, "projectId",0L);
//		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo",1);
//		TransactionQuery transactionQuery = new TransactionQuery();
//		transactionQuery.setPageSize(50);
//		transactionQuery.setProjectId(projectId);
//		transactionQuery.setCurrentPage(pageNo);
//		Page<TransactionForProject> page = transactionService.selectTransactionForProjectsForPage(transactionQuery);
//		List<TransactionForProjectDto> list = Lists.newArrayList();
//		if(Collections3.isNotEmpty(page.getData())){
//			list = BeanCopyUtil.mapList(page.getData(), TransactionForProjectDto.class);
//		}
//		return list;
//	}

}
