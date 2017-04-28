
package com.yourong.web.controller;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.yourong.common.baidu.yun.utility.MessageDigestUtility;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.http.common.HttpRequest;
import com.yourong.common.http.common.HttpResponse;
import com.yourong.common.http.common.Method;
import com.yourong.core.sh.manager.ShopOrderManager;
import com.yourong.core.sh.model.OrderMain;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.MessageEnum;
import com.yourong.common.enums.SinaPayEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.thirdparty.pay.sina.SinaPayConfig;
import com.yourong.common.thirdparty.pay.sina.SinaPayNotifyBaseParmas;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.enums.BatchTradeStatus;
import com.yourong.common.thirdparty.sinapay.common.enums.RefundStatus;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.common.thirdparty.sinapay.pay.core.common.Constants;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.SignUtil;
import com.yourong.core.common.MessageClient;
import com.yourong.core.common.PushClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.model.RechargeLog;
import com.yourong.core.fin.model.WithdrawLog;
import com.yourong.core.ic.model.Project;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.push.PushEnum;
import com.yourong.core.repayment.manager.AfterHostingPayHandleManager;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.engine.SPEngine;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.manager.OrderManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.HostingPayTrade;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberBankCard;
import com.yourong.web.dto.MemberSessionDto;
import com.yourong.web.service.ActivityLotteryService;
import com.yourong.web.service.DouwanService;
import com.yourong.web.service.HostingCollectTradeService;
import com.yourong.web.service.LeaseBonusService;
import com.yourong.web.service.MemberBankCardService;
import com.yourong.web.service.MemberNotifySettingsService;
import com.yourong.web.service.MemberService;
import com.yourong.web.service.OrderService;
import com.yourong.web.service.ProjectService;
import com.yourong.web.service.RechargeLogWithdrawLogService;
import com.yourong.web.service.RepaymentService;
import com.yourong.web.service.TransactionInterestService;
import com.yourong.web.service.TransactionService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Controller
@RequestMapping("notify")
/**
 * 第三方支付notify通知controller
 *
 */
public class NotifyController extends BaseController{

	@Autowired
	private TransactionService transactionService;
	@Autowired
	private RechargeLogWithdrawLogService rechargeLogWithdrawLogService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private TaskExecutor taskExecutor;
	@Autowired
	private HostingCollectTradeManager hostingCollectTradeManager;
	@Autowired
	private HostingPayTradeManager hostingPayTradeManager;
	@Autowired
	private OrderManager orderManager;
	@Autowired
	private TransactionInterestService transactionInterestService;
	@Autowired
	private CouponManager couponManager;
	@Autowired
	private MemberService memberService;
	@Autowired
	private TransactionManager myTransactionManager;
	@Autowired
	private TransactionInterestManager transactionInterestManager;
	@Autowired
	private LeaseBonusService leaseBonusService;
	@Autowired
	private DouwanService douwanService;
	@Autowired
	private MemberNotifySettingsService memberNotifySettingsService;
	@Autowired
	private SinaPayClient sinaPayClient;
	@Autowired
	private BalanceManager balanceManager;
	@Autowired
	private ActivityLotteryService activityLotteryService;

	@Autowired
	private  AfterHostingPayHandleManager afterHostingPayHandleManager;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private HostingCollectTradeService hostingCollectTradeService;

	@Autowired
	private RepaymentService repaymentService;

	@Autowired
	private MemberBankCardService memberBankCardService;
	@Autowired
	private ShopOrderManager shopOrderManager;

	/**
	 * 代收、代付交易回调接口
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/trade")
	public void tradeNotifyRequst(
			HttpServletRequest req,
			HttpServletResponse resp,
			SinaPayNotifyBaseParmas baseParmas
			) {
		String tradeNotify = tradeNotify(req,resp,baseParmas);
		responseWriteString(resp, tradeNotify);
	}

	/**
	 * 批次回调接口
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/batch")
	public void batchNotifyRequst(
			HttpServletRequest req,
			HttpServletResponse resp,
			SinaPayNotifyBaseParmas baseParmas
	) {
		String batchNotify = batchNotify(req,resp,baseParmas);
		responseWriteString(resp, batchNotify);
	}

	/**
	 * 批次回调接口
	 * @param req
	 * @param resp
	 * @return
	 */
	private String batchNotify(
			HttpServletRequest req,
			HttpServletResponse resp,
			SinaPayNotifyBaseParmas baseParmas
	) {
		try {
			String batchNo = req.getParameter("outer_batch_no");
			String outBatchNo = req.getParameter("inner_batch_no");
			String batchStatus = req.getParameter("batch_status");
			String tradeList = req.getParameter("trade_list");
			logger.info("第三方支付批次回调接口进入,batchNo={}, outBatchNo={}, tradeList={}", batchNo, outBatchNo, tradeList);
			//验证签名
			if(!validateSign(req, baseParmas, batchNo, outBatchNo)) {
				return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
			}

			//代收交易最终状态处理
			if(BatchTradeStatus.FINISHED.name().equals(batchStatus)) {
					if(transactionService.afterHostingPayBatchNotifyProcess(batchNo, outBatchNo, tradeList)) {
						return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
					} else {
						return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
					}
				}
			logger.info("第三方支付批次回调接口完成");
		} catch (Exception e) {
			logger.error("第三方支付批次回调接口发生异常" , e);
			return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
		}
		return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
	}

