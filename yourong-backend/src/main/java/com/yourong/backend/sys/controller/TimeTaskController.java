package com.yourong.backend.sys.controller;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yourong.backend.BaseController;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;

/**
 * Created by Administrator on 2015/1/19.
 */
@Controller
@RequestMapping("sysTime")
public class TimeTaskController extends BaseController {
    @Autowired
    private SchedulerFactoryBean startQuertz;

    @RequestMapping(value = "index")
    @RequiresPermissions("sysTime:index")
    public ModelAndView  showMemberqueryBalance(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        Scheduler startQuertzObject = startQuertz.getObject();
        try {
            List<String> jobGroupNames = startQuertzObject.getJobGroupNames();
            String s = jobGroupNames.get(0);
            GroupMatcher groupMatcher = GroupMatcher.groupContains(s);
            Set<JobKey> jobKeys = startQuertzObject.getJobKeys(groupMatcher);
            
            
            
            model.addObject("jobs",jobKeys);
            
        }catch (Exception e){
            logger.error("显示定时任务",e);
        }
        model.setViewName("sys/sysTime/index");


        return model;
    }
    
    @RequestMapping(value = "excuteTask")
    @ResponseBody
    @LogInfoAnnotation(moduleName = "手动定时任务",desc = "执行定时任务")
    public ResultDO<?> excuteTask(@ModelAttribute("taskkey") String  taskId, HttpServletRequest req, HttpServletResponse resp) {
        ResultDO<?>  result  = new ResultDO<>();
        Scheduler startQuertzObject = startQuertz.getObject();
        try {
            JobKey jobKey = JobKey.jobKey(taskId);
            startQuertzObject.triggerJob(jobKey);
        } catch (Exception e) {
            logger.error("手动执行定时任务异常", e);
            result.setSuccess(false);
        }
        result.setSuccess(true);
        return result;
    }


}
