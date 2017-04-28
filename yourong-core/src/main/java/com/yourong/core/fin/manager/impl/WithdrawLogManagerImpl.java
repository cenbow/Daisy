package com.yourong.core.fin.manager.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import rop.thirdparty.com.google.common.collect.Lists;

import com.google.common.collect.Maps;
import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.fin.dao.WithdrawLogMapper;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.manager.WithdrawLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.WithdrawLog;
import com.yourong.core.sys.manager.SysOperateInfoManager;
import com.yourong.core.sys.manager.SysUserManager;
import com.yourong.core.sys.model.SysOperateInfo;
import com.yourong.core.sys.model.SysUser;
import com.yourong.core.uc.dao.MemberBankCardMapper;

@Component
public class WithdrawLogManagerImpl implements WithdrawLogManager {
	@Autowired
	private WithdrawLogMapper withdrawLogMapper;
	@Autowired
	private BalanceManager balanceManager;
	@Autowired
	private MemberBankCardMapper memberBankCardMapper;
	@Autowired
	private SinaPayClient sinaPayClient;
	@Autowired
	private PopularityInOutLogManager popularityInOutLogManager;
	
	@Autowired
	private SysOperateInfoManager sysOperateInfoManager;
	
	@Autowired
	private SysUserManager sysUserManager;

	private Logger logger = LoggerFactory.getLogger(WithdrawLogManagerImpl.class);

