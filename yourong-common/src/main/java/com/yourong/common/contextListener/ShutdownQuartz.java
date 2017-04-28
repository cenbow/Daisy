package com.yourong.common.contextListener;

import com.yourong.common.util.SpringContextHolder;
import org.quartz.Scheduler;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by py  on 2015/5/4.
 */
public class ShutdownQuartz implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            // Get a reference to the Scheduler and shut it down
            Scheduler scheduler = (Scheduler) SpringContextHolder.getBean("pollingFaction");
            scheduler.shutdown(true);
            // Sleep for a bit so that we don't get any errors
            Thread.sleep(1000);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
