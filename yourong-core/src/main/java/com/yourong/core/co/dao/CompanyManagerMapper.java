package com.yourong.core.co.dao;

import com.yourong.core.co.model.CompanyManage;
import com.yourong.core.co.model.query.CompanyManageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/4/1.
 */
public interface CompanyManagerMapper {
    CompanyManage queryById(@Param("id") Long id);

    List<CompanyManage> queryList();

    List<CompanyManage> queryPageCompanyManage(@Param("query")CompanyManageQuery query);

    int queryPageCount(@Param("query")CompanyManageQuery query);

    int updateCompanyManage(@Param("companyManage") CompanyManage companyManage);

    int insertCompanyManage(CompanyManage companyManage);

    int deleteCompanyManage(@Param("managerid")Long managerid);

    int updateSortById(@Param("id")Long id, @Param("sort")Integer sort, @Param("date")Date date);

    int updateHrefById(@Param("id")Long id,@Param("href")String href,@Param("date")Date date);

    int updateStatus(@Param("id")Long id,@Param("status")Integer status,@Param("date")Date date);

    int releaseAll(@Param("date")Date date);

    int queryUnReleaseCount();
}
