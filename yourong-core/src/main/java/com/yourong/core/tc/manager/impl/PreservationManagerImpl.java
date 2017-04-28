package com.yourong.core.tc.manager.impl;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.constant.Config;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.PreservationUtil;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.dao.PreservationMapper;
import com.yourong.core.tc.manager.PreservationManager;
import com.yourong.core.tc.model.Preservation;

@Component
public class PreservationManagerImpl implements PreservationManager {

	private Logger logger = LoggerFactory.getLogger(ContractManagerImpl.class);

	@Autowired
	private PreservationMapper preservationMapper;

	@Autowired
	private SysDictManager sysDictManager;

	@Override
	public Integer insert(Preservation preservation) throws ManagerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Preservation selectByTransactionId(Long transactionId)
			throws ManagerException {
		try {
			return preservationMapper.selectByTransactionId(transactionId);
		} catch (Exception e) {
			logger.error("根据交易号查询保全数据失败，transactionId=" + transactionId, e);
			throw new ManagerException("根据交易号查询保全数据失败", e);
		}
	}

	@Override
	public Preservation createPreservation(Preservation preservation) {
		PreservationUtil preservationUtil = new PreservationUtil();
		try {
			preservation.setSuccessFlag(0);
			//判断是否是生产环境，如果不是生产环境不发送手机号，发送默认邮箱
			String email = null;
			String phone = null;
			//判断保全是否通知用户
			String msg_notice = null;
			SysDict dict = null;
			if("backend".equals(preservation.getFromSys())) {
				dict = sysDictManager.findByGroupNameAndKey("regular_preservation_notice", "preservation_backend_notice");
			} else {
				dict = sysDictManager.findByGroupNameAndKey("regular_preservation_notice", "preservation_web_notice");
			}
			if(dict != null && "Y".equals(dict.getValue())) {
				msg_notice = "Y";
			} else {
				msg_notice = "N";
			}

			if(PropertiesUtil.isDev()) {
				//开发or测试环境
				if("Y".equals(msg_notice))
					email = "cai.lulu@yourongwang.com";
			} else {
				//生成环境
				if("Y".equals(msg_notice))
					phone = preservation.getMemberPhone();
			}
//			Map<String, Object> preservationMap = preservationUtil.createContractPreservation(
//					preservation.getContractPath(),
//					preservation.getContractTitle(),
//					preservation.getIdentityNumber(),
//					preservation.getIdentiferTrueName(),
//					preservation.getMemberId().toString(),
//					preservation.getInvestAmount(),
//					preservation.getTransactionId().toString(),
//					email,
//					phone);
//			if(preservationMap != null && "1".equals(preservationMap.get("isSuccess").toString())) {
				preservation.setPreservationId(preservation.getTransactionId()+10000000000L);
				preservation.setPreservationTime(new Date());
				preservation.setDelFlag(1);
				preservation.setSuccessFlag(1);
				preservationMapper.insertSelective(preservation);
		//	}
		} catch (Exception e) {
			logger.error("创建保全失败，transactionId=" + preservation.getTransactionId(), e);
		}
		return preservation;
	}

	@Override
	public int updateByPreservationIdSelective(Preservation preservation) throws ManagerException {
		try {
			return preservationMapper.updateByPreservationIdSelective(preservation);
		} catch (Exception e) {
			logger.error("根据保全ID更新保全表数据失败，preservationId=" + preservation.getPreservationId(), e);
			throw new ManagerException("根据保全ID更新保全表数据失败", e);
		}
	}

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

}
