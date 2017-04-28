package com.yourong.api.service;

import java.util.List;

import com.yourong.api.dto.CapitalInOutLogDto;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.CapitalInOutLog;
import com.yourong.core.fin.model.biz.MemberSavingPotBiz;
import com.yourong.core.fin.model.query.CapitalQuery;

public interface CapitalInOutLogService {
	//int insert(CapitalInOutLog record);
	
	List<CapitalInOutLog> selectEaring(Long memberId, int payAccountType, int type, int length) ;
	
	/**
	 * 获取我的存钱罐信息
	 * @param memberId
	 * @return
	 */
	public ResultDO<MemberSavingPotBiz> getMemberSavingPotBiz(Long memberId);
	
	/**
	 * 用户资金流水
	 * @param capitalQuery
	 * @return
	 */
	public Page<CapitalInOutLogDto> queryCapitalInOutLogList(CapitalQuery capitalQuery);
	
	/**
	 * 用户资金流水
	 * @param capitalQuery
	 * @return
	 */
	public Page<CapitalInOutLogDto> p2pQueryCapitalInOutLogList(CapitalQuery capitalQuery);
}
