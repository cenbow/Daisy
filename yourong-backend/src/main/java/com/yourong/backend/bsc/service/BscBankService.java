package com.yourong.backend.bsc.service;

import java.util.List;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.bsc.model.BscBank;

public interface BscBankService {
	/**
	 * 后台银行管理查询
	 * @param pageRequest
	 * @param map
	 * @return
	 */
    public Page<BscBank> findByPage(Page<BscBank> pageRequest, Map<String, Object> map);
    
    /**
	 * 后台保存银行
	 * @param pageRequest
	 * @param map
	 * @return
	 */
    public ResultDO<BscBank> saveBscBank(BscBank bscBank);
    
    /**
     * 批量删除
     * @param bscBank
     * @return
     */
    public ResultDO<BscBank> batchDeleteBank(BscBank bscBank);
    
    /**
     * 获取银行列表
     * @param bscBank
     * @return
     */
    public List<BscBank> getBankList();
}