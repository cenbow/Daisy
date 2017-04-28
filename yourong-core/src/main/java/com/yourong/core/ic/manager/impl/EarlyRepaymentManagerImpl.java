package com.yourong.core.ic.manager.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import rop.thirdparty.com.google.common.collect.Maps;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.core.ic.dao.ProjectMapper;
import com.yourong.core.ic.manager.EarlyRepaymentManager;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectEarlyRepayment;
import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.sys.manager.SysOperateInfoManager;
import com.yourong.core.sys.manager.SysUserManager;
import com.yourong.core.sys.model.SysOperateInfo;
import com.yourong.core.sys.model.SysUser;
import com.yourong.core.tc.dao.TransactionInterestMapper;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.query.TransactionInterestQuery;
import com.yourong.core.tc.model.query.TransactionQuery;
import com.yourong.core.uc.dao.EnterpriseMapper;
import com.yourong.core.uc.dao.MemberMapper;

@Component
public class EarlyRepaymentManagerImpl implements EarlyRepaymentManager{
	
	private static final Logger logger = LoggerFactory.getLogger(EarlyRepaymentManagerImpl.class);
	
	@Autowired
	private ProjectMapper projectMapper;
	
	@Autowired
	private TransactionInterestMapper transactionInterestMapper;
	
	@Autowired
	private MemberMapper memberMapper;
	
	@Autowired
	private EnterpriseMapper enterpriseMapper;
	
	
	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired
	private TransactionInterestManager transactionInterestManager;
	
	@Autowired
	private SysOperateInfoManager sysOperateInfoManager;
	
	@Autowired
	private SysUserManager sysUserManager;
	
