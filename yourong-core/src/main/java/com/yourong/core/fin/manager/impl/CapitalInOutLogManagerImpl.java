package com.yourong.core.fin.manager.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.dao.CapitalInOutLogMapper;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.fin.model.CapitalInOutLog;
import com.yourong.core.fin.model.biz.BonusBiz;
import com.yourong.core.fin.model.biz.CapitalInOutForMemberCenter;
import com.yourong.core.fin.model.query.CapitalInOutLogQuery;
import com.yourong.core.fin.model.query.CapitalQuery;
import com.yourong.core.ic.dao.DebtMapper;
import com.yourong.core.ic.dao.ProjectMapper;
import com.yourong.core.uc.dao.ThirdCompanyMapper;
@Component
public class CapitalInOutLogManagerImpl implements CapitalInOutLogManager{
	
	@Autowired
	private CapitalInOutLogMapper capitalInOutLogMapper;
	
	@Autowired
	private ProjectMapper projectMapper;
	
	@Autowired
	private ThirdCompanyMapper thirdCompanyMapper;
	
	@Autowired
	private DebtMapper debtMapper;
	

//	@Override
//	public int insert(CapitalInOutLog record) throws ManagerException {	
//		try{
//			return capitalInOutLogMapper.insert(record);
//		}catch(Exception e ){
//		    throw new ManagerException(e);	
//		}
//		
//	}

	@Override
	public int insert(Long memberId, TypeEnum type, BigDecimal income,
			BigDecimal outlay,BigDecimal balance, String sourceId, String remarks,TypeEnum payAccountType) {		
			CapitalInOutLog record = new CapitalInOutLog();
			record.setMemberId(memberId);
			record.setIncome(income);
			record.setOutlay(outlay);
			record.setSourceId(sourceId);
			record.setRemark(remarks);			
			record.setPayAccountType(payAccountType.getType());
			record.setType(type.getType());
			record.setBalance(balance);
			return capitalInOutLogMapper.insertSelective(record);
		
	}

//	@Override
//	public int insertSelective(CapitalInOutLog record) throws ManagerException {
//		try{
//			return capitalInOutLogMapper.insertSelective(record);
//		}catch(Exception e ){
//		    throw new ManagerException(e);	
//		}
//	}

	@Override
	public List<CapitalInOutLog> selectEaring(Long memberId, int payAccountType, int type, int length) throws ManagerException {
		try{
			return capitalInOutLogMapper.selectEaring(memberId, payAccountType, type, length);
		}catch(Exception e ){
		    throw new ManagerException(e);	
		}
	}

	@Override
	public List<BonusBiz> selectBonusByQuery(CapitalInOutLogQuery query)
			throws ManagerException {
		try{
			return capitalInOutLogMapper.selectBonusByQuery(query);
		}catch(Exception e ){
		    throw new ManagerException(e);	
		}
	}

	@Override
	public Page<CapitalInOutLog> findByPage(Page<CapitalInOutLog> pageRequest,
			Map<String, Object> map) throws ManagerException {
			Page<CapitalInOutLog> page = new Page<CapitalInOutLog>();
		try{
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			List<CapitalInOutLog> capitalInOutLogList=  capitalInOutLogMapper.selectFinCapitalInOutLogList(map);
			int totalCount = capitalInOutLogMapper.selectFinCapitalInOutLogListCount(map);
			page.setData(capitalInOutLogList);
			page.setiTotalDisplayRecords(totalCount);
			page.setiTotalRecords(totalCount);
			return page;
		}catch(Exception e ){
		    throw new ManagerException(e);	
		}
	}

