package com.yourong.core.ic.dao;

import com.yourong.core.ic.model.ProjectOpen;
import com.yourong.core.ic.model.biz.ProjectOpenSynBiz;
import com.yourong.core.ic.model.query.ProjectOpenQuery;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by XR on 2016/11/1.
 */
public interface ProjectOpenMapper {
    List<ProjectOpen> queryPageProjectOpen(@Param("query")ProjectOpenQuery query);

    List<ProjectOpen> queryHandleList();

    int queryPageCountProjectOpen(@Param("query")ProjectOpenQuery query);

    int queryBuildCountByOutBizNo(@Param("outBizNo")String outBizNo);

    ProjectOpen selectByPrimaryKey(Long id);

    ProjectOpenSynBiz queryByOutBizNo(@Param("outBizNo")String outBizNo);

    ProjectOpen queryAuditingInfo(@Param("openid")Long openid);

    ProjectOpen queryRemarkById(@Param("openid")Long openid);

    int updateAttachmentById(ProjectOpen projectOpen);

    int auditingById(@Param("shortdesc")String shortdesc,@Param("borrowdetail")String borrowdetail,@Param("openid")Long openid);

    int refuseById(@Param("refuseCause")String refuseCause,@Param("openid")Long openid);

    int updateRemarkById(@Param("remark")String remark,@Param("openid")Long openid);

    int updateStatusAndRemarkById(@Param("status")Integer status, @Param("updateTime")Date updateTime,@Param("remark")String remark, @Param("id")Long id,@Param("needstatus")Integer needstatus);

    int updateProjectbizJsonAndStatusById(@Param("projectbizJson")String projectbizJson,@Param("status")Integer status, @Param("updateTime")Date updateTime, @Param("id")Long id);

    int buildProject(ProjectOpen projectOpen);

    int insertProjectOpen(ProjectOpen projectOpen);
}
