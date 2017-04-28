package com.yourong.common.thirdparty.sinapay;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.yourong.common.constant.Config;
import com.yourong.common.enums.SinaPayEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.pay.sina.SinaPayConfig;
import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.BankCardType;
import com.yourong.common.thirdparty.sinapay.common.enums.BankCode;
import com.yourong.common.thirdparty.sinapay.common.enums.BankServiceType;
import com.yourong.common.thirdparty.sinapay.common.enums.BatchTradeNotifyMode;
import com.yourong.common.thirdparty.sinapay.common.enums.CharsetType;
import com.yourong.common.thirdparty.sinapay.common.enums.CredentialsType;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.MemberType;
import com.yourong.common.thirdparty.sinapay.common.enums.RechargeBankCode;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeCode;
import com.yourong.common.thirdparty.sinapay.common.enums.VerifyMode;
import com.yourong.common.thirdparty.sinapay.common.enums.VerifyStatus;
import com.yourong.common.thirdparty.sinapay.common.enums.VerifyType;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.BalanceFreezeDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.BalanceUnFreezeDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.BindingBankCardAdvanceDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.BindingBankCardDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.BindingVerifyDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.CreateActiveMemberDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.PersonRealnameDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.QueryAccountDetailsDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.QueryBalanceDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.QueryBankCardDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.QueryCtrlResultDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.QueryVerifyDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.UnBindingBankCardDto;
import com.yourong.common.thirdparty.sinapay.member.domain.result.AccountDetail;
import com.yourong.common.thirdparty.sinapay.member.domain.result.BankCardEntry;
import com.yourong.common.thirdparty.sinapay.member.domain.result.CardBindingResult;
import com.yourong.common.thirdparty.sinapay.member.exception.MemberGatewayInvokeFailureException;
import com.yourong.common.thirdparty.sinapay.member.service.MemberClient;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.CancelAuthTradeDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.CreateBatchPayTradeDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.CreateCollectTradeDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.CreateDepositDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.CreateSinglePayTradeDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.CreateWithDrawDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.FinishAuthTradeDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.QueryDepositDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.QueryRefundDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.QueryTradeDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.QueryWithDrawDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.RefundTradeDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.BalancePayArgs;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.BankPayArgs;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.BindingPayArgs;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.CancelAuthTradeArgs;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.FinishAuthTradeArgs;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.TradeArgs;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.CreateCollectTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.CreateSinglePayTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.PayResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryDepositResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryRefundResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryWithDrawResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.RefundTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.exceptions.PayFrontException;
import com.yourong.common.thirdparty.sinapay.pay.service.facade.HostingTradeFacade;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.common.web.BaseService;

/**
 * 新浪支付客服端类，深度封装
 *
 * @author Leon Ray
 *         2014年9月22日-下午4:05:44
 */
@Service
public class SinaPayClient extends BaseService {
    @Autowired
    private MemberClient memberClient;
    @Autowired
    private HostingTradeFacade hostingTradeFacade;

    public  String getRsaPublicSigin(){
        return  memberClient.getRsaPublicSiginKey();
    }
    public String getRsaPrivateSigin(){
        return memberClient.getRsaPrivateSiginKey();
    }
    public String getRsaEncryPublic(){
        return memberClient.getEncrytPublicKey();
    }

    /**
     * 创建激活会员
     *
     * @param memberId   会员id
     * @param memberType 会员类型（个人、企业）
     * @param clientIp 会员请求IP
     * @return
     */
    public ResultDto<?> createActivateMember(Long memberId, MemberType memberType, String clientIp) throws MemberGatewayInvokeFailureException {
        ResultDto<?> result = null;
        CreateActiveMemberDto activeMemberDto = new CreateActiveMemberDto();
        try {
            activeMemberDto.setCharsetType(CharsetType.UTF8);
            activeMemberDto.setIdentityId(SerialNumberUtil.generateIdentityId(memberId));
            activeMemberDto.setIdType(IdType.UID);
            activeMemberDto.setMemberType(memberType);
            activeMemberDto.setClientIp(clientIp);
            result = memberClient.createActiveMember(activeMemberDto);
        } catch (MemberGatewayInvokeFailureException e) {
            logger.error("[创建激活会员]发生异常，memberId=" + memberId, e);
            throw e;
        }
        return result;
    }

    /**
     * 设置实名信息
     *
     * @param memberId     会员id
     * @param
     * @param verifyStatus 是否需要钱包做实名认证，值为Y/N，默认Y
     * @param certNo       证件号码
     * @param realName     真实姓名
     * @param clientIp     请求IP
     * @return
     */
    public ResultDto<?> setRealName(Long memberId, VerifyStatus verifyStatus, String certNo, String realName, String clientIp) throws MemberGatewayInvokeFailureException {
        ResultDto<?> result = null;
        PersonRealnameDto personRealnameDto = new PersonRealnameDto();
        try {
            personRealnameDto.setCertNo(certNo);
            personRealnameDto.setCertType(CredentialsType.IC);
            personRealnameDto.setCharsetType(CharsetType.UTF8);
            personRealnameDto.setIdentityId(SerialNumberUtil.generateIdentityId(memberId));
            personRealnameDto.setIdentityType(IdType.UID);
            personRealnameDto.setNeedConfirm(VerifyStatus.Y.equals(verifyStatus) ? true : false);
            personRealnameDto.setRealName(realName);
            personRealnameDto.setClientIp(clientIp);
            result = memberClient.setRealname(personRealnameDto);
        } catch (MemberGatewayInvokeFailureException e) {
            logger.error("[设置实名信息]发生异常，personRealnameDto=" + personRealnameDto, e);
            throw e;
        }
        return result;
    }

