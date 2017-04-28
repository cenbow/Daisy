/**
 * 
 */
package com.yourong.core.tc.manager.impl;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rop.thirdparty.com.google.common.collect.Lists;
import rop.thirdparty.com.google.common.collect.Maps;
import cn.bestsign.sdk.domain.vo.params.ReceiveUser;
import cn.bestsign.sdk.domain.vo.params.SendUser;
import cn.bestsign.sdk.domain.vo.result.AutoSignbyCAResult;
import cn.bestsign.sdk.domain.vo.result.CertificateApplyResult;
import cn.bestsign.sdk.domain.vo.result.Continfo;
import cn.bestsign.sdk.domain.vo.result.UploadUserImageResult;
import cn.bestsign.sdk.integration.Constants.CONTRACT_NEEDVIDEO;
import cn.bestsign.sdk.integration.Constants.USER_TYPE;
import cn.bestsign.sdk.integration.exceptions.BizException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.common.util.BestSignUtil;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FileInfoUtil;
import com.yourong.common.util.OSSUtil;
import com.yourong.core.bsc.manager.AttachmentIndexManager;
import com.yourong.core.bsc.manager.BscAttachmentManager;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.manager.TransferProjectManager;
import com.yourong.core.ic.model.Debt;
import com.yourong.core.ic.model.Project;
import com.yourong.core.ic.model.TransferProject;
import com.yourong.core.tc.manager.ContractCoreManager;
import com.yourong.core.tc.manager.ContractManager;
import com.yourong.core.tc.manager.ContractSignManager;
import com.yourong.core.tc.manager.PreservationManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.ContractSign;
import com.yourong.core.tc.model.Preservation;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.uc.manager.EnterpriseManager;
import com.yourong.core.uc.manager.MemberInfoManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Enterprise;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberInfo;

/**
 * @desc 合同第三方核心业务类
 * @author zhanghao
 * 2016年7月6日上午11:53:25
 */
@Component
public class ContractCoreManagerImpl  implements ContractCoreManager{

	private Logger logger = LoggerFactory.getLogger(ContractCoreManagerImpl.class);

	@Autowired
	private ContractSignManager contractSignManager;
	
	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired
	private MemberManager memberManager;
	
	@Autowired
	private EnterpriseManager enterpriseManager;
	
	@Autowired
	private MemberInfoManager memberInfoManager;
	
	@Autowired
	private ContractManager contractManager;
	
	@Autowired
	private BscAttachmentManager bscAttachmentManager;
	
	@Autowired
	private AttachmentIndexManager attachmentIndexManager;
	
	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private DebtManager debtManager;
	
	@Autowired
	private PreservationManager preservationManager;
	
	@Autowired
	private TransferProjectManager transferProjectManager;
	
	/**
	 * @desc 获取手动签名地址
	 * @param transactionId
	 * @param type 设备类型  1-PC; 2-mobile
	 * @return
	 * @author zhanghao
	 * @time 2016年7月6日 下午2:03:44
	 **/
	@Override
	public ResultDO<Object> getSignUrl(Long transactionId,int type,Integer source) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		String signUrl = "";
		Project project = new Project();
		Transaction transaction = new Transaction();
		try {
			transaction = transactionManager.selectTransactionById(transactionId);
			Member member = memberManager.selectByPrimaryKey(transaction.getMemberId());
			if(member.getIsAuth()==0){
				this.memberCa(member.getId());
			}
			project= projectManager.selectByPrimaryKey(transaction.getProjectId());
		} catch (ManagerException e1) {
			logger.error("获取手动签名：校验用户是否ca认证异常,transactionId={}" , transactionId);
		}
		ContractSign contractSign  = null;
		Map<String,Object> map = Maps.newHashMap();
		map.put("transactionId", transactionId);
		map.put("type", StatusEnum.CONTRACT_PARTY_SECOND.getStatus());
		try {
			contractSign  = contractSignManager.selectByMap(map);
		} catch (ManagerException e) {
			logger.error("获取手动签名：获取用户合同相关信息,transactionId={}" , transactionId);
			rDO.setSuccess(false);	
			//rDO.setResultCode(resultCode);
			return rDO;
		}
		
