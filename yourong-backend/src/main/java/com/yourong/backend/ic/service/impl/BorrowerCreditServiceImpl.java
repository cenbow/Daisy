package com.yourong.backend.ic.service.impl;

import java.util.List;
import java.util.Map;

import com.yourong.core.ic.manager.BorrowerCreditGradeManager;
import com.yourong.core.ic.model.BorrowerCreditGrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.ic.service.BorrowerCreditService;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.StringUtil;
import com.yourong.core.ic.dao.BorrowerCreditMapper;
import com.yourong.core.ic.model.BorrowerCredit;
import com.yourong.core.sys.dao.SysDictMapper;

@Service
public class BorrowerCreditServiceImpl implements BorrowerCreditService {
	
	private static Logger logger = LoggerFactory.getLogger(BorrowerCreditServiceImpl.class);
	
	@Autowired
	private BorrowerCreditMapper borrowerCreditMapper;
	
	@Autowired
	private SysDictMapper sysDictMapper;

	@Autowired
	private BorrowerCreditGradeManager borrowerCreditGradeManager;

	@Override
	public int saveBorrower(BorrowerCredit borrowerCredit) {
		try{
			return borrowerCreditMapper.insert(borrowerCredit);
		} catch(Exception e){
			logger.error("保存借款人授信额度信息失败",e);
		}
		return 0;
	}

	@Override
	public int updateById(BorrowerCredit borrowerCredit) {
		try{
			return borrowerCreditMapper.updateById(borrowerCredit);
		} catch(Exception e){
			logger.error("更新借款人授信额度信息失败",e);
		}
		return 0;

	}
	
	@Override
	public int updateByBorrower(BorrowerCredit borrowerCredit) {
		try{
			return borrowerCreditMapper.updateByBorrower(borrowerCredit);
		} catch(Exception e){
			logger.error("根据借款人信息修改借款人授信额度信息失败",e);
		}
		return 0;

	}
	
	@Override
	public BorrowerCredit selectById(Long id) {
		try{
			return borrowerCreditMapper.selectById(id);
		} catch(Exception e){
			logger.error("查询借款人授信额度信息失败",e);
		}
		return null;
	}
	
	@Override
	public BorrowerCredit selectByBorrower(Long borrowerId, Integer borrowerType, String openPlatformKey, Integer investType) {
		try{
			BorrowerCredit borrowerCredit = new BorrowerCredit();
			borrowerCredit.setInvestType(investType);
			borrowerCredit.setBorrowerType(borrowerType);
			if (StringUtil.isNotBlank(openPlatformKey)) {
				borrowerCredit.setOpenPlatformKey(openPlatformKey);
			} else {
				borrowerCredit.setBorrowerId(borrowerId);
			}
			BorrowerCredit result=borrowerCreditMapper.selectByBorrower(borrowerCredit);
			BorrowerCreditGrade borrowerCreditGrade= borrowerCreditGradeManager.queryByBorrowerId(borrowerId);
			if (borrowerCreditGrade!=null){
				if (result==null){
					result=new BorrowerCredit();
				}
				result.setCreditLevel(borrowerCreditGrade.getCreditLevel());
				result.setCreditLevelDes(borrowerCreditGrade.getCreditLevelDes());
				return result;
			}
			return result;
		} catch(Exception e){
			logger.error("根据借款人信息查询借款人授信额度信息失败",e);
		}
		return null;
	}
	
	@Override
	public Page<BorrowerCredit> queryBorrowerCreditByPage(Page<BorrowerCredit> pageRequest, Map<String, Object> map) {
		try {
			Page<BorrowerCredit> page = new Page<BorrowerCredit>();
			map.put("startRow", pageRequest.getiDisplayStart());
			map.put("pageSize", pageRequest.getiDisplayLength());
			List<BorrowerCredit> borrowerCreditList = borrowerCreditMapper.queryBorrowerCredit(map);
			int totalCount = borrowerCreditMapper.queryBorrowerCreditCount(map);
			page.setData(borrowerCreditList);
			page.setiTotalDisplayRecords(totalCount);
			page.setiTotalRecords(totalCount);
			
			return page;
		} catch(Exception e){
			logger.error("BorrowerCredit:findByPage", e);
		}
		return null;
	}
	
}
