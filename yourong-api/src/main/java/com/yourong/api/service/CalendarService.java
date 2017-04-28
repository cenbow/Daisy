package com.yourong.api.service;

import com.yourong.api.dto.CalendarDto;
import com.yourong.api.dto.RecommendProjectDto;
import com.yourong.api.dto.ResultDTO;

public interface CalendarService {
	
	
	/**
	 * 
	 * @Description:初始化我的日历
	 * @param memberId
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月6日 上午11:45:26
	 */
	public ResultDTO<CalendarDto> initCalendar(Long memberId);
	
	/**
	 * 
	 * @Description:初始化我的日历
	 * @param memberId
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月6日 上午11:45:26
	 */
	public ResultDTO<CalendarDto> initCalendarNew(Long memberId);
	
	/**
	 * 
	 * @Description:初始化我的日历   重构当日还款信息方法
	 * @param memberId
	 * @return
	 * @author: zhanghao
	 * @time:2016年6月14日 上午11:45:26
	 */
	public ResultDTO<CalendarDto> initCalendarForNew(Long memberId);

	/**
	 * 
	 * @Description:日历获取单日数据
	 * @param memberId
	 * @param date
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月6日 下午3:38:03
	 */
	public ResultDTO<CalendarDto> getCalendarByDate(Long memberId, String date);
	
	/**
	 * 
	 * @Description:日历获取单日数据
	 * @param memberId
	 * @param date
	 * @return
	 * @author: zhanghao
	 * @time:2016年6月14日 上午11:24:03
	 */
	public ResultDTO<CalendarDto> getCalendarByDateNew(Long memberId, String date);
	
	/**
	 * 
	 * @Description:日历获取单日数据，当日可还款的项目
	 * @param date
	 * @return
	 * @author: zhanghao
	 * @time:2016年5月16日 下午16:07:03
	 */
	public Object getProjectByEndData(String date);
	
}
