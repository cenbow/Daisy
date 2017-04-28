package com.yourong.backend.ic.service.impl;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yourong.backend.ic.service.LeaseBonusService;
import com.yourong.common.constant.Config;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.RemarksEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.SinaPayEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.pay.sina.SinaPayConfig;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.common.enums.AccountType;
import com.yourong.common.thirdparty.sinapay.common.enums.IdType;
import com.yourong.common.thirdparty.sinapay.common.enums.TradeCode;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.CreateCollectTradeResult;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.ic.manager.LeaseBonusDetailManager;
import com.yourong.core.ic.manager.LeaseBonusManager;
import com.yourong.core.ic.manager.LeaseDetailManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.LeaseBonus;
import com.yourong.core.ic.model.LeaseDetail;
import com.yourong.core.ic.model.Project;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.manager.HostingCollectTradeManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.HostingCollectTrade;

@Service
public class LeaseBonusServiceImpl implements LeaseBonusService {
	private static Logger logger = LoggerFactory.getLogger(LeaseBonusServiceImpl.class);
	@Autowired
	private LeaseBonusManager leaseBonusManager;
	@Autowired
	private LeaseBonusDetailManager leaseBonusDetailManager;
	@Autowired
	private TransactionManager myTransactionManager;
	@Autowired
	private BalanceManager balanceManager;
	@Resource
	private SinaPayClient sinaPayClient;
	@Autowired
	private HostingCollectTradeManager hostingCollectTradeManager;
	@Autowired
	private ProjectManager projectManager;
	@Autowired
	private LeaseDetailManager leaseDetailManager;
	@Autowired
	private SysDictManager sysDictManager;

	public int deleteByPrimaryKey(Long id) {
		try {
			return leaseBonusManager.deleteByPrimaryKey(id);
		} catch (Exception e) {
			logger.error("删除租赁分红失败,id=" + id, e);
		}
		return 0;
	}

	public int insert(LeaseBonus leaseBonus) {
		try {
			return leaseBonusManager.insert(leaseBonus);
		} catch (ManagerException e) {
			logger.error("插入租赁分红失败,leaseBonus=" + leaseBonus, e);
		}
		return 0;
	}

	public LeaseBonus selectByPrimaryKey(Long id) {
		try {
			return leaseBonusManager.selectByPrimaryKey(id);
		} catch (ManagerException e) {
			logger.error("查询租赁分红失败,id=" + id, e);
		}
		return null;
	}

	public int updateByPrimaryKey(LeaseBonus leaseBonus) {
		try {
			return leaseBonusManager.updateByPrimaryKey(leaseBonus);
		} catch (ManagerException e) {
			logger.error("更新租赁分红失败,leaseBonus=" + leaseBonus, e);
		}
		return 0;
	}

	public int updateByPrimaryKeySelective(LeaseBonus leaseBonus) {
		try {
			return leaseBonusManager.updateByPrimaryKeySelective(leaseBonus);
		} catch (ManagerException e) {
			logger.error("按字段更新租赁分红失败,leaseBonus=" + leaseBonus, e);
		}
		return 0;
	}

	public int batchDelete(long[] ids) {
		try {
			return leaseBonusManager.batchDelete(ids);
		} catch (ManagerException e) {
			logger.error("批量删除租赁分红失败,ids=" + ids, e);
		}
		return 0;
	}

	public Page<LeaseBonus> findByPage(Page<LeaseBonus> pageRequest, Map<String, Object> map) {
		try {
			return leaseBonusManager.findByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("分页查询租赁分红失败,pageRequest=" + pageRequest + "map=" + map, e);
		}
		return null;
	}

