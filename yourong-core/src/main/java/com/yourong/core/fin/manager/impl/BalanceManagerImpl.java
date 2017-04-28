package com.yourong.core.fin.manager.impl;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yourong.common.constant.Config;
import com.yourong.common.enums.BalanceAction;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.BankCode;
import com.yourong.common.thirdparty.sinapay.member.domain.result.AccountBalance;
import com.yourong.common.thirdparty.sinapay.member.domain.result.AccountDetail;
import com.yourong.common.thirdparty.sinapay.member.domain.result.StatementEntry;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.dao.BalanceMapper;
import com.yourong.core.fin.dao.CapitalInOutLogMapper;
import com.yourong.core.fin.dao.RechargeLogMapper;
import com.yourong.core.fin.dao.WithdrawLogMapper;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.CapitalInOutLog;
import com.yourong.core.fin.model.RechargeLog;
import com.yourong.core.fin.model.WithdrawLog;
import com.yourong.core.fin.model.query.BalanceQuery;
import com.yourong.core.tc.dao.TransactionInterestMapper;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.uc.dao.MemberBankCardMapper;
import com.yourong.core.uc.model.MemberBankCard;

@Service
public class BalanceManagerImpl implements BalanceManager {
    private static final String RECEIVE_BANK = "到账银行：";

	@Autowired
    private BalanceMapper balanceMapper;

    @Autowired
    private SinaPayClient sinaPayClient;

    @Autowired
    private TransactionInterestMapper transactionInterestMapper;
    
    @Autowired
    private TransactionManager transactionManager;