		if(contractSign==null){
			this.beforeCheck(transactionId);
			//初始化成功，修改交易状态
			Transaction tra = new Transaction();
			tra.setId(transactionId);
			tra.setSignStatus(StatusEnum.CONTRACT_STATUS_UNSIGN.getStatus());
			try {
				transactionManager.updateByPrimaryKeySelective(tra);
			} catch (ManagerException e) {
				logger.error("前置校验预处理，更新交易表合同签署状态异常,transactionId={}" , transactionId,e);
			}
			try {
				contractSign  = contractSignManager.selectByMap(map);
			} catch (ManagerException e) {
				logger.error("获取手动签名：获取用户合同相关信息,transactionId={}" , transactionId);
				rDO.setSuccess(false);	
				//rDO.setResultCode(resultCode);
				return rDO;
			}
			/*logger.error("获取手动签名：获取用户合同相关信息为空,该交易尚未完成初始化,transactionId={}" , transactionId);
			rDO.setSuccess(false);	
			rDO.setResultCode(ResultCode.CONTRACT_SIGN_FAIL_TO_INIT);
			return rDO;	*/
		}
		int pagenum = 0;
		float signx = 0;
		float signy = 0;
		if(transaction.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
			 if(project.isDirectProject()){
				 pagenum = 5;
				 signx = 0.38f;
				 signy = 0.05f;
			}else{
				pagenum = 4;
				 signx = 0.38f;
				 signy = 0.65f;
			}
		}else{
			if(project.isDirectProject()){
				 pagenum = 6;
				 signx = 0.38f;
				 signy = 0.7f;
			}else{
				pagenum = 4;
				 signx = 0.38f;
				 signy = 0.25f;
			}
		}
		
		
		try {
			signUrl = BestSignUtil.handSign(contractSign.getSignId(), contractSign.getMobile().toString(),contractSign.getDocId(), getReturnUrl(source),type
					,pagenum,signx,signy);
			logger.info("交易号={},手签地址={}",transactionId,signUrl);
		} catch (Exception e) {
			// TODO 异常处理
			logger.error("获取手动签名：获取手签链接异常,transactionId={}" , transactionId,e);
			rDO.setSuccess(false);	
			//rDO.setResultCode(resultCode);
			return rDO;
		}
		rDO.setResult(signUrl);
		rDO.setSuccess(true);	
		return rDO;
	}

	/**
	 * @desc 第三方自动签名
	 * @param transactionId
	 * @return
	 * @author zhanghao
	 * @time 2016年7月6日 下午2:03:44
	 **/
	@Override
	public ResultDO<Object> autoSignThird(Long transactionId) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		ContractSign contractSign  = null;
		Map<String,Object> map = Maps.newHashMap();
		map.put("transactionId", transactionId);
		map.put("type", StatusEnum.CONTRACT_PARTY_THIRD.getStatus());
		try {
			contractSign  = contractSignManager.selectByMap(map);
		} catch (ManagerException e) {
			logger.error("第三方自动签名：获取用户合同相关信息,transactionId={}" , transactionId);
			rDO.setSuccess(false);	
			//rDO.setResultCode(resultCode);
			return rDO;
		}
		
		if(contractSign==null){
			logger.error("第三方自动签名：获取用户合同相关信息为空,该交易尚未完成初始化,transactionId={}" , transactionId);
			rDO.setSuccess(false);	
			rDO.setResultCode(ResultCode.CONTRACT_SIGN_FAIL_TO_INIT);
			return rDO;	
		}
		Project project = new Project();
		Transaction transaction = new Transaction();
		try {
			transaction = transactionManager.selectTransactionById(transactionId);
			project= projectManager.selectByPrimaryKey(transaction.getProjectId());
		} catch (ManagerException e1) {
			logger.error("第三方自动签名：获取签名锚点异常,transactionId={}" , transactionId);
		}
	
		int pagenum = 0;
		float signx = 0;
		float signy = 0;
		
		if(transaction.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
			if(project.isDirectProject()){
				pagenum = 4;
				 signx = 0.38f;
				 signy = 0.8f;
			}else{
				pagenum = 4;
				 signx = 0.3f;
				 signy = 0.45f;
			}
		}else{
			if(project.isDirectProject()){
				 pagenum = 6;
				 signx = 0.3f;
				 signy = 0.8f;
			}else{
				pagenum = 4;
				 signx = 0.3f;
				 signy = 0.05f;
			}
		}
		
		
		ContractSign recording = new ContractSign();
		recording.setId(contractSign.getId());
		recording.setStatus(StatusEnum.CONTRACT_SIGN_STATUS_SIGNING.getStatus());
		try {
			int num = contractSignManager.updateByPrimaryKey(recording);
		} catch (ManagerException e) {
			logger.error("第三方自动签名：更新签署状态为签署中异常,transactionId={}" , transactionId,e);
		}
		
		AutoSignbyCAResult result= new AutoSignbyCAResult();
		try {
			result = BestSignUtil.autoSign(contractSign.getSignId(), "100044535@qq.com", pagenum, signx, signy);
			logger.info("交易号={},第三方自动签名结果={}",transactionId,result);
		} catch (NoSuchFieldException fileE){
			logger.error("第三方自动签名：自动签名NoSuchFieldException异常,已完成签字,transactionId={}" , transactionId);
		} catch (Exception e) {
			// TODO 异常处理        记录错误信息
			ContractSign record = new ContractSign();
			record.setId(contractSign.getId());
			record.setStatus(StatusEnum.CONTRACT_SIGN_STATUS_FAIL.getStatus());
			record.setRemark(result.getCode()+result.getVmsg());
			try {
				int failNum = contractSignManager.updateByPrimaryKey(record);
			} catch (ManagerException error) {
				logger.error("第三方自动签名：签署失败，记录错误信息异常,transactionId={}" , transactionId,error);
			}
			logger.error("第三方自动签名：自动签名异常,transactionId={}" , transactionId,e);
			rDO.setSuccess(false);	
			return rDO;
		}
		
		ContractSign record = new ContractSign();
		record.setId(contractSign.getId());
		record.setStatus(StatusEnum.CONTRACT_SIGN_STATUS_SUCCESS.getStatus());
		try {
			int num = contractSignManager.updateByPrimaryKey(record);
		} catch (ManagerException e) {
			logger.error("第三方自动签名：签署成功，更新签署状态异常,transactionId={}" , transactionId,e);
		}
		
		rDO.setSuccess(true);	
		return rDO;
	}
	
	/**
	 * @desc 乙方自动签名
	 * @param transactionId
	 * @param type 乙方为个人用户
	 * @return
	 * @author zhanghao
	 * @time 2016年7月6日 下午2:03:44
	 **/
	@Override
	public ResultDO<Object> autoSignSecond(Long transactionId) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		ContractSign contractSign  = null;
		
		
		ResultDO<Object> queryRes = this.queryContractInfo(transactionId);
		if(queryRes.isSuccess()&&Integer.valueOf(queryRes.getResult().toString())>0){
			rDO.setSuccess(true);	
			return rDO;
		}
		Transaction tra = new Transaction();
		try {
			tra = transactionManager.selectTransactionById(transactionId);
			Member member = memberManager.selectByPrimaryKey(tra.getMemberId());
			if(member.getIsAuth()==0){
				this.memberCa(member.getId());
			}
		} catch (ManagerException e1) {
			logger.error("乙方自动签名：校验用户是否ca认证异常,transactionId={}" , transactionId);
		}
		
		Map<String,Object> map = Maps.newHashMap();
		map.put("transactionId", transactionId);
		map.put("type", StatusEnum.CONTRACT_PARTY_SECOND.getStatus());
		try {
			contractSign  = contractSignManager.selectByMap(map);
			
		} catch (ManagerException e) {
			logger.error("乙方自动签名：获取用户合同相关信息,transactionId={}" , transactionId);
			rDO.setSuccess(false);	
			//rDO.setResultCode(resultCode);
			return rDO;
		}
		if(contractSign==null){
			this.beforeCheck(transactionId);
			try {
				contractSign  = contractSignManager.selectByMap(map);
			} catch (ManagerException e) {
				logger.error("获取手动签名：获取用户合同相关信息,transactionId={}" , transactionId);
				rDO.setSuccess(false);	
				//rDO.setResultCode(resultCode);
				return rDO;
			}
			/*logger.error("乙方自动签名：获取用户合同相关信息为空,该交易尚未完成初始化,transactionId={}" , transactionId);
			rDO.setSuccess(false);	
			rDO.setResultCode(ResultCode.CONTRACT_SIGN_FAIL_TO_INIT);
			return rDO;	*/
		}
		
		if(contractSign.getStatus()==StatusEnum.CONTRACT_SIGN_STATUS_SUCCESS.getStatus()){
			rDO.setSuccess(true);	
			return rDO;
		}
		
		ContractSign recording = new ContractSign();
		recording.setId(contractSign.getId());
		recording.setStatus(StatusEnum.CONTRACT_SIGN_STATUS_SIGNING.getStatus());
		try {
			int num = contractSignManager.updateByPrimaryKey(recording);
		} catch (ManagerException e) {
			logger.error("乙方自动签名：更新签署状态为签署中异常,transactionId={}" , transactionId,e);
		}
		
		//TODO签章位置，最后调整
		Project project = new Project();
		Transaction transaction = new Transaction();
		try {
			transaction = transactionManager.selectTransactionById(transactionId);
			project= projectManager.selectByPrimaryKey(transaction.getProjectId());
		} catch (ManagerException e1) {
			logger.error("乙方自动签名：获取签名锚点异常,transactionId={}" , transactionId);
		}
		int pagenum = 0;
		float signx = 0;
		float signy = 0;
		
		if(transaction.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
			 if(project.isDirectProject()){
				 pagenum = 5;
				 signx = 0.38f;
				 signy = 0.05f;
			}else{
				pagenum = 4;
				 signx = 0.38f;
				 signy = 0.65f;
			}
		}else{
			if(project.isDirectProject()){
				 pagenum = 6;
				 signx = 0.38f;
				 signy = 0.7f;
			}else{
				pagenum = 4;
				 signx = 0.38f;
				 signy = 0.4f;
			}
		}
		
		
		
		AutoSignbyCAResult result= new AutoSignbyCAResult();
		try {
			result = BestSignUtil.autoSign(contractSign.getSignId(), contractSign.getMobile().toString(), pagenum, signx, signy);
			logger.debug("交易号={},乙方自动签名结果={}",transactionId,result);
		} catch (NoSuchFieldException fileE){
			logger.error("乙方自动签名：自动签名NoSuchFieldException异常,已完成签字,transactionId={}" , transactionId);
		} catch (Exception e) {
			// TODO 异常处理
			ContractSign record = new ContractSign();
			record.setId(contractSign.getId());
			record.setStatus(StatusEnum.CONTRACT_SIGN_STATUS_FAIL.getStatus());
			record.setRemark(result.getCode()+result.getVmsg());
			try {
				int failNum = contractSignManager.updateByPrimaryKey(record);
			} catch (ManagerException error) {
				logger.error("乙方自动签名：签署失败，记录错误信息异常,transactionId={}" , transactionId,error);
			}
			logger.error("乙方自动签名：自动签名异常,transactionId={}" , transactionId,e);
			rDO.setSuccess(false);	
			//rDO.setResultCode(resultCode);
			return rDO;
		}
		
		ContractSign record = new ContractSign();
		record.setId(contractSign.getId());
		record.setStatus(StatusEnum.CONTRACT_SIGN_STATUS_SUCCESS.getStatus());
		try {
			int num = contractSignManager.updateByPrimaryKey(record);
		} catch (ManagerException e) {
			logger.error("乙方自动签名：签署成功，更新签署状态异常,transactionId={}" , transactionId,e);
		}
		/*
		//乙方签署成功，更改交易状态
		Transaction tra = new Transaction();
		tra.setId(transactionId);
		tra.setSignStatus(StatusEnum.CONTRACT_STATUS_ALREADY_SIGN.getStatus());
		try {
			transactionManager.updateByPrimaryKeySelective(tra);
		} catch (ManagerException e) {
			logger.error("乙方自动签名：签署成功，更新交易表合同签署状态异常,transactionId={}" , transactionId,e);
		}*/
		//合同保全 债权项目签完即保   直投项目签完，且项目为履约中，进行保全
