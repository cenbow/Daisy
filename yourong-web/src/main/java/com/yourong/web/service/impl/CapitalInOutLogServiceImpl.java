package com.yourong.web.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import antlr.StringUtils;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.SerialNumberUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.fin.model.CapitalInOutLog;
import com.yourong.core.fin.model.biz.BonusBiz;
import com.yourong.core.fin.model.biz.CapitalInOutForMemberCenter;
import com.yourong.core.fin.model.biz.MemberSavingPotBiz;
import com.yourong.core.fin.model.query.CapitalInOutLogQuery;
import com.yourong.core.fin.model.query.CapitalQuery;
import com.yourong.core.uc.dao.MemberMapper;
import com.yourong.core.uc.model.Member;
import com.yourong.web.service.BaseService;
import com.yourong.web.service.CapitalInOutLogService;
@Service
public class CapitalInOutLogServiceImpl extends BaseService implements  CapitalInOutLogService {

	@Autowired
	private CapitalInOutLogManager capitalInOutLogManager;
	@Autowired
	private BalanceManager balanceManager;
	@Autowired
	private MemberMapper memberMapper;

//	@Override
//	public int insert(CapitalInOutLog record) {		
//		try{
//			return capitalInOutLogManager.insert(record);
//		}catch(ManagerException e){
//			logger.error("插入资金记录出现异常", e);
//		}
//		return 0 ;
//	}

	@Override
	public List<CapitalInOutLog> selectEaring(Long memberId,
			int payAccountType, int type, int length) {
		try{
			return capitalInOutLogManager.selectEaring(memberId, payAccountType, type, length);
		}catch(ManagerException e){
			logger.error("插入资金记录出现异常", e);
		}
		return null;
	}

	@Override
	public ResultDO<MemberSavingPotBiz> getMemberSavingPotBiz(Long memberId) {
		ResultDO<MemberSavingPotBiz> result = new ResultDO<MemberSavingPotBiz>();
		try{
			MemberSavingPotBiz memberSavingPotBiz = new MemberSavingPotBiz();
			memberSavingPotBiz.setMemberId(memberId);
			memberSavingPotBiz.setSinaMemberId(SerialNumberUtil.generateIdentityId(memberId));
			//查询用户姓名、身份证
			Member member = memberMapper.selectByPrimaryKey(memberId);
			if(member!=null){
				if(member.getTrueName()!=null){
					memberSavingPotBiz.setTruename(member.getTrueName());
				}
				if(member.getIdentityNumber()!=null){
					memberSavingPotBiz.setIdentityNumber(StringUtil.maskIdentityNumber(member.getIdentityNumber()));
				}
			}
			//设置存钱罐余额
			Balance balance = balanceManager.queryBalance(memberId, TypeEnum.BALANCE_TYPE_PIGGY);
			if(balance==null) {
				result.setResultCode(ResultCode.BALANCE_MEMBER_PIGGY_BALANCE_NOT_EXISTS);
				return result;
			}
			memberSavingPotBiz.setSavingPotBalance(balance.getBalance());
			memberSavingPotBiz.setSavingPotavailableBalance(balance.getAvailableBalance());
			//设置总收益
			Balance bonusBalance = balanceManager.queryBalance(memberId, TypeEnum.BALANCE_TYPE_MEMBER_TOTAL_PIGGY);
			memberSavingPotBiz.setTotalBonus(bonusBalance.getBalance());
			CapitalInOutLogQuery query = new CapitalInOutLogQuery();
			query.setMemberId(memberId);
			query.setType(TypeEnum.FINCAPITALINOUT_TYPE_THIRDPAY.getType());
			Date endDate = DateUtils.formatDate(DateUtils.getCurrentDate(),DateUtils.DATE_FMT_3);
			query.setStartTime(DateUtils.addDate(endDate, -7));
			query.setEndTime(endDate);
			//最近7天收益
			List<BonusBiz> lastWeekBonusLists = capitalInOutLogManager.selectBonusByQuery(query);
			lastWeekBonusLists = getPercent(lastWeekBonusLists);
			memberSavingPotBiz.setLastWeekBonusLists(lastWeekBonusLists);
			query.setStartTime(DateUtils.addDate(endDate, -30));
			//最近30天收益
			List<BonusBiz> lastMonthBonusLists = capitalInOutLogManager.selectBonusByQuery(query);
			lastMonthBonusLists = getPercent(lastMonthBonusLists);
			memberSavingPotBiz.setLastMonthBonusLists(lastMonthBonusLists);
			
			memberSavingPotBiz.setLastWeekBonus(getTotalBonus(lastWeekBonusLists));
			memberSavingPotBiz.setLastMonthBonus(getTotalBonus(lastMonthBonusLists));
			memberSavingPotBiz.setYesterdayBonus(getYesterdayBonus(lastWeekBonusLists));
			result.setResult(memberSavingPotBiz);
			return result;
		}catch(ManagerException e){
			logger.error("获取我的存钱罐信息出现异常,memberId="+memberId, e);
		}
		return null;
	}

