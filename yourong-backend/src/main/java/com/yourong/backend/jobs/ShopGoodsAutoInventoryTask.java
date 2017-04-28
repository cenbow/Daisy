package com.yourong.backend.jobs;

import com.yourong.common.util.Collections3;
import com.yourong.core.sh.manager.GoodsManager;
import com.yourong.core.sh.model.Goods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Created by XR on 2016/12/8.
 */
public class ShopGoodsAutoInventoryTask {
    private static Logger logger = LoggerFactory.getLogger(ShopGoodsAutoInventoryTask.class);

    @Autowired
    private GoodsManager goodsManager;
    public void run(){
        List<Goods> goodsList= goodsManager.selectAutoInventory();
        if (Collections3.isNotEmpty(goodsList)){
            for (Goods goods:goodsList) {
                goodsManager.autoInventory(goods.getId(),new Date());
            }
        }
    }
}
