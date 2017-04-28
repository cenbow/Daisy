package com.yourong.api.controller;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yourong.api.dto.FinEaringDto;
import com.yourong.api.dto.MemberInfoDto;
import com.yourong.api.dto.MemberSavingPotBizDto;
import com.yourong.api.dto.RechargeDto;
import com.yourong.api.dto.RechargeLogDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.dto.WithdrawLogDto;
import com.yourong.api.service.ActivityLotteryService;
import com.yourong.api.service.BalanceService;
import com.yourong.api.service.CapitalInOutLogService;
import com.yourong.api.service.CouponService;
import com.yourong.api.service.MemberBankCardService;
import com.yourong.api.service.MemberInfoService;
import com.yourong.api.service.MemberNotifySettingsService;
import com.yourong.api.service.MemberService;
import com.yourong.api.service.OrderService;
import com.yourong.api.service.RechargeLogWithdrawLogService;
import com.yourong.api.utils.APIPropertiesUtil;
import com.yourong.api.utils.SysServiceUtils;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.enums.BankCode;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.RechargeLogManager;
import com.yourong.core.fin.manager.WithdrawLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.RechargeLog;
import com.yourong.core.fin.model.WithdrawFee;
import com.yourong.core.fin.model.WithdrawLog;
import com.yourong.core.fin.model.biz.MemberSavingPotBiz;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.model.Order;
import com.yourong.core.uc.model.MemberBankCard;

/**
 * 资产统计,提现，充值 Created by py on 2015/3/24.
 */
@Controller
@RequestMapping("security/banlance")
public class BalanceController extends BaseController {

	private static final BigDecimal MINWITHDRAW_AMOUNT = new BigDecimal(PropertiesUtil.getProperties("business.minwithdrawAmount"));
	private static final BigDecimal MINRECHARGE_AMOUNT = new BigDecimal(PropertiesUtil.getProperties("business.minrecharge"));
	private static final BigDecimal FREEWITHDRAWAMOUNT = new BigDecimal(PropertiesUtil.getProperties("business.freeWithdrawAmount"));

	@Autowired
	private MemberNotifySettingsService memberNotifySettingsService;

	@Autowired
	private MemberInfoService memberInfoService;

	@Autowired
	private CouponService couponService;

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
	private BalanceService balanceService;

	@Autowired
	private ActivityLotteryService activityLotteryService;
	
	@Autowired
	private BalanceManager balanceManager;
	
	@Autowired
	private RechargeLogManager rechargeLogManager;
	
	@Autowired
    private WithdrawLogManager withdrawLogManager;
	
	@Autowired
	private SysDictManager sysDictManager;

	private static final int IDISPLAYLENGTH = 10;;
	
