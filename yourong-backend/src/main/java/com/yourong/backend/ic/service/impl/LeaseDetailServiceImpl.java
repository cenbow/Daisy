package com.yourong.backend.ic.service.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aliyun.common.utils.Lists;
import com.google.common.collect.Maps;
import com.yourong.backend.ic.service.LeaseDetailService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.manager.LeaseBonusManager;
import com.yourong.core.ic.manager.LeaseDetailManager;
import com.yourong.core.ic.model.LeaseBonus;
import com.yourong.core.ic.model.LeaseDetail;

@Service
public class LeaseDetailServiceImpl implements LeaseDetailService {

	private static Logger logger = LoggerFactory.getLogger(LeaseDetailServiceImpl.class);

	@Autowired
	private LeaseDetailManager leaseDetailManager;
	@Autowired
	private LeaseBonusManager leaseBonusManager;

	/**
	 * 删除租赁记录
	 */
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteByPrimaryKey(Long id, Long leaseBonusId, BigDecimal rental) throws ManagerException {
		int result = 0;
		try {
			int del_result = leaseDetailManager.deleteByPrimaryKey(id);
			if (del_result > 0) {
				int count = leaseDetailManager.selectCountByLeaseBonusId(leaseBonusId);
				if (count == 0) {
					// 判断如果删除的是最后一条数据，则恢复租赁状态以及分红状态
					result = leaseBonusManager.updateStatusByLeaseBonusId(leaseBonusId);
				} else {
					// 如果删除的不是最后一条，则更新租赁分红总额
					result = leaseBonusManager.updateTotalIncomeByLeaseBonusId(rental, leaseBonusId);
				}
			}
		} catch (Exception e) {
			logger.error("删除租赁记录失败,id=" + id, e);
			throw new ManagerException(e);
		}
		return result;
	}

	/**
	 * 添加租赁记录
	 */
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<LeaseDetail> insertSelective(LeaseDetail leaseDetail) throws ManagerException {
		ResultDO<LeaseDetail> resultDO = new ResultDO<LeaseDetail>();
		try {
			int result = leaseDetailManager.insertSelective(leaseDetail);
			if (result > 0) {
				/**
				 * 修改租赁分红租赁状态为已租,修改租赁总额
				 */
				LeaseBonus leaseBonus = leaseBonusManager.selectByPrimaryKey(leaseDetail.getLeaseBonusId());
				if(leaseBonus!=null){
					leaseBonus.setLeaseStatus(StatusEnum.LEASE_BONUS_BEEN_LEASED.getStatus());
					leaseBonus.setTotalIncome(leaseDetail.getTotalRental());
					leaseBonusManager.updateForLeaseSettlement(leaseBonus);
				}

			} else {
				resultDO.setSuccess(false);
			}
		} catch (ManagerException e) {
			logger.error("插入租赁记录失败,leaseDetail=" + leaseDetail, e);
			throw new ManagerException(e);
		}
		return resultDO;
	}

	public LeaseDetail selectByPrimaryKey(Long id) {
		try {
			return leaseDetailManager.selectByPrimaryKey(id);
		} catch (ManagerException e) {
			logger.error("查询租赁记录失败,id=" + id, e);
		}
		return null;
	}

	public int updateByPrimaryKey(LeaseDetail leaseDetail) {
		try {
			return leaseDetailManager.updateByPrimaryKey(leaseDetail);
		} catch (ManagerException e) {
			logger.error("更新租赁记录失败,leaseDetail=" + leaseDetail, e);
		}
		return 0;
	}

	/**
	 * 更新租赁
	 */
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<LeaseDetail> updateByPrimaryKeySelective(LeaseDetail leaseDetail, BigDecimal oldTotalRental,
			BigDecimal newTotalRental) throws ManagerException {
		ResultDO<LeaseDetail> resultDO = new ResultDO<LeaseDetail>();
		try {
			int result = leaseDetailManager.updateByPrimaryKeySelective(leaseDetail);
			if (result > 0) {
				// 更新租赁分红表的租赁分红总额
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", leaseDetail.getLeaseBonusId());
				map.put("oldTotalRental", oldTotalRental);
				map.put("newTotalRental", newTotalRental);
				result = leaseBonusManager.updateTotalIncomeForEditLeaseDetail(map);
			} else {
				resultDO.setSuccess(false);
			}
		} catch (ManagerException e) {
			logger.error("按字段更新租赁记录失败,leaseDetail=" + leaseDetail, e);
		}
		return resultDO;
	}

	public int batchDelete(long[] ids) {
		try {
			return leaseDetailManager.batchDelete(ids);
		} catch (ManagerException e) {
			logger.error("批量删除租赁记录失败,ids=" + ids, e);
		}
		return 0;
	}

	public Page<LeaseDetail> findByPage(Page<LeaseDetail> pageRequest, Map<String, Object> map) {
		try {
			return leaseDetailManager.findByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("分页查询租赁记录失败,pageRequest=" + pageRequest + "map=" + map, e);
		}
		return pageRequest;
	}
}