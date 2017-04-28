package com.yourong.api.service.impl;

import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.api.service.OpenService;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.OpenServiceResultDO;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.OpenServiceResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.CryptHelper;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.Identities;
import com.yourong.common.util.RegexUtils;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.manager.ProjectOpenManager;
import com.yourong.core.ic.model.DebtCollateral;
import com.yourong.core.ic.model.DirectProjectBiz;
import com.yourong.core.ic.model.ProjectOpen;
import com.yourong.core.ic.model.biz.ProjectOpenSynBiz;
import com.yourong.core.os.biz.AuthAndSaveProjectBiz;
import com.yourong.core.os.biz.FileBiz;
import com.yourong.core.os.biz.OpenServiceOutPut;
import com.yourong.core.os.biz.ProjectsPageOutPut;
import com.yourong.core.os.biz.ProjectsPageQuery;
import com.yourong.core.os.biz.ProjectsStatusOutPut;
import com.yourong.core.os.biz.ProjectsStatusQuery;
import com.yourong.core.os.biz.SynStatusBiz;
import com.yourong.core.os.biz.SynStatusOutPut;
import com.yourong.core.os.biz.Token;
import com.yourong.core.os.biz.TokenBiz;
import com.yourong.core.os.biz.TokenOutPut;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.uc.manager.MemberInfoManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.manager.MemberOpenManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberInfo;
import com.yourong.core.uc.model.MemberOpen;
import org.springframework.util.Assert;

@Service
public class OpenServiceImpl implements OpenService {

	private static final Logger logger = LoggerFactory.getLogger(OpenServiceImpl.class);

	@Autowired
	private MemberManager memberManager;

	@Autowired
	private MemberInfoManager memberInfoManager;

	@Autowired
	private TaskExecutor threadPool;

	@Autowired
	private ProjectOpenManager projectOpenManager;

	@Autowired
	private MemberOpenManager memberOpenManager;

	@Autowired
	private SysDictManager sysDictManager;

	@Autowired
	private ProjectManager projectManager;

	@Override
	public OpenServiceResultDO<OpenServiceOutPut> authMemberSaveProject(AuthAndSaveProjectBiz input) {
		// 构建返回对象
		OpenServiceResultDO<OpenServiceOutPut> outPut = new OpenServiceResultDO<OpenServiceOutPut>(OpenServiceResultDO.DEFULT_SUCCESS);
		try {
			OpenServiceOutPut result = new OpenServiceOutPut();
			if (StringUtil.isNotBlank(input.getOutBizNo())) {
				result.setOutBizNo(input.getOutBizNo());
			}
			outPut.setResult(result);
			// 基础校验
			input.valid(outPut);
			if (!outPut.isSuccess()) {
				return outPut;
			}
			// 业务校验
			authMemberSaveProjectPreAvlid(input, outPut);
			if (!outPut.isSuccess()) {
				return outPut;
			}

			ProjectOpen projectOpen = convertProjectOpen(input);
			// 校验对外业务id
			if (!projectOpenManager.canBuildprojectOpen(input.getOutBizNo())) {
				outPut.setResultCodeByEnum(OpenServiceResultCode.CHECK_REPEATED_ERROR);
				outPut.setRemark(OpenServiceResultCode.CHECK_REPEATED_ERROR.getResultMsg());
				return outPut;
			}
			// 记录接口入参信息
			ProjectOpen resultOpen = null;
			try {
				resultOpen = projectOpenManager.insertProjectOpen(projectOpen);
			} catch (DuplicateKeyException e) {
				logger.error("重复的外部业务编号:" + projectOpen.getOutBizNo() + "" + e.toString());
				outPut.setResultCodeByEnum(OpenServiceResultCode.CHECK_REPEATED_ERROR);
				outPut.setRemark(OpenServiceResultCode.CHECK_REPEATED_ERROR.getResultMsg());
				return outPut;
			}
			if (resultOpen == null) {
				outPut.setResultCodeByEnum(OpenServiceResultCode.SYSTEM_ERROR);
				return outPut;
			}
			// 认证会员并创建标的
			threadPool.execute(new authMemberSaveProjectThread(input, projectOpen));
		} catch (Exception e) {
			logger.error("认证会员并创建项目 input={}", input, e);
			outPut.setResultCodeByEnum(OpenServiceResultCode.SYSTEM_ERROR);
		}
		return outPut;
	}

