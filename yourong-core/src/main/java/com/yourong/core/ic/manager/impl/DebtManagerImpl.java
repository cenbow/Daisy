package com.yourong.core.ic.manager.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.yourong.common.constant.Constant;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.core.bsc.dao.BscAttachmentMapper;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.ic.dao.DebtCollateralMapper;
import com.yourong.core.ic.dao.DebtInterestMapper;
import com.yourong.core.ic.dao.DebtMapper;
import com.yourong.core.ic.dao.DebtPledgeMapper;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.ic.model.DebtBiz;
import com.yourong.core.ic.model.DebtCollateral;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.DebtPledge;
import com.yourong.core.sys.manager.SysUserManager;
import com.yourong.core.uc.dao.MemberMapper;
import com.yourong.core.uc.manager.EnterpriseManager;
import com.yourong.core.uc.manager.MemberInfoManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Enterprise;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberBaseBiz;
import com.yourong.core.uc.model.MemberInfo;

@Component
public class DebtManagerImpl implements DebtManager {
	@Autowired
	private DebtMapper debtMapper;
	@Autowired
	private DebtCollateralMapper debtCollateralMapper;
	@Autowired
	private DebtPledgeMapper debtPledgeMapper;
	@Autowired
	private DebtInterestMapper debtInterestMapper;
	@Autowired
	private BscAttachmentMapper bscAttachmentMapper;
	@Autowired
	private MemberMapper memberMapper;
	@Autowired
	private MemberInfoManager memberInfoManager;
	@Autowired
	private SysUserManager sysUserManager;
	
	@Autowired
	private MemberManager memberManager;
	@Autowired
	private EnterpriseManager enterpriseManager;

	private static final String DEBT_TYPE_COL = "collateral";
	private static final String DEBT_TYPE_PLED = "pledge";
	private static final String DEBT_TYPE_CREDIT = "credit";

