package com.yourong.core.handle;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.upload.model.ImageConfig;

@Component
public class AvatarAttachmentHandle extends BaseAttachmentHandle {
	private static Logger logger = LoggerFactory.getLogger(AvatarAttachmentHandle.class);
	@Autowired
	private MemberManager memberManager;
	
	@Autowired
	private Map<String, List<ImageConfig>> imagesConfig;

	@Override
	public Map<String, List<ImageConfig>> getImagesConfig() {
		return imagesConfig;
	}

	@Override
	public void attachmentsHandle(AttachmentInfo info) {
		String avatars = uploadFileToAliyun(info.getAppPath(), info.getKeyId());
		try {
			Long memberId = Long.parseLong(info.getKeyId());
			int row = memberManager.saveMemberAvatarById(memberId, avatars);
			if(row <= 0){
				logger.error("用户修改头像失败.用户编号:"+info.getKeyId());
			}else{
				memberManager.sendCouponOnCompleteMemberInfo(memberId);
				RedisMemberClient.setMemberAvatar(memberId, avatars);
			}
		}catch (ManagerException e) {
			logger.error("用户修改头像异常.用户编号:"+info.getKeyId(),e);
		}
	}

}
