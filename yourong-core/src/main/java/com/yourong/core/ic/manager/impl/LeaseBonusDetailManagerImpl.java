package com.yourong.core.ic.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.dao.LeaseBonusDetailMapper;
import com.yourong.core.ic.dao.LeaseDetailMapper;
import com.yourong.core.ic.manager.LeaseBonusDetailManager;
import com.yourong.core.ic.model.LeaseBonusDetail;
import com.yourong.core.ic.model.query.LeaseBonusDetailQuery;

@Service
public class LeaseBonusDetailManagerImpl implements LeaseBonusDetailManager {
	@Autowired
	private LeaseBonusDetailMapper leaseBonusDetailMapper;
	@Autowired
	private LeaseDetailMapper leaseDetailMapper;

	@Override
	public int deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			return leaseBonusDetailMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insert(LeaseBonusDetail record) throws ManagerException {
		try {
			return leaseBonusDetailMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insertSelective(LeaseBonusDetail record) throws ManagerException {
		try {
			return leaseBonusDetailMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public LeaseBonusDetail selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return leaseBonusDetailMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKeySelective(LeaseBonusDetail record) throws ManagerException {
		try {
			return leaseBonusDetailMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKey(LeaseBonusDetail record) throws ManagerException {
		try {
			return leaseBonusDetailMapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<LeaseBonusDetail> findByPage(Page<LeaseBonusDetail> pageRequest, Map<String, Object> map)
			throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			int totalCount = leaseBonusDetailMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<LeaseBonusDetail> selectForPagin = leaseBonusDetailMapper.selectForPagin(map);
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int batchDelete(long[] ids) throws ManagerException {
		try {
			return leaseBonusDetailMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 分红明细
	 */
	@Override
	public Page<LeaseBonusDetail> findByBonusDetailPage(LeaseBonusDetailQuery leaseBonusDetailQuery)
			throws ManagerException {
		try {
			List<LeaseBonusDetail> bonusDetails = Lists.newArrayList();
			int count = leaseBonusDetailMapper.selectCountByProjectIdForPagin(leaseBonusDetailQuery);
			bonusDetails = leaseBonusDetailMapper.selectBonusDetailByProjectIdForPagin(leaseBonusDetailQuery);
			Page<LeaseBonusDetail> page = new Page<LeaseBonusDetail>();
			page.setData(bonusDetails);
			// 每页总数
			page.setiDisplayLength(leaseBonusDetailQuery.getPageSize());
			// 当前页
			page.setPageNo(leaseBonusDetailQuery.getCurrentPage());
			// 总数
			page.setiTotalDisplayRecords(count);
			page.setiTotalRecords(count);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<LeaseBonusDetail> findListByTransactionId(Long transactionId) throws ManagerException {
		try {
			return leaseBonusDetailMapper.findListByTransactionId(transactionId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public LeaseBonusDetail getLeaseTotalIncomeByTransactionId(Long transactionId) throws ManagerException {
		try {
			return leaseBonusDetailMapper.getLeaseTotalIncomeByTransactionId(transactionId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}
