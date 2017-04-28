/**
 * 
 */
package com.yourong.core.ic.manager.impl;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
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
import com.yourong.common.cache.RedisManager;
import com.yourong.common.cache.RedisProjectClient;
import com.yourong.common.constant.Constant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.MessageEnum;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
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
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.bsc.manager.BscAttachmentManager;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.dao.TransferProjectMapper;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.ic.model.biz.TransactionProjectDetailBiz;
import com.yourong.core.ic.model.biz.TransferProjectPageBiz;
import com.yourong.core.ic.model.query.TransactionProjectDetailBizQuery;
import com.yourong.core.ic.model.query.TransferProjectPageQuery;
import com.yourong.core.ic.model.query.TransferProjectQuery;
import com.yourong.core.ic.model.query.TransferRecordQuery;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.dao.HostingCollectTradeMapper;
import com.yourong.core.tc.dao.OrderMapper;
import com.yourong.core.tc.dao.TransactionMapper;
import com.yourong.core.tc.manager.HostingCollectTradeAuthManager;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.HostingPayTradeManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.HostingPayTrade;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.query.TransactionInterestQuery;
import com.yourong.core.tc.model.query.TransactionQuery;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年8月28日上午11:05:12
 */
@Component
public class TransferProjectManagerImpl implements TransferProjectManager{
	
	private static Logger logger = LoggerFactory.getLogger(TransferProjectManagerImpl.class);

	@Autowired
	private TransferProjectMapper transferProjectMapper;
	
	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired 
	private ProjectManager projectManager;
	
	@Autowired
	private TransactionInterestManager transactionInterestManager;
	
	@Autowired
	private BalanceManager balanceManager;
	
	@Autowired
	private TransactionMapper transactionMapper;
	
	@Autowired
	private HostingCollectTradeAuthManager hostingCollectTradeAuthManager;

	@Autowired
	private HostingCollectTradeManager hostingCollectTradeManager;
	
	@Autowired 
	private BscAttachmentManager bscAttachmentManager;

	@Autowired 
	private HostingPayTradeManager hostingPayTradeManager;

	@Autowired 
	private SysDictManager sysDictManager;

	@Autowired 
	private SinaPayClient sinaPayClient;

	@Autowired
	private CapitalInOutLogManager capitalInOutLogManager;
	
	@Autowired
	private OrderMapper orderMapper;
	
	@Autowired
	private CouponManager couponManager;

	@Autowired
	private HostingCollectTradeMapper hostingCollectTradeMapper;
	
