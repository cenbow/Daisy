package com.yourong.core.uc.manager.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.cache.RedisPlatformClient;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.SinaCashDeskClient;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.MemberType;
import com.yourong.common.thirdparty.sinapay.common.enums.VerifyStatus;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.PayPasswordDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.dto.HandleWithholdAuthorityDto;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.CryptHelper;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.Identities;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.common.MessageClient;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.RechargeLogManager;
import com.yourong.core.fin.manager.WithdrawLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.RechargeLog;
import com.yourong.core.fin.model.WithdrawLog;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.sales.SPParameter;
import com.yourong.core.sales.engine.SPEngine;
import com.yourong.core.sys.dao.SysDictMapper;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.uc.dao.AutoInvestSetMapper;
import com.yourong.core.uc.dao.EnterpriseMapper;
import com.yourong.core.uc.dao.MemberInfoMapper;
import com.yourong.core.uc.dao.MemberMapper;
import com.yourong.core.uc.dao.MemberOpenMapper;
import com.yourong.core.uc.dao.MemberReferMapper;
import com.yourong.core.uc.manager.EnterpriseManager;
import com.yourong.core.uc.manager.MemberInfoManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.manager.MemberNotifySettingsManager;
import com.yourong.core.uc.manager.MemberVerifyManager;
import com.yourong.core.uc.model.AutoInvestSet;
import com.yourong.core.uc.model.Enterprise;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberBaseBiz;
import com.yourong.core.uc.model.MemberInfo;
import com.yourong.core.uc.model.MemberRefer;
import com.yourong.core.uc.model.MemberVerify;
import com.yourong.core.uc.model.biz.MemberBiz;
import com.yourong.core.uc.model.biz.MemberForLottery;

@Component
public class MemberManagerImpl  implements MemberManager {

	@Autowired
	private MemberMapper memberMapper;
	
	@Autowired
	private MemberInfoMapper memberInfoMapper;
	
	@Autowired
	private EnterpriseMapper enterpriseMapper;
	
	@Autowired
	private CouponManager  couponManager;
	
	@Autowired
	private ActivityManager activityManager;
	
	@Autowired
	private EnterpriseManager enterpriseManager;
	
	private Logger logger = LoggerFactory.getLogger(MemberManagerImpl.class);
	
	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private MemberReferMapper memberReferMapper;
	
	@Autowired
    private WithdrawLogManager withdrawLogManager;
	
	@Autowired
    private RechargeLogManager rechargeLogManager;
	
	@Autowired
	private SinaCashDeskClient sinaCashDeskClient;
	
	@Autowired
	private AutoInvestSetMapper autoInvestMapper;
	
	@Autowired
	private MemberOpenMapper memberOpenMapper;
	
	@Autowired
	private SysDictMapper sysDictMapper;
	
	@Autowired
	private SinaPayClient sinaPayClient;
	
	@Autowired
	private MemberVerifyManager memberVerifyManager;

	@Autowired
	private MemberInfoManager memberInfoManager;

	@Autowired
	private MemberNotifySettingsManager memberNotifySettingsManager;

	@Autowired
	private TaskExecutor threadPool;

	@Autowired
	private SysDictManager sysDictManager;

