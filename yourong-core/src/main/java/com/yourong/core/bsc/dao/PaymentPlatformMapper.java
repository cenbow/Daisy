package com.yourong.core.bsc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yourong.common.pageable.Page;
import com.yourong.core.bsc.model.PaymentPlatform;

@Repository
public interface PaymentPlatformMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(PaymentPlatform record);

    int insertSelective(PaymentPlatform record);

    PaymentPlatform selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PaymentPlatform record);

    int updateByPrimaryKeyWithBLOBs(PaymentPlatform record);

    int updateByPrimaryKey(PaymentPlatform record);
    
	Page<PaymentPlatform> findByPage(Page<PaymentPlatform> pageRequest, @Param("map") Map<String, Object> map);

	int delPaymentPlatform(Long id);
	
	/**
	 * 查找指定支付限额
	 * @param record
	 * @return
	 */
	PaymentPlatform findPaymentLimit(PaymentPlatform record);
	
	/**
	 * 保存维护公告
	 * @return
	 */
	int saveMaintenanceById(PaymentPlatform record);
	
	/**
	 * 结束维护
	 * @param id
	 * @return
	 */
	int delMaintence(Long id);
	
	/**
	 * 查询处于维护状态但是仍未被挂起的支付方式
	 * @return
	 */
	List<PaymentPlatform> selectFlushStatusOff();
	
	/**
	 * 挂起处于维护状态的支付方式
	 * @return
	 */
	int autoFlushStatusOff();
	
	/**
	 * 查询不在维护状态但是仍未被激活的支付方式
	 * @return
	 */
	List<PaymentPlatform> selectFlushStatusOn();
	
	/**
	 * 激活在维护状态但是仍未被激活的支付方式
	 * @return
	 */
	int autoFlushStatusOn();
	
	/**
	 * 查询银行支付限额
	 * @param paymentPlatform
	 * @return
	 */
	PaymentPlatform queryPlatformLimit(PaymentPlatform paymentPlatform);
	
	/**
	 * 查询所有银行限额信息
	 * @return
	 */
	List<PaymentPlatform> selectAllPaymentPlatform();
}