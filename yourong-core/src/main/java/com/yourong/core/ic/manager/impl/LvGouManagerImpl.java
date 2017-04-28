package com.yourong.core.ic.manager.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.common.util.DateUtil;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FileInfoUtil;
import com.yourong.common.util.LvGouUtil;
import com.yourong.core.bsc.manager.AttachmentIndexManager;
import com.yourong.core.bsc.manager.BscAttachmentManager;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.manager.LvGouManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.DebtBiz;
import com.yourong.core.ic.model.DebtCollateral;
import com.yourong.core.ic.model.DebtPledge;
import com.yourong.core.ic.model.Project;
import com.yourong.core.tc.manager.ContractManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.ContractForProjectBiz;
import com.yourong.core.tc.model.biz.TransactionForMemberCenter;
import com.yourong.core.uc.manager.MemberInfoManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberInfo;

@Component
public class LvGouManagerImpl implements LvGouManager {

	private Logger logger = LoggerFactory.getLogger(LvGouManagerImpl.class);
			
	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired
	private TransactionInterestManager transactionInterestManager;
	
	@Autowired
    private DebtManager debtManager;
	
	@Autowired
	private MemberInfoManager memberInfoManager;
	
	@Autowired
	private ContractManager contractManager;

	@Autowired
	private BscAttachmentManager bscAttachmentManager;
	
	@Autowired
	private AttachmentIndexManager attachmentIndexManager;
	
	@Override
	public int createLvGouContract(Long projectId) {
		try {
			//入参验证
			Assert.isInstanceOf(Long.class, projectId);
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("id", projectId);
			//List<Project> projectList = projectManager.queryProjectFromLvGou(paraMap);
			Map<String, Object> attMap = attachmentIndexManager.queryAttachmentInfoByIndex(projectId, "project_contract");
			Project project = projectManager.selectByPrimaryKey(projectId);
			String retMsg = null;
			if(attMap == null) {
				//拼装电子合同
				Map<String, Object> model = buildModel(project);
				String path = Config.prefixPath
						+ FileInfoUtil.getContractFolderByProject(projectId.toString(), DateUtils.getCurrentDate());
				String realPath = path + projectId.toString() + FileInfoUtil.FILE_SUFFIX_HTML;
				FileInfoUtil.mkdir(path);
				//生成合同html文件
				contractManager.createHtmlByVelocity(realPath, "contract/greenDog-contractPreview.vm", model);
				//生成合同pdf文件
				BscAttachment attachment = new BscAttachment();
				attachment.setFileUrl(FileInfoUtil.getContractFolderByProject(projectId.toString(), DateUtils.getCurrentDate()));
				attachment.setFileExt(FileInfoUtil.FILE_SUFFIX_PDF);
				contractManager.generatePdfContract(realPath, path , projectId.toString(), attachment);
				attachment.setFileSize(FileInfoUtil.getFileSize(Config.prefixPath + attachment.getFileUrl() + attachment.getFileName()));
				attachment.setStatus(1);
				attachment.setListOrder(0);
				attachment.setUploadTime(DateUtils.getCurrentDate());
				attachment.setModule("project_contract");
				attachment.setDelFlag(Constant.ENABLE);
				attachment.setStorageWay("1");
				int rows = bscAttachmentManager.insertSelective(attachment);
				if(rows>0) {
					attachmentIndexManager.insertAttachmentIndex(attachment.getId(), projectId.toString());
				}
				//过期日期(时间戳,单位秒)
				int endtime = (int)(DateUtils.addDate(project.getEndDate(), 1).getTime() / 1000);
				//融资期限(单位月)
				String duration = String.format("%.2f", (double)(DateUtil.getDiffDays(project.getEndDate(), project.getStartDate())) / 30);
				//融资金额(单位元)
				String financing = Integer.toString(project.getTotalAmount().intValue());
				//提交给绿狗
				LvGouUtil lgUtil = new LvGouUtil();
				retMsg = lgUtil.contractAdd(project.getName(),
						Long.toString(projectId),
						model.get("users").toString(), Config.prefixPath + attachment.getFileUrl()
								+ attachment.getFileName(),
						projectId.toString(), "债权收益权转让协议", endtime, financing,
						duration);
			} else {
				//拼装电子合同
				Map<String, Object> model = buildModel(project);
				//过期日期(时间戳,单位秒)
				int endtime = (int)(project.getEndDate().getTime() / 1000);
				//融资期限(单位月)
				String duration = String.format("%.2f", (double)(DateUtil.getDiffDays(project.getEndDate(), project.getStartDate())) / 30);
				//融资金额(单位元)
				String financing = Integer.toString(project.getTotalAmount().intValue());
				//提交给绿狗
				LvGouUtil lgUtil = new LvGouUtil();
				retMsg = lgUtil.contractAdd(project.getName(),
						Long.toString(projectId),
						model.get("users").toString(), Config.prefixPath + attMap.get("fileDir").toString(),
						projectId.toString(), "债权收益权转让协议", endtime, financing,
						duration);
			}
			logger.info("绿狗推送项目 projectId={}, status={}", projectId, retMsg);
			//标识项目已上传    
			if(retMsg.indexOf(":0") > -1) { //成功的返回码: satats={"code":0}
				Project updatePara = new Project();
				updatePara.setId(projectId);
				updatePara.setLvgouFlag(1);
				return projectManager.updateByPrimaryKeySelective(updatePara);
			}
			return 0;
		} catch(Exception e) {
			logger.error("绿狗推送项目执行失败projectId={}", projectId, e);
			return 0;
		}
	}

