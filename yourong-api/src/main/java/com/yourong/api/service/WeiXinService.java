package com.yourong.api.service;

import java.util.Date;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberToken;

public interface WeiXinService {

	/**
	 * 查询用户余额
	 * @param memberId
	 * @param fromUserName
	 * @param toUserName
	 * @return
	 */
	public String queryMemberBalance(Long memberId, String fromUserName, String userName);
	
	/**
	 * 查询用户投资项目
	 * @param memberId
	 * @param fromUserName
	 * @param toUserName
	 * @return
	 */
	public String queryMemberTransaction(Long memberId, String fromUserName, String userName);
	
	/**
	 * 查询用户优惠
	 * @param memberId
	 * @param fromUserName
	 * @param toUserName
	 * @return
	 */
	public String queryMemberCoupon(Long memberId, String fromUserName, String userName);

	/**
	 * 查询用户提现
	 * @param memberId
	 * @param fromUserName
	 * @param toUserName
	 * @return
	 */
	public String queryMemberWithDraw(Long memberId, String fromUserName, String userName);

	/**
	 * 提示  “注册或者登陆绑定”
	 * @param fromUserName
	 * @param toUserName
	 * @param memberToken
	 * @return
	 */
	public String  registerOrBind(String fromUserName, String toUserName,MemberToken memberToken);
	
	/**
	 * 解绑微信号
	 * @param memberId
	 * @return
	 */
	public String unbundlingWeiXinStep1(String fromUserName, String toUserName, MemberToken memberToken);
	
	public String unbundlingWeiXinStep2(String fromUserName, String toUserName, String directive, MemberToken memberToken);
	
	/**
	 * 转发至多客服
	 * @param fromUserName
	 * @param toUserName
	 * @return
	 */
	public String responseTransferCustomerService(String fromUserName, String toUserName);

	public String keyWords(String content) ;

	/**
	 * @param fromUserName
	 * @param toUserName
	 * @param content
	 * @param memberToken
	 * @return
	 */
	public String unbundlingWeiXinStep3(String fromUserName, String toUserName,
			String content);

	/**
	 * @param fromUserName
	 * @param toUserName
	 * @return
	 */
	public String firstConcern(String fromUserName, String toUserName);

	public String queryMemberInvest(Long memberId, String fromUserName,
			String toUserName ,Date date,String dateString);
	
	
	/**
	 * 
	 * @Description:查询历史投资额
	 * @param pageSize
	 * @return
	 * @author: chaisen
	 * @time:2016年10月24日 下午3:51:32
	 */
	public String queryInvestAmountList(String fromUserName, String toUserName,int pageSize);
}
