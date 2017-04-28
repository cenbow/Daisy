package com.yourong.core.bsc.manager;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.bsc.model.BscBank;

@Component
public interface BscBankManager {

	/**
	 * 根据Code获得银行卡
	 * @param code
	 * @return
	 * @throws ManagerException
	 */
	public BscBank getBscBankByCode(String code) throws ManagerException;
	
	/**
	 * 后台银行管理查询
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	Page<BscBank> findByPage(Page<BscBank> pageRequest, Map<String, Object> map) throws ManagerException;
	
	/**
	 * 后台新增银行
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	int insertBank(BscBank bscBank) throws ManagerException;
	
	/**
	 * 后台更新银行
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	int updateBank(BscBank bscBank) throws ManagerException;
	
	/**
	 * 查询银行
	 * @param pageRequest
	 * @param map
	 * @return
	 * @throws ManagerException
	 */
	BscBank selectBscBank(BscBank bscBank) throws ManagerException;
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 * @throws ManagerException
	 */
	public int batchDeleteBanks(long[] ids) throws ManagerException;
	
	/**
     * 获取银行列表
     * @param bscBank
     * @return
     */
    public List<BscBank> getBankList() throws ManagerException;
}
