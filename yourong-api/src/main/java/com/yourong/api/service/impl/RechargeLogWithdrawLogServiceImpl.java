package com.yourong.api.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yourong.api.dto.RechargeLogDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.dto.WithdrawLogDto;
import com.yourong.api.service.RechargeLogWithdrawLogService;
import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.RechargeLogManager;
import com.yourong.core.fin.manager.WithdrawLogManager;
import com.yourong.core.fin.model.RechargeLog;
import com.yourong.core.fin.model.WithdrawLog;
import com.yourong.core.uc.manager.MemberBankCardManager;
import com.yourong.core.uc.model.MemberBankCard;

@Service
public class RechargeLogWithdrawLogServiceImpl implements
		RechargeLogWithdrawLogService {

	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RechargeLogManager rechargeLogManager;

	@Autowired
	private WithdrawLogManager withdrawLogManager;

	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private MemberBankCardManager memberBankCardManager;

	@Autowired
	private SinaPayClient sinaPayClient;

	@Override
	public int insertSelective(RechargeLog record) {
		try {
			return rechargeLogManager.insertSelective(record);
		} catch (ManagerException e) {
			logger.error("插入充值记录recorder=" + record, e);
			return 0;
		}
	}

	@Override
	public int updateStateByOutTradeNo(BigDecimal amount, int status,
			String rechargeNo, String outTradeNo) {
		try {
			return rechargeLogManager.updateStateByOutTradeNo(amount, status,
					rechargeNo, outTradeNo);
		} catch (ManagerException e) {
			logger.error("updateStateByOutTradeNo--outTradeNo" + outTradeNo, e);
			return 0;
		}
	}

	@Override
	public int reqSubmitWithDraw(WithdrawLog record) {
		try {

			return withdrawLogManager.reqSubmitWithDraw(record);
		} catch (ManagerException e) {
			logger.error("插入提现记录失败" + record, e);
			return 0;
		}
	}

	@Override
	public int updateBywithdrawNo(String withdrawNo, int status) {
		try {
			return withdrawLogManager.updateBywithdrawNo(withdrawNo, status);
		} catch (ManagerException e) {
			logger.error("修改提现记录状态失败" + withdrawNo + " status=" + status, e);
			return 0;
		}
	}

	@Override
	public RechargeLog getRechargeLogByTradeNo(String tradeNo) {
		try {
			return rechargeLogManager.getRechargeLogByTradeNo(tradeNo);
		} catch (ManagerException e) {
			logger.error("查询充值流水号 tradeNo" + tradeNo, e);
			return null;
		}
	}

	@Override
	public boolean rechargeSuccess(String rechargeNo, String outTradeNo,
			String memo) {
		boolean result = false;
		try {
//			result = rechargeLogManager.rechargeSuccess(rechargeNo, outTradeNo, memo);
			return false;
		} catch (Exception e) {
			logger.error("充值成功发生异常 rechargeNo" + rechargeNo, e);
		}
		// 交易成功后，设置安全卡
		if (result) {
			setSecurityCard(rechargeNo);
		}
		return result;
	}

	private void setSecurityCard(String rechargeNo) {
		try {
			RechargeLog rechargeLogByTradeNo = rechargeLogManager
					.getRechargeLogByTradeNo(rechargeNo);
			Long bankCardId = rechargeLogByTradeNo.getBankCardId();
			if (bankCardId != null) {
				memberBankCardManager.setSecurityCardFromThirdPay(bankCardId);
			}
		} catch (Exception e) {
			logger.error("快捷卡升级安全卡异常", e);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public boolean rechargeFailed(String rechargeNo, String outTradeNo,
			String errorMsg) {
		try {
//			return rechargeLogManager.rechargeFailed(rechargeNo, outTradeNo, errorMsg);
			return false;
		} catch (Exception e) {
			logger.error("【充值失败】发生异常 rechargeNo" + rechargeNo, e);
		}
		return false;
	}

	@Override
	public WithdrawLog getWithdrawLogByTradeNo(String tradeNo) {

		try {
			return withdrawLogManager.getWithdrawLogByTradeNo(tradeNo);
		} catch (ManagerException e) {
			logger.error("查询充值流水号 tradeNo" + tradeNo, e);
			return null;
		}
	}

	@Override
	public WithdrawLog getWithdrawLogByOutTradeNo(String tradeNo) {
		try {
			return withdrawLogManager.selectByOuterWithdrawNo(tradeNo);
		} catch (ManagerException e) {
			logger.error("外部充值流水号 tradeNo" + tradeNo, e);
			return null;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public boolean withdrawSuccess(String withdrawNo, String memo) {
		boolean result = false;
		try {
//			result = withdrawLogManager.withdrawSuccess(withdrawNo, memo);
			return false;
		} catch (Exception e) {
			logger.error("提现异常 withdrawNo=" + withdrawNo, e);
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public boolean withdrawFailed(String withdrawNo, String errorMsg) {
		try {
//			return withdrawLogManager.withdrawFailed(withdrawNo, errorMsg);
			return false;
		} catch (Exception e) {
			logger.error("提现异常 withdrawNo=" + withdrawNo, e);
		}
		return false;
	}

	@Override
	public int countWithDraw(long memberId) {
		int countRecharge = RedisMemberClient.getWithdrawSuccessCount(memberId);
		return countRecharge;
	}

	@Override
	public BigDecimal totalWithDraw(long memberId) {

		BigDecimal result = RedisMemberClient
				.getWithdrawSuccessAmount(memberId);

		return result;
	}

	@Override
	public int countRecharge(long memberId) {
		int countRecharge = 0;
		countRecharge = RedisMemberClient.getRechargeSuccessCount(memberId);
		return countRecharge;
	}

	@Override
	public BigDecimal totalRecharge(long memberId) {
		BigDecimal result = RedisMemberClient
				.getRechargeSuccessAmount(memberId);
		return result;
	}

	@Override
	public int selectRechargeLogForPaginTotalCountWeb(Map<String, Object> map) {
		try {
			return rechargeLogManager.selectForPaginTotalCount(map);
		} catch (ManagerException e) {
			logger.error("分页查询 统计充值记录  ", e);
		}
		return 0;
	}

	@Override
	public List<RechargeLogDto> selectRechargeLogForPaginWeb(
			Map<String, Object> map) {
		try {
			List<RechargeLog> selectForPagin = rechargeLogManager
					.selectForPagin(map);
			int selectForPaginTotalCount = rechargeLogManager
					.selectForPaginTotalCount(map);
			List<RechargeLogDto> list = BeanCopyUtil.mapList(selectForPagin,
					RechargeLogDto.class);
			return list;
		} catch (ManagerException e) {
			logger.error("分页查询  充值记录  ", e);
		}
		return null;
	}

	public List<RechargeLogDto> selectRechargeLogForPagin(Page page, Map map) {
		try {
			List<RechargeLog> selectForPagin = rechargeLogManager
					.selectForPagin(map);
			int selectForPaginTotalCount = rechargeLogManager
					.selectForPaginTotalCount(map);
			List<RechargeLogDto> list = BeanCopyUtil.mapList(selectForPagin,
					RechargeLogDto.class);

			return list;
		} catch (ManagerException e) {
			logger.error("分页查询  充值记录  ", e);
		}
		return null;
	}

	@Override
	public List<WithdrawLogDto> selectWithdrawLogForPaginWeb(
			Map<String, Object> map) {
		try {
			List<WithdrawLog> selectForPagin = this.withdrawLogManager
					.selectForPaginWeb(map);
			List<WithdrawLogDto> list = BeanCopyUtil.mapList(selectForPagin,
					WithdrawLogDto.class);
			for (WithdrawLogDto dto : list) {
				if(dto.getBankCardId()!=null){
					MemberBankCard memberBankCard = memberBankCardManager
							.selectByPrimaryKey(dto.getBankCardId());
					dto.setBankCardNo(memberBankCard.getCardNumber());
					dto.setBankCode(memberBankCard.getBankCode());
				}
				
			}
			return list;
		} catch (ManagerException e) {
			logger.error("分页查询  提现记录  ", e);
		}
		return null;
	}

	@Override
	public int selectWithdrawLogForPaginTotalCountWeb(Map<String, Object> map) {
		try {
			return this.withdrawLogManager.selectForPaginTotalCountWeb(map);
		} catch (ManagerException e) {
			logger.error("分页查询 总数  提现记录  ", e);
		}
		return 0;
	}

	/**
	 * 取消提现 返回格式转换
	 * 
	 * @param Id
	 *            提现流水表id
	 * @param status
	 *            状态
	 * @return
	 */
	@Override
	public ResultDTO<WithdrawLog> cancelWithdraw(Long id, int status) {

		ResultDO<WithdrawLog> logDo = withdrawLogManager.cancelWithdraw(id,
				status);

		ResultDTO<WithdrawLog> logDTo = new ResultDTO<WithdrawLog>();

		logDTo.setResultCode(logDo.getResultCode());
		if ((logDo.isSuccess())) {
			logDTo.setIsSuccess();
		} else {
			logDTo.setIsError();
		}

		return logDTo;
	}

}