    /**
     * 绑定认证信息
     *
     * @param memberId     会员id
     * @param verifyEntity 认证内容
     * @param verifyType   认证类型
     * @return
     * @throws MemberGatewayInvokeFailureException
     */
    public ResultDto<?> bindingVerify(Long memberId, String verifyEntity, VerifyType verifyType, String clientIp) throws MemberGatewayInvokeFailureException {
        ResultDto<?> result = null;
        BindingVerifyDto bindingVerifyDto = new BindingVerifyDto();
        try {
            bindingVerifyDto.setCharsetType(CharsetType.UTF8);
            bindingVerifyDto.setIdentityId(SerialNumberUtil.generateIdentityId(memberId));
            bindingVerifyDto.setIdentityType(IdType.UID);
            bindingVerifyDto.setVerifyEntity(verifyEntity);
            bindingVerifyDto.setVerifyType(verifyType);
            // 请求者IP
            bindingVerifyDto.setClientIp(clientIp);
            result = memberClient.bindingVerify(bindingVerifyDto);
        } catch (MemberGatewayInvokeFailureException e) {
            result = new ResultDto<>();
            result.setErrorCode("绑定认证信息]发生异常");
            logger.error("[绑定认证信息]发生异常，bindingVerifyDto=" + bindingVerifyDto, e);
            throw e;
        }
        return result;
    }

    /**
     * 查询信息
     * @param memberId
     * @param verifyType
     * @return
     * @throws MemberGatewayInvokeFailureException
     */
    public ResultDto<?> queryVerify(Long memberId, VerifyType verifyType) throws MemberGatewayInvokeFailureException {
        ResultDto<?> result = null;
        QueryVerifyDto bindingVerifyDto = new QueryVerifyDto();
        try {
            bindingVerifyDto.setCharsetType(CharsetType.UTF8);
            bindingVerifyDto.setIdentityId(SerialNumberUtil.generateIdentityId(memberId));
            bindingVerifyDto.setIdentityType(IdType.UID);
            bindingVerifyDto.setIsMask("N");
            bindingVerifyDto.setVerifyType(verifyType);
            result = memberClient.queryVerify(bindingVerifyDto);
        } catch (MemberGatewayInvokeFailureException e) {
            result = new ResultDto<>();
            result.setErrorCode("绑定认证信息]发生异常");
            logger.error("[绑定认证信息]发生异常，bindingVerifyDto=" + bindingVerifyDto, e);
            throw e;
        }
        return result;
    }


    /**
     * 查询绑定银行卡
     *
     * @return
     */
    public ResultDto<?> queryBankCard(Long memberId, String cardId) throws Exception {
        QueryBankCardDto queryBankCardDto = new QueryBankCardDto();
        queryBankCardDto.setIdentityType(IdType.UID);
        queryBankCardDto.setIdentityId(SerialNumberUtil.generateIdentityId(memberId));
        queryBankCardDto.setCardId(cardId);
        ResultDto<List<BankCardEntry>> listResultDto = memberClient.queryBankCards(queryBankCardDto);
        return listResultDto;
    }

    /**
     * 查询余额
     *
     * @param memberid
     * @return
     */
    public ResultDto<?> queryBalance(Long memberid, AccountType accountType) throws Exception {
        QueryBalanceDto queryBalanceDto = new QueryBalanceDto();
        queryBalanceDto.setIdentityType(IdType.UID);
        queryBalanceDto.setIdentityId(SerialNumberUtil.generateIdentityId(memberid));
        //queryBalanceDto.setAccountType(accountType);
        return memberClient.queryBalance(queryBalanceDto);
    }

    /**
     * 查询余额
     *
     * @param memberid
     * @return
     */
    public ResultDto<?> queryBalance(Long memberid) throws Exception {
		QueryBalanceDto queryBalanceDto = new QueryBalanceDto();
		// 第三方支付不开放查询企业基本户的 资金
		if (StringUtil.equalsIgnoreCase(String.valueOf(memberid),
				Config.internalMemberId)) {
			String identityId = SinaPayConfig.partnerId;
			queryBalanceDto.setIdentityId(identityId);
			queryBalanceDto.setAccountType(AccountType.BASIC);
			queryBalanceDto.setIdentityType(IdType.MEMBER_ID);
		} else {
			queryBalanceDto.setIdentityType(IdType.UID);
			queryBalanceDto.setIdentityId(SerialNumberUtil
					.generateIdentityId(memberid));
			queryBalanceDto.setAccountType(AccountType.SAVING_POT);
		}
        return memberClient.queryBalance(queryBalanceDto);
    }

