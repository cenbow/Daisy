package com.yourong.backend.jobs;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.core.common.MessageClient;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.Coupon;

/**
 * 定时优惠券过期
 * @author Administrator
 *
 */
public class CoponAutoExpireTask {

	@Resource
	private CouponManager couponManager;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	private static final Logger logger = LoggerFactory
			.getLogger(CoponAutoExpireTask.class);

	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("定时优惠券过期 start");
					expireCouponTask();
					logger.info("定时优惠券过期 end");
				} catch (Exception e) {
					logger.error("定时把优惠券状态设置为已过期异常：", e);
				}
			}
		});
	}

	/**
	 * 已过期优惠券
	 * @throws ManagerException
	 */
	private void expireCouponTask() throws ManagerException {
		List<Coupon> couponList = couponManager.getExpiringCoupons();
		int expireNum = couponManager.expireCouponTask();
		Date expiredDate = DateUtils.addDate(DateUtils.getCurrentDate(), 3);
		if(Collections3.isNotEmpty(couponList)){
			for(Coupon coupon : couponList){
				MessageClient.sendMsgForCouponExpired(coupon.getAmount(), expiredDate, coupon.getCouponType(), coupon.getHolderId());
			}
		}
		logger.info("定时把优惠券状态设置为已过期数量：" + expireNum);
	}

}
