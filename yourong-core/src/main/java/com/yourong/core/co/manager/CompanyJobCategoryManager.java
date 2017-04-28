package com.yourong.core.co.manager;

import com.yourong.common.pageable.Page;
import com.yourong.core.co.model.CompanyJobCategory;
import com.yourong.core.co.model.query.CompanyJobCategoryQuery;

import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/3/29.
 */
public interface CompanyJobCategoryManager {
    /**
     * 查询单条
     * @param id
     * @return
     */
    CompanyJobCategory queryById(Long id);

    /**
     * 查询列表
     * @return
     */
    List<CompanyJobCategory> queryList();

    /**
     * 分页查询
     * @param query
     * @return
     */
    Page<CompanyJobCategory> queryPageJobCategory(CompanyJobCategoryQuery query);

    /**
     * 更新
     * @param companyJobCategory
     * @return
     */
    boolean updateJobCategory(CompanyJobCategory companyJobCategory);

    /**
     * 插入信息
     * @param companyJobCategory
     * @return
     */
    boolean insertJobCategory(CompanyJobCategory companyJobCategory);

    /**
     * 删除
     * @param categoryId
     * @return
     */
    boolean deleteJobCategory(Long categoryId);

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
