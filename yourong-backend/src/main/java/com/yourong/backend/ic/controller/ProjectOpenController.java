package com.yourong.backend.ic.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yourong.backend.BaseController;
import com.yourong.backend.ic.service.ProjectOpenService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.mongo.OpenLoadLog;
import com.yourong.common.mongo.logDto.OpenDownLoadDto;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.ConfigUtil;
import com.yourong.common.util.DateUtils;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.ic.model.DirectProjectBiz;
import com.yourong.core.ic.model.ProjectOpen;
import com.yourong.core.ic.model.query.ProjectOpenQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by XR on 2016/11/1.
 */
@Controller
@RequestMapping(value = "/projectopen")
public class ProjectOpenController extends BaseController {
    @Autowired
    private ProjectOpenService projectOpenService;

    @RequestMapping(value = "/index")
    @RequiresPermissions("projectopen:index")
    public String index(){
        return "/ic/projectopen/index";
    }

    @ResponseBody
    @RequestMapping(value = "/ajax")
    @RequiresPermissions("projectopen:ajax")
    public Object ajax(HttpServletRequest req){
        Page<ProjectOpen> page=new Page<>();
        ProjectOpenQuery query=new ProjectOpenQuery();
        String startTime= req.getParameter("startTime");
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (!StringUtils.isEmpty(startTime)){
            try {
                Date st=sdf.parse(startTime);
                query.setStartTime(st);
            } catch (ParseException e) {
                query.setStartTime(null);
            }
        }
        String endTime= req.getParameter("endTime");
        if (!StringUtils.isEmpty(endTime)){
            try {
                Date et=sdf.parse(endTime);
                query.setEndTime(et);
            } catch (ParseException e) {
                query.setEndTime(null);
            }
        }
        String outbizNo= req.getParameter("outbizNo");
        query.setOutBizNo(outbizNo);
        String projectId= req.getParameter("projectId");
        try {
            query.setProjectId(Long.parseLong(projectId));
        } catch (NumberFormatException e) {
            query.setProjectId(null);
        }
        String borrowerName= req.getParameter("borrowerName");
        query.setBorrowerName(borrowerName);
        String status= req.getParameter("status");
        if (StringUtils.isEmpty(status)){
            query.setStatus(null);
        }else {
            query.setStatus(Integer.parseInt(status));
        }
        String openPlatformKey= req.getParameter("openPlatformKey");
        if (StringUtils.isEmpty(openPlatformKey)){
            query.setOpenPlatformKey(null);
        }else {
            query.setOpenPlatformKey(openPlatformKey);
        }

        String pagesize= req.getParameter("iDisplayLength");
        String start= req.getParameter("iDisplayStart");
        query.setPageSize(Integer.parseInt(pagesize));
        query.setStartRow(Integer.parseInt(start));
        page=projectOpenService.queryPageProjectOpen(query);
        return page;
    }

    @RequestMapping(value = "/detail")
    @RequiresPermissions("projectopen:detail")
    public String detail(Model model,@RequestParam("openid") Long openid){
        model.addAttribute("openid",openid);
        return "/ic/projectopen/detail";
    }

