package com.yourong.core.fin.manager.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.core.bsc.dao.BscAttachmentMapper;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.fin.dao.OverdueLogMapper;
import com.yourong.core.fin.dao.OverdueRepayLogMapper;
import com.yourong.core.fin.dao.UnderwriteLogMapper;
import com.yourong.core.fin.manager.OverdueLogManager;
import com.yourong.core.fin.manager.OverdueRepayLogManager;
import com.yourong.core.fin.model.OverdueLog;
import com.yourong.core.fin.model.OverdueRepayLog;
import com.yourong.core.fin.model.UnderwriteLog;
import com.yourong.core.ic.dao.DebtInterestMapper;
import com.yourong.core.ic.manager.CollectManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.CollectionProcess;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectInterestBiz;
import com.yourong.core.sys.dao.SysOperateInfoMapper;
import com.yourong.core.sys.model.SysOperateInfo;
import com.yourong.core.tc.dao.TransactionInterestMapper;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.TransactionInterestForLateFee;

@Component
public class OverdueRepayLogManagerImpl implements OverdueRepayLogManager {
	@Autowired
	private OverdueLogMapper overdueLogMapper;
	
	@Autowired
	private OverdueRepayLogMapper overdueRepayLogMapper;
	
	@Autowired
	private UnderwriteLogMapper underWriteLogMapper;
	
	@Autowired
	private ProjectManager projectManager;
	@Autowired
	private DebtInterestMapper debtInterestMapper;
	
	@Autowired
	private SysOperateInfoMapper   sysOperateInfoMapper;
	
	@Autowired
	private CollectManager collectManager;
	
	@Autowired
	private BscAttachmentMapper bscAttachmentMapper;
	@Autowired
	private TransactionInterestMapper transactionInterestMapper;
	
