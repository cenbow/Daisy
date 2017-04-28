package com.yourong.common.thirdparty.sinapay.pay.domainservice.converter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.thirdparty.sinapay.common.enums.CharsetType;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeCode;
import com.yourong.common.thirdparty.sinapay.common.util.CastStringUtil;
import com.yourong.common.thirdparty.sinapay.pay.core.common.Aliases;
import com.yourong.common.thirdparty.sinapay.pay.core.common.Constants;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.CancelAuthTradeDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.CreateBatchPayTradeDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.CreateCollectTradeDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.CreateDepositDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.CreateSinglePayTradeDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.CreateWithDrawDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.FinishAuthTradeDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.PayTradeDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.QueryDepositDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.QueryPayTradeDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.QueryRefundDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.QueryTradeDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.QueryWithDrawDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.RefundTradeDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.RequestDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.CancelAuthTradeArgs;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.FinishAuthTradeArgs;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.PayArgsBase;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.SplitArgs;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.TradeArgs;
import com.yourong.common.thirdparty.sinapay.pay.exceptions.PayFrontException;

/**
 * <p>支付前置请求Dto对象转换</p>
 * @author Wallis Wang
 * @version $Id: RequestConvertUtil.java, v 0.1 2014年5月15日 上午10:42:50 wangqiang Exp $
 */
public class RequestConverter {

    /**
     * 基本请求参数封装
     * @param basicRequestDto
     * @param paramsMap
     * @param needSignInfo 是否将需要签名的字段加入Map中
     */
    public static void addRequestDto2Map(RequestDto hostRequestDto, Map<String, String> paramsMap)
                                                                                                  throws PayFrontException {
        try {
            Field[] declaredFields = RequestDto.class.getDeclaredFields();
            for (Field field : declaredFields) {
                if (Modifier.isFinal(field.getModifiers()))
                    continue;
                String fieldName = field.getName();
                Object fieldVal = PropertyUtils.getProperty(hostRequestDto, fieldName);

                // 校验必填Field
                verifyFieldNotnull(field, fieldName, fieldVal);
                // 非必填Field为空，跳到下一个
                if (fieldVal == null)
                    continue;

                // 别名
                String resultFieldVal = null;

                // 不同Field类型处理
                Class<? extends Object> fieldType = fieldVal.getClass();
                // field为基本类型或者String
                if (fieldType.isPrimitive() || fieldType.isAssignableFrom(String.class)) {
                    resultFieldVal = String.valueOf(fieldVal);
                }
                // field为枚举
                else if (Enum.class.isAssignableFrom(fieldType)) {
                    // TradeCode枚举
                    if (CharsetType.class.isAssignableFrom(fieldType)) {
                        CharsetType charsetType = (CharsetType) fieldVal;
                        resultFieldVal = charsetType.charset();
                    } else {
                        Enum<?> fieldEnumVal = (Enum<?>) fieldVal;
                        resultFieldVal = fieldEnumVal.name();
                    }
                }
                if (resultFieldVal != null) {
                    paramsMap.put(aliasName(field, fieldName), resultFieldVal);
                }
            }
        } catch (Exception e) {
            throw new PayFrontException(e);
        }
    }

    /**
     * 代收请求对象转换
     * @param destDto
     * @param paramsMap
     */
    public static void addCreateCollectTradeDto2Map(CreateCollectTradeDto destDto,
                                                    Map<String, String> paramsMap)
                                                                                  throws PayFrontException {
        resolveFieldsAdd2Map(destDto, paramsMap);
    }

