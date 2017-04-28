package com.yourong.web.controller;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.co.model.CompanyJob;
import com.yourong.core.co.model.CompanyJobCategory;
import com.yourong.core.co.model.CompanyManage;
import com.yourong.core.co.model.CompanyProfile;
import com.yourong.core.co.model.query.CompanyJobQuery;
import com.yourong.web.service.CompanyJobCategoryService;
import com.yourong.web.service.CompanyJobService;
import com.yourong.web.service.CompanyManageService;
import com.yourong.web.service.CompanyProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 关于有融
 * Created by alan.zheng on 2017/4/1.
 */
@Controller
public class AboutController extends BaseController {
    @Autowired
    private CompanyProfileService companyProfileService;

    @Autowired
    private CompanyJobService companyJobService;

    @Autowired
    private CompanyJobCategoryService companyJobCategoryService;

    @Autowired
    private CompanyManageService companyManageService;
    /**
     * 关于有融
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/about/index.html")
    public ModelAndView toAboutUs(
            HttpServletRequest req,
            HttpServletResponse resp
    ) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/post/about");
        return model;
    }

    /**
     * 关于有融接口请求
     * @param req
     * @param resp
     * @return
     */
    @ResponseBody
    @RequestMapping("/about/event/ajax")
    public ResultDO<List<CompanyProfile>> toAboutUsAjax(
            HttpServletRequest req,
            HttpServletResponse resp
    ) {
        ResultDO<List<CompanyProfile>> resultDO = companyProfileService.queryList();
        return resultDO;
    }

    @RequestMapping(value = "/about/join")
    public String join(){
        return "/post/joinUs";
    }

    @ResponseBody
    @RequestMapping(value = "/about/job/category")
    public ResultDO<List<CompanyJobCategory>> jobCategory(){
        ResultDO<List<CompanyJobCategory>> resultDO =new ResultDO<>();
        resultDO=companyJobCategoryService.queryList();
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "/about/joinajax")
    public ResultDO<CompanyJob> joinAjax(CompanyJobQuery query){
        ResultDO<CompanyJob> resultDO=new ResultDO<>();
        if (query.getCurrentPage()>0){
            query.setStartRow((query.getCurrentPage()-1)*query.getPageSize());
        }
        query.setStatus(2);
        resultDO = companyJobService.queryPageCompanyJob(query);
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "/about/manageajax")
    public ResultDO<List<CompanyManage>> manageAjax(){
        ResultDO<List<CompanyManage>> resultDO=new ResultDO<>();
        resultDO = companyManageService.queryList();
        return resultDO;
    }

    /**
     * 合作伙伴
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/about/cooperator")
    public ModelAndView cooperator(
            HttpServletRequest req,
            HttpServletResponse resp
    ) {
        ModelAndView model = new ModelAndView();
        model.setViewName("post/cooperator");
        return model;
    }


    /**
     * 信息披露
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/about/disclosure")
    public ModelAndView disclosure(
            HttpServletRequest req,
            HttpServletResponse resp
    ) {
        ModelAndView model = new ModelAndView();
        model.setViewName("post/disclosure");
        return model;
    }

    /**
     * 资质荣誉
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/about/honor")
    public ModelAndView honor(
            HttpServletRequest req,
            HttpServletResponse resp
    ) {
        ModelAndView model = new ModelAndView();
        model.setViewName("post/honor");
        return model;
    }

    /**
     * 新安全保障
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("post/subject/safetyAssurance")
    public ModelAndView safetyAssurancePage(
            HttpServletRequest req,
            HttpServletResponse resp
    ) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/post/safetyAssurance");
        return model;
    }
}
