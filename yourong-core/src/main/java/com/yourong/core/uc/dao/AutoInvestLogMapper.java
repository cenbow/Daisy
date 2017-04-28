package com.yourong.core.uc.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yourong.core.uc.model.AutoInvestLog;

@Repository
public interface AutoInvestLogMapper {
	
	/**
	 * 
	 * @Description 保存自动投标记录
	 * @param autoInvestLog
	 * @return
	 * @author luwenshan
	 * @time 2016年8月17日 下午3:39:05
	 */
    int insertSelective(AutoInvestLog autoInvestLog);
    
    /**
     * 
     * @Description 根据投标日期查询用户是否投标过(订单状态是待处理(0)、处理中(1)、支付成功(3))
     * @param memberId
     * @param projectId
     * @param investTime
     * @return
     * @author luwenshan
     * @time 2016年8月17日 下午3:22:37
     */
    int getMemberInvestFlag(@Param("memberId")Long memberId, @Param("projectId")Long projectId, @Param("investTime")Date investTime);
    /**
     * 
     * @Description:更新自动投标记录
     * @param autoLog
     * @return
     * @author: chaisen
     * @time:2016年8月18日 上午10:19:49
     */
	int updateAutoInvestLogByOrderId(AutoInvestLog autoLog);
	/**
	 * 
	 * @Description:通过订单id查询记录
	 * @param orderId
	 * @return
	 * @author: chaisen
	 * @time:2016年8月18日 下午2:02:44
	 */
	AutoInvestLog selectByOrderId(Long orderId);
    
}