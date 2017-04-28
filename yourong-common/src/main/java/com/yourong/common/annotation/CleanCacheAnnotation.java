package com.yourong.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)  
@Target({ElementType.METHOD})  
public @interface CleanCacheAnnotation {
	/**key **/
    String key() default ""; 
    
    /**通过参数配置后缀key，默认为-1*/
	int paramKeyIndex() default -1;
}
