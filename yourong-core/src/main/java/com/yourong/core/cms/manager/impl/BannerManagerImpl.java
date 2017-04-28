package com.yourong.core.cms.manager.impl;

import java.util.List;
import java.util.Map;

import com.yourong.common.domain.BaseQueryParam;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rop.thirdparty.com.google.common.collect.Lists;

import com.yourong.common.constant.Constant;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.core.cms.dao.BannerMapper;
import com.yourong.core.cms.manager.BannerManager;
import com.yourong.core.cms.model.Banner;
import com.yourong.core.cms.model.biz.BannerFroAreaBiz;
import com.yourong.core.ic.model.query.BannerQuery;

@Service
public class BannerManagerImpl implements BannerManager {

	@Autowired
	private BannerMapper bannerMapper;

	@Override
	public int deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			return bannerMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insert(Banner record) throws ManagerException {
		try {
			return bannerMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insertSelective(Banner record) throws ManagerException {
		try {
			return bannerMapper.insertSelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Banner selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return bannerMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKeySelective(Banner record) throws ManagerException {
		try {
			return bannerMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKey(Banner record) throws ManagerException {
		try {
			return bannerMapper.updateByPrimaryKey(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<Banner> findByPage(Page<Banner> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			map.put(Constant.STARTROW, pageRequest.getiDisplayStart());
			map.put(Constant.PAGESIZE, pageRequest.getiDisplayLength());
			int totalCount = bannerMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<Banner> selectForPagin = bannerMapper.selectForPagin(map);
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int batchDelete(long[] ids) throws ManagerException {
		try {
			return bannerMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Integer findMaxWeight() throws ManagerException  {
		try {
			return bannerMapper.findMaxWeight();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Integer findPositionBannerWeight(Integer position, Integer areaSign, Integer type) throws ManagerException {
		try {
			return bannerMapper.findPositionBannerWeight(position, areaSign, type);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Integer resetBannerWeightWhenDown(Integer positionWeight,Integer bannerWeight) throws ManagerException {
		try {
			return bannerMapper.resetBannerWeightWhenDown(positionWeight, bannerWeight);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Integer resetBannerWeightWhenUp(Integer positionWeight,Integer bannerWeight) throws ManagerException {
		try {
			return bannerMapper.resetBannerWeightWhenUp(positionWeight,bannerWeight);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Integer expireBannerTask() throws ManagerException {
		try {
			Integer updateFlag = 0;
			List<Banner> banners = bannerMapper.findExpireBanner();
			List<Banner> expList = Lists.newArrayList();
			List<Banner> activeList = Lists.newArrayList();
			for (Banner banner : banners) {
				if(banner.getEndTime()!=null && banner.getEndTime().getTime()<=DateUtils.getCurrentDate().getTime() && banner.getBannerStatus().equals(1)){
					expList.add(banner);
				}
				if(banner.getStartTime().getTime()<=DateUtils.getCurrentDate().getTime() && banner.getBannerStatus().equals(0)){
					activeList.add(banner);
				}
			}
			if(Collections3.isNotEmpty(expList)){
				updateFlag = bannerMapper.expireBannerTask();
			}
			if(Collections3.isNotEmpty(activeList)){
				updateFlag += bannerMapper.activeBannerTask();
			}
			return updateFlag;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<Banner> findOnlineBanner(Map<String, Object> map) throws ManagerException {
		try {
			return bannerMapper.findOnlineBanner(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<BannerFroAreaBiz> showBannerByArea(int type, String areaSign, Integer rowStart, Integer rowLength) throws ManagerException {
		try {
			return bannerMapper.showBannerByArea(type, areaSign, rowStart, rowLength);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<BannerFroAreaBiz> findActivityBannerByPage(BaseQueryParam baseQuery, String areaSign) throws ManagerException {
		try {
			List<BannerFroAreaBiz> banners = Lists.newArrayList();
			Page<BannerFroAreaBiz> page = new Page<BannerFroAreaBiz>();
			int count = bannerMapper.findActivityListCountByPage(baseQuery,areaSign);
			if(count>0){
				banners = bannerMapper.findActivityListByPage(baseQuery,areaSign);
			}
			page.setiDisplayLength(baseQuery.getPageSize());
			page.setPageNo(baseQuery.getCurrentPage());
			page.setData(banners);
			page.setiTotalRecords(count);
			page.setiTotalDisplayRecords(count);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	@Override
	public Page<BannerFroAreaBiz> findAppActivityBannerByPage(BannerQuery bannerQuery) throws ManagerException {
		try {
			List<BannerFroAreaBiz> banners = Lists.newArrayList();
			Page<BannerFroAreaBiz> page = new Page<BannerFroAreaBiz>();
			int count = bannerMapper.findAppActivityListCountByPage(bannerQuery);
			if(count>0){
				banners = bannerMapper.findAppActivityBannerByPage(bannerQuery);
			}
			page.setiDisplayLength(bannerQuery.getPageSize());
			page.setPageNo(bannerQuery.getCurrentPage());
			page.setData(banners);
			page.setiTotalRecords(count);
			page.setiTotalDisplayRecords(count);
			return page;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Banner queryBannerByType(Integer type) {
		return bannerMapper.queryBannerByType(type);
	}
}
