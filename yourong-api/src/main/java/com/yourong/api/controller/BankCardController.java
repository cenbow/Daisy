package com.yourong.api.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.yourong.api.dto.BankCodeDto;
import com.yourong.api.dto.BindSecurityBankCardDto;
import com.yourong.api.dto.MemberBankCardDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.dto.SimpleMemberBankCardDto;
import com.yourong.api.service.BalanceService;
import com.yourong.api.service.MemberBankCardService;
import com.yourong.api.service.MemberService;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.thirdparty.sinapay.common.enums.BankCode;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberBankCard;

/**
 *  银行卡管理
 * Created by py on 2015/3/23.
 */
@Controller
@RequestMapping("security/bankCard")
public class BankCardController  extends  BaseController{
    @Autowired
    private MemberBankCardService memberBankCardService;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private MemberService memberService;

    /***
     *  银行卡
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "queryBanks",method = RequestMethod.POST,headers = {"Accept-Version=1.0.0"})
    @ResponseBody
    public ResultDTO<Object> queryBanks( HttpServletRequest req, HttpServletResponse resp) {
        ResultDTO<Object>  resultDTO = new ResultDTO<>();
        BankCode[] values = BankCode.values();
        List list = Lists.newArrayList();
        for (BankCode code:values){
            BankCodeDto dto = new BankCodeDto();
            dto.setRemarks(code.getRemarks());
            dto.setCode(code.toString());
            dto.setType(code.getType());
            list.add(dto);
        }
        resultDTO.setResult(list);
        return resultDTO;
    }

    /***
     *  银行卡
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "queryBankCardList",method = RequestMethod.POST,headers = {"Accept-Version=1.0.0"})
    @ResponseBody
    public ResultDTO<Object> bankCardsIndex( HttpServletRequest req, HttpServletResponse resp) {
        ResultDTO<Object>  resultDTO = new ResultDTO<>();
        Long id = getMemberID(req);
        List<MemberBankCard> list = memberBankCardService .getMemberBankCardByMemberId(id);
        List<MemberBankCardDto> bankCardDtos = BeanCopyUtil.mapList(list, MemberBankCardDto.class);
        resultDTO.setResult(bankCardDtos);
        return resultDTO;
    }

    /***
     *  查询该用户可以支付的卡
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "queryCanPayBankCard",method = RequestMethod.POST,headers = {"Accept-Version=1.0.0"})
    @ResponseBody
    public ResultDTO<Object> queryBankCard( HttpServletRequest req, HttpServletResponse resp) {
        ResultDTO<Object>  resultDTO = new ResultDTO<>();
        List<MemberBankCard> list = memberBankCardService.selectAllQuickPayBankCard(getMemberID(req));
        List<MemberBankCardDto> bankCardDtos = BeanCopyUtil.mapList(list, MemberBankCardDto.class);
        resultDTO.setResult(bankCardDtos);
        return resultDTO;
    }

    /**
     *1 绑定银行卡 ,向第三方支付发送绑定银行卡请求
     *
     * @param memberBankCardDto
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "bingQuickBankCard",method = RequestMethod.POST,headers = {"Accept-Version=1.0.0"})
    @ResponseBody
    public ResultDTO<Object> bingQuickBankCard(@Valid @ModelAttribute("form") MemberBankCardDto memberBankCardDto, BindingResult bindingResult, HttpServletRequest req, HttpServletResponse resp) {
        ResultDTO<Object> result = new ResultDTO<Object>();
        validateResult(result, bindingResult);
        if (result.proessError()) {
            return result;
        }
        Long memberId = getMemberID(req);
        return bingQuickBankCard(memberBankCardDto, memberId, result, false, getIp(req));
    }
    
    /**
     * 绑定银行卡 ,向第三方支付发送绑定银行卡请求
     * @param memberBankCardDto
     * @param memberId
     * @param result
     * @param isUpgradeSecurity
     * @param userIp 用户IP
     * @return
     */
    private ResultDTO bingQuickBankCard(MemberBankCardDto memberBankCardDto, Long memberId, ResultDTO result, boolean isUpgradeSecurity, String userIp){
    	 MemberBankCard memberBankCard = new MemberBankCard();
         BeanCopyUtil.copy(memberBankCardDto, memberBankCard);
         Member member = memberService.selectByPrimaryKey(memberId);
         memberBankCard.setCardHolder(member.getTrueName());
         memberBankCard.setMemberId(memberId);
         memberBankCard.setUpgradeSecurity(isUpgradeSecurity);
         memberBankCard.setUserBindIp(userIp);
         ResultDO<Object> objectResultDO = this.memberBankCardService.sendThirdPayCheckBlankCode(memberBankCard);
         converterResultDTO(result, objectResultDO);
         return result;
    }
    
    
    /**
     * 1 绑定银行卡 ,向第三方支付发送绑定银行卡请求
     * V1.0.2安全认证升级
     * @param memberBankCardDto
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "bingQuickBankCard",method = RequestMethod.POST,headers = {"Accept-Version=1.0.2"})
    @ResponseBody
    public ResultDTO<Object> bingQuickBankCard2(@Valid @ModelAttribute("form") MemberBankCardDto memberBankCardDto, BindingResult bindingResult, HttpServletRequest req, HttpServletResponse resp) {
        ResultDTO<Object> result = new ResultDTO<Object>();
        validateResult(result, bindingResult);
        if (result.proessError()) {
    		return result;
        }
        Long id = ServletRequestUtils.getLongParameter(req, "cardId",-1L);
    	if(id <= 0){
    		result.setResultCode(ResultCode.ERROR);
    		return result;
    	}
    	memberBankCardDto.setId(id);
        Long memberId = getMemberID(req);
        return bingQuickBankCard(memberBankCardDto, memberId, result,true, getIp(req));
    }

    
    /**
     *2 绑定银行卡  , 发送验证码 到第三方支付 验证
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "checkCodeAndSaveBankCard",method = RequestMethod.POST,headers = {"Accept-Version=1.0.0"})
    @ResponseBody
    public ResultDTO<Object> checkCodeFromThirdPay(@Valid @ModelAttribute("form") MemberBankCardDto memberBankCardDto ,       BindingResult bindingResult,HttpServletRequest req, HttpServletResponse resp) {
        ResultDTO<Object> result = new ResultDTO<Object>();
        validateResult(result, bindingResult);
        if (result.proessError()) {
            return result;
        }
        Long memberId = getMemberID(req);
        return checkCodeFromThirdPay(memberBankCardDto, memberId, result, false, getIp(req));
    }
    
    /**
     *2 绑定银行卡  , 发送验证码 到第三方支付 验证
     * V1.0.2安全认证升级
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "checkCodeAndSaveBankCard",method = RequestMethod.POST,headers = {"Accept-Version=1.0.2"})
    @ResponseBody
    public ResultDTO<Object> checkCodeFromThirdPay2(@Valid @ModelAttribute("form") MemberBankCardDto memberBankCardDto ,       BindingResult bindingResult,HttpServletRequest req, HttpServletResponse resp) {
        ResultDTO<Object> result = new ResultDTO<Object>();
        validateResult(result, bindingResult);
        if (result.proessError()) {
            return result;
        }
        Long id = ServletRequestUtils.getLongParameter(req, "cardId",-1L);
    	if(id <= 0){
    		result.setResultCode(ResultCode.ERROR);
    		return result;
    	}
    	memberBankCardDto.setId(id);
        Long memberId = getMemberID(req);
        return checkCodeFromThirdPay(memberBankCardDto, memberId, result, true, getIp(req));
    }
    
    
    /**
     * 绑定银行卡 ,推进 绑定成功后，添加银行卡
     * @param memberBankCardDto
     * @param memberId
     * @param result
     * @param isUpgradeSecurity
     * @return
     */
    private ResultDTO checkCodeFromThirdPay(MemberBankCardDto memberBankCardDto, Long memberId, ResultDTO result, boolean isUpgradeSecurity, String clientIp){
    	String ticket = memberBankCardDto.getTicket();
        String validCode = memberBankCardDto.getValidCode();
        if (StringUtil.isBlank(ticket)||StringUtil.isBlank(validCode)){
            result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
            return result;
        }
        MemberBankCard memberBankCard = new MemberBankCard();
        BeanCopyUtil.copy(memberBankCardDto, memberBankCard);
        Member member = memberService.selectByPrimaryKey(memberId);
        memberBankCard.setCardHolder(member.getTrueName());
        memberBankCard.setMemberId(memberId);
        memberBankCard.setUpgradeSecurity(isUpgradeSecurity);
        memberBankCard.setUserBindIp(clientIp);
        ResultDO<Object> resultDO = this.memberBankCardService.sendThirdPayCheckBlankCodeAdvance(ticket, validCode, memberBankCard);
        converterResultDTO(result, resultDO);
        return result;
    }
    
