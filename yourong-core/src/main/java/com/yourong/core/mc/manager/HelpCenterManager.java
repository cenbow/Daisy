package com.yourong.core.mc.manager;

import java.util.Date;
import java.util.Map;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.HelpCenterQuestion;

public interface HelpCenterManager {
	
	/**根据主键删除暂存**/
	Integer deleteByPrimaryKey(Long id) throws ManagerException;

	/**插入暂存**/
	Integer insert(HelpCenterQuestion helpCenterQuestion) throws ManagerException;
	
	/**根据主键选择暂存数据**/
	HelpCenterQuestion selectByPrimaryKey(Long id) throws ManagerException;

	/**根据主键更新暂存数据**/
    Integer updateByPrimaryKey(HelpCenterQuestion helpCenterQuestion) throws ManagerException;
    
    Integer updateByPrimaryKeySelective(HelpCenterQuestion helpCenterQuestion) throws ManagerException;

    /**分页**/
    Page<HelpCenterQuestion> findByPage(Page<HelpCenterQuestion> pageRequest, Map<String, Object> map) throws ManagerException;
    
    /**根据主键删除（逻辑）**/
   Integer deleteByHelpCenterQuestionId(Long id) throws ManagerException;
   
   boolean updateSortById(Long id, Integer sort, Date date);
   
   /**刷新问题到缓存
 * @throws ManagerException **/
   void flushQuestion(Integer terminal) throws ManagerException;
    
}