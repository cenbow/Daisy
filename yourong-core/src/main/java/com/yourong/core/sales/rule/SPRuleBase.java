package com.yourong.core.sales.rule;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.SPRuleMethod;
import com.yourong.core.sales.annotation.SPMethod;


public abstract class SPRuleBase {
	private static final Logger logger = LoggerFactory.getLogger(SPRuleBase.class);
	
	public abstract boolean execute(SPParameter parameter);
	
	protected boolean build(SPParameter parameter){
		Class cla = this.getClass();
		boolean isSuccess = false;
		Method[] methods = cla.getDeclaredMethods();
		try {
			for(Method m : methods){
				if(m.isAnnotationPresent(SPMethod.class)){//并且这个方法==活动中的方法
					String name = m.getAnnotation(SPMethod.class).name();
					SPRuleMethod method = parameter.getTargetMethod(name);
					if(method != null){
						Object obj = null;
						Type parameterTypes[] =m.getGenericParameterTypes();
						if(m.isVarArgs() || parameterTypes.length >0){
							if(StringUtil.isNotBlank(method.getValue())){
								obj = m.invoke(this,parameter, method);
							}else{
								obj = m.invoke(this,parameter);
							}
						}else{
							obj = m.invoke(this);
						}
						if(obj != null && obj instanceof Boolean){
							isSuccess = (Boolean)obj;
						}
//						logger.info("用户："+parameter.getMemberId()+",执行活动规则方法："+name+",结果："+isSuccess);
						if(!isSuccess){//如果某一个活动不满足，则退出条件
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			isSuccess = false;
			logger.error("活动规则解释引擎，执行过程出现异常", e);
		}
		return isSuccess;
	}
	
}
