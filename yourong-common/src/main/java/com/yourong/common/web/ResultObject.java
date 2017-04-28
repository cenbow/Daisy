package com.yourong.common.web;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

/**
 * 操作结果提示
 * 
 * @author py
 *
 */

public class ResultObject<T> implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1123123123L;
	
	private T data;

	// 业务状态 0以下都是失败， 1 操作成功,其他状态 可以自己定义
	private Integer status = 0;	
	
	private String resultCode;	
	/**
	 * 多个消息提示
	 * @return
	 */
	private List<String> messages = Lists.newArrayList();	
		
	public List<String> getMessages() {
		return messages;
	}	
	public void addListMessage(List<String> list){
		messages.addAll(list);
	}
	
	
	public ResultObject(T t) {
		this.data = t;
	}
	
	public ResultObject() {
		
	}	
	
	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public void setMessage(String message) {
		messages.add(message);
	}
	
	public void  setErrorMessage(String message){
		this.status = 0;
		messages.add(message);
	}
	public void setSuccessMessage(String message){
		this.status = 1;
		messages.add(message);
	}
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public void isSuccess(){
		this.status = 1;
	}
	public void isError(){
		this.status = 0;
	}

	public boolean isSuccessed() {
		if (status == 1) {
			return true;
		} else {
			return false;
		}
	}
	

}
