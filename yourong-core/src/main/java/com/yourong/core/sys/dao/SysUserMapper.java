package com.yourong.core.sys.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysUser;

public interface SysUserMapper {
    @Delete({
        "delete from sys_user",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into sys_user (id, office_id, ",
        "login_name, password, ",
        "no, name, email, ",
        "phone, mobile, status, ",
        "user_type, login_ip, ",
        "login_time, avatars, ",
        "create_time, update_time, ",
        "remarks, del_flag)",
        "values (#{id,jdbcType=BIGINT}, #{officeId,jdbcType=BIGINT}, ",
        "#{loginName,jdbcType=VARCHAR}, #{password,jdbcType=VARCHAR}, ",
        "#{no,jdbcType=VARCHAR}, #{name,jdbcType=VARCHAR}, #{email,jdbcType=VARCHAR}, ",
        "#{phone,jdbcType=VARCHAR}, #{mobile,jdbcType=VARCHAR}, #{status,jdbcType=INTEGER}, ",
        "#{userType,jdbcType=INTEGER}, #{loginIp,jdbcType=VARCHAR}, ",
        "#{loginTime,jdbcType=TIMESTAMP}, #{avatars,jdbcType=VARCHAR}, ",
        "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP}, ",
        "#{remarks,jdbcType=VARCHAR}, #{delFlag,jdbcType=INTEGER})"
    })
    int insert(SysUser record);

    int insertSelective(SysUser record);

    @Select({
        "select",
        "id, office_id, login_name, password, no, name, email, phone, mobile, status, ",
        "user_type, login_ip, login_time, avatars, create_time, update_time, remarks, ",
        "del_flag",
        "from sys_user",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    SysUser selectByPrimaryKey(Long id);
    
    
    
    SysUser selectSysUserRole(Long id);
    
    SysUser selectSysUserRoleByLoginName(String loginName);
    
    
    @Select({
        "select",
        "id, office_id, login_name, password, no, name, email, phone, mobile, status, ",
        "user_type, login_ip, login_time, avatars, create_time, update_time, remarks, ",
        "del_flag",
        "from sys_user",
        "where login_name = #{loginName,jdbcType=VARCHAR}"
    })
    @ResultMap("BaseResultMap")
    SysUser selectByLoginName(String loginName);    
    

    int updateByPrimaryKeySelective(SysUser record);

    @Update({
        "update sys_user",
        "set office_id = #{officeId,jdbcType=BIGINT},",
          "login_name = #{loginName,jdbcType=VARCHAR},",
          "password = #{password,jdbcType=VARCHAR},",
          "no = #{no,jdbcType=VARCHAR},",
          "name = #{name,jdbcType=VARCHAR},",
          "email = #{email,jdbcType=VARCHAR},",
          "phone = #{phone,jdbcType=VARCHAR},",
          "mobile = #{mobile,jdbcType=VARCHAR},",
          "status = #{status,jdbcType=INTEGER},",
          "user_type = #{userType,jdbcType=INTEGER},",
          "login_ip = #{loginIp,jdbcType=VARCHAR},",
          "login_time = #{loginTime,jdbcType=TIMESTAMP},",
          "avatars = #{avatars,jdbcType=VARCHAR},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP},",
          "remarks = #{remarks,jdbcType=VARCHAR},",
          "del_flag = #{delFlag,jdbcType=INTEGER}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(SysUser record);

    Page<SysUser> findByPage(Page<SysUser> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") long[] ids);
    
    
    /**
     * 检查登录账号是否存在
     * @param loginName 登录账号
     * @param selfId 当前用户ID
     * @return
     */
    SysUser checkLoginNameExists(@Param("loginName")String loginName, @Param("id")long selfId);
    
    
}