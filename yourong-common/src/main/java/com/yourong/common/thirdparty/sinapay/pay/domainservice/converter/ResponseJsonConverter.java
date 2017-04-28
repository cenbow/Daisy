package com.yourong.common.thirdparty.sinapay.pay.domainservice.converter;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yourong.common.enums.SinaPayEnum;
import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.CharsetType;
import com.yourong.common.thirdparty.sinapay.common.enums.PayStatus;
import com.yourong.common.thirdparty.sinapay.common.enums.RefundStatus;
import com.yourong.common.thirdparty.sinapay.common.enums.ResponseCodeEnum;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.thirdparty.sinapay.common.util.GsonUtil;
import com.yourong.common.thirdparty.sinapay.common.util.ResponseUtil;
import com.yourong.common.thirdparty.sinapay.pay.core.common.Constants;
import com.yourong.common.thirdparty.sinapay.pay.core.common.ReflectTool;
import com.yourong.common.thirdparty.sinapay.pay.domain.respone.CollectTradeResponse;
import com.yourong.common.thirdparty.sinapay.pay.domain.respone.CreateDepositResponse;
import com.yourong.common.thirdparty.sinapay.pay.domain.respone.CreateSingleTradeResponse;
import com.yourong.common.thirdparty.sinapay.pay.domain.respone.CreateWithDrawResponse;
import com.yourong.common.thirdparty.sinapay.pay.domain.respone.PayResponse;
import com.yourong.common.thirdparty.sinapay.pay.domain.respone.PayTradeResponse;
import com.yourong.common.thirdparty.sinapay.pay.domain.respone.QueryDepositResponse;
import com.yourong.common.thirdparty.sinapay.pay.domain.respone.QueryFinResponse;
import com.yourong.common.thirdparty.sinapay.pay.domain.respone.QueryPayTradeResponse;
import com.yourong.common.thirdparty.sinapay.pay.domain.respone.QueryRefundResponse;
import com.yourong.common.thirdparty.sinapay.pay.domain.respone.QueryTradeResponse;
import com.yourong.common.thirdparty.sinapay.pay.domain.respone.QueryWithDrawResponse;
import com.yourong.common.thirdparty.sinapay.pay.domain.respone.RefundTradeResponse;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.CreateCollectTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.CreateSinglePayTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.PayResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryDepositResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryPayTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryRefundResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryWithDrawResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.RefundTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.TradeItem;
import com.yourong.common.thirdparty.sinapay.pay.exceptions.PayFrontException;

/**
 * <p>同步响应结果对象转换</p>
 * @author Wallis Wang
 * @version $Id: ResponseConverter.java, v 0.1 2014年5月16日 下午4:09:18 wangqiang Exp $
 */
public class ResponseJsonConverter {

    private static final int TRADE_REFUND_TRADEITEM_LENGTH   = 6;

    private static final int CREATE_DEPOSIT_TRADEITEM_LENGTH = 5;