    /**
     * 绑定银行卡
     *
     * @return
     */
    public ResultDto<?> bindingBankCard(Long memberid, String bank_code,
                                        String bank_account_no, String account_name, String phoneNo,boolean isVerify, String userIp) throws Exception {
        BindingBankCardDto dto = new BindingBankCardDto();
        dto.setIdentityType(IdType.UID);
        dto.setIdentityId(SerialNumberUtil.generateIdentityId(memberid));
        dto.setRequestNo(SerialNumberUtil.generatePayBingCar(memberid));
        dto.setBankAccountNo(bank_account_no);
        dto.setAccountName(account_name);
        dto.setBankCode(BankCode.valueOf(bank_code));
        dto.setCardType(BankCardType.DEBIT);
        dto.setCardAttr(BankServiceType.C);
        if (isVerify){
            dto.setVerifyMode(VerifyMode.SIGN);
        }
        dto.setPhoneNo(phoneNo);
        dto.setUserBindIp(userIp);
        ResultDto<CardBindingResult> resultResultDto = memberClient.bindBankCard(dto);
        return resultResultDto;
    }

    /**
     * 绑定银行卡推进， 收到短信，验证码
     *
     * @return
     */
    public ResultDto<?> bindingBankCardAdvance(String ticket, String validCode, String clientIp) throws Exception {
        BindingBankCardAdvanceDto dto = new BindingBankCardAdvanceDto();
        dto.setTicket(ticket);
        dto.setValidCode(validCode);
        dto.setClientIp(clientIp);
        return memberClient.bindingBankCardAdvance(dto);
    }

    /**
     * 解绑银行卡
     *
     * @return
     * @throws Exception
     */
    public ResultDto<?> unBindingBankCard(Long memberID, String carID, String userIp) throws Exception {
        UnBindingBankCardDto dto = new UnBindingBankCardDto();
        dto.setIndentityType(IdType.UID);
        dto.setCardId(carID);
        dto.setIndentityId(SerialNumberUtil.generateIdentityId(memberID));
        dto.setUserUnBindIp(userIp);
        return memberClient.unbindBankCard(dto);
    }

    /**
     * 托管充值 , 返回HTML 页面
     *
     * @param
     * @return
     * @throws PayFrontException
     */
	public String createDepositByBank(String outradeNo, Long memberID, String payerIp, BigDecimal amount, RechargeBankCode code)
			throws PayFrontException {
        CreateDepositDto createDepositDto = new CreateDepositDto();
        createDepositDto.setNotifyUrl(SinaPayConfig.depositNotifyUrl);
        createDepositDto.setIdentityType(IdType.UID);
        createDepositDto.setIdentityId(SerialNumberUtil.generateIdentityId(memberID));
		createDepositDto.setPayerIp(payerIp);
        createDepositDto.setOutTradeNo(outradeNo);
        //默认充值到存钱罐
        createDepositDto.setAccountType(AccountType.SAVING_POT);
        createDepositDto.setAmount(new Money(amount));


        createDepositDto.setReturnUrl(SinaPayConfig.returnUrl);


//        //支持快捷支付
//        QuickPayArgs payArgs = new QuickPayArgs();
//        payArgs.setAmount(new Money(amount));
//        payArgs.setBankCardType(BankCardType.DEBIT);
//        // 测试银行
//        payArgs.setBankCode(code);
//        payArgs.setBankServiceType(BankServiceType.C);
//        payArgs.setCredentialsType(CredentialsType.IC);


        //网银支付
        BankPayArgs bankPayArgs = new BankPayArgs();
        bankPayArgs.setAmount(new Money(amount));
        bankPayArgs.setBankCardType(BankCardType.DEBIT);
        bankPayArgs.setBankCode(code);
        bankPayArgs.setBankServiceType(BankServiceType.C);
        createDepositDto.setPayMethod(bankPayArgs);
        return this.hostingTradeFacade.createDepositByBank(createDepositDto);

    }

    /***
	 * 绑卡充值
	 * 
	 * @param outradeNo
	 * @param memberID
	 * @param payerIp
	 * @param amount
	 * @param carID
	 * @return
	 * @throws Exception
	 */
	public ResultDto<PayResult> createDeposit(String outradeNo, Long memberID, String payerIp, BigDecimal amount, String carID)
			throws Exception {
        CreateDepositDto createDepositDto = new CreateDepositDto();
        createDepositDto.setNotifyUrl(SinaPayConfig.depositNotifyUrl);
        createDepositDto.setIdentityType(IdType.UID);
        createDepositDto.setIdentityId(SerialNumberUtil.generateIdentityId(memberID));
        createDepositDto.setOutTradeNo(outradeNo);
        //默认充值到存钱罐
        createDepositDto.setAccountType(AccountType.SAVING_POT);
        createDepositDto.setAmount(new Money(amount));
        createDepositDto.setPayerIp(payerIp);
       BindingPayArgs bindingPayArgs = new BindingPayArgs();
        bindingPayArgs.setCardId(carID);
        bindingPayArgs.setAmount(new Money(amount));
        createDepositDto.setPayMethod(bindingPayArgs);
        return  this.hostingTradeFacade.createDeposit(createDepositDto);
    }

