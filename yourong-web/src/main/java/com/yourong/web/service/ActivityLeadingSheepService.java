package com.yourong.web.service;

import java.util.List;

import com.yourong.core.fin.model.query.PopularityInOutLogQuery;
import com.yourong.core.mc.model.biz.ActivityForFirstInvest;
import com.yourong.core.mc.model.biz.ActivityLeadingSheep;
import com.yourong.core.mc.model.biz.ActivityLeadingSheepRanksAndCount;


/**
 * 一羊领头专题页
 * @author fuyili
 *
 * 创建时间:2015年7月8日下午12:00:15
 */
public interface ActivityLeadingSheepService {
	/**
	 * 获取显示一羊领头专题页的项目
	 */
	public List<ActivityLeadingSheep> getProjectForLeadingSheeps();
	
	/**
	 * 获取所有一羊领头列表的用户信息
	 * @param popularityInOutLogQuery
	 * @return
	 */
	public List<ActivityForFirstInvest> selectLeadingSheepList(PopularityInOutLogQuery popularityInOutLogQuery);
	
	/**
	 * 获取一羊领头等风云榜
	 * @param remark
	 * @return
	 */
	public List<ActivityLeadingSheepRanksAndCount> selectLeadingSheepRanksAndCount();

	/**
	 * @Description: 获取支持一锤定音的项目
	 * @return
	 * @author: fuyili
	 * @time:2015年12月21日 上午9:31:02
	 */
	public ActivityLeadingSheep getLastProjectsForQuintupleGift();
}
