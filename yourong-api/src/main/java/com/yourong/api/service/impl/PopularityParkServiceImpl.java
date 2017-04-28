package com.yourong.api.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rop.thirdparty.com.google.common.collect.Lists;

import com.yourong.api.dto.GoodsForAppDto;
import com.yourong.api.dto.PopularityParkDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.dto.ShopOrderDetilDto;
import com.yourong.api.service.BalanceService;
import com.yourong.api.service.PopularityParkService;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.enums.AttachmentEnum;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.StringUtil;
import com.yourong.core.bsc.manager.BscAttachmentManager;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.sh.manager.GoodsManager;
import com.yourong.core.sh.manager.ShopOrderManager;
import com.yourong.core.sh.model.Goods;
import com.yourong.core.sh.model.OrderMain;
import com.yourong.core.sh.model.OrderSub;
import com.yourong.core.sh.model.query.GoodsQuery;
import com.yourong.core.sh.model.query.ShopOrderForAppQuery;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.manager.MemberVipManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberVip;

@Service
public class PopularityParkServiceImpl implements PopularityParkService{
	
	@Autowired
	private MemberManager memberManager;
	
	@Autowired
	private MemberVipManager memberVipManager;
	
	@Autowired
	private BalanceService balanceService;
	
	@Autowired
	private GoodsManager goodsManager;
	
	@Autowired
	private BscAttachmentManager bscAttachmentManager;
	
	@Autowired
	private TransactionManager transactionManager;
	
	@Autowired
	private ShopOrderManager shopOrderManager;
	
	
	private Logger logger = LoggerFactory.getLogger(getClass());


