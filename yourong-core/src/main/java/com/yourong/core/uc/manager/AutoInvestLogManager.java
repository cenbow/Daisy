package com.yourong.core.uc.manager;

import java.util.Date;
import java.util.List;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.uc.model.AutoInvestLog;
import com.yourong.core.uc.model.biz.AutoInvestMember;

/**
 * 
 * @Description 自动投标记录manager
 * @author luwenshan
 * @time 2016年8月17日
 *
 */
public interface AutoInvestLogManager {

	/**
	 * 
	 * @Description 插入自动投标记录
	 * @param autoInvestLog
	 * @return
	 * @throws ManagerException
	 * @author luwenshan
	 * @time 2016年8月17日 下午3:37:42
	 */
	int insertSelective(AutoInvestLog autoInvestLog) throws ManagerException;
	
	/**
	 * 
	 * @Description 根据投标日期查询用户是否投标过(订单状态是待处理(0)、处理中(1)、支付成功(3))
	 * @param memberId
	 * @param projectid
	 * @param investTime
	 * @return
	 * @throws ManagerException
	 * @author luwenshan
	 * @time 2016年8月17日 下午3:38:52
	 */
	int getMemberInvestFlag(Long memberId, Long projectid, Date investTime) throws ManagerException;
	
	/**
	 * 
	 * @Description 根据自动投标开启状态和有效期时间获取当前有效期内开启自动投标用户
	 * @param investFlag
	 * @param startTime
	 * @param endTime
	 * @return
	 * @author luwenshan
	 * @time 2016年8月17日 下午10:28:07
	 */
	List<AutoInvestMember> getValidMember(Integer investFlag, Date startTime, Date endTime);
	/**
	 * 
	 * @Description:根据交易id更新自动投标记录表
	 * @param autoLog
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年8月18日 上午10:17:06
	 */
	int updateAutoInvestLogByOrderId(AutoInvestLog autoLog) throws ManagerException;
	/**
	 * 
	 * @Description:更新自动投标记录
	 * @param tradeStatus
	 * @param id
	 * @param hostingCollectTrade
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年8月18日 上午11:11:35
	 */
	public void updateAutoInvestLogRemars(String tradeStatus, Long id,
			HostingCollectTrade hostingCollectTrade) throws ManagerException;
	
}
