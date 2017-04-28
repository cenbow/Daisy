package com.yourong.core.sh.manager.impl;

import com.yourong.common.enums.BalanceAction;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.sh.dao.GoodsMapper;
import com.yourong.core.sh.manager.GoodsManager;
import com.yourong.core.sh.model.Goods;
import com.yourong.core.sh.model.query.GoodsQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by XR on 2016/10/20.
 */
@Component
public class GoodsManagerImpl implements GoodsManager {
    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public Goods queryGoodsById(Long id) {
        return goodsMapper.queryGoodsById(id);
    }
    
    @Override
    public Goods selectByPrimaryKeyLock(Long id) {
        return goodsMapper.selectByPrimaryKeyLock(id);
    }

    @Override
    public Page<Goods> queryPageGoods(GoodsQuery goodsQuery) {
        Page<Goods> page=new Page<>();
        List<Goods> list= goodsMapper.queryPageGoods(goodsQuery);
        int totalCount = goodsMapper.queryCountPageGoods(goodsQuery);
        page.setData(list);
        page.setiTotalDisplayRecords(totalCount);
        page.setiTotalRecords(totalCount);
        page.setPageNo(goodsQuery.getCurrentPage());
        page.setiDisplayLength(goodsQuery.getPageSize());
        return page;
    }

    @Override
    public boolean updateStatusById(Long id, Integer status,Date date) {
        if (goodsMapper.updateStatusById(id,status,date)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSortById(Long id, Integer sort, Date date) {
        if (goodsMapper.updateSortById(id,sort,date)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean updateGoodsInfo(Goods goods) {
        if (goodsMapper.updateGoodsInfo(goods)>0){
            return true;
        }
        return false;
    }

    @Override
    public Goods insertGoods(Goods goods) {
        if (goodsMapper.insertGoods(goods)>0){
            return goods;
        }
        return goods;
    }
    
    
    @Override
    public List<Goods> queryGoodsList(GoodsQuery goodsQuery) throws ManagerException {
    	try {
    		return goodsMapper.queryGoodsList(goodsQuery);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
        
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Goods freezeStock(Integer num,Long goodId)throws ManagerException {
    	Goods keyLock = updateStock(num, goodId, BalanceAction.balance_Good_freeze_stock);
        return keyLock;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Goods unfreezeStock(Integer num,Long goodId)throws ManagerException {
    	Goods keyLock = updateStock(num, goodId, BalanceAction.balance_Good_unfreeze_stock);
        return keyLock;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Goods freezeSubtract(Integer num,Long goodId)throws ManagerException {
    	Goods keyLock = updateStock(num, goodId, BalanceAction.balance_Good_total_subtract);
        return keyLock;
    }
    
    
    /**
     * 锁表，更新余额
     * @param num
     * @param goodId
     * @param action
     * @return
     * author: zhanghao
     * 上午10:42:34
     */
	private Goods updateStock(Integer num,Long goodId,BalanceAction action) {
		Goods goods = new Goods();
		Goods keyLock = goodsMapper.selectByPrimaryKeyLock(goodId);
		Integer inventor = null;
		Integer freezeInventor = null;		
		if (action  == BalanceAction.balance_Good_freeze_stock) {
			//冻结库存增加
			inventor = keyLock.getInventor();
			freezeInventor = keyLock.getFreezeInventor()+num;               
		} else if (action == BalanceAction.balance_Good_unfreeze_stock) {
			 //冻结库存减少
			inventor = keyLock.getInventor();
			freezeInventor = keyLock.getFreezeInventor()-num;           
		} else if (action == BalanceAction.balance_Good_total_subtract) {
			/**总库存减少，冻结库存减少**/
			inventor = keyLock.getInventor() - num;
			freezeInventor = keyLock.getFreezeInventor()-num;           
		} 
		keyLock.setInventor(inventor);
		keyLock.setFreezeInventor(freezeInventor);
		goodsMapper.updateInventor(keyLock);
		
		return goods;
	}

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public boolean autoInventory(Long goodsId, Date updatetime) {
        Goods keyLock = goodsMapper.selectByPrimaryKeyLock(goodsId);
        if (keyLock.getAutoInventory()==1){
            if (goodsMapper.autoInventory(goodsId,keyLock.getAutoInventoryCount(),updatetime)>0){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Goods> selectAutoInventory() {
        return goodsMapper.selectAutoInventory();
    }

	@Override
	public List<Goods> queryShelvesGoods() {
		return goodsMapper.queryShelvesGoods();
	}

	@Override
	public boolean deleteById(Long id) {
        if (goodsMapper.deleteById(id)>0){
            return true;
        }
        return false;
	}
}
