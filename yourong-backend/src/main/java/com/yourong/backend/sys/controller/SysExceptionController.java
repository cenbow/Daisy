package com.yourong.backend.sys.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.fin.service.BalanceService;
import com.yourong.backend.fin.service.RechargeLogService;
import com.yourong.backend.ic.service.TransferProjectService;
import com.yourong.backend.mc.service.ActivityService;
import com.yourong.backend.mc.service.CouponService;
import com.yourong.backend.tc.service.ContractSignService;
import com.yourong.backend.tc.service.HostingCollectTradeService;
import com.yourong.backend.tc.service.TransactionService;
import com.yourong.backend.uc.service.MemberBankCardService;
import com.yourong.backend.uc.service.MemberService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;

/**
 * 系统异常处理
 * 
 * @author fuyili
 *
 *         创建时间:2015年8月6日上午10:58:45
 */
@Controller
@RequestMapping("sysException")
public class SysExceptionController extends BaseController {

	@Autowired
	private CouponService couponService;

	@Autowired
	private MemberBankCardService memberBankCardService;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private BalanceService balanceService;
	
	@Autowired
	private ActivityService activityService;

	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private ContractSignService contractSignService;
	
	@Autowired
	private SysDictManager sysDictManager;
	
	@Autowired
	private HostingCollectTradeService hostingCollectTradeService;
	
	@Autowired
	private RechargeLogService rechargeLogService;
	
	
	@Autowired
	private TransferProjectService transferProjectService;
	
	@Autowired
	private TransactionService transactionService;

