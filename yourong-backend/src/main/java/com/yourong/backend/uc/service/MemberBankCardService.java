package com.yourong.backend.uc.service;

import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.uc.model.MemberBankCard;

public interface MemberBankCardService {
	
	/**
	 * 分页查询客户银行卡信息
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public Page<MemberBankCard> findByPage(Page<MemberBankCard> pageRequest, Map map);
	
	/**
	 *后台删除用户绑定银行卡功能
	 */
	public ResultDO<Object> deleteBankCard(Long id, String userIp);
	
	/**
	 *后台删除用户绑定银行卡功能(根据memberId)
	 */
	public ResultDO<Object> deleteBankCardByMemberId(Long id);
	
	/**
	 * 银行卡管理
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public Page<MemberBankCard> queryMemberCard(Page<MemberBankCard> pageRequest, Map map);

}
