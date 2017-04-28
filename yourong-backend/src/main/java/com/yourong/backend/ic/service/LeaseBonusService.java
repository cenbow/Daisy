package com.yourong.backend.ic.service;

import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.LeaseBonus;
import com.yourong.core.ic.model.LeaseDetail;

public interface LeaseBonusService {
    int deleteByPrimaryKey(Long id);

    int insert(LeaseBonus record);

    LeaseBonus selectByPrimaryKey(Long id);

    int updateByPrimaryKey(LeaseBonus record);

    int updateByPrimaryKeySelective(LeaseBonus record);

    int batchDelete(long[] ids);

    Page<LeaseBonus> findByPage(Page<LeaseBonus> pageRequest, Map<String,Object> map);

    /**
     * 分红结算
     * @param leaseBonus
     * @return
     */
	ResultDO<LeaseBonus> doLeaseBonus(LeaseDetail leaseDetail) throws Exception;
}