package com.yourong.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.bsc.manager.BscBankManager;
import com.yourong.core.bsc.model.BscBank;
import com.yourong.web.service.BscBankService;

@Service
public class BscBankServiceImpl implements BscBankService {
	private Logger logger = LoggerFactory.getLogger(BscBankServiceImpl.class);
	
	@Autowired
	private BscBankManager bscBankManager;

	@Override
	public BscBank getBscBankByCode(String code) {
		try{
			return bscBankManager.getBscBankByCode(code);
		}catch(ManagerException ex){
			logger.error("getBscBankByCode",ex);
		}
		return null;
	}

}
