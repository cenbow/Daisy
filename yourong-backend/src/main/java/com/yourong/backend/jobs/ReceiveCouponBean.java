package com.yourong.backend.jobs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.StringUtil;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.biz.CouponReceiveBiz;

/**
 * Created by py on 2015/6/26.
 */
public class ReceiveCouponBean {
    private static Logger logger = LoggerFactory.getLogger(ReceiveCouponBean.class);
    private ExecutorService exec;

    @Autowired
    private CouponManager couponManager;
    public void init() {
        exec = Executors.newSingleThreadExecutor();
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (logger.isDebugEnabled())
                        {
//                            logger.debug("开始异常补偿优惠劵领取");
                        }
                        if (RedisManager.isExit(RedisConstant.REDIS_KEY_ReceiveCoupon)){
                                String json = RedisManager.rpop(RedisConstant.REDIS_KEY_ReceiveCoupon);
                                if (logger.isDebugEnabled())
                                {
                                    logger.debug(json);
                                }
                                if (StringUtil.isNotBlank(json)) {
                                    CouponReceiveBiz biz =  JSONObject.parseObject(json, CouponReceiveBiz.class);
                                    try {
                                        logger.info("补偿优惠劵:{}",json);
                                        couponManager.receiveCouponSource(biz.getMemberId(),biz.getActivityId(),biz.getCouponTemplateId(),biz.getSenderId(),
                                        		TypeEnum.COUPON_WAY_BACKEND.getType(),TypeEnum.COUPON_ACCESS_SOURCE_BACKEND_COMPENSATE.getType());
                                      } catch (Exception e) {
                                        logger.error("领用优惠劵异常,", e);
                                    }
                        }

                        } else {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                logger.error("线程中断", e);
                            }
                        }
                    }
                }
            });

    }


    public void close() {
        exec.isShutdown();
    }

}
