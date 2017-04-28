package com.yourong.common.thirdparty.sinapay.pay.ext.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.CharsetType;
import com.yourong.common.thirdparty.sinapay.common.enums.ErrorCode;
import com.yourong.common.thirdparty.sinapay.common.enums.ResponseCodeEnum;
import com.yourong.common.thirdparty.sinapay.common.enums.SignType;
import com.yourong.common.thirdparty.sinapay.common.util.DateUtil;
import com.yourong.common.thirdparty.sinapay.common.util.RequestUtil;
import com.yourong.common.thirdparty.sinapay.pay.core.common.Constants;
import com.yourong.common.thirdparty.sinapay.pay.core.common.RSAExtend;
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
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.PayArgsBase;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.QuickPayArgs;
import com.yourong.common.thirdparty.sinapay.pay.domain.respone.QueryFinResponse;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.CreateCollectTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.CreateSinglePayTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.PayResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryDepositResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryPayTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryRefundResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryWithDrawResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.RefundTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domainservice.converter.RequestConverter;
import com.yourong.common.thirdparty.sinapay.pay.domainservice.converter.ResponseJsonConverter;
import com.yourong.common.thirdparty.sinapay.pay.exceptions.PayFrontException;
import com.yourong.common.thirdparty.sinapay.pay.service.facade.HostingTradeFacade;
public class HostingTradeFacadeImpl implements HostingTradeFacade, InitializingBean {

    private static Logger       logger                         = LoggerFactory
                                                                   .getLogger(HostingTradeFacade.class);

    //文件MD5的KEY路径
    private String              payfrontMd5KeyPath;
    //支付网关的URL
    private String              payfrontGatewayUrl;
    //支付网关默认的签名版本号
    private String              payfrontDefaultSignVersion;
    //支付网关默认的加密版本号
    private String              payfrontDefaultEncyptVersion;
    //支付网关的默认服务版本号
    private String              payfrontDefaultServerVersion;
    //支付网关默认的RSA公私
    private String              payfrontPublicKey;

    //字段加密的公钥
    private  String     encrytPublicKey;

    //RSA公钥
    private String    rsaPublicSiginKey;
    //RSA私钥
    private String   rsaPrivateSiginKey;

    public void setRsaPublicSiginKey(String rsaPublicSiginKey) {
        this.rsaPublicSiginKey = rsaPublicSiginKey;
    }

    public void setRsaPrivateSiginKey(String rsaPrivateSiginKey) {
        this.rsaPrivateSiginKey = rsaPrivateSiginKey;
    }


    //支付网关的商户ID
    private String              payfrontDefaultParterId;

    private String              payfrontMd5Key                 = null;

    private static final String LOG_POINT_COLLECTTRADE         = "代收[collectTrade]";

    private static final String LOG_POINT_CREATESINGLEPAYTRADE = "单笔代付[createSinglePayTrade]";

    private static final String LOG_POINT_CREATEBATCHPAYTRADE  = "批量代付[createBatchPayTrade]";

    private static final String LOG_POINT_PAYTRADE             = "托管支付[payTrade]";

    private static final String LOG_POINT_REFUNDTRADE          = "退款[refundTrade]";

    private static final String LOG_POINT_CREATEDEPOSIT        = "充值[createDeposit]";

    private static final String LOG_POINT_CREATEWITHDRAW       = "提现[createWithDraw]";

	private static final String LOG_POINT_FINISH_PRE_AUTH_TRADE = "代收完成[finishPreAuthTrade]";

	private static final String LOG_POINT_CANCEL_PRE_AUTH_TRADE = "代收撤销[cancelPreAuthTrade]";

	private static final String LOG_POINT_PAY_HOSTING_TRADE = "托管交易支付[payHostingTrade]";

    public String getEncrytPublicKey() {
        return encrytPublicKey;
    }

    public void setEncrytPublicKey(String encrytPublicKey) {
        this.encrytPublicKey = encrytPublicKey;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        FileInputStream md5InputStream = null;
        try {
            if (payfrontMd5Key == null) {
                md5InputStream = new FileInputStream(new File(payfrontMd5KeyPath));
                List<String> readLines = IOUtils.readLines(md5InputStream,
                    CharsetType.UTF8.charset());
                Preconditions.checkArgument(readLines != null && !readLines.isEmpty(),
                    "MD5KEY文件中没有内容");
                payfrontMd5Key = StringUtils.trim(readLines.get(0));
            }
        } catch (FileNotFoundException e) {
            logger.error("从路径{}找不到MD5的KEY文件", payfrontMd5KeyPath);
            throw new PayFrontException("从路径" + payfrontMd5KeyPath + "找不到MD5的KEY文件", e);
        } catch (IOException e) {
            logger.error("从路径{}读取MD5文件异常", payfrontMd5KeyPath);
            throw new PayFrontException("从路径" + payfrontMd5KeyPath + "读取MD5文件异常", e);
        } finally {
            if (md5InputStream != null) {
                IOUtils.closeQuietly(md5InputStream);
            }
        }

    }

    /**
     * 代收
     */
    @Override
    public ResultDto<CreateCollectTradeResult> collectTrade(CreateCollectTradeDto createCollectTradeDto)
                                                                                                        throws PayFrontException,
                                                                                                        HttpException {
        if (logger.isInfoEnabled()) {
            logger.info(LOG_POINT_COLLECTTRADE + "DTO{}",
                ToStringBuilder.reflectionToString(createCollectTradeDto));
        }
        ResultDto<CreateCollectTradeResult> resultDto = new ResultDto<CreateCollectTradeResult>();

        // 请求信息打点
        Map<String, String> logParams = Maps.newHashMap();
        logParams.put("out_trade_no", createCollectTradeDto.getOutTradeNo());
        logParams.put("out_trade_code", createCollectTradeDto.getOutTradeCode().bizCode());

        try {
            // 进行POST请求
            createCollectTradeDto.setPayMethod(signPayArgsByRSA(
                createCollectTradeDto.getPayMethod(), createCollectTradeDto.getInputCharset()));
            Map<String, String> requestMap = Maps.newHashMap();
            RequestConverter.addRequestDto2Map(createCollectTradeDto, requestMap);
            RequestConverter.addCreateCollectTradeDto2Map(createCollectTradeDto, requestMap);
            addBaseParams(requestMap, Constants.CREATE_HOSTING_COLLECT_TRADE);

            if (logger.isInfoEnabled()) {
                logger.info(fillLoggerTemplate(LOG_POINT_COLLECTTRADE, requestMap));
            }
            String jsonResponse = RequestUtil.buildRequest(payfrontGatewayUrl, requestMap,
                    SignType.RSA.name(), rsaPrivateSiginKey, payfrontDefaultSignVersion,
                    createCollectTradeDto.getInputCharset().charset());

            if (logger.isInfoEnabled()) {
                logger.info(LOG_POINT_COLLECTTRADE + "响应为{}", jsonResponse);
            }

            // 请求结果转换到resultDto
            ResponseJsonConverter.convert2CollectTradeResult(jsonResponse,
                createCollectTradeDto.getInputCharset(), resultDto, rsaPublicSiginKey);

            if (logger.isInfoEnabled()) {
                logger.info(fillLoggerTemplate(LOG_POINT_COLLECTTRADE, resultDto));
            }
        } catch (Exception e) {
            logger.error(fillLoggerTemplate(LOG_POINT_COLLECTTRADE, resultDto), e);
            throw new PayFrontException(ErrorCode.PAY_FRONT_EXCEPTION.msg(), e);
        }
        return resultDto;
    }