	private void responseWriteString(HttpServletResponse resp, String tradeNotify) {
		PrintWriter writer = null;
		try {
			writer = resp.getWriter();
			writer.write(tradeNotify);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			logger.info("返回第三方支付结果异常",e);
		}finally {
			if (writer!=null){
				writer.close();
			}
		}
	}

	/**
	 * 代收、代付交易回调接口
	 * @param req
	 * @param resp
	 * @return
	 */
	private String tradeNotify(
			HttpServletRequest req,
			HttpServletResponse resp,
			SinaPayNotifyBaseParmas baseParmas
			) {
		try {
			String tradeNo = req.getParameter("outer_trade_no");
			String outTradeNo = req.getParameter("inner_trade_no");
			String tradeStatus = req.getParameter("trade_status");
			String authFinishAmount = req.getParameter("auth_finish_amount");
			logger.info("第三方支付交易结果回调接口进入,tradeNo={}, tradeStatus={}, authFinishAmount={}", tradeNo, tradeStatus, authFinishAmount);
			//验证签名
			if(!validateSign(req, baseParmas, tradeNo, outTradeNo)) {
				return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
			}
			//如果是代收回调
			if(tradeNo.indexOf(SerialNumberUtil.PREFIX_COLLECT_TRADE_NO)!=-1) {
				HostingCollectTrade hostingCollectTrade = hostingCollectTradeManager.getByTradeNo(tradeNo);
				if(hostingCollectTrade==null) {
					return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
				}
				//如果是支付完成，则需要更新代收交易支付状态
				if(TradeStatus.PAY_FINISHED.name().equals(tradeStatus)) {
					return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
				}
				// 代收完成/撤销的代收交易最终交易状态
				if (TypeEnum.COLLECT_TRADE_TYPE_FREEZE.getType() == hostingCollectTrade.getIsPreAuth()
						&& (TradeStatus.TRADE_FINISHED.name().equals(tradeStatus) || TradeStatus.PRE_AUTH_CANCELED.name().equals(
								tradeStatus))) {
					return hostingCollectTradeService.handlePreAuthTrade(tradeStatus, hostingCollectTrade);
				}

				//代收交易最终状态处理
				if(TradeStatus.TRADE_FINISHED.name().equals(tradeStatus)
						||TradeStatus.TRADE_FAILED.name().equals(tradeStatus)
						|| TradeStatus.TRADE_CLOSED.name().equals(tradeStatus)
						|| TradeStatus.PRE_AUTH_APPLY_SUCCESS.name().equals(tradeStatus)) {

//					
//					if(TradeStatus.TRADE_FINISHED.name().equals(hostingCollectTrade.getTradeStatus())) {
//						return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
//					}
					//代收投资款处理流程（交易）
					if(TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType()==hostingCollectTrade.getType()) {
						//如果订单已经为最终状态，则直接return
						Order order = orderManager.getOrderByTradeNo(tradeNo);
						if(order==null) {
							if(TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType()==hostingCollectTrade.getType()) {
								return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
							}
						}
						if(order.getStatus().intValue()==StatusEnum.ORDER_WAIT_PAY.getStatus()) {
							return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
						} else {
							if (order.getStatus().intValue() != StatusEnum.ORDER_WAIT_PROCESS.getStatus()
									&& order.getStatus().intValue() != StatusEnum.ORDER_SINA_CASHDESK_CONFIRM.getStatus()) {
								return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
							}
						}

						ResultDO<Transaction> result = transactionService.afterTransactionCollectNotifyProcess(tradeNo, outTradeNo, tradeStatus);
						if(result!=null && result.isSuccess()) {
							if(result.getResult()!=null) {
								Transaction transaction = result.getResult();
								if(transaction!=null) {
									//生成合同
//									transactionService.generateContract(transaction.getId());
									//生成合同
									transactionService.afterTransactionContract(transaction);
									//活动引擎
									Project  dto =  projectService.selectByPrimaryKey(transaction.getProjectId());
									if (dto.isDirectProject()){

										//转让项目投资成功发送站内信和短信
										if(transaction.getProjectCategory()==2){
											MessageClient.sendMsgForCommon(transaction.getMemberId(), Constant.MSG_TEMPLATE_TYPE_STAND, MessageEnum.TRANSFER_INVEST.getCode(),
													DateUtils.getStrFromDate(DateUtils.getCurrentDate(),DateUtils.DATE_FMT_13),transaction.getProjectName(),
													transaction.getInvestAmount().toString(),transaction.getProjectId().toString());

											MessageClient.sendMsgForCommon(transaction.getMemberId(), Constant.MSG_TEMPLATE_TYPE_PHONE, MessageEnum.TRANSFER_SUCCESS.getCode(),
													DateUtils.getStrFromDate(DateUtils.getCurrentDate(),DateUtils.DATE_FMT_7)
													,(transaction.getProjectName().contains("期")?transaction.getProjectName().substring(0, transaction.getProjectName().indexOf("期") + 1):transaction.getProjectName()),
													transaction.getInvestAmount().toString());
										}else{
											//投资成功，发送站内信和短信通知投资者
											MessageClient.sendMsgForCommon(transaction.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.P2P_UNRAISE.getCode(),
													DateUtils.getStrFromDate(DateUtils.getCurrentDate(),DateUtils.DATE_FMT_7),
													(transaction.getProjectName().contains("期")?transaction.getProjectName().substring(0, transaction.getProjectName().indexOf("期") + 1):transaction.getProjectName()),
													transaction.getInvestAmount().toString(),transaction.getProjectId().toString());
										}

										SPParameter parameter = new SPParameter();
										parameter.setMemberId(transaction.getMemberId());
										SPEngine.getSPEngineInstance().run(parameter);
									}else {
										SPParameter parameter = new SPParameter();
										parameter.setMemberId(transaction.getMemberId());
										parameter.setBizType(TypeEnum.ACTIVITY_BIZ_TYPE_TRANSACTION.getType());
										parameter.setBiz(transaction);
										SPEngine.getSPEngineInstance().run(parameter);
										//都玩带过来的会员，第一次交易 回调都玩接口
										//this.douwanService.douwanFirstTransaction(transaction.getMemberId());
										MessageClient.sendMsgForInvestmentSuccess(DateUtils.getCurrentDate(), transaction.getProjectId(), transaction.getMemberId(), transaction.getTotalDays(), transaction.getTotalInterest(), transaction.getInvestAmount());
									}

									activityLotteryService.activityAfterTransaction(transaction);
								}
							}
							//自动投标回调处理
							memberService.autoInvestSetAfterTransaction(result.getResult(), order.getId());
							return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
						} else {
							//自动投标回调处理
							memberService.autoInvestSetAfterTransaction(null, order.getId());
							return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
						}

					}

					//平台垫付现金券
					if(TypeEnum.HOSTING_COLLECT_TRADE_COUPON_A.getType()==hostingCollectTrade.getType()) {
						ResultDO<?> result = transactionService.afterPaltformCouponCollectNotify(tradeNo, outTradeNo, tradeStatus);
						if(result!=null && result.isSuccess()) {
							return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
						}
					}
					//垫资还款代收
					if(TypeEnum.HOSTING_COLLECT_TRADE_OVERDUE_REPAY.getType()==hostingCollectTrade.getType()) {
						ResultDO<List<HostingPayTrade>> result = projectService.afterOverdueRepayCollectNotify(tradeNo, outTradeNo, tradeStatus);
						if(result!=null && result.isSuccess()) {
							projectService.createSinpayHostingPayTradeForRepay(result.getResult());
							return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
						}
					}
					//代收借款人还款或者平台代付收益券代收回调处理
					if(TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getType()==hostingCollectTrade.getType()
							||TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType()==hostingCollectTrade.getType()) {
						ResultDO<HostingCollectTrade> result = repaymentService.afterHostingCollectTradeForRepayment(tradeNo, outTradeNo, tradeStatus);
						if(result!=null && result.isSuccess()) {
							return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
						}
					}
					//保证金归还代收
					if(TypeEnum.HOSTING_COLLECT_TRADE_PROJECT_GUARNANTEEFEE.getType()==hostingCollectTrade.getType()) {
						ResultDO<HostingPayTrade> result = projectService.afterGuaranteeFeeCollectNotify(tradeNo, outTradeNo, tradeStatus);
						if(result!=null && result.isSuccess()) {
							projectService.createSinpayHostingPayTradeForGuaranteeFee(result.getResult());
							return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
						}
					}
					//保证金归还代收
					if(TypeEnum.HOSTING_COLLECT_TRADE_PROJECT_GUARNANTEEFEE.getType()==hostingCollectTrade.getType()) {
						ResultDO<HostingPayTrade> result = projectService.afterGuaranteeFeeCollectNotify(tradeNo, outTradeNo, tradeStatus);
						if(result!=null && result.isSuccess()) {
							projectService.createSinpayHostingPayTradeForGuaranteeFee(result.getResult());
							return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
						}
					}
				}

			}

			//如果是代付回调
			if(tradeNo.indexOf(SerialNumberUtil.PREFIX_PAY_TRADE_NO)!=-1) {
				HostingPayTrade hostingPayTrade = hostingPayTradeManager.getByTradeNo(tradeNo);
				if(hostingPayTrade==null) {
					return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
				}
				//如果是支付完成，则需要更新代收交易支付状态
				if(TradeStatus.PAY_FINISHED.name().equals(tradeStatus)) {
					return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
				}

				//代付交易最终状态处理
				if(TradeStatus.TRADE_FINISHED.name().equals(tradeStatus)
						||TradeStatus.TRADE_FAILED.name().equals(tradeStatus)
						||TradeStatus.TRADE_CLOSED.name().equals(tradeStatus)) {
					//放款业务后续处理流程
					if(TypeEnum.HOSTING_PAY_TRADE_LOAN.getType()==hostingPayTrade.getType()
							|| TypeEnum.HOSTING_PAY_TRADE_LOAN_BORROWER.getType()==hostingPayTrade.getType()
							|| TypeEnum.HOSTING_PAY_TRADE_MANAGER_FEE.getType() == hostingPayTrade.getType()
							|| TypeEnum.HOSTING_PAY_TRADE_GUARANTEEFEE.getType() == hostingPayTrade.getType()
							|| TypeEnum.HOSTING_PAY_TRADE_RISK_FEE.getType() == hostingPayTrade.getType()
							|| TypeEnum.HOSTING_PAY_TRADE_INTRODUCE_FEE.getType() == hostingPayTrade.getType()
							|| TypeEnum.HOSTING_PAY_TRADE_QUICK_REWARD.getType() == hostingPayTrade.getType()) {
						ResultDO<?> result = myTransactionManager.afterHostingPayTradeLoan(tradeStatus, tradeNo);
						if(result.isSuccess()) {
							return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
						}
					}
					//还本付息代付处理
					if (hostingPayTradeProcessing(tradeNo, tradeStatus, hostingPayTrade,outTradeNo))
						return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
					//直接代付回调
					if(TypeEnum.HOSTING_PAY_DIRECT_PAY.getType()==hostingPayTrade.getType()) {
						ResultDO<?> result = myTransactionManager.afterDirectHostingPay(tradeStatus, tradeNo);
						if(result.isSuccess()) {
							return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
						}
					}
					//租赁分红代付回调
					if(TypeEnum.HOSTING_PAY_LEASE_BONUS.getType()==hostingPayTrade.getType()) {
						ResultDO<?> result = leaseBonusService.afterLeaseBonusHostingPay(tradeStatus, tradeNo, outTradeNo);
						if(result.isSuccess()) {
							return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
						}
					}
					//垫资还款代付回调
					if(TypeEnum.HOSTING_PAY_TRADE_OVERDUE_REPAY.getType()==hostingPayTrade.getType()) {
						ResultDO<?> result = projectService.afterOverdueRepayHostingPay(tradeStatus, tradeNo, outTradeNo);
						if(result.isSuccess()) {
							return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
						}
					}
					//项目保险金归还代付回调
					if(TypeEnum.HOSTING_PAY_TRADE_GUARANTEEFEE_RETURN.getType()==hostingPayTrade.getType()) {
						ResultDO<?> result = projectService.afterGuaranteeFeeHostingPay(tradeStatus, tradeNo, outTradeNo);
						if(result.isSuccess()) {
							return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
						}
					}

					//转让项目代付回调
					if (TypeEnum.HOSTING_PAY_TRADE_TRANSFER_AMOUNT.getType() == hostingPayTrade.getType()
							|| TypeEnum.HOSTING_PAY_TRADE_TRANSFER_FEE.getType() == hostingPayTrade.getType()) {
						ResultDO<?> result = projectService.afterHostPayForTransferSuccess(tradeStatus, tradeNo, outTradeNo);
						if(result.isSuccess()) {
							return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
						}
					}
				}
			}

			logger.info("第三方支付交易结果回调接口完成");
		} catch (Exception e) {
			logger.error("第三方支付交易结果回调接口发生异常" , e);
			return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
		}
		return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
	}

