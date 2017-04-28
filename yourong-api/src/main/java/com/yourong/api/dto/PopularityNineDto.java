/**
 * 
 */
package com.yourong.api.dto;

import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.mc.model.biz.ActivityLotteryResultBiz;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年10月24日上午9:34:11
 */
public class PopularityNineDto extends AbstractBaseObject{
	
	
	/** 人气值总额 **/
	private Integer popularity;
	
	/**中奖用户列表**/
	List<ActivityLotteryResultBiz> list;

	/**
	 * @return the popularity
	 */
	public Integer getPopularity() {
		return popularity;
	}

	/**
	 * @param popularity the popularity to set
	 */
	public void setPopularity(Integer popularity) {
		this.popularity = popularity;
	}

	/**
	 * @return the list
	 */
	public List<ActivityLotteryResultBiz> getList() {
		return list;
	}

	/**
	 * @param list the list to set
	 */
	public void setList(List<ActivityLotteryResultBiz> list) {
		this.list = list;
	}
	

}
