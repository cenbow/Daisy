package com.yourong.core.uc.manager.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeStatus;
import com.yourong.core.tc.manager.impl.TransactionManagerImpl;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.uc.dao.AutoInvestLogMapper;
import com.yourong.core.uc.dao.AutoInvestSetMapper;
import com.yourong.core.uc.manager.AutoInvestLogManager;
import com.yourong.core.uc.model.AutoInvestLog;
import com.yourong.core.uc.model.biz.AutoInvestMember;

/**
 * 
 * @Description 自动投标记录manager实现
 * @author luwenshan
 * @time 2016年8月17日
 *
 */
@Component
public class AutoInvestLogManagerImpl implements AutoInvestLogManager {
	
    @Autowired
    private AutoInvestLogMapper autoInvestLogMapper;
    
    @Autowired
    private AutoInvestSetMapper autoInvestSetMapper;
    
    private Logger logger = LoggerFactory.getLogger(AutoInvestLogManagerImpl.class);
    
	@Override
	public int insertSelective(AutoInvestLog autoInvestLog) throws ManagerException {
		try {
			return autoInvestLogMapper.insertSelective(autoInvestLog);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	@Override
	public int getMemberInvestFlag(Long memberId, Long projectid, Date investTime) throws ManagerException {
		try {
			return autoInvestLogMapper.getMemberInvestFlag(memberId, projectid, investTime);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}


	@Override
	public List<AutoInvestMember> getValidMember(Integer investFlag, Date startTime, Date endTime) {
		return autoInvestSetMapper.selectValidMember(investFlag, startTime, endTime);
	}


	@Override
	public int updateAutoInvestLogByOrderId(AutoInvestLog autoLog) throws ManagerException {
		try {
			return autoInvestLogMapper.updateAutoInvestLogByOrderId(autoLog);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}


	@Override
	public void updateAutoInvestLogRemars(String tradeStatus, Long orderId,
			HostingCollectTrade hostingCollectTrade) throws ManagerException {
		try {
			if (TradeStatus.TRADE_FAILED.name().equals(tradeStatus) || TradeStatus.TRADE_CLOSED.name().equals(tradeStatus)) {
				AutoInvestLog autoLog=new AutoInvestLog();
				autoLog.setOrderId(orderId);
				autoLog.setStatus(0);
				autoLog.setRemarks(hostingCollectTrade.getRemarks());
				AutoInvestLog biz=autoInvestLogMapper.selectByOrderId(orderId);
				if(biz==null){
					return;
				}
				autoInvestLogMapper.updateAutoInvestLogByOrderId(autoLog);
			}
		} catch (Exception e) {
			logger.error("交易代收回调后更新自动投标记录发生异常，orderId：" + orderId, e);
		}
	}
    
}
