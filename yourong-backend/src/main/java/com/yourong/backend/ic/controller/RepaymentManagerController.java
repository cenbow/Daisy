package com.yourong.backend.ic.controller;

import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import antlr.StringUtils;

import com.google.common.collect.Maps;
import com.yourong.backend.BaseController;
import com.yourong.backend.ic.service.ProjectService;
import com.yourong.backend.utils.SysServiceUtils;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.util.StringUtil;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.UnderwriteLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.UnderwriteLog;
import com.yourong.core.fin.model.biz.BalanceBiz;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.ic.model.DirectProjectBiz;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.ProjectInterestBiz;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.sys.model.SysUser;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;
	
/**
 * 
 * @desc 还本付息管理
 * @author chaisen
 * 2015年12月30日下午3:16:59
 */
@Controller
@RequestMapping("repaymentManager")
public class RepaymentManagerController extends BaseController{
	
	protected Logger logger = LoggerFactory.getLogger(BaseController.class);
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
    private MemberManager memberManager;
	
	@Autowired
    private BalanceManager balanceManager;
	
	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private SysDictManager sysDictManager;
	
	@Autowired
	private UnderwriteLogManager underWriteLogManager;
	/**
	 * 
	 * @Description:跳转到还本付息管理页面
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年1月5日 下午6:53:17
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("repaymentManager:index")
	public String repayInterest(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		return "/p2p/repayInterest/index";
	} 
	
	/**
	 * 
	 * @Description:分页查询还本付息列表
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: chaisen
	 * @time:2016年1月6日 上午9:54:22
	 */
	@RequestMapping(value = "ajaxRepayment")
	@RequiresPermissions("repaymentManager:ajaxRepayment")
	@ResponseBody
	public Object findInterestList(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		Page<ProjectInterestBiz> pageRequest = new Page<ProjectInterestBiz>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<ProjectInterestBiz> pager = projectService.findRepayInterestForPagin(pageRequest, map);
		return pager;
	}
	
	/**
	 * 
	 * @Description:垫资代还
	 * @param project
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年1月7日 下午2:02:15
	 */
	@RequestMapping(value = "saveRepayment")
	@RequiresPermissions("repaymentManager:saveRepayment")
	@ResponseBody
	@LogInfoAnnotation( moduleName ="还本付息",desc = "垫资代还")
	public Object saveRepayment(@ModelAttribute("project") ProjectInterestBiz project, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return projectService.saveRepayment(project);
	}
	/**
	 * 
	 * @Description:获取第三方垫资公司余额
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年1月7日 下午8:01:04
	 */
	@RequestMapping(value="getThirdAccountMoney")
	@ResponseBody
	public BigDecimal getThirdAccountMoney(@ModelAttribute("memberId")Long memberId){
		return projectService.getThirdAccountMoney(memberId);
	}
	
	/**
	 * 
	 * @Description:同步第三方账户余额
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年1月26日 下午1:23:12
	 */
	@RequestMapping(value="synchronizedBalance")
	@ResponseBody
	public Object synchronizedBalance(HttpServletRequest req, HttpServletResponse resp) throws ManagerException{
		return projectService.synchronizedBalance();
	}
	
	/**
	 * 查询借款人余额
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "queryBalance")
	@ResponseBody
	public Object queryBalance(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Long projectId = ServletRequestUtils.getLongParameter(req, "projectId");
		ResultDto<BalanceBiz> resultDto = new ResultDto<>();
		BalanceBiz biz=new BalanceBiz();
		try {
			Project project=projectManager.selectByPrimaryKey(projectId);
			String channelId="";
			if(project!=null){
				Balance balance = balanceManager.synchronizedBalance(project.getBorrowerId(), TypeEnum.BALANCE_TYPE_PIGGY);
				if(balance!=null){
					biz.setBalance(balance.getBalance());
					biz.setAvailableBalance(balance.getAvailableBalance());
				}
				Member member=memberManager.selectByPrimaryKey(project.getBorrowerId());
				if(member!=null){
					biz.setBorrowerName(member.getTrueName());
				}
				if(project.getOpenPlatformKey()!=null){
					biz.setFlag(true);
					SysDict dict = sysDictManager.findByGroupNameAndKey("channel_business", project.getOpenPlatformKey());
					if (dict != null) {
						channelId = dict.getValue();
					}
				}
				if(StringUtil.isNotBlank(channelId)){
					Balance channel = balanceManager.synchronizedBalance(Long.parseLong(channelId), TypeEnum.BALANCE_TYPE_PIGGY);
					if(channel!=null){
						biz.setChannelBalance(channel.getBalance());
						biz.setChannelAvailableBalance(channel.getAvailableBalance());
					}
					Member channelMember=memberManager.selectByPrimaryKey(Long.parseLong(channelId));
					if(channelMember!=null){
						biz.setChannelName(channelMember.getTrueName());
					}
				}
			}
			resultDto.setModule(biz);
			resultDto.setSuccess(true);
		} catch (Exception e) {
			resultDto.setSuccess(false);
			resultDto.setErrorMsg("第三方支付查询余额失败");
		}
		return resultDto;
	}
	
	/**
	 * 撤销垫资还款
	 * @param interestId
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "cancelUnderWrite")
	@ResponseBody
	public Object cancelUnderWrite(@RequestParam("interestId") Long interestId, HttpServletRequest req,
			HttpServletResponse resp) {
		ResultDO<Object> result = new ResultDO<Object>(); 
		try {
			UnderwriteLog underwriteLog = underWriteLogManager.getUnderwriteLogByInterestId(interestId);
			if(underwriteLog==null){
				result.setResultCode(ResultCode.UNDERWRITE_RECORD_NOT_EXIT_ERROR);
				return result;
			}
			if(underwriteLog.getUnderwriteStatus()!=StatusEnum.UNDERWRITE_STATUS_WAIT_UNDERWRITE.getStatus()){
				result.setResultCode(ResultCode.UNDERWRITE_RECORD_NOT_UNDER_ERROR);
				return result;
			}
			int i = underWriteLogManager.deleteByInterestId(interestId);
			if (i >0) {
				result.setSuccess(true);
			}
		} catch (ManagerException e) {
			logger.error("撤销垫资失败, interestId={}", interestId, e);
		}
		return result;
	}
}
