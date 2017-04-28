package com.yourong.core.sys.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysDict;

@CacheNamespace(implementation=com.yourong.common.cache.MybatisRedisCache.class)
public interface SysDictMapper {
    @Delete({
        "delete from sys_dict",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @Options(flushCache=true)
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into sys_dict (label, ",
        "group_name, `key`, value, ",
        "description, sort, ",
        "`status`, create_time, ",
        "update_time, remarks",
        ")",
        "values ( #{label,jdbcType=VARCHAR}, ",
        "#{groupName,jdbcType=VARCHAR}, #{key,jdbcType=VARCHAR}, #{value,jdbcType=VARCHAR}, ",
        "#{description,jdbcType=VARCHAR}, #{sort,jdbcType=INTEGER}, ",
        "#{status,jdbcType=INTEGER}, now(), ",
        "now(), #{remarks,jdbcType=VARCHAR}",
        ")"
    })
    @Options(flushCache=true)
    int insert(SysDict record);
    
    @Options(flushCache=true)
    int insertSelective(SysDict record);

    @Select({
        "select",
        "id, label, group_name, `key`, value, description, sort, `status`, create_time, update_time, ",
        "remarks, del_flag",
        "from sys_dict",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    SysDict selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysDict record);

    @Update({
        "update sys_dict",
        "set label = #{label,jdbcType=VARCHAR},",
          "group_name = #{groupName,jdbcType=VARCHAR},",
          " 'key' = #{key,jdbcType=VARCHAR},",
          "value = #{value,jdbcType=VARCHAR},",
          "description = #{description,jdbcType=VARCHAR},",
          "sort = #{sort,jdbcType=INTEGER},",
          "status = #{status,jdbcType=INTEGER},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP},",
          "remarks = #{remarks,jdbcType=VARCHAR},",
          "del_flag = #{delFlag,jdbcType=INTEGER}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @Options(flushCache=true)
    int updateByPrimaryKey(SysDict record);

    Page<SysDict> findByPage(Page<SysDict> pageRequest, @Param("map") Map<String, Object> map);

    @Options(flushCache=true)
    int batchDelete(@Param("ids") long[] ids);

	List<SysDict> findByGroupName(String groupName);
    SysDict  findByGroupNameAndKey(@Param("map") Map<String, Object> map);
    
    int updateByGroupNameAndKey(SysDict record);

	
	 @Select({
	        "select",
	        "id, label, group_name, `key`, value, description, sort, `status`, create_time, update_time, ",
	        "remarks, del_flag",
	        "from sys_dict",
	        "where value = #{key,jdbcType=VARCHAR} and group_name='sms_type' "
	    })
	    @ResultMap("BaseResultMap")
	    SysDict selectByKey(String key);
    
    List<SysDict> findByValue(String value);
    
    
    @Select({
        "select",
        "id, label, group_name, `key`, value, description, sort, `status`, create_time, update_time, ",
        "remarks, del_flag",
        "from sys_dict",
        "where value = #{key,jdbcType=VARCHAR} and group_name='channel_key' "
    })
    @ResultMap("BaseResultMap")
    SysDict selectByGroupNameAndValue(String key);
  
    
}