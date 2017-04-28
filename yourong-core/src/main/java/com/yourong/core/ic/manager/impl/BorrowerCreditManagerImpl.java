package com.yourong.core.ic.manager.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.constant.Constant;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.StringUtil;
import com.yourong.core.ic.dao.BorrowerCreditMapper;
import com.yourong.core.ic.manager.BorrowerCreditManager;
import com.yourong.core.ic.model.BorrowerCredit;
import com.yourong.core.sys.dao.SysDictMapper;
import com.yourong.core.sys.model.SysDict;

@Component
public class BorrowerCreditManagerImpl implements BorrowerCreditManager {

	@Autowired
	private BorrowerCreditMapper borrowerCreditMapper;
	
	@Autowired
	private SysDictMapper sysDictMapper;

	@Override
	public BorrowerCredit selectByBorrower(Long borrowerId, Integer borrowerType, String openPlatformKey) throws ManagerException {
		try {
			BorrowerCredit borrowerCredit = new BorrowerCredit();
			borrowerCredit.setBorrowerType(borrowerType);
			if (StringUtil.isNotBlank(openPlatformKey)) {
				borrowerCredit.setOpenPlatformKey(openPlatformKey);
			} else {
				borrowerCredit.setBorrowerId(borrowerId);
			}
			return borrowerCreditMapper.selectByBorrower(borrowerCredit);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByBorrower(BorrowerCredit borrowerCredit) throws ManagerException {
		try {
			return borrowerCreditMapper.updateByBorrower(borrowerCredit);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int saveBorrower(BorrowerCredit borrowerCredit) throws ManagerException{
		try {
			return borrowerCreditMapper.insert(borrowerCredit);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BigDecimal getMemberOrEnterprisePayablePrincipal(Long borrowerId, Integer borrowerType, Integer investType) throws ManagerException {
		BigDecimal payablePrincipal = BigDecimal.ZERO;
		try {
			BorrowerCredit borrowerCredit = new BorrowerCredit();
			borrowerCredit.setBorrowerId(borrowerId);
			borrowerCredit.setBorrowerType(borrowerType);
			borrowerCredit.setInvestType(investType);
			borrowerCredit.setDefaultCreditAmount(Constant.DEFALUT_ZT_BORROWER_CREDIT);
			if(borrowerCreditMapper.getMemberOrEnterpriseBorrowerCredit(borrowerCredit) != null) {
				List<BorrowerCredit> borrowerCredits = borrowerCreditMapper.getMemberOrEnterpriseBorrowerCredit(borrowerCredit);
				if (!borrowerCredits.isEmpty()) {
					payablePrincipal = borrowerCredits.get(0).getPayablePrincipal();
				}
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return payablePrincipal;
	}
	
	@Override
	public BigDecimal getChannelPayablePrincipal(String openPlatformKey, Integer investType) throws ManagerException {
		BigDecimal payablePrincipal = BigDecimal.ZERO;
		try {
			if (StringUtil.isNotBlank(openPlatformKey)) {
				SysDict sysdict = sysDictMapper.selectByGroupNameAndValue(openPlatformKey);
				if(sysdict != null){
					openPlatformKey = sysdict.getKey();
				}
			}
			
			BorrowerCredit borrowerCredit = new BorrowerCredit();
			borrowerCredit.setOpenPlatformKey(openPlatformKey);
			borrowerCredit.setInvestType(investType);
			if (borrowerCreditMapper.getChannelBorrowerCredit(borrowerCredit) != null) {
				List<BorrowerCredit> borrowerCredits = borrowerCreditMapper.getChannelBorrowerCredit(borrowerCredit);
				if (!borrowerCredits.isEmpty()) {
					payablePrincipal = borrowerCredits.get(0).getPayablePrincipal();
				}
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return payablePrincipal;
	}

	@Override
	public void increaseBorrowerCredit(Long borrowerId, Integer borrowerType, String openPlatformKey, BigDecimal projectTotalAmount) throws ManagerException {
		try {
			if (StringUtil.isNotBlank(openPlatformKey)) { // 增加渠道商授信额度存续量
				BorrowerCredit borrowerCredit = selectByBorrower(null, 3, openPlatformKey);
				BigDecimal payablePrincipal = BigDecimal.ZERO;
				if (borrowerCredit != null && borrowerCredit.getPayablePrincipal() != null) {
					payablePrincipal = borrowerCredit.getPayablePrincipal().add(projectTotalAmount);
				} else {
					payablePrincipal = getChannelPayablePrincipal(openPlatformKey, ProjectEnum.PROJECT_TYPE_DIRECT.getType());
				}
				borrowerCredit.setPayablePrincipal(payablePrincipal);
				updateByBorrower(borrowerCredit);
			} else { // 增加个人用户/企业用户授信额度存续量
				BorrowerCredit borrowerCredit = selectByBorrower(borrowerId, borrowerType, null);
				BigDecimal payablePrincipal = BigDecimal.ZERO;
				if (borrowerCredit != null && borrowerCredit.getPayablePrincipal() != null) {
					payablePrincipal = borrowerCredit.getPayablePrincipal().add(projectTotalAmount);
				} else {
					payablePrincipal = getMemberOrEnterprisePayablePrincipal(borrowerId, borrowerType, ProjectEnum.PROJECT_TYPE_DIRECT.getType());
				}
				borrowerCredit.setPayablePrincipal(payablePrincipal);
				updateByBorrower(borrowerCredit);
			}
		} catch (ManagerException e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public void reduceBorrowerCredit(Long borrowerId, Integer borrowerType, String openPlatformKey, BigDecimal projectTotalAmount) throws ManagerException {
		try {
			if (StringUtil.isNotBlank(openPlatformKey)) { // 减少渠道商授信额度存续量
				BorrowerCredit borrowerCredit = selectByBorrower(null, 3, openPlatformKey);
				BigDecimal payablePrincipal = BigDecimal.ZERO;
				if (borrowerCredit != null && borrowerCredit.getPayablePrincipal() != null) {
					payablePrincipal = borrowerCredit.getPayablePrincipal().subtract(projectTotalAmount);
				} else {
					payablePrincipal = getChannelPayablePrincipal(openPlatformKey, ProjectEnum.PROJECT_TYPE_DIRECT.getType());
				}
				borrowerCredit.setPayablePrincipal(payablePrincipal);
				updateByBorrower(borrowerCredit);
			} else { // 减少个人用户/企业用户授信额度存续量
				BorrowerCredit borrowerCredit = selectByBorrower(borrowerId, borrowerType, null);
				BigDecimal payablePrincipal = BigDecimal.ZERO;
				if (borrowerCredit != null && borrowerCredit.getPayablePrincipal() != null) {
					payablePrincipal = borrowerCredit.getPayablePrincipal().subtract(projectTotalAmount);
				} else {
					payablePrincipal = getMemberOrEnterprisePayablePrincipal(borrowerId, borrowerType, ProjectEnum.PROJECT_TYPE_DIRECT.getType());
				}
				borrowerCredit.setPayablePrincipal(payablePrincipal);
				updateByBorrower(borrowerCredit);
			}
		} catch (ManagerException e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<BorrowerCredit> getMemberOrEnterpriseBorrowerCredit(Long borrowerId, Integer borrowerType, Integer investType) throws ManagerException {
		try {
			BorrowerCredit borrowerCredit = new BorrowerCredit();
			borrowerCredit.setBorrowerId(borrowerId);
			borrowerCredit.setBorrowerType(borrowerType);
			borrowerCredit.setInvestType(investType);
			borrowerCredit.setDefaultCreditAmount(Constant.DEFALUT_ZT_BORROWER_CREDIT);
			
			return borrowerCreditMapper.getMemberOrEnterpriseBorrowerCredit(borrowerCredit);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<BorrowerCredit> getChannelBorrowerCredit(String openPlatformKey, Integer investType) throws ManagerException {
		try {
			if (StringUtil.isNotBlank(openPlatformKey)) {
				SysDict sysdict = sysDictMapper.selectByGroupNameAndValue(openPlatformKey);
				if(sysdict != null){
					openPlatformKey = sysdict.getKey();
				}
			}
			
			BorrowerCredit borrowerCredit = new BorrowerCredit();
			borrowerCredit.setOpenPlatformKey(openPlatformKey);
			borrowerCredit.setInvestType(investType);
		    return borrowerCreditMapper.getChannelBorrowerCredit(borrowerCredit);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
}
