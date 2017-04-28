package com.yourong.web.utils;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.SpringContextHolder;
import com.yourong.common.util.StringUtil;
import com.yourong.core.cms.model.biz.BannerFroAreaBiz;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.biz.MemberSavingPotBiz;
import com.yourong.core.ic.dao.DebtMapper;
import com.yourong.core.ic.dao.ProjectMapper;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.uc.dao.ThirdCompanyMapper;
import com.yourong.core.uc.manager.MemberCheckManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberBankCard;
import com.yourong.web.dto.MemberSessionDto;
import com.yourong.web.service.BalanceService;
import com.yourong.web.service.BannerService;
import com.yourong.web.service.CapitalInOutLogService;
import com.yourong.web.service.CouponService;
import com.yourong.web.service.MemberBankCardService;
import com.yourong.web.service.MemberService;
import com.yourong.web.service.TransactionService;


public class ServletUtil {
	
	private static MemberService memberService = SpringContextHolder.getBean(MemberService.class);
	private static BalanceService balanceService = SpringContextHolder.getBean(BalanceService.class);
	private static CouponService couponService = SpringContextHolder.getBean(CouponService.class);
	private static MemberBankCardService memberBankCardService = SpringContextHolder.getBean(MemberBankCardService.class);
	private static MemberCheckManager memberCheckManager = SpringContextHolder.getBean(MemberCheckManager.class);
	private static CapitalInOutLogService capitalInOutLogService = SpringContextHolder.getBean(CapitalInOutLogService.class);
	private static BannerService bannerService = SpringContextHolder.getBean(BannerService.class);
	
	private static ProjectMapper projectMapper = SpringContextHolder.getBean(ProjectMapper.class);
	private static ThirdCompanyMapper thirdCompanyMapper = SpringContextHolder.getBean(ThirdCompanyMapper.class);
	private static DebtMapper debtMapper = SpringContextHolder.getBean(DebtMapper.class);
	
	private static TransactionService transactionService = SpringContextHolder.getBean(TransactionService.class);

	
	
	/**
	 * 获取当前登陆的用户对象
	 * @return
	 */
	public static MemberSessionDto getUserDO() {
		MemberSessionDto username = null;
		HttpSession session = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest().getSession();
		username = (MemberSessionDto) session.getAttribute(Constant.CURRENT_USER);
		return username;
	}
	
	
	/**
	 * 是否实名认证
	 * @return
	 * author: pengyong
	 * 下午3:58:01
	 */
	public static Object getMemberSavingPot(){
		ResultDO<MemberSavingPotBiz> result = capitalInOutLogService.getMemberSavingPotBiz(getUserDO().getId());
		return result;
	}
	
	/**
	 * 获取真实姓名和新浪存钱罐账号
	 * @return
	 * author: wangyanji
	 * 下午3:58:01
	 */
	public static boolean isVerifyTrueName(){
		boolean result = false;
		MemberSessionDto member = getUserDO();
		if(member !=null && StringUtil.isNotBlank(member.getTrueName()) && StringUtil.isNotBlank(member.getIdentityNumber())){
			result = true;
		}	
		return result;
	}
	
	/**
	 * 获得当前登录用户头像
	 * @return
	 */
	public static String getMemberAvatar(){
		MemberSessionDto member = getUserDO();
		return memberService.getMemberAvatar(member.getId());
	}
	
	/**
	 * 获得指定用户的头像
	 * @param id
	 * @return
	 */
	public static String getMemberAvatarById(Long id){
		return memberService.getMemberAvatar(id);
	}
	
	/**
	 * 获取当前加星手机号
	 * @return
	 */
	public static String getMemberMaskMobile(){
		MemberSessionDto member = getUserDO();
		return StringUtil.maskString(member.getMobile().toString(), StringUtil.ASTERISK, 3, 4, 4);
	}
	
	
	/**
	 * 指定手机号加星
	 * @return
	 */
	public static String getMaskMobile(Object mobile){
		if(mobile==null){
			return "";
		}
		return StringUtil.maskString(mobile.toString(), StringUtil.ASTERISK, 3, 4, 4);
	}
	

