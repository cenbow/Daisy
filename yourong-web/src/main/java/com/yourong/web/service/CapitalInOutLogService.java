package com.yourong.web.service;

import java.util.List;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.CapitalInOutLog;
import com.yourong.core.fin.model.biz.CapitalInOutForMemberCenter;
import com.yourong.core.fin.model.biz.MemberSavingPotBiz;
import com.yourong.core.fin.model.query.CapitalInOutLogQuery;
import com.yourong.core.fin.model.query.CapitalQuery;

public interface CapitalInOutLogService {
//	int insert(CapitalInOutLog record);
	
	List<CapitalInOutLog> selectEaring(Long memberId, int payAccountType, int type, int length) ;
	
	/**
	 * 获取我的存钱罐信息
	 * @param memberId
	 * @return
	 */
	public ResultDO<MemberSavingPotBiz> getMemberSavingPotBiz(Long memberId);
	
	public Page<CapitalInOutForMemberCenter> findCapitalPageForAcountCenter(CapitalQuery capitalQuery);

	List<CapitalInOutLog> getTotalTransferAmountForMemberTransfer(
			CapitalInOutLogQuery query)throws ManagerException;
}
