package com.yourong.api.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ZhongNiuProjectDetail {
	private Long pid;
	private String name;
	private String url;
	private int type;
	private String yield;
	private float duration;
	private int repaytype;
	private int guaranttype;
	private Long threshold;
	private int status;
	private Long amount;
	private Long amounted;
	private String progress;
	private String startdate;
	private String enddate;
	private JSONArray detail;
	private String publishtime;
	
	public Long getPid() {
		return pid;
	}
	public void setPid(Long pid) {
		this.pid = pid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getYield() {
		return yield;
	}
	public void setYield(String yield) {
		this.yield = yield;
	}
	public float getDuration() {
		return duration;
	}
	public void setDuration(float duration) {
		this.duration = duration;
	}
	public int getRepaytype() {
		return repaytype;
	}
	public void setRepaytype(int repaytype) {
		this.repaytype = repaytype;
	}
	public int getGuaranttype() {
		return guaranttype;
	}
	public void setGuaranttype(int guaranttype) {
		this.guaranttype = guaranttype;
	}
	public Long getThreshold() {
		return threshold;
	}
	public void setThreshold(Long threshold) {
		this.threshold = threshold;
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
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
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
	public String getStartdate() {
		return startdate;
	}
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	public JSONArray getDetail() {
		return detail;
	}
	public void setDetail(JSONArray detail) {
		this.detail = detail;
	}
	public String getPublishtime() {
		return publishtime;
	}
	public void setPublishtime(String publishtime) {
		this.publishtime = publishtime;
	}
	public String formatContent(String json,String type){
		StringBuffer sb = new StringBuffer();
		try{
			JSONObject jo = JSONObject.parseObject(json);
			if(type.equals("house")){
				sb.append("房屋类型： ").append(jo.get("house_fwlx"));
				sb.append(" 产权证号： ").append(jo.get("house_cqzh"));
				sb.append(" 登记机构： ").append(jo.get("house_djjg"));
				sb.append(" 登记日期： ").append(jo.get("house_djrq"));
				sb.append(" 建筑面积： ").append(jo.get("house_jzmj"));
				sb.append(" 土地取得方式： ").append(jo.get("house_tdqdfs"));
				sb.append(" 房屋坐落： ").append(jo.get("house_fwzl"));
				sb.append(" 评估价格： ").append(jo.get("house_gj")).append("万");
			}else if(type.equals("car")){
				sb.append(" 车型： ").append(jo.get("car_cx"));
				sb.append(" 登记日期： ").append(jo.get("car_djrq"));
				sb.append(" 行驶里程： ").append(jo.get("car_xsgl")).append("公里");
				sb.append(" 市场估值： ").append(jo.get("car_gz")).append("万");
			}else{
				sb.append(jo.get("car_ms"));
			}
		}catch(Exception ex){}
		return sb.toString();
	}
	
	public String getBaseInfoTitel(String type){
		if(type.equals("house")){
			return "抵押物基本信息";
		}
		return "质押物基本信息";
	}
	
	
}