	@Autowired
	private TransferProjectManager transferProjectManager;
	
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	
	@Override
	public Page<ProjectEarlyRepayment> findByPage(Page<ProjectEarlyRepayment> pageRequest, Map<String, Object> map)
			throws ManagerException{
		
		map.put("startRow", pageRequest.getiDisplayStart());
		map.put("pageSize", pageRequest.getiDisplayLength());
	
		List<Project> projectList = projectMapper.findEarlyRepaymentProject(map);
		List<ProjectEarlyRepayment> projectEarlyRepaymentList = BeanCopyUtil.mapList(projectList, ProjectEarlyRepayment.class);
		
		for(ProjectEarlyRepayment projectEarlyRepayment:projectEarlyRepaymentList){
			Map<String,Object> maps = Maps.newHashMap();
			maps.put("projectId", projectEarlyRepayment.getId());
			TransactionInterest transactionInterest = transactionInterestMapper.selectTransactionInterestsByProjectId(maps);
			if(transactionInterest!=null){
				projectEarlyRepayment.setRemainingExtraInterest(transactionInterest.getRealPayExtraInterest());
				projectEarlyRepayment.setRemainingTotalInterest(transactionInterest.getRealPayInterest());
				projectEarlyRepayment.setRemainingTotalPrincipal(transactionInterest.getRealPayPrincipal());
				projectEarlyRepayment.setRemainingTotalInterestByBorrower(transactionInterest.getRealPayInterest().subtract(transactionInterest.getRealPayExtraInterest()));
			}else{
				projectEarlyRepayment.setRemainingExtraInterest(BigDecimal.ZERO);
				projectEarlyRepayment.setRemainingTotalInterest(BigDecimal.ZERO);
				projectEarlyRepayment.setRemainingTotalPrincipal(BigDecimal.ZERO);
				projectEarlyRepayment.setRemainingTotalInterestByBorrower(BigDecimal.ZERO);
			}
			if(projectEarlyRepayment.getPrepayment()==1){
				Map<String, Object> sysOpMap = Maps.newHashMap();
				sysOpMap.put("sourceId", projectEarlyRepayment.getId());
				sysOpMap.put("operateTableType", TypeEnum.OPERATE_TYPE_PROJECT.getType());
				sysOpMap.put("operateCode", TypeEnum.OPERATE_PROJECT_PREREPAYMENT.getCode());
				SysOperateInfo sysOperateInfo= 	sysOperateInfoManager.selectOperateBySourceId(sysOpMap);
				
				projectEarlyRepayment.setOperateDate(sysOperateInfo.getOperateTime());
				projectEarlyRepayment.setOperateRemarks(sysOperateInfo.getRemarks());
				SysUser sysUser = sysUserManager.selectByPrimaryKey(sysOperateInfo.getOperateId());
				projectEarlyRepayment.setOperateName(sysUser.getName());
			}
		}
		int totalCount = projectMapper.findEarlyRepaymentProjectCount(map);
		pageRequest.setiTotalDisplayRecords(totalCount);
		pageRequest.setiTotalRecords(totalCount);
		pageRequest.setData(projectEarlyRepaymentList);
		
		return pageRequest;
	}
	
	
	public ResultDO<Object> prepayment(Date prepaymentDate,Long projectId,String prepaymentRemarkSys,String prepaymentRemarkFront,Long operaId)
			throws ManagerException{
		TransactionQuery transactionQuery = new TransactionQuery();
		transactionQuery.setProjectId(projectId);
		List<Transaction> transactionList= transactionManager.selectTransactionsByQueryParams(transactionQuery);
		for(Transaction transaction:transactionList){
			if(transaction.getStatus()==StatusEnum.TRANSACTION_COMPLETE.getStatus()){
				continue;
			}
			TransactionInterestQuery transactionInterestQuery =new TransactionInterestQuery();
			transactionInterestQuery.setTransactionId(transaction.getId());
			List<TransactionInterest> tInList = transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
			for(TransactionInterest transactionInterest: tInList){
				//根据交易本息start_date和end_date
				transactionInterest.getStartDate();
				transactionInterest.getEndDate();
				this.transactionProcess(prepaymentDate, transactionInterest, transaction);
			}
		}
		
		sysOperateInfoManager.saveOperateInfo(projectId,TypeEnum.OPERATE_TYPE_PROJECT.getType(),operaId,TypeEnum.OPERATE_PROJECT_PREREPAYMENT.getDesc()
				,TypeEnum.OPERATE_PROJECT_PREREPAYMENT.getCode(),prepaymentRemarkSys);
		Project upPor = new Project();
		upPor.setId(projectId);
		upPor.setPrepayment(1);
		upPor.setPrepaymentTime(prepaymentDate);
		upPor.setPrepaymentRemark(prepaymentRemarkFront);
		projectMapper.updateByPrimaryKeySelective(upPor);
		
		//提前还款,转让项目处理
		Map<String,Object> map = Maps.newHashMap();
		map.put("projectId", projectId);
		List<TransferProject> transferList = transferProjectManager.queryTransferProjectListByMap(map);
		for(TransferProject transferPro :transferList){
			TransferProject tranForLock = transferProjectManager.selectByPrimaryKeyForLock(transferPro.getId());
			if(tranForLock.getStatus()!= StatusEnum.TRANSFER_PROJECT_STATUS_INVESTING.getStatus()){
				continue ;
			}
			taskExecutor.execute(new TransferProjectFailThread(tranForLock.getId(),1));
		}
		
		return null;
	}
	
	private class TransferProjectFailThread implements Runnable {
		private Long id;
		private Integer flag;
		public TransferProjectFailThread(final Long id,final Integer flag) {
			this.id = id;
			this.flag =flag;//1提前还款，2转让截止时间到期
		}
		public void run() {
			try {
				transferProjectManager.loseTransferProject(id,flag);
			} catch (ManagerException e) {
				logger.error("【提前还款转让失败】发生异常{}",id,e);
			}
		}
	}
	
