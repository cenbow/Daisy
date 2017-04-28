package com.yourong.api.cache;

import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.util.DateUtils;

/**
 * Created by py on 2015/3/19.
 */
public class RedisAPIMemberClient {
    private static final String PLATFORM = "platform";
    private static  String  CODES = "CODES";
    private static String API_TOKEN="apiToken";
    private static String APP_MEMBER="appMember";
    private static  String  UPDATE_PASSWORD_KEY = "updatePasswordKey";
    private static  int CODE_EXOURE_SECOND = 15*60;

    /**
     * 设置用户短信验证码，
     * @param mobile
     */
    public static void setUserMobileSMScode(Long mobile,String code){
        String key = RedisConstant.REDIS_KEY_USER + RedisConstant.REDIS_SEPERATOR
                + mobile;
        RedisManager.hset(key, CODES, code);
        RedisManager.expire(key, CODE_EXOURE_SECOND);
    }

    /**
     * 获取用户短信验证码
     *
     * @param mobile
     */
    public static String getUserMobileSMScode(Long mobile) {
        String key = RedisConstant.REDIS_KEY_USER + RedisConstant.REDIS_SEPERATOR
                + mobile;
        String code = RedisManager.hget(key, CODES);
        return code;
    }
    
    /**
     * 删除用户短信验证码
     * @param mobile
     */
    public static void removeUserMobileSMSCode(Long mobile){
    	 String key = RedisConstant.REDIS_KEY_USER + RedisConstant.REDIS_SEPERATOR
                 + mobile;
           RedisManager.hdel(key,CODES);
    }
    

    /**
     * 设置修改密码标记
     * @param mobile
     */
    public static void setUserUpdatePasswordFlag(Long mobile, String status){
		  String key = RedisConstant.REDIS_KEY_USER + RedisConstant.REDIS_SEPERATOR + mobile;
		  RedisManager.hset(key, UPDATE_PASSWORD_KEY, status);
		  RedisManager.expire(key, CODE_EXOURE_SECOND);
    }
    
    /**
     * 获得设置修改密码标记
     * @param mobile
     * @return
     */
    public static String getUserUpdatePasswordFlag(Long mobile){
    	String key = RedisConstant.REDIS_KEY_USER + RedisConstant.REDIS_SEPERATOR
                + mobile;
        String flag = RedisManager.hget(key, UPDATE_PASSWORD_KEY);
        return flag;
    }
    
    /**
     * 删除设置修改密码标记
     * @param mobile
     */
    public static void removeUserUpdatePasswordFlag(Long mobile){
    	String key = RedisConstant.REDIS_KEY_USER + RedisConstant.REDIS_SEPERATOR
                + mobile;
          RedisManager.hdel(key,UPDATE_PASSWORD_KEY);
   }
    
}
