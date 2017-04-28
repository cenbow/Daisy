package com.yourong.backend.fin.service;

import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.WithdrawLog;

public interface WithdrawLogService {   
	
    public Page<WithdrawLog> findByPage(Page<WithdrawLog> pageRequest, Map<String, Object> map);

    /**
     * 同步提现状态
     * @return
     */
	public boolean synchronizedWithdraw(); 
    
}