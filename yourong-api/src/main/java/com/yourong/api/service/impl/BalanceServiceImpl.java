package com.yourong.api.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.model.biz.OverduePopularityBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.BalanceService;
import com.yourong.api.service.CouponService;
import com.yourong.api.service.MemberService;
import com.yourong.api.service.MessageLogService;
import com.yourong.api.service.OrderService;
import com.yourong.api.service.TransactionService;
import com.yourong.api.utils.SysServiceUtils;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.pay.BalancePayResponse;
import com.yourong.common.thirdparty.pay.PayMentService;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.common.web.BaseService;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.fin.manager.WithdrawLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.WithdrawFee;
import com.yourong.core.fin.model.biz.BonusBiz;
import com.yourong.core.fin.model.query.CapitalInOutLogQuery;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.msg.manager.MessageLogManager;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.biz.MemberTransactionCapital;
import com.yourong.core.tc.model.query.CollectingProjectQuery;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.manager.MemberVipManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberVip;

/**
 * 资金余额
 * 
 * @author Administrator
 *
 */
@Service
public class BalanceServiceImpl extends BaseService implements BalanceService {

	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private PayMentService payMentService;

	@Autowired
	private CapitalInOutLogManager capitalInOutLogManager;

	@Autowired
	private OrderService orderService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private CouponService couponService;
	
	@Autowired
	private MessageLogService messageLogService;

	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired
	private MemberManager memberManager;
	
	@Autowired
	private SysDictManager sysDictManager;
	
	@Autowired
    private WithdrawLogManager withdrawLogManager;
	
	@Autowired
	private MessageLogManager messageLogManager;
	
	@Autowired 
	private HostingPayTradeManager hostingPayTradeManager;
	
	@Autowired
	private MemberVipManager memberVipManager;

	@Autowired
	private PopularityInOutLogManager popularityInOutLogManager;

	/**
	 * 充值 回调接口
	 */
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void topUpFromThirdPay(long memberID, BigDecimal income, String sourceId) throws Exception {

		// 新浪同步资金
		BalancePayResponse queryBalance = payMentService.queryBalance(SerialNumberUtil.generateIdentityId(memberID));
		// 更新余额表
		Balance balance = balanceManager.queryBalance(memberID, TypeEnum.BALANCE_TYPE_PIGGY);
		if (balance == null) {
			balanceManager.insertBalance(TypeEnum.BALANCE_TYPE_PIGGY, queryBalance.getBalanceBigDecimal(),
					queryBalance.getAvailableBalanceBigDecimal(), memberID);
		} else {
			balanceManager.updateBalanceByID(queryBalance.getBalanceBigDecimal(), queryBalance.getAvailableBalanceBigDecimal(),
					balance.getId());
		}
		// 记录流水
		capitalInOutLogManager.insert(memberID, TypeEnum.FINCAPITALINOUT_TYPE_RECHARGE, income, BigDecimal.ZERO,
				queryBalance.getBalanceBigDecimal(), sourceId, "", TypeEnum.FINCAPITALINOUT_PAYACCOUNTTYPE_MAIN);

	}

	@Override
	public BigDecimal getBalanceByType(TypeEnum type) {
		try {
			Balance balance = balanceManager.queryBalance(-1L, type);
			if (balance != null) {
				return balance.getAvailableBalance();
			}
		} catch (ManagerException e) {
			logger.error("查询余额（主要用于查询平台投资总额和平台收益利息总额）出异常, type=" + type, e);
		}
		return null;
	}

	@Override
	public Balance queryBalance(Long sourceID, TypeEnum type) {

		try {
			Balance balance = balanceManager.queryBalance(sourceID, type);
			return balance;
		} catch (ManagerException e) {
			logger.error("查询余额出异常, sourceID=" + sourceID, e);
		}
		return null;
	}

