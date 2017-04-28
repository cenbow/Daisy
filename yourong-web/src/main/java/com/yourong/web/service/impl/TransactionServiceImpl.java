package com.yourong.web.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.time.StopWatch;
import org.dozer.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.MemberLogEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.TradeItem;
import com.yourong.common.thirdparty.sinapay.pay.domainservice.converter.ResponseJsonConverter;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.BestSignUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.OSSUtil;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.bsc.manager.AttachmentIndexManager;
import com.yourong.core.bsc.manager.BscAttachmentManager;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.fin.manager.OverdueLogManager;
import com.yourong.core.fin.manager.PopularityInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.CapitalInOutLog;
import com.yourong.core.fin.model.query.CapitalInOutLogQuery;
import com.yourong.core.ic.manager.DebtCollateralManager;
import com.yourong.core.ic.manager.DebtInterestManager;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.manager.DebtTransferManager;
import com.yourong.core.ic.manager.LeaseBonusDetailManager;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.ic.model.LeaseBonusDetail;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectExtra;
import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.ic.model.biz.TransactionProjectDetailBiz;
import com.yourong.core.ic.model.biz.TransferInformation;
import com.yourong.core.ic.model.biz.TransferRateList;
import com.yourong.core.ic.model.query.TransactionProjectDetailBizQuery;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.manager.CouponTemplatePrintManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.CouponTemplatePrint;
import com.yourong.core.mc.model.biz.ActivityForInvest;
import com.yourong.core.mc.model.biz.ActivityForKing;
import com.yourong.core.mc.model.biz.ActivityForRankingList2nd;
import com.yourong.core.mc.model.biz.ActivityMonthlyMyRank;
import com.yourong.core.repayment.manager.AfterHostingPayHandleManager;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.dao.ContractSignMapper;
import com.yourong.core.tc.dao.MemberHistoryRepaymentMapper;
import com.yourong.core.tc.dao.MemberRepaymentMapper;
import com.yourong.core.tc.manager.ContractManager;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.manager.OrderManager;
import com.yourong.core.tc.manager.TradeManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.ContractSign;
import com.yourong.core.tc.model.HostingPayTrade;
import com.yourong.core.tc.model.Order;
import com.yourong.core.tc.model.Preservation;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.ContractBiz;
import com.yourong.core.tc.model.biz.DebtForLenderMember;
import com.yourong.core.tc.model.biz.MemberTransactionCapital;
import com.yourong.core.tc.model.biz.TraceSourceCollectBiz;
import com.yourong.core.tc.model.biz.TradeBiz;
import com.yourong.core.tc.model.biz.TransactionForFirstInvestAct;
import com.yourong.core.tc.model.biz.TransactionForMemberCenter;
import com.yourong.core.tc.model.biz.TransactionForProject;
import com.yourong.core.tc.model.biz.TransactionInterestForMember;
import com.yourong.core.tc.model.biz.TransactionProjectBiz;
import com.yourong.core.tc.model.biz.TransferContractBiz;
import com.yourong.core.tc.model.query.DebtForLenderQuery;
import com.yourong.core.tc.model.query.TraceSourceCollectQuery;
import com.yourong.core.tc.model.query.TransactionInterestQuery;
import com.yourong.core.tc.model.query.TransactionProjectBizQuery;
import com.yourong.core.tc.model.query.TransactionQuery;
import com.yourong.core.uc.manager.EnterpriseManager;
import com.yourong.core.uc.manager.MemberBehaviorLogManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;
import com.yourong.web.dto.OrderPayDto;
import com.yourong.web.dto.TransactionDto;
import com.yourong.web.dto.TransactionInterestForMemberDto;
import com.yourong.web.dto.TransferForMemberDto;
import com.yourong.web.service.BaseService;
import com.yourong.web.service.CapitalInOutLogService;
import com.yourong.web.service.MemberInfoService;
import com.yourong.web.service.PreservationService;
import com.yourong.web.service.TransactionService;
import com.yourong.web.utils.ServletUtil;
import com.yourong.web.utils.SysServiceUtils;
import com.yourong.web.utils.WebPropertiesUtil;

/**
 * 交易核心业务类
 * 
 * @author Leon Ray 2014年9月20日-下午6:38:08
 */
@Service
public class TransactionServiceImpl extends BaseService implements TransactionService {
	@Autowired
	private TransactionManager myTransactionManager;

	@Autowired
	private ContractManager contractManager;

	@Autowired
	private ProjectManager projectManager;

	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private DebtManager debtManager;

	@Autowired
	private MemberManager memberManager;
	
	@Autowired
	private DebtCollateralManager debtCollateralManager;
	
	@Autowired
	private CouponManager couponManager;

	@Autowired
	private DebtTransferManager debtTransferManager;

	@Autowired
	private OrderManager orderManager;

	@Autowired
	private TransactionInterestManager transactionInterestManager;

	@Autowired
	private HostingCollectTradeManager hostingCollectTradeManager;

	@Autowired
	private HostingPayTradeManager hostingPayTradeManager;

	@Autowired
	private SinaPayClient sinaPayClient;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;
    
    @Resource
    private BscAttachmentManager bscAttachmentManager;
    
    @Resource
    private AttachmentIndexManager attachmentIndexManager;
    
    @Autowired
    private PopularityInOutLogManager popularityInOutLogManager;
    
    @Autowired
    private LeaseBonusDetailManager leaseBonusDetailManager;
 	
    @Autowired
    PreservationService preservationService;
    
    @Autowired
	private EnterpriseManager enterpriseManager;
    
    @Autowired
	private MemberInfoService memberInfoService;
    
    @Autowired
    CouponTemplatePrintManager couponTemplatePrintManager;
    
    @Autowired
    ActivityManager activityManager;
    
    @Autowired
	private DebtInterestManager debtInterestManager;
    @Autowired
    private AfterHostingPayHandleManager afterHostingPayHandleManager;
    
	@Autowired
	private TradeManager tradeManager;
	
	@Autowired
	private TransferProjectManager transferProjectManager;
	
	@Autowired
	private OverdueLogManager overdueLogManager;
	
	@Autowired
	private CapitalInOutLogService capitalInOutLogService;
	
	@Autowired 
	private SysDictManager sysDictManager;
	
	@Autowired
	private MemberBehaviorLogManager memberBehaviorLogManager;
	
	
	@Autowired
	private CapitalInOutLogManager capitalInOutLogManager;
	
	@Autowired
	private ProjectExtraManager projectExtraManager;
	
	@Autowired
	private MemberRepaymentMapper memberRepaymentMapper;
	
	@Autowired
	private MemberHistoryRepaymentMapper memberHistoryRepaymentMapper;
	
	@Autowired
	private ContractSignMapper contractSignMapper;
    @Override
	public ResultDO<TradeBiz> createTransactionHostingTrade(Order order, int sourceType, String payerIp) {
		String returnUrl = WebPropertiesUtil.getSinaCashDeskReturnUrl();
		return tradeManager.tradeUseCapital(order, sourceType, payerIp, returnUrl);
	}

	@Override
	public boolean generateContract(Long transactionId) {
		try {
			// 生成合同
			if (transactionId != null) {
				taskExecutor.execute(new GenerateContractThread(transactionId));
			}
		} catch (Exception e) {
		}

		return true;
	}

	/**
	 * 生成合同线程
	 * 
	 * @author Administrator
	 *
	 */
	private class GenerateContractThread implements Runnable {
		private Long transactionId;

		public GenerateContractThread(final Long transactionId) {
			this.transactionId = transactionId;
		}

		@Override
		public void run() {
			logger.debug("生成合同线程开始执行，transactionId：" + transactionId);
			try {
				myTransactionManager.generateContractfinal(transactionId, "web");
			} catch (Exception e) {
				logger.error("生成合同发生异常，transactionId：" + transactionId, e);
			}
		}
	}

	@Override
	public List<TransactionForProject> selectTransactionForProjectsForPage(TransactionQuery transactionQuery) {
		try {
			StopWatch watch = new StopWatch();
			watch.start();
			Long projectId = transactionQuery.getProjectId();
			//String key = RedisConstant.TRANSACTION_DETAIL_FOR_PROJECT + RedisConstant.REDIS_SEPERATOR + projectId;
			String key = RedisConstant.TRANSACTION_INVERESTLIST_FOR_PROJECT + RedisConstant.REDIS_SEPERATOR + projectId;
			List<TransactionForProject> transactionForProjects = Lists.newArrayList();
			if (RedisManager.isExitByObjectKey(key)) {
				//缓存存在
				//transactionForProjects = RedisForProjectClient.getAllTransactionDetail(projectId);
				transactionForProjects=(List<TransactionForProject>) RedisManager.getObject(key);
				//Collections.reverse(transactionForProjects);
				watch.stop();
//				logger.info("从交易详情从缓存中取数据时间" + watch.getTime());
			}
			
			//缓存不存在
			if (Collections3.isEmpty(transactionForProjects)) {
				transactionQuery.setProjectCategory(TypeEnum.PROJECT_CATEGORY_NORMAL.getType());
				transactionForProjects = myTransactionManager.selectTransactionDetailByQueryParams(transactionQuery);
				if(Collections3.isNotEmpty(transactionForProjects)){
					//RedisForProjectClient.addBatchTransactionDetail(projectId,transactionForProjects);
					RedisManager.putObject(key, transactionForProjects);
					RedisManager.expire(key, 60*60);
				}
			}
			return transactionForProjects;
		} catch (Exception e) {
			logger.error("通过项目id分页查询交易记录，transactionQuery=" + transactionQuery, e);
		}
		return null;
	}

