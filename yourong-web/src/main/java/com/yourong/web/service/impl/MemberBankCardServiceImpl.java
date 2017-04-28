package com.yourong.web.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.exception.ServiceException;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.BankCode;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.common.thirdparty.sinapay.member.domain.result.CardBindingResult;
import com.yourong.common.util.DateUtils;
import com.yourong.core.bsc.manager.BscBankManager;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.WithdrawLogManager;
import com.yourong.core.uc.manager.MemberBankCardManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberBankCard;
import com.yourong.web.service.BalanceService;
import com.yourong.web.service.MemberBankCardService;

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
    
    @Autowired
	private BscBankManager bscBankManager;

    @Autowired
    private BalanceManager balanceManager;
    
    @Autowired
    private MemberManager memberManager;


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
            logger.error(message);
            result.setSuccess(false);
            result.setResult(errorResult);
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
        MemberBankCard bankCard = getMemberBankCardByCardNumberAndMemberId(
                memberBankCard.getCardNumber(), memberBankCard.getMemberId());
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
        // 银行卡号是否存在
        MemberBankCard bankCard = getMemberBankCardByCardNumberAndMemberId(
                memberBankCard.getCardNumber(), memberBankCard.getMemberId());
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
            	result.setSuccess(false);
                result.setResult(cardResult.getErrorMsg());
                //result.setResultCode(ResultCode.SINA_PAY_ERROR);
            }
        } catch (Exception e) {
            logger.error("第三方接口绑定银行卡异常 memberBankCard =" + memberBankCard, e);
            result.setResultCode(ResultCode.SINA_PAY_ERROR);
        }
        return result;
    }

    @Override
    public ResultDO<Object> sendThirdPayCheckBlankCodeAdvance(String ticket, String validCode, MemberBankCard memberBankCard) {
        ResultDO<Object> result = checkBankCardOnQuickPay(memberBankCard);
        if (result.isError()) {
            return result;
        }
        try {
            ResultDto<?> cardResult = sinaPayClient.bindingBankCardAdvance(ticket, validCode, memberBankCard.getUserBindIp());
            if (cardResult.isError() || cardResult.getModule() == null) {
                result.setResultCode(ResultCode.MEMBER_BANK_ADVACE_CODE);
                result.setResult(cardResult.getErrorMsg());
                return result;
            }
            CardBindingResult cardBindingResult = (CardBindingResult) cardResult.getModule();
            //卡未认证
//            if (!cardBindingResult.isVerified()){
//                result.setResultCode(ResultCode.MEMBER_BANK_NOT_VERIFIED);
//                result.setResult(cardResult.getErrorMsg());
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
            result.setSuccess(true);
        } catch (Exception e) {
            logger.error("第三方接口绑定银行卡异常  ", e);
            result.setResultCode(ResultCode.SINA_PAY_ERROR);
        }
        return result;
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
    public ResultDO<Object> deleteMemberBankCard(Long id, String userIp) throws Exception {
        ResultDO<Object> resultDO = new ResultDO<>();
        try {
            MemberBankCard card = memberBankCardManager.selectByPrimaryKey(id);
            if (card == null) {
                resultDO.setResultCode(ResultCode.MEMBER_BANK_NOT_EXIST_ERROR);
                return resultDO;
            }
            boolean isWithDrawIng = withdrawLogManager.cardIsWithDrawIng(card.getMemberId(), id);
            if (isWithDrawIng){
                resultDO.setResultCode(ResultCode.MEMBER_USERNAME_ISWITHDRAWING_BANKID);
                return resultDO;
            }
            if (card.getIsSecurity() == 1 && card.getCardType() == 2) {
                boolean isZero = balanceManager.isZeroMemberTotalAsset(card.getMemberId());
                if (!isZero) {
                    resultDO.setResultCode(ResultCode.BALANCE_IS_NOT_ZERO);
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
            logger.error("删除银行卡失败 id={}",id,e);
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
            return  memberBankCardManager.selectAllQuickPayBankCard(memberId);
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
	public MemberBankCard selectSecurityBankCard(Long memberId) {
		try {
            return memberBankCardManager.querySecurityBankCard(memberId);
        } catch (Exception ex) {
            logger.error("selectSecurityBankCard", ex);
        }
		return null;
	}

	@Override
	public ResultDO<Object> addMemberBankCardInfo(MemberBankCard memberBankCard) throws Exception {
		ResultDO<Object> result = checkBankCard(memberBankCard);
        if (result.isError()) {
            return result;
        }
        memberBankCard.setCreateTime(DateUtils.getCurrentDateTime());
        memberBankCard.setUpdateTime(DateUtils.getCurrentDateTime());
        memberBankCard.setDelFlag(1);
        memberBankCard.setCardType(1);
        memberBankCardManager.insertSelective(memberBankCard);
        result.setSuccess(true);
        return result;
	}

	@Override
	public MemberBankCard synMemberBankCard(Long memberId, String cardId) {
		try {
			// 如果会员已经设置安全卡，直接返回安全卡ID
			MemberBankCard securityBankCard = memberBankCardManager.querySecurityBankCard(memberId);
			if (securityBankCard != null) {
				return securityBankCard;
			}
			// 目前新浪查询银行卡获取的姓名为加密，所以在次获取姓名
			Member member = memberManager.selectByPrimaryKey(memberId);
//			Long memberMobile = null;
			String trueName = null;
			if (member != null) {
//				memberMobile = member.getMobile();
				trueName = member.getTrueName();
			}
			ResultDO<MemberBankCard> resultDO = memberBankCardManager.synMemberBankCard(memberId, cardId, null, trueName);
			if (resultDO.isSuccess()) {
				return resultDO.getResult();
			} else {
				logger.info("同步银行卡失败，memberId=" + memberId + ",cardId=" + cardId);
			}
		} catch (Exception ex) {
			logger.error("同步银行卡出现异常，memberId=" + memberId + ",cardId=" + cardId + ",errorMsg=" + ex);
		}
		return null;
	}
}