	/**
	 * 绑卡支付显示名，不显示姓
	 * @return
	 */
	public  static  String  getMemberMaskTrueName(){
		MemberSessionDto member = getUserDO();
		return StringUtil.maskFirstName(member.getTrueName());
	}

	
	/**
	 * 获取手机号或者用户名
	 * @return
	 */
	public static String getUserNameOrMobile(){
		MemberSessionDto member = getUserDO();
		if(StringUtil.isNotBlank(member.getUsername())) {
			return member.getUsername();
		} else {
			return member.getMobile().toString();
		}
	}
	
	/**
	 * 邮箱是否已经认证
	 * @return
	 */
	public static boolean isEmailBinded(String email){
		boolean result = false;
		Member member = memberService.selectByEmail(email);
		if(member !=null ){
			result = true;
		}	
		return result;
	}
	
	/**
	 * 查询存钱罐可用余额
	 * @return
	 */
	public BigDecimal getPiggyBalance() {
		MemberSessionDto member = getUserDO();
		Balance balance = balanceService.queryBalance(member.getId(), TypeEnum.BALANCE_TYPE_PIGGY);
		BigDecimal availableBalance = null;
		if(balance !=null ){
			 availableBalance = balance.getAvailableBalance();
		}else{
			availableBalance = BigDecimal.ZERO;
		}		
		return availableBalance;
	}
	
	/**
	 * 查询存钱罐余额
	 * @return
	 */
	public BigDecimal getPiggyTotalBalance() {
		MemberSessionDto member = getUserDO();
		Balance balance = balanceService.queryBalance(member.getId(), TypeEnum.BALANCE_TYPE_PIGGY);
		BigDecimal availableBalance = null;
		if(balance !=null ){
			 availableBalance = balance.getBalance();
		}else{
			availableBalance = BigDecimal.ZERO;
		}		
		return availableBalance;
	}
	
	/**
	 * 查询存钱罐可用余额小数点前金额
	 * @return
	 */
	public String getPrefixPiggyBalance() {
		return FormulaUtil.getIntegerDefaultZero(getPiggyBalance());
	}
	
	/**
	 * 查询存钱罐可用余额小数点后金额
	 * @return
	 */
	public String getSuffixPiggyBalance() {
		return FormulaUtil.getDecimalDefaultZero(getPiggyBalance());
	}
	
	/**
	 * 查询存钱罐余额小数点前金额
	 * @return
	 */
	public String getPrefixPiggyTotalBalance() {
		return FormulaUtil.getIntegerDefaultZero(getPiggyTotalBalance());
	}
	
	/**
	 * 查询存钱罐余额小数点后金额
	 * @return
	 */
	public String getSuffixPiggyTotalBalance() {
		return FormulaUtil.getDecimalDefaultZero(getPiggyTotalBalance());
	}
	
	/**
	 * 获取可用优惠券数量
	 * @return
	 */
	public int getCouponCount() {
		return couponService.getMemberCouponCount(getUserDO().getId());
	}
	
	public int getCouponCountByType(int type) {
		return couponService.getMemberCouponCountByType(getUserDO().getId(),type);
	}
	
	/**
	 * 获取默认银行卡，如果没有默认获取第一张
	 * @return
	 */
	public MemberBankCard getDefaultBankCard() {
		MemberSessionDto member = getUserDO();
		return memberBankCardService.getDefaultBankCardByMemberId(member.getId());
	}
	
