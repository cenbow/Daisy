package com.yourong.api.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Lists;
import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.thirdparty.sinapay.common.util.DateUtil;
import com.yourong.common.util.Collections3;

import java.util.List;

/**
 * API 响应结果集 Created by py on 2015/3/19.
 */
public class ResultDTO<T> extends AbstractBaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 响应时间 yyyyMMddhhmmss
	 */
	private Long responseTime = DateUtil.getCurrentTS().getTime();

	/**
	 * 签名*
	 */
	private String sign;

	public Long getResponseTime() {
		return responseTime;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	/**
	 * 业务请求结果
	 * 
	 * @param t
	 */
	private T t;

	public T getResult() {
		return t;
	}

	/**
	 * 业务请求结果
	 * 
	 * @param t
	 */
	public void setResult(T t) {
		if(t instanceof List){
			//APP 要求如果是集合，都需要data属性
			ResultListObject obj = new ResultListObject();
			obj.setData((List) t);
			this.t = (T) obj;
		}else{
			this.t = t;
		}
		setIsSuccess();
	}

	/**
	 * 业务请求是否正常
	 * 
	 * @param t
	 */
	private boolean isSuccess = false;

	private List<ResultCode> codes = Lists.newArrayList();

	public boolean isSuccess() {
		return isSuccess;
	}

	@JSONField(serialize = false)
	public boolean proessError() {
		return !isSuccess;
	}

	public ResultDTO() {
	}

	public void setResultCodeList(List list) {
		this.codes = list;
		this.setIsError();
	}

	public void setResultCode(ResultCode resultCode) {
		codes.add(resultCode);
		this.setIsError();
	}

	public void setIsSuccess() {
		this.isSuccess = true;
	}

	public void setIsError() {
		this.isSuccess = false;
	}

	public ResultDTO(ResultCode resultCode) {
		codes.add(resultCode);
	}

	private List<ResultCodeDo> resultCodes;

	public List<ResultCodeDo> getResultCodes() {
		List<ResultCodeDo> list = null;
		if (Collections3.isNotEmpty(codes) && codes.size() > 0) {
			list = Lists.newArrayList();
			for (ResultCode code : codes) {
				if (code != null) {
					ResultCodeDo resuldto = new ResultCodeDo();
					resuldto.setMsg(code.getMsg());
					resuldto.setCode(code.getCode());
					resuldto.setCodeStr(code.getCodeStr());
					resuldto.setType(code.getType());
					list.add(resuldto);
				}
			}

		}
		return list;
	}

	/**
	 * 枚举转换类 主要是JSON 在序列化，不会转化枚举
	 *
	 * @author pengyong
	 */
	static class ResultCodeDo {
		private String msg;
		private String code;
		private String codeStr;
		private int type;

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getCodeStr() {
			return codeStr;
		}

		public void setCodeStr(String codeStr) {
			this.codeStr = codeStr;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

	}
	/**
	 * 封装返回的集合对象
	 * @param <T>
	 */
	static class ResultListObject<T>{
		private  List<T> data;
		public List getData() {
			return data;
		}
		public void setData(List<T> data) {
			this.data = data;
		}
	}

}
