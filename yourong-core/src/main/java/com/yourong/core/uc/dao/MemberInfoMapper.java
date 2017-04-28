package com.yourong.core.uc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yourong.common.pageable.Page;
import com.yourong.core.uc.model.MemberInfo;

public interface MemberInfoMapper {
    @Delete({
        "delete from uc_member_info",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into uc_member_info (id, member_id, ",
        "province, city, ",
        "address, postal_code, ",
        "qq, occupation, marriage, ",
        "income, high_edu, ",
        "school,area_full_name,census_register_name,census_register_id, create_time, ",
        "update_time, del_flag)",
        "values (#{id,jdbcType=BIGINT}, #{memberId,jdbcType=BIGINT}, ",
        "#{province,jdbcType=VARCHAR}, #{city,jdbcType=VARCHAR}, ",
        "#{address,jdbcType=VARCHAR}, #{postalCode,jdbcType=INTEGER}, ",
        "#{qq,jdbcType=BIGINT}, #{occupation,jdbcType=VARCHAR}, #{marriage,jdbcType=VARCHAR}, ",
        "#{income,jdbcType=VARCHAR}, #{highEdu,jdbcType=VARCHAR}, ",
        "#{school,jdbcType=VARCHAR},#{areaFullName,jdbcType=VARCHAR},#{censusRegisterName,jdbcType=VARCHAR},#{censusRegisterId,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, ",
        "#{updateTime,jdbcType=TIMESTAMP}, #{delFlag,jdbcType=INTEGER})"
    })
    int insert(MemberInfo record);

    int insertSelective(MemberInfo record);

    @Select({
        "select",
        "id, member_id, province, city, address, postal_code, qq, occupation, marriage, ",
        "income, high_edu, school,area_full_name,census_register_name,census_register_id, create_time, update_time, del_flag",
        "from uc_member_info",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    MemberInfo selectByPrimaryKey(Long id);
    
    

    @Select({
        "select",
        "id, member_id, province, city, address, postal_code, qq, occupation, marriage, ",
        "income, high_edu, school,area_full_name,census_register_name,census_register_id, create_time, update_time, del_flag",
        "from uc_member_info",
        "where member_id = #{memberId,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    List<MemberInfo> selectByMemberId(Long memberId);
    

    @Select({
        "select",
        "id, member_id, province, city, address, postal_code, qq, occupation, marriage, ",
        "income, high_edu, school,area_full_name,census_register_name,census_register_id, register_type,detail_info,create_time, update_time, del_flag",
        "from uc_member_info",
        "where member_id = #{memberId,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    MemberInfo selectSingleByMemberId(Long memberId);
    
    

    int updateByPrimaryKeySelective(MemberInfo record);

    @Update({
        "update uc_member_info",
        "set member_id = #{memberId,jdbcType=BIGINT},",
          "province = #{province,jdbcType=VARCHAR},",
          "city = #{city,jdbcType=VARCHAR},",
          "address = #{address,jdbcType=VARCHAR},",
          "postal_code = #{postalCode,jdbcType=INTEGER},",
          "qq = #{qq,jdbcType=BIGINT},",
          "occupation = #{occupation,jdbcType=VARCHAR},",
          "marriage = #{marriage,jdbcType=VARCHAR},",
          "income = #{income,jdbcType=VARCHAR},",
          "high_edu = #{highEdu,jdbcType=VARCHAR},",
          "school = #{school,jdbcType=VARCHAR},",
          "area_full_name = #{areaFullName,jdbcType=VARCHAR},",
          "census_register_name = #{censusRegisterName,jdbcType=VARCHAR},",
          "census_register_id = #{censusRegisterId,jdbcType=VARCHAR},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP},",
          "del_flag = #{delFlag,jdbcType=INTEGER}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(MemberInfo record);

    Page<MemberInfo> findByPage(Page<MemberInfo> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") int[] ids);
    
    @Select({
        "select",
        "id, member_id, province, city, address, postal_code, qq, occupation, marriage, ",
        "income, high_edu, school, area_full_name,census_register_name,census_register_id, create_time, update_time, del_flag, register_type, detail_info ,evaluation_score",
        "from uc_member_info",
        "where member_id = #{memberId,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    public MemberInfo getMemberInfoByMemberId(Long memberId);
}