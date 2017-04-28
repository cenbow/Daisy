package com.yourong.backend.fin.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.fin.service.LoanService;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.manager.LoanManager;
import com.yourong.core.fin.model.biz.LoanBiz;

@Service
public class LoanServiceImpl implements LoanService {
	private static final Logger logger = LoggerFactory.getLogger(Logger.class);

	@Autowired
	private LoanManager loanManager;
	@Override
	public Page<LoanBiz> findByPage(Page<LoanBiz> pageRequest,
			Map<String, Object> map) {
		try {
			return loanManager.findByPage(pageRequest, map);
		} catch (Exception e) {
			logger.error("获取放款列表出错",e);
		}
		return null;
	}
	@Override
	public LoanBiz findLoanBiz(Long projectId) {
		try {
			return loanManager.findLoanBiz(projectId);
		} catch (Exception e) {
			logger.error("获取放款信息失败,projectId="+projectId);
		}
		return null;
	}

}