	/**
	 * 获取可用人气值
	 * @return
	 */
	public int getPopularityVaule() {
		MemberSessionDto member = getUserDO();
		Balance balance = balanceService.queryBalance(member.getId(), TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
		if(balance != null){
			return balance.getAvailableBalance().intValue();
		}else{
			return 0;
		}
	}
	
	/**
	 * 获取人气值
	 * @return
	 */
	public int getPopularityBalanceValue() {
		MemberSessionDto member = getUserDO();
		Balance balance = balanceService.queryBalance(member.getId(), TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
		if(balance != null){
			return balance.getBalance().intValue();
		}else{
			return 0;
		}
	}
	
	/**
	 * 获取冻结人气值
	 * @return
	 */
	public int getLockPopularityValue() {
		MemberSessionDto member = getUserDO();
		Balance balance = balanceService.queryBalance(member.getId(), TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
		if(balance != null){
			return (balance.getBalance().subtract(balance.getAvailableBalance())).intValue();
		}else{
			return 0;
		}
	}
	
	/**
	 * 是否领取完善信息优惠卷
	 * @return
	 */
	public boolean isReceiveMemberInfoCompleteCoupon(){
		MemberSessionDto member = getUserDO();
		return memberService.isReceiveMemberInfoCompleteCoupon(member.getId());
	}

	/**
	 * 判断是否有安全卡
	 * @param list
	 * @return
	 */
	public boolean  isHasSarfeCard(List<MemberBankCard> list){
		boolean  result = false;
		if (Collections3.isEmpty(list))
			return  result;
		for (MemberBankCard card : list){
			if (card.getIsSecurity() == 1){
				return  true;
			}
		}
		return  result;
	}

	/**
	 * 获取安全卡
	 * @param list
	 * @return
	 */
	public MemberBankCard  getSarfeCard(List<MemberBankCard> list){
		if (Collections3.isEmpty(list))
			return  null;
		for (MemberBankCard card : list){
			if (card.getIsSecurity()!=null && card.getIsSecurity() == 1){
				return  card;
			}
		}
		return  null;
	}

	/**
	 * 获得用户昵称
	 * @param id
	 * @return
	 */
	public static String getMemberUserName(Long id){
		return memberService.getFormatMemberUserName(id);
	}
	
	/** 
	 *  判断是否已签到
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public boolean isChecked() throws ManagerException {
		HttpSession session = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest().getSession();
		Object isChecked = session.getAttribute("checked"+DateUtils.formatDatetoString(DateUtils.getCurrentDate(),DateUtils.DATE_FMT_3));
		boolean checked;
		if(isChecked==null){
			checked = memberCheckManager.isChecked(getUserDO().getId());
			session.setAttribute("checked"+DateUtils.formatDatetoString(DateUtils.getCurrentDate(),DateUtils.DATE_FMT_3), checked);
		}else{
			checked = (boolean)isChecked;
		}
		return checked;
	}

	/**
	 * 获取今日签到所获得的人气值
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public int getPopularityValueForChecked() throws ManagerException {
		return memberCheckManager.getPopularityValueForChecked(getUserDO().getId());
	}

	/**判断登陆用户是否是手机端**/
	public static boolean isMobile(HttpServletRequest request) {
	    String userAgentInfo = request.getHeader("User-Agent");
	    if(StringUtil.isBlank(userAgentInfo)){
	    	return false;
	    }

	    String[] agents = {"Android", "iPhone", "IEMobile","webOS", "Windows Phone", "BlackBerry", "PlayBook", "Opera Mini ",  "iPod"};
	    boolean flag = false;
	    for (int v = 0; v < agents.length; v++) {
	        if (userAgentInfo.indexOf(agents[v]) > 0) {
	            flag = true;
	            break;
	        }
	    }
	    return flag;
	}
	
	/**
	 * 获得生日专题人气值
	 * @return
	 * @throws ManagerException 
	 */
	public int getBirthdayPopularityVaule() throws ManagerException{
		int v = getPopularityValueForChecked();
		if(v >= 10){ //签到10倍
			return v/10;
		}
		return v;
	}
	
	/**
	 * 生日活动是否开始
	 * @return
	 */
	public boolean isBirthdayActivity(){
		Activity activity = memberService.getBirthdayActivity();
		if(activity != null){
			if(DateUtils.getCurrentDate().after(activity.getStartTime())){
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取不同类型（现金券、优惠券）可用优惠券数量
	 */
	public int getActivedCouponCountByType(int type){
		return couponService.getMemberActivedCouponCountByType(getUserDO().getId(),type);
	}
	
	/**
	 * 通过会员id查询被推荐会员总数
	 * @return
	 */
	public long getReferralMemberByIdCount(){
		return couponService.getReferralMemberByIdCount(getUserDO().getId());
	}
	
	/**根据位置获取banner**/
	public Object getBannerByAreaSign(int type, String areaCode, Integer rowStart, Integer rowEnd) {
		ResultDO<BannerFroAreaBiz> retDo = bannerService.showBannerByArea(type, areaCode, rowStart, rowEnd);
		return retDo;
	}
	

	/**
	 * 用户中心是否显示债权管理菜单
	 * @return
	 */
	public boolean isDisplayDebt(){
		MemberSessionDto member = getUserDO();
		boolean flag = false;
		if(member.isDisplayDebt()){
			return true ;
		}
		flag = memberService.countDebtByLenderId(member.getId());
		member.setDisplayDebt(flag);
		return flag;
	}
	
	/**
	 * 资金流水是否显示垫资还款
	 * @return
	 */
	public boolean isDisplayLoanRepayment(){
		MemberSessionDto member = getUserDO();
		boolean flag = false;
		if(member.isDisplayLoanRepayment()){
			return true ;
		}
		//借款人
		Integer ifBorrewerNum = projectMapper.ifBorrower(member.getId());
		//垫资人
		Integer ifAdvancerNum = thirdCompanyMapper.ifAdvancer(member.getId());
		if(ifBorrewerNum!=null ||ifAdvancerNum!=null){
			flag = true;
		}
		member.setDisplayLoanRepayment(flag);
		return flag;
	}
	
	/**
	 * 资金流水是否显示项目收放款
	 * @return
	 */
	public boolean isDisplayProjectMoney(){
		MemberSessionDto member = getUserDO();
		boolean flag = false;
		if(member.isDisplayProjectMoney()){
			return true ;
		}
		//借款人
		Integer ifBorrewerNum = projectMapper.ifBorrower(member.getId());
		//原始债权人
		Integer ifOriginatorsNum = debtMapper.ifOriginators(member.getId());
		if(ifBorrewerNum!=null ||ifOriginatorsNum!=null){
			flag = true;
		}
		member.setDisplayProjectMoney(flag);
		return flag;
	}
	
	/**
	 * 获取是否是M站跳转到PC
	 * 
	 * @return
	 */
	public boolean checkIndexFromMobile() {
		Object flag = null;
		HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		flag = session.getAttribute(Constant.PC_INDEX_FROM_MOBIE);
		if (flag == null) {
			return false;
		}
		if ("Y".equals(flag.toString())) {
			return true;
		}
		return false;
	}
	/**
	 * 获取用户未签署合同数量
	 * @throws ManagerException 
	 */
	public int getUnsignContractNum() throws ManagerException{
		MemberSessionDto member = getUserDO();
		if(member!=null){
			return transactionService.getUnsignContractNum(member.getId());
		}
		return 0;
	}
	
	/**
	 * 
	 * @Description:同步是否设置支付密码
	 * @return true 已设置，false 未设置（或者第三方查询失败）
	 * @author: wangyanji
	 * @time:2016年7月15日 下午2:16:01
	 */
	public int synPayPasswordFlag() {
		MemberSessionDto member = getUserDO();
		if (member == null || member.getId() == null || StringUtil.isBlank(member.getTrueName())
				|| StringUtil.isBlank(member.getIdentityNumber())) {
			return -1;
		}
		if (member.getPayPasswordFlag() == StatusEnum.SET_PAY_SUCCESS_FLAG_Y.getStatus()) {
			return 1;
		}
		ResultDO<Boolean> rDO = (ResultDO<Boolean>) memberService.synPayPasswordFlag(member.getId());
		if(rDO.isError()) {
			return -1;
		}
		// 已经设置了支付密码同步到session
		member = getUserDO();
		if (rDO.getResult() && member.getPayPasswordFlag() == StatusEnum.SET_PAY_SUCCESS_FLAG_N.getStatus()) {
			member.setPayPasswordFlag(StatusEnum.SET_PAY_SUCCESS_FLAG_Y.getStatus());
			HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
			session.setAttribute(Constant.CURRENT_USER, member);
		}
		if (rDO.getResult()) {
			return 1;
		}
		return 0;
	}

	/**
	 * 是否开通委托扣款
	 * 
	 * @return 1：开通，0：关闭，-1：异常
	 * @throws Exception
	 */
	public int synWithholdAuthorityFlag() throws Exception{
		MemberSessionDto memberDto = getUserDO();
		// 如果session为空，或未实名认证，或未设置支付密码，返回false
		if (memberDto == null
				|| memberDto.getId() == null
				|| StringUtil.isBlank(memberDto.getTrueName())
				|| StringUtil.isBlank(memberDto.getIdentityNumber())
				|| memberDto.getPayPasswordFlag() == 0
				) {
			return -1;
		}
		// 同步是否开通委托扣款
		ResultDO<Boolean> isWithholdAuthorityFlag = memberService.synWithholdAuthority(memberDto.getId());
		if (isWithholdAuthorityFlag.isError()) {
			return -1;
		}
		// 更新session
		HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		if (isWithholdAuthorityFlag.getResult()) {
			if (memberDto.getWithholdAuthorityFlag() == StatusEnum.WITHHOLD_AUTHORITY_FLAG_N.getStatus()) {
				memberDto.setWithholdAuthorityFlag(StatusEnum.WITHHOLD_AUTHORITY_FLAG_Y.getStatus());
				session.setAttribute(Constant.CURRENT_USER, memberDto);
			}
			return 1;
		} 
		if (!isWithholdAuthorityFlag.getResult()) {
			if (memberDto.getWithholdAuthorityFlag()==StatusEnum.WITHHOLD_AUTHORITY_FLAG_Y.getStatus()) {
				memberDto.setWithholdAuthorityFlag(StatusEnum.WITHHOLD_AUTHORITY_FLAG_N.getStatus());
				session.setAttribute(Constant.CURRENT_USER, memberDto);
			}
			return 0;
		}
		return 0;
	}

	/**
	 * 获取用户募集中未签署合同数量
	 * @throws ManagerException 
	 */
	public int getCollectSignableContractNum() throws ManagerException{
		MemberSessionDto member = getUserDO();
		if(member!=null){
		return transactionService.getCollectSignableContractNum(member.getId());
		}
		return 0;
	}
	
	/**
	 * 获取用户我的交易未签署合同数量
	 * @throws ManagerException 
	 */
	public int getTransactionSignableContractNum() throws ManagerException{
		MemberSessionDto member = getUserDO();
		if(member!=null){
		return transactionService.getTransactionSignableContractNum(member.getId());
		}
		return 0;
	}
	
	
	/**
	 * 获取用户签署方式
	 * @throws ManagerException 
	 */
	public int getMemberSignWay() throws ManagerException{
		MemberSessionDto member = getUserDO();
		if(member!=null){
		return memberService.getMemberSignWay(member.getId());
		}
		return 0;
	}
	
	/**
	 * 获取用户真实姓名
	 * @throws ManagerException 
	 */
	public String getMemberTrueName() throws ManagerException{
		MemberSessionDto member = getUserDO();
		return member.getTrueName();
	}

}
