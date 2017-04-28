package com.yourong.backend.ic.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.yourong.backend.BaseController;
import com.yourong.backend.ic.service.DebtService;
import com.yourong.backend.uc.service.MemberService;
import com.yourong.backend.utils.SysServiceUtils;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.StringUtil;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.ic.model.DebtBiz;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberBaseBiz;

@Controller
@RequestMapping("debt")
public class DebtController extends BaseController {

	@Autowired
	private MemberService memberService;
	@Autowired
	private DebtService debtService;

	/**
	 * 添加页面
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "add")
	@RequiresPermissions("debt:add")
	public String showIcDebtAdd(HttpServletRequest req, HttpServletResponse resp) {
		return "/ic/debt/form";
	}

	/**
	 * 添加债权
	 * 
	 * @param debtBiz
	 * @param req
	 * @param resp
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "save")
	@RequiresPermissions("debt:save")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "债权模块", desc = "保存债权")
	public ResultDO<Debt> saveDebt(@ModelAttribute("debtBiz") DebtBiz debtBiz, HttpServletRequest req,
			HttpServletResponse resp) {
		ResultDO<Debt> result = new ResultDO<Debt>();
		try {

			debtBiz.setStatus(StatusEnum.DEBT_STATUS_SAVE.getStatus());// 存盘
			// 项目形象图
			List<BscAttachment> signAttachments = parseJsonToObject(req, "signAttachmentsData", BscAttachment.class);
			// 原始债权人附件
			List<BscAttachment> lenderAttachments = parseJsonToObject(req, "lenderAttachmentsData", BscAttachment.class);
			// 借款人附件
			List<BscAttachment> borrowAttachments = parseJsonToObject(req, "borrowAttachmentsData", BscAttachment.class);
			// 质押物或抵押物附件
			List<BscAttachment> thingAttachments = parseJsonToObject(req, "thingAttachmentsData", BscAttachment.class);
			// 合同附件
			List<BscAttachment> contractAttachments = parseJsonToObject(req, "contractAttachmentsData",
					BscAttachment.class);
			// 法律意见书
			List<BscAttachment> legalAttachments = parseJsonToObject(req, "legalAttachmentsData", BscAttachment.class);
			// 其他资料图片
			List<BscAttachment> baseAttachments = parseJsonToObject(req, "baseAttachmentsData", BscAttachment.class);
			// 所有债权附件
			List<BscAttachment> debtAttachments = Lists.newArrayList();

			if (Collections3.isNotEmpty(signAttachments)) {
				debtAttachments.addAll(signAttachments);
			}
			
			if (Collections3.isNotEmpty(lenderAttachments)) {
				debtAttachments.addAll(lenderAttachments);
			}

			if (Collections3.isNotEmpty(borrowAttachments)) {
				debtAttachments.addAll(borrowAttachments);
			}
			if (Collections3.isNotEmpty(thingAttachments)) {
				debtAttachments.addAll(thingAttachments);
			}
			if (Collections3.isNotEmpty(contractAttachments)) {
				debtAttachments.addAll(contractAttachments);
			}
			if (Collections3.isNotEmpty(legalAttachments)) {
				debtAttachments.addAll(legalAttachments);
			}
			if (Collections3.isNotEmpty(baseAttachments)) {
				debtAttachments.addAll(baseAttachments);
			}
			debtBiz.setBscAttachments(debtAttachments);
			if (Collections3.isNotEmpty(debtBiz.getDebtInterests())) {
				if (debtBiz.getDebtInterests().size() > 0 && debtBiz.getDebtInterests().size() == 1
						&& debtBiz.getDebtInterests().get(0).getEndDate() == null) {
					debtBiz.getDebtInterests().remove(0);
				}
			}
			debtBiz.setPublishId(getCurrentLoginUserInfo().getId());
			String appPath = req.getSession().getServletContext().getRealPath("/");
			result = debtService.insertDebtInfo(debtBiz, appPath);
		} catch (Exception e) {
			result.setSuccess(false);
		}
		return result;
	}

	/**
	 * 列表页面
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("debt:index")
	public String showIcDebtIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "/ic/debt/index";
	}
	

	/**
	 * 列表数据
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
	@RequiresPermissions("debt:ajax")
	@ResponseBody
	public Object showDebtPages(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Page<Debt> pageRequest = new Page<Debt>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<Debt> pager = debtService.findByPage(pageRequest, map);
		return pager;
	}
	/**
	 * 编辑债权页面
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "show")
	@RequiresPermissions("debt:show")
	public ModelAndView showDebt(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView mv = new ModelAndView("/ic/debt/form");
		long id = ServletRequestUtils.getLongParameter(req, "id", 0);
		mv.addObject("id", id);
		mv.addObject("action", "show");
		return mv;
	}

	/**
	 * 紧急编辑债权页面
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "emergency")
	@RequiresPermissions("debt:emergency")
	public ModelAndView emergencyEditDebt(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView mv = new ModelAndView("/ic/debt/form");
		long id = ServletRequestUtils.getLongParameter(req, "id", 0);
		mv.addObject("id", id);
		mv.addObject("action", "emergency");
		return mv;
	}

	/**
	 * 查看债权页面
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "detail")
	@RequiresPermissions("debt:detail")
	public ModelAndView detailDebt(HttpServletRequest req, HttpServletResponse resp) {
		ModelAndView mv = new ModelAndView("/ic/debt/detail");
		long id = ServletRequestUtils.getLongParameter(req, "id", 0);
		mv.addObject("id", id);
		mv.addObject("action", "detail");
		return mv;
	}

	/**
	 * 获取债权信息
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "find")
	@RequiresPermissions("debt:find")
	@ResponseBody
	public Object findDebt(HttpServletRequest req, HttpServletResponse resp) {
		ModelMap map = new ModelMap();
		long id = ServletRequestUtils.getLongParameter(req, "id", 0);
		Debt debt = debtService.selectByPrimaryKey(id);
		DebtBiz debtBiz = debtService.getFullDebtInfoBySerialNumber(debt.getSerialNumber());
		if (debtBiz != null && Collections3.isNotEmpty(debtBiz.getBscAttachments())) {
			List<BscAttachment> signAttachments = Lists.newArrayList();
			List<BscAttachment> lenderAttachments = Lists.newArrayList();
			List<BscAttachment> borrowerAttachments = Lists.newArrayList();
			List<BscAttachment> collateralAttachments = Lists.newArrayList();
			List<BscAttachment> contractAttachments = Lists.newArrayList();
			List<BscAttachment> legalAttachments = Lists.newArrayList();
			List<BscAttachment> baseAttachments = Lists.newArrayList();
			for (BscAttachment bscAttachment : debtBiz.getBscAttachments()) {
				if (DebtEnum.ATTACHMENT_MODULE_DEBT_SIGN.getCode().equals(bscAttachment.getModule())) {
					signAttachments.add(bscAttachment);
					continue;
				}
				if (DebtEnum.ATTACHMENT_MODULE_DEBT_LENDER.getCode().equals(bscAttachment.getModule())) {
					lenderAttachments.add(bscAttachment);
					continue;
				}
				if (DebtEnum.ATTACHMENT_MODULE_DEBT_BORROWER.getCode().equals(bscAttachment.getModule())) {
					borrowerAttachments.add(bscAttachment);
					continue;
				}
				if (DebtEnum.ATTACHMENT_MODULE_DEBT_COLLATERAL.getCode().equals(bscAttachment.getModule())) {
					collateralAttachments.add(bscAttachment);
					continue;
				}
				if (DebtEnum.ATTACHMENT_MODULE_DEBT_CONTRACT.getCode().equals(bscAttachment.getModule())) {
					contractAttachments.add(bscAttachment);
					continue;
				}
				if (DebtEnum.ATTACHMENT_MODULE_DEBT_LEGAL.getCode().equals(bscAttachment.getModule())) {
					legalAttachments.add(bscAttachment);
					continue;
				}
				if (DebtEnum.ATTACHMENT_MODULE_DEBT_BASE.getCode().equals(bscAttachment.getModule())) {
					baseAttachments.add(bscAttachment);
					continue;
				}
			}
			map.put("signAttachments", signAttachments);
			map.put("lenderAttachments", lenderAttachments);
			map.put("borrowerAttachments", borrowerAttachments);
			map.put("collateralAttachments", collateralAttachments);
			map.put("contractAttachments", contractAttachments);
			map.put("legalAttachments", legalAttachments);
			map.put("baseAttachments", baseAttachments);
		}
	//	
		MemberBaseBiz lenderMember = new MemberBaseBiz();
		if(debt.getLenderType()==1){
			 lenderMember = debtService.selectMemberBaseBiz(debt.getLenderId());
		}else{
			 lenderMember = debtService.getMemberBaseBizByEnterpriseId(debt.getLenderEnterpriseId());
		}
		//判断如果是企业借款，则查询企业信息，如果是个人借款，则查询个人信息
		MemberBaseBiz borrowerMember = new MemberBaseBiz();
		if(debt.getEnterpriseId()!=null&&debt.getBorrowerType()==2){
			borrowerMember = debtService.getMemberBaseBizByEnterpriseId(debt.getEnterpriseId());
		}else{
			borrowerMember = debtService.selectMemberBaseBiz(debt.getBorrowerId());
		}
		map.put("debtBiz", debtBiz);
		map.put("lenderMember", lenderMember);
		map.put("borrowerMember", borrowerMember);
		return map;
	}

	/**
	 * 更新债权信息
	 * 
	 * @param debtBiz
	 * @param req
	 * @param resp
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "update")
	@RequiresPermissions("debt:update")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "债权模块", desc = "修改债权")
	public ResultDO<Debt> editDebt(@ModelAttribute("debtBiz") DebtBiz debtBiz, @ModelAttribute("action") String action,
			@ModelAttribute("signAttachmentsData") String signAttachmentsData,
			@ModelAttribute("lenderAttachmentsData") String lenderAttachmentsData,
			@ModelAttribute("borrowAttachmentsData") String borrowAttachmentsData,
			@ModelAttribute("thingAttachmentsData") String thingAttachmentsData,
			@ModelAttribute("contractAttachmentsData") String contractAttachmentsData,
			@ModelAttribute("legalAttachmentsData") String legalAttachmentsData,
			@ModelAttribute("baseAttachmentsData") String baseAttachmentsData,
 			HttpServletRequest req, HttpServletResponse resp) {
		ResultDO<Debt> result = new ResultDO<Debt>();
		try {
			//项目形象图
			List<BscAttachment> signAttachments = parseJsonToObject(req, "signAttachmentsData", BscAttachment.class);
			// 原始债权人附件
			List<BscAttachment> lenderAttachments = parseJsonToObject(req, "lenderAttachmentsData", BscAttachment.class);
			// 借款人附件
			List<BscAttachment> borrowAttachments = parseJsonToObject(req, "borrowAttachmentsData", BscAttachment.class);
			// 质押物或抵押物附件
			List<BscAttachment> thingAttachments = parseJsonToObject(req, "thingAttachmentsData", BscAttachment.class);
			// 合同附件
			List<BscAttachment> contractAttachments = parseJsonToObject(req, "contractAttachmentsData",
					BscAttachment.class);
			// 合同附件
			List<BscAttachment> legalAttachments = parseJsonToObject(req, "legalAttachmentsData", BscAttachment.class);
			// 合同附件
			List<BscAttachment> baseAttachments = parseJsonToObject(req, "baseAttachmentsData", BscAttachment.class);
			// 所有债权附件
			List<BscAttachment> debtAttachments = Lists.newArrayList();

			if (Collections3.isNotEmpty(signAttachments)) {
				debtAttachments.addAll(signAttachments);
			}
			
			if (Collections3.isNotEmpty(lenderAttachments)) {
				debtAttachments.addAll(lenderAttachments);
			}

			if (Collections3.isNotEmpty(borrowAttachments)) {
				debtAttachments.addAll(borrowAttachments);
			}
			if (Collections3.isNotEmpty(thingAttachments)) {
				debtAttachments.addAll(thingAttachments);
			}
			if (Collections3.isNotEmpty(contractAttachments)) {
				debtAttachments.addAll(contractAttachments);
			}
			if (Collections3.isNotEmpty(legalAttachments)) {
				debtAttachments.addAll(legalAttachments);
			}
			if (Collections3.isNotEmpty(baseAttachments)) {
				debtAttachments.addAll(baseAttachments);
			}
			debtBiz.setBscAttachments(debtAttachments);
			if (Collections3.isNotEmpty(debtBiz.getDebtInterests())) {
				if (debtBiz.getDebtInterests().size() <= 1 && debtBiz.getDebtInterests().get(0).getEndDate() == null) {
					debtBiz.getDebtInterests().remove(0);
				}
			}
			String appPath = req.getSession().getServletContext().getRealPath("/");
			if(action.equals("show")){
				//更新
				Debt debtStatus = debtService.findDebtAndProjectStatusByDebtId(debtBiz.getId());
				if (StringUtil.isNotBlank(debtStatus.getProjectStatus()) && !"1".equals(debtStatus.getProjectStatus())) {// 项目不在存盘状态
					result.setResultCode(ResultCode.DEBT_UPDATE_PROJECT_NOT_IN_SAVE_ERROR);
					return result;
				}
				debtBiz.setStatus(StatusEnum.DEBT_STATUS_SAVE.getStatus());// 存盘
				result = debtService.updateDebtInfo(debtBiz, appPath);
			}else{
				//紧急更新
				logger.info("紧急修改债权",debtBiz);
				SysServiceUtils.writeLogger("债权模块","紧急修改债权");
				result = debtService.emergencyUpdateDebtInfo(debtBiz, appPath);
			}
			
		} catch (Exception e) {
			logger.error("修改债权失败，debtBiz={}",debtBiz,e);
			result.setSuccess(false);
		}
		return result;
	}

	@RequestMapping(value = "delete")
	@RequiresPermissions("debt:delete")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "债权模块", desc = "删除债权")
	public ResultDO<Debt> deleteDebt(@ModelAttribute("id")long id,HttpServletRequest req, HttpServletResponse resp) {
		ResultDO<Debt> result = new ResultDO<Debt>();
		try {
			Integer delResult = debtService.deleteByDebtId(id);
			if (delResult <= 0) {
				result.setResultCode(ResultCode.DEBT_DELETE_FAIL_ERROR);
			}
		} catch (Exception e) {
			result.setSuccess(true);
		}
		return result;
	}

	/**
	 * 模糊查询会员信息列表
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "searchMember")
	@RequiresPermissions("debt:searchMember")
	@ResponseBody
	public Object searchMemberList(HttpServletRequest req, HttpServletResponse resp) {
		String name = req.getParameter("searchDbInforItem");
		String reg = "[^\u4e00-\u9fa5]";
		String truename=name.replaceAll(reg, "");
		// 判断是否输入的数字，即身份证号码
		String identityReg = "[^0-9]";
		String identityNumber = name.replaceAll(identityReg, "");
			
		String memberType = req.getParameter("memberType");
		List<Member> members = null;
		if (StringUtil.isNotBlank(truename) || StringUtil.isNotBlank(identityNumber)) {
			members = memberService.getMemberInfoByNameIdentity(truename, identityNumber, memberType);// 1：表示个人2：表示企业
		}
//		List<Member> list=Lists.newArrayList();
//		String temp="";
//		for(Member bean:members){
//			temp=bean.getTrueName()+bean.getIdentityNumber();
//			if(temp.contains(name)){
//				list.add(bean);
//			}
//		}
		return members;
	}
	
	/**
	 * 查询会员详细信息
	 * 
	 * @param req
	 * @param resp
	 * @param memberId
	 * @return
	 */
	@RequestMapping(value = "searchMemberInfo")
	@RequiresPermissions("debt:searchMemberInfo")
	@ResponseBody
	public Object searchMemberInfo(HttpServletRequest req, HttpServletResponse resp,@RequestParam("guarantyType")String guarantyType,
			@RequestParam("id") Long id) {
		MemberBaseBiz borrowerMember = new MemberBaseBiz();
		//if(guarantyType.equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_RUNCOMPANY.getCode()) && id!=null){
		if(id!=null){
			borrowerMember = debtService.getMemberBaseBizByEnterpriseId(id);
		}
		/*else{
			borrowerMember = debtService.selectMemberBaseBiz(id);
		}*/
		return borrowerMember;
	}
	
