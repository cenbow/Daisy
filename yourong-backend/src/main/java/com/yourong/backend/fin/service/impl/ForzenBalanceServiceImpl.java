package com.yourong.backend.fin.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.fin.service.ForzenBalanceService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.SinaPayEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.member.domain.dto.QueryCtrlResultDto;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.web.BaseService;
import com.yourong.core.fin.dao.BalanceForzenMapper;
import com.yourong.core.fin.dao.BalanceUnforzenMapper;
import com.yourong.core.fin.manager.BalanceForzenManager;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.BalanceUnForzenManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.BalanceForzen;
import com.yourong.core.fin.model.BalanceUnforzen;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;

/**
 * 冻结资金余额
 * @author Administrator
 *
 */
@Service	
public class ForzenBalanceServiceImpl extends BaseService implements ForzenBalanceService{
	
	private static Logger logger = LoggerFactory.getLogger(ForzenBalanceServiceImpl.class);
	
	@Autowired
	private BalanceForzenManager balanceForzenManager;
	
	@Autowired
	private BalanceForzenMapper balanceForzenMapper;
	
	@Autowired
	private BalanceUnforzenMapper balanceUnforzenMapper;
	
	@Autowired
	private BalanceUnForzenManager balanceUnForzenManager;
	
	@Autowired
	private BalanceManager balanceManager;
	
	@Autowired
    private SinaPayClient sinaPayClient;
	
	@Autowired
	private SysDictManager sysDictManager;
	