	@Override
	public int insertSelective(WithdrawLog record) throws ManagerException {
		try {
			if (StringUtil.isBlank(record.getWithdrawNo())) {
				record.setWithdrawNo(SerialNumberUtil.generateWithdrawNo(record.getMemberId()));
			}
			record.setWithdrawTime(DateUtils.getCurrentDate());
			record.setStatus(StatusEnum.WITHDRAW_STATUS_PROESS.getStatus());
			return withdrawLogMapper.insertSelective(record);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	public WithdrawLog selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return withdrawLogMapper.selectByPrimaryKey(id);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	public WithdrawLog selectByPrimaryKeyForLock(Long id) throws ManagerException {
		try {
			return withdrawLogMapper.selectByPrimaryKeyForLock(id);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	public void updateProssTimeById(Long id) throws ManagerException {
		try {
			withdrawLogMapper.updateProssTimeById(id);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	public int updateByPrimaryKey(WithdrawLog record) throws ManagerException {
		try {
			return withdrawLogMapper.updateByPrimaryKey(record);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	public int updateBywithdrawNo(String withdrawNo, int status)
			throws ManagerException {
		try {
			return withdrawLogMapper.updateBywithdrawNo(withdrawNo, status);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	public Page<WithdrawLog> findByPage(Page<WithdrawLog> pageRequest,
			Map<String, Object> map) throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			int totalCount = withdrawLogMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<WithdrawLog> selectForPagin = withdrawLogMapper.selectForPagin(map);
			if(Collections3.isNotEmpty(selectForPagin)){
				selectForPagin=selectWithDrawInfo(selectForPagin);
			}
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	private List<WithdrawLog> selectWithDrawInfo(List<WithdrawLog> selectForPagin) throws ManagerException {
		List<WithdrawLog> list=Lists.newArrayList();
		for(WithdrawLog log:selectForPagin){
			Map<String, Object> map = Maps.newHashMap();
			map.put("sourceId", log.getId());
			map.put("operateTableType", TypeEnum.OPERATE_TYPE_WITHDRAW.getType());
			SysOperateInfo sysOperateInfo=sysOperateInfoManager.selectOperateBySourceId(map);
			if(sysOperateInfo!=null){
				SysUser sysUser=sysUserManager.selectByPrimaryKey(sysOperateInfo.getOperateId());
				if(sysUser!=null){
					log.setOperateName(sysUser.getLoginName());
				}
			}
			list.add(log);
		}
		return list;
	}

	@Override
	public int updateStateByParimarkey(Long id, int status, int eqStatus,
			String notice) throws ManagerException {
		try {
			return withdrawLogMapper.updateStateByParimarkey(id, status,eqStatus, notice);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}

	}

	@Override
	public WithdrawLog getWithdrawLogByTradeNo(String tradeNo)
			throws ManagerException {
		try {
			return withdrawLogMapper.selectByWithdrawNo(tradeNo);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	public WithdrawLog selectByOuterWithdrawNo(String tradeNo)
			throws ManagerException {
		try {
			return withdrawLogMapper.selectByOuterWithdrawNo(tradeNo);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	public int updateStateByID(Long id, int status, int eqStatus,
			String withdrawNo, String notice, BigDecimal withAmout)
			throws ManagerException {
		try {
			return withdrawLogMapper.updateStateByID(id, status, eqStatus,withdrawNo, notice, withAmout);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	public int countWithDraw(long memberId) throws ManagerException {
		try {
			return withdrawLogMapper.countWithdrawNo(memberId);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	public BigDecimal totalWithDraw(long memberId) throws ManagerException {
		try {
			BigDecimal totalWithDraw = withdrawLogMapper.totalWithDraw(memberId);
			if (totalWithDraw == null) {
				totalWithDraw = BigDecimal.ZERO;
			}
			return totalWithDraw;
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	public List<WithdrawLog> selectForPagin(Map<String, Object> map)
			throws ManagerException {
		try {
			return this.withdrawLogMapper.selectForPagin(map);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	public int selectForPaginTotalCount(Map<String, Object> map)
			throws ManagerException {
		try {
			return this.withdrawLogMapper.selectForPaginTotalCount(map);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	public List<WithdrawLog> selectSynchronizedWithdraws()
			throws ManagerException {
		try {
			return this.withdrawLogMapper.selectSynchronizedWithdraws();
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public boolean withdrawProcess(String withdrawNo, String memo, Long bankCardId)
			throws ManagerException {
		try {
			boolean result = processWithdraw(withdrawNo, memo,StatusEnum.WITHDRAW_STATUS_PAYING.getStatus(), bankCardId);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public boolean withdrawSuccess(String withdrawNo, String memo, Long bankCardId)
			throws ManagerException {
		try {
			boolean result = processWithdraw(withdrawNo, memo,StatusEnum.WITHDRAW_STATUS_SUCESS.getStatus(), bankCardId);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public boolean withdrawFailed(String withdrawNo, String errorMsg, Long bankCardId)
			throws ManagerException {
		try {
			return processWithdraw(withdrawNo, errorMsg, StatusEnum.WITHDRAW_STATUS_FAIL.getStatus(), bankCardId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}

	}
	// 提现处理
	private boolean processWithdraw(String withdrawNo, String notice, int status, Long bankCardId) throws ManagerException {
		boolean result = false;
		WithdrawLog withdrawLogUnLock = this.getWithdrawLogByTradeNo(withdrawNo);
		if (withdrawLogUnLock == null) {
			return result;
		}
		WithdrawLog withdrawLogForLock = this.selectByPrimaryKeyForLock(withdrawLogUnLock.getId());
		if (withdrawLogForLock != null) {
			if (StringUtil.isNotBlank(notice) && notice.length() > 255) {
				notice = notice.substring(0, 254);
			}
			withdrawLogForLock.setStatus(status);
			withdrawLogForLock.setNotice(notice);
			withdrawLogForLock.setBankCardId(bankCardId);
			// 提现失败
			if (status == StatusEnum.WITHDRAW_STATUS_FAIL.getStatus()) {
				// 提现失败，到账金额为0;
				withdrawLogForLock.setArrivedAmount(BigDecimal.ZERO);
				// 提现失败，解冻余额
				// balanceManager.unFrozenMemberBalance(withdrawLog.getWithdrawAmount(),
				// withdrawLog.getMemberId());
			}
			// 提现成功
			if (status == StatusEnum.WITHDRAW_STATUS_SUCESS.getStatus()) {
				// 提现成功，到账金额为=充值金额;
				withdrawLogForLock.setArrivedAmount(withdrawLogForLock.getWithdrawAmount());
			}
			int rows = withdrawLogMapper.updateByPrimaryKeySelective(withdrawLogForLock);
			if (rows > 0) {
				result = true;
				try {
					// 除了提现状态为处理中，都需同步余额
					if (status != StatusEnum.WITHDRAW_STATUS_PROESS.getStatus()) {
						balanceManager.withdrawFromThirdPay(withdrawLogForLock.getMemberId(), withdrawLogForLock.getWithdrawAmount(),
								withdrawLogForLock.getId().toString(), TypeEnum.BALANCE_TYPE_PIGGY);
					}
				} catch (Exception ex) {
					logger.error("提现同步存钱罐余额异常,memberId={},withdrawStatus={}", withdrawLogForLock.getMemberId(), status, ex);
				}
				
				// 提现成功
				if (status == StatusEnum.WITHDRAW_STATUS_SUCESS.getStatus()) {
					// 如果扣除人气值>0,扣除提现手续费人气值
					if (withdrawLogForLock.getWithdrawFee() > 0) {
						popularityInOutLogManager.reduceWithdrawalsFee(
								withdrawLogForLock.getMemberId(), withdrawLogForLock.getId(),
								withdrawLogForLock.getWithdrawFee());
					}
//					balanceManager.withdrawFromThirdPay(withdrawLogForLock.getMemberId(), withdrawLogForLock.getWithdrawAmount(),
//							withdrawLogForLock.getId().toString(), TypeEnum.BALANCE_TYPE_PIGGY);
					// 更新redis中总提现额和总提现笔数
					RedisMemberClient.addWithdrawSuccessCount(withdrawLogForLock.getMemberId(), 1);
					RedisMemberClient.addWithdrawSuccessAmount(withdrawLogForLock.getMemberId(), withdrawLogForLock.getWithdrawAmount());
				} else if (status == StatusEnum.WITHDRAW_STATUS_FAIL.getStatus()) {
					// 提现失败，同步存钱罐余额
					withdrawErrorSysnchronzedBalance(withdrawLogForLock);
					// 解冻提现手续费人气值
					balanceManager.unFrozenPopularityBalance(withdrawLogForLock.getId(), withdrawLogForLock.getMemberId());
				}
			}
		}
		return result;
	}

	public ResultDO<WithdrawLog> cancelWithdraw(Long id, int status) {
		ResultDO<WithdrawLog> logDo = new ResultDO<WithdrawLog>();
		int result = 0;
		logDo.setSuccess(false);
		try {
			WithdrawLog withdrawLog = withdrawLogMapper.selectByPrimaryKey(id);

			// 判断该提现流水是否已經取消
			if (withdrawLog.getStatus().equals(
					StatusEnum.WITHDRAW_STATUS_CANCEL.getStatus())) {
				logDo.setResultCode(ResultCode.BANLANCE_WITHDRAW_STATUS_CANCEL_ERROR);
				return logDo;
			}

			// 判断该提现流水是否可以取消
			if (withdrawLog.getStatus().intValue() != StatusEnum.WITHDRAW_STATUS_PROESS
					.getStatus()
					&& withdrawLog.getStatus().intValue() != StatusEnum.WITHDRAW_STATUS_CANCEL
							.getStatus()) {
				logDo.setResultCode(ResultCode.BANLANCE_WITHDRAW_STATUS_NOT_PROCESS_ERROR);
				return logDo;
			}

			// 取消提现
			result = withdrawLogMapper.updateStateByParimarkey(id, status,
					StatusEnum.WITHDRAW_STATUS_PROESS.getStatus(),
					RemarksEnum.WITHDRAW_USER_CANCEL.getRemarks());
			if (result == 0) {
				logDo.setResultCode(ResultCode.BANLANCE_WITHDRAW_CANCEL_ERROR);
				return logDo;
			}

			// 提现取消，同步新浪余额
			Balance balance = balanceManager.synchronizedBalance(
					withdrawLog.getMemberId(), TypeEnum.BALANCE_TYPE_PIGGY);
			
			balanceManager.unFrozenPopularityBalance(withdrawLog.getId(),
					withdrawLog.getMemberId());

			logDo.setSuccess(true);

		} catch (Exception e) {
			logger.error("用户取消提现失败，id={},status={}", id, status, e);
		}
		return logDo;
	}

	private void withdrawErrorSysnchronzedBalance(WithdrawLog withdrawLog) {
		// 同步查询存钱罐
		try {
			balanceManager.synchronizedBalance(withdrawLog.getMemberId(),
					TypeEnum.BALANCE_TYPE_PIGGY);
		} catch (Exception e) {
			// 同步存钱罐，如果异常，不抛出异常，不影响提现流程进行
			logger.error("同步存钱罐异常 id = %s" + withdrawLog.getMemberId(), e);
		}
	}

	@Override
	public List<WithdrawLog> selectForPaginWeb(Map<String, Object> map)
			throws ManagerException {
		try {
			return this.withdrawLogMapper.selectForPaginWeb(map);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	public int selectForPaginTotalCountWeb(Map<String, Object> map)
			throws ManagerException {
		try {
			return this.withdrawLogMapper.selectForPaginTotalCountWeb(map);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	public int reqSubmitWithDraw(WithdrawLog record) throws ManagerException {
		// 保存提现记录
		int i = insertSelective(record);
		if (i > 0) {
			// 冻结余额
//			balanceManager.frozenMemberBalance(record.getWithdrawAmount(), record.getMemberId());
			// 冻结人气值
			balanceManager.frozenPopularityBalance(record.getMemberId(), record.getWithdrawFee());

			// 调用扣减人气值方法
			// 扣减人气值
			// Balance balance =
			// balanceManager.reducePopularityBalance(freeWithdrawAmount,
			// record.getMemberId());
			// // 记录人气值资金流水
			// popularityInOutLogManager.insert(record.getMemberId(),
			// TypeEnum.FIN_POPULARITY_TYPE_WITHDRAW, null,
			// freeWithdrawAmount, balance.getAvailableBalance(),
			// record.getId(),
			// RemarksEnum.WITHDRAW_POPULARITY_BALANCE.getRemarks());

			// popularityInOutLogManager.reducePopularity(record.getId(),record.getMemberId(),
			// TypeEnum.FIN_POPULARITY_TYPE_WITHDRAW,freeWithdrawAmount,RemarksEnum.WITHDRAW_POPULARITY_BALANCE.getRemarks());
		}
		return i;
	}

	@Override
	public boolean cardIsWithDrawIng(Long memberID, Long cardID)
			throws ManagerException {
		Integer integer = this.withdrawLogMapper.cardIsWithDrawIng(memberID,
				cardID);
		if (integer != null && integer == 1) {
			return true;
		}
		return false;
	}

	@Override
	public int countWithDrawFree(Long memberId,String withdrawTime) throws ManagerException {
		try {
			return withdrawLogMapper.countWithDrawFree(memberId,withdrawTime,DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_6));
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

}
