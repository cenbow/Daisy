/**
 * 
 */
package com.yourong.backend.tc.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.tc.service.ContractSignService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.tc.manager.ContractCoreManager;
import com.yourong.core.tc.manager.ContractSignManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.ContractSignDto;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年7月22日下午4:34:33
 */
@Service
public class ContractSignServiceImpl  implements ContractSignService{

	private static Logger logger = LoggerFactory.getLogger(ContractSignServiceImpl.class);
	@Autowired
	private ContractSignManager contractSignManager;
	
	@Autowired
	private ContractCoreManager contractCoreManager;
	
	@Autowired 
	private TransactionManager transactionManager;
	
	@Autowired
	private MemberManager memberManager;
	
	
	/**
	 * 合同查询
	 * 
	 * @author zhanghao
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	public Page<ContractSignDto> findByPage(Page<ContractSignDto> pageRequest, Map<String, Object> map){
		try {
			return contractSignManager.findByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("合同签署数据查询出错", e);
		}
		return null;
	}
	
	
	@Override
	public ResultDO<Object> contractReInit(Long transactionId) throws ManagerException {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try {
			//删除问题数据
			contractSignManager.deletByTransactionId(transactionId);
			
			rDO = contractCoreManager.preSign(transactionId, "backend");
			
			//甲方自动签名
			ResultDO<Object> firstRDO =contractCoreManager.autoSignFirst(transactionId);
			if(!firstRDO.isSuccess()){
				
			}
			//第三方自动签名
			ResultDO<Object> thirdRDO =contractCoreManager.autoSignThird(transactionId);
			if(!thirdRDO.isSuccess()){
				
			}
			//初始化成功，修改交易状态
			Transaction tra = new Transaction();
			tra.setId(transactionId);
			tra.setSignStatus(StatusEnum.CONTRACT_STATUS_UNSIGN.getStatus());
			transactionManager.updateByPrimaryKeySelective(tra);
			
			Transaction transaction = transactionManager.selectTransactionById(transactionId);
			
			//用户选择自动签名时，乙方自动签名
			Member  member = memberManager.selectByPrimaryKey(transaction.getMemberId());
			if(member.getSignWay()==1){
				//自动签署，直接改为已签署
				tra.setId(transactionId);
				tra.setSignStatus(StatusEnum.CONTRACT_STATUS_ALREADY_SIGN.getStatus());
				transactionManager.updateByPrimaryKeySelective(tra);
				//调用自动签署
				contractCoreManager.autoSignSecond(transactionId);
			}
			
		} catch (Exception e) {
			logger.error("合同重新初始化异常，transactionId={}",transactionId, e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}
	
	@Override
	public ResultDO<Object> contractHistoryInit() throws ManagerException {
		ResultDO<Object> rDO = new ResultDO<Object>();
		try {
			//历史过期数据置为已过期
			int num = transactionManager.expireHistoryContract();
			logger.info("历史过期数据置为已过期，num={}",num);
			List<Transaction> tranLis= transactionManager.getUnendTransaction();
			
			int succNum =0 ;
			int failNum = 0 ;
			for(Transaction transaction : tranLis){
				rDO = contractCoreManager.preSign(transaction.getId(), "backend");
				//甲方自动签名
				ResultDO<Object> firstRDO =contractCoreManager.autoSignFirst(transaction.getId());
				if(!firstRDO.isSuccess()){
					
				}
				//第三方自动签名
				ResultDO<Object> thirdRDO =contractCoreManager.autoSignThird(transaction.getId());
				if(!thirdRDO.isSuccess()){
					
				}
				//初始化成功，修改交易状态
				Transaction tra = new Transaction();
				tra.setId(transaction.getId());
				tra.setSignStatus(StatusEnum.CONTRACT_STATUS_UNSIGN.getStatus());
				transactionManager.updateByPrimaryKeySelective(tra);
				
				if(rDO.isSuccess()&&firstRDO.isSuccess()&&thirdRDO.isSuccess()){
					succNum++;
				}else{
					failNum++;
				}
			}
			String result = "成功处理:"+succNum+"个,发生异常:"+failNum+"个";
			rDO.setResult(result);
		} catch (Exception e) {
			logger.error("合同历史数据初始化异常", e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}
	
	@Override
	public ResultDO<Object> queryContractInfo(Long transactionId){
			
		ResultDO<Object> result =contractCoreManager.queryContractInfo(transactionId);
			
		return result;
	}
	
	@Override
	public ResultDO<Object> autoSignSecond(Long transactionId){
			
		ResultDO<Object> result =contractCoreManager.autoSignSecond(transactionId);
		
		Transaction tra = new Transaction();
		tra.setId(transactionId);
		tra.setSignStatus(StatusEnum.CONTRACT_STATUS_ALREADY_SIGN.getStatus());
		try {
			transactionManager.updateByPrimaryKeySelective(tra);
		} catch (ManagerException e) {
			logger.error("异常处理：自动签署乙方，签署成功，更新交易表合同签署状态异常,tra={}" , tra,e);
		}
			
		return result;
	}
	
}
