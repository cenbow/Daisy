package com.yourong.web.service.impl;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.core.co.manager.CompanyProfileManager;
import com.yourong.core.co.model.CompanyProfile;
import com.yourong.web.service.CompanyProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by alan.zheng on 2017/4/1.
 */
@Service
public class CompanyProfileServiceImpl implements CompanyProfileService {
    private static final Logger logger = LoggerFactory.getLogger(CompanyProfileServiceImpl.class);
    @Autowired
    private CompanyProfileManager companyProfileManager;

    @Override
    public ResultDO<List<CompanyProfile>> queryList() {
        ResultDO<List<CompanyProfile>> resultDO = new ResultDO<>();
        try {
            List<CompanyProfile> list=companyProfileManager.queryList();
            resultDO.setSuccess(true);
            resultDO.setResult(list);
        } catch (Exception e) {
            resultDO.setSuccess(false);
            resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
            logger.error("查询公司简介事件异常", e);
        }
        return resultDO;
    }
}
