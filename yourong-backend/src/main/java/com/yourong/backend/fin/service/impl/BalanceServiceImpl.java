package com.yourong.backend.fin.service.impl;

import com.google.common.collect.Maps;
import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.constant.Constant;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.pay.BalancePayResponse;
import com.yourong.common.thirdparty.pay.PayMentService;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.common.web.BaseService;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;
import  com.yourong.backend.fin.service.BalanceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * 资金余额
 * @author Administrator
 *
 */
@Service
public class BalanceServiceImpl extends BaseService implements BalanceService{
	
	@Autowired
	private BalanceManager balanceManager;
	
	@Autowired
	private PayMentService payMentService;
	
	@Autowired
	private CapitalInOutLogManager capitalInOutLogManager;
	
	@Resource
	private MemberManager  memerManager;
	
	/**
	 * 充值 回调接口
	 */
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void topUpFromThirdPay(long memberID,BigDecimal income,String sourceId) throws Exception {
		
			//新浪同步资金			
			BalancePayResponse queryBalance = payMentService.queryBalance(SerialNumberUtil.generateIdentityId(memberID));
			//更新余额表
			Balance balance = balanceManager.queryBalance(memberID,TypeEnum.BALANCE_TYPE_PIGGY);
			if(balance ==  null){
				balanceManager.insertBalance(TypeEnum.BALANCE_TYPE_PIGGY, 	queryBalance.getBalanceBigDecimal(),queryBalance.getAvailableBalanceBigDecimal(),memberID);
			}else{
				balanceManager.updateBalanceByID(queryBalance.getBalanceBigDecimal(),queryBalance.getAvailableBalanceBigDecimal(), balance.getId());				
			}		
			//记录流水
			capitalInOutLogManager.insert(memberID, TypeEnum.FINCAPITALINOUT_TYPE_RECHARGE, 
					income, BigDecimal.ZERO, queryBalance.getBalanceBigDecimal(), sourceId, "", TypeEnum.FINCAPITALINOUT_PAYACCOUNTTYPE_MAIN);
			
	}

	@Override
	public BigDecimal getBalanceByType(TypeEnum type) {
		try {
			Balance balance =balanceManager.queryBalance(-1L,
					type);
			if(balance!=null) {
				return balance.getAvailableBalance();
			}
		} catch (ManagerException e) {
			logger.error("查询余额（主要用于查询平台投资总额和平台收益利息总额）出异常, type=" + type);
		}
		return null;
	}

	@Override
	public Balance queryBalance(Long sourceID, TypeEnum type) {
		
		try {
			Balance balance =balanceManager.queryBalance(sourceID,type);
			return balance;
		} catch (ManagerException e) {
			logger.error("查询余额出异常, sourceID=" + sourceID);
		}
		return null;
	}

	@Override
	public void initBalance(Long sourceID, TypeEnum type) {
		
		Balance record =  new Balance();
		record.setBalance(BigDecimal.ZERO);
		record.setAvailableBalance(BigDecimal.ZERO);		
		record.setSourceId(sourceID);
		record.setBalanceType(type.getType());
		try {
			int i = balanceManager.insert(record);
		} catch (ManagerException e) {
			logger.error("初始化余额出异常, sourceID=" + sourceID);
		}
	}

	@Override
	public void synBalance(Date startDate,Date endDate) {
		try {
			int size = Constant.BALANCE_SIZE;
			Map<String, Object> map = Maps.newHashMap();
			int totalCount = memerManager.selectActiveForPaginTotalCount(map);
			int totalPageCount = (int) Math.ceil((double) totalCount/ (double) size);
			for (int i = 1; i <= totalPageCount; i++) {
				if (i == 1) {
					map.put(Constant.STARTROW, 0);
				} else {
					map.put(Constant.STARTROW, (i-1) * size);
				}
				map.put(Constant.PAGESIZE, size);
				List<Member> list = memerManager.selectActiveForPagin(map);
				getSinaPayIncome(list,startDate,endDate);
			}
		} catch (ManagerException e) {
			logger.error("同步存钱罐出异常, startDate=" + startDate);
		}
	}
	
	/**
	 * 同步新浪存钱罐收益
	 * @param list
	 */
	private void getSinaPayIncome(List<Member> list,Date startDate,Date endDate) {
		for (Member member : list) {
                try {
                	balanceManager.synThirdPayEaringByAdmin(member.getId(), startDate,endDate);
                } catch (Exception e2) {
                    logger.error("手动同步存钱罐收益 ,memeberid={}",member.getId(), e2);
                }
        }
	}
	
}
