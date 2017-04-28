package com.yourong.common.util;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourong.common.annotation.ValueCheck;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.RegexEnum;

/**
 * 
 * @desc 根据@ValueCheck 注解校验
 * @author wangyanji 2016年7月13日下午1:44:47
 */
public class ValueCheckUtil {

	private static Logger logger = LoggerFactory.getLogger(ValueCheckUtil.class);

	/**
	 * 
	 * @Description:校验
	 * @param object
	 * @return
	 * @author: wangyanji
	 * @time:2016年7月13日 下午3:26:05
	 */
	public static ResultDO<String> valid(Object object) {
		// @TODO 没有适配的DO, errorMsg 先放在ResultDO.result中返回
		ResultDO<String> rDO = new ResultDO<String>();
		if (object == null) {
			rDO.setResult("对象不能为空");
			rDO.setSuccess(false);
			return rDO;
		}
		try {
			// 获取object的类型
			Class<? extends Object> clazz = object.getClass();
			// 获取该类型声明的成员
			Field[] fields = clazz.getDeclaredFields();
			// 遍历属性
			for (Field field : fields) {
				// 对于private私有化的成员变量，通过setAccessible来修改器访问权限
				field.setAccessible(true);
				validate(rDO, field, object);
				field.setAccessible(false);
				// 重新设置会私有权限
				if (rDO.isError()) {
					return rDO;
				}
			}
		} catch (Exception e) {
			logger.error("校验系统异常 object={}", object, e);
			rDO.setResult("系统异常");
			rDO.setSuccess(false);
		}
		return rDO;
	}

	private static ResultDO<String> validate(ResultDO<String> rDO, Field field, Object object) throws IllegalArgumentException,
			IllegalAccessException {
		String description;
		Object value;
		// 获取对象的成员的注解信息
		ValueCheck valueCheck = field.getAnnotation(ValueCheck.class);
		value = field.get(object);

		if (valueCheck == null)
			return rDO;

		description = valueCheck.description().equals("") ? field.getName() : valueCheck.description();

		if (!valueCheck.nullable()) {
			if (value == null || StringUtil.isBlank(value.toString())) {
				rDO.setResult(description + "不能为空");
				rDO.setSuccess(false);
				return rDO;
			}
		}
		if (value.toString().length() > valueCheck.maxLength() && valueCheck.maxLength() != 0) {
			rDO.setResult(description + "长度不能超过" + valueCheck.maxLength());
			rDO.setSuccess(false);
			return rDO;
		}
		if (value.toString().length() < valueCheck.minLength() && valueCheck.minLength() != 0) {
			rDO.setResult(description + "长度不能小于" + valueCheck.minLength());
			rDO.setSuccess(false);
			return rDO;
		}
		if (valueCheck.regexType() != RegexEnum.NULL) {
			switch (valueCheck.regexType()) {
			case NULL:
				break;
			case IDCARD:
				if (!RegexUtils.checkIdCard(value.toString())) {
					rDO.setResult(description + "身份证号码格式不正确");
					rDO.setSuccess(false);
					return rDO;
				}
				break;
			case EMAIL:
				if (!RegexUtils.checkEmail(value.toString())) {
					rDO.setResult(description + "邮箱地址格式不正确");
					rDO.setSuccess(false);
					return rDO;
				}
				break;
			case IP:
				if (!RegexUtils.checkIpAddress(value.toString())) {
					rDO.setResult(description + "IP格式不正确");
					rDO.setSuccess(false);
					return rDO;
				}
				break;
			case NUMBER:
				if (!RegexUtils.checkDigit(value.toString())) {
					rDO.setResult(description + "不是整数");
					rDO.setSuccess(false);
					return rDO;
				}
				break;
			case MOBILE:
				if (!RegexUtils.checkMobileLocal(value.toString())) {
					rDO.setResult(description + "手机号码格式不正确");
					rDO.setSuccess(false);
					return rDO;
				}
				break;
			default:
				break;
			}
		}

		if (StringUtil.isNotBlank(valueCheck.regexExpression())) {
			if (!Pattern.matches(valueCheck.regexExpression(), value.toString())) {
				rDO.setResult(description + "格式不正确");
				rDO.setSuccess(false);
				return rDO;
			}
		}
		return rDO;
	}
}
