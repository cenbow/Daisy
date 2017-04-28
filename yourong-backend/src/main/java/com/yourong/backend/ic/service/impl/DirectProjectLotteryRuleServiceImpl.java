package com.yourong.backend.ic.service.impl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rop.thirdparty.com.google.common.collect.Lists;
import rop.thirdparty.com.google.common.collect.Maps;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.yourong.backend.ic.service.DirectProjectLotteryRuleService;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.core.ic.manager.ProjectExtraManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.DirectLotteryRuleForBackend;
import com.yourong.core.ic.model.DirectProjectBiz;
import com.yourong.core.ic.model.LotteryRuleAmountNumber;
import com.yourong.core.ic.model.PrizeInPool;
import com.yourong.core.ic.model.PrizePool;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectExtra;
import com.yourong.core.ic.model.WinNumber;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.model.Activity;

@Service
public class DirectProjectLotteryRuleServiceImpl implements
		DirectProjectLotteryRuleService {

	private static Logger logger = LoggerFactory
			.getLogger(DirectProjectLotteryRuleServiceImpl.class);

	@Autowired
	private ActivityManager activityManager;

	@Autowired
	private ProjectExtraManager projectExtraManager;
	
	@Autowired
	private ProjectManager projectManager;

	@Override
	public Object save(DirectLotteryRuleForBackend dir) {

		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			/* 前置校验 */
			resultDO = preCheckForAdd(resultDO, dir);
			if (!resultDO.isSuccess()) {
				return resultDO;
			}
			
			//List<PrizePool> prizePool = dir.getPrizePool();
			//String prizePoolJson = JSON.toJSONString(prizePool);
			//String rewardHour = dir.getRewardHour();
			String popularity = dir.getPopularity();
			List<PrizeInPool> prizeInPool = dir.getPrizeInPool();
			Integer i = 1;
			for (PrizeInPool prInPool : prizeInPool) {
				prInPool.setLevel(i);
				i++;
			}
			String prizeInPoolJson = JSON.toJSONString(prizeInPool);
			List<LotteryRuleAmountNumber> lottery = dir.getLottery();
			String lotteryJson = JSON.toJSONString(lottery);
			Map<String, Object> map = Maps.newHashMap();
			//map.put("prizePool", prizePoolJson);
			map.put("prizeInPool", prizeInPoolJson);
			map.put("lottery", lotteryJson);
			//map.put("rewardHour",rewardHour);
			map.put("popularity", popularity);

			String mapJson = JSON.toJSONString(map);
			Activity activity = new Activity();
			activity.setActivityName(ActivityConstant.ACTIVITY_DIRECT_CATALYZER
					+ DateUtils.formatDatetoString(DateUtils.getCurrentDate(),
							DateUtils.DATE_FMT_0));
			activity.setActivityDesc(ActivityConstant.ACTIVITY_DIRECT_CATALYZER);
			activity.setStartTime(DateUtils.getCurrentDate());
			activity.setEndTime(DateUtils.addYearsApart(
					DateUtils.getCurrentDate(), 2));
			activity.setType(3);
			activity.setActivityStatus(StatusEnum.ACTIVITY_STATUS_IS_START
					.getStatus());
			activity.setObtainConditionsJson(mapJson);

			// 添加之前，将之前的活动置为过期，结束
			Activity activityForQuery = new Activity();
			activityForQuery.setActivityDesc(ActivityConstant.ACTIVITY_DIRECT_CATALYZER);
			List<Activity> activityList = activityManager
					.getActivityBySelective(activityForQuery);
			for(Activity act:activityList){
				if(act.getActivityStatus()==StatusEnum.ACTIVITY_STATUS_IS_END.getStatus()){
					continue;
				}
				Activity activityForUpdate = new Activity();
				activityForUpdate.setId(act.getId());
				activityForUpdate.setType(act.getType());
				activityForUpdate.setAuditStatus(StatusEnum.ACTIVITY_STATUS_IS_END.getStatus());
				activityForUpdate.setEndTime(DateUtils.getCurrentDate());
				activityManager.updateByPrimaryKeySelective(activityForUpdate);
			}
			
			activityManager.addActivity(activity);

		} catch (Exception e) {
			logger.error("保存直投项目，满标悬赏规则异常，dir={}", dir, e);
			resultDO.setResultCode(ResultCode.ERROR);
		}
		return resultDO;
	}

	private ResultDO<Object> preCheckForAdd(ResultDO<Object> resultDO, DirectLotteryRuleForBackend dir)
			throws ManagerException {
		Map<String, Object> map = Maps.newHashMap();
		map.put("statusCode", "1");//募集中+预告
		map.put("activitySign", TypeEnum.PROJECT_ACTIVITY_CATALYZER.getType());
		Integer  num = projectExtraManager.getProjectExtraByStatus(map);
		if(num>0){
			resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_PORJECT_INVESTING_ERROR);
			return resultDO;
		}
		Integer  numNo = projectExtraManager.getProjectExtraNoticeByMap(map);//预告中的项目
		if(numNo>0){
			resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_PORJECT_INVESTING_ERROR);
			return resultDO;
		}
		/*List<PrizePool> prizePool = dir.getPrizePool();
		for(PrizePool pri:prizePool){
			if(Float.parseFloat(pri.getRatio())<0){
				resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_PRIZE_POOL_ZERO_ERROR);
				return resultDO;
			}
			if(Float.parseFloat(pri.getRatio())>1){
				resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_PRIZE_POOL_ERROR);
				return resultDO;
			}
		}*/
		
		List<PrizeInPool> prizeInPool = dir.getPrizeInPool();
		for(PrizeInPool priInPool:prizeInPool){
			if(priInPool.getProportion()==null||StringUtils.isBlank(priInPool.getProportion())){
				resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_PRIZE_POOL_NULL_ERROR);
				return resultDO;
			}
			if(Float.parseFloat(priInPool.getProportion())<0){
				resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_PRIZE_IN_POOL_PROPORTION_ZERO_ERROR);
				return resultDO;
			}
			if(priInPool.getNum()==null){
				resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_NUM_NULL_ERROR);
				return resultDO;
			}
			if(priInPool.getNum()<1){
				resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_PRIZE_NUM_ZERO_ERROR);
				return resultDO;
			}
		}
		float and = 0;
		for(PrizeInPool priInPool:prizeInPool){
			and += (new BigDecimal(priInPool.getNum()).multiply(new BigDecimal(priInPool.getProportion())))
					.floatValue();
		}
		if(Math.abs(and-1) >=0.01){
			resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_PRIZE_IN_POOL_ERROR);
			return resultDO;
		}
		List<LotteryRuleAmountNumber> lottery = dir.getLottery();
		Integer index = 0;
		if(lottery.size()<2){
			resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_NUM_MORE_THAN_ONE_ERROR);
			return resultDO;
		}
		for(LotteryRuleAmountNumber lot:lottery){
			if(lot.getNumber()==null){
				resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_LOTTERY_NUM_ERROR);
				return resultDO;
			}
			if(lot.getNumber()<0){
				resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_LOTTERY_NUM_ZERO_ERROR);
				return resultDO;
			}
			
			if(index == 0){
				if(lot.getStartAmount()==null||lot.getEndAmount()==null){
					resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_LOTTERY_NUM_NULL_ERROR);
					return resultDO;
				}
				if(lot.getStartAmount()!=0){
					resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_STARTAMOUNT_ERROR);
					return resultDO;
				}
				if(lot.getEndAmount() <= lot.getStartAmount()){
					resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_STARTAMOUNT_ERROR);
					return resultDO;
				}
				index++;
				continue;
			}
			if(lot.getStartAmount()==null){
				resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_LOTTERY_NUM_NULL_ERROR);
				return resultDO;
			}
			if(lot.getStartAmount()<=lottery.get(index-1).getEndAmount()){//小于上一条的结束金额
				resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_MORE_THAN_ONE_ERROR);
				return resultDO;
			}
			if(lot.getStartAmount()-lottery.get(index-1).getEndAmount()>1){//相差>1
				resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_SUB_MORE_THAN_ONE_ERROR);
				return resultDO;
			}
			if(index == lottery.size()-1 ){//最后一条规则的最大金额必须为空
				if(lot.getEndAmount()!=null){
					resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_LAST_ERROR);
					return resultDO;
				}
				index++;
				continue;
			}
			if(lot.getEndAmount()==null){
				resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_LOTTERY_NUM_NULL_ERROR);
				return resultDO;
			}
			if(lot.getEndAmount() <= lot.getStartAmount()){
				resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_FIRST_ENDAMOUNT_ERROR);
				return resultDO;
			}
			
			index++;
		}
		
		/*String rewardHour = dir.getRewardHour();;
		if(StringUtils.isBlank(rewardHour)||Float.valueOf(rewardHour)<1){
			resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_NOT_SET_HOUR_ERROR);
			return resultDO;
		}*/
		
		String popularity = dir.getPopularity();
		if(StringUtils.isBlank(popularity)||Float.valueOf(popularity)<0){
			resultDO.setResultCode(ResultCode.PROJECT_DIRECT_LOTTERY_NOT_SET_POPULARITY_ERROR);
			return resultDO;
		}
		
		return resultDO;
	}
	
	@Override
	public Object ajaxInit(DirectLotteryRuleForBackend dir) {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			//按照描述取最近一个活动
			Activity activity = new Activity();
			activity.setActivityDesc(ActivityConstant.ACTIVITY_DIRECT_CATALYZER);
			List<Activity> activityList = activityManager
					.getActivityBySelective(activity);
			if (!Collections3.isEmpty(activityList)) {
				Activity act = activityList.get(activityList.size() - 1);

				String mapJson = act.getObtainConditionsJson();

				Map map = JSON.parseObject(mapJson);

				/*String prizePoolJson = map.get("prizePool").toString();
				List<PrizePool> prizePool = JSON.parseArray(prizePoolJson,
						PrizePool.class);*/
				String prizeInPoolJson = map.get("prizeInPool").toString();
				List<PrizeInPool> prizeInPool = JSON.parseArray(
						prizeInPoolJson, PrizeInPool.class);
				String lotteryJson = map.get("lottery").toString();
				List<LotteryRuleAmountNumber> lottery = JSON.parseArray(
						lotteryJson, LotteryRuleAmountNumber.class);

				lottery.get(lottery.size()-1).setEndAmount(null);//规则最后一条处理为null
				
				/*if(map.containsKey("rewardHour")){
					String rewardHour = map.get("rewardHour").toString();
					dir.setRewardHour(rewardHour);
				}*/
				if(map.containsKey("popularity")){
					String popularity = map.get("popularity").toString();
					dir.setPopularity(popularity);
				}
				
				//dir.setPrizePool(prizePool);
				dir.setPrizeInPool(prizeInPool);
				dir.setLottery(lottery);
				
			}
			resultDO.setResult(dir);
		} catch (Exception e) {
			logger.error("初始化满标悬赏规则异常，dir={}", dir, e);
			resultDO.setResultCode(ResultCode.ERROR);
		}
		return resultDO;
	}

	@Override
	public void catalyticActivity(Project p) {

		try {
			ProjectExtra projectExtra = projectExtraManager.getProjectQucikReward(p
					.getId());
			if (projectExtra == null) {
				return;
			}
			if (projectExtra.getActivitySign()==null||projectExtra.getActivitySign() != TypeEnum.PROJECT_ACTIVITY_CATALYZER
					.getType()) {
				return;
			}
			Project pro = projectManager.selectByPrimaryKey(p.getId());
			
			// 根据项目总额计算总共券数
			Integer totalNum = pro
					.getTotalAmount()
					.divide(pro.getMinInvestAmount(), 2,
							BigDecimal.ROUND_HALF_UP).intValue();

			Activity activity = new Activity();
			activity.setActivityDesc(ActivityConstant.ACTIVITY_DIRECT_CATALYZER);
			List<Activity> activityList = activityManager
					.getActivityBySelective(activity);

			if (activityList != null) {
				Activity act = activityList.get(activityList.size() - 1);

				String mapJson = act.getObtainConditionsJson();

				Map map = JSON.parseObject(mapJson);

				String prizeInPoolJson = map.get("prizeInPool").toString();
				List<PrizeInPool> prizeInPool = JSON.parseArray(
						prizeInPoolJson, PrizeInPool.class);

				Integer totalPrize = 0;// 总共奖项个数
				for (PrizeInPool pri : prizeInPool) {
					totalPrize += pri.getNum();
				}
				HashSet<Integer> winNumSet = Sets.newHashSet();
				this.randomSet(1, totalNum, totalPrize, winNumSet);

				List<WinNumber> winNumList = Lists.newArrayList();
				for (Integer number : winNumSet) {
					WinNumber winNumber = new WinNumber();
					winNumber.setNumber(number);
					winNumList.add(winNumber);
				}
				Integer index = 0;
				if(totalPrize>totalNum){
					List<WinNumber> winNumListNew = Lists.newArrayList();
					for (PrizeInPool priToWin : prizeInPool) {
						Integer level = priToWin.getLevel();
						for (Integer i = 0; i < priToWin.getNum(); i++) {
							if(index == winNumList.size() ){
								index = 0;
							}
							WinNumber winNumber = new WinNumber();
							winNumber.setNumber(winNumList.get(index).getNumber());
							winNumber.setPrize(level);
							winNumListNew.add(winNumber);
							index++;
						}
					}
					winNumList = winNumListNew;
				}else{
					for (PrizeInPool priToWin : prizeInPool) {
						Integer level = priToWin.getLevel();

						for (Integer i = 0; i < priToWin.getNum(); i++) {
							winNumList.get(index).setPrize(level);
							index++;
						}
					}
				}
				String winNumListJson = JSON.toJSONString(winNumList);
				ProjectExtra proExtraUpdate = new ProjectExtra();
				proExtraUpdate.setId(projectExtra.getId());
				proExtraUpdate.setActivityId(act.getId());
				proExtraUpdate.setExtraInformation(winNumListJson);
				projectExtraManager.updateByPrimaryKeySelective(proExtraUpdate);
			}
		} catch (ManagerException e) {
			logger.error("项目上线，初始化满标悬赏规则异常", e);
		}
	}

	/**
	 * 随机指定范围内N个不重复的数 利用HashSet的特征，只能存放不同的值
	 * 
	 * @param min
	 *            指定范围最小值
	 * @param max
	 *            指定范围最大值
	 * @param n
	 *            随机数个数
	 * @param HashSet
	 *            <Integer> set 随机数结果集
	 */
	public static void randomSet(int min, int max, int n, HashSet<Integer> set) {
		if(n > (max - min + 1)){ //处理为n=max -min +1 
			n = max - min ;
		}
		if (  max < min) {
			return;
		}
		for (int i = 0; i < n; i++) {
			// 调用Math.random()方法
			int num = (int) (Math.random() * (max - min)) + min;
			set.add(num);// 将不同的数存入HashSet中
			if(n == set.size()){
				return ;
			}
		}
		int setSize = set.size();
		// 如果存入的数小于指定生成的个数，则调用递归再生成剩余个数的随机数，如此循环，直到达到指定大小
		if (setSize < n) {
			randomSet(min, max, n, set);// 递归    此处递归有问题：递归后n值已经改变
		}
	}

}
