package com.yourong.core.uc.manager.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.member.domain.result.BankCardEntry;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.uc.dao.MemberBankCardMapper;
import com.yourong.core.uc.manager.MemberBankCardManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberBankCard;

@Component
public class MemberBankCardManagerImpl implements MemberBankCardManager {
	
	private static final Logger logger = LoggerFactory.getLogger(Logger.class);

    @Autowired
    private MemberBankCardMapper memberBankCardMapper;
    
    @Autowired
    private SinaPayClient sinaPayClient;
    
    @Autowired
    private MemberManager memberManager;

    @Autowired
	private BalanceManager balanceManager;
    
    @Autowired
    private SysDictManager sysDictManager;

    @Override
    public int updateMemberBankCardQuickPay(MemberBankCard record) throws ManagerException {
        try {
            return memberBankCardMapper.updateMemberBankCardQuickPay(record);
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }

    @Override
    public int updateMemberBankCardSecurity(Long id) throws ManagerException {
        try {
            return memberBankCardMapper.updateMemberBankCardSecurity(id);
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }

    @Override
    public Page<MemberBankCard> findByPage(Page<MemberBankCard> pageRequest,
                                           Map<String, Object> map) throws ManagerException {
        try {
            return memberBankCardMapper.findByPage(pageRequest, map);
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }

    public MemberBankCard selectByPrimaryKey(Long id) throws ManagerException {
        try {
            return memberBankCardMapper.selectByPrimaryKey(id);
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }

    @Override
    public MemberBankCard getMemberBankCardByCardNumberAndMemberId(
            String cardNumber, Long memberId) throws ManagerException {
        try {
            return memberBankCardMapper
                    .getMemberBankCardByCardNumberAndMemberId(cardNumber,
                            memberId);
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }

    @Override
    public int insert(MemberBankCard record) throws ManagerException {
        try {
            return memberBankCardMapper.insert(record);
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }

    @Override
    public List<MemberBankCard> selectAllQuickPayBankCard(Long memberId) throws ManagerException {
        try {
            return memberBankCardMapper.selectAllQuickPayBankCard(memberId);
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }

    @Override
    public int insertSelective(MemberBankCard record) throws ManagerException {
        try {
            return memberBankCardMapper.insertSelective(record);
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }

    @Override
    public int resetMemberBankCardDefaultStatus(Long memberId)
            throws ManagerException {
        try {
            return memberBankCardMapper
                    .resetMemberBankCardDefaultStatus(memberId);
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }

    @Override
    public Long getMemberBankCardQuantityByMemberId(Long memberId)
            throws ManagerException {
        try {
            return memberBankCardMapper
                    .getMemberBankCardQuantityByMemberId(memberId);
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }

    @Override
    public List<MemberBankCard> selectByMemberID(Long memberId)
            throws ManagerException {
        try {
        	// 同步更新银行卡
        	try {
        		ResultDO<MemberBankCard> resultDo = this.synMemberBankCard(memberId, null, 0L, null);
        		if (!resultDo.isSuccess()) {
        			logger.info("进入银行卡管理页面,同步更新银行卡失败,memberId={}", memberId);
        		}
        	} catch (Exception ex) {
        		logger.error("进入银行卡管理页面,同步更新银行卡异常,memberId={}", memberId);
        	}
        	
        	List<MemberBankCard> memberBankCards = memberBankCardMapper.selectByMemberID(memberId);
            return memberBankCards;
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }

    @Override
    public int deleteMemberBankCard(Long id, String userUnBindIp) throws ManagerException {
        try {
            return memberBankCardMapper.deleteMemberBankCard(id, userUnBindIp);
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }

    @Override
    public int setDefaultMemberBankCard(Long id) throws ManagerException {
        try {
            return memberBankCardMapper.setDefaultMemberBankCard(id);
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }

    /**
     * 获取默认银行卡，如果没有默认获取第一张
     */
    @Override
    public MemberBankCard getDefaultBankCardByMemberId(Long memberId)
            throws ManagerException {
        try {
            return memberBankCardMapper.getDefaultBankCardByMemberId(memberId);
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }


    /**
     *
     */
    public boolean isExist(Long id, Long memberId) throws ManagerException {
        try {
            Integer exist = memberBankCardMapper.isExist(id, memberId);
            if (exist != null && exist == 1) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }

    @Override
     public MemberBankCard selectDeletedBankCard(Long id) throws ManagerException {
        try {
            return this.memberBankCardMapper.selectDeletedBankCard(id);
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }

    public void setSecurityCardFromThirdPay(Long bankCardId) throws Exception {
        MemberBankCard memberBankCard = selectByPrimaryKey(bankCardId);
        //用户卡已经是安全卡，不用重新设置
        if ((memberBankCard.getCardType() == 2 && memberBankCard.getIsSecurity() == 1) || memberBankCard.getDelFlag() < 1) {
            return;
        }
        //用户是快捷卡， 交易成功后 改卡为安全卡
        if (memberBankCard.getCardType() == 2 && memberBankCard.getIsSecurity() == 0) {
            ResultDto<?> resultDto = sinaPayClient.queryBankCard(memberBankCard.getMemberId(), memberBankCard.getOuterSourceId());
            if (resultDto.isError()) {
                return;
            }
            List<BankCardEntry> list = (List<BankCardEntry>) resultDto.getModule();
            if (Collections3.isEmpty(list)) {
                return;
            }
            BankCardEntry entry = list.get(0);
            if (entry.isSecurity()) {
                int i = updateMemberBankCardSecurity(bankCardId);
                MessageClient.sendMsgForSafeBankCard(memberBankCard.getMemberId(), bankCardId);
            }
        }
    }


    @Override
    public List<MemberBankCard> selectAllQuickPayNotSecurity() {
        return this.memberBankCardMapper.selectAllQuickPayNotSecurity();
    }

	@Override
	public MemberBankCard querySecurityBankCard(Long memberId) throws Exception {
		return this.memberBankCardMapper.querySecurityBankCard(memberId);
	}
	
	/**
	 *	后台删除用户绑定银行卡功能(目前只将权限设置为开发人员可用)
	 */
	public ResultDO<Object> deleteBankCard(Long mobile, String userIp) throws Exception{
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			Member member = memberManager.selectByMobile(mobile);
			if(member==null){
				resultDO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
				return resultDO;
			}
			MemberBankCard card = this.querySecurityBankCard(member.getId());
			if(card==null){
				resultDO.setResultCode(ResultCode.MEMBER_BANK_SECURITY_CARD_NOT_EXIST_ERROR);;
				return resultDO;
			}
			if(!StringUtil.isNotBlank(card.getOuterSourceId())){
				resultDO.setResultCode(ResultCode.MEMBER_BANK_CARD_OUTER_SOURCE_ID_NOT_EXIST_ERROR);
				return resultDO;
			}
			ResultDto<?> unBindingBankCard = sinaPayClient.unBindingBankCard(card.getMemberId(), card.getOuterSourceId(), userIp);
			if (unBindingBankCard.isSuccess()) {
				int i = memberBankCardMapper.deleteMemberBankCard(card.getId(), userIp);
				if (i == 1){
					resultDO.setSuccess(true);
				}else{
					resultDO.setResultCode(ResultCode.MEMBER_BANK_CARD_HAD_DELETED_ERROR);
				}
			}else{
				resultDO.setSuccess(false);
				logger.error(unBindingBankCard.getErrorMsg());
			}
			return resultDO;
		} catch (Exception e) {
			logger.error("后台删除用户绑定银行卡失败，mobile={}",mobile,e);
			resultDO.setSuccess(false);
			return resultDO;
		}
	}

	@Override
	public List<MemberBankCard> selectNonSecurityBankCard(Long memberId)
			throws ManagerException {
		return memberBankCardMapper.selectNonSecurityBankCard(memberId);
	}

	@Override
	public MemberBankCard getMemberBankCardById(Long id, Long memberId)
			throws ManagerException {
		return memberBankCardMapper.getMemberBankCardById(id, memberId);
	}
	
	@Override
    public Page<MemberBankCard> queryMemberCard(Page<MemberBankCard> pageRequest,
                                           Map<String, Object> map) throws ManagerException {
			Page<MemberBankCard> page = new Page<MemberBankCard>();
        try {
        	map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			List<MemberBankCard> memberBankCardList=  memberBankCardMapper.queryMemberCard(map);
			int totalCount = memberBankCardMapper.queryMemberCardCount(map);
			page.setData(memberBankCardList);
			page.setiTotalDisplayRecords(totalCount);
			page.setiTotalRecords(totalCount);
			return page;
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }

	@Override
	public ResultDO<Object> deleteBankCardByMemberId(Long id, String userIp) throws Exception {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			BigDecimal count = null;
			Balance balance = balanceManager.queryBalance(id, TypeEnum.BALANCE_TYPE_PIGGY);
			if(balance !=null ){
				count = balance.getAvailableBalance();
			}else{
				count = BigDecimal.ZERO;
			}
			if(count.compareTo(BigDecimal.ZERO) == 0) {
				MemberBankCard card = this.querySecurityBankCard(id);
				if(card==null){
					resultDO.setResultCode(ResultCode.MEMBER_BANK_SECURITY_CARD_NOT_EXIST_ERROR);;
					return resultDO;
				}
				if(!StringUtil.isNotBlank(card.getOuterSourceId())){
					resultDO.setResultCode(ResultCode.MEMBER_BANK_CARD_OUTER_SOURCE_ID_NOT_EXIST_ERROR);
					return resultDO;
				}
				ResultDto<?> unBindingBankCard = sinaPayClient.unBindingBankCard(card.getMemberId(), card.getOuterSourceId(), userIp);
				if (unBindingBankCard.isSuccess()) {
					int i = memberBankCardMapper.deleteMemberBankCard(card.getId(), userIp);
					if (i == 1){
						resultDO.setSuccess(true);
					}else{
						resultDO.setResultCode(ResultCode.MEMBER_BANK_CARD_HAD_DELETED_ERROR);
					}
				}else{
					resultDO.setSuccess(false);
					logger.error("后台删除用户绑定银行卡失败, memberId={}, 新浪解绑返回：" + unBindingBankCard.getErrorMsg(), id);
				}		
			} else {
				resultDO.setResultCode(ResultCode.MEMBER_BANKCARD_NOT_ZERO);
			}
			return resultDO;
		} catch (Exception e) {
			logger.error("后台删除用户绑定银行卡失败，memberId={}", id, e);
			resultDO.setSuccess(false);
			return resultDO;
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public ResultDO<MemberBankCard> synMemberBankCard(Long memberId, String cardId, Long bankMobile, String trueName) throws Exception {
		ResultDO<MemberBankCard> rDO = new ResultDO<MemberBankCard>();
		// 检查会员银行卡是否存在
//		if (StringUtil.isNotBlank(cardId)) {
//			MemberBankCard checkCardIsExist = memberBankCardMapper.getMemberCardByOuterSource(memberId, cardId);
//			if (checkCardIsExist != null) {
//				rDO.setResult(checkCardIsExist);
//				return rDO;
//			}
//		}
		ResultDto<?> resultDto = sinaPayClient.queryBankCard(memberId, cardId);
        if (resultDto.isError()) {
        	rDO.setSuccess(false);
        	rDO.setResultCode(ResultCode.MEMBER_BANK_CARD_HAD_DELETED_ERROR);
        	logger.error("同步银行卡时，调用新浪查询接口失败, memberId={}, cardId={}", memberId, cardId);
            return rDO;
        }
        List<BankCardEntry> bankCardEntryList = (List<BankCardEntry>) resultDto.getModule();
        if (Collections3.isEmpty(bankCardEntryList)) {
        	rDO.setSuccess(false);
        	rDO.setResultCode(ResultCode.MEMBER_BANK_CARD_HAD_DELETED_ERROR);
        	logger.info("同步银行卡时，调用新浪查询接口,查询银行卡为空, memberId={}, cardId={}", memberId, cardId);
            return rDO;
        }
        if (StringUtil.isBlank(trueName)) {
        	Member member = memberManager.selectByPrimaryKey(memberId);
			if (member != null) {
				trueName = member.getTrueName();
			}
        }
        // 一个银行卡
        if (bankCardEntryList.size() == 1) { 
        	MemberBankCard memberBankCard = saveMemberBankCard(memberId, bankMobile, trueName, bankCardEntryList.get(0));
        	if(memberBankCard != null) {
        		rDO.setResult(memberBankCard);
        	} else {
        		logger.info("[同步会员银行卡]出现错误：memberId = " + memberId + ",cardId=" + cardId);
        		rDO.setSuccess(false);
        	}
        	return rDO;
        }
        // 多个银行卡
        for (BankCardEntry bankCardEntry : bankCardEntryList) {
        	MemberBankCard memberBankCard = saveMemberBankCard(memberId, bankMobile, trueName, bankCardEntry);
        	if (memberBankCard != null) {
        		//如果是安全卡,返回
        		if(memberBankCard.getIsSecurity() == 1) {
        			rDO.setResult(memberBankCard);
        		}
        	} else {
        		logger.info("[同步会员银行卡]出现错误：memberId = " + memberId + ",cardId=" + cardId + ",cardNumber=" + bankCardEntry.getAccountNo());
        		continue;
        	}
        }
        
        return rDO;
	}
	// 保存银行卡
	private MemberBankCard saveMemberBankCard(Long memberId, Long bankMobile, String trueName, BankCardEntry bankCardEntry) throws ManagerException {
		if (bankCardEntry == null) {
			return null;
		}
		// 用户绑定IP
		String userBindIp = null;
		SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
		if (dict != null) {
			userBindIp = dict.getValue();
		}
		
		MemberBankCard existBankCard = memberBankCardMapper.getMemberCardByOuterSource(memberId, bankCardEntry.getCardId());
		// 如果存在，更新银行卡
		if (existBankCard != null) {
			// 是否安全卡
			if (bankCardEntry.isSecurity()) {
				existBankCard.setIsSecurity(1);
			} else {
				existBankCard.setIsSecurity(0);
			}
			// 是否快捷支付
			if (bankCardEntry.isVerified()) {
				existBankCard.setCardType(2);
			} else {
				existBankCard.setCardType(1);
			}
			if (StringUtil.isBlank(existBankCard.getUserBindIp())) {
				existBankCard.setUserBindIp(userBindIp);
			}
			existBankCard.setUpdateTime(DateUtils.getCurrentDateTime());
			memberBankCardMapper.updateByPrimaryKeySelective(existBankCard);
			return existBankCard;
		}
		MemberBankCard memberBankCard = new MemberBankCard();
		memberBankCard.setMemberId(memberId);
		memberBankCard.setCardNumber(bankCardEntry.getAccountNo());
//      memberBankCard.setCardHolder(bankCardEntry.getAccountName());
		memberBankCard.setCardHolder(trueName);
		memberBankCard.setBankCode(bankCardEntry.getBankCode().toString());
		memberBankCard.setOuterSourceId(bankCardEntry.getCardId());
		// 是否安全卡
		if (bankCardEntry.isSecurity()) {
			memberBankCard.setIsSecurity(1);
		} else {
			memberBankCard.setIsSecurity(0);
		}
		// 是否快捷支付
		if (bankCardEntry.isVerified()) {
			memberBankCard.setCardType(2);
		} else {
			memberBankCard.setCardType(1);
		}
		
		if (bankMobile != null) {
			memberBankCard.setBankMobile(bankMobile);
		}
		memberBankCard.setCreateTime(DateUtils.getCurrentDateTime());
		memberBankCard.setUpdateTime(DateUtils.getCurrentDateTime());
		memberBankCard.setDelFlag(1);
		memberBankCard.setUserBindIp(userBindIp);
		int addResult = memberBankCardMapper.insertSelective(memberBankCard);
		if (addResult > 0) {
			return memberBankCard;
		}
		return null;
	}
	
}
