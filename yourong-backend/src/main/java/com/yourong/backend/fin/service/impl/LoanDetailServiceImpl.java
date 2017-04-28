package com.yourong.backend.fin.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.fin.service.LoanDetailService;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.manager.LoanDetailManager;
import com.yourong.core.fin.model.LoanDetail;

@Service
public class LoanDetailServiceImpl implements LoanDetailService {
	
	private static final Logger logger = LoggerFactory.getLogger(LoanDetailServiceImpl.class);
    @Autowired
    private LoanDetailManager loanDetailManager;


    @Override
    public int insertSelective(LoanDetail record) {
    	try {
			return loanDetailManager.insertSelective(record);
		} catch (Exception e) {
			logger.error("插入放款记录出错,loanDetail="+record,e);
		}
    	return 0;
    }

    public LoanDetail selectByPrimaryKey(Long id) {
         try {
			return loanDetailManager.selectByPrimaryKey(id);
		} catch (ManagerException e) {
			logger.error("根据主键查询放款记录失败,id="+id,e);
		}
         return null;
    }


    public Page<LoanDetail> findByPage(Page<LoanDetail> pageRequest, Map<String,Object> map) {
        try {
			return loanDetailManager.findByPage(pageRequest,map);
		} catch (ManagerException e) {
			logger.error("获取放款记录列表失败",e);
		}
		return null; 
    }

}