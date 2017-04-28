package com.yourong.web.service;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.co.model.CompanyManage;

import java.util.List;

/**
 * Created by alan.zheng on 2017/4/5.
 */
public interface CompanyManageService {
    /**
     * 查询管理层
     * @return
     */
    ResultDO<List<CompanyManage>> queryList();
}
