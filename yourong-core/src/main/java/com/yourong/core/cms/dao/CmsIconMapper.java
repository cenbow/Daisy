/**
 * 
 */
package com.yourong.core.cms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yourong.core.cms.model.CmsIcon;

/**
 * @author wanglei
 *
 */
@Repository
public interface CmsIconMapper {
	
	
	/**
	 * 当前生效的ICON
	 * @return
	 */
	List<CmsIcon> findOnlineIcon();

	
	/**
	 * 插入Icon
	 * @param record
	 * @return
	 */
    int insert(CmsIcon record);
    
    
    /**
     * 更新Icon
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(CmsIcon record);
    
    
    /**
     * 用id查取Icon
     * @param id
     * @return
     */
    CmsIcon selectByPrimaryKey(Long id);
    
    
    
    /**
     * 以id删除Icon
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Long id);
    
    
    /**
     * 根据id批量删除Icon
     * @param id
     * @return
     */
    int batchDelete(@Param("ids") long[] id);


    /**
     * 分页总条数
     * @param map
     * @return
     */
	int selectForPaginTotalCount(@Param("map") Map<String,Object> map);

	
	/**
	 * 分页列表
	 * @param map
	 * @return
	 */
	List<CmsIcon> selectForPagin(@Param("map") Map<String,Object> map);




	/**
	 * 获取最大权重值
	 * @return
	 */
	Integer findMaxWeight();


	/**
	 * 获取指定位置Icon的权重
	 * @param position
	 * @return
	 */
	Integer findPositionIconWeight(@Param("position")Integer position);


	/**
	 * 
	 * @param positionWeight
	 * @param iconWeight
	 * @return    
	 */
	Integer resetIconWeightWhenUp(@Param("positionWeight")Integer positionWeight,@Param("iconWeight")Integer iconWeight);


	/**
	 * 
	 * @param positionWeight
	 * @param iconWeight
	 * @return
	 */
	Integer resetIconWeightWhenDown(@Param("positionWeight")Integer positionWeight,@Param("iconWeight")Integer iconWeight);
    
}
