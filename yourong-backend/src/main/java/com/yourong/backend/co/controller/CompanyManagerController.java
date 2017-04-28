package com.yourong.backend.co.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yourong.backend.BaseController;
import com.yourong.backend.co.service.CompanyManagerService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.co.model.CompanyManage;
import com.yourong.core.co.model.query.CompanyManageQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alan.zheng on 2017/4/1.
 */
@Controller
@RequestMapping(value = "companymanager")
public class CompanyManagerController extends BaseController {
    @Autowired
    private CompanyManagerService companyManagerService;
    @RequestMapping(value = "/index")
    @RequiresPermissions("CompanyManager:index")
    public String index(){
        return "/co/manager/index";
    }

    @ResponseBody
    @RequestMapping(value = "/ajax")
    public Object ajax(HttpServletRequest req){
        String pagesize= req.getParameter("iDisplayLength");
        String start= req.getParameter("iDisplayStart");
        Page<CompanyManage> page=new Page<>();
        CompanyManageQuery query= null;
        try {
            query = new CompanyManageQuery();
            query.setPageSize(Integer.parseInt(pagesize));
            Integer startrow = Integer.parseInt(start);
            if (startrow>0){
                Integer cur = startrow/query.getPageSize() + 1;
                query.setCurrentPage(cur);
            }
        } catch (NumberFormatException e) {
            logger.error("查询条件数据解析异常",e.toString());
        }
        page=companyManagerService.queryPageCompanyManage(query);
        return page;
    }

    @ResponseBody
    @RequestMapping(value = "/save")
    public ResultDO save(HttpServletRequest req,CompanyManage companyManage,@RequestParam(value = "managerAttachmentsData")String managerAttachmentsData){
        ResultDO resultDO=new ResultDO();
        String appPath = req.getSession().getServletContext().getRealPath("/");
        List<BscAttachment> achments= new ArrayList<>();
        if (!StringUtils.isEmpty(managerAttachmentsData)){
            List<BscAttachment> common= JSON.parseArray(managerAttachmentsData,BscAttachment.class);
            if (Collections3.isNotEmpty(common)){
                for (BscAttachment bsc:common) {
                    achments.add(bsc);
                }
            }
        }
        boolean result= companyManagerService.saveCompanyManage(companyManage,appPath,achments);
        resultDO.setSuccess(result);
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "edit")
    public ResultDO<CompanyManage> edit(@RequestParam(value = "id")Long id){
        ResultDO<CompanyManage> resultDO=new ResultDO<>();
        CompanyManage companyJobCategory = companyManagerService.queryById(id);
        if (companyJobCategory == null){
            resultDO.setSuccess(false);
        }else {
            resultDO.setResult(companyJobCategory);
            resultDO.setSuccess(true);
        }
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "delete")
    public ResultDO delete(@RequestParam(value = "id")Long id){
        ResultDO resultDO=new ResultDO<>();
        if (companyManagerService.deleteManager(id)){
            resultDO.setSuccess(true);
            return resultDO;
        }
        resultDO.setSuccess(false);
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "savesort")
    public ResultDO saveSort(@RequestParam(value = "sortstr")String sortstr){
        ResultDO resultDO=new ResultDO<>();
        try {
            List<CompanyManage> sorts= JSON.parseObject(sortstr,new TypeReference<ArrayList<CompanyManage>>(){});
            companyManagerService.updateSortById(sorts);
            resultDO.setSuccess(true);
            return resultDO;
        } catch (Exception e) {
            resultDO.setSuccess(false);
            return resultDO;
        }
    }

    @ResponseBody
    @RequestMapping(value = "updatestatus")
    public ResultDO updateStatus(@RequestParam(value = "id")Long id,@RequestParam(value = "status")Integer status){
        ResultDO resultDO=new ResultDO<>();
        resultDO.setSuccess(companyManagerService.updateStatus(id,status));
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "releaseall")
    public ResultDO releaseAll(){
        ResultDO resultDO=new ResultDO<>();
        resultDO = companyManagerService.releaseAll();
        return resultDO;
    }
}
