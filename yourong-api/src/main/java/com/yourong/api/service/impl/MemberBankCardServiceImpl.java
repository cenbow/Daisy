package com.yourong.api.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.api.dto.BindSecurityBankCardDto;
import com.yourong.api.dto.MemberNonSecurityBankCardDto;
import com.yourong.api.dto.SimpleMemberBankCardDto;
import com.yourong.api.service.BalanceService;
import com.yourong.api.service.MemberBankCardService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.exception.ServiceException;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.BankCode;
import com.yourong.common.thirdparty.sinapay.member.domain.result.CardBindingResult;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.manager.WithdrawLogManager;
import com.yourong.core.uc.manager.MemberBankCardManager;
import com.yourong.core.uc.model.MemberBankCard;

@Service
public class MemberBankCardServiceImpl implements MemberBankCardService {

    private Logger logger = LoggerFactory.getLogger(MemberBankCardServiceImpl.class);

    @Autowired
    private MemberBankCardManager memberBankCardManager;

    @Autowired
    private WithdrawLogManager withdrawLogManager;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private SinaPayClient sinaPayClient;
    
    @Override
    public MemberBankCard selectDeletedBankCard(Long id) {
        try {
            return memberBankCardManager.selectDeletedBankCard(id);
        } catch (ManagerException ex) {
            logger.error("selectDeletedBankCard", ex);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ResultDO<Object> addMemberBankCard(MemberBankCard memberBankCard)
            throws Exception {
        ResultDO<Object> result = checkBankCard(memberBankCard);
        if (result.isError()) {
            return result;
        }
        ResultDto<?> cardResult = sinaPayClient.bindingBankCard(
                memberBankCard.getMemberId(), memberBankCard.getBankCode(),
                memberBankCard.getCardNumber(), memberBankCard.getCardHolder(),
                memberBankCard.getBankMobile().toString(), false, memberBankCard.getUserBindIp());
        if (cardResult.isSuccess() && cardResult.getModule() != null) {
            CardBindingResult cardBindingResult = (CardBindingResult) cardResult.getModule();
            String card_id = cardBindingResult.getCardId();
            memberBankCard.setOuterSourceId(card_id);
        } else {
            String errorResult = cardResult != null ? cardResult.getErrorMsg() : "";
            String message = String.format("第三方支付，绑定银行卡异常MemberBankCard= %s,异常原因%s", memberBankCard.toString(), errorResult);
            thirdPayProsessError(result, errorResult, message);
            return result;
        }
        memberBankCard.setCreateTime(DateUtils.getCurrentDateTime());
        memberBankCard.setUpdateTime(DateUtils.getCurrentDateTime());
        memberBankCard.setDelFlag(1);
        memberBankCard.setCardType(1);
        memberBankCard.setIsSecurity(0);
        memberBankCardManager.insertSelective(memberBankCard);
        MessageClient.sendMsgForBindBandCard(memberBankCard.getBankCode(), memberBankCard.getCardNumber(), memberBankCard.getMemberId());
        result.setSuccess(true);
        return result;
    }

    private void thirdPayProsessError(ResultDO<Object> result, String errorResult, String message) {
        result.setResult(message);
        logger.info("第三支付绑卡失败",message);
        result.setSuccess(false);
        result.setResult(errorResult);
        ResultCode.SINA_PAY_ERROR.setMsg(errorResult);
        result.setResultCode(ResultCode.SINA_PAY_ERROR);
    }

    /**
     * 检查提现卡
     * @param memberBankCard
     * @return
     */
    private  ResultDO<Object> checkBankCard(MemberBankCard memberBankCard) {
        ResultDO<Object> result = new ResultDO<>();
        BankCode bscBank = BankCode.getBankCode(memberBankCard.getBankCode());
        if (bscBank == null) {
            // 银行不存在
            result.setSuccess(false);
            result.setResultCode(ResultCode.MEMBER_BANK_NOT_EXIST_ERROR);
            return result;
        }
        if (memberBankCard.getBankMobile() == null || memberBankCard.getBankMobile() == 0) {
            // 手机号码
            result.setSuccess(false);
            result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
            return result;
        }
        // 银行卡号是否存在
        MemberBankCard bankCard = getMemberBankCardByCardNumberAndMemberId(memberBankCard.getCardNumber(), memberBankCard.getMemberId());
        if (bankCard != null) {
            // 银行卡号已经存在
            result.setSuccess(false);
            result.setResultCode(ResultCode.MEMBER_BANK_CARD_EXIST_ERROR);
            return result;
        }

        return result;
    }
    /**
     * 快捷支付，效验卡信息
     * @param memberBankCard
     * @return
     */
    private  ResultDO<Object> checkBankCardOnQuickPay(MemberBankCard memberBankCard) {
        ResultDO<Object> result = new ResultDO<>();
        BankCode bscBank = BankCode.getBankCode(memberBankCard.getBankCode());
        if (bscBank == null) {
            // 银行不存在
            result.setSuccess(false);
            result.setResultCode(ResultCode.MEMBER_BANK_NOT_EXIST_ERROR);
            return result;
        }
        if(bscBank.getType() != 2 ){
        	// 银行不支持快捷支付
            result.setSuccess(false);
            result.setResultCode(ResultCode.MEMBER_BANK_NOTSUPPORT_PAY);
            return result;
        }        
        if (memberBankCard.getBankMobile() == null || memberBankCard.getBankMobile() == 0) {
            // 手机号码
            result.setSuccess(false);
            result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
            return result;
        }
        if(!memberBankCard.isUpgradeSecurity()){
	        // 银行卡号是否存在
	        MemberBankCard bankCard = getMemberBankCardByCardNumberAndMemberId( memberBankCard.getCardNumber(), memberBankCard.getMemberId());
			if (memberBankCard.getId() != null && memberBankCard.getId() != 0) {
	        	 if (bankCard == null) {
	                 // 银行卡号不存在
	                 result.setSuccess(false);
	                 result.setResultCode(ResultCode.MEMBER_USERNAME_WITHDRAW_BANKID);
	                 return result;
	             }        	
	        }else{
	        	 if (bankCard != null) {
	                 // 银行卡号已经存在
	                 result.setSuccess(false);
	                 result.setResultCode(ResultCode.MEMBER_BANK_CARD_EXIST_ERROR);
	                 return result;
	             }
	        } 
        }else{//如果是升级，银行卡必须存在
			 try {
				 if (memberBankCard.getId() == null) {
					 result.setSuccess(false);
	                 result.setResultCode(ResultCode.MEMBER_USERNAME_WITHDRAW_BANKID);
	                 return result;
				 }
				 MemberBankCard bankCard2 = memberBankCardManager.getMemberBankCardById(memberBankCard.getId(), memberBankCard.getMemberId());
				 if(bankCard2 == null || bankCard2.getDelFlag() < 0){
					 // 银行卡号不存在
	                 result.setSuccess(false);
	                 result.setResultCode(ResultCode.MEMBER_USERNAME_WITHDRAW_BANKID);
	                 return result;
				 }
				 if(bankCard2.getCardType() != 1){
					 // 银行卡号不存在
	                 result.setSuccess(false);
	                 result.setResultCode(ResultCode.MEMBER_BANK_CARD_IS_QUICK_PAY);
	                 return result;
				 }
				 //为了安全考虑银行卡号不需客户端传递
				 memberBankCard.setCardNumber(bankCard2.getCardNumber());
			} catch (ManagerException e) {
				e.printStackTrace();
			}
		}
        return result;
    }

    
    
    
    @Override
    public MemberBankCard getMemberBankCardByCardNumberAndMemberId(
            String cardNumber, Long memberId) {
        try {
            return memberBankCardManager.getMemberBankCardByCardNumberAndMemberId(cardNumber, memberId);
        } catch (ManagerException ex) {
            logger.error("getMemberBankCardByCardNumberAndMemberId", ex);
        }
        return null;
    }

    @Override
    public ResultDO<Object> sendThirdPayCheckBlankCode(MemberBankCard memberBankCard) {
        ResultDO<Object> result = checkBankCardOnQuickPay(memberBankCard);
        if (result.isError()) {
            return result;
        }
        try {
            ResultDto<?> cardResult = sinaPayClient.bindingBankCard(memberBankCard.getMemberId(), 
            		memberBankCard.getBankCode(),
            		memberBankCard.getCardNumber(),
                    memberBankCard.getCardHolder(), memberBankCard.getBankMobile().toString(),true, memberBankCard.getUserBindIp());
            if (cardResult.isSuccess() && cardResult.getModule() != null) {
                CardBindingResult cardBindingResult = (CardBindingResult) cardResult.getModule();
                Map<String, Object> map = Maps.newHashMap();
                map.put("ticket", cardBindingResult.getTicket());
                map.put("cardId", cardBindingResult.getCardId());
                result.setSuccess(true);
                result.setResult(map);
            } else {
                thirdPayProsessError(result, cardResult.getErrorMsg(), cardResult.getErrorMsg());
            }
        } catch (Exception e) {
            logger.error("第三方接口绑定银行卡异常 memberBankCard =" + memberBankCard, e);
            result.setResultCode(ResultCode.SINA_PAY_ERROR);
        }
        return result;
    }

    @Override
    public ResultDO<Object> sendThirdPayCheckBlankCodeAdvance(String ticket ,String validCode,MemberBankCard memberBankCard) {
        ResultDO<Object> result = checkBankCardOnQuickPay(memberBankCard);
        if (result.isError()) {
            return result;
        }
        try {
            ResultDto<?> cardResult = sinaPayClient.bindingBankCardAdvance(ticket, validCode, memberBankCard.getUserBindIp());
            if (cardResult.isError() || cardResult.getModule() == null) {
                setMessage(result, cardResult);
                return result;
            }
            CardBindingResult cardBindingResult = (CardBindingResult) cardResult.getModule();
//            //卡未认证
//            if (!cardBindingResult.isVerified()){
//                setMessage(result, cardResult);
//                return result;
//            }
			if (memberBankCard.getId() != null && memberBankCard.getId() != 0) {
				MemberBankCard bankCard = this.memberBankCardManager.selectByPrimaryKey(memberBankCard.getId());			
				bankCard.setOuterSourceId(cardBindingResult.getCardId());
				bankCard.setUpdateTime(DateUtils.getCurrentDateTime());
				bankCard.setBankMobile(memberBankCard.getBankMobile());
				bankCard.setCardType(2);
				this.memberBankCardManager.updateMemberBankCardQuickPay(bankCard);
			} else {
				memberBankCard.setOuterSourceId(cardBindingResult.getCardId());
				memberBankCard.setCreateTime(DateUtils.getCurrentDateTime());
				memberBankCard.setUpdateTime(DateUtils.getCurrentDateTime());
				memberBankCard.setDelFlag(1);
				memberBankCard.setIsDefault(0);
                memberBankCard.setIsSecurity(0);
                memberBankCard .setCardType(2);
				memberBankCardManager.insertSelective(memberBankCard);
			}            
			MessageClient.sendMsgForOpenQuickPayment(memberBankCard.getMemberId(), memberBankCard.getCardNumber(), memberBankCard.getBankCode());
			JSONObject json = new JSONObject();
			json.put("cardId", memberBankCard.getId());
			result.setResult(json);
            result.setSuccess(true);
        } catch (Exception e) {
            logger.error("第三方接口绑定银行卡异常  ", e);
            result.setResultCode(ResultCode.SINA_PAY_ERROR);
        }
        return result;
    }

    private void setMessage(ResultDO<Object> result, ResultDto<?> cardResult) {
     //   ResultCode.MEMBER_BANK_ADVACE_CODE.setMsg(cardResult.getErrorMsg());
        result.setResultCode(ResultCode.MEMBER_ADVACE_CODE_EROOR);
        result.setResult(cardResult.getErrorMsg());
    }

    @Override
    public List<MemberBankCard> getMemberBankCardByMemberId(Long memberId) {
        try {
            return memberBankCardManager.selectByMemberID(memberId);
        } catch (ManagerException ex) {
            logger.error("getMemberBankCardByMemberId", ex);
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ResultDO<Object> deleteMemberBankCard(Long id, Long memberId, String userIp) throws Exception {
        ResultDO<Object> resultDO = new ResultDO<>();
        try {
            MemberBankCard card = memberBankCardManager.selectByPrimaryKey(id);
            if (card == null) {
                resultDO.setResultCode(ResultCode.MEMBER_BANK_NOT_EXIST_ERROR);
                return resultDO;
            }
            if (memberId .compareTo( card.getMemberId()) !=0) {
                resultDO.setResultCode(ResultCode.MEMBER_USERNAME_WRONG_BANKID);
                return resultDO;
            }
            if (card.getDelFlag() != 1) {
                resultDO.setResultCode(ResultCode.MEMBER_USERNAME_WITHDRAW_BANKID);
                return resultDO;
            }
            boolean isWithDrawIng = withdrawLogManager.cardIsWithDrawIng(card.getMemberId(), id);
            if (isWithDrawIng){
                resultDO.setResultCode(ResultCode.MEMBER_USERNAME_ISWITHDRAWING_BANKID);
                return resultDO;
            }
            if (card.getIsSecurity() == 1 && card.getCardType() == 2) {
                boolean isZero = balanceService.isZeroMemberTotalAsset(card.getMemberId());
                if (!isZero) {
                    resultDO.setResultCode(ResultCode.MEMBER_BANKCARD_NOT_ZERO);
                    return resultDO;
                }
            }
            ResultDto<?> unBindingBankCard = sinaPayClient.unBindingBankCard(card.getMemberId(), card.getOuterSourceId(), userIp);
            if (unBindingBankCard.isSuccess()) {
                int i = memberBankCardManager.deleteMemberBankCard(id, userIp);
                if (i == 1){
                    resultDO.setSuccess(true);
                    MessageClient.sendMsgForDeleteBankCard(card.getMemberId(), card.getCardNumber(), card.getBankCode());
                }
            }
        } catch (Exception e) {
            logger.error("删除银行卡失败 id={},memberId={}",id,memberId,e);
            throw new ServiceException(e);
        }
        return resultDO;
    }

    @Override
    public int setDefaultMemberBankCard(Long id, Long memberId) {
        try {
            memberBankCardManager.resetMemberBankCardDefaultStatus(memberId);
            return memberBankCardManager.setDefaultMemberBankCard(id);
        } catch (Exception e) {
            logger.error("设置默认银行卡异常 memberId=" + memberId + " id=" + id, e);
        }
        return 0;
    }

    @Override
    public MemberBankCard getDefaultBankCardByMemberId(Long memberId) {
        try {
            return memberBankCardManager.getDefaultBankCardByMemberId(memberId);
        } catch (Exception e) {
            logger.error("获取默认银行卡失败 memberId=" + memberId, e);
        }
        return null;
    }

    @Override
    public boolean isExist(Long id, Long memberId) {
        try {
            return memberBankCardManager.isExist(id, memberId);
        } catch (Exception e) {
            logger.error("查询 id memberId=" + memberId, e);
        }
        return false;
    }

    @Override
    public List<MemberBankCard> selectAllQuickPayBankCard(Long memberId) {
        try {
        	MemberBankCard  bankCard = querySecurityBankCard(memberId);
        	if(bankCard == null){
        		return  memberBankCardManager.selectAllQuickPayBankCard(memberId);
        	}else{
        		List<MemberBankCard> list = Lists.newArrayList();
        		list.add(bankCard);
        		return list;
        	}
        }catch (Exception e){
            logger.error("查询 用户会员快捷支付卡id memberId=" + memberId, e);
        }
        return  null;
    }

	@Override
	public MemberBankCard selectByPrimaryKey(Long id) {
		try {
			return memberBankCardManager.selectByPrimaryKey(id);
		} catch (Exception e) {
			logger.error("查询 银行卡 id =" + id, e);
		}
		return null;
	}

	@Override
	public MemberBankCard querySecurityBankCard(Long memberId) {
		try {
			return memberBankCardManager.querySecurityBankCard(memberId);
		} catch (Exception e) {
			logger.error("获得用户安全快捷支付卡信息异常，memberId=" + memberId, e);
		}
		return null;
	}

	@Override
	public List<MemberNonSecurityBankCardDto> selectNonSecurityBankCard(Long memberId) {
		try {
			List<MemberBankCard> bankCardList = memberBankCardManager.selectNonSecurityBankCard(memberId);
			if(Collections3.isNotEmpty(bankCardList)){
				return BeanCopyUtil.mapList(bankCardList, MemberNonSecurityBankCardDto.class);
			}
		} catch (Exception e) {
			logger.error("获取用户非安全卡的银行卡异常，memberId=" + memberId, e);
		}
		return null;
	}

	@Override
	public BindSecurityBankCardDto bindSecurity(Long memberId) {
		BindSecurityBankCardDto dto = new BindSecurityBankCardDto();
//		MemberBankCard securityBankCard = querySecurityBankCard(memberId);
//		if(securityBankCard != null){
//			dto.setSecurity(true);
//			return dto;
//		}
		List<MemberNonSecurityBankCardDto> bankCardList = selectNonSecurityBankCard(memberId);
		if(Collections3.isNotEmpty(bankCardList)){
			dto.setBankCardList(bankCardList);
		}
		return dto;
	}

	@Override
	public SimpleMemberBankCardDto getMemberBankCardById(Long id, Long memberId) {
		try {
			MemberBankCard bankCard = memberBankCardManager.getMemberBankCardById(id, memberId);
			if(bankCard != null){
				return BeanCopyUtil.map(bankCard, SimpleMemberBankCardDto.class);
			}
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		return null;
	}
}
