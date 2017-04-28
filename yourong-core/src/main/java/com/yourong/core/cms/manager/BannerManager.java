package com.yourong.core.cms.manager;

import java.util.List;
import java.util.Map;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.Banner;
import com.yourong.core.cms.model.biz.BannerFroAreaBiz;
import com.yourong.core.ic.model.query.BannerQuery;

import org.apache.ibatis.annotations.Param;

public interface BannerManager {
	int deleteByPrimaryKey(Long id) throws ManagerException;

	int insert(Banner record) throws ManagerException;

	int insertSelective(Banner record) throws ManagerException;

	Banner selectByPrimaryKey(Long id) throws ManagerException;

	int updateByPrimaryKeySelective(Banner record) throws ManagerException;

	int updateByPrimaryKey(Banner record) throws ManagerException;

	Page<Banner> findByPage(Page<Banner> pageRequest, Map<String, Object> map) throws ManagerException;

	int batchDelete(long[] ids) throws ManagerException;

	/* 获取最大权重值 */
	Integer findMaxWeight() throws ManagerException;
	
	/* 获取该位置banner的权重  */
    Integer findPositionBannerWeight(Integer position, Integer areaSign, Integer type) throws ManagerException;
    
    /* 向下移动时重置的banner */
    Integer resetBannerWeightWhenDown(Integer positionWeight,Integer bannerWeight) throws ManagerException;
    
    /* 向上移动时重置的banner */
    Integer resetBannerWeightWhenUp(Integer positionWeight,Integer bannerWeight) throws ManagerException;
    
    /* 定时过期 */
    Integer expireBannerTask() throws ManagerException;
    
    /* 获取显示在首页的banner */
    List<Banner> findOnlineBanner(Map<String, Object> map) throws ManagerException;
    
    /**
     * 根据页面区域获取banner
     * @param queryLimit
     * @return
     */
    List<BannerFroAreaBiz> showBannerByArea(int type, String areaSign, Integer rowStart, Integer rowLength) throws ManagerException;

    /**
     * 分页获取活动banner
     * @param baseQuery
     * @param areaSign
     * @return
     * @throws ManagerException
     */
    Page<BannerFroAreaBiz> findActivityBannerByPage(BaseQueryParam baseQuery,String areaSign)throws ManagerException;
    
    /**
     * 分页获取活动banner
     * @param baseQuery
     * @param areaSign
     * @return
     * @throws ManagerException
     */
    Page<BannerFroAreaBiz> findAppActivityBannerByPage(BannerQuery bannerQuery)throws ManagerException;

    /**
     * 根据type查询单条banner
     * @param type
     * @return
     */
    Banner queryBannerByType(Integer type);
}