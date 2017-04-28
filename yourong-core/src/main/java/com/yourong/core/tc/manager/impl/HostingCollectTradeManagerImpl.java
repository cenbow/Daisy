package com.yourong.core.tc.manager.impl;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.tc.dao.HostingCollectTradeMapper;
import com.yourong.core.tc.dao.TransactionMapper;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.OrderManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.Transaction;

@Component
public class HostingCollectTradeManagerImpl implements HostingCollectTradeManager {

	private Logger logger = LoggerFactory.getLogger(HostingCollectTradeManagerImpl.class);

	@Autowired
	private HostingCollectTradeMapper hostingCollectTradeMapper;

	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private TransactionManager transactionManager;

	@Autowired
	private CapitalInOutLogManager capitalInOutLogManager;

	@Autowired
	private OrderManager orderManager;

	@Autowired
	private TransactionMapper transactionMapper;

	@Autowired
	private CouponManager couponManager;

	@Autowired
	private TransferProjectManager transferProjectManager;

	@Override
	public int insertSelective(HostingCollectTrade record) throws ManagerException {
		try {
			return hostingCollectTradeMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public HostingCollectTrade getByTradeNo(String tradeNo)
			throws ManagerException {
		try {
			Map<String, Object> params = Maps.newHashMap();
			params.put("tradeNo", tradeNo);
			return hostingCollectTradeMapper.selectHostingCollectTradeByParams(params);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateHostingCollectTrade(
			HostingCollectTrade hostingCollectTrade) throws ManagerException {
		try {
			return hostingCollectTradeMapper.updateByPrimaryKeySelective(hostingCollectTrade);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public HostingCollectTrade  getBySourceIdAndType(Long sourceId, Integer type)
			throws ManagerException {
		try {
			Map<String, Object> params = Maps.newHashMap();
			params.put("sourceId", sourceId);
			params.put("type", type);
			if(TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getType()==type
					||TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType()==type) {
				params.put("tradeStatus", TradeStatus.WAIT_PAY.name());
			}
			return hostingCollectTradeMapper.selectHostingCollectTradeByParams(params);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<HostingCollectTrade> selectSynchronizedHostingCollectTrades()
			throws ManagerException {
		try {
			return hostingCollectTradeMapper.selectSynchronizedHostingCollectTrades();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<HostingCollectTrade> selectSynchronizedHostingCollectTradesForPayerAndPlaform()
			throws ManagerException {
		try {
			return hostingCollectTradeMapper.selectSynchronizedHostingCollectTradesForPayerAndPlaform();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<HostingCollectTrade> selectSynchronizedHostingCollectTradesForPayerAndReplayment()
			throws ManagerException {
		try {
			return hostingCollectTradeMapper.selectSynchronizedHostingCollectTradesForPayerAndReplayment();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}



	@Override
	public int updateTradeStatus(HostingCollectTrade hostingCollectTrade,
			String status, String remarks) throws ManagerException {
		try {
			hostingCollectTrade.setTradeStatus(status);
			if (StringUtil.isNotBlank(remarks)&&remarks.length()>255){
				remarks = remarks.substring(0,255);
			}
			hostingCollectTrade.setRemarks(remarks);
			return this.updateHostingCollectTrade(hostingCollectTrade);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public HostingCollectTrade getById(Long id) throws ManagerException {
		try {
			return hostingCollectTradeMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public boolean haveHostingCollectTradeForLender(Long sourceId,
			String transactionInterestIds) throws ManagerException {
		try {
			HostingCollectTrade hostingCollectTrade = hostingCollectTradeMapper.selectHostingCollectTradeForLender(
					sourceId, 
					transactionInterestIds, 
					TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getType()
					);
			if(hostingCollectTrade!=null) {
				return true;
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return false;
	}
	
	@Override
	public boolean haveHostingCollectTradeForPaltform(Long sourceId,
			String transactionInterestIds) throws ManagerException {
		try {
			
			HostingCollectTrade hostingCollectTrade = hostingCollectTradeMapper.selectHostingCollectTradeForLender(
					sourceId, 
					transactionInterestIds, 
					TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType()
					);
			if(hostingCollectTrade!=null) {
				return true;
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return false;
	}

	@Override
	public HostingCollectTrade getByIdForLock(Long id) throws ManagerException {
		try {
			return hostingCollectTradeMapper.getByIdForLock(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public boolean haveHostingCollectTradeForLeaseBonus(Long sourceId,
			Long projectId) throws ManagerException {
try {
			
			HostingCollectTrade hostingCollectTrade = hostingCollectTradeMapper.selectHostingCollectTradeForLeaseBonus(
					sourceId, 
					projectId, 
					TypeEnum.HOSTING_COLLECT_TRADE_LEASE_BONUS.getType()
					);
			if(hostingCollectTrade!=null) {
				return true;
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return false;
	}


	@Override
	public HostingCollectTrade getWaitPayHostingCollectTrade(Long orderId)
			throws ManagerException {
		try {
			Map<String, Object> params = Maps.newHashMap();
			params.put("sourceId", orderId);
			params.put("type", TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType());
			params.put("tradeStatus", TradeStatus.WAIT_PAY.name());
			return hostingCollectTradeMapper.selectHostingCollectTradeByParams(params);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public List<HostingCollectTrade> selectHostingCollectTradesBySourceId(Long sourceId) throws ManagerException {
		try {
			List<Integer> types = Lists.newArrayList();
			types.add(TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getType());
			types.add(TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType());
			List<HostingCollectTrade> hostingCollectTrades = hostingCollectTradeMapper.selectHostingCollectTradesBySourceIdAndTypes(sourceId, types);
			return hostingCollectTrades;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}



	@Override
	public HostingCollectTrade selectWaitPayOrFinishedHostingCollectTrade(
			Long orderId, int type) throws ManagerException {
		try {
			return hostingCollectTradeMapper.selectWaitPayOrFinishedHostingCollectTrade(orderId, type);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<HostingCollectTrade> findByPage(
			Page<HostingCollectTrade> pageRequest, Map<String, Object> map)
			throws ManagerException {
		Page<HostingCollectTrade> page = new Page<HostingCollectTrade>();
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			List<HostingCollectTrade> hostingCollectTradeList=  hostingCollectTradeMapper.selectHostingCollectTradeList(map);
			int totalCount = hostingCollectTradeMapper.selectHostingCollectTradeListCount(map);
			page.setData(hostingCollectTradeList);
			page.setiTotalDisplayRecords(totalCount);
			page.setiTotalRecords(totalCount);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<HostingCollectTrade> getWaitPayTradeNoByProjectId(Long projectId) throws ManagerException {
		try {
			return hostingCollectTradeMapper.getWaitPayTradeNoByProjectId(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<HostingCollectTrade> selectHostCollectByProjectIdAndType(Long projectId) throws ManagerException {
		try {
			return hostingCollectTradeMapper.selectHostCollectByProjectIdAndType(projectId,TypeEnum.HOSTING_COLLECT_TRADE_INVEST.getType());
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public List<HostingCollectTrade> selectSynchronizedHostingCollectTradesByProjectId(Long projectId) throws ManagerException {
		try {
			return hostingCollectTradeMapper.selectSynchronizedHostingCollectTradesByProjectId(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public void loadCollectTradeByOrder(Order order, HostingCollectTrade collectTrade) throws ManagerException {
		try {
			if (order.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
				// 转让项目交易不走代收冻结
				return;
			}
			Project project = projectManager.selectByPrimaryKey(order.getProjectId());
			if (!project.isDirectProject()) {
				return;
			}
			collectTrade.setIsPreAuth(TypeEnum.COLLECT_TRADE_TYPE_FREEZE.getType());
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Object> handlePreAuthTrade(String notifyTradeStatus, HostingCollectTrade collectTrade) throws Exception {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try {
			// 本地交易状态为最终状态直接返回成功
			if (TradeStatus.TRADE_FINISHED.name().equals(collectTrade.getTradeStatus())
					|| TradeStatus.PRE_AUTH_CANCELED.name().equals(collectTrade.getTradeStatus())) {
				rDO.setResultCode(ResultCode.COLLECT_TRADE_AUTH_IS_LAST_STATUS_ERROR);
				return rDO;
			}
			logger.info("[代收完成/撤销业务处理开始], notifyTradeStatus={}, tradeNo={}", notifyTradeStatus, collectTrade.getTradeNo());
			HostingCollectTrade updateModel = new HostingCollectTrade();
			updateModel.setId(collectTrade.getId());
			updateModel.setTradeStatus(notifyTradeStatus);
			// 将交易状态置为最终状态
			int num = updateHostingCollectTradeByIdAndTradeStatus(updateModel);
			if (num == 0) {
				// 更新失败,返回
				rDO.setResultCode(ResultCode.COLLECT_TRADE_AUTH_STATUS_ERROR);
				return rDO;
			}
			// 同步存钱罐
			Order order = orderManager.selectByPrimaryKey(collectTrade.getSourceId());
			Transaction transaction = transactionMapper.getTransactionByOrderId(order.getId());
			Balance balance = null;
			try {
				balance = balanceManager.synchronizedBalance(transaction.getMemberId(), TypeEnum.BALANCE_TYPE_PIGGY);
			} catch (Exception e) {
				logger.error("流标查询同步存钱罐余额失败, memberId={}", transaction.getMemberId());
				balance = balanceManager.queryBalance(transaction.getMemberId(), TypeEnum.BALANCE_TYPE_PIGGY);
			}
			// 代收撤销
			if(notifyTradeStatus.equals(TradeStatus.PRE_AUTH_CANCELED.name())) {
				logger.info("代收撤销成功 tradeNo={}", collectTrade.getTradeNo());
				// 更新交易状态为流标
				transactionMapper.updateStatusByOrderId(StatusEnum.TRANSACTION_LOSE.getStatus(),
						StatusEnum.TRANSACTION_INVESTMENTING.getStatus(), RemarksEnum.TRANSACTION_LOSE_AND_RETURN_CAPITAL.getRemarks(),
						order.getId());
				// 2. 使用的现金券根据一定规则写入延迟过期时间
				couponManager.extendCouponEndDateForProjectLose(order, transaction.getTransactionTime(), DateUtils.getCurrentDate());
				return rDO;
			}
			// 代收完成
			logger.info("代收完成成功 tradeNo={}", collectTrade.getTradeNo());
			// 存在插入资金流水
			if (transaction.getUsedCapital() != null && transaction.getUsedCapital().doubleValue() > 0) {
				String remark = "";
				if (transaction.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
					TransferProject tPro = transferProjectManager.selectByPrimaryKey(transaction.getTransferId());
					remark = MessageFormat.format(RemarksEnum.FINCAPITALINOUT_TYPE_INVEST.getRemarks(),
							StringUtil.getShortProjectName(tPro.getTransferName()));
				} else {
					Project p = projectManager.selectByPrimaryKey(transaction.getProjectId());
					remark = MessageFormat.format(RemarksEnum.FINCAPITALINOUT_TYPE_INVEST.getRemarks(),
							StringUtil.getShortProjectName(p.getName()));
				}
				// 记录用户投资资金流水
				capitalInOutLogManager.insert(transaction.getMemberId(), TypeEnum.FINCAPITALINOUT_TYPE_INVEST, null,
						transaction.getUsedCapital(), balance.getAvailableBalance(), transaction.getId().toString(), remark,
						TypeEnum.BALANCE_TYPE_PIGGY);
			}
			return rDO;
		} catch (Exception e) {
			logger.error("代收完成/撤销成功的业务处理 tradeNo={}", collectTrade.getTradeNo(), e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			throw e;
		}
	}

	@Override
	public List<HostingCollectTrade> selectPreAuthApplySuccessByProjectId(Long projectId) throws ManagerException {
		try {
			return hostingCollectTradeMapper.selectPreAuthApplySuccessByProjectId(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateHostingCollectTradeByIdAndTradeStatus(HostingCollectTrade hostingCollectTrade) throws ManagerException {
		try {
			return hostingCollectTradeMapper.updateHostingCollectTradeByIdAndTradeStatus(hostingCollectTrade);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public boolean isAllCollectFinishedForRepayment(String tradeNo)
			throws ManagerException {
		try {
			HostingCollectTrade hostingCollectTrade = this.getByTradeNo(tradeNo);
			if(hostingCollectTrade!=null) {
				HostingCollectTrade hostingCollectTradeForLock = hostingCollectTradeMapper.selectByPrimaryKey(hostingCollectTrade.getId());
				//判断是借款人代收还是平台代收
				//如果是借款人代收，判断是否存在平台垫付代收，如果不存在，只要借款人代收是成功，就返回true，如果存在，需要借款人代收和平台代收都成功才返回true
				if(hostingCollectTradeForLock.getType()==TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getType()
						&& TradeStatus.TRADE_FINISHED.name().equals(hostingCollectTradeForLock.getTradeStatus())) {
					return isJudgeCollectMoneyForPlaForm(hostingCollectTradeForLock);
				}
				//如果是平台垫付代收，
				if(hostingCollectTradeForLock.getType()==TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType()
						&& TradeStatus.TRADE_FINISHED.name().equals(hostingCollectTradeForLock.getTradeStatus())) {
					return isJudgeCollectMoneyForBorrower(hostingCollectTradeForLock);
				}
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return false;
	}
	
	/**
     * 判断平台的代收收益劵是否成功，如果没有，也返回成功
     * @param transactionInterestForPay
     * @return
     */
    private boolean isJudgeCollectMoneyForPlaForm(HostingCollectTrade hostingCollectTradeForLock) {
        String transactionInterestIds = hostingCollectTradeForLock.getTransactionInterestIds();
        Long projectid = hostingCollectTradeForLock.getProjectId();
        BigDecimal totalPayInterestAndPrincipalForPlatform = hostingCollectTradeForLock.getPlatformAmount();
        if (totalPayInterestAndPrincipalForPlatform == null || BigDecimal.ZERO.compareTo(totalPayInterestAndPrincipalForPlatform) == 0) {
            logger.info("项目：projectId={}  不用同步平台收益券代收", hostingCollectTradeForLock.getProjectId());
            return true;
        }
        HostingCollectTrade collectTrade = hostingCollectTradeMapper.queryAlreadyHostingCollectTradeForPalform(projectid, TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType(), transactionInterestIds);
        if (collectTrade != null
                && TypeEnum.HOSTING_COLLECT_TRADE_COUPON_B.getType() == collectTrade.getType()
                && TradeStatus.TRADE_FINISHED.name().equalsIgnoreCase(collectTrade.getTradeStatus())) {
            return true;
        }
        return false;
    }
    
    /**
     * 判断借款人代收是否成功
     * @param hostingCollectTradeForLock
     * @return
     */
    private boolean isJudgeCollectMoneyForBorrower(HostingCollectTrade hostingCollectTradeForLock) {
        String transactionInterestIds = hostingCollectTradeForLock.getTransactionInterestIds();
        Long projectid = hostingCollectTradeForLock.getProjectId();
        HostingCollectTrade collectTrade = hostingCollectTradeMapper.queryAlreadyHostingCollectTradeForPalform(projectid, TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getType(), transactionInterestIds);
        if (collectTrade != null
                && TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getType() == collectTrade.getType()
                && TradeStatus.TRADE_FINISHED.name().equalsIgnoreCase(collectTrade.getTradeStatus())) {
            return true;
        }
        return false;
    }

	@Override
	public HostingCollectTrade getBorrowTradeByPlatformTradeNo(
			String paltformTradeNo) throws ManagerException {
		try {
			HostingCollectTrade hostingCollectTrade = this.getByTradeNo(paltformTradeNo);
			String transactionInterestIds = hostingCollectTrade.getTransactionInterestIds();
	        Long projectid = hostingCollectTrade.getProjectId();
	        return hostingCollectTradeMapper.queryAlreadyHostingCollectTradeForPalform(
	        		projectid, 
	        		TypeEnum.HOSTING_COLLECT_TRADE_RETURN.getType(), 
	        		transactionInterestIds
	        		);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
    

	@Override
	public HostingCollectTrade getByTradeNoAndType(String tradeNo,int type)
			throws ManagerException {
		try {
			Map<String, Object> params = Maps.newHashMap();
			params.put("tradeNo", tradeNo);
			params.put("type", type);
			return hostingCollectTradeMapper.selectHostingCollectTradeByParams(params);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateHostingCollectTradeWithholdAuthority(HostingCollectTrade hostingCollectTrade) throws ManagerException {
		try {
			return hostingCollectTradeMapper.updateHostingCollectTradeWithholdAuthority(hostingCollectTrade);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<HostingCollectTrade> selectPreAuthApplySuccessByTransferId(Long transferId) throws ManagerException {
		try {
			return hostingCollectTradeMapper.selectPreAuthApplySuccessByTransferId(transferId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}