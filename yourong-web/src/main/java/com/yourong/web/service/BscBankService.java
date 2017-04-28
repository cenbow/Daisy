package com.yourong.web.service;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.bsc.model.BscBank;

public interface BscBankService {
	/**
	 * 根据Code获得银行卡
	 * @param code
	 * @return
	 * @throws ManagerException
	 */
	public BscBank getBscBankByCode(String code);
}
