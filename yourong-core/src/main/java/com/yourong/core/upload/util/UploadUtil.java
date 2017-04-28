package com.yourong.core.upload.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.ServletRequestUtils;

import com.yourong.common.util.Collections3;
import com.yourong.common.util.FileInfoUtil;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.SpringContextHolder;
import com.yourong.core.upload.model.ImageConfig;

public class UploadUtil {
	protected static Logger logger = LoggerFactory.getLogger(UploadUtil.class);
	/**
	 * 对图片进行裁剪和添加水印
	 * @param file 目标文件
	 * @param category 类目
	 * @param configMap 处理方案
	 */
	public static void doScaleAndAddWatemarkImage(File file, String category, Map<String, List<ImageConfig>> configMap){
		if((configMap != null && configMap.size() >0)){
			try {
				List<ImageConfig> configList = configMap.get(category);
				String watermarkImagePath = getDefaultWatermarkImagePath();
				String path = file.getPath();
				if(Collections3.isNotEmpty(configList)){
					for(ImageConfig ic : configList){
						String newFilePath = appendSuffix(path, ic.getSizeType());
						if(ic.isAddWatermark()){
							if(ic.getWatermarkImagePath() != null){
								//自定义水印图片
								watermarkImagePath = getCustomWatermarkImagePath(ic.getWatermarkImagePath());
							}
							if(ic.getHeight() <=0 ){
								//裁剪、添加水印（位置：右下角）、
								Thumbnails.of(path).width(ic.getWidth())
									.watermark(Positions.CENTER,ImageIO.read(new File(watermarkImagePath)),1f)
									.toFile(newFilePath); 
							}else{
							//裁剪、添加水印（位置：右下角）、
							Thumbnails.of(path).size(ic.getWidth(), ic.getHeight())
								.watermark(Positions.CENTER,ImageIO.read(new File(watermarkImagePath)),1f)
								.keepAspectRatio(ic.isKeepAspectRatio())
								.toFile(newFilePath);
							}
						}else{
							if(ic.getHeight() <=0 ){
								Thumbnails.of(path).width(ic.getWidth()).toFile(newFilePath);
							}else{
								Thumbnails.of(path).size(ic.getWidth(), ic.getHeight()).keepAspectRatio(ic.isKeepAspectRatio()).toFile(newFilePath);
							}
						}
					}
					deleteHistoryImg(file, path);
				}else{
					String newFilePath = appendSuffix(path, "default");
					//裁剪、添加水印（位置：右下角）、
					Thumbnails.of(path).scale(1f)
						.watermark(Positions.BOTTOM_RIGHT,ImageIO.read(new File(watermarkImagePath)),0.5f)
						.toFile(newFilePath);
					deleteHistoryImg(file, path);
				}
			} catch (IOException e) {
				logger.error("对图片进行裁剪和添加水印 异常",e);
			}
		}
	}
	
	/**
	 * 不同型号文件名称
	 * @param srcUrl
	 * @param suffix2
	 * @return
	 */
	public static String appendSuffix(String srcUrl, String suffix2) {
		StringBuilder sb = new StringBuilder(srcUrl);
		sb.insert(sb.lastIndexOf("."), suffix2.toLowerCase());
		return sb.toString();
	}
	
	/**
	 * 获得自定义的水印图片地址
	 * @param relativePath
	 * @return
	 */
	private static String getCustomWatermarkImagePath(String relativePath){
		return SpringContextHolder.getRootRealPath()+File.separator+relativePath;
	}
	
	
	/**
	 * 获得默认水印图片地址
	 * @return
	 */
	public static String getDefaultWatermarkImagePath() {
		return SpringContextHolder.getRootRealPath()+File.separator+PropertiesUtil.getProperties("defaultWatermarkImagePath");
	}
	
	/**
	 * 如果有配置default则删除历史图片
	 * @param file
	 * @param path
	 */
	private static void deleteHistoryImg(File file, String path){
		File defaultFile = new File(appendSuffix(path, "default"));
		if(defaultFile.exists()){
			//删除原图
			file.delete();
			//重命名
			defaultFile.renameTo(new File(path));
		}
	}
	
	/**
	 * 获得缩略图路径
	 * @param req
	 * @return
	 */
	public static String getThumbnailUrl(HttpServletRequest req) {
		try {
    	int x = Math.round(ServletRequestUtils.getFloatParameter(req, "x", 0));
        int y = Math.round(ServletRequestUtils.getFloatParameter(req, "y", 0));
        int w = Math.round(ServletRequestUtils.getFloatParameter(req, "w", 0));
        int h = Math.round(ServletRequestUtils.getFloatParameter(req, "h", 0));
    	if(w>1 && h>1){
        	String appPath = req.getSession().getServletContext().getRealPath("/");
        	String filePath = appPath+ServletRequestUtils.getStringParameter(req, "thumbnail");
        	String newPath = FileInfoUtil.rename(filePath, "project_");
				Thumbnails.of(filePath).sourceRegion(x, y, w, h).size(120, 120).keepAspectRatio(false).toFile(newPath);
			return newPath;
    	}
		} catch (Exception e) {
			logger.error("获取缩略图地址失败",e);
		}
        return null;
	}
	
}
