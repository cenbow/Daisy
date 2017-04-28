package com.yourong.core.handle;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.OSSUtil;
import com.yourong.core.bsc.manager.BscAttachmentManager;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.handle.AttachmentInfo.AttachMentType;
import com.yourong.core.upload.model.ImageConfig;

/**
 * 附件处理的基类
 *
 */
public abstract class BaseAttachmentHandle {
	
	private static Logger logger = LoggerFactory.getLogger(BaseAttachmentHandle.class);
	
	@Autowired
	public BscAttachmentManager bscAttachmentManager;
	
	/**
	 * 图片配置项
	 * @return
	 */
	public abstract Map<String, List<ImageConfig>> getImagesConfig();
	
	
	/**
	 * 附件处理
	 * @param info
	 */
	public void attachmentsHandle(AttachmentInfo info){
		//TODO 此方法是默认的实现，如果有特殊业务，请在子类重写此方法
		try {
			//上传附件至阿里云
			uploadFileToAliyun(info.getKeyId(), info.getBscAttachments(), info.getAppPath(),info.getAttachmentType());
			
			if(StringUtil.isNotEmpty(info.getOperation())){
				switch(info.getOperation()){
					case AttachmentInfo.UPDATE:
						bscAttachmentManager.updateAttachments(info.getBscAttachments(), info.getKeyId());
						break;
					case AttachmentInfo.SAVE:
						bscAttachmentManager.saveAttachments(info.getBscAttachments(), info.getKeyId());
						break;
					case AttachmentInfo.EMERGENCY_UPDATE://紧急修改债权图片
						bscAttachmentManager.emergencyUpdateAttachments(info.getBscAttachments(), info.getKeyId());
						break;
				}
			}
		} catch (ManagerException e) {
			logger.error("附件处理失败，KeyID:"+info.getKeyId(), e);
		}
	}
	
	
	/**
	 * 上传附件至阿里云
	 * @param keyId
	 * @param bscAttachments
	 */
	public void uploadFileToAliyun(String keyId, List<BscAttachment> bscAttachments, String appPath,AttachMentType attachmemntType){
		if (Collections3.isNotEmpty(bscAttachments)) {
			for (BscAttachment bscAttachment : bscAttachments) {
				String fileName = getFileName(bscAttachment.getFileUrl());
				if(attachmemntType==null){
					attachmemntType = AttachmentInfo.AttachMentType.DEBT;//默认类型是债权
				}
				String debtIdKey ="";
				if(AttachmentInfo.AttachMentType.DEBT.equals(attachmemntType)){
				   debtIdKey = OSSUtil.getDebtKey(keyId, fileName, DateUtils.getCurrentDateTime());
				}
				if(AttachmentInfo.AttachMentType.PROJECT.equals(attachmemntType)){
				   debtIdKey = OSSUtil.getProjectKey(keyId, fileName, DateUtils.getCurrentDateTime());
				}
				if(AttachmentInfo.AttachMentType.GOODS.equals(attachmemntType)){
					debtIdKey = OSSUtil.getGoodsKey(keyId, fileName, DateUtils.getCurrentDateTime());
				}
				if(AttachmentInfo.AttachMentType.ARTICLE.equals(attachmemntType)){
					debtIdKey = OSSUtil.getGoodsKey(keyId, fileName, DateUtils.getCurrentDateTime());
				}
				if(AttachmentInfo.AttachMentType.COMPANYMANAGE.equals(attachmemntType)){
					debtIdKey = OSSUtil.getManageKey(keyId, fileName, DateUtils.getCurrentDateTime());
				}
				//未上传到阿里云的图片
				if(bscAttachment.getId() == null){
					String localPath="";
					if(bscAttachment.getFileUrl().indexOf("open/upload")<0){
						localPath = appPath + bscAttachment.getFileUrl();
					}else {
						localPath =  bscAttachment.getFileUrl();
					}
					String url = OSSUtil.uploadImageToOSS(debtIdKey,localPath);
					String simpleUrl = OSSUtil.getSimpleImageUrl(url);
					bscAttachment.setFileUrl(simpleUrl);
					Map<String, List<ImageConfig>> imagesConfig = getImagesConfig();
					if(imagesConfig != null && imagesConfig.size() > 0){
						//裁剪图片上传阿里云
						List<ImageConfig> configList = imagesConfig.get(bscAttachment.getModule());
						if(Collections3.isNotEmpty(configList)){
							for(ImageConfig ic : configList){
								if("default".equals(ic.getSizeType())){
									continue;
								}
								String newfileName = appendSuffix(fileName,ic.getSizeType());
								String newDebtIdKey ="";
								if(AttachmentInfo.AttachMentType.DEBT.equals(attachmemntType)){
									newDebtIdKey = OSSUtil.getDebtKey(keyId, newfileName, DateUtils.getCurrentDateTime());
								}
								if(AttachmentInfo.AttachMentType.PROJECT.equals(attachmemntType)){
									newDebtIdKey = OSSUtil.getProjectKey(keyId, newfileName, DateUtils.getCurrentDateTime());
								}
								String newFilePath = appendSuffix(localPath, ic.getSizeType());
								OSSUtil.uploadImageToOSS(newDebtIdKey,newFilePath);
							}
						}
					}
				}
			}
		}
	}

	
	public String uploadFileToAliyun(String filePath, String memberId){
		String aliyunPath = "";
		String fileName = getFileName(filePath);
		Map<String, List<ImageConfig>> imagesConfig = getImagesConfig();
		Date currentDate =  DateUtils.getCurrentDate();
		Date tempDate = DateUtils.getDateFromString("2015-07-25","yyyy-MM-dd");
		if(currentDate.getTime() < tempDate.getTime()){//临时处理方案，为了解决头像在各平台的新版显示正常。8.1日后可删除
			currentDate = tempDate;
		}
		if(imagesConfig != null && imagesConfig.size() > 0){
			List<ImageConfig> configList = imagesConfig.get("avatar");
			if(Collections3.isNotEmpty(configList)){
				for(ImageConfig ic : configList){
					if(ic.getSizeType().equals("default")){
						String avatarKey = OSSUtil.getAvatarKey(memberId, fileName, currentDate);
						aliyunPath = OSSUtil.uploadImageToOSS(avatarKey, filePath);
					}else{
						String newfileName = appendSuffix(fileName,ic.getSizeType());
						String newDebtIdKey = OSSUtil.getAvatarKey(memberId, newfileName, currentDate);
						String newFilePath = appendSuffix(filePath, ic.getSizeType());
						if(StringUtil.isBlank(aliyunPath)){
							aliyunPath = OSSUtil.uploadImageToOSS(newDebtIdKey,newFilePath);
						}else{
							OSSUtil.uploadImageToOSS(newDebtIdKey,newFilePath);
						}
					}
				}
			}
		}else{
			String avatarKey = OSSUtil.getAvatarKey(memberId, fileName, currentDate);
			aliyunPath = OSSUtil.uploadImageToOSS(avatarKey, filePath);
		}
		return OSSUtil.getSimpleImageUrl(aliyunPath);
	}
	
	
	/**
	 * 文件重命名
	 * @param srcUrl
	 * @param suffix2
	 * @return
	 */
	private String appendSuffix(String srcUrl, String suffix2) {
		StringBuilder sb = new StringBuilder(srcUrl);
		sb.insert(sb.lastIndexOf("."), suffix2.toLowerCase());
		return sb.toString();
	}
	
	/**
	 * 获得文件名称
	 * @param srcUrl
	 * @return
	 */
	private String getFileName(String srcUrl){
		String fileName = srcUrl.substring(srcUrl.lastIndexOf("/")+1, srcUrl.length());
		return fileName;
	}
	
	
	
}
