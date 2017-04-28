package com.yourong.core.ic.manager.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.constant.Config;
import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.Money;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.BatchTradeNotifyMode;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeCode;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.TradeArgs;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.dao.LeaseBonusMapper;
import com.yourong.core.ic.dao.LeaseDetailMapper;
import com.yourong.core.ic.dao.ProjectMapper;
import com.yourong.core.ic.manager.LeaseBonusManager;
import com.yourong.core.ic.manager.LeaseDetailManager;
import com.yourong.core.ic.model.LeaseBonus;
import com.yourong.core.ic.model.LeaseDetail;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.biz.LeaseBonusForFront;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.dao.TransactionMapper;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.HostingPayTrade;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.query.TransactionQuery;

@Service
public class LeaseBonusManagerImpl implements LeaseBonusManager {

	private static final String LEASE_CONSTANT = "次";
	@Autowired
	private LeaseBonusMapper leaseBonusMapper;
	@Autowired
	private HostingCollectTradeManager hostingCollectTradeManager;
	@Autowired
	private BalanceManager balanceManager;
	@Autowired
	private CapitalInOutLogManager capitalInOutLogManager;
	@Autowired
	private TransactionMapper transactionMapper;
	@Autowired
	private ProjectMapper projectMapper;
	@Autowired
	private HostingPayTradeManager hostingPayTradeManager;
	@Autowired
	private SinaPayClient sinaPayClient;
	@Autowired
	private LeaseDetailManager leaseDetailManager;

	@Autowired
	private LeaseDetailMapper leaseDetailMapper;
	
	@Autowired
	private SysDictManager sysDictManager;

	private Logger logger = LoggerFactory.getLogger(LeaseBonusManagerImpl.class);

