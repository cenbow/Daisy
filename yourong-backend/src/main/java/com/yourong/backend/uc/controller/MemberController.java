package com.yourong.backend.uc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yourong.core.uc.model.biz.MemberBiz;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.yourong.backend.BaseController;
import com.yourong.backend.uc.service.MemberBankCardService;
import com.yourong.backend.uc.service.MemberInfoService;
import com.yourong.backend.uc.service.MemberService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.VerifyType;
import com.yourong.common.thirdparty.sinapay.member.exception.MemberGatewayInvokeFailureException;
import com.yourong.common.thirdparty.sms.SmsMobileSend;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.Identities;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.RegexUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.mc.model.WinxinTemplate;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.uc.manager.EnterpriseManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Enterprise;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberBankCard;
import com.yourong.core.uc.model.MemberBaseBiz;
import com.yourong.core.uc.model.MemberInfo;
import com.yourong.core.uc.model.biz.MemberInfoBiz;

@Controller
@RequestMapping("member")
public class MemberController extends BaseController {

	private static final String MEMBER_CHANGER_MOBILE_NUMBER = "member_changer_mobile_number";
	
	private static final String[] ATTACHMENT_TYPE = { "stampAttachmentsData"};
	
	@Autowired
	private MemberService memberService;

	@Autowired
	private MemberInfoService memberInfoService;

	@Autowired
	private MemberBankCardService memberBankCardService;

	@Autowired
	private SinaPayClient sinaPayClient;

	@Autowired
	private MemberManager memberManager;

	@Autowired
	private BalanceManager balanceManager;

	@Autowired
	private SmsMobileSend smsMobileSend;

	@Autowired
	private EnterpriseManager enterpriseManager;
	
	@Autowired
	private SysDictManager sysDictManager;
	