	/**
	 * 代收跳转收银台
	 */
	@Override
	public String collectTradeToCashDesk(CreateCollectTradeDto createCollectTradeDto)
			throws PayFrontException, HttpException {
		if (logger.isInfoEnabled()) {
			logger.info(LOG_POINT_COLLECTTRADE + "DTO{}", ToStringBuilder.reflectionToString(createCollectTradeDto));
		}
		// 请求信息打点
		Map<String, String> logParams = Maps.newHashMap();
		logParams.put("out_trade_no", createCollectTradeDto.getOutTradeNo());
		logParams.put("out_trade_code", createCollectTradeDto.getOutTradeCode().bizCode());
		String jsonResponse = null;
		try {
			// 进行POST请求
			createCollectTradeDto.setPayMethod(signPayArgsByRSA(createCollectTradeDto.getPayMethod(),
					createCollectTradeDto.getInputCharset()));
			Map<String, String> requestMap = Maps.newHashMap();
			RequestConverter.addRequestDto2Map(createCollectTradeDto, requestMap);
			RequestConverter.addCreateCollectTradeDto2Beta(createCollectTradeDto, requestMap);
			addBaseParams(requestMap, Constants.CREATE_HOSTING_COLLECT_TRADE);

			if (logger.isInfoEnabled()) {
				logger.info(fillLoggerTemplate(LOG_POINT_COLLECTTRADE, requestMap));
			}
			jsonResponse = RequestUtil.buildRequest(payfrontGatewayUrl, requestMap, SignType.RSA.name(), rsaPrivateSiginKey,
					payfrontDefaultSignVersion, createCollectTradeDto.getInputCharset().charset());

			if (logger.isInfoEnabled()) {
				logger.info(LOG_POINT_COLLECTTRADE + "响应为{}", jsonResponse);
			}
			return jsonResponse;
		} catch (Exception e) {
			logger.error("代收跳转收银台失败,jsonResponse={}", jsonResponse, e);
			throw new PayFrontException(ErrorCode.PAY_FRONT_EXCEPTION.msg(), e);
		}
	}

    /**
     * 代收网银支付方式
     */
    @Override
    public String collectTradeByBank(CreateCollectTradeDto createCollectTradeDto)
                                                                                 throws PayFrontException {
        if (logger.isInfoEnabled()) {
            logger.info(LOG_POINT_COLLECTTRADE + "DTO{}",
                ToStringBuilder.reflectionToString(createCollectTradeDto));
        }

        try {
            Map<String, String> requestMap = Maps.newHashMap();
            RequestConverter.addRequestDto2Map(createCollectTradeDto, requestMap);
            RequestConverter.addCreateCollectTradeDto2Map(createCollectTradeDto, requestMap);
            addBaseParams(requestMap, Constants.CREATE_HOSTING_COLLECT_TRADE);

            //生成加签名后的内容
            String charset = createCollectTradeDto.getInputCharset().charset();
            Map<String, String> requestIncludeSign = RequestUtil.buildRequestPara(requestMap,
                SignType.RSA.name(), this.rsaPrivateSiginKey, payfrontDefaultServerVersion, charset);
            // 创建表单
            Document doc = createBankPayForm(requestIncludeSign);
            return doc.body().html();

        } catch (Exception e) {
            if (logger.isInfoEnabled()) {
                logger.info(LOG_POINT_COLLECTTRADE + "BANK出现异常", e);
            }
            throw new PayFrontException(LOG_POINT_COLLECTTRADE + "BANK出现异常", e);
        }
    }

    /**
     * 单笔代付
     */
    @Override
    public ResultDto<CreateSinglePayTradeResult> createSinglePayTrade(CreateSinglePayTradeDto createPayTradeDto)
                                                                                                                throws PayFrontException,
                                                                                                                HttpException {
        if (logger.isInfoEnabled()) {
            logger.info(LOG_POINT_CREATESINGLEPAYTRADE + "入参DTO{}",
                ToStringBuilder.reflectionToString(createPayTradeDto));
        }
        ResultDto<CreateSinglePayTradeResult> resultDto = new ResultDto<CreateSinglePayTradeResult>();

        // 请求信息打点
        Map<String, String> logParams = Maps.newHashMap();
        logParams.put("out_trade_no", createPayTradeDto.getOutTradeNo());
        logParams.put("out_trade_code", createPayTradeDto.getOutTradeCode().bizCode());
        try {
            // 进行POST请求
            Map<String, String> requestMap = Maps.newHashMap();
            RequestConverter.addRequestDto2Map(createPayTradeDto, requestMap);
            RequestConverter.addCreateSinglePayTradeDto2Map(createPayTradeDto, requestMap);
            addBaseParams(requestMap, Constants.CREATE_SINGLE_HOSTING_PAY_TRADE);

            if (logger.isInfoEnabled()) {
                logger.info(fillLoggerTemplate(LOG_POINT_CREATESINGLEPAYTRADE, requestMap));
            }

            String jsonResponse = RequestUtil.buildRequest(payfrontGatewayUrl, requestMap,
                SignType.RSA.name(), this.rsaPrivateSiginKey, payfrontDefaultSignVersion, createPayTradeDto
                    .getInputCharset().charset());

            if (logger.isInfoEnabled()) {
                logger.info(LOG_POINT_CREATESINGLEPAYTRADE + "响应为{}", jsonResponse);
            }

            // 请求结果转换到resultDto
            ResponseJsonConverter.convert2CreateSinglePayTrade(jsonResponse,
                createPayTradeDto.getInputCharset(), resultDto, rsaPublicSiginKey);

            if (logger.isInfoEnabled()) {
                logger.info(fillLoggerTemplate(LOG_POINT_CREATESINGLEPAYTRADE, resultDto));
            }
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            logger.error(fillLoggerTemplate(LOG_POINT_CREATESINGLEPAYTRADE, resultDto), e);
            throw new PayFrontException(ErrorCode.PAY_FRONT_EXCEPTION.msg(), e);
        }
        return resultDto;
    }

