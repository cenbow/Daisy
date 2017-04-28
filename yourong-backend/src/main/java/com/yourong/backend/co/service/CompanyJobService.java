package com.yourong.backend.co.service;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.co.model.CompanyJob;
import com.yourong.core.co.model.query.CompanyJobQuery;

import java.util.List;

/**
 * Created by alan.zheng on 2017/3/29.
 */
public interface CompanyJobService {
    /**
     * id查询
     * @param id
     * @return
     */
    CompanyJob queryById(Long id);

    /**
     * 分页查询
     * @param query
     * @return
     */
    Page<CompanyJob> queryPageCompanyJob(CompanyJobQuery query);

    /**
     * 更新排序
     * @param list
     */
    void updateSortById(List<CompanyJob> list);

    /**
     * 保存
     * @param companyJob
     */
    boolean saveCompanyJob(CompanyJob companyJob);

    /**
     * 删除岗位
     * @param jobId
     * @return
     */
    boolean deleteCompanyJob(Long jobId);

    /**
     * 更新状态
     * @param jobId
     * @param status
     * @return
     */
    boolean updateStatus(Long jobId,Integer status);

    /**
     * 一键发布
     * @return
     */
    ResultDO<String> releaseAll();
}
