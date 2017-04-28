package com.yourong.web.controller;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yourong.core.fin.model.biz.OverduePopularityBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.cms.model.biz.BannerFroAreaBiz;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.WithdrawLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.CapitalInOutLog;
import com.yourong.core.fin.model.WithdrawLog;
import com.yourong.core.fin.model.biz.MemberSavingPotBiz;
import com.yourong.core.fin.model.query.CapitalInOutLogQuery;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.tc.model.query.TransactionInterestQuery;
import com.yourong.core.uc.model.MemberBankCard;
import com.yourong.web.dto.FinEaringDto;
import com.yourong.web.dto.MemberSessionDto;
import com.yourong.web.dto.RechargeLogDto;
import com.yourong.web.dto.WithdrawLogDto;
import com.yourong.web.service.BalanceService;
import com.yourong.web.service.BannerService;
import com.yourong.web.service.CapitalInOutLogService;
import com.yourong.web.service.MemberBankCardService;
import com.yourong.web.service.MemberService;
import com.yourong.web.service.OrderService;
import com.yourong.web.service.ProjectService;
import com.yourong.web.service.RechargeLogWithdrawLogService;
import com.yourong.web.service.TransactionInterestService;
import com.yourong.web.service.TransactionService;
import com.yourong.web.service.PopularityInOutLogService;
import com.yourong.web.utils.ServletUtil;
import com.yourong.web.utils.SysServiceUtils;
/**
 * 资产统计,提现，充值
 * @author Administrator
 *
 */
@Controller
@RequestMapping("memberBalance")
public class MemberBalanceController extends BaseController{	

	@Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionInterestService transactionInterestService;
    
    
    @Autowired
    private BalanceService balanceService;
    
    
    @Autowired
    private SinaPayClient sinaPayClient;
    
    
    @Autowired
    private CapitalInOutLogService capitalInOutLogService;
    
    @Autowired
    private MemberService memberService;
    
    
    @Autowired
    private RechargeLogWithdrawLogService rechargeLogService;
    
    
    @Autowired
    private MemberBankCardService memberBankCardService;
    
	@Autowired
	private OrderService orderService;
	
	@Autowired
    private BannerService bannerService;
	
	@Autowired
    private ProjectService projectService;
	
	@Autowired
	private BalanceManager balanceManager;
	
	@Autowired
	private SysDictManager sysDictManager;
	
	@Autowired
    private WithdrawLogManager withdrawLogManager;

	@Autowired
	private PopularityInOutLogService popularityInOutLogService;
	
	
	
	private static  int IDISPLAYLENGTH = 10;

	private static String  THIRD_PAY_TICKET="third_pay_ticket";