    /**
     * 批量代付
     */
    @Override
    @SuppressWarnings("all")
    public void createBatchPayTrade(CreateBatchPayTradeDto batchHostPayTradeDto)
                                                                                     throws PayFrontException,
                                                                                     HttpException {

            logger.info(LOG_POINT_CREATEBATCHPAYTRADE + "入参DTO{}",  ToStringBuilder.reflectionToString(batchHostPayTradeDto));

      //  ResultDto resultDto = new ResultDto();

        // 请求信息打点
        Map<String, String> logParams = Maps.newHashMap();
        logParams.put("out_pay_no", batchHostPayTradeDto.getOutPayNo());
        logParams.put("out_trade_code", batchHostPayTradeDto.getOutTradeCode().bizCode());
        try {
            // 进行POST请求
            Map<String, String> requestMap = Maps.newHashMap();
            RequestConverter.addRequestDto2Map(batchHostPayTradeDto, requestMap);
            RequestConverter.addCreateBatchPayTradeDto2Map(batchHostPayTradeDto, requestMap);
            addBaseParams(requestMap, Constants.CREATE_BATCH_HOSTING_PAY_TRADE);

            if (logger.isInfoEnabled()) {
                logger.info(fillLoggerTemplate(LOG_POINT_CREATEBATCHPAYTRADE, requestMap));
            }
            RequestUtil.asynbuildRequest(payfrontGatewayUrl, requestMap,
                SignType.RSA.name(), this.rsaPrivateSiginKey, payfrontDefaultSignVersion,
                batchHostPayTradeDto.getInputCharset().charset());
//            //TODO 改成异步线程
//            String jsonResponse =
//            if (logger.isInfoEnabled()) {
//                logger.info(LOG_POINT_CREATEBATCHPAYTRADE + "响应为{}", jsonResponse);
//            }
//            // 请求结果转换到resultDto
//            ResponseJsonConverter.convert2CreateBatchPayTrade(jsonResponse,
//                batchHostPayTradeDto.getInputCharset(), resultDto, payfrontMd5Key);
//
//            if (logger.isInfoEnabled()) {
//                logger.info(fillLoggerTemplate(LOG_POINT_CREATEBATCHPAYTRADE, resultDto));
//            }
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            logger.error("批量代付异常,入口参数={}",ToStringBuilder.reflectionToString(batchHostPayTradeDto),e);
            throw new PayFrontException(ErrorCode.PAY_FRONT_EXCEPTION.msg(), e);
        }
      //  return resultDto;
    }

    /**
     * 支付
     */
//    @Override
//    public ResultDto<PayResult> payTrade(PayTradeDto payTradeDto) throws PayFrontException,
//                                                                 HttpException {
//        if (logger.isInfoEnabled()) {
//            logger.info(LOG_POINT_PAYTRADE + "入参DTO{}",
//                ToStringBuilder.reflectionToString(payTradeDto));
//        }
//        ResultDto<PayResult> resultDto = new ResultDto<PayResult>();
//
//        // 请求信息打点
//        Map<String, String> logParams = Maps.newHashMap();
//        logParams.put("out_pay_no", payTradeDto.getOutPayNo());
//        try {
//
//            // 进行POST请求
//            payTradeDto.setPayMethod(signPayArgsByRSA(payTradeDto.getPayMethod(),
//                payTradeDto.getInputCharset()));
//            // 进行POST请求
//            Map<String, String> requestMap = Maps.newHashMap();
//            RequestConverter.addRequestDto2Map(payTradeDto, requestMap);
//            RequestConverter.add2PayTradeMap(payTradeDto, requestMap);
//            addBaseParams(requestMap, Constants.PAY_HOSTING_TRADE);
//
//            if (logger.isInfoEnabled()) {
//                logger.info(fillLoggerTemplate(LOG_POINT_PAYTRADE, requestMap));
//            }
//
//            String jsonResponse = RequestUtil.buildRequest(payfrontGatewayUrl, requestMap,
//                SignType.RSA.name(), this.rsaPrivateSiginKey, payfrontDefaultSignVersion, payTradeDto
//                    .getInputCharset().charset());
//
//            if (logger.isInfoEnabled()) {
//                logger.info(LOG_POINT_PAYTRADE + "响应为{}", jsonResponse);
//            }
//
//            // 请求结果转换到resultDto
//            ResponseJsonConverter.convert2PayTradeResult(jsonResponse,
//                payTradeDto.getInputCharset(), resultDto, this.rsaPublicSiginKey);
//
//            if (logger.isInfoEnabled()) {
//                logger.info(fillLoggerTemplate(LOG_POINT_PAYTRADE, resultDto));
//            }
//        } catch (HttpException e) {
//            throw e;
//        } catch (Exception e) {
//            logger.error(fillLoggerTemplate(LOG_POINT_PAYTRADE, resultDto), e);
//            throw new PayFrontException(ErrorCode.PAY_FRONT_EXCEPTION.msg(), e);
//        }
//        return resultDto;
//    }

    @Override
    public String payTradeByBank(PayTradeDto payTradeDto) throws PayFrontException {

        if (logger.isInfoEnabled()) {
			logger.info(LOG_POINT_PAY_HOSTING_TRADE + "DTO{}",
                ToStringBuilder.reflectionToString(payTradeDto));
        }

        try {
            Map<String, String> requestMap = Maps.newHashMap();
            RequestConverter.addRequestDto2Map(payTradeDto, requestMap);
            RequestConverter.add2PayTradeMap(payTradeDto, requestMap);
            addBaseParams(requestMap, Constants.PAY_HOSTING_TRADE);
			String jsonResponse = RequestUtil.buildRequest(payfrontGatewayUrl, requestMap, SignType.RSA.name(), rsaPrivateSiginKey,
					payfrontDefaultSignVersion, payTradeDto.getInputCharset().charset());

			if (logger.isInfoEnabled()) {
				logger.info(LOG_POINT_PAY_HOSTING_TRADE + "响应为{}", jsonResponse);
			}
			return jsonResponse;

        } catch (Exception e) {
            if (logger.isInfoEnabled()) {
				logger.info(LOG_POINT_PAY_HOSTING_TRADE + "BANK出现异常", e);
            }
			throw new PayFrontException(LOG_POINT_PAY_HOSTING_TRADE + "BANK出现异常", e);
        }
    }

