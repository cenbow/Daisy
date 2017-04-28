package com.yourong.core.fin.manager.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.yourong.common.constant.Constant;
import com.yourong.common.enums.MessageEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.mail.SendMailService;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sms.SmsMobileSend;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.dao.PayPrincipalInterestMapper;
import com.yourong.core.fin.manager.PayPrincipalInterestManager;
import com.yourong.core.fin.manager.UnderwriteLogManager;
import com.yourong.core.fin.model.UnderwriteLog;
import com.yourong.core.fin.model.biz.PayPrincipalInterestBiz;
import com.yourong.core.fin.model.biz.PrincipalInterestForDirectMessageMember;
import com.yourong.core.ic.manager.DebtInterestManager;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.ic.model.Project;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.manager.ThirdCompanyManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.ThirdCompany;

@Component
public class PayPrincipalInterestManagerImpl implements
		PayPrincipalInterestManager {

	@Autowired
	private PayPrincipalInterestMapper payPrincipalInterestMapper;
	@Autowired
	private DebtManager debtManager;
	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private MemberManager memberManager;
	@Autowired
	private DebtInterestManager debtInterestManager;
	@Autowired
	private TransactionInterestManager transactionInterestManager;
	@Autowired
	private SendMailService sendMailService;
	@Autowired
	private SysDictManager sysDictManager;

	@Autowired
	private SmsMobileSend smsMobileSend;

	@Autowired
	private UnderwriteLogManager underwriteLogManager;
	@Autowired
	private ThirdCompanyManager thirdCompanyManager;
	@Override
	public Page<PayPrincipalInterestBiz> findByPage(
			Page<PayPrincipalInterestBiz> pageRequest, Map<String, Object> map)
			throws ManagerException {
		map.put("startRow", pageRequest.getiDisplayStart());
		map.put("pageSize", pageRequest.getiDisplayLength());

		List<PayPrincipalInterestBiz> interestBizs = payPrincipalInterestMapper
				.selectForPagin(map);
		int totalCount = payPrincipalInterestMapper
				.selectForPaginTotalCount(map);
		pageRequest.setiTotalDisplayRecords(totalCount);
		pageRequest.setiTotalRecords(totalCount);
		for (PayPrincipalInterestBiz payBiz : interestBizs) {
			findPayPrincipalAndInterestByProject(payBiz);
		}
		pageRequest.setData(interestBizs);
		return pageRequest;
	}


	/**
	 * 根据项目获取托管还本付息详情
	 */
	@Override
	public void findPayPrincipalAndInterestByProject(PayPrincipalInterestBiz interestBiz) throws ManagerException {
		Long projectId = interestBiz.getProjectId();
		if (projectId != null) {
			Debt debt = debtManager.selectByPrimaryKey(interestBiz.getDebtId());
			if (debt != null && debt.getLenderId() != null) {
				// 用户信息
				Member lMember = memberManager.selectByPrimaryKey(debt
						.getLenderId());
				interestBiz.setLenderMember(lMember);
			}
			if (debt != null && debt.getBorrowerId() != null) {
				// 用户信息
				Member bMember = memberManager.selectByPrimaryKey(debt
						.getBorrowerId());
				interestBiz.setBorrowerMember(bMember);
			}
			// 总期数
			Map<String, Object> map = Maps.newHashMap();
			map.put("projectId", projectId);
			int totalPeriods = debtInterestManager.findPeriodsByProjectId(map);
			interestBiz.setTotalPeriods(totalPeriods);
			// 当前期数
			map.put("endDate", interestBiz.getEndDate());
			int paidPeriods = debtInterestManager.findPeriodsByProjectId(map);
			interestBiz.setCurrentPeriods(paidPeriods);
			// 距离到期日
			Date nowDate = DateUtils.getCurrentDate();
			Date endDate = DateUtils.addHour(interestBiz.getEndDate(), Constant.PAY_PRINCIPAL_INTEREST_TIME);
			if (DateUtils.daysOfTwo(nowDate, endDate) > 0) {
				interestBiz
						.setExpireDays(DateUtils.daysOfTwo(DateUtils.getCurrentDate(), interestBiz.getEndDate()) + 1);
			} else if (DateUtils.getTimeIntervalHours(nowDate, endDate) > 0) {
				interestBiz.setExpireHours(DateUtils.getTimeIntervalHours(nowDate, endDate) + 1);
			}
			// 1·状态为本息全部支付，状态为“本期已付”
			// 2.status==2在交易本息表中代表部分付款，目前为脏数据，出现设置状态为空
			if (interestBiz.getStatus().intValue() == 2) {
				interestBiz.setStatus(null);
			}
			if (interestBiz.getStatus() != null) {
				if (StatusEnum.TRANSACTION_INTEREST_ALL_PAYED.getStatus() == interestBiz.getStatus().intValue()
						|| StatusEnum.TRANSACTION_INTEREST_NOT_PAY.getStatus() == interestBiz.getStatus().intValue()) {
					interestBiz.setStatus(StatusEnum.FIN_PAY_PRINCIPAL_INTEREST_CURRENT_PAID.getStatus());
				}
				// 状态为付款中和已付款的为待付款
				if (StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus() == interestBiz.getStatus().intValue()
						|| StatusEnum.TRANSACTION_INTEREST_PAYING.getStatus() == interestBiz.getStatus().intValue()) {
					interestBiz.setStatus(StatusEnum.FIN_PAY_PRINCIPAL_INTEREST_WAIT_PAY.getStatus());
				}
			}
			
			// 查询垫付记录表状态
			UnderwriteLog underwriteLog=underwriteLogManager.getUnderwriteLogByInterestId(interestBiz.getInterestId());
			if(underwriteLog!=null){
				ThirdCompany thirdCompany=thirdCompanyManager.getCompanyByMemberId(underwriteLog.getUnderwriteMemberId());
				if(thirdCompany!=null){
					interestBiz.setThirdPayName(thirdCompany.getCompanyName());
				}
			}
		}
	}

	/**
	 * 还款本息数据统计根据还款状态
	 */
	@Override
	public PayPrincipalInterestBiz findTotalPrincipalAndInterestByStatus(
			Map<String, Object> map) throws ManagerException {
		try {
			return payPrincipalInterestMapper
					.findTotalPrincipalAndInterestByStatus(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 距离到期日3天的债权项目 发送邮件
	 */
	@Override
	public Integer sendMailThree2EndDateProject() throws ManagerException {
		try {
			Map<String, Object> map = Maps.newHashMap();
			map.put("n2endDate", 3);
			List<PayPrincipalInterestBiz> projects = payPrincipalInterestMapper
					.findN2EndDateProject(map);
			// 发送邮件通知
			int size = projects.size();
			if (size > 0) {
				for (PayPrincipalInterestBiz payBiz : projects) {
					findPayPrincipalAndInterestByProject(payBiz);
				}
				String subject = "托管还本付息邮件通知";
				String vmFile = "payPrincipalInterestMail";
				Map<String, Object> emailContentMap = Maps.newHashMap();
				emailContentMap.put("payPrincipalInterests", projects);
				// 需要发送邮件的地址
				List<SysDict> emails = sysDictManager
						.findByGroupName("fin_pay_email");// 还本付息邮件地址
				for (SysDict sysDict : emails) {
					if (sysDict.getKey() != null) {
						sendMailService.sendMailNowByTencent(sysDict.getKey(),
								subject, vmFile, emailContentMap);
					}

				}
			}
			return size;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	/**
	 * 距离到期日3天的直投项目 发送邮件
	 */
	@Override
	public Integer sendMailThree2EndDateDirectProject() throws ManagerException {
		try {
			Map<String, Object> map = Maps.newHashMap();
			map.put("n2endDate", 3);
			List<PayPrincipalInterestBiz> projects = payPrincipalInterestMapper
					.findN2EndDateMailDirectProject(map);
			// 发送邮件通知
			int size = projects.size();
			if (size > 0) {
				for (PayPrincipalInterestBiz payBiz : projects) {
					findPayPrincipalAndInterestByDirectProject(payBiz);
				}
				String subject = "托管还本付息邮件通知";
				String vmFile = "payPrincipalInterestDirectMail";
				Map<String, Object> emailContentMap = Maps.newHashMap();
				emailContentMap.put("payPrincipalInterests", projects);
				// 需要发送邮件的地址
				List<SysDict> emails = sysDictManager
						.findByGroupName("fin_pay_email");// 还本付息邮件地址
				for (SysDict sysDict : emails) {
					if (sysDict.getKey() != null) {
						sendMailService.sendMailNowByTencent(sysDict.getKey(),
								subject, vmFile, emailContentMap);
					}

				}
			}
			return size;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	
	

	/**
	 * 距离到期日1天的项目 发送短信
	 */
	@Override
	public Integer sendSmsOne2EndDateProject() throws ManagerException {
		try {
			Map<String, Object> map = Maps.newHashMap();
			map.put("n2endDate", 1);
			List<PayPrincipalInterestBiz> projects = payPrincipalInterestMapper
					.findN2EndDateProjectForMsg(map);
			int size = projects.size();
			if (size > 0) {
				String url = PropertiesUtil.getProperties("smsurl.marketing");
				List<SysDict> dictMobiles = sysDictManager
						.findByGroupName("fin_pay_mobile");// 还本付息邮件地址
				BigDecimal totalPayablePrincipal = BigDecimal.ZERO;// 本金总计
				BigDecimal totalPayableInterest = BigDecimal.ZERO;// 利息总计
				for (PayPrincipalInterestBiz payBiz : projects) {
					//短信部分无效代码注释
					//findPayPrincipalAndInterestByProject(payBiz);
					totalPayablePrincipal = totalPayablePrincipal.add(payBiz
							.getPayablePrincipal());
					totalPayableInterest = totalPayableInterest.add(payBiz
							.getPayableInterest());
				}
				Object[] content = new Object[4];
				content[0] = projects.get(0).getEndDateStr();
				content[1] = size;
				content[2] = totalPayablePrincipal;
				content[3] = totalPayableInterest;
				String sms = "明天（%s）需要还本付息的项目有%s个，总共需支付本金￥%s，支付利息￥%s，详情请登录后台网站,回复TD退订";
				String smsContent = String.format(sms.toString(), content);
				int mobileLen = dictMobiles.size();
				long[] mobiles = new long[mobileLen];
				for (int i = 0; i < mobileLen; i++) {
					mobiles[i] = Long.valueOf(dictMobiles.get(i).getKey());
				}
				smsMobileSend.bachSendSms(url, mobiles, smsContent);
			}
			return projects.size();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	/**
	 * 距离到期日1天的项目 发送短信
	 */
	@Override
	public Integer sendSmsOne2EndDateDirectProject() throws ManagerException {
		try {
			Map<String, Object> map = Maps.newHashMap();
			map.put("n2endDate", 1);
			List<PrincipalInterestForDirectMessageMember> PrincipalInterestForDirectMessageMembers = payPrincipalInterestMapper
					.findN2EndDateDirectProject(map);
			for(PrincipalInterestForDirectMessageMember priIntForMessMember:PrincipalInterestForDirectMessageMembers ){
				MessageClient.sendMsgForCommon(priIntForMessMember.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.P2P_PRINCIPAL_INTEREST_MEMBER.getCode(), 
						DateUtils.getStrFromDate(DateUtils.addDaysByDate(DateUtils.getCurrentDate(),1),DateUtils.DATE_FMT_4),priIntForMessMember.getProjectNum().toString(),
						priIntForMessMember.getPrincipal().toString(),priIntForMessMember.getInterest().toString());
			}
			return PrincipalInterestForDirectMessageMembers.size();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	/**
	 * 距离到期日3天的项目 发送短信
	 */
	@Override
	public Integer sendSmsThree2EndDateDirectProject() throws ManagerException {
		try {
			Map<String, Object> map = Maps.newHashMap();
			map.put("n2endDate", 3);
			List<PrincipalInterestForDirectMessageMember> PrincipalInterestForDirectMessageMembers = payPrincipalInterestMapper
					.findN2EndDateDirectProject(map);
			for(PrincipalInterestForDirectMessageMember priIntForMessMember:PrincipalInterestForDirectMessageMembers ){
				MessageClient.sendMsgForCommon(priIntForMessMember.getMemberId(), Constant.MSG_NOTIFY_TYPE_SYSTEM, MessageEnum.P2P_PRINCIPAL_INTEREST_MEMBER.getCode(), 
						DateUtils.getStrFromDate(DateUtils.addDaysByDate(DateUtils.getCurrentDate(),3),DateUtils.DATE_FMT_4),priIntForMessMember.getProjectNum().toString(),
						priIntForMessMember.getPrincipal().toString(),priIntForMessMember.getInterest().toString());
			}
			return PrincipalInterestForDirectMessageMembers.size();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 根据项目获取托管还本付息详情
	 */
	@Override
	public void findPayPrincipalAndInterestByDirectProject(
			PayPrincipalInterestBiz interestBiz) throws ManagerException {
		Long projectId = interestBiz.getProjectId();
		if (projectId != null) {
			Project project = projectManager.selectByPrimaryKey(interestBiz.getProjectId());
			if (project != null && project.getBorrowerId() != null) {
				// 用户信息
				Member bMember = memberManager.selectByPrimaryKey(project.getBorrowerId() );
				interestBiz.setBorrowerMember(bMember);
			}
			
			// 总期数
			Map<String, Object> map = Maps.newHashMap();
			map.put("projectId", projectId);
			int totalPeriods = debtInterestManager.findPeriodsByProjectId(map);
			interestBiz.setTotalPeriods(totalPeriods);
			// 当前期数
			map.put("endDate", interestBiz.getEndDate());
			int paidPeriods = debtInterestManager.findPeriodsByProjectId(map);
			interestBiz.setCurrentPeriods(paidPeriods);
			// 距离到期日
			Date nowDate = DateUtils.getCurrentDate();
			Date endDate = DateUtils.addHour(interestBiz.getEndDate(),
					Constant.PAY_PRINCIPAL_INTEREST_TIME);
			if (DateUtils.daysOfTwo(nowDate, endDate) > 0) {
				interestBiz
						.setExpireDays(DateUtils.daysOfTwo(
								DateUtils.getCurrentDate(),
								interestBiz.getEndDate()) + 1);
			} else if (DateUtils.getTimeIntervalHours(nowDate, endDate) > 0) {
				interestBiz.setExpireHours(DateUtils.getTimeIntervalHours(
						nowDate, endDate) + 1);
			}
			// 1·状态为本息全部支付，状态为“本期已付”
			// 2.status==2在交易本息表中代表部分付款，目前为脏数据，出现设置状态为空
			if (interestBiz.getStatus().intValue() == 2) {
				interestBiz.setStatus(null);
			}
			if (interestBiz.getStatus() != null) {
				if (StatusEnum.TRANSACTION_INTEREST_ALL_PAYED.getStatus() == interestBiz
						.getStatus().intValue()
						|| StatusEnum.TRANSACTION_INTEREST_NOT_PAY.getStatus() == interestBiz
								.getStatus().intValue()) {
					interestBiz
							.setStatus(StatusEnum.FIN_PAY_PRINCIPAL_INTEREST_CURRENT_PAID
									.getStatus());
				}
				// 状态为付款中和已付款的为待付款
				if (StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus() == interestBiz
						.getStatus().intValue()
						|| StatusEnum.TRANSACTION_INTEREST_PAYING.getStatus() == interestBiz
								.getStatus().intValue()) {
					interestBiz
							.setStatus(StatusEnum.FIN_PAY_PRINCIPAL_INTEREST_WAIT_PAY
									.getStatus());
				}
			}
		}
	}
	
}