	/**
	 * 获取昨日收益
	 * @param lastWeekBonusLists
	 * @return
	 */
	private BigDecimal getYesterdayBonus(List<BonusBiz> lastWeekBonusLists) {
		if(Collections3.isNotEmpty(lastWeekBonusLists)) {
			for (BonusBiz bonusBiz : lastWeekBonusLists) {
				if(DateUtils.getStrFromDate(bonusBiz.getBonusDate(),DateUtils.DATE_FMT_3)
					.equals(DateUtils.getStrFromDate(DateUtils.getYesterDay(), DateUtils.DATE_FMT_3))) {
					return bonusBiz.getBonusAmount();
				}
			}
		}
		return BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * 设置七日收益的百分比(以七日中收益最大的为100%)
	 * @param lastWeekBonusLists
	 * @return
	 */
	private List<BonusBiz> getPercent(List<BonusBiz> lastWeekBonusLists){
		if(Collections3.isNotEmpty(lastWeekBonusLists)) {
			BonusBiz maxBonusBiz =null;
			for (BonusBiz bonusBiz : lastWeekBonusLists) {
				if(maxBonusBiz==null){
					maxBonusBiz = bonusBiz;
					continue;
				}
				if(maxBonusBiz.getBonusAmount().compareTo(bonusBiz.getBonusAmount())<0){
					maxBonusBiz = bonusBiz;
				}
			}
			for (BonusBiz bonusBiz : lastWeekBonusLists) {
				BigDecimal percent = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
				if(maxBonusBiz.getBonusAmount().compareTo(BigDecimal.ZERO)>0){
					percent = bonusBiz.getBonusAmount().divide(maxBonusBiz.getBonusAmount(),2, BigDecimal.ROUND_HALF_UP);
				}
				bonusBiz.setPercent(percent);
			}
		}
		return lastWeekBonusLists;
	}

	private BigDecimal getTotalBonus(List<BonusBiz> bonusLists) {
		BigDecimal totalBonus = BigDecimal.ZERO;
		if(Collections3.isNotEmpty(bonusLists)) {
			for (BonusBiz bonusBiz : bonusLists) {
				totalBonus = totalBonus.add(bonusBiz.getBonusAmount());
			}
		}
		return totalBonus.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	@Override
	public Page<CapitalInOutForMemberCenter> findCapitalPageForAcountCenter(CapitalQuery capitalQuery) {
		try {
			Page<CapitalInOutForMemberCenter> capitalPage = capitalInOutLogManager.findByPageForAccountCenter(capitalQuery);
			return capitalPage;
		} catch (ManagerException e) {
			logger.error("账户中心资金流水分页数据获取失败，capitalQuery:"+capitalQuery,e);
		}
		return null;
	}

	@Override
	public List<CapitalInOutLog> getTotalTransferAmountForMemberTransfer(
			CapitalInOutLogQuery query) throws ManagerException {
		try {
			return capitalInOutLogManager.getTotalTransferAmountForMemberTransfer(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	

}
