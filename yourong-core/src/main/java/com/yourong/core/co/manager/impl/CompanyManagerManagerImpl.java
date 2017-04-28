package com.yourong.core.co.manager.impl;

import com.yourong.common.pageable.Page;
import com.yourong.core.co.dao.CompanyManagerMapper;
import com.yourong.core.co.manager.CompanyManagerManager;
import com.yourong.core.co.model.CompanyManage;
import com.yourong.core.co.model.query.CompanyManageQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/4/1.
 */
@Component
public class CompanyManagerManagerImpl implements CompanyManagerManager {
    @Autowired
    private CompanyManagerMapper companyManagerMapper;
    @Override
    public CompanyManage queryById(Long id) {
        return companyManagerMapper.queryById(id);
    }

    @Override
    public List<CompanyManage> queryList() {
        return companyManagerMapper.queryList();
    }

    @Override
    public Page<CompanyManage> queryPageCompanyManage(CompanyManageQuery query) {
        Page<CompanyManage> page=new Page<>();
        List<CompanyManage> list=new ArrayList<>();
        int totalCount = companyManagerMapper.queryPageCount(query);
        if (totalCount>0){
            list= companyManagerMapper.queryPageCompanyManage(query);
        }
        page.setData(list);
        page.setiTotalDisplayRecords(totalCount);
        page.setPageNo(query.getCurrentPage());
        page.setiDisplayLength(query.getPageSize());
        page.setiTotalRecords(totalCount);
        return page;
    }

    @Override
    public boolean updateCompanyManage(CompanyManage companyManage) {
        if (companyManagerMapper.updateCompanyManage(companyManage)>0){
            return true;
        }
        return false;
    }

    @Override
    public CompanyManage insertCompanyManage(CompanyManage companyManage) {
        companyManagerMapper.insertCompanyManage(companyManage);
        return companyManage;
    }

    @Override
    public boolean deleteCompanyManage(Long manageId) {
        if (companyManagerMapper.deleteCompanyManage(manageId)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSortById(Long id, Integer sort, Date date) {
        if (companyManagerMapper.updateSortById(id,sort,date)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean updateHrefById(Long id, String image) {
        if (companyManagerMapper.updateHrefById(id,image,new Date())>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean updateStatus(Long id, Integer status, Date date) {
        if (companyManagerMapper.updateStatus(id,status,date)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean releaseAll(Date date) {
        if (companyManagerMapper.releaseAll(date)>0){
            return true;
        }
        return false;
    }

    @Override
    public int queryUnReleaseCount() {
        return companyManagerMapper.queryUnReleaseCount();
    }
}
