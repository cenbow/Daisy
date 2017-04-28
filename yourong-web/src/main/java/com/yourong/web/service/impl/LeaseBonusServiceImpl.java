package com.yourong.web.service.impl;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.cache.RedisForProjectClient;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.manager.LeaseBonusDetailManager;
import com.yourong.core.ic.manager.LeaseBonusManager;
import com.yourong.core.ic.manager.LeaseDetailManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.LeaseBonusDetail;
import com.yourong.core.ic.model.LeaseDetail;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.biz.LeaseBonusForFront;
import com.yourong.core.ic.model.biz.LeaseDetailBiz;
import com.yourong.core.ic.model.query.LeaseBonusDetailQuery;
import com.yourong.core.tc.dao.TransactionMapper;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingPayTrade;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.TransactionForProject;
import com.yourong.core.tc.model.query.TransactionQuery;
import com.yourong.web.service.LeaseBonusService;

@Service
public class LeaseBonusServiceImpl implements LeaseBonusService {

	@Autowired
	private LeaseBonusManager leaseBonusManager;

	@Autowired
	private LeaseBonusDetailManager leaseBonusDetailManager;

	@Autowired
	private LeaseDetailManager leaseDetailManager;

	@Autowired
	private HostingPayTradeManager hostingPayTradeManager;

	@Autowired
	private BalanceManager balanceManager;
	@Autowired
	private CapitalInOutLogManager capitalInOutLogManager;
	@Autowired
	private TransactionManager myTransactionManager;
	@Autowired
	private ProjectManager projectManager;
	@Autowired
	private PopularityInOutLogManager popularityInOutLogManager;
	@Autowired
	private TransactionManager transactionManager;

	private static Logger logger = LoggerFactory.getLogger(LeaseBonusServiceImpl.class);

	/**
	 * 前台分页获取租赁分红列表数据
	 */
	@Override
	public Page<LeaseBonusForFront> findLeaseBonusesByPage(BaseQueryParam baseQueryParam) {
		try {
			return leaseBonusManager.findByFrontPage(baseQueryParam);
		} catch (Exception e) {
			logger.error("前台分页获取列表失败，baseQueryParam=" + baseQueryParam, e);
		}
		return null;
	}

	/**
	 * 前台分页获取分红明细列表
	 */
	@Override
	public Page<LeaseBonusDetail> findBonusDetailByPage(LeaseBonusDetailQuery leaseBonusDetailQuery) {
		try {
			return leaseBonusDetailManager.findByBonusDetailPage(leaseBonusDetailQuery);
		} catch (Exception e) {
			logger.error("前台分页获取分红明细失败，leaseBonusDetailQuery=" + leaseBonusDetailQuery, e);
		}
		return null;
	}

