package com.yourong.backend.tc.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.yourong.backend.tc.service.OrderService;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.Constant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.CouponEnum;
import com.yourong.common.enums.MessageEnum;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.enums.BankCode;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.common.MessageClient;
import com.yourong.core.common.PushClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.push.PushEnum;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.HostingRefundManager;
import com.yourong.core.tc.manager.OrderManager;
import com.yourong.core.tc.manager.TradeManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.biz.OrderForMember;
import com.yourong.core.tc.model.biz.TradeBiz;
import com.yourong.core.uc.manager.AutoInvestLogManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.AutoInvestLog;
import com.yourong.core.uc.model.biz.AutoInvestMember;

@Service
public class OrderServiceImpl implements OrderService {

	private static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
	
	@Autowired 
	private OrderManager orderManager;
	@Autowired
	private CouponManager couponManager;
	@Autowired
	private ProjectManager projectManager;
	@Autowired
	private SysDictManager sysDictManager;
	@Autowired
	private MemberManager memberManager;
	@Autowired
	private AutoInvestLogManager autoInvestLogManager;
	@Autowired
	private BalanceManager balanceManager;
	@Autowired
	private TradeManager tradeManager;
	@Autowired
	private TransactionManager transactionManager;
	@Autowired
	private HostingCollectTradeManager hostingCollectTradeManager;
	@Autowired
	private HostingRefundManager hostingRefundManager;
	
