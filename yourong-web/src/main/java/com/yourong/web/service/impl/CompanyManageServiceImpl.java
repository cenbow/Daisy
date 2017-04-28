package com.yourong.web.service.impl;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.core.co.manager.CompanyManagerManager;
import com.yourong.core.co.model.CompanyManage;
import com.yourong.web.service.CompanyManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by alan.zheng on 2017/4/5.
 */
@Service
public class CompanyManageServiceImpl implements CompanyManageService {
    private static final Logger logger = LoggerFactory.getLogger(CompanyManageServiceImpl.class);
    @Autowired
    private CompanyManagerManager companyManagerManager;

    @Override
    public ResultDO<List<CompanyManage>> queryList() {
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