	@Autowired
	private OverdueLogManager overdueLogManager;
	@Override
	public OverdueLog getOverdueLogByInterestId(Long interestId) throws ManagerException {
		try {
			return overdueLogMapper.getOverdueLogByInterestId(interestId);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	@Override
	public int insertSelective(OverdueLog overdueLog) throws ManagerException {
		try {
			return overdueLogMapper.insertSelective(overdueLog);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	@Override
	public OverdueLog selectOverdueByProjectId(Map<String, Object> map) throws ManagerException {
		try {
			return overdueLogMapper.selectOverdueByProjectId(map);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	/**
	 * 
	 * @desc TODO  保存逾期记录
	 * @param project
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年1月29日 下午2:39:48
	 *
	 */
	@Override
	public int saveOverdueLog(ProjectInterestBiz project) throws ManagerException {
		OverdueLog overdueLog=new OverdueLog();
		overdueLog.setProjectId(project.getProjectId());
		overdueLog.setInterestId(project.getInterestId());
		overdueLog.setUnderwriteMemberId(project.getThirdMemberId());
		overdueLog.setOverduePrincipal(project.getPayablePrincipal());
		overdueLog.setOverdueInterest(project.getPayableInterest());
		overdueLog.setStatus(1);
		UnderwriteLog underwriteLog=underWriteLogMapper.getUnderwriteLogByInterestId(project.getInterestId());
		if(underwriteLog!=null){
			overdueLog.setUnderwriteId(underwriteLog.getId());
		}
		try {
			return overdueLogMapper.insertSelective(overdueLog);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	@Override
	public int findOverdueByProjectIdTotalCount(Map<String, Object> map) throws ManagerException {
		try {
			return overdueLogMapper.findOverdueByProjectIdTotalCount(map);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	/**
	 * 
	 * @desc TODO 保存逾期还款记录
	 * @param overdueRepayLog
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年2月1日 下午1:45:43
	 *
	 */
	@Override
	public int saveOverdueRepayLog(ProjectInterestBiz project) throws ManagerException {
		OverdueRepayLog overdueRepayLog=new OverdueRepayLog();
		overdueRepayLog.setProjectId(project.getProjectId());
		overdueRepayLog.setRepayDate(project.getRefundTime());
		overdueRepayLog.setOverdueDay(project.getOverdueDays());
		overdueRepayLog.setOverduePrincipal(project.getOverduePrincipal());
		overdueRepayLog.setOverdueInterest(project.getOverdueInterest());
		overdueRepayLog.setOverdueFine(project.getLateFees());
		overdueRepayLog.setBreachAmount(project.getBreachAmount());
		overdueRepayLog.setPayableAmount(project.getPayableAmount());
		overdueRepayLog.setRealpayAmount(project.getRealPayAmount());
		overdueRepayLog.setOverdueId(project.getOverdueIds());
		overdueRepayLog.setUnreturnPrincipal(project.getUnreturnPrincipal());
		overdueRepayLog.setRepayType(StatusEnum.OVERDUE_REPAYTYPE_UNDERLINE.getStatus());
		overdueRepayLog.setRepayStatus(StatusEnum.OVERDUE_REPAYSTATUS_HADPAY.getStatus());
		overdueRepayLog.setRepayTime(new Date());
		// 1,垫资还款
		overdueRepayLog.setType(1);
		// 本息期数
		overdueRepayLog.setInterestPeriods(project.getInterestPeriods());
		try {
			int i= overdueRepayLogMapper.insertSelective(overdueRepayLog);
			if(i>0){
				//记录逾期还款操作人
				SysOperateInfo info=new SysOperateInfo();
				info.setSourceId(overdueRepayLog.getId());
				info.setOperateId(project.getOperateId());
				info.setOperateTableType(TypeEnum.OPERATE_TYPE_OVERDUE.getType());
				info.setOperateMsg(TypeEnum.OPERATE_OVERDUE_REPAYMENT.getDesc());
				info.setOperateCode(TypeEnum.OPERATE_OVERDUE_REPAYMENT.getCode());
				info.setOperateTime(new Date());
				info.setUpdateTime(new Date());
				info.setCreateTime(new Date());
				info.setDelFlag(1);
				sysOperateInfoMapper.insertSelective(info);
			}
			return i;
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	/**
	 * 
	 * @desc 统计逾期还款记录数
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年2月23日 上午11:15:47
	 *
	 */
	@Override
	public int countOverdueRepayLogByProjectId(Long projectId) throws ManagerException {
		try {
			return overdueRepayLogMapper.countOverdueRepayLogByProjectId(projectId);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	
	@Override
	public int updateByPrimaryKeySelective(OverdueRepayLog overdueRepayLog) throws ManagerException {
		try {
			return overdueRepayLogMapper.updateByPrimaryKeySelective(overdueRepayLog);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	
	@Override
	public OverdueRepayLog saveRecordOnline(Long projectId, Date repayDate) throws ManagerException {
		try {
			ProjectInterestBiz pib = projectManager.getOverdueAmount(projectId, DateUtils.getDateStrFromDate(repayDate));
			if(pib!=null){
				String overdueId="";
				String interestPeriods="";
				Map<String, Object> map = Maps.newHashMap();
				map.put("projectId", projectId);
				map.put("endDate", repayDate);
				List<ProjectInterestBiz> list=debtInterestMapper.getOverdueInfoListByMap(map);
				if(Collections3.isNotEmpty(list)){
					for(ProjectInterestBiz biz:list){
						overdueId=overdueId + biz.getOverdueId() + ",";
						interestPeriods = interestPeriods + biz.getPeriods() + ",";
					}
				}
				OverdueRepayLog overdueRepayLog=new OverdueRepayLog();
				// 查询垫资记录的开始时间
				OverdueLog overdueLog = overdueLogManager.getUnderwriteStartDateByProjectIdandStatus(projectId);
				if(overdueLog!=null){
					overdueRepayLog.setOverdueStartDate(overdueLog.getStartDate());
				}
				overdueRepayLog.setProjectId(projectId);
				overdueRepayLog.setRepayDate(repayDate);
				overdueRepayLog.setUnreturnPrincipal(pib.getUnreturnPrincipal());//未还本金
				overdueRepayLog.setOverdueId(overdueId);//逾期记录id
				overdueRepayLog.setOverdueDay(pib.getOverdueDays());//逾期天数
				overdueRepayLog.setOverduePrincipal(pib.getPayablePrincipal());//逾期本金
				overdueRepayLog.setOverdueInterest(pib.getPayableInterest());//逾期利息
				overdueRepayLog.setOverdueFine(pib.getLateFees());//滞纳金
				overdueRepayLog.setPayableAmount(pib.getPayableAmount());//应付金额
				overdueRepayLog.setRealpayAmount(pib.getPayableAmount());//实际还款金额
				overdueRepayLog.setRepayType(StatusEnum.OVERDUE_REPAYTYPE_ONLINE.getStatus());//还款方式（1：线上；2：线下）
				overdueRepayLog.setRepayStatus(StatusEnum.OVERDUE_REPAYSTATUS_PAYING.getStatus());//还款状态（1-未还款；2-还款中；3-已还款；4-还款失败）
				overdueRepayLog.setType(1);
				overdueRepayLog.setRepayTime(repayDate);
				overdueRepayLog.setInterestPeriods(interestPeriods);
				if(overdueRepayLogMapper.insertSelective(overdueRepayLog)>0) {
					return overdueRepayLog;
				};
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return null;
		
	}

	@Override
	public OverdueRepayLog selectByPrimaryKey(Long id) throws ManagerException{
		try {
			return overdueRepayLogMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public int updateStatusByOverdueId(int beforeStatus, int afterStatus, Long overdueId) throws ManagerException {
		try {
			return overdueRepayLogMapper.updateStatusByOverdueId(beforeStatus,afterStatus,overdueId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public int updateStatusById(int beforeStatus, int afterStatus, Long id) throws ManagerException {
		try {
			return overdueRepayLogMapper.updateStatusById(beforeStatus,afterStatus,id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @desc 逾期结算记录 催收完成
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年5月25日 下午3:32:31
	 *
	 */
	@Override
	public List<OverdueRepayLog> getOverdueRepayListByProjectId(Long projectId) throws ManagerException {
		List<OverdueRepayLog>  listOverdue=overdueRepayLogMapper.getRepayLogByProjectId(projectId);
		if(Collections3.isEmpty(listOverdue)){
			return listOverdue;
		}
		for(OverdueRepayLog overdue:listOverdue){
			List<CollectionProcess> collectList=getCollectListing(overdue.getId(),overdue.getOverdueStartDate());
			//overdue.setOverdueDay(DateUtils.daysBetween(overdue.getOverdueStartDate(), overdue.getRepayTime()) + 1);
			overdue.setCollectList(collectList);
			}
		return listOverdue;
	}
	/**
	 * 
	 * @desc 催收中
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年5月27日 下午2:38:52
	 *
	 */
	@Override
	public OverdueRepayLog getRepayLogByProjectIdCollect(Long projectId) throws ManagerException {
		try {
			OverdueRepayLog overdue=null;
			overdue=overdueRepayLogMapper.getRepayLogByProjectIdCollect(projectId);
			if(overdue!=null){
				/*int i=collectManager.countCollectionByRepayId(overdue.getId());
				if(i<1){
					return null;
				}*/
				if(overdue.getOverdueStartDate()==null){
					return overdue;
				}
				overdue.setOverdueDay(DateUtils.daysBetween(overdue.getOverdueStartDate(), DateUtils.getCurrentDate()) + 1);
				//计算滞纳金
/*				BigDecimal overduePrincipal=BigDecimal.ZERO;
				BigDecimal overdueInterest=BigDecimal.ZERO;
				BigDecimal unreturnPrincipal=BigDecimal.ZERO;
				BigDecimal unreturnInterest=BigDecimal.ZERO;
				BigDecimal rate=BigDecimal.ZERO;
				BigDecimal totalAmount=BigDecimal.ZERO;
				if(overdue.getOverduePrincipal()!=null){
					overduePrincipal=overdue.getOverduePrincipal();
				}
				if(overdue.getOverdueInterest()!=null){
					overdueInterest=overdue.getOverdueInterest();
				}
				if(overdue.getUnreturnPrincipal()!=null){
					unreturnPrincipal=overdue.getUnreturnPrincipal();
				}
				if(overdue.getUnreturnInterest()!=null){
					unreturnInterest=overdue.getUnreturnInterest();
				}
				Project project=projectManager.selectByPrimaryKey(overdue.getProjectId());
				if(project!=null){
					rate=project.getLateFeeRate();
				}
				totalAmount=overduePrincipal.add(overdueInterest).add(unreturnPrincipal).add(unreturnInterest);
				overdue.setOverdueFine(FormulaUtil.getLateFeeAmount(totalAmount, rate,overdue.getOverdueDay()));*/
				BigDecimal rate=BigDecimal.ZERO;
				Project project=projectManager.selectByPrimaryKey(overdue.getProjectId());
				if(project!=null){
					rate=project.getLateFeeRate();
					overdue.setLateFeeRate(rate);
				}
				OverdueRepayLog overdueLog=new OverdueRepayLog();
				overdueLog.setOverdueId(overdue.getOverdueId());
				overdueLog.setLateFeeRate(rate);
				overdueLog.setOverdueStartDate(overdue.getOverdueStartDate());
				overdueLog.setId(overdue.getId());
				overdueLog.setProjectId(overdue.getProjectId());
				overdueLog=projectManager.getLateFeeByProjectId(overdueLog);
				if(overdue!=null){
					overdue.setOverdueFine(overdueLog.getOverdueFine());
				}
				List<CollectionProcess> collectList=getCollectListing(overdue.getId(),overdue.getOverdueStartDate());
				if(Collections3.isEmpty(collectList)){
					return overdue;
				}
				overdue.setCollectList(collectList);
			}
			return overdue;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	public List<CollectionProcess> getCollectListing(Long id,Date date) throws ManagerException{
		List<CollectionProcess> collectList=collectManager.getCollectListByRepayId(id);
		List<CollectionProcess> collectListing=Lists.newArrayList();
		if(Collections3.isEmpty(collectList)){
			return collectList;
		}
		for(int i=0;i<collectList.size();i++){
			CollectionProcess bean =new CollectionProcess();
			bean.setId(collectList.get(i).getId());
			bean.setCollectDetail(collectList.get(i).getCollectDetail());
			bean.setCollectForm(collectList.get(i).getCollectForm());
			bean.setCollectStatus(collectList.get(i).getCollectStatus());
			bean.setCollectTime(collectList.get(i).getCollectTime());
			bean.setDays(DateUtils.getIntervalDays(DateUtils.formatDate(date, DateUtils.DATE_FMT_3), collectList.get(i).getCollectTime())+1);
			bean.setNextDays(DateUtils.getIntervalDays(DateUtils.formatDate(date, DateUtils.DATE_FMT_3), collectList.get(i).getNextCollectTime())+1);
			if(i==0){
				bean.setNextCollectForm(collectList.get(i).getNextCollectForm());
				bean.setNextCollectTime(collectList.get(i).getNextCollectTime());
			}
			List<BscAttachment> bscAttachments = bscAttachmentMapper
					.findAttachmentsByKeyId(String.valueOf(bean.getId()));
				bean.setBscAttachments(bscAttachments);
				collectListing.add(bean);
		}
		/*for(CollectionProcess bean:collectList){ 
			bean.setDays(DateUtils.daysOfTwo(date, bean.getCollectTime()));
			bean.setNextDays(DateUtils.daysOfTwo(date, bean.getNextCollectTime()));
			List<BscAttachment> bscAttachments = bscAttachmentMapper
						.findAttachmentsByKeyId(String.valueOf(bean.getId()));
					bean.setBscAttachments(bscAttachments);
			}*/
		return collectListing;
	}
	/**
	 * 
	 * @Description:查询催收历程
	 * @param id
	 * @return
	 * @throws ManagerException
	 * @author: chaisen
	 * @time:2016年5月27日 下午2:39:21
	 */
	public List<CollectionProcess> getCollectList(Long id,Date date) throws ManagerException{
		List<CollectionProcess> collectList=collectManager.getCollectListByRepayId(id);
		if(Collections3.isNotEmpty(collectList)){
			for(CollectionProcess bean:collectList){ 
				bean.setDays(DateUtils.daysOfTwo(date, bean.getCollectTime()));
				bean.setNextDays(DateUtils.daysOfTwo(date, bean.getNextCollectTime()));
				List<BscAttachment> bscAttachments = bscAttachmentMapper
						.findAttachmentsByKeyId(String.valueOf(bean.getId()));
					bean.setBscAttachments(bscAttachments);
				}
		}
		return collectList;
	}
	
	@Override
	public Map<String, Object> getLateFeeByOverdueRepayLog(OverdueRepayLog overdueRepayLog,BigDecimal overdueFeeRate) throws ManagerException {
		Map<String, Object> map = Maps.newHashMap();
		try {
			//逾期天数
			int overdueDays = DateUtils.daysBetween(overdueRepayLog.getOverdueStartDate(), DateUtils.getCurrentDate())+1;
			//获取逾期结算记录对应本息记录
			String overdueId = overdueRepayLog.getOverdueId();
			String[] overdueIds = overdueId.split(",");
			BigDecimal totalLateFee = BigDecimal.ZERO;//总滞纳金
			BigDecimal totalUnreturnPrincipal = BigDecimal.ZERO;//总未还本金
			BigDecimal totalBorUnreturnInterest = BigDecimal.ZERO;//总借款人未还利息
			List<TransactionInterestForLateFee> transactionInterests = Lists.newArrayList();
			int overdueIdSize = overdueIds.length;
			for (String id : overdueIds) {
				//根据逾期记录id查询逾期记录
				OverdueLog log = overdueLogMapper.selectByPrimaryKey(Long.valueOf(id));
				//根据本息id查询交易本息记录
				List<TransactionInterestForLateFee> interestsByPeriod = transactionInterestMapper.getByProjectInterestId(log.getInterestId());
				//未还本金，未还利息总和
				for (TransactionInterestForLateFee interest : interestsByPeriod) {
					//根据交易ID查询对应的未还本金和利息
					//计算对应交易本息的未还本金和利息
					TransactionInterest unreturnPrincipalAndInterest = transactionInterestMapper.selectUnreturnPrincipalAndInterestByTransactionId(interest.getTransactionId());
					/*//未还本金
					BigDecimal unReturnPricipal = FormulaUtil.subtractDecimal(interest.getTotalPrincipal(), interest.getReceivedPrincipal());
					//未还利息
					BigDecimal unReturnInterest = FormulaUtil.subtractDecimal(interest.getTotalInterest(), interest.getReceivedInterest());
					//未还额外利息
					BigDecimal unReturnExtraInterest = FormulaUtil.subtractDecimal(interest.getTotalExtraInterest(), interest.getReceivedExtraInterest());
					//未还借款人需支付利息
					BigDecimal borUnreturnInterest = FormulaUtil.subtractDecimal(unReturnInterest,unReturnExtraInterest);*/
					//未还本息总和
					BigDecimal unReturnPrincipalAndInterest = unreturnPrincipalAndInterest.getPayablePrincipal().add(unreturnPrincipalAndInterest.getPayableInterest());
					//每一笔交易本息需要归还的滞纳金
					BigDecimal tInterestLateFee = FormulaUtil.getTransactionInterestLateFeeAmount(unReturnPrincipalAndInterest, overdueFeeRate, overdueDays,overdueIdSize);
					interest.setOverdueFine(tInterestLateFee);
					totalLateFee = totalLateFee.add(tInterestLateFee);
					/*//交易本息  未还本金
					totalUnreturnPrincipal = FormulaUtil.addDecimal(totalUnreturnPrincipal,interest.getPayablePrincipal());
					//交易本息  借款人未还利息
					BigDecimal borTraIntUnreturnInterest = FormulaUtil.subtractDecimal(interest.getPayableInterest(),interest.getExtraInterest());
					totalBorUnreturnInterest = FormulaUtil.addDecimal(totalBorUnreturnInterest,borTraIntUnreturnInterest);*/
				}
				transactionInterests.addAll(interestsByPeriod);
			}
			//计算项目对应交易本息的未还本金和利息
			TransactionInterest unreturnPrincipalAndInterest = transactionInterestMapper.selectUnreturnPrincipalAndInterestByProjectId(overdueRepayLog.getProjectId());
			if(unreturnPrincipalAndInterest!=null){
				overdueRepayLog.setUnreturnPrincipal(unreturnPrincipalAndInterest.getPayablePrincipal());//逾期未还本金
				overdueRepayLog.setUnreturnInterest(unreturnPrincipalAndInterest.getPayableInterest());//逾期未还利息
			}
			overdueRepayLog.setOverdueDay(overdueDays);//逾期天数
			overdueRepayLog.setOverdueFine(totalLateFee);//滞纳金
			overdueRepayLog.setPayableAmount(FormulaUtil.addDecimal(overdueRepayLog.getOverdueInterest(),
					overdueRepayLog.getOverduePrincipal(), overdueRepayLog.getOverdueFine()));//应付金额
			map.put("overdueRepayLog", overdueRepayLog);
			map.put("transactionInterests", transactionInterests);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return map;
	}
	
	
	/**
	 * @desc 查询未还款的普通逾期结算记录
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author fuyili
	 * @time 2016年6月5日 上午11:28:17
	 *
	 */
	@Override
	public OverdueRepayLog getOverdueRepayByStatus(Long projectId) throws ManagerException {
		try {
			return overdueRepayLogMapper.getOverdueRepayByStatus(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * @desc 逾期结算记录下是否存在未归还的逾期记录
	 * @param overdueRepayId
	 * @return
	 * @throws ManagerException
	 * @author fuyili
	 * @time 2016年6月6日 上午11:55:09
	 *
	 */
	@Override
	public int getUnreturnCountByOverdueRepayId(Long overdueRepayId) throws ManagerException {
		try {
			return overdueRepayLogMapper.getUnreturnCountByOverdueRepayId(overdueRepayId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int queryOverdueCountByBorrowerId(Long memberId) {
		return overdueRepayLogMapper.queryOverdueCountByBorrowerId(memberId);
	}

	@Override
	public BigDecimal queryOverdueAmountByBorrowerId(Long memberId) {
		BigDecimal bigDecimal=new BigDecimal(0);
		Map map= overdueRepayLogMapper.queryOverdueAmountByBorrowerId(memberId);
		if (map!=null && !map.isEmpty()){
			for (Object value : map.values()){
				if (value!=null){
					bigDecimal=bigDecimal.add(new BigDecimal(value.toString()));
				}
			}
		}
		return bigDecimal;
	}
}
