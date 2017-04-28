package com.yourong.common.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.mapu.themis.ThemisClient;
import org.mapu.themis.api.common.PersonalIdentifer;
import org.mapu.themis.api.common.PreservationType;
import org.mapu.themis.api.request.cer.CertificateLinkGetRequest;
import org.mapu.themis.api.request.contract.ContractFilePreservationCreateRequest;
import org.mapu.themis.api.response.cer.CertificateLinkGetResponse;
import org.mapu.themis.api.response.preservation.PreservationCreateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourong.common.constant.Config;

public class PreservationUtil {
	/**
	 * 日志对象
	 */
	private static Logger logger = LoggerFactory.getLogger(PreservationUtil.class);
	
	/**
	 * 获取SDK对象
	 * @return ThemisClient
	 */
	private ThemisClient getThemisClient() {
		return new ThemisClient(Config.preservationServicesUrl, Config.preservationAppKey, Config.preservationAppSecret);
	}
	
	/**
	 * 创建保全
	 * @param ContractFilePreservationCreateRequest.Builder
	 * @return PreservationCreateResponse
	 */
	private PreservationCreateResponse creatPreservation(ContractFilePreservationCreateRequest.Builder b) {
		//创建保全
		return this.getThemisClient().createPreservation(b.build());
	}
	
	/**
	 * 创建合同保全
	 * @param contractPath 合同文件路径（绝对路径+文件名+后缀名，如D:\\交易合同.pdf）
	 * @param contractTitle 合同标题
	 * @param identityNumber 保全人身份证号
	 * @param identiferTrueName 保全人姓名
	 * @param memberId 保全人ID
	 * @param investAmount 投资金额
	 * @param contractId 合同编号
	 * @param memberEmail 保全人邮箱（选填）
	 * @param memberPhone 保全人手机号
	 * @return PreservationCreateResponse
	 */
	public Map<String, Object> createContractPreservation(String contractPath,
			String contractTitle, String identityNumber,
			String identiferTrueName, String memberId, BigDecimal investAmount,
			String contractId, String memberEmail, String memberPhone) {		
		try {
			Map<String, Object> retMap = new HashMap<String, Object>();
			ContractFilePreservationCreateRequest.Builder contractBuilder = new ContractFilePreservationCreateRequest.Builder();
			contractBuilder.withFile(contractPath);
			contractBuilder.withPreservationTitle(contractTitle);
			contractBuilder.withPreservationType(PreservationType.DIGITAL_CONTRACT);
			contractBuilder.withIdentifer(new PersonalIdentifer(identityNumber, identiferTrueName));
			contractBuilder.withSourceRegistryId(memberId);
			contractBuilder.withContractAmount(investAmount.doubleValue());
			contractBuilder.withContractNumber(contractId);
			if(StringUtil.isNotBlank(memberEmail))
				contractBuilder.withUserEmail(memberEmail);
			contractBuilder.withMobilePhone(memberPhone);
			contractBuilder.withIsNeedSign(true);
			PreservationCreateResponse retResponse = this.creatPreservation(contractBuilder);
			if(retResponse.isSuccess()) {
				retMap.put("preservationId", retResponse.getPreservationId());
				retMap.put("preservationTime", new Date(retResponse.getPreservationTime()));
				retMap.put("isSuccess", 1);
			} else {
				logger.error("创建保全失败!, contractId = " + contractId + ", errorCode: " + retResponse.getError().getCode() + ", errorMessage: " + retResponse.getError().getMessage());
				retMap.put("isSuccess", -1);
			}
			return retMap;
		} catch(Exception e) {
			logger.error("创建保全失败!, contractId = " + contractId, e);
		}
		return null;
	}
	
	/**
	 * 获取保全链接
	 * @param preservationId
	 * @return map<String, Object>
	 */
	public Map<String, Object> getPreservationLink(Long preservationId) {
		try {
			Map<String, Object> retMap = new HashMap<String, Object>();
			//证书链接
			CertificateLinkGetRequest certificateLinkGetRequest = new CertificateLinkGetRequest();
			certificateLinkGetRequest.setPreservationId(preservationId);
			CertificateLinkGetResponse certificateLinkGetResponse = this.getThemisClient().getCertificateLink(certificateLinkGetRequest);
			if(certificateLinkGetResponse.isSuccess()) {
				retMap.put("link", certificateLinkGetResponse.getLink());
				retMap.put("linkExpireTime", new Date(certificateLinkGetResponse.getLinkExpireTime()));
				retMap.put("isSuccess", 1);
			} else {
				logger.error("未获取到保全链接!, preservationId = " + preservationId + ", errorCode: " + certificateLinkGetResponse.getError().getCode() + ", errorMessage: " + certificateLinkGetResponse.getError().getMessage());
				retMap.put("isSuccess", -1);
			}
			return retMap;
		} catch(Exception e) {
			logger.error("获取保全链接出错!, preservationId = " + preservationId, e);
		}
		return null;
	}
	
//	public static void main(String[] args) {
//		try {
//			PreservationUtil p = new PreservationUtil();
//			Map<String, Object> retMap = new HashMap<String, Object>();
//			ContractFilePreservationCreateRequest.Builder contractBuilder = new ContractFilePreservationCreateRequest.Builder();
//			contractBuilder.withFile("/mnt/data/contract/110800/00/02/00/2015/05/20/888800003594.pdf");
//			contractBuilder.withPreservationTitle("测试合同");
//			contractBuilder.withPreservationType(PreservationType.DIGITAL_CONTRACT);
//			contractBuilder.withIdentifer(new PersonalIdentifer("37068219851014271X", "初剑平"));
//			contractBuilder.withSourceRegistryId("123");
//			contractBuilder.withContractAmount(111);
//			contractBuilder.withContractNumber("123");
//			contractBuilder.withMobilePhone("13575785566");
//			new ThemisClient("http://sandbox.api.ebaoquan.org/services", "d40f9d5363d03945", "dd42872ad52d6b0bedf6005018dc237c").createPreservation(contractBuilder.build());
//
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//	}
}