	@Override
	public TransferProject selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return transferProjectMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public TransferProject selectByPrimaryKeyForLock(Long id) throws ManagerException {
		try {
			return transferProjectMapper.selectByPrimaryKeyForLock(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public TransferProject selectByTransactionId(Long transactionId) throws ManagerException {
		try {
			return transferProjectMapper.selectByTransactionId(transactionId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public TransferProject selectByTransactionIdForLock(Long transactionId) throws ManagerException {
		try {
			return transferProjectMapper.selectByTransactionIdForLock(transactionId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public TransferProject selectByTransactionIdToday(Long transactionId) throws ManagerException {
		try {
			return transferProjectMapper.selectByTransactionIdToday(transactionId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public TransferProject selectByTransactionIdStatus(Long transactionId) throws ManagerException {
		try {
			return transferProjectMapper.selectByTransactionIdStatus(transactionId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public int insert(TransferProject transferProject) throws ManagerException {
		try {
			return transferProjectMapper.insert(transferProject);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public int updateByPrimaryKeySelective(TransferProject transferProject) throws ManagerException {
		try {
			return transferProjectMapper.updateByPrimaryKeySelective(transferProject);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public List<TransferProject> queryTransferProjectListByMap(Map<String, Object> map) throws ManagerException {
		try {
			return transferProjectMapper.queryTransferProjectListByMap(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public List<TransferProject> queryTransferProjectListByTransactionId(Long transactionId)throws ManagerException{
		Map<String, Object> map = Maps.newHashMap();
		map.put("transactionId", transactionId);
		return this.queryTransferProjectListByMap(map);
	}
	
	@Override
	@Transactional(rollbackFor = {Exception.class,ManagerException.class}, propagation = Propagation.REQUIRED)
	public ResultDO<Object> transferToProject(Long memberId ,Long transactionId,BigDecimal transferAmount) throws ManagerException  {
		  ResultDO<Object> result = new ResultDO<Object>();
		  
		 try {
			//依赖转让表查询是否已转让
			 TransferProject isAlreadySuccesstransferProject = this.selectByTransactionIdStatus(transactionId);
			 if(isAlreadySuccesstransferProject!=null){
					result.setResultCode(ResultCode.TRANSFER_PROJECT_ALREADY_SUCCESS_TRANSFER);
					return 	result;
			}
			//今天是否二次转让
			TransferProject isAlreadytransferProject=  this.selectByTransactionIdToday(transactionId);
			
			if(isAlreadytransferProject!=null){
				result.setResultCode(ResultCode.TRANSFER_PROJECT_ALREADY_TRANSFER);
				return 	result;
			}
			
			Transaction transaction =transactionManager.selectTransactionByIdLock(transactionId);
			int num =  transactionMapper.updateTransferStatusById(StatusEnum.TRANSACTION_TRANSFER_STATUS_TRANSFERING.getStatus(),
						StatusEnum.TRANSACTION_TRANSFER_STATUS_NO.getStatus(), null,
						transaction.getId());
			if (num == 0) {
					
				int numPart =  transactionMapper.updateTransferStatusById(StatusEnum.TRANSACTION_TRANSFER_STATUS_TRANSFERING.getStatus(),
								StatusEnum.TRANSACTION_TRANSFER_STATUS_PART_TRANSFERED.getStatus(), null,
								transaction.getId());
				if(numPart==0){
				 logger.info("该笔交易已转让，transactionId={}", transactionId);
					return result;
				}
	        }
			  // 2 根据交易信息生成转让项目信息，插入转让项目表
			Transaction tra = transactionManager.selectTransactionById(transactionId);
			Project pro	= projectManager.selectByPrimaryKey(tra.getProjectId());
			TransferProject newTransferProject = new TransferProject();
			//封装转让项目信息
			newTransferProject.setProjectId(pro.getId());
			newTransferProject.setMemberId(memberId);
			newTransferProject.setTransactionId(transactionId);
			newTransferProject.setTransferStartDate(DateUtils.getCurrentDateTime());
			
			newTransferProject.setTransferEndDate(this.getTransferEndDate(transactionId));
			
			newTransferProject.setTransferName(pro.getName());
			newTransferProject.setTransferAmount(transferAmount);
			newTransferProject.setTransactionAmount(tra.getInvestAmount());
			newTransferProject.setSubscriptionPrincipal(this.getTransactionAmount(transactionId));
			
			
			BigDecimal multiple = newTransferProject.getSubscriptionPrincipal().divide(pro.getMinInvestAmount(),10,BigDecimal.ROUND_HALF_DOWN);
			
			BigDecimal unitTransferAmount = transferAmount.divide(multiple,2,BigDecimal.ROUND_HALF_DOWN);
			newTransferProject.setUnitTransferAmount(unitTransferAmount);
			
			newTransferProject.setUnitSubscriptionAmount(pro.getMinInvestAmount());
			
			
			newTransferProject.setTransferAmount(transferAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
			newTransferProject.setStatus(StatusEnum.TRANSFER_PROJECT_STATUS_INVESTING.getStatus());
			newTransferProject.setFailFlag(0); /**0-未失败，1-流标，2-撤销**/ //TODO 加个枚举
			newTransferProject.setProfitType(pro.getProfitType());
			newTransferProject.setInterestFrom(0);//转让项目起息为0
			newTransferProject.setTransferredPrincipal(BigDecimal.ZERO);//已转让本金初始为0
			
			//存形象图，缩略图直接取原项目
			BscAttachment attachment = new BscAttachment();
			
			if(pro.isDirectProject()){
				attachment = bscAttachmentManager.getBscAttachmentByKeyIdAndModule(pro.getId().toString(), "direct_project_sign");
			}else{
				attachment = bscAttachmentManager.getBscAttachmentByKeyIdAndModule(pro.getId().toString(), "debt_sign");
			}
			if (attachment != null) {
				//直投债权区别处理
				newTransferProject.setThumbnail(attachment.getFileUrl());
			}
			
			newTransferProject.setCreateTime(DateUtils.getCurrentDateTime());
		    newTransferProject.setUpdateTime(DateUtils.getCurrentDateTime());
		    newTransferProject.setTransferRate(transactionManager.getTransferRate(transactionId)!=null?transactionManager.getTransferRate(transactionId):new BigDecimal(2));
			
		    this.getTransferAnnualizedRate(transactionId,  newTransferProject);  //转让项目年化利率   和转让价格

		    //3 初始化项目余额数据
			if(this.insert(newTransferProject)>0){
				//如果余额表没有对应的 交易可转让本金，则代表第一次转让
				if(balanceManager.queryBalance(newTransferProject.getTransactionId(), TypeEnum.BALANCE_TYPE_TRANSFER_AMOUNT)==null){
					BigDecimal transactionAmount = this.getTransactionAmount(transactionId);
					// 写入可认购本金
					balanceManager.insertBalance(TypeEnum.BALANCE_TYPE_TRANSFER_AMOUNT,transactionAmount, transactionId);
					// 写入缓存
					RedisProjectClient.setProjectBalance(transactionId, transactionAmount);
				}
				//发送站内信
				MessageClient.sendMsgForCommon(memberId, Constant.MSG_TEMPLATE_TYPE_STAND, MessageEnum.TRANSFER_APPLAY.getCode(), DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_13),
						tra.getProjectName(), transferAmount.toString());

			}			
		} catch (ManagerException e) {
			logger.error("转让项目初始化入库异常",transactionId,e);
			throw new ManagerException(e);
		}
		  return result;
	}
	
	
	private Date getTransferEndDate(Long transactionId){
		Date now = DateUtils.getCurrentDateTime();
		try {
			SysDict sysDict = sysDictManager.findByGroupNameAndKey("transfer_group", "raise_time");
			Date transferEndDate = DateUtils.addHour(now, Integer.valueOf(sysDict.getValue()));
			
			TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
			transactionInterestQuery.setTransactionId(transactionId);
			List<TransactionInterest> traList =  transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
			for(TransactionInterest tra :traList){
				if(now.getTime()<tra.getEndDate().getTime()
						&&tra.getEndDate().getTime()<=transferEndDate.getTime()){
					//当且仅当  起始时间小于还款日期且结束时间大于等于还款日期
					//取还款日之前一天的23:59:59
					transferEndDate = DateUtils.getEndTime(DateUtils.addDate(tra.getEndDate(), -1));
				}
			}
			return transferEndDate;
		} catch (ManagerException e) {
			logger.error("转让项目获取转让截止时间异常，now={}",now,e);
		}
		return now;
	}
	
	private TransferProject getTransferAnnualizedRate(Long transactionId,TransferProject newTransferProject){
		
		Integer residualDays = 0 ;
		BigDecimal transferAmount = newTransferProject.getTransferAmount();
		BigDecimal transferAnnualizedRate  = BigDecimal.ZERO;//转让年化
		BigDecimal residualPrincipal = BigDecimal.ZERO;//剩余本金
		BigDecimal residualInterest = BigDecimal.ZERO;//剩余利息
		BigDecimal currentInterest = BigDecimal.ZERO;//当期利息
		BigDecimal discount = BigDecimal.ZERO;//折价
		Date lastEndDate = DateUtils.getCurrentDate();
		int days = 0 ; //当期天数
		int currentDays = 0 ; //当期总天数
		try {
			TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
			transactionInterestQuery.setTransactionId(transactionId);
			transactionInterestQuery.setStatus(StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus());
			
			List<TransactionInterest> traList =  transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
			
			for(TransactionInterest tra :traList){
				if(lastEndDate.getTime()<tra.getEndDate().getTime()){//遍历最大的还款时间，即为最后一期
					lastEndDate = tra.getEndDate();
				}
				if(tra.getEndDate().getTime()< DateUtils.getCurrentDate().getTime() ){
					continue;
				}
				if(tra.getEndDate().getTime() == DateUtils.getCurrentDate().getTime()){
					continue;
				}
				if(DateUtils.getCurrentDate().getTime() < tra.getEndDate().getTime() ){
					residualPrincipal = residualPrincipal.add(tra.getPayablePrincipal());
					residualInterest = residualInterest.add(tra.getPayableInterest());//接盘人，享受转让人的收益券收益
				}
				//当期利息
				if(tra.getStartDate().getTime() < DateUtils.getCurrentDate().getTime() 
						&& DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3).getTime() <= tra.getEndDate().getTime() ){
					currentInterest = tra.getPayableInterest();
					days = DateUtils.daysOfTwo( tra.getStartDate(),DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3));;
					currentDays = DateUtils.daysOfTwo(tra.getStartDate(), tra.getEndDate()) + 1;
				}
			}
			residualDays = DateUtils.daysOfTwo(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), lastEndDate) + 1 ;
			
			currentInterest = currentInterest.multiply(new BigDecimal(days).divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP))
					.setScale(2, BigDecimal.ROUND_HALF_UP);
			
			newTransferProject.setResidualInterest(residualInterest);
			newTransferProject.setCurrentInterest(currentInterest);
			newTransferProject.setResidualPrincipal(residualPrincipal);
			
			discount = newTransferProject.getSubscriptionPrincipal().subtract(transferAmount);
			newTransferProject.setDiscount(discount);
			//减去转让人持有的利息
			residualInterest = residualInterest.subtract(currentInterest);
			//剩余应付利息* 转让本金/总本金 = 转让利息
			residualInterest = residualInterest.multiply
					(newTransferProject.getSubscriptionPrincipal().divide(newTransferProject.getTransactionAmount(),10,BigDecimal.ROUND_HALF_UP))
					.setScale(2,BigDecimal.ROUND_HALF_UP);
			
			transferAnnualizedRate = FormulaUtil.getTransferAnnualizedRate(residualInterest, discount, transferAmount, residualDays);
			newTransferProject.setTransferAnnualizedRate(transferAnnualizedRate);
		} catch (ManagerException e) {
			logger.error("获取转让项目年化异常，transactionId={}",transactionId,e);
		} 
		return newTransferProject;
	}
	
	public BigDecimal getTransferProjectAnnualized(Long id){
		
		try {
			TransferProject transferProject = this.selectByPrimaryKey(id);
			
			if(DateUtils.formatDate(transferProject.getTransferStartDate(), DateUtils.DATE_FMT_3).getTime()
					==DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3).getTime()){//发起转让当天
				return transferProject.getTransferAnnualizedRate();
			}
			BigDecimal transferAnnualizedRate  = BigDecimal.ZERO;//转让年化
			BigDecimal residualPrincipal = BigDecimal.ZERO;//剩余本金
			BigDecimal residualInterest = BigDecimal.ZERO;//剩余利息
			BigDecimal currentInterest = BigDecimal.ZERO;//当期利息
			BigDecimal discount = transferProject.getDiscount();//折价
			BigDecimal transferAmount = transferProject.getTransferAmount();

			Date lastEndDate = DateUtils.getCurrentDate();
			int days = 0 ; //当期天数
			int currentDays = 0 ; //当期总天数
			Integer residualDays = 0 ;
			
			TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
			transactionInterestQuery.setTransactionId(transferProject.getTransactionId());
			transactionInterestQuery.setStatus(StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus());
		
			List<TransactionInterest> traList =  transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
			
			for(TransactionInterest tra :traList){
				if(lastEndDate.getTime()<tra.getEndDate().getTime()){//遍历最大的还款时间，即为最后一期
					lastEndDate = tra.getEndDate();
				}
				if(tra.getEndDate().getTime()< DateUtils.getCurrentDate().getTime() ){
					continue;
				}
				if(tra.getEndDate().getTime() == DateUtils.getCurrentDate().getTime()){
					continue;
				}
				if(DateUtils.getCurrentDate().getTime() < tra.getEndDate().getTime() ){
					residualPrincipal = residualPrincipal.add(tra.getPayablePrincipal());
					residualInterest = residualInterest.add(tra.getPayableInterest());//接盘人，享受转让人的收益券收益
				}
				//当期利息
				if(tra.getStartDate().getTime() < DateUtils.getCurrentDate().getTime() 
						&& DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3).getTime() <= tra.getEndDate().getTime() ){
					currentInterest = tra.getPayableInterest();
					days = DateUtils.daysOfTwo( tra.getStartDate(),DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3));;
					currentDays = DateUtils.daysOfTwo(tra.getStartDate(), tra.getEndDate()) + 1;
				}
			}
			residualDays = DateUtils.daysOfTwo(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), lastEndDate) + 1 ;
			
			currentInterest = currentInterest.multiply(new BigDecimal(days).divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP))
					.setScale(2, BigDecimal.ROUND_HALF_UP);
			//减去转让人持有的利息
			residualInterest = residualInterest.subtract(currentInterest);
			//剩余应付利息* 转让本金/总本金 = 转让利息
			residualInterest = residualInterest.multiply
					(transferProject.getSubscriptionPrincipal().divide(transferProject.getTransactionAmount(),10,BigDecimal.ROUND_HALF_UP))
					.setScale(2,BigDecimal.ROUND_HALF_UP);
			
			
			transferAnnualizedRate = FormulaUtil.getTransferAnnualizedRate(residualInterest, discount, transferAmount, residualDays);
			
			return transferAnnualizedRate;
		} catch (ManagerException e) {
			logger.error("获取转让项目动态年化异常，id={}",id,e);
		}
		return BigDecimal.ZERO;
	}
	
	@Override
	@Deprecated
	public BigDecimal getProjectValue(Long transactionId){
	
		BigDecimal projectValue = BigDecimal.ZERO;//项目价值
		BigDecimal currentInterest = BigDecimal.ZERO;//当期利息
		BigDecimal residualPrincipal = BigDecimal.ZERO;//剩余本金
		int days = 0 ; //当期天数
		int currentDays = 0 ; //当期总天数
		try {
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
					residualPrincipal = residualPrincipal.add(tra.getPayablePrincipal());
				}
				//当期利息
				if(tra.getStartDate().getTime() < DateUtils.getCurrentDate().getTime()
						&& DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3).getTime() <= tra.getEndDate().getTime() ){
					currentInterest = tra.getPayableInterest();
					days = DateUtils.daysOfTwo( tra.getStartDate(),DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3));;
					currentDays = DateUtils.daysOfTwo(tra.getStartDate(), tra.getEndDate()) + 1;
				}
			}
			//项目价值= 剩余本金+当期利息*当期已经历时间/当期总天数
			projectValue = residualPrincipal.add((currentInterest.multiply(new BigDecimal(days).divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP))))
					.setScale(2, BigDecimal.ROUND_HALF_UP);
			return projectValue;
		} catch (ManagerException e) {
			logger.error("获取转让项目项目价值，transactionId={}",transactionId,e);
		}
		
		return BigDecimal.ZERO;
	}
	
	@Override
	public BigDecimal getResidualPrincipal(Long transactionId){
	
		BigDecimal residualPrincipal = BigDecimal.ZERO;//剩余本金
		try {
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
				}
			}
			return residualPrincipal;
		} catch (ManagerException e) {
			logger.error("获取交易剩余本金异常，transactionId={}",transactionId,e);
		}
		
		return BigDecimal.ZERO;
	}
	
	@Override
	public BigDecimal getTransactionAmount(Long transactionId){
	
		BigDecimal transactionAmount = BigDecimal.ZERO;//剩余认购本金
		try {
			Balance balance = balanceManager.queryBalance(transactionId, TypeEnum.BALANCE_TYPE_TRANSFER_AMOUNT);
			if(balance==null){
				Transaction tran = transactionManager.selectTransactionById(transactionId);
				transactionAmount = tran.getInvestAmount();
				return transactionAmount;
			}
			return balance.getAvailableBalance();
		} catch (ManagerException e) {
			logger.error("获取交易剩余本金异常，transactionId={}",transactionId,e);
		}
		
		return BigDecimal.ZERO;
	}
	
	
	
	@Override
	public Integer getReturnDay(Long transactionId){
	
		Integer residualDays = 0 ;
		Date lastEndDate = DateUtils.getCurrentDate();
		try {
			TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
			transactionInterestQuery.setTransactionId(transactionId);
			List<TransactionInterest> traList =  transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
			
			for(TransactionInterest tra :traList){
				if(lastEndDate.getTime()<tra.getEndDate().getTime()){//遍历最大的还款时间，即为最后一期
					lastEndDate = tra.getEndDate();
				}
				if(tra.getEndDate().getTime()< DateUtils.getCurrentDate().getTime() ){
					continue;
				}
				if(tra.getEndDate().getTime() == DateUtils.getCurrentDate().getTime()){
					continue;
				}
				if(DateUtils.getCurrentDate().getTime() < tra.getEndDate().getTime() ){
				}
			}
			residualDays = DateUtils.daysOfTwo(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), lastEndDate) + 1 ;
			return residualDays;
		} catch (ManagerException e) {
			logger.error("获取交易剩余收益天数异常，transactionId={}",transactionId,e);
		}
		return residualDays;
	}
	
	@Override
	public Page<TransferProject> findTransferProjectList(TransferProjectQuery transferProjectQuery) throws ManagerException {
		try {
			List<TransferProject> transferProjectList = Lists.newArrayList();
			long count = transferProjectMapper.selectCountTransferList(transferProjectQuery);
			if (count > 0) {
				transferProjectList = transferProjectMapper.selectTransferList(transferProjectQuery);
				for (TransferProject transferProject :transferProjectList){
					this.extraInfoForAPP(transferProject);
				}
			}
			Page<TransferProject> page = new Page<TransferProject>();
			page.setiTotalDisplayRecords(count);
			page.setPageNo(transferProjectQuery.getCurrentPage());
			page.setiDisplayLength(transferProjectQuery.getPageSize());
			page.setiTotalRecords(count);
			page.setData(transferProjectList);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	private void extraInfoForAPP(TransferProject transferProject) {
		Long transferId = transferProject.getId();
		String key = transferId + RedisConstant.REDIS_SEPERATOR
    			+ RedisConstant.REDIS_KEY_PROJECT;
		Map<String,Object> map = Maps.newHashMap();
		//投资人数
		Integer investNum = 0 ;
		//投资最高额
		BigDecimal mostInvestAmount = BigDecimal.ZERO;
		//项目总收益
		BigDecimal totalIncome = BigDecimal.ZERO;
		
		if(RedisManager.isExit(key)){
			//Object m = RedisManager.getObject(key);
			map = RedisManager.hgetAll(key);
		}
		
		if(transferProject.getStatus()==StatusEnum.TRANSFER_PROJECT_STATUS_LOAN.getStatus()
				){
			if(map.containsKey(RedisConstant.REDIS_FIELD_PROJECT_INVEST_MEMBER)){
				investNum = Integer.valueOf(map.get(RedisConstant.REDIS_FIELD_PROJECT_INVEST_MEMBER).toString());
			}else{
				investNum = transactionMapper.getTransactionMemberCountByTransferId(transferId);
				RedisManager.hset(key,
		    			RedisConstant.REDIS_FIELD_PROJECT_INVEST_MEMBER,investNum.toString());
			}
		}
		
		if(transferProject.getStatus()==StatusEnum.TRANSFER_PROJECT_STATUS_LOAN.getStatus()
			){
			if(map.containsKey(RedisConstant.REDIS_FIELD_PROJECT_MOST_INVEST_AMOUNT)){
				mostInvestAmount = new BigDecimal(map.get(RedisConstant.REDIS_FIELD_PROJECT_MOST_INVEST_AMOUNT).toString());
			}else{
				Transaction transaction = transactionMapper.selectMostTransactionBytransferId(transferId);
				mostInvestAmount = (transaction!=null?transaction.getTransferPrincipal():BigDecimal.ZERO);
				RedisManager.hset(key,
		    			RedisConstant.REDIS_FIELD_PROJECT_MOST_INVEST_AMOUNT,mostInvestAmount.toString());
			}
		}
		
		if(transferProject.getStatus()==StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()){
			if(map.containsKey(RedisConstant.REDIS_FIELD_PROJECT_TOTAL_AMOUNT)){
				totalIncome = new BigDecimal(map.get(RedisConstant.REDIS_FIELD_PROJECT_TOTAL_AMOUNT).toString());
			}else{
				List<Transaction> tranList= transactionMapper.selectByTransferId(transferId);
				for(Transaction tran :tranList){
					totalIncome = totalIncome.add(tran.getReceivedInterest().add(tran.getReceivedPrincipal()).subtract(tran.getInvestAmount()));
				}
				totalIncome = (totalIncome!=null?totalIncome:BigDecimal.ZERO);
				RedisManager.hset(key,
		    			RedisConstant.REDIS_FIELD_PROJECT_TOTAL_AMOUNT,totalIncome.toString());
			}	
		}
		transferProject.setInvestNum(investNum);
		transferProject.setMostInvestAmount(mostInvestAmount);
		transferProject.setTotalIncome(totalIncome);
	}
	
	public List<TransferProject> selectTransferInfoByMemberId(TransferRecordQuery query) throws ManagerException{
		try {
			return transferProjectMapper.selectTransferInfoByMemberId(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	public int selectCountTransferInfoByMemberId(TransferRecordQuery query) throws ManagerException{
		try {
			return transferProjectMapper.selectCountTransferInfoByMemberId(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public int updateProjectStatus(int newStatus, int currentStatus, Long id) throws ManagerException {
		try {
			return transferProjectMapper.updateProjectStatus(newStatus, currentStatus, id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	@Transactional(rollbackFor = {Exception.class,ManagerException.class}, propagation = Propagation.REQUIRED)
	public ResultDO<Object> cancelTransferProject(Long transactionId) throws ManagerException  {
		  ResultDO<Object> result = new ResultDO<Object>();
		 try {
			 TransferProject transferProject = this.selectByTransactionId(transactionId);
			 if(transferProject ==null){
				logger.info("转让项目不存在，transactionId={}", transactionId);
				result.setResultCode(ResultCode.TRANSFER_PROJECT_NOT_EXIST);
				 return result;
			 }
			 TransferProject transferProjectForLock = this.selectByPrimaryKeyForLock(transferProject.getId());
			 
			 if(transferProjectForLock.getStatus()!=StatusEnum.TRANSFER_PROJECT_STATUS_INVESTING.getStatus()){
				 logger.info("转让项目不是投资中状态，status={},transactionId={}", transferProjectForLock.getStatus(),transactionId);
				 result.setResultCode(ResultCode.TRANSFER_PROJECT_NOT_INVESTING);
				 return result;
			 }
			 Transaction transaction =transactionManager.selectTransactionByIdLock(transactionId);
			
			 if (transaction.getTransferStatus()!=StatusEnum.TRANSACTION_TRANSFER_STATUS_TRANSFERING.getStatus()) {
				 logger.info("该笔交易已经停止转让，transactionId={}", transactionId);
				 result.setResultCode(ResultCode.TRANSFER_PROJECT_ALREADY_CANCLE);
				 return result;
	         }
			 TransactionQuery transactionQuery = new TransactionQuery();
			 transactionQuery.setProjectCategory(TypeEnum.PROJECT_CATEGORY_TRANSFER.getType());
			 transactionQuery.setProjectId(transferProjectForLock.getProjectId());
			 transactionQuery.setTransferId(transferProjectForLock.getId());
			 List<Transaction> tranList =  transactionManager.selectTransactionsByQueryParams(transactionQuery);
			 
			 if(tranList.size()>0){//有成功的交易 项目置为30→52，fail_flag标记2撤销；交易置为部分转让
				 transferProjectForLock.setStatus(StatusEnum.TRANSFER_PROJECT_STATUS_LOAN.getStatus());
				 
				 transactionMapper.updateTransferStatusById(StatusEnum.TRANSACTION_TRANSFER_STATUS_PART_TRANSFERED.getStatus(),
						 StatusEnum.TRANSACTION_TRANSFER_STATUS_TRANSFERING.getStatus(), null,
							transaction.getId());
			 }else{//没成功的交易，项目置为30→90，fail_flag标记2撤销； 查询历史转让项目，是否有已成功的交易
				 transferProjectForLock.setStatus(StatusEnum.TRANSFER_PROJECT_STATUS_LOSE.getStatus());
				 
				 List<TransferProject> transferProjectList = this.queryTransferProjectListByTransactionId(transferProjectForLock.getTransactionId());
				 int historyNum = 0;
				 for(TransferProject transferproject:transferProjectList){
					 TransactionQuery tranQuery = new TransactionQuery();
					 tranQuery.setProjectCategory(TypeEnum.PROJECT_CATEGORY_TRANSFER.getType());
					 tranQuery.setProjectId(transferproject.getProjectId());
					 tranQuery.setTransferId(transferproject.getId());
					 List<Transaction> transferTranList =  transactionManager.selectTransactionsByQueryParams(tranQuery);
					 historyNum +=transferTranList.size();
				 }
				 if(historyNum>0){
					 transactionMapper.updateTransferStatusById(StatusEnum.TRANSACTION_TRANSFER_STATUS_PART_TRANSFERED.getStatus(),
							 StatusEnum.TRANSACTION_TRANSFER_STATUS_TRANSFERING.getStatus(), null,
								transaction.getId());
				 }else{
					 transactionMapper.updateTransferStatusById(StatusEnum.TRANSACTION_TRANSFER_STATUS_NO.getStatus(),
							 StatusEnum.TRANSACTION_TRANSFER_STATUS_TRANSFERING.getStatus(), null,
								transaction.getId());
				 }
				 
			 }
			 
			 transferProjectForLock.setFailTime(DateUtils.getCurrentDate());
			 transferProjectForLock.setFailFlag(TypeEnum.TRANSFER_PROJECT_FAIL_CANCLE.getType());
			
			 transferProjectForLock.setRemarks("用户终止转让");
			 this.updateByPrimaryKeySelective(transferProjectForLock);
			 
			 
			 this.transferEndSendMessage(transferProjectForLock.getId());
					
		} catch (ManagerException e) {
			logger.error("撤销转让项目异常",transactionId,e);
			throw new ManagerException(e);
		}
		  return result;
	}
	
	@Override
	public void transferEndSendMessage(Long id) {
		try {
			TransferProject transferProject = this.selectByPrimaryKey(id);
			if(transferProject==null){
				logger.error("转让项目结束，发送app消息异常,转让项目不存在,id=:",id);
				return;
			}
			
			 TransactionQuery transactionQuery = new TransactionQuery();
			 transactionQuery.setProjectCategory(TypeEnum.PROJECT_CATEGORY_TRANSFER.getType());
			 transactionQuery.setProjectId(transferProject.getProjectId());
			 transactionQuery.setTransferId(transferProject.getId());
			 List<Transaction> tranList =  transactionManager.selectTransactionsByQueryParams(transactionQuery);
			//已转本金  取交易表求和
			BigDecimal transferPrincipal = BigDecimal.ZERO;
			//转让价格  取交易表求和
			//BigDecimal transferAmount = BigDecimal.ZERO;
			for(Transaction tra : tranList){
				transferPrincipal = transferPrincipal.add(tra.getTransferPrincipal());
				//transferAmount = transferAmount.add(tra.getInvestAmount());
			}
				
			//累计获得   根据项目id查询代付表
			BigDecimal transferIncome = BigDecimal.ZERO;
			List<HostingPayTrade> transferHostPay = hostingPayTradeManager.getTransferHostingPayTradeByTransferId(transferProject.getId());
			for(HostingPayTrade hostPayTrad : transferHostPay){
				transferIncome = transferIncome.add(hostPayTrad.getAmount());
			}
			
			//转让结束提醒转让人
			MessageClient.sendMsgForCommon(transferProject.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.APP_TRANSFER_SUCCESS.getCode(), 
					transferProject.getTransferName().contains("期")?transferProject.getTransferName().substring(0, transferProject.getTransferName().indexOf("期") + 1):transferProject.getTransferName()
							,transferPrincipal.toString(),transferIncome.toString());
			
		} catch (ManagerException e) {
			logger.error("转让项目结束，发送app消息异常",id,e);
		}

	}

	

	@Override
	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	public void finishPreAuthTrade(Long transferId) throws Exception {
		try {
			// 发起转让
			List<HostingCollectTrade> freezeList = hostingCollectTradeManager.selectPreAuthApplySuccessByTransferId(transferId);
			if (Collections3.isNotEmpty(freezeList)) {
				hostingCollectTradeAuthManager.finishPreAuthTrade(freezeList);
			}
		} catch (ManagerException e) {
			logger.error("发起转让失败 transferId={} ", transferId, e);
			throw new Exception(e);
		}
	}

	@Override
	@Transactional(rollbackFor = { Exception.class }, propagation = Propagation.REQUIRED)
	public List<HostingPayTrade> transferSuccess(Long transferId) throws Exception {
		return null;
//		List<HostingPayTrade> hostingPayTrades = Lists.newArrayList();
//		try {
//			TransferProject transferProject = this.selectByPrimaryKey(transferId);
//			if (transferProject == null) {
//				logger.error("转让项目不存在，transferId={}", transferId);
//				return hostingPayTrades;
//			}
//			// 更新转让项目的项目状态为已转让
//			int num = updateProjectStatus(StatusEnum.TRANSFER_PROJECT_STATUS_LOAN.getStatus(),
//					StatusEnum.TRANSFER_PROJECT_WAIT_LOAN.getStatus(), transferId);
//			if (num < 1) {
//				logger.error("转让项目更新失败，transferId={}, newStatus={}, curStatus={}", transferId,
//						StatusEnum.TRANSFER_PROJECT_STATUS_LOAN.getStatus(), transferProject.getStatus());
//				return hostingPayTrades;
//			}
//			// 更新原始交易的状态为已转让
//			Transaction sourceTransaction = new Transaction();
//			sourceTransaction.setId(transferProject.getTransactionId());
//			//sourceTransaction.setStatus(StatusEnum.TRANSACTION_TRANSFERED.getStatus());
//			transactionManager.updateByPrimaryKeySelective(sourceTransaction);
//			
//			//查询待支付的交易本息
//			List<TransactionInterest> toTransferInterests = transactionInterestManager.selectToTransferInterestByTransactionId(transferProject.getTransactionId());
//			
//			// 更新原始交易本息的状态为已转让
//			TransactionInterest sourceInterest = new TransactionInterest();
//			sourceInterest.setStatus(StatusEnum.TRANSACTION_INTEREST_TRANSFER.getStatus());
//			sourceInterest.setTransactionId(transferProject.getTransactionId());
//			int updateInterestNum = transactionInterestManager.updateByTransactionId(sourceInterest);
//			logger.info("转让项目Id={} 交易本息表更新原始本息条数={}", transferId, updateInterestNum);
//			// 更新转让项目的交易状态为履约中
//			Transaction transferTransaction = new Transaction();
//			transferTransaction.setTransferId(transferId);
//			transferTransaction.setStatus(StatusEnum.TRANSACTION_REPAYMENT.getStatus());
//			transactionManager.updateByTransferId(transferTransaction);
//			//插入受让人交易本息记录
//			this.insertTransferTransactionInterest(transferId, transferProject,toTransferInterests);
//			List<Transaction> listTransaction=transactionMapper.selectByTransferId(transferId);
//			if(Collections3.isNotEmpty(listTransaction)){
//				for(Transaction biz:listTransaction){
//					// 募集完成发送站内信 和短信
//					MessageClient.sendMsgForCommon(biz.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.TRANSFER_RAISE.getCode(), 
//							DateUtils.getStrFromDate(DateUtils.getCurrentDate(),DateUtils.DATE_FMT_13),biz.getProjectName());
//					
//					//转让募集完成app信息
//					MessageClient.sendMsgForCommon(biz.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.APP_TRANSFER_RAISE_SUCCESS.getCode(), 
//							biz.getProjectName().contains("期")?biz.getProjectName().substring(0, biz.getProjectName().indexOf("期") + 1):biz.getProjectName(),biz.getId().toString());
//				}
//				
//			}
//			MessageClient.sendMsgForCommon(transferProject.getMemberId(), Constant.MSG_TEMPLATE_TYPE_PHONE, MessageEnum.TRANSFER_TRANSUCCESS.getCode(), 
//					transferProject.getTransferName());
//			
//			//转让成功提醒转让人
//			MessageClient.sendMsgForCommon(transferProject.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.APP_TRANSFER_SUCCESS.getCode(), 
//					transferProject.getTransferName().contains("期")?transferProject.getTransferName().substring(0, transferProject.getTransferName().indexOf("期") + 1):transferProject.getTransferName());
//			
//			// 1.发起批付，创建本地代付
//			return insertHostPayForTransferSuccess(transferProject, true);
//		} catch (ManagerException e) {
//			logger.error("转让项目代收完成后续业务失败 transferId={} ", transferId, e);
//			throw new Exception(e);
//		}
	}

	/**
	 * @Description:插入受让人交易本息记录
	 * @param transferId
	 * @param transferProject
	 * @throws ManagerException
	 * @author: fuyili
	 * @time:2016年9月22日 下午8:31:28
	 */
	@Override
	public void insertTransferTransactionInterest(Long transferId, TransferProject transferProject,List<TransactionInterest> toTransferInterests)
			throws ManagerException {
		try {
			// 根据转让项目id查询这个项目的所有转让交易transferId
			List<Transaction> transferTransactions = transactionMapper.selectByTransferId(transferId);
			// 累计总利息，计算最后一笔转让就交易本息的时候要用
			List<BigDecimal> grandTotalInterest = Lists.newArrayList();
			// 累计总本金，计算最后一笔转让就交易本息的时候要用
			List<BigDecimal> grandTotalPrincipal = Lists.newArrayList();
			// 初始化累计总本金和累计总利息
			for (int i = 0, size = toTransferInterests.size(); i < size; i++) {
				grandTotalInterest.add(BigDecimal.ZERO);
				grandTotalPrincipal.add(BigDecimal.ZERO);
			}
			// for循环交易，生成交易本息记录，插入到数据库
			for (int i = 0, size = transferTransactions.size() - 1; i < size; i++) {
				Transaction transferTransation = transferTransactions.get(i);
				TransactionInterest transferInterest = new TransactionInterest();
				// 总利息
				BigDecimal totalInterest = BigDecimal.ZERO;
				// 总本金
				BigDecimal totalPrincipal = BigDecimal.ZERO;
				// 查询待还款的交易本息
				for (int j = 0, sizeInterest = toTransferInterests.size(); j < sizeInterest; j++) {
					TransactionInterest toTransferInterest = toTransferInterests.get(j);
					// 转让交易的本金
					BigDecimal transfPrincipal = transferTransation.getTransferPrincipal();
					// 转让项目的本金
					BigDecimal transfProjectAmount = transferProject.getTransactionAmount();
					// 原交易本息的本金
					BigDecimal transactionsPayablePrincipal = toTransferInterest.getPayablePrincipal();
					// 原交易本息的利息
					BigDecimal transactionsPayableInterest = toTransferInterest.getPayableInterest();
					// 转让交易本息的应付本金
					BigDecimal transfTransactionPrincipal = FormulaUtil.calculateTransferTransactionInterest(
							transfProjectAmount, transfPrincipal, transactionsPayablePrincipal);
					// 计入累计总本金
					grandTotalPrincipal.set(j, grandTotalPrincipal.get(j).add(transfTransactionPrincipal));
					// 转让交易本金的应付利息
					BigDecimal transfTransactionInterest = FormulaUtil.calculateTransferTransactionInterest(
							transfProjectAmount, transfPrincipal, transactionsPayableInterest);
					// 计入累计总利息
					grandTotalInterest.set(j, grandTotalInterest.get(j).add(transfTransactionInterest));
					int totalDay = DateUtils.daysOfTwo(toTransferInterest.getStartDate(),
							toTransferInterest.getEndDate());
					transferInterest.setUnitInterest(transactionsPayableInterest.divide(new BigDecimal(totalDay),2, BigDecimal.ROUND_HALF_UP));
					transferInterest.setUnitPrincipal(transfTransactionPrincipal);
					transferInterest.setMemberId(transferTransation.getMemberId());
					transferInterest.setPayableInterest(transfTransactionInterest);
					transferInterest.setPayablePrincipal(transfTransactionPrincipal);
					transferInterest.setInterestId(toTransferInterest.getInterestId());
					transferInterest.setProjectId(toTransferInterest.getProjectId());
					transferInterest.setTransactionId(transferTransation.getId());
					transferInterest.setStartDate(toTransferInterest.getStartDate());
					transferInterest.setEndDate(toTransferInterest.getEndDate());
					transferInterest.setStatus(StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus());
					transferInterest.setExtraInterest(BigDecimal.ZERO);
					transferInterest.setRealPayPrincipal(BigDecimal.ZERO);
					transferInterest.setRealPayPrincipal(BigDecimal.ZERO);
					transferInterest.setOverdueFine(BigDecimal.ZERO);
					transferInterest.setPeriods(toTransferInterest.getPeriods());
					transactionInterestManager.insert(transferInterest);
					//遍历求和
					totalInterest = totalInterest.add(transfTransactionInterest);
					totalPrincipal = totalPrincipal.add(transfTransactionPrincipal);
				}
				//回写总收益和总本金
				Transaction update = new Transaction();
				update.setId(transferTransation.getId());
				update.setTotalInterest(totalInterest);
				update.setTotalPrincipal(totalPrincipal);
				transactionMapper.updateByPrimaryKeySelective(update);
			}
			// 最后一笔转让交易本息特殊处理
			Transaction transferTransation = Collections3.getLast(transferTransactions);
			TransactionInterest transferInterest = new TransactionInterest();
			// 总利息
			BigDecimal totalInterest = BigDecimal.ZERO;
			// 总本金
			BigDecimal totalPrincipal = BigDecimal.ZERO;
			// 查询待还款的交易本息
			for (int l = 0, sizeLast = toTransferInterests.size(); l < sizeLast; l++) {
				TransactionInterest toTransferInterest = toTransferInterests.get(l);
				// 原交易本息的本金
				BigDecimal transactionsPayablePrincipal = toTransferInterest.getPayablePrincipal();
				// 原交易本息的利息
				BigDecimal transactionsPayableInterest = toTransferInterest.getPayableInterest();
				// 转让交易本息的应付本金
				BigDecimal transfTransactionPrincipal = transactionsPayablePrincipal.subtract(grandTotalPrincipal.get(l));
				// 转让交易本金的应付利息
				BigDecimal transfTransactionInterest = transactionsPayableInterest.subtract(grandTotalInterest.get(l));
				int totalDay = DateUtils.daysOfTwo(toTransferInterest.getStartDate(), toTransferInterest.getEndDate());
				transferInterest.setUnitInterest(transactionsPayableInterest.divide(new BigDecimal(totalDay), 2, BigDecimal.ROUND_HALF_UP));
				transferInterest.setUnitPrincipal(transfTransactionPrincipal);
				transferInterest.setMemberId(transferTransation.getMemberId());
				transferInterest.setPayableInterest(transfTransactionInterest);
				transferInterest.setPayablePrincipal(transfTransactionPrincipal);
				transferInterest.setInterestId(toTransferInterest.getInterestId());
				transferInterest.setProjectId(toTransferInterest.getProjectId());
				transferInterest.setTransactionId(transferTransation.getId());
				transferInterest.setStartDate(toTransferInterest.getStartDate());
				transferInterest.setEndDate(toTransferInterest.getEndDate());
				transferInterest.setStatus(StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus());
				transferInterest.setExtraInterest(BigDecimal.ZERO);
				transferInterest.setRealPayPrincipal(BigDecimal.ZERO);
				transferInterest.setRealPayPrincipal(BigDecimal.ZERO);
				transferInterest.setOverdueFine(BigDecimal.ZERO);
				transferInterest.setPeriods(toTransferInterest.getPeriods());
				transactionInterestManager.insert(transferInterest);
				// 遍历求和
				totalInterest = totalInterest.add(transfTransactionInterest);
				totalPrincipal = totalPrincipal.add(transfTransactionPrincipal);
			}
			// 回写总收益和总本金
			Transaction update = new Transaction();
			update.setId(transferTransation.getId());
			update.setTotalInterest(totalInterest);
			update.setTotalPrincipal(totalPrincipal);
			transactionMapper.updateByPrimaryKeySelective(update);
		} catch (Exception e) {
			logger.error("转让完成插入转让后交易本息记录失败",e);
			throw new ManagerException(e);
		}
	}
	
	
	@Override
	public List<TransferProject> findIndexInvestingProjectList(Integer num) throws ManagerException {
		try {
			Map<String, Object> map = Maps.newHashMap();
			map.put("num", num);
			return transferProjectMapper.findIndexInvestingProjectList(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	@Override
	public List<TransferProject> findIndexNotInvestingProjectList(Integer num) throws ManagerException {
		try {
			Map<String, Object> map = Maps.newHashMap();
			map.put("num", num);
			return transferProjectMapper.findIndexNotInvestingProjectList(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	/**
	 * 
	 * @Description:转让成功之后的代付
	 * @author: fuyili
	 * @time:2016年9月22日 下午8:35:48
	 */
	@Override
	public List<HostingPayTrade> insertHostPayForTransferSuccess(TransferProject transferProject, boolean firstFlag)
			throws ManagerException {

		List<HostingPayTrade> hostingPayTradeList = Lists.newArrayList();
		List<HostingPayTrade> transferFeePay = null;
		List<HostingPayTrade> transferPay = null;
		if (!firstFlag) {
			// 查询新浪没有发起的代付重新发起
			Map<String, Object> queryWaitPayTrade = Maps.newHashMap();
			queryWaitPayTrade.put("sourceId", transferProject.getId());
			queryWaitPayTrade.put("tradeStatus", TradeStatus.TRADE_FINISHED.name());
			queryWaitPayTrade.put("type", TypeEnum.HOSTING_PAY_TRADE_TRANSFER_FEE.getType());
			transferFeePay = hostingPayTradeManager.getHostingPayTradeByParam(queryWaitPayTrade);
			queryWaitPayTrade.put("sourceId", transferProject.getId());
			queryWaitPayTrade.put("tradeStatus", TradeStatus.TRADE_FINISHED.name());
			queryWaitPayTrade.put("type", TypeEnum.HOSTING_PAY_TRADE_TRANSFER_AMOUNT.getType());
			transferPay = hostingPayTradeManager.getHostingPayTradeByParam(queryWaitPayTrade);
		}
		try {
			if (!firstFlag && Collections3.isNotEmpty(transferFeePay) && Collections3.isNotEmpty(transferPay)) {
				// 异常处理入口进来的且
				return hostingPayTradeList;
			}
			String ip = null;
			SysDict dict;
			dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
			if (dict != null) {
				ip = dict.getValue();
			}
			String batchPayNo = SerialNumberUtil.generateBatchPayTradeaNo();
			// 代付手续费 转让项目总额*1‰
			SysDict transferFeeObj = sysDictManager.findByGroupNameAndKey("transferRate", "transferRate");
			BigDecimal transferFee = new BigDecimal(0.001);
			if (transferFeeObj != null) {
				transferFee = new BigDecimal(transferFeeObj.getValue());
			}
			// 统计所有的支付金额
			BigDecimal totalPayToTransferMember = transactionMapper.getInvestAmountByTransferId(transferProject.getId());
			// 手续费
			BigDecimal handlingFee = transferProject.getTransferAmount().multiply(transferFee).setScale(2, BigDecimal.ROUND_HALF_UP);
			if (firstFlag || Collections3.isEmpty(transferFeePay)) {
				HostingPayTrade hostPayForFee = new HostingPayTrade();
				hostPayForFee.setTradeNo(SerialNumberUtil.generatePayTradeaNo(SerialNumberUtil.getInternalMemberId()));
				hostPayForFee.setBatchPayNo(batchPayNo);
				hostPayForFee.setAmount(handlingFee);
				hostPayForFee.setPayeeId(SerialNumberUtil.getInternalMemberId());
				hostPayForFee.setSourceId(transferProject.getId());
				hostPayForFee.setTradeStatus(TradeStatus.WAIT_PAY.name());
				hostPayForFee.setType(TypeEnum.HOSTING_PAY_TRADE_TRANSFER_FEE.getType());
				hostPayForFee.setProjectId(transferProject.getProjectId());
				hostPayForFee.setRemarks(transferProject.getId() + ":转让项目手续费");
				hostPayForFee.setUserIp(ip);
				if (hostingPayTradeManager.insertSelective(hostPayForFee) > 0) {
					hostingPayTradeList.add(hostPayForFee);
				}
			}
			if (firstFlag || Collections3.isEmpty(transferPay)) {
				// 代付转让人
				HostingPayTrade hostPayForTransfer = new HostingPayTrade();
				hostPayForTransfer.setTradeNo(SerialNumberUtil.generatePayTradeaNo(transferProject.getMemberId()));
				hostPayForTransfer.setBatchPayNo(batchPayNo);
				hostPayForTransfer.setAmount(totalPayToTransferMember.subtract(handlingFee));
				hostPayForTransfer.setPayeeId(transferProject.getMemberId());
				hostPayForTransfer.setSourceId(transferProject.getId());// 转让项目ID
				hostPayForTransfer.setTradeStatus(TradeStatus.WAIT_PAY.name());
				hostPayForTransfer.setType(TypeEnum.HOSTING_PAY_TRADE_TRANSFER_AMOUNT.getType());
				hostPayForTransfer.setProjectId(transferProject.getProjectId());
				hostPayForTransfer.setRemarks(transferProject.getPrefixTransferName() + "转让收款，已扣除" + handlingFee + "元手续费");
				hostPayForTransfer.setUserIp(ip);
				if (hostingPayTradeManager.insertSelective(hostPayForTransfer) > 0) {
					hostingPayTradeList.add(hostPayForTransfer);
				}
			}
		} catch (ManagerException e) {
			throw new ManagerException(e);
		}
		return hostingPayTradeList;
	}

	@Override
	public void createSinaHostPayForTransferSuccess(List<HostingPayTrade> hostingPayTrades) throws Exception{
		if (Collections3.isEmpty(hostingPayTrades)) {
			logger.error("[转让项目代付]-本地代付记录为空，hostingPayTrades={}", hostingPayTrades);
			return;
		}
		String batchPayNo = hostingPayTrades.get(0).getBatchPayNo();
		String ip=hostingPayTrades.get(0).getUserIp();
		List<TradeArgs> tradeArgList = Lists.newArrayList();
		String summary = "转让项目代付";
		for (HostingPayTrade hostingPayTrade : hostingPayTrades) {
			TradeArgs tradeArg = new TradeArgs();
			if(SerialNumberUtil.getInternalMemberId().equals(hostingPayTrade.getPayeeId())){
				tradeArg.setAccountType(AccountType.BASIC);
				tradeArg.setIdType(IdType.EMAIL);
			}else{
				tradeArg.setAccountType(AccountType.SAVING_POT);
				tradeArg.setIdType(IdType.UID);
			}
			tradeArg.setMoney(new Money(hostingPayTrade.getAmount()));
			tradeArg.setPayeeId(SerialNumberUtil.generateIdentityId(hostingPayTrade.getPayeeId()));
			tradeArg.setRemark(summary);
			tradeArg.setTradeNo(hostingPayTrade.getTradeNo());
			tradeArgList.add(tradeArg);
		}
		sinaPayClient.createBatchPayTrade(batchPayNo, summary, ip, tradeArgList, TradeCode.PAY_TO_TRANSFER_FOR_TRANSACTION_AMOUNT,
				BatchTradeNotifyMode.single_notify);
		return;
	}

	/**
	 * @desc 转让项目代付回调处理
	 * @param tradeStatus
	 * @param tradeNo
	 * @param outTradeNo
	 * @return
	 * @throws Exception
	 * @author fuyili
	 * @time 2016年9月23日 上午10:41:52
	 *
	 */
	@Override
	public ResultDO<?> afterHostPayForTransferSuccess(String tradeStatus, String tradeNo, String outTradeNo)throws Exception{
		ResultDO<?> result = new ResultDO();
		try{
			HostingPayTrade payTrade = hostingPayTradeManager.getByTradeNo(tradeNo);
			HostingPayTrade hostingPayTrade = hostingPayTradeManager.getByIdForLock(payTrade.getId());
			// 如果是最终状态，则直接返回
			if (hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_FINISHED.name())
					|| hostingPayTrade.getTradeStatus().equals(TradeStatus.TRADE_FAILED.name())) {
				logger.info("转让项目代付是最终状态，tradeNo=" + tradeNo);
				return result;
			}
			// 将交易状态置为最终状态
			hostingPayTrade.setTradeStatus(tradeStatus);
			hostingPayTrade.setOutTradeNo(outTradeNo);
			int updateNum = hostingPayTradeManager.updateHostingPayTradeStatus(tradeStatus, TradeStatus.WAIT_PAY.name(), outTradeNo,
					hostingPayTrade.getId());
			if (updateNum > 0) {
				if (!tradeStatus.equals(TradeStatus.TRADE_FINISHED.name())) {
					return result;
				}
				// 如果成功，则同步余额以及插入资金流水
				if(hostingPayTrade.getType()==TypeEnum.HOSTING_PAY_TRADE_TRANSFER_AMOUNT.getType()){
					Balance balance=new Balance();
					try{
						balance = balanceManager.synchronizedBalance(hostingPayTrade.getPayeeId(), TypeEnum.BALANCE_TYPE_PIGGY);
					}catch(Exception e){
						logger.error("[转让项目代付成功]-[转让款]，资金流水同步余额失败，memberId={}",hostingPayTrade.getPayeeId(),e);
						balance = balanceManager.queryBalance(hostingPayTrade.getPayeeId(), TypeEnum.BALANCE_TYPE_BASIC);
					}
					capitalInOutLogManager.insert(hostingPayTrade.getPayeeId(), TypeEnum.FINCAPITALINOUT_TYPE_PAY_TRANSFER_PAYMENT, hostingPayTrade.getAmount(), null,
							balance.getAvailableBalance(), hostingPayTrade.getId().toString(), hostingPayTrade.getRemarks(), TypeEnum.BALANCE_TYPE_PIGGY);
				}else if(hostingPayTrade.getType()==TypeEnum.HOSTING_PAY_TRADE_TRANSFER_FEE.getType()){
					Balance balance=new Balance();
					try{
						balance = balanceManager.synchronizedBalance(hostingPayTrade.getPayeeId(), TypeEnum.BALANCE_TYPE_BASIC);
					}catch(Exception e){
						logger.error("[转让项目代付成功]-[手续费]，资金流水同步余额失败，memberId={}",hostingPayTrade.getPayeeId(),e);
						balance = balanceManager.queryBalance(hostingPayTrade.getPayeeId(), TypeEnum.BALANCE_TYPE_BASIC);
					}
					capitalInOutLogManager.insert(hostingPayTrade.getPayeeId(), TypeEnum.FINCAPITALINOUT_TYPE_PAY_TRANSFER_FEE,
							hostingPayTrade.getAmount(), null, balance.getAvailableBalance(), hostingPayTrade.getId().toString(), hostingPayTrade.getSourceId()+"转让项目手续费",
							TypeEnum.BALANCE_TYPE_BASIC);
				}
				//a
			}
			if(hostingPayTrade.getType()==TypeEnum.HOSTING_PAY_TRADE_TRANSFER_AMOUNT.getType()){//转让款类型
				TransferProject transferProejct = this.selectByPrimaryKey(hostingPayTrade.getSourceId());
				//交易转让 可认购本金为0表示项目满额
				Balance balance = balanceManager.queryBalance(transferProejct.getTransactionId(),TypeEnum.BALANCE_TYPE_TRANSFER_AMOUNT);
				if (balance.getBalance() != null && balance.getBalance().doubleValue() == 0) {
					//转让项目满额，发送信息
					 this.transferEndSendMessage(transferProejct.getId());
				}
			}
			
		}catch(Exception e){
			logger.error("转让项目代付回调处理失败，tradestatus={},tradeNo={},outTradeNo={}",tradeStatus,tradeNo,outTradeNo);
			throw e;
		}
		return result;
	}

	@Override
	public Page<TransferProject> findTransferProjectListForMember(TransferProjectQuery transferProjectQuery)
			throws ManagerException {
		try {
			List<TransferProject> transferProjectList = Lists.newArrayList();
			long count = transferProjectMapper.selectCountTransferListForMember(transferProjectQuery);
			if (count > 0) {
				transferProjectList = transferProjectMapper.selectTransferListForMember(transferProjectQuery);
				for (TransferProject transferProject :transferProjectList){
					//设置转让进度
					setProgress(transferProject);
					//设置转让手续费
					// 代付手续费    转让项目总额*1‰
					BigDecimal handleFee = setTransferFee(transferProject);
					transferProject.setTransferGain(transferProject.getTransferAmount().subtract(handleFee));
				}
			}
			Page<TransferProject> page = new Page<TransferProject>();
			page.setiTotalDisplayRecords(count);
			page.setPageNo(transferProjectQuery.getCurrentPage());
			page.setiDisplayLength(transferProjectQuery.getPageSize());
			page.setiTotalRecords(count);
			page.setData(transferProjectList);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public void loseTransferProject(Long transferId,Integer flag) throws ManagerException {
		//flag   1提前还款，2转让截止时间到期
		
		 TransferProject transferProjectForLock = this.selectByPrimaryKeyForLock(transferId);

		 Transaction transaction =transactionManager.selectTransactionByIdLock(transferProjectForLock.getTransactionId());
		
		 TransactionQuery transactionQuery = new TransactionQuery();
		 transactionQuery.setProjectCategory(TypeEnum.PROJECT_CATEGORY_TRANSFER.getType());
		 transactionQuery.setProjectId(transferProjectForLock.getProjectId());
		 transactionQuery.setTransferId(transferProjectForLock.getId());
		 List<Transaction> tranList =  transactionManager.selectTransactionsByQueryParams(transactionQuery);
		 
		 if(tranList.size()>0){//有成功的交易 项目置为30→52，fail_flag标记2撤销；交易置为部分转让
			 transferProjectForLock.setStatus(StatusEnum.TRANSFER_PROJECT_STATUS_LOAN.getStatus());
			 
			 transactionMapper.updateTransferStatusById(StatusEnum.TRANSACTION_TRANSFER_STATUS_PART_TRANSFERED.getStatus(),
					 StatusEnum.TRANSACTION_TRANSFER_STATUS_TRANSFERING.getStatus(), null,
						transaction.getId());
			 
		 }else{//没成功的交易，项目置为30→90，fail_flag标记2撤销； 交易置为履约中
			 transferProjectForLock.setStatus(StatusEnum.TRANSFER_PROJECT_STATUS_LOSE.getStatus());
			 
			 List<TransferProject> transferProjectList = this.queryTransferProjectListByTransactionId(transferProjectForLock.getTransactionId());
			 int historyNum = 0;
			 for(TransferProject transferproject:transferProjectList){
				 TransactionQuery tranQuery = new TransactionQuery();
				 tranQuery.setProjectCategory(TypeEnum.PROJECT_CATEGORY_TRANSFER.getType());
				 tranQuery.setProjectId(transferproject.getProjectId());
				 tranQuery.setTransferId(transferproject.getId());
				 List<Transaction> transferTranList =  transactionManager.selectTransactionsByQueryParams(tranQuery);
				 historyNum =+ transferTranList.size();
			 }
			 if(historyNum>0){
				 transactionMapper.updateTransferStatusById(StatusEnum.TRANSACTION_TRANSFER_STATUS_PART_TRANSFERED.getStatus(),
						 StatusEnum.TRANSACTION_TRANSFER_STATUS_TRANSFERING.getStatus(), null,
							transaction.getId());
			 }else{
				 transactionMapper.updateTransferStatusById(StatusEnum.TRANSACTION_TRANSFER_STATUS_NO.getStatus(),
						 StatusEnum.TRANSACTION_TRANSFER_STATUS_TRANSFERING.getStatus(), null,
							transaction.getId());
			 }
		 }
		 
		 transferProjectForLock.setFailTime(DateUtils.getCurrentDate());
		 transferProjectForLock.setFailFlag(TypeEnum.TRANSFER_PROJECT_FAIL_LOSE.getType());
		
		 if(flag==1){//flag   1提前还款，2转让截止时间到期
			 transferProjectForLock.setRemarks("项目提前还款，系统终止转让");
		 }else{
			 transferProjectForLock.setRemarks("转让期限结束，系统终止转让");
		 }
		 
		 this.updateByPrimaryKeySelective(transferProjectForLock);
		 
		 
		 this.transferEndSendMessage(transferProjectForLock.getId());
	}
	
	@Override
	public void afterLoseTransferProject(Long transferId) throws ManagerException {
		//转让没有流标概念
		/*int n = updateProjectStatus(StatusEnum.TRANSFER_PROJECT_STATUS_LOSE.getStatus(), StatusEnum.TRANSFER_PROJECT_STATUS_LOSING.getStatus(),
				transferId);
		if (n < 1) {
			return;
		}
		TransferProject tran = new TransferProject();
		tran.setId(transferId);
		tran.setFailTime(DateUtils.getCurrentDate());
		tran.setFailFlag(TypeEnum.TRANSFER_PROJECT_FAIL_LOSE.getType());
		tran.setStatus(StatusEnum.TRANSFER_PROJECT_STATUS_LOSE.getStatus());
		updateByPrimaryKeySelective(tran);
		//合同置为已过期
		transactionManager.expireContractForTransfer(tran.getId());		
		
		//更新原交易状态为回款中
		TransferProject transferProject = this.selectByPrimaryKey(transferId);
		Transaction transaction = transactionManager.selectTransactionById(transferProject.getTransactionId());
		
		transactionMapper.updateStatusByOrderId(StatusEnum.TRANSACTION_REPAYMENT.getStatus(),
				StatusEnum.TRANSACTION_TRANSFERING.getStatus(),
				null, transaction.getOrderId());		
		
		//转让流标后续处理
		List<Order> orders = orderMapper.selectUseAmountForInvestTransfer(transferId);
		if(Collections3.isNotEmpty(orders)){
			for(Order order:orders){
				couponManager.extendCouponEndDateForProjectLose(order, order.getOrderTime(), DateUtils.getCurrentDate());
				//更新交易表的状态为流标
				transactionMapper.updateStatusByOrderId(StatusEnum.TRANSACTION_LOSE.getStatus(),
						StatusEnum.TRANSACTION_INVESTMENTING.getStatus(),
						RemarksEnum.TRANSACTION_LOSE_AND_RETURN_CAPITAL.getRemarks(), order.getId());
				MessageClient.sendMsgForCommon(order.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM,
						MessageEnum.TRANSFER_RAISEFAILED.getCode(),
						DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_7),
						order.getProjectName(), order.getInvestAmount().toString());
				Transaction tra = transactionManager.getTransactionByOrderId(order.getId());
				//转让失败  app消息提醒受让人
				MessageClient.sendMsgForCommon(order.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.APP_TRANSFER_RAISE_FAIL.getCode(), 
						transferProject.getTransferName().contains("期")?transferProject.getTransferName().substring(0, transferProject.getTransferName().indexOf("期") + 1):transferProject.getTransferName()
								,tra.getId().toString());
			}
		}
		//转让失败站内信通知转让人
		MessageClient.sendMsgForCommon(transferProject.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM,
				MessageEnum.TRANSFER_FAILED.getCode(),
				transferProject.getTransferName(),DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_7));
		//转让失败  app消息提醒转让人
		MessageClient.sendMsgForCommon(transferProject.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.APP_TRANSFER_FAIL.getCode(), 
				transferProject.getTransferName().contains("期")?transferProject.getTransferName().substring(0, transferProject.getTransferName().indexOf("期") + 1):transferProject.getTransferName());
		*/
		
	}

	private BigDecimal setTransferFee(TransferProject transferProject) throws ManagerException {
		SysDict transferFeeObj = sysDictManager.findByGroupNameAndKey("transferRate", "transferRate");
		BigDecimal transferFee = new BigDecimal(0.001);
		if(transferFeeObj!=null){
			transferFee = new BigDecimal(transferFeeObj.getValue());
		}
		BigDecimal handlingFee = transferProject.getTransferAmount().multiply(transferFee)
				.setScale(2, BigDecimal.ROUND_HALF_UP);
		transferProject.setTransferFee(handlingFee);
		return handlingFee;
	}

	private void setProgress(TransferProject transferProject) throws ManagerException {
		//项目进度
		Balance balance = balanceManager.queryBalance(transferProject.getId(), TypeEnum.BALANCE_TYPE_PROJECT);
		String progress = "0";
		if(balance!=null){
			 progress = FormulaUtil.getTransferNumberProgress(transferProject.getTransactionAmount(), balance.getAvailableBalance());
		}
		transferProject.setTransferProgress(progress);
	}

	@Override
	public TransferProject totalTransferProjectByMemberId(Long memberId) throws ManagerException {
		try {
			return transferProjectMapper.totalTransferProjectByMemberId(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransferProject> totalTransferProjectByMemberIdNum(Long memberId) throws ManagerException {
		try {
			return transferProjectMapper.totalTransferProjectByMemberIdNum(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateStatusByProjectId(Long projectId, int newStatus, int oldStatus) throws ManagerException {
		try {
			return transferProjectMapper.updateStatusByProjectId(projectId, newStatus, oldStatus);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public TransferProject selectByTransactionIdAndMemberId(Long transactionId,
			Long memberId) throws ManagerException {
		try {
			return transferProjectMapper.selectByTransactionIdAndMemberId(transactionId, memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<TransferProject> selectTransferByMemberId(Long memberId) throws ManagerException {
		try {
			return transferProjectMapper.selectTransferByMemberId(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public BigDecimal getTransferProjectBalanceById(Long id) {
		// 可用余额
		BigDecimal availableBalance = null;
		try {
			TransferProject transferPro= this.selectByPrimaryKey(id);
			
			// 从缓存中找可用余额
			// availableBalance = RedisProjectClient.getProjectBalance(id);
			if (availableBalance == null) {
				// logger.info("项目"+id+"，可用余额在redis未找到。");
				// 如果为Null，到余额表找
				Balance _balance = balanceManager.queryBalance(transferPro.getTransactionId(), TypeEnum.BALANCE_TYPE_TRANSFER_AMOUNT);
				if (_balance != null) {
					availableBalance = _balance.getAvailableBalance();
				} else {
					logger.debug("转让项目" + id + "，可用余额在余额表未找到。");
				}
			}
			if (availableBalance == null) {
				// 再没有，那只能从项目中去找了
				availableBalance = transferPro.getSubscriptionPrincipal();
			}
			logger.debug("转让项目" + id + "，可用余额" + availableBalance);
		} catch (ManagerException e) {
			logger.error("转让项目" + id + "查找", e);
		}
		return availableBalance;
	}

	@Override
	public Page<TransferProjectPageBiz> queryPageTransferProjectPageBiz(TransferProjectPageQuery query) {
		Page<TransferProjectPageBiz> page=new Page<>();
		List<TransferProjectPageBiz> list= transferProjectMapper.queryTransferProjectByPage(query);
		if (Collections3.isNotEmpty(list)){
			for (TransferProjectPageBiz biz:list) {
				//转让人获得费用
				BigDecimal income= hostingCollectTradeMapper.queryTotalAmountBySourceIdAndType(biz.getId(),12);
				//手续费
				BigDecimal serviceFee= hostingCollectTradeMapper.queryTotalAmountBySourceIdAndType(biz.getId(),13);
				biz.setIncome(income);
				biz.setServiceFee(serviceFee);
			}
		}
		int totalCount = transferProjectMapper.queryTransferProjectCountByPage(query);
		page.setData(list);
		page.setiTotalDisplayRecords(totalCount);
		page.setiTotalRecords(totalCount);
		page.setPageNo(query.getCurrentPage());
		page.setiDisplayLength(query.getPageSize());
		return page;
	}

	@Override
	public void handleTransaction(Transaction transaction, boolean isFull, Balance transferBalance)
			throws ManagerException {
		try {
			TransferProject tPro = selectByPrimaryKey(transaction.getTransferId());
			Transaction oraTransaction = transactionManager.selectTransactionById(tPro.getTransactionId());
			// 查询转让人的当前交易本息
			TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
			transactionInterestQuery.setTransactionId(tPro.getTransactionId());
			List<TransactionInterest> traList = transactionInterestManager
					.selectTransactionInterestsByQueryParams(transactionInterestQuery);
			BigDecimal transTotalPrincipal = BigDecimal.ZERO;
			BigDecimal transTotalInterest = BigDecimal.ZERO;
			BigDecimal transTotalExtraInterest = BigDecimal.ZERO;
			BigDecimal transTotalExtraProjectInterest = BigDecimal.ZERO;
			Integer transTotalDays = 0;
			boolean isAllTransferred = true;
			for (TransactionInterest interest : traList) {
				// 加锁
				TransactionInterest oraInterest = transactionInterestManager
						.selectByPrimaryKeyForLock(interest.getId());
				if (oraInterest.getStatus() != StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus()
						|| oraInterest.getEndDate().before(transaction.getTransactionTime())) {
					continue;
				}
				// 计算并保存单笔转让交易的交易本息
				TransactionInterest transferInterest = calcTransferTransactionInterest(oraInterest, oraTransaction,
						transaction, isFull);
				logger.info("转让项目id={},转让交易id={},转让本息id={},转出本金={},转出利息={},转出额外利息={}", transaction.getTransferId(),
						transaction.getId(), interest.getId(), transferInterest.getRealPayPrincipal(),
						transferInterest.getRealPayInterest(), transferInterest.getRealPayExtraInterest());
				// 统计转让总本息
				transTotalPrincipal = transTotalPrincipal.add(transferInterest.getRealPayPrincipal());
				transTotalInterest = transTotalInterest.add(transferInterest.getRealPayInterest());
				if (transferInterest.getRealPayExtraInterest() != null) {
					transTotalExtraInterest = transTotalExtraInterest.add(transferInterest.getRealPayExtraInterest());
				}
				if (transferInterest.getRealPayExtraProjectInterest() != null) {
					transTotalExtraProjectInterest = transTotalExtraProjectInterest.add(transferInterest.getRealPayExtraProjectInterest());
				}
				transTotalDays += DateUtils.getIntervalDays(transferInterest.getStartDate(),
						transferInterest.getEndDate()) + 1;
				// 更新转让原始交易的剩余本息
				TransactionInterest newOraInterest = calcOraTransactionInterest(oraInterest, transferInterest, isFull);
				if (newOraInterest.getRealPayPrincipal().compareTo(BigDecimal.ZERO) == 1
						|| newOraInterest.getRealPayInterest().compareTo(BigDecimal.ZERO) == 1) {
					isAllTransferred = false;
				}
				logger.info("转让项目id={},转让交易id={},转让本息id={},剩余本金={},剩余利息={},剩余额外利息={}", transaction.getTransferId(),
						transaction.getId(), interest.getId(), newOraInterest.getRealPayPrincipal(),
						newOraInterest.getRealPayInterest(), newOraInterest.getRealPayExtraInterest());
			}
			// 更新转让交易总本息
			Transaction updateTransferTransaction = new Transaction();
			updateTransferTransaction.setId(transaction.getId());
			updateTransferTransaction.setTotalPrincipal(transTotalPrincipal);
			updateTransferTransaction.setTotalInterest(transTotalInterest);
			updateTransferTransaction.setTotalExtraInterest(transTotalExtraInterest);
			updateTransferTransaction.setTotalExtraProjectInterest(transTotalExtraProjectInterest);
			updateTransferTransaction.setTotalDays(transTotalDays);
			transactionManager.updateByPrimaryKeySelective(updateTransferTransaction);
			// 更新转让项目余额
			TransferProject updateTransferProject = new TransferProject();
			updateTransferProject.setId(tPro.getId());
			updateTransferProject.setTransferredPrincipal(tPro.getSubscriptionPrincipal().subtract(
					transferBalance.getBalance()));
			updateByPrimaryKeySelective(updateTransferProject);
			// 满额业务处理
			if (isFull) {
				// 更新原始交易总本息
				Transaction updateOraTransaction = new Transaction();
				updateOraTransaction.setId(tPro.getTransactionId());
				// 更新原始交易状态
				logger.info("转让项目本息更新完毕,状态变更为已转让,转让项目ID={},是否已全部转让标记={}", transaction.getTransferId(), isAllTransferred);
				if (isAllTransferred) {
					updateOraTransaction.setStatus(StatusEnum.TRANSACTION_COMPLETE.getStatus());
				}
				updateOraTransaction.setTransferStatus(StatusEnum.TRANSACTION_TRANSFER_STATUS_ALL_TRANSFERED
						.getStatus());
				// updateOraTransaction.setTotalDays(0);
				transactionManager.updateByPrimaryKeySelective(updateOraTransaction);
			}
			// 同步存钱罐
			Balance balance = new Balance();
			try {
				balance = balanceManager.synchronizedBalance(transaction.getMemberId(), TypeEnum.BALANCE_TYPE_PIGGY);
			} catch (Exception e) {
				logger.error("同步转让人存钱罐账户失败", e);
				balance = balanceManager.queryBalance(transaction.getMemberId(), TypeEnum.BALANCE_TYPE_PIGGY);
			}
			// 记录资金流水
			if (transaction.getUsedCapital() != null && transaction.getUsedCapital().doubleValue() > 0) {
				String remark = MessageFormat.format(RemarksEnum.FINCAPITALINOUT_TYPE_INVEST.getRemarks(),
						StringUtil.getShortProjectName(tPro.getTransferName()));
				// 记录用户投资资金流水
				capitalInOutLogManager.insert(transaction.getMemberId(), TypeEnum.FINCAPITALINOUT_TYPE_INVEST, null,
						transaction.getUsedCapital(), balance.getAvailableBalance(), transaction.getId().toString(),
						remark,
						TypeEnum.BALANCE_TYPE_PIGGY);
			}
			// 计算费率以及保存代付
			List<HostingPayTrade> hostingPayTrades = saveHostPayForTransferTransaction(tPro, oraTransaction,
					transaction, true);
			// 发起代付
			createSinaHostPayForTransferSuccess(hostingPayTrades);
		} catch (Exception e) {
			logger.error("转让交易后续业务处理失败, transactionId={}", transaction.getId(), e);
			throw new ManagerException(e);
		}
	}

	private TransactionInterest calcTransferTransactionInterest(TransactionInterest oraInterest,
			Transaction oraTransaction, Transaction transferTransaction, boolean isFull)
			throws ManagerException {
		Date transferDate = DateUtils.getDateFromString(DateUtils.getDateStrFromDate(transferTransaction
				.getTransactionTime()));
		Date startDate = DateUtils.getDateFromString(DateUtils.getDateStrFromDate(oraInterest.getStartDate()));
		Date endDate = DateUtils.getDateFromString(DateUtils.getDateStrFromDate(oraInterest.getEndDate()));
		boolean isDateBetween = false;
		if (DateUtils.isDateBetween(transferDate, startDate, endDate)) {
			isDateBetween = true;
		}
		BigDecimal transferPayablePrincipal = null;
		BigDecimal transferRealPayPrincipal = null;
		BigDecimal transferPayableInterest = null;
		BigDecimal transferRealPayInterest = null;
		BigDecimal transferExtraInterest = null;
		BigDecimal transferRealPayExtraInterest = null;
		BigDecimal transferExtraProjectInterest = null;
		BigDecimal transferRealPayExtraProjectInterest = null;
		// 非最后一笔
		if (!isFull) {
			if (isDateBetween) {
				// 当期转让收益天数
				BigDecimal transferDays = new BigDecimal(DateUtils.getIntervalDays(transferDate, endDate) + 1);
				// 当期总收益天数
				BigDecimal totalDays = new BigDecimal(DateUtils.getIntervalDays(startDate, endDate) + 1);
				// 计算当期转让应付利息
				transferPayableInterest = oraInterest.getPayableInterest().multiply(transferDays)
						.multiply(transferTransaction.getTransferPrincipal())
						.divide(totalDays.multiply(oraTransaction.getInvestAmount()), 2, BigDecimal.ROUND_DOWN);
				// 实付利息
				transferRealPayInterest = transferPayableInterest;
				if (oraInterest.getExtraInterest() != null) {
					// 计算当期转让应付额外利息
					transferExtraInterest = oraInterest.getExtraInterest().multiply(transferDays)
							.multiply(transferTransaction.getTransferPrincipal())
							.divide(totalDays.multiply(oraTransaction.getInvestAmount()), 2, BigDecimal.ROUND_DOWN);
					// 实付额外利息
					transferRealPayExtraInterest = transferExtraInterest;
				}
				if (oraInterest.getExtraProjectInterest() != null) {
					// 计算当期转让应付额外项目加息
					transferExtraProjectInterest = oraInterest.getExtraProjectInterest().multiply(transferDays)
							.multiply(transferTransaction.getTransferPrincipal())
							.divide(totalDays.multiply(oraTransaction.getInvestAmount()), 2, BigDecimal.ROUND_DOWN);
					// 实付额外项目加息
					transferRealPayExtraProjectInterest = transferExtraProjectInterest;
				}
			} else {
				// 计算当期转让应付利息
				transferPayableInterest = oraInterest.getPayableInterest()
						.multiply(transferTransaction.getTransferPrincipal())
						.divide(oraTransaction.getInvestAmount(), 2, BigDecimal.ROUND_DOWN);
				// 实付利息
				transferRealPayInterest = transferPayableInterest;
				if (oraInterest.getExtraInterest() != null) {
					// 计算当期转让应付额外利息
					transferExtraInterest = oraInterest.getExtraInterest()
							.multiply(transferTransaction.getTransferPrincipal())
							.divide(oraTransaction.getInvestAmount(), 2, BigDecimal.ROUND_DOWN);
					// 实付额外利息
					transferRealPayExtraInterest = transferExtraInterest;
				}
				if (oraInterest.getExtraProjectInterest() != null) {
					// 计算当期转让应付额外利息
					transferExtraProjectInterest = oraInterest.getExtraProjectInterest()
							.multiply(transferTransaction.getTransferPrincipal())
							.divide(oraTransaction.getInvestAmount(), 2, BigDecimal.ROUND_DOWN);
					// 实付额外利息
					transferRealPayExtraProjectInterest = transferExtraProjectInterest;
				}
			}
			// 计算当期转让应付本金
			transferPayablePrincipal = oraInterest.getPayablePrincipal()
					.multiply(transferTransaction.getTransferPrincipal())
					.divide(oraTransaction.getInvestAmount(), 2, BigDecimal.ROUND_DOWN);
			// 实付本金
			transferRealPayPrincipal = transferPayablePrincipal;
		} else {
			if (isDateBetween && transferDate.after(startDate)) {
				// 当期转让收益天数
				BigDecimal transferDays = new BigDecimal(DateUtils.getIntervalDays(transferDate, endDate) + 1);
				// 当期总收益天数
				BigDecimal totalDays = new BigDecimal(DateUtils.getIntervalDays(startDate, endDate) + 1);
				// 计算当期转让应付利息
				transferPayableInterest = oraInterest.getPayableInterest().multiply(transferDays)
						.multiply(transferTransaction.getTransferPrincipal())
						.divide(totalDays.multiply(oraTransaction.getInvestAmount()), 2, BigDecimal.ROUND_DOWN);
				// 实付利息
				transferRealPayInterest = transferPayableInterest;
				if (oraInterest.getExtraInterest() != null) {
					// 计算当期转让应付额外利息
					transferExtraInterest = oraInterest.getExtraInterest().multiply(transferDays)
							.multiply(transferTransaction.getTransferPrincipal())
							.divide(totalDays.multiply(oraTransaction.getInvestAmount()), 2, BigDecimal.ROUND_DOWN);
					// 实付额外利息
					transferRealPayExtraInterest = transferExtraInterest;
				}
				if (oraInterest.getExtraProjectInterest() != null) {
					// 计算当期转让应付额外利息
					transferExtraProjectInterest = oraInterest.getExtraProjectInterest().multiply(transferDays)
							.multiply(transferTransaction.getTransferPrincipal())
							.divide(totalDays.multiply(oraTransaction.getInvestAmount()), 2, BigDecimal.ROUND_DOWN);
					// 实付额外利息
					transferRealPayExtraProjectInterest = transferExtraProjectInterest;
				}
			} else {
				transferPayableInterest = oraInterest.getRealPayInterest();
				transferRealPayInterest = oraInterest.getRealPayInterest();
				transferExtraInterest = oraInterest.getRealPayExtraInterest();
				transferRealPayExtraInterest = oraInterest.getRealPayExtraInterest();
				transferExtraProjectInterest = oraInterest.getRealPayExtraProjectInterest();
				transferRealPayExtraProjectInterest = oraInterest.getRealPayExtraProjectInterest();
			}
			// 最后一笔转让交易
			transferPayablePrincipal = oraInterest.getRealPayPrincipal();
			transferRealPayPrincipal = oraInterest.getRealPayPrincipal();
		}
		TransactionInterest transferInterest = new TransactionInterest();
		transferInterest.setUnitInterest(oraInterest.getUnitInterest());
		transferInterest.setUnitPrincipal(oraInterest.getUnitPrincipal());
		transferInterest.setMemberId(transferTransaction.getMemberId());
		transferInterest.setPayableInterest(transferPayableInterest);
		transferInterest.setPayablePrincipal(transferPayablePrincipal);
		transferInterest.setExtraInterest(transferExtraInterest);
		transferInterest.setExtraProjectInterest(transferExtraProjectInterest);
		transferInterest.setInterestId(oraInterest.getInterestId());
		transferInterest.setProjectId(oraInterest.getProjectId());
		transferInterest.setTransactionId(transferTransaction.getId());
		transferInterest.setStartDate(isDateBetween ? transferDate : startDate);
		transferInterest.setEndDate(oraInterest.getEndDate());
		transferInterest.setStatus(StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus());
		transferInterest.setRealPayPrincipal(transferRealPayPrincipal);
		transferInterest.setRealPayInterest(transferRealPayInterest);
		transferInterest.setRealPayExtraInterest(transferRealPayExtraInterest);
		transferInterest.setRealPayExtraProjectInterest(transferRealPayExtraProjectInterest);
		transferInterest.setOverdueFine(BigDecimal.ZERO);
		transferInterest.setPeriods(oraInterest.getPeriods());
		transactionInterestManager.insert(transferInterest);
		return transferInterest;
	}

	private TransactionInterest calcOraTransactionInterest(TransactionInterest oraInterest,
			TransactionInterest transferInterest, boolean isFull)
			throws ManagerException {
		// 更新原始交易实付本息
		TransactionInterest updateModel = new TransactionInterest();
		updateModel.setRealPayPrincipal(oraInterest.getRealPayPrincipal().subtract(
				transferInterest.getRealPayPrincipal()));
		updateModel
				.setRealPayInterest(oraInterest.getRealPayInterest().subtract(transferInterest.getRealPayInterest()));
		if (oraInterest.getRealPayExtraInterest() != null && transferInterest.getRealPayExtraInterest() != null) {
			updateModel.setRealPayExtraInterest(oraInterest.getRealPayExtraInterest().subtract(transferInterest.getRealPayExtraInterest()));
		}
		if (oraInterest.getRealPayExtraProjectInterest() != null && transferInterest.getRealPayExtraProjectInterest() != null) {
			updateModel.setRealPayExtraProjectInterest(oraInterest.getRealPayExtraProjectInterest().subtract(
					transferInterest.getRealPayExtraProjectInterest()));
		}
		updateModel.setId(oraInterest.getId());
		if (isFull && updateModel.getRealPayPrincipal().compareTo(BigDecimal.ZERO) < 1
				&& updateModel.getRealPayInterest().compareTo(BigDecimal.ZERO) < 1
				&& updateModel.getRealPayExtraInterest().compareTo(BigDecimal.ZERO) < 1
				&& updateModel.getRealPayExtraProjectInterest().compareTo(BigDecimal.ZERO) < 1) {
			updateModel.setStatus(StatusEnum.TRANSACTION_INTEREST_ALL_PAYED.getStatus());
		}
		transactionInterestManager.updateTransactionInterest(updateModel);
		return updateModel;
	}

	private List<HostingPayTrade> saveHostPayForTransferTransaction(TransferProject transferProject,
			Transaction oraTransaction, Transaction transferTransaction, boolean firstFlag) throws ManagerException {

		List<HostingPayTrade> hostingPayTradeList = Lists.newArrayList();
		List<HostingPayTrade> transferFeePay = null;
		List<HostingPayTrade> transferPay = null;
		if (!firstFlag) {
			// 查询新浪没有发起的代付重新发起
			Map<String, Object> queryWaitPayTrade = Maps.newHashMap();
			queryWaitPayTrade.put("sourceId", transferProject.getId());
			queryWaitPayTrade.put("tradeStatus", TradeStatus.TRADE_FINISHED.name());
			queryWaitPayTrade.put("transactionId", transferTransaction.getId());
			queryWaitPayTrade.put("type", TypeEnum.HOSTING_PAY_TRADE_TRANSFER_FEE.getType());
			queryWaitPayTrade.put("tradeStatus", TradeStatus.TRADE_FINISHED.name());
			transferFeePay = hostingPayTradeManager.getHostingPayTradeByParam(queryWaitPayTrade);
			queryWaitPayTrade.put("type", TypeEnum.HOSTING_PAY_TRADE_TRANSFER_AMOUNT.getType());
			transferPay = hostingPayTradeManager.getHostingPayTradeByParam(queryWaitPayTrade);
		}
		try {
			if (!firstFlag && Collections3.isNotEmpty(transferFeePay) && Collections3.isNotEmpty(transferPay)) {
				// 异常处理入口进来的且
				return hostingPayTradeList;
			}
			String ip = null;
			SysDict dict;
			dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
			if (dict != null) {
				ip = dict.getValue();
			}
			String batchPayNo = SerialNumberUtil.generateBatchPayTradeaNo(transferTransaction.getId().toString());
			// 代付手续费
			BigDecimal handlingFee = transferProject.getTransferAmount().
					multiply(transferProject.getTransferRate()).
					multiply(transferTransaction.getTransferPrincipal()).
					divide(transferProject.getSubscriptionPrincipal().multiply(new BigDecimal(100)), 2, BigDecimal.ROUND_HALF_UP);
			if (firstFlag || Collections3.isEmpty(transferFeePay)) {
				HostingPayTrade hostPayForFee = new HostingPayTrade();
				hostPayForFee.setTradeNo(SerialNumberUtil.generatePayTradeaNo(SerialNumberUtil.getInternalMemberId()));
				hostPayForFee.setBatchPayNo(batchPayNo);
				hostPayForFee.setAmount(handlingFee);
				hostPayForFee.setPayeeId(SerialNumberUtil.getInternalMemberId());
				hostPayForFee.setSourceId(transferProject.getId());
				hostPayForFee.setTradeStatus(TradeStatus.WAIT_PAY.name());
				hostPayForFee.setType(TypeEnum.HOSTING_PAY_TRADE_TRANSFER_FEE.getType());
				hostPayForFee.setTransactionId(transferTransaction.getId());
				hostPayForFee.setProjectId(transferProject.getProjectId());
				hostPayForFee.setRemarks(transferProject.getId() + ":转让项目手续费");
				hostPayForFee.setUserIp(ip);
				if (hostingPayTradeManager.insertSelective(hostPayForFee) > 0) {
					hostingPayTradeList.add(hostPayForFee);
				}
			}
			if (firstFlag || Collections3.isEmpty(transferPay)) {
				// 代付转让人
				HostingPayTrade hostPayForTransfer = new HostingPayTrade();
				hostPayForTransfer.setTradeNo(SerialNumberUtil.generatePayTradeaNo(transferProject.getMemberId()));
				hostPayForTransfer.setBatchPayNo(batchPayNo);
				hostPayForTransfer.setAmount(transferTransaction.getInvestAmount().subtract(handlingFee));
				hostPayForTransfer.setPayeeId(transferProject.getMemberId());
				hostPayForTransfer.setSourceId(transferProject.getId());// 转让项目ID
				hostPayForTransfer.setTradeStatus(TradeStatus.WAIT_PAY.name());
				hostPayForTransfer.setType(TypeEnum.HOSTING_PAY_TRADE_TRANSFER_AMOUNT.getType());
				hostPayForTransfer.setTransactionId(transferTransaction.getId());
				hostPayForTransfer.setProjectId(transferProject.getProjectId());
				hostPayForTransfer.setRemarks(transferProject.getPrefixTransferName() + "转让收款，已扣除" + handlingFee
						+ "元手续费");
				hostPayForTransfer.setUserIp(ip);
				if (hostingPayTradeManager.insertSelective(hostPayForTransfer) > 0) {
					hostingPayTradeList.add(hostPayForTransfer);
				}
			}
		} catch (ManagerException e) {
			throw new ManagerException(e);
		}
		return hostingPayTradeList;
	}

	@Override
	public TransactionProjectDetailBiz queryInvestmentedTransactionProjectDetailBiz(Long transactionId, Long memberId) {
		return transferProjectMapper.queryInvestmentedTransactionProjectDetailBiz(transactionId,memberId);
	}

	@Override
	public Page<TransactionProjectDetailBiz> queryPageTransactionProjectDetailBiz(TransactionProjectDetailBizQuery query) {
		//return transferProjectMapper.queryPageTransactionProjectDetailBiz(query);
		List<TransactionProjectDetailBiz> list = Lists.newArrayList();
		int count = transferProjectMapper.queryPageCountTransactionProjectDetailBiz(query);
		list = transferProjectMapper.queryPageTransactionProjectDetailBiz(query);
		Page<TransactionProjectDetailBiz> page = new Page<TransactionProjectDetailBiz>();
		page.setData(list);
		// 每页总数
		page.setiDisplayLength(query.getPageSize());
		// 当前页
		page.setPageNo(query.getCurrentPage());
		// 总数
		page.setiTotalDisplayRecords(count);
		page.setiTotalRecords(count);
		return page;
	}

	@Override
	public BigDecimal getTotalTransferPrincipal(Long memberId) throws ManagerException {
		try {
			return transferProjectMapper.getTotalTransferPrincipal(memberId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
}
