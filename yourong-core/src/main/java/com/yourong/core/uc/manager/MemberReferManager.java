package com.yourong.core.uc.manager;

import java.util.List;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.mc.model.biz.ActivityForKing;
import com.yourong.core.uc.model.MemberRefer;

public interface MemberReferManager {

	/**
	 * 用户推荐
	 * @param record
	 * @return
	 * @throws ManagerException
	 */
	int insertSelective(MemberRefer record) throws ManagerException;

	/**
	 * 我是王推荐好友个数排行榜
	 * @return
	 * @throws ManagerException
	 */
	List<ActivityForKing> getRefferalCountList() throws ManagerException;
}
