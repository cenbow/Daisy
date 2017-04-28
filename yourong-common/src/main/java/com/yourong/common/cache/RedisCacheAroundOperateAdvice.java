package com.yourong.common.cache;

import com.yourong.common.annotation.RedisCacheKey;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yourong.common.annotation.RedisCacheAnnotation;
import com.yourong.common.util.StringUtil;

import java.lang.annotation.Annotation;

/**
 * @desc 缓存注解
 * @author fuyili 2015年12月1日下午5:11:56
 */
@Aspect
@Component
public class RedisCacheAroundOperateAdvice {
	private static final Logger log = LoggerFactory.getLogger(RedisCacheAroundOperateAdvice.class);

	// 在所有标注@CacheAnnotation的地方切入
	@Pointcut("@annotation(com.yourong.common.annotation.RedisCacheAnnotation)")
	private void cacheAnnotation() {
	}

	// 所有core包下managerImpl为后缀的类下的任意方法
	@Pointcut("execution(* com.yourong.core..*ManagerImpl.*(..))")
	private void managerImpl() {
	}

	// 所有core包下managerImpl为后缀的类下的任意方法
	@Pointcut("execution(* com.yourong.*..*ServiceImpl.*(..))")
	private void serviceImpl() {
	}


	@Around(value = "cacheAnnotation() &&(managerImpl() || serviceImpl()) &&@annotation(cache)")
	public Object doAround(ProceedingJoinPoint pjp, RedisCacheAnnotation cache) throws Throwable {
		Object retVal = null;
		String key = cache.key();
		int expire = cache.expire();
		// 根据参数index拼接key,目前支持参数值作为key的结尾最为key的一部分
		if (cache.paramKeyIndex() != -1) {
			Object[] args = pjp.getArgs();
			key = key + args[cache.paramKeyIndex()];
		}
		//以参数为注解的方式 组装key
		if(cache.keyMode() != RedisCacheAnnotation.KeyMode.NULL){
			key = getCacheKey(pjp,cache);
		}
		if (StringUtil.isNotBlank(key)) {
			retVal = getCacheRes(key, retVal);
		}
		// 获取到则返回
		if (retVal != null) {
			return retVal;
		}
		// 获取不到则执行方法
		retVal = pjp.proceed();
		setCacheRes(key, retVal, expire);
		return retVal;
	}

	// 获取缓存数据
	private Object getCacheRes(String key, Object retVal) {
		boolean isExit = RedisManager.isExitByObjectKey(key);
		if (isExit) {
			retVal = RedisManager.getObject(key);
		}
		return retVal;
	}

	// 设置缓存及超时时间
	private void setCacheRes(String key, Object retVal, int expire) {
		RedisManager.putObject(key, retVal);
		//默认 -1 ，永久有效
		if (expire !=-1)
			RedisManager.expireObject(key, expire);
	}
	private String getCacheKey(ProceedingJoinPoint pjp,RedisCacheAnnotation cache) {
		StringBuilder buf=new StringBuilder();
		buf.append(pjp.getSignature().getDeclaringTypeName()).append(".").append(pjp.getSignature().getName());
		if(cache.key().length()>0) {
			buf.append(".").append(cache.key());
		}
		Object[] args=pjp.getArgs();
		if(cache.keyMode()== RedisCacheAnnotation.KeyMode.DEFAULT) {
			Annotation[][] pas=((MethodSignature)pjp.getSignature()).getMethod().getParameterAnnotations();
			for(int i=0;i<pas.length;i++) {
				for(Annotation an:pas[i]) {
					if(an instanceof RedisCacheKey) {
						buf.append(".").append(args[i].toString());
						break;
					}
				}
			}
		} else if(cache.keyMode()== RedisCacheAnnotation.KeyMode.BASIC) {
			for(Object arg:args) {
				if(arg instanceof String) {
					buf.append(".").append(arg);
				} else if(arg instanceof Integer || arg instanceof Long || arg instanceof Short) {
					buf.append(".").append(arg.toString());
				} else if(arg instanceof Boolean) {
					buf.append(".").append(arg.toString());
				}
			}
		} else if(cache.keyMode()== RedisCacheAnnotation.KeyMode.ALL) {
			for(Object arg:args) {
				buf.append(".").append(arg.toString());
			}
		}
		return buf.toString();
	}
}