	private void authMemberSaveProjectPreAvlid(AuthAndSaveProjectBiz input, OpenServiceResultDO<OpenServiceOutPut> outPut) {
		if (input.getTrueName().length() > 20) {
			outPut.setResultCodeByEnum(OpenServiceResultCode.INPUT_VALID_ERROR);
			outPut.setRemark("姓名过长, trueName=" + input.getTrueName());
			return;
		}
		if (!RegexUtils.checkMobile(input.getMobile().toString())) {
			outPut.setResultCodeByEnum(OpenServiceResultCode.INPUT_VALID_ERROR);
			outPut.setRemark("手机号码格式错误, mobile=" + input.getMobile());
			return;
		}
		if (!RegexUtils.checkIdCard(input.getIdentityNumber())) {
			outPut.setResultCodeByEnum(OpenServiceResultCode.INPUT_VALID_ERROR);
			outPut.setRemark("身份证号码格式错误, identityNumber=" + input.getIdentityNumber());
			return;
		}
		if (!RegexUtils.checkIpAddress(input.getIp())) {
			outPut.setResultCodeByEnum(OpenServiceResultCode.INPUT_VALID_ERROR);
			outPut.setRemark("ip格式错误, ip=" + input.getIp());
			return;
		}
		if (input.getOutBizNo().length() > 50) {
			outPut.setResultCodeByEnum(OpenServiceResultCode.INPUT_VALID_ERROR);
			outPut.setRemark("外部业务编号过长, outBizNo==" + input.getOutBizNo());
			return;
		}

		if("shandai".equals(input.getChannelKey())){
			if(input.getTotalAmount().compareTo(new BigDecimal(1)) == -1 || input.getTotalAmount().doubleValue() % 1d != 0){
				outPut.setResultCodeByEnum(OpenServiceResultCode.INPUT_VALID_ERROR);
				outPut.setRemark("借款金额格式错误, totalAmount=" + input.getTotalAmount());
				return;
			}
		}else if("jimistore".equals(input.getChannelKey())){
			if(input.getTotalAmount().compareTo(new BigDecimal(10)) == -1 || input.getTotalAmount().doubleValue() % 10d != 0){
				outPut.setResultCodeByEnum(OpenServiceResultCode.INPUT_VALID_ERROR);
				outPut.setRemark("借款金额格式错误, totalAmount=" + input.getTotalAmount());
				return;
			}
		}

		if (input.getBorrowPeriod() < 1) {
			outPut.setResultCodeByEnum(OpenServiceResultCode.INPUT_VALID_ERROR);
			outPut.setRemark("借款周期格式错误, borrowPeriod=" + input.getBorrowPeriod());
			return;
		}
		if (input.getBorrowPeriodType() < 1 || input.getBorrowPeriodType() > 4) {
			outPut.setResultCodeByEnum(OpenServiceResultCode.INPUT_VALID_ERROR);
			outPut.setRemark("借款周期格式错误, borrowPeriodType=" + input.getBorrowPeriodType());
			return;
		}
		if (input.getBorrowerIncome() != null && (input.getBorrowerIncome() < 1 || input.getBorrowerIncome() > 10)) {
			outPut.setResultCodeByEnum(OpenServiceResultCode.INPUT_VALID_ERROR);
			outPut.setRemark("借款人收入格式错误, borrowerIncome=" + input.getBorrowerIncome());
			return;
		}
		if (StringUtil.isNotBlank(input.getBorrowerJob()) && input.getBorrowerJob().length() > 50) {
			outPut.setResultCodeByEnum(OpenServiceResultCode.INPUT_VALID_ERROR);
			outPut.setRemark("借款人职业过长, borrowerJob=" + input.getBorrowerJob());
			return;
		}
		if (StringUtil.isNotBlank(input.getBorrowerBasicInfo()) && input.getBorrowerBasicInfo().length() > 50) {
			outPut.setResultCodeByEnum(OpenServiceResultCode.INPUT_VALID_ERROR);
			outPut.setRemark("借款人基本信息过长, borrowerBasicInfo=" + input.getBorrowerBasicInfo());
			return;
		}
		if (input.getAnnualizedRate().doubleValue() < 0d || input.getAnnualizedRate().doubleValue() > 100d) {
			outPut.setResultCodeByEnum(OpenServiceResultCode.INPUT_VALID_ERROR);
			outPut.setRemark("年化格式错误, borrowerBasicInfo=" + input.getAnnualizedRate());
			return;
		}
		// 校验实名情况
		checkAuth(input, outPut);
	}

