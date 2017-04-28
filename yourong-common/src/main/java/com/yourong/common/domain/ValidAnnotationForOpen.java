package com.yourong.common.domain;

import java.lang.reflect.Field;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourong.common.enums.OpenServiceResultCode;


@SuppressWarnings("serial")
public abstract class ValidAnnotationForOpen extends AbstractBaseObject {

	private static final Logger logger = LoggerFactory.getLogger(ValidAnnotationForOpen.class);

	/**
	 * 
	 * @Description:成员变量基础校验
	 * @author: wangyanji
	 * @time:2016年11月8日 下午1:27:04
	 */
	public void valid(OpenServiceResultDO<?> outPut) {
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.getAnnotations().length == 0) {
				continue;
			}
			field.setAccessible(true);
			if (field.getAnnotation(NotNull.class) != null) {
				handleNull(field, outPut);
			}
			field.setAccessible(false);
			if (!outPut.isSuccess()) {
				return;
			}
		}
	}

	private void handleNull(Field field, OpenServiceResultDO<?> outPut) {
		try {
			Object value = field.get(this);
			if (value == null) {
				outPut.setResultCodeByEnum(OpenServiceResultCode.INPUT_VALID_ERROR);
				outPut.setRemark(field.getName() + "不能为空！");
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			logger.error("变量基础校验失败 field={} biz={}", field.getName(), this.toString(), e);
			outPut.setResultCodeByEnum(OpenServiceResultCode.INPUT_VALID_ERROR);
			outPut.setRemark(field.getName() + "校验失败！");
		}
	}

}
