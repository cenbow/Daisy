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
import com.yourong.backend.mc.service.HelpCenterService;
import com.yourong.backend.sh.dto.GoodsDto;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.mc.model.HelpCenterQuestion;

@Controller
@RequestMapping("helpCenter")
public class HelpCenterController extends BaseController {
	@Autowired
	private HelpCenterService helpCenterService;


	/**
	 * 列表页面
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "questionIndex")
	@RequiresPermissions("helpCenter:questionIndex")
	public String showQuestionCenterIndex(HttpServletRequest req,
			HttpServletResponse resp) {
		return "/mc/helpCenter/questionIndex";
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
	@ResponseBody
	public Object showQuestionPages(HttpServletRequest req,
			HttpServletResponse resp) throws ServletRequestBindingException {
		Page<HelpCenterQuestion> pageRequest = new Page<HelpCenterQuestion>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<HelpCenterQuestion> pager = helpCenterService.findByPage(pageRequest, map);
		return pager;
		
	}
	
	/**
	 * 保存
	 * @param couponTemplate
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "save")
	//@RequiresPermissions("helpCenter:save")
	@ResponseBody
	public ResultDO<HelpCenterQuestion> saveHelpCenterQuestion(
		@ModelAttribute("helpCenterQuestion") HelpCenterQuestion helpCenterQuestion,HttpServletRequest req, HttpServletResponse resp) {
			ResultDO<HelpCenterQuestion> result = new ResultDO<HelpCenterQuestion>();
			if(helpCenterQuestion.getSort()==null){
				helpCenterQuestion.setSort(0);
			}
			if(helpCenterQuestion.getTerminal()==null){
				helpCenterQuestion.setTerminal(0);
			}
			if(helpCenterQuestion.getLabelId()==null){
				helpCenterQuestion.setLabelId(0L);
			}
			if(helpCenterQuestion.getContent()==null){
				helpCenterQuestion.setContent("");
			}
			if(helpCenterQuestion.getAnswer()==null){
				helpCenterQuestion.setAnswer("");
			}
			
			if (helpCenterQuestion.getId() != null) {
				result = helpCenterService.update(helpCenterQuestion);
			} else {
				result = helpCenterService.insertHelpCenterQuestion(helpCenterQuestion);
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
	@RequiresPermissions("helpCenter:show")
	@ResponseBody
	public Object showHelpCenterQuestion(@RequestParam("id") Long id,HttpServletRequest req, HttpServletResponse resp) {
		HelpCenterQuestion helpCenterQuestion = helpCenterService.selectByPrimaryKey(id);
		return helpCenterQuestion;
	}
	
    @ResponseBody
    @RequestMapping(value = "savesort")
    //@RequiresPermissions("helpCenter:saveSort")
    public Object saveSort(@RequestParam(value = "sortstr")String sortstr){
        ResultDO<Object> resultDO=new ResultDO<>();
        try {
            List<GoodsDto> sorts= JSON.parseObject(sortstr,new TypeReference<ArrayList<GoodsDto>>(){});
            helpCenterService.updateSortById(sorts);
            resultDO.setSuccess(true);
            return resultDO;
        } catch (Exception e) {
            resultDO.setSuccess(false);
            return resultDO;
        }
    }
    
	/**
	 * 删除问题
	 * @param id
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "delete")
	@RequiresPermissions("helpCenter:delete")
	@ResponseBody
	public ResultDO<HelpCenterQuestion> deleteHelpCenterQuestion(
			@RequestParam("id") Long id, HttpServletRequest req,
			HttpServletResponse resp) {
		ResultDO<HelpCenterQuestion> result = new ResultDO<HelpCenterQuestion>();
		if (id != null) {
			result = helpCenterService.deleteByHelpCenterQuestionId(id);
		} else {
			result.setSuccess(false);
		}
		return result;
	}
	
	/**
	 * 刷新到缓存
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "flush")
	//@RequiresPermissions("helpCenter:flush")
	@ResponseBody
	public void flushQuestion(HttpServletRequest req, HttpServletResponse resp) {
		helpCenterService.flushQuestion(new Integer(1));
		helpCenterService.flushQuestion(new Integer(0));
	}

}
