package com.yourong.api.service.impl;

import com.yourong.api.service.AboutService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.core.co.manager.CompanyManagerManager;
import com.yourong.core.co.manager.CompanyProfileManager;
import com.yourong.core.co.model.CompanyManage;
import com.yourong.core.co.model.CompanyProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by alan.zheng on 2017/4/13.
 */
@Service
public class AboutServiceImpl implements AboutService {
    private static final Logger logger = LoggerFactory.getLogger(AboutServiceImpl.class);
    @Autowired
    private CompanyManagerManager companyManagerManager;
    @Autowired
    private CompanyProfileManager companyProfileManager;
    @Override
    public ResultDO<List<CompanyProfile>> queryCompanyProfile() {
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

    @Override
    public ResultDO<List<CompanyManage>> queryCompanyManage() {
        ResultDO<List<CompanyManage>> resultDO=new ResultDO<>();
        try {
            List<CompanyManage> list=companyManagerManager.queryList();
            resultDO.setResult(list);
            resultDO.setSuccess(true);
            return resultDO;
        } catch (Exception e) {
            resultDO.setSuccess(false);
            resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
            logger.error("查询公司管理层异常", e);
        }
        return resultDO;
    }
}
