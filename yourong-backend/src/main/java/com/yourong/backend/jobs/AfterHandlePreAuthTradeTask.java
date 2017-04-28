package com.yourong.backend.jobs;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.common.util.Collections3;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.biz.AfterHandlePreAuthTradeBiz;

/**
 * 直投项目发起代收完成/撤销
 * Created by XR on 2016/12/9.
 */
public class AfterHandlePreAuthTradeTask {

    private static final Logger logger = LoggerFactory.getLogger(AfterHandlePreAuthTradeTask.class);
    @Autowired
    private ProjectManager projectManager;

    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    public void run(){
        taskExecutor.execute(new Runnable(){
            @Override
            public void run() {
                List<AfterHandlePreAuthTradeBiz> list= projectManager.queryAfterHandlePreAuthTrade();
                if (Collections3.isNotEmpty(list)){
                    for (AfterHandlePreAuthTradeBiz biz:list) {
                        try {
                            Calendar calendar = new GregorianCalendar();
                            Calendar nowcalendar = new GregorianCalendar();
                            calendar.setTime(biz.getAuditTime());
							calendar.add(calendar.MINUTE, 30);
                            nowcalendar.setTime(new Date());
                            if (nowcalendar.compareTo(calendar)>0){
                                projectManager.afterHandlePreAuthTrade(biz.getProjectId(), true);
                            }
                        } catch (Exception e) {
                            logger.error("直投项目发起代收完成/撤销异常：ProjectId"+biz.getProjectId()+"", e);
                        }
                    }
                }
            }
        });

    }
}
