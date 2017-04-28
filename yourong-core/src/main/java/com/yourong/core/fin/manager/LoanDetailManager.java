package com.yourong.core.fin.manager;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.LoanDetail;

public interface LoanDetailManager {
	public int deleteByPrimaryKey(Long id) throws ManagerException;

	public int insert(LoanDetail record) throws ManagerException;

	public int insertSelective(LoanDetail record) throws ManagerException;

	public LoanDetail selectByPrimaryKey(Long id) throws ManagerException;

	public int updateByPrimaryKeySelective(LoanDetail record)
			throws ManagerException;

	public int updateByPrimaryKey(LoanDetail record) throws ManagerException;

	public Page<LoanDetail> findByPage(Page<LoanDetail> pageRequest,
			@Param("map") Map<String, Object> map) throws ManagerException;

	public Integer batchDelete(@Param("ids") int[] ids) throws ManagerException;

}
