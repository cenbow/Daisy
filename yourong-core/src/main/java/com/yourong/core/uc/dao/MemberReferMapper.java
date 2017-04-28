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
import com.yourong.core.mc.model.biz.ActivityForKing;
import com.yourong.core.uc.model.MemberRefer;

public interface MemberReferMapper {
    @Delete({
        "delete from uc_member_refer",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into uc_member_refer (id, referral, ",
        "referred, refer_type, ",
        "refer_source, refer_link, ",
        "reward, remarks, ",
        "create_time)",
        "values (#{id,jdbcType=BIGINT}, #{referral,jdbcType=BIGINT}, ",
        "#{referred,jdbcType=BIGINT}, #{referType,jdbcType=INTEGER}, ",
        "#{referSource,jdbcType=INTEGER}, #{referLink,jdbcType=VARCHAR}, ",
        "#{reward,jdbcType=DECIMAL}, #{remarks,jdbcType=VARCHAR}, ",
        "#{createTime,jdbcType=TIMESTAMP})"
    })
    int insert(MemberRefer record);

    int insertSelective(MemberRefer record);

    @Select({
        "select",
        "id, referral, referred, refer_type, refer_source, refer_link, reward, remarks, ",
        "create_time",
        "from uc_member_refer",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    MemberRefer selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MemberRefer record);

    @Update({
        "update uc_member_refer",
        "set referral = #{referral,jdbcType=BIGINT},",
          "referred = #{referred,jdbcType=BIGINT},",
          "refer_type = #{referType,jdbcType=INTEGER},",
          "refer_source = #{referSource,jdbcType=INTEGER},",
          "refer_link = #{referLink,jdbcType=VARCHAR},",
          "reward = #{reward,jdbcType=DECIMAL},",
          "remarks = #{remarks,jdbcType=VARCHAR},",
          "create_time = #{createTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(MemberRefer record);

    Page<MemberRefer> findByPage(Page<MemberRefer> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") int[] ids);

	List<ActivityForKing> getRefferalCountList();
}