	@RequestMapping(value = "index")
	@RequiresPermissions("sysException:index")
	public String showSysDictIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "/sys/exception/index";
	}

	@RequestMapping(value = "unlockedCoupon")
	@RequiresPermissions("sysException:unlockedCoupon")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "")
	public Object unlockedCoupon(String couponNo) {
		// 优惠券解锁
		return couponService.unlockedCouponByCouponNo(couponNo);
	}

	@RequestMapping(value = "delProjectRedis")
	@RequiresPermissions("sysException:delProjectRedis")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "删除项目缓存")
	public Object delProjectRedis(HttpServletRequest req, HttpServletRequest resp, @ModelAttribute("projectId") Long projectId) {
		ResultDO<String> resultDO = new ResultDO<String>();
		//RedisForProjectClient.clearTransactionDetail(projectId);
		String key = RedisConstant.TRANSACTION_INVERESTLIST_FOR_PROJECT + RedisConstant.REDIS_SEPERATOR + projectId;
		RedisManager.removeObject(key);
		resultDO.setSuccess(true);
		;
		return resultDO;
	}

	/**
	 * 删除用户绑定银行卡功能(目前只将权限设置为开发人员可用)
	 */
	@RequestMapping(value = "deleteBankCard")
	@RequiresPermissions("sysException:deleteBankCard")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "删除用户绑定银行卡功能")
	public ResultDO<Object> deleteBankCard(HttpServletRequest req, HttpServletResponse resp, @ModelAttribute("mobile") Long mobile)
			throws Exception {
		SysDict sysDict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
    	String userIp = "";
    	if(sysDict != null) {
    		userIp = sysDict.getValue();
    	}
		return memberBankCardService.deleteBankCard(mobile, userIp);
	}

	/**
	 * 资金异常处理
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "finIndex")
	@RequiresPermissions("sysException:finIndex")
	public String showSysFinIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "/sys/fundsException/index";
	}

	/**
	 * 绑定用户新浪认证信息
	 */
	@RequestMapping(value = "bindingVerify")
	@RequiresPermissions("sysException:bindingVerify")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "绑定用户新浪认证信息")
	public Object bindingVerify(HttpServletRequest req, HttpServletResponse resp, @ModelAttribute("memberId") Long memberId,
			@ModelAttribute("mobile") Long mobile) throws Exception {
		return memberService.bindingVerifyOnly(memberId, mobile);
	}
	/**
	 * 绑定用户新浪认证信息
	 */
	@RequestMapping(value = "cancellationMember")
	@RequiresPermissions("sysException:cancellationMember")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "冻结用户不使用的账户")
	public Object cancellationMember(HttpServletRequest req, HttpServletResponse resp, 
			@ModelAttribute("mobile") Long mobile) throws Exception {
		return memberService.cancellationMember(mobile);
		//@ModelAttribute("memberId") Long memberId,s
	}
	/**
	 * 
	 * @Description:同步存钱罐
	 * @param req
	 * @param resp
	 * @param memberId
	 * @param dataTime
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年4月11日 上午11:52:08
	 */
	@RequestMapping(value = "synBalance")
	@RequiresPermissions("sysException:synBalance")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "同步存钱罐")
	public ResultDO<Object> synBalance(HttpServletRequest req, HttpServletResponse resp, @ModelAttribute("startTime") String startTime,@ModelAttribute("endTime") String endTime) throws Exception {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		Date startDate = DateUtils.getDateFromString(startTime, "yyyy-MM-dd HH:mm");
		Date endDate = DateUtils.getDateFromString(endTime, "yyyy-MM-dd HH:mm");
		balanceService.synBalance(startDate,endDate);
		resultDO.setSuccess(true);
		return resultDO;
	}

	/**
	 * 
	 * @Description:后台人工生成红包
	 * @param req
	 * @param resp
	 * @param memberId
	 * @param dataTime
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年4月11日 上午11:52:08
	 */
	@RequestMapping(value = "createRedPackage")
	@RequiresPermissions("sysException:createRedPackage")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "人工生成红包")
	public Object createRedPackage(HttpServletRequest req, HttpServletResponse resp,
			@ModelAttribute("transactionId") Long transactionId) throws Exception {
		return activityService.createRedPackage(transactionId);
	}

	/**
	 * 
	 * @Description:直投项目发起代收完成/撤销
	 * @param req
	 * @param resp
	 * @param memberId
	 * @param dataTime
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年4月11日 上午11:52:08
	 */
	@RequestMapping(value = "handleDirectPorjectCollect")
	@RequiresPermissions("sysException:handleDirectPorjectCollect")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "直投项目发起代收完成/撤销")
	public Object handleDirectPorjectCollect(HttpServletRequest req, HttpServletResponse resp, @ModelAttribute("projectId") Long projectId)
			throws Exception {
		return projectManager.afterHandlePreAuthTrade(projectId, true);
	}

	/**
	 * 
	 * @Description:转让项目发起代收完成/撤销
	 * @param req
	 * @param resp
	 * @param memberId
	 * @param dataTime
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年4月11日 上午11:52:08
	 */
	@RequestMapping(value = "handleTransferPorjectCollect")
	@RequiresPermissions("sysException:handleTransferPorjectCollect")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "转让项目发起代收完成/撤销")
	public Object handleTransferPorjectCollect(HttpServletRequest req, HttpServletResponse resp,
			@ModelAttribute("transferId") Long projectId)
			throws Exception {
		return transferProjectService.afterHandlePreAuthTrade(projectId, true);
	}

	/**
	 * 
	 * @Description:根据红包加密串查询交易号
	 * @param req
	 * @param resp
	 * @param redBagCode
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年7月21日 下午2:28:11
	 */
	@RequestMapping(value = "findTransactionId")
	@RequiresPermissions("sysException:findTransactionId")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "红包加密串查询交易号")
	public Object findTransactionId(HttpServletRequest req, HttpServletResponse resp,
			@ModelAttribute("redBagCode") String redBagCode) throws Exception {
		return activityService.findTransactionId(redBagCode);
	}
	
	/**
	 * 
	 * @Description:合同数据重新初始化
	 * @param req
	 * @param resp
	 * @param transactionId
	 * @return
	 * @throws Exception
	 * @author: zhanghao
	 * @time:2016年7月25日 上午10:36:08
	 */
	@RequestMapping(value = "contractReInit")
	@RequiresPermissions("sysException:contractReInit")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "合同数据重新初始化")
	public Object contractReInit(HttpServletRequest req, HttpServletResponse resp, @ModelAttribute("transactionId") Long transactionId)
			throws Exception {
		return contractSignService.contractReInit(transactionId);
	}
	
	/**
	 * 
	 * @Description:合同数据重新初始化
	 * @param req
	 * @param resp
	 * @param transactionId
	 * @return
	 * @throws Exception
	 * @author: zhanghao
	 * @time:2016年7月25日 上午10:36:08
	 */
	@RequestMapping(value = "contractHistoryInit")
	@RequiresPermissions("sysException:contractReInit")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "历史合同数据处理")
	public Object contractHistoryInit(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		return contractSignService.contractHistoryInit();
	}
	
	/**
	 * 
	 * @Description:同步合同签署信息
	 * @param req
	 * @param resp
	 * @param transactionId
	 * @return
	 * @throws Exception
	 * @author: zhanghao
	 * @time:2016年7月25日 上午10:36:08
	 */
	@RequestMapping(value = "queryContractInfo")
	@RequiresPermissions("sysException:contractReInit")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "同步合同签署信息")
	public Object queryContractInfo(HttpServletRequest req, HttpServletResponse resp, @ModelAttribute("transactionId") Long transactionId)
			throws Exception {
		return contractSignService.queryContractInfo(transactionId);
	}
	
	/**
	 * 
	 * @Description:乙方自动签署
	 * @param req
	 * @param resp
	 * @param transactionId
	 * @return
	 * @throws Exception
	 * @author: zhanghao
	 * @time:2016年8月8日 上午10:36:08
	 */
	@RequestMapping(value = "autoSignSecond")
	@RequiresPermissions("sysException:contractReInit")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "乙方自动签署")
	public Object autoSignSecond(HttpServletRequest req, HttpServletResponse resp, @ModelAttribute("transactionId") Long transactionId)
			throws Exception {
		return contractSignService.autoSignSecond(transactionId);
	}
	
	
	
	/**
	 * 
	 * @Description:根据代收交易单号退款/解冻
	 * @param req
	 * @param resp
	 * @param tradeNo 代收交易单号
	 * @param type 处理类型(1/项目余额不足)
	 * @return
	 * @throws Exception
	 * @author: luwenshan
	 * @time:2016年8月10日 下午16:50:08
	 */
	@RequestMapping(value = "hostingRefundUnfrozen")