	@Override
	public Page<Member> findByPage(Page<Member> pageRequest,
			Map<String, Object> map) throws ManagerException {
		try{
			map.put(Constant.STARTROW, pageRequest.getiDisplayStart());
			map.put(Constant.PAGESIZE, pageRequest.getiDisplayLength());
			int totalCount = memberMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<Member> selectForPagin = memberMapper.selectForPagin(map);
			pageRequest.setData(selectForPagin);			
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<MemberBiz> findMemberBizByPage(Page<MemberBiz> pageRequest, Map<String, Object> map) throws ManagerException {
		try{
			map.put(Constant.STARTROW, pageRequest.getiDisplayStart());
			map.put(Constant.PAGESIZE, pageRequest.getiDisplayLength());
			Calendar calendar=Calendar.getInstance();
			calendar.add(Calendar.MONTH,-1);
			SimpleDateFormat simpleDateFormat=new  SimpleDateFormat("yyyyMM");
			map.put("vipmonth",simpleDateFormat.format(calendar.getTime()));
			int totalCount = memberMapper.selectMemberBizForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<MemberBiz> selectForPagin = memberMapper.selectMemberBizForPagin(map);
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int batchDelete(long[] ids) throws ManagerException {
		try{
			return memberMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public int insertSelective(Member record) throws ManagerException {
		try {
			return memberMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Member selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return memberMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Member selectByUsername(String username) throws ManagerException {
		try {
			return memberMapper.selectByUsername(username);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Member selectByMobile(Long mobile) throws ManagerException {
		try {
			return memberMapper.selectByMobile(mobile);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updatePasswordByMemberID(Member record) throws ManagerException {
		try {
			return memberMapper.updatePasswordByMemberID(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateIdentityNumberById(String trueName,String identityNumber, Long id, Date birthday, int sex)
			throws ManagerException {
		try {
			return memberMapper.updateIdentityNumberById(trueName,identityNumber, id, birthday, sex);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	public Member getMemberByShortUrl(String shortUrl) throws ManagerException {
		try {
			return memberMapper.getMemberByShortUrl(shortUrl);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 根据会员类型和会员的姓名模糊查询会员和详情列表
	 */
	@Override
	public List<Member> getMemberInfoByTrueName(Map<String, Object> paramsMap)
			throws ManagerException {
		try {
			return memberMapper.getMemberInfoByTrueName(paramsMap);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	

	@Override
	public MemberBaseBiz selectMemberBaseBiz(Long id) throws ManagerException {
		try {
			MemberBaseBiz baseBiz = new MemberBaseBiz();
			Member member = memberMapper.selectByPrimaryKey(id);
			baseBiz.setMember(member);
			baseBiz.setId(id);
			MemberInfo memberId = memberInfoMapper.selectSingleByMemberId(id);
			baseBiz.setMemberInfo(memberId);
			List<Enterprise> selectBylegalId = enterpriseMapper
					.selectBylegalId(id);
			for(Enterprise enterprise : selectBylegalId){
				
				Balance balance = balanceManager.queryBalance(enterprise.getId(),TypeEnum.BALANCE_TYPE_COMPANY_CREDIT_AMOUNT);
				if(balance!=null){
					enterprise.setAvailableCreditAmount(balance.getAvailableBalance());
				}
			}
			baseBiz.setEnterprises(selectBylegalId);
			return baseBiz;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}	

	@Override
	public int selectForPaginTotalCount(Map<String, Object> map)
			throws ManagerException {
		try {
			return this.memberMapper.selectForPaginTotalCount(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Member> selectForPagin(Map<String, Object> map)
			throws ManagerException {
		try {
			return this.memberMapper.selectForPagin(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public boolean isExitMemberByIdentityNumber(String identityNumber)
			throws ManagerException {
		try {
			Integer number = this.memberMapper.selectByIdentityNumber(identityNumber);
			if(number != null && number == 1){
				return true;
			}			
			return false;
		} catch (Exception e) {
			throw new ManagerException(e);
		}		
	}
	@Override
	public int saveMemberAvatarById(Long id, String avatars) throws ManagerException {
		try {
			return this.memberMapper.saveMemberAvatarById(id, avatars);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public String getMemberAvatar(Long id) throws ManagerException {
		try {
			return this.memberMapper.getMemberAvatar(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public int updateByPrimaryKeySelective(Member record)
			throws ManagerException {
		try {
			return memberMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public Member selectByEmail(String email) throws ManagerException {
		try {
			return memberMapper.selectByEmail(email);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public Page<Member> getReferralMemberById(BaseQueryParam query)
			throws ManagerException {
		try {
			List<Member> members =  memberMapper.getReferralMemberById(query);
			//通过项目id和投资额获取年化收益计算预期收益
			long count = memberMapper.getReferralMemberByIdCount(query);
			Page<Member> page = new Page<Member>();
			page.setData(members);
			page.setiDisplayLength(query.getPageSize());
			page.setPageNo(query.getCurrentPage());
			page.setiTotalDisplayRecords(count);
			page.setiTotalRecords(count);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	public int sendCouponOnCompleteMemberInfo(Long memberId) throws ManagerException{
		if(checkMemberInfoCompleted(memberId)){
			List<Coupon> coupon = couponManager.getCouponByMemberIdAndActivity(memberId, Long.parseLong(Config.activityIdForCompleteMemberInfo));
			if(Collections3.isEmpty(coupon)){
				//couponManager.sendCoupon(memberId, TypeEnum.COUPON_SEND_SOURCE_TYPE_COMPLETE_MEMBER_INFO, null);
				SPParameter  parameter =new SPParameter();
    			parameter.setMemberId(memberId);
    			SPEngine.getSPEngineInstance().run(parameter);
				return 1;
			}
			return 0;
		}
		return -1;
	}
	
	/**
	 * 检查用户信息完整性
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	private boolean checkMemberInfoCompleted(Long memberId) throws ManagerException {
		//检查用户昵称、头像、详细信息是否有填写 
		Member member = memberMapper.selectByPrimaryKey(memberId);
		if(StringUtil.isBlank(member.getUsername())){
			return false;
		}
		MemberInfo memberInfo = memberInfoMapper.getMemberInfoByMemberId(memberId);
		if(memberInfo == null){
			return false;
		}
		return true;
	}
	@Override
	public List<MemberForLottery> getMembersForLottery()
			throws ManagerException {
		try {
			return memberMapper.getMembersForLottery();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateMobileByid(Member record)throws ManagerException {
		try {
			return memberMapper.updateMobileByid(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public Long getMaxMemberId() throws ManagerException {
		try {
			return memberMapper.getMaxMemberId();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public Long getMaxMobile() throws ManagerException {
		try {
			return memberMapper.getMaxMobile();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public int frozenMemberByMobile(Long newMobile,Long mobile)throws ManagerException{
		try {
			return memberMapper.frozenMemberByMobile(newMobile, mobile);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	@Override
	public List<Member> selectMember(Map<String, Object> map)
			throws ManagerException {
		try {
			return memberMapper.selectMember(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public Member todayFirstRegistered() throws ManagerException {
		try {
			return memberMapper.todayFirstRegistered();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public int countFriends(Long memberId, Date registerStartTime, Date registerEndTime) throws ManagerException {
		try {
			Integer count =  memberMapper.countFriends(memberId, registerStartTime, registerEndTime);
			if(count == null){
				return 0;
			}
			return count;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public int countFriendsActivateWallet(Long memberId)
			throws ManagerException {
		try {
			Integer count =  memberMapper.countFriendsActivateWallet(memberId);
			if(count == null){
				return 0;
			}
			return count;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public int countFriendsBindEmail(Long memberId) throws ManagerException {
		try {
			Integer count =  memberMapper.countFriendsBindEmail(memberId);
			if(count == null){
				return 0;
			}
			return count;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public int countFrinedsPerfectInformation(Long memberId)
			throws ManagerException {
		try {
			Integer count =  memberMapper.countFrinedsPerfectInformation(memberId);
			if(count == null){
				return 0;
			}
			return count;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public ResultDO updatePassword(Long id, String oldpassword,
			String newPassword) {
		ResultDO<Object> result = new ResultDO<Object>();
		try {
			Member member = selectByPrimaryKey(id);			
			//验证密码是否相同
			Long mobile = member.getMobile();			
			String decryptByYourong = CryptHelper.decryptByYourong(mobile.toString(), oldpassword);
			if(decryptByYourong.equals(member.getPassword())){				
				member.setPassword(CryptHelper.decryptByYourong(mobile.toString(), newPassword));
				int resu = updatePasswordByMemberID(member);
				
				if(resu == 1){
			       result.setSuccess(true);
			       MessageClient.sendMsgForUpdatePassword(DateUtils.getCurrentDate(), member.getId());
				}				
			}else{
				result.setResultCode(ResultCode.MEMBER_PASSWORD_ERROR);
			}
			return result;
			
		} catch (ManagerException e) {
			logger.error(ResultCode.MEMBER_PHONE_NUMBER_ISEXIST_ERROR.getMsg(), e);	
			result.setResultCode(ResultCode.MEMBER_PHONE_NUMBER_ISEXIST_ERROR);
			return result;
		}
	}
	@Override
	public ResultDO updatePasswordByMobile(Long id, Long mobile, String newPassword) {
		ResultDO<Object> result = new ResultDO<Object>();
		try {			
			Member member = selectByPrimaryKey(id);
			if(member == null){
				result.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
				return result;
			}			
			member.setPassword(CryptHelper.decryptByYourong(mobile.toString(), newPassword));
			int resu = updatePasswordByMemberID(member);
			if(resu == 1){
				result.setSuccess(true);
                //密码修改成功号 清除登陆次数
				RedisMemberClient.removeMemberLoginCount(mobile.toString());  
				if(StringUtil.isNotBlank(member.getUsername())){
					RedisMemberClient.removeMemberLoginCount(member.getUsername());  
				}				
				MessageClient.sendMsgForUpdatePassword(DateUtils.getCurrentDate(), member.getId());
			}			
		} catch (ManagerException e) {
			logger.error("忘记密码，用手机短信验证，修改密码失败  mobile="+mobile, e);	
			result.setResultCode(ResultCode.MEMBER_UPDATE_PASSWORD_ERROR);
		}
		return result;
	}
	@Override
	public List<Member> selectTodayBirthdayMember() throws ManagerException {
		String birthday = DateUtils.getStrFromDate(DateUtils.getCurrentDate(),"MM-dd");
		return memberMapper.selectBirthdayMember(birthday);
	}
	@Override
	public Activity getBirthdayActivity() throws ManagerException {
		Activity activity = activityManager.selectByPrimaryKey(PropertiesUtil.getCoupon50ActvityId());
		if(activity != null && activity.getActivityStatus().intValue() == 4){
			return activity;
		}
		return null;
	}
	
	/**
	 * 通过会员id查询被推荐会员总数
	 */
	@Override
	public long getReferralMemberByIdCount(Long memberId)throws ManagerException {
		try {
			BaseQueryParam query = new BaseQueryParam();
			query.setMemberId(memberId);
			return memberMapper.getReferralMemberByIdCount(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int selectActiveForPaginTotalCount(Map<String, Object> map) throws ManagerException {
		try {
			return memberMapper.selectActiveForPaginTotalCount(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Member> selectActiveForPagin(Map<String, Object> map) throws ManagerException {
		try {
			return memberMapper.selectActiveForPagin(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public Member getMemberId(Map<String, Object> map)throws ManagerException {
		try {
			return memberMapper.getMemberId(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public MemberBaseBiz getMemberBizByEnterpriseId(Long enterpriseId){
		try {
			Enterprise enterprise = enterpriseManager.selectByKey(enterpriseId);
			MemberBaseBiz biz = new MemberBaseBiz();
			if(enterprise!=null){
				Balance balance = balanceManager.queryBalance(enterprise.getId(), TypeEnum.BALANCE_TYPE_COMPANY_CREDIT_AMOUNT);
				if(balance!=null){
					enterprise.setCreditAmount(balance.getBalance());
					enterprise.setAvailableCreditAmount(balance.getAvailableBalance());
				}
				biz.setEnterprises(Lists.newArrayList(enterprise));
				if(enterprise.getLegalId()!=null){
					Member member = memberMapper.selectByPrimaryKey(enterprise.getLegalId());
					if (member != null) {
						biz.setMember(member);
						biz.setId(member.getId());
						MemberInfo memberId = memberInfoMapper.selectSingleByMemberId(member.getId());
						biz.setMemberInfo(memberId);
					}
				}
			}
			return biz;
		} catch (Exception e) {
			logger.debug("根据企业id查询用户信息失败,enterpriseId={}", enterpriseId, e);
		}
		return null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Object> addReferral(Long referredId, Long referralMobile)
			throws ManagerException {
		try{
			ResultDO<Object> res = new ResultDO<Object>();
			Member referral = memberMapper.selectByMobile(referralMobile);
			if(referral == null || referral.getDelFlag() != 1) {
				res.setResultCode(ResultCode.MEMBER_ERRO_TUIJIAN_EROOR);
			} else if(referral.getId().equals(referredId)) {
				res.setResultCode(ResultCode.MEMBER_ERRO_REFERRED_MYSELF_EROOR);
			} else if(com.yourong.common.thirdparty.sinapay.common.util.StringUtil.isEmpty(referral.getTrueName()) || com.yourong.common.thirdparty.sinapay.common.util.StringUtil.isEmpty(referral.getIdentityNumber())) {
				res.setResultCode(ResultCode.MEMBER_ERRO_REFERRAL_IDENTY_EROOR);
			} else if(referral.getStatus() != 2) {
				res.setResultCode(ResultCode.MEMBER_ERRO_REFERRAL_STATUS_EROOR);
			}
			if(!res.isSuccess()) {
				return res;
			}
			Member referred = memberMapper.selectByPrimaryKey(referredId);
			if(referred == null || referred.getDelFlag() != 1) {
				res.setResultCode(ResultCode.MEMBER_ERRO_REFERRED_NULL_EROOR);
			} else if(com.yourong.common.thirdparty.sinapay.common.util.StringUtil.isEmpty(referred.getTrueName()) || com.yourong.common.thirdparty.sinapay.common.util.StringUtil.isEmpty(referred.getIdentityNumber())) {
				res.setResultCode(ResultCode.MEMBER_ERRO_REFERRED_IDENTY_EROOR);
			} else if(referred.getStatus() != 2) {
				res.setResultCode(ResultCode.MEMBER_ERRO_REFERRED_STATUS_EROOR);
			} else if(referred.getReferral() != null) {
				res.setResultCode(ResultCode.MEMBER_ERRO_REFERRED_DUPLICATE_EROOR);
			}
			if(!res.isSuccess()) {
				return res;
			}
			MemberRefer refer = new MemberRefer();
			refer.setReferral(referral.getId());
			refer.setReferred(referredId);
			refer.setReferType(3);
			refer.setReferSource(2);
			refer.setCreateTime(DateUtils.getCurrentDate());
			memberReferMapper.insertSelective(refer);
			Member m = new Member();
			m.setId(referredId);
			m.setReferral(referral.getId());
			memberMapper.updateByPrimaryKeySelective(m);
			return res;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public Member registerByRedPackageSuccessful(Long memberId) {
		try {
			return memberMapper.registerByRedPackageSuccessful(memberId);
		} catch (Exception e) {
			logger.debug("红包用户注册判断失败,memberId={}", memberId, e);
		}
		return null;
	}
	@Override
	public MemberBaseBiz selectMemberBaseBizByBorrowerType(Long id, Integer borrowerType ,Long enterpriseId) throws ManagerException {
		try {
			MemberBaseBiz baseBiz = new MemberBaseBiz();
			Member member = memberMapper.selectByPrimaryKey(id);
			baseBiz.setMember(member);
			baseBiz.setId(id);
			MemberInfo memberId = memberInfoMapper.selectSingleByMemberId(id);
			baseBiz.setMemberInfo(memberId);
			List<Enterprise> selectBylegalId =Lists.newArrayList();
			//借款人为企业
			if(ProjectEnum.PROJECT_BORROWER_ENTERPRISE_TYPE_DIRECT.getType()==borrowerType){
				Enterprise bean=enterpriseMapper.selectByPrimaryKey(enterpriseId);
				if(bean!=null){
					selectBylegalId.add(bean);
				}
			}else{
				selectBylegalId = enterpriseMapper
						.selectBylegalId(id);
			}
			if(Collections3.isEmpty(selectBylegalId)){
				return baseBiz;
			}
			for(Enterprise enterprise : selectBylegalId){
				
				Balance balance = balanceManager.queryBalance(enterprise.getId(),TypeEnum.BALANCE_TYPE_COMPANY_CREDIT_AMOUNT);
				if(balance!=null){
					enterprise.setAvailableCreditAmount(balance.getAvailableBalance());
				}
			}
			baseBiz.setEnterprises(selectBylegalId);
			return baseBiz;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/**
	 * 根据会员类型和会员的姓名模糊查询会员和详情列表
	 */
	@Override
	public List<Member> selectListByMobile(Map<String, Object> paramsMap)
			throws ManagerException {
		try {
			return memberMapper.selectListByMobile(paramsMap);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Object synPayPasswordFlag(Long memberId) throws ManagerException {
		ResultDO<Boolean> rDO = new ResultDO<Boolean>();
		try {
			Member member = selectByPrimaryKey(memberId);
			// 本地查询已经开通支付密码
			if (StatusEnum.SET_PAY_SUCCESS_FLAG_Y.getStatus() == member.getPayPasswordFlag()) {
				rDO.setResult(true);
				return rDO;
			}
			// 未实名认证
			if (StringUtil.isBlank(member.getTrueName()) || StringUtil.isBlank(member.getIdentityNumber())) {
				rDO.setResult(false);
				return rDO;
			}
			// 本地未开通需要同步新浪查询
			ResultDto<PayPasswordDto> reusltSinaDto = sinaCashDeskClient.handlePayPassword(memberId,
					TypeEnum.QUERY_IS_SET_PAY_PASSWORD.getType(), null);
			if (reusltSinaDto.isError()) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM);
				logger.error("新浪查询是否设置支付密码失败, memberId={}, errorCode={}, errorMsg={}", memberId, reusltSinaDto.getErrorCode(),
						reusltSinaDto.getErrorMsg());
				String errorMsg = String.format("同步是否开通支付密码异常");
				rDO.getResultCode().setMsg(errorMsg);
				logger.error(errorMsg);
				return rDO;
			}
			// 同步回来如果是已经设置的，更新表记录并返回true
			if ("Y".equals(reusltSinaDto.getModule().getIsSetPaypass())) {
				Member updateModel = new Member();
				updateModel.setId(memberId);
				updateModel.setPayPasswordFlag(StatusEnum.SET_PAY_SUCCESS_FLAG_Y.getStatus());
				memberMapper.updateByPrimaryKeySelective(updateModel);
				rDO.setResult(true);
				return rDO;
			}
			rDO.setResult(false);
			return rDO;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Object handlePayPassword(Long memberId, int handleType, String returnUrl) throws ManagerException {
		ResultDO<String> rDO = new ResultDO<String>();
		try {
			Member member = selectByPrimaryKey(memberId);
			// 未开通支付密码的只允许设置
			if (StatusEnum.SET_PAY_SUCCESS_FLAG_N.getStatus() == member.getPayPasswordFlag()
					&& TypeEnum.SET_PAY_PASSWORD.getType() != handleType) {
				rDO.setResultCode(ResultCode.PAY_PASSWORD_NOT_SET_ERROR);
				return rDO;
			}
			// 已开通支付密码的不允许设置
			if (StatusEnum.SET_PAY_SUCCESS_FLAG_Y.getStatus() == member.getPayPasswordFlag()
					&& TypeEnum.SET_PAY_PASSWORD.getType() == handleType) {
				rDO.setResultCode(ResultCode.PAY_PASSWORD_SET_ERROR);
				return rDO;
			}
			ResultDto<PayPasswordDto> reusltSinaDto = sinaCashDeskClient.handlePayPassword(memberId, handleType, returnUrl);
			if (reusltSinaDto.isError() || StringUtil.isBlank(reusltSinaDto.getModule().getRedirectUrl())) {
				rDO.setResultCode(ResultCode.ERROR_SYSTEM);
				rDO.getResultCode().setMsg("errorCode=" + reusltSinaDto.getErrorCode() + ", errorCodeMsg=" + reusltSinaDto.getErrorMsg());
				return rDO;
			}
			rDO.setResult(reusltSinaDto.getModule().getRedirectUrl());
			return rDO;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public ResultDO<Boolean> synWithholdAuthority(Long memberId) throws ManagerException {
		try {
			ResultDO<Boolean> resultDO = new ResultDO<Boolean>();
			Member member = selectByPrimaryKey(memberId);
			// 如果未设置支付密码
			if (member.getPayPasswordFlag() == 0) {
				resultDO.setResult(false);
				resultDO.setResultCode(ResultCode.PAY_PASSWORD_NOT_SET_ERROR);
				return resultDO;
			}
			// 查询收银台是否开通委托扣款
			ResultDto<HandleWithholdAuthorityDto> resultDto = this.sinaCashDeskClient.queryWithholdAuthority(memberId);
			if (resultDto.isError()) {
				resultDO.setResult(false);
				resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
				resultDO.getResultCode().setMsg(resultDto.getErrorMsg());
				logger.info("[查询并同步会员是否委托扣款]查询新浪是否开通委托扣款失败, memberId={} errorCode={}, errorMsg={}", memberId, resultDto.getErrorCode(), resultDto.getErrorMsg());
				return resultDO;
			}
			
			// 若平台查询未开通委托扣款，但是新浪已开通，此时需要同步到平台
			if (StatusEnum.WITHHOLD_AUTHORITY_FLAG_N.getStatus() == member.getWithholdAuthorityFlag()) {
				if ("Y".equals(resultDto.getModule().getIsWithholdAuthoity())) {
					Member updateModel = new Member();
					updateModel.setId(memberId);
					updateModel.setWithholdAuthorityFlag(StatusEnum.WITHHOLD_AUTHORITY_FLAG_Y.getStatus());
					int updateFlag = memberMapper.updateByPrimaryKeySelective(updateModel);
					if (updateFlag == 0) {
						resultDO.setResult(false);
					} else {
						resultDO.setResult(true);
					}
					return resultDO;
				}
				resultDO.setResult(false);
				return resultDO;
			}
			
			// 若平台查询已开通委托扣款，但是新浪未开通，此时需要同步到平台
			if (StatusEnum.WITHHOLD_AUTHORITY_FLAG_Y.getStatus() == member.getWithholdAuthorityFlag()) {
				if ("N".equals(resultDto.getModule().getIsWithholdAuthoity())) {
					Member updateModel = new Member();
					updateModel.setId(memberId);
					updateModel.setWithholdAuthorityFlag(StatusEnum.WITHHOLD_AUTHORITY_FLAG_N.getStatus());
					int updateFlag = memberMapper.updateByPrimaryKeySelective(updateModel);
					if(updateFlag>0){
						//关闭自动投标
						AutoInvestSet autoInvestSet=autoInvestMapper.selectByMemberId(memberId);
						if(autoInvestSet!=null&&autoInvestSet.getInvestFlag()==StatusEnum.INVEST_FLAG_OPEN.getStatus()){
							AutoInvestSet record=new AutoInvestSet();
							record.setMemberId(memberId);
							record.setInvestFlag(StatusEnum.INVEST_FLAG_CLOSE.getStatus());
							autoInvestMapper.updateByPrimaryMemberIdSelective(record);
						}
					}
					if (updateFlag == 0) {
						resultDO.setResult(true);
					} else {
						resultDO.setResult(false);
					}
					return resultDO;
				} 
				resultDO.setResult(true);
				return resultDO;
			}
			resultDO.setResult(false);
			return resultDO;
		} catch (Exception e) {
			logger.error("[查询并同步会员是否委托扣款]出现异常!", e); 
			throw new ManagerException(e);
		}
	}
	
	@Override
	public ResultDO<String> handWithholdAuthority(Long memberId, int type) throws ManagerException{
		return handleAuthority(memberId, type, null);
	}
	
	// 授权处理
	private ResultDO<String> handleAuthority(Long memberId, int type, String returnUrl) throws ManagerException {
		ResultDO<String> resultDO = new ResultDO<String>();
		String apiName = "";
		if (type == TypeEnum.HANDLE_WITHHOLD_AUTHORITY.getType()) {
			apiName = "委托扣款授权";
		} else if (type == TypeEnum.RELIEVE_WITHHOLD_AUTHORITY.getType()) {
			apiName = "解除委托扣款";
		}
		
		try {
			Member member = selectByPrimaryKey(memberId);
			// 如果未设置支付密码
			if (member.getPayPasswordFlag() == StatusEnum.SET_PAY_SUCCESS_FLAG_N.getStatus()) {
				resultDO.setResultCode(ResultCode.HANDLE_WITHHOLD_AUTHORITY_ERROR);
				return resultDO;
			}
			// 调用收银台委托扣款操作接口
			ResultDto<HandleWithholdAuthorityDto> resultDto = sinaCashDeskClient.handleWithholdAuthority(memberId, type, returnUrl);
			if (resultDto.isError() || StringUtil.isBlank(resultDto.getModule().getRedirectUrl())) {
				resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
				resultDO.getResultCode().setMsg("["+ apiName +"]errorCode=" + resultDto.getErrorCode() + ", errorCodeMsg=" + resultDto.getErrorMsg());
				logger.info("["+ apiName +"]errorCode=" + resultDto.getErrorCode() + ", errorCodeMsg=" + resultDto.getErrorMsg()); 
				return resultDO;
			}
			
			resultDO.setResult(resultDto.getModule().getRedirectUrl());
			return resultDO;
		} catch (Exception e) {
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("["+apiName+"]" + "出现异常!", e); 
			throw new ManagerException(e);
		}
	}
	
	@Override
	public ResultDO<String> handWithholdAuthority(Long memberId, int type, String returnUrl) throws ManagerException{
		return handleAuthority(memberId, type, returnUrl);
	}
	
	/**
	 * 托管充值：收银台模式
	 * @throws Exception 
	 */
	@Override
	public ResultDO<Object> createHostingDeposit(Long memberId, String payerIp, BigDecimal rechargeAmount, TypeEnum type, 
			String orderNo, String tradeNo, Integer rechargeSource) throws Exception {
		return requestRecharge(memberId, payerIp, rechargeAmount, type.getType(), orderNo, tradeNo, null, false, rechargeSource);
	}
	
	// 请求充值
	public ResultDO<Object> requestRecharge(Long memberId, String payerIp, BigDecimal rechargeAmount, int type, 
			String orderNo, String tradeNo, String returnUrl, boolean isMobileSource, Integer rechargeSource) throws Exception {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		Member member = selectByPrimaryKey(memberId);
		// 如果未设置支付密码
		if (member.getPayPasswordFlag() == StatusEnum.SET_PAY_SUCCESS_FLAG_N.getStatus()) {
			resultDO.setResultCode(ResultCode.RECHARGE_MUST_SET_PAY_PASSWORD_ERROR);
			return resultDO;
		}
		if (rechargeAmount != null) {
			rechargeAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
		} 
        // 保存充值记录
        RechargeLog record = new RechargeLog();
        BigDecimal fee = rechargeAmount.multiply(new BigDecimal(Config.poundage));
    	if(fee == null || fee.compareTo(new BigDecimal(Config.minrecharge) ) < 1){
    		fee = new BigDecimal(Config.minrecharge);
    	}
        record.setAmount(rechargeAmount);
        record.setMemberId(memberId);
//        record.setPayMethod(Constant.SINA_PAY); // 默认网银
        record.setRechargeNo(tradeNo);
        record.setFee(fee);
        record.setOrderNo(orderNo);
        record.setType(type);
        record.setStatus(StatusEnum.RECHARGE_STATUS_PROESS.getStatus());
		record.setPayerIp(payerIp);
		record.setRechargeSource(rechargeSource);
		int insertFlag = rechargeLogManager.insertSelective(record);
		
		if (insertFlag > 0) {
			// 调用新浪托管充值接口，返回充值form
			String formResult = sinaCashDeskClient.createHostingDeposit(tradeNo, memberId, payerIp, rechargeAmount, returnUrl, isMobileSource);
			if (StringUtil.isNotBlank(formResult)) {
				resultDO.setResult(formResult);
			}else{
				resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
				resultDO.getResultCode().setMsg("[托管充值]出现异常memberId = " + memberId + ",tradeNo = " 
					+ tradeNo + ",payerIp=" + payerIp + ",rechargeAmount=" + rechargeAmount + ",orderNo=" + orderNo);
				logger.info(resultDO.getResultCode().getMsg());
				throw new ManagerException(resultDO.getResultCode().getMsg());
			}
		} else {
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
			resultDO.getResultCode().setMsg("[托管充值]保存充值记录出现异常 memberId = " + memberId + ",tradeNo = " 
					+ tradeNo + ",payerIp=" + payerIp + ",rechargeAmount=" + rechargeAmount + ",orderNo=" + orderNo);
			logger.info(resultDO.getResultCode().getMsg());
			throw new ManagerException(resultDO.getResultCode().getMsg());
		}
		
        return resultDO;
	}
	
	/**
	 * 托管充值：收银台模式(传递return url)
	 * @throws Exception 
	 */
	@Override
	public ResultDO<Object> createHostingDeposit(Long memberId, BigDecimal rechargeAmount, String orderNo, Map<String, Object> otherParams) throws Exception {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		// 参数有误
		if (otherParams == null) {
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM_REQUEST_PARAM_DECODE);
			return resultDO;
		}
		// 交易单号
		String tradeNo = otherParams.get("tradeNo").toString();
		// 请求IP
		String payerIp = otherParams.get("payerIp").toString();
		// 充值类型
		int type = Integer.parseInt(otherParams.get("type").toString());
		// 新浪return_url
		String returnUrl = "";
		if (otherParams.get("returnUrl") != null) {
			returnUrl = otherParams.get("returnUrl").toString();
		}
		// 充值来源是否为手机端
		boolean isMobileSource = false;
		if (otherParams.get("isMobileSource") != null) {
			isMobileSource = Boolean.parseBoolean(otherParams.get("isMobileSource").toString());
		}
		
		// 充值来源
		int rechargeSource = TypeEnum.MEMBER_LOGIN_SOURCE_PC.getType();
		if (otherParams.get("rechargeSource") != null) {
			rechargeSource = Integer.parseInt(otherParams.get("rechargeSource").toString());
		}
		
		return requestRecharge(memberId, payerIp, rechargeAmount, type, orderNo, tradeNo, returnUrl, isMobileSource, rechargeSource);
	}
	
	@Override
	public ResultDO<Object> createHostingWithdraw(Long memberId, String userIp, BigDecimal withdrawAmount, 
			BigDecimal freeWithdrawAmount, BigDecimal fee) throws Exception {
		return requestWithdraw(memberId, userIp, withdrawAmount, freeWithdrawAmount, null,null, fee, null);
	}
	
	// 请求提现
	private ResultDO<Object> requestWithdraw(Long memberId, String userIp, BigDecimal withdrawAmount, 
			BigDecimal freeWithdrawAmount, String returnUrl,String withdrawNo, BigDecimal fee, Integer withdrawSource) throws Exception {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		Member member = selectByPrimaryKey(memberId);
		// 如果未设置支付密码
		if (member.getPayPasswordFlag() == StatusEnum.SET_PAY_SUCCESS_FLAG_N.getStatus()) {
			resultDO.setResultCode(ResultCode.WITHDRAW_MUST_SET_PAY_PASSWORD_ERROR);
			return resultDO;
		}
		if (withdrawAmount != null) {
			withdrawAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		// 保存提现记录
		WithdrawLog withdrawLog = new WithdrawLog();
		withdrawLog.setWithdrawNo(withdrawNo);
		withdrawLog.setOuterWithdrawNo(withdrawNo);
		withdrawLog.setWithdrawAmount(withdrawAmount);
		withdrawLog.setMemberId(memberId);
		withdrawLog.setWithdrawFee(freeWithdrawAmount.intValue());
		withdrawLog.setUserIp(userIp);
		withdrawLog.setFee(fee);
		withdrawLog.setWithdrawSource(withdrawSource);
		//withdrawLog.setRemarks(DateUtils.getDateStrFromDate(DateUtils.getCurrentDate()));
		int saveFlag = withdrawLogManager.reqSubmitWithDraw(withdrawLog);
		// 调用新浪托管提现接口，返回提现form
		if (saveFlag > 0) {
			String result = sinaCashDeskClient.createHostingWithdraw(withdrawNo, memberId, userIp, withdrawAmount, returnUrl);
			if (StringUtil.isNotBlank(result)) {
				resultDO.setResult(result);
			} else {
				resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
				resultDO.getResultCode().setMsg("[托管提现]调用新浪接口失败 memberId = " + memberId + ",tradeNo = " 
						+ withdrawNo + ",userIp=" + userIp + ",withdrawAmount=" + withdrawAmount);
				logger.info(resultDO.getResultCode().getMsg());
				throw new ManagerException(resultDO.getResultCode().getMsg());
			}
		} else {
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
			resultDO.getResultCode().setMsg("[托管提现]保存提现记录失败 memberId = "  + memberId + ",tradeNo = " 
					+ withdrawNo + ",userIp=" + userIp + ",withdrawAmount=" + withdrawAmount);
			logger.info(resultDO.getResultCode().getMsg());
			throw new ManagerException(resultDO.getResultCode().getMsg());
		}
		return resultDO;
	}
	
	@Override
	public ResultDO<Object> createHostingWithdraw(Long memberId, String userIp, BigDecimal withdrawAmount, 
			BigDecimal freeWithdrawAmount, String returnUrl,String withdrawNo, BigDecimal fee, Integer withdrawSource) throws Exception {
		return requestWithdraw(memberId, userIp, withdrawAmount, freeWithdrawAmount, returnUrl, withdrawNo, fee, withdrawSource);
	}
	
	@Override
	public ResultDO<Object> updateMobileByOldMobile(Long memberId, Long newMobile, Long oldMobile) throws ManagerException {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		// 判断新手机号是否被注册
		Member memberObject = selectByMobile(newMobile);
		if (memberObject != null) {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.MEMBER_PHONE_NUMBER_ISEXIST_ERROR);
			return resultDO;
		}
		// 更改手机号码
		int udpateFlag = memberMapper.updateMobileByOldMobile(memberId, newMobile, oldMobile);
		if (udpateFlag == 0) {
			resultDO.setSuccess(false);
			logger.info("根据原手机号更新会员新手机号码，修改失败, oldMobile=" + oldMobile + ",newMobile=" + newMobile);
			return resultDO;
		}
		
		return resultDO;
	}
	
	@Override
	public ResultDO<Object> validateMemberInfo(Long oldMobile, String trueName, String identityNumber, String password) throws ManagerException {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		String decryptByYourong = CryptHelper.decryptByYourong(oldMobile.toString(), password);
		Member member = selectByMobile(oldMobile);
		// 判断用户的存在性
		if (member == null) {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
			return resultDO;
		}
		// 判断输入的密码是否正确
		if (!decryptByYourong.equals(member.getPassword())){
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.MEMBER_PASSWORD_ERROR);
			logger.info("用户登录密码错误 手机号码：" + oldMobile);
			return resultDO;
		}
		// 判断输入的姓名是否正确
		if (StringUtil.isBlank(trueName) || !member.getTrueName().equals(trueName)) {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.MEMBER_TRUENAME_ERROR);
			logger.info("姓名错误，手机号码：" + oldMobile);
			return resultDO;
		}
		// 判断输入的身份证号码是否有误
		if (StringUtil.isBlank(identityNumber) || !member.getIdentityNumber().equals(identityNumber)) {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.MEMBER_IDENTITY_ERROR);
			logger.info("身份证号码有误，手机号码：" + oldMobile);
			return resultDO;
		}
		return resultDO;
	}
	
	@Override
	public ResultDO<Object> updateMobileByIdentity(Long oldMobile, Long newMobile, String trueName, String identityNumber) throws ManagerException {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		Member member = selectByMobile(oldMobile);
		// 判断用户的存在性
		if (member == null) {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
			return resultDO;
		}
		//更改手机号码
		int udpateFlag = memberMapper.updateMobileByIdentity(newMobile, oldMobile, trueName, identityNumber);
		if (udpateFlag == 0) {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
			logger.error("根据会员身份信息更新手机号码，修改失败, oldMobile=" + oldMobile + ",newMobile=" + newMobile + ","
					+ "trueName=" + trueName + ",identityNumber=" + identityNumber);
			return resultDO;
		}
		
		return resultDO;
	}
	
	@Override
	public ResultDO<Object> updateNewMobilePassword(Long newMobile, String trueName, String identityNumber, String newPassword) throws ManagerException {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		Member memberObject = selectByMobile(newMobile);
		if (memberObject == null) {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.MEMBER_NOT_EXIST_ERROR);
			return resultDO;
		}
		// 核对姓名(根据会员身份信息修改手机号)
		if (StringUtil.isNotBlank(trueName) && !trueName.equals(memberObject.getTrueName())) {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.MEMBER_TRUENAME_ERROR);
			logger.info("姓名错误，手机号码：" + newMobile + "，trueName=" +trueName);
			return resultDO;
		}
		// 核对身份证号(根据会员身份信息修改手机号)
		if (StringUtil.isNotBlank(identityNumber) && !identityNumber.equals(memberObject.getIdentityNumber())) {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.MEMBER_IDENTITY_ERROR);
			logger.info("身份证号码有误，手机号码：" + newMobile + "，identityNumber=" +identityNumber);
			return resultDO;
		}
		
		memberObject.setPassword(CryptHelper.decryptByYourong(newMobile.toString(), newPassword));
		int updateResult = memberMapper.updatePasswordByMemberID(memberObject);
		if(updateResult > 0){
		    //密码修改成功号 清除登陆次数
			RedisMemberClient.removeMemberLoginCount(newMobile.toString());
			if(StringUtil.isNotBlank(memberObject.getUsername())){
				RedisMemberClient.removeMemberLoginCount(memberObject.getUsername()); 
			}
			MessageClient.sendMsgForUpdatePassword(DateUtils.getCurrentDate(), memberObject.getId());
		} else {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.MEMBER_UPDATE_PASSWORD_ERROR);
			logger.error("新手机号重置密码失败, mobile=" + newMobile);
		}
		
		return resultDO;
	}
	@Override
	public ResultDO<Object> autoInvestSet(Long memberId, int investFlag)
			throws ManagerException {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		AutoInvestSet biz=new AutoInvestSet();
		biz.setInvestFlag(investFlag);
		biz.setUpdateTime(DateUtils.getCurrentDate());
		biz.setSortTime(DateUtils.getCurrentDate());
		biz.setMemberId(memberId);
		int i=0;
		//开启
		if(investFlag==StatusEnum.INVEST_FLAG_OPEN.getStatus()){
			//是否开通委托支付
			Member member =memberMapper.selectByPrimaryKey(memberId);
			if (member == null
					|| StatusEnum.WITHHOLD_AUTHORITY_FLAG_Y.getStatus() != member.getWithholdAuthorityFlag()) {
				resultDO.setResultCode(ResultCode.AUTO_INVEST_AUTHORITY_ERROR);
				return resultDO;
			}
			AutoInvestSet autoInvest=	autoInvestMapper.selectByMemberId(memberId);
			if(autoInvest==null){
				//resultDO.setResultCode(ResultCode.AUTO_INVEST_NO_SAVE_ERROR);
				//return resultDO;
				//biz.setStartTime(DateUtils.getCurrentDate());
				//biz.setEndTime(DateUtils.addMonth(DateUtils.getCurrentDate(), 1));
				biz.setCreateTime(DateUtils.getCurrentDate());
				//biz.setInvestFrequency(1);
				//biz.setProjectType("1,2");
				//biz.setPeriodMin(1);
				//biz.setPeriodMax(3);
				//biz.setAmount(new BigDecimal(1000));
				i=autoInvestMapper.insertSelective(biz);
			}else{
				if(autoInvest.getInvestFlag()==StatusEnum.INVEST_FLAG_OPEN.getStatus()){
					resultDO.setResultCode(ResultCode.AUTO_INVEST_HAD_OPEN_ERROR);
					return resultDO;
				}
				i=autoInvestMapper.updateByPrimaryMemberIdSelective(biz);
			}
			if(i>0){
				AutoInvestSet setSort=autoInvestMapper.countMemberIdSort(memberId);
				if(setSort!=null){
					resultDO.setResult(setSort.getSort());
				}
				resultDO.setResultCode(ResultCode.AUTO_INVEST_OPEN_SUCCESS);
				resultDO.setSuccess(true);
				return resultDO;
			}else{
				resultDO.setSuccess(false);
				return resultDO;
			}
			
		//关闭	
		}else{
			AutoInvestSet autoInvest=	autoInvestMapper.selectByMemberId(memberId);
			if(autoInvest==null){
				resultDO.setResultCode(ResultCode.AUTO_INVEST_NO_SAVE_ERROR);
				return resultDO;
			}
			if(autoInvest.getInvestFlag()!=StatusEnum.INVEST_FLAG_OPEN.getStatus()){
				resultDO.setResultCode(ResultCode.AUTO_INVEST_HAD_CLOSE_ERROR);
				return resultDO;
			}
			i=autoInvestMapper.updateByPrimaryMemberIdSelective(biz);
			if(i>0){
				resultDO.setResultCode(ResultCode.AUTO_INVEST_CLOSE_SUCCESS);
				resultDO.setSuccess(true);
				return resultDO;
			}else{
				resultDO.setSuccess(false);
				return resultDO;
			}
			
		}
	}
	
	
	//获取用户信息新浪展示页面
	@Override
	public ResultDO<Object> showMemberInfosSina(Long memberId,String returnUrl) throws ManagerException {
			ResultDO<Object> resultDO = new ResultDO<Object>();
			Member member = selectByPrimaryKey(memberId);
			// 如果未实名认证
			if(member ==null || StringUtil.isBlank(member.getTrueName()) || StringUtil.isBlank(member.getIdentityNumber())){
				resultDO.setResultCode(ResultCode.MEMBER_UN_TRUE_NAME);
				return resultDO;
			}	
			// 调用获取用户信息新浪展示页面接口
			ResultDto<Object> resultDto = sinaCashDeskClient.showMemberInfosSina(memberId,returnUrl);
			if (resultDto.isError() || StringUtil.isBlank(resultDto.getModule().toString())) {
				resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
				resultDO.getResultCode().setMsg("errorCode=" + resultDto.getErrorCode() + ", errorCodeMsg=" + resultDto.getErrorMsg());
				logger.info("errorCode=" + resultDto.getErrorCode() + ", errorCodeMsg=" + resultDto.getErrorMsg()); 
				return resultDO;
			}
			resultDO.setResult(resultDto.getModule());
			return resultDO;
		}
	@Override
	public int countRegisterNumberByDate(Date registerTime) throws ManagerException {
		try{
			return memberMapper.countRegisterNumberByDate(DateUtils.zerolizedTime(registerTime),DateUtils.getEndTime(registerTime));
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<Member> registerMemberAndAuth(Long mobile, String password, String trueName, String identityNumber, int registerMethod,
			String channelBusiness, String ip) throws Exception {
		ResultDO<Member> result = new ResultDO<Member>();
		Member memberDto = new Member();
		result.setResult(memberDto);
		try {
			Member member = selectByMobile(mobile);
			if (member != null) {
				result.setResultCode(ResultCode.MEMBER_PHONE_NUMBER_ISEXIST_ERROR);
				return result;
			}
			if (isExitMemberByIdentityNumber(identityNumber)) {
				result.setResultCode(ResultCode.MEMBER_AUTH_IDENTITY_EXIT);
				return result;
			}
			memberDto.setMobile(mobile);
			memberDto.setPassword(password);
			memberDto.setTrueName(trueName);
			memberDto.setIdentityNumber(identityNumber);
			memberDto.setRegisterMethod(registerMethod);
			memberDto.setRegisterIp(ip);
			memberDto.setChannelBusiness(channelBusiness);
			// 插入会员表
			insertMemberAndPayAccount(memberDto);
			// 实名认证
			authThirdPay(memberDto.getId(), memberDto.getTrueName(), memberDto.getIdentityNumber(), result, memberDto.getMobile(), ip);
			if (result.isSuccess()) {
				insertAutch(memberDto);
			}
			// 插入会员信息表
			MemberInfo memberinfo = new MemberInfo();
			memberinfo.setMemberId(memberDto.getId());
			memberInfoManager.saveMemberInfoByMemberId(memberinfo);
			// 其他数据生成
			final Member memberForInit = memberDto;
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					initOtherMemberData(memberForInit);
				}
			});
		} catch (Exception e) {
			logger.error("插入会员失败  MemberDto=" + memberDto, e);
			result.setSuccess(false);
			result.setResultCode(ResultCode.MEMBER_REGISTER_ERROR);
			throw e;
		}
		return result;
	}
	
	/**
	 * 创建会员和 激活第三方支付账户
	 *
	 * @param
	 * @return
	 * @throws ManagerException
	 */
	private int insertMemberAndPayAccount(Member member) throws ManagerException {
		member.setRegisterTime(DateUtils.getCurrentDateTime());
		member.setDelFlag(Constant.ENABLE);
		member.setSex(StatusEnum.MEMBER_SEX_UN.getStatus());
		if (StringUtil.isBlank(member.getChannelBusiness())) {
			member.setStatus(StatusEnum.MEMBER_STATUS_ACTIVE.getStatus());
		} else {
			member.setStatus(StatusEnum.MEMBER_STATUS_UN_ACTIVE.getStatus());
		}
		member.setMemberType(1);
		member.setRegisterMethod(StatusEnum.MEMBER_SOURCE_BACKEND.getStatus());
		String shortUrl = Identities.randomBase62(6);
		Member shotMember = getMemberByShortUrl(shortUrl);
		if (shotMember == null) {
			member.setShortUrl(shortUrl);
		} else {
			member.setShortUrl(Identities.randomBase62(6));
		}
		Long mobile = member.getMobile();
		String password = member.getPassword();
		String decryptByYourong = CryptHelper.decryptByYourong(mobile.toString(), password);
		member.setPassword(decryptByYourong);
		int i = insertSelective(member);
		if (i > 0) {
			try {
				ResultDto<?> activateMemberResult = sinaPayClient.createActivateMember(member.getId(), MemberType.PERSONAL,
						member.getRegisterIp());
				if (activateMemberResult != null && activateMemberResult.isSuccess()) {
					return i;
				} else {
					String message = activateMemberResult == null ? "返回结果异常" : activateMemberResult.getErrorMsg();
					logger.error("创建会员异常,异常原因：message=" + message);
					throw new ManagerException("创建会员异常,异常原因：message=" + message);
				}

			} catch (Exception e) {
				i = 0;
				logger.error("调用第三方支付出现异常", e);
				throw new ManagerException("调用第三方支付出现异常", e);
			}
		}

		return i;
	}

	private void authThirdPay(long memberID, String realName, String certificateNumber, ResultDO<Member> resultDO, Long mobie,
			String clientIp) throws Exception {
		// 实名认证
		ResultDto<?> dto = sinaPayClient.setRealName(memberID, VerifyStatus.Y, certificateNumber, realName, clientIp);
		if (dto == null || dto.isError()) {
			resultDO.setSuccess(false);
			logger.error("实名认证失败 memberID={}, errorMsg={}", memberID, dto.getErrorMsg());
			throw new Exception("实名认证失败");
		}
	}

	public ResultDO<Object> insertAutch(Member memberDto) throws ManagerException {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			// 更新身份证已经生日信息
			Date birthday = DateUtils.getDateFromString(memberDto.getIdentityNumber().substring(6, 14), DateUtils.DATE_FMT_0);
			int sex = StringUtil.getSexByIdentityNumber(memberDto.getIdentityNumber());
			int rs = updateIdentityNumberById(memberDto.getTrueName(), memberDto.getIdentityNumber(), memberDto.getId(),
					birthday, sex);
			if (rs > 0) {
				// 添加认证信息
				MemberVerify record = new MemberVerify();
				record.setVerifyOperate(Constant.ENABLE);
				record.setVerifyType(TypeEnum.MEMBER_VERIFY_TYPE_IDENTITY.getCode());
				record.setMemberId(memberDto.getId());
				record.setVerifyContent(memberDto.getTrueName() + StringUtil.CARET + memberDto.getIdentityNumber());
				record.setCreateTime(DateUtils.getCurrentDateTime());
				record.setClientIp(memberDto.getRegisterIp());
				memberVerifyManager.insertSelective(record);
				resultDO.setSuccess(true);
			} else {
				// 认证失败
				resultDO.setResultCode(ResultCode.MEMBER_AUTH_IDENTITY_ERROR);
			}
		} catch (ManagerException e) {
			logger.error(ResultCode.MEMBER_AUTH_IDENTITY_ERROR.getMsg(), e);
			throw new ManagerException(e);

		}
		return resultDO;
	}

	private void initOtherMemberData(Member member) {
		// 初始化用户消息配置项
		try {
			memberNotifySettingsManager.initMemberNotifySettings(member.getId());
		} catch (ManagerException e) {
			logger.error("初始化消息通知配置异常, sourceID=" + member.getId());
		}
		// 初始化资金表
		Balance record = new Balance();
		record.setBalance(BigDecimal.ZERO);
		record.setAvailableBalance(BigDecimal.ZERO);
		record.setSourceId(member.getId());
		try {
			record.setBalanceType(TypeEnum.BALANCE_TYPE_PIGGY.getType());
			balanceManager.insert(record);
		} catch (ManagerException e) {
			logger.error("初始化存钱罐余额出异常, sourceID=" + member.getId());
		}
		try {
			record.setBalanceType(TypeEnum.BALANCE_TYPE_MEMBER_TOTAL_PIGGY.getType());
			balanceManager.insert(record);
		} catch (ManagerException e) {
			logger.error("初始化累计收益出异常, sourceID=" + member.getId());
		}
		try {
			record.setBalanceType(TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY.getType());
			balanceManager.insert(record);
		} catch (ManagerException e) {
			logger.error("初始化人气值余额出异常, sourceID=" + member.getId());
		}
		// 增加平台注册人数redis
		RedisPlatformClient.addMemberCount(1);
	}

	@Override
	public boolean needCheckNoviceProject(Long memberId) {
		boolean checkNoviceFlag = true;
		if (memberId == null || memberId == 0) {
			return checkNoviceFlag;
		}
		String memberIdStr = memberId.toString();
		SysDict noCheckNovice = null;
		try {
			noCheckNovice = sysDictManager.findByGroupNameAndKey(Constant.SPECIAL_MEMBER, Constant.NO_CHECK_NOVICE);
		} catch (ManagerException e) {
			logger.error("查询字典表报错!", e);
		}
		if (noCheckNovice == null || StringUtil.isBlank(noCheckNovice.getValue())) {
			return checkNoviceFlag;
		}
		String[] memberIds = noCheckNovice.getValue().split(Constant.DICT_VALUE_SPLIT);
		for (String id : memberIds) {
			if (id.equals(memberIdStr)) {
				checkNoviceFlag = false;
			}
		}
		return checkNoviceFlag;
	}

	@Override
	public int queryMemberReferralCount(Long memberid, Date starttime, Date endtime) {
		return memberMapper.queryMemberReferralCount(memberid,starttime,endtime);
	}

	@Override
	public int queryMemberReferralAndTransactionCount(Long memberid, Date starttime, Date endtime) {
		return memberMapper.queryMemberReferralAndTransactionCount(memberid,starttime,endtime);
	}
}
