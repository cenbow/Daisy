package com.yourong.backend.ic.controller;

import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.ic.service.BorrowerCreditService;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.BorrowerCredit;

/**
 * 
 * @Description 借款人授信额度controller
 * @author luwenshan
 * @time 2016年11月23日 下午4:02:55
 */
@Controller
@RequestMapping("borrowerCredit")
public class BorrowerCreditController extends BaseController {

    @Autowired
    private BorrowerCreditService borrowerCreditService;
    
    /**
     * 
     * @Description 进入借款人授信额度页面
     * @param req
     * @param resp
     * @return
     * @author luwenshan
     * @time 2016年11月23日 下午4:03:42
     */
    @RequestMapping(value = "index")
    public String showSysDictIndex(HttpServletRequest req, HttpServletResponse resp) {
         return "/ic/borrowerCredit/index";
    }
    
    /**
     * 
     * @Description 查询借款人直投项目授信额度列表
     * @param req
     * @param resp
     * @return
     * @throws ServletRequestBindingException
     * @author luwenshan
     * @time 2016年11月23日 下午4:32:30
     */
	@RequestMapping(value = "queryBorrowerCreditList")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public Object queryBorrowerCreditByPage(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Page<BorrowerCredit> pageRequest = new Page<BorrowerCredit>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		map.put("defaultCreditAmount", Constant.DEFALUT_ZT_BORROWER_CREDIT);
		map.put("investType", ProjectEnum.PROJECT_TYPE_DIRECT.getType());
		Page<BorrowerCredit> pager = borrowerCreditService.queryBorrowerCreditByPage(pageRequest, map);
		return pager;
    }
	
	/**
	 * 
	 * @Description 展示借款人直投项目授信额度信息
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author luwenshan
	 * @time 2016年11月24日 下午2:10:23
	 */
	@RequestMapping(value = "queryBorrowerCredit")
	@ResponseBody
	public Object queryBorrowerCredit(@ModelAttribute("borrowerCredit") BorrowerCredit borrowerCredit, 
			HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		ResultDO<BorrowerCredit> resultDO = null;
		try {
			resultDO = new ResultDO<BorrowerCredit>();
			BorrowerCredit borrowerCreditInfo = borrowerCreditService.selectByBorrower(borrowerCredit.getBorrowerId(), 
					borrowerCredit.getBorrowerType(), borrowerCredit.getOpenPlatformKey(), ProjectEnum.PROJECT_TYPE_DIRECT.getType());
			resultDO.setResult(borrowerCreditInfo);
		} catch (Exception e) {
			resultDO.setSuccess(false);
		}
		return resultDO;
	}
	
