package com.yourong.backend.ic.service;

import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.ProjectOpen;
import com.yourong.core.ic.model.query.ProjectOpenQuery;

/**
 * Created by XR on 2016/11/1.
 */
public interface ProjectOpenService {
    Page<ProjectOpen> queryPageProjectOpen(ProjectOpenQuery query);

    ProjectOpen queryById(Long id);

    ProjectOpen queryAuditingInfo(Long openid);

    ProjectOpen queryRemarkById(Long openid);

    boolean updateAttachmentById(ProjectOpen projectOpen);

    boolean auditingById(Integer status,String refuseCause,String shortdesc, String borrowdetail, Long openid);

    boolean updateRemarkById(String remark, Long openid);

    ProjectOpen insertProjectOpen(ProjectOpen projectOpen);
}
