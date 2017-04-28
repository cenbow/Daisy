package com.yourong.core.sys.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysArea;

public interface SysAreaMapper {
    @Delete({
        "delete from sys_area",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into sys_area (id, parent_id, ",
        "code, name, type, ",
        "create_time, update_time, ",
        "remarks, del_flag)",
        "values (#{id,jdbcType=BIGINT}, #{parentId,jdbcType=BIGINT}, ",
        "#{code,jdbcType=VARCHAR}, #{name,jdbcType=VARCHAR}, #{type,jdbcType=INTEGER}, ",
        "now(), now(), ",
        "#{remarks,jdbcType=VARCHAR}, #{delFlag,jdbcType=INTEGER})"
    })
    int insert(SysArea record);

    int insertSelective(SysArea record);

    @Select({
        "select",
        "id, parent_id, code, name, type, create_time, update_time, remarks, del_flag",
        "from sys_area",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    SysArea selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysArea record);

    @Update({
        "update sys_area",
        "set parent_id = #{parentId,jdbcType=BIGINT},",
          "code = #{code,jdbcType=VARCHAR},",
          "name = #{name,jdbcType=VARCHAR},",
          "type = #{type,jdbcType=INTEGER},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP},",
          "remarks = #{remarks,jdbcType=VARCHAR},",
          "del_flag = #{delFlag,jdbcType=INTEGER}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(SysArea record);

    Page<SysArea> findByPage(Page<SysArea> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") long[] ids);

	List<SysArea> selectAllSysArea();
	
	/**根据类别查找区域  type=2查询所有省，type=3 查询所有市，type=4 查询所有区 **/
	List<SysArea> selectSysAreasByType(Integer type);
	
	List<SysArea> selectSysAreasByParentId(Long parentId);
	
	@Select({
		"select parent_id from sys_area where code = #{code,jdbcType=VARCHAR}"
	})
	Long getParentIdByCode(@Param("code")String code);
	
}