package com.yourong.backend.ic.service;

import com.yourong.common.domain.ResultDO;


public interface TransferProjectService {
	
	/**
	 * 
	 * @Description:转让项目发起代收完成/撤销
	 * @param transferId
	 * @param reAuthFlag
	 * @return
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2016年10月8日 上午10:46:31
	 */
	public ResultDO<Object> afterHandlePreAuthTrade(Long transferId, boolean reAuthFlag) throws Exception;

	/**
	 * 
	 * @Description:根据批付号重新发起转让代付
	 * @param batchNo
	 * @return
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2017年1月9日
	 */
	public ResultDO<Object> synTransferPay(String batchNo);
}