	@Override
	public MemberTransactionCapital getMemberTransactionCapital(Long memberID) {
		try {
			MemberTransactionCapital memberTransactionCapital = new MemberTransactionCapital();
			TransactionQuery transactionQuery = new TransactionQuery();
			transactionQuery.setMemberId(memberID);
			transactionQuery.setType(1);
			List<Transaction> transactions = myTransactionManager.selectTransactionsByQueryParams(transactionQuery);
			memberTransactionCapital.setMemberId(memberID);
			if (Collections3.isNotEmpty(transactions)) {
				memberTransactionCapital.setTotalInvestNum(transactions.size());
				BigDecimal finishedInvestTotal = BigDecimal.ZERO;
				BigDecimal subsistingInvestTotal = BigDecimal.ZERO;
				for (Transaction transaction : transactions) {
					// 已完结投资
					if (StatusEnum.TRANSACTION_COMPLETE.getStatus() == transaction.getStatus()) {
//						finishedInvestNum++;
						finishedInvestTotal = finishedInvestTotal.add(transaction.getInvestAmount());
					}
					// 存续期投资
					if (StatusEnum.TRANSACTION_REPAYMENT.getStatus() == transaction.getStatus()) {
//						subsistingInvestNum++;
						subsistingInvestTotal = subsistingInvestTotal.add(transaction.getInvestAmount());
					}
				}
//				memberTransactionCapital.setFinishedInvestNum(finishedInvestNum);
//				memberTransactionCapital.setSubsistingInvestNum(subsistingInvestNum);
				memberTransactionCapital.setFinishedInvestTotal(finishedInvestTotal.setScale(2,
						BigDecimal.ROUND_HALF_UP));
				memberTransactionCapital.setSubsistingInvestTotal(subsistingInvestTotal.setScale(2,
						BigDecimal.ROUND_HALF_UP));
			}
			TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
			transactionInterestQuery.setMemberId(memberID);
			List<TransactionInterest> transactionInterests = transactionInterestManager
					.selectTransactionInterestsByQueryParams(transactionInterestQuery);
			if (Collections3.isNotEmpty(transactionInterests)) {
				BigDecimal receivableInterest = BigDecimal.ZERO;
				BigDecimal receivablePrincipal = BigDecimal.ZERO;
				BigDecimal receivedInterest = BigDecimal.ZERO;
				BigDecimal receivedPrincipal = BigDecimal.ZERO;
				BigDecimal overdueFine = BigDecimal.ZERO;
				/** 待收利息笔数 **/
				int receivableInterestNum = 0;
				/** 已收利息笔数 **/
				int receivedInterestNum = 0;
				/** 已收本金笔数 **/
				int finishedInvestNum = 0;
				/** 代收本金笔数 **/
				int subsistingInvestNum = 0;
				for (TransactionInterest transactionInterest : transactionInterests) {
					// 未付利息和本金
					if (StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus() == transactionInterest.getStatus()
							|| StatusEnum.TRANSACTION_INTEREST_PAYING.getStatus() == transactionInterest.getStatus()) {
						receivableInterest = receivableInterest.add(transactionInterest.getRealPayInterest());
						receivablePrincipal = receivablePrincipal.add(transactionInterest.getRealPayPrincipal());
						if(transactionInterest.getPayablePrincipal().compareTo(BigDecimal.ZERO)>0){
							subsistingInvestNum += 1;
						}
						receivableInterestNum += 1;
					}
					// 已付利息和本金
					if (StatusEnum.TRANSACTION_INTEREST_ALL_PAYED.getStatus() == transactionInterest.getStatus()
							|| StatusEnum.TRANSACTION_INTEREST_PART_PAYED.getStatus() == transactionInterest
									.getStatus()) {
						receivedInterest = receivedInterest.add(transactionInterest.getRealPayInterest());
						receivedPrincipal = receivedPrincipal.add(transactionInterest.getRealPayPrincipal());
						if(transactionInterest.getRealPayPrincipal().compareTo(BigDecimal.ZERO)>0){
							finishedInvestNum += 1;
						}
						receivedInterestNum += 1;
						//逾期已还款计算滞纳金
						if(TypeEnum.REPAYMENT_TYPE_OVERDUE.getType()==transactionInterest.getPayType()){
							overdueFine = overdueFine.add(transactionInterest.getOverdueFine());
						}
					}
					
					
					
				}
				memberTransactionCapital.setFinishedInvestNum(finishedInvestNum);
				memberTransactionCapital.setSubsistingInvestNum(subsistingInvestNum);
				memberTransactionCapital
						.setReceivableInterest(receivableInterest.setScale(2, BigDecimal.ROUND_HALF_UP));
				memberTransactionCapital.setReceivablePrincipal(receivablePrincipal.setScale(2,
						BigDecimal.ROUND_HALF_UP));
				memberTransactionCapital.setReceivedInterest(receivedInterest.setScale(2, BigDecimal.ROUND_HALF_UP));
				memberTransactionCapital.setReceivedPrincipal(receivedPrincipal.setScale(2, BigDecimal.ROUND_HALF_UP));
				memberTransactionCapital.setOverdueFine(overdueFine.setScale(2, BigDecimal.ROUND_HALF_UP));
				memberTransactionCapital.setReceivableInterestNum(receivableInterestNum);
				memberTransactionCapital.setReceivedInterestNum(receivedInterestNum);
			}
			return memberTransactionCapital;
		} catch (Exception e) {
			logger.error("查询会员投资资金信息出错，memberID=" + memberID, e);
		}
		return null;
	}

	@Override
	public List<TransactionForProject> selectNewTransactions(int pageSize) {
		try {
			return myTransactionManager.selectNewTransactions(pageSize);
		} catch (Exception e) {
			logger.error("查询最新投资记录出异常", e);
		}
		return null;
	}

	@Override
	public ResultDO<Transaction> afterTransactionCollectNotifyProcess(String tradeNo, String outTradeNo,
			String tradeStatus) {
		ResultDO<Transaction> result = new ResultDO<Transaction>();
		try {
			result = myTransactionManager.afterTransactionCollectNotifyProcess(tradeNo, outTradeNo, tradeStatus);
			// if(!result.isSuccess()) {
			// afterTransactionCollectNotifyProcessFailed(tradeNo);
			// }
			return result;
		} catch (Exception e) {
			logger.error("执行交易发生异常，tradeNo：" + tradeNo, e);
			result.setSuccess(false);
			// afterTransactionCollectNotifyProcessFailed(tradeNo);
			return result;
		}
	}

	@Override
	public void afterloanToOriginalCreditor(String tradeStatus, String outTradeNo) {
		// 将交易状态置为最终状态
		HostingPayTrade hostingPayTrade;
		try {
			hostingPayTrade = hostingPayTradeManager.getByTradeNo(outTradeNo);
			hostingPayTrade.setTradeStatus(tradeStatus);
			hostingPayTrade.setOutTradeNo(outTradeNo);
			hostingPayTradeManager.updateHostingPayTrade(hostingPayTrade);
		} catch (ManagerException e) {
			logger.error("处理回调后放款给原始债权人业务发生异常，tradeNo：" + outTradeNo, e);
		}
	}

	@Override
	public Page<TransactionForMemberCenter> selectAllTransactionForMember(TransactionQuery query) {
		try {
			Page<TransactionForMemberCenter> page = myTransactionManager.selectAllTransactionForMember(query);
			List<TransactionForMemberCenter> transactionForMemberCenters =page.getData();
			for(TransactionForMemberCenter transactionForMemberCenter :transactionForMemberCenters){
				Project project = projectManager.selectByPrimaryKey(transactionForMemberCenter.getProjectId());
				transactionForMemberCenter.setInvestType(project.getInvestType());
				transactionForMemberCenter.setProjectStatus(project.getStatus());
				this.projectCheck(transactionForMemberCenter, project);
				transactionForMemberCenter.setOperaType(1);
				
				if(project.getTransferFlag()==1){
					transactionForMemberCenter.setOperaType(2);
				}
				//设置是否发起过转让
				TransferProject trfProject = transferProjectManager.selectByTransactionId(transactionForMemberCenter.getTransactionId());
				//交易的实际支付收益券产生的利息和、实付利息
				TransactionForMemberCenter transactionForMemberCenter1= transactionInterestManager.queryTotalInterestByTransactionId(transactionForMemberCenter.getTransactionId());

				transactionForMemberCenter.setTotalRealPayInterest(transactionForMemberCenter1!=null?transactionForMemberCenter1.getTotalRealPayInterest():BigDecimal.ZERO);
				transactionForMemberCenter.setTotalRealPayExtraInterest(transactionForMemberCenter1!=null?transactionForMemberCenter1.getTotalRealPayExtraInterest():BigDecimal.ZERO);
				if(trfProject!=null){
					transactionForMemberCenter.setOperaType(3);
				}
				transactionForMemberCenter.setAnnualizedRate(transactionForMemberCenter.getAnnualizedRate()
						.add(transactionForMemberCenter.getExtraProjectAnnualizedRate()));
				
				if(transactionForMemberCenter.getExtraInterestDay()!=null&&transactionForMemberCenter.getExtraInterestDay()>0){
//					BigDecimal extraInterest=FormulaUtil.calculateInterest(transactionForMemberCenter.getInvestAmount(),transactionForMemberCenter.getExtraAnnualizedRate(),transactionForMemberCenter.getExtraInterestDay());
//					if(transactionForMemberCenter.getStatus()==2){
//						extraInterest=transactionForMemberCenter.getTotalRealPayExtraInterest();
//					}
					//使用实际支付收益券产生的利息和,以上内容注释
					BigDecimal extraInterest=transactionForMemberCenter.getTotalRealPayExtraInterest();
					if(extraInterest==null){
						extraInterest=BigDecimal.ZERO;
					}
					transactionForMemberCenter.setTotalExtraInterest(extraInterest);
//					transactionForMemberCenter.setAllInterest(transactionForMemberCenter.getReceivedInterest());
					//实付利息
					transactionForMemberCenter.setAllInterest(transactionForMemberCenter.getTotalRealPayInterest());
					transactionForMemberCenter.setTotalInterest(transactionForMemberCenter.getTotalRealPayInterest().subtract(extraInterest));
				}else if(transactionForMemberCenter.getExtraAnnualizedRate()!=null){
//					BigDecimal extraInterest=BigDecimal.ZERO;
//					if(project.isDirectProject()){
//						extraInterest=projectManager.invertExtraInterest(project, transactionForMemberCenter.getInvestAmount(), transactionForMemberCenter.getTransactionTime(), transactionForMemberCenter.getExtraAnnualizedRate());
//					}else{
//						extraInterest=transactionForMemberCenter.getTotalRealPayExtraInterest();
//					}
//
//					if(transactionForMemberCenter.getStatus()==2){
//						extraInterest=transactionForMemberCenter.getReceivedExtraInterest();
//					}
					//使用实际支付收益券产生的利息和，以上内容注释
					BigDecimal extraInterest=transactionForMemberCenter.getTotalRealPayExtraInterest();
					if(extraInterest==null){
						extraInterest=BigDecimal.ZERO;
					}
					transactionForMemberCenter.setTotalExtraInterest(extraInterest);
					transactionForMemberCenter.setAllInterest(transactionForMemberCenter.getTotalRealPayInterest());
					transactionForMemberCenter.setTotalInterest(transactionForMemberCenter.getTotalRealPayInterest().subtract(extraInterest));
				}
//				if (transactionForMemberCenter.getInterestStatus()!=null){
//					if (transactionForMemberCenter.getInterestStatus()==1){
//						transactionForMemberCenter.setStatus(2);
//					}else {
//						transactionForMemberCenter.setStatus(1);
//					}
//				}

				//转让状态为1转让中时 查询转让项目是否有人投资了
				if (transactionForMemberCenter.getTransferStatus()==1){
					if (myTransactionManager.queryTransactionTransferCount(transactionForMemberCenter.getTransactionId())>0){
						transactionForMemberCenter.setTransferStatus(2);
					}
				}
			}
			return page;
		} catch (Exception e) {
			logger.error("查询用户中心交易记录，TransactionQuery：" + query, e);
		}
		return null;
	}
	
