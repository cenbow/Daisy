/**
 * 
 */
package com.yourong.backend.ic.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.ic.service.ManagerFeeService;
import com.yourong.common.constant.Config;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FileInfoUtil;
import com.yourong.common.util.OSSUtil;
import com.yourong.core.bsc.manager.BscAttachmentManager;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.fin.model.ProjectFee;
import com.yourong.core.fin.model.ManagementFeeAgreement;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.biz.ContractBiz;
import com.yourong.core.tc.model.query.TransactionQuery;
import com.yourong.core.uc.manager.EnterpriseManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Enterprise;
import com.yourong.core.uc.model.Member;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年4月29日下午4:19:46
 */
@Service
public class ManagerFeeServiceImpl implements ManagerFeeService{
	private static Logger logger = LoggerFactory.getLogger(ManagerFeeServiceImpl.class);
	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private EnterpriseManager enterpriseManager;
	
	@Autowired
	private MemberManager memberManager;
	
	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired
	private BscAttachmentManager bscAttachmentManager;
	
	
	@Override
	public ProjectFee selectBorrowerInformation(Long projectId)  {
		ProjectFee managementFee = new ProjectFee();
		try {
			Project project = projectManager.selectByPrimaryKey(projectId);
			if(project.getBorrowerType()!=null&&project.getBorrowerType()==2){//借款人为企业
				Enterprise enterprise = enterpriseManager.selectByKey(project.getEnterpriseId());
				if(enterprise!=null){
					managementFee.setBorrowerType(TypeEnum.MEMBER_TYPE_COMPANY.getType());//借款类型为2，且企业信息不为空，置为2
					managementFee.setBorrowerName(enterprise.getFullName());
					managementFee.setOrganizationCode(enterprise.getOrganizationCode());
					return managementFee;
				}
			}
				Member member = memberManager.selectByPrimaryKey(project.getBorrowerId());
				managementFee.setBorrowerType(TypeEnum.MEMBER_TYPE_PERSONAL.getType());
				managementFee.setBorrowerName(member.getTrueName());
				managementFee.setIdentityNumber(member.getIdentityNumber());
		} catch (ManagerException e) {
			logger.error("管理费列表获取项目借款人相关数据", e);
		}
		return managementFee;
		
	}
	
	@Override
	public List<ManagementFeeAgreement> selectAgreementInformation(Long projectId)  {
		List<ManagementFeeAgreement> managementFeeAgreementList = null;
		TransactionQuery transactionQuery = new TransactionQuery();	
		try {
			transactionQuery.setProjectId(projectId);
			List<Transaction>  transactionList =transactionManager.selectTransactionsByQueryParams(transactionQuery);
			managementFeeAgreementList = BeanCopyUtil.mapList(transactionList, ManagementFeeAgreement.class);
			for(ManagementFeeAgreement managementFeeAgreement :managementFeeAgreementList){
				Member member = memberManager.selectByPrimaryKey(managementFeeAgreement.getMemberId());  
				managementFeeAgreement.setMember(member);
			}
		} catch (ManagerException e) {
			logger.error("管理费列表获取项目借款协议", e);
		}
		return managementFeeAgreementList;
		
	}
	
	@Override
	public ResultDO<ContractBiz> p2pViewContract(Long transactionId){
		ResultDO<ContractBiz> result = new ResultDO<ContractBiz>();
		try {
			Transaction transaction = transactionManager.selectTransactionById(transactionId);
			return transactionManager.p2pViewContract(transaction.getOrderId(), transaction.getMemberId());
		} catch (Exception e) {
			result.setSuccess(false);
			logger.error("管理费列表，查看合同发生异常，transactionId：" + transactionId, e);
		}
		return result;
	}
	 
	public String  agreementDown(Long projectId,HttpServletResponse resp){
    	try {
			return this.Down(projectId, resp);
		} catch (Exception e) {
			logger.error("获取项目下合同压缩包异常，projectId：" + projectId, e);
		}
    	return "";
    }    
	 
