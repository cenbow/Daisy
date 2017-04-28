package com.yourong.backend.sys.controller;

import com.yourong.backend.BaseController;
import com.yourong.common.mongo.OpenLoadLog;
import com.yourong.common.mongo.OpenServiceLog;
import com.yourong.common.mongo.logDto.OpenDownLoadDto;
import com.yourong.common.mongo.logDto.OpenServiceInputDto;
import com.yourong.common.mongo.query.OpenDownLoadQuery;
import com.yourong.common.mongo.query.OpenServiceInputQuery;
import com.yourong.common.pageable.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by XR on 2016/11/21.
 */
@Controller
@RequestMapping(value = "/mongo")
public class MongoLogController extends BaseController {
    @RequestMapping(value = "/loadindex")
    public String loadIndex(){
        return "/sys/mongo/loadindex";
    }

    @RequestMapping(value = "/inputindex")
    public String inputIndex(){
        return "/sys/mongo/serviceindex";
    }

    @ResponseBody
    @RequestMapping(value = "/loadajax")
    public Object loadAjax(HttpServletRequest req){
        OpenDownLoadQuery openDownLoadQuery=new OpenDownLoadQuery();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String createtime=req.getParameter("createtime");
        if (StringUtils.isEmpty(createtime)){
            openDownLoadQuery.setCreateTime(new Date());
        }else {
            try {
                Date st=simpleDateFormat.parse(createtime);
                openDownLoadQuery.setCreateTime(st);
            } catch (ParseException e) {
                openDownLoadQuery.setCreateTime(null);
            }
        }
        String openid=req.getParameter("openid");
        if (!StringUtils.isEmpty(openid)){
            openDownLoadQuery.setOpenId(Long.parseLong(openid));
        }
        String starttime=req.getParameter("starttime");
        if (!StringUtils.isEmpty(starttime)){
            try {
                Date st=sdf.parse(starttime);
                openDownLoadQuery.setStartTime(st);
            } catch (ParseException e) {
                openDownLoadQuery.setStartTime(null);
            }
        }
        String endtime=req.getParameter("endtime");
        if (!StringUtils.isEmpty(endtime)){
            try {
                Date st=sdf.parse(endtime);
                openDownLoadQuery.setEndTime(st);
            } catch (ParseException e) {
                openDownLoadQuery.setEndTime(null);
            }
        }
        String pagesize= req.getParameter("iDisplayLength");
        String start= req.getParameter("iDisplayStart");
        Page<OpenDownLoadDto> page=new Page<>();
        openDownLoadQuery.setPageSize(Integer.parseInt(pagesize));
        openDownLoadQuery.setStartRow(Integer.parseInt(start));
        List<OpenDownLoadDto> list= OpenLoadLog.queryLog(openDownLoadQuery);
        int i= OpenLoadLog.queryLogCount(openDownLoadQuery);
        page.setData(list);
        page.setiTotalDisplayRecords(i);
        page.setiTotalRecords(i);
        page.setiDisplayLength(openDownLoadQuery.getPageSize());
        return page;
    }

    @ResponseBody
    @RequestMapping(value = "/inputajax")
    public Object inputAjax(HttpServletRequest req){
        OpenServiceInputQuery openServiceInputQuery=new OpenServiceInputQuery();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String createtime=req.getParameter("createtime");
        if (StringUtils.isEmpty(createtime)){
            openServiceInputQuery.setCreateTime(new Date());
        }else {
            try {
                Date st=simpleDateFormat.parse(createtime);
                openServiceInputQuery.setCreateTime(st);
            } catch (ParseException e) {
                openServiceInputQuery.setCreateTime(null);
            }
        }
        String outBizNo=req.getParameter("outBizNo");
        if (!StringUtils.isEmpty(outBizNo)){
            openServiceInputQuery.setOutBizNo(outBizNo);
        }
        String channelKey=req.getParameter("channelKey");
        if (!StringUtils.isEmpty(channelKey)){
            openServiceInputQuery.setChannelKey(channelKey);
        }
        String starttime=req.getParameter("starttime");
        if (!StringUtils.isEmpty(starttime)){
            try {
                Date st=sdf.parse(starttime);
                openServiceInputQuery.setStartTime(st);
            } catch (ParseException e) {
                openServiceInputQuery.setStartTime(null);
            }
        }
        String endtime=req.getParameter("endtime");
        if (!StringUtils.isEmpty(endtime)){
            try {
                Date st=sdf.parse(endtime);
                openServiceInputQuery.setEndTime(st);
            } catch (ParseException e) {
                openServiceInputQuery.setEndTime(null);
            }
        }
        String pagesize= req.getParameter("iDisplayLength");
        String start= req.getParameter("iDisplayStart");
        Page<OpenServiceInputDto> page=new Page<>();
        openServiceInputQuery.setPageSize(Integer.parseInt(pagesize));
        openServiceInputQuery.setStartRow(Integer.parseInt(start));
        List<OpenServiceInputDto> list= OpenServiceLog.queryLog(openServiceInputQuery);
        int i= OpenServiceLog.queryLogCount(openServiceInputQuery);
        page.setData(list);
        page.setiTotalDisplayRecords(i);
        page.setiTotalRecords(i);
        page.setiDisplayLength(openServiceInputQuery.getPageSize());
        return page;
    }
}
