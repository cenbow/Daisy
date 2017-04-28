package com.yourong.core.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import com.yourong.core.sys.model.SysUserRole;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserRoleMapper {
	@Delete({
        "delete from sys_user_role",
        " where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into sys_user_role (id, user_id, ",
        "role_id)",
        "values (#{id,jdbcType=BIGINT}, #{userId,jdbcType=BIGINT}, ",
        "#{roleId,jdbcType=BIGINT})"
    })
    int insert(SysUserRole record);

    int insertSelective(SysUserRole record);

    int batchDelete(@Param("ids") int[] ids);
    
    
    @Delete({"delete from sys_user_role",
    		" where user_id=#{userId,jdbcType=BIGINT}"
    })
    /**
	 * 根据用户编号删除角色
	 * @param id
	 */
	public void deleteSysUserRoleByUserId(Long userId);
    
    /**
     * 批量为用户分配角色
     * @param record
     * @return
     */
    public int batchInsertSysUserRole(List<SysUserRole> record);
    
    @Select({
    	"select id, user_id, role_id from sys_user_role where user_id = #{userId,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    public List<SysUserRole> getSysUserRoleByUserId(Long userId);
}