	/**
	 *  代付处理
	 * @param tradeNo
	 * @param tradeStatus
	 * @param hostingPayTrade
	 * @return
	 * @throws Exception
	 */
	private boolean hostingPayTradeProcessing(String tradeNo, String tradeStatus, HostingPayTrade hostingPayTrade,String outTradeNo) throws Exception {
		//还本付息业务后续处理流程
		if(TypeEnum.HOSTING_PAY_TRADE_RETURN.getType()==hostingPayTrade.getType()) {
			ResultDO<TransactionInterest> result = afterHostingPayHandleManager.afterPayInterestAndPrincipal(tradeStatus, tradeNo,outTradeNo);
			if(result.isSuccess()&&result.getResult()!=null) {
				if(result.getResult()!=null){
					afterHostingPayHandleManager.afterHostingPayTradeSucess(result.getResult());
				}
				return true;
			}
		}
		return false;
	}

	@RequestMapping(value = "/refund")
	@ResponseBody
	public void refundNotifyRequest(HttpServletRequest req, HttpServletResponse resp, SinaPayNotifyBaseParmas baseParmas) {
		String tradeNotify = refundNotify(req, resp, baseParmas);
		responseWriteString(resp, tradeNotify);
	}

	/**
	 * @Description:退款
	 * @param req
	 * @param resp
	 * @param baseParmas
	 * @return
	 * @author: fuyili
	 * @time:2016年2月22日 下午5:11:55
	 */
	private String refundNotify(HttpServletRequest req, HttpServletResponse resp, SinaPayNotifyBaseParmas baseParmas) {
		try {
			String tradeNo = req.getParameter("outer_trade_no");
			String outTradeNo = req.getParameter("inner_trade_no");
			String refundStatus = req.getParameter("refund_status");
			logger.info("第三方支付退款结果回调接口进入 tradeNo={}, outTradeNo={}, refundStatus={}", tradeNo, outTradeNo, refundStatus);
			// 验证签名
			if (!validateSign(req, baseParmas, tradeNo, outTradeNo)) {
				logger.info("第三方支付退款结果回调接口验证签名不通过 tradeNo={}", tradeNo);
				return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
			}
			// 退款最终状态处理
			if (RefundStatus.SUCCESS.name().equals(refundStatus) || RefundStatus.FAILED.name().equals(refundStatus)) {
				ResultDO<?> result = myTransactionManager.afterHostingRefund(refundStatus, tradeNo, outTradeNo);
				if (result != null && result.isSuccess()) {
					return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
				}
			}
			logger.info("第三方支付退款结果回调接口完成");
		} catch (Exception e) {
			logger.error("第三方支付交易结果回调接口发生异常", e);
			return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
		}
		return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
	}

