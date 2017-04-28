package com.yourong.core;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.DateUtils;
import com.yourong.core.fin.manager.OverdueLogManager;
import com.yourong.core.ic.manager.DebtInterestManager;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.mc.dao.CouponMapper;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.Coupon;

public class CouponTemplateTest extends BaseTest {

	@Autowired
	private CouponMapper couponMapper;
	
	@Autowired
	private CouponManager couponManager;

	@Autowired
	private OverdueLogManager overdueLogManager;
	
	@Autowired
	private DebtInterestManager debtInterestManager;
	//@Test
	@Rollback(false)
	public void getCouponInsert() {
		List<Coupon> coupons = Lists.newArrayList();
		Coupon coupon = new Coupon();	
		coupon.setCouponCode("KKDJJDUSJJSJM");
		coupon.setCouponTemplateId(Long.valueOf("1"));
		coupon.setAmount(new BigDecimal("121"));
		coupon.setCouponType(1);
		coupon.setVaildCalcType(0);
		coupon.setStatus(0);
		coupons.add(coupon);
		couponMapper.batchInsertCoupon(coupons);
	}
	
//	@Test
	@Rollback(false)
	public void receiveCoupon() {
	}
	
	//@Test
	@Rollback(false)
	public void insertOverdue(){
//		try {
//			int v= overdueLogManager.insertOverdue(989800456L, 936L, 936L, 936L, new BigDecimal(1000), new BigDecimal(12), 0, DateUtils.getCurrentDate());
//			System.out.println(v);
//		} catch (ManagerException e) {
//			e.printStackTrace();
//		}
	}
	
	@Test
	@Rollback(false)
	public void findDebtInterest(){
		try {
			DebtInterest interest = debtInterestManager.findDebtInterestByEndDateAndProjectId(989800430L,DateUtils.getDateFromString("2016-04-26", DateUtils.DATE_FMT_3));
			System.out.println(interest.toString());
		} catch (ManagerException e) {
			e.printStackTrace();
		}
	}
}
