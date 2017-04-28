package com.yourong.backend.cms.service;

import java.util.List;
import java.util.Map;

import com.yourong.core.bsc.model.BscAttachment;
import org.springframework.web.multipart.MultipartFile;

import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.CmsArticle;

public interface CmsArticleService {
    Integer deleteByPrimaryKey(Long id);

    public Integer insert(CmsArticle record,String appPath,List<BscAttachment> bscAttachments);

    CmsArticle selectByPrimaryKey(Long id);

    Integer updateByPrimaryKey(CmsArticle record);

    public Integer updateByPrimaryKeySelective(CmsArticle record,String appPath,List<BscAttachment> bscAttachments);
    
    Integer updateByPrimaryKeyWithBLOBs(CmsArticle record);

    Integer batchDelete(long[] ids);

    Page<CmsArticle> findByPage(Page<CmsArticle> pageRequest, Map<String, Object> map);
    
    //根据栏目ID查找
    List<CmsArticle> selectByCategoryId(Long categoryId);
    
    // 获取app推送文章
 	public List<CmsArticle> selectAppPushArticle();

}