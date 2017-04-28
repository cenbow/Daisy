package com.yourong.common.thirdparty.sinapay.pay.service.facade;

import org.apache.commons.httpclient.HttpException;

import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
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
import com.yourong.common.thirdparty.sinapay.pay.domain.result.CreateCollectTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.CreateSinglePayTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.PayResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryDepositResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryPayTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryRefundResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryWithDrawResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.RefundTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.exceptions.PayFrontException;

/**
 * <p>钱包前置服务</p>
 *
 * @author Wallis Wang
 * @version $Id: HostingTradeFacade.java, v 0.1 2014年5月16日 上午11:06:59 wangqiang Exp $
 */
public interface HostingTradeFacade {

    /**
     * 代收
     * <p>直接返回处理结果</p>
     *
     * @param createCollectTradeDto 代收请求对象
     * @return 支付网关返回JSON串，处理后最终结果对象
     * @throws PayFrontException 支付前置异常（请求对象检验、组装网关请求数据、支付网关返回结果解析）
     * @throws HttpException     向支付网关提交请求时出现的网络异常
     */
    ResultDto<CreateCollectTradeResult> collectTrade(CreateCollectTradeDto createCollectTradeDto)
            throws PayFrontException,
            HttpException;

    /**
     * 代收跳转收银台
     * <p>直接返回处理结果</p>
     *
     * @param createCollectTradeDto 代收请求对象
     * @return 支付网关返回JSON串，处理后最终结果对象
     * @throws PayFrontException 支付前置异常（请求对象检验、组装网关请求数据、支付网关返回结果解析）
     * @throws HttpException     向支付网关提交请求时出现的网络异常
     */
	public String collectTradeToCashDesk(CreateCollectTradeDto createCollectTradeDto)
			throws PayFrontException, HttpException;
			
    /**
     * 代收（网银支付方式）
     * <p>生成post表单，用于给浏览器进行跳转到对应的银行进行支付 </p>
     *
     * @param createCollectTradeDto 代收请求对象
     * @return FORM表单，类似<form id='frmBankID' name='frmBankName'
     * @throws PayFrontException 支付前置异常（请求对象检验、组装网关请求数据、支付网关返回结果解析）
     * @throws HttpException     向支付网关提交请求时出现的网络异常
     */
    String collectTradeByBank(CreateCollectTradeDto createCollectTradeDto) throws PayFrontException;

    /**
     * 单笔代付
     *
     * @param createPayTradeDto 单笔代付请求对象
     * @return 支付网关返回JSON串，处理后最终结果对象
     * @throws PayFrontException 支付前置异常（请求对象检验、组装网关请求数据、支付网关返回结果解析）
     * @throws HttpException     向支付网关提交请求时出现的网络异常
     */
    ResultDto<CreateSinglePayTradeResult> createSinglePayTrade(CreateSinglePayTradeDto createPayTradeDto)
            throws PayFrontException,
            HttpException;

    /**
     * 批量代付
     *
     * @param createBatchPayTradeDto 批量代付请求对象
     * @return 批量代付没有对应的具体处理结果，只包含接口调用成功失败
     * @throws PayFrontException 支付前置异常（请求对象检验、组装网关请求数据、支付网关返回结果解析）
     * @throws HttpException     向支付网关提交请求时出现的网络异常
     */
    @SuppressWarnings("all")
    void  createBatchPayTrade(CreateBatchPayTradeDto batchCreatePayTradeDto)
            throws PayFrontException,
            HttpException;

    /**
     * 托管支付
     *
     * @param payTradeDto 托管支付请求对象
     * @return 支付网关返回JSON串，处理后最终结果对象
     * @throws PayFrontException 支付前置异常（请求对象检验、组装网关请求数据、支付网关返回结果解析）
     * @throws HttpException     向支付网关提交请求时出现的网络异常
     */
//   ResultDto<PayResult> payTrade(PayTradeDto payTradeDto) throws PayFrontException, HttpException;