	/**
	 * 客户信息首页
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("member:index")
	public String showMemberIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "/uc/member/index";
	}


	/**
	 * 分页查询客户信息
	 *
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "ajax")
	@RequiresPermissions("member:ajax")
	@ResponseBody
	public Object showMemberPages(HttpServletRequest req,
								  HttpServletResponse resp) throws ServletRequestBindingException {
		Page<MemberBiz> pageRequest = new Page<MemberBiz>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<MemberBiz> pager = memberService.findByPage(pageRequest, map);
		return pager;
	}


	/**
	 * 删除客户
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "delete")
	@RequiresPermissions("member:delete")
	@ResponseBody
	@LogInfoAnnotation( moduleName ="会员管理",desc = "删除会员")
	public Object deleteMember(HttpServletRequest req, HttpServletResponse resp) {
		long[] ids = getParametersIds(req, "ids");
		return memberService.batchDelete(ids);
	}

	/**
	 * 查看客户资料
	 *
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "show")
	@RequiresPermissions("member:show")
	@ResponseBody
	public Object showMember(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		List list = Lists.newArrayList();
		Member member = memberService.getMemberById(id);
		if (member != null) {
			list.add(member);
			MemberInfo info = memberInfoService.getMemberInfoByMemberId(member.getId());
			if (info != null) {
				list.add(info);
			}
		}
		return list;
	}

	@RequestMapping(value = "findMemberBankCard")
	@RequiresPermissions("member:findMemberBankCard")
	@ResponseBody
	public Object findMemberBankCardByMemberId(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Long memberId = ServletRequestUtils.getLongParameter(req, "memberId");
		Page<MemberBankCard> pageRequest = new Page<MemberBankCard>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		map.put("memberId", memberId);
		Page<MemberBankCard> pager = memberBankCardService.findByPage(pageRequest, map);
		return pager;
	}

	@RequestMapping(value = "queryBalance")
	@RequiresPermissions("member:queryBalance")
	@ResponseBody
	public Object queryBalance(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Long memberId = ServletRequestUtils.getLongParameter(req, "memberId");
		ResultDto<Balance> resultDto = new ResultDto<>();
		try {
			Member selectByMobile = memberManager.selectByMobile(memberId);
			Balance balance = balanceManager.synchronizedBalance(selectByMobile.getId(), TypeEnum.BALANCE_TYPE_PIGGY);
			resultDto.setModule(balance);
			resultDto.setSuccess(true);
		} catch (Exception e) {
			resultDto.setSuccess(false);
			resultDto.setErrorMsg("第三方支付查询余额失败");
		}
		return resultDto;
//		if (resultDto.isSuccess()) {
//			String result = "余额:" + resultDto.getModule().getBalance().toPlainString() + ",可用余额:"
//					+ resultDto.getModule().getAvailableBalance().toEngineeringString();
//			return result;
//		} else {
//			return resultDto.getErrorMsg();
//		}
	}
	
	@RequestMapping(value = "queryProfit")
	@RequiresPermissions("member:queryProfit")
	@ResponseBody
	public Object queryProfit(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Long memberId = ServletRequestUtils.getLongParameter(req, "memberId");
		ResultDto<Balance> resultDto = new ResultDto<>();
		try {
			Member selectByMobile = memberManager.selectByMobile(memberId);
			balanceManager.synThirdPayEaring(selectByMobile.getId());
			resultDto.setSuccess(true);
		} catch (Exception e) {
			resultDto.setSuccess(false);
			resultDto.setErrorMsg("同步收益失败");
		}
		return resultDto;
	}

	/**
	 * 客户信息首页
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "showMemberqueryBalance")
	@RequiresPermissions("member:showMemberqueryBalance")
	public String showMemberqueryBalance(HttpServletRequest req, HttpServletResponse resp) {
		return "/uc/member/memberBalance";
	}

	@RequestMapping(value = "addMemberPage")
	@RequiresPermissions("member:addMemberPage")
	public String addMemberPage(HttpServletRequest req, HttpServletResponse resp) {
		return "/uc/member/addMember";
	}

	@RequestMapping(value = "cancleMobile")
	@RequiresPermissions("member:cancleMobile")
	@ResponseBody
	@LogInfoAnnotation( moduleName ="会员管理",desc = "新浪存钱罐更换手机号码")
	public ResultDto cancelMobile(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ResultDto<?> resultDto = new ResultDto();
		try {
			Long id  =ServletRequestUtils.getLongParameter(req, "uid");
			SysDict sysDict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
        	String clientIp = "";
        	if(sysDict != null) {
        		clientIp = sysDict.getValue();
        	}
			resultDto = sinaPayClient.unbindingVerify(id, VerifyType.MOBILE, clientIp);
			return resultDto;
		} catch (MemberGatewayInvokeFailureException e) {
		     logger.error("id");
		}
		return resultDto;
	}
	@RequestMapping(value = "cancleMobilePage")
	@RequiresPermissions("member:cancleMobile")
	public String cancelMobilePage(HttpServletRequest req, HttpServletResponse resp) {
		return "/uc/member/cancleMobile";
	}


	/**
	 * 后台注册会员
	 *
	 * @param member
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "regedistMember")
	@RequiresPermissions("member:regedistMember")
	@ResponseBody
	@LogInfoAnnotation( moduleName ="会员管理",desc = "后台添加会员")
	public Object regedistMember(@ModelAttribute("member") Member member, @ModelAttribute("memberInfo") MemberInfo memberInfo, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ResultDO<Object> resultDO = new ResultDO<>();
		if (StringUtil.isBlank(member.getTrueName()) || StringUtil.isBlank(member.getIdentityNumber())) {
			resultDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
			resultDO.setResult(ResultCode.MEMBER_AUTH_IDENTITY_EXIT.getMsg());
			resultDO.setSuccess(false);
			return resultDO;
		}
		if (memberManager.isExitMemberByIdentityNumber(member.getIdentityNumber())) {
			resultDO.setResultCode(ResultCode.MEMBER_AUTH_IDENTITY_EXIT);
			resultDO.setResult(ResultCode.MEMBER_AUTH_IDENTITY_EXIT.getMsg());
			resultDO.setSuccess(false);
			return resultDO;
		}
		try {
			resultDO = this.memberService.register(member);
			memberInfo.setMemberId(member.getId());
			this.memberInfoService.saveMemberInfoByMemberId(memberInfo);
			if (resultDO.isError())
				return resultDO;
			member = this.memberManager.selectByMobile(member.getMobile());
			if (resultDO.isSuccess()) {
				this.memberService.initOtherMemberData(member);
				//this.memberService.saveMemberOpen(member);
			}
		} catch (Exception e) {
			resultDO.setResultCode(ResultCode.MEMBER_AUTH_IDENTITY_ERROR);
			resultDO.setSuccess(false);
		}
		return resultDO;
	}

	@RequestMapping(value = "sendMobileCode")
	@RequiresPermissions("member:sendMobileCode")
	@ResponseBody
	@LogInfoAnnotation( moduleName ="会员管理",desc = "后台发送手机验证码")
	public Object sendMobileCode(@ModelAttribute("newMobile") Long newMobile , HttpServletRequest req, HttpServletResponse resp) throws Exception {
		boolean checkMobile = RegexUtils.checkMobile(String.valueOf(newMobile));
		if (!checkMobile) {
			return false;
		}
		String smsContent = Identities.randomNumberLength(Constant.SEND_SMS_LENGTH);
		req.getSession().setAttribute(MEMBER_CHANGER_MOBILE_NUMBER, smsContent);
		if (PropertiesUtil.isDev()) {
			logger.info("短信随机验证码 smsContent--" + smsContent);
			return true;
		} else {
			logger.info("短信随机验证码 smsContent--" + smsContent);
			ResultDO<Integer> integerResultDO = smsMobileSend.sendSMSVerificationCode(newMobile, smsContent);
			if (integerResultDO.isSuccess())
				return true;
		}
		return false;
	}

	@RequestMapping(value = "changeMobile")
	@RequiresPermissions("member:changeMobile")
	@ResponseBody
	@LogInfoAnnotation( moduleName ="会员管理",desc = "会员改变手机号码")
	public Object changeMobile(@ModelAttribute("newMobile")  Long newMobile , @ModelAttribute("member") Member member, HttpServletRequest req, HttpServletResponse resp) throws Exception {
	//	Long newMobile = ServletRequestUtils.getLongParameter(req, "newMobile");
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		String token = ServletRequestUtils.getStringParameter(req, "token");
		String smsContent = Identities.randomNumberLength(Constant.SEND_SMS_LENGTH);
		String sessionToken = (String) req.getSession().getAttribute(MEMBER_CHANGER_MOBILE_NUMBER);
		ResultDO<Object> resultDO = new ResultDO<>();
		if (StringUtil.equals(token, sessionToken, true)) {
			resultDO = this.memberService.changeMobile(id, newMobile);
		} else {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.MEMBER_CAPTCHA_ERROR);
		}
		return resultDO;
	}


	@RequestMapping(value = "saveUcEnterprise")
	@RequiresPermissions("member:saveUcEnterprise")
	@ResponseBody
	@LogInfoAnnotation( moduleName ="会员管理",desc = "会员保存企业信息")
	public Object saveUcEnterprise(@ModelAttribute("enterprise") Enterprise enterprise, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return memberService.saveMemberEnterprise(enterprise);
	}

	@RequestMapping(value = "geEnterprise")
	@RequiresPermissions("member:saveUcEnterprise")
	@ResponseBody
	public Object getEnterprise(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Long memberID = ServletRequestUtils.getLongParameter(req, "memberID", 0L);

		Enterprise enterprise = this.enterpriseManager.selectByMemberID(memberID);
		if(enterprise == null){
			Member member = this.memberManager.selectByPrimaryKey(memberID);
			enterprise = new Enterprise();
			enterprise.setLegalId(memberID);
			enterprise.setLegalName(member.getTrueName());
			enterprise.setIdentity(member.getIdentityNumber());
			return  enterprise;
		}else{
			Balance balance = balanceManager.queryBalance(enterprise.getId(), TypeEnum.BALANCE_TYPE_COMPANY_CREDIT_AMOUNT);
			if(balance!=null){
				enterprise.setCreditAmount(balance.getBalance());
			}
		}
		
		return enterprise;
	}
	/**
	 * 
	 * @Description:新增
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年4月19日 上午10:33:35
	 */
	@RequestMapping(value = "addEnterprise")
	@RequiresPermissions("member:addEnterprise")
	@ResponseBody
	public Object addEnterprise(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Long memberID = ServletRequestUtils.getLongParameter(req, "memberID", 0L);
		Enterprise enterprise = new Enterprise();
		Member member = this.memberManager.selectByPrimaryKey(memberID);
		enterprise = new Enterprise();
		enterprise.setLegalId(memberID);
		enterprise.setLegalName(member.getTrueName());
		enterprise.setIdentity(member.getIdentityNumber());
		return enterprise;
	}

