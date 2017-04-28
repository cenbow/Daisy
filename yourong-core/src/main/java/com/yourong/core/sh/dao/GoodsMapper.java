package com.yourong.core.sh.dao;

import com.yourong.core.sh.model.Goods;
import com.yourong.core.sh.model.query.GoodsQuery;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by XR on 2016/10/19.
 */
public interface GoodsMapper {
    Goods queryGoodsById(Long id);
    List<Goods> queryGoodsList(@Param("query")GoodsQuery query);
    List<Goods> queryPageGoods(@Param("query")GoodsQuery query);
    Integer queryCountPageGoods(@Param("query")GoodsQuery query);
    int updateStatusById(@Param("id")Long id,@Param("status")Integer status,@Param("date")Date date);
    int updateSortById(@Param("id")Long id, @Param("sort")Integer sort, @Param("date")Date date);
    int updateGoodsInfo(Goods goods);
    int insertGoods(Goods goods);
    
    int updateInventor(Goods goods);
    
    Goods selectByPrimaryKeyLock(@Param("id")Long id);

    int autoInventory(@Param("goodsId") Long goodsId,@Param("inventory") Integer inventory,@Param("updatetime") Date updatetime);

    List<Goods> selectAutoInventory();
    
    /**
     * 查询需要做上下架处理的数据
     * @return
     */
    List<Goods> queryShelvesGoods();
    
    int deleteById(@Param("id")Long id);
}
