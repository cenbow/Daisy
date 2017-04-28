package com.yourong.common.thirdparty.sinapay;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.yourong.common.constant.Config;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.pay.sina.SinaPayConfig;
import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.AuthTypeWhitelist;
import com.yourong.common.thirdparty.sinapay.common.enums.BankCardType;
import com.yourong.common.thirdparty.sinapay.common.enums.BankServiceType;
import com.yourong.common.thirdparty.sinapay.common.enums.CharsetType;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.RechargeBankCode;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeCode;
import com.yourong.common.thirdparty.sinapay.member.common.Constants;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.PayPasswordDto;
import com.yourong.common.thirdparty.sinapay.member.exception.MemberGatewayInvokeFailureException;
import com.yourong.common.thirdparty.sinapay.member.service.MemberClient;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.CreateCollectTradeDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.CreateDepositDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.CreateWithDrawDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.HandleWithholdAuthorityDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.PayTradeDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.BankPayArgs;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.CreateCollectTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.exceptions.PayFrontException;
import com.yourong.common.thirdparty.sinapay.pay.service.facade.HostingTradeFacade;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;

/**
 * 
 * @desc 新浪收营台接口
 * @author wangyanji 2016年7月11日下午3:17:17
 */
@Service
public class SinaCashDeskClient {

	private static Logger logger = LoggerFactory.getLogger(HostingTradeFacade.class);
	
	@Autowired
    private MemberClient memberClient;
	
	@Autowired
    private HostingTradeFacade hostingTradeFacade;

	/**
	 * 
	 * @Description:支付密码请求方法
	 * @param memberId
	 * @param handleType :
	 *  					1.TypeEnum.SET_PAY_PASSWORD          设置
	 *						2.TypeEnum.MODIFY_PAY_PASSWORD       修改
	 *						3.TypeEnum.FIND_PAY_PASSWORD         找回
	 *						4.TypeEnum.QUERY_IS_SET_PAY_PASSWORD 查询
	 * @return
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2016年7月13日 下午8:31:22
	 */
	public ResultDto<PayPasswordDto> handlePayPassword(Long memberId, int handleType, String returnUrl) throws Exception {
		String apiName = "";
		if (handleType == TypeEnum.SET_PAY_PASSWORD.getType()) {
			apiName = Constants.SET_PAY_PASSWORD;
		} else if (handleType == TypeEnum.MODIFY_PAY_PASSWORD.getType()) {
			apiName = Constants.MODIFY_PAY_PASSWORD;
		} else if (handleType == TypeEnum.FIND_PAY_PASSWORD.getType()) {
			apiName = Constants.FIND_PAY_PASSWORD;
		} else if (handleType == TypeEnum.QUERY_IS_SET_PAY_PASSWORD.getType()) {
			apiName = Constants.QUERY_IS_SET_PAY_PASSWORD;
		}
		ResultDto<PayPasswordDto> rDto = null;
		PayPasswordDto payPasswordDto = new PayPasswordDto(SerialNumberUtil.generateIdentityId(memberId), IdType.UID.toString(), null);
		payPasswordDto.setReturnUrl(returnUrl);
		rDto = memberClient.handlePayPassword(payPasswordDto, apiName);
		return rDto;
	}
	
	/**
	 * 委托扣款授权操作
	 * 
	 * @param memberId
	 * @param handleType
	 * @param returnUrl
	 * @return
	 * @author luwenshan
	 * @time: 2016年8月9日 下午15:38:11
	 * @throws Exception
	 */
	public ResultDto<HandleWithholdAuthorityDto> handleWithholdAuthority(Long memberId, int handleType, String returnUrl) throws Exception {
		ResultDto<HandleWithholdAuthorityDto> result = null;
		HandleWithholdAuthorityDto handleWithholdAuthorityDto = new HandleWithholdAuthorityDto();
		handleWithholdAuthorityDto.setIdentityType(IdType.UID);
		handleWithholdAuthorityDto.setIdentityId(SerialNumberUtil.generateIdentityId(memberId));
		handleWithholdAuthorityDto.setReturnUrl(returnUrl);
		String apiName = "";
		if (handleType == TypeEnum.HANDLE_WITHHOLD_AUTHORITY.getType()) { // 委托
			apiName = Constants.HANDLE_WITHHOLD_AUTHORITY;
			
			handleWithholdAuthorityDto.setAuthTypeWhitelist(AuthTypeWhitelist.ACCOUNT);
			handleWithholdAuthorityDto.setQuota(Constants.WITHHOLD_AUTHORITY_QUOTA);
			handleWithholdAuthorityDto.setDayQuota(Constants.WITHHOLD_AUTHORITY_DAYQUOTA);
		} else if (handleType == TypeEnum.RELIEVE_WITHHOLD_AUTHORITY.getType()) { // 解除
			apiName = Constants.RELIEVE_WITHHOLD_AUTHORITY;
		}
		
		try {
			result = memberClient.handleWithholdAuthority(handleWithholdAuthorityDto, apiName);
		} catch (MemberGatewayInvokeFailureException e) {
			logger.error("[委托扣款操作]发生异常，memberId=" + memberId + "，apiName=" + apiName, e);
			throw e;
		}
		return result;
	}
	
