package com.yourong.web.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yourong.common.enums.BestSignCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.StringUtil;
import com.yourong.core.ic.model.Project;
import com.yourong.core.tc.manager.ContractCoreManager;
import com.yourong.core.tc.manager.ContractSignManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.ContractSign;
import com.yourong.core.tc.model.Transaction;
import com.yourong.web.service.ProjectService;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年7月13日下午2:59:41
 */
@Controller
@RequestMapping("contractNotify")
public class BestSignRetrunController extends BaseController {

	
		@Autowired 
		private ContractSignManager contractSignManager;
		
		@Autowired 
		private TransactionManager transactionManager;
		
		@Autowired
		private ContractCoreManager contractCoreManager;
		
		@Autowired
		private ProjectService projectService;
		
		@Resource
		private ThreadPoolTaskExecutor taskExecutor;
	
	
	 	@RequestMapping(value = "/getVersion")
	    @ResponseBody
	    public ModelAndView getResult(HttpServletRequest req,   HttpServletResponse resp) throws Exception {
		 	ModelAndView model = new ModelAndView();
		 	//code=100000&signID=1467190561227ERD92
		 	String loginSource = ServletRequestUtils.getStringParameter(req, "loginSource", "");
		 	String code = ServletRequestUtils.getStringParameter(req, "code", "");
		 	String signID = ServletRequestUtils.getStringParameter(req, "signID", "");
		 	if(!validateSign(loginSource,code,signID)) {
				model.setViewName("/contract/transferContract");
				model.addObject("vali", false);
		 		return model;
			}
		 	model.addObject("vali", true);
		 	Map  map = Maps.newHashMap(); 
		 	map.put("code", code);
		 	map.put("signID", signID);
		 	Map<String,Object>	resultMap = this.afterContranct(map);
		 	JSONObject jsonObject =(JSONObject) JSONObject.toJSON(resultMap);
			model.setViewName("/contract/transferContract");
			model.addObject("result", jsonObject.toString());
			model.addObject("loginSource", loginSource);
			//WEB 端校验  当前session人是否跟合同对应人一致
			//校验是否前置状态正确
			return model;
	    }
	 	
	 	
	 	private Map<String,Object> afterContranct(Map  map){
	 		String code = map.get("code").toString();
	 		String signId = map.get("signID").toString();
	 		int resultCode = Integer.valueOf(code);
	 		
	 		boolean resultFlag = false;
	 		if(code.equals(BestSignCode.BUSINESS_100000.getCode())){
	 			resultFlag = true;
	 		}else if(!code.equals(BestSignCode.BUSINESS_100001.getCode())&&!code.equals(BestSignCode.BUSINESS_120122.getCode())){
	 			//其他异常统一封装
	 			code ="100001";
	 		}
	 		//更新合同相关状态
	 		taskExecutor.execute(new ProcessContractAfterTransactionThread(signId, resultFlag));
	 		
	 		
	 		Map<String,Object> resultMap= Maps.newHashMap();
	 		resultMap.put("success", resultFlag);
	 		String message = BestSignCode.getResultCodeByCode(code).getDesc();
	 		
		 	resultMap.put("message", message);
	 		return resultMap;
	 	}
	 	
	 	
	 	/**
		 * @desc 合同签署信息回调线程
		 * @author zhanghao
		 * 2016年7月11日下午2:11:07
		 */
		private class ProcessContractAfterTransactionThread implements Runnable {
			
			private String signId;
			
			private boolean resultFlag;

			public ProcessContractAfterTransactionThread(final String signId,final boolean resultFlag) {
				this.signId = signId;
				this.resultFlag = resultFlag;
			}

			@Override
			@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
			public void run() {
				logger.info("手签结果回调线程开始执行，signId：" + signId +"resultFlag:"+resultFlag);
				ContractSign contractSign  = null;
				Map<String,Object> queryMap = Maps.newHashMap();
				queryMap.put("signId", signId);
				queryMap.put("type", StatusEnum.CONTRACT_PARTY_SECOND.getStatus());
				try {
					contractSign  = contractSignManager.selectByMap(queryMap);
				} catch (ManagerException e) {
					logger.error("回调后续处理：获取用户合同相关信息,queryMap={}" , queryMap);
				}

				ContractSign record = new ContractSign();
				record.setId(contractSign.getId());
				if(resultFlag){
					record.setStatus(StatusEnum.CONTRACT_SIGN_STATUS_SUCCESS.getStatus());
				}else{
					record.setStatus(StatusEnum.CONTRACT_SIGN_STATUS_FAIL.getStatus());
				}
				try {
					int num = contractSignManager.updateByPrimaryKey(record);
				} catch (ManagerException e) {
					logger.error("回调后续处理：更新签署状态异常,record={}" , record,e);
				}
				
				if(!resultFlag){
					//TODO 失败信息记录
					return ;
				}
				//乙方签署成功，更改交易状态
				Transaction tra = new Transaction();
				tra.setId(contractSign.getTransactionId());
				tra.setSignStatus(StatusEnum.CONTRACT_STATUS_ALREADY_SIGN.getStatus());
				try {
					transactionManager.updateByPrimaryKeySelective(tra);
				} catch (ManagerException e) {
					logger.error("回调后续处理：签署成功，更新交易表合同签署状态异常,tra={}" , tra,e);
				}
				//数据保全
				Transaction tran = new Transaction();;
				try {
					tran = transactionManager.selectTransactionById(contractSign.getTransactionId());
				} catch (ManagerException e) {
					logger.error("回调后续处理：签署成功，获取交易信息异常,transactionId={}" , contractSign.getTransactionId(),e);
				}
				Project project = projectService.selectByPrimaryKey(tran.getProjectId());
				//债权项目签完即保   直投项目签完，且项目为履约中，进行保全
				if(!project.isDirectProject()||
						(project.isDirectProject()&&
								(project.getStatus()==StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus()
								||project.getStatus()==StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus())		
								)){
					contractCoreManager.getContractPreservation(contractSign.getTransactionId(), "web");
				}
				
				logger.info("手签结果回调线程执行结束，signId：" + signId +"resultFlag:"+resultFlag);
			}
		}	
	 	
		
		
		private boolean validateSign(String loginSource,String code,String signID) {
			// 
			/*if(StringUtil.isBlank(loginSource)){
				return false;
			}*/
			if(StringUtil.isBlank(code)){
				return false;
			}
			if(StringUtil.isBlank(signID)){
				return false;
			}
			return true;
		}
	 	
}
