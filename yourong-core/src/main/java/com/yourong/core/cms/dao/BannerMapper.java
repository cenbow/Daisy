package com.yourong.core.cms.dao;

import java.util.List;
import java.util.Map;

import com.yourong.common.domain.BaseQueryParam;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.Banner;
import com.yourong.core.cms.model.biz.BannerFroAreaBiz;
import com.yourong.core.ic.model.query.BannerQuery;
@Repository
public interface BannerMapper {
    int deleteByPrimaryKey(Long id);

    /*插入banner*/
    int insert(Banner record);

    int insertSelective(Banner record);

    Banner selectByPrimaryKey(Long id);

    /*更新banner*/
    int updateByPrimaryKeySelective(Banner record);

    int updateByPrimaryKey(Banner record);

    Page<Banner> findByPage(Page<Banner> pageRequest, @Param("map") Map<String,Object> map);

    int batchDelete(@Param("ids") long[] id);

    List<Banner> selectForPagin(@Param("map") Map<String,Object> map);

    int selectForPaginTotalCount(@Param("map") Map<String,Object> map);
   
    Integer findMaxWeight();
    
    /** 获取该位置banner的权重  **/
    Integer findPositionBannerWeight(@Param("position")Integer position, @Param("areaSign")Integer areaSign, @Param("type")Integer type);
    
    /** 向下移动时重置的banner */
    Integer resetBannerWeightWhenDown(@Param("positionWeight")Integer positionWeight,@Param("bannerWeight")Integer bannerWeight);
    
    /** 向上移动时重置的banner */
    Integer resetBannerWeightWhenUp(@Param("positionWeight")Integer positionWeight,@Param("bannerWeight")Integer bannerWeight);

    /** banner过期之后删除 */
	Integer expireBannerTask();
	
	/** banner生效 */
	Integer activeBannerTask();
	
	/** 获取显示在首页的banner*/
	List<Banner> findOnlineBanner(@Param("map") Map<String, Object> map);
	
	/** 查询即将过期的banner**/
	List<Banner> findExpireBanner();
	
	/**根据页面区域获取banner**/
	List<BannerFroAreaBiz> showBannerByArea(@Param("type")int type, @Param("areaSign")String areaSign, @Param("rowStart")Integer rowStart, @Param("rowLength")Integer rowLength);

    /**分页获取活动banner**/
    List<BannerFroAreaBiz> findActivityListByPage(@Param("baseQuery")BaseQueryParam baseQuery,@Param("areaSign")String areaSign);

    /**分页获取活动banner**/
    int findActivityListCountByPage(@Param("baseQuery")BaseQueryParam baseQuery,@Param("areaSign")String areaSign);
    

    /**分页获取活动banner**/
    List<BannerFroAreaBiz> findAppActivityBannerByPage(@Param("bannerQuery")BannerQuery bannerQuery);

    /**分页获取活动banner**/
    int findAppActivityListCountByPage(@Param("bannerQuery")BannerQuery bannerQuery);

    /**
     * 根据type查询单条banner
     * @param type
     * @return
     */
    Banner queryBannerByType(Integer type);
}