    /**
     * 托管支付（网银支付方式）
     *
     * @param payTradeDto 托管支付请求对象
     * @return 生成post表单，在托管代收的时，选择网银，没有支付，再次支付则通知托管支付生成FORM表单，用于给浏览器进行跳转到对应的银行进行支付
     * FORM表单，类似<form id='frmBankID' name='frmBankName'
     * @throws PayFrontException 支付前置异常（请求对象检验、组装网关请求数据、支付网关返回结果解析）
     * @throws HttpException     向支付网关提交请求时出现的网络异常
     */
    String payTradeByBank(PayTradeDto payTradeDto) throws PayFrontException;

    /**
     * 退款
     *
     * @param refundTradeDto 退款请求对象
     * @return 退款处理结果
     * @throws PayFrontException 支付前置异常（请求对象检验、组装网关请求数据、支付网关返回结果解析）
     * @throws HttpException     向支付网关提交请求时出现的网络异常
     */
    ResultDto<RefundTradeResult> refundTrade(RefundTradeDto refundTradeDto)
            throws PayFrontException,
            HttpException;

    /**
     * 交易查询
     *
     * @param queryTradeDto 交易查询请求对象
     * @return 交易查询结果
     * @throws PayFrontException 支付前置异常（请求对象检验、组装网关请求数据、支付网关返回结果解析）
     * @throws HttpException     向支付网关提交请求时出现的网络异常
     */
    ResultDto<QueryTradeResult> queryTrade(QueryTradeDto queryTradeDto) throws PayFrontException,
            HttpException;

    /**
     * 充值处理
     *
     * @param createDepositDto 充值请求对象
     * @return 充值处理结果
     * @throws PayFrontException 支付前置异常（请求对象检验、组装网关请求数据、支付网关返回结果解析）
     *                           HttpException 向支付网关提交请求时出现的网络异常
     */
    ResultDto<PayResult> createDeposit(CreateDepositDto createDepositDto) throws PayFrontException,
            HttpException;

    /**
     * 充值处理(网银支付方式)
     * <p>生成post表单，用于给浏览器进行跳转到对应的银行进行充值支付 </p>
     *
     * @param createDepositDto 充值请求对象
     * @return FORM表单，类似<form id='frmBankID' name='frmBankName'
     * @throws PayFrontException 支付前置异常（请求对象检验、组装网关请求数据、支付网关返回结果解析）
     * @throws HttpException     向支付网关提交请求时出现的网络异常
     */
    String createDepositByBank(CreateDepositDto createDepositDto) throws PayFrontException;
    
    /**
     * 充值处理(收银台模式)
     *
     * @param createDepositDto 充值请求对象
     * @return FORM表单，类似<form id="form1" action='https://test.pay.sina.com.cn/cashdesk-web/view/recharge.html' method="GET">
     * @throws PayFrontException 支付前置异常（请求对象检验、组装网关请求数据、支付网关返回结果解析）
     * @throws HttpException     向支付网关提交请求时出现的网络异常
     */
    String createHostingDeposit(CreateDepositDto createDepositDto) throws PayFrontException;

    /**
     * 充值查询
     *
     * @param queryDepositDto 充值查询请求对象
     * @return 充值查询结果
     * @throws PayFrontException 支付前置异常（请求对象检验、组装网关请求数据、支付网关返回结果解析）
     * @throws HttpException     向支付网关提交请求时出现的网络异常
     */
    ResultDto<QueryDepositResult> queryDeposit(QueryDepositDto queryDepositDto)
            throws PayFrontException,
            HttpException;

    /**
     * 提现处理
     *
     * @param createWithDrawDto 提现处理请求对象
     * @return 提现处理结果
     * @throws PayFrontException 支付前置异常（请求对象检验、组装网关请求数据、支付网关返回结果解析）
     * @throws HttpException     向支付网关提交请求时出现的网络异常
     */
    ResultDto<PayResult> createWithDraw(CreateWithDrawDto createWithDrawDto)
            throws PayFrontException,
            HttpException;
    
