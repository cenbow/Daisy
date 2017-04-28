package com.yourong.common.mail.impl;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.yourong.common.constant.Constant;
import com.yourong.common.mail.SendMailService;
import com.yourong.common.util.HttpUtil;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.StringUtil;

/**
 * Created by Administrator on 2014/9/13.
 */
@Service
public class SendMailServiceImpl implements SendMailService {


    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JavaMailSender mailSender;


    @Autowired
    private JavaMailSender qqmailSender;

   @Autowired
    private VelocityEngineFactoryBean velocityEngine;

    @Autowired
    private TaskExecutor  threadPool ;


    private String tempPath;

    public String getTempPath() {
        return tempPath;
    }

    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }


    public TaskExecutor getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(TaskExecutor threadPool) {
        this.threadPool = threadPool;
    }

    public JavaMailSender getMailSender() {
        return mailSender;
    }

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public  FactoryBean<VelocityEngine>  getVelocityEngine() {
        return velocityEngine;
    }

    public void setVelocityEngine(VelocityEngineFactoryBean velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public JavaMailSender getQqmailSender() {
        return qqmailSender;
    }

    public void setQqmailSender(JavaMailSender qqmailSender) {
        this.qqmailSender = qqmailSender;
    }

    @Override
    public void sendMailNow(final String to,final String subject ,String vmFile,final  Map<String, Object> map) {
         //final String  vmfilePath  = "/mailTemplate/"+vmFile+".vm";
         sendMailNowBySohuWebAPI(to, subject, vmFile, map);
//        threadPool.execute(new Runnable() {
//            @Override
//            public void run() {
              /*try {
                    VelocityEngine ve = velocityEngine.createVelocityEngine();
                    String s = VelocityEngineUtils.mergeTemplateIntoString(ve, vmfilePath, Constant.DEFAULT_CODE, map);
                    MimeMessage msg = mailSender.createMimeMessage();                    
                    msg.setContent(s,"text/html;charset=UTF-8");                   
                    MimeMessageHelper helper = new MimeMessageHelper(msg, true,Constant.DEFAULT_CODE);
                    String  sendFrom = PropertiesUtil.getProperties("mail.send.from");                  
                    String  sendname = PropertiesUtil.getProperties("mail.send.name");                  
                    helper.setFrom(new InternetAddress(sendFrom, sendname, "UTF-8"));
                    helper.setTo(to);
                    helper.setSubject(subject);
                    helper.setText(s, true);                  
                    mailSender.send(helper.getMimeMessage());
                } catch (Exception e) {
                   logger.error("发送邮件异常 email ="+to,e);
                }*/
//            }
//        });



    }

    @Override
    public void sendMailNowByTencent(String to, String subject, String vmFile, Map<String, Object> map) {
         String  vmfilePath  = "/mailTemplate/"+vmFile+".vm";
        try {
            VelocityEngine ve = velocityEngine.createVelocityEngine();
            String s = VelocityEngineUtils.mergeTemplateIntoString(ve, vmfilePath, Constant.DEFAULT_CODE, map);
            MimeMessage msg = qqmailSender.createMimeMessage();
            msg.setContent(s,"text/html;charset=UTF-8");
            MimeMessageHelper helper = new MimeMessageHelper(msg, true,Constant.DEFAULT_CODE);
            helper.setFrom(new InternetAddress("service@yourongwang.com"));
            msg.setSubject(subject);
            helper.setTo(to);
            helper.setText(s, true);
            qqmailSender.send(helper.getMimeMessage());
        } catch (Exception e) {
            logger.error("发送邮件异常 email ="+to,e);
        }

    }
    
    @Override
	public void sendMailNowBySohuWebAPI(final String to,final String subject ,String vmFile,final  Map<String, Object> map) {
        String  vmfilePath  = "/mailTemplate/"+vmFile+".vm";
        try {
	        VelocityEngine ve = velocityEngine.createVelocityEngine();
	        String s = VelocityEngineUtils.mergeTemplateIntoString(ve, vmfilePath, Constant.DEFAULT_CODE, map);
	        String  url = PropertiesUtil.getProperties("mail.send.url");   
	        String  apiUser = PropertiesUtil.getProperties("mail.send.apiUser");                  
            String  apiKey = PropertiesUtil.getProperties("mail.send.apiKey");  
	        String  sendFrom = PropertiesUtil.getProperties("mail.send.from");                  
            String  sendname = PropertiesUtil.getProperties("mail.send.name");  
            
	        Map<String, String> paraMap = new HashMap<String, String>();
	        paraMap.put("api_user", apiUser);
	        paraMap.put("api_key", apiKey);
	        paraMap.put("from", sendFrom);
	        paraMap.put("fromname", sendname);
	        paraMap.put("subject", subject);
	        paraMap.put("to", to);
	        paraMap.put("html", s);
	        paraMap.put("resp_email_id", "true");
	        
	        String retStr = HttpUtil.doPost(url, paraMap);
	        logger.info("发送邮件返回" + retStr);
        } catch (Exception e) {
            logger.error("发送邮件异常 email ="+to,e);
        }
    }
    
    @Override
	public void sendMailNowBySohuTemplate(final String[] toArr,
			final String subject, final String templateInvokeName,
			final Map<String, String[]> subMap) {
		try {
			String url = PropertiesUtil.getProperties("mail.template.send.url");
			String apiUser = PropertiesUtil.getProperties("mail.send.apiUser");
			String apiKey = PropertiesUtil.getProperties("mail.send.apiKey");
			String sendFrom = PropertiesUtil.getProperties("mail.send.from");
			String sendname = PropertiesUtil.getProperties("mail.send.name");

			Map<String, Object> subVarsMap = new HashMap<String, Object>();

			subVarsMap.put("to", toArr);
			subVarsMap.put("sub", subMap);

			String jsonStr = JSONObject.valueToString(subVarsMap);
			System.out.println(jsonStr);

			Map<String, String> paraMap = new HashMap<String, String>();
			paraMap.put("api_user", apiUser);
			paraMap.put("api_key", apiKey);
			paraMap.put("template_invoke_name", templateInvokeName);
			paraMap.put("from", sendFrom);
			paraMap.put("fromname", sendname);
			paraMap.put("subject", subject);
			paraMap.put("substitution_vars", jsonStr);
			paraMap.put("resp_email_id", "true");

			String retStr = HttpUtil.doPost(url, paraMap);
			logger.info("发送邮件返回" + retStr);
		} catch (Exception e) {
			logger.error("查询邮件模板异常 templateInvokeName=" + templateInvokeName, e);
		}
	}
    
	@Override
	public String sohuTemplateGet(String invokeName) {
		try {
			String  url = PropertiesUtil.getProperties("mail.template.get.url");   
			String  apiUser = PropertiesUtil.getProperties("mail.send.apiUser");                  
			String  apiKey = PropertiesUtil.getProperties("mail.send.apiKey");  
			
			Map<String, String> paraMap = new HashMap<String, String>();
			paraMap.put("api_user", apiUser);
			paraMap.put("api_key", apiKey);
			paraMap.put("invoke_name", invokeName);
			
			String retStr = HttpUtil.doPost(url, paraMap);
			logger.info("发送邮件返回" + retStr);
			return retStr;
		} catch (Exception e) {
			logger.error("查询邮件模板异常 invokeName=" + invokeName, e);
			return "error";
		}
	}
	
	@Override
	public String sohuTemplateAdd(String invokeName, String name, String subject, Integer emailType, String html, String text) {
		try {
			String  url = PropertiesUtil.getProperties("mail.template.add.url");   
			String  apiUser = PropertiesUtil.getProperties("mail.send.apiUser");                  
			String  apiKey = PropertiesUtil.getProperties("mail.send.apiKey");  
			
			Map<String, String> paraMap = new HashMap<String, String>();
			paraMap.put("api_user", apiUser);
			paraMap.put("api_key", apiKey);
			paraMap.put("invoke_name", invokeName);
			paraMap.put("subject", subject);
			paraMap.put("name", name);
			paraMap.put("email_type", emailType.toString());
			if(StringUtil.isNotBlank(html)){
				paraMap.put("html", html);		
			}
			if(StringUtil.isNotBlank(text)){
				paraMap.put("text", text);				
			}
			String retStr = HttpUtil.doPost(url, paraMap);
			logger.info("发送邮件返回" + retStr);
			return retStr;
		} catch (Exception e) {
			logger.error("增加邮件模板异常 name=" + name, e);
			return "error";
		}
	}

	@Override
	public String sohuTemplateDelete(String invokeName) {
		try {
			String  url = PropertiesUtil.getProperties("mail.template.delete.url");   
			String  apiUser = PropertiesUtil.getProperties("mail.send.apiUser");                  
			String  apiKey = PropertiesUtil.getProperties("mail.send.apiKey");  
			
			Map<String, String> paraMap = new HashMap<String, String>();
			paraMap.put("api_user", apiUser);
			paraMap.put("api_key", apiKey);
			paraMap.put("invoke_name", invokeName);
			
			String retStr = HttpUtil.doPost(url, paraMap);
			logger.info("发送邮件返回" + retStr);
			return retStr;
		} catch (Exception e) {
			logger.error("删除邮件模板异常 invokeName=" + invokeName, e);
			return "error";
		}
	}

	@Override
	public String sohuTemplateUpdate(String invokeName, String name, String subject, Integer emailType, String html) {
		try {
			String  url = PropertiesUtil.getProperties("mail.template.update.url");   
			String  apiUser = PropertiesUtil.getProperties("mail.send.apiUser");                  
			String  apiKey = PropertiesUtil.getProperties("mail.send.apiKey");  
			
			Map<String, String> paraMap = new HashMap<String, String>();
			paraMap.put("api_user", apiUser);
			paraMap.put("api_key", apiKey);
			paraMap.put("invoke_name", invokeName);
			paraMap.put("name", name);
			paraMap.put("html", html);
			paraMap.put("subject", subject);
			paraMap.put("email_type", emailType.toString());
			String retStr = HttpUtil.doPost(url, paraMap);
			logger.info("发送邮件返回" + retStr);
			return retStr;
		} catch (Exception e) {
			logger.error("更新邮件模板异常 invokeName=" + invokeName, e);
			return "error";
		}
	}

}
