package com.yourong.core.ic.manager.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.dao.LeaseDetailMapper;
import com.yourong.core.ic.manager.LeaseDetailManager;
import com.yourong.core.ic.model.LeaseDetail;

@Service
public class LeaseDetailManagerImpl implements LeaseDetailManager {

	@Autowired
	private LeaseDetailMapper leaseDetailMapper;

	@Override
	public int deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			return leaseDetailMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insert(LeaseDetail record) throws ManagerException {
		try {
			return leaseDetailMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insertSelective(LeaseDetail insertSelective) throws ManagerException {
		try {
			return leaseDetailMapper.insertSelective(insertSelective);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public LeaseDetail selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return leaseDetailMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKeySelective(LeaseDetail record) throws ManagerException {
		try {
			return leaseDetailMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKey(LeaseDetail record) throws ManagerException {
		try {
			return leaseDetailMapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<LeaseDetail> findByPage(Page<LeaseDetail> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			int totalCount = leaseDetailMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<LeaseDetail> selectForPagin = leaseDetailMapper.selectForPagin(map);
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int batchDelete(long[] ids) throws ManagerException {
		try {
			return leaseDetailMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int selectCountByLeaseBonusId(Long leaseBonusId) throws ManagerException {
		try {
			return leaseDetailMapper.selectCountByLeaseBonusId(leaseBonusId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<LeaseDetail> selectListByLeaseBonusId(Long leaseBonusId) throws ManagerException {
		try {
			return leaseDetailMapper.selectListByLeaseBonusId(leaseBonusId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 根据项目id获取租赁记录
	 */
	@Override
	public List<LeaseDetail> findListByProjectId(Long projectId) throws ManagerException {
		try {
			return leaseDetailMapper.findListByProjectId(projectId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 获取一个分红项目中用户已分红的总额
	 */
	@Override
	public BigDecimal getTotalUserBonusByLeaseBonusId(Long leaseBonusId) throws ManagerException {
		try {
			return leaseDetailMapper.getTotalUserBonusByLeaseBonusId(leaseBonusId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}


}
