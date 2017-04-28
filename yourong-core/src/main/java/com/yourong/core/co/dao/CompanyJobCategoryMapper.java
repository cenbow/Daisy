package com.yourong.core.co.dao;

import com.yourong.core.co.model.CompanyJobCategory;
import com.yourong.core.co.model.query.CompanyJobCategoryQuery;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/3/29.
 */
public interface CompanyJobCategoryMapper {
    CompanyJobCategory queryById(@Param("id") Long id);

    List<CompanyJobCategory> queryList();

    List<CompanyJobCategory> queryPageJobCategory(@Param("query")CompanyJobCategoryQuery query);

    int queryPageCount(@Param("query")CompanyJobCategoryQuery query);

    int updateJobCategory(@Param("companyJobCategory")CompanyJobCategory companyJobCategory);

    int insertJobCategory(@Param("companyJobCategory")CompanyJobCategory companyJobCategory);

    int deleteJobCategory(@Param("categoryId") Long categoryId);

    int updateSortById(@Param("id")Long id, @Param("sort")Integer sort, @Param("date")Date date);

    int updateStatus(@Param("id")Long id,@Param("status")Integer status,@Param("date")Date date);

    int releaseAll(@Param("date")Date date);

    int queryUnReleaseCount();
}
