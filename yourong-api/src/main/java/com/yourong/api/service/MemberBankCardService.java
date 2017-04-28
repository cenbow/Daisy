package com.yourong.api.service;

import java.util.List;

import com.yourong.api.dto.BindSecurityBankCardDto;
import com.yourong.api.dto.MemberNonSecurityBankCardDto;
import com.yourong.api.dto.SimpleMemberBankCardDto;
import com.yourong.common.domain.ResultDO;
import com.yourong.core.uc.model.MemberBankCard;

public interface MemberBankCardService {

	/**
	 * 添加银行卡
	 * 
	 * @param memberBankCard
	 * @return
	 */
	public ResultDO<Object> addMemberBankCard(MemberBankCard memberBankCard)throws Exception;


	/**
	 * 绑定银行卡 ,向第三方支付发送绑定银行卡请求
	 *
	 * @param memberBankCard
	 * @return
	 */
	public ResultDO<Object> sendThirdPayCheckBlankCode(MemberBankCard memberBankCard);
	/**
	 * 2绑定银行卡 ,推进
	 * 绑定成功后，添加银行卡
	 * @param memberBankCard
	 * @return
	 */
	public ResultDO<Object> sendThirdPayCheckBlankCodeAdvance(String ticket, String validCode, MemberBankCard memberBankCard);



	/**
	 * 根据卡号和客户编号获得绑定的银行卡
	 * 
	 * @param cardNumber
	 * @param memberId
	 * @return
	 */
	public MemberBankCard getMemberBankCardByCardNumberAndMemberId(
			String cardNumber, Long memberId);

	/**
	 * 根据会员ID 获取所有银行卡
	 * 
	 * @param memberId
	 * @return
	 */
	public List<MemberBankCard> getMemberBankCardByMemberId(Long memberId);

	/***
	 * 解除银行卡 并 调用第三方支付接口
	 */
	public ResultDO<Object> deleteMemberBankCard(Long id, Long memberId, String userIp) throws Exception;
	/**
	 * 设置银行卡，为默认
	 * 
	 * @param id
	 * @param memberId
	 * @return
	 */

	public int setDefaultMemberBankCard(Long id, Long memberId);

	/**
	 * 获取默认银行卡，如果没有默认获取第一张
	 * 
	 * @param memberId
	 * @return
	 */
	public MemberBankCard getDefaultBankCardByMemberId(Long memberId);
	
	/**
	 * 此ID 是否属于这个 会员
	 * @param id
	 * @param memberId
	 * @return
	 * author: pengyong
	 * 下午5:31:17
	 */
	public boolean  isExist(Long id,Long memberId);

	/**
	 * 查询当前用户所有快捷支付卡
	 * @param memberId
	 * @return
	 */
	List<MemberBankCard> selectAllQuickPayBankCard(Long memberId);

	/**
	 * 查询已经删除银行卡
	 * @param id
	 * @return
	 */
	MemberBankCard  selectDeletedBankCard(Long id);

	/**
	 * 根据主键查询
	 * @param id
	 * @return
	 */
	MemberBankCard  selectByPrimaryKey(Long id);
	
	/**
	 * 获得用户安全快捷支付卡信息
	 * @param memberId
	 * @return
	 */
	MemberBankCard querySecurityBankCard(Long memberId);
	
	/**
	 * 获取用户非安全卡的银行卡
	 * @param memberId
	 * @return
	 */
	List<MemberNonSecurityBankCardDto> selectNonSecurityBankCard(Long memberId);
	
	/**
	 * 绑定安全卡
	 * @param memberId
	 * @return
	 */
	BindSecurityBankCardDto bindSecurity(Long memberId);
	
	SimpleMemberBankCardDto getMemberBankCardById(Long id, Long memberId);
}
