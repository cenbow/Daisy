package com.yourong.core.co.manager.impl;

import com.yourong.common.pageable.Page;
import com.yourong.core.co.dao.CompanyJobMapper;
import com.yourong.core.co.manager.CompanyJobManager;
import com.yourong.core.co.model.CompanyJob;
import com.yourong.core.co.model.query.CompanyJobQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/3/29.
 */
@Component
public class CompanyJobManagerImpl implements CompanyJobManager {
    @Autowired
    private CompanyJobMapper companyJobMapper;

    @Override
    public CompanyJob queryById(Long id) {
        return companyJobMapper.queryById(id);
    }

    @Override
    public Page<CompanyJob> queryPageCompanyJob(CompanyJobQuery query) {
        Page<CompanyJob> page=new Page<>();
        List<CompanyJob> list=new ArrayList<>();
        int totalCount = companyJobMapper.queryPageCount(query);
        if (totalCount>0){
            list= companyJobMapper.queryPageCompanyJob(query);
        }
        page.setData(list);
        page.setiTotalDisplayRecords(totalCount);
        page.setPageNo(query.getCurrentPage());
        page.setiDisplayLength(query.getPageSize());
        page.setiTotalRecords(totalCount);
        return page;
    }

    @Override
    public boolean updateCompanyJob(CompanyJob companyJob) {
        if (companyJobMapper.updateCompanyJob(companyJob)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean insertCompanyJob(CompanyJob companyJob) {
        if (companyJobMapper.insertCompanyJob(companyJob)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteCompanyJob(Long jobid) {
        if (companyJobMapper.deleteCompanyJob(jobid)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSortById(Long id, Integer sort, Date date) {
        if (companyJobMapper.updateSortById(id,sort,date)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean updateStatus(Long id, Integer status, Date date) {
        if (companyJobMapper.updateStatus(id,status,date)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean releaseAll(Date date) {
        if (companyJobMapper.releaseAll(date)>0){
            return true;
        }
        return false;
    }

    @Override
    public int queryUnReleaseCount() {
        return companyJobMapper.queryUnReleaseCount();
    }
}