	private void transactionProcess(Date prepaymentDate,TransactionInterest transactionInterest,Transaction transaction) throws ManagerException  {
		
		if(DateUtils.compareTwoDate(prepaymentDate,transactionInterest.getStartDate())==1){//提前还款时间<start_date
			TransactionInterest upTransactionInterest = new TransactionInterest();
			upTransactionInterest.setId(transactionInterest.getId());
			upTransactionInterest.setPayType(TypeEnum.REPAYMENT_TYPE_PREREPAYMENT.getType());
			if(transactionInterest.getRealPayPrincipal().compareTo(BigDecimal.ZERO)!=1){//实付本金为0时，代表本息皆为0
				upTransactionInterest.setStatus(StatusEnum.TRANSACTION_INTEREST_ALL_PAYED.getStatus());
				upTransactionInterest.setPayType(TypeEnum.REPAYMENT_TYPE_PREREPAYMENT.getType());
				upTransactionInterest.setTopayDate(prepaymentDate);
			}
			upTransactionInterest.setRealPayPrincipal(transactionInterest.getRealPayPrincipal());// 生成交易本息时同时生成实付字段，之后实时更新 ,对象初始化为0，需显式赋值
			upTransactionInterest.setRealPayInterest(BigDecimal.ZERO);
			upTransactionInterest.setRealPayExtraInterest(BigDecimal.ZERO);
			upTransactionInterest.setTopayDate(prepaymentDate);
			transactionInterestManager.updateTransactionInterest(upTransactionInterest);
		}	
		if(DateUtils.compareTwoDate(transactionInterest.getEndDate(),prepaymentDate)==1){//end_date<提前还款时间
			return;
		}	
		if(DateUtils.compareTwoDate(transactionInterest.getStartDate(),prepaymentDate)!=2 &&//  !提前还款时间<start_date
				DateUtils.compareTwoDate(transactionInterest.getEndDate(),prepaymentDate)!=1){// !end_date<提前还款时间
			int days = DateUtils.daysOfTwo(transactionInterest.getStartDate(),prepaymentDate)+1;
			
			int currentDays = DateUtils.daysOfTwo(transactionInterest.getStartDate(),transactionInterest.getEndDate())+1;
			
			
			BigDecimal annualizedRate = transaction.getAnnualizedRate();
			if(transaction.getExtraProjectAnnualizedRate()!=null){
				annualizedRate = annualizedRate.add(transaction.getExtraProjectAnnualizedRate());
			}
			
			Project project = projectMapper.selectByPrimaryKey(transaction.getProjectId());
			
			//高额收益券
			if(transaction.getExtraInterestDay()>0){
				this.transactionProcessExtraHighInterest(prepaymentDate, transactionInterest, transaction, days, currentDays, project, annualizedRate);
			}else{
				this.transactionProcessNormal(prepaymentDate, transactionInterest, transaction, days, currentDays, project, annualizedRate);
			}
			
		}
	}
	
