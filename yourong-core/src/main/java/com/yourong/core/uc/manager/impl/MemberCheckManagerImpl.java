package com.yourong.core.uc.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.core.uc.dao.MemberCheckMapper;
import com.yourong.core.uc.manager.MemberCheckManager;
import com.yourong.core.uc.model.MemberCheck;
import com.yourong.core.uc.model.query.MemberCheckQuery;

@Component
public class MemberCheckManagerImpl implements MemberCheckManager {
	@Autowired
	private MemberCheckMapper memberCheckMapper;

	@Override
	public int insert(MemberCheck memberCheck) throws ManagerException {
		try {
			return memberCheckMapper.insertSelective(memberCheck);
		} catch (DuplicateKeyException mysqlE) { //重复签到异常抛出
			throw mysqlE;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<MemberCheck> queryMemberCheckListByQuery(MemberCheckQuery query) throws ManagerException{
		try {
			return memberCheckMapper.queryMemberCheckListByQuery(query);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public boolean isChecked(Long memberId)  throws ManagerException{
		try {
			String todayYMD = DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3);
			MemberCheck checkRes = memberCheckMapper.queryMemberCheckToday(memberId, todayYMD);
			if(checkRes == null) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int getPopularityValueForChecked(Long memberId)
			throws ManagerException {
		try {
			MemberCheckQuery query = new MemberCheckQuery();
			query.setMemberId(memberId);
			query.setStartTime(DateUtils.getCurrentDate());
			query.setEndTime(DateUtils.getCurrentDate());
			List<MemberCheck> memberCheckList = this.queryMemberCheckListByQuery(query);
			if(Collections3.isNotEmpty(memberCheckList)) {
				return memberCheckList.get(0).getGainPopularity().intValue();
			}
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return 0;
	}

	@Override
	public int countPopularityValueByMemberId(Long memberId)
			throws ManagerException {
		try {
			Integer count = memberCheckMapper.countPopularityValueByMemberId(memberId);
			if(count != null){
				return count;
			}
			return 0;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

}