	@Override
	public int deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			return leaseBonusMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insert(LeaseBonus record) throws ManagerException {
		try {
			return leaseBonusMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insertSelective(LeaseBonus record) throws ManagerException {
		try {
			return leaseBonusMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public LeaseBonus selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return leaseBonusMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKeySelective(LeaseBonus record) throws ManagerException {
		try {
			return leaseBonusMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKey(LeaseBonus record) throws ManagerException {
		try {
			return leaseBonusMapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<LeaseBonus> findByPage(Page<LeaseBonus> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			int totalCount = leaseBonusMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<LeaseBonus> selectForPagin = leaseBonusMapper.selectForPagin(map);
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int batchDelete(long[] ids) throws ManagerException {
		try {
			return leaseBonusMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public LeaseBonus findLeaseBonusByProjectId(long projectId) throws ManagerException {
		try {
			return leaseBonusMapper.findLeaseBonusByProjectId(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateForLeaseSettlement(LeaseBonus leaseBonus) throws ManagerException {
		try {
			return leaseBonusMapper.updateForLeaseSettlement(leaseBonus);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	/**
	 * 1、平台分红代收回调成功后更新代收状态
	 * 2、同步平台余额，写入平台流水
	 * 3、创建分红代付记录
	 * @param tradeNo
	 * @param processStatus
	 * @return
	 * @throws Exception
	 */
	public ResultDO<LeaseBonus> afterLeaseBonusCollectNotifyProcess(String tradeNo, String processStatus)
			throws Exception {
		ResultDO<LeaseBonus> result = new ResultDO<LeaseBonus>();
		try {
			StopWatch sw = new StopWatch();
			sw.start();
			logger.info("平台垫付租赁收益分红回调后处理流程开始，tradeNo=" + tradeNo);
			HostingCollectTrade hostingCollectTradeNoLock = hostingCollectTradeManager.getByTradeNo(tradeNo);
			String ip = null;
			SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
			if (dict != null) {
				ip = dict.getValue();
			}
			if (hostingCollectTradeNoLock != null) {
				HostingCollectTrade collectTradeForLock = hostingCollectTradeManager
						.getByIdForLock(hostingCollectTradeNoLock.getId());

				if (collectTradeForLock != null) {
					// 如果是最终状态，则直接返回
					if (collectTradeForLock.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())
							|| collectTradeForLock.getTradeStatus().equals(TradeStatus.TRADE_FAILED.name())
							|| collectTradeForLock.getTradeStatus().equals(TradeStatus.TRADE_CLOSED.name())) {
						logger.info("平台垫付租赁收益分红代收已经是最终状态，tradeNo=" + tradeNo);
						result.setSuccess(false);
						return result;
					}
					// 将交易状态置为最终状态
					collectTradeForLock.setTradeStatus(processStatus);
					hostingCollectTradeManager.updateHostingCollectTrade(collectTradeForLock);
					// 如果交易为交易成功状态
					if (TradeStatus.TRADE_FINISHED.name().equals(processStatus)) {
						// 写流水
						Balance balance = balanceManager.synchronizedBalance(Long.parseLong(Config.internalMemberId),
								TypeEnum.BALANCE_TYPE_BASIC);
						// 记录用户投资资金流水
						capitalInOutLogManager.insert(Long.parseLong(Config.internalMemberId),
								TypeEnum.FINCAPITALINOUT_TYPE_LEASE_BONUS, null, collectTradeForLock.getAmount(),
								balance.getAvailableBalance(), collectTradeForLock.getId().toString(), null,
								TypeEnum.BALANCE_TYPE_BASIC);
						// TODO 创建分红代付记录
						LeaseDetail leaseDetail = leaseDetailManager.selectByPrimaryKey(collectTradeForLock
								.getSourceId());
						LeaseBonus leaseBonus = leaseBonusMapper.selectByPrimaryKey(leaseDetail.getLeaseBonusId());
						TransactionQuery transactionQuery = new TransactionQuery();
						transactionQuery.setProjectId(leaseBonus.getProjectId());
						List<Transaction> transactions = transactionMapper
								.selectTransactionsByQueryParams(transactionQuery);
						Project project = projectMapper.selectByPrimaryKey(leaseBonus.getProjectId());
						if (Collections3.isNotEmpty(transactions)) {
							BigDecimal unitBonus = BigDecimal.valueOf(collectTradeForLock.getAmount().doubleValue()
									/ project.getTotalAmount().doubleValue());
							List<TradeArgs> tradeList = Lists.newArrayList();
							String batchPayNo = SerialNumberUtil.generateBatchPayTradeaNo(tradeNo);
							String summary = RemarksEnum.HOSTING_PAY_TRADE_FOR_LEASE_BONUS.getRemarks();
							for (Transaction transaction : transactions) {
								BigDecimal userBonus = unitBonus.multiply(transaction.getInvestAmount()).setScale(2,
										BigDecimal.ROUND_HALF_UP);
								if (userBonus.doubleValue() > 0) {

									// 本地创建代付交易hostingPayTrade
									// 判断这笔分红有没有代付过
									if (hostingPayTradeManager.haveHostPayTradeForPayLeaseBonus(leaseDetail.getId(),
											TypeEnum.HOSTING_PAY_LEASE_BONUS.getType(), transaction.getId())) {
										break;
									}
									HostingPayTrade hostingPayTrade = new HostingPayTrade();
									createHostingPayTrade(userBonus, batchPayNo, hostingPayTrade, transaction,
											leaseDetail.getId());
									// 组建批量代付参数

									TradeArgs tradeArgs = new TradeArgs();
									tradeArgs.setAccountType(AccountType.SAVING_POT);
									tradeArgs.setIdType(IdType.UID);
									tradeArgs.setMoney(new Money(hostingPayTrade.getAmount()));
									tradeArgs.setPayeeId(SerialNumberUtil.generateIdentityId(hostingPayTrade
											.getPayeeId()));
									tradeArgs.setRemark(hostingPayTrade.getSummary());
									tradeArgs.setTradeNo(hostingPayTrade.getTradeNo());
									tradeList.add(tradeArgs);
									if (tradeList.size() == 100) {
										// 当批量代付满100笔时提交代付请求
										try {
											sinaPayClient.createBatchPayTrade(batchPayNo, summary, ip, tradeList,
													TradeCode.PAY_TO_INVESTOR_FOR_LEASEBONUS, BatchTradeNotifyMode.single_notify);
										} catch (Exception e) {
											throw e;
										}
										batchPayNo = SerialNumberUtil.generateBatchPayTradeaNo(tradeNo);
										tradeList.clear();
									}
								}

							}
							if (Collections3.isNotEmpty(tradeList) && tradeList.size() < 100) {
								try {
									sinaPayClient.createBatchPayTrade(batchPayNo, summary,ip, tradeList,
											TradeCode.PAY_TO_INVESTOR_FOR_LEASEBONUS, BatchTradeNotifyMode.single_notify);
								} catch (Exception e) {
									throw e;
								}
							}
						}

					}

					// 如果是原始债权人代收失败，则将分红记录置为待分红
					if (TradeStatus.TRADE_FAILED.name().equals(processStatus)
							|| TradeStatus.TRADE_CLOSED.name().equals(processStatus)) {

					}
					logger.info("平台垫付租赁收益分红回调后处理流程结束，总共耗时：" + sw.getTime() + ",tradeNo=" + tradeNo);
				}
			}

			return result;
		} catch (Exception e) {
			logger.error("平台垫付租赁收益分红回调后处理流程发生异常，tradeNo：" + tradeNo, e);
			result.setSuccess(false);
			throw e;
		}
	}

	@Override
	public Page<LeaseBonusForFront> findByFrontPage(BaseQueryParam baseQueryParam) throws ManagerException {
		try {
			List<LeaseBonusForFront> leaseBonuses = Lists.newArrayList();
			int count = leaseBonusMapper.selectForFrontPaginTotalCount(baseQueryParam);
			leaseBonuses = leaseBonusMapper.selectForFrontPagin(baseQueryParam);
			for (LeaseBonusForFront leaseBonus : leaseBonuses) {
				BigDecimal totalUserBonus = leaseDetailManager.getTotalUserBonusByLeaseBonusId(leaseBonus.getId()); // 已分红的总额；
				if (totalUserBonus != null) {
					leaseBonus.setTotalIncome(totalUserBonus);
				}else{
					leaseBonus.setTotalIncome(BigDecimal.ZERO);
				}
				Map<String, Object> map = Maps.newHashMap();
				map.put("leaseBonusId", leaseBonus.getId());
				map.put("bonusStatus", StatusEnum.LEASE_BONUS_DID_BONUS.getStatus());
				int bonusCount = leaseDetailMapper.getLeaseDetailCountByStatus(map);// 已分红期数
				leaseBonus.setBonusPeriods(bonusCount);
				map.remove("bonusStatus");
				int leaseCount = leaseDetailMapper.getLeaseDetailCountByStatus(map);// 总租赁期数
				leaseBonus.setLeasePeriods(leaseCount);
				if (leaseBonus.getProjectStatus().intValue() >= StatusEnum.PROJECT_STATUS_INVESTING.getStatus()
						&& leaseBonus.getProjectStatus().intValue() < StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()) {//投资中<=项目状态<已还款
					if(leaseBonus.getLeaseStatus().intValue()==StatusEnum.LEASE_BONUS_WAIT_LEASE.getStatus()){//待租
						leaseBonus.setLeaseStatus(StatusEnum.LEASE_BONUS_WAIT_LEASE_FOR_FRONT.getStatus());
						leaseBonus.setBonusStatus(StatusEnum.LEASE_BONUS_WAIT_BONUS_FOR_FRONT.getStatus());
					}
					if(leaseBonus.getLeaseStatus().intValue()==StatusEnum.LEASE_BONUS_BEEN_LEASE_FOR_FRONT.getStatus()&&bonusCount<=0){//已租&&分红期数小于等于0
						leaseBonus.setLeaseStatus(StatusEnum.LEASE_BONUS_BEEN_LEASE_FOR_FRONT.getStatus());
						leaseBonus.setBonusStatus(StatusEnum.LEASE_BONUS_WAIT_BONUS_FOR_FRONT.getStatus());
					}
					if(leaseBonus.getLeaseStatus().intValue()==StatusEnum.LEASE_BONUS_BEEN_LEASE_FOR_FRONT.getStatus()&&bonusCount>0){
						leaseBonus.setLeaseStatus(StatusEnum.LEASE_BONUS_BEEN_LEASE_FOR_FRONT.getStatus());
						leaseBonus.setBonusStatus(StatusEnum.LEASE_BONUS_BEEN_BONUS_FOR_FRONT.getStatus());
					}
				}
				if(leaseBonus.getProjectStatus().intValue() >= StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()){//项目状态>=已还款
					if(leaseBonus.getLeaseStatus().intValue()==StatusEnum.LEASE_BONUS_WAIT_LEASE.getStatus()){//待租
						leaseBonus.setLeaseStatus(StatusEnum.LEASE_BONUS_NOT_LEASE_FOR_FRONT.getStatus());
						leaseBonus.setBonusStatus(StatusEnum.LEASE_BONUS_NOT_BONUS_FOR_FRONT.getStatus());
					}
					if(leaseBonus.getLeaseStatus().intValue()==StatusEnum.LEASE_BONUS_BEEN_LEASE_FOR_FRONT.getStatus()&&bonusCount<=0){//已租&&分红期数小于等于0
						leaseBonus.setLeaseStatus(StatusEnum.LEASE_BONUS_BEEN_END_LEASE_FOR_FRONT.getStatus());
						leaseBonus.setBonusStatus(StatusEnum.LEASE_BONUS_WAIT_BONUS_FOR_FRONT.getStatus());
					}
					if(leaseBonus.getLeaseStatus().intValue()==StatusEnum.LEASE_BONUS_BEEN_LEASE_FOR_FRONT.getStatus()&&bonusCount>0){
						leaseBonus.setLeaseStatus(StatusEnum.LEASE_BONUS_BEEN_END_LEASE_FOR_FRONT.getStatus());
						leaseBonus.setBonusStatus(StatusEnum.LEASE_BONUS_BEEN_BONUS_FOR_FRONT.getStatus());
					}
				}
			}
			Page<LeaseBonusForFront> page = new Page<LeaseBonusForFront>();
			page.setData(leaseBonuses);
			// 每页总数
			page.setiDisplayLength(baseQueryParam.getPageSize());
			// 当前页
			page.setPageNo(baseQueryParam.getCurrentPage());
			// 总数
			page.setiTotalDisplayRecords(count);
			page.setiTotalRecords(count);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	private Integer createHostingPayTrade(BigDecimal userBonus, String batchPayNo, HostingPayTrade hostingPayTrade,
			Transaction transaction, Long sourceId) throws ManagerException {
		hostingPayTrade.setAmount(userBonus);
		hostingPayTrade.setBatchPayNo(batchPayNo);
		hostingPayTrade.setPayeeId(transaction.getMemberId());
		hostingPayTrade.setRemarks(RemarksEnum.HOSTING_PAY_TRADE_FOR_LEASE_BONUS.getRemarks());
		hostingPayTrade.setSourceId(sourceId);
		// hostingPayTrade.setSummary(summary);
		hostingPayTrade.setTradeNo(SerialNumberUtil.generatePayTradeaNo(sourceId));
		hostingPayTrade.setTradeStatus(TradeStatus.WAIT_PAY.name());
		hostingPayTrade.setType(TypeEnum.HOSTING_PAY_LEASE_BONUS.getType());
		hostingPayTrade.setProjectId(transaction.getProjectId());
		hostingPayTrade.setTransactionId(transaction.getId());
		return hostingPayTradeManager.insertSelective(hostingPayTrade);
	}

	/**
	 * 更新租赁分红状态
	 */
	@Override
	public int updateStatusByLeaseBonusId(long id) throws ManagerException {
		try {
			return leaseBonusMapper.updateStatusByLeaseBonusId(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 删除租赁记录时更新租赁分红总额
	 */
	@Override
	public int updateTotalIncomeByLeaseBonusId(BigDecimal totalIncome, long id) throws ManagerException {
		try {
			return leaseBonusMapper.updateTotalIncomeByLeaseBonusId(totalIncome, id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateTotalIncomeForEditLeaseDetail(Map<String, Object> map) throws ManagerException {
		try {
			return leaseBonusMapper.updateTotalIncomeForEditLeaseDetail(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public LeaseBonus getByProject(Long projectId) throws ManagerException {
		try {
			return leaseBonusMapper.getByProject(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}
