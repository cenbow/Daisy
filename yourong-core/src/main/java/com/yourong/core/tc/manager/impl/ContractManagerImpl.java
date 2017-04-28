package com.yourong.core.tc.manager.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import rop.thirdparty.com.google.common.collect.Maps;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.Pipeline;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import com.yourong.common.constant.Config;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FileInfoUtil;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.MoneyUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.ic.manager.DebtManager;
import com.yourong.core.ic.manager.ProjectInterestManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.DebtBiz;
import com.yourong.core.ic.model.DebtCollateral;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.DebtPledge;
import com.yourong.core.ic.model.Project;
import com.yourong.core.tc.dao.TransactionMapper;
import com.yourong.core.tc.manager.ContractManager;
import com.yourong.core.tc.manager.ContractSignManager;
import com.yourong.core.tc.manager.PreservationManager;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.ContractSign;
import com.yourong.core.tc.model.Preservation;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.ContractBiz;
import com.yourong.core.tc.model.biz.ContractTransactionInterest;
import com.yourong.core.tc.model.biz.TransferContractBiz;
import com.yourong.core.tc.model.query.TransactionInterestQuery;
import com.yourong.core.uc.manager.MemberInfoManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberInfo;

@Component
public class ContractManagerImpl implements ContractManager {
	@Autowired
	private TransactionMapper transactionMapper;

    @Autowired
    private ProjectManager projectManager;
    
    @Autowired
    private DebtManager debtManager;
    
    
    @Autowired
    private TransactionInterestManager transactionInterestManager;
    
    @Resource
	private ThreadPoolTaskExecutor taskExecutor;
    
    
    @Resource
    private ProjectInterestManager projectInterestManager;
    
    @Autowired
    private MemberInfoManager memberInfoManager;
    
    @Resource
	private VelocityEngine velocityEngine;
    
    @Autowired
    private MemberManager memberManager;
    
    @Autowired
    private PreservationManager preservationManager;
    
	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired
	private ContractSignManager contractSignManager;

    private String prefixPath = "/data/contract/";
    
    private String prefixImgPath = "/data/contract/img/";
	
	private String defaultFont = "simsun";
	
	public void setPrefixPath(String prefixPath) {
		this.prefixPath = prefixPath;
	}

	public void setDefaultFont(String defaultFont) {
		this.defaultFont = defaultFont;
	}
	
    public void setPrefixImgPath(String prefixImgPath) {
		this.prefixImgPath = prefixImgPath;
	}



	private Logger logger = LoggerFactory.getLogger(ContractManagerImpl.class);

	
	@Override
	public BscAttachment saveContract(Long transactionId, String fromSys) throws ManagerException {
		BscAttachment attachment = new BscAttachment();
		attachment.setFileExt(FileInfoUtil.FILE_SUFFIX_PDF);
		try {
			// 通过交易id查询交易信息
			Transaction transaction = transactionMapper.selectByPrimaryKey(transactionId);
			if (transaction == null) {
				logger.info("生成合同异常：交易记录不存在"+ ",transactionId=" + transactionId);
				return null;
			}
			logger.debug("生成合同transactionId={}", transactionId);
			// 根据项目获取合同数据
			Map<String, Object> model = buildModelByProject(transaction);
			String path = Config.prefixPath
					+ FileInfoUtil.getContractFolder(transaction.getMemberId().toString(), DateUtils.getCurrentDate());
			attachment.setFileUrl(FileInfoUtil.getContractFolder(transaction.getMemberId().toString(), DateUtils.getCurrentDate()));
			String realPath = path + transactionId.toString() + FileInfoUtil.FILE_SUFFIX_HTML;
			FileInfoUtil.mkdir(path);
			createHtmlByVelocity(realPath, model.get("contractTemplate").toString(), model);
			generatePdfContract(realPath, path, transaction.getId().toString(), attachment);
			attachment.setFileSize(FileInfoUtil.getFileSize(Config.prefixPath + attachment.getFileUrl() + attachment.getFileName()));
			attachment.setStatus(1);
			String fileDir = Config.prefixPath + attachment.getFileUrl()
					+ attachment.getFileName();
			//将合同文件上传到阿里云OSS  签完保全上传
			/*OSSUtil.uploadContractToOSS(
					OSSUtil.getContractKey(transaction.getMemberId().toString(), attachment.getFileName(), DateUtils.getCurrentDate()), 
					fileDir
					);*/
			//创建数据保全
			Preservation preservation = new Preservation();
			preservation.setContractPath(fileDir);
			preservation.setContractTitle(transactionId + model.get("preservationTitle").toString());
			preservation.setIdentityNumber(model.get("identity").toString());
			preservation.setIdentiferTrueName(model.get("name").toString());
			preservation.setMemberId(transaction.getMemberId());
			preservation.setInvestAmount(transaction.getInvestAmount());
			preservation.setTransactionId(transactionId);
			preservation.setMemberPhone(model.get("mobile").toString());
			preservation.setFromSys(fromSys);
			Preservation retPre = preservationManager.createPreservation(preservation);
			if(retPre != null && retPre.getSuccessFlag() == 1) {
				attachment.setPreservationId(retPre.getPreservationId());
			}
		} catch (Exception e) {
			logger.error("生成交易合同出错, transactionId=" + transactionId, e);
			throw new ManagerException("生成交易合同出错", e);
		}
		return attachment;
	}
	