//		if(!project.isDirectProject()||
//				(project.isDirectProject()&&
//						(project.getStatus()==StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus()
//						||project.getStatus()==StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus())
//						)){
			this.getContractPreservation(transactionId, "web");
//		}
		rDO.setSuccess(true);	
		return rDO;
	}
	
	/**
	 * @desc 甲方自动签名
	 * @param transactionId
	 * @return
	 * @author zhanghao
	 * @time 2016年7月6日 下午2:03:44
	 **/
	@Override
	public ResultDO<Object> autoSignFirst(Long transactionId) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		ContractSign contractSign  = null;
		Map<String,Object> map = Maps.newHashMap();
		map.put("transactionId", transactionId);
		map.put("type", StatusEnum.CONTRACT_PARTY_FIRST.getStatus());
		try {
			contractSign  = contractSignManager.selectByMap(map);
		} catch (ManagerException e) {
			logger.error("甲方自动签名：获取用户合同相关信息,transactionId={}" , transactionId);
			rDO.setSuccess(false);	
			//rDO.setResultCode(resultCode);
			return rDO;
		}
		
		if(contractSign==null){
			logger.error("甲方自动签名：获取用户合同相关信息为空,该交易尚未完成初始化,transactionId={}" , transactionId);
			rDO.setSuccess(false);	
			rDO.setResultCode(ResultCode.CONTRACT_SIGN_FAIL_TO_INIT);
			return rDO;	
		}
		
		ContractSign recording = new ContractSign();
		recording.setId(contractSign.getId());
		recording.setStatus(StatusEnum.CONTRACT_SIGN_STATUS_SIGNING.getStatus());
		try {
			int num = contractSignManager.updateByPrimaryKey(recording);
		} catch (ManagerException e) {
			logger.error("甲方自动签名：更新签署状态为签署中异常,transactionId={}" , transactionId,e);
		}
		
		//TODO签章位置，最后调整
		Project project = new Project();
		Transaction transaction = new Transaction();
		try {
			transaction = transactionManager.selectTransactionById(transactionId);
			project= projectManager.selectByPrimaryKey(transaction.getProjectId());
		} catch (ManagerException e1) {
			logger.error("甲方自动签名：获取签名锚点异常,transactionId={}" , transactionId);
		}
		int pagenum = 0;
		float signx = 0;
		float signy = 0;
		if(transaction.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
			if(project.isDirectProject()){
				pagenum = 4;
				signx = 0.3f;
				signy = 0.6f;
			}else{
				pagenum = 4;
				signx = 0.3f;
				signy = 0.3f;
			}
		}else{
			if(project.isDirectProject()){
				pagenum = 6;
				signx = 0.3f;
				signy = 0.5f;
			}else{
				pagenum = 3;
				signx = 0.3f;
				signy = 0.7f;
			}
		}		
		
		AutoSignbyCAResult result= new AutoSignbyCAResult();
		try {
			result = BestSignUtil.autoSign(contractSign.getSignId(), contractSign.getMobile().toString(), pagenum, signx, signy);
			logger.debug("交易号={},甲方自动签名结果={}",transactionId,result);
		} catch (BizException bize){
			logger.error("甲方自动签名：自动签名异常,BizException,transactionId={}" , transactionId);
		} catch (NoSuchFieldException fileE){
			logger.error("甲方自动签名：自动签名NoSuchFieldException异常,已完成签字,transactionId={}" , transactionId);
		} catch (Exception e) {
			// TODO 异常处理
			ContractSign record = new ContractSign();
			record.setId(contractSign.getId());
			record.setStatus(StatusEnum.CONTRACT_SIGN_STATUS_FAIL.getStatus());
			record.setRemark(result.getCode()+result.getVmsg());
			try {
				int failNum = contractSignManager.updateByPrimaryKey(record);
			} catch (ManagerException error) {
				logger.error("甲方自动签名：签署失败，记录错误信息异常,transactionId={}" , transactionId,error);
			}
			
			logger.error("甲方自动签名：自动签名异常,transactionId={}" , transactionId,e);
			rDO.setSuccess(false);	
			//rDO.setResultCode(resultCode);
			return rDO;
		}
		
		ContractSign record = new ContractSign();
		record.setId(contractSign.getId());
		record.setStatus(StatusEnum.CONTRACT_SIGN_STATUS_SUCCESS.getStatus());
		try {
			int num = contractSignManager.updateByPrimaryKey(record);
		} catch (ManagerException e) {
			logger.error("甲方自动签名：签署成功，更新签署状态异常,transactionId={}" , transactionId,e);
		}
		
		rDO.setSuccess(true);	
		return rDO;
	}

	/**
	 * @desc 签署合同预处理
	 * @param transactionId
	 * @return
	 * @author zhanghao
	 * @throws ManagerException 
	 * @time 2016年7月6日 下午2:03:44
	 **/
	@Override
	public ResultDO<Object> preSign(Long transactionId,String fromSys){
		// TODO  发送合同， 初始化甲方 和第三方数据 
		ResultDO<Object> rDO = new ResultDO<Object>();
		String filePath = "";
		ReceiveUser secondPart=null;
		ReceiveUser firstPart=null;
		SendUser senduser=null;
		Long attachmentId = null;
		
		Long firstId=null;
		String firstMobile="";
		Long secondId=null;
		String secondMobile="";
		
		Enterprise yrwEnterprise =new Enterprise();
		Member yrwMember = new Member();
		String projectName ="";
		try { 
			//初始化合同
			BscAttachment attachment = contractManager.saveContract(transactionId, fromSys);
			attachment.setListOrder(0);
			attachment.setUploadTime(DateUtils.getCurrentDate());
			attachment.setModule(Constant.ATTACHMENT_CONTRACT_IDENTITY);
			attachment.setDelFlag(Constant.ENABLE);
			attachment.setStorageWay(TypeEnum.ATTACHMENT_STORAGE_WAY_OSS.getCode());
			int rows = bscAttachmentManager.insertSelective(attachment);
			if (rows > 0) {
				// 如果有索引，则先把他删除
				attachmentIndexManager.deleteAttachmentIndexByKeyId(transactionId.toString());
				attachmentIndexManager.insertAttachmentIndex(attachment.getId(), transactionId.toString());
			}
			filePath = Config.prefixPath + attachment.getFileUrl()
					+ attachment.getFileName();
			attachmentId = attachment.getId();
			//发送上上签，初始化合同签署数据
			//甲方
			Transaction transaction = transactionManager.selectTransactionById(transactionId);
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			String firstName ="";
			USER_TYPE usertype=null;
			Member memberFirst = null;
					
			if(transaction.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
				
				TransferProject transferProject = transferProjectManager.selectByPrimaryKey(transaction.getTransferId());
				
					firstId = transferProject.getMemberId();
					memberFirst = memberManager.selectByPrimaryKey(transferProject.getMemberId());
					firstMobile = memberFirst.getMobile().toString();
					firstName = memberFirst.getTrueName();
					usertype = USER_TYPE.PERSONAL;
				
			}else{
				if(project.isDirectProject()){
					if(project.getBorrowerType()==TypeEnum.MEMBER_TYPE_COMPANY.getType()){//企业类型
						firstId = project.getEnterpriseId();
						Enterprise enterprise =enterpriseManager.selectByKey(project.getEnterpriseId());
						Member member = memberManager.selectByPrimaryKey(enterprise.getLegalId());
						firstMobile = enterprise.getTelephone().toString();//采用企业信息中维护的手机号
						firstName = enterprise.getFullName();
						usertype = USER_TYPE.ENTERPRISE;
					}
					if(project.getBorrowerType()==TypeEnum.MEMBER_TYPE_PERSONAL.getType()){//个人
						firstId = project.getBorrowerId();
						memberFirst = memberManager.selectByPrimaryKey(project.getBorrowerId());
						firstMobile = memberFirst.getMobile().toString();
						firstName = memberFirst.getTrueName();
						usertype = USER_TYPE.PERSONAL;
					}
				}else{
					Debt debt = debtManager.selectByPrimaryKey(project.getDebtId());
					if(debt.getLenderType()==2){//企业
						firstId = debt.getLenderEnterpriseId();
						Enterprise enterprise =enterpriseManager.selectByKey(debt.getLenderEnterpriseId());
						Member member = memberManager.selectByPrimaryKey(enterprise.getLegalId());
						firstMobile = member.getMobile().toString();
						firstName = enterprise.getFullName();
						usertype = USER_TYPE.ENTERPRISE;
					}else{
						firstId = debt.getLenderId();
						memberFirst = memberManager.selectByPrimaryKey(debt.getLenderId());
						firstMobile = memberFirst.getMobile().toString();
						firstName = memberFirst.getTrueName();
						usertype = USER_TYPE.PERSONAL;
					}
				}
			}
			
			//甲方CA认证校验
			if(memberFirst!=null&&memberFirst.getIsAuth()==0){
				this.memberCa(memberFirst.getId());
			}	
			
			firstPart = new ReceiveUser(null, firstName, firstMobile, usertype, CONTRACT_NEEDVIDEO.NONE, false);
			//乙方
			String secondName ="";
			
			Member member = memberManager.selectByPrimaryKey(transaction.getMemberId());
			secondId = transaction.getMemberId();
			secondName = member.getTrueName();
			secondMobile = member.getMobile().toString();
			secondPart = new ReceiveUser(null, secondName, secondMobile, USER_TYPE.PERSONAL, CONTRACT_NEEDVIDEO.NONE, false);
	
			// 有融网处理
			/*yrwEnterprise =enterpriseManager.selectYRW();
			try {
				yrwMember = memberManager.selectByPrimaryKey(yrwEnterprise.getLegalId());
			} catch (ManagerException e1) {
				logger.error("签署合同预处理：获取yrw信息异常，enterprise={}", yrwEnterprise,e1);
			}*/
			projectName =this.getPrefixProjectName(project.getName());
			senduser = new SendUser("100044535@qq.com", "浙江小融网络科技股份有限公司", "18268030378", 99, true, USER_TYPE.ENTERPRISE, false,projectName, "");
		} catch (Exception e) {
			logger.error("签署合同预处理：签署合同准备数据异常，transactionId={}", transactionId);
		}	
		logger.info("签署合同预处理:transactionId={},filePath={},secondPart={},firstPart={},senduser={}" ,transactionId,filePath,secondPart,firstPart,senduser);
		String signId="";
		String docId="";
		try {
			String fileName=projectName+"-"+transactionId+"借款协议.pdf";
			Continfo[] lastContinfoList = BestSignUtil.createContract(filePath, firstPart, secondPart, senduser,fileName);
			logger.debug("交易号={},签署合同预处理结果={}",transactionId,lastContinfoList);
			signId=lastContinfoList[0].getSignid();
			docId=lastContinfoList[0].getDocid();
		} catch (Exception e) {
			logger.info("签署合同预处理:上上签交互异常,transactionId={},filePath={},secondPart={},firstPart={},senduser={}" ,transactionId,filePath,secondPart,firstPart,senduser);
			rDO.setSuccess(false);
			return rDO;
		}	
		
		List<ContractSign> records =Lists.newArrayList();
		ContractSign contractSign = new ContractSign(); 
		contractSign.setSignId(signId);
		contractSign.setDocId(docId);
		contractSign.setAttachmentId(attachmentId);
		contractSign.setTransactionId(transactionId);
		contractSign.setStatus(StatusEnum.CONTRACT_SIGN_STATUS_UNSIGN.getStatus());
		
		contractSign.setType(StatusEnum.CONTRACT_PARTY_FIRST.getStatus());
		contractSign.setSourceId(firstId);
		contractSign.setMobile(Long.valueOf(firstMobile));
		records.add(contractSign);
		
		ContractSign contractSignS= (ContractSign)contractSign.clone();
		contractSignS.setType(StatusEnum.CONTRACT_PARTY_SECOND.getStatus());
		contractSignS.setSourceId(secondId);
		contractSignS.setMobile(Long.valueOf(secondMobile));
		records.add(contractSignS);
		
		// 有融网处理
		
		ContractSign contractSignT= (ContractSign)contractSign.clone();
		contractSignT.setType(StatusEnum.CONTRACT_PARTY_THIRD.getStatus());
		contractSignT.setSourceId(0L);
		contractSignT.setMobile(Long.valueOf("18268030378"));;
		records.add(contractSignT);
		
		try {
			contractSignManager.batchInsert(records);
		} catch (ManagerException e) {
			logger.error("签署合同预处理：初始化合同表信息异常，transactionId={},signId={},docId={},attachmentId={}", transactionId,signId,docId,attachmentId);
		}
		
		rDO.setSuccess(true);
		return rDO;
	}
	
	private String getPrefixProjectName(String projectName) {
		if (projectName.contains("期")) {
			return projectName.substring(0, projectName.indexOf("期") + 1);
		} else {
			return projectName;
		}
	}
	
	/**
	 * @desc 个人CA认证申请
	 * @param memberId
	 * @author zhanghao
	 * @time 2016年7月6日 下午2:11:13
	 **/
	@Override
	public ResultDO<Object>  memberCa(Long memberId) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		Member member =null;
		MemberInfo memberInfo=null;
		try {
			member = memberManager.selectByPrimaryKey(memberId);
			memberInfo = memberInfoManager.getMemberInfoByMemberId(memberId);
		} catch (ManagerException e) {
			logger.error("个人CA申请,获取个人信息异常,memberId={}" ,memberId,e);
		}
		/*if(member==null || memberInfo==null){
			//TODO 空处理
		}*/
		String linkIdCode=member.getIdentityNumber();
		String name = member.getTrueName();
		String linkMobile = member.getMobile().toString();
	
		String  address = "浙江省杭州市";
		String regisName = "浙江省杭州市";
		if(memberInfo!=null){
			address = (memberInfo.getAddress()!=null&&StringUtil.isNotBlank(memberInfo.getAddress()))?memberInfo.getAddress():address;
			regisName =(memberInfo.getCensusRegisterName()!=null&&StringUtil.isNotBlank(memberInfo.getCensusRegisterName()))?memberInfo.getCensusRegisterName():regisName;
		}
		/*String  province =  regisName.substring(regisName.indexOf("省"));
		String  city = regisName.substring(regisName.indexOf("省"),regisName.indexOf("市"));*/
		CertificateApplyResult result = new CertificateApplyResult();
		try {
			// TODO Auto-generated method stub   用户CA认证  
			result = BestSignUtil.handleCFCACertificate(name, linkMobile, address, regisName, regisName, linkIdCode);
			logger.debug("会员ID={},个人CA认证结果={}",memberId,result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("个人CA申请异常,memberId={},result={}" ,memberId,e,result);
		}	
		if(!result.getIsResult()){
			//TODO 失败处理
			return rDO;
		}
		// 记录用户ca证书编号
		String  caNo = result.getCerNo();
		Member record =new Member();
		record.setId(memberId);
		record.setCaNo(caNo);
		record.setIsAuth(1);
		try {
			memberManager.updateByPrimaryKeySelective(record);
		} catch (ManagerException e) {
			// TODO Auto-generated catch block
			logger.error("个人CA申请成功,记录CA信息异常,CaNo={},memberId={}" ,caNo,memberId,e);
		}
		rDO.setSuccess(true);
		return rDO;
	}

	/**
	 * @desc 企业CA认证
	 * @param id
	 * @author zhanghao
	 * @time 2016年7月6日 下午2:11:13
	 **/
	@Override
	public ResultDO<Object> enterpriseCa(Long id) {
		// TODO Auto-generated method stub   企业ca认证 记录企业ca证书编号
		ResultDO<Object> rDO = new ResultDO<Object>();
		
		Member member=new Member();
		Enterprise enterprise=new Enterprise();
		try {
			enterprise  =  enterpriseManager.selectByKey(id);
			member = memberManager.selectByPrimaryKey(enterprise.getLegalId());
		} catch (ManagerException e1) {
			logger.error("企业CA申请,获取企业信息异常,id={}" ,id,e1);
			//rDO.setResultCode(resultCode);
			rDO.setSuccess(false);
			return rDO;
		}
		CertificateApplyResult result = new CertificateApplyResult();	
		
		try {// enterprise.getTelephone() 采用企业信息中维护的手机号
			result = BestSignUtil.handleCACertificate(enterprise.getFullName(), member.getTrueName(), enterprise.getTelephone(),
					enterprise.getAddress(), enterprise.getProvince(), enterprise.getCity(), member.getIdentityNumber(), 
					enterprise.getRegisNo(), enterprise.getOrganizNo(), enterprise.getTaxNo());
			logger.debug("企业ID={},企业CA认证结果={}",id,result);
		} catch (Exception e) {
			logger.error("企业CA申请异常,id={},result={}" ,id,e,result);
			rDO.setSuccess(false);
			return rDO;
		}
		String  caNo = result.getCerNo();
		Enterprise record =new Enterprise();
		record.setId(id);
		record.setCaNo(caNo);
		record.setIsAuth(1);
		enterpriseManager.updateByPrimaryKeySelective(record);
		
		rDO.setSuccess(true);
		return rDO;
	}

	/**
	 * @desc 图片上传
	 * @param id
	 * @author zhanghao
	 * @time 2016年7月6日 下午2:11:13
	 **/
	@Override
	public ResultDO<Object> uploadImage(Long id,String imgType,String filePath,String imgName) {
		// TODO Auto-generated method stub    图片上传
		ResultDO<Object> rDO = new ResultDO<Object>();
		Member member=new Member();
		Enterprise enterprise=new Enterprise();
		try {
			enterprise  =  enterpriseManager.selectByKey(id);
			member = memberManager.selectByPrimaryKey(enterprise.getLegalId());
		} catch (ManagerException e1) {
			logger.error("企业图片上传,获取企业信息异常,id={}" ,id,e1);
		}
		UploadUserImageResult result=new UploadUserImageResult();
		try {
			result = BestSignUtil.uploadUserSign(enterprise.getTelephone().toString(), imgType, filePath, imgName, enterprise.getFullName());
		} catch (Exception e) {
			logger.error("企业图片上传异常,id={},result={}" ,id,e,result);
			rDO.setSuccess(false);
			return rDO;
		}
		
		String url = this.upLoadFile(id, filePath, imgName);
		rDO.setResult(url);
		rDO.setSuccess(true);
		return rDO;
	}
	
	private String upLoadFile(Long id,String filePath,String imgName){
		
		
		String lKey = "";
    	SimpleDateFormat lSdf = new SimpleDateFormat("yyyy-MM-dd");
    	
    	String[] lDateFrags = lSdf.format(DateUtils.getCurrentDate()).split("-");
    	lKey += id + "/" +	lDateFrags[0] + "/" +lDateFrags[1] + "/" + lDateFrags[2] + "/" +imgName;
		
		String ossurl = OSSUtil.uploadImageToOSS(lKey,filePath);
		return ossurl;
	}
	

	/**
	 * @desc 获取合同下载链接
	 * @param transactionId
	 * @return
	 * @author zhanghao
	 * @time 2016年7月6日 下午2:11:13
	 **/
	@Override
	public ResultDO<Object> getDownUrl(Long transactionId) {
		// TODO Auto-generated method stub
		ResultDO<Object> rDO = new ResultDO<Object>();
		ContractSign contractSign  = null;
		String url="";
		Map<String,Object> map = Maps.newHashMap();
		map.put("transactionId", transactionId);
		map.put("type", StatusEnum.CONTRACT_PARTY_SECOND.getStatus());
		try {
			contractSign  = contractSignManager.selectByMap(map);
		} catch (ManagerException e) {
			logger.error("获取合同下载链接,获取合同相关信息异常,transactionId={}" ,transactionId,e);
		}
		
		try {
			url = BestSignUtil.getContractDownURL(contractSign.getSignId());
			logger.info("交易ID={},获取下载链接结果={}",transactionId,url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("获取合同下载链接,transactionId={}" ,transactionId,e);
			e.printStackTrace();
		}
		
		rDO.setResult(url);
		rDO.setSuccess(true);	
		return rDO;
	}
	
	
	
	
	/**
	 * @desc 针对不同途径的手签，拼接返回url
	 * @return
	 * @author zhanghao
	 * @time 2016年7月6日 下午2:11:13
	 **/
	private String getReturnUrl(Integer source){
		String  returnurl=Config.bestSignReturnUrl;
		returnurl = returnurl + "?loginSource="+source;
		return returnurl;
	}
	
	
	
	//TODO 保全
	/**
	 * 
	 * @Description:合同保全（下载PDF合同，准备保全）
	 * @throws Exception
	 * @author: zhanghao
	 * @time:2016年7月18日 下午9:08:31
	 */
	public String getContractPreservation(Long transactionId,String fromSys) {
		Preservation  pre = new Preservation();
		try {
			pre = preservationManager.selectByTransactionId(transactionId);
		} catch (ManagerException e1) {
			logger.error("合同保全，查询是否已经保全异常，transactionId={}" ,transactionId,e1);
		}
		if(pre!=null&&pre.getPreservationId()!=null){
			logger.info("合同已经保全，transactionId={}" ,transactionId);
			return "";
		}
		
		//String signId = "14666773425714IQK2";
		ContractSign contractSign  = null;
		String saveDirUrl="";
		Map<String,Object> map = Maps.newHashMap();
		map.put("transactionId", transactionId);
		map.put("type", StatusEnum.CONTRACT_PARTY_SECOND.getStatus());
		try {
			contractSign  = contractSignManager.selectByMap(map);
		} catch (ManagerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			saveDirUrl = BestSignUtil.getContractDown(contractSign.getSignId(),transactionId);
			logger.info("交易ID={},下载合同文件结果={}",transactionId,saveDirUrl);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("下载合同文件,transactionId={}" ,transactionId,e);
			e.printStackTrace();
		}
		
		try {
			BestSignUtil.unzipFile(saveDirUrl,saveDirUrl,transactionId);
		} catch (Exception e) {
			logger.error("解压缩合同文件,transactionId={}，saveDirUrl={}" ,transactionId,saveDirUrl,e);
		}
		
		BscAttachment attachment = new BscAttachment();
		attachment.setFileExt(FileInfoUtil.FILE_SUFFIX_PDF);
		attachment.setFileUrl(saveDirUrl);
		
		attachment.setFileSize(FileInfoUtil.getFileSize(saveDirUrl + transactionId+"已签署.pdf"));
		//attachment.setFileSize(FileInfoUtil.getFileSize( saveDirUrl + transactionId+".pdf"));
		attachment.setStatus(1);
		String fileDir =  saveDirUrl + transactionId+"已签署.pdf";
		//String fileDir =  saveDirUrl + "\\" +transactionId+".pdf";
		//将合同文件上传到阿里云OSS
		attachment.setListOrder(0);
		attachment.setUploadTime(DateUtils.getCurrentDate());
		attachment.setModule(Constant.ATTACHMENT_CONTRACT_IDENTITY);
		attachment.setDelFlag(Constant.ENABLE);
		attachment.setStorageWay(TypeEnum.ATTACHMENT_STORAGE_WAY_OSS.getCode());
		attachment.setFileName(transactionId+"已签署"+".pdf");
		
		OSSUtil.uploadContractToOSS(
				OSSUtil.getContractKey(contractSign.getSourceId().toString(), attachment.getFileName(), DateUtils.getCurrentDate()), 
				fileDir
				);
		try {
			int rows = bscAttachmentManager.insertSelective(attachment);
			if (rows > 0) {
				attachmentIndexManager.deleteAttachmentIndexByKeyId(transactionId.toString());
				attachmentIndexManager.insertAttachmentIndex(attachment.getId(), transactionId.toString());
			}
			//创建数据保全2017-04-21 易保全接口屏蔽换成上上签实时保全接口
			Transaction transaction = transactionManager.selectTransactionById(transactionId);
			Member member = memberManager.selectByPrimaryKey(transaction.getMemberId());
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			String name="";
			if(project.isDirectProject()){
				name=Constant.CONTRACT_P2P_TILE;
			}else{
				name=Constant.CONTRACT_DEBT_TILE;
			}

			Preservation preservation =preservationManager.selectByTransactionId(transactionId);
			if(preservation ==null) {
				preservation = new Preservation();
				preservation.setContractPath(fileDir);
				preservation.setContractTitle(transactionId + name);
				preservation.setIdentityNumber(member.getIdentityNumber());
				preservation.setIdentiferTrueName(member.getTrueName());
				preservation.setMemberId(transaction.getMemberId());
				preservation.setInvestAmount(transaction.getInvestAmount());
				preservation.setTransactionId(transactionId);
				preservation.setMemberPhone(member.getMobile().toString());
				preservation.setFromSys(fromSys);
				preservation.setAttachmentId(attachment.getId());
				preservationManager.createPreservation(preservation);
			}
		} catch (Exception e) {
			logger.error("保存已签署合同异常,transactionId={}，saveDirUrl={}" ,transactionId,saveDirUrl,e);
		}
		
		
		return saveDirUrl;
	}
	
	/**
	 * @desc 同步签署信息
	 * @param transactionId
	 * @return
	 * @author zhanghao
	 * @time 2016年7月6日 下午2:03:44
	 **/
	@Override
	public ResultDO<Object> queryContractInfo(Long transactionId) {
		ResultDO<Object> rDO = new ResultDO<Object>();
		String signId ="";
		ContractSign contractSign  = null;
		Map<String,Object> map = Maps.newHashMap();
		map.put("transactionId", transactionId);
		map.put("type", StatusEnum.CONTRACT_PARTY_SECOND.getStatus());
		try {
			contractSign  = contractSignManager.selectByMap(map);
		
		} catch (ManagerException e) {
			logger.error("同步签署信息：获取用户合同相关信息,transactionId={}" , transactionId);
			rDO.setSuccess(false);	
			return rDO;
		}
		if(contractSign==null){
			logger.error("同步签署信息：获取用户合同相关信息为空,该交易尚未完成初始化,transactionId={}" , transactionId);
			rDO.setSuccess(false);	
			rDO.setResultCode(ResultCode.CONTRACT_SIGN_FAIL_TO_INIT);
			return rDO;	
		}
		signId = contractSign.getSignId();
		
		JSONArray jsonArray;
		try {
			jsonArray = BestSignUtil.queryContractInfo(signId);
		} catch (Exception e) {
			logger.error("同步签署信息：同步失败,transactionId={}" , transactionId);
			rDO.setSuccess(false);	
			return rDO;
		}
		int num = 0;
		for(Object obj : jsonArray){
			 Map objMap = (Map)obj;
			 objMap.get("userinfo");
			 JSONObject  jasonobj= (JSONObject) objMap.get("userinfo");
			 Map objRes = (Map)jasonobj;
			 if(Long.valueOf(1L).equals(Long.valueOf(objRes.get("is_sender").toString()))){
				 continue;
			 }
			 if(contractSign.getMobile().equals(Long.valueOf(objRes.get("mobile").toString()))){
				 if(Integer.valueOf(objRes.get("status").toString())==2&&contractSign.getStatus()!=2){
					 ContractSign record = new ContractSign();
						record.setId(contractSign.getId());
						record.setStatus(StatusEnum.CONTRACT_SIGN_STATUS_SUCCESS.getStatus());
						try {
							 num = contractSignManager.updateByPrimaryKey(record);
						} catch (ManagerException e) {
							logger.error("同步签署信息：同步成功，签署成功，更新签署状态异常,transactionId={}" , transactionId,e);
						}
						Transaction tra = new Transaction();
						tra.setId(contractSign.getTransactionId());
						tra.setSignStatus(StatusEnum.CONTRACT_STATUS_ALREADY_SIGN.getStatus());
						try {
							transactionManager.updateByPrimaryKeySelective(tra);
						} catch (ManagerException e) {
							logger.error("同步签署信息：同步成功，签署成功，更新交易表合同签署状态异常,tra={}" , tra,e);
						}
						
				 }
			 }
		 }
		rDO.setResult(num);
		rDO.setSuccess(true);	
		return rDO;
	}
	
	private void beforeCheck(Long transactionId){
		this.preSign(transactionId, "web");
		this.autoSignFirst(transactionId);
		this.autoSignThird(transactionId);
	}

	
	
}
