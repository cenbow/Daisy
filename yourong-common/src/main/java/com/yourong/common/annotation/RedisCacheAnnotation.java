package com.yourong.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @desc 缓存注解
 * @author fuyili 2015年12月1日上午11:00:52
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCacheAnnotation {
	/** 缓存key */
	String key() default "";

	/** 缓存超时时间 **/
	int expire() default -1;
	
	/**通过参数配置后缀key，默认为-1 ,以后废弃*/
	@Deprecated
	int paramKeyIndex() default -1;

	String  paramName() default "";

	public enum KeyMode{
		NULL,  // 默认空   兼容以前的代码
		DEFAULT,    //只有加了@RedisCacheKey,才加入key后缀中
		BASIC,      //只有基本类型参数,才加入key后缀中,如:String,Integer,Long,Short,Boolean
		ALL;        //所有参数都加入key后缀
	}
	public KeyMode keyMode() default KeyMode.NULL;       //key的后缀模式

}
