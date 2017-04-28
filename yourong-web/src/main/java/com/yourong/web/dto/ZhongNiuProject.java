package com.yourong.web.dto;

public class ZhongNiuProject {
	private Long pid;
	private int status;
	private Long amounted;
	private String progress;
	public Long getPid() {
		return pid;
	}
	public void setPid(Long pid) {
		this.pid = pid;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		if(status == 30){
			status = 1;
		}else if(status == 20){
			status = 0;//预告项目
		}else{
			status = 2;
		}
		this.status = status;
	}
	public Long getAmounted() {
		return amounted;
	}
	public void setAmounted(Long amounted) {
		this.amounted = amounted;
	}
	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}
	
	
}
