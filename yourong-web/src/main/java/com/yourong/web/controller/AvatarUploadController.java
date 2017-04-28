package com.yourong.web.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
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
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FileInfoUtil;
import com.yourong.common.util.OSSUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.upload.model.ImageConfig;
import com.yourong.core.upload.model.UploadFileInfo;
import com.yourong.core.upload.util.UploadUtil;

@Controller
@RequestMapping("avatarUpload")
public class AvatarUploadController {
	
	private static Logger log = LoggerFactory.getLogger(AvatarUploadController.class);
	
	private static final String DEFAULT_CATEGORY = "avatar";

	@Autowired
	private MultipartResolver multipartResolver;
	
	/**
	 * 上传文件
	 * @param req
	 * @param resp
	 */
	@RequestMapping(value = "upload")
	public void uploadFile(HttpServletRequest req, HttpServletResponse resp){
		ModelMap model = new ModelMap();
		MultipartHttpServletRequest multipartRequest = null;
		try {
			if(multipartResolver.isMultipart(req)){
				String appPath = req.getSession().getServletContext().getRealPath("/");
				multipartRequest = (MultipartHttpServletRequest) req;
				Map<String,MultipartFile> fileMap = multipartRequest.getFileMap();
				List<UploadFileInfo> fileInfoList = Lists.newArrayList();
				String relativePath = getFileSavePath(DEFAULT_CATEGORY);
				int uploadStatus = 1;
				for(String name : fileMap.keySet()){
					MultipartFile multipartFile = fileMap.get(name);
					if(FileInfoUtil.checkFileType(multipartFile.getOriginalFilename()) && uploadStatus > 0){
						//文件保存
						File file = FileInfoUtil.writeFile(multipartFile.getInputStream(), multipartFile.getOriginalFilename(), appPath+relativePath);
						//裁剪和添加水印
						UploadUtil.doScaleAndAddWatemarkImage(file, DEFAULT_CATEGORY, getAvatarConfig());
						BufferedImage img = ImageIO.read(file);
						//文件上传信息
						
						String path = getOssPicUrl(file);
						
						UploadFileInfo fileInfo = new UploadFileInfo();
						fileInfo.setName(getName(file.getName()));
						fileInfo.setOriginalFilename(getName(multipartFile.getOriginalFilename()));
						fileInfo.setFilePath((relativePath+File.separator+file.getName()).replace("\\", "/"));
						fileInfo.setOssPicUrl(path);
						fileInfo.setSuffix(getFileSuffix(multipartFile.getOriginalFilename()));
						//获得图片的宽度/高度
						fileInfo.setWidth(img.getWidth());
						fileInfo.setHeight(img.getHeight());
						fileInfoList.add(fileInfo);
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
		sb.append("static");
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
	 * 获得Oss图片地址
	 * @param file
	 * @return
	 */
	private String getOssPicUrl(File file){
		String key = "avatar/temp/"+DateUtils.formatDatetoString(DateUtils.getCurrentDate(),"yyyy/MM")+"/"+file.getName();
		String path = OSSUtil.uploadImageToOSS(key, file.getPath(),DateUtils.addHour(DateUtils.getCurrentDate(), 1));
		if(path != null){
			return path.substring(0, path.indexOf("?"));
		}
		return file.getPath();
	}
	
	private Map<String, List<ImageConfig>> getAvatarConfig(){
		Map<String, List<ImageConfig>> imagesConfig = new HashMap<String, List<ImageConfig>>();
		List<ImageConfig> configList = new ArrayList<ImageConfig>();
		ImageConfig c1 = new ImageConfig();
		c1.setWidth(300);
		c1.setHeight(300);
		c1.setIsAddWatermark(false);
		c1.setSizeType("default");
		configList.add(c1);
		
		imagesConfig.put(DEFAULT_CATEGORY, configList);
		
		return imagesConfig;
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
			log.error("上传文件出现异常。",e);
		}
	}
	
	public void renderJson(HttpServletResponse response,Map map) {
		String jsonString = JSONObject.toJSON(map).toString();
		render(response,"application/json", jsonString);
	}
}
