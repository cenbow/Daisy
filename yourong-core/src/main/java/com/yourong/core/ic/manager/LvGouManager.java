package com.yourong.core.ic.manager;

import com.yourong.common.exception.ManagerException;

/**
 * 绿狗合同接口
 * @author wangyanji
 *
 */
public interface LvGouManager {

	/**
	 * 项目履约后推送电子合同给绿狗
	 * @param projectId
	 * @return
	 * @throws ManagerException
	 */
	public int createLvGouContract(Long projectId);
	
	/**
	 * 更新绿狗项目状态
	 * @param projectId
	 * @param status 状态0=>无坏账或无逾期,1=>是坏账或逾期
	 * @return
	 * @throws ManagerException
	 */
	public void updateLvGouContractStatus(Long projectId, int status);
}
