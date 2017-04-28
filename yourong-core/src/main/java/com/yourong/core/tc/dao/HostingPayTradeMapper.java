package com.yourong.core.tc.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.pageable.Page;
import com.yourong.core.tc.model.HostingPayProjectMember;
import com.yourong.core.tc.model.HostingPayTrade;

public interface HostingPayTradeMapper {

	/**
	 * 插入代付交易
	 * @param record
	 * @return
	 */
    int insertSelective(HostingPayTrade record);
    /**
     * 批量插入代付交易
     * @param record
     * @return
     */
    int batchInsertHostingPayTrade(@Param("hostingPayTrade")List<HostingPayTrade> hostingPayTrade);

    /**
     * 通过id查询代付交易
     * @param id
     * @return
     */
    HostingPayTrade selectByPrimaryKey(Long id);

    /**
     * 更新代付交易
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(HostingPayTrade record);

	/**
	 * 通过参数查询代付交易
	 * @param params
	 * @return
	 */
	List<HostingPayTrade> selectHostingPayTradesByParams(Map<String, Object> params);

	/**
	 * 查询需要同步的代付交易记录
	 */
	List<HostingPayTrade> selectSynchronizedHostingPayTrades();

	/**
	 * 查询是否有本息代付记录
	 * @param sourceId
	 * @param type
	 * @param transactionId 
	 * @return
	 */
	HostingPayTrade selectHostPayTradeForPayInterestAndPrincipal(@Param("sourceId")Long sourceId,
			@Param("type")int type);
	
	HostingPayTrade selectHostPayTradeForPayLeaseBonus(@Param("sourceId")Long sourceId,
			@Param("type")int type, @Param("transactionId")Long transactionId);

	HostingPayTrade getByIdForLock(@Param("id")Long id);

	int countHostPayBySourceIdAndType(@Param("sourceId")Long sourceId, @Param("type")int type, @Param("tradeStatus")String tradeStatus);


	/**
	 * 代付查询
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	Page<HostingPayTrade> findByPage(Page<HostingPayTrade> pageRequest, @Param("map") Map<String, Object> map);

	List<HostingPayProjectMember> selectProjectInverstAndHostingPayTrade(@Param("projectId")Long projectId);

	/**
	 * @Description:根据批付号和状态查询批付
	 * @param batchPayNo
	 * @param tradeStatus
	 * @return
	 * @author: fuyili
	 * @time:2016年1月29日 下午4:08:53
	 */
	int countHostPayByBatchNoAndStatus(@Param("batchPayNo")String batchPayNo, @Param("tradeStatus")String tradeStatus );
	
	/**
	 * @Description:更新代付交易的状态
	 * @param afterTradeStatus
	 * @param beforeTradeStatus
	 * @param outTradeNo
	 * @param id
	 * @return
	 * @author: fuyili
	 * @time:2016年3月15日 下午4:20:38
	 */
	int updateHostingPayTradeStatus(@Param("afterTradeStatus") String afterTradeStatus,
			@Param("beforeTradeStatus") String beforeTradeStatus, @Param("outTradeNo") String outTradeNo, @Param("id") Long id);

	/**
	 * @Description:代付列表数据
	 * @return
	 * @author: zhanghao
	 * @time:2016年4月26日 上午11:52:38
	 */
	List<HostingPayTrade> selectHostingPayTradeList(@Param("map") Map<String, Object> map);
	
	/**
	 * @Description:代付列表数据数量
	 * @return
	 * @author: zhanghao
	 * @time:2016年4月26日 上午11:52:38
	 */
	int selectHostingPayTradeListCount(@Param("map") Map<String, Object> map);
	
	/**
	 * @Description:根据批付号和状态查询批付
	 * @param batchPayNo
	 * @param tradeStatus
	 * @return
	 * @author: fuyili
	 * @time:2016年6月17日 下午2:49:50
	 */
	List<HostingPayTrade> selectHostPayByBatchNoAndStatus(@Param("batchPayNo")String batchPayNo, @Param("tradeStatus")String tradeStatus);
	
	/**
	 * 通过项目id和状态查询批次号
	 * @param projectId
	 * @param status
	 * @return
	 */
	List<String> selectBatchPayNosByProject(@Param("projectId")Long projectId, @Param("status")String status, @Param("type")Integer type);

	/**
	 * 
	 * @Description:查询会员转让总收款
	 * @param payeeId
	 * @return
	 * @author: wangyanji
	 * @time:2017年1月3日
	 */
	Map getTotalTransferGetAmount(@Param("payeeId") Long payeeId);

	/**
	 * 
	 * @Description:查询会员转让总支付手续费
	 * @param payeeId
	 * @return
	 * @author: wangyanji
	 * @time:2017年1月3日
	 */
	Map getTotalTransferFeeAmount(@Param("payeeId") Long payeeId);

	/**
	 * 
	 * @Description:查询会员转让总收款
	 * @param payeeId
	 * @return
	 * @author: wangyanji
	 * @time:2017年1月3日
	 */
	List<HostingPayTrade> getTotalTransferGetList(@Param("payeeId") Long payeeId);

	/**
	 * 
	 * @Description:查询会员转让总支付手续费
	 * @param payeeId
	 * @return
	 * @author: wangyanji
	 * @time:2017年1月3日
	 */
	List<HostingPayTrade> getTotalTransferFeeList(@Param("payeeId") Long payeeId);
	
	BigDecimal totalMemberPayAmountByType(@Param("payeeId")Long payeeId, @Param("type")int type, @Param("tradeStatus")String tradeStatus);
}