    /**
     *   充值提现列表页面
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "rechargeIndex",method = RequestMethod.GET)
    public ModelAndView rechargeIndex(HttpServletRequest req,
                                 HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
		

//		if (ServletUtil.isVerifyTrueName()) {

			MemberSessionDto member = getMember();
			Long memberId = member.getId();

			countReAndWithDraw(model, memberId);
			getBalanceAndEaring(model, memberId);
			List<FinEaringDto> finList= memberService.getfin();
			model.addObject("fin",finList);
			Map<String, Object> map = Maps.newHashMap();
			map.put("memberId", memberId);

			int repage = rechargeLogService
					.selectRechargeLogForPaginTotalCountWeb(map);
			model.addObject("pageRe", repage);
			int wipage = rechargeLogService
					.selectWithdrawLogForPaginTotalCountWeb(map);
			model.addObject("pagWh", wipage);
			// 存钱罐七日收益
			ResultDO<MemberSavingPotBiz> result = capitalInOutLogService
					.getMemberSavingPotBiz(getMember().getId());
			model.addObject("memberSavingPotBiz", result.getResult());
			// 存钱罐收益
			BigDecimal savingPotEarnig = this.balanceService.queryBalance(
					memberId, TypeEnum.BALANCE_TYPE_MEMBER_TOTAL_PIGGY)
					.getBalance();
			model.addObject("savingPotEarnig", savingPotEarnig);
			model.setViewName("/member/rechargeIndex");
//		} else {
//			model.setViewName("forward:/member/sinapay");
//		}

		return model;
	}

	private void countReAndWithDraw(ModelAndView model, Long memberId) {
		int countRecharge = rechargeLogService.countRecharge(memberId);
		model.addObject("countRecharge", countRecharge);
		int countWithDraw = rechargeLogService.countWithDraw(memberId);
		model.addObject("countWithDraw", countWithDraw);
		BigDecimal totalRecharge = rechargeLogService.totalRecharge(memberId);
		model.addObject("totalRecharge", FormulaUtil.formatCurrencyNoUnit(totalRecharge));
		BigDecimal totalWithDraw = rechargeLogService.totalWithDraw(memberId);
		model.addObject("totalWithDraw", FormulaUtil.formatCurrencyNoUnit(totalWithDraw));
	}   
    /**
     * 存钱罐余额，存钱收益
     * @param model
     * @param memberID
     * author: pengyong
     * 下午8:58:02
     */
    private void getBalanceAndEaring(ModelAndView model,Long memberID){
    	 Balance queryBalance = this.balanceService.queryBalance(memberID, TypeEnum.BALANCE_TYPE_PIGGY);
    	 model.addObject("balance", queryBalance);    	 
         List<CapitalInOutLog> earing = capitalInOutLogService.selectEaring(memberID, TypeEnum.BALANCE_TYPE_PIGGY.getType(),
                 TypeEnum.FINCAPITALINOUT_TYPE_THIRDPAY.getType(), Constant.FIN_SEVEN);         
         model.addObject("earings", earing);         
         
    }

    /**
     * 会员中心
     * @author pengyong
     */
    @RequestMapping(value = "home")
    public ModelAndView getMemberCenter() {
    	ModelAndView model = new ModelAndView();
    	Long memberId = getMember().getId();
    	ResultDO result = balanceService.getMemberCenterData(memberId);
    	model.addObject("data", result.getResult());
    	ResultDO<BannerFroAreaBiz> banner = bannerService.showBannerByArea(TypeEnum.BANNER_CHANNEL_TYPE_PC.getType(), "memberCenterHead", 0, 1);
    	if(banner.isSuccess()) {
    		model.addObject("headBanner", banner.getResultList().get(0));
    	}
    	int count = projectService.collectingProject(memberId);
    	//统计登录用户的 借款项目数
    	//int countBorrower=projectService.countCurrentBorrowerByMemberId(memberId);
    	BigDecimal investAmount = projectService.selectCollectProjectForMemberInvestAmount(memberId);
    	Long transactionTime = projectService.selectCollectProjectDescTransactionTime(memberId);
		OverduePopularityBiz overduepopularity= popularityInOutLogService.queryOverduePopularity(memberId);
		model.addObject("overduepopularity",overduepopularity);
    	model.addObject("transactionTime",transactionTime);
    	model.addObject("investAmount",investAmount);
    	model.addObject("count",count);
    	//model.addObject("countBorrower",countBorrower);
    	model.setViewName("/member/memberIndex");		
        return model;
    }

