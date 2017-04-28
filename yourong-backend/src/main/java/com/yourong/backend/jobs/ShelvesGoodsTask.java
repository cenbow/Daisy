package com.yourong.backend.jobs;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.backend.sh.enums.GoodsStatus;
import com.yourong.core.sh.manager.GoodsManager;
import com.yourong.core.sh.model.Goods;

/**
 * @author:quanguo.wang
 * @time:2017年3月30日 下午4:54:57
 * 对商品定时做上下架处理
 */
public class ShelvesGoodsTask {
	@Resource
    private GoodsManager goodsManager;
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;
	
	private static final Logger logger = LoggerFactory.getLogger(ShelvesGoodsTask.class);
	
	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					handGoods();
				} catch (Exception e) {
					logger.error("商品定时做上下架处理异常",e);
				}
			}
		});
	}
	
	/**
	 * 处理商品上下架
	 */
	private void handGoods(){
		logger.info("商品上下架处理开始");
		List<Goods> list = goodsManager.queryShelvesGoods();
		if(null != list && list.size() > 0){
			for(Goods goods : list){
				if(goods.getStatus()==GoodsStatus.OFF_SHELVES.getStatus()){
					goodsManager.updateStatusById(goods.getId(), GoodsStatus.SHELVES.getStatus(), new Date());
					logger.info("对商品进行上架处理成功id="+goods.getId());
				}else{
					goodsManager.updateStatusById(goods.getId(), GoodsStatus.OFF_SHELVES.getStatus(), new Date());
					logger.info("对商品进行下架处理成功id="+goods.getId());
				}
			}
		}
		logger.info("商品上下架处理结束");
	}

}

