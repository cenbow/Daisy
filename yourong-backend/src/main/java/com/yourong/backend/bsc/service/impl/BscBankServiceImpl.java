package com.yourong.backend.bsc.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.bsc.service.BscBankService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.bsc.manager.BscBankManager;
import com.yourong.core.bsc.model.BscBank;

@Service
public class BscBankServiceImpl implements BscBankService {

	private static Logger logger = LoggerFactory.getLogger(BscBankServiceImpl.class);
	
	@Autowired
	private BscBankManager bscBankManager;

	@Override
	public Page<BscBank> findByPage(Page<BscBank> pageRequest,
			Map<String, Object> map) {
		try {
			return bscBankManager.findByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("后台银行管理查询异常", e);
		}
		return null;
	}


	@Override
	public ResultDO<BscBank> saveBscBank(BscBank bscBank) {
		ResultDO<BscBank> bscBankDO = new ResultDO<BscBank>();
		bscBankDO.setSuccess(false);
		if(bscBank != null) {
			try {
				if(bscBank.getId() != null) {
					//编辑
					if(bscBankManager.updateBank(bscBank) == 1) {
						bscBankDO.setSuccess(true);
					}
				} else {
					//新增
					BscBank bscBankRes = bscBankManager.selectBscBank(bscBank);
					if(bscBankRes == null) {
						if(bscBankManager.insertBank(bscBank) == 1) {
							bscBankDO.setSuccess(true);
						}
					} else {
						bscBank.setRemarks("已存在相同银行信息！");
						bscBankDO.setResult(bscBank);
					}					
				}
			} catch (ManagerException e) {
				logger.error("后台保存银行异常", e);
			}
		}
		return bscBankDO;
	}


	@Override
	public ResultDO<BscBank> batchDeleteBank(BscBank bscBank) {
		ResultDO<BscBank> bscBankDO = new ResultDO<BscBank>();
		bscBankDO.setSuccess(false);
		try {
			if(bscBank.getIds().length > 0) {
				int num = bscBankManager.batchDeleteBanks(bscBank.getIds());
				bscBank.setRemarks("成功删除" + num + "条信息!");
				bscBankDO.setResult(bscBank);
				bscBankDO.setSuccess(true);
			}
			
		} catch (ManagerException e) {
			logger.error("后台批量删除 银行信息异常", e);
		}
		return bscBankDO;
	}


	@Override
	public List<BscBank> getBankList() {
		ResultDO<BscBank> bscBankDO = new ResultDO<BscBank>();
		bscBankDO.setSuccess(false);
		try {
			return bscBankManager.getBankList();
		} catch (ManagerException e) {
			logger.error("后台获取银行列表异常", e);
		}
		return null;
	}

}