    /**
     * 退款
     */
    @Override
    public ResultDto<RefundTradeResult> refundTrade(RefundTradeDto refundTradeDto)
                                                                                  throws PayFrontException,
                                                                                  HttpException {
        if (logger.isInfoEnabled()) {
            logger.info(LOG_POINT_REFUNDTRADE + "入参DTO{}",
                ToStringBuilder.reflectionToString(refundTradeDto));
        }
        ResultDto<RefundTradeResult> resultDto = new ResultDto<RefundTradeResult>();

        // 请求信息打点
        Map<String, String> logParams = Maps.newHashMap();
        logParams.put("out_trade_no", refundTradeDto.getOutTradeNo());
        logParams.put("orig_outer_tradeNo", refundTradeDto.getOrigOuterTradeNo());
        logParams.put("user_ip", refundTradeDto.getUserIp());
        try {
            // 进行POST请求
            Map<String, String> requestMap = Maps.newHashMap();
            RequestConverter.addRequestDto2Map(refundTradeDto, requestMap);
            RequestConverter.addRefundTradeDto2Map(refundTradeDto, requestMap);
            addBaseParams(requestMap, Constants.REFUND_HOSTING_TRADE);

            if (logger.isInfoEnabled()) {
                logger.info(fillLoggerTemplate(LOG_POINT_REFUNDTRADE, requestMap));
            }

            String jsonResponse = RequestUtil.buildRequest(payfrontGatewayUrl, requestMap,
                SignType.RSA.name(), this.rsaPrivateSiginKey, payfrontDefaultSignVersion, refundTradeDto
                    .getInputCharset().charset());

            if (logger.isInfoEnabled()) {
                logger.info(LOG_POINT_REFUNDTRADE + "响应为{}", jsonResponse);
            }

            // 请求结果转换到resultDto
            ResponseJsonConverter.convert2RefundTradeResult(jsonResponse,
                refundTradeDto.getInputCharset(), resultDto, this.rsaPublicSiginKey);

            if (logger.isInfoEnabled()) {
                logger.info(fillLoggerTemplate(LOG_POINT_REFUNDTRADE, resultDto));
            }
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            logger.error(fillLoggerTemplate(LOG_POINT_REFUNDTRADE, resultDto), e);
            throw new PayFrontException(ErrorCode.PAY_FRONT_EXCEPTION.msg(), e);
        }
        return resultDto;
    }

    /**
     * 交易查询
     */
    @Override
    public ResultDto<QueryTradeResult> queryTrade(QueryTradeDto queryTradeDto)
                                                                              throws PayFrontException,
                                                                              HttpException {
        if (logger.isInfoEnabled()) {
            logger.info("交易查询DTO{}", ToStringBuilder.reflectionToString(queryTradeDto));
        }
        ResultDto<QueryTradeResult> resultDto = new ResultDto<QueryTradeResult>();
        try {
            // 进行POST请求
            Map<String, String> requestMap = Maps.newHashMap();
            RequestConverter.addRequestDto2Map(queryTradeDto, requestMap);
            RequestConverter.addQueryTradeDto2Map(queryTradeDto, requestMap);
            addBaseParams(requestMap, Constants.QUERY_HOSTING_TRADE);

            if (logger.isInfoEnabled()) {
                logger.info(fillLoggerTemplate("交易查询", requestMap));
            }

            String jsonResponse = RequestUtil.buildRequest(payfrontGatewayUrl, requestMap,
                SignType.RSA.name(), rsaPrivateSiginKey, payfrontDefaultSignVersion, queryTradeDto
                    .getInputCharset().charset());
            if (logger.isInfoEnabled()) {
                logger.info("交易查询结果:{}", jsonResponse);
            }
            // 请求结果转换到resultDto
            ResponseJsonConverter.convert2QueryTradeResult(jsonResponse,
                queryTradeDto.getInputCharset(), resultDto, rsaPublicSiginKey);
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
			logger.error("交易查询出现异常", e);
            throw new PayFrontException(ErrorCode.PAY_FRONT_EXCEPTION.msg(), e);
        }
        return resultDto;
    }

    /**
     * 充值
     */
    @Override
    public ResultDto<PayResult> createDeposit(CreateDepositDto createDepositDto)
                                                                                throws PayFrontException,
                                                                                HttpException {
        if (logger.isInfoEnabled()) {
            logger.info(LOG_POINT_CREATEDEPOSIT + "入参DTO{}",
                ToStringBuilder.reflectionToString(createDepositDto));
        }
        ResultDto<PayResult> resultDto = new ResultDto<PayResult>();

        // 请求信息打点
        Map<String, String> logParams = Maps.newHashMap();
        logParams.put("out_trade_no", createDepositDto.getOutTradeNo());
        try {
            // 进行POST请求
            createDepositDto.setPayMethod(signPayArgsByRSA(createDepositDto.getPayMethod(),
                createDepositDto.getInputCharset()));
            Map<String, String> requestMap = Maps.newHashMap();
            RequestConverter.addRequestDto2Map(createDepositDto, requestMap);
            RequestConverter.addCreateDeposit2Map(createDepositDto, requestMap);
            addBaseParams(requestMap, Constants.CREATE_HOSTING_DEPOSIT);

            if (logger.isInfoEnabled()) {
                logger.info(fillLoggerTemplate(LOG_POINT_CREATEDEPOSIT, requestMap));
            }

            String jsonResponse = RequestUtil.buildRequest(payfrontGatewayUrl, requestMap,
                SignType.RSA.name(), rsaPrivateSiginKey, payfrontDefaultSignVersion, createDepositDto
                    .getInputCharset().charset());

            if (logger.isInfoEnabled()) {
                logger.info(LOG_POINT_CREATEDEPOSIT + "响应为{}", jsonResponse);
            }

            // 请求结果转换到resultDto
            ResponseJsonConverter.convert2CreateDepositResult(jsonResponse,
                createDepositDto.getInputCharset(), resultDto, rsaPublicSiginKey);

            // 处理结果打点
            if (logger.isInfoEnabled()) {
                logger.info(fillLoggerTemplate(LOG_POINT_CREATEDEPOSIT, resultDto));
            }
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            logger.error(fillLoggerTemplate(LOG_POINT_CREATEDEPOSIT, resultDto), e);
            throw new PayFrontException(ErrorCode.PAY_FRONT_EXCEPTION.msg(), e);
        }
        return resultDto;
    }

    @Override
    public String createDepositByBank(CreateDepositDto createDepositDto) throws PayFrontException {

        if (logger.isInfoEnabled()) {
            logger.info(LOG_POINT_CREATEDEPOSIT + "DTO{}",
                ToStringBuilder.reflectionToString(createDepositDto));
        }
         StopWatch  stopwatch = new StopWatch();
        try {
        	stopwatch.isStarted();
            Map<String, String> requestMap = Maps.newHashMap();
            RequestConverter.addRequestDto2Map(createDepositDto, requestMap);
            RequestConverter.addCreateDeposit2Map(createDepositDto, requestMap);
            addBaseParams(requestMap, Constants.CREATE_HOSTING_DEPOSIT);

            //生成加签名后的内容
            String charset = createDepositDto.getInputCharset().charset();
            Map<String, String> requestIncludeSign = RequestUtil.buildRequestPara(requestMap,
                SignType.RSA.name(), rsaPrivateSiginKey, payfrontDefaultServerVersion, charset);

            // 创建表单
            Document doc = createBankPayForm(requestIncludeSign);
            stopwatch.isStopped();
            logger.info("生成充值form 耗时="+stopwatch.getTime());
            return doc.body().html();

        } catch (Exception e) {
            if (logger.isInfoEnabled()) {
                logger.info(LOG_POINT_CREATEDEPOSIT + "BANK出现异常", e);
            }
            throw new PayFrontException(LOG_POINT_CREATEDEPOSIT + "BANK出现异常", e);
        }

    }
    
