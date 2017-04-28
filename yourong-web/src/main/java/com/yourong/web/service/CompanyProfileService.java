package com.yourong.web.service;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.co.model.CompanyProfile;

import java.util.List;

/**
 * Created by alan.zheng on 2017/4/1.
 */
public interface CompanyProfileService {
    /**
     * 查询公司简介事件
     * @return
     */
    ResultDO<List<CompanyProfile>> queryList();
}
