package com.yourong.core.co.manager;

import com.yourong.common.pageable.Page;
import com.yourong.core.co.model.CompanyProfile;
import com.yourong.core.co.model.query.CompanyProfileQuery;

import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/3/29.
 */
public interface CompanyProfileManager {
    /**
     * 查询单条
     * @param id
     * @return
     */
    CompanyProfile queryById(Long id);

    /**
     * 查询列表
     * @return
     */
    List<CompanyProfile> queryList();

    /**
     * 分页查询
     * @param query
     * @return
     */
    Page<CompanyProfile> queryPageCompanyProfile(CompanyProfileQuery query);

    /**
     * 修改信息
     * @param companyProfile
     * @return
     */
    boolean updateCompanyProfile(CompanyProfile companyProfile);

    /**
     * 插入信息
     * @param companyProfile
     * @return
     */
    boolean insertCompanyProfile(CompanyProfile companyProfile);

    /**
     * 删除
     * @param profileid
     * @return
     */
    boolean deleteCompanyProfile(Long profileid);

    /**
     * 保存排序
     * @param id
     * @param sort
     * @param date
     * @return
     */
    boolean updateSortById(Long id, Integer sort, Date date);

    /**
     * 更新状态
     * @param id
     * @param status
     * @param date
     * @return
     */
    boolean updateStatus(Long id, Integer status, Date date);

    /**
     * 一键发布
     * @param date
     * @return
     */
    boolean releaseAll(Date date);

    /**
     * 查询为发布条数
     * @return
     */
    int queryUnReleaseCount();
}
