/**
 * 
 */
package com.yourong.backend.cms.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.yourong.backend.cms.service.CmsIconService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.OSSUtil;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.core.cms.manager.CmsIconManager;
import com.yourong.core.cms.model.Banner;
import com.yourong.core.cms.model.CmsIcon;

/**
 * @author wanglei
 *
 */
@Service
public class CmsIconServiceImpl implements CmsIconService {

	private static final Logger logger = LoggerFactory.getLogger(CmsIconServiceImpl.class);
	
	
	@Autowired
	private CmsIconManager cmsIconManager;
	
	@Override
	public Page<CmsIcon> findByPage(Page<CmsIcon> pageRequest,
			Map<String, Object> map) {
		try {
			return cmsIconManager.findByPage(pageRequest, map);
		} catch (ManagerException e) {
			logger.error("获取icon列表失败，pageRequest=" + pageRequest + "map=" + map,e);
		}
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(CmsIcon icon, String appPath,
			MultipartFile imageFile) {
		int result = 0;
		try {
			String imgUrl = uploadFile(appPath,imageFile);
			icon.setImage(imgUrl);
			result = cmsIconManager.updateByPrimaryKeySelective(icon);
		} catch (ManagerException e) {
			logger.error("更新icon失败，icon=" + icon,e);
		}
		return result;
		
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

	@Override
	public int insert(CmsIcon banner, String appPath, MultipartFile imageFile) {
		int result = 0;
		try {
			String imgUrl = uploadFile(appPath,imageFile);
			banner.setImage(imgUrl);
			
			//获取最大权重
			Integer maxWeight = cmsIconManager.findMaxWeight();
			if(maxWeight==null){
				maxWeight = 0;
			}
			banner.setWeight(maxWeight+1);
			result = cmsIconManager.insert(banner);
			
		} catch (ManagerException e) {
			logger.error("插入icon失败，icon=" + banner,e);
		}
		return result;
		
	}

	@Override
	public CmsIcon selectByPrimaryKey(Long id) {
		try {
			return cmsIconManager.selectByPrimaryKey(id);
		} catch (ManagerException e) {
			logger.error("查询icon失败，id=" + id,e);
		}
		return null;
	}

	@Override
	public int batchDelete(long[] ids) {
		try {
			return cmsIconManager.batchDelete(ids);
		} catch (ManagerException e) {
			logger.error("批量删除banner失败，ids=" + ids,e);
		}
		return 0;
	}

	
	@Override
	public ResultDO<Banner> updateWeight(Long iconId,Integer position,String userName) {
		ResultDO<Banner> result = new ResultDO<Banner>();
		try {
			//获取需要调整位置的Icon
			CmsIcon icon = cmsIconManager.selectByPrimaryKey(iconId);
			//获取position位置的Icon权重
			Integer positionWeight = cmsIconManager.findPositionIconWeight(position);
System.out.println(positionWeight);
			//比较position位置banner的权重和需要调整位置的banner的权重
			//向上移动
			if(positionWeight > icon.getWeight()){
				//重置大于需要调整banner权重并且小于等于position位置权重和的Icon的权重
				int i = cmsIconManager.resetIconWeightWhenUp(positionWeight,icon.getWeight());
System.out.println(i);
			}
			if(positionWeight < icon.getWeight()){
				//重置小于需要调整banner权重并且大于等于position位置权重的Icon的权重
				int j = cmsIconManager.resetIconWeightWhenDown(positionWeight,icon.getWeight());
System.out.println(j);
			}
			//设置调整位置的banner的权重为positionWeight
			icon.setWeight(positionWeight);
			icon.setUpdateBy(userName);
			int row = cmsIconManager.updateByPrimaryKey(icon);
			if(row<0){
				result.setSuccess(false);
			}
		} catch (Exception e) {
			logger.error("更新icon权重失败：",e);
		}
		return result;
	}

}
