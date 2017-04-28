package com.yourong.backend.sys.controller;

import com.yourong.backend.BaseController;
import com.yourong.backend.sys.service.DataService;
import com.yourong.common.util.ConfigUtil;
import com.yourong.common.util.DateUtils;
import com.yourong.core.data.model.AlreadyInvest;
import com.yourong.core.data.model.DouWan;
import com.yourong.core.data.model.LiCai;
import com.yourong.core.data.model.NoInvest;

import com.yourong.core.sys.model.SysUser;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by XR on 2016/11/28.
 */
@Controller
@RequestMapping(value = "/data")
public class DataController extends BaseController {
    @Autowired
    private DataService dataService;

    @RequestMapping(value = "/index")
    public String index(){
        return "/sys/data/index";
    }

    /**
     * 理财
     * @param date
     * @param req
     * @param resp
     */
    @ResponseBody
    @RequestMapping(value = "/licai")
    public void liCai(String date,HttpServletRequest req, HttpServletResponse resp){
        Date start=new Date();
        Date stop=new Date();
        SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
        if (!StringUtils.isEmpty(date)){
            try {
                Calendar calendar=Calendar.getInstance();
                calendar.setTime(sdf1.parse(date));
                calendar.add(Calendar.DATE,1);
                calendar.add(Calendar.SECOND,-1);
                start=sdf1.parse(date);
                stop=calendar.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        HSSFWorkbook hssfWorkbook=new HSSFWorkbook();
        HSSFSheet sheet = hssfWorkbook.createSheet("sheet0");
        HSSFRow row = sheet.createRow(0);
        List<String> cells= Arrays.asList("公司名称","描述","联系人电话","联系人邮箱","备注","公司地址","所属人");
        setCell(row,cells);
        List<LiCai> liCais=dataService.queryLiCai(start,stop);
        for (int i=0;i<liCais.size();i++){
            //创建HSSFRow对象
            HSSFRow valuerow = sheet.createRow(i+1);
            setCell(valuerow,liCais.get(i));
        }
        //输出Excel文件
        String appPath = req.getSession().getServletContext().getRealPath("/");
        File file= getFileSavePath(appPath,date+"理财.xls");
        doflush(hssfWorkbook,file,date+"理财",req,resp);
    }

    /**
     * 昨日注册未投资已实名客户数据
     * @param req
     * @param resp
     */
    @ResponseBody
    @RequestMapping(value = "/noinvest")
    public void noInvest(String date,HttpServletRequest req, HttpServletResponse resp){
        Date start=new Date();
        Date stop=new Date();
        SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
        if (!StringUtils.isEmpty(date)){
            try {
                Calendar calendar=Calendar.getInstance();
                calendar.setTime(sdf1.parse(date));
                calendar.add(Calendar.DATE,1);
                start=sdf1.parse(date);
                stop=calendar.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        HSSFWorkbook hssfWorkbook=new HSSFWorkbook();
        HSSFSheet sheet = hssfWorkbook.createSheet("sheet0");
        HSSFRow row = sheet.createRow(0);
        List<String> cells= Arrays.asList("公司地址","公司名称","联系人电话","备注","公司网站","所属人");
        setCell(row,cells);
        List<NoInvest> noInvests=dataService.queryNoInvest(start,stop);
        for (int i=0;i<noInvests.size();i++){
            //创建HSSFRow对象
            HSSFRow valuerow = sheet.createRow(i+1);
            setCell(valuerow,noInvests.get(i));
        }
        //输出Excel文件
        String appPath = req.getSession().getServletContext().getRealPath("/");
        File file= getFileSavePath(appPath,date+"昨日注册未投资已实名客户数据.xls");
        doflush(hssfWorkbook,file,date+"昨日注册未投资已实名客户数据",req,resp);
    }

    /**
     * 生日已投资客户数据
     * @param date
     * @param req
     * @param resp
     */
    @ResponseBody
    @RequestMapping(value = "/alreadyinvest")
    public void alreadyInvest(String date,HttpServletRequest req, HttpServletResponse resp){
        HSSFWorkbook hssfWorkbook=new HSSFWorkbook();
        HSSFSheet sheet = hssfWorkbook.createSheet("sheet0");
        HSSFRow row = sheet.createRow(0);
        List<String> cells= Arrays.asList("公司名称","联系人电话","公司地址","备注","公司网站","所属人");
        setCell(row,cells);
        List<AlreadyInvest> alreadyInvests=dataService.queryAlreadyInvest(date);
        for (int i=0;i<alreadyInvests.size();i++){
            //创建HSSFRow对象
            HSSFRow valuerow = sheet.createRow(i+1);
            setCell(valuerow,alreadyInvests.get(i));
        }
        //输出Excel文件
        String appPath = req.getSession().getServletContext().getRealPath("/");
        File file= getFileSavePath(appPath,date+"生日已投资客户数据.xls");
        doflush(hssfWorkbook,file,date+"生日已投资客户数据",req,resp);
    }
    
    /**
     * 
     * @Description:渠道商注册数据
     * @param start
     * @param req
     * @param resp
     * @author: chaisen
     * @time:2016年11月29日 下午2:01:35
     */
    @ResponseBody
    @RequestMapping(value = "/douWanInvest")
    public void douWanInvest(String start,String end,HttpServletRequest req, HttpServletResponse resp){
    	Date startDate=DateUtils.getDateFromString(start);
    	Date endDate=DateUtils.getDateFromString(end);
        HSSFWorkbook hssfWorkbook=new HSSFWorkbook();
        HSSFSheet sheet = hssfWorkbook.createSheet("sheet0");
        HSSFRow row = sheet.createRow(0);
        List<String> cells= Arrays.asList("会员id","追踪码","注册日期");
        setCell(row,cells);
        List<DouWan> douWans=dataService.queryDouWans(startDate,endDate);
        for (int i=0;i<douWans.size();i++){
            //创建HSSFRow对象
            HSSFRow valuerow = sheet.createRow(i+1);
            setCell(valuerow,douWans.get(i));
        }
        //输出Excel文件
        String appPath = req.getSession().getServletContext().getRealPath("/");
        File file= getFileSavePath(appPath,start+"都玩注册用户数据.xls");
        doflush(hssfWorkbook,file,start+"都玩注册用户数据",req,resp);
    }

    private static File getFileSavePath(String localPath,String filename){
        StringBuffer sb = new StringBuffer();
        sb.append(ConfigUtil.getInstance().getUploadDirectory());
        sb.append("/excel");
        sb.append(File.separator);
        sb.append(DateUtils.getYear(DateUtils.getCurrentDate()));
        sb.append(File.separator);
        sb.append(DateUtils.getMonth(DateUtils.getCurrentDate()));
        sb.toString();
        File file = new File(localPath+sb.toString());
        //检查文件路径是否存在
        if(!file.exists()){file.mkdirs();}
        File outFile = null;
        String filePath = file.getPath()+File.separator+filename;
        outFile = new File(filePath);
        if (outFile.exists()){
            outFile.delete();
        }
        return outFile;
    }


    /**
     * 设置table头
     * @param hssfRow
     */
    private void setCell(HSSFRow hssfRow,List<String> cells){
        for (int i=0;i<cells.size();i++){
            HSSFCell cell=hssfRow.createCell(i);
            cell.setCellValue(cells.get(i));
        }
    }

    /**
     * 设置table值
     * @param hssfRow
     * @param liCai
     */
    private void setCell(HSSFRow hssfRow,LiCai liCai){
        HSSFCell cell=hssfRow.createCell(0);
        cell.setCellValue(liCai.getId());
        HSSFCell cell1=hssfRow.createCell(1);
        cell1.setCellValue(liCai.getcName());
        HSSFCell cell2=hssfRow.createCell(2);
        cell2.setCellValue(liCai.getMobile());
        HSSFCell cell3=hssfRow.createCell(3);
        cell3.setCellValue(liCai.getEmail());
        HSSFCell cell4=hssfRow.createCell(4);
        cell4.setCellValue(liCai.getField5());
        HSSFCell cell5=hssfRow.createCell(5);
        cell5.setCellValue(liCai.getRegTime());
        HSSFCell cell6=hssfRow.createCell(6);
        SysUser sysUser= getCurrentLoginUserInfo();
        cell6.setCellValue(sysUser.getName());
    }

    /**
     * 设置table值
     * @param hssfRow
     * @param alreadyInvest
     */
    private void setCell(HSSFRow hssfRow,AlreadyInvest alreadyInvest){
        HSSFCell cell=hssfRow.createCell(0);
        cell.setCellValue(alreadyInvest.getcName());
        HSSFCell cell1=hssfRow.createCell(1);
        cell1.setCellValue(alreadyInvest.getMobile());
        HSSFCell cell2=hssfRow.createCell(2);
        cell2.setCellValue(alreadyInvest.getRegisterTime());
        HSSFCell cell3=hssfRow.createCell(3);
        cell3.setCellValue(alreadyInvest.getMsg());
        HSSFCell cell4=hssfRow.createCell(4);
        cell4.setCellValue(alreadyInvest.getInvest());
        HSSFCell cell5=hssfRow.createCell(5);
        SysUser sysUser= getCurrentLoginUserInfo();
        cell5.setCellValue(sysUser.getName());
    }

    /**
     * 设置table值
     * @param hssfRow
     * @param noInvest
     */
    private void setCell(HSSFRow hssfRow,NoInvest noInvest){
        HSSFCell cell=hssfRow.createCell(0);
        cell.setCellValue(noInvest.getRegisterTime());
        HSSFCell cell1=hssfRow.createCell(1);
        cell1.setCellValue(noInvest.getcName());
        HSSFCell cell2=hssfRow.createCell(2);
        cell2.setCellValue(noInvest.getMobile());
        HSSFCell cell3=hssfRow.createCell(3);
        cell3.setCellValue(noInvest.getRegisterTraceSource());
        HSSFCell cell4=hssfRow.createCell(4);
        cell4.setCellValue(noInvest.getNoInvest());
        HSSFCell cell5=hssfRow.createCell(5);
        SysUser sysUser= getCurrentLoginUserInfo();
        cell5.setCellValue(sysUser.getName());
    }
    
    
    private void setCell(HSSFRow hssfRow,DouWan douWan){
        HSSFCell cell=hssfRow.createCell(0);
        cell.setCellValue(douWan.getMemberId().toString());
        HSSFCell cell1=hssfRow.createCell(1);
        cell1.setCellValue(douWan.getRegisterTraceNo());
        HSSFCell cell2=hssfRow.createCell(2);
        cell2.setCellValue(douWan.getRegisterTime());
    }

    private void doflush(HSSFWorkbook hssfWorkbook,File file, String filename,HttpServletRequest req,HttpServletResponse resp){
        try {
            FileOutputStream output=new FileOutputStream(file);
            hssfWorkbook.write(output);
            output.flush();
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
                            +  new String((filename+".xls").getBytes("utf-8"), "ISO8859-1"));// 设置头部信息
            byte[] buff = new byte[2048];
            int bytesRead;
            // 开始向网络传输文件流
            while (-1 != (bytesRead = bins.read(buff, 0, buff.length))) {
                bouts.write(buff, 0, bytesRead);
            }
            bouts.flush();// 这里一定要调用flush()方法
            ins.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
