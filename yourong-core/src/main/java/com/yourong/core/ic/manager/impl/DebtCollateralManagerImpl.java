package com.yourong.core.ic.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.dao.DebtCollateralMapper;
import com.yourong.core.ic.manager.DebtCollateralManager;
import com.yourong.core.ic.model.DebtCollateral;

@Component
public class DebtCollateralManagerImpl implements DebtCollateralManager {
	@Autowired
	private DebtCollateralMapper debtCollateralMapper;

	@Override
	public int deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			return debtCollateralMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insert(DebtCollateral record) throws ManagerException {
		try {
			return debtCollateralMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insertSelective(DebtCollateral record) throws ManagerException {
		try {
			return debtCollateralMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public DebtCollateral selectByPrimaryKey(Long id)
			throws ManagerException {
		try {
			return debtCollateralMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKeySelective(DebtCollateral record)
			throws ManagerException {
		try {
			return debtCollateralMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
		}
		return 0;
	}

	@Override
	public int updateByPrimaryKey(DebtCollateral record)
			throws ManagerException {
		try {
			return debtCollateralMapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<DebtCollateral> findByPage(Page<DebtCollateral> pageRequest,
			Map<String, Object> map) throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			int totalCount = debtCollateralMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<DebtCollateral> selectForPagin = debtCollateralMapper
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
			return debtCollateralMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 根据债权id获取抵押物信息
	 */
	@Override
	public DebtCollateral findCollateralByDebtId(Long debtId)
			throws ManagerException {
		try {
			return debtCollateralMapper.findCollateralByDebtId(debtId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int deleteCollateralByDebtId(Long debtId) throws ManagerException {
		try {
			return debtCollateralMapper.deleteCollateralByDebtId(debtId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public DebtCollateral findCollateralByProjectId(Long debtId) throws ManagerException {
		try {
			return debtCollateralMapper.findCollateralByProjectId(debtId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}
