package com.yourong.backend.jobs;

import com.google.common.base.Optional;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.ActivityConstant;
import com.yourong.common.constant.RedisConstant;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.core.common.MessageClient;
import com.yourong.core.lottery.container.LotteryContainer;
import com.yourong.core.lottery.draw.DrawByPrizeDirectly;
import com.yourong.core.lottery.model.RewardsBase;
import com.yourong.core.lottery.model.RuleBody;
import com.yourong.core.mc.manager.ActivityLotteryManager;
import com.yourong.core.mc.manager.ActivityLotteryResultManager;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.mc.model.ActivityLottery;
import com.yourong.core.mc.model.biz.ActivityForNewYearGrab;
import com.yourong.core.mc.model.biz.ActivityGrabResultBiz;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 除夕新年活动发放人气值
 * Created by alan.zheng on 2017/1/23.
 */
public class NewYearGrabTask {
    private static Logger logger = LoggerFactory.getLogger(DownLoadImgTask.class);

    @Autowired
    private DrawByPrizeDirectly drawByPrizeDirectly;
    @Autowired
    private ActivityLotteryResultManager activityLotteryResultManager;
    @Autowired
    private ActivityLotteryManager activityLotteryManager;
    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    public void run(){
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Optional<Activity> annerActivity = LotteryContainer.getInstance()
                            .getActivityByName(ActivityConstant.ACTIVITY_GRABBAG);
                    if (!annerActivity.isPresent()) {
                        return;
                    }
                    Activity activity=annerActivity.get();
                    ActivityForNewYearGrab grab = LotteryContainer.getInstance().getObtainConditions(activity, ActivityForNewYearGrab.class,
                            ActivityConstant.ACTIVITY_GRABBAG_KEY);
                    if (activity.getStartTime().before(new Date())) {
                        //记录用户抢成功列表key
                        String grabedMember=RedisConstant.REDIS_KEY_GRAB+RedisConstant.REDIS_SEPERATOR+RedisConstant.REDIS_KEY_GRAB_MEMBER;
                        List<String> memberids= new ArrayList<String>();
                        try {
                            memberids = RedisManager.lrangeAll(grabedMember);
                        } catch (Exception e) {
                            logger.error("【除夕抢压岁钱】 Redis查询发放用户列表异常", e);
                        }
                        //当redis出现异常则从抽奖表拿取数据
                        List<ActivityLottery> activityLotteryList=activityLotteryManager.queryActivityLotteryByActivityId(activity.getId());
                        if (Collections3.isNotEmpty(activityLotteryList)){
                            for (ActivityLottery lot:activityLotteryList) {
                                memberids.add(lot.getMemberId().toString());
                            }
                        }
                        if (Collections3.isEmpty(memberids)){
                            return;
                        }
                        logger.error("当前用{}用户等待发放人气值",memberids.size());
                        for (String s:memberids) {
                            if (StringUtils.isNotEmpty(s)){
                                Long memberId= null;
                                try {
                                    memberId = Long.parseLong(s);
                                } catch (NumberFormatException e) {
                                    logger.error("【除夕抢压岁钱】 memberid转换异常memberId={}",s, e);
                                    continue;
                                }
                                boolean lotteryResult= activityLotteryResultManager.queryLotteryResultByActivityAndMemberId(activity.getId(),memberId);
                                if (lotteryResult){
                                    logger.error("此用户已发放 memberid={}",s);
                                    continue;
                                }
                                try {
                                    RewardsBase rBase = new RewardsBase();
                                    RuleBody rb = new RuleBody();
                                    rBase.setRewardName(grab.getPopularity() + ActivityConstant.popularityDesc);
                                    rBase.setRewardValue(grab.getPopularity());
                                    rBase.setRewardType(TypeEnum.ACTIVITY_LOTTERY_TYPE_POPULARITY.getType());
                                    rb.setDeductRemark(activity.getActivityDesc());
                                    rb.setMemberId(memberId);
                                    rb.setActivityId(activity.getId());
                                    rb.setActivityName(activity.getActivityDesc());
                                    rb.setCycleStr(activity.getId()+"-"+memberId);
                                    drawByPrizeDirectly.drawLottery(rb, rBase, "");
                                    //发送站内信
                                    String rewardInfo=grab.getPopularity()+ActivityConstant.popularityDesc;
                                    MessageClient.sendMsgForSPEngin(memberId, ActivityConstant.ACTIVITY_GRABBAG, rewardInfo);
                                    List<ActivityGrabResultBiz> list= activityLotteryResultManager.queryActivityGrabResult(activity.getId());
                                    //中奖结果存入redis
                                    String grabResultkey=RedisConstant.REDIS_KEY_GRAB+RedisConstant.REDIS_SEPERATOR+RedisConstant.REDIS_KEY_GRAB_RESULT;
                                    RedisManager.putObject(grabResultkey,list);
                                    RedisManager.expire(grabResultkey, DateUtils.calculateCurrentToEndTime());
                                } catch (Exception e) {
                                    logger.error("【除夕抢压岁钱】 发送人气值失败memberId={}",s, e);
                                    continue;
                                }
                            }
                        }

                    }
                } catch (Exception e) {
                    logger.error("【除夕抢压岁钱】 发送人气值失败", e);
                }
            }
        });
    }
}
