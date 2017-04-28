package com.yourong.core.upload.model;

public class ImageConfig {
	private String	category;//类目
	private int		height;//高度
	private int		width;//宽度
	private String  sizeType;//大小型号 (default、m1、m2、m3,s1、s2、s3使用这7种规格来定义图片的大小(m1最大)。注：如果不指定默认，将会保存原图。)
	private boolean isAddWatermark = true;//是否添加水印
	private String  watermarkImagePath;//水印图片地址
	private boolean keepAspectRatio = true;//保持宽高比
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public String getSizeType() {
		return sizeType;
	}
	public void setSizeType(String sizeType) {
		this.sizeType = sizeType;
	}
	public boolean isAddWatermark() {
		return isAddWatermark;
	}
	public void setIsAddWatermark(boolean isAddWatermark) {
		this.isAddWatermark = isAddWatermark;
	}
	public String getWatermarkImagePath() {
		return watermarkImagePath;
	}
	public void setWatermarkImagePath(String watermarkImagePath) {
		this.watermarkImagePath = watermarkImagePath;
	}
	public boolean isKeepAspectRatio() {
		return keepAspectRatio;
	}
	public void setKeepAspectRatio(boolean keepAspectRatio) {
		this.keepAspectRatio = keepAspectRatio;
	}
	
}
