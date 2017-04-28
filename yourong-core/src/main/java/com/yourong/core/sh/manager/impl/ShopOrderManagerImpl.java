package com.yourong.core.sh.manager.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.yourong.common.util.Collections3;
import com.yourong.core.sh.dao.GoodsMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.DateUtils;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.sh.dao.OrderDeliveryMapper;
import com.yourong.core.sh.dao.OrderLogMapper;
import com.yourong.core.sh.dao.OrderMainMapper;
import com.yourong.core.sh.dao.OrderSubMapper;
import com.yourong.core.sh.manager.GoodsManager;
import com.yourong.core.sh.manager.ShopOrderManager;
import com.yourong.core.sh.model.Goods;
import com.yourong.core.sh.model.OrderDelivery;
import com.yourong.core.sh.model.OrderForCreat;
import com.yourong.core.sh.model.OrderLog;
import com.yourong.core.sh.model.OrderMain;
import com.yourong.core.sh.model.OrderSub;
import com.yourong.core.sh.model.biz.OrderBiz;
import com.yourong.core.sh.model.biz.OrderRechargeBiz;
import com.yourong.core.sh.model.query.ShopOrderForAppQuery;
import com.yourong.core.sh.model.query.ShopOrderQuery;

/**
 * Created by XR on 2016/10/21.
 */
@Component
public class ShopOrderManagerImpl implements ShopOrderManager {
    
	private Logger logger = LoggerFactory.getLogger(ShopOrderManagerImpl.class);
	
	@Autowired
    private OrderMainMapper orderMainMapper;
	
	@Autowired
	private OrderSubMapper orderSubMapper;

	@Autowired
	private OrderLogMapper orderLogMapper;
    
	@Autowired
	private BalanceManager balanceManager;
	
	@Autowired
	private GoodsManager goodsManager;

	@Autowired
	private GoodsMapper goodsMapper;
	
	@Autowired
	private CouponManager couponManager;
	
	@Autowired
	private OrderDeliveryMapper orderDeliveryMapper;
	
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;
    
    @Override
    public Page<OrderBiz> queryPageOrderInfo(ShopOrderQuery query) {
        Page<OrderBiz> page=new Page<>();
        List<OrderBiz> list= orderMainMapper.queryPageOrderInfo(query);
        int totalCount = orderMainMapper.queryPageCountOrderInfo(query);
        page.setData(list);
        page.setiTotalDisplayRecords(totalCount);
        page.setiTotalRecords(totalCount);
        page.setPageNo(query.getCurrentPage());
        page.setiDisplayLength(query.getPageSize());
        return page;
    }

