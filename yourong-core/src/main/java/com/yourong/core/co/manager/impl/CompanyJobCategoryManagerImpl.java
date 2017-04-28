package com.yourong.core.co.manager.impl;

import com.yourong.common.pageable.Page;
import com.yourong.core.co.dao.CompanyJobCategoryMapper;
import com.yourong.core.co.dao.CompanyJobMapper;
import com.yourong.core.co.manager.CompanyJobCategoryManager;
import com.yourong.core.co.model.CompanyJobCategory;
import com.yourong.core.co.model.query.CompanyJobCategoryQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/3/29.
 */
@Component
public class CompanyJobCategoryManagerImpl implements CompanyJobCategoryManager {
    @Autowired
    private CompanyJobCategoryMapper companyJobCategoryMapper;
    @Autowired
    private CompanyJobMapper companyJobMapper;

    @Override
    public CompanyJobCategory queryById(Long id) {
        return companyJobCategoryMapper.queryById(id);
    }

    @Override
    public List<CompanyJobCategory> queryList() {
        return companyJobCategoryMapper.queryList();
    }

    @Override
    public Page<CompanyJobCategory> queryPageJobCategory(CompanyJobCategoryQuery query) {
        Page<CompanyJobCategory> page=new Page<>();
        List<CompanyJobCategory> list=new ArrayList<>();
        int totalCount = companyJobCategoryMapper.queryPageCount(query);
        if (totalCount>0){
            list= companyJobCategoryMapper.queryPageJobCategory(query);
            if (list!=null&&list.size()>0){
                for (CompanyJobCategory category:list) {
                    category.setHiringCount(companyJobMapper.queryCountByCategory(category.getId()));
                }
            }
        }
        page.setData(list);
        page.setiTotalDisplayRecords(totalCount);
        page.setiTotalRecords(totalCount);
        page.setPageNo(query.getCurrentPage());
        page.setiDisplayLength(query.getPageSize());
        return page;
    }

    @Override
    public boolean updateJobCategory(CompanyJobCategory companyJobCategory) {
        if (companyJobCategoryMapper.updateJobCategory(companyJobCategory)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean insertJobCategory(CompanyJobCategory companyJobCategory) {
        if (companyJobCategoryMapper.insertJobCategory(companyJobCategory)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteJobCategory(Long categoryId) {
        if (companyJobCategoryMapper.deleteJobCategory(categoryId)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSortById(Long id, Integer sort, Date date) {
        if (companyJobCategoryMapper.updateSortById(id,sort,date)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean updateStatus(Long id, Integer status, Date date) {
        if (companyJobCategoryMapper.updateStatus(id,status,date)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean releaseAll(Date date) {
        if (companyJobCategoryMapper.releaseAll(date)>0){
            return true;
        }
        return false;
    }

    @Override
    public int queryUnReleaseCount() {
        return companyJobCategoryMapper.queryUnReleaseCount();
    }
}
