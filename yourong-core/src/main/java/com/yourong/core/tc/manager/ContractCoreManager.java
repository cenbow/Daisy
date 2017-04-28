/**
 * 
 */
package com.yourong.core.tc.manager;

import com.yourong.common.domain.ResultDO;

import cn.bestsign.sdk.domain.vo.result.AutoSignbyCAResult;

/**
 * @desc 合同第三方核心业务类
 * @author zhanghao
 * 2016年7月6日上午11:53:50
 */
public interface ContractCoreManager {

	
	/**
	 * 获取手签地址
	 * 
	 * @param transactionId 
	 * @return
	 */
	public ResultDO<Object> getSignUrl (Long transactionId,int typeDevice,Integer source);
	
	/**
	 * 第三方自动签署
	 * 
	 * @param transactionId 
	 * @return
	 */
	public ResultDO<Object> autoSignThird (Long transactionId);
	
	/**
	 * 乙方自动签署
	 * 
	 * @param transactionId
	 * @return
	 */
	public ResultDO<Object> autoSignSecond(Long transactionId) ;
	
	/**
	 * 甲方自动签署
	 * 
	 * @param transactionId 
	 * @return
	 */
	public ResultDO<Object> autoSignFirst (Long transactionId);
	
	/**
	 * 初始化合同数据，自动签署甲方和有融公章
	 * 
	 * @param transactionId 
	 * @return
	 */
	public ResultDO<Object> preSign (Long transactionId,String fromSys);

	/**
	 * 用户CA认证
	 * 
	 * @param transactionId
	 * @return
	 */
	public ResultDO<Object>  memberCa (Long memberId);
	
	/**
	 * 企业CA认证
	 * 
	 * @param transactionId
	 * @return
	 */
	public ResultDO<Object> enterpriseCa (Long id);
	
	/**
	 * 图片上传
	 *
	 * @param transactionId
	 * @return
	 */
	public ResultDO<Object> uploadImage(Long id,String imgType,String filePath,String imgName);
	
	/**
	 * 获取下载地址
	 * 
	 * @param transactionId 
	 * @return
	 */
	public ResultDO<Object> getDownUrl (Long transactionId);

	/**
	 * 合同保全
	 * 
	 * @param transactionId 
	 * @return
	 */
	public String getContractPreservation(Long transactionId,String fromSys);
	
	/**
	 * 同步签署信息
	 * 
	 * @param transactionId 
	 * @return
	 */
	public ResultDO<Object> queryContractInfo(Long transactionId);
}