	private void projectCheck(TransactionForMemberCenter transactionForMemberCenter,Project project) throws ManagerException{
		//是否为P2P项目
		if(project.isDirectProject()){
 			transactionForMemberCenter.setProfitPeriod(project.getProfitPeriod());
			transactionForMemberCenter.setInterestFrom(project.getInterestFrom());
		}else{
			transactionForMemberCenter.setProfitPeriod(transactionForMemberCenter.getTotalDays()+"天");
		}
		//转让项目交易
		if(transactionForMemberCenter.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
			TransferProject trfProject = transferProjectManager.selectByPrimaryKey(transactionForMemberCenter.getTransferId());
			if(trfProject!=null){
				transactionForMemberCenter.setProfitPeriod(transferProjectManager.getReturnDay(trfProject.getTransactionId())+"天");
			}
		}
		
		if(StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()==project.getStatus()){//已还款，显示实收收益
			transactionForMemberCenter.setTotalInterest(transactionForMemberCenter.getReceivedInterest());
		}else{
			transactionForMemberCenter.setTotalInterest(transactionForMemberCenter.getTotalInterest());
		}
		//不计算实际时间
		/*if(StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()==project.getStatus()&&1==project.getPrepayment()){
			Map<String,Object> map = Maps.newHashMap();
			map.put("projectId", transactionForMemberCenter.getProjectId());
			map.put("transactionId", transactionForMemberCenter.getTransactionId());
			map.put("memberId", transactionForMemberCenter.getMemberId());
			TransactionInterest transactionInterest = transactionInterestManager.getPreTransactionInterest(map);
			transactionForMemberCenter.setProfitPeriod(DateUtils.daysOfTwo(project.getStartDate(),transactionInterest.getPayTime())+"天");
		}*/
	}
	
	private void p2pProjectCheck(TransactionForMemberCenter transactionForMemberCenter,Project project){
		//是否为P2P项目
		if(project.isDirectProject()){
 			transactionForMemberCenter.setProfitPeriod(project.getProfitPeriod());
			transactionForMemberCenter.setInterestFrom(project.getInterestFrom());
			transactionForMemberCenter.setProjectStatus(project.getStatus());
		}
	}

	@Override
	public ResultDO<TransactionForMemberCenter> getTransactionForMemberCenter(Long transactionId,Long memberId) {
		ResultDO<TransactionForMemberCenter> result = new ResultDO<TransactionForMemberCenter>();
		try {
			TransactionForMemberCenter transactionForMemberCenter = myTransactionManager
					.getTransactionForMember(transactionId,memberId);

			//交易的实际支付收益券产生的利息和、实付利息
			TransactionForMemberCenter transactionForMemberCenter1= transactionInterestManager.queryTotalInterestByTransactionId(transactionForMemberCenter.getTransactionId());
			transactionForMemberCenter.setTotalRealPayInterest(transactionForMemberCenter1!=null?transactionForMemberCenter1.getTotalRealPayInterest():BigDecimal.ZERO);
			transactionForMemberCenter.setTotalRealPayExtraInterest(transactionForMemberCenter1!=null?transactionForMemberCenter1.getTotalRealPayExtraInterest():BigDecimal.ZERO);
			//租赁分红明细
			List<LeaseBonusDetail> details = leaseBonusDetailManager.findListByTransactionId(transactionId);
			if(Collections3.isNotEmpty(details)){
				transactionForMemberCenter.setLeaseBonusDetails(details);
			}
			//租赁总额
			LeaseBonusDetail bonusDetail = leaseBonusDetailManager.getLeaseTotalIncomeByTransactionId(transactionId); 
			if(bonusDetail!=null){
				transactionForMemberCenter.setLeaseBonusAmounts(bonusDetail.getBonusAmount());
				transactionForMemberCenter.setBonusAnnualizedRate(bonusDetail.getBonusRate());
			}else {
				transactionForMemberCenter.setLeaseBonusAmounts(BigDecimal.ZERO);
				transactionForMemberCenter.setBonusAnnualizedRate(BigDecimal.ZERO);
			}
			Project project = projectManager.selectByPrimaryKey(transactionForMemberCenter.getProjectId());
			transactionForMemberCenter.setInvestType(project.getInvestType());
			transactionForMemberCenter.setAnnualizedRate(transactionForMemberCenter.getAnnualizedRate()
					.add(transactionForMemberCenter.getExtraProjectAnnualizedRate()));
			if(transactionForMemberCenter!=null&&transactionForMemberCenter.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_NORMAL.getType()){
				this.p2pProjectCheck(transactionForMemberCenter, project);
				if(transactionForMemberCenter.getExtraInterestDay()!=null&&transactionForMemberCenter.getExtraInterestDay()>0){
					if(transactionForMemberCenter.getTotalExtraInterest()!=null){
						transactionForMemberCenter.setTotalInterest(transactionForMemberCenter.getTotalInterest());
					}
					
				}
			}else if(transactionForMemberCenter!=null&&transactionForMemberCenter.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
				TransferProject transferProject =transferProjectManager.selectByPrimaryKey(transactionForMemberCenter.getTransferId());
				transactionForMemberCenter.setResidualDays(transferProjectManager.getReturnDay(transferProject.getTransactionId()));
				transactionForMemberCenter.setTotalInterest(transactionForMemberCenter.getTotalInterest());
				//预计赚取， 通过总还本付息金额-总支付
				BigDecimal expectedEarning = transactionForMemberCenter.getTotalInterest().add(transactionForMemberCenter.getTotalPrincipal())
						.subtract(transactionForMemberCenter.getInvestAmount());
				transactionForMemberCenter.setExpectedEarning(expectedEarning);
			}
		
			
			result.setResult(transactionForMemberCenter);
			return result;
		} catch (Exception e) {
			result.setSuccess(false);
			logger.error("获取交易详情发生异常，transactionId：" + transactionId, e);
		}
		return null;
	}

	@Override
	public ResultDO<OrderPayDto> getTransactionByOrderId(Long orderId) {
		ResultDO<OrderPayDto> result = new ResultDO<OrderPayDto>();
		OrderPayDto dto = new OrderPayDto();
		result.setResult(dto);
		try {
			Order order = orderManager.selectByPrimaryKey(orderId);
			if (order != null) {
				Order retOrder = new Order();
				retOrder.setId(order.getId());
				retOrder.setStatus(order.getStatus());
				dto.setOrder(retOrder);
			}
			Transaction transaction = myTransactionManager.getTransactionByOrderId(orderId);
			if (transaction == null) {
				return result;
			}
			TransactionDto transactionDto = BeanCopyUtil.map(transaction, TransactionDto.class);
			if (transactionDto.getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
				TransferProject tPro = transferProjectManager.selectByPrimaryKey(transactionDto.getTransferId());
				transactionDto.setInterestFrom(tPro.getInterestFrom());
				dto.setTransactionDto(transactionDto);
				return result;
			}
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			transactionDto.setDirectProject(project.isDirectProject());
			transactionDto.setInterestFrom(project.getInterestFrom());
			dto.setTransactionDto(transactionDto);
		} catch(MappingException e ){
			logger.error("通过订单id获取交易信息发生异常,transaction空，orderId：" + orderId);
		} catch (Exception e) {
			result.setSuccess(false);
			logger.error("通过订单id获取交易信息发生异常，orderId：" + orderId, e);
		}
		return result;
	}

	@Override
	public ResultDO<ContractBiz> previewContract(Long memberId, Long projectId, BigDecimal investAmount,
			BigDecimal annualizedRate) {
		ResultDO<ContractBiz> result = new ResultDO<ContractBiz>();
		try {
			ContractBiz contractBiz = contractManager.getPreviewContract(memberId, projectId, investAmount,
					annualizedRate, DateUtils.getCurrentDate());
			result.setResult(contractBiz);
			return result;
		} catch (Exception e) {
			result.setSuccess(false);
			logger.error("合同预览发生异常，projectId：" + projectId + ", memberId=" + memberId, e);
		}
		return result;
	}

	@Override
	public ResultDO<String> downloadContract(Long memberId, Long transactionId) {
		ResultDO<String> result = new ResultDO<String>();
		try {
			// 判断该交易是否是属于该用户
			Transaction transaction = myTransactionManager.selectTransactionById(transactionId);
			if (transaction == null) {
				result.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
				return result;
			}
			if (!transaction.getMemberId().equals(memberId)) {
				result.setResultCode(ResultCode.TRANSACTION_NOT_BELONG_TO_MEMBER_ERROR);
				return result;
			}
			// 判断密码是否正确
			// Member member = memberManager.selectByPrimaryKey(memberId);
			// String mobile = member.getMobile().toString();
			// String localPassword = mobile.substring(mobile.length()-6,
			// mobile.length());
			// if(!localPassword.equals(password)) {
			// result.setResultCode(ResultCode.MEMBER_DOWNLOAD_CONTRACT_PASSWORD_NOT_EQUAL);
			// return result;
			// }
			BscAttachment attachment = bscAttachmentManager.getBscAttachmentByKeyIdAndModule(transactionId.toString(),DebtEnum.ATTACHMENT_MODULE_CONTRACT.getCode());
			String downloadUrl = OSSUtil.getContractDownloadUrl(attachment.getFileUrl() + attachment.getFileName(),null);
			result.setResult(downloadUrl);
			return result;
		} catch (Exception e) {
			result.setSuccess(false);
			logger.error("下载合同发生异常，memberId：" + memberId, e);
		}
		return result;
	}

	@Override
	public int getTransactionCount(Long memberId, int type) {
		try {
			return myTransactionManager.getTransactionCount(memberId, type);
		} catch (Exception e) {
			logger.error("查询交易记录总数发生异常，memberId：" + memberId, e);
		}
		return 0;
	}

	@Override
	public ResultDO<?> afterPaltformCouponCollectNotify(String tradeNo, String outTradeNo, String tradeStatus) {
		try {
			return myTransactionManager.afterPaltformCouponCollectNotify(tradeNo, outTradeNo, tradeStatus);
		} catch (Exception e) {
			logger.error("代收平台垫付优惠券回调处理方法发生异常，tradeNo：" + tradeNo, e);
		}
		return null;
	}

