package com.yourong.web.service.impl;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.pageable.Page;
import com.yourong.core.co.manager.CompanyJobManager;
import com.yourong.core.co.model.CompanyJob;
import com.yourong.core.co.model.query.CompanyJobQuery;
import com.yourong.web.service.CompanyJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by alan.zheng on 2017/4/1.
 */
@Service
public class CompanyJobServiceImpl implements CompanyJobService {
    private static final Logger logger = LoggerFactory.getLogger(CompanyJobServiceImpl.class);
    @Autowired
    private CompanyJobManager companyJobManager;
    @Override
    public ResultDO<CompanyJob> queryPageCompanyJob(CompanyJobQuery query) {
        ResultDO<CompanyJob> resultDO=new ResultDO<>();
        try {
            Page<CompanyJob> page = companyJobManager.queryPageCompanyJob(query);
            resultDO.setPage(page);
            resultDO.setSuccess(true);
        } catch (Exception e) {
            resultDO.setSuccess(false);
            resultDO.setResultCode(ResultCode.ERROR_SYSTEM);
            logger.error("查询公司岗位异常", e);
        }
        return resultDO;
    }
}
