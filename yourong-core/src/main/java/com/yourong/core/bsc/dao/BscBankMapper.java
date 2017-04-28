package com.yourong.core.bsc.dao;

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
import com.yourong.core.bsc.model.BscBank;

@Repository
public interface BscBankMapper {
    @Delete({
        "delete from bsc_bank",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into bsc_bank (id, simple_name, ",
        "full_name, code, ",
        "status, create_time, ",
        "update_time)",
        "values (#{id,jdbcType=BIGINT}, #{simpleName,jdbcType=VARCHAR}, ",
        "#{fullName,jdbcType=VARCHAR}, #{code,jdbcType=VARCHAR}, ",
        "#{status,jdbcType=INTEGER}, #{createTime,jdbcType=TIMESTAMP}, ",
        "#{updateTime,jdbcType=TIMESTAMP})"
    })
    int insert(BscBank record);

    int insertSelective(BscBank record);

    @Select({
        "select",
        "id, simple_name, full_name, code, status, create_time, update_time",
        "from bsc_bank",
        "where id = #{id,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    BscBank selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BscBank record);

    @Update({
        "update bsc_bank",
        "set simple_name = #{simpleName,jdbcType=VARCHAR},",
          "full_name = #{fullName,jdbcType=VARCHAR},",
          "code = #{code,jdbcType=VARCHAR},",
          "status = #{status,jdbcType=INTEGER},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(BscBank record);

    Page<BscBank> findByPage(Page<BscBank> pageRequest, @Param("map") Map<String, Object> map);

    int batchDelete(@Param("ids") long[] ids);
    
    @Select({
    	"select",
        "id, simple_name, full_name, code, status, create_time, update_time",
        "from bsc_bank",
        "where code = #{code,jdbcType=VARCHAR}"
    })
    @ResultMap("BaseResultMap")
    public BscBank getBscBankByCode(String code);
    
    /**
     * 查询银行
     * @param record
     * @return
     */
    BscBank selectBscBank(BscBank record);
    
    /**
     * 获取银行列表
     * @param record
     * @return
     */
    List<BscBank> getBankList();
}