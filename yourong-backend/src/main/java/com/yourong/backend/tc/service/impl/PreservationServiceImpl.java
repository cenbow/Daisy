package com.yourong.backend.tc.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.tc.service.PreservationService;
import com.yourong.backend.utils.SysServiceUtils;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.util.PreservationUtil;
import com.yourong.common.web.BaseService;
import com.yourong.core.bsc.manager.AttachmentIndexManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.manager.ContractCoreManager;
import com.yourong.core.tc.manager.PreservationManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Preservation;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;

@Service
public class PreservationServiceImpl extends BaseService implements PreservationService {

	@Autowired
	private PreservationManager preservationManager;

	@Autowired
	private TransactionManager transactionManager;

	@Autowired
	private MemberManager memberManager;

	@Autowired
	private AttachmentIndexManager attachmentIndexManager;

	@Autowired
	private SysDictManager sysDictManager;

	@Autowired
	private ProjectManager projectManager;
	
	@Autowired
	private ContractCoreManager contractCoreManager;

	@Override
	public Preservation getPreservationLink(Long preservationId) {
		try {
			PreservationUtil preservationUtil = new PreservationUtil();
			Map<String, Object> retMap = preservationUtil.getPreservationLink(preservationId);
			if (retMap != null && "1".equals(retMap.get("isSuccess").toString())) {
				Preservation perPreservation = new Preservation();
				perPreservation.setPreservationLink(retMap.get("link").toString());
				perPreservation.setPreservationTime((Date) retMap.get("linkExpireTime"));
				return perPreservation;
			}
		} catch (Exception e) {
			logger.error("通过保全ID查询保全链接失败！，preservationId=" + preservationId, e);
		}
		return null;
	}

	@Override
	public int updateByPreservationIdSelective(Preservation preservation) {
		try {
			return preservationManager.updateByPreservationIdSelective(preservation);
		} catch (Exception e) {
			logger.error("根据保全ID更新保全数据失败！，preservationId=" + preservation.getPreservationId(), e);
		}
		return 0;
	}

	@Override
	public Preservation selectByTransactionId(Long transactionId) {
		try {
			return preservationManager.selectByTransactionId(transactionId);
		} catch (Exception e) {
			logger.error("通过id查询保全记录失败！ transactionId=" + transactionId, e);
			return null;
		}
	}

	@Override
	public ResultDO<Preservation> createPreservationByTransactionId(Long transactionId) {
		ResultDO<Preservation> resultDO = new ResultDO<Preservation>();
		if (selectByTransactionId(transactionId) != null) {
			resultDO.setSuccess(false);
			resultDO.setResultCode(ResultCode.PROJECT_IS_PRESERVATIONED_ERROR);
			return resultDO;
		}
		try {
			// 通过交易id查询交易信息
			Transaction transaction = transactionManager.selectTransactionById(transactionId);
			if (transaction == null) {
				resultDO.setSuccess(false);
				resultDO.setResultCode(ResultCode.PROJECT_NOT_EXIST_ERROR);
				return resultDO;
			}
			/*@SuppressWarnings("rawtypes")
			Map attMap = attachmentIndexManager.queryAttachmentInfoByIndex(transactionId, "contract");
			if (!MapUtils.isEmpty(attMap)) {
				
			}*/
			contractCoreManager.getContractPreservation(transactionId, "backend");
			if (selectByTransactionId(transactionId) == null) {
				resultDO.setSuccess(false);
				return resultDO;
			}
			
			resultDO.setSuccess(true);
			return resultDO;
		} catch (Exception e) {
			logger.error("根据交易号创建保全失败！ transactionId=" + transactionId, e);
			resultDO.setSuccess(false);
			return resultDO;
		}
	}

	@Override
	public int createPreservationTask() throws Exception {
		int count = 0;
		int pageSize = 10;
		List<Transaction> transList = null;
		Long transactionId = null;
		String excuteFlag = SysServiceUtils.getDictValue("task_notice", "regular_preservation_notice", "N");
		// 把标识设为N，避免定时任务的唯一性
		if (!"N".equals(excuteFlag)) {
			// 读取创建保全的开始时间和结束
			String[] times = excuteFlag.split("To");
			if (times.length != 2) {
				logger.info("定时创建保全未配置起止时间");
				return 0;
			}
			SysDict dict = sysDictManager.findByGroupNameAndKey("regular_preservation_notice", "task_notice");
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("startTime", times[0]);
			paraMap.put("endTime", times[1]);
			// 开始创建保全
			while (!"N".equals(SysServiceUtils.getDictValue("task_notice", "regular_preservation_notice", "N"))) {
				if (transactionId != null && transactionId > 0L) {
					paraMap.put("id", transactionId);
				} else {
					paraMap.remove("id");
				}
				paraMap.put("maxNum", pageSize);
				transList = transactionManager.queryNotPreservTransForList(paraMap);
				if (CollectionUtils.isNotEmpty(transList)) {
					for (Transaction t : transList) {
						logger.info("开始创建保全 transactionId=" + t.getId());
						createPreservationByTransactionId(t.getId());
						transactionId = t.getId();
						count++;
					}
				} else {
					break;
				}
				transList = null;
			}
			SysDict updateDict = new SysDict();
			updateDict.setId(dict.getId());
			updateDict.setValue("N");
			sysDictManager.updateByPrimaryKeySelective(updateDict);
		}
		return count;
	}

}