    /**
     * 托管充值：收银台模式
     */
    @Override
    public String createHostingDeposit(CreateDepositDto createDepositDto) throws PayFrontException {
    	 if (logger.isInfoEnabled()) {
             logger.info(LOG_POINT_CREATEDEPOSIT + "DTO{}",
                 ToStringBuilder.reflectionToString(createDepositDto));
         }
    	 StopWatch stopwatch = new StopWatch();
         try {
         	 stopwatch.isStarted();
             Map<String, String> requestMap = Maps.newHashMap();
             RequestConverter.addRequestDto2Map(createDepositDto, requestMap);
             RequestConverter.addCreateDeposit2Map(createDepositDto, requestMap);
             addBaseParams(requestMap, Constants.CREATE_HOSTING_DEPOSIT);
             
             String jsonResponse = RequestUtil.buildRequest(payfrontGatewayUrl, requestMap, SignType.RSA.name(),
            		 			   rsaPrivateSiginKey, payfrontDefaultSignVersion, createDepositDto.getInputCharset().charset());
             stopwatch.isStopped();
             logger.info("[托管充值]生成form=" + jsonResponse + ",耗时="+stopwatch.getTime());
             return jsonResponse;
         } catch (Exception e) {
             if (logger.isInfoEnabled()) {
                 logger.error(LOG_POINT_CREATEDEPOSIT + "出现异常", e);
             }
             throw new PayFrontException(LOG_POINT_CREATEDEPOSIT + "出现异常", e);
         }
    }

    /**
     * 查询充值
     */
    @Override
    public ResultDto<QueryDepositResult> queryDeposit(QueryDepositDto queryDepositDto)
                                                                                      throws PayFrontException,
                                                                                      HttpException {
        if (logger.isInfoEnabled()) {
            logger.info("充值查询DTO{}", ToStringBuilder.reflectionToString(queryDepositDto));
        }
        ResultDto<QueryDepositResult> resultDto = new ResultDto<QueryDepositResult>();
        try {
            // 进行POST请求
            Map<String, String> requestMap = Maps.newHashMap();
            RequestConverter.addRequestDto2Map(queryDepositDto, requestMap);
            RequestConverter.addQueryDepositDto2Map(queryDepositDto, requestMap);
            addBaseParams(requestMap, Constants.QUERY_HOSTING_DEPOSIT);

            if (logger.isInfoEnabled()) {
                logger.info(fillLoggerTemplate("查询充值", requestMap));
            }

            String jsonResponse = RequestUtil.buildRequest(payfrontGatewayUrl, requestMap,
                SignType.RSA.name(), rsaPrivateSiginKey, payfrontDefaultSignVersion, queryDepositDto
                    .getInputCharset().charset());
            logger.info("[查询充值]生成json=" + jsonResponse);
            // 请求结果转换到resultDto
            ResponseJsonConverter.convert2QueryDepositResult(jsonResponse,
                queryDepositDto.getInputCharset(), resultDto, rsaPublicSiginKey);
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            logger.error("充值查询出现异常", e);
            throw new PayFrontException(ErrorCode.PAY_FRONT_EXCEPTION.msg(), e);
        }
        return resultDto;
    }

    /**
     * 提现 
     */
    @Override
    public ResultDto<PayResult> createWithDraw(CreateWithDrawDto createWithDrawDto)
                                                                                   throws PayFrontException,
                                                                                   HttpException {
        if (logger.isInfoEnabled()) {
            logger.info(LOG_POINT_CREATEWITHDRAW + "入参DTO{}",
                ToStringBuilder.reflectionToString(createWithDrawDto));
        }
        ResultDto<PayResult> resultDto = new ResultDto<PayResult>();

        // 请求信息打点
        Map<String, String> logParams = Maps.newHashMap();
        logParams.put("out_trade_no", createWithDrawDto.getOutTradeNo());
        try {
            // 进行POST请求
            Map<String, String> requestMap = Maps.newHashMap();
            RequestConverter.addRequestDto2Map(createWithDrawDto, requestMap);
            RequestConverter.addCreateWithDrawMap(createWithDrawDto, requestMap);
            addBaseParams(requestMap, Constants.CREATE_HOSTING_WITHDRAW);

            if (logger.isInfoEnabled()) {
                logger.info(fillLoggerTemplate(LOG_POINT_CREATEWITHDRAW, requestMap));
            }

            String jsonResponse = RequestUtil.buildRequest(payfrontGatewayUrl, requestMap,
                SignType.RSA.name(), rsaPrivateSiginKey, payfrontDefaultSignVersion, createWithDrawDto
                    .getInputCharset().charset());

            if (logger.isInfoEnabled()) {
                logger.info(LOG_POINT_CREATEWITHDRAW + "响应为{}", jsonResponse);
            }

            // 请求结果转换到resultDto
            ResponseJsonConverter.convert2CreateWithDrawResult(jsonResponse,
                createWithDrawDto.getInputCharset(), resultDto, rsaPublicSiginKey);

            // 处理结果打点
            if (logger.isInfoEnabled()) {
                logger.info(fillLoggerTemplate(LOG_POINT_CREATEWITHDRAW, resultDto));
            }
        } catch (HttpException e) {        	
            throw e;
        } catch (Exception e) {
            logger.error(fillLoggerTemplate(LOG_POINT_CREATEWITHDRAW, resultDto), e);
            throw new PayFrontException(ErrorCode.PAY_FRONT_EXCEPTION.msg(), e);
        }
        return resultDto;
    }
    
