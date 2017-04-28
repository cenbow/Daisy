package com.yourong.core.co.manager;

import com.yourong.common.pageable.Page;
import com.yourong.core.co.model.CompanyJob;
import com.yourong.core.co.model.query.CompanyJobQuery;

import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/3/29.
 */
public interface CompanyJobManager {
    /**
     * 查询单条
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
     * 更新
     * @param companyJob
     * @return
     */
    boolean updateCompanyJob(CompanyJob companyJob);

    /**
     * 插入信息
     * @param companyJob
     * @return
     */
    boolean insertCompanyJob(CompanyJob companyJob);

    /**
     * 删除
     * @param jobid
     * @return
     */
    boolean deleteCompanyJob(Long jobid);

    /**
     * 保存排序
     * @param id
     * @param sort
     * @param date
     * @return
     */
    boolean updateSortById(Long id, Integer sort, Date date);

    /**
     * 更新状态
     * @param id
     * @param status
     * @param date
     * @return
     */
    boolean updateStatus(Long id, Integer status, Date date);

    /**
     * 一键发布
     * @param date
     * @return
     */
    boolean releaseAll(Date date);

    /**
     * 查询为发布条数
     * @return
     */
    int queryUnReleaseCount();
}