	@Override
	public void generatePdfContract(String htmlPath, String path, String fileName, BscAttachment attachment) {
		try {
			FileInputStream is = new FileInputStream(new File(htmlPath));
			fileName = fileName + FileInfoUtil.FILE_SUFFIX_PDF;
			String realPath = path;
			//attachment.setFileUrl(realPath);
			attachment.setFileName(fileName);
			FileInfoUtil.mkdir(realPath);
			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(realPath
					+ fileName));
			document.open();
			MyFontsProvider fontProvider = new MyFontsProvider();
			fontProvider.addFontSubstitute("lowagie", "garamond");
			fontProvider.setUseUnicode(true);
			// 使用我们的字体提供器，并将其设置为unicode字体样式
			CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);
			HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
			htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
			CSSResolver cssResolver = XMLWorkerHelper.getInstance().getDefaultCssResolver(true);
			Pipeline<?> pipeline = new CssResolverPipeline(cssResolver, new HtmlPipeline(
					htmlContext, new PdfWriterPipeline(document, writer)));
			XMLWorker worker = new XMLWorker(pipeline, true);
			XMLParser p = new XMLParser(worker);
			p.parse(new InputStreamReader(is, "UTF-8"));
			document.close();
		} catch (Exception e) {
			logger.error("生成pdf合同出错,fileName="+fileName, e);
		}
	}

	
	@SuppressWarnings("unchecked")
	private Map<String, Object> buildModel(Transaction transaction) throws ManagerException {
		try {
			Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
			// 获取债权信息，包括债权本息列表
			DebtBiz debtBiz = debtManager.getFullDebtInfoById(project.getDebtId());
			Member member = memberManager.selectByPrimaryKey(transaction.getMemberId());

			ContractBiz contractBiz = new ContractBiz();
			contractBiz.setSealUrl(Config.getPrefixPath());
			contractBiz.setInvestDate(DateUtils.getStrFromDate(transaction.getTransactionTime(),
					DateUtils.DATE_FMT_3));
			
			BigDecimal annualizedRate = transaction.getAnnualizedRate();
			if(transaction.getExtraAnnualizedRate()!=null) {
				contractBiz.setAnnualizedRate(annualizedRate.toPlainString());
				contractBiz.setExtraAnnualizedRate(transaction.getExtraAnnualizedRate());
				contractBiz.setTotalAnnualizedRate(annualizedRate.add(transaction.getExtraAnnualizedRate()).setScale(2, BigDecimal.ROUND_HALF_UP));
			}else{
				contractBiz.setAnnualizedRate(annualizedRate.toPlainString());
				contractBiz.setTotalAnnualizedRate(annualizedRate);
			}
			
			if(debtBiz.getDebtCollateral()!=null) {
				DebtCollateral debtCollateral = debtBiz.getDebtCollateral();
				Map<String, Object> ruleMap = JSON.parseObject(
						debtCollateral.getCollateralDetails(),
						new TypeReference<Map<String, Object>>() {
						});
				if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_CAR.getCode())) {
					if(debtBiz.getInstalment()==1){
						contractBiz.setCollateralDetails(ruleMap.get("base_info").toString());
					}else{
						contractBiz.setCollateralDetails(ruleMap.get("car_cx").toString());
					}
				}
				if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_HOUSE.getCode())) {
					contractBiz.setCollateralDetails(ruleMap.get("house_fwzl").toString()+ruleMap.get("house_fwlx").toString()+ruleMap.get("house_jzmj").toString());
				}
				if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_HOUSERECORD.getCode())) {
					contractBiz.setCollateralDetails(ruleMap.get("houseRecord_info").toString());
				}
				if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_CARPAYIN.getCode())) {
					contractBiz.setCollateralDetails(ruleMap.get("carPayIn_info").toString());
				}
				if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_CARBUSINESS.getCode())) {
					contractBiz.setCollateralDetails(ruleMap.get("base_info").toString());
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
					if(debtBiz.getInstalment()==1){
						contractBiz.setCollateralDetails(ruleMap.get("base_info").toString());
					}else{
						contractBiz.setCollateralDetails(ruleMap.get("car_cx").toString());
					}
				}
				if(pledgeType.equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_NEWCAR.getCode()) || pledgeType.equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_EQUITY.getCode())) {
					contractBiz.setCollateralDetails(ruleMap.get("car_ms").toString());
				}
				if(pledgeType.equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_HOUSERECORD.getCode())) {
					contractBiz.setCollateralDetails(ruleMap.get("houseRecord_info").toString());
				}
			}
			contractBiz.setContractId(transaction.getId().toString());
			contractBiz.setOriginalDebtNumber(debtBiz.getOriginalDebtNumber());

			//格式化debtAmount
			contractBiz.setDebtAmount(debtBiz.getAmount().toPlainString());
			contractBiz.setDebtAnnualizedRate(debtBiz.getAnnualizedRate() + "");
			contractBiz.setDebtEndTime(DateUtils.getStrFromDate(debtBiz.getEndDate(),
					DateUtils.DATE_FMT_4));
			contractBiz.setDebtStartTime(DateUtils.getStrFromDate(debtBiz.getStartDate(),
					DateUtils.DATE_FMT_4));
			contractBiz.setIdentity(member.getIdentityNumber());
			contractBiz.setMobile(member.getMobile().toString());
			contractBiz.setName(member.getTrueName());
			Double investAmount = transaction.getInvestAmount().doubleValue();
			contractBiz.setPrice(investAmount);
			contractBiz.setPriceCap(MoneyUtil.toChinese(investAmount.toString()));
			contractBiz.setProfitTypeCode(project.getProfitType());
			contractBiz.setDebtTypeCode(debtBiz.getDebtType());
			contractBiz.setGuarantyType(debtBiz.getGuarantyType());
			contractBiz.setInstalment(debtBiz.getInstalment());
			TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
			transactionInterestQuery.setTransactionId(transaction.getId());
			List<TransactionInterest> transactionInterestList = transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
			List<ContractTransactionInterest> contractTransactionInterestList = new ArrayList<ContractTransactionInterest>();
			if (transactionInterestList != null && transactionInterestList.size() > 0) {
				Date startTime = transactionInterestList.get(0).getStartDate();
				int remainDays = DateUtils.getIntervalDays(startTime, debtBiz.getEndDate()) + 1;
				contractBiz.setRemainDays(remainDays);
				BigDecimal interestTotal = BigDecimal.ZERO;
				for (TransactionInterest transactionInterest : transactionInterestList) {
					ContractTransactionInterest contractTransactionInterest = new ContractTransactionInterest();
					contractTransactionInterest.setPayTime(DateUtils.getStrFromDate(
							transactionInterest.getEndDate(), DateUtils.DATE_FMT_4));
					contractTransactionInterest.setPayableInterestAndPrincipal(transactionInterest
							.getPayableInterest().add(transactionInterest.getPayablePrincipal()).setScale(2, BigDecimal.ROUND_HALF_UP));
					contractTransactionInterestList.add(contractTransactionInterest);
					interestTotal = interestTotal.add(transactionInterest.getPayableInterest());
				}

					// 利息+本金
					double userDebtAmount = new BigDecimal(investAmount).add(interestTotal)
							.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					contractBiz.setUserDebtAmount(userDebtAmount);
					contractBiz.setUserInterestTotal(interestTotal.doubleValue());
					contractBiz.setUserInvestAmount(investAmount);
			}

			// contractBiz.setTransactionInterestList(transactionInterestList);
			// build原始债权人信息
			Member originalCreditorPersonal = debtBiz.getLenderMember();
			if (originalCreditorPersonal != null) {
				contractBiz.setOriginalCreditorIdentityNumber(originalCreditorPersonal
						.getIdentityNumber());
				contractBiz.setOriginalCreditorName(originalCreditorPersonal.getTrueName());
				contractBiz.setOriginalCreditorPhone(originalCreditorPersonal.getMobile()+"");
			}
			MemberInfo originalCreditorPersonalMemberInfo = memberInfoManager.getMemberInfoByMemberId(originalCreditorPersonal.getId());
			if (originalCreditorPersonalMemberInfo != null) {
				contractBiz.setOriginalCreditorAddress(originalCreditorPersonalMemberInfo.getAddress());
			}
			if(2 == debtBiz.getLenderType()){
				//现在法人企业一对一，当法人企业1对多时需要修改
				contractBiz.setLenderType(debtBiz.getLenderType());
				contractBiz.setCompanyName(debtBiz.getLenderMemberBaseBiz().getEnterprises().get(0).getFullName());
				contractBiz.setLegalName(debtBiz.getLenderMemberBaseBiz().getMember().getTrueName());
				contractBiz.setCompanyTelephone(debtBiz.getLenderMemberBaseBiz().getEnterprises().get(0).getTelephone());
			}
			Map<String, Object> model = null;
			model = BeanCopyUtil.map(contractBiz, HashMap.class);
			model.put("contractTransactionInterestList", contractTransactionInterestList);
			model.put("contractTemplate", "contract/contract-preview.vm");
			// 保全title
			model.put("preservationTitle", "债权收益权转让协议");
			return model;
		} catch (ManagerException e) {
			logger.error("build交易合同出错，transactionId=" + transaction.getId(), e);
			throw new ManagerException("build交易合同出错", e);
		}
	}
	
	@Override
	public void createHtmlByVelocity(String htmlPath, String vmTemPath, Map<String, Object> params) {

		try {
			FileOutputStream fos = new FileOutputStream(htmlPath);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
			Template velocity_template = velocityEngine.getTemplate(vmTemPath, "UTF-8");

			VelocityContext context = new VelocityContext();
			context.put("contract", params);
			velocity_template.merge(context, writer);
			writer.close();

		} catch (Exception e) {
			logger.error("文件路径失败！", e);
		}
	}
	
	class MyFontsProvider extends XMLWorkerFontProvider {
		public MyFontsProvider() {
			super(null, null);
		}

		@Override
		public Font getFont(final String fontname, String encoding, float size, final int style) {

			String fntname = fontname;
			if (fntname == null) {
				fntname = defaultFont;
			}
			return super.getFont(fntname, encoding, size, style);
		}
	}

	@Override
	public ContractBiz getPreviewContract(Long memberId, Long projectId,
			BigDecimal investAmount, BigDecimal annualizedRate, Date orderTime) throws ManagerException {
		try {
			Project project = projectManager.selectByPrimaryKey(projectId);
			// 获取债权信息，包括债权本息列表
			DebtBiz debtBiz = debtManager.getFullDebtInfoById(project.getDebtId());
			Member member = memberManager.selectByPrimaryKey(memberId);

			ContractBiz contractBiz = new ContractBiz();
			contractBiz.setSealUrl("/data/contract/seal.gif");
			contractBiz.setInvestDate(DateUtils.getStrFromDate(orderTime,DateUtils.DATE_FMT_4));
			contractBiz.setAnnualizedRate(annualizedRate.toPlainString());
			contractBiz.setTotalAnnualizedRate(annualizedRate);
			if(debtBiz.getDebtCollateral()!=null) {
				DebtCollateral debtCollateral = debtBiz.getDebtCollateral();
				Map<String, Object> ruleMap = JSON.parseObject(
						debtCollateral.getCollateralDetails(),
						new TypeReference<Map<String, Object>>() {
						});
				if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_CAR.getCode())) {
					if(debtBiz.getInstalment()==1){
						contractBiz.setCollateralDetails(ruleMap.get("base_info").toString());
					}else{
						contractBiz.setCollateralDetails(ruleMap.get("car_cx").toString());
					}
				}
				if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_HOUSE.getCode())) {
					contractBiz.setCollateralDetails(ruleMap.get("house_fwzl").toString()+ruleMap.get("house_fwlx").toString()+ruleMap.get("house_jzmj").toString());
				}
				if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_HOUSERECORD.getCode())) {
					contractBiz.setCollateralDetails(ruleMap.get("houseRecord_info").toString());
				}
				if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_CARPAYIN.getCode())) {
					contractBiz.setCollateralDetails(ruleMap.get("carPayIn_info").toString());
				}
				if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_CARBUSINESS.getCode())) {
					contractBiz.setCollateralDetails(ruleMap.get("base_info").toString());
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
					if(debtBiz.getInstalment()==1){
						contractBiz.setCollateralDetails(ruleMap.get("base_info").toString());
					}else{
						contractBiz.setCollateralDetails(ruleMap.get("car_cx").toString());
					}
				}
				if(pledgeType.equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_NEWCAR.getCode()) || pledgeType.equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_EQUITY.getCode())) {
					contractBiz.setCollateralDetails(ruleMap.get("car_ms").toString());
				}
				if(pledgeType.equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_HOUSERECORD.getCode())) {
					contractBiz.setCollateralDetails(ruleMap.get("houseRecord_info").toString());
				}
			}
