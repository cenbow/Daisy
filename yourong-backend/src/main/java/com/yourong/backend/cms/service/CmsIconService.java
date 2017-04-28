/**
 * 
 */
package com.yourong.backend.cms.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.CmsIcon;

/**
 * @author wanglei
 *
 */
public interface CmsIconService {

	/**
	 * 分页查询icon
	 * @param pageRequest
	 * @param map
	 * @return
	 */
	Page<CmsIcon> findByPage(Page<CmsIcon> pageRequest, Map<String, Object> map);

	/**
	 * 修改Icon
	 * @param cmsIcon
	 * @param appPath
	 * @param imageFile
	 * @return
	 */
	int updateByPrimaryKeySelective(CmsIcon cmsIcon, String appPath,
			MultipartFile imageFile);

	/**
	 * 新增icon
	 * @param cmsIcon
	 * @param appPath
	 * @param imageFile
	 * @return
	 */
	int insert(CmsIcon cmsIcon, String appPath, MultipartFile imageFile);

	
	/**
	 * 查取指定id的Icon
	 * @param id
	 * @return
	 */
	CmsIcon selectByPrimaryKey(Long id);

	/**
	 * 批量删除指定id的icon
	 * @param ids 以“,”间隔的id字符串
	 * @return
	 * @throws ManagerException
	 */
	int batchDelete(long[] ids);

	Object updateWeight(Long iconId, Integer position, String userName);
	
	
	
	

}
