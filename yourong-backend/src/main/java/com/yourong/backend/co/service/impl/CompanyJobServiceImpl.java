package com.yourong.backend.co.service.impl;

import com.yourong.backend.co.service.CompanyJobService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.core.co.manager.CompanyJobManager;
import com.yourong.core.co.model.CompanyJob;
import com.yourong.core.co.model.query.CompanyJobQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/3/29.
 */
@Service
public class CompanyJobServiceImpl implements CompanyJobService {
    private static Logger logger = LoggerFactory.getLogger(CompanyJobServiceImpl.class);
    @Autowired
    private CompanyJobManager companyJobManager;
    @Override
    public CompanyJob queryById(Long id) {
        try {
            return companyJobManager.queryById(id);
        } catch (Exception e) {
            logger.error("查询公司岗位异常", e);
        }
        return null;
    }

    @Override
    public Page<CompanyJob> queryPageCompanyJob(CompanyJobQuery query) {
        try {
            return companyJobManager.queryPageCompanyJob(query);
        } catch (Exception e) {
            logger.error("分页查询公司岗位异常", e);
        }
        return null;
    }

    @Override
    public void updateSortById(List<CompanyJob> list) {
        if (Collections3.isNotEmpty(list)){
            for (CompanyJob dto:list) {
                companyJobManager.updateSortById(dto.getId(),dto.getSort(),new Date());
            }
        }
    }

    @Override
    public boolean saveCompanyJob(CompanyJob companyJob) {
        try {
            if (companyJob.getId()!=null&&companyJob.getId()>0){
                companyJob.setUpdateTime(new Date());
                return companyJobManager.updateCompanyJob(companyJob);
            }
            companyJob.setCreateTime(new Date());
            companyJob.setSort(0);
            companyJob.setDelFlag(1);
            companyJob.setStatus(1);
            return companyJobManager.insertCompanyJob(companyJob);
        } catch (Exception e) {
            logger.error("保存公司岗位异常", e);
        }
        return false;
    }

    @Override
    public boolean deleteCompanyJob(Long jobId) {
        try {
            return companyJobManager.deleteCompanyJob(jobId);
        } catch (Exception e) {
            logger.error("删除公司岗位异常", e);
        }
        return false;
    }

    @Override
    public boolean updateStatus(Long jobId, Integer status) {
        try {
            return companyJobManager.updateStatus(jobId,status,new Date());
        } catch (Exception e) {
            logger.error("更新公司岗位状态异常", e);
        }
        return false;
    }

    @Override
    public ResultDO<String> releaseAll() {
        ResultDO<String> resultDO=new ResultDO<>();
        try {
            if (companyJobManager.queryUnReleaseCount()<1){
                resultDO.setResult("已无未发布公司岗位");
                return resultDO;
            }
            resultDO.setSuccess(companyJobManager.releaseAll(new Date()));
        } catch (Exception e) {
            resultDO.setSuccess(false);
            logger.error("公司岗位一键发布异常", e);
        }
        return resultDO;
    }
}
