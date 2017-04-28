package com.yourong.core.ic.manager.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rop.thirdparty.com.google.common.collect.Lists;
import rop.thirdparty.com.google.common.collect.Maps;

import com.alibaba.fastjson.JSON;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.core.ic.dao.ProjectExtraMapper;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.LotteryRuleAmountNumber;
import com.yourong.core.ic.model.PrizeInPool;
import com.yourong.core.ic.model.PrizePool;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectExtra;
import com.yourong.core.ic.model.QuickRewardConfig;
import com.yourong.core.mc.manager.ActivityLotteryResultManager;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityLotteryResult;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Transaction;

@Component
public class ProjectExtraManagerImpl implements ProjectExtraManager {

	@Autowired
	private ProjectExtraMapper projectExtraMapper;
	
	@Autowired
	private ActivityManager activityManager;
	
	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private ActivityLotteryResultManager activityLotteryResultManager;
	
	@Autowired
	private SysDictManager sysDictManager;
	
	private static Logger logger = LoggerFactory.getLogger(ProjectExtraManagerImpl.class);

	@Override
	public int insert(ProjectExtra projectExtra) throws ManagerException {
		try {
			return projectExtraMapper.insert(projectExtra);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int getProjectActivitySign(Long projectId) throws ManagerException {
		try {
			ProjectExtra pe = projectExtraMapper.getProjectActivitySign(projectId);
			if (pe != null) {
				return pe.getActivitySign();
			}
			return 0;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public ProjectExtra getProjectQucikReward(Long projectId) throws ManagerException {
		try {
			ProjectExtra pe = projectExtraMapper.getProjectQucikReward(projectId);
			if (pe != null) {
				return pe;
			}
			return new ProjectExtra();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public ProjectExtra getAddRateProject(Long projectId) throws ManagerException{
		Map<String, Object> map = Maps.newHashMap();
		map.put("projectId", projectId);
		map.put("extraType", TypeEnum.PROJECT_EXTRA_PROJECT_ADD_RATE.getType());
		ProjectExtra pe = projectExtraMapper.getProjectExtraInforByMap(map);
		return pe;
	}
	
	@Override
	public int updateByPrimaryKeySelective(ProjectExtra projectExtra) throws ManagerException {
		try {
			int num = projectExtraMapper.updateByPrimaryKeySelective(projectExtra);
			return num;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public int deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			int num = projectExtraMapper.deleteByPrimaryKey(id);
			return num;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public List<PrizePool> getPrizePoolByProjectId(Long projectId){
		List<PrizePool> prizPoolList = Lists.newArrayList();
		try {
			ProjectExtra projectExtra = this.getProjectQucikReward(projectId);
			
			if(projectExtra != null&&projectExtra.getActivitySign()!=null&&projectExtra.getActivityId()!=null){
				if (projectExtra.getActivitySign() == TypeEnum.PROJECT_ACTIVITY_CATALYZER
						.getType()) {
					//根据快投活动描述获取最新活动的奖池系数变化
					Activity  act = activityManager.selectByPrimaryKey(projectExtra.getActivityId());
					if(act!=null){
						String mapJson = act.getObtainConditionsJson();
						Map map = JSON.parseObject(mapJson);
						if(map.containsKey("prizePool")){
							String prizePoolJson = map.get("prizePool").toString();
							prizPoolList = JSON.parseArray(prizePoolJson,
									PrizePool.class);
							return prizPoolList;
						}
					}
				}
			}
			
			//最近的活动规则取
			Activity activity = new Activity();
			activity.setActivityDesc(ActivityConstant.ACTIVITY_DIRECT_CATALYZER);
			List<Activity> activityList = activityManager
						.getActivityBySelective(activity);
			if (!Collections3.isEmpty(activityList)) {
				Activity actNew = activityList.get(activityList.size() - 1);
				String mapJson = actNew.getObtainConditionsJson();
				Map map = JSON.parseObject(mapJson);
				if(map.get("prizePool")!=null){
					String prizePoolJson = map.get("prizePool").toString();
					prizPoolList = JSON.parseArray(prizePoolJson,PrizePool.class);
				}
			}
			return prizPoolList;
			
		} catch (ManagerException e) {
			logger.error("获取快投有奖，奖金池系数随天数变化的关系异常，projectId={}",projectId,e);
		}
		return prizPoolList;
		
	}
	
	@Override
	public List<PrizeInPool> getPrizeInPoolByProjectId(Long projectId){
		List<PrizeInPool> prizeInPoolList = Lists.newArrayList();
		try {
			ProjectExtra projectExtra = this.getProjectQucikReward(projectId);
			if(projectExtra != null&&projectExtra.getActivitySign()!=null&&projectExtra.getActivityId()!=null){
				if (projectExtra.getActivitySign() == TypeEnum.PROJECT_ACTIVITY_CATALYZER
						.getType()) {
					Activity  act = activityManager.selectByPrimaryKey(projectExtra.getActivityId());
					if(act!=null){
						String mapJson = act.getObtainConditionsJson();
						Map map = JSON.parseObject(mapJson);
						if(map.containsKey("prizeInPool")){
							String prizeInPoolJson = map.get("prizeInPool").toString();
							prizeInPoolList = JSON.parseArray(
									prizeInPoolJson, PrizeInPool.class);
							return prizeInPoolList;
						}
					}
				}
			}
			
			//最近的活动规则取
			Activity activity = new Activity();
			activity.setActivityDesc(ActivityConstant.ACTIVITY_DIRECT_CATALYZER);
			List<Activity> activityList = activityManager
					.getActivityBySelective(activity);
			if (!Collections3.isEmpty(activityList)) {
				Activity actNew = activityList.get(activityList.size() - 1);
				String mapJson = actNew.getObtainConditionsJson();
				Map map = JSON.parseObject(mapJson);
				if(map.containsKey("prizeInPool")){
					String prizeInPoolJson = map.get("prizeInPool").toString();
					prizeInPoolList = JSON.parseArray(
							prizeInPoolJson, PrizeInPool.class);
				}
			}
			return prizeInPoolList;
		
		} catch (ManagerException e) {
			logger.error("获取快投有奖，奖项等级及奖金系数异常，projectId={}",projectId,e);
		}
		return prizeInPoolList;
		
	}
	
	@Override
	public List<LotteryRuleAmountNumber> getLotteryByProjectId(Long projectId){
		List<LotteryRuleAmountNumber> lotteryList = Lists.newArrayList();
		try {
			ProjectExtra projectExtra = this.getProjectQucikReward(projectId);
			if(projectExtra != null&&projectExtra.getActivitySign()!=null&&projectExtra.getActivityId()!=null){
				if (projectExtra.getActivitySign() == TypeEnum.PROJECT_ACTIVITY_CATALYZER
						.getType()) {
					Activity  act = activityManager.selectByPrimaryKey(projectExtra.getActivityId());
					if(act!=null){
						String mapJson = act.getObtainConditionsJson();
						Map map = JSON.parseObject(mapJson);
						if(map.containsKey("lottery")){
							String lotteryJson = map.get("lottery").toString();
							lotteryList = JSON.parseArray(
									lotteryJson, LotteryRuleAmountNumber.class);
							return lotteryList;
						}
					}
				}
			}
			//最近的活动规则取
			Activity activity = new Activity();
			activity.setActivityDesc(ActivityConstant.ACTIVITY_DIRECT_CATALYZER);
			List<Activity> activityList = activityManager
					.getActivityBySelective(activity);
			if (!Collections3.isEmpty(activityList)) {
				Activity actNew = activityList.get(activityList.size() - 1);
				String mapJson = actNew.getObtainConditionsJson();
				Map map = JSON.parseObject(mapJson);
				if(map.containsKey("lottery")){
					String lotteryJson = map.get("lottery").toString();
					lotteryList = JSON.parseArray(
							lotteryJson, LotteryRuleAmountNumber.class);
				}
			}
			return lotteryList;
			
		
		} catch (ManagerException e) {
			logger.error("获取快投有奖，投资金额与抽奖次数关系异常，projectId={}",projectId,e);
		}
		return lotteryList;
	}
	
	@Override
	public String getRewardHourByProjectId(Long projectId){
		String rewardHour = "0";
		try {
			ProjectExtra projectExtra = this.getProjectQucikReward(projectId);
			if(projectExtra != null&&projectExtra.getActivitySign()!=null
					//&&projectExtra.getActivityId()!=null 项目预告时，未关联活动id
					){
				if (projectExtra.getActivitySign() == TypeEnum.PROJECT_ACTIVITY_CATALYZER
						.getType()) {
					rewardHour = projectExtra.getExtraInformationTwo();
				}
			}
			return rewardHour;
		} catch (ManagerException e) {
			logger.error("获取快投有奖，奖励时间异常，projectId={}",projectId,e);
		}
		return rewardHour;
		
	}
	
	@Override
	public String getRewardPopularityByProjectId(Long projectId){
		String popularity = "0";
		try {
			ProjectExtra projectExtra = this.getProjectQucikReward(projectId);
			if(projectExtra != null&&projectExtra.getActivitySign()!=null&&projectExtra.getActivityId()!=null){
				if (projectExtra.getActivitySign() == TypeEnum.PROJECT_ACTIVITY_CATALYZER
						.getType()) {
					Activity  act = activityManager.selectByPrimaryKey(projectExtra.getActivityId());
					if(act!=null){
						String mapJson = act.getObtainConditionsJson();
						Map map = JSON.parseObject(mapJson);
						if(map.containsKey("popularity")){
							popularity = map.get("popularity").toString();
							return popularity;
						}
					}
				}
			}
			
			//最近的活动规则取
			Activity activity = new Activity();
			activity.setActivityDesc(ActivityConstant.ACTIVITY_DIRECT_CATALYZER);
			List<Activity> activityList = activityManager
					.getActivityBySelective(activity);
			if (!Collections3.isEmpty(activityList)) {
				Activity newAct = activityList.get(activityList.size() - 1);
				String mapJson = newAct.getObtainConditionsJson();

				Map map = JSON.parseObject(mapJson);
				if(map.containsKey("popularity")){
					popularity = map.get("popularity").toString();
				}
			}
			return popularity;
			
		
		} catch (ManagerException e) {
			logger.error("获取快投有奖，补偿人气值异常，projectId={}",projectId,e);
		}
		return popularity;
		
	}

	@Override
	public ProjectExtra getNewProjectExtra() throws ManagerException {
		try {
			return projectExtraMapper.getNewProjectExtra();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public Integer getLotteryNum(Long transactionId){
		try {
			Transaction transaction = transactionManager.selectTransactionById(transactionId);
			if(transaction==null){
				return 0 ;
			}
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			if(project==null){
				return 0 ;
			}
			ActivityLotteryResult model = new ActivityLotteryResult();
			model.setRewardId(transactionId.toString());
			model.setRemark(project.getId().toString());
			//model.setLotteryStatus(StatusEnum.LOTTERY_STATUS_NO.getStatus());
			List<ActivityLotteryResult> lotteryList = activityLotteryResultManager.getLotteryResultBySelective(model);
			if(lotteryList.size()>0){
				int num = 0;
				for(ActivityLotteryResult act:lotteryList){
					if(act.getLotteryStatus()==StatusEnum.LOTTERY_STATUS_YES.getStatus()){
						continue;
					}
					num++;
				}
				return num;
			}
			ProjectExtra pe = projectExtraMapper.getProjectActivitySign(transaction.getProjectId());
			if(pe==null){
				return 0 ;
			}
			Activity activity=activityManager.selectByPrimaryKey(pe.getActivityId());
			if(activity==null){
				return 0 ;
			}
			
			//募集时间超过奖励期限,不生成次数
			int totalHour=DateUtils.getTimeIntervalHours(project.getOnlineTime(),DateUtils.getCurrentDate());
			String rewardHour =this.getRewardHourByProjectId(transaction.getProjectId());
			if(!(totalHour<Float.valueOf(rewardHour))){
				return 0;
			}
			
			//抽奖次数
			int number=transaction.getInvestAmount().divide(project.getMinInvestAmount()).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			//额外抽奖次数
			int extraNumber=0;
			Map map = JSON.parseObject(activity.getObtainConditionsJson()); 
			String lotteryJson = map.get("lottery").toString();
			List<LotteryRuleAmountNumber> instance =JSON.parseArray(lotteryJson,LotteryRuleAmountNumber.class); 
			if(Collections3.isNotEmpty(instance)){
				for(LotteryRuleAmountNumber bean :instance){
					if(transaction.getInvestAmount().intValue()>=bean.getStartAmount()&&transaction.getInvestAmount().intValue()<=bean.getEndAmount()){
						extraNumber=bean.getNumber();
						break;
					}
				}
			}
			return number+extraNumber;
		} catch (Exception e) {
			logger.error("根据交易ID获取抽奖次数异常，transactionId={}",transactionId,e);
		}
		return 0 ;
		
	}
	
	@Override
	public Integer getProjectExtraByStatus(Map<String, Object> map) throws ManagerException {
		try {
			Integer num = projectExtraMapper.getProjectExtraByMap(map);
			return num;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public Integer getProjectExtraNoticeByMap(Map<String, Object> map) throws ManagerException {
		try {
			Integer num = projectExtraMapper.getProjectExtraNoticeByMap(map);
			return num;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public boolean isQuickProject (Long projectId){
		try {
			ProjectExtra projectExtra = this.getProjectQucikReward(projectId);
			if(projectExtra!=null&&projectExtra.getActivitySign()!=null&&projectExtra.getActivitySign()==TypeEnum.PROJECT_ACTIVITY_CATALYZER
					.getType()){
				return true;
			}
		} catch (ManagerException e) {
			logger.error("根据项目Id判断项目是否为快投项目异常，projectId={}",projectId,e);
		}
		return false;
	} 
	
	
	@Override
	public boolean isInvestingQuickProject()throws ManagerException {
		Map<String, Object> map = Maps.newHashMap();
		map.put("statusCode", "30");//投资中
		map.put("activitySign", TypeEnum.PROJECT_ACTIVITY_CATALYZER.getType());
		Integer num = projectExtraMapper.getExtraProjectNumByStatus(map);
		if(num>0){
			return true;
		}
		return false;
	}
	
	@Override 
	public Long getQuickProjectLately()throws ManagerException {
		List<Long>  idList  = projectExtraMapper.getQuickProjectLately();
		Long i =0L;
		for(Long id:idList){
			String prizePool = this.getPrizePoolAmountByProjectId(id);
			if(new BigDecimal(prizePool).intValue()>0){
				i=id;
				break;
			}
		}
		return i;
	}
	
	
	@Override
	public String getPrizePoolAmountByProjectId(Long id){
		
		try {
			Project project = projectManager.selectByPrimaryKey(id);
			ProjectExtra projectExtra = this.getProjectQucikReward(id);
			// 募集满额经历天数
			int totalDays = DateUtils.getIntervalDays(project.getOnlineTime(), project.getSaleComplatedTime())+ 1;
			
			List<PrizePool> prizePoolList = this.getPrizePoolByProjectId(project.getId());
			int maxDay = 0;
			if (Collections3.isNotEmpty(prizePoolList)) {
				for (PrizePool pri : prizePoolList) {
					if (Float.parseFloat(pri.getRatio())<= 0) {
						continue;
					}
					if (maxDay < pri.getDay()) {
						maxDay = pri.getDay();
					}
				}
			}
			String ratio = "0";
			if (totalDays > maxDay&&Collections3.isNotEmpty(prizePoolList)) {
				return "0";
			}else if(Collections3.isNotEmpty(prizePoolList)){
				for (PrizePool pri : prizePoolList) {
					if (totalDays==pri.getDay()) {
						ratio = pri.getRatio();
					}
				}
				return projectExtra.getExtraAmount().multiply(new BigDecimal(ratio)).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
			}else{
				//查询是否有中奖的记录
				ActivityLotteryResult model=new ActivityLotteryResult();
				model.setRewardType(7);
				model.setRewardId(project.getId().toString());
				model.setActivityId(projectExtra.getActivityId());
				List<ActivityLotteryResult> listActivityResult=activityLotteryResultManager.getLotteryResultBySelectiveAndLotteryStatus(model);
				if(Collections3.isNotEmpty(listActivityResult)){
					return projectExtra.getExtraAmount().toString();
				}
			}
			
		} catch (ManagerException e) {
			logger.error("根据项目Id,获取项目奖池金额异常,projectId={}",id,e);
		}
		return "0";
	}
	
	
	@Override
	public QuickRewardConfig getQuickRewardConfig(Long id){
		QuickRewardConfig result = new QuickRewardConfig();
		try {
			SysDict startSysDict=sysDictManager.findByGroupNameAndKey("quick_reward_configure", "startDate");
			SysDict endSysDict=sysDictManager.findByGroupNameAndKey("quick_reward_configure", "endDate");
			SysDict flagSysDict =sysDictManager.findByGroupNameAndKey("quick_reward_configure", "reward_flag");
			
			if("Y".equals(flagSysDict.getValue())){
				result.setFlag(true);
			}
			
			Date startDate=DateUtils.getDateFromString(startSysDict.getValue()); 
			Date endDate=DateUtils.getDateFromString(endSysDict.getValue()); 
			result.setStartDate(startDate);
			result.setEndDate(endDate);
			
			
			Project project = projectManager.selectByPrimaryKey(id);
			ProjectExtra projectExtra = this.getProjectQucikReward(id);
			
			String popularity = "0";
			if(project!=null&&projectExtra!=null){
				Activity  act = activityManager.selectByPrimaryKey(projectExtra.getActivityId());
				if(act!=null){
					String mapJson = act.getObtainConditionsJson();
					Map map = JSON.parseObject(mapJson);
					if(map.containsKey("popularity")){
						popularity = map.get("popularity").toString();
						result.setPopularity(popularity);
						return result;
					}
				}
			}
			//最近的活动规则取
			Activity activity = new Activity();
			activity.setActivityDesc(ActivityConstant.ACTIVITY_DIRECT_CATALYZER);
			List<Activity> activityList = activityManager
					.getActivityBySelective(activity);
			if (!Collections3.isEmpty(activityList)) {
				Activity act = activityList.get(activityList.size() - 1);
				String mapJson = act.getObtainConditionsJson();

				Map map = JSON.parseObject(mapJson);
				if(map.containsKey("popularity")){
					popularity = map.get("popularity").toString();
				}
			}
			
			result.setPopularity(popularity);
			return result;
			
		} catch (ManagerException e) {
			logger.error("根据项目Id,获取快投返还人气值设置异常,projectId={}",id,e);
		}
		return result;
	}
	
	
}
