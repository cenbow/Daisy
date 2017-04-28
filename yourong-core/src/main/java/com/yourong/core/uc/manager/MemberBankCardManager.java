package com.yourong.core.uc.manager;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.uc.model.MemberBankCard;

public interface MemberBankCardManager {

	/**
	 * 分页查询用户银行卡
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	public Page<MemberBankCard> findByPage(Page<MemberBankCard> pageRequest, @Param("map") Map<String, Object> map) throws ManagerException;
	
	/**
	 * 根据卡号和用户编号获得绑定的银行卡
	 * @param cardNumber
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public MemberBankCard getMemberBankCardByCardNumberAndMemberId(String cardNumber, Long memberId) throws ManagerException;
	
	/**
	 * 把客户默认的银行卡重置为非默认
	 * @param memberId
	 * @return
	 */
	public int resetMemberBankCardDefaultStatus(Long memberId) throws ManagerException;
	/**
	 * 设置默认
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public int setDefaultMemberBankCard(Long id) throws ManagerException;
	/**
	 * 获得客户银行卡的数量
	 * @param memberId
	 * @return
	 */
	Long getMemberBankCardQuantityByMemberId(Long memberId) throws ManagerException;
	
	/**
	 * 
	 * @param record
	 * @return
	 */
	public int insert(MemberBankCard record) throws ManagerException;

	/**
	 * 
	 * @param record
	 * @return
	 */
	public int insertSelective(MemberBankCard record) throws ManagerException;
	
	
	List<MemberBankCard> selectByMemberID(Long memberId) throws ManagerException;
	
	/**
	 * 解绑会员银行卡
	 * 
	 * @param id
	 * @param userUnBindIp 解绑IP
	 * @return
	 * @throws ManagerException
	 */
    int deleteMemberBankCard(Long id, String userUnBindIp)throws ManagerException;
    
    public  MemberBankCard selectByPrimaryKey(Long id)throws ManagerException;
    
    public MemberBankCard getDefaultBankCardByMemberId(Long memberId)throws ManagerException;
    
    
	public boolean  isExist(Long id,Long memberId)throws ManagerException;

	/**
	 * 查询当前用户所有的 快捷支付卡
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	List<MemberBankCard> selectAllQuickPayBankCard(Long memberId) throws ManagerException;

	/**
	 * 查询已经删除银行卡（提现卡升级为支付卡）
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	MemberBankCard selectDeletedBankCard(Long id)throws ManagerException;

	/**
	 * 提现银行卡，升级支付卡
	 * @param record
	 * @return
	 */
	int updateMemberBankCardQuickPay(MemberBankCard record)throws ManagerException;

	/**
	 *  快捷卡，交易成功后， 该卡变成安全卡
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	int updateMemberBankCardSecurity(Long id)throws ManagerException;


	/**
	 *  查询第三方接口 是否此卡是安全卡，如果是 安全卡， 改成安全卡
	 * @param bankCardId
	 * @throws Exception
	 */
	public void setSecurityCardFromThirdPay(Long bankCardId) throws Exception;

	/**
	 * 查询快捷卡，不是安全卡
	 * @return
	 */
	List<MemberBankCard>  selectAllQuickPayNotSecurity();
	
	/**
	 * 获得用户安全快捷支付卡信息
	 * @param memberId
	 * @return
	 */
	MemberBankCard querySecurityBankCard(Long memberId) throws Exception;
	
	/**
	 * 获取用户非安全卡的银行卡
	 * @param memberId
	 * @return
	 * @throws Exception
	 */
	List<MemberBankCard> selectNonSecurityBankCard(Long memberId) throws ManagerException;
	
	
	MemberBankCard getMemberBankCardById(Long id, Long memberId) throws ManagerException;

	/**
	 *后台删除用户绑定银行卡功能
	 */
	public ResultDO<Object> deleteBankCard(Long id, String userIp) throws Exception;
	
	/**
	 *后台删除用户绑定银行卡功能
	 */
	public ResultDO<Object> deleteBankCardByMemberId(Long id, String userIp) throws Exception;
	
	/**
	 * 银行卡管理
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	public Page<MemberBankCard> queryMemberCard(Page<MemberBankCard> pageRequest,
            Map<String, Object> map) throws ManagerException;
	
	/**
	 * 同步更新银行卡
	 * 
	 * @param memberId
	 * @param cardId
	 * @param bankMobile
	 * @param trueName
	 * @param rechargeType
	 * @return MemberBankCard
	 */
	public ResultDO<MemberBankCard> synMemberBankCard(Long memberId, String cardId, Long bankMobile, String trueName) throws Exception;
}
