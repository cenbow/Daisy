package com.yourong.core.co.dao;

import com.yourong.core.co.model.CompanyJob;
import com.yourong.core.co.model.query.CompanyJobQuery;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/3/29.
 */
public interface CompanyJobMapper {
    CompanyJob queryById(@Param("id") Long id);

    List<CompanyJob> queryPageCompanyJob(@Param("query")CompanyJobQuery query);

    int queryPageCount(@Param("query")CompanyJobQuery query);

    int queryCountByCategory(@Param("categoryId") Long categoryId);

    int updateCompanyJob(@Param("companyJob") CompanyJob companyJob);

    int insertCompanyJob(@Param("companyJob") CompanyJob companyJob);

    int deleteCompanyJob(@Param("jobid")Long jobid);

    int updateSortById(@Param("id")Long id, @Param("sort")Integer sort, @Param("date")Date date);

    int updateStatus(@Param("id")Long id,@Param("status")Integer status,@Param("date")Date date);

    int releaseAll(@Param("date")Date date);

    int queryUnReleaseCount();
}