    @Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void saveSendRemark(Long orderid, String sendremark) {
		try {
			List<OrderSub> subs= orderSubMapper.queryOrderSubListByOrderMainId(orderid);
			orderMainMapper.saveSend(orderid,sendremark,new Date());
			OrderLog orderLog=new OrderLog();
			orderLog.setOrderId(orderid);
			orderLog.setRemark("订单【"+orderid+"】发放成功");
			orderLog.setCreateTime(new Date());
			orderLogMapper.insertOrderLog(orderLog);
			if (Collections3.isNotEmpty(subs)){
                for (OrderSub sub:subs) {
                    Goods keyLock = goodsMapper.selectByPrimaryKeyLock(sub.getGoodsId());
                    if (keyLock!=null){
                        //总库存释放，冻结库存释放
                        keyLock.setInventor(keyLock.getInventor() - sub.getGoodsCount());
                        keyLock.setFreezeInventor(keyLock.getFreezeInventor()-sub.getGoodsCount());
                        goodsMapper.updateInventor(keyLock);
                    }
                }
            }
		} catch (Exception e) {
			logger.error("订单发放出错：orderid=" + orderid + "", e);
			throw e;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void rechargeOrder(Long orderid) {
		try {
			orderMainMapper.rechargeOrder(orderid,new Date());
			OrderLog orderLog=new OrderLog();
			orderLog.setOrderId(orderid);
			orderLog.setRemark("订单【"+orderid+"】充值处理中");
			orderLog.setCreateTime(new Date());
			orderLogMapper.insertOrderLog(orderLog);
		} catch (Exception e) {
			logger.error("订单充值处理出错：orderid=" + orderid + "", e);
			throw e;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void rechargeResult(Long orderid,Integer resultstatus) {
		try {
			orderMainMapper.rechargeResult(orderid,resultstatus,new Date());
			OrderLog orderLog=new OrderLog();
			orderLog.setOrderId(orderid);
			List<OrderSub> subs= orderSubMapper.queryOrderSubListByOrderMainId(orderid);
			if (resultstatus.equals(5)){
				orderLog.setRemark("订单【"+orderid+"】充值失败");
				if (Collections3.isNotEmpty(subs)){
					for (OrderSub sub:subs) {
						Goods keyLock = goodsMapper.selectByPrimaryKeyLock(sub.getGoodsId());
						if (keyLock!=null){
							//总库存不变，冻结库存释放
							keyLock.setFreezeInventor(keyLock.getFreezeInventor()-sub.getGoodsCount());
							goodsMapper.updateInventor(keyLock);
						}
					}
				}
			}
			if (resultstatus.equals(2)){
				if (Collections3.isNotEmpty(subs)){
					for (OrderSub sub:subs) {
						Goods keyLock = goodsMapper.selectByPrimaryKeyLock(sub.getGoodsId());
						if (keyLock!=null){
							//总库存释放，冻结库存释放
							keyLock.setInventor(keyLock.getInventor() - sub.getGoodsCount());
							keyLock.setFreezeInventor(keyLock.getFreezeInventor()-sub.getGoodsCount());
							goodsMapper.updateInventor(keyLock);
						}
					}
				}
				orderLog.setRemark("订单【"+orderid+"】充值成功");
			}
			orderLog.setCreateTime(new Date());
			orderLogMapper.insertOrderLog(orderLog);
		} catch (Exception e) {
			logger.error("订单充值处理出错：orderid=" + orderid + "", e);
			throw e;
		}
	}

	@Override
    public Page<OrderMain> getOrderMainPage(ShopOrderForAppQuery query) {
        Page<OrderMain> page=new Page<>();
        List<OrderMain> list= orderMainMapper.getOrderMainListByQuery(query);
        int totalCount = orderMainMapper.getOrderMainListCountByQuery(query);
        page.setData(list);
        page.setPageNo(query.getCurrentPage());
        page.setiDisplayLength(query.getPageSize());
        page.setiTotalDisplayRecords(totalCount);
        page.setiTotalRecords(totalCount);
        return page;
    }

    @Override
    public List<OrderMain> getOrderMainList(Long memberId) throws ManagerException {
        List<OrderMain> list= orderMainMapper.getOrderMainList(memberId);
        return list;
    }


    @Override
    public List<OrderSub> queryOrderSubListByOrderMainId(Long orderMainId) throws ManagerException {
    	 List<OrderSub> list = orderSubMapper.queryOrderSubListByOrderMainId(orderMainId);
        return list;
    }

    @Override
    public OrderMain selectByPrimaryKey(Long orderMainId) throws ManagerException {
    	OrderMain  orderMain= orderMainMapper.selectByPrimaryKey(orderMainId);
        return orderMain;
    }


    @Override
	@Transactional(rollbackFor = {Exception.class,ManagerException.class}, propagation = Propagation.REQUIRED)
    public Object creatGoodsOrder(OrderForCreat orderForCreat) throws Exception{
    	ResultDO<Object> result = new ResultDO<Object>();
    	try{
	    	
	    	Long memberId = orderForCreat.getMemberId();
	    	BigDecimal amount = orderForCreat.getAmount();
	    	Long goodsId =orderForCreat.getGoodId();
	    	Goods goodsForLock = goodsManager.selectByPrimaryKeyLock(orderForCreat.getGoodId());
	    	if(goodsForLock.getSurplusInventor()<1){
	    		result.setResultCode(ResultCode.SHOP_GOOD_NUM_NOT_ENOUGH_ERROR);
	    		return result;
	    	}

	    	//冻结商品 1个
	    	goodsManager.freezeStock(1,goodsId);
	
	    	Goods goods = goodsManager.queryGoodsById(orderForCreat.getGoodId());
	    	//订单主表 //订单子表
	    	Long orderMainId = null;
	    	if(goods.getGoodsType()==TypeEnum.GOODS_TYPE_FOR_INVEST.getType()||goods.getGoodsType()==TypeEnum.GOODS_TYPE_DOUBLE.getType()){
	    		
	    		OrderMain orderMain = new OrderMain();
	    		orderMain.setMemberId(memberId);
	    		orderMain.setStatus(StatusEnum.SHOP_ORDER_TO_SENT.getStatus());
	    		orderMain.setTotalAmount(orderForCreat.getAmount());
	    		orderMain.setReceiver(orderForCreat.getReceiver());
	    		orderMain.setCreateTime(DateUtils.getCurrentDateTime());
	    		orderMain.setUpdateTime(DateUtils.getCurrentDateTime());
	    		orderMainMapper.insertOrderMain(orderMain);
	    		orderMainId = orderMain.getId();
	    		OrderSub orderSub = new OrderSub();
	    		orderSub.setOrderId(orderMain.getId());
	    		orderSub.setOrderType(TypeEnum.GOODS_TYPE_FOR_INVEST.getType());
	    		orderSub.setGoodsId(goodsId);
	    		orderSub.setSourceId(goods.getSourceId());
	    		orderSub.setAmount(orderForCreat.getAmount());
	    		orderSub.setGoodsCount(orderForCreat.getGoodsCount());
	    		orderSub.setCreateTime(DateUtils.getCurrentDateTime());
	    		orderSubMapper.insertOrderSub(orderSub);
	    	}
			if(goods.getGoodsType()==TypeEnum.GOODS_TYPE_VIRTUAL_CARD.getType()){
				OrderMain orderMain = new OrderMain();
	    		orderMain.setMemberId(memberId);
	    		orderMain.setStatus(StatusEnum.SHOP_ORDER_TO_SENT.getStatus());
	    		orderMain.setTotalAmount(orderForCreat.getAmount());
	    		orderMain.setReceiver(orderForCreat.getReceiver());
	    		orderMain.setCreateTime(DateUtils.getCurrentDateTime());
	    		orderMain.setUpdateTime(DateUtils.getCurrentDateTime());
	    		orderMainMapper.insertOrderMain(orderMain);
	     		orderMainId = orderMain.getId();
	    		OrderSub orderSub = new OrderSub();
	    		orderSub.setOrderId(orderMain.getId());
	    		orderSub.setOrderType(TypeEnum.GOODS_TYPE_VIRTUAL_CARD.getType());
	    		orderSub.setGoodsId(goodsId);
	    		orderSub.setSourceId(goods.getSourceId());
	    		orderSub.setAmount(orderForCreat.getAmount());
	    		orderSub.setGoodsCount(orderForCreat.getGoodsCount());
	    		orderSub.setRechargeType(goods.getRechargeType());
	    		orderSub.setCreateTime(DateUtils.getCurrentDateTime());
	    		if(goods.getRechargeType()==TypeEnum.GOODS_RECHARGE_TYPE_RECHARGE.getType()){
	    			orderSub.setRechargeCard(orderForCreat.getRechargeCard());
	    			orderSub.setRechargeAmount(goods.getRechargeAmount());
	    		}
	    		orderSubMapper.insertOrderSub(orderSub);
			}
			if(goods.getGoodsType()==TypeEnum.GOODS_TYPE_PHYSICAL.getType()){
				OrderMain orderMain = new OrderMain();
	    		orderMain.setMemberId(memberId);
	    		orderMain.setTotalAmount(orderForCreat.getAmount());
	    		orderMain.setStatus(StatusEnum.SHOP_ORDER_TO_SENT.getStatus());
	    		orderMain.setAreaFullName(orderForCreat.getAreaFullName());
	    		orderMain.setAddress(orderForCreat.getAddress());
	    		orderMain.setReceiver(orderForCreat.getReceiver());
	    		//orderMain.setAreaFullName(orderForCreat.getAreaFullName());
	    		orderMain.setMobile(orderForCreat.getMobile());
	    		orderMain.setCreateTime(DateUtils.getCurrentDateTime());
	    		orderMain.setUpdateTime(DateUtils.getCurrentDateTime());
	    		orderMainMapper.insertOrderMain(orderMain);
	     		orderMainId = orderMain.getId();
	    		OrderSub orderSub = new OrderSub();
	    		orderSub.setOrderId(orderMain.getId());
	    		orderSub.setOrderType(TypeEnum.GOODS_TYPE_PHYSICAL.getType());
	    		orderSub.setGoodsId(goodsId);
	    		orderSub.setSourceId(goods.getSourceId());
	    		orderSub.setAmount(orderForCreat.getAmount());
	    		orderSub.setGoodsCount(orderForCreat.getGoodsCount());
	    		orderSub.setCreateTime(DateUtils.getCurrentDateTime());
	    		orderSubMapper.insertOrderSub(orderSub);
			}
	    	
	    	//人气值更新，插入流水
			balanceManager.reduceForShopConsume(memberId, orderMainId, amount.intValue());
			result.setSuccess(true);
			taskExecutor.execute(new AfterShopOrderThread(orderMainId));
			
			return result;
    	}catch(Exception e){
    		logger.error("插入商品订单信息异常,orderForCreat={}",orderForCreat,e);   
    		throw new Exception();
    	}
    }

	@Override
	public OrderRechargeBiz queryOrderRechargeInfo(Long orderid) {
		return orderMainMapper.queryOrderRechargeInfo(orderid);
	}

	@Override
	public boolean updateSendRemarkById(Long orderid, String sendremark) {
		if (orderMainMapper.updateSendRemarkById(orderid,sendremark)>0){
			return true;
		}
		return false;
	}

	@Override
	public boolean updateRemarkById(Long orderid, String remark) {
		if (orderMainMapper.updateRemarkById(orderid,remark)>0){
			return true;
		}
		return false;
	}

	@Override
	public String queryRemarkById(Long orderid) {
		return orderMainMapper.queryRemarkById(orderid);
	}

	private class AfterShopOrderThread implements Runnable {
		private Long orderMainId;
		public AfterShopOrderThread(final Long orderMainId) {
			this.orderMainId = orderMainId;
		}
		public void run() {
			try {
				//线程沉睡1S,防止之前数据尚未提交，获取问题
	            Thread.sleep(1000);  

				OrderMain  orderMain = orderMainMapper.selectByPrimaryKey(orderMainId);
				if(orderMain==null){
					logger.error("【商品订单后续线程】,订单不存在，orderMainId={}",orderMainId);
					return;
				}
					
				List<OrderSub>  orderSubList = orderSubMapper.queryOrderSubListByOrderMainId(orderMainId);
				for(OrderSub orS :orderSubList){
					if(orS.getOrderType()!=TypeEnum.GOODS_TYPE_FOR_INVEST.getType()){
						continue;	
					}

					Coupon coupon = couponManager.receiveCouponSource(orderMain.getMemberId(),null,orS.getSourceId(),-1L,
							TypeEnum.COUPON_WAY_SHOP.getType(),TypeEnum.COUPON_ACCESS_SOURCE_BACKEND_SHOP.getType());
					if(coupon==null){
						logger.error("【商品订单后续线程】,优惠券发送失败，模板ID={}",orS.getSourceId());
						
						Coupon couponAgain = couponManager.receiveCouponSource(orderMain.getMemberId(),null,orS.getSourceId(),-1L,
								TypeEnum.COUPON_WAY_SHOP.getType(),TypeEnum.COUPON_ACCESS_SOURCE_BACKEND_SHOP.getType());
						if(couponAgain==null){
							logger.error("【商品订单后续线程】,优惠券二次尝试发送失败，模板ID={}",orS.getSourceId());
							continue;
						}
					}
					//发送成功，总库存减少、冻结减少
					goodsManager.freezeSubtract(1,orS.getGoodsId());
					orderMainMapper.saveSend(orderMainId, "", DateUtils.getCurrentDate());
				}
				
				
			} catch (Exception e) {
				logger.error("【商品订单后续线程】发生异常，orderMainId={}",orderMainId,e);
			}
		}
	}
	
	@Override
	public int creatOrderDelivery(OrderDelivery orderDelivery) throws ManagerException{
			return orderDeliveryMapper.insertOrderDelivery(orderDelivery);
			
	}

	@Override
	public OrderDelivery queryOrderDelivery(Long memberId) {
		return orderDeliveryMapper.queryDeliveryByMemberId(memberId);
	}

	@Override
	public int updateOrderDelivery(OrderDelivery orderDelivery)
			throws ManagerException {
		return orderDeliveryMapper.updateOrderDelivery(orderDelivery);
	}
}
