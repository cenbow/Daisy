package com.yourong.core.bsc.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.core.bsc.dao.BscAttachmentMapper;
import com.yourong.core.bsc.manager.AttachmentIndexManager;
import com.yourong.core.bsc.manager.BscAttachmentManager;
import com.yourong.core.bsc.model.AttachmentIndex;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.ic.manager.ProjectManager;

@Component
public class BscAttachmentManagerImpl implements BscAttachmentManager {
	@Autowired
	private BscAttachmentMapper bscAttachmentMapper;

	@Autowired
	private AttachmentIndexManager attachmentIndexManager;
	
	@Autowired
	private ProjectManager projectManager;

	@Override
	public int deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			return bscAttachmentMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insert(BscAttachment record) throws ManagerException {
		try {
			return bscAttachmentMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insertSelective(BscAttachment record) throws ManagerException {
		try {
			return bscAttachmentMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BscAttachment selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return bscAttachmentMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKeySelective(BscAttachment record) throws ManagerException {
		try {
			return bscAttachmentMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKey(BscAttachment record) throws ManagerException {
		try {
			return bscAttachmentMapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<BscAttachment> findByPage(Page<BscAttachment> pageRequest, Map<String, Object> map)
			throws ManagerException {
		try {
			return bscAttachmentMapper.findByPage(pageRequest, map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int batchDelete(int[] ids) throws ManagerException {
		try {
			return bscAttachmentMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<BscAttachment> selectForPagin(Map<String, Object> map) throws ManagerException {
		try {
			return bscAttachmentMapper.selectForPagin(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int selectForPaginTotalCount(Map<String, Object> map) throws ManagerException {
		try {
			return bscAttachmentMapper.selectForPaginTotalCount(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<BscAttachment> findAttachmentsByKeyId(String keyId) throws ManagerException {
		try {
			return bscAttachmentMapper.findAttachmentsByKeyId(keyId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<BscAttachment> findAttachmentsByKeyIdAndModule(String keyId, String module, Integer num)
			throws ManagerException {
		try {
			return bscAttachmentMapper.findAttachmentsByKeyIdAndModule(keyId, module, num);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public BscAttachment getBscAttachmentByKeyIdAndModule(String keyId, String module) throws ManagerException {
		try {
			List<BscAttachment> attachList = bscAttachmentMapper.findAttachmentsByKeyIdAndModule(keyId, module, -1);
			if (attachList != null && attachList.size() > 0) {
				return attachList.get(0);
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return null;
	}

	/**
	 * 保存债权附件
	 */
	@Override
	public void saveAttachments(List<BscAttachment> bscAttachments, String keyId) throws ManagerException {
		// 保存债权附件
		List<AttachmentIndex> attachmentIndexs = Lists.newArrayList();
		if (Collections3.isNotEmpty(bscAttachments)) {
			int k = 0;
			for (BscAttachment bscAttachment : bscAttachments) {
				bscAttachment.setListOrder(k);// 排序
				insertSelective(bscAttachment);
				AttachmentIndex index = new AttachmentIndex();
				index.setAttachmentId(bscAttachment.getId());
				attachmentIndexs.add(index);
				k = k + 1;
			}
			// 保存附件索引
			attachmentIndexManager.batchInsertAttachmentIndex(attachmentIndexs, keyId);
		}
	}

	/**
	 * 更新债权附件
	 */
	@Override
	public void updateAttachments(List<BscAttachment> bscAttachments, String keyId) throws ManagerException {
		// 索引
		List<AttachmentIndex> attachmentIndexs = Lists.newArrayList();
		// 查询当前债权附件
		List<BscAttachment> existAttachments = findAttachmentsByKeyId(keyId);
		// 判断附件是否已存在，存在更新，不存在，新增，移除未使用的附件
		int k = 0;
		for (BscAttachment newAttachment : bscAttachments) {
			AttachmentIndex index = new AttachmentIndex();
			boolean flag = false;
			for (BscAttachment oldAttachment : existAttachments) {
				if (oldAttachment.getId().equals(newAttachment.getId())) {
					existAttachments.remove(oldAttachment);
					flag = true;
					break;
				}
			}
			if (flag) {
				updateByPrimaryKeySelective(newAttachment);
			} else {
				insertSelective(newAttachment);
			}
			index.setAttachmentId(newAttachment.getId());
			attachmentIndexs.add(index);
			k = k + 1;
		}
		if (Collections3.isNotEmpty(existAttachments)) {
			for (BscAttachment bscAttachment : existAttachments) {
				deleteByPrimaryKey(bscAttachment.getId());
			}
		}

		// 删除原有附件索引
		attachmentIndexManager.deleteAttachmentIndexByKeyId(keyId);
		// 生成新的附件索引
		if (Collections3.isNotEmpty(attachmentIndexs)) {
			attachmentIndexManager.batchInsertAttachmentIndex(attachmentIndexs, keyId);
		}

	}

	/**
	 * 更新项目图片索引
	 */
	@Override
	public void emergencyUpdateAttachments(List<BscAttachment> bscAttachments, String keyId)
			throws ManagerException {
		// 索引
		List<AttachmentIndex> attachmentIndexs = Lists.newArrayList();
		// 查询当前债权附件
		List<BscAttachment> existAttachments = findAttachmentsByKeyId(keyId);
		// 判断附件是否已存在，存在更新，不存在，新增，移除未使用的附件
		int k = 0;
		for (BscAttachment newAttachment : bscAttachments) {
			AttachmentIndex index = new AttachmentIndex();
			boolean flag = false;
			for (BscAttachment oldAttachment : existAttachments) {
				if (oldAttachment.getId().equals(newAttachment.getId())) {
					existAttachments.remove(oldAttachment);
					flag = true;
					break;
				}
			}
			if (flag) {
				updateByPrimaryKeySelective(newAttachment);
			} else {
				insertSelective(newAttachment);
			}
			index.setAttachmentId(newAttachment.getId());
			attachmentIndexs.add(index);
			k = k + 1;
		}
		if (Collections3.isNotEmpty(existAttachments)) {
			for (BscAttachment bscAttachment : existAttachments) {
				deleteByPrimaryKey(bscAttachment.getId());
			}
		}

		// 删除原有附件索引
		attachmentIndexManager.deleteAttachmentIndexByKeyId(keyId);
		// 生成新的附件索引
		if (Collections3.isNotEmpty(attachmentIndexs)) {
			attachmentIndexManager.batchInsertAttachmentIndex(attachmentIndexs, keyId);
		}
		
		//根据债权id获取项目id
		//删除原有该项目的图片索引
		//生成新的索引
		 Long projectId = projectManager.findProjectIdByDebtId(Long.valueOf(keyId));
		 if(projectId!=null){
			 attachmentIndexManager.deleteAttachmentIndexByKeyId(projectId.toString());
				// 生成新的附件索引
				if (Collections3.isNotEmpty(attachmentIndexs)) {
					attachmentIndexManager.batchInsertAttachmentIndex(attachmentIndexs, projectId.toString());
				}
		 }
		
	}

}