	@Override
	public void initBalance(Long sourceID, TypeEnum type) {

		Balance record = new Balance();
		record.setBalance(BigDecimal.ZERO);
		record.setAvailableBalance(BigDecimal.ZERO);
		record.setSourceId(sourceID);
		record.setBalanceType(type.getType());
		try {
			int i = balanceManager.insert(record);
		}catch (DuplicateKeyException e){
			logger.error("初始化余额重复插入, sourceID=" + sourceID);
		} catch (ManagerException e) {
			logger.error("初始化余额出异常, sourceID=" + sourceID, e);
		}
	}

	@Override
	public boolean balanceIsZero(Long memberID) {
		boolean result = false;
		try {
			Balance balance = balanceManager.synchronizedBalance(memberID, TypeEnum.BALANCE_TYPE_PIGGY);
			if (BigDecimal.ZERO.compareTo(balance.getAvailableBalance()) == 0) {
				result = true;
			}
		} catch (ManagerException e) {
			logger.error("同步化余额出异常, sourceID={}", memberID, e);
		}
		return result;
	}

	@Override
	public ResultDTO queryMemberBalance(Long memberId) {
		ResultDTO<Map<String, Object>> result = new ResultDTO();
		try {
			Map<String, Object> balanceMap = Maps.newHashMap();
			// 取会员余额放进model
			Balance balance = queryBalance(memberId, TypeEnum.BALANCE_TYPE_PIGGY);
			balanceMap.put("balance", FormulaUtil.getFormatPriceRound(balance.getBalance()));
			balanceMap.put("availableBalance", FormulaUtil.getFormatPriceRound(balance.getAvailableBalance()));
			// 投资数据
			//MemberTransactionCapital capital = transactionService.getMemberTransactionCapital(memberId);
			//m站和app 账户中心公用一个方法（m站过滤p2p，app 放开  新建一个方法）
			MemberTransactionCapital capital = transactionService.getMemberTransactionCapitalTemp(memberId);
			// 待收本金
			BigDecimal receivablePrincipal = BigDecimal.ZERO;
			// 待收收益
			BigDecimal receivableInterest = BigDecimal.ZERO;
			// 已收收益
			BigDecimal receivedInterest = BigDecimal.ZERO;
			// 累计投资
			BigDecimal investTotal = BigDecimal.ZERO;
			 //滞纳金
	  	     BigDecimal overdueFine = BigDecimal.ZERO;
	  	     //其他收益
	  	     BigDecimal otherIncome = BigDecimal.ZERO;
			if (capital != null) {
				receivablePrincipal = capital.getReceivablePrincipal();
				receivableInterest = capital.getReceivableInterest();
				receivedInterest = capital.getReceivedInterest();
				investTotal = capital.getFinishedInvestTotal().add(capital.getSubsistingInvestTotal());
				 overdueFine = capital.getOverdueFine();
			}
			// 资产总计＝存钱罐余额＋待收本金＋待收收益
			BigDecimal totalAssets = balance.getBalance().add(receivablePrincipal).add(receivableInterest);
			balanceMap.put("totalAssets", FormulaUtil.getFormatPriceRound(totalAssets));

			// 累计收益 = 待收收益 + 存钱罐收益 + 已收收益
			//累计赚取 = 待收收益 + 存钱罐收益 + 已收收益 +其他 +现金券
			// 存钱罐收益
			BigDecimal savingPotEarnig = queryBalance(memberId, TypeEnum.BALANCE_TYPE_MEMBER_TOTAL_PIGGY).getBalance();
			 //使用现金券
	  	     BigDecimal totalMemberUsedCouponAmount = transactionManager.totalMemberUsedCouponAmount(memberId);
	  	     //其他收益 = 租赁分红+滞纳金
	  	     BigDecimal totalMemberLeaseBonusAmounts = transactionManager.totalMemberLeaseBonusAmounts(memberId);
	  	     otherIncome = totalMemberLeaseBonusAmounts.add(overdueFine);
			// 累计收益 改为累计赚取，M站没有显示此数据
			BigDecimal totalEarnings = savingPotEarnig.add(receivableInterest).add(receivedInterest).add(otherIncome).add(totalMemberUsedCouponAmount);   	
			balanceMap.put("totalEarnings", totalEarnings.setScale(2, BigDecimal.ROUND_HALF_UP));
			balanceMap.put("yesterdayBonus",getYesterdayBonus(memberId));
			// 用户昵称
			String userName = memberService.getMemberUserName(memberId);
			if (StringUtil.isNumeric(userName) && userName.length() >= 11) {// 手机号码
				userName = StringUtil.maskString(userName, StringUtil.ASTERISK, 3, 4, 4);
			}

			Member member = memberService.selectByPrimaryKey(memberId);
			Balance popularity = queryBalance(memberId, TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
			balanceMap.put("userName", userName);
			balanceMap.put("isSetNickname", member.getUsername() == null ? false : true);
			balanceMap.put("trueName", member.getTrueName());
			balanceMap.put("receivablePrincipal", FormulaUtil.getFormatPriceRound(receivablePrincipal));// 待收本金
			balanceMap.put("receivableInterest", FormulaUtil.getFormatPriceRound(receivableInterest));// 待收收益
			balanceMap.put("receivedInterest", FormulaUtil.getFormatPriceRound(receivedInterest));// 累计投资收益
			balanceMap.put("receivableInterestNum", capital.getReceivableInterestNum());// 待收利息笔数
			balanceMap.put("subsistingInvestNum", capital.getSubsistingInvestNum());// 待收本金笔数
			String avatar = memberService.getMemberAvatar(member.getId());
			if (StringUtil.isNotBlank(avatar)) {
				balanceMap.put("avatar", Config.ossPicUrl + avatar);// 用户头像
			} else {
				balanceMap.put("avatar", "");// 用户头像
			}
			balanceMap.put("investTotal", investTotal);// 累计投资
			balanceMap.put("popularity", FormulaUtil.getIntegerDefaultZero(popularity.getBalance()).replace(",", ""));// 人气值
			balanceMap.put("popularityAvailableBalance",
					FormulaUtil.getIntegerDefaultZero(popularity.getAvailableBalance()).replace(",", ""));// 可用人气值
			//查询已领取未使用的优惠券，不用过滤项目类型
			balanceMap.put("couponCount", couponService.getMemberCouponCount(member.getId()));// 优惠券数量
			//balanceMap.put("couponCount", couponService.getMemberCouponCountFilterP2p(member.getId()));//过滤p2p
			
			balanceMap.put("isDirectProject",transactionService.isMemberDirectInvest(memberId));
			// 未读站内信
			//int newMsg = messageLogService.countUnreadMessage(memberId);
			//balanceMap.put("hasNewMsg", newMsg > 0 ? true : false);
			// P2P项目项目和资金统计
			CollectingProjectQuery query = new CollectingProjectQuery();
			query.setMemberId(memberId);
			int count = transactionManager.selectCollectForMemberCounting(query);
			balanceMap.put("collectingNum", count);
			if (count > 0) {
				BigDecimal amount = projectManager.selectCollectProjectForMemberInvestAmount(query);
				balanceMap.put("collectingAmount", amount);
			}
			
			// 用户未签署合同数量
			int unSignContracts = transactionManager.getUnsignContractNum(memberId);
			balanceMap.put("unSignContracts", unSignContracts);
			
			ResultDO<Boolean> isPassword = (ResultDO<Boolean>) memberManager.synPayPasswordFlag(memberId);
			balanceMap.put("isPayPassword", isPassword.getResult());
		/*	ResultDO<Boolean> isAuthority = (ResultDO<Boolean>)memberManager.synWithholdAuthority(memberId);
			balanceMap.put("isAuthority", isAuthority.getResult());*/
			
			Integer unreadMessage = messageLogManager.countUnreadMessageByType(memberId, Constant.MSG_TEMPLATE_TYPE_APP);
			balanceMap.put("unreadMessage", unreadMessage);
			
			MemberVip memberVip = memberVipManager
					.selectRecentMemberVipByMemberId(memberId);
			balanceMap.put("vipLevel", memberVip.getVipLevel());
			
			result.setResult(balanceMap);
		} catch (Exception ex) {
			result.setResultCode(ResultCode.ERROR);
			logger.error("查询会员所有投资记录数据异常 memberid={}", memberId, ex);
		}
		return result;
	}

	/**
	 * 获取昨日收益
	 * @param lastWeekBonusLists
	 * @return
	 * @throws ManagerException 
	 */
	private BigDecimal getYesterdayBonus(Long memberId) throws ManagerException {
		
		CapitalInOutLogQuery query = new CapitalInOutLogQuery();
		Date endDate = DateUtils.formatDate(DateUtils.getCurrentDate(),DateUtils.DATE_FMT_3);
		query.setStartTime(DateUtils.addDate(endDate, -1));
		query.setEndTime(endDate);
		query.setMemberId(memberId);
		query.setType(TypeEnum.FINCAPITALINOUT_TYPE_THIRDPAY.getType());
		List<BonusBiz> lastWeekBonusLists = capitalInOutLogManager.selectBonusByQuery(query);
		if(Collections3.isNotEmpty(lastWeekBonusLists)) {
			for (BonusBiz bonusBiz : lastWeekBonusLists) {
				if(DateUtils.getStrFromDate(bonusBiz.getBonusDate(),DateUtils.DATE_FMT_3)
					.equals(DateUtils.getStrFromDate(DateUtils.getYesterDay(), DateUtils.DATE_FMT_3))) {
					return bonusBiz.getBonusAmount();
				}
			}
		}
		return BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	
	@Override
	public boolean isZeroMemberTotalAsset(Long memberId) {
		boolean result = false;
		try {
			result = balanceManager.isZeroMemberTotalAsset(memberId);
		} catch (ManagerException e) {
			logger.error("判断会员所有资产是否为0 , memberId={}", memberId, e);
		}
		return result;
	}

	@Override
	public ResultDTO queryMemberBalance3(Long memberId) {
		ResultDTO<Map<String, Object>> result = new ResultDTO();
		try {
			Map<String, Object> balanceMap = Maps.newHashMap();
			// 取会员余额放进model
			Balance balance = queryBalance(memberId, TypeEnum.BALANCE_TYPE_PIGGY);
			balanceMap.put("balance", FormulaUtil.getFormatPriceRound(balance.getBalance()));
			balanceMap.put("availableBalance", FormulaUtil.getFormatPriceRound(balance.getAvailableBalance()));
			// 投资数据
			MemberTransactionCapital capital = transactionService.getMemberTransactionCapital(memberId);
			//m站和app 账户中心公用一个方法（m站过滤p2p，app 放开  新建一个方法）
			//MemberTransactionCapital capital = transactionService.getMemberTransactionCapitalTemp(memberId);
			// 待收本金
			BigDecimal receivablePrincipal = BigDecimal.ZERO;
			// 待收收益
			BigDecimal receivableInterest = BigDecimal.ZERO;
			// 已收收益
			BigDecimal receivedInterest = BigDecimal.ZERO;
			// 累计投资
			BigDecimal investTotal = BigDecimal.ZERO;
			if (capital != null) {
				receivablePrincipal = capital.getReceivablePrincipal();
				receivableInterest = capital.getReceivableInterest();
				receivedInterest = capital.getReceivedInterest();
				investTotal = capital.getFinishedInvestTotal().add(capital.getSubsistingInvestTotal());
			}
			// 资产总计＝存钱罐余额＋待收本金＋待收收益
			BigDecimal totalAssets = balance.getBalance().add(receivablePrincipal).add(receivableInterest);
			balanceMap.put("totalAssets", FormulaUtil.getFormatPriceRound(totalAssets));

			// 累计收益 = 待收收益 + 存钱罐收益 + 已收收益
			// 存钱罐收益
			BigDecimal savingPotEarnig = queryBalance(memberId, TypeEnum.BALANCE_TYPE_MEMBER_TOTAL_PIGGY).getBalance();
			// 累计收益
			BigDecimal totalEarnings = savingPotEarnig.add(receivableInterest).add(receivedInterest);
			balanceMap.put("totalEarnings", totalEarnings.setScale(2, BigDecimal.ROUND_HALF_UP));

			// 用户昵称
			String userName = memberService.getMemberUserName(memberId);
			if (StringUtil.isNumeric(userName) && userName.length() >= 11) {// 手机号码
				userName = StringUtil.maskString(userName, StringUtil.ASTERISK, 3, 4, 4);
			}

			Member member = memberService.selectByPrimaryKey(memberId);
			Balance popularity = queryBalance(memberId, TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
			balanceMap.put("userName", userName);
			balanceMap.put("isSetNickname", member.getUsername() == null ? false : true);
			balanceMap.put("trueName", member.getTrueName());
			balanceMap.put("receivablePrincipal", FormulaUtil.getFormatPriceRound(receivablePrincipal));// 待收本金
			balanceMap.put("receivableInterest", FormulaUtil.getFormatPriceRound(receivableInterest));// 待收收益
			balanceMap.put("receivedInterest", FormulaUtil.getFormatPriceRound(receivedInterest));// 累计投资收益
			balanceMap.put("receivableInterestNum", capital.getReceivableInterestNum());// 待收利息笔数
			balanceMap.put("subsistingInvestNum", capital.getSubsistingInvestNum());// 待收本金笔数
			String avatar = memberService.getMemberAvatar(member.getId());
			if (StringUtil.isNotBlank(avatar)) {
				balanceMap.put("avatar", Config.ossPicUrl + avatar);// 用户头像
			} else {
				balanceMap.put("avatar", "");// 用户头像
			}
			balanceMap.put("investTotal", investTotal);// 累计投资
			balanceMap.put("popularity", FormulaUtil.getIntegerDefaultZero(popularity.getBalance()).replace(",", ""));// 人气值
			balanceMap.put("popularityAvailableBalance",
					FormulaUtil.getIntegerDefaultZero(popularity.getAvailableBalance()).replace(",", ""));// 可用人气值
			//查询已领取未使用的优惠券，不用过滤项目类型
			balanceMap.put("couponCount", couponService.getMemberCouponCount(member.getId()));// 优惠券数量
			//balanceMap.put("couponCount", couponService.getMemberCouponCountFilterP2p(member.getId()));//过滤p2p
			
			balanceMap.put("isDirectProject",transactionService.isMemberDirectInvest(memberId));
			// 未读站内信
			//int newMsg = messageLogService.countUnreadMessage(memberId);
			//balanceMap.put("hasNewMsg", newMsg > 0 ? true : false);
			// P2P项目项目和资金统计
			CollectingProjectQuery query = new CollectingProjectQuery();
			query.setMemberId(memberId);
			int count = transactionManager.selectCollectForMemberCounting(query);
			balanceMap.put("collectingNum", count);
			if (count > 0) {
				BigDecimal amount = projectManager.selectCollectProjectForMemberInvestAmount(query);
				balanceMap.put("collectingAmount", amount);
			}
			result.setResult(balanceMap);
		} catch (Exception ex) {
			result.setResultCode(ResultCode.ERROR);
			logger.error("查询会员所有投资记录数据异常 memberid={}", memberId, ex);
		}
		return result;
	}
	
	
	@Override
	public ResultDTO queryMemberWorth(Long memberId) {
		ResultDTO<Map<String, Object>> result = new ResultDTO();
		try {
			Map<String, Object> balanceMap = Maps.newHashMap();
			// 取会员余额放进model
			Balance balance = queryBalance(memberId, TypeEnum.BALANCE_TYPE_PIGGY);
			balanceMap.put("balance", FormulaUtil.getFormatPriceRound(balance.getBalance()));
			balanceMap.put("availableBalance", FormulaUtil.getFormatPriceRound(balance.getAvailableBalance()));
			balanceMap.put("freezeBalance", FormulaUtil.getFormatPriceRound(balance.getBalance().subtract(balance.getAvailableBalance())));//冻结余额
			// 投资数据                                                                                                                getMemberTransactionCapitalTemp
			MemberTransactionCapital capital = transactionService.getMemberTransactionCapitalTemp(memberId);
			// 待收本金
			BigDecimal receivablePrincipal = BigDecimal.ZERO;
			// 待收收益
			BigDecimal receivableInterest = BigDecimal.ZERO;
			
			if (capital != null) {
				receivablePrincipal = capital.getReceivablePrincipal();
				receivableInterest = capital.getReceivableInterest();
			}
			// 资产总计＝存钱罐余额＋待收本金＋待收收益
			BigDecimal totalAssets = balance.getBalance().add(receivablePrincipal).add(receivableInterest);
			balanceMap.put("totalAssets", FormulaUtil.getFormatPriceRound(totalAssets));

			balanceMap.put("receivablePrincipal", FormulaUtil.getFormatPriceRound(receivablePrincipal));// 待收本金
			balanceMap.put("receivableInterest", FormulaUtil.getFormatPriceRound(receivableInterest));// 待收收益
			
			result.setResult(balanceMap);
		} catch (Exception ex) {
			result.setResultCode(ResultCode.ERROR);
			logger.error("查询会员资产记录数据异常 memberid={}", memberId, ex);
		}
		return result;
	}
	
	
	
	@Override
	public ResultDTO queryMemberEarn(Long memberId) {
		ResultDTO<Map<String, Object>> result = new ResultDTO();
		try {
			Map<String, Object> balanceMap = Maps.newHashMap();
			// 投资数据
			MemberTransactionCapital capital = transactionService.getMemberTransactionCapitalTemp(memberId);
			//m站和app 账户中心公用一个方法（m站过滤p2p，app 放开  新建一个方法）
			//MemberTransactionCapital capital = transactionService.getMemberTransactionCapitalTemp(memberId);
			// 待收本金
			BigDecimal receivablePrincipal = BigDecimal.ZERO;
			// 待收收益
			BigDecimal receivableInterest = BigDecimal.ZERO;
			// 已收收益
			BigDecimal receivedInterest = BigDecimal.ZERO;
			// 累计投资
			BigDecimal investTotal = BigDecimal.ZERO;
			 //滞纳金
	  	     BigDecimal overdueFine = BigDecimal.ZERO;
	  	     //其他收益
	  	     BigDecimal otherIncome = BigDecimal.ZERO;
			if (capital != null) {
				receivablePrincipal = capital.getReceivablePrincipal();
				receivableInterest = capital.getReceivableInterest();
				receivedInterest = capital.getReceivedInterest();
				investTotal = capital.getFinishedInvestTotal().add(capital.getSubsistingInvestTotal());
				 overdueFine = capital.getOverdueFine();
			}

			// 累计赚取 = 待收收益 + 存钱罐收益 + 已收收益 +其他 +现金券
			// 存钱罐收益
			BigDecimal savingPotEarning = queryBalance(memberId, TypeEnum.BALANCE_TYPE_MEMBER_TOTAL_PIGGY).getBalance();
			// 租赁分红
	  	    BigDecimal totalMemberLeaseBonusAmounts = transactionManager.totalMemberLeaseBonusAmounts(memberId);
	  	    //快投有奖现金奖励
	  	    ActivityLotteryResult modelResultAmount = new ActivityLotteryResult();
			modelResultAmount.setMemberId(memberId);
			BigDecimal quickRewardCash = hostingPayTradeManager.totalMemberPayAmountByType(memberId, TypeEnum.HOSTING_PAY_TRADE_QUICK_REWARD.getType());//快投发放的现金奖励
			//其他收益 = 租赁分红+滞纳金+快投有奖现金奖励
			otherIncome = totalMemberLeaseBonusAmounts.add(overdueFine).add(quickRewardCash);
	  	    //使用现金券
	  	    BigDecimal totalMemberUsedCouponAmount = transactionManager.totalMemberUsedCouponAmount(memberId);
	  	    //累计收益                                                     
	  	    BigDecimal totalEarnings = savingPotEarning.add(receivableInterest).add(receivedInterest).add(otherIncome).add(totalMemberUsedCouponAmount);   	 
	  	    balanceMap.put("totalEarnings", FormulaUtil.getFormatPriceRound(totalEarnings.setScale(2, BigDecimal.ROUND_HALF_UP))); //总共赚取
	  	    
	  	    balanceMap.put("interestEarning", FormulaUtil.getFormatPriceRound(receivedInterest.add(receivableInterest)));//利息收益
	  	    balanceMap.put("totalMemberUsedCouponAmount",FormulaUtil.getFormatPriceRound(totalMemberUsedCouponAmount));//使用现金券
	  	    balanceMap.put("savingPotEarning", FormulaUtil.getFormatPriceRound(savingPotEarning));//存钱罐收益
	  	    balanceMap.put("otherIncome",FormulaUtil.getFormatPriceRound(otherIncome));//其他收益
			balanceMap.put("investTotal", FormulaUtil.getFormatPriceRound(investTotal));// 累计投资
			
			result.setResult(balanceMap);
		} catch (Exception ex) {
			result.setResultCode(ResultCode.ERROR);
			logger.error("查询会员赚取记录数据异常 memberid={}", memberId, ex);
		}
		return result;
	}
	

	@Override
	public BigDecimal getWithdrawFee(BigDecimal withdrawAmount, Long memberId) {
		BigDecimal freeWithdrawAmount=BigDecimal.ZERO;
		Optional<Activity> withActivity = LotteryContainer.getInstance()
				.getActivityByName(ActivityConstant.WITH_DRAW_NAME);
		if (!withActivity.isPresent()) {
			return new BigDecimal(SysServiceUtils.getWithdrawalFees());
		}
	    Activity activity=withActivity.get();
	    try {
	    if (activity.getActivityStatus() == StatusEnum.ACTIVITY_STATUS_IS_START.getStatus()) {
	    	SysDict sysDict=sysDictManager.findByGroupNameAndKey("withdrawal_fees_app", "withdrawalFees");
	    	WithdrawFee withdrawFee=new WithdrawFee();
	    	if(sysDict!=null){
	    		withdrawFee=JSON.parseObject(sysDict.getRemarks(),WithdrawFee.class);
	    		if(withdrawAmount.compareTo(withdrawFee.getWithDrawAmount().get(0))<0){
	    			freeWithdrawAmount=withdrawFee.getPopularValue();
	    		}else if(withdrawAmount.compareTo(withdrawFee.getWithDrawAmount().get(1))>=0){
	    			freeWithdrawAmount=BigDecimal.ZERO;
	    		}else{
	    			int i=withdrawLogManager.countWithDrawFree(memberId,DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_6));
	    			if(i>withdrawFee.getSecond()){
	    				freeWithdrawAmount=withdrawFee.getPopularValue();
	    			}
	    		}
	    	}
	    }else{
	    	freeWithdrawAmount=new BigDecimal(SysServiceUtils.getWithdrawalFees());
	    }
	    } catch (ManagerException e) {
			logger.error("获取提现手续费异常={}", memberId);
		}
		return freeWithdrawAmount;
	}

	@Override
	public OverduePopularityBiz queryOverduePopularity(Long memberId) {
		try {
			return popularityInOutLogManager.queryOverduePopularity(memberId);
		} catch (ManagerException e) {
			logger.error("查询用户过期人气值异常，memberId={}",memberId, e);
		}
		return null;
	}
}
