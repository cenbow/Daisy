/**
 * 
 */
package com.yourong.common.thirdparty.sinapay.member.domain.result;

import java.io.Serializable;

import com.yourong.common.thirdparty.sinapay.common.enums.CredentialsType;



/**
 * 证件
 * @version 0.1 2014年5月20日 下午2:23:56
 */
public class Credentials implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private String number;
    private CredentialsType type;
    
    /**
     * 
     * @param number 证件号码
     * @param type 证件类型
     * @throws IllegalArgumentException 如果任一参数为<code>null</code>
     */
    public Credentials(String number, CredentialsType type) {
        if(number == null)
            throw new IllegalArgumentException("number is required");
        if(type == null)
            throw new IllegalArgumentException("type is required");
        
        this.number = number;
        this.type = type;
    }

    /**
     * 证件号码
     * @return not null
     */
    public String getNumber() {
        return number;
    }

    /**
     * 证件类型
     * @return not null
     */
    public CredentialsType getType() {
        return type;
    }
    
}
