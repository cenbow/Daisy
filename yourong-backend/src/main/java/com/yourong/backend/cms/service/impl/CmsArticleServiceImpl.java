package com.yourong.backend.cms.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.handle.AttachmentInfo;
import com.yourong.core.handle.AttachmentThread;
import com.yourong.core.handle.CmsArticleAttachmentHandle;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import rop.thirdparty.com.google.common.collect.Maps;

import com.google.common.base.Optional;
import com.yourong.backend.cms.service.CmsArticleService;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.OSSUtil;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.core.cms.manager.CmsArticleManager;
import com.yourong.core.cms.model.CmsArticle;

import javax.annotation.Resource;

@Service
public class CmsArticleServiceImpl implements CmsArticleService {

	private static Logger logger = LoggerFactory
			.getLogger(CmsArticleServiceImpl.class);

	@Autowired
	private CmsArticleManager cmsArticleManager;

	@Autowired
	private CmsArticleAttachmentHandle cmsArticleAttachmentHandle;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	public Integer deleteByPrimaryKey(Long id) {
		try {
			return cmsArticleManager.deleteByPrimaryKey(id);
		} catch (ManagerException e) {
			logger.error("删除文章信息失败,id=" + id, e);
		}
		return null;
	}

	public Integer insert(CmsArticle cmsArticle,String appPath,List<BscAttachment> bscAttachments) {
		try {
//			String imgUrl = uploadFile(appPath,file,cmsArticle.getCategoryId());
//			cmsArticle.setImage(imgUrl);
			if(Optional.fromNullable(cmsArticle.getOnlineTime()).isPresent()){
				cmsArticle.setPublishState(0);
			}else{
				cmsArticle.setPublishState(1);//上线时间为空则立即上线
			}
			CmsArticle insertCmsArticle = cmsArticleManager.insertAndReturnId(cmsArticle);

			if (insertCmsArticle!=null){
				/* 添加附件 */
				AttachmentInfo info = new AttachmentInfo();
				info.setKeyId(insertCmsArticle.getId().toString());
				info.setBscAttachments(bscAttachments);
				info.setAppPath(appPath);
				info.setAttachMentType(AttachmentInfo.AttachMentType.ARTICLE);
				info.setOperation(AttachmentInfo.SAVE);
				taskExecutor.execute(new AttachmentThread(cmsArticleAttachmentHandle, info));
			}
		} catch (ManagerException e) {
			logger.error("插入文章信息失败,cmsArticle=" + cmsArticle, e);
		}
		return null;
	}

	public CmsArticle selectByPrimaryKey(Long id) {
		try {
			CmsArticle cmsArticle = cmsArticleManager.selectByPrimaryKey(id);
			List<BscAttachment> bsclist=null;
			if (cmsArticle!=null){
				if (!StringUtils.isEmpty(cmsArticle.getImage())){
					BscAttachment common=new BscAttachment();
					common.setId(1L);
					common.setFileUrl(cmsArticle.getImage());
					common.setModule("article_common");
					bsclist=new ArrayList<>();
					bsclist.add(common);
					cmsArticle.setCommonAttachments(bsclist);
				}
				if (!StringUtils.isEmpty(cmsArticle.getChosenImage())){
					BscAttachment chosen=new BscAttachment();
					chosen.setId(1L);
					chosen.setFileUrl(cmsArticle.getChosenImage());
					chosen.setModule("article_chosen");
					bsclist=new ArrayList<>();
					bsclist.add(chosen);
					cmsArticle.setChosenAttachments(bsclist);
				}
				if (cmsArticle.getContent()!=null && cmsArticle.getContent().length>0){
					cmsArticle.setContentHtml(new String(cmsArticle.getContent()));
				}
			}
			return cmsArticle;
		} catch (ManagerException e) {
			logger.error("查询文章信息失败,id=" + id, e);
		}
		return null;
	}

	public Integer updateByPrimaryKey(CmsArticle cmsArticle) {
		try {
			return cmsArticleManager.updateByPrimaryKey(cmsArticle);
		} catch (ManagerException e) {
			logger.error("更新文章信息失败,cmsArticle=" + cmsArticle, e);
		}
		return null;
	}

	public Integer batchDelete(long[] ids) {
		Integer delFlag = 0;
		try {
			delFlag = cmsArticleManager.batchDelete(ids);
		} catch (ManagerException e) {
			logger.error("批量删除文章失败,ids=" + ids, e);
		}
		return delFlag;
	}

	public Page<CmsArticle> findByPage(Page<CmsArticle> pageRequest,
			Map<String, Object> map) {
		try {
			return cmsArticleManager.findByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("分页查询文章信息失败", e);
		}
		return null;
	}

	@Override
	public Integer updateByPrimaryKeySelective(CmsArticle record,String appPath,List<BscAttachment> bscAttachments) {
		try {
			// 文件上传
//			String imgUrl = uploadFile(appPath,file,record.getCategoryId());
//			record.setImage(imgUrl);
			cmsArticleManager.updateByPrimaryKeySelective(record);
			AttachmentInfo info = new AttachmentInfo();
			info.setKeyId(record.getId().toString());
			info.setBscAttachments(bscAttachments);
			info.setAttachMentType(AttachmentInfo.AttachMentType.ARTICLE);
			info.setAppPath(appPath);
			taskExecutor.execute(new AttachmentThread(cmsArticleAttachmentHandle, info));
			return 1;
		} catch (Exception e) {
			logger.error("更新文章信息失败,cmsArticle=" + record, e);
		}
		return null;
	}
	
	//文件上传
	public String uploadFile(String appPath,MultipartFile file,Long categoryId){
		// 文件上传
		if (!file.isEmpty()) { // 获取文件名称
			String fileName = file.getOriginalFilename(); // 获取文件需要保存的路径
			//判断目录是否存在
			File fileDir =new File( appPath + PropertiesUtil.getUploadDirectory()); 
			if(!fileDir.exists() && !fileDir.isDirectory()) {     
				fileDir.mkdir();
			}
			String path = appPath + PropertiesUtil.getUploadDirectory() + "/" + fileName;
			logger.debug("文件路径：" + path);
			File localFile = new File(path);
			try { // 保存文件
				file.transferTo(localFile);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String key = OSSUtil.getArticleKey(categoryId.toString(), fileName,
					DateUtils.getCurrentDateTime());
			String url = OSSUtil.uploadImageToOSS(key, path);
			String simpleUrl = OSSUtil.getSimpleImageUrl(url);
			return simpleUrl;
		}
		return null;
	}

	@Override
	public Integer updateByPrimaryKeyWithBLOBs(CmsArticle record) {
		try {
			return cmsArticleManager.updateByPrimaryKeyWithBLOBs(record);
		} catch (Exception e) {
			logger.error("更新文章包括文章内容失败,cmsArticle=" + record, e);
		}
		return null;
	}

	// 根据栏目Id获取文章
	@Override
	public List<CmsArticle> selectByCategoryId(Long categoryId) {
		try {
			return cmsArticleManager.selectByCategoryId(categoryId);
		} catch (Exception e) {
			logger.error("根据栏目获取文章列表失败,categoryId=" + categoryId, e);
		}
		return null;
	}
	
	// 获取app推送文章
	@Override
	public List<CmsArticle> selectAppPushArticle() {
		try {
			Map<String, Object> map = Maps.newHashMap();
			map.put("posid", 1);
			return cmsArticleManager.selectListByMap(map);
		} catch (Exception e) {
			logger.error("获取app推送文章失败" , e);
		}
		return null;
	}

}