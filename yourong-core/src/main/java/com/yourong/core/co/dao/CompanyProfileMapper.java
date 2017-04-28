package com.yourong.core.co.dao;

import com.yourong.core.co.model.CompanyProfile;
import com.yourong.core.co.model.query.CompanyProfileQuery;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/3/29.
 */
public interface CompanyProfileMapper {
    CompanyProfile queryById(@Param("id") Long id);

    List<CompanyProfile> queryList();

    List<CompanyProfile> queryPageCompanyProfile(@Param("query")CompanyProfileQuery query);

    int queryPageCount(@Param("query")CompanyProfileQuery query);

    int updateCompanyProfile(@Param("companyProfile") CompanyProfile companyProfile);

    int insertCompanyProfile(@Param("companyProfile") CompanyProfile companyProfile);

    int deleteCompanyProfile(@Param("profileid") Long profileid);

    int updateSortById(@Param("id")Long id, @Param("sort")Integer sort, @Param("date")Date date);

    int updateStatus(@Param("id")Long id,@Param("status")Integer status,@Param("date")Date date);

    int releaseAll(@Param("date")Date date);

    int queryUnReleaseCount();
}