	private void checkAuth(AuthAndSaveProjectBiz input, OpenServiceResultDO<OpenServiceOutPut> outPut) {
		Member realMember = null;
		try {
			// 先查询这个身份证号是否已经注册
			Map<String, Object> map = Maps.newHashMap();
			map.put("identityNumber", input.getIdentityNumber());
			Member memberForThisID = memberManager.getMemberId(map);
			if (memberForThisID == null) {
				// 身份证没有在平台实名
				Member memberForMobile = memberManager.selectByMobile(input.getMobile());
				// 手机号已经被占用
				if (memberForMobile != null) {
					outPut.setResultCodeByEnum(OpenServiceResultCode.MEMBER_AUTH_ERROR);
					outPut.setRemark("手机号在平台已经存在,但是身份证信息不匹配！");
					return;
				}
			} else {
				// 身份证已经在平台实名, 看下姓名是否匹配
				realMember = memberManager.selectByPrimaryKey(memberForThisID.getId());
				if (!input.getTrueName().equals(realMember.getTrueName())) {
					outPut.setResultCodeByEnum(OpenServiceResultCode.MEMBER_AUTH_ERROR);
					outPut.setRemark("身份证在平台已经存在,但是姓名不匹配！");
					return;
				}
			}
		} catch (Exception e) {
			logger.error("认证会员并创建项目失败 input={}", e);
			outPut.setResultCodeByEnum(OpenServiceResultCode.SYSTEM_ERROR);
		}
	}
	
	private class authMemberSaveProjectThread implements Runnable{

		private AuthAndSaveProjectBiz input;
		private ProjectOpen projectOpen;
		
		public authMemberSaveProjectThread(final AuthAndSaveProjectBiz authAndSaveProjectBiz, ProjectOpen projectOpen) {
			this.input = authAndSaveProjectBiz;
			this.projectOpen = projectOpen;
		}
		
		@Override
		public void run() {
			authMemberSaveProjectProc();
		}
		

