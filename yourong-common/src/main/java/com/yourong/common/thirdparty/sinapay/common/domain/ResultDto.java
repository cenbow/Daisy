package com.yourong.common.thirdparty.sinapay.common.domain;

import java.io.Serializable;

/**
 * <p>支付结果返回对象</p>
 * @author Wallis Wang
 * @version $Id: ResultDto.java, v 0.1 2014年5月15日 下午6:47:33 wangqiang Exp $
 */
public class ResultDto<T> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 991489187609347805L;

    /**
     * 接口调用状态，成功还是失败
     */
    private boolean           success          = false;

    /**
     * 结果对象
     */
    private T                 module;

    /**
     * 错误码
     */
    private String            errorCode;

    /**
     * 错误消息
     */
    private String            errorMsg;
    /**
     * 如果查询结果是个分页查询结果，则该属性表示查询的总记录数。
     */
    private int               totalCount       = 0;

    /**
     * 摘要
     */
    private String            memo;

    public ResultDto() {
    }

    public ResultDto(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
    public boolean isError(){
    	boolean result = !success;
    	return result;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getModule() {
        return module;
    }

    public void setModule(T module) {
        this.module = module;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

}
