package com.yourong.backend.co.service.impl;

import com.yourong.backend.co.service.CompanyJobCategoryService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.core.co.manager.CompanyJobCategoryManager;
import com.yourong.core.co.model.CompanyJobCategory;
import com.yourong.core.co.model.query.CompanyJobCategoryQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/3/30.
 */
@Service
public class CompanyJobCategoryServiceImpl implements CompanyJobCategoryService {
    private static Logger logger = LoggerFactory.getLogger(CompanyJobCategoryServiceImpl.class);
    @Autowired
    private CompanyJobCategoryManager companyJobCategoryManager;
    @Override
    public CompanyJobCategory queryById(Long id) {
        try {
            return companyJobCategoryManager.queryById(id);
        } catch (Exception e) {
            logger.error("查询公司岗位分类异常", e);
        }
        return null;
    }

    @Override
    public List<CompanyJobCategory> queryList() {
        try {
            return companyJobCategoryManager.queryList();
        } catch (Exception e) {
            logger.error("查询公司岗位列表分类异常", e);
        }
        return null;
    }

    @Override
    public Page<CompanyJobCategory> queryPageCompanyJobCategory(CompanyJobCategoryQuery query) {
        try {
            return companyJobCategoryManager.queryPageJobCategory(query);
        } catch (Exception e) {
            logger.error("分页查询公司岗位分类异常", e);
        }
        return null;
    }

    @Override
    public void updateSortById(List<CompanyJobCategory> list) {
        if (Collections3.isNotEmpty(list)){
            for (CompanyJobCategory dto:list) {
                companyJobCategoryManager.updateSortById(dto.getId(),dto.getSort(),new Date());
            }
        }
    }

    @Override
    public boolean saveCompanyJobCategory(CompanyJobCategory companyJobCategory) {
        try {
            if (companyJobCategory.getId()!=null&&companyJobCategory.getId()>0){
                companyJobCategory.setUpdateTime(new Date());
                return companyJobCategoryManager.updateJobCategory(companyJobCategory);
            }
            companyJobCategory.setStatus(1);
            companyJobCategory.setSort(0);
            companyJobCategory.setCreateTime(new Date());
            companyJobCategory.setDelFlag(1);
            return companyJobCategoryManager.insertJobCategory(companyJobCategory);
        } catch (Exception e) {
            logger.error("保存公司岗位分类异常", e);
        }
        return false;
    }

    @Override
    public boolean deleteCompanyJobCategory(Long categoryId) {
        try {
            return companyJobCategoryManager.deleteJobCategory(categoryId);
        } catch (Exception e) {
            logger.error("删除公司岗位分类异常", e);
        }
        return false;
    }

    @Override
    public boolean updateStatus(Long categoryId, Integer status) {
        try {
            return companyJobCategoryManager.updateStatus(categoryId,status,new Date());
        } catch (Exception e) {
            logger.error("更新公司岗位分类状态异常", e);
        }
        return false;
    }

    @Override
    public ResultDO<String> releaseAll() {
        ResultDO<String> resultDO=new ResultDO<>();
        try {
            if (companyJobCategoryManager.queryUnReleaseCount()<1){
                resultDO.setResult("已无未发布公司岗位分类");
                return resultDO;
            }
            resultDO.setSuccess(companyJobCategoryManager.releaseAll(new Date()));
        } catch (Exception e) {
            resultDO.setSuccess(false);
            logger.error("公司岗位分类一键发布异常", e);
        }
        return resultDO;
    }
}
