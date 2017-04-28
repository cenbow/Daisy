/**
 * 
 */
package com.yourong.core.cms.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.common.constant.Constant;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.dao.CmsIconMapper;
import com.yourong.core.cms.manager.CmsIconManager;
import com.yourong.core.cms.model.CmsIcon;

/**
 * @author wanglei
 *
 */
@Service
public class CmsIconManagerImpl implements CmsIconManager{
	
	@Autowired
	private CmsIconMapper cmsIconMapper;
	
	


	@Override
	public List<CmsIcon> findOnlineIcon() throws ManagerException {
		try {
			return cmsIconMapper.findOnlineIcon();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	@Override
	public int insert(CmsIcon record) throws ManagerException {
		try {
			return cmsIconMapper.insert(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}


	@Override
	public int updateByPrimaryKeySelective(CmsIcon record) throws ManagerException {
		try {
			return cmsIconMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	@Override
	public CmsIcon selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return cmsIconMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	@Override
	public int deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			return cmsIconMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	@Override
	public int batchDelete(long[] ids) throws ManagerException {
		try {
			return cmsIconMapper.batchDelete(ids);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}


	@Override
	public Page<CmsIcon> findByPage(Page<CmsIcon> pageRequest,
			Map<String, Object> map)throws ManagerException {
		try {
			map.put(Constant.STARTROW, pageRequest.getiDisplayStart());
			map.put(Constant.PAGESIZE, pageRequest.getiDisplayLength());
			int totalCount = cmsIconMapper.selectForPaginTotalCount(map);
			pageRequest.setiTotalDisplayRecords(totalCount);
			pageRequest.setiTotalRecords(totalCount);
			List<CmsIcon> selectForPagin = cmsIconMapper.selectForPagin(map);
			pageRequest.setData(selectForPagin);
			return pageRequest;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			throw new ManagerException(e);
			
		}
	}


	@Override
	public Integer findMaxWeight() throws ManagerException {
		try {
			return cmsIconMapper.findMaxWeight();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}


	@Override
	public int updateByPrimaryKey(CmsIcon icon)throws ManagerException {
		try {
			return cmsIconMapper.updateByPrimaryKeySelective(icon);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}


	@Override
	public Integer findPositionIconWeight(Integer position)throws ManagerException {
		try {
			return cmsIconMapper.findPositionIconWeight(position);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}


	@Override
	public Integer resetIconWeightWhenUp(Integer positionWeight, Integer iconWeight)throws ManagerException  {
		try {
			return cmsIconMapper.resetIconWeightWhenUp(positionWeight,iconWeight);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		
	}


	@Override
	public Integer resetIconWeightWhenDown(Integer positionWeight,
			Integer iconWeight) throws ManagerException {
		try {
			return cmsIconMapper.resetIconWeightWhenDown(positionWeight, iconWeight);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	
	
	

}
