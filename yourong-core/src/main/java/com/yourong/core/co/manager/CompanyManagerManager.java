package com.yourong.core.co.manager;

import com.yourong.common.pageable.Page;
import com.yourong.core.co.model.CompanyManage;
import com.yourong.core.co.model.query.CompanyManageQuery;

import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/4/1.
 */
public interface CompanyManagerManager {
    /**
     * 查询单条
     * @param id
     * @return
     */
    CompanyManage queryById(Long id);

    /**
     * 查询列表
     * @return
     */
    List<CompanyManage> queryList();

    /**
     * 分页查询
     * @param query
     * @return
     */
    Page<CompanyManage> queryPageCompanyManage(CompanyManageQuery query);

    /**
     * 更新
     * @param companyManage
     * @return
     */
    boolean updateCompanyManage(CompanyManage companyManage);

    /**
     * 插入信息
     * @param companyManage
     * @return
     */
    CompanyManage insertCompanyManage(CompanyManage companyManage);

    /**
     * 删除
     * @param manageId
     * @return
     */
    boolean deleteCompanyManage(Long manageId);

    /**
     * 更新排序
     * @param id
     * @param sort
     * @param date
     * @return
     */
    boolean updateSortById(Long id, Integer sort, Date date);

    /**
     * 更新头像
     * @param id
     * @param image
     * @return
     */
    boolean updateHrefById(Long id,String image);

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
