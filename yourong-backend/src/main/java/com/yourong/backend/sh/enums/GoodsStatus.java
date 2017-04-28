package com.yourong.backend.sh.enums;
/**
 * @author:quanguo.wang
 * @time:2017年3月30日 下午5:28:45
 *
 */
public enum GoodsStatus {
	OFF_SHELVES(1, "下架"),
	SHELVES(2, "上架");
	
	private int status;
	private String text;

	GoodsStatus(int status, String text) {
		this.status = status;
		this.text = text;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}

