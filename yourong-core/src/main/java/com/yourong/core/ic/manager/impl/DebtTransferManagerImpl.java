package com.yourong.core.ic.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.dao.DebtTransferMapper;
import com.yourong.core.ic.manager.DebtTransferManager;
import com.yourong.core.ic.model.DebtTransfer;
@Component
public class DebtTransferManagerImpl implements DebtTransferManager {
	@Autowired
	private DebtTransferMapper debtTransferMapper;

	@Override
	public int deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			return debtTransferMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insert(DebtTransfer record) throws ManagerException {
		try {
			return debtTransferMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insertSelective(DebtTransfer record) throws ManagerException {
		try {
			return debtTransferMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public DebtTransfer selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return debtTransferMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKeySelective(DebtTransfer record)
			throws ManagerException {
		try {
			return debtTransferMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKey(DebtTransfer record) throws ManagerException {
		try {
			return debtTransferMapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<DebtTransfer> findByPage(Page<DebtTransfer> pageRequest,
			Map<String, Object> map) throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			int totalCount = debtTransferMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<DebtTransfer> selectForPagin = debtTransferMapper
					.selectForPagin(map);
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int batchDelete(int[] ids) throws ManagerException {
		try {
			return debtTransferMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}
