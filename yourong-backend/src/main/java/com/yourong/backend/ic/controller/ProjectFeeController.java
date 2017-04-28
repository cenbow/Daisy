package com.yourong.backend.ic.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.yourong.backend.BaseController;
import com.yourong.backend.ic.service.ManagerFeeService;
import com.yourong.backend.ic.service.ProjectService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.model.ProjectFee;
import com.yourong.core.fin.model.ManagementFeeAgreement;
import com.yourong.core.tc.model.biz.ContractBiz;
	
/**
 * 
 * @desc 直投项目各费用收取记录
 * @author chaisen
 * 2015年12月30日下午3:16:59
 */
@Controller
@RequestMapping("manageFeeManager")
public class ProjectFeeController extends BaseController{
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private ManagerFeeService managerFeeService;
	/**
	 * 
	 * @Description:跳转到直投项目服务费列表
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * @author: chaisen
	 * @time:2016年1月11日 下午5:03:59
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("manageFeeManager:index")
	public String manageFee(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		return "/p2p/projectFee/index";
	}
	
	/**
	 * 
	 * @Description:分页查询管理费记录
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: chaisen
	 * @time:2016年1月11日 下午5:26:26
	 */
	@RequestMapping(value = "ajaxManage")
	@RequiresPermissions("manageFeeManager:index")
	@ResponseBody
	public Object findManageFeeList(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		Page<ProjectFee> pageRequest = new Page<ProjectFee>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
 		Page<ProjectFee> pager = projectService.selectManageFeeForPagin(pageRequest, map);
		return pager;
	}
	
	/**
	 * 
	 * @Description:获取借款人信息
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: zhanghao
	 * @time:2016年4月29日 下午15:26:26
	 */
	@RequestMapping(value = "borrowerInformation")
	@RequiresPermissions("manageFeeManager:index")
	@ResponseBody
	public Object selectBorrowerInformation(@ModelAttribute("projectId") Long projectId, 
			HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		ProjectFee managementFee = managerFeeService.selectBorrowerInformation(projectId);
		return managementFee;
	}
	
	/**
	 * 
	 * @Description:获取项目所有借款协议
	 * @param req
	 * @param resp 
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: zhanghao
	 * @time:2016年4月29日 下午15:26:26
	 */
	@RequestMapping(value = "agreement")
	@RequiresPermissions("manageFeeManager:index")
	@ResponseBody
	public ModelAndView selectAgreementInformation(@ModelAttribute("projectId") Long projectId, 
			HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		 ModelAndView agreement = new ModelAndView();
		List<ManagementFeeAgreement> managementFeeAgreementList = managerFeeService.selectAgreementInformation(projectId);
		agreement.addObject("agreementList", managementFeeAgreementList);
		agreement.addObject("projectId", projectId);
		agreement.setViewName("/p2p/projectFee/agreement");
		return agreement;
	}
	
	/**
     * 直投项目合同查看
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value ="/p2pContract/view" )
    @RequiresPermissions("manageFeeManager:index")
    @ResponseBody
    public ModelAndView p2pViewContract(@ModelAttribute("transactionId") Long transactionId, 
    		HttpServletRequest req,HttpServletResponse resp  ) {
    	ModelAndView model = new ModelAndView();
    	Long orderId = ServletRequestUtils.getLongParameter(req,"orderId",0L);
    	
    	ResultDO<ContractBiz> result = managerFeeService.p2pViewContract(transactionId);
    	 if (!result.isSuccess()) {
             model.setViewName("redirect:/404");
             return model;
         }
    	model.addObject("preview", false);
        model.addObject("contract", result.getResult());
        model.setViewName("/contract/ztContract-preview");
        return model;
    }
    
    /**
	 * 
	 * @Description:下载项目所有借款协议
	 * @param req
	 * @param resp 
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: zhanghao
	 * @time:2016年4月29日 下午15:26:26
	 */
	@RequestMapping(value = "agreementDown")
	@RequiresPermissions("manageFeeManager:index")
	@ResponseBody
	public void agreementDown(@ModelAttribute("projectId") Long projectId, 
			HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException{
		String url = managerFeeService.agreementDown(projectId,resp);
		this.downFile(req,resp, url,projectId);
	}
	
	/**  
     * 文件下载  
     * @param response  
     * @param str  
     */  
    private void downFile(HttpServletRequest req,HttpServletResponse resp, String str,Long projectId) {   
    	 InputStream ins = null;
    	 BufferedInputStream bins = null;
    	 OutputStream outs = null;
    	 BufferedOutputStream bouts = null;
    	try {   
    		req.setCharacterEncoding("UTF-8");
            String path =  str;   
            File file = new File(path);   
            if (file.exists()) {   
                 ins = new FileInputStream(path);   
                 // 获取输入流
                 bins = new BufferedInputStream(ins);// 放到缓冲流里面   
                 // 获取文件输出IO流   
                 outs = resp.getOutputStream();
                 // 输出流
                 bouts = new BufferedOutputStream(outs);   
                resp.setContentType("application/octet-stream");// 设置response内容的类型   
                resp.setHeader(   
                        "Content-disposition",   
                        "attachment;filename="  
                                +  new String((projectId.toString()+".rar").getBytes("utf-8"), "ISO8859-1"));// 设置头部信息   
                byte[] buff = new byte[2048];
				int bytesRead;
				// 开始向网络传输文件流   
				while (-1 != (bytesRead = bins.read(buff, 0, buff.length))) {
					bouts.write(buff, 0, bytesRead);
				}
				
                bouts.flush();// 这里一定要调用flush()方法   
            }  
        } catch (IOException e) {   
        	logger.error("合同压缩文件下载出错", e);   
        }finally {
			// 关闭流
			try {
				 ins.close();   
	                bins.close();   
	                outs.close();   
	                bouts.close();   
			} catch (Exception e) {
				 logger.error("合同压缩文件下载,关闭流失败！", e);
			}
		}
    }   
	
}