	/**
	 * 代收请求对象转换
	 * 
	 * @param destDto
	 * @param paramsMap
	 */
	public static void addCreateCollectTradeDto2Beta(CreateCollectTradeDto destDto, Map<String, String> paramsMap) throws PayFrontException {
		try {
			for (Field field : destDto.getClass().getDeclaredFields()) {
				if (!Modifier.isFinal(field.getModifiers())) {
					String fieldName = field.getName();
					Object fieldVal = PropertyUtils.getProperty(destDto, fieldName);
					// 解析域的类型
					resolveEntityType(field, fieldName, fieldVal, paramsMap);
					resolveMapTypeBeta(field, fieldName, fieldVal, paramsMap);
					resolveListArgsType(field, fieldName, fieldVal, paramsMap);
					resolvePayArgsType(field, fieldName, fieldVal, paramsMap);
					resoloveSuperClassType(destDto, paramsMap);
				}
			}
		} catch (Exception e) {
			throw new PayFrontException(e);
		}
	}

    /**
     * 单笔代付
     * @param destDto
     * @param paramsMap
     * @throws PayFrontException
     */
    public static void addCreateSinglePayTradeDto2Map(CreateSinglePayTradeDto destDto,
                                                      Map<String, String> paramsMap)
                                                                                    throws PayFrontException {
        resolveFieldsAdd2Map(destDto, paramsMap);
    }

    /**
     * 批量代付
     * @param destDto
     * @param paramsMap
     * @throws PayFrontException
     */
    public static void addCreateBatchPayTradeDto2Map(CreateBatchPayTradeDto destDto,
                                                     Map<String, String> paramsMap)
                                                                                   throws PayFrontException {
        resolveFieldsAdd2Map(destDto, paramsMap);
    }

    /**
     * 支付
     * @param destDto
     * @param paramsMap
     * @throws PayFrontException
     */
    public static void add2PayTradeMap(PayTradeDto destDto, Map<String, String> paramsMap)
                                                                                          throws PayFrontException {
        resolveFieldsAdd2Map(destDto, paramsMap);
    }

    /**
     * 退款
     * @param destDto
     * @param paramsMap
     * @throws PayFrontException
     */
    public static void addRefundTradeDto2Map(RefundTradeDto destDto, Map<String, String> paramsMap)
                                                                                                   throws PayFrontException {
        resolveFieldsAdd2Map(destDto, paramsMap);
    }

    /**
     * 交易查询
     * @param destDto
     * @param paramsMap
     * @throws PayFrontException 
     */
    public static void addQueryTradeDto2Map(QueryTradeDto destDto, Map<String, String> paramsMap)
                                                                                                 throws PayFrontException {
        resolveFieldsAdd2Map(destDto, paramsMap);
    }

    /**
     * 充值处理
     * @param destDto
     * @param paramsMap
     * @throws PayFrontException 
     */
    public static void addCreateDeposit2Map(CreateDepositDto destDto, Map<String, String> paramsMap)
                                                                                                    throws PayFrontException {
        resolveFieldsAdd2Map(destDto, paramsMap);
    }

    /**
     * 充值查询
     * @param destDto
     * @param paramsMap
     * @throws PayFrontException 
     */
    public static void addQueryDepositDto2Map(QueryDepositDto destDto, Map<String, String> paramsMap)
                                                                                                     throws PayFrontException {
        resolveFieldsAdd2Map(destDto, paramsMap);
    }

    /**
     * 提现处理
     * @param destDto
     * @param paramsMap
     * @throws PayFrontException 
     */
    public static void addCreateWithDrawMap(CreateWithDrawDto destDto, Map<String, String> paramsMap)
                                                                                                     throws PayFrontException {
        resolveFieldsAdd2Map(destDto, paramsMap);
    }

    /**
     * 提现查询
     * @param destDto
     * @param paramsMap
     * @throws PayFrontException 
     */
    public static void addQueryWithDraw2Map(QueryWithDrawDto destDto, Map<String, String> paramsMap)
                                                                                                    throws PayFrontException {
        resolveFieldsAdd2Map(destDto, paramsMap);
    }

    /**
     * 支付查询
     * @param destDto
     * @param paramsMap
     * @throws PayFrontException 
     */
    public static void addQueryPayTrade2Map(QueryPayTradeDto destDto, Map<String, String> paramsMap)
                                                                                                    throws PayFrontException {
        resolveFieldsAdd2Map(destDto, paramsMap);
    }

