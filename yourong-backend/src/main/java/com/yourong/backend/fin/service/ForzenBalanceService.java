package com.yourong.backend.fin.service;

import java.util.List;
import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.BalanceForzen;
import com.yourong.core.fin.model.BalanceUnforzen;

/**
 * 
 * @desc 冻结和解冻	
 * @author chaisen
 * 2016年7月28日下午3:22:46
 */
public interface ForzenBalanceService {

	int saveForzen(BalanceForzen balanceFrozen);
	/**
	 * 
	 * @Description:冻结
	 * @param balanceFrozen
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年8月1日 上午9:26:03
	 */
	ResultDO<BalanceForzen> frozenBalance(BalanceForzen balanceFrozen) throws Exception;
	/**
	 * 
	 * @Description:解冻
	 * @param unforzen
	 * @return
	 * @author: chaisen
	 * @throws Exception 
	 * @time:2016年8月1日 上午9:26:14
	 */
	ResultDO<BalanceUnforzen> unforzenBalance(BalanceUnforzen unforzen) throws Exception;
	/**
	 * 
	 * @Description:分页查询冻结列表
	 * @param pageRequest
	 * @param map
	 * @return
	 * @author: chaisen
	 * @time:2016年8月1日 上午10:29:17
	 */
	Page<BalanceForzen> showForzenPage(Page<BalanceForzen> pageRequest,
			Map<String, Object> map);
	/**
	 * 
	 * @Description:解冻明细
	 * @param forzenNo
	 * @return
	 * @author: chaisen
	 * @time:2016年8月1日 上午11:09:45
	 */
	List<BalanceUnforzen> selectUnforzenListByForzenNo(String forzenNo);
	/**
	 * 
	 * @Description:同步
	 * @param forzenNo
	 * @param type
	 * @return
	 * @author: chaisen
	 * @throws Exception 
	 * @time:2016年8月1日 上午11:24:47
	 */
	ResultDO synchronizedBalanceForzen(String forzenNo, Long type) throws Exception;
	/**
	 * 
	 * @Description:根据id查询解冻信息
	 * @param id
	 * @return
	 * @author: chaisen
	 * @throws ManagerException 
	 * @time:2016年8月1日 下午5:57:28
	 */
	BalanceForzen selectByPrimayKey(Long id) throws ManagerException;
	
}

