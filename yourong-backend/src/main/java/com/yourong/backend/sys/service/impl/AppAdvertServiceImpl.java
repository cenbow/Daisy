/**
 * 
 */
package com.yourong.backend.sys.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.yourong.backend.sys.service.AppAdvertService;
import com.yourong.backend.sys.service.SysDictService;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.OSSUtil;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.core.sys.model.SysDict;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年1月5日下午2:27:59
 */
@Service
public class AppAdvertServiceImpl implements AppAdvertService{
	private static final Logger logger = LoggerFactory.getLogger(AppAdvertServiceImpl.class);
	
	 @Autowired
	 private SysDictService sysDictService;
	
	 public int clear(long id) {
			int result = 0;
			SysDict sysDict = new SysDict();
			sysDict.setValue("");
			sysDict.setId(id);
			result =  sysDictService.updateByPrimaryKeySelective(sysDict);
			return result;
		}
	 
	 public Page<SysDict> findByPage(Page<SysDict> pageRequest, Map<String, Object> map) {
	    	
		 return sysDictService.findByPage(pageRequest,map);
	       
	    }
	 
	 
	public int insert(long id ,String appPath,MultipartFile file) {
		int result = 0;
		String imgUrl = uploadFile(appPath,file);
		
		SysDict sysDict = new SysDict();
		sysDict.setValue(imgUrl);
		sysDict.setId(id);
		result =  sysDictService.updateByPrimaryKeySelective(sysDict);
		return result;
	}
		
	// 文件上传
		public String uploadFile(String appPath, MultipartFile file) {
			// 文件上传
			if (!file.isEmpty()) { // 获取文件名称
				
				String date = DateUtils.getStrFromDate(DateUtils.getCurrentDate(),DateUtils.TIME_PATTERN_MILLISECOND);
				String fileName = file.getOriginalFilename(); // 获取文件需要保存的路径
				fileName = date+fileName;//文件名加时间前缀，以防同名文件，阿里云服务器上被替换
				
				// 判断目录是否存在
				File fileDir = new File(appPath + PropertiesUtil.getUploadDirectory());
				if (!fileDir.exists() && !fileDir.isDirectory()) {
					fileDir.mkdir();
				}
				String path = appPath + PropertiesUtil.getUploadDirectory() + "/" + fileName;
				logger.debug("文件路径：" + path);
				File localFile = new File(path);
				try { // 保存文件
					file.transferTo(localFile);
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				String key = OSSUtil.getBannerKey(fileName, DateUtils.getCurrentDateTime());
				String url = OSSUtil.uploadImageToOSS(key, path);
				String simpleUrl = OSSUtil.getSimpleImageUrl(url);
				return simpleUrl;
			}
			return null;
		}
	
		public int insertTitle(long id ,String value) {
			int result = 0;
			SysDict record = new SysDict();
			record.setId(id);
			record.setValue(value);
			result = sysDictService.updateByPrimaryKeySelective(record);
			return result;
		}
}
