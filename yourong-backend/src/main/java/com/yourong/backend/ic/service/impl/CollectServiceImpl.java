package com.yourong.backend.ic.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.yourong.backend.ic.service.CollectService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.core.bsc.dao.BscAttachmentMapper;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.fin.manager.OverdueRepayLogManager;
import com.yourong.core.fin.model.OverdueRepayLog;
import com.yourong.core.handle.AttachmentInfo;
import com.yourong.core.handle.AttachmentThread;
import com.yourong.core.handle.DebtAttachmentHandle;
import com.yourong.core.ic.dao.CollectionProcessMapper;
import com.yourong.core.ic.manager.CollectManager;
import com.yourong.core.ic.model.CollectionProcess;

@Service
public class CollectServiceImpl implements CollectService {
	
	private static Logger logger = LoggerFactory
			.getLogger(CollectServiceImpl.class);
	
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;
	
	@Autowired	
	private DebtAttachmentHandle debtAttachmentHandle;
	
	@Autowired
	private CollectManager collectManager;
	
	@Autowired
	private CollectionProcessMapper collectionProcessMapper;
	
	@Autowired
	private OverdueRepayLogManager overdueRepayLogManager;
	
	@Autowired
	private BscAttachmentMapper bscAttachmentMapper;
	
	@Override
	public ResultDO<CollectionProcess> insertCollect(CollectionProcess collect, String appPath) throws ManagerException {
		ResultDO<CollectionProcess> resultDO = new ResultDO<CollectionProcess>();
		try {
				if(collect.getCollectStatus()==1){
					OverdueRepayLog overdueRepayLog=overdueRepayLogManager.selectByPrimaryKey(collect.getOverdueRepayId());
					if(overdueRepayLog==null){
						resultDO.setResultCode(ResultCode.OVERDUE_REPAY_LOG_NOT_EXITS);
						return resultDO;
					}/*else{
						if(overdueRepayLog.getRepayStatus()!=3){
							resultDO.setResultCode(ResultCode.OVERDUE_REPAY_LOG_NOT_HADPAY_STATUS);
							return resultDO;
						}
					}*/
				}
				CollectionProcess biz=collectManager.getCollectByRepayId(collect.getOverdueRepayId());
				if(biz!=null){
					collect.setCollectTime(biz.getNextCollectTime());
					collect.setCollectForm(biz.getNextCollectForm());
				}
				int result = collectManager.insertSelective(collect);
				if (result > 0) { 
					AttachmentInfo info = new AttachmentInfo();
					info.setKeyId(collect.getId().toString());
					info.setBscAttachments(collect.getBscAttachments());
					info.setAppPath(appPath);
					info.setOperation(AttachmentInfo.SAVE);
					taskExecutor.execute(new AttachmentThread(debtAttachmentHandle, info));
				}
		} catch (ManagerException e) {
			logger.error("添加才催收历程失败,collect=" + collect, e);
			throw new ManagerException(e);
		}
		return resultDO;
	}

	@Override
	public CollectionProcess getCollectByRepayId(Long overdueRepayId) throws ManagerException {
		try {
			return collectManager.getCollectByRepayId(overdueRepayId);
		} catch (Exception e) {
			logger.error("查询失败,overdueRepayId=" + overdueRepayId, e);
		}
		return null;
	}

	@Override
	public List<CollectionProcess> getCollectListByRepayId(long overdueRepayId) throws ManagerException {
		try {
			List<CollectionProcess> collectList=collectManager.getCollectListByRepayId(overdueRepayId);
			if(Collections3.isNotEmpty(collectList)){
				for(CollectionProcess bean:collectList){
					List<BscAttachment> bscAttachments = bscAttachmentMapper
							.findAttachmentsByKeyId(String.valueOf(bean.getId()));
					if (!Collections3.isEmpty(bscAttachments)) {
						bean.setBscAttachments(bscAttachments);
					}
				}
			}
			return collectList;
		} catch (Exception e) {
			logger.error("查询催收历程失败,overdueRepayId=" + overdueRepayId, e);
		}
		return null;
	}

	@Override
	public ResultDO<CollectionProcess> updateCollect(CollectionProcess collect,String appPath) throws ManagerException {
		try {
			return collectManager.updateCollect(collect,appPath);
		} catch (Exception e) {
			logger.error("更新催收历程失败,collect=" + collect, e);
		}
		return null;
	}
	@Override
	public Page<CollectionProcess> showCollectRecord(Page<CollectionProcess> pageRequest, Map<String, Object> map) {
		try {
			List<CollectionProcess> collectList=collectManager.getCollectList((Long)map.get("overdueRepayId"));
			pageRequest.setData(collectList);
			return pageRequest;
		} catch (ManagerException e) {
			logger.error("查询逾期还款记录列表失败", e);
		}
		return null;
	}

	@Override
	public CollectionProcess findCollectProcess(Long id) throws ManagerException {
		
		try {
			CollectionProcess process=collectionProcessMapper.selectByPrimaryKey(id);
			List<BscAttachment> bscAttachments = bscAttachmentMapper
					.findAttachmentsByKeyId(String.valueOf(id));
			process.setBscAttachments(bscAttachments);
			return process;
		} catch (Exception e) {
			logger.error("查询失败,overdueRepayId=" + id, e);
		}
		return null;
	}
		
}
