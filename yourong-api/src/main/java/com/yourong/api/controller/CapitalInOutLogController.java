package com.yourong.api.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.api.dto.CapitalInOutLogDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.CapitalInOutLogService;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.query.CapitalQuery;

@Controller
@RequestMapping("security/capitalInOutLog")
public class CapitalInOutLogController extends BaseController {
	@Autowired
    private CapitalInOutLogService capitalInOutLogService;
	
	@RequestMapping(value = "queryCapitalInOutLogList", method = RequestMethod.POST, headers = {"Accept-Version=1.0.1"})
    @ResponseBody
	public ResultDTO p2pQueryCapitalInOutLogList(HttpServletRequest req, HttpServletResponse resp){
		ResultDTO result = new ResultDTO();
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo",1);
		CapitalQuery capitalQuery = new CapitalQuery();
		capitalQuery.setCurrentPage(pageNo);
		capitalQuery.setPageSize(20);
		capitalQuery.setMemberId(getMemberID(req));
		Page<CapitalInOutLogDto>  capitalInOutLogList = capitalInOutLogService.p2pQueryCapitalInOutLogList(capitalQuery);
		result.setResult(capitalInOutLogList);
		return result;
	}
	
	@RequestMapping(value = "queryCapitalInOutLogList", method = RequestMethod.POST, headers = {"Accept-Version=1.3.0"})
    @ResponseBody
	public ResultDTO queryCapitalInOutLogList(HttpServletRequest req, HttpServletResponse resp){
		ResultDTO result = new ResultDTO();
		int pageNo = ServletRequestUtils.getIntParameter(req, "pageNo",1);
		CapitalQuery capitalQuery = new CapitalQuery();
		capitalQuery.setCurrentPage(pageNo);
		capitalQuery.setPageSize(20);
		capitalQuery.setMemberId(getMemberID(req));
		Page<CapitalInOutLogDto>  capitalInOutLogList = capitalInOutLogService.queryCapitalInOutLogList(capitalQuery);
		result.setResult(capitalInOutLogList);
		return result;
	}
}