    /**
     * 提现处理(收银台模式)
     */
    @Override
    public String createHostingWithdraw(CreateWithDrawDto createWithDrawDto) throws PayFrontException, HttpException {
        if (logger.isInfoEnabled()) {
            logger.info(LOG_POINT_CREATEWITHDRAW + "入参DTO{}", ToStringBuilder.reflectionToString(createWithDrawDto));
        }
        ResultDto<PayResult> resultDto = new ResultDto<PayResult>();
        StopWatch stopwatch = new StopWatch();
        try {
        	stopwatch.start();
        	// 请求信息打点
            Map<String, String> requestMap = Maps.newHashMap();
            RequestConverter.addRequestDto2Map(createWithDrawDto, requestMap);
            RequestConverter.addCreateWithDrawMap(createWithDrawDto, requestMap);
            addBaseParams(requestMap, Constants.CREATE_HOSTING_WITHDRAW);
            // 扩展信息参数
            Map<String, String> extendParamMap = Maps.newHashMap();
            extendParamMap.put("customNotify", "Y");
            createWithDrawDto.setExtendParam(extendParamMap);
            buildWithdrawExtendParamDto(requestMap, createWithDrawDto);
            
            if (logger.isInfoEnabled()) {
                logger.info(fillLoggerTemplate(LOG_POINT_CREATEWITHDRAW, requestMap));
            }
            // 进行POST请求
            String jsonResponse = RequestUtil.buildRequest(payfrontGatewayUrl, requestMap, SignType.RSA.name(),
            				      rsaPrivateSiginKey, payfrontDefaultSignVersion, createWithDrawDto.getInputCharset().charset());
            if (logger.isInfoEnabled()) {
                logger.info(LOG_POINT_CREATEWITHDRAW + "响应为{}", jsonResponse);
            }
            // 处理结果打点
            if (logger.isInfoEnabled()) {
                logger.info(fillLoggerTemplate(LOG_POINT_CREATEWITHDRAW, resultDto));
            }
            
            stopwatch.isStopped();
            logger.info("[托管提现]生成form=" + jsonResponse + ",耗时="+stopwatch.getTime());
            return jsonResponse;
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            logger.error(fillLoggerTemplate(LOG_POINT_CREATEWITHDRAW, resultDto), e);
            throw new PayFrontException(ErrorCode.PAY_FRONT_EXCEPTION.msg(), e);
        }
    }
    // 提现扩展信息参数
    private static void buildWithdrawExtendParamDto(Map<String, String> requestMap, CreateWithDrawDto createWithDrawDto) {
    	if (createWithDrawDto.getExtendParam() != null && !createWithDrawDto.getExtendParam().isEmpty()) {
    		requestMap.put("extend_param", map2FormatStyle(createWithDrawDto.getExtendParam()));
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
//                sb.append(CastStringUtil.underLineName(entry.getKey()));
                sb.append(entry.getKey());
                sb.append(Constants.ANGLE_BRACKETS);
                sb.append(entry.getValue());
                keyJoinValList.add(sb.toString());
            }
            return Joiner.on(Constants.VERTICAL_LINE).skipNulls().join(keyJoinValList);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 提现查询
     */
    @Override
    public ResultDto<QueryWithDrawResult> queryWithDraw(QueryWithDrawDto queryWithDrawDto)
                                                                                          throws PayFrontException,
                                                                                          HttpException {
        if (logger.isInfoEnabled()) {
            logger.info("提现查询DTO{}", ToStringBuilder.reflectionToString(queryWithDrawDto));
        }
        ResultDto<QueryWithDrawResult> resultDto = new ResultDto<QueryWithDrawResult>();
        try {
            // 进行POST请求
            Map<String, String> requestMap = Maps.newHashMap();
            RequestConverter.addRequestDto2Map(queryWithDrawDto, requestMap);
            RequestConverter.addQueryWithDraw2Map(queryWithDrawDto, requestMap);
            addBaseParams(requestMap, Constants.QUERY_HOSTING_WITHDRAW);

            if (logger.isInfoEnabled()) {
                logger.info(fillLoggerTemplate("提现", requestMap));
            }
            String jsonResponse = RequestUtil.buildRequest(payfrontGatewayUrl, requestMap,
                SignType.RSA.name(), rsaPrivateSiginKey, payfrontDefaultSignVersion, queryWithDrawDto
                    .getInputCharset().charset());
            logger.info("[托管提现]生成json=" + jsonResponse);
            // 最终结果对象转换
            ResponseJsonConverter.convert2QueryWithDrawResult(jsonResponse,
                queryWithDrawDto.getInputCharset(), resultDto, rsaPublicSiginKey);
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            logger.error("提现查询出现异常", e);
            throw new PayFrontException(ErrorCode.PAY_FRONT_EXCEPTION.msg(), e);
        }
        return resultDto;
    }

    @Override
    public ResultDto<QueryPayTradeResult> queryPayTrade(QueryPayTradeDto queryPayTradeDto)
                                                                                          throws PayFrontException,
                                                                                          HttpException {
        if (logger.isInfoEnabled()) {
            logger.info("交易查询DTO{}", ToStringBuilder.reflectionToString(queryPayTradeDto));
        }
        ResultDto<QueryPayTradeResult> resultDto = new ResultDto<QueryPayTradeResult>();
        try {
            // 进行POST请求
            Map<String, String> requestMap = Maps.newHashMap();
            RequestConverter.addRequestDto2Map(queryPayTradeDto, requestMap);
            RequestConverter.addQueryPayTrade2Map(queryPayTradeDto, requestMap);
            addBaseParams(requestMap, Constants.QUERY_PAY_RESULT);

            if (logger.isInfoEnabled()) {
                logger.info(fillLoggerTemplate("交易查询", requestMap));
            }

            String jsonResponse = RequestUtil.buildRequest(payfrontGatewayUrl, requestMap,
                SignType.RSA.name(), rsaPrivateSiginKey, payfrontDefaultSignVersion, queryPayTradeDto
                    .getInputCharset().charset());

            // 最终结果对象转换
            ResponseJsonConverter.convert2QueryPayTradeResult(jsonResponse,
                queryPayTradeDto.getInputCharset(), resultDto, rsaPublicSiginKey);
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            logger.error("交易查询出现异常", e);
            throw new PayFrontException(ErrorCode.PAY_FRONT_EXCEPTION.msg(), e);
        }
        return resultDto;
    }

    @Override
    public ResultDto<QueryRefundResult> queryRefund(QueryRefundDto queryRefundDto)
                                                                                  throws PayFrontException,
                                                                                  HttpException {
        if (logger.isInfoEnabled()) {
            logger.info("退款查询DTO{}", ToStringBuilder.reflectionToString(queryRefundDto));
        }
        ResultDto<QueryRefundResult> resultDto = new ResultDto<QueryRefundResult>();
        try {
            // 进行POST请求
            Map<String, String> requestMap = Maps.newHashMap();
            RequestConverter.addRequestDto2Map(queryRefundDto, requestMap);
            RequestConverter.addQueryRefund2Map(queryRefundDto, requestMap);
            addBaseParams(requestMap, Constants.QUERY_REFUND);

            if (logger.isInfoEnabled()) {
                logger.info(fillLoggerTemplate("退款查询", requestMap));
            }
            String jsonResponse = RequestUtil.buildRequest(payfrontGatewayUrl, requestMap,
                SignType.RSA.name(), rsaPrivateSiginKey, payfrontDefaultSignVersion, queryRefundDto
                    .getInputCharset().charset());

            // 最终结果对象转换
            ResponseJsonConverter.convert2QueryRefundResult(jsonResponse,
                queryRefundDto.getInputCharset(), resultDto, rsaPublicSiginKey);
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            logger.error("查询退款出现异常", e);
            throw new PayFrontException(ErrorCode.PAY_FRONT_EXCEPTION.msg(), e);
        }
        return resultDto;
    }
    
    @Override
	public ResultDto<?> queryFundYield(String fincode) throws Exception {
		 Map<String, String> requestMap = Maps.newHashMap();
		 requestMap.put("fund_code", fincode);
		 requestMap.put("_input_charset", CharsetType.UTF8.charset());
		 
		  addBaseParams(requestMap, Constants.QUERY_FUND_YIELD);

          if (logger.isInfoEnabled()) {
              logger.info(fillLoggerTemplate("基金查询", requestMap));
          }
          String jsonResponse = RequestUtil.buildRequest(payfrontGatewayUrl, requestMap,
              SignType.RSA.name(), rsaPrivateSiginKey, payfrontDefaultSignVersion, CharsetType.UTF8.charset());
          ResultDto<QueryFinResponse> resultDto = new ResultDto<QueryFinResponse>();       
          ResponseJsonConverter.convert2QueryFinResponseResult(jsonResponse,CharsetType.UTF8, resultDto, rsaPublicSiginKey);
		return resultDto;
	}
    

    /**
     * 支付参数RSA加密 
     * @param payMethod
     * @param charsetType
     * @return
     */
    public PayArgsBase signPayArgsByRSA(PayArgsBase payMethod, CharsetType charsetType) {
        if (payMethod == null)
            return null;
        if (payMethod instanceof QuickPayArgs) {
            QuickPayArgs quickPayArgs = (QuickPayArgs) payMethod;
            if (StringUtils.isNotBlank(quickPayArgs.getAccountName())) {
                String accountNameCrytion = RSAExtend.crytionByRSA(quickPayArgs.getAccountName(),
                    charsetType, encrytPublicKey);
                quickPayArgs.setAccountName(accountNameCrytion);
            }
            if (StringUtils.isNotBlank(quickPayArgs.getBankCardNo())) {
                String bankCardNoCrytion = RSAExtend.crytionByRSA(quickPayArgs.getBankCardNo(),
                    charsetType, encrytPublicKey);
                quickPayArgs.setBankCardNo(bankCardNoCrytion);
            }
            if (StringUtils.isNotBlank(quickPayArgs.getValidTime())) {
                String validTimeCrytion = RSAExtend.crytionByRSA(quickPayArgs.getValidTime(),
                    charsetType, encrytPublicKey);
                quickPayArgs.setValidTime(validTimeCrytion);
            }
            if (StringUtils.isNotBlank(quickPayArgs.getCVV2())) {
                String cvv2Crytion = RSAExtend.crytionByRSA(quickPayArgs.getCVV2(), charsetType,
                        encrytPublicKey);
                quickPayArgs.setCVV2(cvv2Crytion);
            }
        }
        return payMethod;
    }

    /**
     * JSOUP生成代收的POST表单
     * @param requestMap
     * @return
     */
    private Document createBankPayForm(Map<String, String> requestMap) throws  Exception{
        // 生成<form></from>最外围结点
        Document doc = Jsoup.parseBodyFragment(Constants.FORM_ELEMENT_OUTER);

        // 获取form元素
        Element formElement = doc.select("form").first();
        formElement.attr("action", payfrontGatewayUrl);
        formElement.attr("method", Constants.FORM_REQEUST_METHOD);
        formElement.attr("id", "frmBankID");
        //formElement.attr("target", "_blank");
        //生成input元素
        for (Map.Entry<String, String> entry : requestMap.entrySet()) {
            Element inputElement = doc.createElement("input");
            inputElement.attr("type", "hidden");
            inputElement.attr("name", entry.getKey());
            inputElement.attr("value", URLEncoder.encode(entry.getValue(),CharsetType.UTF8.name()));
            formElement.appendChild(inputElement);
        }
        logger.info("网银充值form"+doc.html());
//          if (logger.isDebugEnabled()) {
//              Element inputElement = doc.createElement("input");
//              inputElement.attr("type", "submit");
//              formElement.appendChild(inputElement);
//          }
        return doc;
    }

    public void addBaseParams(Map<String, String> requestMap, String service) {
        requestMap.put("version", payfrontDefaultServerVersion);
        requestMap.put("encrypt_version", payfrontDefaultEncyptVersion);
        requestMap.put("request_time", DateUtil.format(new Date(), DateUtil.longFormat));
        requestMap.put("service", service);
        requestMap.put("partner_id", payfrontDefaultParterId);
    }

    private String fillLoggerTemplate(String bizTitle, ResultDto<?> resultDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(bizTitle);
        sb.append("接口调用");
        sb.append(resultDto.isSuccess() ? "成功" : String.format("失败，[errorCode]=%s,[errorMsg]=%s",
            resultDto.getErrorCode(), resultDto.getErrorMsg()));
        return sb.toString();
    }

    private String fillLoggerTemplate(String bizTitle, Map<String, String> logParams) {
        StringBuilder sb = new StringBuilder();
        sb.append(bizTitle);
        sb.append("接口调用");
        sb.append(String.format("，请求参数:%s", logParams));
        return sb.toString();
    }

    public void setPayfrontMd5KeyPath(String payfrontMd5KeyPath) {
        this.payfrontMd5KeyPath = payfrontMd5KeyPath;
    }

    public void setPayfrontGatewayUrl(String payfrontGatewayUrl) {
        this.payfrontGatewayUrl = payfrontGatewayUrl;
    }

    public void setPayfrontDefaultSignVersion(String payfrontDefaultSignVersion) {
        this.payfrontDefaultSignVersion = payfrontDefaultSignVersion;
    }

    public void setPayfrontDefaultEncyptVersion(String payfrontDefaultEncyptVersion) {
        this.payfrontDefaultEncyptVersion = payfrontDefaultEncyptVersion;
    }

    public void setPayfrontDefaultServerVersion(String payfrontDefaultServerVersion) {
        this.payfrontDefaultServerVersion = payfrontDefaultServerVersion;
    }

    public void setPayfrontPublicKey(String payfrontPublicKey) {
        this.payfrontPublicKey = payfrontPublicKey;
    }

    public void setPayfrontDefaultParterId(String payfrontDefaultParterId) {
        this.payfrontDefaultParterId = payfrontDefaultParterId;
    }

	public void setPayfrontMd5Key(String payfrontMd5Key) {
		this.payfrontMd5Key = payfrontMd5Key;
	}
    private boolean isSuccess(String responseCode) {
        return (ResponseCodeEnum.APPLY_SUCCESS.name().equals(responseCode) || ResponseCodeEnum.BIZ_PENDING
                .name().equals(responseCode)) ? true : false;
    }

    @Override
    public ResultDto<?> advanceHostingPay(String outAdvanceNo, String ticket, String validateCode, String userIp) throws Exception {

        Map<String, String> requestMap = Maps.newHashMap();
        requestMap.put("out_advance_no",outAdvanceNo);
        requestMap.put("ticket",ticket);
        requestMap.put("validate_code",validateCode);
        requestMap.put("user_ip", userIp);
        requestMap.put("_input_charset",CharsetType.UTF8.charset());
        addBaseParams(  requestMap,Constants.ADVANCE_HOSTING_PAY );
        if (logger.isInfoEnabled()) {
            logger.info(fillLoggerTemplate("支付推进请求", requestMap));
        }
        String jsonResponse = RequestUtil.buildRequest(payfrontGatewayUrl, requestMap,
                SignType.RSA.name(), rsaPrivateSiginKey, payfrontDefaultSignVersion, CharsetType.UTF8.charset());
        if (logger.isInfoEnabled()) {
            logger.info("支付推进" + "响应为{}", jsonResponse);
        }
        JSONObject obj = JSONObject.parseObject(jsonResponse);
        boolean isSuccess = isSuccess(obj.getString("response_code"));
        ResultDto  resultDto = new ResultDto();
        if (isSuccess) {
            resultDto.setSuccess(true);
        } else {
            resultDto.setErrorCode(obj.getString("response_code"));
            resultDto.setErrorMsg(obj.getString("response_message"));
        }
        return resultDto;
    }

    @Override
    public ResultDto<QueryTradeResult> queryHostingBatchTrade(String outBatchNo) throws Exception {
        Map<String, String> requestMap = Maps.newHashMap();
        requestMap.put("out_batch_no",outBatchNo);
        requestMap.put("_input_charset",CharsetType.UTF8.charset());
        addBaseParams(  requestMap,Constants.QUERY_HOSTING_BATCH_TRADE );
        if (logger.isInfoEnabled()) {
            logger.info(fillLoggerTemplate("托管交易批次查询", requestMap));
        }
        String jsonResponse = RequestUtil.buildRequest(payfrontGatewayUrl, requestMap,
                SignType.RSA.name(), rsaPrivateSiginKey, payfrontDefaultSignVersion, CharsetType.UTF8.charset());
        if (logger.isInfoEnabled()) {
            logger.info("托管交易批次查询" + "响应为{}", jsonResponse);
        }
        JSONObject obj = JSONObject.parseObject(jsonResponse);
        boolean isSuccess = isSuccess(obj.getString("response_code"));
        ResultDto<QueryTradeResult>  resultDto = new ResultDto<QueryTradeResult>();
        if (isSuccess) {
            resultDto.setSuccess(true);
        } else {
            resultDto.setErrorCode(obj.getString("response_code"));
            resultDto.setErrorMsg(obj.getString("response_message"));
        }
     // 请求结果转换到resultDto
        ResponseJsonConverter.convert2QueryTradeResult(jsonResponse,
        		CharsetType.UTF8, resultDto, rsaPublicSiginKey);
        return resultDto;
    }

	@Override
	public ResultDto<Object> finishPreAuthTrade(FinishAuthTradeDto preAuthTradeDto) throws PayFrontException, HttpException {
		ResultDto<Object> resultDto = new ResultDto<Object>();
		logger.info(LOG_POINT_FINISH_PRE_AUTH_TRADE + "入参DTO{}", ToStringBuilder.reflectionToString(preAuthTradeDto));
		// 请求信息打点
		Map<String, String> logParams = Maps.newHashMap();
		logParams.put("out_request_no", preAuthTradeDto.getOutRequestNo());
		logParams.put("user_ip", preAuthTradeDto.getUserIp());
		try {
			// 进行POST请求
			Map<String, String> requestMap = Maps.newHashMap();
			RequestConverter.addRequestDto2Map(preAuthTradeDto, requestMap);
			RequestConverter.addFinishAuthTradeDto2Map(preAuthTradeDto, requestMap);
			addBaseParams(requestMap, Constants.FINISH_PRE_AUTH_TRADE);

			if (logger.isInfoEnabled()) {
				logger.info(fillLoggerTemplate(LOG_POINT_FINISH_PRE_AUTH_TRADE, requestMap));
			}
			String jsonResponse = RequestUtil.buildRequest(payfrontGatewayUrl, requestMap, SignType.RSA.name(), this.rsaPrivateSiginKey,
					payfrontDefaultSignVersion, preAuthTradeDto.getInputCharset().charset());
			if (logger.isInfoEnabled()) {
				logger.info(LOG_POINT_FINISH_PRE_AUTH_TRADE + "响应为{}", jsonResponse);
			}
			// 请求结果转换到resultDto
			ResponseJsonConverter.convert2AuthTrade(jsonResponse, preAuthTradeDto.getInputCharset(), resultDto, rsaPublicSiginKey);
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			logger.error("代收完成异常,入口参数={}", ToStringBuilder.reflectionToString(preAuthTradeDto), e);
			throw new PayFrontException(ErrorCode.PAY_FRONT_EXCEPTION.msg(), e);
		}
		return resultDto;
	}

	@Override
	public ResultDto<Object> cancelPreAuthTrade(CancelAuthTradeDto preAuthTradeDto) throws PayFrontException, HttpException {
		ResultDto<Object> resultDto = new ResultDto<Object>();
		logger.info(LOG_POINT_CANCEL_PRE_AUTH_TRADE + "入参DTO{}", ToStringBuilder.reflectionToString(preAuthTradeDto));
		// 请求信息打点
		Map<String, String> logParams = Maps.newHashMap();
		logParams.put("out_request_no", preAuthTradeDto.getOutRequestNo());
		try {
			// 进行POST请求
			Map<String, String> requestMap = Maps.newHashMap();
			RequestConverter.addRequestDto2Map(preAuthTradeDto, requestMap);
			RequestConverter.addCancelAuthTradeDto2Map(preAuthTradeDto, requestMap);
			addBaseParams(requestMap, Constants.CANCEL_PRE_AUTH_TRADE);

			if (logger.isInfoEnabled()) {
				logger.info(fillLoggerTemplate(LOG_POINT_CANCEL_PRE_AUTH_TRADE, requestMap));
			}
			String jsonResponse = RequestUtil.buildRequest(payfrontGatewayUrl, requestMap, SignType.RSA.name(), this.rsaPrivateSiginKey,
					payfrontDefaultSignVersion, preAuthTradeDto.getInputCharset().charset());
			if (logger.isInfoEnabled()) {
				logger.info(LOG_POINT_CANCEL_PRE_AUTH_TRADE + "响应为{}", jsonResponse);
			}
			// 请求结果转换到resultDto
			ResponseJsonConverter.convert2AuthTrade(jsonResponse, preAuthTradeDto.getInputCharset(), resultDto, rsaPublicSiginKey);
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			logger.error("代收撤销异常,入口参数={}", ToStringBuilder.reflectionToString(preAuthTradeDto), e);
			throw new PayFrontException(ErrorCode.PAY_FRONT_EXCEPTION.msg(), e);
		}
		return resultDto;
	}
}
