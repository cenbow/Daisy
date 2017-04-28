package com.yourong.core.cms.manager;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.CmsArticle;
import com.yourong.core.cms.model.query.CmsArticleQuery;

public interface CmsArticleManager {
    int deleteByPrimaryKey(Long id) throws ManagerException;

    int insert(CmsArticle record) throws ManagerException;

    CmsArticle insertAndReturnId(CmsArticle record) throws ManagerException;

    CmsArticle selectByPrimaryKey(Long id) throws ManagerException;
    
    int updateByPrimaryKeySelective(CmsArticle record) throws ManagerException;

    int updateByPrimaryKeyWithBLOBs(CmsArticle record) throws ManagerException;

    int updateByPrimaryKey(CmsArticle record) throws ManagerException;

    int batchDelete(long[] ids) throws ManagerException;

    Page<CmsArticle> findByPage(Page<CmsArticle> pageRequest, Map<String, Object> map) throws ManagerException;
    
    List<CmsArticle> selectByCategoryId(Long categoryId) throws ManagerException;
    
    /*前台文章分页*/
    Page<CmsArticle> findByFrontPage(CmsArticleQuery cmsArticleQuery) throws ManagerException;
    
    List<CmsArticle> selectHomeNotice(Map<String,Object> map) throws ManagerException;
    
    CmsArticle selectArticleByIdAndCategoryId(Map<String,Object> map) throws ManagerException;
    
    /*文章 后一条*/
    Long selectNextArticle(Map<String,Object> map) throws ManagerException;
    
    /*文章 前一条*/
    Long selectPreArticle(Map<String,Object> map) throws ManagerException;
    /*根据上线时间，更改文章发布状态*/
    int updateArticlePubState() throws ManagerException;
    
    public CmsArticle selectNoticeArticle() throws ManagerException;
    
    public List<CmsArticle> selectListByMap(Map<String, Object> map) throws ManagerException;

	List<CmsArticle> selectHomeArticle(String name) throws ManagerException;

	List<CmsArticle> selectHomeArticleByCategoryId(String name) throws ManagerException;

	int selectArticles(Integer newsId) throws ManagerException;

    boolean updateCmsArticleAttachments(Long id,String image,String chosenImage) throws ManagerException;
}