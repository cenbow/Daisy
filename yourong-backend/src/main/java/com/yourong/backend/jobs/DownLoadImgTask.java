package com.yourong.backend.jobs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yourong.common.mongo.MongodbManager;
import com.yourong.common.mongo.OpenLoadLog;
import com.yourong.common.mongo.OpenServiceLog;
import com.yourong.common.mongo.logDto.OpenDownLoadDto;
import com.yourong.common.util.*;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.ic.manager.ProjectOpenManager;
import com.yourong.core.ic.model.DirectProjectBiz;
import com.yourong.core.ic.model.ProjectOpen;
import org.apache.commons.fileupload.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by XR on 2016/11/3.
 */
public class DownLoadImgTask {
    private static Logger logger = LoggerFactory.getLogger(DownLoadImgTask.class);

    @Autowired
    private ProjectOpenManager projectOpenManager;

    public void run(){
        try {
            List<ProjectOpen> list= projectOpenManager.queryHandleList();
            if (Collections3.isNotEmpty(list)){
                for (ProjectOpen open:list) {
                    boolean iscontinue=false;
                    List<BscAttachment> bscAttachments= JSON.parseObject(open.getBscAttachmentsJson(),new TypeReference<ArrayList<BscAttachment>>(){});
                    for (BscAttachment bsc:bscAttachments) {
                        boolean isload=false;
                        boolean urltrue=true;
                        int loadtime=1;
                        while (!isload&&loadtime<4&&urltrue){
                            //处理网络图片
                            try {
                                if (bsc.getFileUrl().indexOf("http://")>=0||bsc.getFileUrl().indexOf("https://")>=0){
                                    //new一个URL对象
                                    URL url = new URL(bsc.getFileUrl());
                                    //打开链接
                                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                                    //设置请求方式为"GET"
                                    conn.setRequestMethod("GET");
                                    //超时响应时间为5秒
                                    conn.setConnectTimeout(5 * 1000);
                                    //通过输入流获取图片数据
                                    InputStream inStream = conn.getInputStream();

                                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                    //创建一个Buffer字符串
                                    byte[] buffer = new byte[1024];
                                    //每次读取的字符串长度，如果为-1，代表全部读取完毕
                                    int len = 0;
                                    //使用一个输入流从buffer里把数据读取出来
                                    while( (len=inStream.read(buffer)) != -1 ){
                                        //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                                        outStream.write(buffer, 0, len);
                                    }
                                    //关闭输入流
                                    inStream.close();
                                    //得到图片的二进制数据，以二进制封装得到数据，具有通用性
                                    byte[] data = outStream.toByteArray();
                                    String savepath= getFileSavePath();


                                    //后缀
                                    String suffix = bsc.getFileExt();
                                    //new一个文件对象用来保存图片，默认保存当前工程根目录
                                    File imageFile = download(savepath,suffix);

                                    String fileName=imageFile.getName();
                                    //创建输出流
                                    FileOutputStream fileoutStream = new FileOutputStream(imageFile);
                                    //写入数据
                                    fileoutStream.write(data);
                                    //关闭输出流
                                    fileoutStream.close();

                                    OpenDownLoadDto openDownLoadDto=new OpenDownLoadDto();
                                    openDownLoadDto.setOpenId(open.getId());
                                    openDownLoadDto.setLoadUrl(bsc.getFileUrl());
                                    openDownLoadDto.setMsg("图片下载地址"+imageFile.getPath());
                                    openDownLoadDto.setCreateTime(new Date());
                                    OpenLoadLog.log(openDownLoadDto);

                                    bsc.setFileName(fileName.substring(0,fileName.lastIndexOf(".")));
                                    bsc.setFileUrl((savepath+imageFile.separator+imageFile.getName()).replace("\\", "/"));
                                    bsc.setFileSize(imageFile.length());
                                    bsc.setFileExt(suffix);
                                    isload=true;
                                }else {
                                    urltrue=false;
                                }
                            } catch (IOException e) {
                                logger.error("保存机密图片文件出现异常。",e);
                                loadtime++;
                                OpenDownLoadDto openDownLoadDto=new OpenDownLoadDto();
                                openDownLoadDto.setOpenId(open.getId());
                                openDownLoadDto.setLoadUrl(bsc.getFileUrl());
                                openDownLoadDto.setMsg("第"+(loadtime-1)+"次上传图片"+e.toString());
                                openDownLoadDto.setCreateTime(new Date());
                                OpenLoadLog.log(openDownLoadDto);
                            }
                        }
                        //如果下载3次还是不能下载或者url是错误的则break
                        if ((!isload&&loadtime>3)||!urltrue){
                            iscontinue=true;
                            break;
                        }
                    }
                    if (iscontinue){
                        projectOpenManager.updateStatusAndRemarkById(2,"图片下载出错",open.getId(),1);
                        continue;
                    }else {
                        ProjectOpen projectOpen=new ProjectOpen();
                        String attachmentsJson= JSON.toJSONString(bscAttachments);
                        projectOpen.setBscAttachmentsJson(attachmentsJson);
                        projectOpen.setUpdateTime(new Date());
                        projectOpen.setStatus(3);
                        projectOpen.setId(open.getId());
                        projectOpenManager.updateAttachmentById(projectOpen);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("对外项目订单处理图片异常：", e);
            OpenDownLoadDto openDownLoadDto=new OpenDownLoadDto();
            openDownLoadDto.setOpenId(0L);
            openDownLoadDto.setMsg("图片下载异常"+e.toString());
            openDownLoadDto.setCreateTime(new Date());
            OpenLoadLog.log(openDownLoadDto);
        }
    }

    private File download(String path,String suffix){
        File file = new File(path);
        //检查文件路径是否存在
        if(!file.exists()){file.mkdirs();}
        boolean outFileExists = true;
        File outFile = null;
        //确保文件名称唯一性
        do{
            String fileName = Identities.randomBase62(10)+suffix;
            String filePath = file.getPath()+File.separator+fileName;
            outFile = new File(filePath);
            outFileExists = outFile.exists();
        }while(outFileExists);
        return outFile;
    }

    private String getFileSavePath(){
        StringBuffer sb = new StringBuffer();
        sb.append(ConfigUtil.getInstance().getOpenUploadDirectory());
        sb.append(File.separator);
        sb.append(DateUtils.getYear(DateUtils.getCurrentDate()));
        sb.append(File.separator);
        sb.append(DateUtils.getMonth(DateUtils.getCurrentDate()));
        return sb.toString();
    }
}
