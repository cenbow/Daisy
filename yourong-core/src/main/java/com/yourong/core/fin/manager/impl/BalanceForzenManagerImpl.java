package com.yourong.core.fin.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rop.thirdparty.com.google.common.collect.Lists;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.core.fin.dao.BalanceForzenMapper;
import com.yourong.core.fin.dao.BalanceUnforzenMapper;
import com.yourong.core.fin.manager.BalanceForzenManager;
import com.yourong.core.fin.model.BalanceForzen;
import com.yourong.core.fin.model.BalanceUnforzen;

@Component
public class BalanceForzenManagerImpl implements BalanceForzenManager {

	@Autowired
	private BalanceForzenMapper balanceForzenMapper;
	@Autowired
	private BalanceUnforzenMapper balanceUnforzenMapper;
	
	@Override
	public int insertSelective(BalanceForzen forzen) throws ManagerException {
		try {
			return balanceForzenMapper.insertSelective(forzen);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	
	@Override
	public int updateByPrimaryKeySelective(BalanceForzen forzen) throws ManagerException {
		try {
			return balanceForzenMapper.updateByPrimaryKeySelective(forzen);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	@Override
	public BalanceForzen selectBalanceForzenByForzenNo(String forzenNo) throws ManagerException {
		try {
			return balanceForzenMapper.selectBalanceForzenByForzenNo(forzenNo);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}

	/**
	 * 
	 * @desc 分页查询冻结记录
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年8月1日 上午10:43:00
	 *
	 */
	@Override
	public Page<BalanceForzen> showForzenPage(Page<BalanceForzen> pageRequest,
			Map<String, Object> map) throws ManagerException {
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			List<BalanceForzen> selectForPagin =Lists.newArrayList();
			int totalCount = balanceForzenMapper.selectForzenForPaginTotalCount(map);
			if(totalCount>0){
				List<BalanceForzen> forzenList = balanceForzenMapper.selectForzenForPagin(map);
				selectForPagin =  getForzenInfo(forzenList);
			}
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	private List<BalanceForzen> getForzenInfo(List<BalanceForzen> forzenList) {
		List<BalanceForzen> bizs = Lists.newArrayList();
		
		List<BalanceUnforzen> unbiz = Lists.newArrayList();
		if(Collections3.isEmpty(forzenList)){
			return bizs;
		}
		for(BalanceForzen biz:forzenList){
			unbiz=balanceUnforzenMapper.selectUnforzenListByForzenNo(biz.getForzenNo());
			if(Collections3.isNotEmpty(unbiz)&&unbiz.size()>0){
				biz.setUnforzenRecord(1);
			}
			List<BalanceUnforzen> unforzenList=balanceUnforzenMapper.selectUnforzenListByForzenNoAndProcessing(biz.getForzenNo());
			if(Collections3.isNotEmpty(unforzenList)&&unforzenList.size()>0){
				biz.setProcessRecord(1);
			}
			bizs.add(biz);
		}
		return bizs;
	}

	@Override
	public BalanceForzen selectByPrimayKey(Long id) throws ManagerException {
		try {
			return balanceForzenMapper.selectByPrimaryKey(id);
		} catch (Exception ex) {
			throw new ManagerException(ex);
		}
	}
	
}	