	@Override
	public ResultDO<ContractBiz> viewContract(Long orderId, Long memberId) {
		ResultDO<ContractBiz> result = new ResultDO<ContractBiz>();
		try {
			// TODO 前置校验
			Order order = orderManager.selectByPrimaryKey(orderId);
			if(order==null){
				result.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
				return result;
			}
			if(!order.getMemberId().equals(memberId)){
				result.setResultCode(ResultCode.TRANSACTION_NOT_BELONG_TO_MEMBER_ERROR);//交易不属于该会员，则返回404
				return result;
			}
			if (order.getStatus() == StatusEnum.ORDER_PAYED_INVESTED.getStatus()) {
				Transaction transaction = myTransactionManager.getTransactionByOrderId(orderId);							
				if (transaction == null) {
					result.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
					return result;
				}

//				if (!transaction.getMemberId().equals(memberId)) {
//					result.setResultCode(ResultCode.TRANSACTION_NOT_BELONG_TO_MEMBER_ERROR);
//					return result;
//				}
				ContractBiz contractBiz = contractManager.getViewContract(transaction.getId());
				Map<String,Object> mapPara =new HashMap<String,Object>();
				mapPara.put("transactionId", transaction.getId());
				mapPara.put("sourceId", memberId);
				ContractSign contractSign = contractSignMapper.getByMap(mapPara);
				if(contractSign!=null){
					String url = BestSignUtil.getContractArbitrationURL(contractSign.getMobile().toString(),contractSign.getSignId());
					contractBiz.setPreservationLink(url);
				}
				//投资完成展示保全链接
//				Preservation preservation =  preservationService.selectByTransactionId(transaction.getId());
//				if(preservation != null && preservation.getPreservationId() != null && preservation.getPreservationId() > 0) {
//					Preservation retPreservation = preservationService.getPreservationLink(preservation.getPreservationId());
//					if(retPreservation != null && StringUtil.isNotBlank(retPreservation.getPreservationLink())) {
//						contractBiz.setPreservationLink(retPreservation.getPreservationLink());					
//					}
//				}
				result.setResult(contractBiz);
			} else {
				BigDecimal annualizedRate = order.getAnnualizedRate();
				if (order.getExtraAnnualizedRate() != null) {
					annualizedRate = annualizedRate.add(order.getExtraAnnualizedRate()).setScale(2,
							BigDecimal.ROUND_HALF_UP);
				}
				ContractBiz contractBiz = contractManager.getPreviewContract(memberId, order.getProjectId(),
						order.getInvestAmount(), annualizedRate, order.getOrderTime());
				result.setResult(contractBiz);
			}

			return result;
		} catch (Exception e) {
			result.setSuccess(false);
			logger.error("查看合同发生异常，orderId：" + orderId, e);
		}
		return result;
	}
	
	@Override
	public ResultDO<ContractBiz> p2pViewContract(Long orderId, Long memberId) {
		ResultDO<ContractBiz> result = new ResultDO<ContractBiz>();
		try {
			return myTransactionManager.p2pViewContract(orderId, memberId);
		} catch (Exception e) {
			result.setSuccess(false);
			logger.error("查看合同发生异常，orderId：" + orderId, e);
		}
		return result;
	}

	@Override
	public int getTransactionCountByProject(Long projectId) {
		try {
			return myTransactionManager.getTransactionCountByProject(projectId);
		} catch (Exception e) {
			logger.error("通过项目id查询投资笔数发生异常,projectId= " + projectId, e);
		}
		return 0;
	}
	
	@Override
	public int getTransactionCountByTransferId(Long transferId) {
		try {
			return myTransactionManager.getTransactionCountByTransferId(transferId);
		} catch (Exception e) {
			logger.error("通过转让项目id查询投资笔数发生异常,transferId= " + transferId, e);
		}
		return 0;
	}
	
	
	
	@Override
	public int getTransactionMemberCountByTransferId(Long transferId) {
		try {
			return myTransactionManager.getTransactionMemberCountByTransferId(transferId);
		} catch (Exception e) {
			logger.error("通过转让项目id查询投资会员数发生异常,transferId= " + transferId, e);
		}
		return 0;
	}

	@Override
	public int getTransactionMemberCountByProject(Long projectId) {
		try {
			return myTransactionManager.getTransactionMemberCountByProject(projectId);
		} catch (Exception e) {
			logger.error("通过项目id查询投资会员数发生异常,projectId= " + projectId, e);
		}
		return 0;
	}

	@Override
	public BigDecimal getTotalTransactionInterestByProject(Long projectId) {
		try {
			return myTransactionManager.getTotalTransactionInterestByProject(projectId);
		} catch (Exception e) {
			logger.error("通过项目id查询该项目总收益发生异常,projectId= " + projectId, e);
		}
		return BigDecimal.ZERO;
	}
	
	@Override
	public BigDecimal getTotalTransactionInterestByTransferId(Long transferId) {
		try {
			return myTransactionManager.getTotalTransactionInterestByTransferId(transferId);
		} catch (Exception e) {
			logger.error("通过转让项目id查询该项目总收益发生异常,transferId= " + transferId, e);
		}
		return BigDecimal.ZERO;
	}
	
	

	@Override
	public List<ActivityForKing> getRefferalInvestAmountList() {
		try {
			List<ActivityForKing> activityForKings = myTransactionManager.getRefferalInvestAmountList();
			if (Collections3.isNotEmpty(activityForKings)) {
				for (ActivityForKing activityForKing : activityForKings) {
					Member member = memberManager.selectByPrimaryKey(activityForKing.getMemberId());
					activityForKing.setUsername(StringUtil.maskUserNameOrMobile(member.getUsername(),
							member.getMobile()));
				}
			}
			return activityForKings;
		} catch (Exception e) {
			logger.error("我是王推荐好友投资额排行榜发生异常", e);
		}
		return null;
	}

	@Override
	public List<TransactionForFirstInvestAct> selectTopTenInvest() {
		try {
			return myTransactionManager.selectTopTenInvest();
		} catch (Exception e) {
			logger.error("查询投资排行前10异常", e);
		}
		return null;
	}

	@Override
	public boolean hasTransactionByMember(Long memberId) {
		try {
			return  myTransactionManager.isMemberInvested(memberId);
		} catch (Exception e) {
			logger.error("查询用户是否投资异常", e);
		}
		return false;
	}

