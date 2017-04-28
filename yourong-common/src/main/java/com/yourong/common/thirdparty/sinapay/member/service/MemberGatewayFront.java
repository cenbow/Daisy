/**
 * 
 */
package com.yourong.common.thirdparty.sinapay.member.service;

import java.util.List;

import javax.naming.CommunicationException;
import javax.security.auth.login.AccountNotFoundException;

import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.BalanceFreezeDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.BalanceUnFreezeDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.BindingBankCardDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.CreateActiveMemberDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.PayPasswordDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.PersonRealnameDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.QueryAccountDetailsDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.QueryBalanceDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.QueryBankCardDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.UnBindingBankCardDto;
import com.yourong.common.thirdparty.sinapay.member.domain.result.AccountBalance;
import com.yourong.common.thirdparty.sinapay.member.domain.result.AccountDetail;
import com.yourong.common.thirdparty.sinapay.member.domain.result.BankCardEntry;
import com.yourong.common.thirdparty.sinapay.member.domain.result.CardBindingResult;
import com.yourong.common.thirdparty.sinapay.member.exception.MemberGatewayInvokeFailureException;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.HandleWithholdAuthorityDto;

/**
 * <p>微钱包的会员网关前置</p>
 * @author Wallis Wang
 * @version $Id: MemberGatewayFront.java, v 0.1 2014年6月25日 下午2:23:26 wangqiang Exp $
 */
public interface MemberGatewayFront {

    public static final int CARD_LIST_CHECK_LENGTH = 9;

    /**
     * <p>其他信息的格式是<pre><参数名1>^<参数值1>|<参数名2>^<参数值2>|...|<参数名n>^<参数值n></pre>
     * @param activeMemberDto
     * @return
     * @throws MemberGatewayInvokeFailureException
     */
    ResultDto<?> createActiveMember(CreateActiveMemberDto activeMemberDto)
                                                                          throws MemberGatewayInvokeFailureException;

    /**
     * 设置会员的实名信息。
     * 
     * <p>如果需要微钱包对该信息做认证，则微钱包会对其提供的实名信息进行验证；否则仅是简单地将实名信息保存。
     * @param realname
     * @return
     * @throws MemberGatewayInvokeFailureException
     */
    ResultDto<?> setRealname(PersonRealnameDto realname) throws MemberGatewayInvokeFailureException;

    /**
     * 绑定银行卡。
     * <p>绑定成功后，会返回一个对应的标识，以后就可以根据这个标识查询到这张银行卡的具体信息。
     * @param bindingBankCardDto 要绑定的银行卡信息
     * @return 该银行卡绑定后的标识
     * @throws MemberGatewayInvokeFailureException 调用网关失败
     */
    ResultDto<CardBindingResult> bindBankCard(BindingBankCardDto bindingBankCardDto)
                                                                                    throws MemberGatewayInvokeFailureException;

    /**
     * 取消银行卡的绑定。
     * @param unBindingBankCardDto
     * @return
     * @throws MemberGatewayInvokeFailureException 网关通信异常
     */
    ResultDto<?> unbindBankCard(UnBindingBankCardDto unBindingBankCardDto)
                                                                          throws MemberGatewayInvokeFailureException;

    /**
     * 查询指定账户已绑定的全部银行卡信息。
     * @param queryBankCardDto
     * @return
     * @throws MemberGatewayInvokeFailureException 网关通信异常
     */
    ResultDto<List<BankCardEntry>> queryBankCards(QueryBankCardDto queryBankCardDto)
                                                                                    throws MemberGatewayInvokeFailureException;

    /**
     * 查询会员指定账户的余额。
     * <p>返回的信息包括该账户的余额，及可用金额。
     * @param queryBalanceDto
     * @return
     * @throws MemberGatewayInvokeFailureException 网关通信异常
     */
    ResultDto<AccountBalance> queryBalance(QueryBalanceDto queryBalanceDto)
                                                                           throws MemberGatewayInvokeFailureException;

    /**
     * 查询会员账户收支明细。
     * <p>网关接口对每次查询最多返回的记录数是有限制的，也即<code>pageSize</code>值大于接口的规定的上限值是没有实际意义的。
     * @param account 要查询的会员账户
     * 
     * @return 返回查询到的明细列表；如果没有记录，返回空列表。
     * @throws IllegalArgumentException 如果提供了任何不正确参数则会抛出该异常，如前三个参数为空，account中信息不完整，
     * 或者查询的起始时间晚于终止时间，页号或者每页大小的值小于1等等
     * @throws AccountNotFoundException 如果指定的资金账户不存在
     * @throws CryptionExecption 加解密失败异常
     * @throws CommunicationException 网关通信异常
     * @throws StatementException 其他原因导致的查询失败
     */
    ResultDto<AccountDetail> queryAccountDetails(QueryAccountDetailsDto queryAccountDetailsDto)
                                                                                               throws MemberGatewayInvokeFailureException;

    /**
     * 冻结指定账户中的部分资金。
     * @param balanceFreezeDto
     * @return
     * @throws MemberGatewayInvokeFailureException 网关通信异常
     */
    ResultDto<?> freezeBalance(BalanceFreezeDto balanceFreezeDto)
                                                                 throws MemberGatewayInvokeFailureException;

    /**
     * 解冻资金。
     * 
     * <p>将已冻结的指定资金恢复成可用资金。
     * @param balanceUnFreezeDto
     * @return
     * @throws MemberGatewayInvokeFailureException 网关通信异常
     */
    ResultDto<?> unfreezeBalance(BalanceUnFreezeDto balanceUnFreezeDto)
                                                                       throws MemberGatewayInvokeFailureException;
    /**
     * 新浪页面展示用户信息
     * @return
     */
    public String auditMemberInfos(CreateActiveMemberDto activeMemberDto) throws MemberGatewayInvokeFailureException;
    
	/**
	 * 
	 * @Description:设置支付密码
	 * @param payPasswordDto
	 * @param apiName
	 * @return
	 * @author: wangyanji
	 * @time:2016年7月13日 下午8:41:12
	 */
	public ResultDto<PayPasswordDto> handlePayPassword(PayPasswordDto payPasswordDto, String apiName);
	
	/**
	 * 委托扣款授权操作
	 * 
	 * @param handleWithholdAuthorityDto 
	 * @param apiName 调用接口
	 * @return
	 * @throws MemberGatewayInvokeFailureException
	 */
    public ResultDto<HandleWithholdAuthorityDto> handleWithholdAuthority(HandleWithholdAuthorityDto handleWithholdAuthorityDto, String apiName) throws MemberGatewayInvokeFailureException;
    
    /**
     * 查看用户是否委托扣款
     * 
     * @param handleWithholdAuthorityDto
     * @return
     * @throws MemberGatewayInvokeFailureException
     */
    public ResultDto<HandleWithholdAuthorityDto> queryWithholdAuthority(HandleWithholdAuthorityDto handleWithholdAuthorityDto) throws MemberGatewayInvokeFailureException;
    
    /**
	 * 
	 * @Description:获取用户信息新浪展示页
	 * @param memberId
	 * @return
	 * @author: zhanghao
	 * @time:2016年8月12日 下午15:16:12
	 */
    public ResultDto<Object> showMemberInfosSina(Long memberId,String returnUrl);
    
}
