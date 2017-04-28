package com.yourong.backend.ic.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.ic.service.LeaseBonusDetailService;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.manager.LeaseBonusDetailManager;
import com.yourong.core.ic.model.LeaseBonusDetail;

@Service
public class LeaseBonusDetailServiceImpl implements LeaseBonusDetailService {
	private static Logger logger = LoggerFactory.getLogger(LeaseBonusDetailServiceImpl.class);
	
	@Autowired
	private LeaseBonusDetailManager leaseBonusDetailManager;

	public int deleteByPrimaryKey(Long id) {
		try {
			return leaseBonusDetailManager.deleteByPrimaryKey(id);
		} catch (Exception e) {
			logger.error("删除分红明细失败,id=" + id, e);
		}
		return 0;
	}

	public int insert(LeaseBonusDetail leaseBonusDetail) {
		try {
			return leaseBonusDetailManager.insert(leaseBonusDetail);
		} catch (Exception e) {
			logger.error("插入分红明细失败,leaseBonusDetail=" + leaseBonusDetail, e);
		}
		return 0;
	}

	public LeaseBonusDetail selectByPrimaryKey(Long id) {
		try {
			return leaseBonusDetailManager.selectByPrimaryKey(id);
		} catch (Exception e) {
			logger.error("查询分红明细失败,id=" + id, e);
		}
		return null;
	}

	public int updateByPrimaryKey(LeaseBonusDetail leaseBonusDetail) {
		try {
			return leaseBonusDetailManager.updateByPrimaryKey(leaseBonusDetail);
		} catch (ManagerException e) {
			logger.error("更新分红明细失败,leaseBonusDetail=" + leaseBonusDetail, e);
		}
		return 0;
	}

	public int updateByPrimaryKeySelective(LeaseBonusDetail leaseBonusDetail) {
		try {
			return leaseBonusDetailManager
					.updateByPrimaryKeySelective(leaseBonusDetail);
		} catch (ManagerException e) {
			logger.error("按字段更新分红明细失败,leaseBonusDetail=" + leaseBonusDetail, e);
		}
		return 0;
	}

	public int batchDelete(long[] ids) {
		try {
			return leaseBonusDetailManager.batchDelete(ids);
		} catch (ManagerException e) {
			logger.error("批量删除分红明细失败,ids=" + ids, e);
		}
		return 0;
	}

	public Page<LeaseBonusDetail> findByPage(
			Page<LeaseBonusDetail> pageRequest, Map<String, Object> map) {
		try {
			return leaseBonusDetailManager.findByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("分页查询分红明细失败,pageRequest=" + pageRequest + "map=" + map , e);
		}
		return null;
	}
}