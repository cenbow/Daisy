package com.yourong.core.fin.manager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.CapitalInOutLog;
import com.yourong.core.fin.model.biz.BonusBiz;
import com.yourong.core.fin.model.biz.CapitalInOutForMemberCenter;
import com.yourong.core.fin.model.query.CapitalInOutLogQuery;
import com.yourong.core.fin.model.query.CapitalQuery;

public interface CapitalInOutLogManager {
	/**
	 * 插入资金流水
	 * @param record
	 * @return
	 * @throws ManagerException
	 */
//	int insert(CapitalInOutLog record) throws ManagerException;
	
	/**
	 * 插入资金流水
	 * @param record
	 * @return
	 * @throws ManagerException
	 */
//	int insertSelective(CapitalInOutLog record)throws ManagerException;
	 
	
	List<CapitalInOutLog> selectEaring(Long memberId, int payAccountType,int type, int length) throws ManagerException;

	public int insert(Long memberId, TypeEnum type, BigDecimal income, BigDecimal outlay,BigDecimal balance, String sourceId, String remarks,TypeEnum payAccountType);

	/**
	 * 查询存钱罐收益
	 * @param query
	 * @return
	 * @throws ManagerException
	 */
	List<BonusBiz> selectBonusByQuery(CapitalInOutLogQuery query) throws ManagerException;
	
	/**
     * 资金流水记录分页查询
     * @param pageRequest
     * @param map
     * @return
     * @throws ManagerException
     */
    Page<CapitalInOutLog> findByPage(Page<CapitalInOutLog> pageRequest, Map<String, Object> map) throws ManagerException;
    
    /**
	 * 我的账户资金流水分页数据
	 * @param capitalQuery
	 * @return
	 */
	Page<CapitalInOutForMemberCenter> findByPageForAccountCenter(CapitalQuery capitalQuery) throws ManagerException;
	
	
	/**
	 * 我的账户资金流水分页数据，剔除P2P数据
	 * @param capitalQuery
	 * @return
	 */
	Page<CapitalInOutForMemberCenter> p2pFindByPageForAccountCenter(CapitalQuery capitalQuery) throws ManagerException ;
	
	
	/**
	 * 出借人资金流水
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	public Page<CapitalInOutLog> findLenderCapitalInOutLogPage(Page<CapitalInOutLog> pageRequest,
			Map<String, Object> map) throws ManagerException;

	BigDecimal queryTransferAmountByMemberId(Long memberId)throws ManagerException;

	int queryTransferAmountByMemberIdNum(Long memberId) throws ManagerException;

	List<CapitalInOutLog> getTotalTransferAmountForMemberTransfer(
			CapitalInOutLogQuery query)throws ManagerException;
	
}
