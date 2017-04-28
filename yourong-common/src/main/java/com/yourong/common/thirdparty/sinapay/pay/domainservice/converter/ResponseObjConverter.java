package com.yourong.common.thirdparty.sinapay.pay.domainservice.converter;

import java.lang.reflect.Field;
import java.util.TreeMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.yourong.common.thirdparty.sinapay.common.enums.PayStatus;
import com.yourong.common.thirdparty.sinapay.common.enums.RefundStatus;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.thirdparty.sinapay.common.util.RequestUtil;
import com.yourong.common.thirdparty.sinapay.common.util.sign.MD5;
import com.yourong.common.thirdparty.sinapay.pay.core.common.Ignore;
import com.yourong.common.thirdparty.sinapay.pay.core.common.ReflectTool;
import com.yourong.common.thirdparty.sinapay.pay.domain.notify.NotifyCreateDepositResponse;
import com.yourong.common.thirdparty.sinapay.pay.domain.notify.NotifyRefundResponse;
import com.yourong.common.thirdparty.sinapay.pay.domain.notify.NotifyResponse;
import com.yourong.common.thirdparty.sinapay.pay.domain.notify.NotifyTradeResponse;
import com.yourong.common.thirdparty.sinapay.pay.domain.notify.NotifyWithdrawResponse;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.PayFrontResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.PayResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.RefundTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.TradeResult;
import com.yourong.common.thirdparty.sinapay.pay.exceptions.PayFrontException;

/**
 * <p>异步响应结果转换成返回结果</p>
 * <pre>
 *  因为是通知，不论成功还是失败，最终的订单号和最终的状态都会给的
 *  1、最终处理失败，会给失败的状态码和失败的原因,errorCode和errorMessage
 *  2、最终处理成功了，不会存在errorCode和errorMessage
 *  </pre>
 * @author Wallis Wang
 * @version $Id: ResponseObjConverter.java, v 0.1 2014年5月20日 下午1:18:00 wangqiang Exp $
 */
public class ResponseObjConverter {

    /**
     * check 异步响应结果
     * @param response
     * @param md5Key
     * @throws PayFrontException
     */
    private static void checkNotifyResponse(Object response, String md5Key)
                                                                           throws PayFrontException {
        try {
            //字段检验，验证签名
            Preconditions.checkNotNull(response, "通知结果为空");
            ReflectTool.checkFieldNotnull(response);
            verifyResponseSign(response, md5Key);
        } catch (Exception e) {
            throw new PayFrontException(e);
        }
    }

    /**
     * 代收、代付、支付
     * @param response
     * @return
     * @throws Exception 
     */
    public static PayFrontResult<TradeResult> convert2TradeResult(NotifyTradeResponse response,
                                                                  String md5Key)
                                                                                throws PayFrontException {
        checkNotifyResponse(response, md5Key);
        //异步处理结果
        PayFrontResult<TradeResult> destResult = new PayFrontResult<TradeResult>();
        TradeResult bizResult = new TradeResult();
        bizResult.setOutTradeNo(response.getOuterTradeNo());
        bizResult.setTradeStatus(TradeStatus.valueOf(response.getTradeStatus()));
        destResult.setModule(bizResult);
        destResult.setMemo(response.getMemo());
        destResult.setErrorCode(response.getErrorCode());
        destResult.setErrorMsg(response.getErrorMessage());
        return destResult;
    }

    /**
     * 退款
     * @param response
     * @return
     * @throws Exception 
     */
    public static PayFrontResult<RefundTradeResult> convert2RefundTradeResult(NotifyRefundResponse response,
                                                                              String md5Key)
                                                                                            throws PayFrontException {
        checkNotifyResponse(response, md5Key);
        //异步处理结果
        PayFrontResult<RefundTradeResult> destResult = new PayFrontResult<RefundTradeResult>();
        RefundTradeResult bizResult = new RefundTradeResult();
        bizResult.setOutTradeNo(response.getOuterTradeNo());
        bizResult.setRefundStatus(RefundStatus.valueOf(response.getRefundStatus()));
        destResult.setModule(bizResult);
        destResult.setMemo(response.getMemo());
        destResult.setErrorCode(response.getErrorCode());
        destResult.setErrorMsg(response.getErrorMessage());

        return destResult;
    }

