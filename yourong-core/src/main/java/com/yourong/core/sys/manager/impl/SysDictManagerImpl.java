package com.yourong.core.sys.manager.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sms.SmsMobileSend;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.core.common.MessageClient;
import com.yourong.core.sys.dao.SysDictMapper;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;

@Component
public class SysDictManagerImpl implements SysDictManager {
	
	private static final Logger logger = LoggerFactory
			.getLogger(SysDictManagerImpl.class);
	@Autowired
	private SysDictMapper sysDictMapper;
	
	@Autowired
	private SmsMobileSend smsMobileSend;

	public Integer deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			int result = sysDictMapper.deleteByPrimaryKey(id);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer insert(SysDict sysDict) throws ManagerException {
		try {
			int result = sysDictMapper.insert(sysDict);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public SysDict selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return sysDictMapper.selectByPrimaryKey(id);

		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer updateByPrimaryKey(SysDict sysDict) throws ManagerException {
		try {

			return sysDictMapper.updateByPrimaryKey(sysDict);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer updateByPrimaryKeySelective(SysDict sysDict)
			throws ManagerException {
		try {

			return sysDictMapper.updateByPrimaryKeySelective(sysDict);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Integer batchDelete(long[] ids) throws ManagerException {
		try {

			return sysDictMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	public Page<SysDict> findByPage(Page<SysDict> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			return sysDictMapper.findByPage(pageRequest, map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<SysDict> findByGroupName(String groupName)
			throws ManagerException {
		try {
			return sysDictMapper.findByGroupName(groupName);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public SysDict findByGroupNameAndKey(String groupName, String key) throws ManagerException {
		try {
			Map map = Maps.newHashMap();
			map.put("groupName",groupName);
			map.put("key",key);
			return sysDictMapper.findByGroupNameAndKey(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	public Integer updateByGroupNameAndKey(SysDict sysDict)
			throws ManagerException {
		try {
			return sysDictMapper.updateByGroupNameAndKey(sysDict);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Integer insertSelective(SysDict sysDict) throws ManagerException {
		try {
			return sysDictMapper.insertSelective(sysDict);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	/**
	 * 
	 * @desc 短信提醒
	 * @throws ManagerException
	 * @author chaisen
	 * @time 2016年4月7日 下午1:05:56
	 *
	 */
	@Override
	public void sendSmsRemind() throws ManagerException {
		List<SysDict> dictList = sysDictMapper.findByGroupName(TypeEnum.SMS_REMIND.getCode());
		BigDecimal balance=BigDecimal.ZERO;
		BigDecimal number=new BigDecimal(10);
		int numbers=0;
		int setNumbers=0;
		if(Collections3.isEmpty(dictList)){
			return;
		}
		for(SysDict dict:dictList){
			balance=smsMobileSend.queryBalanceSMS(dict.getKey()).getResult();
			//剩余数量
			numbers=balance.multiply(number).intValue();
			if(dict.getRemarks()!=null){
				//预警数量
				setNumbers=Integer.parseInt(dict.getRemarks());
				if(setNumbers>numbers){
					logger.info("短信剩余数量预警提醒定时任务，短信账号"+dict.getKey() +",目前剩余数量:"+numbers+ "条,发送短信开始");
					String message="您好！有融网用于"+dict.getLabel()+"的短信账号,目前剩余数量为"+numbers+"条,低于预警值"+dict.getRemarks()+"条,请及时安排充值！回复TD退订";
					MessageClient.sendShortMessageByMobile(Long.parseLong(dict.getValue()),message);
					SysDict sd=new SysDict();
					sd.setId(dict.getId());
					sd.setDescription(message);
					sysDictMapper.updateByPrimaryKeySelective(sd);
				}
			}
		}
		
	}

	@Override
	public SysDict selectByKey(String key) throws ManagerException {
		try {
			return sysDictMapper.selectByKey(key);

		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
}