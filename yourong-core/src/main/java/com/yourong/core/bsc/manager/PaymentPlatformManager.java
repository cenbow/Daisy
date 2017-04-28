package com.yourong.core.bsc.manager;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.bsc.model.PaymentPlatform;

@Component
public interface PaymentPlatformManager {

	/**
	 * 支付平台列表查询
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	Page<PaymentPlatform> findByPage(Page<PaymentPlatform> pageRequest, Map<String, Object> map) throws ManagerException;

	/**
	 * 保存
	 * @param paymentPlatform
	 * @return
	 */
	ResultDO<PaymentPlatform> save(PaymentPlatform paymentPlatform) throws ManagerException;
	
	/**
	 * 查找指定支付限额
	 * @param record
	 * @return
	 * @throws ManagerException
	 */
	PaymentPlatform findPaymentLimit(PaymentPlatform record) throws ManagerException;
	
	/**
	 * 删除 
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	ResultDO<PaymentPlatform> delPaymentPlatform(Long id) throws ManagerException;
	
	/**
	 * 保存公告
	 * @param id
	 * @return
	 */
	ResultDO<PaymentPlatform> saveMaintence(PaymentPlatform paymentPlatform) throws ManagerException;
	
	/**
	 * 结束维护
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	ResultDO<PaymentPlatform> delMaintence(Long id) throws ManagerException;
	
	/**
	 * 刷新支付平台状态定时任务
	 * @throws ManagerException
	 */
	void flushStatus() throws ManagerException;

	/**
	 * 查找指定银行限额
	 * @param record
	 * @return
	 * @throws ManagerException
	 */
	ResultDO<PaymentPlatform> queryPlatformLimit(PaymentPlatform record) throws ManagerException;
	
	/**
	 * 查询所有银行限额信息
	 * @return
	 * @throws ManagerException
	 */
	List<PaymentPlatform> selectAllPaymentPlatform() throws ManagerException;
}