	/**
	 * 查看用户是否委托扣款
	 * @param memberId
	 * @return
	 * @author luwenshan
	 * @time: 2016年8月9日 下午15:38:11
	 * @throws Exception
	 */
	public ResultDto<HandleWithholdAuthorityDto> queryWithholdAuthority(Long memberId) throws Exception {
		ResultDto<HandleWithholdAuthorityDto> result = null;
		HandleWithholdAuthorityDto handleWithholdAuthorityDto = new HandleWithholdAuthorityDto();
		handleWithholdAuthorityDto.setIdentityType(IdType.UID);
		handleWithholdAuthorityDto.setIdentityId(SerialNumberUtil.generateIdentityId(memberId));
//		handleWithholdAuthorityDto.setAuthTypeWhitelist(AuthTypeWhitelist.ACCOUNT);
//		handleWithholdAuthorityDto.setIsDetailDisp("N");
		try {
			result = memberClient.queryWithholdAuthority(handleWithholdAuthorityDto);
		} catch (MemberGatewayInvokeFailureException e) {
			logger.error("[查看用户是否委托扣款]发生异常，memberId=" + memberId, e);
			throw e;
		}
		return result;
	}
	
	/**
	 * 托管充值 , 返回HTML页面
	 * 
	 * @param outradeNo 交易号
	 * @param memberID 会员ID
	 * @param payerIp 付款IP
	 * @param amount 金额
	 * @param returnUrl 新浪返回url
	 * @param isMobileSource 是否手机端的来源
	 * @return
	 * @author luwenshan
	 * @time: 2016年8月9日 下午15:38:11
	 * @throws PayFrontException
	 */
	public String createHostingDeposit(String outradeNo, Long memberID, String payerIp, BigDecimal amount, String returnUrl, boolean isMobileSource) throws PayFrontException {
        CreateDepositDto createDepositDto = new CreateDepositDto();
        createDepositDto.setIdentityType(IdType.UID);
        createDepositDto.setIdentityId(SerialNumberUtil.generateIdentityId(memberID));
        createDepositDto.setNotifyUrl(SinaPayConfig.depositNotifyUrl);
		createDepositDto.setPayerIp(payerIp);
        createDepositDto.setOutTradeNo(outradeNo);
        //默认充值到存钱罐
        createDepositDto.setAccountType(AccountType.SAVING_POT);
        createDepositDto.setAmount(new Money(amount));
        createDepositDto.setReturnUrl(returnUrl);
        createDepositDto.setDepositCloseTime(com.yourong.common.thirdparty.sinapay.pay.core.common.Constants.DEPOSIT_CLOSE_TIME);
        //网银支付方式
        BankPayArgs bankPayArgs = new BankPayArgs();
        bankPayArgs.setAmount(new Money(amount));
        bankPayArgs.setBankCode(RechargeBankCode.SINAPAY);
        bankPayArgs.setBankCardType(BankCardType.DEBIT);
        bankPayArgs.setBankServiceType(BankServiceType.C);
        createDepositDto.setPayMethod(bankPayArgs);
        //扩展参数:如果是手机端操作，不允许网银支付
        if (isMobileSource) {
        	Map<String, String> channelWhiteList = Maps.newHashMap();
        	channelWhiteList.put("channelBlackList", "online_bank");
        	createDepositDto.setExtendParam(channelWhiteList);
        }
        
        return this.hostingTradeFacade.createHostingDeposit(createDepositDto);
    }
	
	/**
	 * 托管提现 , 返回HTML页面
	 * 
	 * @param outradeNo 交易号
	 * @param memberID 会员ID
	 * @param userIp 会员IP
	 * @param amount 金额
	 * @return
	 * @throws PayFrontException
	 * @throws HttpException 
	 */
	public String createHostingWithdraw(String outTradeNo, Long memberId, String userIp, BigDecimal amount, String returnUrl) throws PayFrontException, HttpException {
		 CreateWithDrawDto createWithDrawDto = new CreateWithDrawDto();
		 createWithDrawDto.setIdentityId(SerialNumberUtil.generateIdentityId(memberId));
		 createWithDrawDto.setIdentityType(IdType.UID);
		 createWithDrawDto.setUserIp(userIp);
		 createWithDrawDto.setAmount(new Money(amount));
		 createWithDrawDto.setNotifyUrl(SinaPayConfig.withdrawNotifyUrl);
		 createWithDrawDto.setOutTradeNo(outTradeNo);
		 createWithDrawDto.setAccountType(AccountType.SAVING_POT);
		 createWithDrawDto.setReturnUrl(returnUrl);
		 createWithDrawDto.setWithdrawCloseTime(com.yourong.common.thirdparty.sinapay.pay.core.common.Constants.WITHDRAW_CLOSE_TIME);
		 createWithDrawDto.setWithdrawMode(com.yourong.common.thirdparty.sinapay.pay.core.common.Constants.WITHDRAW_MODE);
        return this.hostingTradeFacade.createHostingWithdraw(createWithDrawDto);
    }
	
