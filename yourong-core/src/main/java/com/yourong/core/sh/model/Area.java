package com.yourong.core.sh.model;

public class Area {
	private String text;
	private String code;
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
    @Override  
    public String toString() {  
        return "[text=" + text + ", code=" + code + "]";  
    }
}
