package com.yourong.core.fin.model.query;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;

public class PopularityInOutLogQuery extends BaseQueryParam {

	/** 1-推荐好友；2-平台活动；3-平台派送 4-兑换优惠券 **/
	private Integer type;

	/** 来源id（交易id，用户id等） **/
	private Long sourceId;

	/** 备注 **/
	private String remark;
	
	/** 查询数量 **/
	private Integer limitSize;
	
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public String getRemark() {
	
		return remark;
	}

	public void setRemark(String remark)  {
		String temp="";
		try {
			if(StringUtil.isNotEmpty(remark)){
				if(Charset.forName("ISO-8859-1").newEncoder().canEncode(remark)){
					temp= new String(remark.getBytes("ISO-8859-1"),"UTF-8"); 
					remark=temp;
				}
			}
			} catch (UnsupportedEncodingException e) {
			}
		this.remark = remark;
	}

	public Integer getLimitSize() {
		return limitSize;
	}

	public void setLimitSize(Integer limitSize) {
		this.limitSize = limitSize;
	}

}