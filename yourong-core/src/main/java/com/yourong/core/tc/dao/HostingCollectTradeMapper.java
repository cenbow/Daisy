package com.yourong.core.tc.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yourong.common.pageable.Page;
import com.yourong.core.tc.model.HostingCollectTrade;

@Repository
public interface HostingCollectTradeMapper {

	/**
	 * 插入代收交易
	 * @param record
	 * @return
	 */
	public int insertSelective(HostingCollectTrade record);

	/**
	 * 通过id查询代收交易
	 * @param id
	 * @return
	 */
	public HostingCollectTrade selectByPrimaryKey(Long id);

	/**
	 * 更新代收交易
	 * @param record
	 * @return
	 */
	public int updateByPrimaryKeySelective(HostingCollectTrade record);

	/**
	 * 查询代收交易
	 * @param params
	 * @return
	 */
	public HostingCollectTrade selectHostingCollectTradeByParams(
			Map<String, Object> params);

	/**
	 * 查询需要同步的代收交易
	 * @return
	 */
	public List<HostingCollectTrade> selectSynchronizedHostingCollectTrades();
	/**
	 * 判断是否有正在还款的原始债权人代收
	 * @param projectId
	 * @param transactionInterestIds
	 * @param type
	 * @return
	 */
	public HostingCollectTrade selectHostingCollectTradeForLender(@Param("sourceId")Long sourceId,
			@Param("transactionInterestIds")String transactionInterestIds, @Param("type")int type);

	public HostingCollectTrade getByIdForLock(@Param("id") Long id);

	public HostingCollectTrade selectHostingCollectTradeForLeaseBonus(@Param("sourceId")Long sourceId,
			@Param("projectId")Long projectId, @Param("type")int type);

	
	/**
	 * 查询类型为用户投资、交易状态是待支付或完成的代收交易
	 * @param sourceId
	 * @param type
	 * @return
	 */
	public HostingCollectTrade selectWaitPayOrFinishedHostingCollectTrade(@Param("sourceId")Long sourceId, @Param("type")int type);
	
	/**
	 * 代收查询
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	Page<HostingCollectTrade> findByPage(Page<HostingCollectTrade> pageRequest, @Param("map") Map<String, Object> map);
	
	/**
	 * @Description:根据项目id获取等待付款的交易号
	 * @param projectId
	 * @return
	 * @author: fuyili
	 * @time:2015年11月25日 下午1:43:01
	 */
	List<HostingCollectTrade>  getWaitPayTradeNoByProjectId(@Param("projectId")Long projectId);



	/**
	 * 是否有正在还款的原始债权人代收
	 * @param sourceId
	 * @param type
	 * @return
	 */
	public HostingCollectTrade queryHostingCollectTradeForLender(@Param("sourceId")Long sourceId, @Param("type")int type);

	/**
	 * 查询原始债权人代收成功
	 * @param sourceId
	 * @param type
	 * @return
	 */
	public HostingCollectTrade queryAlreadyHostingCollectTradeForLender(@Param("sourceId")Long sourceId, @Param("type")int type,@Param("transactionInterestIds")String  transactionInterestIds);

	/**
	 * 查询需要同步的代收交易  除用户投资以外的状态
	 * @return
	 */
	public List<HostingCollectTrade> selectSynchronizedHostingCollectTradesForPayerAndPlaform();

	/**
	 * 查询需要同步的还本付息 的代收交易
	 * @return
	 */
	public List<HostingCollectTrade> selectSynchronizedHostingCollectTradesForPayerAndReplayment();


	/**
	 * 查询代收交易
	 * @return
	 */
	public List<HostingCollectTrade> selectHostingCollectTradesBySourceIdAndTypes(@Param("sourceId")Long sourceId,@Param("types")List<Integer> types);

	/**
	 * @Description:根据项目id和类型查询代收记录
	 * @param projectId
	 * @param type
	 * @return
	 * @author: fuyili
	 * @time:2016年2月4日 下午2:53:15
	 */
	public List<HostingCollectTrade> selectHostCollectByProjectIdAndType(@Param("projectId")Long projectId,@Param("type")Integer type);

	/**
	 * 查询平台的代收是否成功
	 * @param sourceId
	 * @param type
	 * @return
	 */
	public HostingCollectTrade queryAlreadyHostingCollectTradeForPalform(@Param("sourceId")Long sourceId, @Param("type")int type,@Param("transactionInterestIds")String  transactionInterestIds);
	
	/**
	 * @Description:代收列表数据
	 * @return
	 * @author: zhanghao
	 * @time:2016年4月26日 上午11:52:38
	 */
	List<HostingCollectTrade> selectHostingCollectTradeList(@Param("map") Map<String, Object> map);
	
	/**
	 * @Description:代收列表数据数量
	 * @return
	 * @author: zhanghao
	 * @time:2016年4月26日 上午11:52:38
	 */
	int selectHostingCollectTradeListCount(@Param("map") Map<String, Object> map);
	
	
	/**
	 * 根据项目ID查询需要同步的代收交易
	 * @return
	 */
	public List<HostingCollectTrade> selectSynchronizedHostingCollectTradesByProjectId(@Param("projectId")Long projectId);

	/**
	 * 
	 * @Description:查询项目下代收冻结的代收记录
	 * @return
	 * @author: wangyanji
	 * @time:2016年5月26日 下午5:48:58
	 */
	List<HostingCollectTrade> selectPreAuthApplySuccessByProjectId(@Param("projectId") Long projectId);

	/**
	 * 根据主键和交易状态更新代收交易记录
	 * 
	 * @param record
	 * @return
	 */
	public int updateHostingCollectTradeByIdAndTradeStatus(HostingCollectTrade record);

	/**
	 * 
	 * @Description:更新委托支付状态
	 * @param hostingCollectTrade
	 * @return
	 * @author: wangyanji
	 * @time:2016年8月1日 上午11:52:55
	 */
	int updateHostingCollectTradeWithholdAuthority(HostingCollectTrade hostingCollectTrade);

	/**
	 * 
	 * @param 统计待支付订单数量
	 * @return
	 */
	public int countWaitOrderCollectTrade(@Param("projectId") Long projectId);

	/**
	 * 
	 * @Description:冻结的转让项目代收记录
	 * @param transferId
	 * @return
	 * @author: wangyanji
	 * @time:2016年9月18日 下午8:08:21
	 */
	List<HostingCollectTrade> selectPreAuthApplySuccessByTransferId(@Param("transferId") Long transferId);

	/**
	 * 根据sourceid和Type查询收益总和
	 * @param sourceid
	 * @param type
     * @return
     */
	BigDecimal queryTotalAmountBySourceIdAndType(@Param("sourceid") Long sourceid,@Param("type") Integer type);
}