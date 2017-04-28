package com.yourong.web.service;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.co.model.CompanyJob;
import com.yourong.core.co.model.query.CompanyJobQuery;

/**
 * Created by alan.zheng on 2017/4/1.
 */
public interface CompanyJobService {
    ResultDO<CompanyJob> queryPageCompanyJob(CompanyJobQuery query);
}
