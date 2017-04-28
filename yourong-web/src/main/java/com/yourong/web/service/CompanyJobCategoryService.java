package com.yourong.web.service;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.co.model.CompanyJobCategory;

import java.util.List;

/**
 * Created by alan.zheng on 2017/4/1.
 */
public interface CompanyJobCategoryService {
    ResultDO<List<CompanyJobCategory>> queryList();
}