//			contractBiz.setCollateralDetails(StringUtil.splitAndFilterString(
//					debtBiz.getDebtCollateral().getCollateralDetails(), 100));
			contractBiz.setContractId(debtBiz.getOriginalDebtNumber());
			contractBiz.setOriginalDebtNumber(debtBiz.getOriginalDebtNumber());
			//格式化debtAmount
			contractBiz.setDebtAmount(debtBiz.getAmount().toPlainString());
			contractBiz.setDebtAnnualizedRate(debtBiz.getAnnualizedRate() + "");
			contractBiz.setDebtEndTime(DateUtils.getStrFromDate(debtBiz.getEndDate(),
					DateUtils.DATE_FMT_4));
			contractBiz.setDebtStartTime(DateUtils.getStrFromDate(debtBiz.getStartDate(),
					DateUtils.DATE_FMT_4));
			contractBiz.setIdentity(member.getIdentityNumber());
			contractBiz.setMobile(member.getMobile().toString());
			contractBiz.setName(member.getTrueName());
			contractBiz.setPrice(investAmount.doubleValue());
			contractBiz.setPriceCap(MoneyUtil.toChinese(investAmount.toString()));
			contractBiz.setProfitTypeCode(project.getProfitType());
			contractBiz.setDebtTypeCode(debtBiz.getDebtType());
			contractBiz.setGuarantyType(debtBiz.getGuarantyType());
			contractBiz.setInstalment(debtBiz.getInstalment());
			List<ContractTransactionInterest> contractTransactionInterestList = new ArrayList<ContractTransactionInterest>();
			List<DebtInterest> debtInterests = debtManager.getFullDebtInfoById(project.getDebtId()).getDebtInterests();
			if(Collections3.isNotEmpty(debtInterests)) {
				BigDecimal expectAmount = BigDecimal.ZERO;
				int totalDays = 0;
				int period = 0;
				int totalPeriod = debtInterests.size();
				Date startInterestDate = DateUtils.addDate(DateUtils.formatDate(orderTime,  DateUtils.DATE_FMT_3), project.getInterestFrom());
				for (DebtInterest debtInterest : debtInterests) {
					//计算总的交易本息期数
					if(startInterestDate.after(debtInterest.getEndDate())){
						totalPeriod = debtInterests.size() - 1; 
					}
					if(!startInterestDate.after(debtInterest.getEndDate())){
						ContractTransactionInterest contractTransactionInterest = new ContractTransactionInterest();
						int days = 0;
						if(DateUtils.isDateBetween(startInterestDate, debtInterest.getStartDate(), debtInterest.getEndDate())) {
							days = DateUtils.daysOfTwo(startInterestDate, debtInterest.getEndDate()) + 1;
						} else {
							days = DateUtils.daysOfTwo(debtInterest.getStartDate(), debtInterest.getEndDate()) + 1;
						}
						contractTransactionInterest.setPayTime(DateUtils.getStrFromDate(debtInterest.getEndDate(), DateUtils.DATE_FMT_4));
						// 后期需要根据收益类型不同而采用不同的计算方式，目前只有按日计息，按月付息
						if(DebtEnum.RETURN_TYPE_DAY.getCode().equals(project.getProfitType())||DebtEnum.RETURN_TYPE_ONCE.getCode().equals(project.getProfitType())) {
							// 单位利息
							BigDecimal unitInterest = FormulaUtil.getUnitInterest(project.getProfitType(),
											investAmount, annualizedRate);
							BigDecimal value = (unitInterest.multiply(new BigDecimal(days))).setScale(2, BigDecimal.ROUND_HALF_UP);
							expectAmount = expectAmount.add(value);
							if(DateUtils.isDateBetween(project.getEndDate(), debtInterest.getStartDate(), debtInterest.getEndDate())) {
								contractTransactionInterest.setPayableInterestAndPrincipal(investAmount.add(value));
							} else {
								contractTransactionInterest.setPayableInterestAndPrincipal(value);
							}
						} else if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode()// 还款方式：等本等息 
								.equals(project.getProfitType())) {
							BigDecimal interest = BigDecimal.ZERO;
							interest = FormulaUtil.getTransactionInterest(project.getProfitType(),
									investAmount,
									annualizedRate,
									period, 
									debtInterest.getStartDate(),
									startInterestDate,
									debtInterest.getEndDate());//应付利息
							BigDecimal principal = BigDecimal.ZERO;
							principal = FormulaUtil.getPrincipal(project.getProfitType(), investAmount, totalPeriod,period);//单位本金=应付本金
							principal = principal.add(interest);
							contractTransactionInterest.setPayableInterestAndPrincipal(principal);
							expectAmount = expectAmount.add(interest);
						}
						contractTransactionInterestList.add(contractTransactionInterest);
						totalDays += days;
						period = period+1;
					}
				}
			
			
			double userDebtAmount = investAmount.add(expectAmount)
					.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			contractBiz.setUserDebtAmount(userDebtAmount);
			contractBiz.setUserInterestTotal(expectAmount.doubleValue());
			contractBiz.setUserInvestAmount(investAmount.doubleValue());
			contractBiz.setRemainDays(totalDays);
			
			 contractBiz.setTransactionInterestList(contractTransactionInterestList);
			// build原始债权人信息
			Member originalCreditorPersonal = debtBiz.getLenderMember();
			MemberInfo originalCreditorPersonalMemberInfo = memberInfoManager.getMemberInfoByMemberId(originalCreditorPersonal.getId());
			if (originalCreditorPersonal != null) {
				contractBiz.setOriginalCreditorIdentityNumber(StringUtil.maskIdentityNumber(originalCreditorPersonal
						.getIdentityNumber()));
//				contractBiz.setOriginalCreditorName(StringUtil.maskTrueName(originalCreditorPersonal.getTrueName()));
				contractBiz.setOriginalCreditorName("***(投资后可见)");
				contractBiz.setOriginalCreditorPhone(StringUtil.maskMobile(originalCreditorPersonal.getMobile()));
			}
			if(originalCreditorPersonalMemberInfo!=null) {
				contractBiz.setOriginalCreditorAddress(originalCreditorPersonalMemberInfo.getAddress());
			}
			}
			if(2 == debtBiz.getLenderType()){
				//现在法人企业一对一，当法人企业1对多时需要修改
				contractBiz.setLenderType(debtBiz.getLenderType());
				contractBiz.setCompanyName("****(投资后可见)");
				contractBiz.setLegalName(StringUtil.maskTrueName(debtBiz.getLenderMemberBaseBiz().getMember().getTrueName()));
				String telephone = debtBiz.getLenderMemberBaseBiz().getEnterprises().get(0).getTelephone();
				contractBiz.setCompanyTelephone(this.maskTelephone(telephone));
			}
			
			return contractBiz;
		} catch (ManagerException e) {
			logger.error("获取预览合同信息，memberId=" + memberId + "，projectId=" + projectId, e);
		}
		return null;
	}

	
	private String maskTelephone(String telephone) throws ManagerException {
		
		if(-1 == telephone.indexOf("-")){
			return StringUtil.maskMobileCanNull(telephone);
		}else{
			return StringUtil.maskTelephoneCanNull(telephone);
		}
		
	}
	
	@Override
	public ContractBiz getViewContract(Long transactionId)
			throws ManagerException {
		try {
			Transaction transaction = transactionMapper.selectByPrimaryKey(transactionId);
			if(transaction!=null) {
				Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
				// 获取债权信息，包括债权本息列表
				DebtBiz debtBiz = debtManager.getFullDebtInfoById(project.getDebtId());
				Member member = memberManager.selectByPrimaryKey(transaction.getMemberId());

				ContractBiz contractBiz = new ContractBiz();
				contractBiz.setTransactionId(transactionId);
				contractBiz.setSealUrl(prefixImgPath);
				contractBiz.setInvestDate(DateUtils.getStrFromDate(transaction.getTransactionTime(),
						DateUtils.DATE_FMT_4));
				BigDecimal annualizedRate = transaction.getAnnualizedRate();
				if(transaction.getExtraAnnualizedRate()!=null) {
					contractBiz.setAnnualizedRate(annualizedRate.toPlainString());
					contractBiz.setExtraAnnualizedRate(transaction.getExtraAnnualizedRate());
					contractBiz.setTotalAnnualizedRate(annualizedRate.add(transaction.getExtraAnnualizedRate()).setScale(2, BigDecimal.ROUND_HALF_UP));
				}else{
					contractBiz.setAnnualizedRate(annualizedRate.toPlainString());
					contractBiz.setTotalAnnualizedRate(annualizedRate);
				}
				
				if(debtBiz.getDebtCollateral()!=null) {
					DebtCollateral debtCollateral = debtBiz.getDebtCollateral();
					Map<String, Object> ruleMap = JSON.parseObject(
							debtCollateral.getCollateralDetails(),
							new TypeReference<Map<String, Object>>() {
							});
					if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_CAR.getCode())) {
						if(debtBiz.getInstalment()==1){
							contractBiz.setCollateralDetails(ruleMap.get("base_info").toString());
						}else{
							contractBiz.setCollateralDetails(ruleMap.get("car_cx").toString());
						}
					}
					if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_HOUSE.getCode())) {
						contractBiz.setCollateralDetails(ruleMap.get("house_fwzl").toString()+ruleMap.get("house_fwlx").toString()+ruleMap.get("house_jzmj").toString());
					}
					if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_HOUSERECORD.getCode())) {
						contractBiz.setCollateralDetails(ruleMap.get("houseRecord_info").toString());
					}
					if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_CARPAYIN.getCode())) {
						contractBiz.setCollateralDetails(ruleMap.get("carPayIn_info").toString());
					}
					if(debtCollateral.getCollateralType().equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_CARBUSINESS.getCode())) {
						contractBiz.setCollateralDetails(ruleMap.get("base_info").toString());
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
						if(debtBiz.getInstalment()==1){
							contractBiz.setCollateralDetails(ruleMap.get("base_info").toString());
						}else{
							contractBiz.setCollateralDetails(ruleMap.get("car_cx").toString());
						}
					}
					if(pledgeType.equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_NEWCAR.getCode()) || pledgeType.equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_EQUITY.getCode())) {
						contractBiz.setCollateralDetails(ruleMap.get("car_ms").toString());
					}
					if(pledgeType.equals(DebtEnum.DEBT_PLEDGE_COLLATERAL_TYPE_HOUSERECORD.getCode())) {
						contractBiz.setCollateralDetails(ruleMap.get("houseRecord_info").toString());
					}
				}
