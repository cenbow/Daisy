package com.yourong.backend.cms.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.yourong.backend.cms.service.BannerService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.OSSUtil;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.core.cms.manager.BannerManager;
import com.yourong.core.cms.model.Banner;
import com.yourong.core.mc.manager.ActivityManager;
import com.yourong.core.mc.model.biz.ActivityBiz;

@Service
public class BannerServiceImpl implements BannerService {
	private static final Logger logger = LoggerFactory.getLogger(BannerServiceImpl.class);
	@Autowired
	private BannerManager bannerManager;
	@Autowired
	private ActivityManager activityManager;
	
	public int deleteByPrimaryKey(Long id) {
		int result = 0;
		try {
			result = bannerManager.deleteByPrimaryKey(id);
		} catch (ManagerException e) {
			logger.error("删除banner失败，id=" + id,e);
		}
		return result;
	}

	public int insert(Banner banner,String appPath,MultipartFile imageFile,MultipartFile imageBgFile) {
		int result = 0;
		try {
			String imgUrl = uploadFile(appPath,imageFile);
			banner.setImage(imgUrl);
			String imgBgUrl = uploadFile(appPath,imageBgFile);
			banner.setImageBg(imgBgUrl);
			//获取最大权重
			Integer maxWeight = bannerManager.findMaxWeight();
			if(maxWeight==null){
				maxWeight = 0;
			}
			banner.setWeight(maxWeight+1);
			if(banner.getStartTime().after(DateUtils.getCurrentDate())) {
				banner.setBannerStatus(0);
			} else {
				banner.setBannerStatus(1);
			}
			if(banner.getEndTime() != null && banner.getEndTime().before(DateUtils.getCurrentDate())) {
				banner.setBannerStatus(-1);
			}
			result = bannerManager.insert(banner);
		} catch (ManagerException e) {
			logger.error("插入banner失败，banner=" + banner,e);
		}
		return result;
	}

	public Banner selectByPrimaryKey(Long id) {
		try {
			return bannerManager.selectByPrimaryKey(id);
		} catch (ManagerException e) {
			logger.error("查询banner失败，id=" + id,e);
		}
		return null;
	}

	public int updateByPrimaryKey(Banner banner) {
		try {
			return bannerManager.updateByPrimaryKey(banner);
		} catch (ManagerException e) {
			logger.error("更新banner失败，banner=" + banner,e);
		}
		return 0;
	}

	public int updateByPrimaryKeySelective(Banner banner,String appPath,MultipartFile imageFile,MultipartFile imageBgFile) {
		try {
			String imgUrl = uploadFile(appPath,imageFile);
			banner.setImage(imgUrl);
			String imgBgUrl = uploadFile(appPath,imageBgFile);
			banner.setImageBg(imgBgUrl);
			if(banner.getStartTime().after(DateUtils.getCurrentDate())) {
				banner.setBannerStatus(0);
			} else {
				banner.setBannerStatus(1);
			}
			if(banner.getEndTime() != null && banner.getEndTime().before(DateUtils.getCurrentDate())) {
				banner.setBannerStatus(-1);
			}
			return bannerManager.updateByPrimaryKeySelective(banner);
		} catch (ManagerException e) {
			logger.error("更新banner失败，banner=" + banner,e);
		}
		return 0;
	}

	public int batchDelete(long[] ids) {
		try {
			return bannerManager.batchDelete(ids);
		} catch (ManagerException e) {
			logger.error("批量删除banner失败，ids=" + ids,e);
		}
		return 0;
	}

	public Page<Banner> findByPage(Page<Banner> pageRequest, Map<String, Object> map) {
		try {
			return bannerManager.findByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("获取banner列表失败，pageRequest=" + pageRequest + "map=" + map,e);
		}
		return null;
	}

	// 文件上传
	public String uploadFile(String appPath, MultipartFile file) {
		// 文件上传
		if (!file.isEmpty()) { // 获取文件名称
			
			
			String date = DateUtils.getStrFromDate(DateUtils.getCurrentDate(),DateUtils.TIME_PATTERN_MILLISECOND);
			String fileName = file.getOriginalFilename(); // 获取文件需要保存的路径
			fileName = date+fileName;//文件名加时间前缀，以防同名文件，阿里云服务器上被替换
			// 判断目录是否存在
			File fileDir = new File(appPath + PropertiesUtil.getUploadDirectory());
			if (!fileDir.exists() && !fileDir.isDirectory()) {
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
			String key = OSSUtil.getBannerKey(fileName, DateUtils.getCurrentDateTime());
			String url = OSSUtil.uploadImageToOSS(key, path);
			String simpleUrl = OSSUtil.getSimpleImageUrl(url);
			return simpleUrl;
		}
		return null;
	}

	//更新权重
	@Override
	public ResultDO<Banner> updateWeight(Long bannerId,Integer position,String userName) {
		ResultDO<Banner> result = new ResultDO<Banner>();
		try {
			//获取需要调整位置的banner
			Banner banner = bannerManager.selectByPrimaryKey(bannerId);
			//获取position位置的banner
			Integer positionWeight = bannerManager.findPositionBannerWeight(position, banner.getAreaSign(), banner.getType());
			//比较position位置banner的权重和需要调整位置的banner的权重
			//向上移动
			if(positionWeight > banner.getWeight()){
				//重置大于需要调整banner权重并且小于等于position位置权重和的banner的权重
				bannerManager.resetBannerWeightWhenUp(positionWeight,banner.getWeight());
			}
			if(positionWeight < banner.getWeight()){
				//重置小于需要调整banner权重并且大于等于position位置权重的banner的权重
				bannerManager.resetBannerWeightWhenDown(positionWeight,banner.getWeight());
			}
			//设置调整位置的banner的权重为positionWeight
			banner.setWeight(positionWeight);
			banner.setUpdateBy(userName);
			int row = bannerManager.updateByPrimaryKey(banner);
			if(row<0){
				result.setSuccess(false);
			}
		} catch (Exception e) {
			logger.error("更新banner权重失败：",e);
		}
		return result;
	}

	@Override
	public Integer expireBannerTask() {
		try {
			return bannerManager.expireBannerTask();
		} catch (Exception e) {
			logger.error("定时过期banner失败",e);
		}
		return null;
	}

	@Override
	public List<ActivityBiz> showNotFinishActivityList() {
		try {
			return activityManager.showNotFinishActivityList();
		} catch (Exception e) {
			logger.error("定时过期banner失败",e);
		}
		return null;
	}


}