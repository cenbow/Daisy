package com.yourong.backend.logAdvice;

import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.constant.Constant;
import com.yourong.core.sys.manager.SysLogManager;
import com.yourong.core.sys.model.SysLog;
import com.yourong.core.sys.model.SysUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAfterOperationAdvice {

	//private final static ThreadLocal<Object>	threadLocal =  new ThreadLocal<>();

	Logger logger = LoggerFactory.getLogger(LogAfterOperationAdvice.class);

	@Autowired
	private SysLogManager sysLogManager;
	
	
	//@Pointcut("execution(* com.yourong.backend.**.**(..))")
	@Pointcut("@annotation(com.yourong.common.annotation.LogInfoAnnotation)")
	private void loginfoAnnotation(){}//定义一个切入点

	@Pointcut("execution(* com.yourong.backend..*.*Controller.**(..))")
	private void controller(){}//定义一个切入点
	/**
	 * 前置通知：在某连接点之前执行的通知，但这个通知不能阻止连接点前的执行
	 * 
	 * @param jp
	 *            连接点：程序执行过程中的某一行为，例如，AServiceImpl.barA()的调用或者抛出的异常行为
	 */
//	 @Before(value = "loginfoAnnotation()" )
//	public void doBefore(JoinPoint jp) {
//		String strLog = " "
//				+ jp.getTarget().getClass().getName() + "."
//				+ jp.getSignature().getName();
//	    System.out.print(strLog);
//	}

	/**
	 * 环绕通知：包围一个连接点的通知，可以在方法的调用前后完成自定义的行为，也可以选择不执行
	 * 类似Web中Servlet规范中的Filter的doFilter方法。
	 * 
	 * @param pjp
	 *            当前进程中的连接点
	 * @return
	 * @throws Throwable
	 */
	@Around(value = "loginfoAnnotation()&&controller()&& @annotation(annotation) &&args(object,..) ")
	public Object doAround(ProceedingJoinPoint pjp, LogInfoAnnotation annotation,
			Object object) throws Throwable {
		SysLog sysLog = new SysLog();
		sysLog.setModuleName(annotation.moduleName());
		sysLog.setModuleDesc(annotation.desc());
		//构造登陆用户的信息
		buildSysLog(sysLog);
		 Object[] args = pjp.getArgs();
		StringBuffer  param = new StringBuffer();
		//获取参数详细信息
		for (Object temp : args){
			if(!temp.getClass().isArray()){
				Class<?> tempClass = temp.getClass();
				String packageName =	tempClass.getPackage().getName();
				if(temp != null  &&isRecordPackage(packageName) )
					param.append(temp.toString()).append("|");
			}

		}
		sysLog.setParams(param.toString());
		sysLog.setRequestUrl(pjp.getSignature().getDeclaringTypeName()+"."+pjp.getSignature().getName());
		Object retVal = pjp.proceed();
		if (retVal != null){
			String result = retVal.toString();
			sysLog.setResult(result);
		}
		try {
			int i = sysLogManager.insertSelective(sysLog);
		}catch (Exception e ){
			 logger.error("插入日志异常",e);
		}
		return retVal;
	}
	private  boolean isRecordPackage(String p){
		String[] pack =	{"java.lang","java.util","com.yourong.core"};
		for (String s :pack){
			if (p.startsWith(s))
				return  true;
		}
		return  false;
	}
	/***
	 * 构造日志对象， 主要是登陆后用户
	 * @param sysLog
	 */
	private  void  buildSysLog(SysLog sysLog){
		Subject subject = SecurityUtils.getSubject();
		sysLog.setRemoteAddr(subject.getSession().getHost());
		SysUser sysUser = null;
		if(subject != null && subject.isAuthenticated() ){
			sysUser =  (SysUser) subject.getSession().getAttribute(Constant.CURRENT_USER);
			sysLog.setOperateName(sysUser.getName());
			sysLog.setOperateId(sysUser.getId());
		}
	}

	/**
	 * 抛出异常后通知 ： 在方法抛出异常退出时执行的通知。
	 * 
	 * @param jp
	 *            连接点：程序执行过程中的某一行为，例如，AServiceImpl.barA()的调用或者抛出的异常行为
	 */
//	@After(value = "loginfoAnnotation()")
//	public void doAfter(JoinPoint jp) {
//		String strLog = "程序执行完成:log Ending method: "
//				+ jp.getTarget().getClass().getName() + "."
//				+ jp.getSignature().getName();
//		logger.warn(strLog);
//	}
//
//
//	@AfterReturning("loginfoAnnotation()")
//	public void afterReturn(){
//
//	}
//




}