	//普通提前还款 无高额加息
	private void transactionProcessNormal(Date prepaymentDate,TransactionInterest transactionInterest,Transaction transaction,
			int days,int currentDays,Project project,BigDecimal annualizedRate
			) throws ManagerException {
		BigDecimal interest = BigDecimal.ZERO;
		BigDecimal realPayExtraInterest = BigDecimal.ZERO;
		BigDecimal realPayExtraProjectInterest = BigDecimal.ZERO;
		if(transaction.getExtraAnnualizedRate()!=null){
			annualizedRate = annualizedRate.add(transaction.getExtraAnnualizedRate());
		}
		
		//等本等息按月还款
		if(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(project.getProfitType())){
			interest = transactionInterest.getRealPayInterest().multiply(new BigDecimal(days))
					.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		//按日计息，按月付息，到期还本
		if(DebtEnum.RETURN_TYPE_DAY.getCode().equals(project.getProfitType())){
			interest = transactionInterest.getRealPayInterest().multiply(new BigDecimal(days))
					.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		//一次性还本付息
		if(DebtEnum.RETURN_TYPE_ONCE.getCode().equals(project.getProfitType())){
			interest = transactionInterest.getRealPayInterest().multiply(new BigDecimal(days))
					.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		
		// 按日计息，按季付息，到期还本
		if(DebtEnum.RETURN_TYPE_DAY_SEASON.getCode().equals(project.getProfitType())) {
			interest = transactionInterest.getRealPayInterest().multiply(new BigDecimal(days))
					.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		// 等本等息按周还款
		if(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getCode().equals(project.getProfitType())) {
			interest = transactionInterest.getRealPayInterest().multiply(new BigDecimal(days))
					.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		if(transactionInterest.getRealPayExtraInterest()!=null){
			if(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(project.getProfitType())){
				/*interest = transactionInterest.getPayableInterest().multiply(new BigDecimal(days))
						.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);*/
				//取实付字段
				realPayExtraInterest = transactionInterest.getRealPayExtraInterest().multiply(new BigDecimal(days))
						.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
			//按日计息，按月付息，到期还本
			if(DebtEnum.RETURN_TYPE_DAY.getCode().equals(project.getProfitType())){ 
				/*interest = FormulaUtil.calculateInterest(transaction.getInvestAmount(),transaction.getAnnualizedRate(),days);*/
				realPayExtraInterest = transactionInterest.getRealPayExtraInterest().multiply(new BigDecimal(days))
						.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
			//一次性还本付息
			if(DebtEnum.RETURN_TYPE_ONCE.getCode().equals(project.getProfitType())){
				/*interest = FormulaUtil.calculateInterest(transaction.getInvestAmount(),transaction.getAnnualizedRate(),days);*/
				realPayExtraInterest = transactionInterest.getRealPayExtraInterest().multiply(new BigDecimal(days))
						.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
			
			// 等本等息按周还款
			if(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getCode().equals(project.getProfitType())) {
				realPayExtraInterest = transactionInterest.getRealPayExtraInterest().multiply(new BigDecimal(days))
						.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
			// 按日计息，按季付息，到期还本
			if(DebtEnum.RETURN_TYPE_DAY_SEASON.getCode().equals(project.getProfitType())) {
				/*interest = FormulaUtil.calculateInterestByAvgSeason(transaction.getInvestAmount(),transaction.getAnnualizedRate(), days);*/
				realPayExtraInterest = transactionInterest.getRealPayExtraInterest().multiply(new BigDecimal(days))
						.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
		}
		//项目加息
		if(transactionInterest.getRealPayExtraProjectInterest()!=null){
			if(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(project.getProfitType())){
				realPayExtraProjectInterest = transactionInterest.getRealPayExtraProjectInterest().multiply(new BigDecimal(days))
						.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
			//按日计息，按月付息，到期还本
			if(DebtEnum.RETURN_TYPE_DAY.getCode().equals(project.getProfitType())){ 
				realPayExtraProjectInterest = transactionInterest.getRealPayExtraProjectInterest().multiply(new BigDecimal(days))
						.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
			//一次性还本付息
			if(DebtEnum.RETURN_TYPE_ONCE.getCode().equals(project.getProfitType())){
				realPayExtraProjectInterest = transactionInterest.getRealPayExtraProjectInterest().multiply(new BigDecimal(days))
						.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
			
			// 等本等息按周还款
			if(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getCode().equals(project.getProfitType())) {
				/*interest = transactionInterest.getPayableInterest().multiply(new BigDecimal(days))
						.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);*/
				//取实付字段
				realPayExtraProjectInterest =transactionInterest.getRealPayExtraProjectInterest().multiply(new BigDecimal(days))
						.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
			// 按日计息，按季付息，到期还本
			if(DebtEnum.RETURN_TYPE_DAY_SEASON.getCode().equals(project.getProfitType())) {
				realPayExtraProjectInterest = transactionInterest.getRealPayExtraProjectInterest().multiply(new BigDecimal(days))
						.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
		}
		
		BigDecimal realPayInterest = interest;
		TransactionInterest upR = new TransactionInterest();
		upR.setRealPayExtraInterest(realPayExtraInterest);
		upR.setRealPayInterest(realPayInterest);
		upR.setRealPayExtraProjectInterest(realPayExtraProjectInterest);
		upR.setRealPayPrincipal(transactionInterest.getPayablePrincipal());
		upR.setId(transactionInterest.getId());
		upR.setPayType(TypeEnum.REPAYMENT_TYPE_PREREPAYMENT.getType());
		upR.setTopayDate(prepaymentDate);
		transactionInterestManager.updateTransactionInterest(upR);
	} 
	
	
	//高额加息 提前还款
	private void transactionProcessExtraHighInterest(Date prepaymentDate,TransactionInterest transactionInterest,Transaction transaction,
			int days,int currentDays,Project project,BigDecimal annualizedRate
			) throws ManagerException {
			BigDecimal interest = BigDecimal.ZERO;
			BigDecimal realPayExtraInterest = BigDecimal.ZERO;
			BigDecimal realPayExtraProjectInterest = BigDecimal.ZERO;
			
			BigDecimal payableInterest = transactionInterest.getRealPayInterest().subtract(transactionInterest.getRealPayExtraInterest());
			//等本等息按月还款
			if(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(project.getProfitType())){
				interest = payableInterest.multiply(new BigDecimal(days))
						.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
			// 等本等息按周还款
			if(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getCode().equals(project.getProfitType())) {
				interest = payableInterest.multiply(new BigDecimal(days))
						.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
			
			//按日计息，按月付息，到期还本
			if(DebtEnum.RETURN_TYPE_DAY.getCode().equals(project.getProfitType())){
				interest = payableInterest.multiply(new BigDecimal(days))
						.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
			//一次性还本付息
			if(DebtEnum.RETURN_TYPE_ONCE.getCode().equals(project.getProfitType())){
				interest = payableInterest.multiply(new BigDecimal(days))
						.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
			
			// 按日计息，按季付息，到期还本
			if(DebtEnum.RETURN_TYPE_DAY_SEASON.getCode().equals(project.getProfitType())) {
				interest = payableInterest.multiply(new BigDecimal(days))
						.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
			//高额年化
			BigDecimal extraHighRate = transaction.getExtraAnnualizedRate();
			TransactionInterest upR = new TransactionInterest();
			int extraDays=0;
			//高额收益券
			if(DateUtils.compareTwoDate(transactionInterest.getExtraEndDay(),prepaymentDate)!=1){//extra_date>=提前还款时间
				extraDays = DateUtils.daysOfTwo(transactionInterest.getStartDate(),prepaymentDate)+1;
				upR.setExtraEndDay(prepaymentDate);
			}else{
				extraDays = DateUtils.daysOfTwo(transactionInterest.getStartDate(),transactionInterest.getExtraEndDay())+1;
			}
			realPayExtraInterest=FormulaUtil.calculateInterest(transaction.getInvestAmount(),extraHighRate,extraDays);
			
			//项目加息
			if(transactionInterest.getRealPayExtraProjectInterest()!=null){
				if(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(project.getProfitType())){
					realPayExtraProjectInterest = transactionInterest.getRealPayExtraProjectInterest().multiply(new BigDecimal(days))
							.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
				}
				//按日计息，按月付息，到期还本
				if(DebtEnum.RETURN_TYPE_DAY.getCode().equals(project.getProfitType())){ 
					realPayExtraProjectInterest = transactionInterest.getRealPayExtraProjectInterest().multiply(new BigDecimal(days))
							.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
				}
				//一次性还本付息
				if(DebtEnum.RETURN_TYPE_ONCE.getCode().equals(project.getProfitType())){
					realPayExtraProjectInterest = transactionInterest.getRealPayExtraProjectInterest().multiply(new BigDecimal(days))
							.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
				}
				
				// 等本等息按周还款
				if(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getCode().equals(project.getProfitType())) {
					/*interest = transactionInterest.getPayableInterest().multiply(new BigDecimal(days))
							.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);*/
					//取实付字段
					realPayExtraProjectInterest =transactionInterest.getRealPayExtraProjectInterest().multiply(new BigDecimal(days))
							.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
				}
				// 按日计息，按季付息，到期还本
				if(DebtEnum.RETURN_TYPE_DAY_SEASON.getCode().equals(project.getProfitType())) {
					realPayExtraProjectInterest = transactionInterest.getRealPayExtraProjectInterest().multiply(new BigDecimal(days))
							.divide(new BigDecimal(currentDays),10, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
				}
			}
			
			BigDecimal realPayInterest = interest;
			upR.setRealPayExtraInterest(realPayExtraInterest);
			upR.setRealPayInterest(realPayInterest);
			upR.setRealPayExtraProjectInterest(realPayExtraProjectInterest);
			upR.setRealPayPrincipal(transactionInterest.getRealPayPrincipal());//生成交易本息时同时生成实付字段，之后实时更新,对象初始化为0，需显式赋值
			upR.setId(transactionInterest.getId());
			upR.setPayType(TypeEnum.REPAYMENT_TYPE_PREREPAYMENT.getType());
			upR.setTopayDate(prepaymentDate);
			transactionInterestManager.updateTransactionInterest(upR);
		} 

}
