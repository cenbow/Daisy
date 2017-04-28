package com.yourong.backend.co.service.impl;

import com.yourong.backend.co.service.CompanyProfileService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.core.co.manager.CompanyProfileManager;
import com.yourong.core.co.model.CompanyProfile;
import com.yourong.core.co.model.query.CompanyProfileQuery;
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
public class CompanyProfileServiceImpl implements CompanyProfileService {
    private static Logger logger = LoggerFactory.getLogger(CompanyProfileServiceImpl.class);
    @Autowired
    private CompanyProfileManager companyProfileManager;

    @Override
    public CompanyProfile queryById(Long id) {
        try {
            return companyProfileManager.queryById(id);
        } catch (Exception e) {
            logger.error("查询公司简介异常", e);
        }
        return null;
    }

    @Override
    public Page<CompanyProfile> queryPageCompanyProfile(CompanyProfileQuery query) {
        try {
            return companyProfileManager.queryPageCompanyProfile(query);
        } catch (Exception e) {
            logger.error("分页查询公司简介异常", e);
        }
        return null;
    }

    @Override
    public void updateSortById(List<CompanyProfile> list) {
        if (Collections3.isNotEmpty(list)){
            for (CompanyProfile dto:list) {
                companyProfileManager.updateSortById(dto.getId(),dto.getSort(),new Date());
            }
        }
    }

    @Override
    public boolean saveCompanyProfile(CompanyProfile companyProfile) {
        try {
            if (companyProfile.getId()!=null&&companyProfile.getId()>0){
                companyProfile.setUpdateTime(new Date());
                return companyProfileManager.updateCompanyProfile(companyProfile);
            }
            companyProfile.setStatus(1);
            companyProfile.setSort(0);
            companyProfile.setDelFlag(1);
            companyProfile.setCreateTime(new Date());
            return companyProfileManager.insertCompanyProfile(companyProfile);
        } catch (Exception e) {
            logger.error("保存公司简介异常", e);
        }
        return false;
    }

    @Override
    public boolean deleteProfile(Long profileid) {
        try {
            return companyProfileManager.deleteCompanyProfile(profileid);
        } catch (Exception e) {
            logger.error("删除公司简介异常", e);
        }
        return false;
    }

    @Override
    public boolean updateStatus(Long profileid, Integer status) {
        try {
            return companyProfileManager.updateStatus(profileid,status,new Date());
        } catch (Exception e) {
            logger.error("更新公司简介状态异常", e);
        }
        return false;
    }

    @Override
    public ResultDO<String> releaseAll() {
        ResultDO<String> resultDO=new ResultDO<>();
        try {
            if (companyProfileManager.queryUnReleaseCount()<1){
                resultDO.setResult("已无未发布公司简介");
                return resultDO;
            }
            resultDO.setSuccess(companyProfileManager.releaseAll(new Date()));
        } catch (Exception e) {
            resultDO.setSuccess(false);
            logger.error("公司简介一键发布异常", e);
        }
        return resultDO;
    }
}