	@Override
	public ActivityForRankingList2nd getMemberMeetTotalInvestRange(BigDecimal minAmount,BigDecimal maxAmount,Integer limitNum) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("startTime", Config.rankingStartTime);
		map.put("endTime", Config.rankingEndTime);
		if(minAmount!=null){
			map.put("minAmount", minAmount);
		}
		if(maxAmount!=null){
			map.put("maxAmount", maxAmount);
		}
		if(limitNum!=null){
			map.put("limitNum", limitNum);
		}
		ActivityForRankingList2nd list2nd = new ActivityForRankingList2nd();
		try {
			List<TransactionForFirstInvestAct> list = myTransactionManager.getMemberMeetTotalInvestRange(map);
			if(Collections3.isNotEmpty(list)){
				list2nd.setRankingList(list);
				list2nd.setMemberCount(myTransactionManager.getCountMemberMeetTotalInvestRange(map));
			}
		} catch (Exception e) {
			logger.error("获取满足投资总额在某一范围内的用户失败,map" + map, e);
		}
		return list2nd;
	}

	@Override
	public int getCountMemberMeetTotalInvestRange(Map<String, Object> map) {
		try {
			return myTransactionManager.getCountMemberMeetTotalInvestRange(map);
		} catch (Exception e) {
			logger.error("获取满足投资总额在某一范围内的用户总数失败,map" + map, e);
		}
		return 0;
	}

	@Override
	public ActivityMonthlyMyRank findMyRank(Map<String, Object> map) {
		try {
			map.put("startTime", WebPropertiesUtil.getProperties("activity.monthlyStartTime"));
			map.put("endTime", WebPropertiesUtil.getProperties("activity.monthlyEndTime"));
			return myTransactionManager.findMyRank(map);
		} catch (Exception e) {
			logger.error("查看我排名失败,map" + map, e);
		}
		return null;
	}
	
	@Override
	public ActivityForRankingList2nd getMonthlyMemberMeetTotalInvestRange(BigDecimal minAmount,BigDecimal maxAmount,Integer limitNum) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("startTime", WebPropertiesUtil
				.getProperties("activity.monthlyStartTime"));
		map.put("endTime", WebPropertiesUtil
				.getProperties("activity.monthlyEndTime"));
		if(minAmount!=null){
			map.put("minAmount", minAmount);
		}
		if(maxAmount!=null){
			map.put("maxAmount", maxAmount);
		}
		if(limitNum!=null){
			map.put("limitNum", limitNum);
		}
		map.put("transactionOrder", "ASC");
		ActivityForRankingList2nd list2nd = new ActivityForRankingList2nd();
		try {
			List<TransactionForFirstInvestAct> list = myTransactionManager.getMemberMeetTotalInvestRange(map);
			if(Collections3.isNotEmpty(list)){
				list2nd.setRankingList(list);
			}
		} catch (Exception e) {
			logger.error("获取满足投资总额在某一范围内的用户失败,map" + map, e);
		}
		return list2nd;
	}

	@Override
	public ResultDO<ActivityForInvest> getInvestHouseList() {
		ResultDO<ActivityForInvest> res = new ResultDO<ActivityForInvest>();
		try {
			String activityIdStr = PropertiesUtil.getProperties("activity.investHouseHappy.activityId");
			Activity ac = activityManager.selectByPrimaryKey(Long.parseLong(activityIdStr));
			if(ac == null || (ac.getActivityStatus() < 4)) {
				return res;
			}
			//查询投资列表
			if(DateUtils.getCurrentDate().after(ac.getStartTime())) {
				ActivityForInvest param = new ActivityForInvest();
				String queryLimit = PropertiesUtil.getProperties("activity.investHouseHappy.queryNum");
				param.setStartTime(ac.getStartTime());
				param.setEndTime(ac.getEndTime());
				param.setQueryLimit(Integer.parseInt(queryLimit));
				param.setGuarantyType(new String[] {"house", "houseRecord"});
				List<Transaction> returnList = myTransactionManager.getInvestHouseList(param);
				if(Collections3.isNotEmpty(returnList)) {
					List<ActivityForInvest> newList = new ArrayList<ActivityForInvest>();
					for(Transaction t : returnList) {
						ActivityForInvest invest = new ActivityForInvest();
						invest.setUsername(ServletUtil.getMemberUserName(t.getMemberId()));
						invest.setAvatar(ServletUtil.getMemberAvatarById(t.getMemberId()));
						invest.setInvestAmount(t.getInvestAmount());
						newList.add(invest);
					}
					res.setResultList(newList);
				}
			}
		} catch (Exception e) {
			logger.error("获取投房有喜投资列表失败", e);
			res.setSuccess(false);
		}
		return res;
	}

	@Override
	public ResultDO<ActivityForInvest> getMyInvestHouseRank(Long memberId) {
		ResultDO<ActivityForInvest> res = new ResultDO<ActivityForInvest>();
		try {
			String activityIdStr = PropertiesUtil.getProperties("activity.investHouseHappy.activityId");
			Activity ac = activityManager.selectByPrimaryKey(Long.parseLong(activityIdStr));
			if(ac == null) {
				res.setSuccess(false);
				return res;
			}
			ActivityForInvest myRank = new ActivityForInvest();
			myRank.setActivityStatus(ac.getActivityStatus());
			if(ac.getActivityStatus() != 4) {
				//活动已结束
				res.setResult(myRank);
				return res;
			}
			//查询我的排名
			if(DateUtils.getCurrentDate().after(ac.getStartTime())) {
				ActivityForInvest param = new ActivityForInvest();
				param.setStartTime(ac.getStartTime());
				param.setEndTime(ac.getEndTime());
				param.setMemberId(memberId);
				param.setGuarantyType(new String[] {DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_HOUSE.getCode(), DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_HOUSERECORD.getCode()});
				ActivityForInvest retRank = myTransactionManager.getMyInvestHouseRank(param);
				if(retRank != null) {
					myRank = retRank;
					myRank.setMemberId(null);
				} 
			}
			myRank.setActivityStatus(ac.getActivityStatus());
			res.setResult(myRank);
		} catch (Exception e) {
			logger.error("获取投房有喜投资列表失败", e);
			res.setSuccess(false);
		}
		return res;
	}

	@Override
	public ResultDO<ActivityForInvest> getInvestHouseCoupon() {
		ResultDO<ActivityForInvest> res = new ResultDO<ActivityForInvest>();
		try {
			//查询剩余优惠券
			String couponTemplateId = PropertiesUtil.getProperties("activity.investHouseHappy.couponTemplateId");
			CouponTemplatePrint couponInfo = couponTemplatePrintManager.selectByTemplateId(Long.parseLong(couponTemplateId));
			if(couponInfo != null) {
				ActivityForInvest coupon = new ActivityForInvest();
				int surplusCoupon = couponInfo.getPrintNum() - couponInfo.getReceivedNum();
				coupon.setSentCoupon(couponInfo.getReceivedNum());
				coupon.setSurplusCoupon(surplusCoupon);
				res.setResult(coupon);
			} else {
				logger.error("获取投房有喜投资列表失败, 优惠券数据异常");
				res.setSuccess(false);
			}
		} catch (Exception e) {
			logger.error("获取投房有喜投资列表失败", e);
			res.setSuccess(false);
		}
		return res;
	}

	@Override
	public ResultDO getTransactionsDetailForMember(TransactionInterestQuery query) {
		ResultDO result = new ResultDO();
		try {
			List<TransactionInterestForMember> detailList = myTransactionManager.getTransactionsDetailForMember(query);
			if(Collections3.isNotEmpty(detailList)){
				List<TransactionInterestForMemberDto> detailDtoList =BeanCopyUtil.mapList(detailList, TransactionInterestForMemberDto.class);
				result.setResult(detailDtoList);
			}
		} catch (ManagerException e) {
			logger.error("查看用户交易本金明细发生异常" , e);
			result.setSuccess(false);
		}
		return result;
	}
	
	@Override
	public Page<DebtForLenderMember> getDebtInfoByLenderId(DebtForLenderQuery debtForLenderQuery) {
		try {
			Page<DebtForLenderMember> page = myTransactionManager.getDebtInfoByLenderId(debtForLenderQuery);
			List<DebtForLenderMember> debtForLenderMembers=page.getData();
			for(DebtForLenderMember debtForLenderMember :debtForLenderMembers){
				this.debtComplement(debtForLenderMember);
			}
			return page;
		} catch (Exception e) {
			logger.error("查询用户中心债权管理，debtForLenderQuery：" + debtForLenderQuery, e);
		}
		return null;
	}
	
	private void debtComplement(DebtForLenderMember debtForLenderMember) throws ManagerException{
		
		Long  projectId = debtForLenderMember.getProjectId();
		// 总期数
		Map<String, Object> map = Maps.newHashMap();
		map.put("projectId", projectId);
		int totalPeriods = debtInterestManager.findPeriodsByProjectId(map);
		debtForLenderMember.setTotalPeriods(totalPeriods);
		/*//当前项目债权还本付息状态
		DebtInterest debtInterest  =debtInterestManager.findStatusByProjectId(map);
		debtForLenderMember.setStatus(debtInterest.getStatus());*/
		// 当前期数
		map.put("endDate", debtForLenderMember.getEndDate());
		int paidPeriods = debtInterestManager.findPeriodsByProjectId(map);
		debtForLenderMember.setCurrentPeriods(paidPeriods);
		
		//计算当期显示金额
		BigDecimal CurAmount = BigDecimal.ZERO; 
		List<TransactionInterest> transactionInterests = transactionInterestManager.queryInterestAndPrincipalFlagByProjectId(projectId);
		TransactionInterest transactionInterestsPaidPeriods = transactionInterests.get(paidPeriods-1);
		debtForLenderMember.setCurAmount(transactionInterestsPaidPeriods.getFormatSum());
		debtForLenderMember.setPayableInterest(transactionInterestsPaidPeriods.getPayableInterest());
		debtForLenderMember.setPayablePrincipal(transactionInterestsPaidPeriods.getPayablePrincipal());
	}
	
	@Override
	public DebtForLenderMember getStatisticalDataByLenderId(DebtForLenderQuery debtForLenderQuery) {
		try {
			DebtForLenderMember debtForLenderMember = myTransactionManager.getStatisticalDataByLenderId(debtForLenderQuery);
			return debtForLenderMember;
		} catch (Exception e) {
			logger.error("获取会员出借人身份名下的债权项目数量，本息金额,debtForLenderQuery" + debtForLenderQuery, e);
		}
		return null;
	}
	@Override
	public ResultDO<DebtForLenderMember> getTransactionInterestDetail(Long projectId) {
		ResultDO<DebtForLenderMember> result = new ResultDO<DebtForLenderMember>();
		try {
			DebtForLenderMember debtForLenderMember = new DebtForLenderMember();
			List<TransactionInterest> transactionInterests = transactionInterestManager.queryInterestAndPrincipalFlagByProjectId(projectId);
			for(TransactionInterest transactionInterest:transactionInterests){
				BigDecimal payableInterest = transactionInterest.getPayableInterest();
				BigDecimal payablePrincipal = transactionInterest.getPayablePrincipal();
				transactionInterest.setRemarks("3");//都不为零表示还款为本息
				if(payableInterest==null||payableInterest.compareTo(BigDecimal.ZERO)==0){//利息为零表示还款为本金
					transactionInterest.setRemarks("2");
				}
				if(payablePrincipal==null||payablePrincipal.compareTo(BigDecimal.ZERO)==0){//本金为零表示还款为利息
					transactionInterest.setRemarks("1");
				}
			}
			debtForLenderMember.setTransactionInterests(transactionInterests);
			result.setResult(debtForLenderMember);
			return result;
		} catch (Exception e) {
			result.setSuccess(false);
			logger.error("获取债权本息数据，projectId：" + projectId, e);
		}
		return null;
	}

	@Override
	public BigDecimal getTotalTransactionReceivedInterestByProject(Long projectId) {
		try {
			return myTransactionManager.getTotalTransactionReceivedInterestByProject(projectId);
		} catch (Exception e) {
			logger.error("通过项目id查询该项目实际总收益发生异常,projectId= " + projectId, e);
		}
		return BigDecimal.ZERO;
	}
	

	@Override
	public BigDecimal getTotalTransactionReceivedInterestByTransferId(Long transferId) {
		try {
			return myTransactionManager.getTotalTransactionReceivedInterestByTransferId(transferId);
		} catch (Exception e) {
			logger.error("通过转让项目id查询该项目实际总收益发生异常,transferId= " + transferId, e);
		}
		return BigDecimal.ZERO;
	}
	
	

	@Override
	public boolean afterHostingPayBatchNotifyProcess(String batchNo,
			String outBatchNo, String tradeList) {
		try {
			if(StringUtil.isNotBlank(tradeList)) {
				List<TradeItem> payItemList = ResponseJsonConverter.convert2TradeItemList2(tradeList);
				if(Collections3.isNotEmpty(payItemList)) {
					for (TradeItem tradeItem : payItemList) {
						if(TradeStatus.TRADE_FINISHED.name().equals(tradeItem.getProcessStatus())
								||TradeStatus.TRADE_CLOSED.name().equals(tradeItem.getProcessStatus())
								||TradeStatus.TRADE_FAILED.name().equals(tradeItem.getProcessStatus())) {
							 ResultDO<TransactionInterest> result = afterHostingPayHandleManager.afterPayInterestAndPrincipal(tradeItem.getProcessStatus(), tradeItem.getTradeNo(),null);
					            if(result.isSuccess()&&result.getResult()!=null) {
					            	if(result.getResult()!=null){
					            		afterHostingPayHandleManager.afterHostingPayTradeSucess(result.getResult());
					            	}
					            }
						}
					}
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("代付批次回调业务处理发生异常,batchNo= " + batchNo, e);
		}
		return false;
	}
	
	@Override
	public ResultDO<Object> signAllContractByType(Long memberId,Integer type) {
		ResultDO<Object> result = new ResultDO<Object>();
		try {
			int num = myTransactionManager.signAllContractByType(memberId,type);
			if(num<1){
				result.setResultCode(ResultCode.CONTRACT_SIGN_FAIL_TO_INIT);
				result.setSuccess(false);
				return result;
			}
			result.setResult(num);
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("一键签署异常,memberId= " + memberId, e);
			result.setResultCode(ResultCode.ERROR);
		}
		return result;
	}
	
	@Override
	public ResultDO<Object> signContract(Long transactionId) {
		ResultDO<Object> result = new ResultDO<Object>();
		try {
			ResultDO<Object> queryContractInfoResult = myTransactionManager.queryContractInfo(transactionId);
			if(queryContractInfoResult.isSuccess()&&Integer.valueOf(queryContractInfoResult.getResult().toString())>0){
				result.setResultCode(ResultCode.CONTRACT_SIGN_ALREADY_SIGN);
				result.setSuccess(false);
				return result;
			}
			
			Transaction transaction = myTransactionManager.selectTransactionById(transactionId);
			if(transaction.getSignStatus()==StatusEnum.CONTRACT_STATUS_INIT.getStatus()){
				result.setResultCode(ResultCode.CONTRACT_SIGN_FAIL_TO_INIT);
				result.setSuccess(false);
				return result;
			}
			
			if(transaction.getSignStatus()==StatusEnum.CONTRACT_STATUS_ALREADY_SIGN.getStatus()){
				result.setResultCode(ResultCode.CONTRACT_SIGN_ALREADY_SIGN);
				result.setSuccess(false);
				return result;
			} 
			
			if(transaction.getSignStatus()==StatusEnum.CONTRACT_STATUS_EXPIRED.getStatus()){
				result.setResultCode(ResultCode.CONTRACT_SIGN_EXPIRED);
				result.setSuccess(false);
				return result;
			} 
			
			String signUrl = myTransactionManager.signContract(transactionId,StatusEnum.CONTRACT_SIGN_WAY_PC.getStatus(),StatusEnum.CONTRACT_MANUAL_WAY_PC.getStatus());
			if(StringUtil.isBlank(signUrl)){
				result.setResultCode(ResultCode.CONTRACT_SIGN_FAIL_TO_INIT);
				result.setSuccess(false);
				return result;
			}
			result.setResult(signUrl);
		} catch (Exception e) {
			logger.error("手动签署异常,transactionId =" + transactionId, e);
		}
		return result;
	}
	
	@Override
	public void afterTransactionContract(Transaction t) {
		try {
			myTransactionManager.afterTransactionContract(t);
		} catch (Exception e) {
			Long transactionId = 0l;
			if (t != null)
				transactionId = t.getId();
			logger.error("处理交易后的合同初始化业务失败 transactionId={}", transactionId, e);
		}
	}
	
	@Override
	public ResultDO<Object> signContractAuto(Long memberId,
			Long transactionId) {
		ResultDO<Object> result = new ResultDO<Object>();
		try {
			Transaction transaction = myTransactionManager.selectTransactionById(transactionId);
			if(!transaction.getMemberId().equals(memberId)){
				result.setResultCode(ResultCode.CONTRACT_SIGN_MEMBER_NOT_MATCH);
				result.setSuccess(false);
				return result;
			}
			
			int num =  myTransactionManager.signContractAuto(transactionId);
			if(num<1){
				result.setResultCode(ResultCode.CONTRACT_SIGN_FAIL_TO_INIT);
				result.setSuccess(false);;
				return result;
			}
			//乙方签署成功，更改交易状态
			Transaction tra = new Transaction();
			tra.setId(transactionId);
			tra.setSignStatus(StatusEnum.CONTRACT_STATUS_ALREADY_SIGN.getStatus());
			try {
				myTransactionManager.updateByPrimaryKeySelective(tra);
			} catch (ManagerException e) {
				logger.error("自动签名：更新交易表合同签署状态异常,transactionId={}" , transactionId,e);
			}
			
			result.setResult(num);
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("自动签署异常,transactionId =" + transactionId, e);
		}
		return result;
	}
	
	@Override
	public ResultDO<Object> getContractDownUrl(Long memberId,
			Long transactionId) {
		ResultDO<Object> result = new ResultDO<Object>();
		try {
			memberBehaviorLogManager.logInsert(memberId, transactionId.toString(), MemberLogEnum.MEMBER_BEHAVIOR_TYPE_DOWN_CONTRACT.getType(),
					MemberLogEnum.MEMBER_BEHAVIOR_WEB.getType(), "");
			
			Transaction transaction = myTransactionManager.selectTransactionById(transactionId);
			if(!transaction.getMemberId().equals(memberId)){
				result.setResultCode(ResultCode.CONTRACT_SIGN_MEMBER_NOT_MATCH);
				result.setSuccess(false);
				return result;
			}
			
			String downUrl = myTransactionManager.getContractDownUrl(transactionId);
			if(StringUtil.isBlank(downUrl)){
				result.setResultCode(ResultCode.CONTRACT_SIGN_FAIL_TO_INIT);
				result.setSuccess(false);
				return result;
			}
			result.setResult(downUrl);
		} catch (Exception e) {
			logger.error("合同下载异常,transactionId =" + transactionId, e);
		}
		return result;
	}
	
	@Override
	public int getUnsignContractNum(Long memberId) {
		try {
			return myTransactionManager.getUnsignContractNum(memberId);
		} catch (Exception e) {
			logger.error("获取用户未签署合同数量失败,memberId" + memberId, e);
		}
		return 0;
	}
	
	@Override
	public int getCollectSignableContractNum(Long memberId) {
		try {
			return myTransactionManager.getCollectSignableContractNum(memberId);
		} catch (Exception e) {
			logger.error("获取用户募集中未签署合同数量失败,memberId" + memberId, e);
		}
		return 0;
	}
	
	@Override
	public int getTransactionSignableContractNum(Long memberId) {
		try {
			return myTransactionManager.getTransactionSignableContractNum(memberId);
		} catch (Exception e) {
			logger.error("获取用户我的交易未签署合同数量失败,memberId" + memberId, e);
		}
		return 0;
	}
	
	public void afterTransactionContractIsFullCoupon(Order order){
		myTransactionManager.afterTransactionContractIsFullCoupon(order);
	}
	
	

	@Override
	public ResultDO<TradeBiz> isRepeatToCashDest(Order order, String payerIp) {
		ResultDO<TradeBiz> rDO = null;
		try {
			return tradeManager.isRepeatToCashDest(order, payerIp);
		} catch (Exception e) {
			logger.error("非委托支付获取支付链接, orderNo={}", order.getOrderNo(), e);
			rDO = new ResultDO<TradeBiz>();
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			return rDO;
		}
	}

	@Override
	public int getTotalTransactionCount() {
		try {
			return myTransactionManager.getTotalTransactionCount();
		} catch (Exception e) {
			logger.error("获取平台累计交易笔数失败", e);
		}
		return 0;
	}
	

	@Override
	public Page<TraceSourceCollectBiz> queryByPageCollectByTraceSource(TraceSourceCollectQuery query) {
		return myTransactionManager.queryByPageCollectByTraceSource(query);
	}
	
	@Override
	public ResultDO<Object> transferToProject(Long memberId,Long transactionId,BigDecimal transferAmount) {
		ResultDO<Object> rDO = null;
		rDO = this.checktransferToProject(memberId, transactionId, transferAmount);
		if (rDO.isError()) {
			return rDO;
		}
		try {
			rDO = transferProjectManager.transferToProject(memberId, transactionId,transferAmount);
			return rDO;
		} catch (Exception e) {
			logger.error("发起转让异常, memberId={},transactionId={},transferAmount={}", memberId,
					transactionId,transferAmount, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			return rDO;
		}
		
	}
	
	
	private ResultDO<Object> checktransferToProject(Long memberId,Long transactionId,BigDecimal transferAmount) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try {

			rDO = this.ableToTransfer(memberId, transactionId);//交易是否可转让
			if(!rDO.isSuccess()){
				return rDO;
			}
			//转让价格小于等于剩余未还本金
			BigDecimal residualPrincipal = transferProjectManager.getResidualPrincipal(transactionId);
			if(residualPrincipal.compareTo(transferAmount)==-1){
				rDO.setResultCode(ResultCode.TRANSFER_PROJECT_TRANSFERAMOUNT_INVALID);
				return rDO;
			}

			/*if(residualPrincipal.compareTo((transferAmount.add(discount)))!=0){
				//外部传来的转让价格不等于项目价值-折价
				rDO.setResultCode(ResultCode.TRANSFER_PROJECT_TRANSFERAMOUNT_ERROR);
				return rDO;
			}*/
			
		} catch (Exception e) {
			logger.error("发起转让校验异常, memberId={},transactionId={},transferAmount={}", memberId,
					transactionId,transferAmount, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			return rDO;
		}
		return rDO;
	}
	
	
	@Override
	public ResultDO<Object> ableToTransfer(Long memberId,Long transactionId) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try {
			Transaction transaction = myTransactionManager.selectTransactionById(transactionId);
			if(transaction==null){
				rDO.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
				return rDO;
			}
			if(memberId.compareTo(transaction.getMemberId()) != 0){
				rDO.setResultCode(ResultCode.TRANSACTION_NOT_BELONG_TO_MEMBER_ERROR);
				return rDO;
			}
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			if(project==null){
				rDO.setResultCode(ResultCode.PROJECT_NOT_EXIST_ERROR);
				return rDO;
			}
			if(project.getTransferFlag() == null ||  project.getTransferFlag() != 1){//TODO 转让标记 枚举
				rDO.setResultCode(ResultCode.TRANSFER_PROJECT_PROJECT_CANNOT_TRANSFER);
				return rDO;
			}
			if(project.getPrepayment()==1){//标记提前还款的项目不能进行转让
				rDO.setResultCode(ResultCode.TRANSFER_PROJECT_PROJECT_CANNOT_TRANSFER_EARLY_REPAYMENT);
				return rDO;
			}
			if( (project.isDirectProject() && project.getStatus()!=StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus()
					&&project.getStatus()!=StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus())
					){//非履约中项目，不能转让 || ( project.getStatus()!=StatusEnum.PROJECT_STATUS_FULL.getStatus() && !project.isDirectProject())
				rDO.setResultCode(ResultCode.TRANSFER_PROJECT_PROJECT_CANNOT_TRANSFER);
				return rDO;
			}
			if(overdueLogManager.isOverDueUnrepayment(project.getId())){//普通逾期未还，即不可转让
				rDO.setResultCode(ResultCode.TRANSFER_PROJECT_PROJECT_CANNOT_TRANSFER_OVER);
				return rDO;
			}
			if(transaction.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){//二次转让
				rDO.setResultCode(ResultCode.TRANSFER_PROJECT_PROJECT_CANNOT_TRANSFER);
				return rDO;
			}
			if(transaction.getStatus()!=StatusEnum.TRANSACTION_REPAYMENT.getStatus()){//不是回款中，不可以进行转让
				rDO.setResultCode(ResultCode.TRANSFER_PROJECT_PROJECT_CANNOT_TRANSFER);
				return rDO;
			}
			if(transaction.getTransferStatus()!=StatusEnum.TRANSACTION_TRANSFER_STATUS_NO.getStatus()&&
					transaction.getTransferStatus()!=StatusEnum.TRANSACTION_TRANSFER_STATUS_PART_TRANSFERED.getStatus()){//不是未转让且部分转让，不可以进行转让
				rDO.setResultCode(ResultCode.TRANSFER_PROJECT_PROJECT_CANNOT_TRANSFER);
				return rDO;
			}
			if(transaction.getCanTransferDate()==null||transaction.getExtraInterestDay()>0){//高额加息券该部分值不写入，高额加息券不可转让
				rDO.setResultCode(ResultCode.TRANSFER_PROJECT_PROJECT_CANNOT_TRANSFER_HIGH_EXTRA);
				return rDO;
			}
			//交易是否处于可转让期（项目可转让期）
			if(transaction.getCanTransferDate()!=null){
				if(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3).getTime()<transaction.getCanTransferDate().getTime()){
					String message =  "距离可转让还有" + (DateUtils.daysBetween( DateUtils.getCurrentDate() , DateUtils.formatDate(transaction.getCanTransferDate(), DateUtils.DATE_FMT_3)))
							+ "天";
					rDO.setResult(DateUtils.daysBetween(DateUtils.formatDate( DateUtils.getCurrentDate(),DateUtils.DATE_FMT_3),transaction.getCanTransferDate()));
					rDO.setResultCode(ResultCode.CUSTOM);
					rDO.getResultCode().setMsg(message);
					return rDO;
				}
			}
			
			TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
			transactionInterestQuery.setTransactionId(transactionId);
			List<TransactionInterest> traList =  transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
			Date lastEndDate = traList.get(0).getEndDate();
			Date firstStartDate = traList.get(0).getStartDate();
			Integer days = 0;
			for(TransactionInterest tra :traList){
				if(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3).getTime() ==tra.getEndDate().getTime()
						){
					rDO.setResultCode(ResultCode.TRANSFER_PROJECT_PROJECT_CANNOT_TRANSFER_REPAY);
					return rDO;
				}
				/*if(lastEndDate.getTime()<tra.getEndDate().getTime()){//遍历最大的还款时间，即为最后一期
					lastEndDate = tra.getEndDate();
				}
				if(firstStartDate.getTime()>tra.getStartDate().getTime()){//遍历最小的起始时间，即为第一期
					firstStartDate = tra.getStartDate();
				}
				//当期期
				if(tra.getStartDate().getTime() < DateUtils.getCurrentDate().getTime() 
						&& DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3).getTime() <= tra.getEndDate().getTime() ){
					days = DateUtils.daysOfTwo(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), tra.getEndDate()) + 1  ;
				}*/
			}
			/*//距离最近一期还款日X天
			if(!(project.getTransferRecentRepayment() < days) && days>0){
				String message =  "该项目" + (project.getTransferRecentRepayment()-days + 1) + "天后可转让";
				ResultCode.TRANSFER_PROJECT_TRANSACTION_TIME_FAIL.setMsg(message);
				rDO.setResult((project.getTransferRecentRepayment()-days + 1));
				rDO.setResultCode(ResultCode.TRANSFER_PROJECT_TRANSACTION_TIME_FAIL);
				return rDO;
			}*/
			
			//起息日后X个自然日可以转让
			/*int transferAfterInterest = DateUtils.daysOfTwo( firstStartDate , DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3))+1;
			if(!(project.getTransferAfterInterest() < 
					(DateUtils.daysOfTwo( firstStartDate , DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3))+1))){
				String message =  "距离可转让还有" + (project.getTransferAfterInterest() - transferAfterInterest+ 1) + "天";
				rDO.setResult((project.getTransferAfterInterest() - transferAfterInterest + 1));
				rDO.setResultCode(ResultCode.CUSTOM);
				rDO.getResultCode().setMsg(message);
				return rDO;
			}*/
			
		/*	//距离最后一期还款日X天
			if(!(project.getTransferLastRepayment() < 
					(DateUtils.daysOfTwo(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), lastEndDate)+1))){
				rDO.setResultCode(ResultCode.TRANSFER_PROJECT_TRANSACTION_TIME_FAIL);
				return rDO;
			}*/
			
			//今天是否二次转让
			TransferProject isAlreadytransferProject=  transferProjectManager.selectByTransactionIdToday(transactionId);
			
			if(isAlreadytransferProject!=null){
				rDO.setResultCode(ResultCode.TRANSFER_PROJECT_ALREADY_TRANSFER);
				return 	rDO;
			}
			
		} catch (Exception e) {
			logger.error("是否可以发起转让校验异常, memberId={},transactionId={}", memberId,
					transactionId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			return rDO;
		}
		return rDO;
	}
	
	@Override
	public ResultDO<Object> cancelTransferProject(Long memberId,Long transactionId) {
		ResultDO<Object> rDO = null;
		rDO = this.checkCancleTransferToProject(memberId, transactionId);
		if (rDO.isError()) {
			return rDO;
		}
		try {
			rDO = transferProjectManager.cancelTransferProject(transactionId);
			return rDO;
		} catch (Exception e) {
			logger.error("撤销转让异常, memberId={},transactionId={}", memberId,
					transactionId,e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			return rDO;
		}
	}
	
	private ResultDO<Object> checkCancleTransferToProject(Long memberId,Long transactionId) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try {
			Transaction transaction = myTransactionManager.selectTransactionById(transactionId);
			if(transaction==null){
				rDO.setResultCode(ResultCode.TRANSACTION_NOT_EXIST_ERROR);
				return rDO;
			}
			if(memberId.compareTo(transaction.getMemberId()) != 0){
				rDO.setResultCode(ResultCode.TRANSACTION_NOT_BELONG_TO_MEMBER_ERROR);
				return rDO;
			}
		} catch (Exception e) {
			logger.error("撤销转让校验异常, memberId={},transactionId={}", memberId,
					transactionId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
			return rDO;
		}
		return rDO;
	}
	
	@Override
	public List<TransactionForProject> getTransferProjectDetailTransactions(TransactionQuery transactionQuery) {
		try {
			Long projectId = transactionQuery.getProjectId();
			TransferProject transferProject = transferProjectManager.selectByPrimaryKey(projectId);
			Project project = projectManager.selectByPrimaryKey(transferProject.getProjectId());
			transactionQuery.setProjectId(project.getId());
			//转让项目交易类型
			transactionQuery.setProjectCategory(TypeEnum.PROJECT_CATEGORY_TRANSFER.getType());
			transactionQuery.setTransferId(transferProject.getId());
			Page<TransactionForProject>	transactionsPage = myTransactionManager.selectTransactionForProjectsForPage(transactionQuery);
			
			return transactionsPage.getData();
		} catch (Exception e) {
			logger.error("通过项目id分页查询转让交易记录，transactionQuery=" + transactionQuery, e);
		}
		return null;
	}
	
	@Override
	public ResultDO<Object> transferInformation(Long memberId,Long transactionId) {
//		ResultDO<Object> result = new ResultDO<Object>();
//		TransferInformation tran = new TransferInformation();
//		try {
//			Transaction transaction = myTransactionManager.selectTransactionById(transactionId);
//			if(transaction==null){
//				result.setResultCode(ResultCode.ERROR_SYSTEM);
//				return result;
//			}
//			//WEB根据交易号判断是否有转让数据
//			TransferProject transferProject = transferProjectManager.selectByTransactionId(transactionId);
//			tran.setTransactionId(transactionId);
//
//			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
//			tran.setInvestType(project.getInvestType());
//			if(transferProject!=null){
//
//				tran.setTransferProjectName(transferProject.getTransferName());
//				tran.setTransferAmount(transferProject.getTransferAmount());
//				BigDecimal balance = getProjectBalanceById(transferProject.getId());
//				String progress = getProjectNumberProgress(transferProject.getTransactionAmount(), balance);
//				tran.setInvestmentProgress(progress);
//				BigDecimal totalIncome = BigDecimal.ZERO;
//				if(transferProject.getStatus()==StatusEnum.TRANSFER_PROJECT_STATUS_INVESTING.getStatus()||
//						transferProject.getStatus()==StatusEnum.TRANSFER_PROJECT_STATUS_FULL.getStatus()||
//						transferProject.getStatus()==StatusEnum.TRANSFER_PROJECT_WAIT_LOAN.getStatus()
//						){
////					BigDecimal transferRate= new BigDecimal(SysServiceUtils.getDictValue("transferRate", "transferRate", ""));
//					BigDecimal transferRate=myTransactionManager.getTransferRate(transactionId);
//					BigDecimal transferRateAmount = transferProject.getTransferAmount().multiply(transferRate).setScale(2, BigDecimal.ROUND_HALF_UP);
//					totalIncome = transferProject.getTransferAmount().subtract(transferRateAmount);
//					tran.setTotalIncome(totalIncome);
//					tran.setStatus(1);
//				}
//				if(transferProject.getStatus()==StatusEnum.TRANSFER_PROJECT_STATUS_LOAN.getStatus()||
//						transferProject.getStatus()==StatusEnum.TRANSFER_PROJECT_STATUS_REPAYMENT.getStatus()
//						){
//					HostingPayTrade hostPay = hostingPayTradeManager.getHostingPayTradeBySourceIdType(transferProject.getId(), TypeEnum.HOSTING_PAY_TRADE_TRANSFER_AMOUNT.getType());
//					CapitalInOutLogQuery query = new CapitalInOutLogQuery();
//					query.setSourceId(hostPay.getId().toString());
//					query.setMemberId(memberId);
//					query.setType(TypeEnum.FINCAPITALINOUT_PAYACCOUNTTYPE_MAIN.getType());
//					 List<CapitalInOutLog> capiList = capitalInOutLogManager.getTotalTransferAmountForMemberTransfer(query);
//					  if(Collections3.isNotEmpty(capiList)){
//						totalIncome = capiList.get(0).getIncome();
//					  }
//					 tran.setTotalIncome(totalIncome);
//					tran.setTransferDate(transferProject.getTransferSaleComplatedTime());
//					tran.setStatus(2);
//				}
//				if(transferProject.getStatus()==StatusEnum.TRANSFER_PROJECT_STATUS_LOSING.getStatus()||
//						transferProject.getStatus()==StatusEnum.TRANSFER_PROJECT_STATUS_LOSE.getStatus()
//						){
//					tran.setStatus(3);
//					tran.setRemarks(transferProject.getRemarks());
//					tran.setFailDate(transferProject.getFailTime());
//				}
//			}else{
//				tran.setStatus(4);
//			}
//
//			TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
//			transactionInterestQuery.setTransactionId(transactionId);
//			List<TransactionInterest> traList =  transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
//			for(TransactionInterest tra :traList){
//				//当期利息
//				if(tra.getStartDate().getTime() < DateUtils.getCurrentDate().getTime() && DateUtils.getCurrentDate().getTime() < tra.getEndDate().getTime() ){
//					currentInterest = tra.getPayableInterest();
//					days = DateUtils.daysOfTwo( tra.getStartDate(),DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3));
//					currentDays = DateUtils.daysOfTwo(tra.getStartDate(), tra.getEndDate()) + 1;
//				}
//				if(DateUtils.getCurrentDate().getTime() < tra.getEndDate().getTime() ){
//					residualPrincipal = residualPrincipal.add(tra.getPayablePrincipal());
//				}
//			}
////			currentInterest = currentInterest.multiply(new BigDecimal(days).divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP))
////					.setScale(2,BigDecimal.ROUND_HALF_UP);;
//			tran.setCurrentInterest(currentInterest);
//			tran.setResidualPrincipal(residualPrincipal);
//			tran.setTransferRate(myTransactionManager.getTransferRate(transactionId));
//			tran.setHoldDays(DateUtils.daysBetween(transaction.getTransactionTime(),new Date()));
//
//			result.setResult(tran);
//
//		} catch (ManagerException e) {
//			logger.error("获取转让信息异常, memberId={},transactionId={}", memberId,transactionId,e);
//		}

		ResultDO<Object> result = new ResultDO<Object>();
		TransferInformation tran = new TransferInformation();
		BigDecimal residualPrincipal = BigDecimal.ZERO;//剩余本金
		BigDecimal residualInterest = BigDecimal.ZERO;//剩余收益
		try {
			Transaction transaction = myTransactionManager.selectTransactionById(transactionId);
			if(transaction==null){
				result.setResultCode(ResultCode.ERROR_SYSTEM);
				return result;
			}
			if (memberId.compareTo(transaction.getMemberId()) != 0) {
				result.setResultCode(ResultCode.TRANSACTION_NOT_BELONG_TO_MEMBER_ERROR);
				return result;
			}

			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			tran.setInvestType(project.getInvestType());
			tran.setTransactionId(transactionId);

			TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
			transactionInterestQuery.setTransactionId(transactionId);
			List<TransactionInterest> traList =  transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
			for(TransactionInterest tra :traList){
				if(tra.getEndDate().getTime()< DateUtils.getCurrentDate().getTime() ){
					continue;
				}
				if(tra.getEndDate().getTime() == DateUtils.getCurrentDate().getTime()){
					continue;
				}
				if(DateUtils.getCurrentDate().getTime() < tra.getEndDate().getTime() ){
					residualPrincipal = residualPrincipal.add(tra.getRealPayPrincipal());
					residualInterest = residualInterest.add(tra.getRealPayInterest());//接盘人，享受转让人的收益券收益
				}

			}
			tran.setHoldDays(DateUtils.daysBetween(traList.get(0).getStartDate(), DateUtils.getCurrentDate())+1);
			tran.setRemainingTime(DateUtils.daysBetween(DateUtils.getCurrentDate(),traList.get(traList.size()-1).getEndDate())+1);//今天算一天
			tran.setResidualPrincipal(residualPrincipal);
			tran.setResidualInterest(residualInterest);

			tran.setTransferTime(Integer.valueOf(SysServiceUtils.getDictValue("transfer_group", "raise_time","72")));
			tran.setTransferRate(myTransactionManager.getTransferRate(transactionId)!=null?myTransactionManager.getTransferRate(transactionId):new BigDecimal(2));
			tran.setTransferRateList(this.getTransferRate());
			result.setResult(tran);

		} catch (ManagerException e) {
			logger.error("获取转让信息异常, memberId={},transactionId={}", memberId,transactionId,e);
		}

		return result;
	}

	private List<TransferRateList> getTransferRate(){

		List<TransferRateList> transferRateList  = Lists.newArrayList();
		SysDict dict=new SysDict();
		try {
			dict= sysDictManager.findByGroupNameAndKey("transferRate_group","Rate");
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		if (dict==null){
			return transferRateList;
		}
		try {
			transferRateList= JSON.parseObject(dict.getValue(),new TypeReference<ArrayList<TransferRateList>>(){}) ;
		} catch (Exception e) {
			return null;
		}
		return transferRateList;
	}
	
	/**
	 * 项目进度
	 * @param totalAmount
	 * @param availableBalance
	 * @return
	 */
	private String getProjectNumberProgress(BigDecimal totalAmount, BigDecimal availableBalance){
		String  progress = "0";
		if(availableBalance != null){
			if(availableBalance.compareTo(BigDecimal.ZERO) <= 0){
				progress = "100";
			}else if(availableBalance.compareTo(totalAmount) == 0){
				progress = "0";
			}else{
				progress = new DecimalFormat("###.##").format((totalAmount.subtract(availableBalance)).divide(totalAmount,4,RoundingMode.HALF_UP).multiply(new BigDecimal("100")));
			}
		}
		return progress;
	}
	
	private BigDecimal getProjectBalanceById(Long id) {
		//可用余额
		BigDecimal availableBalance = null;
		try {
			//从缓存中找可用余额
			//availableBalance = RedisProjectClient.getProjectBalance(id);
			if(availableBalance == null){
				//logger.info("项目"+id+"，可用余额在redis未找到。");
				//如果为Null，到余额表找
				Balance _balance = balanceManager.queryBalance(id, TypeEnum.BALANCE_TYPE_PROJECT);
				if(_balance != null){
					availableBalance = _balance.getAvailableBalance();
				}else{
					logger.debug("项目"+id+"，可用余额在余额表未找到。");
				}
			}
			if(availableBalance == null){
				//再没有，那只能从项目中去找了
				Project project = projectManager.selectByPrimaryKey(id);
				if(project!=null){
					availableBalance = project.getTotalAmount();
				}
				availableBalance = transferProjectManager.getTransferProjectBalanceById(id);
			}
			logger.debug("项目"+id+"，可用余额"+availableBalance);
		} catch (ManagerException e) {
			logger.error("项目"+id+"查找",e);
		}
		return availableBalance;
	}
	
	@Override
	public ResultDO<TransferContractBiz> viewZtTransferContract(Long orderId,Long memberId) {
		ResultDO<TransferContractBiz> result = new ResultDO<TransferContractBiz>();
			try {
				result = myTransactionManager.viewZtTransferContract(orderId, memberId);
				return result;
			} catch (Exception e) {
				result.setSuccess(false);
				logger.error("查看P2P转让合同发生异常，orderId：" + orderId, e);
			}
			return result;
		}
	
	@Override
	public ResultDO<TransferContractBiz> transferContract(Long orderId,Long memberId) {
		ResultDO<TransferContractBiz> result = new ResultDO<TransferContractBiz>();
			try {
				ResultDO<TransferContractBiz> resultDO = myTransactionManager.transferContract(orderId, memberId);
				result.setResult(resultDO.getResult());
				return result;
			} catch (Exception e) {
				result.setSuccess(false);
				logger.error("查看转让债权合同发生异常，orderId：" + orderId, e);
			}
			return result;
		}

	@Override
	public ResultDO getTransactionInterestDetailForMemberTransfer(
			TransactionInterestQuery query) {
		ResultDO result = new ResultDO();
		try {
			List<TransactionInterestForMember> detailList = myTransactionManager.getTransactionInterestDetailForMemberTransfer(query);
			if(Collections3.isNotEmpty(detailList)){
				for(TransactionInterestForMember bean :detailList){
					TransferProject tr=transferProjectManager.selectByTransactionIdAndMemberId(bean.getTransactionId(),query.getMemberId());
					if(tr!=null){
						bean.setEndDate(tr.getTransferSaleComplatedTime());
					}
				}
				List<TransactionInterestForMemberDto> detailDtoList =BeanCopyUtil.mapList(detailList, TransactionInterestForMemberDto.class);
				result.setResult(detailDtoList);
			}
		} catch (ManagerException e) {
			logger.error("查看用户已转让本金明细发生异常" , e);
			result.setSuccess(false);
		}
		return result;
	}

	@Override
	public Object getTransferDetailForMember(Integer type, Long memberId) {
		ResultDO<List<TransferForMemberDto>> result = new ResultDO<List<TransferForMemberDto>>();
		try {
			if (type == null || memberId == null || (type != TypeEnum.HOSTING_PAY_TRADE_TRANSFER_AMOUNT.getType()
					&& type != TypeEnum.HOSTING_PAY_TRADE_TRANSFER_FEE.getType())) {
				result.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_FORMAT);
				return result;
			}
			List<HostingPayTrade> dataList = Lists.newArrayList();
			if (type == TypeEnum.HOSTING_PAY_TRADE_TRANSFER_AMOUNT.getType()) {
				dataList = hostingPayTradeManager.getTotalTransferGetList(memberId);
			} else if (type == TypeEnum.HOSTING_PAY_TRADE_TRANSFER_FEE.getType()) {
				dataList = hostingPayTradeManager.getTotalTransferFeeList(memberId);
			}
			List<TransferForMemberDto> resultList = Lists.newArrayList();
			for (HostingPayTrade trade : dataList) {
				TransferForMemberDto dto = new TransferForMemberDto();
				dto.setAmount(trade.getAmount());
				dto.setHappenTime(trade.getUpdateTime());
				dto.setProjectId(trade.getSourceId());
				dto.setProjectName(trade.getRemarks());
				resultList.add(dto);
			}
			result.setResult(resultList);
		} catch (Exception e) {
			logger.error("查看会员已转让收款/手续费发生异常", e);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return result;
	}

	@Override
	public ResultDO getTotalTransferAmountForMemberTransfer(
			CapitalInOutLogQuery query) {
		ResultDO result = new ResultDO();
		List<TransactionInterestForMemberDto> list=Lists.newArrayList();
		try {
			List<CapitalInOutLog> detailList = capitalInOutLogService.getTotalTransferAmountForMemberTransfer(query);
			//List<TransferProject> detailList=transferProjectManager.selectTransferByMemberId(query.getMemberId());
			if(Collections3.isNotEmpty(detailList)){
				for(CapitalInOutLog log:detailList){
					HostingPayTrade hostingPayTrade=hostingPayTradeManager.selectByPrimaryKey(Long.parseLong(log.getSourceId()));
					if(hostingPayTrade==null){
						continue;
						}
					TransferProject transferProject=transferProjectManager.selectByPrimaryKey(hostingPayTrade.getSourceId());
					if(transferProject==null){
						continue;
						}
					TransactionInterestForMemberDto project=new TransactionInterestForMemberDto();
					project.setProjectName(transferProject.getTransferName());
					project.setEndDate(log.getHappenTime());
					project.setPayablePrincipal(log.getIncome());
					project.setProjectId(transferProject.getId());
					list.add(project);
					}
			
				result.setResult(list);
			}
		} catch (ManagerException e) {
			logger.error("查看用户总计转让收款明细发生异常" , e);
			result.setSuccess(false);
		}
		return result;
	}

	@Override
	public int getLotteryNum(Long orderId) {
		try {
			Transaction transaction=myTransactionManager.getTransactionByOrderId(orderId);
			Long transactionId=0L;
			if(transaction!=null){
				transactionId=transaction.getId();
			}
			return projectExtraManager.getLotteryNum(transactionId);
		} catch (Exception e) {
			logger.error("获取抽奖次数异常", e);
		}
		return 0;
	}

	@Override
	public boolean isQuickLotteryProject(Long projectId) {
		try {
			ProjectExtra pe = projectExtraManager.getProjectQucikReward(projectId);
			if(pe!=null&&pe.getActivitySign()!=null){
				if(pe.getActivitySign()==2){
					return true;
				}
			}
		} catch (ManagerException e) {
		}
		
		return false;
	}

	@Override
	public int queryMemberTransactionCount(Long memberid, Date starttime, Date endtime) {
		return myTransactionManager.queryMemberTransactionCount(memberid,starttime,endtime);
	}

	@Override
	public Page<TransactionProjectBiz> queryPageTransactionProjectBiz(TransactionProjectBizQuery query) {
		return myTransactionManager.queryPageTransactionProjectBiz(query);
	}

	@Override
	public TransactionProjectDetailBiz queryInvestmentedTransactionProjectDetailBiz(Long transactionId, Long memberId) {
		return transferProjectManager.queryInvestmentedTransactionProjectDetailBiz(transactionId,memberId);
	}

	@Override
	public Page<TransactionProjectDetailBiz> queryPageTransactionProjectDetailBiz(TransactionProjectDetailBizQuery query) {
		return transferProjectManager.queryPageTransactionProjectDetailBiz(query);
	}
}