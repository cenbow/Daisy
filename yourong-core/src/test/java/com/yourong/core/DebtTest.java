package com.yourong.core;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.DateUtils;
import com.yourong.core.fin.dao.LoanDetailMapper;
import com.yourong.core.fin.model.LoanDetail;
import com.yourong.core.ic.dao.DebtCollateralMapper;
import com.yourong.core.ic.dao.DebtInterestMapper;
import com.yourong.core.ic.dao.DebtMapper;
import com.yourong.core.ic.dao.DebtPledgeMapper;
import com.yourong.core.ic.dao.DebtTransferMapper;
import com.yourong.core.ic.manager.DebtInterestManager;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.DebtTransfer;
import com.yourong.core.uc.dao.MemberMapper;

public class DebtTest extends BaseTest {

//	
//	@Autowired
//	private DebtMapper debtMapper;
//	@Autowired
//	private DebtCollateralMapper debtCollateralMapper;
//	@Autowired
//	private DebtPledgeMapper debtPledgeMapper;
	@Autowired
	private DebtInterestMapper debtInterestMapper;
//	@Autowired
//	private DebtTransferMapper debtTransferMapper;
//	@Autowired
//	private MemberMapper memberMapper;
	
	@Autowired
	private LoanDetailMapper loanDetailMapper;
	@Autowired
	private DebtInterestManager debtInterestManager;

//	@Test
//	@Rollback(false)
//	public void getDebtBizTest(){
//		int a = debtPledgeMapper.deletePledgeByDebtId(Long.valueOf("9999000014"));
//		System.out.println(a);
//	}
	
//	@Test
//	@Rollback(false)
//	public void getAttrachmentsByDebtIdTest(){
//		DebtTransfer debtTransfer = new DebtTransfer();
//		debtTransfer.setAmount(BigDecimal.ZERO);
//		debtTransfer.setAnnualizedRate(new BigDecimal("2.30"));
//		debtTransfer.setOwnerId(Long.valueOf("110800000014"));
//		debtTransfer.setDebtId(Long.valueOf("9999000013"));
//		debtTransfer.setStartDate(DateUtils.getCurrentDate());
//		debtTransfer.setTransferTime(DateUtils.getCurrentDate());
//		debtTransfer.setEndDate(DateUtils.getCurrentDate());
//		debtTransferMapper.insertSelective(debtTransfer);
//	}
	//@Test
	@Rollback(false)
	public void insertLoanDetail() {
		LoanDetail loanDetail = new LoanDetail();
		loanDetail.setLoanAmount(BigDecimal.TEN);
		loanDetail.setPlatformPay(BigDecimal.ONE);
		loanDetail.setUserPay(BigDecimal.TEN);
		loanDetail.setProjectId(Long.valueOf("989800005"));
		loanDetailMapper.insertSelective(loanDetail);
	}
	/**
	 * 根据垫资记录状态为  未垫资 和 本息时间 ，状态  查询出 本息记录
	 */
//	@Test
	@Rollback(false)
	public void findUnderWriteDebtInterestTest() {
		 List<DebtInterest> debtList=debtInterestMapper.findUnderWriteDebtInterest();
		 System.out.println(debtList.toString());
	}
	
	@Test
	@Rollback(false)
	public void debtInterestTest(){
		try {
			debtInterestManager.generateGeneralOverdue();
			System.out.println("dfasd");
		} catch (ManagerException e) {
		}
		
	}
}
