package com.yourong.backend.sys.controller;

import com.google.common.collect.Maps;
import com.yourong.backend.BaseController;
import com.yourong.backend.sys.service.SysLogService;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("sysLog")
public class SysLogController extends BaseController {
    @Autowired
    private SysLogService sysLogService;

    @RequestMapping(value = "index")
    @RequiresPermissions("sysLog:index")
    public String showSysLogIndex(HttpServletRequest req, HttpServletResponse resp) {
        return "/sysLog/index";
    }

    @RequestMapping(value = "ajax")
    @RequiresPermissions("sysLog:ajax")
    @ResponseBody
    public Object showSysLogPages(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
        Map<String, Object> map = Maps.newHashMap();
        getFilterMapFromRequest(req, map);
        Page<SysLog> pageRequest = new Page<SysLog>();
        getPageInfoFromRequest(req, pageRequest);
        Page<SysLog> pager = sysLogService.findByPage(pageRequest, map);
        return pager;
    }

    @RequestMapping(value = "save")
    @RequiresPermissions("sysLog:save")
    @ResponseBody
    public Object saveSysLog(@ModelAttribute SysLog sysLog, HttpServletRequest req, HttpServletResponse resp) {
        if (sysLog.getId() != null) {
            int insertSelective = sysLogService.updateByPrimaryKeySelective(sysLog);
        } else {
            int insertSelective = sysLogService.insert(sysLog);
        }
        return "1";
    }

    @RequestMapping(value = "show")
    @RequiresPermissions("sysLog:show")
    @ResponseBody
    public Object showSysLog(HttpServletRequest req, HttpServletResponse resp) {
        Long id = ServletRequestUtils.getLongParameter(req, "id", 0);
        SysLog sysLog = sysLogService.selectByPrimaryKey(id);
        return sysLog;
    }

}