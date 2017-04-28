package com.yourong.api.service;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.co.model.CompanyManage;
import com.yourong.core.co.model.CompanyProfile;

import java.util.List;

/**
 * Created by alan.zheng on 2017/4/13.
 */
public interface AboutService {
    /**
     * 查询公司成长
     * @return
     */
    ResultDO<List<CompanyProfile>> queryCompanyProfile();

    /**
     * 查询公司管理层
     * @return
     */
    ResultDO<List<CompanyManage>> queryCompanyManage();
}
