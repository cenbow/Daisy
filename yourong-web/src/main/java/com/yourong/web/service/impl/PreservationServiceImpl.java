package com.yourong.web.service.impl;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.yourong.common.util.PreservationUtil;
import com.yourong.core.tc.manager.PreservationManager;
import com.yourong.core.tc.model.Preservation;
import com.yourong.web.service.BaseService;
import com.yourong.web.service.PreservationService;

@Service
public class PreservationServiceImpl extends BaseService implements PreservationService {

	@Resource
	private PreservationManager preservationManager;
	
	@Override
	public Preservation getPreservationLink(Long preservationId) {
		try {
			PreservationUtil preservationUtil = new PreservationUtil();
			Map<String, Object> retMap = preservationUtil.getPreservationLink(preservationId);
			if(retMap != null && "1".equals(retMap.get("isSuccess").toString())) {
				Preservation perPreservation = new Preservation();
				perPreservation.setPreservationLink(retMap.get("link").toString());
				perPreservation.setPreservationTime((Date)retMap.get("linkExpireTime"));
				return perPreservation;
			}
		} catch(Exception e) {
			logger.error("通过保全ID查询保全链接失败！，preservationId=" + preservationId, e);
		}
		return null;
	}

	@Override
	public int updateByPreservationIdSelective(Preservation preservation) {
		try {
			return preservationManager.updateByPreservationIdSelective(preservation);
		} catch(Exception e) {
			logger.error("根据保全ID更新保全数据失败！，preservationId=" + preservation.getPreservationId(), e);
		}
		return 0;
	}

	@Override
	public Preservation selectByTransactionId(Long transactionId) {
		try {
			return preservationManager.selectByTransactionId(transactionId);
		} catch(Exception e) {
			logger.error("通过id查询保全记录失败！ transactionId=" + transactionId, e);
			return null;
		}
	}
    
}