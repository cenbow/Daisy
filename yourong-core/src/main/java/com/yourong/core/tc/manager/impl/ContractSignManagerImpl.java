/**
 * 
 */
package com.yourong.core.tc.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rop.thirdparty.com.google.common.collect.Maps;

import com.yourong.common.enums.StatusEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.tc.dao.ContractSignMapper;
import com.yourong.core.tc.dao.TransactionMapper;
import com.yourong.core.tc.manager.ContractSignManager;
import com.yourong.core.tc.model.ContractSign;
import com.yourong.core.tc.model.ContractSignDto;

/**
 * @desc 合同签署信息
 * @author zhanghao
 * 2016年7月6日上午11:53:25
 */
@Component
public class ContractSignManagerImpl  implements ContractSignManager{

	

	@Autowired
	private ContractSignMapper contractSignMapper;
	
	@Autowired 
	private TransactionMapper transactionMapper;
	
	

	@Override
	public Page<ContractSignDto> findByPage(Page<ContractSignDto> pageRequest,
			Map<String, Object> map) throws ManagerException {
			Page<ContractSignDto> page = new Page<ContractSignDto>();
		try {
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			List<ContractSignDto> tranConList=  transactionMapper.getContractSignByMap(map);
			for(ContractSignDto con:  tranConList){

				Map<String,Object> querymap = Maps.newHashMap();
				querymap.put("transactionId", con.getId());
				querymap.put("type", StatusEnum.CONTRACT_PARTY_SECOND.getStatus());
				ContractSign contractSign  = this.selectByMap(querymap);
				if(contractSign==null){
					continue;
				}
				if(contractSign.getStatus()==StatusEnum.CONTRACT_SIGN_STATUS_SUCCESS.getStatus()){
					con.setSignTime(contractSign.getUpdateTime());
				}
			}
			
			int totalCount = transactionMapper.getContractSignByMapCount(map);
			page.setData(tranConList);
			page.setiTotalDisplayRecords(totalCount);
			page.setiTotalRecords(totalCount);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	
	@Override
	public int batchInsert(List<ContractSign> records) throws ManagerException {
		try {
			return contractSignMapper.batchInsert(records);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public ContractSign selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return contractSignMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public List<ContractSign> selectListByPrimaryKey(Map<String,Object> map) throws ManagerException {
		try {
			return contractSignMapper.selectListByPrimaryKey(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKey(ContractSign record) throws ManagerException {
		try {
			return contractSignMapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public ContractSign selectByMap(Map<String,Object> map) throws ManagerException {
		try {
			return contractSignMapper.selectByMap(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public int deletByTransactionId(Long transactionId) throws ManagerException {
		try {
			return contractSignMapper.deletByTransactionId(transactionId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
}