	/**
	 * 充值回调处理
	 *
	 * @param req
	 * @param resp
	 * @param baseParmas
	 */
	@RequestMapping(value = "/recharge")
	public void rechargeNotifyRequest(HttpServletRequest req, HttpServletResponse resp, SinaPayNotifyBaseParmas baseParmas) {
		String rechargeNotify = rechargeNotify(req, resp, baseParmas);
		responseWriteString(resp,rechargeNotify);
	}
	// 回调处理
	private String rechargeNotify(HttpServletRequest req, HttpServletResponse resp, SinaPayNotifyBaseParmas baseParmas) {
		try {
			// 充值订单号
			String tradeNo = req.getParameter("outer_trade_no");
			String outTradeNo = req.getParameter("inner_trade_no");
			// 充值状态
			String depositStatus = req.getParameter("deposit_status");

			logger.info("第三方支付充值结果回调接口进入,status=" + depositStatus);
			// 验证签名
			if(!validateSign(req, baseParmas, tradeNo, outTradeNo)) {
				return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
			}
			// 验证签名并获取充值信息，如果没有记录，则返回失败码，有记录则将
			RechargeLog rechargeLog = rechargeLogWithdrawLogService.getRechargeLogByTradeNo(tradeNo);
			if (rechargeLog == null) {
				logger.error("充值流水不存在, tradeNo=" + tradeNo);
				return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
			}
			// 如果记录状态为充值失败,无需更改状态，直接返回
			if(StatusEnum.RECHARGE_STATUS_FAIL.getStatus() == rechargeLog.getStatus()) {
				logger.error("该笔充值状态已经为失败, tradeNo=" + tradeNo);
				return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
			}
			Long memberId = rechargeLog.getMemberId();
			// 获取支付方式，并解银行相关信息
			String bankCardId = null; // 绑卡支付的银行卡ID，网银支付为空
			String bankCode = null; // 网银支付的银行编码，绑卡支付为空
			Integer payType = null; // 支付方式 1-网银直连，2-绑卡支付
			String payMethod = req.getParameter("pay_method");
			BankCardInfo bankCardInfo = getRechargePayMethodInfo(payMethod);
			if (bankCardInfo != null) {
				bankCardId = bankCardInfo.getBangkCardId();
				bankCode = bankCardInfo.getBankCode(); // 第一次绑卡或网银支付时,可获取到银行代码
				payType = bankCardInfo.getPayType();
			}
			String rechargePayMethod = "";
			if (payType != null) {
				rechargePayMethod = payType==1?TypeEnum.RECHARGELOG_PAY_METHOD_EBANK.getDesc():TypeEnum.RECHARGELOG_PAY_METHOD_QUICK.getDesc();
			}
			logger.info("【托管充值】回调后返回的银行信息,memberId={},rechargeNo={},cardId={},bankCode={},payMethod={},sina_pay_method={}", memberId, tradeNo,
					bankCardId, bankCode, rechargePayMethod, payMethod);
			Long memberBankCardId = null;
			// 同步更新银行卡
			MemberBankCard memberBankCard = memberBankCardService.synMemberBankCard(memberId, bankCardId);
			if (memberBankCard != null) {
				// 如果是绑卡支付或第一次绑卡支付，获得平台会员绑卡ID和绑卡银行code
				if (payType!=null && TypeEnum.RECHARGELOG_PAY_METHOD_QUICK.getType()==payType) {
					memberBankCardId = memberBankCard.getId();
					bankCode = memberBankCard.getBankCode()!=null ? memberBankCard.getBankCode() : bankCode;
				}
			}
			logger.info("【托管充值】银行卡同步后的信息,memberId={},rechargeNo={},memberBankCardId={},bankCode={},sina_pay_method={}",
					memberId, tradeNo, memberBankCardId, bankCode, payMethod);
			// 如果记录状态充值成功，则无需更改状态，直接告诉第三方接口notify通知结束，无需再回调
			if (StatusEnum.RECHARGE_STATUS_SUCESS.getStatus() == rechargeLog.getStatus()) {
				//活动引擎
				SPParameter parameter = new SPParameter();
				parameter.setMemberId(memberId);
				SPEngine.getSPEngineInstance().run(parameter);
				logger.info("【托管充值】平台数据库中充值状态已成功,memberId={},rechargeNo={}", memberId, tradeNo);
				// 如果是交易充值，订单信息不为空
				return handleRechargeOrder(rechargeLog);
			}

			// 如果充值后状态为成功
			if (SinaPayEnum.PAY_SUCCESS.getCode().equals(StringUtils.trim(depositStatus))) {
				//将充值记录置为成功
				if (rechargeLogWithdrawLogService.rechargeSuccess(rechargeLog.getRechargeNo(), outTradeNo,
					baseParmas.getMemo(), memberBankCardId, bankCode, payType)) {
					//发送消息
//					SendMsgClient.sendRechargeMsg(memberId, DateUtils.getCurrentDate(), rechargeLog.getAmount());
					//活动引擎
					SPParameter parameter = new SPParameter();
					parameter.setMemberId(memberId);
					SPEngine.getSPEngineInstance().run(parameter);
					logger.info("充值成功,memberId={},rechargeNo={},bankCode={},sina_card_id={},memberBankCardId={},pay_method={}",
							memberId, tradeNo, bankCode, bankCardId, memberBankCardId, payMethod);
					// 如果是交易充值，订单信息不为空
					return handleRechargeOrder(rechargeLog);
				}
			}

			// 如果充值后状态为失败
			if (SinaPayEnum.PAY_FAILED.getCode().equals(StringUtils.trim(depositStatus))) {
				//将充值记录置为失败，无论是否为交易充值，订单状态不会改变
				if(rechargeLogWithdrawLogService.rechargeFailed(rechargeLog.getRechargeNo(), outTradeNo,
				   baseParmas.getError_message(), memberBankCardId, bankCode, payType)) {
					logger.info("充值失败,memberId={},rechargeNo={},bankCode={},sina_card_id={},memberBankCardId={},pay_method={},errorCode={},errorMessage={}",
							memberId, tradeNo, bankCode, bankCardId, memberBankCardId, payMethod, baseParmas.getError_code(), baseParmas.getError_message());
				}
			}

			logger.info("第三方支付充值结果回调接口完成");
		} catch (Exception e) {
			logger.error("第三方支付充值结果回调接口发生异常" , e);
			return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
		}
		return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
	}