		public void authMemberSaveProjectProc() {
			Member realMember = null;
			try {
				// 先查询这个身份证号是否已经注册
				Map<String, Object> map = Maps.newHashMap();
				map.put("identityNumber", input.getIdentityNumber());
				Member memberForThisID = memberManager.getMemberId(map);
				if (memberForThisID == null) {
					// 身份证没有在平台实名
					Member memberForMobile = memberManager.selectByMobile(input.getMobile());
					// 手机号已经被占用
					if (memberForMobile != null) {
						logger.error("手机号已经被占用 input={}", input);
						projectOpenManager.updateStatusAndRemarkById(2, "手机号已经被占用", projectOpen.getId(), 1);
						return;
					}
					// 注册新会员
					try {
						// 生成密码
						String password = Identities.randomBase62(6);
						ResultDO<Member> resultDO = memberManager.registerMemberAndAuth(input.getMobile(), password, input.getTrueName(),
								input.getIdentityNumber(), StatusEnum.MEMBER_SOURCE_OPEN_PLATFORM.getStatus(), "jimistore", input.getIp());
						realMember = resultDO.getResult();

					} catch (Exception e) {
						logger.error("注册会员失败 input={}", input, e);
						projectOpenManager.updateStatusAndRemarkById(2, "注册会员失败", projectOpen.getId(), 1);
						return;
					}
				} else {
					// 身份证已经在平台实名, 但是与姓名是否匹配
					realMember = memberManager.selectByPrimaryKey(memberForThisID.getId());
					if (!input.getTrueName().equals(realMember.getTrueName())) {
						logger.error("身份证已经在平台实名, 但是与姓名是否匹配 input={}", input);
						projectOpenManager.updateStatusAndRemarkById(2, "身份证已经在平台实名, 但是与姓名是否匹配", projectOpen.getId(), 1);
						return;
					}
					// 查看是否存在会员信息表记录
					MemberInfo memberInfo = memberInfoManager.getMemberInfoByMemberId(realMember.getId());
					if (memberInfo == null) {
						MemberInfo memberinfo = new MemberInfo();
						memberinfo.setMemberId(realMember.getId());
						try {
							memberInfoManager.saveMemberInfoByMemberId(memberinfo);
						} catch (DuplicateKeyException e) {
							logger.error("重复插入会员信息表记录 memberId={}", realMember.getId());
						}
					}
				}
				// 查询是否存在会员渠道绑定关系
				List<MemberOpen> memberOpenList = memberOpenManager.queryMemberOpenByMemberIdAndKey(realMember.getId(),
						input.getChannelKey());
				if (Collections3.isEmpty(memberOpenList)) {
					// 获取渠道编号前缀
					SysDict openDict = sysDictManager.findByGroupNameAndKey("channel_key", input.getChannelKey());
					if (openDict != null) {
						// 插入会员渠道绑定关系
						MemberOpen memberOpen = new MemberOpen();
						memberOpen.setMemberId(realMember.getId());
						memberOpen.setOpenPlatformKey(input.getChannelKey());
						memberOpen.setOpenId(SerialNumberUtil.generateOpenNo(realMember.getId(), openDict.getDescription()));
						memberOpen.setStatus(1);
						memberOpenManager.insertMemberOpen(memberOpen);
					}
				}
				// 插入或更新收入和身份信息
				MemberInfo memberInfo = memberInfoManager.getMemberInfoByMemberId(realMember.getId());
				if (input.getBorrowerIncome() != null || StringUtil.isNotBlank(input.getBorrowerJob())) {
					if (input.getBorrowerIncome() != null) {
						memberInfo.setIncome(input.getBorrowerIncome().toString());
					}
					if (StringUtil.isNotBlank(input.getBorrowerJob())) {
						memberInfo.setDetailInfo(input.getBorrowerJob());
					}
					memberInfoManager.updateMemberInfoByMemberId(memberInfo);
				}
				// 更新项目表借款人信息
				projectOpenManager.updateBorrowMemberBaseBizAndStatusById(projectOpen.getId(), realMember, memberInfo);
			} catch (Exception e) {
				logger.error("认证会员并创建项目失败 input={}", input, e);
				projectOpenManager.updateStatusAndRemarkById(2, "认证会员并创建项目失败", projectOpen.getId(), 1);
				return;
			}
		}
	}

