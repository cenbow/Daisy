package com.yourong.common.log;

import com.yourong.common.annotation.BussAnnotation;
import com.yourong.common.util.DateUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//@Aspect
//@Component
public class LogAfterOperationAdvice {

	Logger logger = LoggerFactory.getLogger(LogAfterOperationAdvice.class);
	
	
	@Pointcut("execution(* com.yourong.backend.service..*.*(..))")  
	private void anyMethod(){}//定义一个切入点 

	/**
	 * 前置通知：在某连接点之前执行的通知，但这个通知不能阻止连接点前的执行
	 * 
	 * @param jp
	 *            连接点：程序执行过程中的某一行为，例如，AServiceImpl.barA()的调用或者抛出的异常行为
	 */
//	 @Before(value = "anyMethod()" )
//	public void doBefore(JoinPoint jp) {
//		String strLog = "log Begining method: "
//				+ jp.getTarget().getClass().getName() + "."
//				+ jp.getSignature().getName();
//		logger.warn(strLog);
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
	@Around(value = "anyMethod() && @annotation(annotation) &&args(object,..) ", argNames = "annotation,object")
	public Object doAround(ProceedingJoinPoint pjp, BussAnnotation annotation,
			Object object) throws Throwable {
		Subject subject = SecurityUtils.getSubject();
		String user = "";
		if(subject != null && subject.isAuthenticated()){
			user = subject.getPrincipal().toString();
			String IP = subject.getSession().getHost();
			logger.info("时间:"+DateUtils.toString(DateUtils.getCurrentDateTime(),DateUtils.TIME_PATTERN_SHORT_2)+" IP:"+IP+"  操作人:"+user+"  模块:"+annotation.moduleName()+" 执行内容:"+annotation.option());
				
		}		
		long time = System.currentTimeMillis();
		//Object[] args = pjp.getArgs();
		
		Object retVal = pjp.proceed();
		time = System.currentTimeMillis() - time;
		logger.info("执行时间" + time + " ms");
		
		return retVal;
	}

	/**
	 * 抛出异常后通知 ： 在方法抛出异常退出时执行的通知。
	 * 
	 * @param jp
	 *            连接点：程序执行过程中的某一行为，例如，AServiceImpl.barA()的调用或者抛出的异常行为
	 */
	@After(value = "anyMethod()")
	public void doAfter(JoinPoint jp) {
		String strLog = "doAfter:log Ending method: "
				+ jp.getTarget().getClass().getName() + "."
				+ jp.getSignature().getName();
		logger.warn(strLog);
	}

	@AfterThrowing("anyMethod()")
	public void doAfterThrow() {
		System.out.println("例外通知");
	}




}
