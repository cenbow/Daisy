package com.yourong.core.co.manager.impl;

import com.yourong.common.pageable.Page;
import com.yourong.core.co.dao.CompanyProfileMapper;
import com.yourong.core.co.manager.CompanyProfileManager;
import com.yourong.core.co.model.CompanyProfile;
import com.yourong.core.co.model.query.CompanyProfileQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/3/29.
 */
@Component
public class CompanyProfileManagerImpl implements CompanyProfileManager {
    @Autowired
    private CompanyProfileMapper companyProfileMapper;

    @Override
    public CompanyProfile queryById(Long id) {
        return companyProfileMapper.queryById(id);
    }

    @Override
    public List<CompanyProfile> queryList() {
        return companyProfileMapper.queryList();
    }

    @Override
    public Page<CompanyProfile> queryPageCompanyProfile(CompanyProfileQuery query) {
        Page<CompanyProfile> page=new Page<>();
        List<CompanyProfile> list=new ArrayList<>();
        int totalCount = companyProfileMapper.queryPageCount(query);
        if (totalCount>0){
            list= companyProfileMapper.queryPageCompanyProfile(query);
        }
        page.setData(list);
        page.setiTotalDisplayRecords(totalCount);
        page.setiTotalRecords(totalCount);
        page.setPageNo(query.getCurrentPage());
        page.setiDisplayLength(query.getPageSize());
        return page;
    }

    @Override
    public boolean updateCompanyProfile(CompanyProfile companyProfile) {
        if (companyProfileMapper.updateCompanyProfile(companyProfile)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean insertCompanyProfile(CompanyProfile companyProfile) {
        if (companyProfileMapper.insertCompanyProfile(companyProfile)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteCompanyProfile(Long profileid) {
        if (companyProfileMapper.deleteCompanyProfile(profileid)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSortById(Long id, Integer sort, Date date) {
        if (companyProfileMapper.updateSortById(id,sort,date)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean updateStatus(Long id, Integer status, Date date) {
        if (companyProfileMapper.updateStatus(id,status,date)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean releaseAll(Date date) {
        if (companyProfileMapper.releaseAll(date)>0){
            return true;
        }
        return false;
    }

    @Override
    public int queryUnReleaseCount() {
        return companyProfileMapper.queryUnReleaseCount();
    }
}