	/**
	 * 充值 （收银台模式）
	 */
	@RequestMapping(value = "recharge", method = RequestMethod.POST, headers = { "Accept-Version=1.7.0" })
	@ResponseBody
	public ResultDTO<Object> rechargeCheck(@ModelAttribute RechargeDto dto, HttpServletRequest req, HttpServletResponse resp) {
		ResultDTO<Object> resultDTO = new ResultDTO<>();
		Long memberID = getMemberID(req);
		if (StringUtil.isBlank(dto.getAmount()) || dto.getRechargeType() ==null) {
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
			return resultDTO;
		}
		Integer source = getRequestSource(req);
	     try {
			String payerIp = getIp(req);
			resultDTO = this.memberService.createHostingDepositAndRechargeLog(memberID, payerIp, dto,source);
		} catch (Exception e) {
			logger.error("[托管充值]出现异常",e);
			resultDTO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
	        return resultDTO;
	}
	/**
	 * 
	 * @Description:查询充值结果
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年8月9日 下午4:49:33
	 */
	 @RequestMapping(value = "queryRecharge", method = RequestMethod.POST, headers = {"Accept-Version=1.7.0"})
	 @ResponseBody
	 public ResultDTO queryRecharge(HttpServletRequest req, HttpServletResponse resp){
	     	Long memberId = getMemberID(req);
	     	ResultDTO result = new ResultDTO();
	     	String tradeNo = ServletRequestUtils.getStringParameter(req, "tradeNo", null);
	     	RechargeLogDto dto=new RechargeLogDto();
	     	try {
	     		RechargeLog rechargeLog=rechargeLogManager.getRechargeLogByTradeNo(tradeNo);
	     		if(rechargeLog==null){
	     			result.setResult(dto);
	     			result.setResultCode(ResultCode.RECHARGE_NO_NOT_EXSIST_ERROR);
	     			return result;
	     		}
	     		dto.setAmount(rechargeLog.getAmount());
	     		dto.setRechargeNo(rechargeLog.getRechargeNo());
	     		dto.setRechargeTime(rechargeLog.getRechargeTime());
	     		dto.setRemarks(rechargeLog.getRemarks());
	     		dto.setStatus(rechargeLog.getStatus());
	     		dto.setBankCode(rechargeLog.getBankCode());
	     		dto.setOrderNo(rechargeLog.getOrderNo());
	     		dto.setType(rechargeLog.getType());
	     		BankCode bankCode = BankCode.getBankCode(rechargeLog.getBankCode());
        		if(bankCode!=null){
        			dto.setBankName(bankCode.getRemarks());
        		}
	     		result.setResult(dto);
			} catch (ManagerException e) {
				logger.error("查询充值结果出现异常",e);
			}
	        return result;
	    }

	/**
	 * 提现记录 分页
	 *
	 * @return
	 */
	@RequestMapping(value = "queryWithdrawLogByPage", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public ResultDTO queryWithdrawLogByPage(@RequestParam("pageNo") int currentPage, HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		int iDisplayLength = ServletRequestUtils.getIntParameter(req, "pageSize", IDISPLAYLENGTH);
		if (iDisplayLength > IDISPLAYLENGTH) {
			iDisplayLength = IDISPLAYLENGTH;
		}
		int iDisplayStart = iDisplayLength * (currentPage - 1);
		Map<String, Object> map = Maps.newHashMap();
		map.put(Constant.STARTROW, iDisplayStart);
		map.put(Constant.PAGESIZE, iDisplayLength);
		map.put("memberId", memberId);
		List<WithdrawLogDto> list = rechargeLogService.selectWithdrawLogForPaginWeb(map);
		int totalCountWeb = rechargeLogService.selectWithdrawLogForPaginTotalCountWeb(map);

		Page page = new Page<>();
		page.setData(list);
		page.setiDisplayLength(iDisplayLength);
		page.setPageNo(currentPage);
		page.setiTotalRecords(totalCountWeb);
		ResultDTO result = new ResultDTO();
		result.setResult(page);
		return result;
	}

	/**
	 * 充值记录 分页
	 *
	 * @return
	 */
	@RequestMapping(value = "queryRechargeLogByPage", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public ResultDTO<List<RechargeLogDto>> queryRechargeLogByPage(@RequestParam("pageNo") int currentPage, HttpServletRequest req,
			HttpServletResponse resp) {
		int iDisplayLength = ServletRequestUtils.getIntParameter(req, "pageSize", IDISPLAYLENGTH);
		if (iDisplayLength > IDISPLAYLENGTH) {
			iDisplayLength = IDISPLAYLENGTH;
		}
		int iDisplayStart = iDisplayLength * (currentPage - 1);
		Map<String, Object> map = Maps.newHashMap();
		map.put("memberId", getMemberID(req));
		map.put(Constant.STARTROW, iDisplayStart);
		map.put(Constant.PAGESIZE, iDisplayLength);
		List<RechargeLogDto> rechargeLogDtos = rechargeLogService.selectRechargeLogForPaginWeb(map);
		Page page = new Page<>();
		int totalCountWeb = rechargeLogService.selectRechargeLogForPaginTotalCountWeb(map);
		page.setData(rechargeLogDtos);
		page.setiDisplayLength(iDisplayLength);
		page.setiTotalRecords(totalCountWeb);
		page.setPageNo(currentPage);
		ResultDTO result = new ResultDTO();
		result.setResult(page);

		return result;
	}
	
	/**
	 * 充值记录 分页
	 *
	 * @return
	 */
	@RequestMapping(value = "queryRechargeLogByPage", method = RequestMethod.POST, headers = { "Accept-Version=1.7.0" })
	@ResponseBody
	public ResultDTO<List<RechargeLogDto>> queryRechargeLogByPage1(@RequestParam("pageNo") int currentPage, HttpServletRequest req,
			HttpServletResponse resp) {
		int iDisplayLength = ServletRequestUtils.getIntParameter(req, "pageSize", IDISPLAYLENGTH);
		if (iDisplayLength > IDISPLAYLENGTH) {
			iDisplayLength = IDISPLAYLENGTH;
		}
		int iDisplayStart = iDisplayLength * (currentPage - 1);
		Map<String, Object> map = Maps.newHashMap();
		map.put("memberId", getMemberID(req));
		map.put(Constant.STARTROW, iDisplayStart);
		map.put(Constant.PAGESIZE, iDisplayLength);
		List<RechargeLogDto> rechargeLogDtos = rechargeLogService.selectRechargeLogForPaginWeb(map);
		Page page = new Page<>();
		int totalCountWeb = rechargeLogService.selectRechargeLogForPaginTotalCountWeb(map);
		page.setData(rechargeLogDtos);
		page.setiDisplayLength(iDisplayLength);
		page.setiTotalRecords(totalCountWeb);
		page.setPageNo(currentPage);
		ResultDTO result = new ResultDTO();
		result.setResult(page);

		return result;
	}
	
	/**
	 * 
	 * @Description:提现，收银台模式
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年8月8日 上午11:26:27
	 */
    @RequestMapping(value = "withdraw",method = RequestMethod.POST, headers = { "Accept-Version=1.7.0" }) 
    @ResponseBody
    public ResultDTO<Object> withdraw(@ModelAttribute("form") WithdrawLog record,HttpServletRequest req, HttpServletResponse resp) {
    	Long memberID = getMemberID(req);
		ResultDTO<Object> result = new ResultDTO<Object>();
        // 提现金额必须大于0.01
        if (record.getWithdrawAmount()==null ||  record.getWithdrawAmount().compareTo(new BigDecimal(Config.minwithdrawAmount))== -1) {
        	result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM);
        	return result;
        }
        try {
        	BigDecimal withdrawAmount = record.getWithdrawAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
        	// 查询并同步存钱罐可用余额是否大于提现金额，是否足够提现
        	//同步存钱罐余额
			Balance balance = new Balance();
			try {
				balance = balanceManager.synchronizedBalance(memberID,
						TypeEnum.BALANCE_TYPE_PIGGY);
			} catch (Exception e) {
				logger.error("【冻结存钱罐余额】查询第三方余额失败,memberId={}",memberID);
			}
        	// 提现金额大于可用余额
        	if (balance==null||balance.getAvailableBalance()==null||balance.getAvailableBalance().compareTo(withdrawAmount) == -1) {
        		result.setResultCode(ResultCode.BALANCE_MEMBER_PIGGY_BALANCE_LACKING);
        		logger.error("[会员提现发起]" + result.getResult());
        		return result;
        	}
        	// 提现手续费值，通过数据字典添加
        	//BigDecimal freeWithdrawAmount=balanceService.getWithdrawFee(withdrawAmount,memberID);
        	BigDecimal freeWithdrawAmount=new BigDecimal(APIPropertiesUtil.getWithdrawalFees());
        	// 查询会员人气值
        	Balance popularity = balanceService.queryBalance(memberID, TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
        	if(popularity.getAvailableBalance().doubleValue() < freeWithdrawAmount.doubleValue()) {
        		String msg = "提现需扣除{0}人气值，当前您的人气值不足无法提现。";
        		msg = MessageFormat.format(msg, freeWithdrawAmount);
        		ResultCode.BALANCE_MEMBER_POPULARITY_LACKING.setMsg(msg);
        		result.setResultCode(ResultCode.BALANCE_MEMBER_POPULARITY_LACKING);
        		logger.error("[会员提现发起]" + msg);
        		return result;
        	}
        	Integer source = getRequestSource(req);
        	result = this.memberService.createWithdrawAndWithdrawLog(memberID, getIp(req), withdrawAmount, freeWithdrawAmount,source,FREEWITHDRAWAMOUNT);
        } catch (Exception e){
        	result.setResultCode(ResultCode.ERROR_SYSTEM);
        	logger.error("[会员提现发起]出现异常",e);
        	return result;
        }
        return result;
    }
    
    /**
     * 
     * @Description:查询提现结果
     * @param req
     * @param resp
     * @return
     * @author: chaisen
     * @time:2016年8月9日 下午6:47:58
     */
    @RequestMapping(value = "queryWithdraw", method = RequestMethod.POST, headers = {"Accept-Version=1.7.0"})
	 @ResponseBody
	 public ResultDTO queryWithdraw(HttpServletRequest req, HttpServletResponse resp){
	     	Long memberId = getMemberID(req);
	     	ResultDTO result = new ResultDTO();
	     	String withdrawNo = ServletRequestUtils.getStringParameter(req, "withdrawNo", null);
	     	try {
	     		WithdrawLog withdrawLog=withdrawLogManager.getWithdrawLogByTradeNo(withdrawNo);
	     		if(withdrawLog==null){
	     			result.setResultCode(ResultCode.RECHARGE_NO_NOT_EXSIST_ERROR);
	     			 return result;
	     		}
	     		WithdrawLogDto dto=new WithdrawLogDto();
	     		dto.setWithdrawAmount(withdrawLog.getWithdrawAmount());
	     		dto.setWithdrawTime(withdrawLog.getWithdrawTime());
	     		dto.setStatus(withdrawLog.getStatus());
	     		dto.setNotice(withdrawLog.getNotice());
	     		dto.setWithdrawNo(withdrawLog.getWithdrawNo());
	     		if(withdrawLog!=null&&withdrawLog.getBankCardId()!=null){
	     			MemberBankCard bankCard = memberBankCardService.selectByPrimaryKey(withdrawLog.getBankCardId());
	     			dto.setBankCardNo(bankCard.getCardNumber());
	     		}
	     		result.setResult(dto);
			} catch (ManagerException e) {
				logger.error("查询提现结果出现异常",e);
			}
	        return result;
	    }
	/**
	 * 我的存钱罐
	 *
	 * @return
	 */
	@RequestMapping(value = "queryMemberSavingPot", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public ResultDTO getMemberSavingPotBiz(HttpServletRequest req, HttpServletResponse resp) {
		ResultDTO dto = new ResultDTO();
		ResultDO<MemberSavingPotBiz> result = capitalInOutLogService.getMemberSavingPotBiz(getMemberID(req));
		MemberSavingPotBiz memberSavingPotBiz = result.getResult();
		MemberSavingPotBizDto memberSavingPotBizDto = BeanCopyUtil.map(memberSavingPotBiz, MemberSavingPotBizDto.class);
		List<FinEaringDto> finList = memberService.getfin();
		if (Collections3.isNotEmpty(finList)) {
			Collections.reverse(finList);
		}
		Map map = Maps.newHashMap();
		map.put("fin", finList);
		map.put("savingPot", memberSavingPotBizDto);
		dto.setResult(map);
		return dto;
	}

	/**
	 * 查询用户余额统计信息
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryMemberBalance", headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public ResultDTO queryMemberBalance(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		return balanceService.queryMemberBalance3(memberId);
	}

	/**
	 * 查询用户余额统计信息1.3.0
	 * 没过滤
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryMemberBalance", headers = { "Accept-Version=1.3.0" })
	@ResponseBody
	public ResultDTO queryMemberBalance2(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		return balanceService.queryMemberBalance(memberId);
	}
	/**
	 * 查询用户余额统计信息1.0.2
	 * @param req  过滤
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryMemberBalance", headers = { "Accept-Version=1.0.2" })
	@ResponseBody
	public ResultDTO queryMemberBalance3(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		return balanceService.queryMemberBalance3(memberId);
	}
	/**
	 * 取消提现
	 * 
	 * @param req
	 * @param withdrawId
	 *            提现流水表主键id
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "cancelWithdraw", method = RequestMethod.POST, headers = { "Accept-Version=1.0.0" })
	@ResponseBody
	public ResultDTO<WithdrawLog> cancelWithdraw(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {

		Long Id = ServletRequestUtils.getLongParameter(req, "withdrawId", 0l);

		int status = StatusEnum.WITHDRAW_STATUS_CANCEL.getStatus();

		ResultDTO<WithdrawLog> resultDto = rechargeLogService.cancelWithdraw(Id, status);
		return resultDto;
	}
	
	
	/**
	 * 我的资产
	 * @param req  
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryMemberWorth", headers = { "Accept-Version=1.9.0" })
	@ResponseBody
	public ResultDTO queryMemberWorth(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		return balanceService.queryMemberWorth(memberId);
	}
	
	/**
	 * 累计赚取
	 * @param req  
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "queryMemberEarn", headers = { "Accept-Version=1.9.0" })
	@ResponseBody
	public ResultDTO queryMemberEarn(HttpServletRequest req, HttpServletResponse resp) {
		Long memberId = getMemberID(req);
		return balanceService.queryMemberEarn(memberId);
	}

}

