/**
 * 
 */
package com.yourong.api.service;

import java.util.List;

import com.yourong.api.dto.FindDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.common.domain.DynamicParamBuilder;
import com.yourong.common.domain.ResultDO;
import com.yourong.core.mc.model.biz.ActivityForAnniversary;
import com.yourong.core.mc.model.biz.ActivityLotteryResultBiz;
import com.yourong.core.sh.model.Area;
import com.yourong.core.sh.model.OrderDelivery;
import com.yourong.common.domain.DynamicParamBuilder;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年5月16日上午9:45:27
 */
public interface FindeService {
	
	
	public ResultDTO<FindDto> getFindPage(FindDto fingDto);
	
	public ResultDTO<FindDto> getFindPageUnlogin(FindDto fingDto);
	
	public ResultDTO<Object> getPopularityParkInfor(Long memberId);
	
	public Object anniversaryNineGrid(DynamicParamBuilder paramBuilder);
	
	public Object anniversaryNineGridResult(DynamicParamBuilder paramBuilder) ;
	
	public Object getGoodsDetailInfromations(DynamicParamBuilder paramBuilder);
	
	public Object creatGoodsOrder(DynamicParamBuilder paramBuilder) ;
	
	public Object purchaseHistory(DynamicParamBuilder paramBuilder);
	//人气值乐园部分
	
	
	public Object getQuickReward(DynamicParamBuilder paramBuilder);
	
	
	public ResultDTO<Object> quickRewardInit(Long memberId);
	
	public Object directLottery(DynamicParamBuilder paramBuilder);
	
	public Object quickRewardRefresh(DynamicParamBuilder paramBuilder);
	

	public Object getShopOrderDetail(DynamicParamBuilder paramBuilder);
	
	public Object queryMemberPopularityLogList(DynamicParamBuilder paramBuilder);
	
	public Object queryMemberVipList(DynamicParamBuilder paramBuilder);
	
	public Object memberBehavior(DynamicParamBuilder paramBuilder);
	
	public Object getGoodsList(DynamicParamBuilder paramBuilder);
	
	public List<Area> queryAreasByParentCode(Long id);
	
	public OrderDelivery queryOrderDelivery(Long memberId);
}
