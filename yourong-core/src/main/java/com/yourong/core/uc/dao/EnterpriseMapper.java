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
import com.yourong.core.uc.model.Enterprise;

public interface EnterpriseMapper {
    @Select({
            "select",
            "  *  from uc_enterprise",
            "where legal_Id = #{memberID,jdbcType=BIGINT}  and del_flage = 1  limit 1"
    })
    @ResultMap("BaseResultMap")
    Enterprise selectByMemberID(Long memberID);
    
    @Select({
        "select",
        "  *  from uc_enterprise",
        "where full_name ='浙江小融网络科技股份有限公司' and del_flage = 1 "
	})
	@ResultMap("BaseResultMap")
	Enterprise selectYRW();
	    
    
    @Select({
        "select  * ",
            "from uc_enterprise",
        "where legal_Id = #{legalId,jdbcType=BIGINT}"
    })
    @ResultMap("BaseResultMap")
    List<Enterprise> selectBylegalId(Long legalId);


    int deleteByPrimaryKey(Long id);

    int insert(Enterprise record);

    int insertSelective(Enterprise record);

    Enterprise selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Enterprise record);

    int updateByPrimaryKey(Enterprise record);

    Page findByPage(Page pageRequest, @Param("map") Map map);

    int batchDelete(@Param("ids") int[] ids);

    List selectForPagin(@Param("map") Map map);

    int selectForPaginTotalCount(@Param("map") Map map);
    
    /**根据企业名称查询企业*/
    List<Enterprise> getEnterpriseByName(@Param("name")String name);
    
    /**根据企业法人名称查询企业*/
    List<Enterprise> getEnterpriseByLegalName(@Param("legalname")String legalname);
    /**
     * 
     * @Description:根据注册号查询企业
     * @param regisNo
     * @return
     * @author: chaisen
     * @time:2016年12月20日 上午11:30:49
     */
	List<Enterprise> getEnterpriseByRegisNo(@Param("regisNo")String regisNo);
    
}