	/**
	 * 转换ProjectOpen
	 * @param input
	 * @return
     */
	private ProjectOpen convertProjectOpen(AuthAndSaveProjectBiz input){
		ProjectOpen projectOpen=new ProjectOpen();
		DirectProjectBiz directProjectBiz =new DirectProjectBiz();
		directProjectBiz.setTotalAmount(input.getTotalAmount());
		directProjectBiz.setAnnualizedRate(input.getAnnualizedRate());
		directProjectBiz.setBorrowPeriod(input.getBorrowPeriod());
		directProjectBiz.setBorrowPeriodType(input.getBorrowPeriodType());
		DebtCollateral debtCollateral=new DebtCollateral();
		debtCollateral.setCollateralDetails(input.getBorrowerBasicInfo());
		debtCollateral.setDebtType("credit");
		debtCollateral.setCollateralType("consumer_inst");
		debtCollateral.setCollateralDetails(input.getBorrowerBasicInfo());
		directProjectBiz.setDebtCollateral(debtCollateral);
		List<BscAttachment> bscAttachments=new ArrayList<>();
		//项目缩略图
		if (input.getAttachmentThumbnail() != null){
			BscAttachment thumbnail=new BscAttachment();
			thumbnail.setFileUrl(input.getAttachmentThumbnail().getFileUrl());
			thumbnail.setDelFlag(1);
			thumbnail.setFileExt(input.getAttachmentThumbnail().getSuffix());
			thumbnail.setFileName(input.getAttachmentThumbnail().getName());
			thumbnail.setFileSize(input.getAttachmentThumbnail().getFileSize());
			thumbnail.setModule("thumbnail");
			bscAttachments.add(thumbnail);
		}
		//项目形象图
		if (input.getAttachmentThumbnail() != null){
			BscAttachment sign=new BscAttachment();
			sign.setFileUrl(input.getAttachmentThumbnail().getFileUrl());
			sign.setDelFlag(1);
			sign.setFileExt(input.getAttachmentThumbnail().getSuffix());
			sign.setFileName(input.getAttachmentThumbnail().getName());
			sign.setFileSize(input.getAttachmentThumbnail().getFileSize());
			sign.setModule("direct_project_sign");
			bscAttachments.add(sign);
		}
		//个人相关图片
		convertFilebiz(bscAttachments,input.getAttachmentsPersonal(),"direct_project_collateral");
		//借款人相关(身份证正反面)图片
		convertFilebiz(bscAttachments,input.getAttachmentsBorrower(),"direct_project_borrower");
		//合同相关图片
		convertFilebiz(bscAttachments,input.getAttachmentsContract(),"direct_project_contract");
		//法律相关图片
		convertFilebiz(bscAttachments,input.getAttachmentsLegal(),"direct_project_legal");
		//征信相关图片
		convertFilebiz(bscAttachments,input.getAttachmentsCredit(),"direct_project_credit");
		//其他相关图片
		convertFilebiz(bscAttachments,input.getAttachmentsOther(),"direct_project_base");
		projectOpen.setOutBizNo(input.getOutBizNo());
		projectOpen.setOpenPlatformKey(input.getChannelKey());
		projectOpen.setTotalAmount(input.getTotalAmount());
		projectOpen.setBorrowPeriod(input.getBorrowPeriod());
		projectOpen.setBorrowPeriodType(input.getBorrowPeriodType());
		projectOpen.setBorrowerName(input.getTrueName());
		projectOpen.setIdentityNumber(input.getIdentityNumber());
		projectOpen.setProjectbizJson(JSON.toJSONString(directProjectBiz));
		projectOpen.setBscAttachmentsJson(JSON.toJSONString(bscAttachments));
		projectOpen.setPersonalInfo(input.getBorrowerBasicInfo());
		projectOpen.setJob(input.getBorrowerJob());
		projectOpen.setAnnualizedRate(input.getAnnualizedRate());
		projectOpen.setIncome(input.getBorrowerIncome());
		projectOpen.setStatus(1);
		projectOpen.setCreateTime(new Date());
		projectOpen.setDelFlag(1);
		projectOpen.setSku(input.getSku());
		return projectOpen;
	}

