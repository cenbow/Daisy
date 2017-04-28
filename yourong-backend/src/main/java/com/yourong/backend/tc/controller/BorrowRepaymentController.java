package com.yourong.backend.tc.controller;

import com.yourong.backend.BaseController;
import com.yourong.backend.tc.service.TransactionInterestService;
import com.yourong.common.pageable.Page;
import com.yourong.core.tc.model.biz.BorrowInterestDetailBiz;
import com.yourong.core.tc.model.biz.BorrowRepayInterestBiz;
import com.yourong.core.tc.model.biz.BorrowRepayInterestCollect;
import com.yourong.core.tc.model.query.BorrowRepayQuery;
import com.yourong.core.tc.model.query.BorrowTransactionInterestQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by XR on 2016/9/10.
 */
@Controller
@RequestMapping(value = "/borrowrepayment")
public class BorrowRepaymentController extends BaseController {

    @Autowired
    private TransactionInterestService transactionInterestService;

    @RequestMapping(value = "/index")
    @RequiresPermissions("borrowRepayment:index")
    public String index(Model model){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        model.addAttribute("enddate",simpleDateFormat.format(new Date()).toString());
        return "/tc/borrowrepay/index";
    }

    @RequestMapping(value = "/ajaxborrowrepayment")
    @RequiresPermissions("borrowRepayment:ajaxBorrowRepayment")
    @ResponseBody
    public Object findInterestList(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
        BorrowRepayQuery query=new BorrowRepayQuery();
        Page<BorrowRepayInterestBiz> pager=new Page<BorrowRepayInterestBiz>();
        String enddate=req.getParameter("endDate");
        String borrowName=req.getParameter("borrowName");
        String borrowMobile=req.getParameter("borrowMobile");
        String borrowId=req.getParameter("borrowId");
        String pagesize= req.getParameter("iDisplayLength");
        String start= req.getParameter("iDisplayStart");
        if (StringUtils.isEmpty(enddate)){
            List<BorrowRepayInterestBiz> list=new ArrayList<BorrowRepayInterestBiz>();
            pager.setData(list);
            return pager;
        }
        try {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(enddate);
            if (date==null){
                return pager;
            }
            query.setEndDate(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        query.setBorrowName(borrowName);
        query.setBorrowMobile(borrowMobile);
        if (!StringUtils.isEmpty(borrowId)){
            query.setBorrowId(Long.parseLong(borrowId));
        }
        query.setPageSize(Integer.parseInt(pagesize));
        query.setStartRow(Integer.parseInt(start));
        pager = transactionInterestService.findPageBorrowRepayInterest(query);
        return pager;
    }

    @RequestMapping(value = "/ajaxcollectborrowrepayment")
    @RequiresPermissions("borrowRepayment:ajaxCollectBorrowRepayment")
    @ResponseBody
    public Object findInterestCollect(HttpServletRequest req, HttpServletResponse resp){
        BorrowRepayInterestCollect borrowRepayInterestCollect=new BorrowRepayInterestCollect();
        String enddatestr=req.getParameter("endDate");
        Date enddate=new Date();
        if (StringUtils.isEmpty(enddatestr)){
            return borrowRepayInterestCollect;
        }
        try {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            enddate = simpleDateFormat.parse(enddatestr);
            if (enddate==null){
                return borrowRepayInterestCollect;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        BorrowRepayQuery query=new BorrowRepayQuery();
        query.setEndDate(enddate);
        Map<String,Object> map=new HashMap<>();
        Page<BorrowRepayInterestBiz> pager=new Page<BorrowRepayInterestBiz>();
        pager = transactionInterestService.findPageBorrowRepayInterest(query);
        int waitnum=0;
        BigDecimal extraInterest=new BigDecimal(0);
        BigDecimal payableInterest=new BigDecimal(0);
        BigDecimal payablePrincipal=new BigDecimal(0);
        BigDecimal projectExtraInterest=new BigDecimal(0);
        if (pager!=null&&pager.getData()!=null&&pager.getData().size()>0){
            for (BorrowRepayInterestBiz b:pager.getData()) {
                waitnum+=b.getWaitRepayNum()+b.getEndRepayNum();
                if (b.getExtraInterest()!=null){
                    extraInterest=extraInterest.add(b.getExtraInterest());   //平台贴息
                }
                if (b.getPayableInterest()!=null){
                    payableInterest=payableInterest.add(b.getPayableInterest());   //利息
                }
                if (b.getPayablePrincipal()!=null){
                    payablePrincipal=payablePrincipal.add(b.getPayablePrincipal());   //本金
                }
                if (b.getPayExtraProjectInterest()!=null){
                	projectExtraInterest=projectExtraInterest.add(b.getPayExtraProjectInterest()); //项目加息
                }
                
            }
        }
        borrowRepayInterestCollect.setEndDate(enddate);
        borrowRepayInterestCollect.setWaitRepayNum(waitnum);
        borrowRepayInterestCollect.setExtraInterest(extraInterest.add(projectExtraInterest));
        borrowRepayInterestCollect.setPayablePrincipal(payablePrincipal);
        borrowRepayInterestCollect.setPayableInterest(payableInterest);
        borrowRepayInterestCollect.setPayabletotal(payablePrincipal.add(payableInterest));
        map.put("code",1);
        map.put("data",borrowRepayInterestCollect);
        return map;
    }

    @RequestMapping(value = "/ajaxborrowrepaymentdetail")
    @RequiresPermissions("borrowRepayment:ajaxBorrowRepaymentDetail")
    @ResponseBody
    public Object findInterestDetail(HttpServletRequest req, HttpServletResponse resp){
        Page<BorrowInterestDetailBiz> pager=new Page<BorrowInterestDetailBiz>();
        String borrowidstr=req.getParameter("borrowid");
        String projectids=req.getParameter("projectids");
        String enddatestr=req.getParameter("enddate");
        String pagesize= req.getParameter("iDisplayLength");
        String start= req.getParameter("iDisplayStart");

        BorrowTransactionInterestQuery query=new BorrowTransactionInterestQuery();
        if (StringUtils.isEmpty(borrowidstr)||StringUtils.isEmpty(enddatestr)){
            return null;
        }
        Long borrowid=Long.parseLong(borrowidstr);
        query.setBorrowId(borrowid);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date enddate = simpleDateFormat.parse(enddatestr);
            query.setEndDate(enddate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        query.setPageSize(Integer.parseInt(pagesize));
        query.setStartRow(Integer.parseInt(start));
        pager=transactionInterestService.findDayBorrowRepayInterestByBorrowId(query,projectids);
        return pager;
    }
}
