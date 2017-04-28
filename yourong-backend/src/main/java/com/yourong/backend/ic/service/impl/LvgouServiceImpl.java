package com.yourong.backend.ic.service.impl;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.backend.ic.service.LvgouService;
import com.yourong.backend.utils.SysServiceUtils;
import com.yourong.common.web.BaseService;
import com.yourong.core.ic.manager.LvGouManager;
import com.yourong.core.ic.manager.ProjectManager;
import com.yourong.core.ic.model.Project;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;

@Service
public class LvgouServiceImpl extends BaseService implements LvgouService {
	
	@Autowired
	private SysDictManager sysDictManager;
	
	@Autowired
	private LvGouManager lvGouManager;
	
	@Autowired
	private ProjectManager projectManager;

	@Override
	public int createLvgouContractTask() throws Exception {
		int lvgouCount = 0;
		String excuteFlag = SysServiceUtils.getDictValue("lvgou_notice", "regular_preservation_notice", "0");
		//把标识设为N，避免定时任务的唯一性
		if("Y".equals(excuteFlag)) {
			SysDict dict = sysDictManager.findByGroupNameAndKey("regular_preservation_notice", "lvgou_notice");
			
			SysDict updateDict = new SysDict();
			updateDict.setId(dict.getId());
			updateDict.setValue("N");
			sysDictManager.updateByPrimaryKeySelective(updateDict);
			//批量推送绿狗合同
			List<Project> proList = projectManager.queryProjectFromLvGou(new HashMap<String, Object>());
			
			if(CollectionUtils.isNotEmpty(proList)) {
				for(Project p : proList) {
					lvgouCount += lvGouManager.createLvGouContract(p.getId());
				}
			}
			logger.info("推送绿狗合同{}个", lvgouCount);
			updateDict.setValue("Y");
			sysDictManager.updateByPrimaryKeySelective(updateDict);
		}
		return lvgouCount;
	}
    
}