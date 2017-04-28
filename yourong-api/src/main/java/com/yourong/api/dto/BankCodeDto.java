package com.yourong.api.dto;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * Created by Administrator on 2015/4/3.
 */
public class BankCodeDto extends  AbstractBaseObject {
    private String remarks;

    private int type;

    private String code;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