	/**
	 * 转换Filebiz
	 * @param Bsclist
	 * @param fileBizs
     */
	private void convertFilebiz(List<BscAttachment> Bsclist,List<FileBiz> fileBizs,String module){
		if (Collections3.isNotEmpty(fileBizs)){
			for (FileBiz filebiz:fileBizs) {
				BscAttachment bsc=new BscAttachment();
				bsc.setFileUrl(filebiz.getFileUrl());
				bsc.setDelFlag(1);
				bsc.setFileExt(filebiz.getSuffix());
				bsc.setFileName(filebiz.getName());
				bsc.setFileSize(filebiz.getFileSize());
				bsc.setModule(module);
				Bsclist.add(bsc);
			}
		}
	}

	@Override
	public OpenServiceResultDO<SynStatusOutPut> synProjectStatus(SynStatusBiz synStatusBiz) {
		// 构建返回对象
		OpenServiceResultDO<SynStatusOutPut> outPut = new OpenServiceResultDO<SynStatusOutPut>(OpenServiceResultDO.DEFULT_SUCCESS);
		SynStatusOutPut outPutBiz = new SynStatusOutPut();
		outPut.setResult(outPutBiz);
		try {
			ProjectOpenSynBiz biz = projectOpenManager.queryByOutBizNo(synStatusBiz.getOutBizNo());
			if (biz == null) {
				outPut.setResultCodeByEnum(OpenServiceResultCode.DATA_NOT_FOUND_ERROR);
			} else {
				outPut.getResult().setOutBizNo(biz.getOutBizNo());
				outPut.getResult().setProjectName(biz.getProjectName());
				outPut.getResult().setSku(biz.getSku());

				//已放款
				if (biz.getLoanStatus()!=null&&biz.getLoanStatus()==1){
					outPut.getResult().setStatus("5");
					outPut.getResult().setLoanTime(biz.getLoanTime());
				}else {
					switch (biz.getStatus()){
						case 1:outPut.getResult().setStatus("1");break;
						case 2:outPut.getResult().setStatus("2");break;
						case 3:outPut.getResult().setStatus("3");break;
						case 4:outPut.getResult().setStatus("4");break;
						case 5:outPut.getResult().setStatus("3");break;
						case 6:outPut.getResult().setStatus("6");outPut.getResult().setRefuseInfo(biz.getRefuseCause());break;
					}
				}
			}
		} catch (Exception e) {
			logger.error("认证会员并创建项目 input={}", synStatusBiz, e);
			outPut.setResultCodeByEnum(OpenServiceResultCode.SYSTEM_ERROR);
		}
		return outPut;
	}

	@Override
	public TokenOutPut getDataQueryToken(TokenBiz tokenBiz) {
		if (StringUtil.isBlank(tokenBiz.getUsername()) || StringUtil.isBlank(tokenBiz.getPassword())) {
			return new TokenOutPut(new Token(""));
		}
		try {
			// 字典表获取
			SysDict openDict = sysDictManager.findByGroupNameAndKey("open_api_token", tokenBiz.getUsername());
			if (openDict != null && tokenBiz.getPassword().equals(openDict.getValue())) {
				// 生成token
				String tokenStr = openDict.getLabel() + RedisConstant.REDIS_SEPERATOR
						+ DateUtils.getCurrentDate().getTime();
				String key = sysDictManager.findByGroupNameAndKey("open_api_key", "key").getValue();
				// aes加密,密码默认作为私钥
				String token = CryptHelper.aesEncrypt(tokenStr, key);
				// 记录redis
				String redisKey = RedisConstant.OPEN_SERVICE_DATA_QUERY_TOKEN + openDict.getLabel();
				if (RedisManager.set(redisKey, token)) {
					// 默认两小时有效
					RedisManager.expire(redisKey, 60 * 60 * 2);
					return new TokenOutPut(new Token(token));
				}
			}
		} catch (ManagerException | GeneralSecurityException e) {
			logger.error("获取数据对接token失败 input={}", tokenBiz, e);
		}
		// 构建返回对象
		return new TokenOutPut(new Token(""));
	}

