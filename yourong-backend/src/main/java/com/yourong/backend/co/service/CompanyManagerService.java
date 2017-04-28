package com.yourong.backend.co.service;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.co.model.CompanyManage;
import com.yourong.core.co.model.query.CompanyManageQuery;

import java.util.List;

/**
 * Created by alan.zheng on 2017/4/1.
 */
public interface CompanyManagerService {
    /**
     * id查询
     * @param id
     * @return
     */
    CompanyManage queryById(Long id);

    /**
     * 分页查询
     * @param query
     * @return
     */
    Page<CompanyManage> queryPageCompanyManage(CompanyManageQuery query);

    /**
     * 更新排序
     * @param list
     */
    void updateSortById(List<CompanyManage> list);

    /**
     * 保存
     * @param companyManage
     */
    boolean saveCompanyManage(CompanyManage companyManage,String appPath,List<BscAttachment> bscAttachments);

    /**
     * 删除简介
     * @param managerid
     * @return
     */
    boolean deleteManager(Long managerid);

    /**
     * 更新状态
     * @param managerid
     * @param status
     * @return
     */
    boolean updateStatus(Long managerid,Integer status);

    /**
     * 一键发布
     * @return
     */
    ResultDO<String> releaseAll();
}
