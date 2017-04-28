package com.yourong.core.tc.manager.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rop.thirdparty.com.google.common.collect.Lists;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.SinaPayEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.thirdparty.sinapay.SinaCashDeskClient;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.mc.manager.ActivityAfterTransactionManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.OrderManager;
import com.yourong.core.tc.manager.TradeManager;
import com.yourong.core.tc.manager.TradeProcManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.biz.TradeBiz;
import com.yourong.core.uc.manager.MemberManager;

@Service
public class TradeManagerImpl implements TradeManager {

	@Autowired
	private OrderManager orderManager;

	@Autowired
	private ProjectManager projectManager;

	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private TransactionManager transactionManager;

	@Autowired
	private CouponManager couponManager;

	@Autowired
	private HostingCollectTradeManager hostingCollectTradeManager;

	@Autowired
	private ActivityAfterTransactionManager activityAfterTransactionManager;

	@Autowired
	private SinaPayClient sinaPayClient;

	@Autowired
	private SinaCashDeskClient sinaCashDeskClient;

	@Autowired
	private MemberManager memberManager;

	@Autowired
	private TradeProcManager tradeProcManager;

	private Logger logger = LoggerFactory.getLogger(TransactionInterestManagerImpl.class);

	@Override
	public ResultDO<TradeBiz> tradeUseCapital(Order order, int sourceType, String payerIp, String returnUrl) {
		ResultDO<TradeBiz> rDO = new ResultDO<TradeBiz>();
		TradeBiz biz = new TradeBiz();
		rDO.setResult(biz);
		try {
			// 查询是否开通委托扣款
			boolean isWithholdAuthority = false;
			try {
				ResultDO<Boolean> synWithholdAuthorityDO = memberManager.synWithholdAuthority(order.getMemberId());
				if (synWithholdAuthorityDO.isSuccess()) {
					isWithholdAuthority = synWithholdAuthorityDO.getResult();
				}
			} catch (Exception e) {
				logger.error("发起新浪代收同步用户是否开通委托失败, memberId={}", order.getMemberId(), e);
			}
			biz.setIsWithholdAuthority(isWithholdAuthority);
			// 如果是充值交易，非委托扣款则直接返回
//			if (TypeEnum.TRADE_SOURCE_TYPE_RECHARGE_NOTIFY.getType() == sourceType && !isWithholdAuthority) {
//				logger.info("充值交易因为未开通非委托支付授权,停止发起代收交易 orderNo={}", order.getOrderNo());
//				return rDO;
//			}
			// 创建本地代收
			rDO = tradeProcManager.createTransactionHostingTradeLocal(order, sourceType, payerIp, isWithholdAuthority);
			// 本地创建代收失败，或者现金券等于投资金额的场景直接返回,不需要发起新浪代收
			if(rDO.getResult().isAllCashCouponPay()){//现金券等于投资额情况下的特殊处理
				transactionManager.afterTransactionContractIsFullCoupon(order);
			}
			if (rDO.isError()) {
				return rDO;
			}
			Order returnOrder = new Order();
			returnOrder.setOrderNo(order.getOrderNo());
			returnOrder.setStatus(order.getStatus());
			if (rDO.getResult().isAllCashCouponPay()) {
				rDO.getResult().setOrder(returnOrder);
				return rDO;
			}
			rDO = tradeProcManager.createSinpayHostingCollectTradeAfterLocalSuccess(order, sourceType, payerIp, isWithholdAuthority,
					returnUrl);
			if (rDO.isSuccess() && rDO.getResult() != null) {
				rDO.getResult().setOrder(returnOrder);
			}
		} catch (Exception e) {
			logger.error("使用存钱罐余额发起代收交易失败， orderNo={}, sourceType={}", order.getOrderNo(), sourceType, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public ResultDO<TradeBiz> isRepeatToCashDest(Order order, String payerIp) {
		ResultDO<TradeBiz> rDO = new ResultDO<TradeBiz>();
		TradeBiz biz = new TradeBiz();
		rDO.setResult(biz);
		try {
			HostingCollectTrade collectTrade = hostingCollectTradeManager.getBySourceIdAndType(order.getId(),
					TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType());
			if (collectTrade == null) {
				rDO.setResultCode(ResultCode.ORDER_COLLECT_TRADE_NULL_ERROR);
				return rDO;
			}
			if (!SinaPayEnum.TRANSACTION_WAIT_PAY.getCode().equals(collectTrade.getTradeStatus())) {
				rDO.setResultCode(ResultCode.ORDER_COLLECT_TRADE_WAIT_PAY_ERROR);
				return rDO;
			}
			if (collectTrade.getIsWithholdAuthority() == null
					|| StatusEnum.WITHHOLD_AUTHORITY_FLAG_N.getStatus() != collectTrade.getIsWithholdAuthority()) {
				rDO.setResultCode(ResultCode.ORDER_IS_NOT_WITHHOLD_AUTHORITY_ERROR);
				return rDO;
			}
			List<String> tradeNoList = Lists.newArrayList(collectTrade.getTradeNo());
			String resp = sinaCashDeskClient.payHostingTrade(collectTrade.getPayerId(), tradeNoList, collectTrade.getAmount(), payerIp);
			biz.setNotFirstRequest(true);
			biz.setRedirectUrl(resp);
		} catch (Exception e) {
			logger.error("判断是否获取二次支付重定向链接失败, orderNo={}", order.getOrderNo(), e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

}
