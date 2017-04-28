package com.yourong.core.ic.manager.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.core.handle.AttachmentInfo;
import com.yourong.core.handle.AttachmentThread;
import com.yourong.core.handle.DebtAttachmentHandle;
import com.yourong.core.ic.dao.CollectionProcessMapper;
import com.yourong.core.ic.manager.CollectManager;
import com.yourong.core.ic.model.CollectionProcess;

@Component
public class CollectManagerImpl implements CollectManager {
	@Autowired
	private CollectionProcessMapper collectMapper;
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;
	
	@Autowired	
	private DebtAttachmentHandle debtAttachmentHandle;
	@Override
	public int insertSelective(CollectionProcess record) throws ManagerException {
		try {
			return collectMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public CollectionProcess getCollectByRepayId(Long overdueRepayId) throws ManagerException {
		try {
			return collectMapper.getCollectByRepayId(overdueRepayId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public List<CollectionProcess> getCollectListByRepayId(long overdueRepayId) throws ManagerException {
		try {
			return collectMapper.getCollectListByRepayId(overdueRepayId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public ResultDO<CollectionProcess> updateCollect(CollectionProcess collect,String appPath) throws ManagerException {
		ResultDO<CollectionProcess> result=new ResultDO<CollectionProcess>();
				int i=0;
				CollectionProcess collectBean=new CollectionProcess();
				collectBean.setId(collect.getId());
				collectBean.setCollectDetail(collect.getCollectDetail());
				i=collectMapper.updateByPrimaryKeySelective(collectBean);
				if(i>0){
						AttachmentInfo info = new AttachmentInfo();
						info.setKeyId(collect.getId().toString());
						info.setBscAttachments(collect.getBscAttachments());
						info.setAppPath(appPath);
						info.setOperation(AttachmentInfo.UPDATE);
						taskExecutor.execute(new AttachmentThread(debtAttachmentHandle, info));
				}else{
					result.setSuccess(false);
				}
				return result;
	}
	/*@Override
	public CollectionProcess selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return collectMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}*/
	@Override
	public List<CollectionProcess> getCollectList(Long overdueRepayId) throws ManagerException {
		try {
			return collectMapper.getCollectList(overdueRepayId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	@Override
	public int countCollectionByRepayId(Long id) throws ManagerException {
		try {
			return collectMapper.countCollectionByRepayId(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
}
