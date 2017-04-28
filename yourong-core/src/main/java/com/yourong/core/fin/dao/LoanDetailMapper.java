package com.yourong.core.fin.dao;

import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.LoanDetail;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface LoanDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoanDetail record);

    int insertSelective(LoanDetail record);

    LoanDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanDetail record);

    int updateByPrimaryKey(LoanDetail record);

    Page<LoanDetail> findByPage(Page<LoanDetail> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") int[] ids);

    List<LoanDetail> selectForPagin(@Param("map") Map<String, Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String, Object> map);

	LoanDetail getLoanTimeByProjectId(Long id);
}