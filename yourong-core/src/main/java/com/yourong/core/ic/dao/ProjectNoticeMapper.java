package com.yourong.core.ic.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.ProjectNotice;
import com.yourong.core.ic.model.biz.ProjectNoticeForFront;

public interface ProjectNoticeMapper {
    @Delete({
        "delete from ic_project_notice",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into ic_project_notice (id, project_id, ",
        "start_time, end_time, ",
        "sort, status, index_show,notice_message, ",
        "create_time, update_time, ",
        "remarks, del_flag)",
        "values (#{id,jdbcType=BIGINT}, #{projectId,jdbcType=BIGINT}, ",
        "#{startTime,jdbcType=TIMESTAMP}, #{endTime,jdbcType=TIMESTAMP}, ",
        "#{sort,jdbcType=INTEGER}, #{status,jdbcType=INTEGER},#{indexShow,jdbcType=INTEGER}, #{noticeMessage,jdbcType=VARCHAR}, ",
        "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP}, ",
        "#{remarks,jdbcType=VARCHAR}, #{delFlag,jdbcType=INTEGER})"
    })
    int insert(ProjectNotice record);

    int insertSelective(ProjectNotice record);

    @Select({
        "select",
        "id, project_id, start_time, end_time, sort, status, index_show, notice_message, create_time, ",
        "update_time, remarks, del_flag",
        "from ic_project_notice",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    ProjectNotice selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProjectNotice record);

    @Update({
        "update ic_project_notice",
        "set project_id = #{projectId,jdbcType=BIGINT},",
          "start_time = #{startTime,jdbcType=TIMESTAMP},",
          "end_time = #{endTime,jdbcType=TIMESTAMP},",
          "sort = #{sort,jdbcType=INTEGER},",
          "status = #{status,jdbcType=INTEGER},",
          "index_show = #{indexShow,jdbcType=INTEGER},",
          "notice_message = #{noticeMessage,jdbcType=VARCHAR},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP},",
          "remarks = #{remarks,jdbcType=VARCHAR},",
          "del_flag = #{delFlag,jdbcType=INTEGER}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(ProjectNotice record);

    Page<ProjectNotice> findByPage(Page<ProjectNotice> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") int[] ids);

    List<ProjectNotice> selectForPagin(@Param("map") Map<String, Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String, Object> map);
    
    @Update({
    	" update ic_project_notice set status = 10 where status = 1 and start_time <= now()"
    })
    int autoStartProjectNoticeTask();
    
    @Update({
    	" update ic_project_notice set status = 30 where status = 10 and end_time <= now()"
    })
    int autoEndProjectNoticeTask();
    
    @Update({
        " update ic_project_notice set del_flag = -1",
        " where id = #{id,jdbcType=BIGINT}"
    })
    int deleteProjectNoticeById(Long id);
    
    @Update({
        " update ic_project_notice set status = #{newStatus,jdbcType=INTEGER},update_time = now()",
        " where status = #{currentStatus,jdbcType=INTEGER} and  id = #{id,jdbcType=BIGINT}"
    })
    int updateProjectNoticeStatus(@Param("newStatus")int newStatus, @Param("currentStatus")int currentStatus, @Param("id")Long id);
    
    @Update({
    	"update ic_project_notice set index_show = 1 where id = #{id,jdbcType=BIGINT}"
    })
    int recommendProjectNotice(@Param("id")Long id);
    
    @Update({
    	"update ic_project_notice set index_show = 0 where id = #{id,jdbcType=BIGINT} and index_show = 1"
    })
    int cancelRecommendProjectNoticeById(@Param("id")Long id);
    
    @Update({
    	"update ic_project_notice set index_show = 0 where index_show = 1"
    })
    int cancelRecommendProjectNotice();
    
    
    List<ProjectNoticeForFront> getProjectNoticeForFront(@Param("num")int num);
    
    List<ProjectNoticeForFront> p2pGetProjectNoticeForFront(@Param("num")int num);
    
    @Select({
        "SELECT a.id,a.project_id,a.start_time,a.end_time FROM ic_project_notice a,ic_project b WHERE a.STATUS = 10 ",
        " and a.project_id =b.id and b.package_flag =0 AND a.del_flag = 1 AND b.package_flag = 0 ",
        " AND a.end_time > now() ORDER BY	a.sort ASC LIMIT 0,1 "
    })
    @ResultMap("ProjectNoticeForFrontResultMap")
    ProjectNoticeForFront getProjectNoticeByIndexShow();
    
    
    
    @Select({
    	"select",
        "notice.id, notice.project_id, notice.start_time, notice.end_time",
        "from ic_project_notice as notice",
        "inner join ic_project as ic on notice.project_id=ic.id and ic.invest_type=1",
        "where notice.status=10 and notice.del_flag=1 and notice.end_time > now() ",
        "order by sort asc limit 0,1"
    })
    @ResultMap("ProjectNoticeForFrontResultMap")
    ProjectNoticeForFront p2pGetProjectNoticeByIndexShow();
    
    
    
    
    @Update({
    	" update ic_project_notice set sort = #{sort, jdbcType=INTEGER} where id=#{id,jdbcType=BIGINT}"
    })
    int updateProjectNoticeSort(@Param("id")Long id, @Param("sort")int sort);
    
    
    @Update({
    	" update ic_project_notice set sort = sort + 1 where  sort >= #{sort, jdbcType=INTEGER} and status <=10"
    })
    int resetProjectNoticeSort(@Param("sort") int sort);
    
    
    @Select({
        "select",
        "id, project_id, sort",
        "from ic_project_notice",
        "where status <= 10 and del_flag=1 order by sort asc limit #{sort, jdbcType=INTEGER},1"
    })
    @ResultMap("BaseResultMap")
    ProjectNotice getProjectNoticeBySortIndex(@Param("sort")int sort);
    
    List<ProjectNotice> findUpcomingProjectNotice(); 
    
    @Select({
        "select",
        "id, project_id, end_time",
        "from ic_project_notice",
        "where status = 10 and del_flag=1 and project_id=#{projectId, jdbcType=BIGINT} order by start_time desc limit 0,1"
    })
    ProjectNotice getProjectNoticingByProjectId(Long projectId);
    
    /**
     * 根据排序规则获得预告中的项目 
     * @return
     */
    ProjectNotice getSortFirstProjectNotice();
    
    /**
     * 根据排序规则获得预告中的项目 
     * @return
     */
    ProjectNotice p2pGetSortFirstProjectNotice();
    
    
    
    
    
    
    
}