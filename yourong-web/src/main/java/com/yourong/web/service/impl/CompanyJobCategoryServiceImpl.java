package com.yourong.web.service.impl;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.core.co.manager.CompanyJobCategoryManager;
import com.yourong.core.co.model.CompanyJobCategory;
import com.yourong.web.service.CompanyJobCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by alan.zheng on 2017/4/1.
 */
@Service
public class CompanyJobCategoryServiceImpl implements CompanyJobCategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CompanyJobCategoryServiceImpl.class);
    @Autowired
    private CompanyJobCategoryManager companyJobCategoryManager;
    @Override
    public ResultDO<List<CompanyJobCategory>> queryList() {
        ResultDO<List<CompanyJobCategory>> resultDO=new ResultDO<>();
        try {
            List<CompanyJobCategory> list = companyJobCategoryManager.queryList();
            resultDO.setResult(list);
            resultDO.setSuccess(true);
        } catch (Exception e) {
            resultDO.setSuccess(false);
            resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
            logger.error("查询公司岗位分类异常", e);
        }
        return resultDO;
    }
}
