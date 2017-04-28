package com.yourong.api.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yourong.api.dto.CalendarDto;
import com.yourong.api.dto.CalendarTransactionInterestDetailAPPDto;
import com.yourong.api.dto.RecommendProjectDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.CalendarService;
import com.yourong.api.service.ProjectService;
import com.yourong.api.service.TransactionInterestService;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.model.biz.TransactionInterestDetail;
import com.yourong.core.tc.model.biz.TransactionInterestForDate;
import com.yourong.core.tc.model.biz.TransactionInterestForMonth;

@Service
public class CalendarServiceImpl implements CalendarService {

	private Logger logger = LoggerFactory.getLogger(CalendarServiceImpl.class);

	@Autowired
	private TransactionInterestManager transactionInterestManager;

	@Autowired
	private TransactionInterestService transactionInterestService;
	
	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private ProjectService projectService;

	@Override
	public ResultDTO<CalendarDto> initCalendar(Long memberId) {
		ResultDTO<CalendarDto> rDO = new ResultDTO<CalendarDto>();
		CalendarDto model = new CalendarDto();
		try {
			// 统计月收金额
			Date now = DateUtils.getCurrentDate();
			String startYM = DateUtils.getStrFromDate(now, DateUtils.DATE_FMT_6);
			Date endDate = DateUtils.addMonth(now, Constant.CALENDAR_MONTHS_NUM);
			String endYM = DateUtils.getStrFromDate(endDate, DateUtils.DATE_FMT_6);
			List<TransactionInterestForMonth> transactionInterestByMonth = transactionInterestManager.selectTransactionInterestsByMonth(
					memberId, startYM, endYM);
			if (Collections3.isNotEmpty(transactionInterestByMonth)) {
				model.setTransactionInterestByMonth(transactionInterestByMonth);
			}
			// 还款日历
			List<TransactionInterestForDate> transactionInterestByCalendar = transactionInterestManager.selectTransactionInterestsByEndDate(
					memberId, startYM, endYM);
			if (Collections3.isNotEmpty(transactionInterestByCalendar)) {
				model.setTransactionInterestByCalendar(transactionInterestByCalendar);
			}
			// 当天还款列表
			String queryDate = DateUtils.getDateStrFromDate(now);
			ResultDTO<CalendarDto> transactionInterestDO = getCalendarByDate(memberId, queryDate);
			if (!transactionInterestDO.isSuccess()) {
				logger.error("初始化我的日历中查询当日还款列表失败  memberId={}, queryDate={}", memberId, queryDate);
			}
			model.setTransactionInterestByDate(transactionInterestDO.getResult().getTransactionInterestByDate());
			rDO.setResult(model);
			rDO.setIsSuccess();
		} catch (Exception e) {
			logger.error("初始化我的日历失败 memberId={}", memberId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}
	
	@Override
	public ResultDTO<CalendarDto> initCalendarNew(Long memberId) {
		ResultDTO<CalendarDto> rDO = new ResultDTO<CalendarDto>();
		CalendarDto model = new CalendarDto();
		try {
			Integer flag =0;
			// 统计月收金额
			Date now = DateUtils.getCurrentDate();
			String startYM = DateUtils.getStrFromDate(now, DateUtils.DATE_FMT_6);
			Date endDate = DateUtils.addMonth(now, Constant.CALENDAR_MONTHS_NUM);
			String endYM = DateUtils.getStrFromDate(endDate, DateUtils.DATE_FMT_6);
			String queryDate = DateUtils.getDateStrFromDate(now);
			List<TransactionInterestForMonth> transactionInterestByMonth = transactionInterestManager.selectTransactionInterestsByMonth(
					memberId, startYM, endYM);
			if (Collections3.isNotEmpty(transactionInterestByMonth)) {
				model.setTransactionInterestByMonth(transactionInterestByMonth);
			}
			// 还款日历
			List<TransactionInterestForDate> transactionInterestByCalendar = transactionInterestManager.selectTransactionInterestsByEndDate(
					memberId, startYM, endYM);
			//投资，当日可还款
			Set<String> dates = this.findInvestingDebtProject();
			for(TransactionInterestForDate transactionInterestForDate:transactionInterestByCalendar){
				if(dates.contains(transactionInterestForDate.getDateStr())){
					//当日有回款，不显示投资可回款标记
					//transactionInterestForDate.setInvestRepaymentStatus(1);
					dates.remove(transactionInterestForDate.getDateStr());
				}
				if(queryDate.equals(transactionInterestForDate.getDateStr())){
					flag=1;	//今日有回款，推荐精选项目：1 ；今日无回款，有今日投资，当日可回款项目：2
				}
			}
			for (String date :dates){
				TransactionInterestForDate addDate = new TransactionInterestForDate();
				addDate.setDateStr(date);
				addDate.setInvestRepaymentStatus(1);
				transactionInterestByCalendar.add(addDate);
			}	
			if(dates.contains(queryDate)){
					flag=2;//今日有回款，推荐精选项目：1 ；今日无回款，有今日投资，当日可回款项目：2
			}
			if (Collections3.isNotEmpty(transactionInterestByCalendar)) {
				model.setTransactionInterestByCalendar(transactionInterestByCalendar);
			}
			// 当天还款列表
			if(flag==1){
				ResultDTO<CalendarDto> transactionInterestDO = getCalendarByDate(memberId, queryDate);
				if (!transactionInterestDO.isSuccess()) {
					logger.error("初始化我的日历中查询当日还款列表失败  memberId={}, queryDate={}", memberId, queryDate);
				}
				model.setTransactionInterestByDate(transactionInterestDO.getResult().getTransactionInterestByDate());
				model.setRecommendProjectList(transactionInterestDO.getResult().getRecommendProjectList());
			}
			if(flag==2){
				ResultDTO<CalendarDto> RecommendProjectDto = getProjectByEndData( queryDate);
				model.setRecommendProjectList(RecommendProjectDto.getResult().getRecommendProjectList());
			}
			model.setFlag(flag);
			rDO.setResult(model);
			rDO.setIsSuccess();
		} catch (Exception e) {
			logger.error("初始化我的日历失败 memberId={}", memberId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}
	
	@Override
	public ResultDTO<CalendarDto> initCalendarForNew(Long memberId) {
		ResultDTO<CalendarDto> rDO = new ResultDTO<CalendarDto>();
		CalendarDto model = new CalendarDto();
		try {
			Integer flag =0;
			// 统计月收金额
			Date now = DateUtils.getCurrentDate();
			String startYM = DateUtils.getStrFromDate(now, DateUtils.DATE_FMT_6);
			Date endDate = DateUtils.addMonth(now, Constant.CALENDAR_MONTHS_NUM);
			String endYM = DateUtils.getStrFromDate(endDate, DateUtils.DATE_FMT_6);
			String queryDate = DateUtils.getDateStrFromDate(now);
			List<TransactionInterestForMonth> transactionInterestByMonth = transactionInterestManager.selectTransactionInterestsByMonth(
					memberId, startYM, endYM);
			if (Collections3.isNotEmpty(transactionInterestByMonth)) {
				model.setTransactionInterestByMonth(transactionInterestByMonth);
			}
			// 还款日历
			List<TransactionInterestForDate> transactionInterestByCalendar = transactionInterestManager.selectTransactionInterestsByEndDate(
					memberId, startYM, endYM);
			//投资，当日可还款
			Set<String> dates = this.findInvestingDebtProject();
			for(TransactionInterestForDate transactionInterestForDate:transactionInterestByCalendar){
				if(dates.contains(transactionInterestForDate.getDateStr())){
					//当日有回款，不显示投资可回款标记
					//transactionInterestForDate.setInvestRepaymentStatus(1);
					dates.remove(transactionInterestForDate.getDateStr());
				}
				if(queryDate.equals(transactionInterestForDate.getDateStr())){
					flag=1;	//今日有回款，推荐精选项目：1 ；今日无回款，有今日投资，当日可回款项目：2
				}
			}
			for (String date :dates){
				TransactionInterestForDate addDate = new TransactionInterestForDate();
				addDate.setDateStr(date);
				addDate.setInvestRepaymentStatus(1);
				transactionInterestByCalendar.add(addDate);
			}	
			if(dates.contains(queryDate)){
					flag=2;//今日有回款，推荐精选项目：1 ；今日无回款，有今日投资，当日可回款项目：2
			}
			if (Collections3.isNotEmpty(transactionInterestByCalendar)) {
				model.setTransactionInterestByCalendar(transactionInterestByCalendar);
			}
			// 当天还款列表
			if(flag==1){
				ResultDTO<CalendarDto> calendarTransactionInterestDetailAPPDto = getCalendarByDateNew(memberId, queryDate);
				if (!calendarTransactionInterestDetailAPPDto.isSuccess()) {
					logger.error("初始化我的日历中查询当日还款列表失败  memberId={}, queryDate={}", memberId, queryDate);
				}
				model.setCalendarTransactionInterestDetailAPPDto(calendarTransactionInterestDetailAPPDto.getResult().getCalendarTransactionInterestDetailAPPDto());
				model.setRecommendProjectList(calendarTransactionInterestDetailAPPDto.getResult().getRecommendProjectList());
			}
			if(flag==2){
				ResultDTO<CalendarDto> RecommendProjectDto = getProjectByEndData( queryDate);
				model.setRecommendProjectList(RecommendProjectDto.getResult().getRecommendProjectList());
			}
			model.setFlag(flag);
			rDO.setResult(model);
			rDO.setIsSuccess();
		} catch (Exception e) {
			logger.error("初始化我的日历失败 memberId={}", memberId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	@Override
	public ResultDTO<CalendarDto> getCalendarByDate(Long memberId, String date) {
		ResultDTO<CalendarDto> rDO = new ResultDTO<CalendarDto>();
		CalendarDto model = new CalendarDto();
		try {
			if (StringUtil.isBlank(date)) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDO;
			}
			ResultDO<TransactionInterestDetail> transactionInterestDO = transactionInterestService.getMemberInterestDetail(memberId, date);
			if (transactionInterestDO.isError()) {
				rDO.setResultCode(transactionInterestDO.getResultCode());
				return rDO;
			}
			model.setTransactionInterestByDate(transactionInterestDO.getResult());
			
			Map<String, Object> map = Maps.newHashMap();
			map.put("memberId", memberId);
			map.put("endDate", date);
			/*if(DateUtils.compareTwoDate(DateUtils.formatDate(DateUtils.addDate(DateUtils.getCurrentDate(),-2), DateUtils.DATE_FMT_3)
					,DateUtils.formatDate(DateUtils.getDateFromString(date),DateUtils.DATE_FMT_3))!=2)*/
			if(DateUtils.isTheSameDay(DateUtils.zerolizedTime(DateUtils.getDateFromString(date)),
					DateUtils.zerolizedTime(DateUtils.getCurrentDate()))
					&&	transactionInterestManager.ifPayByEndDateAndMemberId(map)>0
					){
				//当天，且已还款，显示精选项目
				List<RecommendProjectDto> recommendProjectList = projectService.getRecommendProject(2);
				model.setRecommendProjectList(recommendProjectList);
			}
			model.setFlag(1);
			rDO.setResult(model);
			rDO.setIsSuccess();
		} catch (Exception e) {
			logger.error("日历获取单日数据失败 memberId={},date={}", memberId, date, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}
	
	/*
	 *1.5.0 获取当日回款数据方法重构 
	 **/
	@Override
	public ResultDTO<CalendarDto> getCalendarByDateNew(Long memberId, String date) {
		ResultDTO<CalendarDto> rDO = new ResultDTO<CalendarDto>();
		CalendarDto model = new CalendarDto();
		try {
			if (StringUtil.isBlank(date)) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return rDO;
			}
			ResultDO<CalendarTransactionInterestDetailAPPDto> calendarTransactionInterestDetailAPPDto = transactionInterestService.getMemberInterestDetailNew(memberId, date);
			if (calendarTransactionInterestDetailAPPDto.isError()) {
				rDO.setResultCode(calendarTransactionInterestDetailAPPDto.getResultCode());
				return rDO;
			}
			model.setCalendarTransactionInterestDetailAPPDto(calendarTransactionInterestDetailAPPDto.getResult());
			
			Map<String, Object> map = Maps.newHashMap();
			map.put("memberId", memberId);
			map.put("endDate", date);
			/*if(DateUtils.compareTwoDate(DateUtils.formatDate(DateUtils.addDate(DateUtils.getCurrentDate(),-2), DateUtils.DATE_FMT_3)
					,DateUtils.formatDate(DateUtils.getDateFromString(date),DateUtils.DATE_FMT_3))!=2)*/
			if(DateUtils.isTheSameDay(DateUtils.zerolizedTime(DateUtils.getDateFromString(date)),
					DateUtils.zerolizedTime(DateUtils.getCurrentDate()))
					&&	transactionInterestManager.ifPayByEndDateAndMemberId(map)>0
					){
				//当天，且已还款，显示精选项目
				List<RecommendProjectDto> recommendProjectList = projectService.getRecommendProject(2);
				model.setRecommendProjectList(recommendProjectList);
			}
			model.setFlag(1);
			rDO.setResult(model);
			rDO.setIsSuccess();
		} catch (Exception e) {
			logger.error("日历获取单日数据失败 memberId={},date={}", memberId, date, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}
	
	private Set<String> findInvestingDebtProject() throws ManagerException {
		
		Set<String> dates = Sets.newHashSet();
		Map<String, Object> map = Maps.newHashMap();
		List<Project> projectList = projectManager.findInvestingDebtProject(map);
		for(Project project : projectList){
			dates.add(DateUtils.getStrFromDate(project.getEndDate(),DateUtils.DATE_FMT_3));
		}
		return dates;
	}
	
	@Override
	public ResultDTO<CalendarDto> getProjectByEndData(String date){
		ResultDTO<CalendarDto> rDO = new ResultDTO<CalendarDto>();
		List<RecommendProjectDto> recommendProjectList = Lists.newArrayList();
		Map<String, Object> map = Maps.newHashMap();
		CalendarDto model = new CalendarDto();
		try {
			map.put("endDate", date);
			map.put("num", 2);
			List<Project> projectList = projectManager.findInvestingDebtProject(map);
			for (Project project :projectList){
				RecommendProjectDto recommendProjectDto = BeanCopyUtil.map(project, RecommendProjectDto.class);
				recommendProjectDto.setAvailableBalance(projectService.getProjectBalanceById(recommendProjectDto.getId()));
				recommendProjectDto.setIncomeDays(project.getEarningsDaysByStatus());
				recommendProjectList.add(recommendProjectDto);
			}
			model.setFlag(2);
			model.setRecommendProjectList(recommendProjectList);
			rDO.setResult(model);
			rDO.setIsSuccess();
		} catch (Exception e) {
			logger.error("当日可还款的项目失败date={}", date, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}
	
	
	
}
