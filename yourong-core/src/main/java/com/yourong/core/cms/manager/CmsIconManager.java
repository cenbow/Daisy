package com.yourong.core.cms.manager;

import java.util.List;
import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.CmsIcon;

public interface CmsIconManager {
	
	
	
	/**
	 * 当前生效的ICON
	 * @return
	 */
	List<CmsIcon> findOnlineIcon()throws ManagerException;
	

	/**
	 * 新增icon
	 * @param record
	 * @return
	 * @throws ManagerException
	 */
	int insert(CmsIcon record) throws ManagerException;
	
	
	/**
     * 更新Icon
     * @param record
     * @return
	 * @throws ManagerException 
     */
    int updateByPrimaryKeySelective(CmsIcon record) throws ManagerException;


    /**
     * 查取指定的icon
     * @param id
     * @return
     * @throws ManagerException
     */
	CmsIcon selectByPrimaryKey(Long id) throws ManagerException;


	/**
	 * 删除指定的icon
	 * @param id
	 * @return
	 * @throws ManagerException
	 */
	int deleteByPrimaryKey(Long id) throws ManagerException;


	/**
	 * 批量删除指定id的icon
	 * @param ids 以“,”间隔的id字符串
	 * @return
	 * @throws ManagerException
	 */
	int batchDelete(long[] ids) throws ManagerException;


	/**
	 * 分页查询icon
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	Page<CmsIcon> findByPage(Page<CmsIcon> pageRequest, Map<String, Object> map) throws ManagerException;


	/**
	 * 获取最大权重值
	 * @return
	 */
	Integer findMaxWeight()throws ManagerException;


	/**
	 * 更新Icon
	 * @param icon
	 * @return
	 * @throws ManagerException
	 */
	int updateByPrimaryKey(CmsIcon icon) throws ManagerException;


	/**
	 * 获取当前位置Icon的权重
	 * @param position
	 * @return
	 */
	Integer findPositionIconWeight(Integer position)throws ManagerException;


	/**
	 * 
	 * @param positionWeight
	 * @param iconWeight
	 * @return 
	 */
	Integer resetIconWeightWhenUp(Integer positionWeight, Integer iconWeight)throws ManagerException ;


	/**
	 * 
	 * @param positionWeight
	 * @param iconWeight
	 * @return
	 */
	Integer resetIconWeightWhenDown(Integer positionWeight, Integer iconWeight)throws ManagerException;
	

    
    
    
}
