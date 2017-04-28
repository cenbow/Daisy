package com.yourong.backend.upload.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yourong.common.util.ConfigUtil;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FileInfoUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.upload.model.ImageConfig;
import com.yourong.core.upload.model.UploadFileInfo;
import com.yourong.core.upload.util.UploadUtil;

@Controller
@RequestMapping("commonUpload")
public class UploadController {
	private static Logger log = LoggerFactory.getLogger(UploadController.class);
	@Autowired
	private MultipartResolver multipartResolver;
	
	@Autowired
	private Map<String, List<ImageConfig>> imagesConfig;

	/**
	 * 上传文件
	 * @param req
	 * @param resp
	 */
	@RequestMapping(value = "uploadFile")
	public void uploadFile(HttpServletRequest req, HttpServletResponse resp){
		ModelMap model = new ModelMap();
		MultipartHttpServletRequest multipartRequest = null;
		try {
			if(multipartResolver.isMultipart(req)){
				String appPath = req.getSession().getServletContext().getRealPath("/");
				multipartRequest = (MultipartHttpServletRequest) req;
				Map<String,MultipartFile> fileMap = multipartRequest.getFileMap();
				List<UploadFileInfo> fileInfoList = Lists.newArrayList();
				String category = req.getParameter("category");
				if(StringUtil.isBlank(category)){
					category = UploadFileInfo.DEFAULT_CATEOGRY;
				}
				String relativePath = getFileSavePath(category);
				int uploadStatus = 1;
				for(Map.Entry<String,MultipartFile> en:fileMap.entrySet()){
					MultipartFile multipartFile = en.getValue();
					if(FileInfoUtil.checkFileType(multipartFile.getOriginalFilename()) && uploadStatus > 0){
						//文件保存
						File file = FileInfoUtil.writeFile(multipartFile.getInputStream(), multipartFile.getOriginalFilename(), appPath+relativePath);
						//文件上传信息
						UploadFileInfo fileInfo = new UploadFileInfo();
						fileInfo.setName(getName(file.getName()));
						fileInfo.setOriginalFilename(getName(multipartFile.getOriginalFilename()));
						fileInfo.setFilePath((relativePath+File.separator+file.getName()).replace("\\", "/"));
						fileInfo.setFileSize(file.length());
						fileInfo.setCategory(category);
						fileInfo.setSuffix(getFileSuffix(multipartFile.getOriginalFilename()));
						fileInfoList.add(fileInfo);
						
						//裁剪和添加水印
						UploadUtil.doScaleAndAddWatemarkImage(file, category, imagesConfig);
					}else{
						uploadStatus = -5;
						log.error("不支持的上传文件类型："+multipartFile.getOriginalFilename());
						break;
					}
				}
				model.addAttribute("uploadStatus", uploadStatus);
				model.addAttribute("fileInfo", fileInfoList);
			}else{
				model.addAttribute("uploadStatus", -1);
				log.error("无效的上传请求。");
			}
		} catch (IOException e) {
			model.addAttribute("uploadStatus", -2);
			log.error("上传文件出现异常。",e);
		}finally{
			if(multipartRequest != null){
				multipartResolver.cleanupMultipart(multipartRequest);
			}
		}
		renderJson(resp,model);
	}

	/**
	 * 文件保存地址
	 * @param category
	 * @return
	 */
	private String getFileSavePath(String category){
		StringBuffer sb = new StringBuffer();
		sb.append(ConfigUtil.getInstance().getUploadDirectory());
		sb.append(File.separator);
		if(StringUtil.isNotBlank(category)){
			sb.append(category);
			sb.append(File.separator);
		}
		sb.append(DateUtils.getYear(DateUtils.getCurrentDate()));
		sb.append(File.separator);
		sb.append(DateUtils.getMonth(DateUtils.getCurrentDate()));
		return sb.toString();
	}
	
	/**
	 * 文件后缀
	 * @param fileName
	 * @return
	 */
	private String getFileSuffix(String fileName) {
		return fileName.substring(fileName.lastIndexOf("."));
	}
	
	/**
	 * 文件名称（不含后缀）
	 * @param fileName
	 * @return
	 */
	private String getName(String fileName){
		return fileName.substring(0,fileName.lastIndexOf("."));
	}
	
	public void render(HttpServletResponse response,String contentType, String content) {
		try {
			String encoding = "UTF-8";
			String fullContentType = contentType + ";charset=" + encoding;
			response.setContentType(fullContentType);
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			response.getWriter().write(content);
			response.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void renderJson(HttpServletResponse response,Map map) {
		String jsonString = JSONObject.toJSON(map).toString();
		render(response,"application/json", jsonString);
	}
}