    /**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CapitalInOutLogMapper capitalInOutLogMapper;
    
    @Autowired
    private WithdrawLogMapper  withdrawLogMapper;
    
    @Autowired
    private RechargeLogMapper rechargeLogMapper;
    
    @Autowired
    private MemberBankCardMapper memberBankCardMapper;
    
    @Autowired
    private PopularityInOutLogManager popularityInOutLogManager;
     
    @Override
    public Balance queryBalance(Long sourceId, TypeEnum type) throws ManagerException {
        try {
        	BalanceQuery balanceQuery = new BalanceQuery();
        	balanceQuery.setBalanceType(type.getType());
        	balanceQuery.setSourceId(sourceId);
            Balance result = balanceMapper.selectByQuery(balanceQuery);
            return result;
        } catch (Exception e) {
            throw new ManagerException(e);
        }

    }

    @Override
    public int insert(Balance record) throws ManagerException {
        try {
            return balanceMapper.insertSelective(record);
        } catch (Exception e) {
            throw new ManagerException(e);
        }
    }

    
    public int update(Balance record) throws ManagerException {
        try {
            return balanceMapper.updateByPrimaryKeySelective(record);
        } catch (Exception e) {
            throw new ManagerException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Balance reduceBalance(TypeEnum type, BigDecimal amount, Long sourceId, BalanceAction action)
            throws ManagerException {
        Balance keyLock = updateBalanceByType(type, amount, sourceId, action);
        return keyLock;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Balance reducePopularityBalance(BigDecimal amount, Long sourceId)
            throws ManagerException {
        Balance keyLock = updateBalanceByType(TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY, amount, sourceId, BalanceAction.balance_subtract_Available_subtract);
        return keyLock;
    }
    
    
    private Balance updateBalanceByType(TypeEnum type, BigDecimal amount, Long sourceId,BalanceAction action) throws ManagerException {
        try {        	
        	Balance result = queryBalance(sourceId, type);          
            //锁行,修改表
            Balance keyLock = balanceAndAvailableAction(amount, result,action);          
            return keyLock;
        } catch (Exception e) { 
        	String error = String.format("更新余额表异常，sourceId=%s,amount=%s,action=%s,type=%s", sourceId,amount,action,type);
        	logger.error(error, e);
            throw new ManagerException(e);
        }
    }
    /**
     * 锁表，更新余额
     * @param amount
     * @param status
     * @param result
     * @return
     * author: pengyong
     * 上午10:42:34
     */
	private Balance balanceAndAvailableAction(BigDecimal amount,Balance result,BalanceAction action) {
		Balance keyLock = balanceMapper.selectByPrimaryKeyLock(result.getId());
		BigDecimal balance = null;
		BigDecimal availableBalance = null;		
		if (action  == BalanceAction.balance_subtract) {
			//余额减少，可用余额不变
		    balance = keyLock.getBalance().subtract(amount);
		    availableBalance = keyLock.getAvailableBalance();               
		} else if (action == BalanceAction.balance_subtract_Available_subtract) {
			 //余额减少，可用余额也减少
		    balance = keyLock.getBalance().subtract(amount);
		    availableBalance = keyLock.getAvailableBalance().subtract(amount);                
		} else if (action == BalanceAction.balance_Available) {
			//直接修改
		    balance = amount;
		    availableBalance = amount;
		}else if(action == BalanceAction.balance_Add_Available_add){
			//余额 和 可用余额增加
			 balance = keyLock.getBalance().add(amount);
		     availableBalance = keyLock.getAvailableBalance().add(amount);             
		}else if(action == BalanceAction.balance_Available_subtract){
			 //余额不变， 可用余额减少
			 balance = keyLock.getBalance();
		     availableBalance = keyLock.getAvailableBalance().subtract(amount); 
		}else if(action == BalanceAction.balance_Available_add){
			 //余额不变， 可用余额增加
			 balance = keyLock.getBalance();
		     availableBalance = keyLock.getAvailableBalance().add(amount); 
		}
		keyLock.setAvailableBalance(availableBalance);
		keyLock.setBalance(balance);
		balanceMapper.updateBalanceByTypeAndSource(keyLock);
		return keyLock;
	}

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int updateBalanceByID(BigDecimal balance, BigDecimal availableBalance, Long id) throws ManagerException {
        try {
            Balance keyLock = balanceMapper.selectByPrimaryKeyLock(id);
            keyLock.setAvailableBalance(availableBalance);
            keyLock.setBalance(balance);
            return balanceMapper.updateBalanceByTypeAndSource(keyLock);
        } catch (Exception e) {
            throw new ManagerException(e);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Balance increaseBalance(TypeEnum type, BigDecimal amount, Long sourceId) throws ManagerException {
        return updateBalanceByType(type, amount, sourceId, BalanceAction.balance_Add_Available_add);
    }

    @Override
    public int insertBalance(TypeEnum type, BigDecimal balance, Long sourceId)
            throws ManagerException {
        try {
            Balance blan = new Balance();
            blan.setAvailableBalance(balance);
            blan.setBalance(balance);
            blan.setBalanceType(type.getType());
            blan.setSourceId(sourceId);
            blan.setCreateTime(DateUtils.getCurrentDateTime());
            return balanceMapper.insertSelective(blan);
        } catch (Exception e) {
            throw new ManagerException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int resetProjectBalance(BigDecimal balance, Long sourceId)
            throws ManagerException {
        Balance type = updateBalanceByType(TypeEnum.BALANCE_TYPE_PROJECT, balance, sourceId, BalanceAction.balance_Available);
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Balance frozenBalance(TypeEnum balanceTypeProject,
                                 BigDecimal investAmount, Long projectId) throws ManagerException {
		if (balanceTypeProject != TypeEnum.BALANCE_TYPE_PROJECT
				&& balanceTypeProject != TypeEnum.BALANCE_TYPE_TRANSFER_AMOUNT) {
            return null;
        }
        Balance blance = queryBalance(projectId, balanceTypeProject);
        Balance keyLock = balanceMapper.selectByPrimaryKeyLock(blance.getId());
        //冻结余额， 可用余额 减去  investAmount
        BigDecimal availableBalace = keyLock.getAvailableBalance().subtract(investAmount);
        keyLock.setAvailableBalance(availableBalace);
        //keyLock.setBalance(queryBalance.getBalanceBigDecimal());
        balanceMapper.updateBalanceByTypeAndSource(keyLock);
        return keyLock;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Balance unfrozenBalance(TypeEnum balanceTypeProject,
                                   BigDecimal investAmount, Long projectId) throws ManagerException {
        if (balanceTypeProject != TypeEnum.BALANCE_TYPE_PROJECT) {
            return null;
        }
        Balance blance = queryBalance(projectId, balanceTypeProject);
        Balance keyLock = balanceMapper.selectByPrimaryKeyLock(blance.getId());
        //解冻余额， 可用余额 加  investAmount
        BigDecimal balace = keyLock.getAvailableBalance().add(investAmount);
        keyLock.setAvailableBalance(balace);
        balanceMapper.updateBalanceByTypeAndSource(keyLock);
        return keyLock;

    }

    @Override
    public int insertBalance(TypeEnum type, BigDecimal balance,
                             BigDecimal availableBalance, Long sourceId) throws ManagerException {
        try {
            Balance blan = new Balance();
            blan.setAvailableBalance(availableBalance);
            blan.setBalance(balance);
            blan.setBalanceType(type.getType());
            blan.setSourceId(sourceId);
            return balanceMapper.insert(blan);
        } catch (Exception e) {
            throw new ManagerException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Balance synchronizedBalance(Long memberId, TypeEnum balanceTypePiggy)
            throws ManagerException {
        //项目资金 不需要同步
        if (balanceTypePiggy == TypeEnum.BALANCE_TYPE_PROJECT) {
            return null;
        }
		if (PropertiesUtil.isDev()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error("同步新浪余额线程睡眠失败", e);
			}
		}
        Balance blance = synThidPay(memberId, balanceTypePiggy,false);
		if (PropertiesUtil.isDev()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error("同步新浪余额线程睡眠失败", e);
			}
		}
        return blance;
    }
    
    public Balance queryFromThirdPay(Long memberId, TypeEnum balanceTypePiggy)throws ManagerException{
    	return synThidPay(memberId, balanceTypePiggy,true);
    } 
    
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Balance rechargeFromThirdPay(long memberID, BigDecimal income, String sourceId, TypeEnum balanceType) throws ManagerException {

        Balance blance = synThidPay(memberID, balanceType,false);
        RechargeLog rechargeLog = rechargeLogMapper.selectByPrimaryKey(Long.valueOf(sourceId));
        //资金流水的备注信息
        Object[] param = {"",""};
        if(rechargeLog!=null){
			if(StringUtil.isNotBlank(rechargeLog.getBankCode())){
				BankCode bankCode =BankCode.getBankCode(rechargeLog.getBankCode());
				if(bankCode!=null){
					param[0]=bankCode.getRemarks();
				}
			}
			if (rechargeLog.getPayMethod() != null) {
				if(TypeEnum.RECHARGELOG_PAY_METHOD_EBANK.getType() == rechargeLog.getPayMethod().intValue()){
					param[1]=TypeEnum.RECHARGELOG_PAY_METHOD_EBANK.getDesc();
				}
				if(TypeEnum.RECHARGELOG_PAY_METHOD_QUICK.getType()== rechargeLog.getPayMethod().intValue()){
					param[1]=TypeEnum.RECHARGELOG_PAY_METHOD_QUICK.getDesc();
				}
			}
		}
        String remark = MessageFormat.format(RemarksEnum.FINCAPITALINOUT_TYPE_RECHARGE.getRemarks(), param);
        //记录充值流水
        insertCapitalInOutLogMapper(memberID, TypeEnum.FINCAPITALINOUT_TYPE_RECHARGE, income, BigDecimal.ZERO, blance.getBalance(), sourceId, remark, balanceType);
        BigDecimal fee = income.multiply(new BigDecimal(Config.poundage));
        if(fee.compareTo(new BigDecimal(Config.minrecharge)) == -1){
        	fee = new BigDecimal(Config.minrecharge);
        }       
        reduceEnterpriseBalance(TypeEnum.FINCAPITALINOUT_TYPE_RECHARGE_POUNDAGE,sourceId, fee);
        return blance;
    }
    /**
     * 资金流水
     * @param memberId
     * @param type
     * @param income
     * @param outlay
     * @param balance
     * @param sourceId
     * @param remarks
     * @param payAccountType
     * @return
     * author: pengyong
     * 下午3:46:49
     */
    private int insertCapitalInOutLogMapper(Long memberId, TypeEnum type, BigDecimal income,
                                            BigDecimal outlay, BigDecimal balance, String sourceId, String remarks, TypeEnum payAccountType) {
        CapitalInOutLog record = new CapitalInOutLog();
        record.setMemberId(memberId);
        record.setIncome(income);
        record.setOutlay(outlay);
        record.setSourceId(sourceId);
        record.setRemark(remarks);
        record.setPayAccountType(payAccountType.getType());
        record.setType(type.getType());
        record.setBalance(balance);
        return capitalInOutLogMapper.insertSelective(record);
    }
    
    /**
     *  同步第三方 支付
     * @param memberId
     * @param balanceTypePiggy
     * @param isNotUpdate  true 不更新， false 更新余额
     * @return
     * @throws ManagerException
     * author: pengyong
     * 下午4:40:22
     */
    private Balance synThidPay(long memberId, TypeEnum balanceTypePiggy,boolean isNotUpdate) throws ManagerException {
        //新浪同步资金
        try {  
        	ResultDto<?> resultDto = getFromSinaPayBalance(memberId);
            AccountBalance module = (AccountBalance)resultDto.getModule();
            if(isNotUpdate){
            	return buildBanlaceObject(memberId, balanceTypePiggy, module);            	
            }           
            Balance blance = queryBalance(memberId, balanceTypePiggy);
            if (blance != null) {
                if (blance.getBalance().compareTo(module.getBalance().getAmount()) != 0 || blance.getAvailableBalance().compareTo(module.getAvailable().getAmount()) != 0) {
                    //更新余额表
                	logger.info("更新余额表  余额= "+module.getBalance().getAmount().toPlainString()+" 可用余额 ="+module.getAvailable().getAmount().toPlainString());
                    Balance keyLock = balanceMapper.selectByPrimaryKeyLock(blance.getId());
                    keyLock.setAvailableBalance(module.getAvailable().getAmount());
                    keyLock.setBalance(module.getBalance().getAmount());                                   
                    balanceMapper.updateBalanceByTypeAndSource(keyLock);
                    return keyLock;
                } else {                   
                    return blance;
                }
            }else{
            	 blance = buildBanlaceObject(memberId, balanceTypePiggy, module);
            	 blance.setCreateTime(DateUtils.getCurrentDateTime());
                 balanceMapper.insertSelective(blance);
             	 logger.info("插入余额表  余额= "+module.getBalance().getAmount().toPlainString()+" 可用余额 ="+module.getAvailable().getAmount().toPlainString());

                 return blance;
            }
        }catch (Exception e){
        	logger.error("查询第三方支付余额发生异常，memberId=" + memberId, e);
            throw  new ManagerException(e);
        }

    }
    /**
     * 从新浪查询余额，存钱罐收益
     * @param memberId
     * @param balanceTypePiggy
     * @return
     * @throws Exception
     * @throws ManagerException
     * author: pengyong
     * 下午4:34:13
     */
	private ResultDto<?> getFromSinaPayBalance(long memberId) throws Exception, ManagerException {
		logger.info("开始查询第三方资金   memberId= "+memberId);		
		ResultDto<?> resultDto = this.sinaPayClient.queryBalance(memberId);           
		if (!resultDto.isSuccess()   || resultDto.getModule() == null){
			logger.error("查询第三方资金异常    memberId= "+memberId+" msg "+resultDto.getErrorMsg());
		    throw  new ManagerException("第三方支付查询余额异常 memberID ="+memberId+":msg "+resultDto.getErrorMsg());
		}
		return resultDto;
	}
    /***
     * 构造对象 thirdblance
     * @param memberId
     * @param balanceTypePiggy
     * @param module  
     */
	private Balance buildBanlaceObject(long memberId, TypeEnum balanceTypePiggy,
			AccountBalance module) {
		 Balance thirdblance = new Balance();
		thirdblance.setAvailableBalance(module.getAvailable().getAmount());
		thirdblance.setBalance(module.getBalance().getAmount());
		thirdblance.setBalanceType(balanceTypePiggy.getType());
		thirdblance.setSourceId(memberId);
		return thirdblance;
	}


    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Balance withdrawFromThirdPay(long memberID, BigDecimal income, String sourceId, TypeEnum balanceType) throws ManagerException {
        Balance blance = synThidPay(memberID, balanceType,false);
        WithdrawLog wLog = withdrawLogMapper.selectByPrimaryKey(Long.valueOf(sourceId));
        // 如果提现记录状态不是成功
        if (StatusEnum.WITHDRAW_STATUS_SUCESS.getStatus() != wLog.getStatus()) {
        	return blance;
        }
        Object[] param = {"",""};  
        if(wLog!=null&&wLog.getBankCardId()!=null){
        	MemberBankCard bankCard = memberBankCardMapper.selectByPrimaryKey(wLog.getBankCardId());
        	if(bankCard!=null){
        		if(StringUtil.isNotBlank(bankCard.getBankCode())){//银行编号
	        		BankCode bankCode = BankCode.getBankCode(bankCard.getBankCode());
	        		if(bankCode!=null){
	        			param[0]=bankCode.getRemarks();
	        		}
        		}
        		if(StringUtil.isNotBlank(bankCard.getCardNumber())){//银行卡号
        			param[1] = StringUtil.maskBankCodeNumber(bankCard.getCardNumber());
        		}
        	}
        }
        
        String remark = MessageFormat.format(RemarksEnum.FINCAPITALINOUT_TYPE_WITHDRAW.getRemarks(), param);
         //记录流水
        insertCapitalInOutLogMapper(memberID, TypeEnum.FINCAPITALINOUT_TYPE_WITHDRAW, BigDecimal.ZERO, income, blance.getBalance(), sourceId, remark, balanceType);
        //	如果有手续费，也要记录流水
         WithdrawLog withdrawNo = this.withdrawLogMapper.selectByPrimaryKey(Long.parseLong(sourceId));
         BigDecimal fee = withdrawNo.getFee();
         if(fee != null && fee.compareTo(BigDecimal.ZERO) > 0){
        	 reduceEnterpriseBalance(TypeEnum.FINCAPITALINOUT_TYPE_WITHDRAW_POUNDAGE,sourceId, fee);
         }          
        
        return blance;
    }
    /**
     *  扣减 平台 在第三方支付费用， 
     * @param sourceId
     * @param blance
     * @param fee
     * @throws ManagerException
     */
	private void reduceEnterpriseBalance(TypeEnum type,String sourceId, BigDecimal fee) throws ManagerException {
		//查询企业余额
		 Balance balance = queryBalance(Long.parseLong(Config.internalMemberId), TypeEnum.BALANCE_TYPE_BASIC);      
		 Balance keyLock = balanceMapper.selectByPrimaryKeyLock(balance.getId());
		 //解冻余额， 总余额 减去  investAmount
		 BigDecimal avabalace = keyLock.getAvailableBalance().subtract(fee);
		 BigDecimal balace = keyLock.getBalance().subtract(fee);             
		 keyLock.setBalance(balace);
		 keyLock.setAvailableBalance(avabalace);             
		 balanceMapper.updateBalanceByTypeAndSource(keyLock);
		 //可用余额，和 总余额     扣去手续费     
		 insertCapitalInOutLogMapper(Long.parseLong(Config.internalMemberId), type, BigDecimal.ZERO, fee, keyLock.getBalance(), sourceId, "", TypeEnum.BALANCE_TYPE_BASIC);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Balance frozenMemberBalance(BigDecimal amount, Long sourceId)
			throws ManagerException {
		  Balance balance = updateBalanceByType(TypeEnum.BALANCE_TYPE_PIGGY, amount, sourceId, BalanceAction.balance_Available_subtract);
		return balance;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Balance unFrozenMemberBalance(BigDecimal amount, Long sourceId)throws ManagerException {
		 Balance balance = updateBalanceByType(TypeEnum.BALANCE_TYPE_PIGGY, amount, sourceId, BalanceAction.balance_Available_add);
		return balance;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void synThirdPayEaring(long memberId) throws ManagerException {
		try {
			ResultDto<?> sinaPayBalance = getFromSinaPayBalance(memberId);
			AccountBalance module = (AccountBalance) sinaPayBalance.getModule();
            //如果存钱罐余额==0， 总收益也==0，说明此用户不是活跃用户 无需同步收益
//            if (module != null && BigDecimal.ZERO.compareTo(module.getBalance().getAmount()) == 0
//                    && BigDecimal.ZERO.compareTo(module.getBonus().getAmount()) == 0
//                    ) {
//                return;
//            }
            // 更新余额
			Balance blance = queryBalance(memberId, TypeEnum.BALANCE_TYPE_PIGGY);
			if (blance != null) {
				if (blance.getBalance().compareTo(
						module.getBalance().getAmount()) != 0
						|| blance.getAvailableBalance().compareTo(
								module.getAvailable().getAmount()) != 0) {
					// 更新余额表
                    logger.info("id={},余额={}, 总收益={},昨日收益={}",memberId,module.getBalance().getAmount().toPlainString(), module.getBonus().getAmount().toPlainString(), module.getYestDayBonus().getAmount().toPlainString());
					Balance keyLock = balanceMapper	.selectByPrimaryKeyLock(blance.getId());
					keyLock.setAvailableBalance(module.getAvailable().getAmount());
					keyLock.setBalance(module.getBalance().getAmount());
					balanceMapper.updateBalanceByTypeAndSource(keyLock);
				}
			}
			// 更新存钱罐收益
			blance = queryBalance(memberId,	TypeEnum.BALANCE_TYPE_MEMBER_TOTAL_PIGGY);
			if (blance != null) {
				// 更新余额表
                logger.info("id={},更新余额表  余额= {},可用余额 ={}",memberId, module.getBonus().getAmount().toPlainString(), module.getBonus().getAmount().toPlainString());
                Balance keyLock = balanceMapper.selectByPrimaryKeyLock(blance.getId());
				keyLock.setAvailableBalance(module.getBonus().getAmount());
				keyLock.setBalance(module.getBonus().getAmount());
				balanceMapper.updateBalanceByTypeAndSource(keyLock);
				// 记录资金流水,				
//				if(logger.isDebugEnabled()){
//					module.setYestDayBonus(new Money("0.01"));
//				}				
				if(module.getYestDayBonus().getAmount().compareTo(BigDecimal.ZERO) == 1){
                    int i = sinaPayEaringlog(memberId, module.getYestDayBonus().getAmount(), module.getBalance().getAmount(),DateUtils.getYesterDay());
                    if (i  > 0){
                        MessageClient.sendMsgForSinaWalletIncome(DateUtils.getYesterDay(), module.getYestDayBonus().getAmount(), memberId);
                    }
                }
			} else {
				//初始化新浪存钱余额
				insertBalance(TypeEnum.BALANCE_TYPE_MEMBER_TOTAL_PIGGY, BigDecimal.ZERO, memberId);				
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	/**
	 * 
	 * @Description:同步存钱罐收益
	 * @param memberId
	 * @param date
	 * @throws ManagerException
	 * @throws Exception
	 * @author: chaisen
	 * @return 
	 * @time:2016年4月11日 上午10:24:47
	 */
	@Override
	public void synThirdPayEaringByAdmin(long memberId,Date startDate,Date endDate) throws ManagerException{
		try {
			//Date start = DateUtils.getDateTimeFromString(DateUtils.getDateStrFromDate(date)+" 00:00:00");
			//Date end = DateUtils.getEndTime(date);
			ResultDto<AccountDetail> detail = sinaPayClient.queryAccountDetails(memberId, startDate, endDate);
			if(detail!=null&&detail.getModule()!=null){
				List<StatementEntry> list=detail.getModule().getDetailList();
				//发生额
				BigDecimal amountBig=BigDecimal.ZERO;
				//余额
				BigDecimal balanceBig=BigDecimal.ZERO;
				if(Collections3.isEmpty(list)){
					return ;
				}
				for(StatementEntry entry:list){
					String monthDay=entry.getSummary();
					if(StringUtil.isNotBlank(monthDay)&&monthDay.length()>4){
						amountBig=new BigDecimal(entry.getAmount().toString());
						balanceBig=new BigDecimal(entry.getBalance().toString());
						if(amountBig.compareTo(BigDecimal.ZERO) == 1){
							int i = sinaPayEaringlog(memberId, amountBig, balanceBig,DateUtils.toDate(DateUtils.getYear(DateUtils.getCurrentDate())+monthDay.substring(0, 4),DateUtils.DATE_FMT_0));
						}
					}
				}
					
			}
				
		} catch (Exception e) {
			logger.error("同步存钱罐收益发生异常，memberId=" + memberId+",startDate="+startDate+",endDate="+endDate, e);
		}
		
		
	}
	
	
	
	/**
	 * 存钱罐收益，记录流水 (如果记录存在， 收益不一致，则更新， 如果记录不存在，则新加)
	 * @param memberId
	 * @param income
	 * @param balance
	 * @return
	 * @throws ManagerException
	 * author: pengyong
	 * 下午7:51:55
	 */
	private int sinaPayEaringlog(Long memberId, BigDecimal income,
			BigDecimal balance,Date date) throws ManagerException {
		Date beginDate = DateUtils.zerolizedTime(date);
		Date endDate = DateUtils.getEndTime(date);
		
		CapitalInOutLog capitalInOutLog = capitalInOutLogMapper.selectByHappenTime(memberId, TypeEnum.BALANCE_TYPE_PIGGY.getType(),
				TypeEnum.FINCAPITALINOUT_TYPE_THIRDPAY.getType(), beginDate, endDate);
		//资金流水备注
		String remark = MessageFormat.format(RemarksEnum.FINCAPITALINOUT_TYPE_THIRDPAY.getRemarks(),
				DateUtils.formatDatetoString(date, DateUtils.DATE_FMT_3));
		if(capitalInOutLog == null){
			capitalInOutLog = new CapitalInOutLog();
			capitalInOutLog.setMemberId(memberId);
			capitalInOutLog.setIncome(income);
			capitalInOutLog.setOutlay(BigDecimal.ZERO);
			capitalInOutLog.setSourceId("");
			capitalInOutLog.setRemark(remark);
			capitalInOutLog.setPayAccountType(TypeEnum.BALANCE_TYPE_PIGGY.getType());
			capitalInOutLog.setType(TypeEnum.FINCAPITALINOUT_TYPE_THIRDPAY.getType());
			capitalInOutLog.setBalance(balance);
			Date happenTime = DateUtils.getDateTimeFromString(DateUtils.getDateStrFromDate(date)+" 23:59:59");
			capitalInOutLog.setHappenTime(happenTime);
			return this.capitalInOutLogMapper.insertSelective(capitalInOutLog);
		}
		// 收益不一致 ，更新流水表
		if (income.compareTo(capitalInOutLog.getIncome()) != 0) {
			capitalInOutLog.setBalance(balance);
			capitalInOutLog.setIncome(income);
			return this.capitalInOutLogMapper.updateByPrimaryKeySelective(capitalInOutLog);
		}
		return 0;

	}

	@Override
	public Balance queryBalanceLocked(Long sourceId, TypeEnum type)
			throws ManagerException {
		BalanceQuery balanceQuery = new BalanceQuery();
    	balanceQuery.setBalanceType(type.getType());
    	balanceQuery.setSourceId(sourceId);
        Balance result = balanceMapper.selectByQuery(balanceQuery);
        Balance keyLock = balanceMapper.selectByPrimaryKeyLock(result.getId());
		return keyLock;
	}

    @Override
    public BigDecimal getMemberTotalAsset(Long memberId) throws ManagerException {
        BigDecimal collectedAmount = transactionInterestMapper.queryCollectedAmount(memberId);
        BigDecimal totalAsset = BigDecimal.ZERO;
        if(collectedAmount == null){
            collectedAmount = BigDecimal.ZERO;
        }
        totalAsset = totalAsset.add(collectedAmount);
        Balance balance = null;
        try {
            balance = synThidPay(memberId, TypeEnum.BALANCE_TYPE_PIGGY,true);
            if (balance!=null && balance.getBalance()!=null)
                totalAsset = totalAsset.add(balance.getBalance());
        } catch (Exception e) {
           throw  new ManagerException(e);
        }

         return totalAsset;
    }

    public Boolean  isZeroMemberTotalAsset(Long memberId)throws ManagerException{
        Boolean  result = false;
        BigDecimal memberTotalAsset = getMemberTotalAsset(memberId);
        if (logger.isDebugEnabled()){
            logger.debug("会员id={},资产统计 总额={}",memberId,memberTotalAsset);
        }
        if (memberTotalAsset.compareTo(BigDecimal.ZERO)==0)
            result = true;
        return result;
    }

	@Override
	public BigDecimal getPaltformTotalInvest() throws ManagerException {
		Balance balance = queryBalance(-1L, TypeEnum.BALANCE_TYPE_PLATFORM_TOTAL_INVEST);
		if(balance != null){
			return balance.getAvailableBalance();
		}
		return BigDecimal.ZERO;
	}

	@Override
	public void refundWithdrawFee(Long memberId, Long sourceId,String remark) throws ManagerException {
		BigDecimal gainPopularity = new BigDecimal(Config.freeWithdrawAmount);
		//调用赠送人气值接口
//		Balance balance = increaseBalance(TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY, gainPopularity, memberId);
//		//记录人气值资金流水
//		popularityInOutLogManager.insert(
//				memberId, 
//				TypeEnum.FIN_POPULARITY_TYPE_WITHDRAW, 
//				gainPopularity, 
//				null, 
//				balance.getAvailableBalance(), 
//				sourceId, 
//				remark
//				);
		
		transactionManager.givePopularity(sourceId, memberId, TypeEnum.FIN_POPULARITY_TYPE_WITHDRAW,  gainPopularity, remark);
		
	}

	@Override
	public Balance frozenPopularityBalance(Long memberId,Integer withdrawFree)
			throws ManagerException {
		BigDecimal freeWithdrawAmount = new BigDecimal(withdrawFree);
		Balance balance = updateBalanceByType(TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY, freeWithdrawAmount, memberId, BalanceAction.balance_Available_subtract);
		return balance;
	}

    @Override
    public int updateByIdAndTypeForLotty(BigDecimal point, Long sourceId)  throws ManagerException{
        int i = this.balanceMapper.updateByIdAndTypeForLotty(point, sourceId, TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY.getType());
        if (i<1){
            logger.error("可用余额不够, amount={}, sourceId={}", point, sourceId);
            throw new ManagerException("可用余额不够");
        }
        return i;
    }

	@Override
	public Balance unFrozenPopularityBalance(Long withdrawLogId, Long memberId)
			throws ManagerException {
		WithdrawLog withdrawLog = withdrawLogMapper.selectByPrimaryKey(withdrawLogId);
		if(withdrawLog != null){
			if(withdrawLog.getWithdrawFee() > 0){
				BigDecimal freeWithdrawAmount = new BigDecimal(withdrawLog.getWithdrawFee());
				Balance balance = updateBalanceByType(TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY, freeWithdrawAmount, memberId, BalanceAction.balance_Available_add);
				return balance;
			}
		}
		return null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Balance reduceWithdrawalsFee(Long memberId, Long sourceId,Integer withdrawalsFee) throws ManagerException {
		try{
			BigDecimal freeWithdrawAmount = new BigDecimal(withdrawalsFee);
			Balance balance = updateBalanceByType(TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY, freeWithdrawAmount, memberId, BalanceAction.balance_subtract);
			popularityInOutLogManager.insert(memberId, TypeEnum.FIN_POPULARITY_TYPE_WITHDRAW, null, freeWithdrawAmount, balance.getAvailableBalance(), sourceId, RemarksEnum.WITHDRAW_POPULARITY_BALANCE.getRemarks());
			return balance;
		}catch(Exception e){
			throw new ManagerException(e);
		}
	}
	
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Balance reduceForShopConsume(Long memberId, Long sourceId,Integer amount) throws ManagerException {
		try{
			BigDecimal popularSub = new BigDecimal(amount);
			Balance balance = updateBalanceByType(TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY, popularSub, memberId, BalanceAction.balance_subtract_Available_subtract);
			popularityInOutLogManager.insert(memberId, TypeEnum.FIN_POPULARITY_TYPE_SHOP_CONSUME, null, popularSub, balance.getAvailableBalance(), sourceId, RemarksEnum.WITHDRAW_POPULARITY_SHOP_CONSUME.getRemarks());
			return balance;
		}catch(Exception e){
			throw new ManagerException(e);
		}
	}
	

    @Override
    public int incrExchangePlatformTotalPoint(BigDecimal outLay) {
        Balance balance = new Balance();
        balance.setBalance(outLay);
        balance.setBalanceType(TypeEnum.BALANCE_TYPE_PLATFORM_TOTAL_RECHARGE.getType());
        balance.setSourceId(-1L);
        int i = this.balanceMapper.updatePlatformTotalPoint(balance);
        return i;
    }

    @Override
    public int incrGivePlatformTotalPoint(BigDecimal outLay) {
   	   /* 
   	    * 2017-03-14 在多线程环境下运行，该数据被共享使用，会出现锁等待
   	    * Balance balance = new Balance();
        balance.setBalance(outLay);
        balance.setBalanceType(TypeEnum.BALANCE_TYPE_PLATFORM_TOTAL_GIVE.getType());
        balance.setSourceId(-1L);
        int i = this.balanceMapper.updatePlatformTotalPoint(balance);*/
        return 1;
    }
}