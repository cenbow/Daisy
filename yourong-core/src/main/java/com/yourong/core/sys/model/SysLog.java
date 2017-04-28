package com.yourong.core.sys.model;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.StringUtil;

import java.util.Date;

public class SysLog extends AbstractBaseObject {
    /**
     * 编号*
     */
    private Long id;

    /**
     * 模块名称*
     */
    private String moduleName;

    /**
     * 日志描述*
     */
    private String moduleDesc;

    /**
     * 操作IP地址*
     */
    private String remoteAddr;

    /**
     * 操作者ID*
     */
    private Long operateId;

    /**
     * 操作者姓名*
     */
    private String operateName;

    /**
     * 操作方法*
     */
    private String operateMethod;

    /**
     * 请求URL*
     */
    private String requestUrl;

    /**
     * 操作提交的数据*
     */
    private String params;

    /**
     * 操作返回的结果*
     */
    private String result;

    /**
     * 异常信息*
     */
    private String exception;

    /**
     * 操作时间*
     */
    private Date operateTime;

    /**
     * 创建时间*
     */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {

        moduleName = moduleName == null ? null : moduleName.trim();
         this.moduleName  = substring(moduleName, 256);
    }

    public String getModuleDesc() {
        return moduleDesc;
    }

    public void setModuleDesc(String moduleDesc) {
        this.moduleDesc  = substring(moduleDesc, 1000);
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr == null ? null : remoteAddr.trim();
    }

    public Long getOperateId() {
        return operateId;
    }

    public void setOperateId(Long operateId) {
        this.operateId = operateId;
    }

    public String getOperateName() {
        return operateName;
    }

    public void setOperateName(String operateName) {
        this.operateName = operateName == null ? null : operateName.trim();
    }

    public String getOperateMethod() {
        return operateMethod;
    }

    public void setOperateMethod(String operateMethod) {
        this.operateMethod = operateMethod == null ? null : operateMethod.trim();
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl == null ? null : requestUrl.trim();
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        params  = params == null ? null : params.trim();
        this.params  = substring(params, 3000);
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        result = result == null ? null : result.trim();
        this.result = substring(result,3000);
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        exception = exception == null ? null : exception.trim();
        this.exception = substring(exception,3000);
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    private String substring(String s, int size) {
        if (StringUtil.isNotBlank(s) && s.length() > size) {
            return s.substring(0, size - 1);
        }
        return s;
    }

}