	@Override
	public int saveForzen(BalanceForzen balanceFrozen) {
		try {
			return balanceForzenManager.insertSelective(balanceFrozen);
		} catch (ManagerException e) {
			logger.error("保存冻结余额记录失败", e);
		}
		return 0;
	}
	/**
	 * 
	 * @desc 冻结余额
	 * @param balanceFrozen
	 * @return
	 * @author chaisen
	 * @throws Exception 
	 * @time 2016年7月29日 下午5:42:29
	 *
	 */
	@Override
	public ResultDO<BalanceForzen> frozenBalance(BalanceForzen balanceFrozen) throws Exception {
		ResultDO<BalanceForzen> retDO = new ResultDO<BalanceForzen>();
		try {
			//校验
			if(balanceFrozen.getForzenReason()==null||balanceFrozen.getType()<1||balanceFrozen.getAmount().compareTo(BigDecimal.ZERO)<0.01){
				retDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return retDO;
			}
			//同步存钱罐余额
			Balance balance = new Balance();
			try {
				balance = balanceManager.synchronizedBalance(balanceFrozen.getMemberId(),
						TypeEnum.BALANCE_TYPE_PIGGY);
			} catch (Exception e) {
				logger.error("【冻结存钱罐余额】查询第三方余额失败,memberId={}",balanceFrozen.getMemberId());
			}
			if(balance==null||balance.getAvailableBalance()==null){
				retDO.setResultCode(ResultCode.BALANCE_MEMBER_PIGGY_BALANCE_LACKING);
				return retDO;
			}
			if(balanceFrozen.getAmount().compareTo(balance.getAvailableBalance())>0){
				retDO.setResultCode(ResultCode.BALANCE_MEMBER_PIGGY_BALANCE_LACKING);
				return retDO;
			}
			String ip = null;
			SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
			if (dict != null) {
				ip = dict.getValue();
			}
			//插入本地冻结记录
			balanceFrozen.setForzenNo(SerialNumberUtil.generateFreezeOutCtrlNo());
			balanceFrozen.setForzenTime(DateUtils.getCurrentDate());
			balanceFrozen.setCreateTime(DateUtils.getCurrentDate());
			balanceFrozen.setUpdateTime(DateUtils.getCurrentDate());
			//balanceFrozen.setBalance(balanceFrozen.getAmount());
			balanceFrozen.setClientIp(ip);
			balanceFrozen.setForzenStatus(SinaPayEnum.FORZEN_PROCESSING.getCode());
			int i=balanceForzenManager.insertSelective(balanceFrozen);
			if(i>0){
				BalanceForzen biz=new BalanceForzen();
				//调用新浪冻结接口
				ResultDto<?> sinaResult=sinaPayClient.freezeBalance(balanceFrozen.getMemberId(),  balanceFrozen.getForzenNo(), balanceFrozen.getAmount(), Integer.toString(balanceFrozen.getType()),ip);
	    		if(sinaResult.isError()){
	    			logger.info("冻结余额结果{}失败,冻结订单号：", balanceFrozen.getForzenNo(),sinaResult.getErrorMsg());
	    			biz.setId(balanceFrozen.getId());
	    			biz.setRemarks(sinaResult.getErrorMsg());
	    			biz.setForzenStatus(SinaPayEnum.FORZEN_FAILED.getCode());
	    			balanceForzenManager.updateByPrimaryKeySelective(biz);
	    			retDO.setResult(biz);
	    			retDO.setSuccess(false);
					return retDO;
	    		}
				//查询冻结结果
	    		ResultDto<QueryCtrlResultDto>queryResult=sinaPayClient.queryCtrlResult(balanceFrozen.getForzenNo());
	    		if (queryResult == null || queryResult.getModule() == null ) {
					logger.info("查询冻结结果{}失败，返回结果为空", balanceFrozen.getForzenNo());
					if(sinaResult.isSuccess()){
						retDO.setSuccess(true);
						return retDO;
					}
				}
	    		if(queryResult.isSuccess()){
	    			QueryCtrlResultDto queryTradeResult = queryResult.getModule();
	    			logger.info("查询冻结订单号：" + balanceFrozen.getForzenNo() + "状态为：" + queryTradeResult.getCtrlStatus());
	    			//更新冻结结果状态
	    			biz.setId(balanceFrozen.getId());
	    			biz.setCreateTime(DateUtils.getCurrentDate());
	    			if(queryTradeResult.getCtrlStatus()!=null){
	    				if((queryTradeResult.getCtrlStatus().equals(SinaPayEnum.FORZEN_SUCCESS.getCode()))){
	    					biz.setBalance(balanceFrozen.getAmount());
	    				}
	    				biz.setForzenStatus(queryTradeResult.getCtrlStatus());
		    			biz.setRemarks(queryTradeResult.getErrorMsg());
		    			balanceForzenManager.updateByPrimaryKeySelective(biz);
	    			}
	    			try {
	    				balanceManager.synchronizedBalance(balanceFrozen.getMemberId(),
	    						TypeEnum.BALANCE_TYPE_PIGGY);
	    			} catch (Exception e) {
	    				logger.error("【冻结存钱罐余额】同步第三方余额失败,memberId={}",balanceFrozen.getMemberId());
	    			}
	    			retDO.setSuccess(true);
	    		}
			}
    		
		} catch (ManagerException e) {
			retDO.setSuccess(false);
			logger.error("冻结余额失败", e);
		}
		return retDO;
	}
	/**
	 * 
	 * @desc 解冻余额
	 * @param unforzen
	 * @return
	 * @author chaisen
	 * @throws Exception 
	 * @time 2016年8月1日 上午9:27:29
	 *
	 */
	@Override
	public ResultDO<BalanceUnforzen> unforzenBalance(BalanceUnforzen unforzen) throws Exception {
		ResultDO<BalanceUnforzen> retDO = new ResultDO<BalanceUnforzen>();
		try {
			//校验
			if(unforzen.getUnforzenReason()==null||unforzen.getAmount().compareTo(BigDecimal.ZERO)<0.01){
				retDO.setResultCode(ResultCode.ERROR_SYSTEM_PARAM_CANNOT_EMPTY);
				return retDO;
			}
			//冻结订单号是否存在
			BalanceForzen forzen=balanceForzenManager.selectBalanceForzenByForzenNo(unforzen.getForzenNo());
			if(forzen==null){
				retDO.setResultCode(ResultCode.FORZEN_BALANCE_NO_EXIST_ERROR);
				return retDO;
			}
			if(forzen.getBalance()==null){
				retDO.setResultCode(ResultCode.UNFORZEN_BALANCE_GREATHER_THAN_FORZEN_ERROR);
				return retDO;
			}
			//解冻金额不能大于剩余冻结金额
			if(unforzen.getAmount().compareTo(forzen.getBalance())>0){
				retDO.setResultCode(ResultCode.UNFORZEN_BALANCE_GREATHER_THAN_FORZEN_ERROR);
				return retDO;
			}
			String ip = null;
			SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
			if (dict != null) {
				ip = dict.getValue();
			}
			//插入本地解冻记录
			unforzen.setUnforzenNo(SerialNumberUtil.generateUnFreezeOutCtrlNo());
			unforzen.setCreateTime(DateUtils.getCurrentDate());
			unforzen.setUnforzenTime(DateUtils.getCurrentDate());
			unforzen.setMemberId(forzen.getMemberId());
			unforzen.setUpdateTime(DateUtils.getCurrentDate());
			unforzen.setUnforzenStatus(SinaPayEnum.FORZEN_PROCESSING.getCode());
			unforzen.setClientIp(ip);
			int i=balanceUnForzenManager.insertSelective(unforzen);
			if(i>0){
				//更新剩余冻结金额
    			BalanceForzen balanceBiz=new BalanceForzen();
    			BigDecimal balance=forzen.getBalance().subtract(unforzen.getAmount());
    			balanceBiz.setId(forzen.getId());
    			balanceBiz.setBalance(balance);
    			balanceForzenManager.updateByPrimaryKeySelective(balanceBiz);
				BalanceUnforzen biz=new BalanceUnforzen();
				//调用新浪解冻接口
				ResultDto<?> sinaResult=sinaPayClient.unfreezeBalance(unforzen.getMemberId(),  unforzen.getForzenNo(),unforzen.getUnforzenNo(), unforzen.getAmount(), unforzen.getUnforzenNo(),ip);
	    		if(sinaResult.isError()){
	    			logger.info("解冻余额结果{}失败,解冻订单号：", unforzen.getUnforzenNo(),sinaResult.getErrorMsg());
	    			biz.setId(unforzen.getId());
	    			biz.setRemarks(sinaResult.getErrorMsg());
	    			biz.setUnforzenStatus(SinaPayEnum.FORZEN_FAILED.getCode());
	    			balanceUnForzenManager.updateByPrimaryKeySelective(biz);
	    			balance=forzen.getBalance().add(unforzen.getAmount());
        			balanceBiz.setBalance(balance);
	    			balanceForzenManager.updateByPrimaryKeySelective(balanceBiz);
	    			retDO.setResult(biz);
	    			retDO.setSuccess(false);
					return retDO;
	    		}
				//查询解冻结果
	    		ResultDto<QueryCtrlResultDto>queryResult=sinaPayClient.queryCtrlResult(unforzen.getUnforzenNo());
	    		if (queryResult == null || queryResult.getModule() == null ) {
					logger.info("查询解冻结果{}失败，返回结果为空", unforzen.getUnforzenNo());
					if(sinaResult.isSuccess()){
						retDO.setSuccess(true);
						return retDO;
					}
					
				}
	    		if(queryResult.isSuccess()){
	    			QueryCtrlResultDto queryTradeResult = queryResult.getModule();
	    			logger.info("查询解冻订单号：" + unforzen.getUnforzenNo() + "状态为：" + queryTradeResult.getCtrlStatus());
	    			//更解冻结果状态
	    			biz.setId(unforzen.getId());
	    			biz.setUpdateTime(DateUtils.getCurrentDate());
	    			if(queryTradeResult.getCtrlStatus()!=null){
	    				biz.setUnforzenStatus(queryTradeResult.getCtrlStatus());
		    			balanceUnForzenManager.updateByPrimaryKeySelective(biz);
		    			if(queryTradeResult.getCtrlStatus().equals(SinaPayEnum.FORZEN_FAILED.getCode())){
		    				balance=forzen.getBalance().add(unforzen.getAmount());
		        			balanceBiz.setBalance(balance);
			    			balanceForzenManager.updateByPrimaryKeySelective(balanceBiz);
		    			}
	    			}
	    			try {
	    				balanceManager.synchronizedBalance(unforzen.getMemberId(),
	    						TypeEnum.BALANCE_TYPE_PIGGY);
	    			} catch (Exception e) {
	    				logger.error("【解冻存钱罐余额】同步第三方余额失败,memberId={}",unforzen.getMemberId());
	    			}
	    			retDO.setSuccess(true);
	    		}
			}
    		
		} catch (ManagerException e) {
			retDO.setSuccess(false);
			logger.error("解冻余额失败", e);
		}
		return retDO;
	}
	/**
	 * 
	 * @desc 分页
	 * @param pageRequest
	 * @param map
	 * @return
	 * @author chaisen
	 * @time 2016年8月1日 上午10:31:15
	 *
	 */
	@Override
	public Page<BalanceForzen> showForzenPage(Page<BalanceForzen> pageRequest,
			Map<String, Object> map) {
		try {
			return balanceForzenManager.showForzenPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("findByPage", e);
		}
		return pageRequest;
	}
	/**
	 * 
	 * @desc 解冻明细
	 * @param forzenNo
	 * @return
	 * @author chaisen
	 * @time 2016年8月1日 上午11:10:14
	 *
	 */
	@Override
	public List<BalanceUnforzen> selectUnforzenListByForzenNo(String forzenNo) {
		try {
			return balanceUnForzenManager.selectUnforzenListByForzenNo(forzenNo);
		} catch (ManagerException e) {
			logger.error("解冻明细列表失败", e);
		}
		return null;
	}
	/**
	 * 
	 * @desc 同步冻结和解冻
	 * @param forzenNo
	 * @param type
	 * @return
	 * @author chaisen
	 * @throws Exception 
	 * @time 2016年8月1日 上午11:25:59
	 *
	 */
	@Override
	public ResultDO synchronizedBalanceForzen(String forzenNo, Long type) throws Exception {
		ResultDO retDO = new ResultDO();
		try {
			//冻结订单号是否存在
			BalanceForzen	forzen = balanceForzenManager.selectBalanceForzenByForzenNo(forzenNo);
			if(forzen==null){
				retDO.setResultCode(ResultCode.FORZEN_BALANCE_NO_EXIST_ERROR);
				return retDO;
			}
			if(type==2){
				//查询解冻结果
	    		ResultDto<QueryCtrlResultDto>queryResult=sinaPayClient.queryCtrlResult(forzenNo);
	    		if (queryResult == null || queryResult.getModule() == null ) {
					logger.info("查询解冻结果{}失败，返回结果为空", forzenNo);
					retDO.setSuccess(false);
					return retDO;
				} 
	    		QueryCtrlResultDto queryTradeResult = queryResult.getModule();
    			logger.info("查询解冻订单号：" + forzenNo + "状态为：" + queryTradeResult.getCtrlStatus());
    			//更新冻结结果状态
    			BalanceForzen biz=new BalanceForzen();
    			if(queryTradeResult.getCtrlStatus()!=null){
    				if((queryTradeResult.getCtrlStatus().equals(SinaPayEnum.FORZEN_SUCCESS.getCode()))){
    					biz.setBalance(forzen.getAmount());
    				}
    			}
    			biz.setForzenNo(forzenNo);
    			biz.setCreateTime(DateUtils.getCurrentDate());
    			biz.setForzenStatus(queryTradeResult.getCtrlStatus());
    			biz.setRemarks(queryTradeResult.getErrorMsg());
    			balanceForzenMapper.updateByForzenNoSelective(biz);
    			retDO.setSuccess(true);
			}else{
				List<BalanceUnforzen> unforzenList=balanceUnforzenMapper.selectUnforzenListByForzenNoAndProcessing(forzenNo);
				if(Collections3.isEmpty(unforzenList)){
					logger.info("没有可同步的解冻订单", forzenNo);
					retDO.setSuccess(false);
					return retDO;
				}
				for(BalanceUnforzen unBiz:unforzenList){
					ResultDto<QueryCtrlResultDto>queryResult=sinaPayClient.queryCtrlResult(unBiz.getUnforzenNo());
					if (queryResult == null || queryResult.getModule() == null ) {
						logger.info("查询解冻结果{}失败，返回结果为空", forzenNo);
						retDO.setSuccess(false);
						return retDO;
					} 
		    		QueryCtrlResultDto queryTradeResult = queryResult.getModule();
	    			logger.info("查询解冻订单号：" + forzenNo + "状态为：" + queryTradeResult.getCtrlStatus());
	    			//更新冻结结果状态
	    			BalanceUnforzen biz=new BalanceUnforzen();
	    			biz.setForzenNo(forzenNo);
	    			biz.setUpdateTime(DateUtils.getCurrentDate());
	    			biz.setUnforzenStatus(queryTradeResult.getCtrlStatus());
	    			biz.setRemarks(queryTradeResult.getErrorMsg());
	    			balanceUnforzenMapper.updateByUnforzenNoSelective(biz);
	    			retDO.setSuccess(true);
				}
				
			}
		} catch (ManagerException e) {
			logger.error("同步冻结、解冻异常", e);
		}
		return retDO;
	}
	@Override
	public BalanceForzen selectByPrimayKey(Long id) throws ManagerException {
		try {
			return balanceForzenManager.selectByPrimayKey(id);
		} catch (ManagerException e) {
			logger.error("解冻明细失败", e);
		}
		return null;
	}
	
}