	@Override
	public synchronized void schedueCloseOrder() {
		try {
			//查询需要关闭的订单
			List<Order> orderLists = orderManager.selectSchedueCloseOrder();
			if(Collections3.isNotEmpty(orderLists)) {
				for (Order order : orderLists) {
					if(orderManager.schedueCloseOrder(order.getId(),StatusEnum.ORDER_WAIT_PAY.getStatus(), StatusEnum.ORDER_CLOSED.getStatus())>0) {
						//解锁收益券
						if(StringUtil.isNotBlank(order.getProfitCouponNo())) {
							couponManager.unLockCoupon(order.getProfitCouponNo());
						}
						//解锁现金券
						if(StringUtil.isNotBlank(order.getCashCouponNo())) {
							couponManager.unLockCoupon(order.getCashCouponNo());
						}
						//发送邮件
//						SendMsgClient.sendOrderInvalidMsg(order.getMemberId());
						//取消提交订单失效 bychanpin yang
						/*PushClient.pushMsgToMember("您" + DateUtils.formatDatetoString(order.getOrderTime(), "HH:mm") + "提交的订单失效了，订单号："
								+ order.getOrderNo(), order.getMemberId(), order.getOrderNo(), PushEnum.CANCEL_ORDER);*/
					}
				}
			}
		} catch (Exception e) {
			logger.error("定时关闭订单出错", e);
		}
	}
	@Override
	public Page<OrderForMember> findByPage(Page<OrderForMember> pageRequest,
			Map<String, Object> map) {
		try {
			if(map.get("memberId") != null || map.get("mobile") != null) {
				map.put("memberSel", "INNER JOIN");
			}
			if(map.get("checkAll") == null ) {
				//支付方式：组合查询
				String SQLStr = null;
				if (map.containsKey("checkCapital")
						&& !map.containsKey("checkCoupon")
						&& !map.containsKey("checkEBank")
						&& !map.containsKey("checkFastPayment")) {
					//仅存钱罐
					SQLStr = " IFNULL(o.pay_amount, 0) = 0 and IFNULL(o.cash_coupon_no,'') = '' and o.used_capital > 0  ";
				}
				if (!map.containsKey("checkCapital")
						&& map.containsKey("checkCoupon")
						&& !map.containsKey("checkEBank")
						&& !map.containsKey("checkFastPayment")) {
					//仅现金券
					SQLStr = " IFNULL(o.used_capital, 0) = 0 and IFNULL(o.pay_amount, 0) = 0 and IFNULL(o.cash_coupon_no,'') != ''  ";
				}
				if (!map.containsKey("checkCapital")
						&& !map.containsKey("checkCoupon")
						&& map.containsKey("checkEBank")
						&& !map.containsKey("checkFastPayment")) {
					//仅网银
					SQLStr = " IFNULL(o.used_capital, 0) = 0 and IFNULL(o.cash_coupon_no,'') = '' and o.pay_amount > 0 and o.pay_method = 1  ";
				}
				if (!map.containsKey("checkCapital")
						&& !map.containsKey("checkCoupon")
						&& !map.containsKey("checkEBank")
						&& map.containsKey("checkFastPayment")) {
					//仅快捷支付
					SQLStr = " IFNULL(o.used_capital, 0) = 0 and IFNULL(o.cash_coupon_no,'') = '' and o.pay_amount > 0 and o.pay_method = 2  ";
				}
				if (map.containsKey("checkCapital")
						&& map.containsKey("checkCoupon")
						&& !map.containsKey("checkEBank")
						&& !map.containsKey("checkFastPayment")) {
					//存钱罐+现金券
					SQLStr = " IFNULL(o.pay_amount, 0) = 0 and IFNULL(o.cash_coupon_no,'') != '' and o.used_capital > 0  ";
				}
				if (map.containsKey("checkCapital")
						&& !map.containsKey("checkCoupon")
						&& map.containsKey("checkEBank")
						&& !map.containsKey("checkFastPayment")) {
					//存钱罐+网银
					SQLStr = " o.pay_amount > 0 and o.pay_method = 1 and o.used_capital > 0 and IFNULL(o.cash_coupon_no,'') = '' ";
				}
				if (map.containsKey("checkCapital")
						&& !map.containsKey("checkCoupon")
						&& !map.containsKey("checkEBank")
						&& map.containsKey("checkFastPayment")) {
					//存钱罐+快捷
					SQLStr = " o.pay_amount > 0 and o.pay_method = 2 and o.used_capital > 0 and IFNULL(o.cash_coupon_no,'') = '' ";
				}
				if (!map.containsKey("checkCapital")
						&& map.containsKey("checkCoupon")
						&& map.containsKey("checkEBank")
						&& !map.containsKey("checkFastPayment")) {
					//现金券+网银
					SQLStr = " IFNULL(o.cash_coupon_no,'') != '' and o.pay_amount > 0 and o.pay_method = 1 and IFNULL(o.used_capital, 0) = 0 ";
				}
				if (!map.containsKey("checkCapital")
						&& map.containsKey("checkCoupon")
						&& !map.containsKey("checkEBank")
						&& map.containsKey("checkFastPayment")) {
					//现金券+快捷
					SQLStr = " IFNULL(o.cash_coupon_no,'') != '' and o.pay_amount > 0 and o.pay_method = 2 and IFNULL(o.used_capital, 0) = 0 ";
				}
				if (map.containsKey("checkCapital")
						&& map.containsKey("checkCoupon")
						&& map.containsKey("checkEBank")
						&& !map.containsKey("checkFastPayment")) {
					//存钱罐余额+现金券+网银
					SQLStr = " IFNULL(o.cash_coupon_no,'') != '' and o.pay_amount > 0 and o.pay_method = 1 and o.used_capital > 0 ";
				}
				if (map.containsKey("checkCapital")
						&& map.containsKey("checkCoupon")
						&& !map.containsKey("checkEBank")
						&& map.containsKey("checkFastPayment")) {
					//存钱罐余额+现金券+快捷
					SQLStr = " IFNULL(o.cash_coupon_no,'') != '' and o.pay_amount > 0 and o.pay_method = 2 and o.used_capital > 0 ";
				}
				map.put("payMethod", SQLStr);
			}
			return orderManager.findByPage(pageRequest, map);
		}catch (ManagerException e) {
			logger.error("订单查询出错", e);
		}
		return null;
	}
	@Override
	public ResultDO<Order> cancelOrder(Long orderId) {
		ResultDO<Order> retVal = new ResultDO<Order>();
		try {
			if(orderManager.cancelOrder(orderId, "后台人工取消订单") > 0) {
				retVal.setSuccess(true);
			} else {
				retVal.setSuccess(false);
			}
		} catch (ManagerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			retVal.setSuccess(false);
		}
		return retVal;
	}
	@Override
	public ResultDO<Order> reflashOrder(Long orderId) {
		ResultDO<Order> retVal = new ResultDO<Order>();
		try {
			Order o = orderManager.selectByPrimaryKey(orderId);
			retVal.setResult(o);
			retVal.setSuccess(true);
		} catch (ManagerException e) {
			retVal.setSuccess(false);
			e.printStackTrace();
		}
		return retVal;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ResultDO<Map> selectTransPersonByOrderNo(String orderNo) {
		ResultDO<Map> retVal = new ResultDO<Map>();
		try {
			Map m = orderManager.selectTransPersonByOrderNo(orderNo);
			if(MapUtils.isNotEmpty(m) && m.get("bank_code") != null) {
				BankCode b = BankCode.getBankCode(m.get("bank_code").toString());
				m.put("bank_code", b.getRemarks());
			}
			retVal.setResult(m);
			retVal.setSuccess(true);
		} catch (ManagerException e) {
			retVal.setSuccess(false);
			e.printStackTrace();
		}
		return retVal;
	}
	
	@Override
	public synchronized void schedueAutoIntest() {
		try {
			// 判断是否上线
			SysDict sysDict = sysDictManager.findByGroupNameAndKey("autoInvestOnLineFlag", "userAutoInvestFlag");
        	String userAutoInvestFlag = ""; // 用户自动投标标识
        	if(sysDict != null) {
        		userAutoInvestFlag = sysDict.getValue();
        	}
        	if (StringUtil.isBlank(userAutoInvestFlag) || Integer.parseInt(userAutoInvestFlag)==0) {
        		logger.info("【定时自动投标】未上线！");
	    		return;
        	}
        	// 1）根据有效开始时间start_time和有效结束时间end_time，查询表uc_auto_invest_set获取当前有效期内开启自动投标用户
	    	List<AutoInvestMember> autoInvestUsers = autoInvestLogManager.getValidMember(StatusEnum.INVEST_FLAG_OPEN.getStatus(), new Date(), new Date());
	    	//------用户开始------
	    	for (int i=0; i<autoInvestUsers.size(); i++) {
	    		AutoInvestMember autoInvestMember = autoInvestUsers.get(i);
	    		Long memberId = autoInvestMember.getMemberId();
	    		// 自动投标设置信息json字符串
	    		String autoInvestSetInfo = JSON.toJSONString(autoInvestMember);
	    		// 校验用户
	    		if(!preAutoInvestMemberValidate(autoInvestMember, memberId, autoInvestSetInfo)) continue;
	    		// 6）如果账户余额足够投标，查询当前非新手投资的在投项目列表并按上线时间早晚排序查询当前非新手投资的在投项目列表
	    		List<Project> investingProjects = projectManager.getNoviceInvestingProject(StatusEnum.PROJECT_IS_NOT_NOVICE.getStatus(), 
	    				StatusEnum.PROJECT_STATUS_INVESTING.getStatus());
	    		//------项目开始------
	    		for (int j=0; j<investingProjects.size(); j++) {
	    			// 7）判断用户的项目类型是否与该项目投资类型一致
	    			Project project = investingProjects.get(j);
	    			// 如果项目类型和自动投标设置的项目类型一致
	    			if(autoInvestMember.getProjectType().contains(project.getInvestType().toString())) {
	    				
	    				// 8）类型一致，判断该项目的收益周期是否在用户的项目周期范围内
	    				int earningsDays = 0; // 收益天数
	    				// 如果该项目是直投项目
	    				if (ProjectEnum.PROJECT_TYPE_DIRECT.getType() == project.getInvestType()) {
	    					earningsDays = project.countProjectDays();
	    				} else if (ProjectEnum.PROJECT_TYPE_DEBT.getType() == project.getInvestType()){
	    					// 如果该项目是债权项目
	    					earningsDays = project.getEarningsDaysByStatus();
	    				}
	    				// 项目的收益天数不在自动投标设置的项目周期范围
	    				if (earningsDays > autoInvestMember.countPeriodMax() || earningsDays < autoInvestMember.countPeriodMin()) {
	    					String errorMsg = "项目周期不在你设置的项目周期范围内，当前收益天数是" + earningsDays + "天，设置的项目周期是最大" + autoInvestMember.countPeriodMax() + "天,最小周期是" + autoInvestMember.countPeriodMin() + "天";
	    					saveAutoInvestLog(autoInvestMember.getAutoInvestSetId(), memberId, null, project.getId(), null, StatusEnum.ORDER_PAYED_FAILED.getStatus(), autoInvestSetInfo, errorMsg);
	    					logger.info("【定时自动投标】项目周期不在你设置的项目周期范围内,当前收益天数={}天,设置的项目周期最大={}天,最小周期={}天", earningsDays, autoInvestMember.countPeriodMax(), autoInvestMember.countPeriodMin());
	    					continue;
	    				} 
	    				
	    				// 9）查询该用户是否有待处理或处理中的自动投标订单
//	    				if (orderManager.getHandleingOrderCount(memberId, StatusEnum.MEMBER_INVEST_FLAG_AUTO.getStatus()) > 0) {
//	    					saveAutoInvestLog(autoInvestMember.getAutoInvestSetId(), memberId, null, project.getId(), null, StatusEnum.ORDER_PAYED_FAILED.getStatus(), autoInvestSetInfo, 
//	    							"有未处理或处理中的自动投标订单，memberId=" + memberId);
//	    					continue;
//	    				}
	    				
	    				// 10）判断用户的投标频率若是每个项目一次，从redis中获取该用户是否自动投标过该项目的数据
	    				if (autoInvestMember.getInvestFrequency() == StatusEnum.INVEST_FREQUENCY_EVERYPROJECT.getStatus()) {
	    					String memberCurrentProjectInvestFlagRedisKey = memberId + RedisConstant.MEMBER_CURRENT_PROJECT_AUTO_INVEST_FLAG + project.getId();
	    					if(!RedisManager.isExit(memberCurrentProjectInvestFlagRedisKey)) {
	    						// 11）如果redis数据为空，查询表uc_auto_invest_log，判定当天该用户是否已经自动投标过该项目
	    						int isAutoInvestFlag = autoInvestLogManager.getMemberInvestFlag(memberId, project.getId(), new Date());
	    						// 查询数据库用户对该项目未投资过
	    						if (isAutoInvestFlag > 0) {
	    							// 已投资过该项目
	    							logger.info("【定时自动投标】用户已投资过该项目,memberId={},projectId={}", memberId, project.getId());
	    							continue;
	    						}
	    						
	    					} else if(StringUtil.isNotBlank(RedisManager.get(memberCurrentProjectInvestFlagRedisKey)) 
	    							&& Constant.AUTO_INVEST_FLAG.equals(RedisManager.get(memberCurrentProjectInvestFlagRedisKey))){
	    						// 缓存中查询到用户对该项目已投资过
	    						logger.info("【定时自动投标】用户已投资过该项目,memberId={},projectId={}", memberId, project.getId());
	    						continue;
	    					}
	    					
	    				} 
	    				
	    				// 判断优惠券的使用类型
	    				String profitCouponNo = "";
	    				String cashCouponNo = "";
	    				BigDecimal usedCapital = autoInvestMember.getAmount();
	    				BigDecimal usedCouponAmount = null;
	    				if (autoInvestMember.getCouponType() != null) {
	    					// 优先使用收益最高优惠券
	    					if (autoInvestMember.getCouponType() == TypeEnum.AUTO_INVEST_COUPON_USE_TYPE_INCOME.getType()) {
	    						// 现金券
	    						Coupon cashCoupon = couponManager.findUsableAutoInvestCouponsByMemberId(memberId, TypeEnum.COUPON_TYPE_CASH.getType(), TypeEnum.AUTO_INVEST_COUPON_USE_TYPE_INCOME.getType());
	    						if (cashCoupon != null) {
	    							cashCouponNo = cashCoupon.getCouponCode();
	    							usedCapital = usedCapital.subtract(cashCoupon.getAmount());
	    							usedCouponAmount = cashCoupon.getAmount();
	    						}
	    						// 收益券券
	    						Coupon incomeCoupon = couponManager.findUsableAutoInvestCouponsByMemberId(memberId, TypeEnum.COUPON_TYPE_INCOME.getType(), TypeEnum.AUTO_INVEST_COUPON_USE_TYPE_INCOME.getType());
	    						if (incomeCoupon != null) {
	    							profitCouponNo = incomeCoupon.getCouponCode();
	    						}
	    					}
	    					// 优先使用有效期最短优惠券
	    					if (autoInvestMember.getCouponType() == TypeEnum.AUTO_INVEST_COUPON_USE_TYPE_EXPIRY_DATE.getType()) {
	    						// 现金券
	    						Coupon cashCoupon = couponManager.findUsableAutoInvestCouponsByMemberId(memberId, TypeEnum.COUPON_TYPE_CASH.getType(), TypeEnum.AUTO_INVEST_COUPON_USE_TYPE_EXPIRY_DATE.getType());
	    						if (cashCoupon != null) {
	    							cashCouponNo = cashCoupon.getCouponCode();
	    							usedCapital = usedCapital.subtract(cashCoupon.getAmount());
	    							usedCouponAmount = cashCoupon.getAmount();
	    						}
	    						// 收益券券
	    						Coupon incomeCoupon = couponManager.findUsableAutoInvestCouponsByMemberId(memberId, TypeEnum.COUPON_TYPE_INCOME.getType(), TypeEnum.AUTO_INVEST_COUPON_USE_TYPE_EXPIRY_DATE.getType());
	    						if (incomeCoupon != null) {
	    							profitCouponNo = incomeCoupon.getCouponCode();
	    						}
	    					}
	    				}
	    				
	    				// 已满足以上条件，进行投资
	    				Order order = new Order();
	    				order.setProjectId(project.getId());
	    				order.setProjectName(project.getName());
	    				order.setInvestAmount(autoInvestMember.getAmount());
	    				order.setMemberId(memberId);
	    				// 收益券
	    				if (StringUtil.isNotBlank(profitCouponNo)) {
	    					order.setProfitCouponNo(profitCouponNo);
	    				}
	    				order.setOrderSource(TypeEnum.ORDER_SOURCE_PC.getType());
	    				order.setInvestFlag(StatusEnum.MEMBER_INVEST_FLAG_AUTO.getStatus());
	    				order.setProjectCategory(TypeEnum.PROJECT_CATEGORY_NORMAL.getType());
	    				ResultDO<Order> handleResult = handleMemberInvest(order, cashCouponNo, usedCouponAmount, usedCapital, memberId, autoInvestMember.getRegisterIp(), autoInvestMember.getAutoInvestSetId(), autoInvestSetInfo);
	    				// 如果投资成功
	    				if (handleResult.isSuccess()) {
	    					// 判断用户的投标频率如果是每天一次，直接该用户退出
	    					if (autoInvestMember.getInvestFrequency() == StatusEnum.INVEST_FREQUENCY_EVERYDAY.getStatus()) {
	    						break;
	    					}
	    					
	    				}
		    			
	    			}
	    		}
	    		//------项目结束------
	    	}
	    	//------用户结束------
		} catch (Exception e) {
			logger.error("【定时自动投标】定时用户自动投标出现异常！", e);
		}
	}
	
	// 在用户自动投标前校验用户
	private boolean preAutoInvestMemberValidate(AutoInvestMember autoInvestMember, Long memberId, String autoInvestSetInfo) {
		// 校验
		// (1)是否会员实名认证和手机认证
		if (StringUtil.isBlank(autoInvestMember.getIdentityNumber()) || autoInvestMember.getMobile() == null) {
			logger.info("【定时自动投标】会员未实名认证和手机认证,memberId={}", memberId);
			// 保存自动投标记录
			saveAutoInvestLog(autoInvestMember.getAutoInvestSetId(), memberId, null, null, null, StatusEnum.ORDER_PAYED_FAILED.getStatus(), autoInvestSetInfo, "会员未实名认证和手机认证");
			return false;
		}
		// (2)是否设置支付密码
		if (StatusEnum.SET_PAY_SUCCESS_FLAG_Y.getStatus() != autoInvestMember.getPayPasswordFlag()) {
			logger.info("【定时自动投标】会员未设置支付密码,memberId={}", memberId);
			// 保存自动投标记录
			saveAutoInvestLog(autoInvestMember.getAutoInvestSetId(), memberId, null, null, null, StatusEnum.ORDER_PAYED_FAILED.getStatus(), autoInvestSetInfo, "会员未设置支付密码");
			return false;
		}
		// (3)是否开通委托支付
		try {
			ResultDO<Boolean> synWithholdAuthorityDO = memberManager.synWithholdAuthority(memberId);
			// 如果查询异常，返回false
			if(!synWithholdAuthorityDO.getResult() && ResultCode.ERROR_SYSTEM.equals(synWithholdAuthorityDO.getResultCode())) {
				logger.info("【定时自动投标】查询并同步会员是否委托扣款出现异常,memberId={},errorMsg={}", memberId, synWithholdAuthorityDO.getResultCode().getMsg());
				// 保存自动投标记录
				saveAutoInvestLog(autoInvestMember.getAutoInvestSetId(), memberId, null, null, null, StatusEnum.ORDER_PAYED_FAILED.getStatus(), 
						autoInvestSetInfo, "查询并同步会员是否委托扣款出现异常,errorMsg=" + synWithholdAuthorityDO.getResultCode().getMsg());
				return false;
			}
			
			if (!synWithholdAuthorityDO.isSuccess() || !synWithholdAuthorityDO.getResult()) {
				logger.info("【定时自动投标】会员未开通委托支付,memberId={}", memberId);
				// 保存自动投标记录
				saveAutoInvestLog(autoInvestMember.getAutoInvestSetId(), memberId, null, null, null, StatusEnum.ORDER_PAYED_FAILED.getStatus(), autoInvestSetInfo, "会员未开通委托支付");
				return false;
			}
		} catch (ManagerException mex) {
			logger.error("【定时自动投标】同步查询会员是否开通委托支付异常，memberId={}", memberId);
			return false;
		}
		
		// 2）确定开启标识invest_flag=1
		if (autoInvestMember.getInvestFlag() != StatusEnum.INVEST_FLAG_OPEN.getStatus()) {
			logger.info("【定时自动投标】会员未开启自动投标,memberId={}", memberId);
			return false;
		}
		// 3）判断用户的投标频率如果是每天一次，从redis中获取该用户是否当天自动投标过的数据
		boolean isCurrentDayInvestFlag = false;
		if (autoInvestMember.getInvestFrequency() == StatusEnum.INVEST_FREQUENCY_EVERYDAY.getStatus()) {
			try {
				String memberCurrentDayInvestFlagRedisKey = memberId + RedisConstant.MEMBER_CURRENT_DAY_AUTO_INVEST_FLAG + DateUtils.getStrFromDate(new Date(), DateUtils.DATE_FMT_3);
				if(!RedisManager.isExit(memberCurrentDayInvestFlagRedisKey)) {
					// 4）如果redis数据为空，查询自动投标记录，判定当天该用户是否已经自动投标过 
					int isAutoInvestFlag = autoInvestLogManager.getMemberInvestFlag(memberId, null, new Date());
					if (isAutoInvestFlag > 0) isCurrentDayInvestFlag = true;
					
				} else if(StringUtil.isNotBlank(RedisManager.get(memberCurrentDayInvestFlagRedisKey)) 
						&& Constant.AUTO_INVEST_FLAG.equals(RedisManager.get(memberCurrentDayInvestFlagRedisKey))){
					isCurrentDayInvestFlag = true;
				}
			} catch (ManagerException mex) {
				logger.error("【定时自动投标】根据投标日期查询用户是否投标过异常,memberId={},日期={}", memberId, DateUtils.getStrFromDate(new Date(), DateUtils.DATE_FMT_3));
				return false;
			}
		}
		// 如果用户的投标频率如果是每天一次且已自动投资过
		if (isCurrentDayInvestFlag) {
			logger.info("【定时自动投标】会员设置每天自动投标一次,今天已经投过,memberId={},日期={}", memberId, DateUtils.getStrFromDate(new Date(), DateUtils.DATE_FMT_3));
			return false;
		}
		
		// 5）如果当天未自动投标过或用户的投标频率是每个项目一次
		if (!isCurrentDayInvestFlag || autoInvestMember.getInvestFrequency() == StatusEnum.INVEST_FREQUENCY_EVERYPROJECT.getStatus()) {
			// 同步查询新浪存钱罐余额，判断投标金额是否大于账户余额
			try {
				Balance balance = balanceManager.synchronizedBalance(memberId, TypeEnum.BALANCE_TYPE_PIGGY);
				if (balance.getAvailableBalance().compareTo(autoInvestMember.getAmount()) == -1) {
					// 保存自动投标记录
					String errorMsg = "会员存钱罐余额不足，当前存钱罐余额=" + balance.getAvailableBalance() + "，设置自动投标金额=" + autoInvestMember.getAmount();
					saveAutoInvestLog(autoInvestMember.getAutoInvestSetId(), memberId, null, null, null, StatusEnum.ORDER_PAYED_FAILED.getStatus(), autoInvestSetInfo, errorMsg);
					logger.info("【定时自动投标】会员存钱罐余额不足，当前存钱罐余额={},设置自动投标金额={},memberId={}", autoInvestMember.getAmount(), balance.getAvailableBalance(), memberId);
					return false;
				}
			} catch (ManagerException mex) {
				logger.error("【定时自动投标】同步查询新浪存钱罐余额异常，memberId={}", memberId, mex);
				return false;
			}
		}
		
		return true;
	}
	
	// 进行投资处理
	private ResultDO<Order> handleMemberInvest(Order order, String cashCouponNo, BigDecimal usedCouponAmount, BigDecimal usedCapital, Long memberId, String registerIp, Long autoInvestSetId, String autoInvestSetInfo) throws ManagerException {
		// 12）如果满足条件，保存用户投标的订单并支付订单
		ResultDO<Order> result = saveOrder(order);
		if (usedCapital != null) {
			order.setUsedCapital(usedCapital);
		}
		// 现金券
		if (StringUtil.isNotBlank(cashCouponNo)) {
			order.setCashCouponNo(cashCouponNo);
		}
		// 使用现金券金额
		if (usedCouponAmount != null) {
			order.setUsedCouponAmount(usedCouponAmount);
		}
		// 13）保存订单，支付
		if (result.isSuccess()) {
			if (StringUtil.isBlank(registerIp)) {
				SysDict sysDict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
		    	if(sysDict != null) {
		    		registerIp = sysDict.getValue();
		    	}
			}
	    	ResultDO<TradeBiz> resultDO = tradeManager.tradeUseCapital(order, TypeEnum.TRADE_SOURCE_TYPE_TRANSACTION.getType(), registerIp, null);
	    	// 如果是非委托支付，置为失败
	    	if (resultDO.getResult().getIsWithholdAuthority() != StatusEnum.WITHHOLD_AUTHORITY_FLAG_Y.getStatus()) {
	    		// 保存自动投标记录
		    	saveAutoInvestLog(autoInvestSetId, memberId, null, order.getProjectId(), order.getId(), StatusEnum.ORDER_PAYED_FAILED.getStatus(), autoInvestSetInfo, "创建代收失败，未开通委托支付");
		    	logger.info("【定时自动投标】创建代收失败,未开通委托支付,memberId={},orderId={},projectId={}", memberId, order.getId(), order.getProjectId());
	    	} else {
	    		// 保存自动投标记录
	    		saveAutoInvestLog(autoInvestSetId, memberId, null, order.getProjectId(), order.getId(), null, autoInvestSetInfo, null);
	    	}
		} else {
			// 保存自动投标记录
	    	saveAutoInvestLog(autoInvestSetId, memberId, null, order.getProjectId(), null, StatusEnum.ORDER_PAYED_FAILED.getStatus(), autoInvestSetInfo, "保存订单失败，errorMsg="+ result.getResultCode().getMsg() +"！");
	    	logger.info("【定时自动投标】保存订单失败,memberId={},projectId={},errorMsg={}", memberId, order.getProjectId(), result.getResultCode().getMsg());
		}
		return result;
	}
	
	// 保存订单
	private ResultDO<Order> saveOrder(Order order) {
		ResultDO<Order> result = new ResultDO<Order>();
		try {
			//创建订单前的校验
			result = orderManager.preCreateOrderValidate(order);
			if(!result.isSuccess()) {
				return result;
			}
			// 生成trade_no
			order.setOrderNo(SerialNumberUtil.generateOrderNo(order.getMemberId()));
			// 初始订单状态为待支付状态
			order.setStatus(StatusEnum.ORDER_WAIT_PAY.getStatus());
			// 根据投资额获取年化收益
			order.setAnnualizedRate(projectManager.getAnnualizedRateByProjectIdAndInvestAmount(order.getInvestAmount(), order.getProjectId()));
			// 如果有收益券，则验证收益券是否有效并且查询出收益券收益额
			if(StringUtil.isNotBlank(order.getProfitCouponNo())) {
				result = checkOrderProfitCouponNo(order, false);
				if(result.isSuccess()) {
					Coupon coupon = couponManager.getCouponByCouponNo(order.getProfitCouponNo());
					
					order.setExtraAnnualizedRate(coupon.getAmount());
					//使用高额收益券
					if(coupon.getExtraInterestType()==1&&coupon.getExtraInterestDay()>0){
						order.setExtraInterestDay(coupon.getExtraInterestDay());
					}
					//将优惠券置为使用中，锁定优惠券
					couponManager.lockCoupon(order.getProfitCouponNo());
				}else{
					return result;
				}
			}
			if (orderManager.insert(order) > 0) {
				return result;
			}
			
		} catch (Exception e) {
			logger.error("【定时自动投标】保存订单失败,order={}", order, e);
		}
		return result;
	} 
	
	/**
	 * 检查订单优惠券是否可用
	 */
	private ResultDO<Order> checkOrderProfitCouponNo(Order order, boolean checkStatus){
		if(StringUtil.isNotBlank(order.getProfitCouponNo())){
			ResultDO<Coupon> result = checkOrderCoupon(order, order.getProfitCouponNo());
			return checkOrderCoupon(result, checkStatus);
		}
		return new ResultDO<Order>();
	}
	
	/**
	 * 检查优惠券是否可用
	 * @param order
	 * @param couponNo
	 * @return
	 */
	private ResultDO<Coupon> checkOrderCoupon(Order order, String couponNo){
		ResultDO<Coupon> result = new ResultDO<Coupon>();
		try{
			Coupon coupon = couponManager.getCouponByCouponNo(couponNo);
			if(coupon != null){
				Coupon couponForLock = couponManager.selectCouponforUpdate(coupon.getId());
				Project project = projectManager.selectByPrimaryKey(order.getProjectId());
				int days = 0;
				if (project.isDirectProject()){
					days = project.countProjectDays();
				}else {
					days = DateUtils.getIntervalDays(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), project.getEndDate())+1 - project.getInterestFrom();
				}
				 couponManager.couponForCurrentInvestIsUsable(result, couponForLock, CouponEnum.COUPON_CLIENT_WEB.getCode(), days, order);				
				result.setResult(couponForLock);
			} else {
				result.setResultCode(ResultCode.ORDER_USED_COUPON_NOT_EXISTS_ERROR);
			}
		} catch (Exception e) {
			result.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("优惠券是否可使用检查异常" , e);
		}
		return result;
	}
	// 检查优惠券
	private ResultDO<Order> checkOrderCoupon(ResultDO<Coupon> result, boolean checkStatus){
		ResultDO<Order> resultOrder = new ResultDO<Order>();
		if(checkStatus && !result.isSuccess()){
			Coupon coupon = result.getResult();
			if(coupon != null){
				int status = coupon.getStatus();
				if(status != StatusEnum.COUPON_STATUS_USING.getStatus()){
					return resultOrder;
				}
			}
		}
		resultOrder.setSuccess(result.isSuccess());
		if(!result.isSuccess()){
			resultOrder.setResultCode(result.getResultCode());
		}
		return resultOrder;
	}
	
