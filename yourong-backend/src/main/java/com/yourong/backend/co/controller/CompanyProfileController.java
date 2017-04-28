package com.yourong.backend.co.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yourong.backend.BaseController;
import com.yourong.backend.co.service.CompanyProfileService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.co.model.CompanyProfile;
import com.yourong.core.co.model.query.CompanyProfileQuery;
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
 * Created by alan.zheng on 2017/3/29.
 */
@Controller
@RequestMapping(value = "/companyprofile")
public class CompanyProfileController extends BaseController {
    @Autowired
    private CompanyProfileService companyProfileService;
    @RequestMapping(value = "/index")
    @RequiresPermissions("CompanyProfile:index")
    public String index(){
        return "/co/profile/index";
    }

    @ResponseBody
    @RequestMapping(value = "/ajax")
    public Object ajax(HttpServletRequest req){
        String pagesize= req.getParameter("iDisplayLength");
        String start= req.getParameter("iDisplayStart");
        Page<CompanyProfile> page=new Page<>();
        CompanyProfileQuery query= null;
        try {
            query = new CompanyProfileQuery();
            query.setPageSize(Integer.parseInt(pagesize));
            Integer startrow = Integer.parseInt(start);
            if (startrow>0){
                Integer cur = startrow/query.getPageSize() + 1;
                query.setCurrentPage(cur);
            }
        } catch (NumberFormatException e) {
            logger.error("查询条件数据解析异常",e.toString());
        }
        page=companyProfileService.queryPageCompanyProfile(query);
        return page;
    }

    @ResponseBody
    @RequestMapping(value = "/save")
    public ResultDO save(CompanyProfile companyProfile){
        ResultDO resultDO=new ResultDO();
        boolean result= companyProfileService.saveCompanyProfile(companyProfile);
        resultDO.setSuccess(result);
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "edit")
    public ResultDO<CompanyProfile> edit(@RequestParam(value = "id")Long id){
        ResultDO<CompanyProfile> resultDO=new ResultDO<>();
        CompanyProfile companyJobCategory = companyProfileService.queryById(id);
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
        if (companyProfileService.deleteProfile(id)){
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
            List<CompanyProfile> sorts= JSON.parseObject(sortstr,new TypeReference<ArrayList<CompanyProfile>>(){});
            companyProfileService.updateSortById(sorts);
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
        resultDO.setSuccess(companyProfileService.updateStatus(id,status));
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "releaseall")
    public ResultDO releaseAll(){
        ResultDO resultDO=new ResultDO<>();
        resultDO = companyProfileService.releaseAll();
        return resultDO;
    }
}
