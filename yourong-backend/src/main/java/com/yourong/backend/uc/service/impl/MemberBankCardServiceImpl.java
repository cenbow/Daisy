package com.yourong.backend.uc.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.uc.service.MemberBankCardService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.uc.manager.MemberBankCardManager;
import com.yourong.core.uc.model.MemberBankCard;

@Service
public class MemberBankCardServiceImpl implements MemberBankCardService {
	
	private static Logger logger = LoggerFactory.getLogger(MemberBankCardServiceImpl.class);
	
	@Autowired
	private MemberBankCardManager memberBankCardManager;
	
	@Autowired
	private SysDictManager sysDictManager;

	@Override
	public Page<MemberBankCard> findByPage(Page<MemberBankCard> pageRequest, Map map) {
		try {
			return memberBankCardManager.findByPage(pageRequest, map);
		} catch(ManagerException e){
			logger.error("findByPage", e);
		}
		return null;
	}

	@Override
	public ResultDO<Object> deleteBankCard(Long mobile, String userIp) {
		try {
			return memberBankCardManager.deleteBankCard(mobile, userIp);
		} catch (Exception e) {
			logger.error("后台删除用户绑定银行卡失败,mobile={}",mobile,e);
		}
		return null;
	}

	@Override
	public Page<MemberBankCard> queryMemberCard(
			Page<MemberBankCard> pageRequest, Map map) {
		try {
			return memberBankCardManager.queryMemberCard(pageRequest, map);
		} catch(ManagerException e){
			logger.error("findByPage", e);
		}
		return null;
	}

	@Override
	public ResultDO<Object> deleteBankCardByMemberId(Long id) {
		try {
			SysDict sysDict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
	    	String userIp = "";
	    	if(sysDict != null) {
	    		userIp = sysDict.getValue();
	    	}
			return memberBankCardManager.deleteBankCardByMemberId(id, userIp);
		} catch (Exception e) {
			logger.error("后台删除用户绑定银行卡失败,id={}",id,e);
		}
		return null;
	}
}
