package com.yourong.core.upload.model;

public class UploadFileInfo {
	//默认类目
	public static final String DEFAULT_CATEOGRY = "default";

	//原使文件名
	private String originalFilename;
	//文件名称
	private String name;
	//文件后缀
	private String suffix;
	//文件相对路径
	private String filePath;
	//文件类别
	private String category;
	//文件大小
	private long fileSize;
	//宽
	private int  width;
	//高
	private int  height;
	//远程图片地址
	private String ossPicUrl;
	
	public UploadFileInfo(){
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getOssPicUrl() {
		return ossPicUrl;
	}

	public void setOssPicUrl(String ossPicUrl) {
		this.ossPicUrl = ossPicUrl;
	}

	
	
}
