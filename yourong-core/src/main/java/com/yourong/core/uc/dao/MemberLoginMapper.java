package com.yourong.core.uc.dao;

import com.yourong.common.pageable.Page;
import com.yourong.core.uc.model.MemberLogin;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface MemberLoginMapper {
    @Delete({
        "delete from uc_member_login",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into uc_member_login (member_id, ",
        "login_time, login_ip, ",
        "login_type, user_agent, login_source, ",
        "device, position,create_time)",
        "values (#{memberId,jdbcType=BIGINT}, ",
        "now(), #{loginIp,jdbcType=VARCHAR}, ",
        "#{loginType,jdbcType=INTEGER}, #{userAgent,jdbcType=VARCHAR}, #{loginSource,jdbcType=INTEGER}, ",
        "#{device,jdbcType=VARCHAR}, #{position,jdbcType=VARCHAR},now())"
    })
    int insert(MemberLogin record);

    int insertSelective(MemberLogin record);

    @Select({
        "select",
        "id, member_id, login_time, login_ip, login_type, user_agent, login_source, device, create_time",
        "from uc_member_login",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    MemberLogin selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MemberLogin record);

    @Update({
        "update uc_member_login",
        "set member_id = #{memberId,jdbcType=BIGINT},",
          "login_time = #{loginTime,jdbcType=TIMESTAMP},",
          "login_ip = #{loginIp,jdbcType=VARCHAR},",
          "login_type = #{loginType,jdbcType=INTEGER},",
          "user_agent = #{userAgent,jdbcType=VARCHAR},",
          "login_source = #{loginSource,jdbcType=INTEGER},",
          "device = #{device,jdbcType=VARCHAR},",
          "create_time = #{createTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(MemberLogin record);

    Page findByPage(Page pageRequest, @Param("map") Map map);
  
    List selectForPagin(@Param("map") Map map);

    int selectForPaginTotalCount(@Param("map") Map map);
}