package com.yourong.core.cms.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.dao.CmsArticleMapper;
import com.yourong.core.cms.dao.CmsCategoryMapper;
import com.yourong.core.cms.manager.CmsArticleManager;
import com.yourong.core.cms.model.CmsArticle;
import com.yourong.core.cms.model.query.CmsArticleQuery;

@Service
public class CmsArticleManagerImpl implements CmsArticleManager {
	@Autowired
	private CmsArticleMapper cmsArticleMapper;
	
	@Autowired
	private CmsCategoryMapper cmsCategoryMapper;
	
	@Override
	public int deleteByPrimaryKey(Long id) throws ManagerException {
		try {
			int result = cmsArticleMapper.deleteByPrimaryKey(id);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int insert(CmsArticle record) throws ManagerException {
		try {
			int result = cmsArticleMapper.insert(record);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public CmsArticle insertAndReturnId(CmsArticle record) throws ManagerException {
		try {
			cmsArticleMapper.insertCmsArticle(record);
			return record;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public CmsArticle selectByPrimaryKey(Long id) throws ManagerException {
		try {
			return cmsArticleMapper.selectByPrimaryKey(id);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKeyWithBLOBs(CmsArticle record) throws ManagerException {
		try {
			int result = cmsArticleMapper.updateByPrimaryKeyWithBLOBs(record);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKey(CmsArticle record) throws ManagerException {
		try {
			int result = cmsArticleMapper.updateByPrimaryKey(record);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int batchDelete(long[] ids) throws ManagerException {
		try {
			int result = cmsArticleMapper.batchDelete(ids);
			return result;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<CmsArticle> findByPage(Page<CmsArticle> pageRequest, Map<String, Object> map) throws ManagerException {
		try {
			// map.put("delFlag", 1);
			return cmsArticleMapper.findByPage(pageRequest, map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int updateByPrimaryKeySelective(CmsArticle record) throws ManagerException {
		try {
			return cmsArticleMapper.updateByPrimaryKeySelective(record);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public int updateArticlePubState() throws ManagerException {
		try {
			return cmsArticleMapper.updateArticlePubState();
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<CmsArticle> selectByCategoryId(Long categoryId) throws ManagerException {
		try {
			return cmsArticleMapper.selectByCategoryId(categoryId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public Page<CmsArticle> findByFrontPage(CmsArticleQuery cmsArticleQuery) throws ManagerException {
		try {
			List<CmsArticle> articles = Lists.newArrayList();
			int count = cmsArticleMapper.selectForPaginTotalCountFront(cmsArticleQuery);
			if (count>0){
				articles = cmsArticleMapper.selectForPaginFront(cmsArticleQuery);
			}
			Page<CmsArticle> articlePage = new Page<CmsArticle>();
			articlePage.setData(articles);
			articlePage.setiDisplayLength(cmsArticleQuery.getPageSize());
			articlePage.setPageNo(cmsArticleQuery.getCurrentPage());
			articlePage.setiTotalDisplayRecords(count);
			articlePage.setiTotalRecords(count);
			return articlePage;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public List<CmsArticle> selectHomeNotice(Map<String, Object> map) throws ManagerException {
		try {
			List<CmsArticle> articles = Lists.newArrayList();
			map.put("categoryId", 1);
			articles = cmsArticleMapper.selectHomeNotice(map);
			return articles;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public CmsArticle selectArticleByIdAndCategoryId(Map<String, Object> map) throws ManagerException {
		try {
			CmsArticle article = cmsArticleMapper.selectArticleByIdAndCategoryId(map);
			return article;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public CmsArticle selectNoticeArticle() throws ManagerException {
		try {
			CmsArticle article = cmsArticleMapper.selectNoticeArticle();
			return article;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	
	/* 文章 后一条 */
	@Override
	public Long selectNextArticle(Map<String, Object> map) throws ManagerException {
		try {
			Long article = cmsArticleMapper.selectNextArticle(map);
			return article;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	/* 文章 前一条 */
	@Override
	public Long selectPreArticle(Map<String, Object> map) throws ManagerException {
		try {
			Long article = cmsArticleMapper.selectPreArticle(map);
			return article;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public List<CmsArticle> selectListByMap(Map<String, Object> map) throws ManagerException {
		try {
			return cmsArticleMapper.selectListByMap(map);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	

	@Override
	public List<CmsArticle> selectHomeArticle(String name) throws ManagerException {
		try {
			Map<String, Object> map = Maps.newHashMap();
			List<CmsArticle> articles = Lists.newArrayList();
			map.put("categoryId", cmsCategoryMapper.selectIdByName(name));
			articles = cmsArticleMapper.selectHomeArticle(map);
			return articles;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}
	
	@Override
	public List<CmsArticle> selectHomeArticleByCategoryId(String name) throws ManagerException {
		try {
			Map<String, Object> map = Maps.newHashMap();
			List<CmsArticle> articles = Lists.newArrayList();
			map.put("categoryId", cmsCategoryMapper.selectIdByName(name));
			articles = cmsArticleMapper.selectHomeArticleByCategoryId(map);
			return articles;
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public int selectArticles(Integer newsId) throws ManagerException{
		try {
			return cmsArticleMapper.selectArticles(newsId);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	@Override
	public boolean updateCmsArticleAttachments(Long id, String image, String chosenImage) throws ManagerException {
		try {
			if (cmsArticleMapper.updateCmsArticleAttachments(id,image,chosenImage)>0){
                return true;
            }
		} catch (Exception e) {
			throw new ManagerException(e);
		}
		return false;
	}
}