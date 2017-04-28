package com.yourong.core.uc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.yourong.common.pageable.Page;
import com.yourong.core.uc.model.MemberNotifySettings;

@Repository
public interface MemberNotifySettingsMapper {
    @Delete({
        "delete from uc_member_notify_settings",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into uc_member_notify_settings (id, member_id, ",
        "notify_type, notify_way, ",
        "status, create_time, ",
        "update_time)",
        "values (#{id,jdbcType=BIGINT}, #{memberId,jdbcType=BIGINT}, ",
        "#{notifyType,jdbcType=INTEGER}, #{notifyWay,jdbcType=INTEGER}, ",
        "#{status,jdbcType=INTEGER}, #{createTime,jdbcType=TIMESTAMP}, ",
        "#{updateTime,jdbcType=TIMESTAMP})"
    })
    int insert(MemberNotifySettings record);

    int insertSelective(MemberNotifySettings record);

    @Select({
        "select",
        "id, member_id, notify_type, notify_way, status, create_time, update_time",
        "from uc_member_notify_settings",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    MemberNotifySettings selectByPrimaryKey(Long id);

    @Select({
            "select",
            "id, member_id, notify_type, notify_way, status, create_time, update_time",
            "from uc_member_notify_settings",
            "where member_id = #{memberId,jdbcType=BIGINT} and notify_type =#{notifyType,jdbcType=INTEGER} and status = 1"
    })
    @ResultMap("BaseResultMap")
  List<MemberNotifySettings> selectByMemberIDyAndNotifyType(@Param("memberId")Long memberId,@Param("notifyType")int notifyType);



    int updateByPrimaryKeySelective(MemberNotifySettings record);

    @Update({
        "update uc_member_notify_settings",
        "set member_id = #{memberId,jdbcType=BIGINT},",
          "notify_type = #{notifyType,jdbcType=INTEGER},",
          "notify_way = #{notifyWay,jdbcType=INTEGER},",
          "status = #{status,jdbcType=INTEGER},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(MemberNotifySettings record);

    Page<MemberNotifySettings> findByPage(Page<MemberNotifySettings> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") int[] ids);
    
    @Update({
    	"update uc_member_notify_settings ",
    	" set status = #{status,jdbcType=INTEGER},",
    	" update_time = #{updateTime,jdbcType=TIMESTAMP}",
    	" where member_id = #{memberId,jdbcType=BIGINT} and notify_type = #{notifyType,jdbcType=INTEGER} and notify_way = #{notifyWay,jdbcType=INTEGER}"
    })
    int updateNotifySettingsStatus(MemberNotifySettings record);
    
    int batchInsertNotifySettings(@Param("notifySettingsList")List<MemberNotifySettings> notifySettingsList);
    
    @Delete({
        "delete from uc_member_notify_settings",
        " where member_id = #{memberId,jdbcType=BIGINT}"
    })
    int deleteMemberNotifySettingsByMemberId(Long memberId);
    
    @Select({
        "select",
        "id, member_id, notify_type, notify_way",
        "from uc_member_notify_settings",
        "where member_id = #{memberId,jdbcType=BIGINT} and status <= 0"
    })
	@ResultMap("BaseResultMap")
	List<MemberNotifySettings> getUncheckedNotifySettings(@Param("memberId")Long memberId);
    
    @Select({
        "select",
        "id, member_id, notify_type, notify_way",
        "from uc_member_notify_settings",
        "where member_id = #{memberId,jdbcType=BIGINT} and status > 0"
    })
	@ResultMap("BaseResultMap")
	List<MemberNotifySettings> getCheckedNotifySettings(@Param("memberId")Long memberId);
    
    /**
     * 取消邮箱订阅
     * @param memberId
     * @return
     */
    int unsubscribe(@Param("memberId")Long memberId);
}