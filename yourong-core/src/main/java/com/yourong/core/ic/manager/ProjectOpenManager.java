package com.yourong.core.ic.manager;

import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.ProjectOpen;
import com.yourong.core.ic.model.biz.ProjectOpenSynBiz;
import com.yourong.core.ic.model.query.ProjectOpenQuery;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberInfo;

import java.util.Date;
import java.util.List;

/**
 * Created by XR on 2016/11/1.
 */
public interface ProjectOpenManager {
    Page<ProjectOpen> queryPageProjectOpen(ProjectOpenQuery query);

    List<ProjectOpen> queryHandleList();

    ProjectOpen queryById(Long id);

    ProjectOpenSynBiz queryByOutBizNo(String outBizNo);

    ProjectOpen queryAuditingInfo(Long openid);

    ProjectOpen queryRemarkById(Long openid);

    boolean canBuildprojectOpen(String subject);

    boolean updateAttachmentById(ProjectOpen projectOpen);

    boolean updateStatusAndRemarkById(Integer status,String remark,Long openId,Integer needstatus);

    boolean updateBorrowMemberBaseBizAndStatusById(Long openId,Member member, MemberInfo memberInfo);

    boolean buildProject(ProjectOpen projectOpen);

    boolean auditingById(String shortdesc,String borrowdetail,Long openid);

    boolean refuseById(String refuseCause,Long openid);

    boolean updateRemarkById(String remark,Long openid);

    ProjectOpen insertProjectOpen(ProjectOpen projectOpen);
}
