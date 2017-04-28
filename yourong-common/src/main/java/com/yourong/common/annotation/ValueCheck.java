package com.yourong.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yourong.common.enums.RegexEnum;

/**
 * 
 * @desc 校验注解
 * @author wangyanji 2016年7月13日上午11:51:16
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueCheck {

	// 是否可以为空
    boolean nullable() default false;
     
	// 最大长度
    int maxLength() default 0;
     
	// 最小长度
    int minLength() default 0;
     
    //提供几种常用的正则验证
	RegexEnum regexType() default RegexEnum.NULL;
     
	// 自定义正则验证
	String regexExpression() default "";

	// 参数或者字段描述,这样能够显示友好的异常信息
	String description() default "";
    
}