    /**
     * 退款查询
     * @param destDto
     * @param paramsMap
     * @throws PayFrontException 
     */
    public static void addQueryRefund2Map(QueryRefundDto destDto, Map<String, String> paramsMap)
                                                                                                throws PayFrontException {
        resolveFieldsAdd2Map(destDto, paramsMap);
    }

	/**
	 * 代付完成Field转Map
	 * 
	 * @param destDto
	 * @param paramsMap
	 * @throws PayFrontException
	 */
	public static void addFinishAuthTradeDto2Map(FinishAuthTradeDto destDto, Map<String, String> paramsMap) throws PayFrontException {
		resolveFieldsAdd2Map(destDto, paramsMap);
	}

	/**
	 * 代付撤销Field转Map
	 * 
	 * @param destDto
	 * @param paramsMap
	 * @throws PayFrontException
	 */
	public static void addCancelAuthTradeDto2Map(CancelAuthTradeDto destDto, Map<String, String> paramsMap) throws PayFrontException {
		resolveFieldsAdd2Map(destDto, paramsMap);
	}

    /**
     * 解析该对象的字段域
     * @param destDto 
     * @param paramsMap
     * @throws PayFrontException
     */
    public static void resolveFieldsAdd2Map(Object destDto, Map<String, String> paramsMap)
                                                                                          throws PayFrontException {
        try {
            for (Field field : destDto.getClass().getDeclaredFields()) {
                if (!Modifier.isFinal(field.getModifiers())) {
                    String fieldName = field.getName();
                    Object fieldVal = PropertyUtils.getProperty(destDto, fieldName);
                    // 解析域的类型
                    resolveEntityType(field, fieldName, fieldVal, paramsMap);
                    resolveMapType(field, fieldName, fieldVal, paramsMap);
                    resolveListArgsType(field, fieldName, fieldVal, paramsMap);
                    resolvePayArgsType(field, fieldName, fieldVal, paramsMap);
                    resoloveSuperClassType(destDto, paramsMap);
                }
            }
        } catch (Exception e) {
            throw new PayFrontException(e);
        }
    }

    /**当前类父类操作
     * @param obj
     * @param paramsMap
     * @throws PayFrontException
     */
    private static void resoloveSuperClassType(Object obj, Map<String, String> paramsMap)
                                                                                         throws PayFrontException {
        try {
            // 父类Field值拷贝
            Field[] superDeclaredFields = obj.getClass().getSuperclass().getDeclaredFields();
            for (Field field : superDeclaredFields) {
                if (!Modifier.isFinal(field.getModifiers())) {
                    String fieldName = field.getName();
					if (field.getAnnotation(Aliases.class) != null && field.getAnnotation(Aliases.class).value().equals("_input_charset")) {
						continue;
					}
                    Object fieldVal = PropertyUtils.getProperty(obj, fieldName);
                    resolveEntityType(field, fieldName, fieldVal, paramsMap);
                }
            }
        } catch (Exception e) {
            throw new PayFrontException(e);
        }
    }

    /**
     * Map数据格式转化
     * @param extParamMap
     * @return
     */
    private static String map2FormatStyle(Map<String, String> extParamMap) {
        List<String> keyJoinValList = Lists.newArrayList();
        if (extParamMap != null && !extParamMap.isEmpty()) {
            for (Map.Entry<String, String> entry : extParamMap.entrySet()) {
                StringBuilder sb = new StringBuilder();
                sb.append(CastStringUtil.underLineName(entry.getKey()));
                sb.append(Constants.ANGLE_BRACKETS);
                sb.append(entry.getValue());
                keyJoinValList.add(sb.toString());
            }
            return StringUtils.join(keyJoinValList, Constants.VERTICAL_LINE);
        }
        return StringUtils.EMPTY;
    }

