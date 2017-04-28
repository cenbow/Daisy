package com.yourong.core.tc.manager;

import java.util.List;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.core.tc.model.HostingCollectTrade;
import com.yourong.core.tc.model.HostingCollectTradeAuth;

public interface HostingCollectTradeAuthManager {

	/**
	 * 插入代收交易记录
	 * @param record
	 * @return
	 * @throws ManagerException
	 */
	public int batchInsert(List<HostingCollectTradeAuth> records) throws ManagerException;

	/**
	 * 
	 * @Description:根据代收发起撤销
	 * @param collectTrade
	 * @return
	 * @author: wangyanji
	 * @time:2016年8月1日 下午3:04:08
	 */
	public ResultDto<?> cancelPreAuthTradeByCollectTrade(HostingCollectTrade collectTrade, String summary);

	/**
	 * 
	 * @Description:批量代收完成
	 * @param freezeList
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年9月18日 下午1:02:09
	 */
	public ResultDO<Object> finishPreAuthTrade(List<HostingCollectTrade> freezeList) throws ManagerException;

	/**
	 * 
	 * @Description:批量代收撤销
	 * @param freezeList
	 * @return
	 * @throws ManagerException
	 * @author: wangyanji
	 * @time:2016年9月18日 下午1:01:29
	 */
	public ResultDO<Object> cancelPreAuthTrade(List<HostingCollectTrade> freezeList) throws ManagerException;

	/**
	 * 
	 * @Description:同步冻结代收的交易状态
	 * @param freezeList
	 * @return
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2016年10月8日 下午1:15:34
	 */
	public List<HostingCollectTrade> synHostingCollectTrade(List<HostingCollectTrade> freezeList) throws Exception;

}