	/**
	 * 用户查询页面
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "selectIndex")
	@RequiresPermissions("member:selectMember")
	public String searchMemberPage(HttpServletRequest req, HttpServletResponse resp) {
		req.setAttribute("action", "selectMember");
		return "/uc/member/selectMember";
	}


	/**
	 * 用户查询
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException 
	 */
	@RequestMapping(value = "selectMember")
	@RequiresPermissions("member:selectMember")
	@ResponseBody
	public Page<Member> searchMember(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Page<Member> pageRequest = new Page<Member>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		List<Member> memberList = memberService.selectMember(map);
		if(Collections3.isNotEmpty(memberList)){
			pageRequest.setiTotalDisplayRecords(memberList.size());
			pageRequest.setiTotalRecords(memberList.size());
			pageRequest.setData(memberList);
		}else{
			pageRequest.setiTotalDisplayRecords(0);
			pageRequest.setiTotalRecords(0);
			pageRequest.setData(new ArrayList());
		}
		return pageRequest;
	}
	
	/**
	 * 查询用户详情信息
	 * @param req
	 * @param resp
	 * @param memberId
	 * @return
	 */
	@RequestMapping(value = "findMemberInfo")
	@RequiresPermissions("selectMember:findMemberInfo")
	@ResponseBody
	public ResultDO<MemberBaseBiz> searchMemberInfo(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam("memberId") Long memberId) {
		ResultDO<MemberBaseBiz> resultDO = new ResultDO<MemberBaseBiz>();
		try {
			MemberBaseBiz biz = memberManager.selectMemberBaseBiz(memberId);
			resultDO.setResult(biz);
		} catch (ManagerException e) {
			resultDO.setSuccess(false);
		}
		return resultDO;
	}