    /**
     * 充值
     * @param response
     * @return
     * @throws Exception 
     */
    public static PayFrontResult<PayResult> convert2PayResult(NotifyCreateDepositResponse response,
                                                              String md5Key)
                                                                            throws PayFrontException {
        checkNotifyResponse(response, md5Key);
        //异步处理结果
        PayFrontResult<PayResult> destResult = new PayFrontResult<PayResult>();
        PayResult bizResult = new PayResult();
        bizResult.setTradeNo(response.getOuterTradeNo());
        bizResult.setPayStatus(PayStatus.valueOf(response.getDepositStatus()));
        destResult.setModule(bizResult);
        destResult.setMemo(response.getMemo());
        destResult.setErrorCode(response.getErrorCode());
        destResult.setErrorMsg(response.getErrorMessage());

        return destResult;
    }

    /**
     * 提现
     * @param response
     * @return
     * @throws Exception 
     */
    public static PayFrontResult<PayResult> convert2PayResult(NotifyWithdrawResponse response,
                                                              String md5Key)
                                                                            throws PayFrontException {
        checkNotifyResponse(response, md5Key);
        //异步处理结果 
        PayFrontResult<PayResult> destResult = new PayFrontResult<PayResult>();
        PayResult bizResult = new PayResult();
        bizResult.setTradeNo(response.getOuterTradeNo());
        bizResult.setPayStatus(PayStatus.valueOf(response.getWithdrawStatus()));
        destResult.setModule(bizResult);
        destResult.setMemo(response.getMemo());
        destResult.setErrorCode(response.getErrorCode());
        destResult.setErrorMsg(response.getErrorMessage());

        return destResult;
    }

    /**
     * 执行结果验签
     * <p>去除sign,sign_type,sign_version三个字段</p>
     * @param response
     * @throws Exception
     */
    private static <T> void verifyResponseSign(T response, String md5Key) throws PayFrontException {
        try {
            String needVerifyContent = signVerifyContent(response);
            NotifyResponse payRespone = (NotifyResponse) response;
            boolean verifyResult = MD5.verify(needVerifyContent, payRespone.getSign(), md5Key,
                payRespone.getInputCharset());
            if (!verifyResult)
                throw new PayFrontException("验证签名失败,验证内容为" + needVerifyContent);
        } catch (Exception e) {
            throw new PayFrontException(e);
        }
    }

    /**
     * 获取所有需要验证签名的对象字段
     * @param obj
     * @return
     * @throws Exception
     */
    private static String signVerifyContent(Object obj) throws Exception {
        TreeMap<String, String> resultMap = Maps.newTreeMap();

        Field[] resultFields = getCurSuperDeclaredFields(obj);
        for (Field superField : resultFields) {
            String fieldName = superField.getName();
            String fieldValue = (String) PropertyUtils.getProperty(obj, fieldName);
            // 如果带有Ignore注解，不在验签范围之内
            Ignore ignored = superField.getAnnotation(Ignore.class);
            if (ignored == null && StringUtils.isNotBlank(fieldValue)) {
                // 如果带有SerializedName注解，MAP中的KEY的内容为该注解的value,如_input_charset
                resultMap.put(ReflectTool.aliasName(superField, fieldName), fieldValue);
            }
        }
        if (resultMap != null && !resultMap.isEmpty())
            return RequestUtil.createLinkString(resultMap, false);

        return StringUtils.EMPTY;
    }

    /**
     * 当前类和父类的所有必须验签的字段
     * @param obj
     * @return
     */
    private static Field[] getCurSuperDeclaredFields(Object obj) {
        Field[] curDeclaredFields = obj.getClass().getDeclaredFields();

        // 是否存在基类
        Class<?> superClazz = obj.getClass().getSuperclass();
        if (superClazz != null) {
            Field[] superDeclaredFields = superClazz.getDeclaredFields();

            Field[] resultFields = new Field[curDeclaredFields.length + superDeclaredFields.length];
            System.arraycopy(curDeclaredFields, 0, resultFields, 0, curDeclaredFields.length);
            System.arraycopy(superDeclaredFields, 0, resultFields, curDeclaredFields.length,
                superDeclaredFields.length);
            return resultFields;
        }
        return curDeclaredFields;
    }
}