	// 获取支付方式，如果绑定支付，获取充值后的cardId
	private BankCardInfo getRechargePayMethodInfo(String payMethod) {
		BankCardInfo bankCardInfo = new BankCardInfo();
		if (StringUtil.isNotBlank(payMethod)) {
			int lastIndex = payMethod.lastIndexOf(Constants.ANGLE_BRACKETS);
			if (payMethod.startsWith(SinaPayEnum.PAY_TYPE_QUICK_PAY.getCode())) { // 第一次充值，选择绑卡支付
				bankCardInfo.setPayType(TypeEnum.RECHARGELOG_PAY_METHOD_QUICK.getType());
				bankCardInfo.setBankCode(payMethod.substring(lastIndex + 1, payMethod.indexOf(Constants.COMMA)));
				return bankCardInfo;
			} else if (payMethod.startsWith(SinaPayEnum.PAY_TYPE_BINDING_PAY.getCode())) { // 绑定支付
				bankCardInfo.setPayType(TypeEnum.RECHARGELOG_PAY_METHOD_QUICK.getType());
				bankCardInfo.setBangkCardId(payMethod.substring(lastIndex + 1));
				return bankCardInfo;
			} else if (payMethod.startsWith(SinaPayEnum.PAY_TYPE_ONLINE_BANK.getCode())) { // 网银支付
				bankCardInfo.setPayType(TypeEnum.RECHARGELOG_TYPE_DIRECTLY.getType());
				bankCardInfo.setBankCode(payMethod.substring(lastIndex + 1, payMethod.indexOf(Constants.COMMA)));
				return bankCardInfo;
			}
		}
		return null;
	}