	/**
	 * 创建托管代收交易(跳转收银台)
	 *
	 * @param tradeNo
	 * @param summary
	 * @param payerId
	 * @param payerIp
	 * @param accountType
	 * @param amount
	 * @param payerIdentityType
	 * @param tradeCode
	 * @return
	 * @throws ManagerException
	 */
	public String createHostingCollectTrade(String tradeNo, String summary, String payerId, String payerIp,
			BigDecimal amount, IdType payerIdentityType, TradeCode tradeCode, String collectTradeType, String returnUrl)
			throws ManagerException {
		ResultDto<CreateCollectTradeResult> result = null;
		CreateCollectTradeDto createCollectTradeDto = new CreateCollectTradeDto();
		try {
			createCollectTradeDto.setInputCharset(CharsetType.UTF8);
			createCollectTradeDto.setNotifyUrl(SinaPayConfig.tradeNotifyUrl);
			createCollectTradeDto.setOutTradeCode(tradeCode);
			createCollectTradeDto.setOutTradeNo(tradeNo);
			createCollectTradeDto.setPayerId(payerId);
			createCollectTradeDto.setPayerIdentityType(payerIdentityType);
			createCollectTradeDto.setPayerIp(payerIp);
			BankPayArgs bankPayArgs = new BankPayArgs();
			bankPayArgs.setAmount(new Money(amount));
			bankPayArgs.setBankCardType(BankCardType.DEBIT);
			bankPayArgs.setBankCode(RechargeBankCode.SINAPAY);
			bankPayArgs.setBankServiceType(BankServiceType.C);
			createCollectTradeDto.setPayMethod(bankPayArgs);
			createCollectTradeDto.setSummary(summary);
			createCollectTradeDto.setTradeCloseTime(Config.sinaPayDefaultOrderCloseTime);
			if (StringUtil.isNotBlank(collectTradeType)) {
				createCollectTradeDto.setCollectTradeType(collectTradeType);
			}
			createCollectTradeDto.setCanRepayOnFailed("Y");
			createCollectTradeDto.setReturnUrl(returnUrl);
			Map<String, String> channelWhiteList = Maps.newHashMap();
			channelWhiteList.put("channelWhiteList", "balance_svp");
			createCollectTradeDto.setExtendParam(channelWhiteList);
			return hostingTradeFacade.collectTradeToCashDesk(createCollectTradeDto);
		} catch (HttpException | PayFrontException e) {
			logger.error("[创建托管代收交易(跳转收银台)]发生异常，createCollectTradeDto=" + createCollectTradeDto, e);
			throw new ManagerException("[创建托管代收交易(跳转收银台)]发生异常", e);
		}
	}
	
	public String payHostingTrade(Long payerId, List<String> tradeNos, BigDecimal amount, String payerIp) throws ManagerException {
		ResultDto<PayTradeDto> result = null;
		PayTradeDto payTradeDto = new PayTradeDto();
		try {
			payTradeDto.setInputCharset(CharsetType.UTF8);
			payTradeDto.setOutPayNo(SerialNumberUtil.generatePayHostingCollectTradeNo(payerId));
			payTradeDto.setOuterTradeNoList(tradeNos);
			BankPayArgs bankPayArgs = new BankPayArgs();
			bankPayArgs.setAmount(new Money(amount));
			bankPayArgs.setBankCardType(BankCardType.DEBIT);
			bankPayArgs.setBankCode(RechargeBankCode.SINAPAY);
			bankPayArgs.setBankServiceType(BankServiceType.C);
			payTradeDto.setPayMethod(bankPayArgs);
			payTradeDto.setPayerIp(payerIp);
			return hostingTradeFacade.payTradeByBank(payTradeDto);
		} catch (PayFrontException e) {
			logger.error("[托管交易支付)]发生异常，payTradeDto=" + payTradeDto, e);
			throw new ManagerException("[托管交易支付(跳转收银台)]发生异常", e);
		}
	}
	
	public ResultDto<Object> showMemberInfosSina(Long memberId,String returnUrl) {
			ResultDto<Object> rDto = new ResultDto<Object>();
			rDto = memberClient.showMemberInfosSina(memberId,returnUrl);
			return rDto;
	 }
	
	

}
