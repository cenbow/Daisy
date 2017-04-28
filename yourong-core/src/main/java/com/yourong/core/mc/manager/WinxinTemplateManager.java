package com.yourong.core.mc.manager;

import java.util.List;
import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.WinxinTemplate;

public interface WinxinTemplateManager {
	int deleteByPrimaryKey(Long id) throws ManagerException;

    int insert(WinxinTemplate winxinMenu) throws ManagerException ;

    WinxinTemplate selectByPrimaryKey(Long id) throws ManagerException;

    int updateByPrimaryKey(WinxinTemplate winxinMenu) throws ManagerException ;

    int updateByPrimaryKeySelective(WinxinTemplate winxinMenu) throws ManagerException ;
	
    int deleteBymenuId(Long id) throws ManagerException;

    Page<WinxinTemplate> findByPage(Page<WinxinTemplate> pageRequest, Map<String, Object> map) throws ManagerException;

	/**
	 * @return
	 */
	List<WinxinTemplate> getAllWeixinMenu()throws ManagerException;
	
	String getParentMenu()throws ManagerException;

	/**
	 * @param ids
	 * @return
	 */
	Integer batchDelete(long[] ids)throws ManagerException;
	
	List<WinxinTemplate> getWeixininfo()throws ManagerException;

	/**
	 * @param winxin
	 * @return
	 */
	int insertSelective(WinxinTemplate winxin)throws ManagerException;

	/**
	 * @param id
	 * @return
	 */
	WinxinTemplate queryInfobyId(long id)throws ManagerException;

	/**
	 * @return
	 * @throws ManagerException
	 */
	List<WinxinTemplate> queryWeixinAtten()throws ManagerException;

	/**
	 * @return
	 * @throws ManagerException
	 */
	int updateWeixin() throws ManagerException;
}
