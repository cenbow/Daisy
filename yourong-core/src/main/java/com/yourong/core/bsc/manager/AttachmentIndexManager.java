package com.yourong.core.bsc.manager;

import java.util.List;
import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.bsc.model.AttachmentIndex;

public interface AttachmentIndexManager {
	
	/**
	 * 批量插入附件索引
	 * @param list
	 * @param keyId
	 * @return
	 */
	public int batchInsertAttachmentIndex(List<AttachmentIndex> list, String keyId) throws ManagerException;

	 /**
	  * 根据KeyId删除附件索引
	  * @param keyId
	  * @return
	  */
	 int deleteAttachmentIndexByKeyId(String keyId) throws ManagerException;
	 
	 /**
		 * 插入附件索引
		 * @param list
		 * @param keyId
		 * @return
		 */
	public int insertAttachmentIndex(Long attachmentId, String keyId) throws ManagerException;
	
	/**
	 * 根据附件索引的keyId查询附件信息
	 * @param keyId
	 * @return
	 * @throws ManagerException
	 */
	public Map<String, Object> queryAttachmentInfoByIndex(Long keyId, String module) throws ManagerException;
}