    /**
     * field类型为List
     * @param field
     * @param fieldName
     * @param fieldVal
     * @param paramsMap
     * @throws PayFrontException
     */
    @SuppressWarnings("unchecked")
    private static void resolveListArgsType(Field field, String fieldName, Object fieldVal,
                                            Map<String, String> paramsMap) throws PayFrontException {

        // 校验必填Field
        verifyFieldNotnull(field, fieldName, fieldVal);
        // 非必填Field为空
        if (fieldVal == null)
            return;

        // 别名
        String aliasName = aliasName(field, fieldName);

        Class<? extends Object> fieldType = fieldVal.getClass();
        if (List.class.isAssignableFrom(fieldType)) {
            // Field的泛型类型
            Type type = field.getGenericType();
            ParameterizedType ptype = (ParameterizedType) type;
            Class<?> clazz = (Class<?>) ptype.getActualTypeArguments()[0];
            // List上的泛型类型为String
            if (clazz.isAssignableFrom(String.class))
                paramsMap.put(aliasName,
                    StringUtils.join((List<String>) fieldVal, Constants.ANGLE_BRACKETS));
            // List中的泛型类型为SplitArgs
            else if (SplitArgs.class.isAssignableFrom(clazz))
                paramsMap.put(aliasName, splitArgsListJoin((List<SplitArgs>) fieldVal));
            // List中的泛型类型为TradeArgs
            else if (TradeArgs.class.isAssignableFrom(clazz))
                paramsMap.put(aliasName, tradeArgsListJoin((List<TradeArgs>) fieldVal));
			// List中的泛型类型为FinishAuthTradeArgs
			else if (FinishAuthTradeArgs.class.isAssignableFrom(clazz))
				paramsMap.put(aliasName, finishAuthTradeArgsListJoin((List<FinishAuthTradeArgs>) fieldVal));
			// List中的泛型类型为CancelAuthTradeArgs
			else if (CancelAuthTradeArgs.class.isAssignableFrom(clazz))
				paramsMap.put(aliasName, cancelAuthTradeArgsListJoin((List<CancelAuthTradeArgs>) fieldVal));
        }
    }

    /**
     * field类型为支付方式
     * @param field
     * @param fieldName
     * @param fieldVal
     * @param paramsMap
     * @throws PayFrontException
     */
    private static void resolvePayArgsType(Field field, String fieldName, Object fieldVal,
                                           Map<String, String> paramsMap) throws PayFrontException {

        // 校验必填Field
        verifyFieldNotnull(field, fieldName, fieldVal);
        // 非必填Field为空
        if (fieldVal == null)
            return;

        // 别名
        String aliasName = aliasName(field, fieldName);

        try {
            Class<? extends Object> fieldType = fieldVal.getClass();
            if (PayArgsBase.class.isAssignableFrom(fieldType)) {
                // 支付方式
                Enum<?> payMethod = (Enum<?>) MethodUtils.invokeMethod(fieldVal, "getPayMethod",
                    null);
                // 支付金额
                Money amount = (Money) checkNotnull(PropertyUtils.getProperty(fieldVal, "amount"),
                    "amount");

                // 具体支付类型中的各个Field的值
                LinkedList<String> innnerFieldValList = Lists.newLinkedList();
                Field[] innerFields = fieldType.getDeclaredFields();
                for (Field innerField : innerFields) {
                    String innerFieldName = innerField.getName();
                    Object innerFieldVal = PropertyUtils.getProperty(fieldVal, innerFieldName);

                    // 校验必填Field
                    verifyFieldNotnull(innerField, innerFieldName, innerFieldVal);

                    // field为基本类型或者枚举
                    Class<?> innerFieldType = innerField.getType();
                    if (innerFieldType.isPrimitive()
                        || innerFieldType.isAssignableFrom(String.class)) {
                        innnerFieldValList.add((innerFieldVal == null) ? StringUtils.EMPTY : String
                            .valueOf(innerFieldVal));
                    } else if (Enum.class.isAssignableFrom(innerFieldType)) {
                        innnerFieldValList.add((innerFieldVal == null) ? StringUtils.EMPTY
                            : ((Enum<?>) innerFieldVal).name());
                    }
                }

                // 将支付方式具体内容拼装成为类似(支付方式^金额^扩展)
                StringBuilder payMethodBuilder = new StringBuilder(payMethod.name().toLowerCase())
                    .append(Constants.ANGLE_BRACKETS).append(amount.toString())
                    .append(Constants.ANGLE_BRACKETS)
                    .append(StringUtils.join(innnerFieldValList, Constants.COMMA));
                paramsMap.put(aliasName, payMethodBuilder.toString());
            }
        } catch (Exception e) {
            throw new PayFrontException(e);
        }
    }