    /**
     * 解绑认证信息
     *
     * @param memberId   会员id
     * @param verifyType 认证类型
     * @return
     */
    public ResultDto<?> unbindingVerify(Long memberId, VerifyType verifyType, String clientIp) throws MemberGatewayInvokeFailureException {
        ResultDto<?> result = null;
        BindingVerifyDto bindingVerifyDto = new BindingVerifyDto();
        try {
            bindingVerifyDto.setCharsetType(CharsetType.UTF8);
            bindingVerifyDto.setIdentityId(SerialNumberUtil.generateIdentityId(memberId));
            bindingVerifyDto.setIdentityType(IdType.UID);
            bindingVerifyDto.setVerifyType(verifyType);
            // 会员请求IP
            bindingVerifyDto.setClientIp(clientIp);
            result = memberClient.unbindingVerify(bindingVerifyDto);
        } catch (MemberGatewayInvokeFailureException e) {
            logger.error("[解绑认证信息]发生异常，bindingVerifyDto=" + bindingVerifyDto, e);
            throw e;
        }
        return result;
    }

    /**
     * 提现
     *
     * @return
     * @throws Exception
     */
    public ResultDto<?> createWithDraw(Long memberId, String cardId, BigDecimal amount, String outTradeNo, String userIp) throws Exception {
        CreateWithDrawDto createWithDrawDto = new CreateWithDrawDto();
        createWithDrawDto.setIdentityId(SerialNumberUtil.generateIdentityId(memberId));
        createWithDrawDto.setIdentityType(IdType.UID);
        createWithDrawDto.setCardId(cardId);
        createWithDrawDto.setAmount(new Money(amount));
        createWithDrawDto.setNotifyUrl(SinaPayConfig.withdrawNotifyUrl);
        createWithDrawDto.setOutTradeNo(outTradeNo);
        createWithDrawDto.setAccountType(AccountType.SAVING_POT);
        createWithDrawDto.setUserIp(userIp);
        ResultDto<PayResult> resultDto = hostingTradeFacade.createWithDraw(createWithDrawDto);
        return resultDto;
    }

    /**
     * 支付推进
     * @param outAdvanceNo 外部订单号
     * @param ticket
     * @param validateCode 手机验证码
     * @param userIp 用户IP
     * @return
     */
    public ResultDto<?> advanceHostingPay(String outAdvanceNo, String ticket, String validateCode, String userIp) throws Exception{
        ResultDto resultDto = new ResultDto();
        try {
            resultDto = hostingTradeFacade.advanceHostingPay(outAdvanceNo,ticket,validateCode, userIp);
        }catch (Exception e ){
            logger.error("支付推进发生异常，outAdvanceNo=" + outAdvanceNo, e);
            throw e;
        }
        return resultDto;
    }


    /**
     * 查询代收和代付交易
     *
     * @param identityId
     * @param identityType
     * @param tradeNo
     * @param pageNo
     * @param pageSize
     * @param startTime
     * @param endTime
     * @return
     */
    public ResultDto<QueryTradeResult> queryTrade(
            String identityId,
            IdType identityType,
            String tradeNo,
            int pageNo,
            int pageSize,
            Date startTime,
            Date endTime
    ) throws Exception {
        ResultDto<QueryTradeResult> result = null;
        QueryTradeDto queryTradeDto = new QueryTradeDto();
        try {
            if (startTime != null) {
                queryTradeDto.setStartTime(DateUtils.getStrFromDate(startTime, DateUtils.TIME_PATTERN_SESSION));
            }
            if (endTime != null) {
                queryTradeDto.setEndTime(DateUtils.getStrFromDate(endTime, DateUtils.TIME_PATTERN_SESSION));
            }
            queryTradeDto.setIdentityId(identityId);
            queryTradeDto.setIdentityType(identityType);
            queryTradeDto.setInputCharset(CharsetType.UTF8);
            queryTradeDto.setOutTradeNo(tradeNo);
            queryTradeDto.setPageNo(pageNo);
            queryTradeDto.setPageSize(pageSize);
            result = hostingTradeFacade.queryTrade(queryTradeDto);
        } catch (HttpException | PayFrontException e) {
            logger.error("[托管交易查询]发生异常，queryTradeDto=" + queryTradeDto, e);
            throw e;
        }
        return result;
    }


