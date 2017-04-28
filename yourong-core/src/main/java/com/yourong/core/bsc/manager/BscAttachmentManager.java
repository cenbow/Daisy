package com.yourong.core.bsc.manager;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.bsc.model.BscAttachment;

public interface BscAttachmentManager {
	int deleteByPrimaryKey(Long id) throws ManagerException;

	int insert(BscAttachment record) throws ManagerException;

	public int insertSelective(BscAttachment record) throws ManagerException;

	BscAttachment selectByPrimaryKey(Long id) throws ManagerException;

	int updateByPrimaryKeySelective(BscAttachment record)
			throws ManagerException;

	int updateByPrimaryKey(BscAttachment record) throws ManagerException;

	Page<BscAttachment> findByPage(Page<BscAttachment> pageRequest,
			Map<String, Object> map) throws ManagerException;

	int batchDelete(int[] ids) throws ManagerException;

	List<BscAttachment> selectForPagin(Map<String, Object> map)
			throws ManagerException;

	int selectForPaginTotalCount(Map<String, Object> map)
			throws ManagerException;
	
	List<BscAttachment> findAttachmentsByKeyId(String keyId) throws ManagerException;
	
	/**
	 * 根据Key和Module获取附件
	 * @param keyId
	 * @param module
	 * @param num 数量，如果不限制提供负数即可
	 * @return
	 */
	public List<BscAttachment> findAttachmentsByKeyIdAndModule(String keyId, String module, Integer num) throws ManagerException;
	
	/**
	 * 根据Key和Module获取附件
	 * @param keyId
	 * @param module
	 * @return
	 * @throws ManagerException
	 */
	public BscAttachment getBscAttachmentByKeyIdAndModule(String keyId, String module) throws ManagerException;
	
	/**
	 * 保存附件
	 * @param bscAttachments
	 * @param keyId
	 * @throws ManagerException
	 */
	public void saveAttachments(List<BscAttachment> bscAttachments, String keyId) throws ManagerException;
	
	/**
	 * 更新附件
	 * @param bscAttachments
	 * @param keyId
	 * @throws ManagerException
	 */
	public void updateAttachments(List<BscAttachment> bscAttachments, String keyId) throws ManagerException;

	/**
	 * 更新债权附件
	 * 更新与该债权关联的项目图片索引
	 * @param bscAttachments
	 * @param keyId
	 * @throws ManagerException
	 */
	void emergencyUpdateAttachments(List<BscAttachment> bscAttachments, String projectId) throws ManagerException;
}
