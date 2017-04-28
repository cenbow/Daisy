package com.yourong.common.mail;

import java.util.Map;

/**
 * Created by Administrator on 2014/9/13.
 */
public interface SendMailService {
    /***
     *   发送邮件
     * @param to  地址
     * @param subject 主题
     * @param vmFile  模板文件
     * @param map 填充模板的数据
     */
    public void sendMailNow(String to,String subject ,String vmFile, Map<String, Object> map);

    /***
     *   后台发送邮件，使用不同邮件服务器
     * @param to  地址
     * @param subject 主题
     * @param vmFile  模板文件
     * @param map 填充模板的数据
     */
    public void sendMailNowByTencent(String to,String subject ,String vmFile, Map<String, Object> map);
    
    /***
     *   后台发送邮件，使用webapi
     * @param to  地址
     * @param subject 主题
     * @param vmFile  模板文件
     * @param map 填充模板的数据
     */
    public void sendMailNowBySohuWebAPI(String to,String subject ,String vmFile, Map<String, Object> map);
    
    /**
	 * 根据指定模板发送邮件
	 * @param toArr 收件人数组
	 * @param subject 邮件主题
	 * @param templateInvokeName 模板名称
	 * @param Map<String, String[]> subMap 比如 "%name%": ["Ben", "Joe"],"%money%":[288, 497]}
	 */
	public void sendMailNowBySohuTemplate(String[] toArr, String subject, String templateInvokeName, Map<String, String[]> subMap);
 
	/**
	 * 获取模板
	 * @param invokeName
	 */
    public String sohuTemplateGet(String invokeName);
    
    /**
     * 新增模板
     * @param invokeName
     * @param name
     * @param html
     * @param text
     */
    public String sohuTemplateAdd(String invokeName, String name, String subject, Integer emailType, String html, String text);
    
    /**
     * 删除模板
     * @param invokeName
     */
    public String sohuTemplateDelete(String invokeName);
    
    /**
     * 更新模板
     * @param invokeName
     * @param name
     * @param html
     */
    public String sohuTemplateUpdate(String invokeName, String name, String subject, Integer emailType, String html);
}
