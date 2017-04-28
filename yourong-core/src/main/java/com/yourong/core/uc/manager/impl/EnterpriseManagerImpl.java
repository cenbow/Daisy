package com.yourong.core.uc.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.yourong.common.constant.Constant;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.dao.DebtMapper;
import com.yourong.core.ic.dao.ProjectMapper;
import com.yourong.core.uc.dao.EnterpriseMapper;
import com.yourong.core.uc.dao.MemberMapper;
import com.yourong.core.uc.manager.EnterpriseManager;
import com.yourong.core.uc.model.Enterprise;
import com.yourong.core.uc.model.Member;

/**
 * Created by Administrator on 2015/1/6.
 */
@Component
public class EnterpriseManagerImpl implements EnterpriseManager {
    @Autowired
    private  EnterpriseMapper  enterpriseMapper;

    @Autowired
    private MemberMapper  memberMapper;
    @Autowired
	private BalanceManager balanceManager;
    
    @Autowired
   	private DebtMapper debtMapper;
    
    @Autowired
	private ProjectMapper projectMapper;

    @Override
    public int updateByPrimaryKeySelective(Enterprise record) {
        return this.enterpriseMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int insertSelective(Enterprise record) {
    	Member member=memberMapper.selectByPrimaryKey(record.getLegalId());
    	if(member!=null){
    		if(member.getMemberType()==1&&record.getType()==1){
    			 memberMapper.updateMemberTypeSatusByID(record.getLegalId());
    		}else if(member.getMemberType()==1&&record.getType()==2){
    			 memberMapper.updateMemberTypeByMemberId(record.getLegalId());
    		}
    	}
        return this.enterpriseMapper.insertSelective(record);

    }

    @Override
    public Enterprise selectByKey(Long id) {
        return this.enterpriseMapper.selectByPrimaryKey(id);
    }

    @Override
    public Enterprise selectByMemberID(Long memberID) {
        return this.enterpriseMapper.selectByMemberID(memberID);
    }
    
    @Override
    public Enterprise selectYRW() {
        return this.enterpriseMapper.selectYRW();
    }
    
    /**
     * 根据企业名称查询企业
     */
	@Override
	public List<Enterprise> getEnterpriseByName(String name) throws ManagerException{
		try {
			List<Enterprise> enterprises = enterpriseMapper.getEnterpriseByName(name);
			return enterprises;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		
	}
    
    
    /**
     * 根据企业名称查询企业
     */
	@Override
	public List<Enterprise> getEnterpriseByLegalName(String legalname) throws ManagerException{
		try {
			List<Enterprise> enterprises = enterpriseMapper.getEnterpriseByLegalName(legalname);
			return enterprises;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		
	}

	@Override
	public Page<Enterprise> findByPage(Page<Enterprise> pageRequest, Map<String, Object> map) throws ManagerException {
		try{
			map.put(Constant.STARTROW, pageRequest.getiDisplayStart());
			map.put(Constant.PAGESIZE, pageRequest.getiDisplayLength());
			int totalCount = enterpriseMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<Enterprise> selectForPagin = enterpriseMapper.selectForPagin(map);
			if(Collections3.isNotEmpty(selectForPagin)){
				selectForPagin=findEnterpriseinfo(selectForPagin);
			}
			pageRequest.setData(selectForPagin);			
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	private List<Enterprise> findEnterpriseinfo(List<Enterprise> selectForPagin) throws ManagerException {
		List<Enterprise> list =Lists.newArrayList();
		for(Enterprise enter:selectForPagin){
			Balance balance = balanceManager.queryBalance(enter.getId(), TypeEnum.BALANCE_TYPE_COMPANY_CREDIT_AMOUNT);
			if(balance!=null){
				enter.setCreditAmount(balance.getBalance());
			}
			list.add(enter);
		}
		return list;
	}

	@Override
	public int deleteByPrimaryKey(Long enterpriseID) throws ManagerException {
		try {
			return enterpriseMapper.deleteByPrimaryKey(enterpriseID);
		} catch (Exception e) {
			throw new ManagerException(e);
		}

	}

	@Override
	public boolean checkIfUse(Long enterpriseID) {
		int i=debtMapper.countDebtByEnterpriseId(enterpriseID);
		int j=debtMapper.countDebtByLenderEnterpriseId(enterpriseID);
		int k=projectMapper.countProjectByEnterpriseId(enterpriseID);
		if(i>0){
			return true;
		}
		if(j>0){
			return true;
		}
		if(k>0){
			return true;
		}
		return false;
	}

	@Override
	public List<Enterprise> getEnterpriseByRegisNo(String regisNo) throws ManagerException {
		try {
			List<Enterprise> enterprises = enterpriseMapper.getEnterpriseByRegisNo(regisNo);
			return enterprises;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		
	}
}
