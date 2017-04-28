package com.yourong.common.mongo.logDto;

import java.util.Date;

/**
 * Created by XR on 2016/11/7.
 */
public class OpenDownLoadDto {
    /**
     * 对外项目存储id
     */
    private Long openId;
    /**
     * 下载图片地址
     */
    private String loadUrl;
    /**
     * 错误信息
     */
    private String msg;
    /**
     * 创建时间
     */
    private Date createTime;

    public Long getOpenId() {
        return openId;
    }

    public void setOpenId(Long openId) {
        this.openId = openId;
    }

    public String getLoadUrl() {
        return loadUrl;
    }

    public void setLoadUrl(String loadUrl) {
        this.loadUrl = loadUrl;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