    /**
     * 创建托管代收交易
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
    public ResultDto<CreateCollectTradeResult> createHostingCollectTrade(
            String tradeNo,
            String summary,
            String payerId,
            String payerIp,
            AccountType accountType,
            BigDecimal amount,
            IdType payerIdentityType,
            TradeCode tradeCode,
            String collectTradeType
    ) throws ManagerException {
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
            BalancePayArgs balancePayArgs = new BalancePayArgs();
            balancePayArgs.setAccountType(accountType);
            balancePayArgs.setAmount(new Money(amount));
            createCollectTradeDto.setPayMethod(balancePayArgs);
            createCollectTradeDto.setSummary(summary);
            createCollectTradeDto.setTradeCloseTime(Config.sinaPayDefaultOrderCloseTime);
			if (StringUtil.isNotBlank(collectTradeType)) {
				createCollectTradeDto.setCollectTradeType(collectTradeType);
			}
			// 代收冻结允许在代收完成的时候失败重新提交
			if (TypeEnum.COLLECT_TRADE_TYPE_FREEZE.getCode().equals(createCollectTradeDto.getCollectTradeType())) {
				createCollectTradeDto.setCanRepayOnFailed("Y");
			}
            result = hostingTradeFacade.collectTrade(createCollectTradeDto);
//			if(result!=null) {
//				if(!result.isSuccess()) {
//					throw new PayFrontException(result.getErrorCode());
//				}
//			}
        } catch (HttpException | PayFrontException e) {
            logger.error("[创建托管代收交易]发生异常，createCollectTradeDto=" + createCollectTradeDto, e);
            throw new ManagerException("[创建托管代收交易]发生异常", e);
        }
        return result;
    }

    /**
     * 创建单笔托管代付交易
     *
     * @param accountType
     * @param amount
     * @param tradeNo
     * @param payeeIdentityType
     * @param payeeIdentityId
     * @param summary
     * @param tradeCode
     * @return
     */
    public ResultDto<CreateSinglePayTradeResult> createSinglePayTrade(
            AccountType accountType,
            BigDecimal amount,
            String tradeNo,
            IdType payeeIdentityType,
            String payeeIdentityId,
            String summary,
            String ip,
            TradeCode tradeCode
    ) throws Exception {
        ResultDto<CreateSinglePayTradeResult> result = null;
        CreateSinglePayTradeDto createSinglePayTradeDto = new CreateSinglePayTradeDto();
        try {
            createSinglePayTradeDto.setAccountType(accountType);
            createSinglePayTradeDto.setAmount(new Money(amount));
            createSinglePayTradeDto.setInputCharset(CharsetType.UTF8);
            createSinglePayTradeDto.setNotifyUrl(SinaPayConfig.tradeNotifyUrl);
            createSinglePayTradeDto.setOutTradeCode(tradeCode);
            createSinglePayTradeDto.setOutTradeNo(tradeNo);
            createSinglePayTradeDto.setPayeeIdentityId(payeeIdentityId);
            createSinglePayTradeDto.setSummary(summary);
            createSinglePayTradeDto.setUserIp(ip);
            createSinglePayTradeDto.setPayeeIdentityType(payeeIdentityType);
            result = hostingTradeFacade.createSinglePayTrade(createSinglePayTradeDto);
//			if(result!=null) {
//				if(!result.isSuccess()) {
//					throw new PayFrontException(result.getErrorCode());
//				}
//			}
        } catch (HttpException | PayFrontException e) {
            logger.error("[创建单笔托管代付交易]发生异常，createSinglePayTradeDto=" + createSinglePayTradeDto, e);
            throw e;
        }
        return result;
    }

    /**
     * 托管提现查询
     *
     * @param identityId
     * @param identityType
     * @param tradeNo
     * @param pageNo
     * @param pageSize
     * @param startTime
     * @param endTime
     * @return
     */
    public ResultDto<QueryWithDrawResult> queryWithDraw(
            String identityId,
            IdType identityType,
            AccountType accountType,
            String tradeNo,
            int pageNo,
            int pageSize,
            Date startTime,
            Date endTime
    ) throws Exception {
        ResultDto<QueryWithDrawResult> result = null;
        QueryWithDrawDto queryWithDrawDto = new QueryWithDrawDto();
        try {
            if (startTime != null) {
                queryWithDrawDto.setStartTime(DateUtils.getStrFromDate(startTime, DateUtils.TIME_PATTERN_SESSION));
            }
            if (endTime != null) {
                queryWithDrawDto.setEndTime(DateUtils.getStrFromDate(endTime, DateUtils.TIME_PATTERN_SESSION));
            }
            queryWithDrawDto.setIdentityId(identityId);
            queryWithDrawDto.setIdentityType(identityType);
            queryWithDrawDto.setInputCharset(CharsetType.UTF8);
            queryWithDrawDto.setOutTradeNo(tradeNo);
            queryWithDrawDto.setPageNo(pageNo);
            queryWithDrawDto.setPageSize(pageSize);
            result = hostingTradeFacade.queryWithDraw(queryWithDrawDto);
        } catch (HttpException | PayFrontException e) {
            logger.error("[托管提现查询]发生异常，queryWithDrawDto=" + queryWithDrawDto, e);
            throw e;
        }
        return result;
    }


