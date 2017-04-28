package com.yourong.backend.fin.controller;

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
import com.yourong.backend.fin.service.ForzenBalanceService;
import com.yourong.backend.uc.service.MemberService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.fin.manager.BalanceUnForzenManager;
import com.yourong.core.fin.model.BalanceForzen;
import com.yourong.core.fin.model.BalanceUnforzen;
import com.yourong.core.uc.model.Member;
/**
 * 
 * @desc 冻结和解冻
 * @author chaisen
 * 2016年7月27日下午1:47:10
 */
@Controller
@RequestMapping("forzenBalance")
public class ForzenBalanceController extends BaseController {

    @Autowired
    private ForzenBalanceService forzenBalanceService;
    
    @Autowired
	private MemberService memberService;
    
    @Autowired
  	private BalanceUnForzenManager balanceUnForzenManager;
    
    

    /**
     * 
     * @Description:调转到页面
     * @param req
     * @param resp
     * @return
     * @author: chaisen
     * @time:2016年7月27日 下午1:50:22
     */
    @RequestMapping(value = "index")
	@RequiresPermissions("forzenBalance:index")
	public String showActivityDataIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "/fin/forzenBalance/index";
	}

	/**
	 * 
	 * @Description:获取冻结列表
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 * @author: chaisen
	 * @time:2016年7月27日 下午1:53:21
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
	@RequiresPermissions("forzenBalance:ajax")
	@ResponseBody
	public Object showForzenPage(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Page<BalanceForzen> pageRequest = new Page<BalanceForzen>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
		Page<BalanceForzen> pager = forzenBalanceService.showForzenPage(pageRequest, map);
		return pager;
	}
	
	/**
	 * 
	 * @Description:冻结
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年7月27日 下午1:55:25
	 */
	@RequestMapping(value = "forzen")
    @ResponseBody
    public Object forzenBalance(@ModelAttribute BalanceForzen balanceFrozen,HttpServletRequest req, HttpServletResponse resp) {
    	ResultDO<BalanceForzen> retDO = new ResultDO<BalanceForzen>();
    	try {
    		retDO=forzenBalanceService.frozenBalance(balanceFrozen);
    	}catch(Exception e) {
    		logger.error("冻结异常,冻结订单号：", balanceFrozen.getForzenNo());
    		retDO.setSuccess(false);
    	}
    	return retDO ;
    }
    
   /**
    * 
    * @Description:解冻
    * @param req
    * @param resp
    * @return
    * @author: chaisen
    * @time:2016年7月27日 下午1:56:37
    */
	@RequestMapping(value = "unforzen")
    @ResponseBody
    public Object synchronizedHostingPayTrade(@ModelAttribute BalanceUnforzen unforzen,HttpServletRequest req, HttpServletResponse resp) {
		ResultDO<BalanceUnforzen> retDO = new ResultDO<BalanceUnforzen>();
    	try {
    		retDO=forzenBalanceService.unforzenBalance(unforzen);
    	}catch(Exception e) {
    		logger.error("解冻异常,解冻订单号：", unforzen.getUnforzenNo());
    		retDO.setSuccess(false);
    	}
    	return retDO ;
    }
	/**
	 * 
	 * @Description:同步冻结、解冻状态
	 * @param req
	 * @param resp
	 * @return
	 * @author: chaisen
	 * @time:2016年7月27日 下午1:58:21
	 */
	@RequestMapping(value = "syncFrozen")
    @ResponseBody
    public Object syncFrozen(HttpServletRequest req, HttpServletResponse resp,@RequestParam("forzenNo") String forzenNo,@RequestParam("type") Long type) {
    	ResultDO retDO = new ResultDO();
    	try {
    		retDO=forzenBalanceService.synchronizedBalanceForzen(forzenNo,type);
    	}catch(Exception e) {
    		logger.error("同步冻结和解冻异常,订单号：", forzenNo);
    		retDO.setSuccess(false);
    	}
    	return retDO ;
    }
   /**
    * 
    * @Description:解冻明细
    * @param req
    * @param resp
    * @param projectId
    * @return
    * @throws ServletRequestBindingException
    * @author: chaisen
    * @time:2016年7月27日 下午1:59:27
    */
    @RequestMapping(value = "unforzenDetail")
    @ResponseBody
    public Object hostingCollectTradeList(HttpServletRequest req, HttpServletResponse resp,@RequestParam("forzenNo") String forzenNo) throws Exception {
        Page<BalanceUnforzen> pageRequest = new Page<BalanceUnforzen>();
        try {
            List<BalanceUnforzen> unForzenList = balanceUnForzenManager.selectUnforzenListByForzenNo(forzenNo);
            int size = unForzenList.size();
            pageRequest.setiDisplayLength(size);
            pageRequest.setData(unForzenList);
            pageRequest.setPageNo(1);
            pageRequest.setiTotalRecords(size);
            pageRequest.setiTotalDisplayRecords(size);
            pageRequest.setiDisplayStart(0);
        } catch (Exception e) {
            logger.error("查询解冻明细异常", e);
        }
        return pageRequest;
    }
    /**
     * 
     * @Description:查询解冻详情
     * @param req
     * @param resp
     * @return
     * @throws Exception
     * @author: chaisen
     * @time:2016年8月1日 下午5:53:54
     */
    @RequestMapping(value = "detail")
	@ResponseBody
	public Object getUnforzenInfo(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Long id = ServletRequestUtils.getLongParameter(req, "id");
		BalanceForzen balanceForzen=forzenBalanceService.selectByPrimayKey(id);
		if(balanceForzen!=null){
			balanceForzen.setAmount(null);
			Member member = memberService.getMemberById(balanceForzen.getMemberId());
			if(member!=null){
				balanceForzen.setTrueName(member.getTrueName());
				balanceForzen.setMobile(member.getMobile());
			}
		}
			return balanceForzen;
		}
   
}
