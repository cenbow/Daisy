package com.yourong.core.ic.manager;

import java.util.List;
import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.ic.model.LotteryRuleAmountNumber;
import com.yourong.core.ic.model.PrizeInPool;
import com.yourong.core.ic.model.PrizePool;
import com.yourong.core.ic.model.ProjectExtra;
import com.yourong.core.ic.model.QuickRewardConfig;

/**
 * 
 * @desc 特殊项目
 * @author wangyanji 2015年12月31日下午4:49:10
 */
public interface ProjectExtraManager {

	/**
	 * 
	 * @Description:保存
	 * @param projectExtra
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2015年12月31日 下午4:49:22
	 */
	public int insert(ProjectExtra projectExtra) throws ManagerException;

	/**
	 * 
	 * @Description:获取特殊项目标识
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年1月1日 下午3:35:56
	 */
	public int getProjectActivitySign(Long projectId) throws ManagerException;
	
	/**
	 * 查取指定项目ID的快投有奖信息
	 * @Description:获取特殊额外信息
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年10月31日 下午3:35:56
	 */
	public ProjectExtra getProjectQucikReward(Long projectId) throws ManagerException;
	
	/**
	 * 
	 * @Description:更新
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年10月31日 下午3:35:56
	 */
	public int updateByPrimaryKeySelective(ProjectExtra projectExtra) throws ManagerException;
	
	/**
	 * 
	 * @Description:删除
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年10月31日 下午3:35:56
	 */
	public int deleteByPrimaryKey(Long id) throws ManagerException;
	
	/**
	 * 获取指定项目ID的活动的PrizePool（奖池）集合
	 * @param projectId
	 * @return
	 */
	public List<PrizePool> getPrizePoolByProjectId(Long projectId);
	
	
	/**
	 * 获取快投有奖，奖项等级及奖金系数集合
	 * @param projectId
	 * @return
	 */
	public List<PrizeInPool> getPrizeInPoolByProjectId(Long projectId);

	public String getRewardPopularityByProjectId(Long projectId);
	
	public String getRewardHourByProjectId(Long projectId);
	/**
	 * 获取项目关联活动参与资格的规则
	 * @param projectId
	 * @return
	 */
	public List<LotteryRuleAmountNumber> getLotteryByProjectId(Long projectId);
	
	
	/**
	 * 
	 * @Description:获取项目最新的活动信息
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年11月2日 下午5:22:48
	 */
	public ProjectExtra getNewProjectExtra() throws ManagerException;
	
	/**
	 * 
	 * @Description:获取交易号获取抽奖次数
	 * @return
	 * @author: zhanghao
	 * @time:2016年11月7日 下午5:22:48
	 */
	public Integer getLotteryNum(Long transactionId);
	
	public Integer getProjectExtraByStatus(Map<String, Object> map) throws ManagerException;
	
	public Integer getProjectExtraNoticeByMap(Map<String, Object> map) throws ManagerException;
	
	public boolean isQuickProject (Long projectId);
	
	public ProjectExtra getAddRateProject(Long projectId) throws ManagerException;
	public boolean isInvestingQuickProject()throws ManagerException ;
	
	public Long getQuickProjectLately()throws ManagerException;
	
	public String getPrizePoolAmountByProjectId(Long id);
	
	public QuickRewardConfig getQuickRewardConfig(Long id);
}