    @ResponseBody
    @RequestMapping(value = "/finddetail")
    @RequiresPermissions("projectopen:findDetail")
    public Object findDetail(@RequestParam("openid") Long openid){
        Map<String,Object> map=new HashMap<>();
        DirectProjectBiz directProjectBiz=new DirectProjectBiz();
        ProjectOpen projectOpen=projectOpenService.queryById(openid);
        if (projectOpen==null){
            map.put("code",-1);
            return map;
        }
        try {
            directProjectBiz= JSON.parseObject(projectOpen.getProjectbizJson(),DirectProjectBiz.class);
            List<BscAttachment> bscAttachments= JSON.parseObject(projectOpen.getBscAttachmentsJson(),new TypeReference<ArrayList<BscAttachment>>(){});
            directProjectBiz.setBscAttachments(bscAttachments);
            projectOpen.setProjectbizJson(null);
            projectOpen.setBscAttachmentsJson(null);
            map.put("code",1);
            map.put("open",projectOpen);
            map.put("bscAttachments",bscAttachments);
            return map;
        } catch (Exception e) {
            map.put("code",-1);
            return map;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/download")
    @RequiresPermissions("projectopen:download")
    public void downLoad(@RequestParam("openid") Long openid,HttpServletRequest req,HttpServletResponse resp){
        ProjectOpen projectOpen= projectOpenService.queryById(openid);
        String appPath = req.getSession().getServletContext().getRealPath("/");
        List<File> files=new ArrayList<>();
        if (projectOpen.getStatus()==3||projectOpen.getStatus()==4||projectOpen.getStatus()==5||projectOpen.getStatus()==6){
            List<BscAttachment> bscAttachments= JSON.parseObject(projectOpen.getBscAttachmentsJson(),new TypeReference<ArrayList<BscAttachment>>(){});
            if (Collections3.isNotEmpty(bscAttachments)){
                for (BscAttachment bsc:bscAttachments) {
                    File file=new File(bsc.getFileUrl());
                    files.add(file);
                }
            }
        }
        File file= getFileSavePath(appPath,openid);
        zipFile(files,file);
        try {
            InputStream ins = new FileInputStream(file.getPath());
            // 获取输入流
            BufferedInputStream bins = new BufferedInputStream(ins);// 放到缓冲流里面
            // 获取文件输出IO流
            OutputStream outs = resp.getOutputStream();
            // 输出流
            BufferedOutputStream bouts = new BufferedOutputStream(outs);
            resp.setContentType("application/octet-stream");// 设置response内容的类型
            resp.setHeader(
                    "Content-disposition",
                    "attachment;filename="
                            +  new String((openid.toString()+".rar").getBytes("utf-8"), "ISO8859-1"));// 设置头部信息
            byte[] buff = new byte[2048];
            int bytesRead;
            // 开始向网络传输文件流
            while (-1 != (bytesRead = bins.read(buff, 0, buff.length))) {
                bouts.write(buff, 0, bytesRead);
            }
            bouts.flush();// 这里一定要调用flush()方法
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 审核
     * @param openid
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/auditing")
    @RequiresPermissions("projectopen:auditing")
    public Object auditing(@RequestParam("openid") Long openid,@RequestParam("status")Integer status,@RequestParam("refuse")String refuse,
                           @RequestParam("shortDesc") String shortDesc, @RequestParam("borrowDetail") String borrowDetail){
        ResultDO result = new ResultDO();
        if (projectOpenService.auditingById(status,refuse,shortDesc,borrowDetail,openid)){
            result.setSuccess(true);
            return result;
        }
        result.setSuccess(false);
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/auditinginfo")
    @RequiresPermissions("projectopen:auditingInfo")
    public Object auditingInfo(@RequestParam("openid") Long openid){
        ResultDO result = new ResultDO();
        ProjectOpen projectOpen= new ProjectOpen();
        try {
            projectOpen = projectOpenService.queryAuditingInfo(openid);
            result.setSuccess(true);
            result.setResult(projectOpen);
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            return result;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/remark")
    @RequiresPermissions("projectopen:remark")
    public Object remark(@RequestParam("openid") Long openid){
        ResultDO result = new ResultDO();
        ProjectOpen projectOpen= new ProjectOpen();
        try {
            projectOpen = projectOpenService.queryRemarkById(openid);
            result.setSuccess(true);
            result.setResult(projectOpen);
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            return result;
        }
    }

    /**
     * 备注
     * @param openid
     * @param remark
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/addremark")
    @RequiresPermissions("projectopen:addRemark")
    public Object addRemark(@RequestParam("openid") Long openid,@RequestParam("remark") String remark){
        ResultDO result = new ResultDO();
        if (projectOpenService.updateRemarkById(remark,openid)){
            result.setSuccess(true);
            return result;
        }
        result.setSuccess(false);
        return result;
    }

    /**
     * 打包
     * @param files
     * @param outfile
     */
    private static void zipFile(List<File> files, File outfile){
        try {
            byte[] buffer = new byte[1024 * 10];
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outfile));
            if (Collections3.isNotEmpty(files)){
                for(int i=0;i<files.size();i++) {
                    if (files.get(i).exists()){
                        FileInputStream fis = new FileInputStream(files.get(i));
                        out.putNextEntry(new ZipEntry(files.get(i).getName()));
                        int len;
                        //读入需要下载的文件的内容，打包到zip文件
                        while((len = fis.read(buffer))>0) {
                            out.write(buffer,0,len);
                        }
                        out.closeEntry();
                        fis.close();
                    }
                }
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取保存路径
     * @param localPath
     * @param openid
     * @return
     */
    private static File getFileSavePath(String localPath,Long openid){
        StringBuffer sb = new StringBuffer();
        sb.append(ConfigUtil.getInstance().getUploadDirectory());
        sb.append("/zip");
        sb.append(File.separator);
        sb.append(DateUtils.getYear(DateUtils.getCurrentDate()));
        sb.append(File.separator);
        sb.append(DateUtils.getMonth(DateUtils.getCurrentDate()));
        sb.toString();
        File file = new File(localPath+sb.toString());
        //检查文件路径是否存在
        if(!file.exists()){file.mkdirs();}
        File outFile = null;
        String fileName = openid.toString()+".zip";
        String filePath = file.getPath()+File.separator+fileName;
        outFile = new File(filePath);
        if (outFile.exists()){
            outFile.delete();
        }
        return outFile;
    }
}
