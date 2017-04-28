package com.yourong.backend.msc.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.ic.service.ProjectService;
import com.yourong.backend.sys.service.SysDictService;
import com.yourong.backend.tc.service.TransactionService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysDict;
/**
 * 
 * @desc 短信剩余提醒
 * @author chaisen
 * 2016年4月6日下午2:00:55
 */
@Controller
@RequestMapping("smsRemind")
public class SmsRemindController extends BaseController{
	
	@Autowired
	private ProjectService projectService;
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private SysDictService sysDictService;

	/**
	 * 
	 * @Description:短信提醒页面
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年4月6日 下午2:01:59
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("smsRemind:index")
	public String smsRemindIndex(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		return "/msg/sms/index";
	}
	/**
	 * 
	 * @Description:短信预警列表
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: chaisen
	 * @time:2016年4月6日 下午3:42:14
	 */
	@RequestMapping(value = "ajax")
	@RequiresPermissions("smsRemind:ajax")
	@ResponseBody
	public Object findLoanList(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		Page<SysDict> pageRequest = new Page<SysDict>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		map.put("groupName", TypeEnum.SMS_REMIND.getCode());
        Page<SysDict> pager = sysDictService.findByPage(pageRequest,map);
		return pager;
	}
	
	
	/**
	 * 
	 * @Description:保存短信预警设置
	 * @param sysDict
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年4月6日 下午3:31:52
	 */
	 @RequestMapping(value = "save")
	 @RequiresPermissions("smsRemind:save")
	 @ResponseBody
	 @LogInfoAnnotation(moduleName = "短信剩余提醒",desc = "保存短信预警设置")
	 public Object saveSmsRemind(@ModelAttribute SysDict sysDict, HttpServletRequest req, HttpServletResponse resp) {
		 ResultDO<SysDict> result = new ResultDO<SysDict>();
		 int insertSelective=0;
		 sysDict.setStatus(1);
		 SysDict dict=sysDictService.selectByKey(sysDict.getKey());
		 if(dict!=null){
			 sysDict.setLabel(dict.getLabel()); 
		 }
	     if(sysDict.getId()!=null){ 
	    	 insertSelective = sysDictService.updateByPrimaryKeySelective(sysDict); 
	     }else{	 
	    	 insertSelective = sysDictService.insert(sysDict);
	     }	 
	     if(insertSelective>0){
	    	 result.setSuccess(true);
	     }
	       return result;		 
	    }
	 /**
	  * 
	  * @Description:查询
	  * @param req
	  * @param resp
	  * @return
	  * @author: chaisen
	  * @time:2016年4月6日 下午4:45:56
	  */
	 @RequestMapping(value = "show")
	 @RequiresPermissions("smsRemind:show")
	 @ResponseBody
	 public Object showSmsRemind(HttpServletRequest req, HttpServletResponse resp) {
	      long id =  ServletRequestUtils.getIntParameter(req, "id", 0); 
	      SysDict sysDict = sysDictService.selectByPrimaryKey(id);	 
	      return sysDict;		 
	 }
	 /**
	  * 
	  * @Description:删除短信预警设置
	  * @param id
	  * @param req
	  * @param resp
	  * @return
	  * @author: chaisen
	  * @time:2016年4月6日 下午5:32:09
	  */
	 @RequestMapping(value = "delete")
	 @RequiresPermissions("smsRemind:delete")
	 @ResponseBody
	 @LogInfoAnnotation(moduleName = "短信剩余提醒",desc = "删除短信预警设置")
	 public ResultDO<SysDict> deleteSmsRemidn(
				@RequestParam("id") Long id, HttpServletRequest req,
				HttpServletResponse resp) {
			ResultDO<SysDict> result = new ResultDO<SysDict>();
			if (id != null) {
				result = sysDictService.deleteId(id);
			} else {
				result.setSuccess(false);
			}
			return result;
		}
	 /**
	  * 
	  * @Description:查询预警账号
	  * @param req
	  * @param resp
	  * @return
	  * @author: chaisen
	  * @time:2016年4月6日 下午5:56:57
	  */
	 @RequestMapping(value = "getSmsCdkeyList")
	 @ResponseBody
	 public ResultDO<SysDict> getSmsCdkeyList(HttpServletRequest req, HttpServletResponse resp){
		ResultDO<SysDict> result = new ResultDO<SysDict>();
		List<SysDict> dictList = sysDictService.findByGroupName(TypeEnum.SMS_TYPE.getCode());
		result.setResultList(dictList);
		return result;
	}
	/**
	 * 
	 * @Description:查询短信余额
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: chaisen
	 * @time:2016年4月7日 上午9:37:14
	 */
	@RequestMapping(value = "queryBalance")
	@RequiresPermissions("smsRemind:queryBalance")
	@ResponseBody
	public Object queryBalance(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		ResultDO<BigDecimal> resultDto = new ResultDO<BigDecimal>();
		try {
			resultDto=sysDictService.queryBalance(id);
			resultDto.setSuccess(true);
		} catch (Exception e) {
			resultDto.setSuccess(false);
		}
		return resultDto;
		}
		
	 
}
