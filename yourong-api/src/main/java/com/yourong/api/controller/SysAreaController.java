package com.yourong.api.controller;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.sys.model.SysArea;
import com.yourong.api.service.SysAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("area")
public class SysAreaController {
	
	@Autowired
	private SysAreaService sysAreaService;

	@RequestMapping(value = "getAreaList")
	@ResponseBody
	public ResultDO<SysArea> getAreaList(HttpServletRequest req, HttpServletResponse resp){
		ResultDO<SysArea> result = new ResultDO<SysArea>();
		Long code = ServletRequestUtils.getLongParameter(req, "code",1L);
		List<SysArea> areaList = sysAreaService.getSysAreasByParentId(code);
		result.setResultList(areaList);
		return result;
		
	}
	
	
}
