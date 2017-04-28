package com.yourong.backend.ic.controller;

import com.yourong.backend.BaseController;
import com.yourong.backend.ic.service.BorrowerCreditGradeService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.BorrowerCreditGrade;
import com.yourong.core.ic.model.query.BorrowerCreditGradeQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by alan.zheng on 2017/3/2.
 */
@Controller
@RequestMapping("borrowerCreditGrade")
public class BorrowerCreditGradeController extends BaseController {
    @Autowired
    private BorrowerCreditGradeService borrowerCreditGradeService;

    @RequiresPermissions("borrowerCreditGrade:index")
    @RequestMapping(value = "index")
    public String index(HttpServletRequest req, HttpServletResponse resp) {
        return "/ic/borrowerCreditGrade/index";
    }

    @ResponseBody
    @RequestMapping(value = "/ajax")
    public Page<BorrowerCreditGrade> ajax(HttpServletRequest req){
        String borrowerId=req.getParameter("borrowerId");
        String borrowerTrueName=req.getParameter("borrowerTrueName");
        String creditLevel=req.getParameter("creditLevel");
        String pagesize= req.getParameter("iDisplayLength");
        String start= req.getParameter("iDisplayStart");
        Page<BorrowerCreditGrade> page=new Page<>();
        BorrowerCreditGradeQuery query=new BorrowerCreditGradeQuery();
        if (!StringUtils.isEmpty(borrowerId)){
            try {
                query.setBorrowerId(Long.parseLong(borrowerId));
            } catch (NumberFormatException e) {
                query.setBorrowerId(null);
            }
        }
        query.setBorrowerTrueName(borrowerTrueName);
        query.setCreditLevel(creditLevel);
        query.setPageSize(Integer.parseInt(pagesize));
        query.setStartRow(Integer.parseInt(start));
        page=borrowerCreditGradeService.queryPageBorrowerCreditGrade(query);
        return page;
    }

    @ResponseBody
    @RequestMapping(value = "/queryBorrowerCredit")
    public ResultDO<BorrowerCreditGrade> queryBorrowerCredit(@RequestParam(value = "memberid") Long memberid){
        ResultDO<BorrowerCreditGrade> resultDO=new ResultDO<>();
        resultDO=borrowerCreditGradeService.queryBorrowerCredit(memberid);
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "/saveBorrowerCreditGrade")
    public ResultDO<String> saveBorrowerCreditGrade(@RequestParam(value = "borrowerId") Long borrowerId){
        ResultDO<String> resultDO=new ResultDO<>();
        BorrowerCreditGrade borrowerCreditGrade= borrowerCreditGradeService.queryByBorrowerId(borrowerId);
        if (borrowerCreditGrade!=null){
            resultDO.setSuccess(false);
            resultDO.setResult("该用户已存在");
            return resultDO;
        }
        if (borrowerCreditGradeService.saveBorrowerCreditGrade(borrowerId)){
            resultDO.setSuccess(true);
        }else {
            resultDO.setSuccess(false);
            resultDO.setResult("新增用户异常");
        }
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "/updateCreditLevel")
    public ResultDO<BorrowerCreditGrade> updateCreditLevel(@RequestParam(value = "borrowerId") Long borrowerId,@RequestParam(value = "creditLevel") String creditLevel){
        ResultDO<BorrowerCreditGrade> resultDO=new ResultDO<>();
        if (borrowerCreditGradeService.updateCreditLevel(creditLevel,borrowerId)){
            resultDO.setSuccess(true);
        }else {
            resultDO.setSuccess(false);
        }
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "/queryByBorrowerId")
    public ResultDO<BorrowerCreditGrade> queryByBorrowerId(@RequestParam(value = "borrowerId") Long borrowerId){
        ResultDO<BorrowerCreditGrade> resultDO=new ResultDO<>();
        BorrowerCreditGrade borrowerCreditGrade= borrowerCreditGradeService.queryByBorrowerId(borrowerId);
        resultDO.setResult(borrowerCreditGrade);
        return resultDO;
    }
}