    /**
     * 创建批量代付交易
     *
     * @param batchPayNo
     * @param summary
     * @param tradeList
     * @param tradeCode
     * @param batchNotify 
     * @return
     */
    public ResultDto<?> createBatchPayTrade(
            String batchPayNo,
            String summary,
            String ip,
            List<TradeArgs> tradeList,
            TradeCode tradeCode, 
            BatchTradeNotifyMode batchNotify
    ) throws Exception {
        ResultDto<?> result = null;
        CreateBatchPayTradeDto createBatchPayTradeDto = new CreateBatchPayTradeDto();
        try {
            createBatchPayTradeDto.setInputCharset(CharsetType.UTF8);
            createBatchPayTradeDto.setNotifyMethod(batchNotify);
            if(BatchTradeNotifyMode.batch_notify.name().equals(batchNotify.name())) {
            	createBatchPayTradeDto.setNotifyUrl(SinaPayConfig.batchNotifyUrl);
            } 
            if(BatchTradeNotifyMode.single_notify.name().equals(batchNotify.name())) {
            	createBatchPayTradeDto.setNotifyUrl(SinaPayConfig.tradeNotifyUrl);
            } 
            createBatchPayTradeDto.setOutPayNo(batchPayNo);
            createBatchPayTradeDto.setOutTradeCode(tradeCode);
            createBatchPayTradeDto.setSummary(summary);
            createBatchPayTradeDto.setUserIp(ip);
            createBatchPayTradeDto.setTradeList(tradeList);
             hostingTradeFacade.createBatchPayTrade(createBatchPayTradeDto);
        } catch (HttpException | PayFrontException e) {
            logger.error("[创建批量代付交易]发生异常，batchPayNo=" + batchPayNo, e);
            throw e;
        }
        return result;
    }

    /**
     * 查询基金七日收益
     *
     * @param fincode
     * @return
     */
    public ResultDto<?> queryFundYield(String fincode) {
        try {
            return this.hostingTradeFacade.queryFundYield(fincode);
        } catch (Exception e) {
            logger.error("[查询基金七日收益]发生异常，fincode=" + fincode, e);
        }
        return null;
    }
    
    /**
     * 查询验证新浪存钱罐 返回HTML 页面
     * @return
     */
    public String auditMemberInfos(Long memberId) throws MemberGatewayInvokeFailureException{
    	String result=null;
    	try {
    		CreateActiveMemberDto activeMemberDto = new CreateActiveMemberDto();
    		activeMemberDto.setIdentityId(SerialNumberUtil.generateIdentityId(memberId));
    		activeMemberDto.setIdType(IdType.UID);
    		result= memberClient.auditMemberInfos(activeMemberDto);
		} catch (MemberGatewayInvokeFailureException e) {
			logger.error("[查询验证新浪存钱罐]发生异常，memberId=" + memberId, e);
			throw e;
		}
		return result;
    }

    /**
     * 查询收支明显
     * @param memberId
     * @return
     * @throws MemberGatewayInvokeFailureException
     */
    public  ResultDto<AccountDetail>  queryAccountDetails(Long memberId,Date start,Date end)throws MemberGatewayInvokeFailureException{
        QueryAccountDetailsDto dto = new QueryAccountDetailsDto();
        dto.setIdentityType(IdType.UID);
        dto.setIdentityId(SerialNumberUtil.generateIdentityId(memberId));
        dto.setAccountType(AccountType.SAVING_POT);
        dto.setStartTime(start);
        dto.setEndTime(end);
        Map map = Maps.newHashMap();
        map.put("svpTradeType","bonus");
        dto.setExtendParam(map);
        ResultDto<AccountDetail> resultDto = memberClient.queryAccountDetails(dto);
        return  resultDto;
    }

    /**
     * 查询收支明细(包括收益、收入和支出)
     * @param memberId
     * @param svpTradeType
     * @param start
     * @param end
     * @param pageNo
     * @param pageSize
     * @return
     * @throws MemberGatewayInvokeFailureException
     */
    public  ResultDto<AccountDetail>  queryAccountDetailsAll(Long memberId, String svpTradeType, Date start,Date end, String pageNo, String pageSize)throws MemberGatewayInvokeFailureException{
        QueryAccountDetailsDto dto = new QueryAccountDetailsDto();
        dto.setIdentityType(IdType.UID);
        dto.setIdentityId(SerialNumberUtil.generateIdentityId(memberId));
        dto.setAccountType(AccountType.SAVING_POT);
        dto.setStartTime(start);
        dto.setEndTime(end);
        dto.setPageNo(pageNo);
        dto.setPageSize(pageSize);
        Map<String, String> map = Maps.newHashMap();
        if(StringUtil.isNotBlank(svpTradeType)) {        	
        	map.put("svpTradeType",svpTradeType);
        }
        dto.setExtendParam(map);
        ResultDto<AccountDetail> resultDto = memberClient.queryAccountDetails(dto);
        return  resultDto;
    }


    /**
     * 托管交易批次查询
     *
     * @param outBatchNo 批次号
     * @return
     */
    public ResultDto<QueryTradeResult> queryHostingBatchTrade(String outBatchNo) {
        try {
            return this.hostingTradeFacade.queryHostingBatchTrade(outBatchNo);
        } catch (Exception e) {
            logger.error("托管交易批次查询[发生异常]，outBatchNo=" + outBatchNo, e);
        }
        return null;
    }


