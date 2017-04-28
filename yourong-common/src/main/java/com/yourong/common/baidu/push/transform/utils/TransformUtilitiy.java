package com.yourong.common.baidu.push.transform.utils;

import com.yourong.common.baidu.push.model.PushRequest;
import com.yourong.common.baidu.yun.annotation.HttpPathKeyName;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;



public class TransformUtilitiy {

	public static String extractResourceId(PushRequest request, String defaultResourceId) {
		
		Field[] childField = request.getClass().getDeclaredFields();
		Field[] superFileds = request.getClass().getSuperclass()
				.getDeclaredFields();

		List<Field> fieldList = new LinkedList<Field>();
		fieldList.addAll(Arrays.asList(childField));
		fieldList.addAll(Arrays.asList(superFileds));

		for (Field field : fieldList.toArray(new Field[0])) {
			try {
				field.setAccessible(true);

				if (field.isAnnotationPresent(HttpPathKeyName.class)) {
					Object obj = field.get(request);
					if (obj != null) {
						HttpPathKeyName annotation = field
								.getAnnotation(HttpPathKeyName.class);
//						resurl = resurl + obj.toString();
						return obj.toString();
					}
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return defaultResourceId;
		
	}
	
}