	// 充值后新浪返回的支付方式，解析的银行卡信息
	private class BankCardInfo {
		// 支付方式： 1-网银直连，2-绑卡支付
		private Integer payType;
		// 银行编码
		private String bankCode;
		// 新浪绑卡ID
		private String bangkCardId;

		public Integer getPayType() {
			return payType;
		}
		public void setPayType(Integer payType) {
			this.payType = payType;
		}
		public String getBankCode() {
			return bankCode;
		}
		public void setBankCode(String bankCode) {
			this.bankCode = bankCode;
		}
		public String getBangkCardId() {
			return bangkCardId;
		}
		public void setBangkCardId(String bangkCardId) {
			this.bangkCardId = bangkCardId;
		}
	}

	// 处理交易充值订单
	private String handleRechargeOrder (RechargeLog rechargeLog) throws Exception {
		Order order = orderService.getOrderByOrderNo(rechargeLog.getOrderNo());
		// 如果不是交易充值，返回
		if (TypeEnum.RECHARGELOG_TYPE_TRADING.getType()!=rechargeLog.getType() || order==null) {
			MessageClient.sendMsgForRechargeSuccess(rechargeLog.getMemberId(), rechargeLog.getAmount());
			return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
		} else {
			// 如果交易订单状态不是支付中，则直接返回
			if (StatusEnum.ORDER_WAIT_PAY.getStatus() != order.getStatus()) {
				logger.error("订单已经为最终状态, orderNo=" + order.getOrderNo());
				return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
			}
			// 支付成功，如果充值的用户已开通委托支付，创建代收交易
			logger.info("[充值notify]创建代收交易, tradeNo={}, orderNo={},", rechargeLog.getRechargeNo(), order.getOrderNo());
			transactionService.createTransactionHostingTrade(order, TypeEnum.TRADE_SOURCE_TYPE_RECHARGE_NOTIFY.getType(),
					rechargeLog.getPayerIp());
			return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
		}
	}

