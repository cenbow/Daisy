package com.yourong.core.mc.manager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.HelpCenterLabel;

public interface HelpCenterLabelManager {
	
	/**根据主键删除暂存**/
	Integer deleteByPrimaryKey(Long id) throws ManagerException;

	/**插入暂存**/
	Integer insert(HelpCenterLabel helpCenterLabel) throws ManagerException;
	
	/**根据主键选择暂存数据**/
	HelpCenterLabel selectByPrimaryKey(Long id) throws ManagerException;

	/**根据主键更新暂存数据**/
    Integer updateByPrimaryKey(HelpCenterLabel helpCenterLabel) throws ManagerException;
    
    Integer updateByPrimaryKeySelective(HelpCenterLabel helpCenterLabel) throws ManagerException;

    /**分页**/
    Page<HelpCenterLabel> findByPage(Page<HelpCenterLabel> pageRequest, Map<String, Object> map) throws ManagerException;
    
    /**根据主键删除（逻辑）**/
   Integer deleteByHelpCenterLabelId(Long id) throws ManagerException;
   
   boolean updateSortById(Long id, Integer sort, Date date);

   List<HelpCenterLabel> selectByCategory(String category);
    
}