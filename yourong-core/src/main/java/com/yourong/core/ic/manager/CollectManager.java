package com.yourong.core.ic.manager;

import java.util.List;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.core.ic.model.CollectionProcess;


public interface CollectManager {

	int insertSelective(CollectionProcess collect)throws ManagerException;

	CollectionProcess getCollectByRepayId(Long overdueRepayId) throws ManagerException;

	List<CollectionProcess> getCollectListByRepayId(long overdueRepayId)throws ManagerException;

	ResultDO<CollectionProcess> updateCollect(CollectionProcess collect, String appPath)throws ManagerException;

	List<CollectionProcess> getCollectList(Long overdueRepayId)throws ManagerException;

	int countCollectionByRepayId(Long id)throws ManagerException;

	//CollectionProcess selectByPrimaryKey(Long id)throws ManagerException;
		
}
