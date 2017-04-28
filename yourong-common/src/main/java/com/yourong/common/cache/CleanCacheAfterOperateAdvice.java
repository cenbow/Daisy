package com.yourong.common.cache;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yourong.common.annotation.CleanCacheAnnotation;
import com.yourong.common.util.StringUtil;

/**
 * 清除指定缓存 只支持对象级别
 * 
 * @author Administrator
 *
 */
@Aspect
@Component
public class CleanCacheAfterOperateAdvice {
	Logger logger = LoggerFactory.getLogger(CleanCacheAfterOperateAdvice.class);

	@Pointcut("@annotation(com.yourong.common.annotation.CleanCacheAnnotation)")
	private void anyMethod() {
	}// 定义一个切入点

	// 所有core包下managerImpl为后缀的类下的任意方法
	@Pointcut("execution(* com.yourong.core..*ManagerImpl.*(..))")
	private void managerImpl() {
	}

	/**
	 * @Description:返回后通知.在某连接点（join point）正常完成后执行的通知：例如，一个方法没有抛出任何异常，正常返回。
	 * @param jp
	 * @param annotation
	 * @author: fuyili
	 * @time:2015年12月3日 上午9:53:16
	 */
	@AfterReturning(value = "anyMethod() && managerImpl() && @annotation(annotation)")
	public void doAfter(JoinPoint jp, CleanCacheAnnotation annotation) {
		String key = annotation.key();
		int paramKeyIndex = annotation.paramKeyIndex();
		if (paramKeyIndex != -1) {
			Object[] args = jp.getArgs();
			key = key + args[paramKeyIndex];
		}
		if (StringUtil.isNotBlank(key)) {
			RedisManager.removeObject(key);
			logger.info("【清除缓存注解】key={}", key);
		}
	}

}