    /**
     * 托管充值查询
     *
     * @param identityId
     * @param identityType
     * @param tradeNo
     * @param pageNo
     * @param pageSize
     * @param startTime
     * @param endTime
     * @return
     */
    public ResultDto<QueryDepositResult> queryDeposit(
            String identityId,
            IdType identityType,
            AccountType accountType,
            String tradeNo,
            int pageNo,
            int pageSize,
            Date startTime,
            Date endTime
    ) throws Exception {
        ResultDto<QueryDepositResult> result = null;
        QueryDepositDto queryDepositDto = new QueryDepositDto();
        try {
            if (startTime != null) {
            	queryDepositDto.setStartTime(DateUtils.getStrFromDate(startTime, DateUtils.TIME_PATTERN_SESSION));
            }
            if (endTime != null) {
            	queryDepositDto.setEndTime(DateUtils.getStrFromDate(endTime, DateUtils.TIME_PATTERN_SESSION));
            }
            queryDepositDto.setIdentityId(identityId);
            queryDepositDto.setIdentityType(identityType);
            queryDepositDto.setInputCharset(CharsetType.UTF8);
            queryDepositDto.setOutTradeNo(tradeNo);
            queryDepositDto.setPageNo(pageNo);
            queryDepositDto.setPageSize(pageSize);
            result = hostingTradeFacade.queryDeposit(queryDepositDto);
        } catch (HttpException | PayFrontException e) {
            logger.error("[托管充值查询]发生异常，queryDepositDto=" + queryDepositDto, e);
            throw e;
        }
        return result;
    }

    /**
     * 
     * @Description:创建退款交易
     * @param outTradeNo
     * @param origOuterTradeNo
     * @param refundAmount
     * @param summary
     * @return
     * @throws ManagerException
     * @author: fuyili
     * @time:2016年2月16日 下午5:47:50
     */
    public ResultDto<RefundTradeResult> createHostingRefund(
            String outTradeNo,
            String origOuterTradeNo,
            BigDecimal refundAmount,
            String summary,
            String ip
    ) throws ManagerException {
        ResultDto<RefundTradeResult> result = null;
        RefundTradeDto refundTradeDto = new RefundTradeDto();
        try {
        	refundTradeDto.setInputCharset(CharsetType.UTF8);
        	refundTradeDto.setNotifyUrl(SinaPayConfig.refundNotifyUrl);//退款的交易回调地址
        	refundTradeDto.setOutTradeNo(outTradeNo);
        	refundTradeDto.setOrigOuterTradeNo(origOuterTradeNo);
        	refundTradeDto.setRefundAmount(new Money(refundAmount));
        	refundTradeDto.setSummary(summary);
        	refundTradeDto.setUserIp(ip);
            result = hostingTradeFacade.refundTrade(refundTradeDto);
        } catch (HttpException | PayFrontException e) {
            logger.error("[创建退款交易]发生异常，refundTradeDto=" + refundTradeDto, e);
            throw new ManagerException("[创建退款交易]发生异常", e);
        }
        return result;
    }
    
    
    /**
     * 查询退款交易
     *
     * @param identityId
     * @param identityType
     * @param tradeNo
     * @param pageNo
     * @param pageSize
     * @param startTime
     * @param endTime
     * @return
     */
    public ResultDto<QueryRefundResult> queryRefund(
            String identityId,
            IdType identityType,
            String tradeNo,
            int pageNo,
            int pageSize,
            Date startTime,
            Date endTime
    ) throws Exception {
        ResultDto<QueryRefundResult> result = null;
        QueryRefundDto queryRefundDto= new QueryRefundDto();
        try {
            if (startTime != null) {
            	queryRefundDto.setStartTime(DateUtils.getStrFromDate(startTime, DateUtils.TIME_PATTERN_SESSION));
            }
            if (endTime != null) {
            	queryRefundDto.setEndTime(DateUtils.getStrFromDate(endTime, DateUtils.TIME_PATTERN_SESSION));
            }
            queryRefundDto.setIdentityId(identityId);
            queryRefundDto.setIdentityType(identityType);
            queryRefundDto.setInputCharset(CharsetType.UTF8);
            queryRefundDto.setOutTradeNo(tradeNo);
            queryRefundDto.setPageNo(pageNo);
            queryRefundDto.setPageSize(pageSize);
            result = hostingTradeFacade.queryRefund(queryRefundDto);
        } catch (HttpException | PayFrontException e) {
            logger.error("[托管退款查询]发生异常，queryTradeDto=" + queryRefundDto, e);
            throw e;
        }
        return result;
    }

	/**
	 * 
	 * @Description:代收完成
	 * @param outRequestNo
	 * @param tradeList
	 * @return
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2016年5月23日 下午11:26:32
	 */
	public ResultDto<?> finishPreAuthTrade(String outRequestNo, List<FinishAuthTradeArgs> tradeList,String ip) throws Exception {
		ResultDto<?> resultDto = new ResultDto<Object>();
		FinishAuthTradeDto preAuthTradeDto = new FinishAuthTradeDto();
		try {
			preAuthTradeDto.setOutRequestNo(outRequestNo);
			preAuthTradeDto.setTradeList(tradeList);
			preAuthTradeDto.setUserIp(ip);
			resultDto = hostingTradeFacade.finishPreAuthTrade(preAuthTradeDto);
		} catch (HttpException | PayFrontException e) {
			logger.error("[代收完成]发生异常，outRequestNo={}", outRequestNo, e);
			throw e;
		}
		return resultDto;
	}