    /**
     * 添加一般银行卡
     *
     * @param dto
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "saveBankCard",method = RequestMethod.POST,headers = {"Accept-Version=1.0.0"})
    @ResponseBody
    public ResultDTO<Object> saveBankCard(@Valid @ModelAttribute("form") MemberBankCardDto dto, BindingResult bindingResult, HttpServletRequest req, HttpServletResponse resp) {
        ResultDTO<Object> addMemberBankCardresult = new ResultDTO<Object>();
        validateResult(addMemberBankCardresult, bindingResult);
        if (addMemberBankCardresult.proessError()) {
            return addMemberBankCardresult;
        }
        MemberBankCard memberBankCard = new MemberBankCard();
        BeanCopyUtil.copy(dto, memberBankCard);
        memberBankCard.setIsDefault(Constant.ISNOTDEFAULT);
        memberBankCard.setUserBindIp(getIp(req));
        try {
            ResultDO result = memberBankCardService.addMemberBankCard(memberBankCard);
            converterResultDTO(addMemberBankCardresult, result);
        } catch (Exception e) {
            addMemberBankCardresult.setResultCode(ResultCode.MEMBER_ADD_BANK_CARD_ERROR);
            logger.error("绑定银行卡失败memberId=" + memberBankCard.getMemberId() + ",dto=" + dto, e);
        }
        return addMemberBankCardresult;
    }

    /**
     *  查询 余额是否为0
     * @param req
     * @param resp
     * @return
     */
//    @RequestMapping(value = "balanceIsZero",method = RequestMethod.POST,headers = {"Accept-Version=1.0.0"})
//    @ResponseBody
//    public ResultDO<Object> balanceIsZero(HttpServletRequest req, HttpServletResponse resp){
//        ResultDO<Object>  result = new ResultDO<>();
//        if (ServletUtil.isVerifyTrueName()) {
//            MemberSessionDto memberSessionDto = getMember();
//            boolean isZero = this.balanceService.balanceIsZero(memberSessionDto.getId());
//            result.setSuccess(true);
//            result.setResult(isZero);
//        }else {
//            result.setSuccess(false);
//            result.setResult(false);
//        }
//        return result;
//    }
    /**
     * 解绑银行卡
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "deleteBankCardByID",method = RequestMethod.POST,headers = {"Accept-Version=1.0.0"})
    @ResponseBody
    public ResultDTO<Object> deleteBankCard(@RequestParam("bankCardID") Long cardID, HttpServletRequest req, HttpServletResponse resp) {
        ResultDTO<Object> result = new ResultDTO<Object>();
        Long memberId = getMemberID(req);
        try {
            ResultDO<Object> resultDO = this.memberBankCardService.deleteMemberBankCard(cardID, memberId, getIp(req));
            converterResultDTO(result, resultDO);
        } catch (Exception e) {
            logger.error("解除银行卡失败银行卡id" + cardID , e);
            result.setResultCode(ResultCode.ERROR_SYSTEM);
        }
        return result;
    }

    
    @RequestMapping(value = "queryNonSecurityBankCard",method = RequestMethod.POST,headers = {"Accept-Version=1.0.2"})
    @ResponseBody
    public ResultDTO queryNonSecurityBankCard(HttpServletRequest req, HttpServletResponse resp) {
    	ResultDTO result = new ResultDTO();
    	Long memberId = getMemberID(req);
    	BindSecurityBankCardDto security = memberBankCardService.bindSecurity(memberId);
    	result.setResult(security);
    	return result;
    }


    /**
     * 根据ID获得银行卡
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "getMemberBankCardById",method = RequestMethod.POST,headers = {"Accept-Version=1.0.2"})
    @ResponseBody
    public ResultDTO getMemberBankCardById(HttpServletRequest req, HttpServletResponse resp) {
    	ResultDTO result = new ResultDTO();
    	Long id = ServletRequestUtils.getLongParameter(req, "cardId",-1L);
    	if(id <= 0){
    		result.setResultCode(ResultCode.ERROR);
    	}
    	Long memberId = getMemberID(req);
    	SimpleMemberBankCardDto bankCard = memberBankCardService.getMemberBankCardById(id, memberId);
    	if(bankCard != null){
    		result.setResult(bankCard);
    	}else{
    		result.setResultCode(ResultCode.MEMBER_USERNAME_WITHDRAW_BANKID);
    	}
    	return result;
    }




}
