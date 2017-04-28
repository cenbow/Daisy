package com.yourong.backend.fin.service;

import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.LoanDetail;

public interface LoanDetailService {

	/**
	 * 插入放款记录
	 * @param record
	 * @return
	 */
    int insertSelective(LoanDetail record);
	/**
	 * 查询放款记录
	 * @param id
	 * @return
	 */
    LoanDetail selectByPrimaryKey(Long id);

    /**
     * 分页查询放款记录列表
     * @param pageRequest
     * @param map
     * @return
     */
    Page<LoanDetail> findByPage(Page<LoanDetail> pageRequest, Map<String, Object> map);
}