    /**
     * 提现处理(收银台模式)
     *
     * @param createWithDrawDto 提现处理请求对象
     * @return 提现处理结果
     * @throws PayFrontException 支付前置异常（请求对象检验、组装网关请求数据、支付网关返回结果解析）
     * @throws HttpException     向支付网关提交请求时出现的网络异常
     */
    String createHostingWithdraw(CreateWithDrawDto createWithDrawDto) throws PayFrontException, HttpException;

    /**
     * 提现查询
     *
     * @param queryWithDrawDto 提现查询请求对象
     * @return 提现查询结果
     * @throws PayFrontException 支付前置异常（请求对象检验、组装网关请求数据、支付网关返回结果解析）
     * @throws HttpException     向支付网关提交请求时出现的网络异常
     */
    ResultDto<QueryWithDrawResult> queryWithDraw(QueryWithDrawDto queryWithDrawDto)
            throws PayFrontException,
            HttpException;

    /**
     * 支付查询
     *
     * @param queryPayTradeDto 支付查询请求对象
     * @return 支付查询结果
     * @throws PayFrontException 支付前置异常（请求对象检验、组装网关请求数据、支付网关返回结果解析）
     * @throws HttpException     向支付网关提交请求时出现的网络异常
     */
    ResultDto<QueryPayTradeResult> queryPayTrade(QueryPayTradeDto queryPayTradeDto)
            throws PayFrontException,
            HttpException;

    /**
     * 退款查询
     *
     * @param queryRefundDto 退款查询请求对象
     * @return 退款查询结果
     * @throws PayFrontException 支付前置异常（请求对象检验、组装网关请求数据、支付网关返回结果解析）
     * @throws HttpException     向支付网关提交请求时出现的网络异常
     */
    ResultDto<QueryRefundResult> queryRefund(QueryRefundDto queryRefundDto)
            throws PayFrontException,
            HttpException;

    /**
     * 查询基金代码
     *
     * @param fincode
     * @return
     * @throws Exception
     */
    ResultDto<?> queryFundYield(String fincode) throws Exception;

    /**
     * 支付推进
     * @param outAdvanceNo
     * @param ticket
     * @param validateCode
     * @param userIp
     * @return
     * @throws PayFrontException
     * @throws HttpException
     */
    ResultDto<?>  advanceHostingPay(String outAdvanceNo, String ticket, String validateCode, String userIp) throws Exception;

    /**
     * 托管交易批次查询
     * @param outBatchNo  批次号
     * @return
     */
    ResultDto<QueryTradeResult> queryHostingBatchTrade(String outBatchNo)throws Exception;

	/**
	 * 代付完成
	 *
	 * @param FinishAuthTradeDto
	 *            请求对象
	 * @return 没有对应的具体处理结果，只包含接口调用成功失败
	 * @throws PayFrontException
	 *             支付前置异常（请求对象检验、组装网关请求数据、支付网关返回结果解析）
	 * @throws HttpException
	 *             向支付网关提交请求时出现的网络异常
	 */
	@SuppressWarnings("all")
	ResultDto<Object> finishPreAuthTrade(FinishAuthTradeDto preAuthTradeDto) throws PayFrontException, HttpException;

	/**
	 * 代付撤销
	 *
	 * @param FinishAuthTradeDto
	 *            请求对象
	 * @return 没有对应的具体处理结果，只包含接口调用成功失败
	 * @throws PayFrontException
	 *             支付前置异常（请求对象检验、组装网关请求数据、支付网关返回结果解析）
	 * @throws HttpException
	 *             向支付网关提交请求时出现的网络异常
	 */
	@SuppressWarnings("all")
	ResultDto<Object> cancelPreAuthTrade(CancelAuthTradeDto preAuthTradeDto) throws PayFrontException, HttpException;

}
