package com.yourong.core.ic.manager.impl;

import com.alibaba.fastjson.JSON;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.dao.ProjectOpenMapper;
import com.yourong.core.ic.manager.ProjectOpenManager;
import com.yourong.core.ic.model.DirectProjectBiz;
import com.yourong.core.ic.model.ProjectOpen;
import com.yourong.core.ic.model.biz.ProjectOpenSynBiz;
import com.yourong.core.ic.model.query.ProjectOpenQuery;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberBaseBiz;
import com.yourong.core.uc.model.MemberInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by XR on 2016/11/1.
 */
@Component
public class ProjectOpenManagerImpl implements ProjectOpenManager {
    @Autowired
    private ProjectOpenMapper projectOpenMapper;

    @Override
    public Page<ProjectOpen> queryPageProjectOpen(ProjectOpenQuery query) {
        Page<ProjectOpen> page=new Page<>();
        List<ProjectOpen> list= projectOpenMapper.queryPageProjectOpen(query);
        int totalCount = projectOpenMapper.queryPageCountProjectOpen(query);
        page.setData(list);
        page.setiTotalDisplayRecords(totalCount);
        page.setiTotalRecords(totalCount);
        page.setPageNo(query.getCurrentPage());
        page.setiDisplayLength(query.getPageSize());
        return page;
    }

    @Override
    public List<ProjectOpen> queryHandleList() {
        return projectOpenMapper.queryHandleList();
    }

    @Override
    public ProjectOpen queryById(Long id) {
        return projectOpenMapper.selectByPrimaryKey(id);
    }

    @Override
    public ProjectOpenSynBiz queryByOutBizNo(String outBizNo) {
        return projectOpenMapper.queryByOutBizNo(outBizNo);
    }

    @Override
    public ProjectOpen queryAuditingInfo(Long openid) {
        return projectOpenMapper.queryAuditingInfo(openid);
    }

    @Override
    public ProjectOpen queryRemarkById(Long openid) {
        return projectOpenMapper.queryRemarkById(openid);
    }

    @Override
    public boolean canBuildprojectOpen(String subjectId) {
        if (projectOpenMapper.queryBuildCountByOutBizNo(subjectId)>0){
            return false;
        }
        return true;
    }

    @Override
    public boolean updateAttachmentById(ProjectOpen projectOpen) {
        if (projectOpenMapper.updateAttachmentById(projectOpen)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean updateStatusAndRemarkById(Integer status, String remark, Long openId,Integer needstatus) {
        if (projectOpenMapper.updateStatusAndRemarkById(status,new Date(),remark,openId,needstatus)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean updateBorrowMemberBaseBizAndStatusById(Long openId,Member member, MemberInfo memberInfo) {
        ProjectOpen projectOpen= projectOpenMapper.selectByPrimaryKey(openId);
        if (projectOpen==null){
            return false;
        }
        DirectProjectBiz directProjectBiz= JSON.parseObject(projectOpen.getProjectbizJson(),DirectProjectBiz.class);
        MemberBaseBiz memberBaseBiz=new MemberBaseBiz();
        memberBaseBiz.setId(member.getId());
        memberBaseBiz.setMember(member);
        memberBaseBiz.setMemberInfo(memberInfo);
        directProjectBiz.setBorrowerMemberBaseBiz(memberBaseBiz);
        if (projectOpenMapper.updateProjectbizJsonAndStatusById(JSON.toJSONString(directProjectBiz),1,new Date(),projectOpen.getId())>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean buildProject(ProjectOpen projectOpen) {
        if (projectOpenMapper.buildProject(projectOpen)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean auditingById(String shortdesc, String borrowdetail, Long openid) {
        if (projectOpenMapper.auditingById(shortdesc,borrowdetail,openid)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean refuseById(String refuseCause, Long openid) {
        if (projectOpenMapper.refuseById(refuseCause,openid)>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean updateRemarkById(String remark, Long openid) {
        if (projectOpenMapper.updateRemarkById(remark,openid)>0){
            return true;
        }
        return false;
    }

    @Override
    public ProjectOpen insertProjectOpen(ProjectOpen projectOpen) {
        if (projectOpenMapper.insertProjectOpen(projectOpen)>0){
            return projectOpen;
        }
        return null;
    }
}
