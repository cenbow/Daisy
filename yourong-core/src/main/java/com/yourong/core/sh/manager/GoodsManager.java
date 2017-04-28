package com.yourong.core.sh.manager;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.sh.model.Goods;
import com.yourong.core.sh.model.query.GoodsQuery;

import java.util.Date;
import java.util.List;

/**
 * Created by XR on 2016/10/20.
 */
public interface GoodsManager {
    Goods queryGoodsById(Long id);
    Page<Goods> queryPageGoods(GoodsQuery goodsQuery);
    boolean updateStatusById(Long id,Integer status,Date date);
    boolean updateSortById(Long id, Integer sort, Date date);
    boolean updateGoodsInfo(Goods goods);
    Goods insertGoods(Goods goods);
    
    public List<Goods> queryGoodsList(GoodsQuery goodsQuery) throws ManagerException;
    
    public Goods freezeStock(Integer num,Long goodId)throws ManagerException;
    
    public Goods unfreezeStock(Integer num,Long goodId)throws ManagerException;
    
    public Goods freezeSubtract(Integer num,Long goodId)throws ManagerException;
    
    public Goods selectByPrimaryKeyLock(Long id);

    boolean autoInventory(Long goodsId,Date updatetime);

    List<Goods> selectAutoInventory();
    /**
     * 查询需要做上下架处理的数据
     * @return
     */
    List<Goods> queryShelvesGoods();
    boolean deleteById(Long id);
}
