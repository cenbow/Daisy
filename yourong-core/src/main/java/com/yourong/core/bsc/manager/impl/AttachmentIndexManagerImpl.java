package com.yourong.core.bsc.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.DateUtils;
import com.yourong.core.bsc.dao.AttachmentIndexMapper;
import com.yourong.core.bsc.manager.AttachmentIndexManager;
import com.yourong.core.bsc.model.AttachmentIndex;

@Component
public class AttachmentIndexManagerImpl implements AttachmentIndexManager {
	
	@Autowired
	AttachmentIndexMapper attachmentIndexMapper;

	@Override
	public int batchInsertAttachmentIndex(List<AttachmentIndex> attachmentIndexList, String keyId)
			throws ManagerException {
		try{
			for(AttachmentIndex attachmentIndex : attachmentIndexList){
				attachmentIndex.setKeyId(keyId);
				attachmentIndex.setCreateTime(DateUtils.getCurrentDateTime());
			}
			return attachmentIndexMapper.batchInsertAttachmentIndex(attachmentIndexList);
		}catch(Exception ex){
			throw new ManagerException(ex);
		}
	}

	@Override
	public int deleteAttachmentIndexByKeyId(String keyId)
			throws ManagerException {
		try{
			return attachmentIndexMapper.deleteAttachmentIndexByKeyId(keyId);
		}catch(Exception ex){
			throw new ManagerException(ex);
		}
	}

	@Override
	public int insertAttachmentIndex(Long attachmentId, String keyId)
			throws ManagerException {
		try{
			AttachmentIndex attachmentIndex = new AttachmentIndex();
			attachmentIndex.setAttachmentId(attachmentId);
			attachmentIndex.setKeyId(keyId);
			return attachmentIndexMapper.insertSelective(attachmentIndex);
		}catch(Exception ex){
			throw new ManagerException(ex);
		}
	}

	@Override
	public Map<String, Object> queryAttachmentInfoByIndex(Long keyId, String module)
			throws ManagerException {
		
		try{
			return attachmentIndexMapper.queryAttachmentInfoByIndex(keyId, module);
		}catch(Exception ex){
			throw new ManagerException(ex);
		}
	}

}