	/**
	 * 债权信息查询
	 * 
	 * @param req
	 * @param resp
	 * @param memberId
	 * @return
	 */
	@RequestMapping(value = "infoIndex")
	@RequiresPermissions("debt:infoIndex")
	public String showDebtInfoIndex(HttpServletRequest req, HttpServletResponse resp) {
		req.setAttribute("action", "debtInfo");
		return "/ic/debt/info";
	}
	
	/**
	 * 债权信息列表数据
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "infoAjax")
	@RequiresPermissions("debt:infoAjax")
	@ResponseBody
	public Object showDebtInfoPages(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Page<Debt> pageRequest = new Page<Debt>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<Debt> pager = debtService.findDebtInfoByPage(pageRequest, map);
		return pager;
	}
	
	/**
	 * 根据债权ID查询付息记录
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "showInterest")
	@RequiresPermissions("debt:showInterest")
	@ResponseBody
	public Object showInterestRecord(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Page<DebtInterest> pageRequest = new Page<DebtInterest>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("debtId", ServletRequestUtils.getLongParameter(req, "debtId"));
		Page<DebtInterest> pager = debtService.showInterestRecord(pageRequest, map);
		return pager;
	}
	
	
	@RequestMapping(value = "addControlRemarks")
	@RequiresPermissions("debt:addControlRemarks")
	@ResponseBody
	@LogInfoAnnotation( moduleName ="债权信息查询",desc = "添加备注")
	public Object addControlRemarks(@ModelAttribute("newControlRemarks")  String newControlRemarks, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		return debtService.addControlRemarks(id, newControlRemarks);
	}
	
	@RequestMapping(value="debtStatus")
	@ResponseBody
	public Object findDebtStatus(@ModelAttribute("id")Long id){
		return debtService.findDebtAndProjectStatusByDebtId(id);
	}
	
	/**
	 * 查询会员详细信息
	 * 
	 * @param req
	 * @param resp
	 * @param memberId
	 * @return
	 */
	@RequestMapping(value = "searchEnterprise")
	@RequiresPermissions("debt:searchEnterprise")
	@ResponseBody
	public Object searchEnterprise(HttpServletRequest req, HttpServletResponse resp,
			@ModelAttribute("enterpriseName") String enterpriseName) {
		return memberService.getEnterpriseByName(enterpriseName);
	}
	