//				contractBiz.setCollateralDetails(StringUtil.splitAndFilterString(
//						debtBiz.getDebtCollateral().getCollateralDetails(), 100));
				contractBiz.setContractId(transaction.getId().toString());
				contractBiz.setOriginalDebtNumber(debtBiz.getOriginalDebtNumber());

				//格式化debtAmount
				contractBiz.setDebtAmount(debtBiz.getAmount().toPlainString());
				contractBiz.setDebtAnnualizedRate(debtBiz.getAnnualizedRate() + "");
				contractBiz.setDebtEndTime(DateUtils.getStrFromDate(debtBiz.getEndDate(),
						DateUtils.DATE_FMT_4));
				contractBiz.setDebtStartTime(DateUtils.getStrFromDate(debtBiz.getStartDate(),
						DateUtils.DATE_FMT_4));
				contractBiz.setIdentity(member.getIdentityNumber());
				contractBiz.setMobile(member.getMobile().toString());
				contractBiz.setName(member.getTrueName());
				Double investAmount = transaction.getInvestAmount().doubleValue();
				contractBiz.setPrice(investAmount);
				contractBiz.setPriceCap(MoneyUtil.toChinese(investAmount.toString()));
				contractBiz.setProfitTypeCode(project.getProfitType());
				contractBiz.setDebtTypeCode(debtBiz.getDebtType());
				contractBiz.setGuarantyType(debtBiz.getGuarantyType());
				contractBiz.setInstalment(debtBiz.getInstalment());
				TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
				transactionInterestQuery.setTransactionId(transaction.getId());
				List<TransactionInterest> transactionInterestList = transactionInterestManager.selectTransactionInterestsByQueryParams(transactionInterestQuery);
				List<ContractTransactionInterest> contractTransactionInterestList = new ArrayList<ContractTransactionInterest>();
				if (transactionInterestList != null && transactionInterestList.size() > 0) {
					Date startTime = transactionInterestList.get(0).getStartDate();
					int remainDays = DateUtils.getIntervalDays(startTime, debtBiz.getEndDate()) + 1;
					contractBiz.setRemainDays(remainDays);
					BigDecimal interestTotal = BigDecimal.ZERO;
					for (TransactionInterest transactionInterest : transactionInterestList) {
						ContractTransactionInterest contractTransactionInterest = new ContractTransactionInterest();
						contractTransactionInterest.setPayTime(DateUtils.getStrFromDate(
								transactionInterest.getEndDate(), DateUtils.DATE_FMT_4));
						contractTransactionInterest.setPayableInterestAndPrincipal(transactionInterest
								.getPayableInterest().add(transactionInterest.getPayablePrincipal()).setScale(2, BigDecimal.ROUND_HALF_UP));
						contractTransactionInterestList.add(contractTransactionInterest);
						interestTotal = interestTotal.add(transactionInterest.getPayableInterest());
					}

					// 利息+本金
					double userDebtAmount = new BigDecimal(investAmount).add(interestTotal)
							.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					contractBiz.setUserDebtAmount(userDebtAmount);
					contractBiz.setUserInterestTotal(interestTotal.doubleValue());
					contractBiz.setUserInvestAmount(investAmount);
				}

				// build原始债权人信息
				Member originalCreditorPersonal = debtBiz.getLenderMember();
				if (originalCreditorPersonal != null) {
					contractBiz.setOriginalCreditorIdentityNumber(originalCreditorPersonal
							.getIdentityNumber());
					contractBiz.setOriginalCreditorName(originalCreditorPersonal.getTrueName());
					contractBiz.setOriginalCreditorPhone(originalCreditorPersonal.getMobile()+"");
				}
				MemberInfo originalCreditorPersonalMemberInfo = memberInfoManager.getMemberInfoByMemberId(originalCreditorPersonal.getId());
				if (originalCreditorPersonalMemberInfo != null) {
					contractBiz.setOriginalCreditorAddress(originalCreditorPersonalMemberInfo.getAddress());
				}
				contractBiz.setTransactionInterestList(contractTransactionInterestList);
				if(2 == debtBiz.getLenderType()){
					//现在法人企业一对一，当法人企业1对多时需要修改
					contractBiz.setLenderType(debtBiz.getLenderType());
					contractBiz.setCompanyName(debtBiz.getLenderMemberBaseBiz().getEnterprises().get(0).getFullName());
					contractBiz.setLegalName(debtBiz.getLenderMemberBaseBiz().getMember().getTrueName());
					contractBiz.setCompanyTelephone(debtBiz.getLenderMemberBaseBiz().getEnterprises().get(0).getTelephone());
				}
				
				Map<String,Object> map = Maps.newHashMap();
				map.put("transactionId", transaction.getId());
				 List<ContractSign>  contractSignList = contractSignManager.selectListByPrimaryKey(map);
				 Integer thirdIsSign = 0;
				 Integer firstIsSign = 0;
				 Integer secondIsSign = 0;
				 for(ContractSign con :contractSignList){
					 if(con.getType()==StatusEnum.CONTRACT_PARTY_FIRST.getStatus()){
						 if(con.getStatus()==StatusEnum.CONTRACT_SIGN_STATUS_SUCCESS.getStatus()){
							 firstIsSign=1;
						 }
					 }
					if(con.getType()==StatusEnum.CONTRACT_PARTY_SECOND.getStatus()){
						if(con.getStatus()==StatusEnum.CONTRACT_SIGN_STATUS_SUCCESS.getStatus()){
							secondIsSign=1;
						 }
					 }
					if(con.getType()==StatusEnum.CONTRACT_PARTY_THIRD.getStatus()){
						if(con.getStatus()==StatusEnum.CONTRACT_SIGN_STATUS_SUCCESS.getStatus()){
							thirdIsSign=1;
						 }
					}
				 }
				 contractBiz.setFirstIsSign(firstIsSign);
				 contractBiz.setSecondIsSign(secondIsSign);
				 contractBiz.setThirdIsSign(thirdIsSign);
				 
				 contractBiz.setSignStatus(transaction.getSignStatus());
				
				return contractBiz;
			}
			
		} catch(Exception e) {
			logger.error("获取查看合同biz发生异常,transactionId=" + transactionId, e);
		}
		return null;
	}

	/**
	 * 
	 * @Description:传入交易，判断项目获取对应的合同数据,以及合同模板
	 * @param transaction
	 * @return
	 * @author: wangyanji
	 * @time:2016年3月11日 下午3:44:10
	 */
	private Map<String, Object> buildModelByProject(Transaction transaction) throws ManagerException {
		Project project = projectManager.selectByPrimaryKey(transaction.getProjectId());
		if(transaction.getProjectCategory()==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
			if (ProjectEnum.PROJECT_TYPE_DIRECT.getType() == project.getInvestType()) {
				// 转让直投项目
				return buildModelFromDirectTransfer(transaction);
			} else {
				// 转让债权项目
				return buildModelFromDebtTransfer(transaction);
			}
		}else{
			if (ProjectEnum.PROJECT_TYPE_DIRECT.getType() == project.getInvestType()) {
				// 直投项目
				return buildModelFromDirect(transaction);
			} else {
				// 债权项目
				return buildModel(transaction);
			}
		}
	
	}

	/**
	 * 
	 * @Description:直投项目生成合同数据
	 * @param transaction
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年3月11日 下午4:05:03
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> buildModelFromDirect(Transaction transaction) throws ManagerException {
		ResultDO<ContractBiz> rDO = transactionManager.p2pViewContract(transaction.getOrderId(), transaction.getMemberId());
		if (rDO.isSuccess()) {
			ContractBiz biz = rDO.getResult();
			Map<String, Object> model = BeanCopyUtil.map(biz, HashMap.class);
			// 模板
			model.put("contractTemplate", "contract/ztContract-preview.vm");
			// 章
			model.put("sealUrl", Config.getPrefixPath());
			// 保全title
			model.put("preservationTitle", "借款协议");
			return model;
		} else {
			String errorMsg = rDO.getResultCode().getMsg();
			logger.error("创建直投项目合同失败：{}", errorMsg);
			throw new ManagerException(errorMsg);
		}
	}
	
	/**
	 * 
	 * @Description:转让直投项目生成合同数据
	 * @param transaction
	 * @return
	 * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年9月27日 下午4:05:03
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> buildModelFromDirectTransfer(Transaction transaction) throws ManagerException {
		ResultDO<TransferContractBiz> rDO = transactionManager.viewZtTransferContract(transaction.getOrderId(), transaction.getMemberId());
		if (rDO.isSuccess()) {
			TransferContractBiz biz = rDO.getResult();
			Map<String, Object> model = BeanCopyUtil.map(biz, HashMap.class);
			// 模板
			model.put("contractTemplate", "contract/ztTransfer-preview.vm");
			// 章
			model.put("sealUrl", Config.getPrefixPath());
			// 保全title
			model.put("preservationTitle", "债权转让协议");
			model.put("identity",biz.getSecondIdentity());
			model.put("mobile",biz.getSecondMobile());
			model.put("name",biz.getSecondName());
			return model;
		} else {
			String errorMsg = rDO.getResultCode().getMsg();
			logger.error("创建转让直投项目合同失败：{}", errorMsg);
			throw new ManagerException(errorMsg);
		}
	}
	
	/**
	 * 
	 * @Description:转让债权项目生成合同数据
	 * @param transaction
	 * @return
	 * @throws ManagerException
	 * @author: zhanghao
	 * @time:2016年9月27日 下午4:05:03
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> buildModelFromDebtTransfer(Transaction transaction) throws ManagerException {
		ResultDO<TransferContractBiz> rDO = transactionManager.transferContract(transaction.getOrderId(), transaction.getMemberId());
		if (rDO.isSuccess()) {
			TransferContractBiz biz = rDO.getResult();
			Map<String, Object> model = BeanCopyUtil.map(biz, HashMap.class);
			// 模板
			model.put("contractTemplate", "contract/transferContract-preview.vm");
			// 章
			model.put("sealUrl", Config.getPrefixPath());
			// 保全title
			model.put("preservationTitle", "债权收益权再转让协议");
			model.put("preservationTitle", "债权转让协议");
			model.put("identity",biz.getSecondIdentity());
			model.put("mobile",biz.getSecondMobile());
			model.put("name",biz.getSecondName());
			return model;
		} else {
			String errorMsg = rDO.getResultCode().getMsg();
			logger.error("创建转让债权项目合同失败：{}", errorMsg);
			throw new ManagerException(errorMsg);
		}
	}

}