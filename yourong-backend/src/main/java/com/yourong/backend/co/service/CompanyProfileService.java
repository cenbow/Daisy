package com.yourong.backend.co.service;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.co.model.CompanyProfile;
import com.yourong.core.co.model.query.CompanyProfileQuery;

import java.util.List;

/**
 * Created by alan.zheng on 2017/3/29.
 */
public interface CompanyProfileService {
    /**
     * id查询
     * @param id
     * @return
     */
    CompanyProfile queryById(Long id);

    /**
     * 分页查询
     * @param query
     * @return
     */
    Page<CompanyProfile> queryPageCompanyProfile(CompanyProfileQuery query);

    /**
     * 更新排序
     * @param list
     */
    void updateSortById(List<CompanyProfile> list);

    /**
     * 保存
     * @param companyProfile
     */
    boolean saveCompanyProfile(CompanyProfile companyProfile);

    /**
     * 删除简介
     * @param profileid
     * @return
     */
    boolean deleteProfile(Long profileid);

    /**
     * 更新状态
     * @param profileid
     * @param status
     * @return
     */
    boolean updateStatus(Long profileid,Integer status);

    /**
     * 一键发布
     * @return
     */
    ResultDO<String> releaseAll();
}
