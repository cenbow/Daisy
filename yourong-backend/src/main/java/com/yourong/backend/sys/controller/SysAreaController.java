package com.yourong.backend.sys.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.cache.MyCacheManager;
import com.yourong.backend.sys.service.SysAreaService;
import com.yourong.backend.utils.SysServiceUtils;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.sys.model.SysArea;

@Controller
@RequestMapping("sysArea")
public class SysAreaController extends BaseController {
	
    @Autowired
    private SysAreaService sysAreaService;
    @Autowired
    private MyCacheManager myCacheManager;

    @RequestMapping(value = "index")
    @RequiresPermissions("sysArea:index")
    public String showSysAreaIndex(HttpServletRequest req, HttpServletResponse resp) {
         return "/sys/sysArea/index";
    }

    @RequestMapping(value = "ajax")
    @RequiresPermissions("sysArea:ajax")
    @ResponseBody
    public Object showSysAreaPages(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
    	Page<SysArea> pageRequest = new Page<SysArea>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
        	Page<SysArea> pager = sysAreaService.findByPage(pageRequest,map);		 
         return pager;
    }

    @RequestMapping(value = "save")
    @RequiresPermissions("sysArea:save")
    @ResponseBody
    @LogInfoAnnotation(moduleName = "区域模块",desc = "保存区域")
    public Object saveSysArea(@ModelAttribute SysArea sysArea, HttpServletRequest req, HttpServletResponse resp) {
        if(sysArea.getId()!=null){ 
	        int insertSelective = sysAreaService.updateByPrimaryKeySelective(sysArea); 
         }else{	 
        	int insertSelective = sysAreaService.insert(sysArea);
         }	 
    	return "1";		 
    }

    @RequestMapping(value = "show")
    @RequiresPermissions("sysArea:show")
    @ResponseBody
    public Object showSysArea(HttpServletRequest req, HttpServletResponse resp) {
        long id =  ServletRequestUtils.getIntParameter(req, "id", 0); 
        SysArea sysArea = sysAreaService.selectByPrimaryKey(id);	 
        	return sysArea;		 
    }

    @RequestMapping(value = "delete")
    @RequiresPermissions("sysArea:delete")
    @ResponseBody
    @LogInfoAnnotation(moduleName = "区域模块",desc = "删除区域")
    public Object deleteSysArea(HttpServletRequest req, HttpServletResponse resp) {
        long[] id = ServletRequestUtils.getLongParameters(req, "id[]"); 
        sysAreaService.batchDelete(id); 
        	return "1";		 
    }
    
    /**
     * 获取指定父区域的所有子区域（公共方法，未做权限控制）
     * @param code
     * @return
     */
    @RequestMapping(value="areaSelect")
    @ResponseBody
    public Object selectSysArea(@RequestParam("code") Integer code){
    	//从缓存类当中获取区域数据
    	List<SysArea> areas = myCacheManager.getAllArea();
    	List<SysArea> childArea = new ArrayList<SysArea>();
    	for (SysArea sysArea:areas) {
			if(sysArea.getParentId().equals(code)){
				childArea.add(sysArea);
			}
		}
    	return childArea;
    }
    
    @RequestMapping(value = "getAreaList")
	@ResponseBody
	public ResultDO<SysArea> getAreaList(HttpServletRequest req, HttpServletResponse resp){
		ResultDO<SysArea> result = new ResultDO<SysArea>();
		Long code = ServletRequestUtils.getLongParameter(req, "code",1L);
		List<SysArea> areaList = sysAreaService.getSysAreasByParentId(code);
		result.setResultList(areaList);
		return result;
	}
    @RequestMapping(value = "getCityList")
   	@ResponseBody
   	public ResultDO<SysArea> getCityList(HttpServletRequest req, HttpServletResponse resp,@RequestParam("code") Long code) throws Exception{
   		ResultDO<SysArea> result = new ResultDO<SysArea>();
   		Long cod1e = ServletRequestUtils.getLongParameter(req, "code",0);
   		List<SysArea> areaList = sysAreaService.getSysAreasByParentId(code);
   		result.setResultList(areaList);
   		return result;
   	}
    /**
     * 
     * @Description:根据区code获取上级code
     * @param req
     * @param resp
     * @param code
     * @return
     * @throws Exception
     * @author: chaisen
     * @time:2016年4月6日 上午10:15:13
     */
    @RequestMapping(value = "getSysAreasByParentId")
   	@ResponseBody
   	public ResultDO<SysArea> getSysAreasByParentId(HttpServletRequest req, HttpServletResponse resp,@RequestParam("code") Long code) throws Exception{
   		ResultDO<SysArea> result = new ResultDO<SysArea>();
   		List list = sysAreaService.getParentIdsByCode(code);
   		result.setResultList(list);
   		return result;
   	}
}