	// 验证签名
	private boolean validateSign(HttpServletRequest req, SinaPayNotifyBaseParmas baseParmas, String tradeNo, String outTradeNo) {
		// 将request中参数转换成map
		Map<String, String> params = Collections3.transformRequestMap(req.getParameterMap());
		logger.info("第三方回调入参：" + params);
		if( params == null){
			return false;
		}
		// 验证签名是否匹配
		try {
			String signType = params.get("sign_type");
			boolean isRsa = "RSA".equalsIgnoreCase(signType);
			if (isRsa){
				if (!SignUtil.generateSignByRsa(params,baseParmas.getSign(),sinaPayClient.getRsaPublicSigin())) {
					logger.info("签名不匹配，sign:{} , tradeNo={},outTradeNo={}",baseParmas.getSign(),tradeNo, outTradeNo);
					return false;
				}
			}else{
				String mySign=SignUtil.generateSign(params, SinaPayConfig.md5Key);
				if (!mySign.equals(baseParmas.getSign())) {
					logger.info("签名不匹配，sign:" + baseParmas.getSign() + ",mySign=" + mySign + ",tradeNo=" + tradeNo+ ",outTradeNo=" + outTradeNo);
					return false;
				}
			}
		} catch (Exception e) {
			logger.error("第三方支付回调接口验证签名发生异常" , e);
			return false;
		}
		return true;
	}

	@RequestMapping(value = "/withdraw")
	public void withdrawNotifyReqest(HttpServletRequest req, HttpServletResponse resp, SinaPayNotifyBaseParmas baseParmas) {
		String withdrawNotify = withdrawNotify(req, resp, baseParmas);
		responseWriteString(resp,withdrawNotify);
	}

	// 提现回调处理
	private String withdrawNotify(HttpServletRequest req, HttpServletResponse resp, SinaPayNotifyBaseParmas baseParmas) {
		try {
			String tradeNo = req.getParameter("outer_trade_no");
			String outTradeNo = req.getParameter("inner_trade_no");
			String withdrawStatus = req.getParameter("withdraw_status");
			String cardId = req.getParameter("card_id");
			logger.info("第三方支付提现结果回调接口进入");
			logger.info("【托管提现】, tradeNo={},sina_card_id={}", tradeNo, cardId);
			// 验证签名
			if(!validateSign(req, baseParmas, tradeNo, outTradeNo)) {
				return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
			}
			// 验证签名并获取充值信息，如果没有记录，则返回失败码，有记录则将
			WithdrawLog withdrawLog = rechargeLogWithdrawLogService.getWithdrawLogByOutTradeNo(tradeNo);
			if (withdrawLog == null) {
				logger.error("提现流水不存在, tradeNo=" + tradeNo);
				return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
			}
			// 如果记录状态为提现失败或者提现成功，则无需更改状态，直接告诉第三方接口notify通知结束，无需再回调
			if (StatusEnum.WITHDRAW_STATUS_FAIL.getStatus() == withdrawLog.getStatus()
					|| StatusEnum.WITHDRAW_STATUS_SUCESS.getStatus() == withdrawLog.getStatus()) {
				logger.info("该笔提现状态已经更新, tradeNo=" + tradeNo);
				return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
			}

			Long memberId = withdrawLog.getMemberId();
			Long memberBankCardId = null; // 银行卡ID
			// 同步更新银行卡
			MemberBankCard memberBankCard = memberBankCardService.synMemberBankCard(memberId, cardId);
			if (memberBankCard != null) {
				memberBankCardId = memberBankCard.getId();
			}

			// 将提现记录置为支付中
			if (SinaPayEnum.WITHDRAW_PROCESSING.getCode().equals(StringUtils.trim(withdrawStatus))) {
				if(rechargeLogWithdrawLogService.withdrawProcess(tradeNo, baseParmas.getMemo(), memberBankCardId)) {
					logger.info("提现支付中, memberId={},tradeNo={},memberBankCardId={}", memberId, tradeNo, memberBankCardId);
					return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
				}
			}

			//将提现记录置为成功
			if (SinaPayEnum.WITHDRAW_SUCCESS.getCode().equals(StringUtils.trim(withdrawStatus))) {
				if(rechargeLogWithdrawLogService.withdrawSuccess(tradeNo, baseParmas.getMemo(), memberBankCardId)) {
					//提现成功发送消息
//						SendMsgClient.sendWithdrawSuccessMsg(withdrawLog.getMemberId(), withdrawLog.getWithdrawAmount());
					//提现成功 推送取消  by chanpin yang
					//PushClient.pushMsgToMember(DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_7)+"您有一笔提现成功了", memberId, tradeNo, PushEnum.WITHDRAW_SUCCESS);
					MessageClient.sendMsgForWithdrawalsSuccess(withdrawLog.getWithdrawAmount(), withdrawLog.getBankCardId(), DateUtils.getCurrentDate(), memberId);
					logger.info("提现成功, memberId={},tradeNo={},memberBankCardId={}", memberId, tradeNo, memberBankCardId);
					return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
				}
			}

			// 将提现记录置为失败
			if (SinaPayEnum.WITHDRAW_FAILED.getCode().equals(StringUtils.trim(withdrawStatus))) {
				if(rechargeLogWithdrawLogService.withdrawFailed(tradeNo, baseParmas.getError_message(), memberBankCardId)) {
					//提现失败
//						SendMsgClient.sendWithdrawFailedMsg(withdrawLog.getMemberId(), DateUtils.getCurrentDate());
					//提现失败 推送取消  by chanpin yang
					//PushClient.pushMsgToMember(DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_7)+"您有一笔提现失败了", memberId, tradeNo, PushEnum.WITHDRAW_FAILED);
					MessageClient.sendMsgForWithdrawalsError(withdrawLog.getWithdrawAmount(), withdrawLog.getBankCardId(), DateUtils.getCurrentDate(), memberId,baseParmas.getError_message());
					logger.info("提现失败, tradeNo=" + tradeNo + ", errorCode=" + baseParmas.getError_code() + ",errorMessage=" + baseParmas.getError_message());
					return Constant.NOTIFY_RETURN_RESULT_SUCCESS_CODE;
				}
			}

			logger.info("第三方支付提现结果回调接口完成");
		} catch (Exception e) {
			logger.error("第三方支付提现结果回调接口发生异常" , e);
			return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
		}
		return Constant.NOTIFY_RETURN_RESULT_FAIL_CODE;
	}

