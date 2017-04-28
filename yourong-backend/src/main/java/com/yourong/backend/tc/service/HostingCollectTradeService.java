package com.yourong.backend.tc.service;

import java.util.Map;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryTradeResult;
import com.yourong.core.tc.model.HostingCollectTrade;

public interface HostingCollectTradeService {

	/**
	 * 代收分页查询
	 * @param pageReques
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Page<HostingCollectTrade> findByPage(Page<HostingCollectTrade> pageReques, Map<String, Object> map);

	/**
	 * 
	 * @Description:根据代收交易号查询代收交易记录
	 * @param id
	 * @return
	 * @author: chaisen
	 * @time:2015年11月27日 下午4:08:23
	 */
	public ResultDto<QueryTradeResult> queryHostingCollectTradeRecord(Long id);
	
	/**
	 * 根据代收交易单号退款/解冻(项目余额不足退款失败时的异常处理)
	 * 
	 * @param tradeNo
	 * @author: luewenshan
	 * @time:2016年8月9日 下午14:55:23
	 * @return
	 */
	public ResultDO<Object> hostingRefundUnfrozen(String tradeNo);
	/**
	 * 根据代收交易号发起退款
	 * @param tradeNo
	 * @param remark
	 * @return
	 */
	public Object refundTradeCollection(String tradeNo, String remark);

	/**
	 * 
	 * @Description:根据代收号发起代收完成/撤销
	 * @param tradeNo
	 * @param handleType
	 * @return
	 * @author: wangyanji
	 * @time:2016年9月8日 上午9:27:52
	 */
	public Object handlePreAuthTrade(String tradeNo, int handleType);

	/**
	 * 补发五重礼人气值
	 * @param projectId
	 * @return
	 */
	public Object sendFiveRites(Long projectId);

}