    /**
     * 检验对象内容不能为空
     * @param obj
     * @param fieldName
     * @return
     * @throws PayFrontException
     */
    private static Object checkNotnull(Object obj, String fieldName) throws PayFrontException {
        if (obj == null)
            throw new PayFrontException(fieldName + "不能为空");

        return obj;
    }

    /**
     * field为基本对象类型
     * @param field
     * @param fieldName
     * @param fieldVal
     * @param paramsMap
     * @throws PayFrontException 
     */
    private static void resolveEntityType(Field field, String fieldName, Object fieldVal,
                                          Map<String, String> paramsMap) throws PayFrontException {
        // 校验必填Field
        verifyFieldNotnull(field, fieldName, fieldVal);
        // 非必填Field为空，直接返回
        if (fieldVal == null)
            return;

        // 别名
        String aliasName = aliasName(field, fieldName);

        // 不同Field类型处理
        Class<? extends Object> fieldType = fieldVal.getClass();

        // field为基本类型或者String
        if (fieldType.isPrimitive() || fieldType.isAssignableFrom(String.class)
            || Integer.class.isAssignableFrom(fieldType)) {
            String fieldValStr = String.valueOf(fieldVal);
            paramsMap.put(aliasName, fieldValStr);
        }
        // field为基本对象，如Money
        else if (Money.class.isAssignableFrom(fieldType)) {
            Money money = (Money) fieldVal;
            paramsMap.put(aliasName, money.toString());
        }
        // field为枚举
        else if (Enum.class.isAssignableFrom(fieldType)) {
            // TradeCode枚举
            if (TradeCode.class.isAssignableFrom(fieldType)) {
                TradeCode tradeCode = (TradeCode) fieldVal;
                paramsMap.put(aliasName, tradeCode.bizCode());
            } else {
                Enum<?> fieldEnumVal = (Enum<?>) fieldVal;
                paramsMap.put(aliasName, fieldEnumVal.name());
            }
        }
    }

    /**
     * 当前field是否有别名
     * <p>有别名的话就用别名，没有别名将驼峰转为下划线</p>
     * @param field
     * @param fieldName
     * @return
     */
    private static String aliasName(Field field, String fieldName) {
        // 有别名使用别名否则转为下划线
        Aliases aliasesAnno = field.getAnnotation(Aliases.class);
        if (aliasesAnno != null)
            fieldName = aliasesAnno.value();
        else
            fieldName = CastStringUtil.underLineName(fieldName);

        return fieldName;
    }