	@RequestMapping(value = "/thirdPayRedict")
	public String thirdPayRedict(){
		return "/thirdPayRedict";
	}

	/**
	 * 邮箱认证
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/email/verify")
	@ResponseBody
	public ModelAndView bindEmail(
			HttpServletRequest req,
			HttpServletResponse resp
			) {
		ResultDO<Member> result = null;
		ModelAndView modelAndView = new ModelAndView();
		String emailToken = ServletRequestUtils.getStringParameter(req, "token", "");
		if(emailToken==""){
			modelAndView.setViewName("/404");
			return modelAndView;
		}
		try {
			result = memberService.bindEmail(emailToken);
			if(result.isSuccess()) {
				//update session email
				if(getMember()!=null && result.getResult().getId().equals(getMember().getId())){
					MemberSessionDto memberSession = getMember();
					memberSession.setEmail(result.getResult().getEmail());
					updateMemberSessionDto(req, memberSession);
				}
				modelAndView.addObject("email", result.getResult().getEmail());
				modelAndView.setViewName("/member/emailBinded");
			} else {
				modelAndView.addObject("result", result);
				modelAndView.setViewName("/member/emailBinded");
			}
		} catch (Exception e) {
			logger.error("邮箱认证失败, emailToken="+emailToken, e);
		}
		return modelAndView;
	}

	/**
	 * 取消订阅
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/unsubscribe")
	public String unsubscribe(HttpServletRequest req, HttpServletResponse resp){
		String emailToken = ServletRequestUtils.getStringParameter(req, "token", "");
		if(StringUtil.isNotBlank(emailToken)){
			String memberId = RedisMemberClient.getEmailUnsubscribe(emailToken);
			if(StringUtil.isNotBlank(memberId) && StringUtil.isNumeric(memberId)){
				memberNotifySettingsService.unsubscribe(Long.parseLong(memberId));
				return "/member/unsubscribe";
			}
		}
		return "redirect:/index";
	}

	@ResponseBody
	@RequestMapping(value = "/duobei")
	public void duobeiNotify(HttpServletRequest req, HttpServletResponse resp,@RequestParam(value = "serialnumber")String serialnumber,
							   @RequestParam(value = "result")String result, @RequestParam(value = "tranid")String tranid,
							   @RequestParam(value = "sign")String sign){
		try {
			OrderMain orderMain= shopOrderManager.selectByPrimaryKey(Long.parseLong(serialnumber));
			if (orderMain==null){
				resp.sendError(403);
				resp.getWriter().flush();
			}
			if (result.equals("1")){      //成功
				shopOrderManager.rechargeResult(Long.parseLong(serialnumber),2);
			}
			if (result.equals("2")){      //失败
				shopOrderManager.rechargeResult(Long.parseLong(serialnumber),5);
			}
			resp.getWriter().flush();
		} catch (Exception e) {
			logger.error("多贝回调异常, 订单号="+serialnumber, e);
			try {
				resp.sendError(403);
				resp.getWriter().flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}