	@Override
	public ResultDO<?> afterLeaseBonusHostingPay(String tradeStatus, String tradeNo, String outTradeNo)
			throws Exception {
		ResultDO<TransactionInterest> result = new ResultDO<TransactionInterest>();
		try {
			HostingPayTrade payTrade = hostingPayTradeManager.getByTradeNo(tradeNo);
			HostingPayTrade hostingPayTrade = hostingPayTradeManager.getByIdForLock(payTrade.getId());
			// 如果是最终状态，则直接返回
			if (hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())
					|| hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_FAILED.name())) {
				logger.info("租赁分红代付已经是最终状态，tradeNo=" + tradeNo + ", status=" + hostingPayTrade.getTradeStatus());
				return result;
			}
			// 将交易状态置为最终状态
			hostingPayTrade.setTradeStatus(tradeStatus);
			hostingPayTrade.setOutTradeNo(outTradeNo);
			hostingPayTradeManager.updateHostingPayTrade(hostingPayTrade);
			if (hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())) {
				// 写入分红明细
				LeaseBonusDetail leaseBonusDetail = new LeaseBonusDetail();
				leaseBonusDetail.setBonusAmount(hostingPayTrade.getAmount());
				leaseBonusDetail
						.setLeaseBonusId(leaseBonusManager.getByProject(hostingPayTrade.getProjectId()).getId());
				leaseBonusDetail.setMemberId(hostingPayTrade.getPayeeId());
				leaseBonusDetail.setSinaPayNo(hostingPayTrade.getTradeNo());
				leaseBonusDetail.setProjectId(hostingPayTrade.getProjectId());
				leaseBonusDetail.setTransactionId(hostingPayTrade.getTransactionId());
				Transaction transaction = myTransactionManager.selectTransactionById(hostingPayTrade.getTransactionId());
				BigDecimal bonusAnnualizedRate = FormulaUtil.getBonusAnnualizedRate(
						hostingPayTrade.getAmount(), 
						transaction.getTotalInterest(), 
						transaction.getAnnualizedRate(), 
						transaction.getExtraAnnualizedRate()
						);
				leaseBonusDetail.setBonusRate(bonusAnnualizedRate);
				leaseBonusDetail.setLeaseDetailId(hostingPayTrade.getSourceId());
				leaseBonusDetail.setDelFlag(StatusEnum.DEL_STATUS_NORMAL.getStatus());
				leaseBonusDetailManager.insert(leaseBonusDetail);
				
				//更新项目租赁分红所增加的收益率
				if(transaction.getBonusAnnualizedRate()!=null) {
					transaction.setBonusAnnualizedRate(transaction.getBonusAnnualizedRate().add(bonusAnnualizedRate));
				} else {
					transaction.setBonusAnnualizedRate(bonusAnnualizedRate);
				}
				// 更新交易表分红金额
				if(transaction.getLeaseBonusAmounts()!=null) {
					transaction.setLeaseBonusAmounts(transaction.getLeaseBonusAmounts().add(hostingPayTrade.getAmount()));
				} else {
					transaction.setLeaseBonusAmounts(hostingPayTrade.getAmount());
				}
				myTransactionManager.updateByPrimaryKeySelective(transaction);
				// 写流水
				Balance balance = balanceManager.synchronizedBalance(hostingPayTrade.getPayeeId(),
						TypeEnum.BALANCE_TYPE_PIGGY);
				//项目名称
				Project project = projectManager.selectByPrimaryKey(hostingPayTrade.getProjectId());
				String remark = "";
				if(project!=null){
					remark = MessageFormat.format(RemarksEnum.FINCAPITALINOUT_TYPE_PAY_LEASE_BONUS.getRemarks(), 
							StringUtil.getShortProjectName(project.getName()));
				}
				// 记录用户投资资金流水
				capitalInOutLogManager.insert(hostingPayTrade.getPayeeId(),
						TypeEnum.FINCAPITALINOUT_TYPE_PAY_LEASE_BONUS, hostingPayTrade.getAmount(), null,
						balance.getAvailableBalance(), hostingPayTrade.getId().toString(), remark,
						TypeEnum.BALANCE_TYPE_PIGGY);
				
				MessageClient.sendMsgForLeaseBonus(hostingPayTrade.getProjectId(), hostingPayTrade.getAmount(), hostingPayTrade.getPayeeId());
			}
			// 判断是否还有未处理的代付，如果没有的话，将分红记录置为已分红状态
			if (hostingPayTradeManager.countHostPayBySourceIdAndType(hostingPayTrade.getSourceId(),
					TypeEnum.HOSTING_PAY_LEASE_BONUS.getType(), TradeStatus.WAIT_PAY.name()) < 1) {
				LeaseDetail leaseDetail = leaseDetailManager.selectByPrimaryKey(hostingPayTrade.getSourceId());
				leaseDetail.setBonusDate(DateUtils.getCurrentDate());
				leaseDetail.setBonusStatus(StatusEnum.LEASE_BONUS_DID_BONUS.getStatus());
				leaseDetailManager.updateByPrimaryKeySelective(leaseDetail);
				// 清理缓存
//				RedisProjectClient.clearTransactionDetailForProject(hostingPayTrade.getProjectId());
				editTransactionDetailForLeaseBonus(hostingPayTrade.getProjectId());
			}

			return result;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<LeaseDetail> findLeaseDetailByPage(Long projectId) {
		try {
			return leaseDetailManager.findListByProjectId(projectId);
		} catch (Exception e) {
			logger.error("前台获取租赁记录失败，projectId=" + projectId, e);
		}
		return null;
	}

	@Override
	public LeaseDetailBiz findLeaseDetailBiz(LeaseBonusDetailQuery leaseBonusDetailQuery) {
		try {
			LeaseDetailBiz leaseDetailBiz = null;
			// 获取租赁结算记录
			LeaseDetail detail = leaseDetailManager.selectByPrimaryKey(leaseBonusDetailQuery.getLeaseDetailId());
			if (detail != null) {
				leaseDetailBiz = BeanCopyUtil.map(detail, LeaseDetailBiz.class);
				// 获取分红明细分页列表
				Page<LeaseBonusDetail> bonusDetailPage = leaseBonusDetailManager.findByBonusDetailPage(leaseBonusDetailQuery);
				if (bonusDetailPage != null) {
					leaseDetailBiz.setBonusDetail(bonusDetailPage);
				}
			}
			return leaseDetailBiz;
		} catch (Exception e) {
			logger.error("获取租赁结算明细失败，leaseBonusDetailQuery=" + leaseBonusDetailQuery, e);
		}
		return null;
	}

	/**
	 * 租赁分红数据改变之后调整项目交易明细
	 * @param projectId
	 */
	private void editTransactionDetailForLeaseBonus(Long projectId){
		String key = RedisConstant.TRANSACTION_DETAIL_FOR_PROJECT + RedisConstant.REDIS_SEPERATOR + projectId;
		try {
			List<TransactionForProject> transactionForProjects = Lists.newArrayList();
			transactionForProjects = RedisForProjectClient.getAllTransactionDetail(projectId);//从缓存中获取项目交易详情
			//缓存中存在数据
			if(Collections3.isNotEmpty(transactionForProjects)){
				// 设置租赁分红收益
				for (TransactionForProject transactionForProject : transactionForProjects) {
					// 租赁分红明细
					List<LeaseBonusDetail> details = leaseBonusDetailManager
							.findListByTransactionId(transactionForProject.getId());
					if (Collections3.isNotEmpty(details)) {
						transactionForProject.setLeaseBonusDetails(details);
					}
					// 租赁总额
					LeaseBonusDetail bonusDetail;
					
					bonusDetail = leaseBonusDetailManager.getLeaseTotalIncomeByTransactionId(transactionForProject
							.getId());
					
					if (bonusDetail != null) {
						transactionForProject.setLeaseBonusAmounts(bonusDetail.getBonusAmount());
						transactionForProject.setBonusAnnualizedRate(bonusDetail.getBonusRate());
					} else {
						transactionForProject.setLeaseBonusAmounts(BigDecimal.ZERO);
						transactionForProject.setBonusAnnualizedRate(BigDecimal.ZERO);
					}
				}
			}else{
				TransactionQuery transactionQuery = new TransactionQuery();
				transactionQuery.setProjectId(projectId);
				transactionForProjects = transactionManager.selectTransactionDetailByQueryParams(transactionQuery);
			}
			if(Collections3.isNotEmpty(transactionForProjects)){
				// 把交易数据放入缓存
				RedisForProjectClient.addBatchTransactionDetail(projectId, transactionForProjects);
			}
		} catch (Exception e) {
			logger.error("租赁分红后将交易详情放入缓存失败：projectId={},缓存的key={}", projectId, key, e);
		}
	}
}