	@Override
	public ProjectsPageOutPut getOverPros(ProjectsPageQuery projectsPageQuery) {
		if (projectsPageQuery.getToken() == null
				|| projectsPageQuery.getDate() == null
				|| projectsPageQuery.getPage() == null
				|| projectsPageQuery.getPageSize() == null) {
			return null;
		}
		try {
			if (checkToken(projectsPageQuery.getToken(), "getOverPros")) {
				String date = projectsPageQuery.getDate();
				Date startTime = DateUtils.getDateFromString(date, DateUtils.DATE_FMT_3);
				Date endTime = DateUtils.getEndTime(startTime);
				Map<String, Object> queryMap = Maps.newHashMap();
				queryMap.put("startTime", startTime);
				queryMap.put("endTime", endTime);
				queryMap.put("currentPage", projectsPageQuery.getPage());
				queryMap.put("startRow", (projectsPageQuery.getPage() - 1) * projectsPageQuery.getPageSize());
				queryMap.put("pageSize", projectsPageQuery.getPageSize());
				return projectManager.getOverPros(queryMap);
			}
		} catch (ManagerException | GeneralSecurityException e) {
			logger.error("查询满标项目列表失败, input={}", projectsPageQuery, e);
		}
		return null;
	}

	private boolean checkToken(String token, String methodName) throws ManagerException, GeneralSecurityException {
		String key = sysDictManager.findByGroupNameAndKey("open_api_key", "key").getValue();
		String tokenStr = CryptHelper.aesDecrypt(token, key);
		String platformName = tokenStr.split(":")[0];
		String authorities = sysDictManager.findByGroupNameAndKey("open_api_authority", methodName).getValue();
		for (String authority : authorities.split("\\|")) {
			if (platformName.equals(authority)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ProjectsPageOutPut getInvestingPros(ProjectsPageQuery projectsPageQuery) {
		if (projectsPageQuery.getToken() == null
				|| projectsPageQuery.getDate() == null
				|| projectsPageQuery.getPage() == null
				|| projectsPageQuery.getPageSize() == null) {
			return null;
		}
		try {
			if (checkToken(projectsPageQuery.getToken(), "getInvestingPros")) {
				String date = projectsPageQuery.getDate();
				Date startTime = DateUtils.getDateFromString(date, DateUtils.DATE_FMT_3);
				Date endTime = DateUtils.getEndTime(startTime);
				Map<String, Object> queryMap = Maps.newHashMap();
				queryMap.put("startTime", startTime);
				queryMap.put("endTime", endTime);
				queryMap.put("currentPage", projectsPageQuery.getPage());
				queryMap.put("startRow", (projectsPageQuery.getPage() - 1) * projectsPageQuery.getPageSize());
				queryMap.put("pageSize", projectsPageQuery.getPageSize());
				return projectManager.getInvestingPros(queryMap);
			}
		} catch (ManagerException | GeneralSecurityException e) {
			logger.error("查询投资中项目列表失败, input={}", projectsPageQuery, e);
		}
		return null;
	}

	@Override
	public ProjectsStatusOutPut getProsStatus(ProjectsStatusQuery projectsStatusQuery) {
		if (projectsStatusQuery.getToken() == null
				|| projectsStatusQuery.getIdStr() == null) {
			return null;
		}
		try {
			if (checkToken(projectsStatusQuery.getToken(), "getProsStatus")) {
				String[] arr = projectsStatusQuery.getIdStr().split("\\|");
				if (arr == null || arr.length == 0) {
					return null;
				}
				List<Long> ids = Lists.newArrayList();
				for (String str : arr) {
					ids.add(Long.valueOf(str));
				}
				return projectManager.getProsStatus(ids);
			}
		} catch (ManagerException | GeneralSecurityException e) {
			logger.error("查询投资中项目列表失败, input={}", projectsStatusQuery, e);
		}
		return null;
	}

}