	/**
	 * 我的账户资金流水分页数据
	 */
	@Override
	public Page<CapitalInOutForMemberCenter> findByPageForAccountCenter(CapitalQuery capitalQuery) throws ManagerException {
		try {
			List<CapitalInOutForMemberCenter> capitalInOutLogs = Lists.newArrayList();
			this.capitalInOutLogShield(capitalQuery);
			int count = capitalInOutLogMapper.selectForPaginCapitalInOutLogCount(capitalQuery);
			if(capitalQuery.getPageSize() <= 0){
				capitalQuery.setPageSize(count);
			}
			capitalInOutLogs = capitalInOutLogMapper.selectForPaginCapitalInOutLog(capitalQuery);
			Page<CapitalInOutForMemberCenter> page = new Page<CapitalInOutForMemberCenter>();
			page.setData(capitalInOutLogs);
			// 每页总数
			page.setiDisplayLength(capitalQuery.getPageSize());
			// 当前页
			page.setPageNo(capitalQuery.getCurrentPage());
			// 总数
			page.setiTotalDisplayRecords(count);
			page.setiTotalRecords(count);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	/**
	 * 我的账户资金流水分页数据
	 */
	@Override
	public Page<CapitalInOutForMemberCenter> p2pFindByPageForAccountCenter(CapitalQuery capitalQuery) throws ManagerException {
		try {
			List<CapitalInOutForMemberCenter> capitalInOutLogs = Lists.newArrayList();
			this.capitalInOutLogShield(capitalQuery);
			int count = capitalInOutLogMapper.p2pSelectForPaginCapitalInOutLogCount(capitalQuery);
			if(capitalQuery.getPageSize() <= 0){
				capitalQuery.setPageSize(count);
			}
			capitalInOutLogs = capitalInOutLogMapper.p2pSelectForPaginCapitalInOutLog(capitalQuery);
			Page<CapitalInOutForMemberCenter> page = new Page<CapitalInOutForMemberCenter>();
			page.setData(capitalInOutLogs);
			// 每页总数
			page.setiDisplayLength(capitalQuery.getPageSize());
			// 当前页
			page.setPageNo(capitalQuery.getCurrentPage());
			// 总数
			page.setiTotalDisplayRecords(count);
			page.setiTotalRecords(count);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	private void capitalInOutLogShield (CapitalQuery capitalQuery){
		//借款人
		Integer ifBorrewerNum = projectMapper.ifBorrower(capitalQuery.getMemberId());
		//垫资人
		Integer ifAdvancerNum = thirdCompanyMapper.ifAdvancer(capitalQuery.getMemberId());
		//原始债权人
		Integer ifOriginatorsNum = debtMapper.ifOriginators(capitalQuery.getMemberId());
		if(ifBorrewerNum!=null ||ifAdvancerNum!=null){
			capitalQuery.setLoaningRepayment(true);
		}
		if(ifBorrewerNum!=null ||ifOriginatorsNum!=null){
			capitalQuery.setProjectLoan(true);
			capitalQuery.setProjectRepayment(true);
		}
		
	}
	
	/**
	 * 出借人资金流水
	 */
	@Override
	public Page<CapitalInOutLog> findLenderCapitalInOutLogPage(Page<CapitalInOutLog> pageRequest,
			Map<String, Object> map) throws ManagerException {
			Page<CapitalInOutLog> page = new Page<CapitalInOutLog>();
		try{
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			List<CapitalInOutLog> capitalInOutLogList=  capitalInOutLogMapper.queryLenderLogByPage(map);
			int totalCount = capitalInOutLogMapper.queryLenderLogByPageCount(map);
			page.setData(capitalInOutLogList);
			page.setiTotalDisplayRecords(totalCount);
			page.setiTotalRecords(totalCount);
			return page;
		}catch(Exception e ){
		    throw new ManagerException(e);	
		}
	}

	@Override
	public BigDecimal queryTransferAmountByMemberId(Long memberId) throws ManagerException {
		try{
			return capitalInOutLogMapper.queryTransferAmountByMemberId(memberId);
		}catch(Exception e ){
		    throw new ManagerException(e);	
		}
	}
	
	@Override
	public int queryTransferAmountByMemberIdNum(Long memberId) throws ManagerException {
		try{
			return capitalInOutLogMapper.queryTransferAmountByMemberIdNum(memberId);
		}catch(Exception e ){
		    throw new ManagerException(e);	
		}
	}

	@Override
	public List<CapitalInOutLog> getTotalTransferAmountForMemberTransfer(
			CapitalInOutLogQuery query) throws ManagerException {
		try{
			return capitalInOutLogMapper.getTotalTransferAmountForMemberTransfer(query);
		}catch(Exception e ){
		    throw new ManagerException(e);	
		}
	}

}
