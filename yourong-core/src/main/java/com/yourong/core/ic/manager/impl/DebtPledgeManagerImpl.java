package com.yourong.core.ic.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.dao.DebtPledgeMapper;
import com.yourong.core.ic.manager.DebtPledgeManager;
import com.yourong.core.ic.model.DebtPledge;

@Component
public class DebtPledgeManagerImpl implements DebtPledgeManager {

	@Autowired
	private DebtPledgeMapper debtPledgeMapper;

	@Override
	public int deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			return debtPledgeMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insert(DebtPledge record) throws ManagerException {
		try {
			return debtPledgeMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insertSelective(DebtPledge record) throws ManagerException {
		try {
			return debtPledgeMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public DebtPledge selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return debtPledgeMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKeySelective(DebtPledge record)
			throws ManagerException {
		try {
			return debtPledgeMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKey(DebtPledge record) throws ManagerException {
		try {
			return debtPledgeMapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<DebtPledge> findByPage(Page<DebtPledge> pageRequest,
			Map<String, Object> map) throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			int totalCount = debtPledgeMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<DebtPledge> selectForPagin = debtPledgeMapper
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
			return debtPledgeMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public DebtPledge findPledgeByDebtId(Long debtId) throws ManagerException {
		try {
			return debtPledgeMapper.findPledgeByDebtId(debtId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int deletePledgeByDebtId(Long debtId) throws ManagerException {
		try {
			return debtPledgeMapper.deletePledgeByDebtId(debtId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}
