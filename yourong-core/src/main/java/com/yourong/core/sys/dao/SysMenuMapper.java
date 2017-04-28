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
import com.yourong.core.sys.model.SysMenu;

public interface SysMenuMapper {
    @Delete({
        "delete from sys_menu",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into sys_menu (parent_id, type,",
        "name, href, target, ",
        "icon, sort, status, ",
        "permission, create_time, ",
        "update_time, remarks",
        ")",
        "values (#{parentId,jdbcType=BIGINT}, #{type,jdbcType=INTEGER},",
        "#{name,jdbcType=VARCHAR}, #{href,jdbcType=VARCHAR}, #{target,jdbcType=VARCHAR}, ",
        "#{icon,jdbcType=VARCHAR}, #{sort,jdbcType=INTEGER}, #{status,jdbcType=INTEGER}, ",
        "#{permission,jdbcType=VARCHAR}, now(), ",
        "now(), #{remarks,jdbcType=VARCHAR}",
        ")"
    })
    int insert(SysMenu record);

    int insertSelective(SysMenu record);

    @Select({
        "select",
        "id, parent_id, type, name, href, target, icon, sort, status, permission, create_time, ",
        "update_time, remarks, del_flag",
        "from sys_menu",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    SysMenu selectByPrimaryKey(Long id);
    
    
    @Select({
        "select",
        "id, parent_id, type, name, href, target, icon, sort, status, permission, create_time, ",
        "update_time, remarks, del_flag",
        "from sys_menu where del_flag >= 0 and  status = 1    order by parent_id ,type,sort"        
    })
    @ResultMap("BaseResultMap")
    List<SysMenu> selectAllSysMenu();
    /**
     *  根据父菜单 和类型， 查找下级子菜单
     * @param parentid
     * @param type
     * @return
     */    
    @Select({
        "select",
        "id, parent_id, type, name, href, target, icon, sort, status, permission, create_time, ",
        "update_time, remarks, del_flag",
        "from sys_menu where del_flag >= 0 and  status = 1   and parent_id = #{parendID,jdbcType=BIGINT}  "
        , " and type =  #{type,jdbcType=INTEGER}  order by parent_id ,type,sort"        
    })
    @ResultMap("BaseResultMap")
    List<SysMenu> selectChildByParent(@Param("parendID")Long parendID,@Param("type")Integer type);
    
    
    int updateByPrimaryKeySelective(SysMenu record);

    @Update({
        "update sys_menu",
        "set parent_id = #{parentId,jdbcType=BIGINT},",
        "type = #{type,jdbcType=INTEGER},",
          "name = #{name,jdbcType=VARCHAR},",
          "href = #{href,jdbcType=VARCHAR},",
          "target = #{target,jdbcType=VARCHAR},",
          "icon = #{icon,jdbcType=VARCHAR},",
          "sort = #{sort,jdbcType=INTEGER},",
          "status = #{status,jdbcType=INTEGER},",
          "permission = #{permission,jdbcType=VARCHAR},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP},",
          "remarks = #{remarks,jdbcType=VARCHAR},",
          "del_flag = #{delFlag,jdbcType=INTEGER}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(SysMenu record);

    Page<SysMenu> findByPage(Page<SysMenu> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") long[] ids);
}