package com.yourong.common.util;

public class ConfigUtil {
	
	private static ConfigUtil instance = new ConfigUtil();
	private ConfigUtil(){
	}
	
	public static ConfigUtil getInstance(){
		return instance;
	}
	
	
	/**
	 * 上传文件根目录
	 * @return
	 */
	public String getUploadDirectory(){
		return PropertiesUtil.getProperties("upload.directory");
	}

	/**
	 * open上传文件根目录
	 * @return
	 */
	public String getOpenUploadDirectory(){
		return PropertiesUtil.getProperties("open.directory");
	}
	
	
}