	@Override
	public ResultDTO<Object> getMemberCenter(Long memberId) {
		//long a=System.currentTimeMillis();

		ResultDTO<Object> result = new ResultDTO<Object>();
		try{
			PopularityParkDto popDto = new PopularityParkDto();
			Boolean levelUpFlag =false;
			Member member = memberManager.selectByPrimaryKey(memberId);
			if (member != null) {
				MemberVip memberVip = memberVipManager
						.selectRecentMemberVipByMemberId(memberId);
				if (memberVip == null) {
					ResultCode.CUSTOM.setMsg("data is shit");
					result.setResultCode(ResultCode.CUSTOM);
					return result;
				}
				popDto.setMemberId(memberId);
				popDto.setUsername(StringUtil.isNotBlank(member.getUsername())?member.getUsername():StringUtil.maskMobileCanNull(member.getMobile()));
				popDto.setAvatars(member.getAvatars());
				popDto.setVipLevel(memberVip.getVipLevel());
				popDto.setScore(memberVip.getScore().intValue());
				popDto.setNeedSncreaseScore(memberVip.getNeedSncreaseScore());
				levelUpFlag = memberVipManager.isLevelUp(memberId);
				
				// 人气值总额
				Balance balance = balanceService.queryBalance(memberId,
						TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
				if (balance != null) {
					popDto.setPopularity(balance.getBalance().setScale(0, BigDecimal.ROUND_DOWN));
				}
			}
			popDto.setLevelUpFlag(levelUpFlag);
			result.setResult(popDto);
			//logger.info("执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");

		}catch(ManagerException e){
			logger.error("会员中心获取数据异常，member={}",memberId);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return result;
	}

	@Override
	public ResultDTO<Object> getGrowthSystem(Long memberId,Integer pageNo) {
		//long a=System.currentTimeMillis();

		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		try {
			BaseQueryParam query = new BaseQueryParam();
			query.setMemberId(memberId);
			query.setPageSize(20);
			query.setCurrentPage(pageNo);
			Page<MemberVip> memberVipPage = memberVipManager.queryMemberVipList(query);
			
			rDTO.setResult(memberVipPage);
			//logger.info("执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");

		} catch (Exception e) {
			logger.error("获取会员成长记录异常, memberId={}", memberId, e);
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDTO;
	}

	@Override
	public ResultDTO<Object> getInvestProductList(Long memberId) {
		//long a=System.currentTimeMillis();
		ResultDTO<Object> result = new ResultDTO<Object>();
		PopularityParkDto popDto = new PopularityParkDto();
		try {
		//全部
		GoodsQuery goodsQueryAll = new GoodsQuery();
		// 投资专项
		String keyInvest = RedisConstant.POPULAR_PARK_GOOD_INVEST_ALL;
		List<GoodsForAppDto> investListAllDto = Lists.newArrayList();
		if (RedisManager.isExitByObjectKey(keyInvest)) {
			//缓存存在
			investListAllDto= (List<GoodsForAppDto>) RedisManager.getObject(keyInvest);
		}else{
			//缓存不存在
			goodsQueryAll.setGoodsType(TypeEnum.GOODS_TYPE_FOR_INVEST.getType());
			List<Goods> investListAll = goodsManager.queryGoodsList(goodsQueryAll);
			 investListAllDto = BeanCopyUtil.mapList(
					investListAll, GoodsForAppDto.class);
			if(Collections3.isNotEmpty(investListAllDto)){
				//RedisForProjectClient.addBatchTransactionDetail(projectId,transactionForProjects);
				RedisManager.putObject(keyInvest, investListAllDto);
				RedisManager.expireObject(keyInvest, 60);
			}
		}
		this.getMoreInforForGood(investListAllDto,memberId);//价格根据用户变动
		popDto.setInvestmentListAll(investListAllDto);
		
		// 双节特惠
		String keyDouble = RedisConstant.POPULAR_PARK_GOOD_Double_ALL;
		List<GoodsForAppDto> doubleListAllDto = Lists.newArrayList();
		if (RedisManager.isExitByObjectKey(keyDouble)) {
			//缓存存在
			doubleListAllDto= (List<GoodsForAppDto>) RedisManager.getObject(keyDouble);
		}
		//缓存不存在
		if (Collections3.isEmpty(doubleListAllDto)) {
			goodsQueryAll.setGoodsType(TypeEnum.GOODS_TYPE_DOUBLE.getType());
			List<Goods> doubleListAll = goodsManager.queryGoodsList(goodsQueryAll);
			doubleListAllDto = BeanCopyUtil.mapList(
				doubleListAll, GoodsForAppDto.class);
			if(Collections3.isNotEmpty(doubleListAllDto)){
			//RedisForProjectClient.addBatchTransactionDetail(projectId,transactionForProjects);
			RedisManager.putObject(keyDouble, doubleListAllDto);
			RedisManager.expireObject(keyDouble, 60);
			}
		}
		this.getMoreInforForGood(doubleListAllDto,memberId);//价格根据用户变动
		popDto.setDoubleListAll(doubleListAllDto);
		
		result.setResult(popDto);
		//logger.info("执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");

		} catch (Exception e) {
			logger.error("获取投资商品全部异常, memberId={}", memberId, e);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return result;
	}
	
	
	@Override
	public ResultDTO<Object> getAllProductList(Long memberId,Integer goodsType) {
		//long a=System.currentTimeMillis();
		ResultDTO<Object> result = new ResultDTO<Object>();
		PopularityParkDto popDto = new PopularityParkDto();
		try {
		//全部
		GoodsQuery goodsQueryAll = new GoodsQuery();
		// 商品类型rediskey
		String keyInvest = RedisConstant.POPULAR_PARK_GOOD_ALL+RedisConstant.REDIS_SEPERATOR+goodsType;
		List<GoodsForAppDto> listAllDto = Lists.newArrayList();
		if (RedisManager.isExitByObjectKey(keyInvest)) {
			//缓存存在
			listAllDto= (List<GoodsForAppDto>) RedisManager.getObject(keyInvest);
		}else{
			//缓存不存在
			goodsQueryAll.setGoodsType(goodsType);
			List<Goods> investListAll = goodsManager.queryGoodsList(goodsQueryAll);
			listAllDto = BeanCopyUtil.mapList(
					investListAll, GoodsForAppDto.class);
			if(Collections3.isNotEmpty(listAllDto)){
				//RedisForProjectClient.addBatchTransactionDetail(projectId,transactionForProjects);
				RedisManager.putObject(keyInvest, listAllDto);
				RedisManager.expireObject(keyInvest, 60);
			}
		}
		this.getMoreInforForGood(listAllDto,memberId);//价格根据用户变动
		popDto.setGoodsListAll(listAllDto);
		result.setResult(popDto);
		//logger.info("执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");
		} catch (Exception e) {
			logger.error("获取全部商品异常, memberId={}，goodsType={}", memberId,goodsType, e);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return result;
	}

	
	private void getMoreInforForGood(List<GoodsForAppDto> goodsList,Long memberId)
			throws ManagerException {

		for (GoodsForAppDto goods : goodsList) {
			BigDecimal discountPrice = memberVipManager.getGoodValueByMemberVip(memberId, goods.getId());
			goods.setDiscountPrice(discountPrice);
			
			BscAttachment bsc = bscAttachmentManager
					.getBscAttachmentByKeyIdAndModule(goods.getId().toString(),
							AttachmentEnum.ATTACHMENT_MODULE_SHOP_GOODS.getCode());
			if (bsc == null) {
				continue;
			}
			goods.setImage(bsc.getFileUrl());
		}
	}
	
	
	@Override
	public ResultDTO<Object> getProductDetail(Long goodId, Long memberId) {
		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		try{
			//long a=System.currentTimeMillis();
			Goods goods =  goodsManager.queryGoodsById(goodId);
			GoodsForAppDto goodsForApp = BeanCopyUtil.map(goods, GoodsForAppDto.class);
			List<BscAttachment> imageList = bscAttachmentManager
					.findAttachmentsByKeyIdAndModule(goods.getId().toString(),
							AttachmentEnum.ATTACHMENT_MODULE_SHOP_GOODS.getCode(),99);
			goodsForApp.setImageList(imageList);
			goodsForApp.setInvestFlag(transactionManager.isMemberInvested(memberId));
			Balance balance = balanceService.queryBalance(memberId,
					TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
			if (balance != null) {
				goodsForApp.setPopularity(balance.getBalance().intValue());
			} 
			MemberVip memberVip =memberVipManager.selectRecentMemberVipByMemberId(memberId);
			goodsForApp.setMemberVip(memberVip.getVipLevel());
			
			BigDecimal discountPrice = memberVipManager.getGoodValueByMemberVip(memberId, goods.getId());
			goodsForApp.setDiscountPrice(discountPrice);
			
			String des = goodsForApp.getGoodsDes();//\r 空格 \n换行
			des = des.replaceAll("\\r", "#");
			des = des.replaceAll("\\n", "&");
			goodsForApp.setGoodsDes(des);
			
			
			rDTO.setResult(goodsForApp);
			//logger.info("执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");
		} catch (Exception e) {
			logger.error("获取商品详细信息查询失败, goodId={},memberId={}", goodId,memberId, e);
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDTO;
	}

	@Override
	public ResultDTO<Object> getExchangeRecord(Long memberId,Integer currentPage) {
		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		try {
			//long a=System.currentTimeMillis();
			Page<ShopOrderDetilDto> shopPage = new Page<ShopOrderDetilDto>();
			List<ShopOrderDetilDto> shopList = Lists.newArrayList();
			ShopOrderForAppQuery query = new ShopOrderForAppQuery();
			query.setMemberId(memberId);
			query.setCurrentPage(currentPage);
			query.setPageSize(10);
			
			Page<OrderMain> orderMainPage = shopOrderManager.getOrderMainPage(query);
			List<OrderMain> orderMainList = orderMainPage.getData();
			for(OrderMain orM :orderMainList){
				ShopOrderDetilDto shOrD = new ShopOrderDetilDto();
				List<OrderSub> orderSubList = shopOrderManager.queryOrderSubListByOrderMainId(orM.getId());
				OrderSub orderSub = orderSubList.get(0);
				Goods goods = goodsManager.queryGoodsById(orderSub.getGoodsId());
				
				BscAttachment bsc = bscAttachmentManager
						.getBscAttachmentByKeyIdAndModule(goods.getId().toString(),
								AttachmentEnum.ATTACHMENT_MODULE_SHOP_GOODS.getCode());
				if(bsc!=null){
					shOrD.setImage(bsc.getFileUrl());
				}
				shOrD.setStatus(orM.getStatus());
				shOrD.setCreateTime(orM.getCreateTime());
				shOrD.setGoodsName(goods.getGoodsName());
				shOrD.setOrderId(orM.getId());
				
				shopList.add(shOrD);
			}
			shopPage.setData(shopList);
			shopPage.setPageNo(query.getCurrentPage());
			shopPage.setiDisplayLength(query.getPageSize());
			shopPage.setiTotalDisplayRecords(orderMainPage.getiTotalDisplayRecords());
			shopPage.setiTotalRecords(orderMainPage.getiTotalRecords());
			
			rDTO.setResult(shopPage);
			//logger.info("执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");

		} catch (ManagerException e) {
			logger.error("获取购买历史记录异常, memberId={}", memberId, e);
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDTO;
	}

	@Override
	public ResultDTO<Object> getExchangeOrderInfo(Long orderMainId,
			Long memberId) {
		//long a=System.currentTimeMillis();

		ResultDTO<Object> rDTO = new ResultDTO<Object>();
		try {
			ShopOrderDetilDto shopOrderDetail = new ShopOrderDetilDto();
			OrderMain orM = shopOrderManager.selectByPrimaryKey(orderMainId);
			if(orM.getMemberId().longValue()!=memberId.longValue()) {
				rDTO.setResultCode(ResultCode.ORDER_NOT_BELONG_TO_MEMBER_ERROR);
				return rDTO;
			}
			List<OrderSub> orderSubList = shopOrderManager.queryOrderSubListByOrderMainId(orM.getId());
			OrderSub orderSub = orderSubList.get(0);
			Goods goods = goodsManager.queryGoodsById(orderSub.getGoodsId());
			
			shopOrderDetail.setOrderId(orM.getId());
			shopOrderDetail.setAmount(orM.getTotalAmount().toString());
			shopOrderDetail.setStatus(orM.getStatus());
			
			String address = orM.getAddress();//\r 空格 \n换行
			if(StringUtil.isNotBlank(address)){
				address = address.replaceAll("\\r", "#");
				address = address.replaceAll("\\s*", "#"); 
				address = address.replaceAll(" +", "#");//中文空格
				address = address.replaceAll(" +", "#");//英文空格
				address = address.replaceAll(" +", "#");
				address = address.replaceAll("\\n", "&");
				address = address.replaceAll("\'", "^");
				address = address.replaceAll("\"", "\\$");
				address = address.replaceAll(":", "冒号");
			}
			shopOrderDetail.setAddress(orM.getAreaFullName() + address);
			shopOrderDetail.setAreaFullName(orM.getAreaFullName());
			shopOrderDetail.setReceiver(orM.getReceiver());
			shopOrderDetail.setMobile(orM.getMobile());

			String sendRemark = orM.getSendRemark();//\r 空格 \n换行
			if(goods.getGoodsType()==TypeEnum.GOODS_TYPE_VIRTUAL_CARD.getType()
					&&goods.getRechargeType()==TypeEnum.GOODS_RECHARGE_TYPE_RECHARGE.getType()){
				sendRemark = orM.getRemark();//充话费的备注如此处理显示
			}
			
			if(StringUtil.isNotBlank(sendRemark)){
				sendRemark = sendRemark.replaceAll("\\r", "#");
				sendRemark = sendRemark.replaceAll("\\n", "&");
			}
			shopOrderDetail.setSendRemark(sendRemark);
			
			shopOrderDetail.setGoodsName(goods.getGoodsName());
			shopOrderDetail.setOrderType(orderSub.getOrderType());
			shopOrderDetail.setCreateTime(orderSub.getCreateTime());
			shopOrderDetail.setRechargeType(orderSub.getRechargeType());
			shopOrderDetail.setRechargeCard(orderSub.getRechargeCard());
			
			List<BscAttachment> imageList = bscAttachmentManager
					.findAttachmentsByKeyIdAndModule(goods.getId().toString(),
							AttachmentEnum.ATTACHMENT_MODULE_SHOP_GOODS.getCode(),99);
			shopOrderDetail.setImageList(imageList);
			
			rDTO.setResult(shopOrderDetail);
			//logger.info("执行耗时 : "+(System.currentTimeMillis()-a)/1000f+" 秒 ");

		} catch (ManagerException e) {
			logger.error("获取商城订单详情异常, orderMainId={}", orderMainId, e);
			rDTO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDTO;
	}
	
}
