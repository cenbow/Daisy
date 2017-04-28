package com.yourong.backend.ic.service;

import java.util.Map;

import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.LeaseBonusDetail;

public interface LeaseBonusDetailService {
    int deleteByPrimaryKey(Long id);

    int insert(LeaseBonusDetail record);

    LeaseBonusDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKey(LeaseBonusDetail record);

    int updateByPrimaryKeySelective(LeaseBonusDetail record);

    int batchDelete(long[] ids);

    Page<LeaseBonusDetail> findByPage(Page<LeaseBonusDetail> pageRequest, Map<String,Object> map);
}