    /**
     * 代收请求JSON对象转换成结果对象
     * @param jsonResponse
     * @param resultDto
     * @param md5Key 
     * @throws PayFrontException 
     */
    public static void convert2CollectTradeResult(String jsonResponse, CharsetType charset,
                                                  ResultDto<CreateCollectTradeResult> resultDto,
                                                  String md5Key) throws PayFrontException {
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonResponse), "请求钱包无响应结果");

        // 结果包装
        CollectTradeResponse jsonMappingResponse = GsonUtil.fromJsonUnderScoreStyle(jsonResponse,
            CollectTradeResponse.class);
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonMappingResponse.getResponseCode()),
            "支付网关响应结果无response_code");

        // 订单号不存在，就当处理资金网关失败
        if (StringUtils.isBlank(jsonMappingResponse.getOutTradeNo())
            || StringUtils.isBlank(jsonMappingResponse.getPayStatus())) {
            resultDto.setErrorCode(jsonMappingResponse.getResponseCode());
            resultDto.setErrorMsg(jsonMappingResponse.getResponseMessage());
            resultDto.setMemo(jsonMappingResponse.getMemo());
            return;
        }
        // 验证签名
        ResponseUtil.verifyResponseSignbyRSA(jsonResponse, charset, md5Key);
        // 响应结果
        CreateCollectTradeResult result = new CreateCollectTradeResult();
        result.setTradeNo(jsonMappingResponse.getOutTradeNo());
        result.setTradeStatus(TradeStatus.valueOf(jsonMappingResponse.getTradeStatus()));
        if (StringUtils.isNotBlank(jsonMappingResponse.getPayStatus()))
            result.setPayStatus(PayStatus.valueOf(jsonMappingResponse.getPayStatus()));
        resultDto.setModule(result);
        resultDto.setMemo(jsonMappingResponse.getMemo());
        resultDto.setSuccess(true);
    }

    /**
     * 单笔代付请求JSON对象转换成结果对象
     * @param jsonResponse
     * @param resultDto
     * @param md5Key 
     * @throws PayFrontException 
     */
    public static void convert2CreateSinglePayTrade(String jsonResponse,
                                                    CharsetType charset,
                                                    ResultDto<CreateSinglePayTradeResult> resultDto,
                                                    String md5Key) throws PayFrontException {
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonResponse), "请求钱包无响应结果");

        CreateSingleTradeResponse jsonMappingResponse = GsonUtil.fromJsonUnderScoreStyle(
            jsonResponse, CreateSingleTradeResponse.class);
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonMappingResponse.getResponseCode()),
            "支付网关响应结果无response_code");

        // 订单号不存在，就当处理资金网关失败
        if (StringUtils.isBlank(jsonMappingResponse.getOutTradeNo())
            || StringUtils.isBlank(jsonMappingResponse.getTradeStatus())) {
            resultDto.setErrorCode(jsonMappingResponse.getResponseCode());
            resultDto.setErrorMsg(jsonMappingResponse.getResponseMessage());
            resultDto.setMemo(jsonMappingResponse.getMemo());
            return;
        }
        // 验证签名
        ResponseUtil.verifyResponseSignbyRSA(jsonResponse, charset, md5Key);
        // 响应结果
        CreateSinglePayTradeResult result = new CreateSinglePayTradeResult();
        result.setTradeNo(jsonMappingResponse.getOutTradeNo());
        result.setTradeStatus(TradeStatus.valueOf(jsonMappingResponse.getTradeStatus()));
        resultDto.setModule(result);
        resultDto.setMemo(jsonMappingResponse.getMemo());
        resultDto.setSuccess(true);
    }

    /**
     * 批量代付
     * @param jsonResponse
     * @param resultDto
     * @param md5Key 
     * @throws PayFrontException 
     */
    @SuppressWarnings("all")
    public static void convert2CreateBatchPayTrade(String jsonResponse, CharsetType charset,
                                                   ResultDto resultDto, String md5Key)
                                                                                      throws PayFrontException {
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonResponse), "请求钱包无响应结果");

        // 结果包装
        PayResponse jsonMappingResponse = GsonUtil.fromJsonUnderScoreStyle(jsonResponse,
            PayResponse.class);

        // 响应结果为失败
        if (checkResponseFailure(jsonMappingResponse, jsonResponse, charset, resultDto, md5Key))
            return;

        // resultDto
        resultDto.setMemo(jsonMappingResponse.getMemo());
        resultDto.setSuccess(evaluateResponse(jsonMappingResponse.getResponseCode()));
    }

    /**
     * 支付请求JSON对象转换成结果对象
     * @param jsonResponse
     * @param resultDto
     * @param md5Key 
     * @throws PayFrontException 
     */
    public static void convert2PayTradeResult(String jsonResponse, CharsetType charset,
                                              ResultDto<PayResult> resultDto, String md5Key)
                                                                                            throws PayFrontException {
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonResponse), "请求钱包无响应结果");

        // 结果包装
        PayTradeResponse jsonMappingResponse = GsonUtil.fromJsonUnderScoreStyle(jsonResponse,
            PayTradeResponse.class);
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonMappingResponse.getResponseCode()),
            "支付网关响应结果无response_code");

        // 订单号不存在，就当处理资金网关失败
        if (StringUtils.isBlank(jsonMappingResponse.getOutPayNo())
            || StringUtils.isBlank(jsonMappingResponse.getPayStatus())) {
            resultDto.setErrorCode(jsonMappingResponse.getResponseCode());
            resultDto.setErrorMsg(jsonMappingResponse.getResponseMessage());
            resultDto.setMemo(jsonMappingResponse.getMemo());
            return;
        }
        // 验证签名
        ResponseUtil.verifyResponseSignbyRSA(jsonResponse, charset, md5Key);
        // 响应结果
        PayResult result = new PayResult();
        result.setTradeNo(jsonMappingResponse.getOutPayNo());
        result.setPayStatus(PayStatus.valueOf(jsonMappingResponse.getPayStatus()));
        // resultDto
        resultDto.setModule(result);
        resultDto.setMemo(jsonMappingResponse.getMemo());
        resultDto.setSuccess(true);
    }

    /**
     * 网银请求JSON对象转换成结果对象
     * @param jsonResponse
     * @param resultDto
     * @param md5Key 
     * @throws PayFrontException 
     */
    public static void convert2BankFormResult(String jsonResponse, CharsetType charset,
                                              ResultDto<String> resultDto, String md5Key)
                                                                                         throws PayFrontException {
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonResponse), "请求钱包无响应结果");

        //响应结果是一个表单
        Elements formElements = Jsoup.parse(jsonResponse).getElementsByTag("form");
        if (formElements == null || formElements.isEmpty()) {
            PayResponse jsonMappingResponse = GsonUtil.fromJsonUnderScoreStyle(jsonResponse,
                PayResponse.class);
            if (checkResponseFailure(jsonMappingResponse, jsonResponse, charset, resultDto, md5Key))
                return;
        }
        resultDto.setModule(jsonResponse);
        resultDto.setSuccess(true);
    }

    /**
     * 退款请求JSON对象转换成结果对象
     * @param jsonResponse
     * @param resultDto
     * @param md5Key 
     * @throws PayFrontException 
     */
    public static void convert2RefundTradeResult(String jsonResponse, CharsetType charset,
                                                 ResultDto<RefundTradeResult> resultDto,
                                                 String md5Key) throws PayFrontException {
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonResponse), "请求钱包无响应结果");

        // 结果包装
        RefundTradeResponse jsonMappingResponse = GsonUtil.fromJsonUnderScoreStyle(jsonResponse,
            RefundTradeResponse.class);
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonMappingResponse.getResponseCode()),
            "支付网关响应结果无response_code");

        // 订单号不存在，就当处理资金网关失败
        if (StringUtils.isBlank(jsonMappingResponse.getOutTradeNo())
            || StringUtils.isBlank(jsonMappingResponse.getRefundStatus())) {
            resultDto.setErrorCode(jsonMappingResponse.getResponseCode());
            resultDto.setErrorMsg(jsonMappingResponse.getResponseMessage());
            resultDto.setMemo(jsonMappingResponse.getMemo());
            return;
        }
        // 验证签名
        ResponseUtil.verifyResponseSignbyRSA(jsonResponse, charset, md5Key);
        // 响应结果
        RefundTradeResult result = new RefundTradeResult();
        result.setOutTradeNo(jsonMappingResponse.getOutTradeNo());
        result.setRefundStatus(RefundStatus.valueOf(jsonMappingResponse.getRefundStatus()));
        resultDto.setModule(result);
        resultDto.setMemo(jsonMappingResponse.getMemo());
        resultDto.setSuccess(true);
    }

    /**
     * 充值请求JSON对象转换成结果对象
     * @param jsonResponse
     * @param resultDto
     * @param md5Key 
     * @throws PayFrontException 
     */
    public static void convert2CreateDepositResult(String jsonResponse, CharsetType charset,
                                                   ResultDto<PayResult> resultDto, String md5Key)
                                                                                                 throws PayFrontException {
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonResponse), "请求钱包无响应结果");

        // 结果包装
        CreateDepositResponse jsonMappingResponse = GsonUtil.fromJsonUnderScoreStyle(jsonResponse,
            CreateDepositResponse.class);
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonMappingResponse.getResponseCode()),
            "支付网关响应结果无response_code");

        // 订单号不存在，就当处理资金网关失败
        if (StringUtils.isBlank(jsonMappingResponse.getOutTradeNo())
            || StringUtils.isBlank(jsonMappingResponse.getDepositStatus())) {
            resultDto.setErrorCode(jsonMappingResponse.getResponseCode());
            resultDto.setErrorMsg(jsonMappingResponse.getResponseMessage());
            resultDto.setMemo(jsonMappingResponse.getMemo());
            return;
        }
        // 验证签名
        ResponseUtil.verifyResponseSignbyRSA(jsonResponse, charset, md5Key);
        // 响应结果
        PayResult result = new PayResult();       ;
        result.setTradeNo(jsonMappingResponse.getOutTradeNo());
        result.setPayStatus(PayStatus.valueOf(jsonMappingResponse.getDepositStatus()));
        result.setTicket(jsonMappingResponse.getTicket());
        resultDto.setModule(result);
        resultDto.setMemo(jsonMappingResponse.getMemo());
        resultDto.setSuccess(true);
    }

    /**
     * 提现请求JSON对象转换成结果对象
     * @param jsonResponse
     * @param resultDto
     * @param md5Key 
     * @throws PayFrontException 
     */
    public static void convert2CreateWithDrawResult(String jsonResponse, CharsetType charset,
                                                    ResultDto<PayResult> resultDto, String md5Key)
                                                                                                  throws PayFrontException {
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonResponse), "请求钱包无响应结果");

        // 结果包装
        CreateWithDrawResponse jsonMappingResponse = GsonUtil.fromJsonUnderScoreStyle(jsonResponse,
            CreateWithDrawResponse.class);
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonMappingResponse.getResponseCode()),
            "支付网关响应结果无response_code");

        // 订单号不存在，就当处理资金网关失败
        if (StringUtils.isBlank(jsonMappingResponse.getOutTradeNo())
            || StringUtils.isBlank(jsonMappingResponse.getWithdrawStatus())) {
            resultDto.setErrorCode(jsonMappingResponse.getResponseCode());
            resultDto.setErrorMsg(jsonMappingResponse.getResponseMessage());
            resultDto.setMemo(jsonMappingResponse.getMemo());
            return;
        }
        // 验证签名
        ResponseUtil.verifyResponseSignbyRSA(jsonResponse, charset, md5Key);
        // 响应结果
        PayResult result = new PayResult();
        result.setTradeNo(jsonMappingResponse.getOutTradeNo());
        PayStatus payStatus = PayStatus.valueOf(jsonMappingResponse.getWithdrawStatus());
        result.setPayStatus(payStatus);
        resultDto.setModule(result);
        resultDto.setMemo(jsonMappingResponse.getMemo());
        resultDto.setSuccess(true);
        
        if(payStatus == PayStatus.FAILED){
        	resultDto.setErrorCode(jsonMappingResponse.getResponseCode());
            resultDto.setErrorMsg(jsonMappingResponse.getResponseMessage());
            resultDto.setMemo(jsonMappingResponse.getMemo());
            resultDto.setSuccess(false);
        }
    
    }

    /**
     * 
     * 响应校验
     * <p>响应为成功先校验响应的字段，然后再对响应结果验证签名，失败的话两者都不验证，直接返回</p>
     * @param jsonMappingResponse
     * @param jsonResponse
     * @param charset
     * @param resultDto
     * @param md5Key 
     * @return
     * @throws PayFrontException 
     */
    private static <T> boolean checkResponseFailure(PayResponse jsonMappingResponse,
                                                    String jsonResponse, CharsetType charset,
                                                    ResultDto<T> resultDto, String md5Key)
                                                                                          throws PayFrontException {
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonMappingResponse.getResponseCode()),
            "支付网关响应结果无response_code");
        try {
            if (evaluateResponse(jsonMappingResponse.getResponseCode())) {
                ReflectTool.checkFieldNotnull(jsonMappingResponse);
                ResponseUtil.verifyResponseSignbyRSA(jsonResponse, charset, md5Key);
                return false;
            }
            resultDto.setErrorCode(jsonMappingResponse.getResponseCode());
            resultDto.setErrorMsg(jsonMappingResponse.getResponseMessage());
            resultDto.setMemo(jsonMappingResponse.getMemo());
            return true;
        } catch (Exception e) {
            throw new PayFrontException(e);
        }
    }

    /**
     * 提现查询请求JSON对象转换成结果对象
     * @param jsonResponse
     * @param resultDto
     * @param md5Key 
     * @throws PayFrontException 
     */
    public static void convert2QueryWithDrawResult(String jsonResponse, CharsetType charset,
                                                   ResultDto<QueryWithDrawResult> resultDto,
                                                   String md5Key) throws PayFrontException {
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonResponse), "请求钱包无响应结果");

        // 结果包装
        QueryWithDrawResponse jsonMappingResponse = GsonUtil.fromJsonUnderScoreStyle(jsonResponse,
            QueryWithDrawResponse.class);

        // 响应结果为失败
        if (checkResponseFailure(jsonMappingResponse, jsonResponse, charset, resultDto, md5Key))
            return;

        // result
        QueryWithDrawResult result = new QueryWithDrawResult();
        result.setPageNo(Integer.valueOf(jsonMappingResponse.getPageNo()));
        result.setPageSize(Integer.valueOf(jsonMappingResponse.getPageSize()));
        String tradeList = jsonMappingResponse.getWithdrawList();
        if (StringUtils.isNotBlank(tradeList)) {
            result.setWithDrawList(convert2TradeItemList(tradeList));
        }
        // resultDto
        resultDto.setModule(result);
        resultDto.setMemo(jsonMappingResponse.getMemo());
        resultDto.setTotalCount(Integer.valueOf(jsonMappingResponse.getTotalItem()));
        resultDto.setSuccess(evaluateResponse(jsonMappingResponse.getResponseCode()));
    }

    /**
     * 支付查询请求JSON对象转换成结果对象
     * @param jsonResponse
     * @param resultDto
     * @param md5Key 
     * @throws PayFrontException 
     */
    public static void convert2QueryPayTradeResult(String jsonResponse, CharsetType charset,
                                                   ResultDto<QueryPayTradeResult> resultDto,
                                                   String md5Key) throws PayFrontException {
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonResponse), "请求钱包无响应结果");

        // 结果包装
        QueryPayTradeResponse jsonMappingResponse = GsonUtil.fromJsonUnderScoreStyle(jsonResponse,
            QueryPayTradeResponse.class);

        // 响应结果为失败
        if (checkResponseFailure(jsonMappingResponse, jsonResponse, charset, resultDto, md5Key))
            return;

        // result
        QueryPayTradeResult result = new QueryPayTradeResult();
        result.setOutPayNo(jsonMappingResponse.getOutPayNo());
        result.setPayStatus(PayStatus.valueOf(jsonMappingResponse.getPayStatus()));

        // resultDto
        resultDto.setModule(result);
        resultDto.setMemo(jsonMappingResponse.getMemo());
        resultDto.setSuccess(evaluateResponse(jsonMappingResponse.getResponseCode()));
    }

    /**
     * 退款查询请求JSON对象转换成结果对象
     * @param jsonResponse
     * @param resultDto
     * @param md5Key 
     * @throws PayFrontException 
     */
    public static void convert2QueryRefundResult(String jsonResponse, CharsetType charset,
                                                 ResultDto<QueryRefundResult> resultDto,
                                                 String md5Key) throws PayFrontException {
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonResponse), "请求钱包无响应结果");

        // 结果包装
        QueryRefundResponse jsonMappingResponse = GsonUtil.fromJsonUnderScoreStyle(jsonResponse,
            QueryRefundResponse.class);

        // 响应结果为失败
        if (checkResponseFailure(jsonMappingResponse, jsonResponse, charset, resultDto, md5Key))
            return;

        // result
        QueryRefundResult result = new QueryRefundResult();
        result.setPageNo(Integer.valueOf(jsonMappingResponse.getPageNo()));
        result.setPageSize(Integer.valueOf(jsonMappingResponse.getPageSize()));
        String tradeList = jsonMappingResponse.getTradeList();
        if (StringUtils.isNotBlank(tradeList)) {
            result.setTradeList(convert2TradeItemList(tradeList));
        }
        resultDto.setModule(result);
        resultDto.setMemo(jsonMappingResponse.getMemo());
        resultDto.setSuccess(evaluateResponse(jsonMappingResponse.getResponseCode()));
    }

    /**
     * 交易查询请求JSON对象转换成结果对象
     * @param jsonResponse
     * @param resultDto
     * @param md5Key 
     * @param
     * @throws PayFrontException 
     */
    public static void convert2QueryTradeResult(String jsonResponse, CharsetType charset,
                                                ResultDto<QueryTradeResult> resultDto, String md5Key)
                                                                                                     throws PayFrontException {
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonResponse), "请求钱包无响应结果");

        // 结果包装
        QueryTradeResponse jsonMappingResponse = GsonUtil.fromJsonUnderScoreStyle(jsonResponse,
            QueryTradeResponse.class);

        // 响应结果为失败
        if (checkResponseFailure(jsonMappingResponse, jsonResponse, charset, resultDto, md5Key))
            return;

        // result
        QueryTradeResult result = new QueryTradeResult();
        result.setPageNo(Integer.valueOf(jsonMappingResponse.getPageNo()));
        result.setPageSize(Integer.valueOf(jsonMappingResponse.getPageSize()));
        String tradeList = jsonMappingResponse.getTradeList();
        if (StringUtils.isNotBlank(tradeList)) {
            result.setPayItemList(convert2TradeItemList(tradeList));
        }
        // resultDto
        resultDto.setModule(result);
        resultDto.setMemo(jsonMappingResponse.getMemo());
        resultDto.setTotalCount(Integer.valueOf(jsonMappingResponse.getTotalItem()));
        resultDto.setSuccess(evaluateResponse(jsonMappingResponse.getResponseCode()));
    }

    /**
     * 充值查询JSON对象转换成结果对象
     * @param jsonResponse
     * @param resultDto
     * @param md5Key 
     * @param
     * @throws PayFrontException 
     */
    public static void convert2QueryDepositResult(String jsonResponse, CharsetType charset,
                                                  ResultDto<QueryDepositResult> resultDto,
                                                  String md5Key) throws PayFrontException {
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonResponse), "请求钱包无响应结果");
        // 结果包装
        QueryDepositResponse jsonMappingResponse = GsonUtil.fromJsonUnderScoreStyle(jsonResponse,
            QueryDepositResponse.class);

        // 响应结果为失败
        if (checkResponseFailure(jsonMappingResponse, jsonResponse, charset, resultDto, md5Key))
            return;

        // result
        QueryDepositResult result = new QueryDepositResult();
        result.setPageNo(Integer.valueOf(jsonMappingResponse.getPageNo()));
        result.setPageSize(Integer.valueOf(jsonMappingResponse.getPageSize()));
        String depositList = jsonMappingResponse.getDepositList();
        if (StringUtils.isNotBlank(depositList)) {
            result.setTradeItemList(convert2TradeItemList(depositList));
        }
        // resultDto
        resultDto.setModule(result);
        resultDto.setMemo(jsonMappingResponse.getMemo());
        resultDto.setTotalCount(Integer.valueOf(jsonMappingResponse.getTotalItem()));
        resultDto.setSuccess(evaluateResponse(jsonMappingResponse.getResponseCode()));
    }

    /**
     * 基金代码收益JSON对象转换成结果对象
     * @param jsonResponse
     * @param resultDto
     * @param md5Key 
     * @param
     * @throws PayFrontException 
     */
    public static void convert2QueryFinResponseResult(String jsonResponse, CharsetType charset,
                                                  ResultDto<QueryFinResponse> resultDto,
                                                  String md5Key) throws PayFrontException {
        Preconditions.checkArgument(StringUtils.isNotBlank(jsonResponse), "请求钱包无响应结果");
        // 结果包装
        QueryFinResponse jsonMappingResponse = GsonUtil.fromJsonUnderScoreStyle(jsonResponse,
        		QueryFinResponse.class);

        // 响应结果为失败
        if (checkResponseFailure(jsonMappingResponse, jsonResponse, charset, resultDto, md5Key))
            return;

        // result
//        QueryFinResponse result = new QueryFinResponse();    
//        String depositList = jsonMappingResponse.getDepositList();
//        if (StringUtils.isNotBlank(depositList)) {
//            result.setTradeItemList(convert2TradeItemList(depositList));
//        }
        // resultDto
        resultDto.setModule(jsonMappingResponse);
        resultDto.setMemo(jsonMappingResponse.getMemo()); 
        resultDto.setSuccess(evaluateResponse(jsonMappingResponse.getResponseCode()));
    }
    
    /**
     * 交易条目参数字符串转换成对象 
     * 
     * @param tradeList
     * @return
     */
    public static List<TradeItem> convert2TradeItemList(String tradeList) {
        List<TradeItem> tradeItemList = Lists.newArrayList();

        String[] allTradeItemStr = StringUtils.split(tradeList, Constants.VERTICAL_LINE);
        for (String tradeItemStr : allTradeItemStr) {
            TradeItem tradeItem = new TradeItem();
            String[] tradeItemFields = StringUtils.split(tradeItemStr, Constants.ANGLE_BRACKETS);
            //            Preconditions.checkArgument(
            //                (tradeItemFields.length == TRADE_REFUND_TRADEITEM_LENGTH)
            //                        || (tradeItemFields.length == CREATE_DEPOSIT_TRADEITEM_LENGTH),
            //                "交易条目中字段的个数应为(充值提现[%s])或(交易退款[%s])，实际为[%s]", TRADE_REFUND_TRADEITEM_LENGTH,
            //                CREATE_DEPOSIT_TRADEITEM_LENGTH, tradeItemFields.length);

            if (tradeItemFields.length < CREATE_DEPOSIT_TRADEITEM_LENGTH)
                continue;

            //充值、提现查询
            if (tradeItemFields.length == CREATE_DEPOSIT_TRADEITEM_LENGTH) {
                tradeItem.setTradeNo(tradeItemFields[0]);
                tradeItem.setAmount(new Money(tradeItemFields[1]));
                tradeItem.setProcessStatus(tradeItemFields[2]);
                tradeItem.setGmtCreate(tradeItemFields[3]);
                tradeItem.setGmtModified(tradeItemFields[4]);
            }
            //交易、退款查询
            else if (tradeItemFields.length == TRADE_REFUND_TRADEITEM_LENGTH) {
                tradeItem.setTradeNo(tradeItemFields[0]);
                tradeItem.setRemark(tradeItemFields[1]);
                tradeItem.setAmount(new Money(tradeItemFields[2]));
                tradeItem.setProcessStatus(tradeItemFields[3]);
                tradeItem.setGmtCreate(tradeItemFields[4]);
                tradeItem.setGmtModified(tradeItemFields[5]);
            }

            tradeItemList.add(tradeItem);
        }
        return tradeItemList;
    }
    
    /**
     * 交易条目参数字符串转换成对象 分隔符为~ 和 $
     * 
     * @param tradeList
     * @return
     */
    public static List<TradeItem> convert2TradeItemList2(String tradeList) {
        List<TradeItem> tradeItemList = Lists.newArrayList();

        String[] allTradeItemStr = StringUtils.split(tradeList, Constants.DOLLAR);
        for (String tradeItemStr : allTradeItemStr) {
            TradeItem tradeItem = new TradeItem();
            String[] tradeItemFields = StringUtils.split(tradeItemStr, Constants.WAVY_LINES);
            //            Preconditions.checkArgument(
            //                (tradeItemFields.length == TRADE_REFUND_TRADEITEM_LENGTH)
            //                        || (tradeItemFields.length == CREATE_DEPOSIT_TRADEITEM_LENGTH),
            //                "交易条目中字段的个数应为(充值提现[%s])或(交易退款[%s])，实际为[%s]", TRADE_REFUND_TRADEITEM_LENGTH,
            //                CREATE_DEPOSIT_TRADEITEM_LENGTH, tradeItemFields.length);

            if (tradeItemFields.length < CREATE_DEPOSIT_TRADEITEM_LENGTH)
                continue;

            //充值、提现查询
            if (tradeItemFields.length == CREATE_DEPOSIT_TRADEITEM_LENGTH) {
                tradeItem.setTradeNo(tradeItemFields[0]);
                tradeItem.setAmount(new Money(tradeItemFields[1]));
                tradeItem.setProcessStatus(tradeItemFields[2]);
                tradeItem.setGmtCreate(tradeItemFields[3]);
                tradeItem.setGmtModified(tradeItemFields[4]);
            }
            //交易、退款查询
            else if (tradeItemFields.length == TRADE_REFUND_TRADEITEM_LENGTH) {
                tradeItem.setTradeNo(tradeItemFields[0]);
                tradeItem.setRemark(tradeItemFields[1]);
                tradeItem.setAmount(new Money(tradeItemFields[2]));
                tradeItem.setProcessStatus(tradeItemFields[3]);
                tradeItem.setGmtCreate(tradeItemFields[4]);
                tradeItem.setGmtModified(tradeItemFields[5]);
            }

            tradeItemList.add(tradeItem);
        }
        return tradeItemList;
    }

    private static boolean evaluateResponse(String responseCode) {
        return ResponseCodeEnum.APPLY_SUCCESS.name().equals(responseCode);
    }

	/**
	 * 单笔代收完成/撤销请求JSON对象转换成结果对象
	 * 
	 * @param jsonResponse
	 * @param resultDto
	 * @param md5Key
	 * @throws PayFrontException
	 */
	public static void convert2AuthTrade(String jsonResponse, CharsetType charset, ResultDto<Object> resultDto, String md5Key)
			throws PayFrontException {
		Preconditions.checkArgument(StringUtils.isNotBlank(jsonResponse), "请求钱包无响应结果");

		CreateSingleTradeResponse jsonMappingResponse = GsonUtil.fromJsonUnderScoreStyle(jsonResponse, CreateSingleTradeResponse.class);
		Preconditions.checkArgument(StringUtils.isNotBlank(jsonMappingResponse.getResponseCode()), "支付网关响应结果无response_code");

		// 订单号不存在，就当处理资金网关失败
		if (StringUtils.isBlank(jsonMappingResponse.getResponseCode())
				|| !SinaPayEnum.RESPONSE_APPLY_SUCCESS.getCode().equals(jsonMappingResponse.getResponseCode())) {
			resultDto.setErrorCode(jsonMappingResponse.getResponseCode());
			resultDto.setErrorMsg(jsonMappingResponse.getResponseMessage());
			resultDto.setMemo(jsonMappingResponse.getMemo());
			return;
		}
		// 验证签名
		ResponseUtil.verifyResponseSignbyRSA(jsonResponse, charset, md5Key);
		// 响应结果
		resultDto.setMemo(jsonMappingResponse.getMemo());
		resultDto.setSuccess(true);
	}
}