	@Override
	/**
	 * 1、把分红结算信息写入分红表
	 * 2、创建平台代收并且调用新浪代收接口
	 */
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public ResultDO<LeaseBonus> doLeaseBonus(LeaseDetail leaseDetail) throws Exception {
		ResultDO<LeaseBonus> result = new ResultDO<LeaseBonus>();
		try {
			LeaseBonus bonus = leaseBonusManager.selectByPrimaryKey(leaseDetail.getLeaseBonusId());
			// 判断项目是否是已还款状态
			Project project = projectManager.selectByPrimaryKey(bonus.getProjectId());
			if(project.getStatus()<StatusEnum.PROJECT_STATUS_FULL.getStatus()) {
				logger.info("项目："+ bonus.getProjectId() + ResultCode.LEASE_BONUS_PROJECT_NOT_REPAYMENT_STATUS_ERROR.getMsg());
				result.setResultCode(ResultCode.LEASE_BONUS_PROJECT_NOT_REPAYMENT_STATUS_ERROR);
				return result;
			}
			//判断有没有创建过分红代收
			if(hostingCollectTradeManager.haveHostingCollectTradeForLeaseBonus(leaseDetail.getLeaseBonusId(), bonus.getProjectId())) {
				logger.info("项目："+ bonus.getProjectId() + ResultCode.LEASE_BONUS_DUPLICATE_ERROR.getMsg());
				result.setResultCode(ResultCode.LEASE_BONUS_DUPLICATE_ERROR);
				return result;
			}
			//创建平台代付分红金额代收
			BigDecimal bonusAmount = BigDecimal.valueOf(leaseDetail.getTotalRental().doubleValue()*leaseDetail.getUserBonus().doubleValue()/100).setScale(2, BigDecimal.ROUND_HALF_UP);
			if(bonusAmount.doubleValue()<=0) {
				logger.info("项目："+ bonus.getProjectId() + ResultCode.LEASE_LEASE_BONUS_NOT_MORE_ZERO_ERROR.getMsg());
				result.setResultCode(ResultCode.LEASE_LEASE_BONUS_NOT_MORE_ZERO_ERROR);
				return result;
			}
			//判断平台余额是否足够
			Balance paltformBalance = balanceManager.queryBalanceLocked(Long.parseLong(Config.internalMemberId), TypeEnum.BALANCE_TYPE_BASIC);
			if(bonusAmount.doubleValue()>paltformBalance.getAvailableBalance().doubleValue()) {
				result.setResultCode(ResultCode.BALANCE_PALTFORM_BALANCE_LACKING);
				return result;
			}
			//将租赁分红表职位已租状态
			bonus.setLeaseStatus(StatusEnum.LEASE_BONUS_BEEN_LEASED.getStatus());
			if(bonus.getPeriods()!=null) {
				bonus.setPeriods(bonus.getPeriods()+1);
			} else {
				bonus.setPeriods(1);
			}
			if(bonus.getTotalIncome()!=null) {
				bonus.setTotalIncome(bonus.getTotalIncome().add(leaseDetail.getTotalRental()));
			} else {
				bonus.setTotalIncome(leaseDetail.getTotalRental());
			}
			
			if(leaseBonusManager.updateByPrimaryKeySelective(bonus)>0) {
				//插入租赁租赁分红租赁结算记录表
				BigDecimal unitRental = FormulaUtil.getUnitRental(leaseDetail.getTotalRental(), leaseDetail.getLeaseDays());
				leaseDetail.setRental(unitRental);
				leaseDetail.setBonusStatus(StatusEnum.LEASE_BONUS_DOING_BONUS.getStatus());
				leaseDetailManager.insertSelective(leaseDetail);
				
				String ip = null;
				SysDict dict = sysDictManager.findByGroupNameAndKey("defaultIp", "backendIp");
				if (dict != null) {
					ip = dict.getValue();
				}
				//创建平台优惠券垫付代收交易
				HostingCollectTrade couponHostingCollectTrade = insertHostingCollectTradeForLeaseBonus(bonusAmount, leaseDetail.getId(),
						bonus.getProjectId(), ip);
				if(couponHostingCollectTrade!=null) {
					//调用第三方接口完成托管代收交易
					ResultDto<CreateCollectTradeResult> collectTradeResult = sinaPayClient.createHostingCollectTrade(
							couponHostingCollectTrade.getTradeNo(), 
							TypeEnum.HOSTING_COLLECT_TRADE_LEASE_BONUS.getDesc(), // 后期修改
							SinaPayConfig.indentityEmail, 
							ip,
							AccountType.BASIC,
							couponHostingCollectTrade.getAmount(),
							IdType.EMAIL,
							TradeCode.COLLECT_FROM_PLATFORM_FOR_LEASEBONUS,
							TypeEnum.COLLECT_TRADE_TYPE_NORMAL.getCode()
							);
					// 后期需要对同步返回的状态进行一次update，同时异步也需要去处理
					if(collectTradeResult!=null && !collectTradeResult.isSuccess()) {
						throw new ManagerException(collectTradeResult.getErrorMsg());
					}
					
				}
				
				
			}
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	private HostingCollectTrade insertHostingCollectTradeForLeaseBonus(BigDecimal bonusAmount, Long leaseBounsId, Long projectId, String payerIp)
			throws ManagerException {
		try {
			//本地保存HostingCollectTrade信息
			HostingCollectTrade collectTrade = new HostingCollectTrade();
			collectTrade.setAmount(bonusAmount);
			collectTrade.setPayerId(Long.parseLong(Config.internalMemberId));
			collectTrade.setPayMethod(SinaPayEnum.PAY_TYPE_BALANCE.getCode());
			collectTrade.setRemarks(RemarksEnum.PALTFORM_COUPON_FOR_LEASE_BONUS.getRemarks()+":leaseBounsId="+leaseBounsId);
			collectTrade.setSourceId(leaseBounsId);
			collectTrade.setTradeCloseTime(Config.sinaPayDefaultOrderCloseTime);
			collectTrade.setTradeNo(SerialNumberUtil.generateCollectTradeaNo());
			collectTrade.setTradeStatus(SinaPayEnum.TRANSACTION_WAIT_PAY.getCode());
			collectTrade.setPayStatus((SinaPayEnum.PAY_PROCESSING.getCode()));
			collectTrade.setTradeTime(DateUtils.getCurrentDate());
			collectTrade.setType(TypeEnum.HOSTING_COLLECT_TRADE_LEASE_BONUS.getType());
			collectTrade.setProjectId(projectId);
			collectTrade.setPayerIp(payerIp);
			if(hostingCollectTradeManager.insertSelective(collectTrade)>0) {
				return collectTrade;
			}
			
		} catch (ManagerException e) {
			logger.error("[插入平台垫付租赁分红代收交易信息出错]：leaseBounsId=" + leaseBounsId, e);
			throw e;
		}
		return null;
	}

}