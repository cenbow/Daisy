package com.yourong.web.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.uc.model.MemberBankCard;
import com.yourong.web.dto.MemberBankCardDto;
import com.yourong.web.dto.MemberSessionDto;
import com.yourong.web.service.BalanceService;
import com.yourong.web.service.MemberBankCardService;
import com.yourong.web.utils.ServletUtil;

/**
 * 银行卡管理
 */
@Controller
@RequestMapping("memberBankCard")
public class MemberBankCardController extends BaseController {

    @Autowired
    private MemberBankCardService memberBankCardService;

    @Autowired
    private BalanceService balanceService;
    

    /**
     * 银行卡管理页面
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "bankManage")
    public ModelAndView bankManage(HttpServletRequest req,
                                   HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        if (ServletUtil.isVerifyTrueName()) {
            List<MemberBankCard> list = memberBankCardService
                    .getMemberBankCardByMemberId(getMember().getId());
            model.addObject("bankList", list);
            model.setViewName("/member/bankManage");
        } else {
            model.setViewName("forward:/member/sinapay");
        }
        return model;
    }

    /**
     * 添加银行卡页面
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "bankAdd")
    public ModelAndView bankAdd(HttpServletRequest req,
                                HttpServletResponse resp) throws ServletRequestBindingException {
        ModelAndView model = new ModelAndView();
//        if (ServletUtil.isVerifyTrueName()) {
            Long id = ServletRequestUtils.getLongParameter(req,"id",0);
            if (id !=0 ){
                MemberBankCard memberBankCard = this.memberBankCardService.selectByPrimaryKey(id);
                if (checkBankCard(model, memberBankCard)) return model;
                MemberBankCardDto memberBankCardDto = BeanCopyUtil.map(memberBankCard, MemberBankCardDto.class);
                model.addObject("quickCard",memberBankCardDto);
            }
            model.setViewName("/member/bankAdd");
//        } else {
//            model.setViewName("forward:/member/sinapay");
//        }
        return model;
    }

    private boolean checkBankCard(ModelAndView model, MemberBankCard memberBankCard) {
        MemberSessionDto memberSessionDto = getMember();
        if(memberBankCard ==null || memberSessionDto.getId() .compareTo(memberBankCard.getMemberId())!=0 ){
            model.addObject("ex", ResultCode.MEMBER_USERNAME_WRONG_BANKID.getMsg());
            model.setViewName("/error");
            return true;
        }
        if (memberBankCard.getCardType() ==2){
            model.addObject("ex", ResultCode.MEMBER_ADD_BANK_CARD_ERROR.getMsg());
            model.setViewName("/error");
            return true;
        }
        if (memberBankCard.getDelFlag() < 1){
            model.addObject("ex", ResultCode.MEMBER_USERNAME_WITHDRAW_BANKID.getMsg());
            model.setViewName("/error");
            return true;
        }
        return false;
    }

    /**
     * 1.绑定银行卡 ,向第三方支付发送绑定银行卡请求
     *
     * @param memberBankCardDto
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "sendThirdPayBingBankCard",method = RequestMethod.POST)
    @ResponseBody
    public ResultDO<Object> sendThirdPayBingBankCard(@Valid @ModelAttribute("form") MemberBankCardDto memberBankCardDto, BindingResult bindingResult, HttpServletRequest req, HttpServletResponse resp) {
        ResultDO<Object> result = new ResultDO<Object>();
        validateResult(result, bindingResult);
        if (!result.isSuccess()) {
            return result;
        }
        MemberBankCard memberBankCard = new MemberBankCard();
        BeanCopyUtil.copy(memberBankCardDto, memberBankCard);
        memberBankCard.setMemberId(getMember().getId());
        memberBankCard.setUserBindIp(getIp(req));
        result = this.memberBankCardService.sendThirdPayCheckBlankCode(memberBankCard);
        return result;
    }

    /**
     * 2.绑定银行卡  , 发送验证码 到第三方支付 验证
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "checkCodeFromThirdPay",method = RequestMethod.POST)
    @ResponseBody
    public ResultDO<Object> checkCodeFromThirdPay(@Valid @ModelAttribute("form") MemberBankCardDto memberBankCardDto ,BindingResult bindingResult,HttpServletRequest req, HttpServletResponse resp) {
        ResultDO<Object> result = new ResultDO<Object>();
        validateResult(result, bindingResult);
        String ticket = ServletRequestUtils.getStringParameter(req, "ticket", "");
        String validCode = ServletRequestUtils.getStringParameter(req, "checkCode", "");
        if (StringUtil.isBlank(ticket) || StringUtil.isBlank(validCode)) {
            result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
            return result;
        }
        MemberBankCard memberBankCard = new MemberBankCard();
        BeanCopyUtil.copy(memberBankCardDto, memberBankCard);
        memberBankCard.setMemberId(getMember().getId());
        memberBankCard.setUserBindIp(getIp(req));
        result = this.memberBankCardService.sendThirdPayCheckBlankCodeAdvance(ticket,validCode,memberBankCard);
        return result;
    }

    /**
     * 添加银行卡
     *
     * @param dto
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "addMemberBankCard",method = RequestMethod.POST)
    @ResponseBody
    public ResultDO<Object> addMemberBankCard(
            @Valid @ModelAttribute("form") MemberBankCardDto dto, BindingResult bindingResult,
            HttpServletRequest req, HttpServletResponse resp) {

        ResultDO<Object> addMemberBankCardresult = new ResultDO<Object>();
        validateResult(addMemberBankCardresult, bindingResult);
        if (!addMemberBankCardresult.isSuccess()) {
            return addMemberBankCardresult;
        }
        //验证两次输入银行卡是否正确的
        if (!StringUtil.equals(dto.getCardNumber(), dto.getCheckCardNumber(), true)) {
            addMemberBankCardresult.setSuccess(false);
            addMemberBankCardresult.setResultCode(ResultCode.MEMBER_BANKCARD_CHECK_ERROR);
            return addMemberBankCardresult;
        }
        MemberBankCard memberBankCard = new MemberBankCard();
        BeanCopyUtil.copy(dto, memberBankCard);
        memberBankCard.setBankMobile(getMember().getMobile());
        memberBankCard.setIsDefault(Constant.ISNOTDEFAULT);
        memberBankCard.setUserBindIp(getIp(req));
        try {
            addMemberBankCardresult = memberBankCardService.addMemberBankCard(memberBankCard);
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
    @RequestMapping(value = "balanceIsZero",method = RequestMethod.POST)
    @ResponseBody
    public ResultDO<Object> balanceIsZero(HttpServletRequest req, HttpServletResponse resp){
        ResultDO<Object>  result = new ResultDO<>();
        if (ServletUtil.isVerifyTrueName()) {
            MemberSessionDto memberSessionDto = getMember();
            boolean isZero = this.balanceService.isZeroMemberTotalAsset(memberSessionDto.getId());
            result.setSuccess(true);
            result.setResult(isZero);
        }else {
            result.setSuccess(false);
            result.setResult(false);
        }
        return result;
    }
    /**
     * 解绑银行卡
     *
     * @param dto
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "deleteMemberBankCard")
    @ResponseBody
    public ResultDO<Object> deleteMemberBankCard(
            @ModelAttribute("form") MemberBankCardDto dto,
            HttpServletRequest req, HttpServletResponse resp) {
        ResultDO<Object> result = new ResultDO<Object>();

        try {
            result =  this.memberBankCardService.deleteMemberBankCard(dto.getId(), getIp(req));
//            if (result.isError())
//                result.setResultCode(ResultCode.ERROR_SYSTEM);
        } catch (Exception e) {
            logger.error("解除银行卡失败memberId=" + dto.getMemberId() + ",dto=" + dto, e);
            result.setResultCode(ResultCode.ERROR_SYSTEM);
        }
        return result;
    }





}
