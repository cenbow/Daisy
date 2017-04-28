package com.yourong.core.sys.manager.impl;


import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.core.sys.dao.SysLogMapper;
import com.yourong.core.sys.manager.SysLogManager;
import com.yourong.core.sys.model.SysLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2015/1/14.
 */
@Component
public class SysLogManagerImpl implements SysLogManager {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SysLogMapper sysLogMapper;

    @Override
    public int insertSelective(SysLog record) {
        try {
            record.setCreateTime(DateUtils.getCurrentDate());
            record.setOperateTime(DateUtils.getCurrentDate());
            return sysLogMapper.insertSelective(record);
        }catch ( Exception e){
            logger.error("插入日志错误",e);
        }
        return 0;
    }

}