	public String  Down(Long projectId,HttpServletResponse resp) throws Exception {   
		
		//生产环境
		String saveDirUrl =  Config.prefixPath
				+ FileInfoUtil.getContractFolder(projectId.toString()+"000", DateUtils.getCurrentDate());
		//String saveDirUrl = "D:\\temporary";//本机测试
		String saveZipDirUrl = Config.prefixPath
				+ FileInfoUtil.getContractFolder(projectId.toString()+"001", DateUtils.getCurrentDate());  
		
        logger.info("开始创建目录");
        FileInfoUtil.mkdir(saveDirUrl);
        	//saveDir.mkdir();  
        FileInfoUtil.mkdir(saveZipDirUrl);
        	//saveZipDir.mkdir();  
        logger.info("saveDir:"+saveDirUrl+"saveZipDir"+saveZipDirUrl);
      //文件保存位置  
		 TransactionQuery transactionQuery = new TransactionQuery();	
		 transactionQuery.setProjectId(projectId);
		 List<Transaction>  transactionList =transactionManager.selectTransactionsByQueryParams(transactionQuery);
		 for(Transaction transaction :transactionList){
			 	BscAttachment attachment = bscAttachmentManager.getBscAttachmentByKeyIdAndModule(transaction.getId().toString(),DebtEnum.ATTACHMENT_MODULE_CONTRACT.getCode());
			 	 logger.info("交易对应url"+attachment.getFileUrl()+attachment.getFileName());
			 	String url = OSSUtil.getContractDownloadUrl(attachment.getFileUrl() + attachment.getFileName(),null);
				URL fileUrl = new URL(url); 
				HttpURLConnection conn = (HttpURLConnection)fileUrl.openConnection();    
                //设置超时间为3秒  
				conn.setConnectTimeout(3*1000);  
		        //防止屏蔽程序抓取而返回403错误  
		        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
		        //得到输入流  
		        InputStream inputStream = conn.getInputStream();    
		        //获取自己数组  
		        byte[] getData = readInputStream(inputStream);      
		         
		        File file = new File(saveDirUrl+File.separator+attachment.getFileName());      
		        FileOutputStream fos = new FileOutputStream(file);       
		        fos.write(getData);   
		        if(fos!=null){  
		            fos.close();    
		        }  
		        if(inputStream!=null){  
		            inputStream.close();  
		        }  
		        System.out.println("info:"+url+" download success,transactionId+"+transaction.getId().toString());   
		 }
		 logger.info("开始压缩");
		 boolean flag = this.fileToZip(saveDirUrl, saveZipDirUrl, projectId.toString());  
		 if(flag){
			 //this.downFile(resp, saveZipDirUrl + "/" + projectId.toString() +".zip");
			 return saveZipDirUrl + File.separator + projectId.toString() +".zip";
		 }
		return "";
    } 
	

	/** 
     * 压缩确定目录文件，到指定目录
     * @return 
     * @throws IOException 
     */  
	public static boolean fileToZip(String sourceFilePath, String zipFilePath,
			String fileName) {
		boolean flag = false;
		File sourceFile = new File(sourceFilePath);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		if (sourceFile.exists() == false) {
			 logger.info("待压缩的文件目录：" + sourceFilePath + "不存在.");
		} else {
			try {
				File zipFile = new File(zipFilePath + File.separator + fileName
						+ ".zip");
				if (zipFile.exists()) {
					 logger.info(zipFilePath + "目录下存在名字为:" + fileName
							+ ".zip" + "打包文件.删除重新打包");
					zipFile.delete();
				}
				File[] sourceFiles = sourceFile.listFiles();
				if (null == sourceFiles || sourceFiles.length < 1) {
					 logger.info("待压缩的文件目录：" + sourceFilePath
							+ "里面不存在文件，无需压缩.");
				} else {
					fos = new FileOutputStream(zipFile);
					zos = new ZipOutputStream(new BufferedOutputStream(fos));
					byte[] bufs = new byte[1024 * 10];
					for (int i = 0; i < sourceFiles.length; i++) {
						// 创建ZIP实体，并添加进压缩包
						ZipEntry zipEntry = new ZipEntry(
								sourceFiles[i].getName());
						zos.putNextEntry(zipEntry);
						// 读取待压缩的文件并写进压缩包里
						fis = new FileInputStream(sourceFiles[i]);
						bis = new BufferedInputStream(fis, 1024 * 10);
						int read = 0;
						while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
							zos.write(bufs, 0, read);
						}
					}
					flag = true;
				}
			} catch (FileNotFoundException e) {
				logger.error("获取相关文件异常，sourceFilePath：" + sourceFilePath, e);
			} catch (IOException e) {
				logger.error("压缩项目下合同文件异常，sourceFilePath：" + sourceFilePath, e);
			} finally {
				try {// 关闭流
					if (null != bis)
						bis.close();
					if (null != zos)
						zos.close();
				} catch (IOException e) {
					logger.error("压缩项目下合同文件，关闭流异常，sourceFilePath：" + sourceFilePath, e);
				}
			}
		}
		return flag;
	}
	
	/** 
     * 从输入流中获取字节数组 
     * @param inputStream 
     * @return 
     * @throws IOException 
     */  
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {    
        byte[] buffer = new byte[1024];    
        int len = 0;    
        ByteArrayOutputStream bos = new ByteArrayOutputStream();    
        while((len = inputStream.read(buffer)) != -1) {    
            bos.write(buffer, 0, len);    
        }    
        bos.close();    
        return bos.toByteArray();    
    }   
	
	
	
}
