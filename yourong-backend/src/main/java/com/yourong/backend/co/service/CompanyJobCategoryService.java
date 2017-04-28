package com.yourong.backend.co.service;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.co.model.CompanyJobCategory;
import com.yourong.core.co.model.query.CompanyJobCategoryQuery;

import java.util.List;

/**
 * Created by alan.zheng on 2017/3/30.
 */
public interface CompanyJobCategoryService {
    /**
     * id查询
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
    Page<CompanyJobCategory> queryPageCompanyJobCategory(CompanyJobCategoryQuery query);

    /**
     * 更新排序
     * @param list
     */
    void updateSortById(List<CompanyJobCategory> list);

    /**
     * 保存
     * @param companyJobCategory
     */
    boolean saveCompanyJobCategory(CompanyJobCategory companyJobCategory);

    /**
     * 删除分类
     * @param categoryId
     * @return
     */
    boolean deleteCompanyJobCategory(Long categoryId);

    /**
     * 更新状态
     * @param categoryId
     * @param status
     * @return
     */
    boolean updateStatus(Long categoryId,Integer status);

    /**
     * 一键发布
     * @return
     */
    ResultDO<String> releaseAll();
}
