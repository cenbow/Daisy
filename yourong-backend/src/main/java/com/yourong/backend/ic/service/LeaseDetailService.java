package com.yourong.backend.ic.service;

import java.math.BigDecimal;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.LeaseDetail;

public interface LeaseDetailService {
	int deleteByPrimaryKey(Long id, Long leaseBonusId, BigDecimal rental) throws ManagerException;

	ResultDO<LeaseDetail> insertSelective(LeaseDetail record) throws ManagerException;

	LeaseDetail selectByPrimaryKey(Long id);

	int updateByPrimaryKey(LeaseDetail record);

	ResultDO<LeaseDetail> updateByPrimaryKeySelective(LeaseDetail record, BigDecimal oldTotalRental,
			BigDecimal newTotalRental) throws ManagerException;

	int batchDelete(long[] ids);

	Page<LeaseDetail> findByPage(Page<LeaseDetail> pageRequest, Map<String, Object> map);
}