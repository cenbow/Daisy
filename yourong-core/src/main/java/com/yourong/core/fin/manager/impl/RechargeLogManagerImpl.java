package com.yourong.core.fin.manager.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.StringUtil;
import com.yourong.core.fin.dao.RechargeLogMapper;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.RechargeLogManager;
import com.yourong.core.fin.model.RechargeLog;

@Component
public class RechargeLogManagerImpl implements RechargeLogManager {
    @Autowired
    private RechargeLogMapper rechargeLogMapper;
    @Autowired
    private BalanceManager balanceManager;
    
    private Logger logger = LoggerFactory.getLogger(RechargeLogManagerImpl.class);

    @Override
    public int insertSelective(RechargeLog record) throws ManagerException {
        try {
            return rechargeLogMapper.insertSelective(record);
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }

    @Override
    public Page<RechargeLog> findByPage(Page<RechargeLog> pageRequest,
                                        Map<String, Object> map) throws ManagerException {
        try {

            map.put("startRow", pageRequest.getiDisplayStart());
            map.put("pageSize", pageRequest.getiDisplayLength());
            int totalCount = rechargeLogMapper.selectForPaginTotalCount(map);
            pageRequest.setiTotalDisplayRecords(totalCount);
            pageRequest.setiTotalRecords(totalCount);
            List<RechargeLog> selectForPagin = rechargeLogMapper.selectForPagin(map);
            pageRequest.setData(selectForPagin);
            return pageRequest;
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }


    @Override
    public int updateStateByOutTradeNo(BigDecimal amount, int status,String rechargeNo, String outTradeNo)
            throws ManagerException {
        try {
            return rechargeLogMapper.updateStateByOutTradeNo(amount, status,rechargeNo, outTradeNo);
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }

    }

    @Override
    public RechargeLog getRechargeLogByTradeNo(String tradeNo) throws ManagerException {
        try {
            return rechargeLogMapper.selectRechargeLogByTradeNo(tradeNo );
        } catch (Exception ex) {
            throw new ManagerException(ex);
        }
    }

    @Override
    public int updateStateByID(long id, int status,int eqStatus, String remarks) throws ManagerException {
        try {
            return  rechargeLogMapper.updateStateByID(id,status,eqStatus,remarks);
        }catch (Exception ex){
            throw new ManagerException(ex);
        }

    }

	@Override
	public int countRecharge(long memberId) throws ManagerException {
		try {
			return rechargeLogMapper.countRecharge(memberId);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	public BigDecimal totalRecharge(long memberId) throws ManagerException {
		try {
			BigDecimal totalRecharge = rechargeLogMapper.totalRecharge(memberId);
			if(totalRecharge == null){
				totalRecharge = BigDecimal.ZERO;
			}
			return totalRecharge;
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	public int selectForPaginTotalCount(Map<String, Object> map) throws ManagerException {
		try {
			return rechargeLogMapper.selectForPaginTotalCount(map);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}		
	}

	@Override
	public List<RechargeLog> selectForPagin(Map<String, Object> map) throws ManagerException{
		try {
			return rechargeLogMapper.selectForPagin(map);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}	
	}
	
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public boolean rechargeSuccess(String rechargeNo,String outTradeNo, String memo, Long memberBankCardId, String bankCode, Integer payMethod) throws Exception {
		try {
			RechargeLog rechargeLog = this.getRechargeLogByTradeNo(rechargeNo);
			if(rechargeLog == null || rechargeLog.getStatus() == StatusEnum.RECHARGE_STATUS_SUCESS.getStatus()){
				return false;
			}		
			boolean result = processRechage(rechargeNo, outTradeNo, memo, StatusEnum.RECHARGE_STATUS_SUCESS.getStatus(), memberBankCardId, bankCode, payMethod);
			return result;
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public boolean rechargeFailed(String rechargeNo, String outTradeNo, String errorMsg, Long memberBankCardId, String bankCode, Integer payMethod) throws Exception{
		try {
			RechargeLog rechargeLog = this.getRechargeLogByTradeNo(rechargeNo);
			if(rechargeLog == null || rechargeLog.getStatus() == StatusEnum.RECHARGE_STATUS_FAIL.getStatus()){
				return false;
			}
			return processRechage(rechargeNo, outTradeNo, errorMsg, StatusEnum.RECHARGE_STATUS_FAIL.getStatus(), memberBankCardId, bankCode, payMethod);
		} catch (Exception e) {
			throw e;
		}
	}
	
	// 充值处理
	private boolean processRechage(String rechargeNo, String outTradeNo, String memo, int status, Long memberBankCardId, String bankCode, Integer payMethod) throws ManagerException {
		boolean result = false;
		RechargeLog rechargeLogUnLock = this.getRechargeLogByTradeNo(rechargeNo);
		if (rechargeLogUnLock == null) {
			return result;
		}
		RechargeLog rechargeLogForLock = this.getRechargeLogByTradeNo(rechargeNo);
		if(rechargeLogForLock != null) {
			if (StringUtil.isNotBlank(memo) && memo.length() > 255) {
				memo = memo.substring(0, 254);
			}
			rechargeLogForLock.setOuterRechargeNo(outTradeNo);
			rechargeLogForLock.setStatus(status);
			rechargeLogForLock.setRemarks(memo);
			rechargeLogForLock.setBankCardId(memberBankCardId);
			rechargeLogForLock.setBankCode(bankCode);
			if (payMethod != null) {
				rechargeLogForLock.setPayMethod(payMethod);
			}
			int rows = rechargeLogMapper.updateByPrimaryKeySelective(rechargeLogForLock);
			if (rows > 0) {
				result = true;
				// 调用资金余额同步方法
				if (status == StatusEnum.RECHARGE_STATUS_SUCESS.getStatus()) {
					balanceManager.rechargeFromThirdPay(
							rechargeLogForLock.getMemberId(), 
							rechargeLogForLock.getAmount(), 
							this.getRechargeLogByTradeNo(rechargeNo).getId().toString(),
							TypeEnum.BALANCE_TYPE_PIGGY
							);
					//更新redis中总充值金额和笔数
					RedisMemberClient.addRechargeSuccessCount(rechargeLogForLock.getMemberId(), 1);
					RedisMemberClient.addRechargeSuccessAmount(rechargeLogForLock.getMemberId(),rechargeLogForLock.getAmount());
				}
			}
		}
		return result;
	}

	@Override
	public List<RechargeLog> selectRechargeByMap(Map<String, Object> map) throws ManagerException {
		try {
			return rechargeLogMapper.selectRechargeByMap(map);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	public Page<RechargeLog> queryLogByPage(Page<RechargeLog> pageRequest,
			Map<String, Object> map) throws ManagerException {
			Page<RechargeLog> page = new Page<RechargeLog>();
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			List<RechargeLog> rechargeLogList=  rechargeLogMapper.queryLogByPage(map);
			int totalCount = rechargeLogMapper.queryLogByPageCount(map);
			page.setData(rechargeLogList);
			page.setiTotalDisplayRecords(totalCount);
			page.setiTotalRecords(totalCount);
			return page;
		} catch (Exception ex) {
            throw new ManagerException(ex);
        }
	}

	@Override
	public RechargeLog queryAppFirstRechargeAmount(Long memberId, Date startDate,
			Date endDate) throws ManagerException {
		try {
			return rechargeLogMapper.queryAppFirstRechargeAmount(memberId,  startDate, endDate);
		} catch (Exception ex) {
            throw new ManagerException(ex);
        }
	}

	@Override
	public RechargeLog selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return rechargeLogMapper.selectByPrimaryKey(id);
		} catch (Exception ex) {
            throw new ManagerException(ex);
        }
	}

	/**
	 * 查询在处理中的充值记录
	 */
	@Override
	public List<RechargeLog> selectSynchronizedRecharge(Integer status, String rechargeNo, String startTime, String endTime) throws ManagerException{
		try {
			return rechargeLogMapper.selectSynchronizedRecharge(status, rechargeNo, startTime, endTime);
		} catch (Exception ex) {
            throw new ManagerException(ex);
        }
	}
	
	@Override
	public RechargeLog selectByPrimaryKeyForLock(Long id) throws ManagerException {
		try {
			return rechargeLogMapper.selectByPrimaryKeyForLock(id);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
}
