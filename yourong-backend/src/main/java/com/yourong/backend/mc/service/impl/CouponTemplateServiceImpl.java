package com.yourong.backend.mc.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.yourong.backend.mc.service.CouponTemplateService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.CouponEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.manager.CouponTemplateManager;
import com.yourong.core.mc.manager.CouponTemplatePrintManager;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.mc.model.CouponTemplate;
import com.yourong.core.mc.model.CouponTemplatePrint;

@Service
public class CouponTemplateServiceImpl implements CouponTemplateService {

	private static Logger logger = LoggerFactory.getLogger(CouponTemplateServiceImpl.class);
	@Autowired
	private CouponTemplateManager couponTemplateManager;

	@Autowired
	private CouponTemplatePrintManager couponTemplatePrintManager;

	@Autowired
	private CouponManager couponManager;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	// 印刷单次批量插入个数
	private static final int PRINT_UNIT = 500;

	/**
	 * 优惠券模板分页列表
	 */
	@Override
	public Page<CouponTemplate> findByPage(Page<CouponTemplate> pageRequest, Map<String, Object> map) {
		try {
			return couponTemplateManager.findByPage(pageRequest, map);
		} catch (Exception e) {
			logger.error("优惠券分页列表获取失败", e);
		}
		return null;
	}

	/**
	 * 插入优惠券模板
	 */
	@Override
	public ResultDO<CouponTemplate> insertCouponTemplate(CouponTemplate couponTemplate) {
		ResultDO<CouponTemplate> resultDO = new ResultDO<CouponTemplate>();
		try {
			int insertResult = couponTemplateManager.insert(couponTemplate);
			if (insertResult <= 0) {
				resultDO.setSuccess(false);
			}
		} catch (Exception e) {
			logger.error("新增优惠券模板失败,couponTemplate=" + couponTemplate, e);
			resultDO.setResultCode(ResultCode.COUPONTEMPLATE_ADD_SAVE_FAIL_ERROR);
		}
		return resultDO;
	}

	/**
	 * 删除优惠券模板
	 */
	@Override
	public ResultDO<CouponTemplate> deleteByCouponTemplateId(Long id) {
		ResultDO<CouponTemplate> resultDO = new ResultDO<CouponTemplate>();
		try {
			int result = couponTemplateManager.deleteByCouponTemplateId(id);
			if (result <= 0) {
				resultDO.setSuccess(false);
			}
		} catch (Exception e) {
			logger.error("删除优惠券模板失败", e);
		}
		return resultDO;
	}

	/**
	 * 根据id获取优惠券模板
	 */
	@Override
	public CouponTemplate selectByPrimaryKey(Long id) {
		try {
			return couponTemplateManager.selectByPrimaryKey(id);
		} catch (Exception e) {
			logger.error("获取优惠券模板失败,id=" + id, e);
		}
		return null;
	}

	/**
	 * 优惠券模板更新
	 */
	@Override
	public ResultDO<CouponTemplate> update(CouponTemplate record) {
		ResultDO<CouponTemplate> resultDO = new ResultDO<CouponTemplate>();
		try {
			int result = couponTemplateManager.updateByPrimaryKeyPartSelective(record);
			if (result <= 0) {
				resultDO.setSuccess(false);
			}
		} catch (Exception e) {
			logger.error("更新优惠券模板失败,couponTemplate=" + record, e);
		}
		return resultDO;
	}

	/**
	 * 根据优惠券模板印刷
	 */
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<CouponTemplate> printCouponTemplate(CouponTemplatePrint couponTemplatePrint)
			throws ManagerException {
		ResultDO<CouponTemplate> result = new ResultDO<CouponTemplate>();
		try {
			if (couponTemplatePrint.getCouponTemplateId() != null) {
				// 根据优惠券模板id查询优惠券模板
				CouponTemplate couponTemplate = couponTemplateManager.selectByPrimaryKey(couponTemplatePrint
						.getCouponTemplateId());
				// 优惠券是否为现金券，现金券计算总金额
				if (CouponEnum.COUPONTEMPLATE_COUPON_TYPE_CASH.getCode().equals(
						couponTemplate.getCouponType().toString())) {
					BigDecimal totalAmount = getTotalAmount(couponTemplatePrint.getPrintNum(),
							couponTemplate.getAmount());
					couponTemplatePrint.setTotalAmount(totalAmount);
				} else {
					couponTemplatePrint.setTotalAmount(BigDecimal.ZERO);
				}
				// 插入印刷记录
				int insertResult = couponTemplatePrintManager.insertSelective(couponTemplatePrint);

				if (insertResult > 0) {
					if (couponTemplate.getPrintTimes() != null) {
						couponTemplate.setPrintTimes(1);
					}
					couponTemplate.setPrintTimes(couponTemplate.getPrintTimes());
					couponTemplate.setStatus(StatusEnum.COUPONTEMPLATE_STATUS_USED.getStatus());
					couponTemplateManager.updateByPrimaryKeySelective(couponTemplate);
					// 生成优惠券
					taskExecutor.execute(new GenerateCouponThread(couponTemplate, couponTemplatePrint));
				}
			} else {
				result.setSuccess(false);
			}
		} catch (Exception e) {
			logger.error("印刷优惠券失败,couponTemplatePrint=" + couponTemplatePrint, e);
			result.setResultCode(ResultCode.COUPONTEMPLATE_PRINT_FAIL_ERROR);
			throw new ManagerException(e);
		}
		return result;
	}