	/**
	 * 
	 * @Description 暂停上线
	 * @param id
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author luwenshan
	 * @time 2016年11月24日 下午2:19:03
	 */
	@RequestMapping(value = "pauseOnline")
	@RequiresPermissions("borrowerCredit:pauseOnline")
	@ResponseBody
	public Object pauseOnline(@RequestParam("id")Long id, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			BorrowerCredit borrowerCredit = borrowerCreditService.selectById(id);
			int operationFlag = 0;
			if (borrowerCredit != null) {
				borrowerCredit.setOnlineFlag(TypeEnum.BORROWER_CREDIT_PAUSE.getType());
				operationFlag = borrowerCreditService.updateById(borrowerCredit);
			}
			if (operationFlag == 0) {
				resultDO.setSuccess(false);
			}
			resultDO.setResult(operationFlag);
		} catch (Exception e) {
			resultDO.setSuccess(false);
		}
		return resultDO;
	}
	
	/**
	 * 
	 * @Description 恢复上线
	 * @param id
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author luwenshan
	 * @time 2016年11月24日 下午2:19:03
	 */
	@RequestMapping(value = "recoveryOnline")
	@RequiresPermissions("borrowerCredit:recoveryOnline")
	@ResponseBody
	public Object recoveryOnline(@RequestParam("id")Long id, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			BorrowerCredit borrowerCredit = borrowerCreditService.selectById(id);
			int operationFlag = 0;
			if (borrowerCredit != null) {
				borrowerCredit.setOnlineFlag(TypeEnum.BORROWER_CREDIT_NORMAL.getType());
				operationFlag = borrowerCreditService.updateById(borrowerCredit);
			}
			if (operationFlag == 0) {
				resultDO.setSuccess(false);
			}
			resultDO.setResult(operationFlag);
		} catch (Exception e) {
			resultDO.setSuccess(false);
		}
		return resultDO;
	}
	
	/**
	 * 
	 * @Description 添加备注
	 * @param id
	 * @param remark
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author luwenshan
	 * @time 2016年11月24日 下午2:19:03
	 */
	@RequestMapping(value = "addRemark")
	@ResponseBody
	public Object addRemark(@RequestParam("id")Long id, @RequestParam("remark")String remark, 
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			BorrowerCredit borrowerCredit = borrowerCreditService.selectById(id);
			int operationFlag = 0;
			if (borrowerCredit != null) {
				borrowerCredit.setRemark(remark);
				operationFlag = borrowerCreditService.updateById(borrowerCredit);
			}
			if (operationFlag == 0) {
				resultDO.setSuccess(false);
			}
			resultDO.setResult(operationFlag);
		} catch (Exception e) {
			resultDO.setSuccess(false);
		}
		return resultDO;
	}
	
	/**
	 * 
	 * @Description 修改授信额度
	 * @param id
	 * @param creditAmount
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author luwenshan
	 * @time 2016年11月24日 下午2:35:08
	 */
	@RequestMapping(value = "updateCreditAmount")
	@ResponseBody
	public Object updateCreditAmount(@RequestParam("id")Long id, @RequestParam("creditAmount")BigDecimal creditAmount, 
			HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ResultDO<Object> resultDO = new ResultDO<Object>();
		try {
			BorrowerCredit borrowerCredit = borrowerCreditService.selectById(id);
			int operationFlag = 0;
			if (borrowerCredit != null) {
				borrowerCredit.setCreditAmount(creditAmount);
				operationFlag = borrowerCreditService.updateById(borrowerCredit);
			}
			if (operationFlag == 0) {
				resultDO.setSuccess(false);
			}
			resultDO.setResult(operationFlag);
		} catch (Exception e) {
			resultDO.setSuccess(false);
		}
		return resultDO;
	}
	
	/**
	 * 
	 * @Description 根据借款人信息查询个人用户或企业用户的直投项目借款授信额度信息
	 * @param borrowerCredit
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author luwenshan
	 * @time 2016年11月24日 下午4:36:09
	 */
	@RequestMapping(value = "queryMemberOrEnterpriseCredit")
	@RequiresPermissions("borrowerCredit:queryMemberOrEnterpriseCredit")
	@ResponseBody
	public Object queryMemberOrEnterpriseCredit(@ModelAttribute("borrowerCredit") BorrowerCredit borrowerCredit, 
			HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		ResultDO<BorrowerCredit> resultDO = null;
		try {
			resultDO = new ResultDO<BorrowerCredit>();
			BorrowerCredit borrowerCreditInfo = borrowerCreditService.selectByBorrower
					(borrowerCredit.getBorrowerId(), borrowerCredit.getBorrowerType(), null, ProjectEnum.PROJECT_TYPE_DIRECT.getType());
			resultDO.setResult(borrowerCreditInfo);
		} catch (Exception e) {
			resultDO.setSuccess(false);
		}
		return resultDO;
	}
	
	/**
	 * 
	 * @Description 根据借款人信息查询渠道商用户的直投项目借款授信额度信息
	 * @param borrowerCredit
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author luwenshan
	 * @time 2016年11月24日 下午4:36:26
	 */
	@RequestMapping(value = "queryChannelCredit")
	@RequiresPermissions("borrowerCredit:queryChannelCredit")
	@ResponseBody
	public Object queryChannelCredit(@ModelAttribute("borrowerCredit") BorrowerCredit borrowerCredit, 
			HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		ResultDO<BorrowerCredit> resultDO = null;
		try {
			resultDO = new ResultDO<BorrowerCredit>();
			BorrowerCredit borrowerCreditInfo = borrowerCreditService.selectByBorrower
					(null, borrowerCredit.getBorrowerType(), borrowerCredit.getOpenPlatformKey(), ProjectEnum.PROJECT_TYPE_DIRECT.getType());
			resultDO.setResult(borrowerCreditInfo);
		} catch (Exception e) {
			resultDO.setSuccess(false);
		}
		return resultDO;
	}
    
}