//	@RequiresPermissions("sysException:hostingRefundUnfrozen")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "根据代收交易单号退款/解冻")
	public Object hostingRefundUnfrozen(HttpServletRequest req, HttpServletResponse resp, 
										@ModelAttribute("tradeNo") String tradeNo, @ModelAttribute("type") Integer type) throws Exception {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		if (type == 1) {
			resultDO = hostingCollectTradeService.hostingRefundUnfrozen(tradeNo);
		} else {
			resultDO.setSuccess(false);
		}
		
		return resultDO;
	}
	
	/**
	 * @Description:根据代收交易号发起退款
	 * @param req
	 * @param resp
	 * @param transactionId
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 */
	@RequestMapping(value = "refundTradeCollection")
	@RequiresPermissions("sysException:refundTradeCollection")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "根据代收交易号发起退款")
	public Object refundTradeCollection(HttpServletRequest req, HttpServletResponse resp, @ModelAttribute("tradeNo") String tradeNo, @ModelAttribute("remark") String remark)
			throws Exception {
		return hostingCollectTradeService.refundTradeCollection(tradeNo,remark);
	}
	
	/**
	 * 
	 * @param req
	 * @param resp
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "sendFiveRites")
	@RequiresPermissions("sysException:sendFiveRites")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "补发五重礼人气值")
	public Object sendFiveRites(HttpServletRequest req, HttpServletResponse resp, @ModelAttribute("projectId") Long projectId)
			throws Exception {
		return hostingCollectTradeService.sendFiveRites(projectId);
	}
	
	/**
	 * 同步选择状态的充值记录
	 * 
	 * @param req
	 * @param resp
	 * @param status
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "sysRechargeLog")
	@RequiresPermissions("sysException:sysRechargeLog")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "同步充值记录")
	public Object sysRechargeLog(HttpServletRequest req, HttpServletResponse resp, @ModelAttribute("rechargeNo") String rechargeNo, 
			@ModelAttribute("startTime") String startTime,@ModelAttribute("endTime") String endTime) throws Exception {
//		startTime = DateUtils.getStrFromDate(DateUtils.getDateFromString(startTime), "yyyy-MM-dd");
		endTime = DateUtils.getDateStrFromDate(DateUtils.addDate(DateUtils.getDateFromString(endTime), 1));
		
		return rechargeLogService.synchronizedRecharge(StatusEnum.RECHARGE_STATUS_PROESS.getStatus(), rechargeNo, startTime, endTime);
	}
	
	
	 /**
     * 获取合同下载链接
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "getContractDownUrl")
	@RequiresPermissions("sysException:getContractDownUrl")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "获取合同下载链接")
    public ResultDO<Object> getContractDownUrl(@RequestParam("transactionId") Long transactionId,HttpServletRequest req, HttpServletResponse resp) {
    	return transactionService.getContractDownUrl(transactionId);
    }
    
    /**
     * 发送会员升级礼包
     * @param transactionId
     * @return
     */
    @RequestMapping(value = "memberLevelUpHandle")
	@RequiresPermissions("sysException:memberLevelUpHandle")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "会员升级礼包")
    public void memberLevelUpHandle(HttpServletRequest req, HttpServletResponse resp) {
    	 memberService.memberLevelUpHandle();
    }
    
    /**
     * 
     * @Description:补发快投有奖
     * @param req
     * @param resp
     * @param projectId
     * @return
     * @throws Exception
     * @author: chaisen
     * @time:2016年12月14日 下午2:41:09
     */
    @RequestMapping(value = "sendQuickDirectLottery")
	@RequiresPermissions("sysException:sendFiveRites")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "补发快投有奖")
	public Object sendQuickDirectLottery(HttpServletRequest req, HttpServletResponse resp, @ModelAttribute("projectId") Long projectId)
			throws Exception {
		return activityService.sendQuickDirectLottery(projectId);
	}
	
	/**
	 * 根据批付号重新发起转让代付
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "synTransferPay")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "异常处理模块", desc = "根据批付号重新发起转让代付")
	@RequiresPermissions("sysException:synTransferPay")
	public ResultDO<?> synTransferPay(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam("batchNo") String batchNo) {
		return transferProjectService.synTransferPay(batchNo);
	}
}