	/**
	 * 计算印刷的总额
	 * 
	 * @param printNum
	 * @param amount
	 * @return
	 */
	private BigDecimal getTotalAmount(Integer printNum, BigDecimal amount) {
		BigDecimal totalAmount = amount.multiply(new BigDecimal(printNum));
		return totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 生成优惠券线程
	 * 
	 * @param couponTemplate
	 * @param couponTemplatePrint
	 */
	private class GenerateCouponThread implements Runnable {
		private CouponTemplate couponTemplate;
		private CouponTemplatePrint couponTemplatePrint;

		public GenerateCouponThread(final CouponTemplate couponTemplate, final CouponTemplatePrint couponTemplatePrint) {
			this.couponTemplate = couponTemplate;
			this.couponTemplatePrint = couponTemplatePrint;
		}

		@Override
		public void run() {
			try {
				int printNum = couponTemplatePrint.getPrintNum();
				int printRemainder = printNum % PRINT_UNIT;
				int printUnitNum = printNum / PRINT_UNIT;
				if (printNum != 0) {
					if (printRemainder != 0) {
						printUnitNum = printUnitNum + 1;
					}
				}
				StopWatch allSw = new StopWatch();
				allSw.start();// allSw start
				for (int i = 0; i < printUnitNum; i++) {
					allSw.suspend();// allSw suspend
					List<Coupon> coupons = Lists.newArrayList();
					for (int j = 0; j < PRINT_UNIT; j++) {
						Coupon coupon = new Coupon();
						coupon.setCouponType(couponTemplate.getCouponType());// 优惠券类型
						String couponCode = null;
						if (CouponEnum.COUPONTEMPLATE_COUPON_TYPE_CASH.getCode().equals(
								couponTemplate.getCouponType().toString())) {
							couponCode = SerialNumberUtil.generateCouponANo(i * PRINT_UNIT + j);// 现金券编号
						}
						if (CouponEnum.COUPONTEMPLATE_COUPON_TYPE_INCOME.getCode().equals(
								couponTemplate.getCouponType().toString())) {
							couponCode = SerialNumberUtil.generateCouponBNo(i * PRINT_UNIT + j);// 收益权编号
						}
						coupon.setCouponCode(couponCode);
						coupon.setCouponTemplateId(couponTemplate.getId());
						coupon.setCouponTemplatePrintId(couponTemplatePrint.getId());
						coupon.setAmount(couponTemplate.getAmount());// 优惠券面额
						coupon.setVaildCalcType(couponTemplate.getVaildCalcType());
						coupon.setDays(couponTemplate.getDays());
						coupon.setWebScope(couponTemplate.getWebScope());
						coupon.setWapScope(couponTemplate.getWapScope());
						coupon.setAppScope(couponTemplate.getAppScope());
						coupon.setAmountScope(couponTemplate.getAmountScope());
						coupon.setDaysScope(couponTemplate.getDaysScope());
						coupon.setStartDate(couponTemplate.getStartDate());
						coupon.setEndDate(couponTemplate.getEndDate());
						coupon.setStatus(StatusEnum.COUPON_STATUS_NOT_RECEIVED.getStatus());
						coupon.setExtraInterestDay(couponTemplate.getExtraInterestDay());
						coupon.setExtraInterestType(couponTemplate.getExtraInterestType());
						// 默认全网通用
						// if
						// (couponTemplate.getAmountScope().compareTo(BigDecimal.ZERO)
						// == 0
						// && couponTemplate.getDaysScope() == 0) {
						// if(couponTemplate.getWebScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("全网通用");
						// }else if(couponTemplate.getWebScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•WAP、APP投资可用");
						// }else if(couponTemplate.getWebScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•APP投资可用");
						// }else if(couponTemplate.getWebScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•WEB、APP投资可用");
						// }else if(couponTemplate.getWebScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•WEB投资可用");
						// }else if(couponTemplate.getWebScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•WAP投资可用");
						// }
						// }else
						// if(couponTemplate.getAmountScope().compareTo(BigDecimal.ZERO)>0&&
						// couponTemplate.getDaysScope() == 0){
						// if(couponTemplate.getWebScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•投资金额>="+couponTemplate.getAmountScopeStr());
						// }else if(couponTemplate.getWebScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•WAP APP投资可用  •投资金额>="+couponTemplate.getAmountScopeStr());
						// }else if(couponTemplate.getWebScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•APP投资可用  •投资金额>="+couponTemplate.getAmountScopeStr());
						// }else if(couponTemplate.getWebScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•WEB APP投资可用  •投资金额>="+couponTemplate.getAmountScopeStr());
						// }else if(couponTemplate.getWebScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•WEB投资可用  •投资金额>="+couponTemplate.getAmountScopeStr());
						// }else if(couponTemplate.getWebScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•WAP投资可用  •投资金额>="+couponTemplate.getAmountScopeStr());
						// }
						// }else
						// if(couponTemplate.getAmountScope().compareTo(BigDecimal.ZERO)>0&&
						// couponTemplate.getDaysScope() > 0){
						// if(couponTemplate.getWebScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•投资金额>="+couponTemplate.getAmountScopeStr()+" •投资期限>="+couponTemplate.getDaysScope());
						// }else if(couponTemplate.getWebScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•WAP APP投资可用  •投资金额>="+couponTemplate.getAmountScopeStr()+" •投资期限>="+couponTemplate.getDaysScope());
						// }else if(couponTemplate.getWebScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•APP投资可用  •投资金额>="+couponTemplate.getAmountScopeStr()+" •投资期限>="+couponTemplate.getDaysScope());
						// }else if(couponTemplate.getWebScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•WEB APP投资可用  •投资金额>="+couponTemplate.getAmountScopeStr()+" •投资期限>="+couponTemplate.getDaysScope());
						// }else if(couponTemplate.getWebScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•WEB投资可用  •投资金额>="+couponTemplate.getAmountScopeStr()+" •投资期限>="+couponTemplate.getDaysScope());
						// }else if(couponTemplate.getWebScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•WAP投资可用  •投资金额>="+couponTemplate.getAmountScopeStr()+" •投资期限>="+couponTemplate.getDaysScope());
						// }
						// }else
						// if(couponTemplate.getAmountScope().compareTo(BigDecimal.ZERO)==0&&
						// couponTemplate.getDaysScope() > 0){
						// if(couponTemplate.getWebScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•投资期限>="+couponTemplate.getDaysScope());
						// }else if(couponTemplate.getWebScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•投资期限>="+couponTemplate.getDaysScope());
						// }else if(couponTemplate.getWebScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•投资期限>="+couponTemplate.getDaysScope());
						// }else if(couponTemplate.getWebScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•投资期限>="+couponTemplate.getDaysScope());
						// }else if(couponTemplate.getWebScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•投资期限>="+couponTemplate.getDaysScope());
						// }else if(couponTemplate.getWebScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getWapScope() ==
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
						// && couponTemplate.getAppScope() !=
						// StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
						// coupon.setUseCondition("•投资期限>="+couponTemplate.getDaysScope());
						// }
						// }
						if (couponTemplate.getAmountScope().compareTo(BigDecimal.ZERO) == 0
								&& couponTemplate.getDaysScope() == 0
								&& couponTemplate.getWebScope() == StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
								&& couponTemplate.getWapScope() == StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()
								&& couponTemplate.getAppScope() == StatusEnum.COUPON_CLIENT_SUPPORT.getStatus()){
							coupon.setUseCondition("全网通用");
						}
							coupons.add(coupon);
						if ((i * PRINT_UNIT + j + 1) == printNum) {
							break;
						}
					}
					allSw.resume();// allSw resume
					StopWatch st = new StopWatch();
					st.start();// st start
					couponManager.batchInsertCoupon(coupons);
					st.stop();// st stop
					logger.info("印刷" + couponTemplatePrint.getPrintNum() + "张优惠券,第" + (i + 1) + "次，耗费时间="
							+ st.getTime());
				}
				allSw.stop();// allSw stop
				logger.info("印刷" + couponTemplatePrint.getPrintNum() + "张优惠券，批量单次打印" + PRINT_UNIT + ",耗费总时间="
						+ allSw.getTime());
			} catch (Exception e) {
				logger.error("根据优惠券模板印刷优惠券失败,coupon=" + couponTemplatePrint.getId(), e);
			}
		}

	}

	@Override
	public int updateSort(Date sorttime,Long id) {
		return couponTemplateManager.updateSort(sorttime,id);
	}
}
