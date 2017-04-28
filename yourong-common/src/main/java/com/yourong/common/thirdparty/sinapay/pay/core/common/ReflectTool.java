package com.yourong.common.thirdparty.sinapay.pay.core.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;
import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.util.CastStringUtil;

/**
 * <p>反射工具包</p>
 * @author Wallis Wang
 * @version $Id: ReflectTool.java, v 0.1 2014年5月22日 下午6:22:41 wangqiang Exp $
 */
public class ReflectTool {

    /**
     * 校验带有<code>@Notnull</code>注解的字段不能为空
     * @param object
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public static void checkFieldNotnull(Object object) throws IllegalAccessException,
                                                       InvocationTargetException,
                                                       NoSuchMethodException {
        Preconditions.checkArgument(ArrayUtils.isNotEmpty(object.getClass().getDeclaredFields()),
            "[object=%s] do not has fields", object.getClass().getSimpleName());

        Field[] declaredFields = relfect3LevelFields(object);
        for (Field field : declaredFields) {
            NotNull notNullAnno = field.getAnnotation(NotNull.class);
            if (notNullAnno != null) {
                String fieldName = field.getName();
                Preconditions.checkNotNull(PropertyUtils.getProperty(object, fieldName),
                    "[Field=%s] can not be null", fieldName);
            }
        }
    }

    /**
     * 当前对象往上找2层，找出其所有fields
     * @param obj
     * @return
     */
    public static Field[] relfect3LevelFields(Object obj) {
        // 当前类，第一层
        Field[] curDeclaredFields = obj.getClass().getDeclaredFields();
        // 基类，第二层
        Class<?> fClazz = obj.getClass().getSuperclass();
        if (fClazz != null) {
            Field[] appendFClazz = appendSuperClassFields(curDeclaredFields,
                fClazz.getDeclaredFields());
            // 基类，第三层
            Class<?> tClazz = fClazz.getSuperclass();
            if (tClazz != null) {
                Field[] appendTClazz = appendSuperClassFields(appendFClazz,
                    tClazz.getDeclaredFields());
                return appendTClazz;
            }
            return appendFClazz;
        }
        return curDeclaredFields;
    }

    /**
        * 加上基类的所有成员
        * @param obj
        * @param curDeclaredFields
        */
    public static Field[] appendSuperClassFields(Field[] curDeclaredFields,
                                                 Field[] superDeclaredFields) {
        if (ArrayUtils.isNotEmpty(superDeclaredFields)) {
            Field[] resultFields = new Field[curDeclaredFields.length + superDeclaredFields.length];
            System.arraycopy(curDeclaredFields, 0, resultFields, 0, curDeclaredFields.length);
            System.arraycopy(superDeclaredFields, 0, resultFields, curDeclaredFields.length,
                superDeclaredFields.length);

            return resultFields;
        }
        return curDeclaredFields;
    }

    /**
     * field获取对应的set方法名称
     * @param fieldName
     * @return
     */
    public static String methodSetName(String fieldName) {
        StringBuilder result = new StringBuilder();
        result.append("set");
        result.append(fieldName.substring(0, 1).toUpperCase());
        result.append(fieldName.substring(1));

        return result.toString();
    }

    /**
     * 别名
     * <p>有SerializedName注解的话使用注解中的内容，没有的话，字段名转换成"lower_underscore"</p>
     * @param field
     * @param fieldName
     * @return
     */
    public static String aliasName(Field field, String fieldName) {
        String paramFieldName = CastStringUtil.underLineName(fieldName);
        SerializedName aliasesAnnoation = field.getAnnotation(SerializedName.class);
        if (aliasesAnnoation != null)
            paramFieldName = aliasesAnnoation.value();

        return paramFieldName;
    }

    /**
     * 拷贝paramsMap中的参数到obj对象
     * @param obj
     * @param paramMap
     * @throws Exception
     */
    public static void copyMapParams2DestObj(Object obj, Map<String, String> paramMap)
                                                                                      throws Exception {
        Object resultObj = obj;
        Field[] curDeclaredFields = relfect3LevelFields(resultObj);
        if (ArrayUtils.isEmpty(curDeclaredFields))
            return;
        for (Field field : curDeclaredFields) {
            String fieldName = field.getName();
            String requestFieldVal = paramMap.get(aliasName(field, fieldName));
            if (StringUtils.isNotBlank(requestFieldVal))
                MethodUtils.invokeMethod(obj, methodSetName(fieldName), requestFieldVal);
        }
    }

    /**
     * <code>Map<String,String[])</code>转换成<code>Map<String,String></code>
     * @param paramsMap
     */
    public static Map<String, String> trans2ParamsValues(Map<String, String[]> paramsMap) {
        Map<String, String> resultMap = Maps.newHashMap();
        for (Map.Entry<String, String[]> entry : paramsMap.entrySet()) {
            String[] entryValue = entry.getValue();
            if (ArrayUtils.isNotEmpty(entryValue)) {
                resultMap.put(entry.getKey(), entryValue[0]);
            }
        }
        return resultMap;
    }
}