	/**
	 * 
	 * @Description:查询企业注册号
	 * @param req
	 * @param resp
	 * @param regisNo
	 * @return
	 * @author: chaisen
	 * @time:2016年12月20日 上午11:27:48
	 */
	@RequestMapping(value = "searchRegisNo")
	@RequiresPermissions("debt:searchRegisNo")
	@ResponseBody
	public Object searchRegisNo(HttpServletRequest req, HttpServletResponse resp,
			@ModelAttribute("regisNo") String regisNo) {
		return memberService.getEnterpriseByRegisNo(regisNo);
	}
	
	/**
	 * 查询企业详细信息
	 * 
	 * @param req
	 * @param resp
	 * @param memberId
	 * @return
	 */
	@RequestMapping(value = "searchEnterpriseLegal")
	@RequiresPermissions("debt:searchEnterpriseLegal")
	@ResponseBody
	public Object searchEnterpriseByLegalName(HttpServletRequest req, HttpServletResponse resp,
			@ModelAttribute("legalname") String legalname) {
		return memberService.getEnterpriseByLegalName(legalname);
	}
	/**
	 * 
	 * @Description:自动分期
	 * @param req
	 * @param resp
	 * @param returnType
	 * @param interestFrom
	 * @param sDate
	 * @param eDate
	 * @return
	 * @author: chaisen
	 * @time:2016年5月4日 上午10:44:16
	 */
	@RequestMapping(value="autoInterest")
	@ResponseBody
	public Object autoInterest(HttpServletRequest req, HttpServletResponse resp,@ModelAttribute("returnType") String returnType,@ModelAttribute("interestFrom")int interestFrom,
			@ModelAttribute("sDate") String sDate,@ModelAttribute("eDate") String eDate){
		ResultDO<DebtInterest> result = new ResultDO<DebtInterest>();
		List<DebtInterest> list=debtService.autoInterest(returnType,interestFrom,sDate,eDate);
		result.setSuccess(true);
		result.setResultList(list);
		return result;
	}
	
}
