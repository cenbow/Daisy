package com.yourong.web.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.PopularityInOutLog;
import com.yourong.core.fin.model.biz.OverduePopularityBiz;
import com.yourong.core.fin.model.biz.PopularityMemberBiz;
import com.yourong.core.fin.model.biz.QuadrupleGiftCount;
import com.yourong.core.fin.model.query.PopularityInOutLogQuery;
import com.yourong.core.mc.model.biz.ActivityForFirstInvest;


public interface PopularityInOutLogService {

	/**
	 * 问卷调查赠送人气值
	 */
	ResultDO<PopularityInOutLog> giveQuestionPopularity(Long memberId,String projId);
	
	/**
	 * 获取用户分别通过任务、投资、活动赚取的人气值
	 */
	PopularityMemberBiz findTotalPopByMemberId(Long memberId) ;
	
	/**
	 * 获取用户人气值流水
	 * @param query
	 * @return
	 * @throws ManagerException
	 */
	Page<PopularityInOutLog> getPopularityInOutLog(BaseQueryParam query) ;
	
	/**
	 * 获取用户获取四重礼的次数
	 * @param memberId
	 * @param remark
	 * @return
	 */
	QuadrupleGiftCount getQuadrupleGiftCountByMemberId(Long memberId);

	/**
	 * 
	 * @Description:平台五重礼每一种获取的总次数和人气值总数
	 * @return
	 * @author: fuyili
	 * @time:2015年12月11日 下午4:57:46
	 */
	QuadrupleGiftCount getQuintupleGiftCount();
	
	/**
	 * @Description:获取五重礼送人气值用户列表
	 * @param query
	 * @return
	 * @author: fuyili
	 * @time:2015年12月11日 下午5:34:04
	 */
	List<ActivityForFirstInvest> selectQuintupleGiftMemberList(PopularityInOutLogQuery query);

	/**
	 * 查询用户过期人气值
	 * @param memberid
     * @return
     */
	OverduePopularityBiz queryOverduePopularity(Long memberid);
}
