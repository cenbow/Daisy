package com.yourong.core.handle;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FileInfoUtil;
import com.yourong.common.util.OSSUtil;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.upload.model.ImageConfig;

@Component
public class ProjectThumbnailsAttachmentHandle extends BaseAttachmentHandle {
	private static Logger logger = LoggerFactory.getLogger(ProjectThumbnailsAttachmentHandle.class);

	@Autowired
	private ProjectManager projectManager;
	
	@Override
	public Map<String, List<ImageConfig>> getImagesConfig() {
		return null;
	}

	@Override
	public void attachmentsHandle(AttachmentInfo info) {
		uploadDefaultThumbnailsAttachment(info);
		
		String avatarKey = OSSUtil.getProjectKey(info.getKeyId(), info.getKeyId()+"."+FileInfoUtil.getFileExt(info.getAppPath()), DateUtils.getCurrentDateTime());
		String aliyunPath = OSSUtil.uploadImageToOSS(avatarKey, info.getAppPath());
	    String imgUrl = OSSUtil.getSimpleImageUrl(aliyunPath);
	    Project project = new Project();
	    project.setThumbnail(imgUrl);
	    project.setId(Long.parseLong(info.getKeyId()));
	    try {
			projectManager.updateByPrimaryKeySelective(project);
		} catch (ManagerException e) {
			logger.error("项目缩略图保存至阿里云异常:"+info.getKeyId(), e);
		}
	}
	
	/**
	 * 上传默认缩略图300*300
	 * @param info
	 */
	private void uploadDefaultThumbnailsAttachment(AttachmentInfo info){
		String path = info.getAppPath();
		path = path.replace("project_", "");
		String avatarKey = OSSUtil.getProjectKey(info.getKeyId(), info.getKeyId()+"_300."+FileInfoUtil.getFileExt(info.getAppPath()), DateUtils.getCurrentDateTime());
		OSSUtil.uploadImageToOSS(avatarKey, path);
	}
}
