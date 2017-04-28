package com.yourong.core.fin.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.core.fin.model.biz.PayPrincipalInterestBiz;
import com.yourong.core.fin.model.biz.PrincipalInterestForDirectMessageMember;
import com.yourong.core.ic.model.ProjectInterestBiz;

public interface PayPrincipalInterestMapper {

    List<PayPrincipalInterestBiz> selectForPagin(@Param("map") Map<String, Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String, Object> map);
    
    /**根据项目和项目结束时间查询每一期还本付息状态*/
    int findStatusByProjectAndEndDate(@Param("projectId") Long projectId,@Param("endDate") Date endDate);
    
    /**还款本息数据统计根据还款状态**/
    PayPrincipalInterestBiz findTotalPrincipalAndInterestByStatus(@Param("map")  Map<String, Object> map);
    
    /**距离到期日n天的债权项目*/
    List<PayPrincipalInterestBiz> findN2EndDateProject(@Param("map") Map<String, Object> map);
    
    /**距离到期日n天的项目*/
    List<PayPrincipalInterestBiz> findN2EndDateProjectForMsg(@Param("map") Map<String, Object> map);
    
    /**距离到期日n天的直投项目*/
    List<PayPrincipalInterestBiz> findN2EndDateMailDirectProject(@Param("map") Map<String, Object> map);
    
    
    List<ProjectInterestBiz> selectDirectForPagin(@Param("map") Map<String, Object> map);

    int selectDirectForPaginTotalCount(@Param("map") Map<String, Object> map);
    
    
    List<PrincipalInterestForDirectMessageMember> findN2EndDateDirectProject (@Param("map") Map<String, Object> map);
    
}