	@Override
	public void updateLvGouContractStatus(Long projectId, int status) {
		try {
			if(projectId > 0 && status >= 0 && status <= 1) {
				LvGouUtil util = new LvGouUtil();
				String retMsg = util.projectstatusUpdate(projectId.toString(), Integer.toString(status));
				logger.info("绿狗更新项目 projectId=" + projectId + "状态码status=" + status + "执行成功，返回码=" + retMsg);				
			}	
		} catch(Exception e) {
			logger.error("绿狗更新项目 projectId=" + projectId + "状态码status=" + status + "执行失败", e);
		}
	}

	/**
	 * 拼装合同信息
	 * @param project
	 * @return
	 * @throws ManagerException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> buildModel(Project project) throws ManagerException {
		//获取债权信息，包括债权本息列表
		DebtBiz debtBiz = debtManager.getFullDebtInfoById(project.getDebtId());
		//获取投资人的手机、身份证号、年利率
		List<TransactionForMemberCenter> memberList = transactionManager.selectTransactionMemberByPorjectId(project.getId());
		ContractForProjectBiz contractForProjectBiz = new ContractForProjectBiz();
		//获取项目履约时间
		contractForProjectBiz.setInvestDate(DateUtils.getStrFromDate(memberList.get(memberList.size() - 1).getTransactionTime(),
				DateUtils.DATE_FMT_4));
		//拼装投资该项目的所有投资人用户名和身份证后六位
		String users = null;
		for(TransactionForMemberCenter t : memberList) {
			t.setPrice(t.getInvestAmount().add(t.getTotalInterest()));
			if(StringUtil.isEmpty(users))
				users = t.getMobile() + ":" + t.getIdentityNumber().substring(t.getIdentityNumber().length() - 6);
			else
				users += "," + t.getMobile() + ":" + t.getIdentityNumber().substring(t.getIdentityNumber().length() - 6);
		}
		contractForProjectBiz.setUsers(users);
		if(debtBiz.getDebtCollateral()!=null) {
			DebtCollateral debtCollateral = debtBiz.getDebtCollateral();
			Map<String, Object> ruleMap = JSON.parseObject(
					debtCollateral.getCollateralDetails(),
					new TypeReference<Map<String, Object>>() {
					});
			if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_CAR.getCode())) {
				contractForProjectBiz.setCollateralDetails(ruleMap.get("car_cx").toString());
			}
			if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_HOUSE.getCode())) {
				contractForProjectBiz.setCollateralDetails(ruleMap.get("house_fwzl").toString()+ruleMap.get("house_fwlx").toString()+ruleMap.get("house_jzmj").toString());
			}
		}
		if(debtBiz.getDebtPledge()!=null) {
			DebtPledge debtPledge = debtBiz.getDebtPledge();
			Map<String, Object> ruleMap = JSON.parseObject(
					debtPledge.getPledgeDetails(),
					new TypeReference<Map<String, Object>>() {
					});
			String pledgeType = debtPledge.getPledgeType();
			if(pledgeType.equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_CAR.getCode())) {
				contractForProjectBiz.setCollateralDetails(ruleMap.get("car_cx").toString());
			}
			if(pledgeType.equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_NEWCAR.getCode()) || pledgeType.equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_EQUITY.getCode())) {
				contractForProjectBiz.setCollateralDetails(ruleMap.get("car_ms").toString());
			}
		}
		//债权相关
		contractForProjectBiz.setContractId(project.getId().toString());
		contractForProjectBiz.setOriginalDebtNumber(debtBiz.getOriginalDebtNumber());
		contractForProjectBiz.setDebtAmount(debtBiz.getAmount().toPlainString());
		contractForProjectBiz.setDebtAnnualizedRate(debtBiz.getAnnualizedRate() + "");
		contractForProjectBiz.setDebtEndTime(DateUtils.getStrFromDate(debtBiz.getEndDate(),
				DateUtils.DATE_FMT_4));
		contractForProjectBiz.setDebtStartTime(DateUtils.getStrFromDate(debtBiz.getStartDate(),
				DateUtils.DATE_FMT_4));
		contractForProjectBiz.setDebtTypeCode(debtBiz.getDebtType());
		//原始债权人信息
		Member originalCreditorPersonal = debtBiz.getLenderMember();
		MemberInfo originalCreditorPersonalMemberInfo = memberInfoManager.getMemberInfoByMemberId(originalCreditorPersonal.getId());
		if (originalCreditorPersonal != null && originalCreditorPersonalMemberInfo!=null) {
			contractForProjectBiz.setOriginalCreditorAddress(originalCreditorPersonalMemberInfo.getAddress());
			contractForProjectBiz.setOriginalCreditorIdentityNumber(originalCreditorPersonal
					.getIdentityNumber());
			contractForProjectBiz.setOriginalCreditorName(originalCreditorPersonal.getTrueName());
			contractForProjectBiz.setOriginalCreditorPhone(originalCreditorPersonal.getMobile()+"");
		}
		//获取项目的付息时间金额表
		List<TransactionInterest> transInterestList =  transactionInterestManager.queryInterestByProjectId(project.getId());
		for (TransactionInterest transactionInterest : transInterestList) {
			transactionInterest.setPayTimeStr(DateUtils.getStrFromDate(
					transactionInterest.getEndDate(), DateUtils.DATE_FMT_4));
		}
		//章
		contractForProjectBiz.setSealUrl(Config.getPrefixPath());
		Map<String, Object> model = null;
		model = BeanCopyUtil.map(contractForProjectBiz, HashMap.class);
		model.put("memberList", memberList);
		model.put("transInterestList", transInterestList);
		return model;
	}
}
