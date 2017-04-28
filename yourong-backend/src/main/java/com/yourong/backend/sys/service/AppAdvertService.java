/**
 * 
 */
package com.yourong.backend.sys.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysDict;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年1月5日下午2:27:47
 */
public interface AppAdvertService {

	public int insert(long id ,String appPath,MultipartFile file);
	
	public int clear(long id );
	
	public Page<SysDict> findByPage(Page<SysDict> pageRequest, Map<String, Object> map);

	public int insertTitle(long id ,String value);
	
}