    /**
     * 对象的所有值加入和
     * @param obj
     * @param paramList
     * @throws PayFrontException
     */
    @SuppressWarnings("unchecked")
    private static List<String> resolveObjectAllFields2List(Object obj) throws PayFrontException {
        ArrayList<String> allFieldValList = Lists.newArrayList();
        try {
            // 依据Field类型进行值拷贝
            for (Field field : obj.getClass().getDeclaredFields()) {
                String fieldName = field.getName();
                Class<?> fieldType = field.getType();

                Object fieldVal = PropertyUtils.getProperty(obj, fieldName);

                // 基本类型
                if (fieldType.isPrimitive() || fieldType.isAssignableFrom(String.class)) {
                    allFieldValList.add((fieldVal == null) ? StringUtils.EMPTY : String
                        .valueOf(fieldVal));
                }
                // 枚举类型
                else if (Enum.class.isAssignableFrom(fieldType)) {
                    allFieldValList.add((fieldVal == null) ? StringUtils.EMPTY
                        : ((Enum<?>) fieldVal).name());
                }
                // Map类型
                else if (Map.class.isAssignableFrom(fieldType)) {
                    allFieldValList.add((fieldVal == null) ? StringUtils.EMPTY
                        : map2FormatStyle((Map<String, String>) fieldVal));
                }
                // List类型
                else if (List.class.isAssignableFrom(fieldType)) {
                    // 当前传入obj为TradeArgs类型
                    if (obj instanceof TradeArgs) {
                        TradeArgs tradeArgsObj = (TradeArgs) obj;
                        // List泛型为SplitArgs类型
                        Type type = field.getGenericType();
                        ParameterizedType ptype = (ParameterizedType) type;
                        Class<?> clazz = (Class<?>) ptype.getActualTypeArguments()[0];
                        if (SplitArgs.class.isAssignableFrom(clazz)) {
                            allFieldValList.add((CollectionUtils.isEmpty(tradeArgsObj
                                .getSplitArgs())) ? StringUtils.EMPTY
                                : splitArgsListJoin(tradeArgsObj.getSplitArgs()));
                        }
                    }
                }
                // Money对象
                else if (Money.class.isAssignableFrom(fieldType)) {
                    Money amount = (Money) fieldVal;
					allFieldValList.add(amount.toString());
                }
            }
        } catch (Exception e) {
            throw new PayFrontException(e);
        }
        return allFieldValList;
    }

    /**
     * field为Map类型
     * @param field
     * @param fieldName
     * @param fieldVal
     * @param paramsMap
     * @throws PayFrontException 
     */
    @SuppressWarnings("unchecked")
    private static void resolveMapType(Field field, String fieldName, Object fieldVal,
                                       Map<String, String> paramsMap) throws PayFrontException {
        // 校验必填Field
        verifyFieldNotnull(field, fieldName, fieldVal);
        // 非必填Field为空
        if (fieldVal == null)
            return;

        // 别名
        String aliasName = aliasName(field, fieldName);

        Class<? extends Object> fieldType = fieldVal.getClass();
        if (Map.class.isAssignableFrom(fieldType)) {
            Map<String, String> fieldValMap = (Map<String, String>) fieldVal;
            if (fieldValMap != null && !fieldValMap.isEmpty()) {
                HashMap<String, String> entryValNotnullMap = Maps.newHashMap();

                for (Map.Entry<String, String> entry : fieldValMap.entrySet()) {
                    if (StringUtils.isBlank(entry.getValue()))
                        continue;
                    entryValNotnullMap.put(CastStringUtil.underLineName(entry.getKey()),
                        entry.getValue());
                }
                if (entryValNotnullMap != null && !entryValNotnullMap.isEmpty())
                    paramsMap.put(aliasName, map2FormatStyle(entryValNotnullMap));
            }
        }
    }

    /**
     * 校验字段非空
     * @param field
     * @param fieldName
     * @param fieldVal
     * @throws PayFrontException
     */
    private static void verifyFieldNotnull(Field field, String fieldName, Object fieldVal)
                                                                                          throws PayFrontException {
        NotNull notnullAnno = field.getAnnotation(NotNull.class);
        if (notnullAnno != null && fieldVal == null)
            throw new PayFrontException(String.format("%s不能为空", fieldName));
    }

    /**
     * 按指定格式组装splitArgsList
     * @param splitArgsList
     * @return
     * @throws PayFrontException
     */
    private static String splitArgsListJoin(List<SplitArgs> splitArgsList) throws PayFrontException {
        if (CollectionUtils.isEmpty(splitArgsList))
            return StringUtils.EMPTY;

        List<String> argsJoin = Lists.newArrayList();
        for (SplitArgs splitArgs : splitArgsList) {
            argsJoin.add(StringUtils.join(resolveObjectAllFields2List(splitArgs),
                Constants.ANGLE_BRACKETS));
        }
        return StringUtils.join(argsJoin, Constants.VERTICAL_LINE);
    }