	/**
	 * 
	 * @Description:代收撤销
	 * @param outRequestNo
	 * @param tradeList
	 * @return
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2016年5月23日 下午11:26:32
	 */
	public ResultDto<?> cancelPreAuthTrade(String outRequestNo, List<CancelAuthTradeArgs> tradeList) throws Exception {
		ResultDto<?> resultDto = new ResultDto<Object>();
		CancelAuthTradeDto preAuthTradeDto = new CancelAuthTradeDto();
		try {
			preAuthTradeDto.setOutRequestNo(outRequestNo);
			preAuthTradeDto.setTradeList(tradeList);
			resultDto = hostingTradeFacade.cancelPreAuthTrade(preAuthTradeDto);
		} catch (HttpException | PayFrontException e) {
			logger.error("[代收撤销]发生异常，outRequestNo={}", outRequestNo, e);
			throw e;
		}
		return resultDto;
	}
	
	/**
	 * 
	 * @Description:冻结余额
	 * @param memberId
	 * @param outFreezeNo
	 * @param amount
	 * @param summary
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年7月27日 下午5:05:01
	 */
	 public ResultDto<?> freezeBalance(Long memberId, String outFreezeNo, BigDecimal amount,String summary,String ip) throws Exception {
	        ResultDto<?> result = new ResultDto<Object>();
	        BalanceFreezeDto balanceFreezeDto = new BalanceFreezeDto();
	        try {
	        	balanceFreezeDto.setCharsetType(CharsetType.UTF8);
	        	balanceFreezeDto.setIdentityId(SerialNumberUtil.generateIdentityId(memberId));
	        	balanceFreezeDto.setOutFreezeNo(outFreezeNo);
	        	balanceFreezeDto.setIdentityType(IdType.UID);
	        	balanceFreezeDto.setAccountType(AccountType.SAVING_POT);
	        	balanceFreezeDto.setAmount((new Money(amount)));
	        	balanceFreezeDto.setClientIp(ip);
	        	balanceFreezeDto.setSummary(summary);
	            result = memberClient.freezeBalance(balanceFreezeDto);
	        } catch (MemberGatewayInvokeFailureException e) {
	            logger.error("[冻结余额]发生异常，balanceFreezeDto=" + balanceFreezeDto, e);
	            throw e;
	        }
	        return result;
	    }
	 /**
	  * 
	  * @Description:解冻余额
	  * @param memberId
	  * @param outFreezeNo
	  * @param outUnFreezeNo
	  * @param amount
	  * @param summary
	  * @return
	  * @throws Exception
	  * @author: chaisen
	  * @time:2016年7月27日 下午5:11:34
	  */
	 public ResultDto<?> unfreezeBalance(Long memberId, String outFreezeNo,String outUnFreezeNo, BigDecimal amount,String summary,String ip) throws Exception {
	        ResultDto<?> result = new ResultDto<Object>();
	        BalanceUnFreezeDto balanceUnFreezeDto = new BalanceUnFreezeDto();
	        try {
	        	balanceUnFreezeDto.setCharsetType(CharsetType.UTF8);
	        	balanceUnFreezeDto.setIdentityId(SerialNumberUtil.generateIdentityId(memberId));
	        	balanceUnFreezeDto.setOutFreezeNo(outFreezeNo);
	        	balanceUnFreezeDto.setOutUnFreezeNo(outUnFreezeNo);
	        	balanceUnFreezeDto.setIdentityType(IdType.UID);
	        	balanceUnFreezeDto.setClientIp(ip);
	        	balanceUnFreezeDto.setAmount((new Money(amount)));
	        	balanceUnFreezeDto.setSummary(summary);
	            result = memberClient.unfreezeBalance(balanceUnFreezeDto);
	        } catch (MemberGatewayInvokeFailureException e) {
	            logger.error("[解冻余额]发生异常，balanceUnFreezeDto=" + balanceUnFreezeDto, e);
	            throw e;
	        }
	        return result;
	    }
	 
	 /**
	  * 
	  * @Description:查询冻结和解冻结果
	  * @param outCtrlNo
	  * @return
	  * @throws Exception
	  * @author: chaisen
	  * @time:2016年7月28日 上午9:44:38
	  */
	 public ResultDto<QueryCtrlResultDto> queryCtrlResult(String outCtrlNo) throws Exception {
			ResultDto<QueryCtrlResultDto> resultDto = new ResultDto<QueryCtrlResultDto>();
			QueryCtrlResultDto queryCtrlResultDto = new QueryCtrlResultDto();
			try {
				queryCtrlResultDto.setOutCtrlNo(outCtrlNo);
				resultDto = memberClient.queryCtrlResult(queryCtrlResultDto);
			} catch (MemberGatewayInvokeFailureException e) {
				logger.error("查询冻结和解冻结果发生异常，outCtrlNo={}", outCtrlNo, e);
				throw e;
			}
			return resultDto;
		}
}
