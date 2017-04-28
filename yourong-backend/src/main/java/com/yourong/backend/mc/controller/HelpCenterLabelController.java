package com.yourong.backend.mc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yourong.backend.BaseController;
import com.yourong.backend.mc.service.HelpCenterLabelService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.HelpCenterLabel;

@Controller
@RequestMapping("helpCenterLabel")
public class HelpCenterLabelController extends BaseController {
	@Autowired
	private HelpCenterLabelService helpCenterLabelService;


	/**
	 * 标签列表页面
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "labelIndex")
	@RequiresPermissions("helpCenterLabel:labelIndex")
	public String showLabelIndex(HttpServletRequest req,
			HttpServletResponse resp) {
		return "/mc/helpCenterLabel/labelIndex";
	}
	
	
	
	/**
	 * 列表数据
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
	@RequiresPermissions("helpCenterLabel:ajax")
	@ResponseBody
	public Object showLabelPages(HttpServletRequest req,HttpServletResponse resp) throws ServletRequestBindingException {
		Page<HelpCenterLabel> pageRequest = new Page<HelpCenterLabel>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<HelpCenterLabel> pager = helpCenterLabelService.findByPage(pageRequest, map);
		return pager;
		
	}
	
	/**
	 * 保存标签
	 * @param helpCenterLabel
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "save")
	//@RequiresPermissions("helpCenterLabel:save")
	@ResponseBody
	public ResultDO<HelpCenterLabel> saveHelpCenterLabel(
		@ModelAttribute("HelpCenterLabel") HelpCenterLabel helpCenterLabel,HttpServletRequest req, HttpServletResponse resp) {
			ResultDO<HelpCenterLabel> result = new ResultDO<HelpCenterLabel>();
			if(helpCenterLabel.getSort()==null){
				helpCenterLabel.setSort(0);
			}
			if(helpCenterLabel.getLabelName()==null){
				helpCenterLabel.setLabelName("");
			}
			if(helpCenterLabel.getCategory()==null){
				helpCenterLabel.setCategory("");
			}
			if(helpCenterLabel.getQuestionType()==null){
				helpCenterLabel.setQuestionType(0);
			}
			
			if (helpCenterLabel.getId() != null) {
				result = helpCenterLabelService.update(helpCenterLabel);
			} else {
				result = helpCenterLabelService.insertHelpCenterLabel(helpCenterLabel);
			}
			return result;
	}
	
	/**
	 * 编辑显示数据
	 * @param id
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "show")
	@RequiresPermissions("helpCenterLabel:show")
	@ResponseBody
	public Object showHelpCenterLabel(@RequestParam("id") Long id,HttpServletRequest req, HttpServletResponse resp) {
		HelpCenterLabel helpCenterLabel = helpCenterLabelService.selectByPrimaryKey(id);
		return helpCenterLabel;
	}
	
    @ResponseBody
    @RequestMapping(value = "savesort")
    //@RequiresPermissions("helpCenterLabel:saveSort")
    public Object saveSort(@RequestParam(value = "sortstr")String sortstr){
        ResultDO<Object> resultDO=new ResultDO<>();
        try {
            List<HelpCenterLabel> sorts= JSON.parseObject(sortstr,new TypeReference<ArrayList<HelpCenterLabel>>(){});
            helpCenterLabelService.updateSortById(sorts);
            resultDO.setSuccess(true);
            return resultDO;
        } catch (Exception e) {
            resultDO.setSuccess(false);
            return resultDO;
        }
    }
    
	/**
	 * 删除问题(逻辑)
	 * @param id
	 * @param req
	 * @param resp
	 * @return
	 */
	/*@RequestMapping(value = "deleteFlag")
	@RequiresPermissions("helpCenter:delete")
	@ResponseBody
	public ResultDO<HelpCenterLabel> deleteHelpCenterLabelFlag(@RequestParam("id") Long id, HttpServletRequest req,HttpServletResponse resp) {
		ResultDO<HelpCenterLabel> result = new ResultDO<HelpCenterLabel>();
		if (id != null) {
			result = helpCenterLabelService.deleteByHelpCenterLabelId(id);
		} else {
			result.setSuccess(false);
		}
		return result;
	}*/
	
	/**
	 * 删除问题
	 * @param id
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "delete")
	@RequiresPermissions("helpCenterLabel:delete")
	@ResponseBody
	public ResultDO<HelpCenterLabel> deleteHelpCenterLabel(@RequestParam("id") Long id, HttpServletRequest req,HttpServletResponse resp) {
		ResultDO<HelpCenterLabel> result = new ResultDO<HelpCenterLabel>();
		if (id != null) {
			result = helpCenterLabelService.deleteByPrimaryKey(id);
		} else {
			result.setSuccess(false);
		}
		return result;
	}

	/**
	 * 根据标签类别（category）显示数据
	 * @param id
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "showLabel")
	//@RequiresPermissions("helpCenterLabel:show")
	@ResponseBody
	public Object showHelpCenterLabel(@RequestParam("category") String category,HttpServletRequest req, HttpServletResponse resp) {
		List<HelpCenterLabel> list = helpCenterLabelService.selectByCategory(category);
		return list;
	}
}
