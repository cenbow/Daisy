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
import com.yourong.core.sys.model.SysRole;
import com.yourong.core.sys.model.SysRoleMenu;

public interface SysRoleMapper {
    @Delete({
        "delete from sys_role",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into sys_role (id, office_id, ",
        "name, data_scope, create_by, ",
        "create_date, update_by, ",
        "update_date, remarks, ",
        "del_flag)",
        "values (#{id,jdbcType=BIGINT}, #{officeId,jdbcType=BIGINT}, ",
        "#{name,jdbcType=VARCHAR}, #{dataScope,jdbcType=CHAR}, #{createBy,jdbcType=VARCHAR}, ",
        "#{createDate,jdbcType=TIMESTAMP}, #{updateBy,jdbcType=VARCHAR}, ",
        "#{updateDate,jdbcType=TIMESTAMP}, #{remarks,jdbcType=VARCHAR}, ",
        "#{delFlag,jdbcType=CHAR})"
    })
    int insert(SysRole record);

    int insertSelective(SysRole record);

    @Select({
        "select",
        "id, office_id, name, data_scope, create_by, create_date, update_by, update_date, ",
        "remarks, del_flag",
        "from sys_role",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    SysRole selectByPrimaryKey(Long id);
    
    SysRole selectRoleMenus(Long id);

    int updateByPrimaryKeySelective(SysRole record);

    @Update({
        "update sys_role",
        "set office_id = #{officeId,jdbcType=BIGINT},",
          "name = #{name,jdbcType=VARCHAR},",
          "data_scope = #{dataScope,jdbcType=CHAR},",
          "create_by = #{createBy,jdbcType=VARCHAR},",
          "create_date = #{createDate,jdbcType=TIMESTAMP},",
          "update_by = #{updateBy,jdbcType=VARCHAR},",
          "update_date = #{updateDate,jdbcType=TIMESTAMP},",
          "remarks = #{remarks,jdbcType=VARCHAR},",
          "del_flag = #{delFlag,jdbcType=CHAR}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(SysRole record);

    Page<SysRole> findByPage(Page<SysRole> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") long[] ids);
    /**
     * 批量插入，角色和菜单 的关系表
     * @param id
     * @param menus
     * @return
     */
	int batchInsertRoleAndMenus(List<SysRoleMenu> list);
	
	 @Delete({
	        "delete from sys_role_menu",
	        "where role_id = #{id,jdbcType=BIGINT}"
	    })
	int batchDeleteRoleAndMenus(Long id);
	 
	 @Select({
		 "select ",
		 "id,name ",
		 "from sys_role where del_flag >= 0 order by id"
	 })
	List<SysRole> selectAllSysRole();
	
	 /**
     * 检查登录账号是否存在
     * @param name 角色名称
     * @param selfId 当前角色编号
     * @return
     */
	 SysRole checkRoleNameExists(@Param("name")String name, @Param("id")long selfId);
	
}