	// 保存自动投标记录
	private void saveAutoInvestLog(Long autoInvestId, Long memberId, Long transactionId, Long projectId, Long orderId, Integer status, String autoInvestSetInfo, String remarks) {
		AutoInvestLog autoInvestLog = new AutoInvestLog();
		autoInvestLog.setAutoInvestId(autoInvestId);
		autoInvestLog.setMemberId(memberId);
		autoInvestLog.setTransactionId(transactionId);
		autoInvestLog.setProjectId(projectId);
		autoInvestLog.setOrderId(orderId);
		autoInvestLog.setStatus(status);
		autoInvestLog.setAutoInvestSetInfo(autoInvestSetInfo);
		autoInvestLog.setRemarks(remarks);
		autoInvestLog.setCreateTime(new Date());
		autoInvestLog.setUpdateTime(new Date());
		try {
			if(autoInvestLogManager.insertSelective(autoInvestLog) == 0) {
				logger.info("【定时自动投标】保存自动投标记录错误,memberId={},projectId={},transactionId={},autoInvestSetInfo={}", memberId, projectId, transactionId, autoInvestSetInfo);
			}
		} catch (ManagerException e) {
			logger.error("【定时自动投标】保存自动投标记录异常,memberId={},projectId={},transactionId={},autoInvestSetInfo={}", memberId, projectId, transactionId, autoInvestSetInfo, e);
		}
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Order> closeDirectProjectOrder(Long orderId) throws Exception {
		ResultDO<Order> result = new ResultDO<Order>();
		try {
			Order order = orderManager.selectByPrimaryKey(orderId);
			Project project = projectManager.selectByPrimaryKey(order.getProjectId());
			if(!project.isDirectProject()){
				result.setSuccess(false);
				result.setResultCode(ResultCode.PROJECT_INVEST_TYPE_ERROR);
				return result;
			}
			if(orderManager.closeDirectProjectOrder(orderId, "后台人工关闭订单") > 0) {
				// 更新代收状态
				HostingCollectTrade hostingCollectTrade = hostingCollectTradeManager.getBySourceIdAndType(orderId,
						TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType());
				HostingCollectTrade hostingCollectTradeForLock = hostingCollectTradeManager.getByIdForLock(hostingCollectTrade.getId());
				if (!hostingCollectTradeForLock.getTradeStatus().equals(TradeStatus.WAIT_PAY.name())) {
					result.setResultCode(ResultCode.TRANSACTION_PRE_VALIDATE_EXCEPTION_ERROR);
					return result;
				}
				hostingCollectTrade.setTradeStatus(TradeStatus.TRADE_CLOSED.name());
				hostingCollectTrade.setRemarks("后台人工关闭待确认订单");
				hostingCollectTrade.setProjectId(order.getProjectId());
				hostingCollectTradeManager.updateHostingCollectTrade(hostingCollectTrade);
				//解锁收益券
				if(StringUtil.isNotBlank(order.getProfitCouponNo())) {
					couponManager.unLockCoupon(order.getProfitCouponNo());
				}
				//解锁现金券
				if(StringUtil.isNotBlank(order.getCashCouponNo())) {
					couponManager.unLockCoupon(order.getCashCouponNo());
				}
				result.setSuccess(true);
			} else {
				result.setResultCode(ResultCode.TRANSACTION_PRE_VALIDATE_EXCEPTION_ERROR);
			}
			return result;
		} catch (ManagerException e) {
			logger.error("关闭直投项目待确认订单失败,order={}", orderId, e);
			throw e;
		}
	}

	@Override
	public String queryStatusByOrderId(String orderid) {
		return hostingRefundManager.queryStatusByOrderId(orderid);
	}
}