    /**
     * 按指定格式组装tradeArgsList
     * @param tradeArgsList
     * @return
     * @throws PayFrontException
     */
    private static String tradeArgsListJoin(List<TradeArgs> tradeArgsList) throws PayFrontException {
        if (CollectionUtils.isEmpty(tradeArgsList))
            return StringUtils.EMPTY;

        List<String> tradeArgsParamsList = Lists.newArrayList();
        for (TradeArgs tradeArgs : tradeArgsList) {
            List<String> curTradeArgsFieldsList = resolveObjectAllFields2List(tradeArgs);
            if (!CollectionUtils.isEmpty(curTradeArgsFieldsList))
                tradeArgsParamsList.add(StringUtils.join(curTradeArgsFieldsList,
                    Constants.WAVY_LINES));
        }
        return StringUtils.join(tradeArgsParamsList, Constants.DOLLAR);
    }

	/**
	 * 按指定格式组装FinishAuthTradeArgsList
	 * 
	 * @param tradeArgsList
	 * @return
	 * @throws PayFrontException
	 */
	private static String finishAuthTradeArgsListJoin(List<FinishAuthTradeArgs> tradeArgsList) throws PayFrontException {
		if (CollectionUtils.isEmpty(tradeArgsList))
			return StringUtils.EMPTY;

		List<String> tradeArgsParamsList = Lists.newArrayList();
		for (FinishAuthTradeArgs args : tradeArgsList) {
			List<String> curTradeArgsFieldsList = resolveObjectAllFields2List(args);
			if (!CollectionUtils.isEmpty(curTradeArgsFieldsList))
				tradeArgsParamsList.add(StringUtils.join(curTradeArgsFieldsList, Constants.WAVY_LINES));
		}
		return StringUtils.join(tradeArgsParamsList, Constants.DOLLAR);
	}

	/**
	 * 按指定格式组装CancelAuthTradeArgsList
	 * 
	 * @param tradeArgsList
	 * @return
	 * @throws PayFrontException
	 */
	private static String cancelAuthTradeArgsListJoin(List<CancelAuthTradeArgs> tradeArgsList) throws PayFrontException {
		if (CollectionUtils.isEmpty(tradeArgsList))
			return StringUtils.EMPTY;

		List<String> tradeArgsParamsList = Lists.newArrayList();
		for (CancelAuthTradeArgs args : tradeArgsList) {
			List<String> curTradeArgsFieldsList = resolveObjectAllFields2List(args);
			if (!CollectionUtils.isEmpty(curTradeArgsFieldsList))
				tradeArgsParamsList.add(StringUtils.join(curTradeArgsFieldsList, Constants.WAVY_LINES));
		}
		return StringUtils.join(tradeArgsParamsList, Constants.DOLLAR);
	}

	/**
	 * field为Map类型(修复驼峰格式转化问题)
	 * 
	 * @param field
	 * @param fieldName
	 * @param fieldVal
	 * @param paramsMap
	 * @throws PayFrontException
	 */
	@SuppressWarnings("unchecked")
	private static void resolveMapTypeBeta(Field field, String fieldName, Object fieldVal, Map<String, String> paramsMap)
			throws PayFrontException {
		// 校验必填Field
		verifyFieldNotnull(field, fieldName, fieldVal);
		// 非必填Field为空
		if (fieldVal == null)
			return;

		// 别名
		String aliasName = aliasName(field, fieldName);

		Class<? extends Object> fieldType = fieldVal.getClass();
		if (Map.class.isAssignableFrom(fieldType)) {
			Map<String, String> fieldValMap = (Map<String, String>) fieldVal;
			if (fieldValMap != null && !fieldValMap.isEmpty()) {
				HashMap<String, String> entryValNotnullMap = Maps.newHashMap();

				for (Map.Entry<String, String> entry : fieldValMap.entrySet()) {
					if (StringUtils.isBlank(entry.getValue()))
						continue;
					entryValNotnullMap.put(entry.getKey(), entry.getValue());
				}
				if (entryValNotnullMap != null && !entryValNotnullMap.isEmpty())
					paramsMap.put(aliasName, map2FormatStyle(entryValNotnullMap));
			}
		}
	}
}