	/**
	 * 注销会员
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "cancelMember")
	@RequiresPermissions("member:cancelMember")
	@ResponseBody
	public Object cancelMember(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		return memberService.cancelMember(id);
	}

	
	
	
	/**
	 *	删除用户绑定银行卡功能(目前只将权限设置为开发人员可用)
	 */
	@RequestMapping(value = "addReferral")
	@RequiresPermissions("member:addReferral")
	@ResponseBody
	public ResultDO<Object> addReferral(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Long referredId = ServletRequestUtils.getLongParameter(req, "referredId", 0L);
		Long referralMobile = ServletRequestUtils.getLongParameter(req, "referralMobile", 0L);
		return memberService.addReferral(referredId, referralMobile);
	}
	
	/**
	 *	同步存钱罐余额
	 */
	@RequestMapping(value = "synThirdPayEaring")
	@RequiresPermissions("member:synThirdPayEaring")
	@ResponseBody
	public Object synThirdPayEaring(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ResultDO<Member> member = new ResultDO<Member>();
		Long memberId = ServletRequestUtils.getLongParameter(req, "memberId", 0L);
		member.setSuccess(false);
		if(memberService.synThirdPayEaring(memberId)){
			member.setSuccess(true);
		}
		return member;
	}
	
	/**
	 * @Description:获取会员信息
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年4月1日 下午1:07:10
	 */
	@RequestMapping(value = "showMemberInfo")
	@ResponseBody
	public Object showMemberInfo(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		MemberInfoBiz biz=new MemberInfoBiz();
		Member member = memberService.getMemberById(id);
		if(member!=null){
			biz.setUsername(member.getUsername());
			biz.setTrueName(member.getTrueName());
			biz.setMobile(member.getMobile());
			biz.setIdentityNumber(member.getIdentityNumber());
			biz.setMemberId(id);
		}
		MemberInfo memberInfo=memberInfoService.getMemberInfoByMemberId(id);
		if(memberInfo!=null){
			biz.setMarriage(memberInfo.getMarriage());
			biz.setHighEdu(memberInfo.getHighEdu());
			biz.setRegisterType(memberInfo.getRegisterType());
			biz.setIncome(memberInfo.getIncome());
			biz.setAddress(memberInfo.getAddress());
			biz.setDetailInfo(memberInfo.getDetailInfo());
			biz.setCity(memberInfo.getCity());
			biz.setCensusRegisterId(memberInfo.getCensusRegisterId());
		}
		return biz;
	}
	/**
	 * 
	 * @Description:保存会员信息
	 * @param memberInfoBiz
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年4月5日 上午10:19:23
	 */
	@RequestMapping(value = "saveUcMemberInfo")
	@RequiresPermissions("member:saveUcMemberInfo")
	@ResponseBody
	@LogInfoAnnotation( moduleName ="会员管理",desc = "保存会员信息")
	public Object saveUcMemberInfo(@ModelAttribute("memberInfoBiz") MemberInfoBiz memberInfoBiz, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return memberService.saveUcMemberInfo(memberInfoBiz);
	}
	/**
	 * 
	 * @Description:会员详情
	 * @param id
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年4月18日 下午3:56:38
	 */
	@RequestMapping(value = "showDetail")
	@RequiresPermissions("member:showDetail")
	public ModelAndView showEdit(@RequestParam("id") Long id, HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/uc/member/showDetail");
		mv.addObject("id", id);
		Member member = memberService.getMemberById(id);
		if(member!=null){
			mv.addObject("memberName", member.getTrueName());
		}
		mv.addObject("action", "show");
		return mv;
	}
	/**
	 * 
	 * @Description:企业列表
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年4月19日 上午10:01:23
	 */
	@RequestMapping(value = "findMemberEnterprise")
	@RequiresPermissions("member:findMemberEnterprise")
	@ResponseBody
	public Object findMemberEnterpriseByMemberId(HttpServletRequest req, HttpServletResponse resp) throws  Exception {
		Long memberId = ServletRequestUtils.getLongParameter(req, "memberId");
		Integer type = ServletRequestUtils.getIntParameter(req, "type");
		Page<Enterprise> pageRequest = new Page<Enterprise>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		map.put("legalId", memberId);
		map.put("type", type);
		Page<Enterprise> pager = enterpriseManager.findByPage(pageRequest, map);
		return pager;
	}
	/**
	 * 
	 * @Description:获取企业信息
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年4月19日 上午11:57:38
	 */
	@RequestMapping(value = "getEnterpriseDetail")
	@RequiresPermissions("member:getEnterpriseDetail")
	@ResponseBody
	public Object getEnterpriseDetail(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Long enterpriseID = ServletRequestUtils.getLongParameter(req, "enterpriseID", 0L);
		Enterprise enterprise = this.enterpriseManager.selectByKey(enterpriseID);
		if(enterprise!=null){
			Balance balance = balanceManager.queryBalance(enterprise.getId(), TypeEnum.BALANCE_TYPE_COMPANY_CREDIT_AMOUNT);
			if(balance!=null){
				enterprise.setCreditAmount(balance.getBalance());
			}
		}
		return enterprise;
	}
	/**
	 * 
	 * @Description:删除企业
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年4月19日 下午1:29:20
	 */
	 @RequestMapping(value = "delEnterprise")
	 @RequiresPermissions("member:delEnterprise")
	 @ResponseBody
	 public Object delEnterprise(HttpServletRequest req, HttpServletResponse resp) throws ManagerException {
		 ResultDO<WinxinTemplate> result = new ResultDO<WinxinTemplate>();
		Long enterpriseID = ServletRequestUtils.getLongParameter(req, "enterpriseID", 0L);
		if(enterpriseManager.checkIfUse(enterpriseID)){
			result.setSuccess(false);
			result.setResultCode(ResultCode.ENTERPRISE_NOT_DEL);
			return result;
		}
		int i= enterpriseManager.deleteByPrimaryKey(enterpriseID);
		if(i>0){
			result.setSuccess(true);
		}else{
			result.setSuccess(false);
		}
		return result;
	  }
	 

