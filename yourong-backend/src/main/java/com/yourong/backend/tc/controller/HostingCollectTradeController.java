package com.yourong.backend.tc.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.yourong.backend.BaseController;
import com.yourong.backend.tc.service.HostingCollectTradeService;
import com.yourong.backend.tc.service.TransactionService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.QueryTradeResult;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.TradeItem;
import com.yourong.core.tc.model.HostingCollectTrade;

/**
 * 代收controller
 * @author wangyanji
 *
 */
@Controller
@RequestMapping("hostingCollectTrade")
public class HostingCollectTradeController extends BaseController{

	@Autowired
	private HostingCollectTradeService hostingCollectTradeService;
	@Autowired
	private TransactionService transactionService;
	@RequestMapping(value = "index")
	@RequiresPermissions("hostingCollectTrade:index")
	public String showOrderIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "tc/hostingCollectTrade/index";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
    @RequiresPermissions("hostingCollectTrade:ajax")
    @ResponseBody
    public Object findByPage(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
    	Page<HostingCollectTrade> pageRequest = new Page<HostingCollectTrade>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
        Page<HostingCollectTrade> pager = hostingCollectTradeService.findByPage(pageRequest,map);		
        return pager;
    }


	@SuppressWarnings("unchecked")
	@RequestMapping(value = "rebuild")
	@RequiresPermissions("hostingCollectTrade:rebuildCreate")
	@ResponseBody
	public Object rebuildCreate(HttpServletRequest req, HttpServletResponse resp,@RequestParam("tradeNo") String  tradeNo) throws ServletRequestBindingException {
		return transactionService.rebuildHostingCollectTrade(tradeNo);
	}





	/**
	 * 
	 * @Description:根据代收交易号查询代收交易记录
	 * @param req
	 * @param resp
	 * @param id
	 * @return
	 * @author: chaisen
	 * @time:2015年11月27日 下午4:07:28
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "tradeDetail")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "托管代收管理",desc = "根据代收交易号查询代收交易记录")
	@RequiresPermissions("hostingCollectTrade:queryTrade")
	public Object hostTradeDetailByPage(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam("id") Long id
			) {
		ResultDto<QueryTradeResult> result=new ResultDto<QueryTradeResult>();
		 Page<TradeItem> pager=new  Page<TradeItem> ();
		 List<TradeItem> list = Lists.newArrayList();
		try {
			result= hostingCollectTradeService.queryHostingCollectTradeRecord(id);
			QueryTradeResult queryTradeResult = result.getModule();
			if(queryTradeResult!=null){
				pager.setData(queryTradeResult.getPayItemList());
				pager.setTotalPageCount(result.getTotalCount());
				pager.setPageNo(queryTradeResult.getPageNo());
				pager.setiTotalRecords(result.getTotalCount());
			}else{
				pager.setData(list);
			}
		} catch (Exception e) {
			logger.error("根据代收交易号查询代收交易记录发生异常，id="+id , e);
		}
			return pager;
		
	}

	/**
	 * 
	 * @Description:同步单笔代收交易状态
	 * @param req
	 * @param resp
	 * @param tradeNo
	 * @return
	 * @author: chaisen
	 * @time:2015年11月27日 下午4:07:49
	 */
	@RequestMapping(value = "/syn/host/collect")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "托管代收管理",desc = "同步单笔代收交易状态")
	@RequiresPermissions("hostingCollectTrade:synTrade")
	public ResultDO<?> synHostingCollectTrade(
			HttpServletRequest req, 
			HttpServletResponse resp,
			@RequestParam("tradeNo") String tradeNo
			) {
		
		try {
			return transactionService.synHostingCollectTrade(tradeNo);
		} catch (Exception e) {
			logger.error("同步单笔代收交易状态发生异常，tradeNo="+tradeNo , e);
		}
		return null;
	}

	/**
	 * 
	 * @Description:根据代收号发起代收完成/撤销
	 * @param req
	 * @param resp
	 * @param tradeNo
	 * @param handleType
	 * @return
	 * @author: wangyanji
	 * @time:2016年9月8日 上午9:26:08
	 */
	@RequestMapping(value = "/handlePreAuthCollectTrade", method = RequestMethod.GET)
	@ResponseBody
	@LogInfoAnnotation(moduleName = "托管代收管理", desc = "根据代收号发起代收完成/撤销")
	public Object handlePreAuthTrade(HttpServletRequest req, HttpServletResponse resp) {
		String tradeNo = ServletRequestUtils.getStringParameter(req, "tradeNo", null);
		int handleType = ServletRequestUtils.getIntParameter(req, "handleType", 0);
		try {
			return hostingCollectTradeService.handlePreAuthTrade(tradeNo, handleType);
		} catch (Exception e) {
			logger.error("根据代收号发起代收完成/撤销发生异常，tradeNo=" + tradeNo, e);
		}
		return null;
	}

}
