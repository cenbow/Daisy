package com.yourong.backend.ic.service.impl;

import com.yourong.backend.ic.service.ProjectOpenService;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.manager.ProjectOpenManager;
import com.yourong.core.ic.model.ProjectOpen;
import com.yourong.core.ic.model.query.ProjectOpenQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by XR on 2016/11/1.
 */
@Service
public class ProjectOpenServiceImpl implements ProjectOpenService {
    @Autowired
    private ProjectOpenManager projectOpenManager;

    @Override
    public Page<ProjectOpen> queryPageProjectOpen(ProjectOpenQuery query) {
        return projectOpenManager.queryPageProjectOpen(query);
    }

    @Override
    public ProjectOpen queryById(Long id) {
        return projectOpenManager.queryById(id);
    }

    @Override
    public ProjectOpen queryAuditingInfo(Long openid) {
        return projectOpenManager.queryAuditingInfo(openid);
    }

    @Override
    public ProjectOpen queryRemarkById(Long openid) {
        return projectOpenManager.queryRemarkById(openid);
    }

    @Override
    public boolean updateAttachmentById(ProjectOpen projectOpen) {
        return projectOpenManager.updateAttachmentById(projectOpen);
    }

    @Override
    public boolean updateRemarkById(String remark, Long openid) {
        return projectOpenManager.updateRemarkById(remark,openid);
    }

    @Override
    public boolean auditingById(Integer status,String refuseCause,String shortdesc, String borrowdetail, Long openid) {
        if (status==5){
            return projectOpenManager.auditingById(shortdesc,borrowdetail,openid);
        }
        if (status==6){
            return projectOpenManager.refuseById(refuseCause,openid);
        }
        return false;
    }

    @Override
    public ProjectOpen insertProjectOpen(ProjectOpen projectOpen) {
        return projectOpenManager.insertProjectOpen(projectOpen);
    }
}
