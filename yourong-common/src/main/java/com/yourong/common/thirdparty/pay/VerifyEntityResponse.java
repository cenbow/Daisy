package com.yourong.common.thirdparty.pay;

/**
 * 认证内容
 * @author Administrator
 *
 */
public class VerifyEntityResponse extends PayResponse{

    /**
     * 
     */
    private static final long serialVersionUID = 6070045672098648213L;
    
    //认证内容
    private String verify_entity;
    //认证时间
    private String verify_time;
    //扩展信息
    private String  extend_param;
    
    public String getVerify_entity() {
        return verify_entity;
    }
    public void setVerify_entity(String verify_entity) {
        this.verify_entity = verify_entity;
    }
    public String getVerify_time() {
        return verify_time;
    }
    public void setVerify_time(String verify_time) {
        this.verify_time = verify_time;
    }
    public String getExtend_param() {
        return extend_param;
    }
    public void setExtend_param(String extend_param) {
        this.extend_param = extend_param;
    }


}
