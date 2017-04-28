package com.yourong.backend.ic.service;

import java.util.List;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.CollectionProcess;


public interface CollectService {

	ResultDO<CollectionProcess> insertCollect(CollectionProcess collect, String appPath) throws ManagerException;

	CollectionProcess getCollectByRepayId(Long overdueRepayId) throws ManagerException;

	List<CollectionProcess> getCollectListByRepayId(long overdueRepayId) throws ManagerException;

	ResultDO<CollectionProcess> updateCollect(CollectionProcess collect, String appPath)throws ManagerException;

	Page<CollectionProcess> showCollectRecord(Page<CollectionProcess> pageRequest, Map<String, Object> map);

	CollectionProcess findCollectProcess(Long id)throws ManagerException;
		
}