	/**
	 * 会员充值页面
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "rechargePage")
	public ModelAndView rechargePage(HttpServletRequest req,
									 HttpServletResponse resp) {
		ModelAndView model = new ModelAndView();
//		if (ServletUtil.isVerifyTrueName()) {
			List<MemberBankCard> memberBankCards = this.memberBankCardService.selectAllQuickPayBankCard(getMember().getId());
			model.addObject("payBankCards", memberBankCards);
			model.setViewName("/member/rechargePage");
//		} else {
//			model.setViewName("forward:/member/sinapay");
//		}
		return model;
	}
	
    /**
     * 会员充值.验证第三方签名(收银台模式)
     * 
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "recharge",method = RequestMethod.POST) 
    @ResponseBody
    public ResultDO<Object> recharge(HttpServletRequest req, HttpServletResponse resp) {
        MemberSessionDto memberDto = ServletUtil.getUserDO();
        Long memberID = memberDto.getId();
        String amount = ServletRequestUtils.getStringParameter(req,"amount","0.0");
        String orderNo = ServletRequestUtils.getStringParameter(req,"orderNo","");
        int rechargeType = ServletRequestUtils.getIntParameter(req,"rechargeType",1);
        TypeEnum type = TypeEnum.RECHARGELOG_TYPE_DIRECTLY;
        if(rechargeType == TypeEnum.RECHARGELOG_TYPE_TRADING.getType()){
        	type = TypeEnum.RECHARGELOG_TYPE_TRADING;
        }
        
        ResultDO<Object> result = new ResultDO<Object>();
        BigDecimal bigDecimal = new BigDecimal(amount);
        //充值金额必须大于0.01
        if(bigDecimal.compareTo(new BigDecimal(Config.minrechargeAmount)) == -1){
        	result.setResultCode(ResultCode.MEMBER_RECHARGE_MUST_MORE_ERROR);
        	return result;
        }
        try {
        	boolean isMobile = ServletUtil.isMobile(req);
        	return this.memberService.createHostingDepositAndRechargeLog(memberID, getIp(req), bigDecimal, type, orderNo, isMobile);
        } catch (Exception e){
        	result.setResultCode(ResultCode.ERROR_SYSTEM);
            logger.error("[托管充值]出现异常",e);
            return result;
        }
        
    }
    
	/**
	 * 会员充值.绑卡充值 1,发送请求到第三方支付
	 */
	@RequestMapping(value = "rechargeOnBankCard",method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<Object> rechargeOnBankCard(HttpServletRequest req, HttpServletResponse resp) {
		MemberSessionDto userName = ServletUtil.getUserDO();
		Long memberID = userName.getId();
		ResultDO<Object> result = new ResultDO<Object>();
		String  amount = 		ServletRequestUtils.getStringParameter(req,"amount","0.0");
		Long  cardID =ServletRequestUtils.getLongParameter(req, "cardID", 0);
		int  rechargeType = ServletRequestUtils.getIntParameter(req,"rechargeType",1);
		String  orderNo = ServletRequestUtils.getStringParameter(req,"orderNo","");
		TypeEnum type = TypeEnum.RECHARGELOG_TYPE_DIRECTLY;
		if(rechargeType == TypeEnum.RECHARGELOG_TYPE_TRADING.getType()){
			type = TypeEnum.RECHARGELOG_TYPE_TRADING;
		}
		BigDecimal  bigDecimal =  new BigDecimal(amount);
		//充值金额必须大于1.01 必须要银行卡号
		if(bigDecimal.compareTo(new BigDecimal(Config.minrechargeAmount))== -1 || cardID ==0){
			result.setResultCode(ResultCode.MEMBER_RECHARGE_MUST_MORE_ERROR);
			return result;
		}
		try {
			String payerIp = getIp(req);
			result = this.memberService.rechargeOnCardAndRechargeLog(memberID, payerIp, bigDecimal, cardID, type, orderNo);
		}catch (Exception e){
			logger.error("绑卡支付异常",e);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return result;
	}
	/**
	 * 会员充值.绑卡充值 2,用户验证码和ticket
	 */
	@RequestMapping(value = "rechargeOnBankCardCheck", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO<Object> rechargeOnBankCardCheck(HttpServletRequest req, HttpServletResponse resp) {
		MemberSessionDto userName = ServletUtil.getUserDO();
		Long memberID = userName.getId();
		String ticket = ServletRequestUtils.getStringParameter(req, "ticket", "");
		//充值记录订单号
		String outAdvanceNo = ServletRequestUtils.getStringParameter(req, "outAdvanceNo", "");
		String checkCode = ServletRequestUtils.getStringParameter(req, "checkCode", "");
		if (StringUtil.isBlank(ticket) || StringUtil.isBlank(outAdvanceNo) || StringUtil.isBlank(checkCode)) {
			ResultDO<Object> resultDO = new ResultDO<>();
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
			resultDO.setSuccess(false);
			return resultDO;
		}
		ResultDO<Object> resultDO = this.memberService.rechargeOnCardAndCheck(outAdvanceNo, ticket, checkCode, getIp(req));
		return resultDO;
	}

    /**
     * 提现发起页面
     */  
    @RequestMapping(value = "withdrawPage")
    public ModelAndView withdrawPage(HttpServletRequest req,
                                 HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();      
//        if(ServletUtil.isVerifyTrueName()){        	  
            MemberSessionDto userName = ServletUtil.getUserDO();
            List<MemberBankCard>  cardlist = memberBankCardService.getMemberBankCardByMemberId(userName.getId());
            Balance queryBalance = balanceService.queryBalance(userName.getId(), TypeEnum.BALANCE_TYPE_PIGGY);
            Balance popularityBalance = balanceService.queryBalance(userName.getId(), TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
            model.addObject("cardlist", cardlist);  
            model.addObject("availableBalance", queryBalance.getAvailableBalance());
            model.addObject("popularityBalance", popularityBalance.getAvailableBalance());  
            model.setViewName("/member/withdraw");
//        }else{
//        	 model.setViewName("forward:/member/sinapay");
//        }       
       return  model;
    }
    
    /**
     * 提现记录 分页
     *
     * @return
     */
    @RequestMapping(value = "findByPageWithdrawLog")
    @ResponseBody
    public ResultDO<WithdrawLogDto> findByPageWithdrawLog(HttpServletRequest req, HttpServletResponse resp) {
 	   int currentPage = ServletRequestUtils.getIntParameter(req, "currentPage", 0);        
       int iDisplayLength = ServletRequestUtils.getIntParameter(req, "iDisplayLength", IDISPLAYLENGTH);
       int iDisplayStart = iDisplayLength* (currentPage) ;
       	MemberSessionDto userName = ServletUtil.getUserDO();
		Long memberId = userName.getId();		   
		Map<String, Object> map = Maps.newHashMap();
		map.put(Constant.STARTROW, iDisplayStart);
		map.put(Constant.PAGESIZE, iDisplayLength);
		map.put("memberId", memberId);
		List<WithdrawLogDto> list = rechargeLogService.selectWithdrawLogForPaginWeb(map);
		ResultDO<WithdrawLogDto> result = new ResultDO<WithdrawLogDto>();
		result.setResultList(list);
		return result;
    }
    
    /**
     * 充值记录 分页
     *
     * @return
     */
    @RequestMapping(value = "findByPageRechargeLog")
    @ResponseBody
    public ResultDO<RechargeLogDto> findByPageRechargeLog(HttpServletRequest req, HttpServletResponse resp) {
    	
    	   int currentPage = ServletRequestUtils.getIntParameter(req, "currentPage", 0);        
           int iDisplayLength = ServletRequestUtils.getIntParameter(req, "iDisplayLength", IDISPLAYLENGTH);
           int iDisplayStart = iDisplayLength* (currentPage) ;
           MemberSessionDto userName = ServletUtil.getUserDO();
   		Long memberId = userName.getId();
        Map<String, Object> map = Maps.newHashMap();   
        map.put("memberId", memberId);
        map.put(Constant.STARTROW, iDisplayStart);
        map.put(Constant.PAGESIZE, iDisplayLength);       
        List<RechargeLogDto> page1= rechargeLogService.selectRechargeLogForPaginWeb(map);
        ResultDO<RechargeLogDto> result = new ResultDO<RechargeLogDto>();
        result.setResultList(page1);       
        return result;
    }
    

    /**
     * 会员发起提现业务
     */
    @RequestMapping(value = "withdrawSubmit")
    @ResponseBody
    public ResultDO<Object> withdrawSubmit( @ModelAttribute("form") WithdrawLog record,HttpServletRequest req,
                                           HttpServletResponse resp) {
        ResultDO<Object> result = new ResultDO<Object>();
        MemberSessionDto userName = ServletUtil.getUserDO();
        record.setMemberId(userName.getId());
        if(record.getBankCardId() == null  ){
        	result.setSuccess(false);
        	result.setResultCode(ResultCode.MEMBER_USERNAME_WITHDRAW_BANKID);
        	return result;
        }       
        if(record.getWithdrawAmount() == null
        		|| record.getWithdrawAmount().compareTo(new BigDecimal(Config.minwithdrawAmount)) == -1        		     		
        		){
        	result.setSuccess(false);
        	result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM);
        	return result;
        }        
        if(!memberBankCardService.isExist( record.getBankCardId(),userName.getId())){
        	result.setSuccess(false);
        	result.setResultCode(ResultCode.MEMBER_USERNAME_WITHDRAW_BANKID);
        	return result;
        }
       
        //判断余额是否足够
        Balance balance = balanceService.queryBalance(getMember().getId(), TypeEnum.BALANCE_TYPE_PIGGY);
        if(balance.getAvailableBalance().doubleValue()<record.getWithdrawAmount().doubleValue()) {
        	result.setResultCode(ResultCode.BALANCE_MEMBER_PIGGY_BALANCE_LACKING);
        	return result;
        }
        BigDecimal freeWithdrawAmount = new BigDecimal(SysServiceUtils.getWithdrawalFees());//提现手续费值，通过数据字典添加
        Balance popularity = balanceService.queryBalance(getMember().getId(), TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
        if(popularity.getAvailableBalance().doubleValue()<freeWithdrawAmount.doubleValue()) {
			String msg = "提现需扣除{0}人气值，当前您的人气值不足无法提现。";
			msg = MessageFormat.format(msg, freeWithdrawAmount);
			ResultCode.BALANCE_MEMBER_POPULARITY_LACKING.setMsg(msg);
        	result.setResultCode(ResultCode.BALANCE_MEMBER_POPULARITY_LACKING);
        	return result;
        }
        //提现手续
		record.setFee(new BigDecimal(PropertiesUtil.getProperties("business.freeWithdrawAmount")));
		record.setWithdrawSource(TypeEnum.MEMBER_LOGIN_SOURCE_PC.getType());
        BigDecimal  withdrawAmount  = record.getWithdrawAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
        record.setWithdrawAmount(withdrawAmount);
        record.setWithdrawFee(freeWithdrawAmount.intValue());
        //TODO 后期需要判断是否超过每日提现笔数
        int insertSelective = rechargeLogService.reqSubmitWithDraw(record);
        if (insertSelective == 1) {
            result.setSuccess(true);
        } else {
            result.setSuccess(false);
        }
        return result;
    }
    
    /**
     * 会员提现发起(收银台模式)
     * 
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "withdraw",method = RequestMethod.POST) 
    @ResponseBody
    public ResultDO<Object> withdraw(HttpServletRequest req, HttpServletResponse resp) {
        MemberSessionDto memberDto = ServletUtil.getUserDO();
        Long memberId = memberDto.getId();
        String amount = ServletRequestUtils.getStringParameter(req,"withdrawAmount","0.0");
        ResultDO<Object> result = new ResultDO<Object>();
        // 提现金额必须大于0.01
        if (StringUtil.isBlank(amount) || new BigDecimal(amount).compareTo(new BigDecimal(Config.minwithdrawAmount))== -1) {
        	result.setResultCode(ResultCode.MEMBER_WITHDRAW_MUST_MORE_ERROR);
        	return result;
        }
        try {
        	BigDecimal withdrawAmount = new BigDecimal(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
        	// 查询并同步存钱罐可用余额是否大于提现金额，是否足够提现
        	Balance balance = balanceManager.synchronizedBalance(memberId, TypeEnum.BALANCE_TYPE_PIGGY);
        	// 提现金额大于可用余额
        	if (balance.getAvailableBalance().compareTo(withdrawAmount) == -1) {
        		result.setResultCode(ResultCode.BALANCE_MEMBER_PIGGY_BALANCE_LACKING);
        		logger.info("[会员提现发起]" + result.getResultCode().getMsg());
        		return result;
        	}
        	// 提现手续费值，
        	//BigDecimal freeWithdrawAmount=balanceService.getWithdrawFee(withdrawAmount,memberId);
        	BigDecimal freeWithdrawAmount = new BigDecimal(SysServiceUtils.getWithdrawalFees());//提现手续费值，通过数据字典添加
        	// 查询会员人气值
        	Balance popularity = balanceService.queryBalance(memberId, TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
        	if(popularity.getAvailableBalance().doubleValue() < freeWithdrawAmount.doubleValue()) {
        		String msg = "提现需扣除{0}人气值，当前您的人气值不足无法提现。";
        		msg = MessageFormat.format(msg, freeWithdrawAmount);
        		ResultCode.BALANCE_MEMBER_POPULARITY_LACKING.setMsg(msg);
        		result.setResultCode(ResultCode.BALANCE_MEMBER_POPULARITY_LACKING);
        		logger.info("[会员提现发起]" + msg);
        		return result;
        	}
        	return this.memberService.createWithdrawAndWithdrawLog(memberId, getIp(req), withdrawAmount, freeWithdrawAmount, 
        			new BigDecimal(PropertiesUtil.getProperties("business.freeWithdrawAmount")));
        } catch (Exception e){
        	result.setResultCode(ResultCode.ERROR_SYSTEM);
        	logger.error("[会员提现发起]出现异常",e);
        	return result;
        }
    }
    
    @RequestMapping(value = "withdrawSubmitSucess")
    public String withdrwaSucess(){
    	return "/member/withdrawSubmit";
    }

	/**
	 * 我的存钱罐
	 * 
	 * @return
	 */
	@RequestMapping(value = "/savingPot")
	public ModelAndView getMemberSavingPotBiz() {
		ModelAndView model = new ModelAndView();
//		if (ServletUtil.isVerifyTrueName()) {
			ResultDO<MemberSavingPotBiz> result = capitalInOutLogService.getMemberSavingPotBiz(getMember().getId());
			List<FinEaringDto> finList = memberService.getfin();
			if(Collections3.isNotEmpty(finList)){
				Collections.reverse(finList);
			}
			int countBorrower=projectService.countCurrentBorrowerByMemberId(getMember().getId());
			model.addObject("fin", finList);
			model.addObject("result", result);
			model.addObject("countBorrower", countBorrower);
			model.setViewName("/member/savingPot");
//		} else {
//			model.setViewName("forward:/member/sinapay");
//		}
		return model;
	}
	
	/**
	 * 用户利息收益详情
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "getTransactionInterestDetailForMember")
    @ResponseBody
	public ResultDO getTransactionInterestDetailForMember(HttpServletRequest req, HttpServletResponse resp){
		Long memberId = getMember().getId();    	
    	TransactionInterestQuery query = new TransactionInterestQuery();
    	query.setMemberId(memberId);
    	getTransactionInterestQuery(query,req);
    	return transactionInterestService.getTransactionInterestDetailForMember(query);
	}
	
	/**
	 * 用户本金收益详情
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "getTransactionPrincipalDetailForMember")
    @ResponseBody
	public ResultDO getTransactionPrincipalDetailForMember(HttpServletRequest req, HttpServletResponse resp){
		Long memberId = getMember().getId();
    	TransactionInterestQuery query = new TransactionInterestQuery();
    	query.setMemberId(memberId);
    	query.setPrincipal(true);//本金
    	getTransactionInterestQuery(query,req);
    	return transactionInterestService.getTransactionInterestDetailForMember(query);
	}
	
	/**
	 * 用户累计投资本金详情
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "getTransactionDetailForMember")
	@ResponseBody
	public ResultDO getTransactionDetailForMember(HttpServletRequest req, HttpServletResponse resp){
		Long memberId = getMember().getId();
		TransactionInterestQuery query = new TransactionInterestQuery();
		query.setMemberId(memberId);
		getTransactionInterestQuery(query,req);
		return transactionService.getTransactionsDetailForMember(query);
	}
	/**
	 * 已转让本金
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "getTransactionInterestDetailForMemberTransfer")
	@ResponseBody
	public ResultDO getTransactionInterestDetailForMemberTransfer(HttpServletRequest req, HttpServletResponse resp){
		Long memberId = getMember().getId();
		TransactionInterestQuery query = new TransactionInterestQuery();
		query.setMemberId(memberId);
		return transactionService.getTransactionInterestDetailForMemberTransfer(query);
	}
	
	/**
	 * 
	 * @Description:会员已转让收款/手续费
	 * @param req
	 * @param resp
	 * @return
	 * @author: wangyanji
	 * @time:2017年1月4日
	 */
	@RequestMapping(value = "getTransferDetailForMember")
	@ResponseBody
	public Object getTransferDetailForMember(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMember().getId();
		int type = ServletRequestUtils.getIntParameter(req, "type", 0);
		return transactionService.getTransferDetailForMember(type, memberId);
	}
	
	/**
	 *  总计转让收款
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "getTotalTransferAmountForMemberTransfer")
	@ResponseBody
	public ResultDO getTotalTransferAmountForMemberTransfer(HttpServletRequest req, HttpServletResponse resp){
		Long memberId = getMember().getId();
		CapitalInOutLogQuery query = new CapitalInOutLogQuery();
		query.setMemberId(memberId);
		return transactionService.getTotalTransferAmountForMemberTransfer(query);
	}
	/**
	 * 还本付息查询Model
	 * @param query
	 * @param req
	 * @return
	 */
	private TransactionInterestQuery getTransactionInterestQuery(TransactionInterestQuery query, HttpServletRequest req){
		String type = ServletRequestUtils.getStringParameter(req, "type", "");
		if(type.equals("unreceived")){//待收
			query.setAsc(true);
			query.setStatus(0);
		}else if(type.equals("received")){//已收
			query.setAsc(false);
			query.setStatus(1);
		}else{//全部 
			query.setAsc(false);
		}
		return query;
	}
	
	
	/**
	 * 根据日期获得用户还本付息详情
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "getMemberInterestDetail")
    @ResponseBody
	public ResultDO getMemberInterestDetail(HttpServletRequest req, HttpServletResponse resp){
		Long memberId = getMember().getId();
		String endDate = ServletRequestUtils.getStringParameter(req, "date",null);
    	return transactionInterestService.getMemberInterestDetail(memberId, endDate);
	}
	
	/**
	 * 用户中心数据
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "getMemberCenterStatData")
    @ResponseBody
	public ResultDO getMemberCenterStatData(HttpServletRequest req, HttpServletResponse resp){
		Long memberId = getMember().getId();
    	return balanceService.getMemberCenterData(memberId);
	}
	
	/**
	 * 用户取消提现
	 */
	@RequestMapping(value = "cancelWithdraw",method=RequestMethod.POST)
	@ResponseBody
	public ResultDO<WithdrawLog> cancelWithdraw(@ModelAttribute("id")Long id){
		ResultDO<WithdrawLog> logDo = new ResultDO<WithdrawLog>();
		int status = StatusEnum.WITHDRAW_STATUS_CANCEL.getStatus();
		logDo = rechargeLogService.cancelToWithDrawLog(id, status);
		return logDo;
	}
}