	@Override
	public int deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			return debtMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insert(Debt record) throws ManagerException {
		try {
			return debtMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insertSelective(Debt record) throws ManagerException {
		try {
			return debtMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Debt selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return debtMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKeySelective(Debt record) throws ManagerException {
		try {
			return debtMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKey(Debt record) throws ManagerException {
		try {
			record.setDelFlag(1);// 更新时
			return debtMapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<Debt> findByPage(Page<Debt> pageRequest, Map<String, Object> map)
			throws ManagerException {
		try {
			map.put(Constant.STARTROW, pageRequest.getiDisplayStart());
			map.put(Constant.PAGESIZE, pageRequest.getiDisplayLength());
			
			//针对前台传入时间，添加时分秒字段
			if(map.containsKey("createdStartTime")){
				map.put("createdStartTime", (map.get("createdStartTime")+" 00:00:00"));
			}
			if(map.containsKey("createdEndTime")){
				map.put("createdEndTime", (map.get("createdEndTime")+" 23:59:59"));
			}
			
			int totalCount = debtMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<Debt> selectForPagin = debtMapper.selectForPagin(map);
			for (Debt debt : selectForPagin) {
				/*if(debt.getPublishId()!=null){
					SysUser user = sysUserManager.selectByPrimaryKey(debt.getPublishId());
					if(user!=null){
						debt.setPublishName(user.getName());
					}
				}*/
				if(debt.getInterestFrom()>0){
					debt.setStartDate(DateUtils.addDate(debt.getStartDate() , debt.getInterestFrom()));
				}
				if(debt.getGuarantyType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_RUNCOMPANY.getCode()) && debt.getEnterpriseId()!=null){
					Enterprise enterprise = enterpriseManager.selectByKey(debt.getEnterpriseId());
					if(enterprise!=null){
						debt.setEnterpriseName(enterprise.getName());
					}
				}
			}
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int batchDelete(long[] ids) throws ManagerException {
		try {
			return debtMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Debt findDebtBySerialNumber(String serialNumber)
			throws ManagerException {
		try {
			return debtMapper.findDebtBySerialNumber(serialNumber);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 获取所有债权信息根据公司债权编号
	 */
	@Override
	public DebtBiz getFullDebtInfoBySerialNumber(String serialNumber)
			throws ManagerException {
		try {
			Debt debt = debtMapper.findDebtBySerialNumber(serialNumber);
			if (debt != null) {
				// 债权基本信息
				DebtBiz debtBiz = BeanCopyUtil.map(debt, DebtBiz.class);
				// 抵押物信息
				if (DebtEnum.DEBT_TYPE_COLLATERAL.getCode().equals(debtBiz.getDebtType())||DebtEnum.DEBT_TYPE_CREDIT.getCode().equals(debtBiz.getDebtType())) {
					DebtCollateral debtCollateral = debtCollateralMapper
							.findCollateralByDebtId(debt.getId());
					debtBiz.setDebtCollateral(debtCollateral);
				}
				// 质押物信息
				if (DebtEnum.DEBT_TYPE_PLEDGE.getCode().equals(debtBiz.getDebtType())) {
					DebtPledge debtPledge = debtPledgeMapper
							.findPledgeByDebtId(debt.getId());
					debtBiz.setDebtPledge(debtPledge);
				}
				// 借款人信息
				if (debtBiz.getBorrowerId() != null) {
					MemberBaseBiz borrowMember = memberManager
							.selectMemberBaseBiz(debtBiz.getBorrowerId());
					debtBiz.setBorrowMemberBaseBiz(borrowMember);
				}
				// 出借人信息
				if (debtBiz.getLenderId() != null) {
					Member lenderMember = memberMapper
							.selectByPrimaryKey(debtBiz.getLenderId());
					debtBiz.setLenderMember(lenderMember);
				}
				// 债权本息信息
				List<DebtInterest> debtInterests = debtInterestMapper
						.findInterestsByDebtId(debt.getId());
				if (!Collections3.isEmpty(debtInterests)) {
					debtBiz.setDebtInterests(debtInterests);
				}
				// 债权附件信息
				List<BscAttachment> bscAttachments = bscAttachmentMapper
						.findAttachmentsByKeyId(String.valueOf(debt.getId()));
				if (!Collections3.isEmpty(bscAttachments)) {
					debtBiz.setBscAttachments(bscAttachments);
				}
				return debtBiz;
			}
			return null;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Map<String, String>> findSerialNumberAndMemberName(
			String serialNumber) throws ManagerException {
		try {
			return debtMapper.findSerialNumberAndMemberName(serialNumber);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateDebtStatusById(Integer status, Long id)
			throws ManagerException {
		try {
			return debtMapper.updateDebtStatusById(status, id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 根据主键删除（逻辑）
	 */
	@Override
	public int deleteByDebtId(Long id) throws ManagerException {
		try {
			return debtMapper.deleteByDebtId(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 根据原始债权编号查询数据
	 */
	@Override
	public int findDebtByOriginalDebtNumber(String originalDebtNumber)
			throws ManagerException {
		try {
			return debtMapper.findDebtByOriginalDebtNumber(originalDebtNumber);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public DebtBiz getFullDebtInfoById(Long id) throws ManagerException{
		DebtBiz debtBiz = null;
		try {
			Debt debt = debtMapper.selectByPrimaryKey(id);
			if (debt != null) {
				// 债权基本信息
				debtBiz = BeanCopyUtil.map(debt, DebtBiz.class);
				
				// 抵押物信息
				if (DebtEnum.DEBT_TYPE_COLLATERAL.getCode().equals(debtBiz.getDebtType())||DebtEnum.DEBT_TYPE_CREDIT.getCode().equals(debtBiz.getDebtType())) {
					DebtCollateral debtCollateral = debtCollateralMapper.findCollateralByDebtId(debt.getId());
					debtBiz.setDebtCollateral(debtCollateral);
				}
				// 质押物信息
				if (DebtEnum.DEBT_TYPE_PLEDGE.getCode().equals(debtBiz.getDebtType())) {
					DebtPledge debtPledge = debtPledgeMapper.findPledgeByDebtId(debt.getId());
					debtBiz.setDebtPledge(debtPledge);
				}
				// 借款人信息
				if (debtBiz.getBorrowerId() != null) {
					//MemberBaseBiz borrowMember = memberManager.selectMemberBaseBiz(debtBiz.getBorrowerId());
					MemberBaseBiz borrowMember = memberManager.selectMemberBaseBizByBorrowerType(debtBiz.getBorrowerId(),debtBiz.getBorrowerType(),debtBiz.getEnterpriseId());
					debtBiz.setBorrowMemberBaseBiz(borrowMember);
					if(borrowMember != null){
						MemberInfo memberInfo = memberInfoManager.getMemberInfoByMemberId(borrowMember.getId());
						borrowMember.setMemberInfo(memberInfo);
					}
				}
				// 出借人信息
				if (debtBiz.getLenderId() != null) {
					Member lenderMember = memberMapper.selectByPrimaryKey(debtBiz.getLenderId());
					debtBiz.setLenderMember(lenderMember);
					if(lenderMember != null){
						MemberInfo memberInfo = memberInfoManager.getMemberInfoByMemberId(lenderMember.getId());
						lenderMember.setMemberInfo(memberInfo);
					}
				}
				
				// 出借人信息 包含企业信息  
				if (debtBiz.getLenderId() != null) {
					//MemberBaseBiz lenderMemberBaseBiz = memberManager.selectMemberBaseBiz(debtBiz.getLenderId());
					MemberBaseBiz lenderMemberBaseBiz = memberManager.selectMemberBaseBizByBorrowerType(debtBiz.getLenderId(),debtBiz.getLenderType(),debtBiz.getLenderEnterpriseId());
					debtBiz.setLenderMemberBaseBiz(lenderMemberBaseBiz);
					if(lenderMemberBaseBiz != null){
						MemberInfo memberInfo = memberInfoManager.getMemberInfoByMemberId(lenderMemberBaseBiz.getId());
						lenderMemberBaseBiz.setMemberInfo(memberInfo);
					}
				}
				
				// 债权本息信息
				List<DebtInterest> debtInterests = debtInterestMapper.findInterestsByDebtId(debt.getId());
				if (!Collections3.isEmpty(debtInterests)) {
					debtBiz.setDebtInterests(debtInterests);
				}
				
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return debtBiz;
	}

	@Override
	public List<Long> findDebtIdByMemberInfo(Map<String, Object> map)
			throws ManagerException {
		try {
			return debtMapper.findDebtIdByMemberInfo(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 紧急修改
	 */
	@Override
	public int emergencyUpdateByPrimaryKeySelective(Debt record) throws ManagerException {
		try {
			return debtMapper.emergencyUpdateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public DebtBiz getDebtInfoToZhongNiu(Long debtId) throws ManagerException {
		DebtBiz debtBiz = null;
		try {
			Debt debt = debtMapper.selectByPrimaryKey(debtId);
			if (debt != null) {
				// 债权基本信息
				debtBiz = BeanCopyUtil.map(debt, DebtBiz.class);
				// 抵押物信息
				if (DebtEnum.DEBT_TYPE_COLLATERAL.getCode().equals(debtBiz.getDebtType())||DebtEnum.DEBT_TYPE_CREDIT.getCode().equals(debtBiz.getDebtType())) {
					DebtCollateral debtCollateral = debtCollateralMapper.findCollateralByDebtId(debt.getId());
					debtBiz.setDebtCollateral(debtCollateral);
				}
				// 质押物信息
				if (DebtEnum.DEBT_TYPE_PLEDGE.getCode().equals(debtBiz.getDebtType())) {
					DebtPledge debtPledge = debtPledgeMapper.findPledgeByDebtId(debt.getId());
					debtBiz.setDebtPledge(debtPledge);
				}
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return debtBiz;
	}

	@Override
	public Page<Debt> findDebtInfoByPage(Page<Debt> pageRequest,
			Map<String, Object> map) throws ManagerException {
			Page<Debt> page = new Page<Debt>();
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			List<Debt> debtList=  debtMapper.findDebtInfoByPage(map);
			int totalCount = debtMapper.findDebtInfoByPageCount(map);
			page.setData(debtList);
			page.setiTotalDisplayRecords(totalCount);
			page.setiTotalRecords(totalCount);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	@Override
	public List<Long> findDebtIdByMemberName(Map<String, Object> map)
			throws ManagerException {
		try {
			return debtMapper.findDebtIdByMemberName(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateControlRemarksById(Debt record) throws ManagerException {
		try {
			return debtMapper.updateControlRemarksById(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Debt findDebtAndProjectStatusByDebtId(Long id) throws ManagerException {
		try {
			return debtMapper.findDebtAndProjectStatusByDebtId(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Debt selectEnterpriseIdByProjectId(Long projectId) throws ManagerException {
		try {
			return debtMapper.selectEnterpriseIdByProjectId(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public boolean countDebtByLenderId(Long memberId)throws ManagerException{
		try {
			boolean flag = true;
			Integer num = debtMapper.countDebtByLenderId(memberId);
			if (num == null) {
				flag = false;
			} else {
				flag = true;
			}
			return flag;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<DebtInterest> autoInterest(String returnType, int interestFrom, String sDate, String eDate) {
		List<DebtInterest> list= Lists.newArrayList();
		//按日计息
		if(returnType.equals(DebtEnum.RETURN_TYPE_DAY.getCode())){
			list=getInterests(interestFrom,sDate,eDate);
		}
		//一次性还本付息
		if(returnType.equals(DebtEnum.RETURN_TYPE_ONCE.getCode())){
			list=getOneTimeInterests(interestFrom,sDate,eDate);
		}
		//等本等息
		if(returnType.equals(DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode()) ){
			list=getInterests(interestFrom,sDate,eDate);
		}
		return list;	
	} 
	public static List<DebtInterest> getInterests(int interestFrom, String sDate, String eDate){
		List<DebtInterest> list= Lists.newArrayList();
		Date beginDate=DateUtils.getDateFromString(sDate);
		Date endDate = DateUtils.getDateFromString(eDate);
		int borrowPeriod=DateUtils.daysBetween(beginDate, endDate);
		Date startDate = DateUtils.addDate(beginDate , interestFrom);
		Date addDate = DateUtils.addDate(startDate, borrowPeriod);
		Calendar  calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		Calendar acalendar = Calendar.getInstance();
		acalendar.setTime(addDate);
		int monthsOfAge = DateUtils.getMonthsBetweenDates(startDate, endDate);
		//System.out.println("借款天数:"+borrowPeriod);
		Date temp = startDate;
		//System.out.println("借款开始时间"+DateUtils.getTimeFrom(startDate));
		int totalDay = 0;
		for (int i =1;i <= monthsOfAge;i++) {
			DebtInterest interest=new DebtInterest();
			//System.out.println("第"+i+"个周期开始时间"+DateUtils.getTimeFrom(temp));
			interest.setStartDate(temp);
			Date endDateAddMonth = null;
			int intervalDays = 0;
			//最后一个周期 =
			if (i == monthsOfAge) {
				int i1 = borrowPeriod - totalDay ;
				//endDateAddMonth = DateUtils.addDate(temp, i1 );
				endDateAddMonth=endDate;
				//System.out.println("第"+i+"个周期结束时间"+DateUtils.getTimeFrom(endDateAddMonth));
				intervalDays = DateUtils.getIntervalDays(temp, endDateAddMonth) ;
				totalDay  = intervalDays+totalDay;
			}else {
				endDateAddMonth =  DateUtils.addMonth(temp,1);
				endDateAddMonth = DateUtils.addDate(endDateAddMonth, -1 );
				//System.out.println("第"+i+"个周期结束时间"+DateUtils.getTimeFrom(endDateAddMonth));
				intervalDays = DateUtils.getIntervalDays(temp, endDateAddMonth) +1;
				totalDay  = intervalDays+totalDay;
				temp = DateUtils.addDate(endDateAddMonth,1);

			}
			interest.setEndDate(endDateAddMonth);
			interest.setStatus(StatusEnum.DEBT_INTEREST_WAIT_PAY.getStatus());
			//System.out.println("第"+i+"个周期天数"+intervalDays);
			list.add(interest);
		}
		return list;
		
	}
	public List<DebtInterest> getOneTimeInterests(int interestFrom, String sDate, String eDate){
		List<DebtInterest> list= Lists.newArrayList();
		DebtInterest interest=new DebtInterest();
		Date beginDate=DateUtils.getDateFromString(sDate);
		Date endDate = DateUtils.getDateFromString(eDate);
		Date startDate = DateUtils.addDate(beginDate,interestFrom);
		interest.setStartDate(startDate);
		interest.setEndDate(endDate);
		list.add(interest);
		return list;
		
	}
	public static void main(String[] args) {
		Date beginDate =DateUtils.getDateFromString("2016-05-3");
		Date endDate =DateUtils.getDateFromString("2016-12-22");
		int borrowPeriod=DateUtils.daysBetween(beginDate, endDate);
		int interestFrom=0;
		getInterests(interestFrom,"2016-05-3","2016-12-22");
		
		Date startDate = DateUtils.addDate(beginDate , interestFrom);
		
		Date addDate = DateUtils.addDate(startDate, borrowPeriod);
		System.out.println(DateUtils.getTimeFrom(startDate));
		System.out.println(DateUtils.getTimeFrom(addDate));
		Calendar  calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		Calendar acalendar = Calendar.getInstance();
		acalendar.setTime(addDate);
		int monthsOfAge = DateUtils.getMonthsBetweenDates(startDate, endDate);
		System.out.println("借款天数:"+borrowPeriod);
		Date temp = startDate;
		System.out.println("借款开始时间"+DateUtils.getTimeFrom(startDate));
		int totalDay = 0;
		for (int i =1;i <= monthsOfAge;i++) {
			System.out.println("第"+i+"个周期开始时间"+DateUtils.getTimeFrom(temp));
			Date endDateAddMonth = null;
			int intervalDays = 0;
			//最后一个周期 =
			if (i == monthsOfAge) {
				int i1 = borrowPeriod - totalDay ;
				//endDateAddMonth = DateUtils.addDate(temp, i1 );
				endDateAddMonth=endDate;
				System.out.println("第"+i+"个周期结束时间"+DateUtils.getTimeFrom(endDateAddMonth));
				intervalDays = DateUtils.getIntervalDays(temp, endDateAddMonth) ;
				totalDay  = intervalDays+totalDay;
			}else {
				endDateAddMonth =  DateUtils.addMonth(temp,1);
				endDateAddMonth = DateUtils.addDate(endDateAddMonth, -1 );
				System.out.println("第"+i+"个周期结束时间"+DateUtils.getTimeFrom(endDateAddMonth));
				intervalDays = DateUtils.getIntervalDays(temp, endDateAddMonth) +1;
				totalDay  = intervalDays+totalDay;
				temp = DateUtils.addDate(endDateAddMonth,1);

			}

			System.out.println("第"+i+"个周期天数"+intervalDays);
		}
		System.out.println("第周期天数"+totalDay);

	}
	
}
