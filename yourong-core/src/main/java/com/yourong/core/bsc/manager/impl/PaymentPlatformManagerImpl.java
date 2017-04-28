package com.yourong.core.bsc.manager.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.enums.BankCode;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.core.bsc.dao.PaymentPlatformMapper;
import com.yourong.core.bsc.manager.PaymentPlatformManager;
import com.yourong.core.bsc.model.PaymentPlatform;

@Component
public class PaymentPlatformManagerImpl implements PaymentPlatformManager {

	private Logger logger = LoggerFactory.getLogger(PaymentPlatformManagerImpl.class);
	
	@Autowired
	private PaymentPlatformMapper paymentPlatformMapper;
	
	@Override
	public Page<PaymentPlatform> findByPage(Page<PaymentPlatform> pageRequest,
			Map<String, Object> map) throws ManagerException {
		try{
			return paymentPlatformMapper.findByPage(pageRequest, map);
		}catch(Exception e){
			throw new ManagerException(e);
		}
	}

	@Override
	public PaymentPlatform findPaymentLimit(PaymentPlatform record)
			throws ManagerException {
		try{
			return paymentPlatformMapper.findPaymentLimit(record);
		}catch(Exception e){
			throw new ManagerException(e);
		}
	}

	@Override
	public ResultDO<PaymentPlatform> save(PaymentPlatform paymentPlatform)
			throws ManagerException {
		ResultDO<PaymentPlatform> resultDO = new ResultDO<PaymentPlatform>();
		resultDO.setSuccess(false);
		try {
			if(paymentPlatform.getId() != null && paymentPlatform.getId() > 0) {
				//修改
				int retNum = paymentPlatformMapper.updateByPrimaryKeySelective(paymentPlatform);
				if(retNum == 1) {
					resultDO.setSuccess(true);
				}
			} else {
				//新增
				paymentPlatform.setDelFlag(1);
				PaymentPlatform obj = findPaymentLimit(paymentPlatform);
				if(obj == null) {
					int retNum = paymentPlatformMapper.insertSelective(paymentPlatform);
					if(retNum == 1) {
						resultDO.setSuccess(true);
					}
				} 
			}
		}catch(Exception e){
			throw new ManagerException(e);
		}
		return resultDO;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<PaymentPlatform> delPaymentPlatform(Long id)
			throws ManagerException {
		ResultDO<PaymentPlatform> resultDO = new ResultDO<PaymentPlatform>();
		resultDO.setSuccess(false);
		try {
			int retNum = paymentPlatformMapper.delPaymentPlatform(id);
			if(retNum == 1) {
				resultDO.setSuccess(true);
			} else {
				throw new ManagerException("删除失败，删除条数为"+retNum);
			}
		}catch(Exception e){
			throw new ManagerException(e);
		}
		return resultDO;
	}

	@Override
	public ResultDO<PaymentPlatform> saveMaintence(
			PaymentPlatform paymentPlatform) throws ManagerException {
		ResultDO<PaymentPlatform> resultDO = new ResultDO<PaymentPlatform>();
		resultDO.setSuccess(false);
		try {
			paymentPlatform.setId(paymentPlatform.getMaintenanceId());
			paymentPlatform.setStartTime(DateUtils.getDateTimeFromString(paymentPlatform.getStartTimeStr()));
			paymentPlatform.setEndTime(DateUtils.getDateTimeFromString(paymentPlatform.getEndTimeStr()));
			if(DateUtils.isDateBetween(DateUtils.getCurrentDate(), paymentPlatform.getStartTime(), paymentPlatform.getEndTime())) {
				paymentPlatform.setServiceStatus(0);
			} else {
				paymentPlatform.setServiceStatus(1);
			}
			int retNum = paymentPlatformMapper.saveMaintenanceById(paymentPlatform);
			if(retNum == 1) {
				resultDO.setSuccess(true);
			}
		}catch(Exception e){
			throw new ManagerException(e);
		}
		return resultDO;
	}

	@Override
	public ResultDO<PaymentPlatform> delMaintence(Long id)
			throws ManagerException {
		ResultDO<PaymentPlatform> resultDO = new ResultDO<PaymentPlatform>();
		resultDO.setSuccess(false);
		try {
			int retNum = paymentPlatformMapper.delMaintence(id);
			if(retNum == 1) {
				resultDO.setSuccess(true);
			}
		}catch(Exception e){
			throw new ManagerException(e);
		}
		return resultDO;
	}

	@Override
	public void flushStatus() throws ManagerException {
		try {
			List<PaymentPlatform> offList = paymentPlatformMapper.selectFlushStatusOff();
			if(CollectionUtils.isNotEmpty(offList)) {
				int retNum1 = paymentPlatformMapper.autoFlushStatusOff();
				logger.info("刷新支付平台状态定时任务，关闭{}个支付方式", retNum1);				
			}
			List<PaymentPlatform> onList = paymentPlatformMapper.selectFlushStatusOn();
			if(CollectionUtils.isNotEmpty(onList)) {
				int retNum2 = paymentPlatformMapper.autoFlushStatusOn();
				logger.info("刷新支付平台状态定时任务，开启{}个支付方式", retNum2);			
			}
		}catch(Exception e){
			throw new ManagerException(e);
		}
	}

	@Override
	public ResultDO<PaymentPlatform> queryPlatformLimit(PaymentPlatform record)
			throws ManagerException {
		ResultDO<PaymentPlatform> resultDO = new ResultDO<PaymentPlatform>();
		resultDO.setSuccess(false);
		try {
			PaymentPlatform res = paymentPlatformMapper.queryPlatformLimit(record);
			if(res != null) {
				String code = record.getCode();
				if(PropertiesUtil.isDev() && record.getTypeLimit() == 1) {
					//网银充值在开发环境显示TESTBANK
					res.setSimpleName("网银测试银行");
				} else {
					res.setSimpleName(BankCode.getBankCode(code).getRemarks());
				}
				if(StringUtil.isNotEmpty(res.getMaintenanceContent())) {
					if(!DateUtils.isDateBetween(DateUtils.getCurrentDate(), res.getStartTime(), res.getEndTime())) {
						res.setMaintenanceContent(null);
						res.setStartTime(null);
						res.setEndTime(null);
					}
				}
				resultDO.setResult(res);
				resultDO.setSuccess(true);
			}
		}catch(Exception e){
			throw new ManagerException(e);
		}
		return resultDO;
	}

	@Override
	public List<PaymentPlatform> selectAllPaymentPlatform()
			throws ManagerException {
		try {
			return paymentPlatformMapper.selectAllPaymentPlatform();
		}catch(Exception e){
			throw new ManagerException(e);
		}
	}
	
}
