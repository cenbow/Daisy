package com.yourong.backend.cms.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.cms.model.Banner;
import com.yourong.core.mc.model.biz.ActivityBiz;

public interface BannerService {
	int deleteByPrimaryKey(Long id);

	int insert(Banner banner,String appPath,MultipartFile imageFile,MultipartFile imageBgFile);

	int updateByPrimaryKey(Banner record);

	int updateByPrimaryKeySelective(Banner banner,String appPath,MultipartFile imageFile,MultipartFile imageBgFile);

	Page<Banner> findByPage(Page<Banner> pageRequest, Map<String, Object> map);

	Banner selectByPrimaryKey(Long id);

	int batchDelete(long[] id);
	
	ResultDO<Banner> updateWeight(Long bannerId,Integer position,String userName);
	
	Integer expireBannerTask();
	
	List<ActivityBiz> showNotFinishActivityList();
}