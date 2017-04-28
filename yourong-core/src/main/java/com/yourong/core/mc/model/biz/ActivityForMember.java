package com.yourong.core.mc.model.biz;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.StringUtil;

/**
 * 
 * @desc 	
 * @author 
 */
public class ActivityForMember extends AbstractBaseObject {
	
	
	 /**用户名**/
    private String username;

    /**手机号**/    
    private Long mobile;
    
    private String avatars;
    
    private Date dateTime;
    
    

    public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getUsername() {
		if(StringUtil.isNotBlank(username)
				||mobile!=null) {
			return StringUtil.maskUserNameOrMobile(username, mobile);
		} else {
			return null;
		}
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public String getAvatars() {
		return avatars;
	}

	public void setAvatars(String avatars) {
		this.avatars = avatars;
	}
    
    
}
