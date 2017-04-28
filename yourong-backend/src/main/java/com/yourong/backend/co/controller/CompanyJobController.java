package com.yourong.backend.co.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yourong.backend.BaseController;
import com.yourong.backend.co.service.CompanyJobCategoryService;
import com.yourong.backend.co.service.CompanyJobService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.co.model.CompanyJob;
import com.yourong.core.co.model.CompanyJobCategory;
import com.yourong.core.co.model.query.CompanyJobCategoryQuery;
import com.yourong.core.co.model.query.CompanyJobQuery;
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
@RequestMapping(value = "/companyjob")
public class CompanyJobController extends BaseController {
    @Autowired
    private CompanyJobService companyJobService;
    @Autowired
    private CompanyJobCategoryService companyJobCategoryService;

    @RequestMapping(value = "/index")
    @RequiresPermissions("CompanyJob:index")
    public String index(){
        return "/co/job/jobindex";
    }

    @RequestMapping(value = "/category")
    @RequiresPermissions("CompanyJob:category")
    public String category(){
        return "/co/job/categoryindex";
    }

    @ResponseBody
    @RequestMapping(value = "/jobajax")
    public Object jobAjax(HttpServletRequest req){
        String categoryId=req.getParameter("query_categoryId");
        String pagesize= req.getParameter("iDisplayLength");
        String start= req.getParameter("iDisplayStart");
        Integer category;
        try {
            category=Integer.parseInt(categoryId);
        } catch (NumberFormatException e) {
            category=null;
        }
        Page<CompanyJob> page=new Page<>();
        CompanyJobQuery query= null;
        try {
            query = new CompanyJobQuery();
            query.setPageSize(Integer.parseInt(pagesize));
            query.setCategoryId(category);
            Integer startrow = Integer.parseInt(start);
            if (startrow>0){
                Integer cur = startrow/query.getPageSize() + 1;
                query.setCurrentPage(cur);
            }
        } catch (NumberFormatException e) {
            logger.error("查询条件数据解析异常",e.toString());
        }
        page=companyJobService.queryPageCompanyJob(query);
        return page;
    }

    @ResponseBody
    @RequestMapping(value = "/categoryajax")
    public Object categoryAjax(HttpServletRequest req){
        String pagesize= req.getParameter("iDisplayLength");
        String start= req.getParameter("iDisplayStart");
        Page<CompanyJobCategory> page=new Page<>();
        CompanyJobCategoryQuery query = null;
        try {
            query = new CompanyJobCategoryQuery();
            query.setPageSize(Integer.parseInt(pagesize));
            Integer startrow = Integer.parseInt(start);
            if (startrow>0){
                Integer cur = startrow/query.getPageSize() + 1;
                query.setCurrentPage(cur);
            }
        } catch (NumberFormatException e) {
            logger.error("查询条件数据解析异常",e.toString());
        }
        page=companyJobCategoryService.queryPageCompanyJobCategory(query);
        return page;
    }

    @ResponseBody
    @RequestMapping(value = "/savejob")
    public ResultDO saveCategory(CompanyJob companyJob){
        ResultDO resultDO=new ResultDO();
        boolean result= companyJobService.saveCompanyJob(companyJob);
        resultDO.setSuccess(result);
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "/savecategory")
    public ResultDO saveCategory(CompanyJobCategory category){
        ResultDO resultDO=new ResultDO();
        boolean result= companyJobCategoryService.saveCompanyJobCategory(category);
        resultDO.setSuccess(result);
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "editcategory")
    public ResultDO<CompanyJobCategory> editCategory(@RequestParam(value = "categoryid")Long categoryid){
        ResultDO<CompanyJobCategory> resultDO=new ResultDO<>();
        CompanyJobCategory companyJobCategory = companyJobCategoryService.queryById(categoryid);
        if (companyJobCategory == null){
            resultDO.setSuccess(false);
        }else {
            resultDO.setResult(companyJobCategory);
            resultDO.setSuccess(true);
        }
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "editjob")
    public ResultDO<CompanyJob> editJob(@RequestParam(value = "jobid")Long jobid){
        ResultDO<CompanyJob> resultDO=new ResultDO<>();
        CompanyJob companyJob = companyJobService.queryById(jobid);
        if (companyJob == null){
            resultDO.setSuccess(false);
        }else {
            resultDO.setResult(companyJob);
            resultDO.setSuccess(true);
        }
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "deletejob")
    public ResultDO deleteJob(@RequestParam(value = "jobid")Long jobid){
        ResultDO<CompanyJob> resultDO=new ResultDO<>();
        if (companyJobService.deleteCompanyJob(jobid)){
            resultDO.setSuccess(true);
            return resultDO;
        }
        resultDO.setSuccess(false);
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "deletecategory")
    public ResultDO deleteCategory(@RequestParam(value = "categoryid")Long categoryid){
        ResultDO<CompanyJob> resultDO=new ResultDO<>();
        if (companyJobCategoryService.deleteCompanyJobCategory(categoryid)){
            resultDO.setSuccess(true);
            return resultDO;
        }
        resultDO.setSuccess(false);
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "queryCompanyJobCategory")
    public ResultDO<List<CompanyJobCategory>> queryCompanyJobCategory(){
        ResultDO<List<CompanyJobCategory>> resultDO =new ResultDO<>();
        List<CompanyJobCategory> list = companyJobCategoryService.queryList();
        if (list!=null){
            resultDO.setResult(list);
            resultDO.setSuccess(true);
            return resultDO;
        }
        resultDO.setSuccess(false);
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "savecategorysort")
    public ResultDO saveCategorySort(@RequestParam(value = "sortstr")String sortstr){
        ResultDO resultDO=new ResultDO<>();
        try {
            List<CompanyJobCategory> sorts= JSON.parseObject(sortstr,new TypeReference<ArrayList<CompanyJobCategory>>(){});
            companyJobCategoryService.updateSortById(sorts);
            resultDO.setSuccess(true);
            return resultDO;
        } catch (Exception e) {
            resultDO.setSuccess(false);
            return resultDO;
        }
    }

    @ResponseBody
    @RequestMapping(value = "savejobsort")
    public ResultDO savejobSort(@RequestParam(value = "sortstr")String sortstr){
        ResultDO resultDO=new ResultDO<>();
        try {
            List<CompanyJob> sorts= JSON.parseObject(sortstr,new TypeReference<ArrayList<CompanyJob>>(){});
            companyJobService.updateSortById(sorts);
            resultDO.setSuccess(true);
            return resultDO;
        } catch (Exception e) {
            resultDO.setSuccess(false);
            return resultDO;
        }
    }

    @ResponseBody
    @RequestMapping(value = "updatecategorystatus")
    public ResultDO updateCategoryStatus(@RequestParam(value = "id")Long id,@RequestParam(value = "status")Integer status){
        ResultDO resultDO=new ResultDO<>();
        resultDO.setSuccess(companyJobCategoryService.updateStatus(id,status));
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "updatejobstatus")
    public ResultDO updateJobStatus(@RequestParam(value = "id")Long id,@RequestParam(value = "status")Integer status){
        ResultDO resultDO=new ResultDO<>();
        resultDO.setSuccess(companyJobService.updateStatus(id,status));
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "releaseAllCategory")
    public ResultDO releaseAllCategory(){
        ResultDO resultDO=new ResultDO<>();
        resultDO = companyJobCategoryService.releaseAll();
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "releaseAllJob")
    public ResultDO releaseAllJob(){
        ResultDO resultDO=new ResultDO<>();
        resultDO = companyJobService.releaseAll();
        return resultDO;
    }
}