	@RequestMapping(value = "caEnterprise")
	@RequiresPermissions("member:caEnterprise")
	@ResponseBody
	public Object caEnterprise(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ResultDO<Enterprise> result = new ResultDO<Enterprise>();
		Long id = ServletRequestUtils.getLongParameter(req, "id", 0L);
		
		Enterprise enterprise =memberService.caEnterprise(id);
		if(enterprise != null){
			result.setResult(enterprise);
			return  result;
		}
		result.setSuccess(false);
		return result;
	}
	
	@RequestMapping(value = "caApply")
	@RequiresPermissions("member:caApply")
	@ResponseBody
	public Object caApply(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ResultDO<Enterprise> result = new ResultDO<Enterprise>();
		Long enterpriseId = ServletRequestUtils.getLongParameter(req, "enterpriseId", 0L);
		return memberService.enterpriseCa(enterpriseId);
	}
	
	/**
	 * 企业图片上传
	 */
	@RequestMapping(value = "saveImage")
	@RequiresPermissions("member:saveImage")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "会员模块", desc = "企业图片上传")
	public ResultDO<Object> saveImage(	HttpServletRequest req, HttpServletResponse resp) {
		ResultDO<Object> result = new ResultDO<Object>();
		Long enterpriseId = ServletRequestUtils.getLongParameter(req, "enterpriseId", 0L);
			// 所有附件类型
		String appPath = req.getSession().getServletContext().getRealPath("/");
		List<BscAttachment> stampAttachment = Lists.newArrayList();
		stampAttachment = parseJsonToObject(req, ATTACHMENT_TYPE[0], BscAttachment.class);
		return	memberService.saveImage(enterpriseId, stampAttachment,appPath);
	}
	
	 
	 
	@RequestMapping(value = "getMemberInfo")
	@ResponseBody
	public Object getMemberInfo(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		MemberInfoBiz biz=new MemberInfoBiz();
		Member member = memberService.getMemberById(id);
		if(member!=null){
			biz.setTrueName(member.getTrueName());
			biz.setMobile(member.getMobile());
			biz.setMemberId(member.getId());
		}
		//Balance balance = balanceManager.queryBalance(id, TypeEnum.BALANCE_TYPE_PIGGY);
		try {
			Balance balance = balanceManager.synchronizedBalance(id, TypeEnum.BALANCE_TYPE_PIGGY);
			if(balance!=null){
				biz.setAvailableBalance(balance.getAvailableBalance());
			}
		} catch (ManagerException e) {
			logger.error("第三方支付查询存钱罐余额失败");
		}
			return biz;
		}
	
	/**
	 * 根据会员手机号查询ID
	 *
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "mobileMemberId")
	@RequiresPermissions("member:mobileMemberId")
	public String showMobileMember(HttpServletRequest req, HttpServletResponse resp) {
		return "/uc/member/mobileMember";
	}
	
	/**
	 * 查看ID
	 *
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "showMemberId")
	@ResponseBody
	public Object showMemberId(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Long mobile = ServletRequestUtils.getLongParameter(req, "mobile");
		Member member = memberService.getMemberByMobile(mobile);
		String memberId = "";
		if (member != null) {
			memberId = member.getId().toString();
			
		}
		return memberId;
	}
	
	/**
	 * 查看mobile
	 *
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "showMobile")
	@ResponseBody
	public Object showMobile(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Long id = ServletRequestUtils.getLongParameter(req, "memberId");
		Member member = memberService.getMemberById(id);
		String mobile = "";
		if (member != null) {
			mobile = member.getMobile().toString();
		}
		return mobile;
	}
	 
}
