package com.yourong.backend.tc.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.backend.BaseController;
import com.yourong.backend.ic.service.ProjectService;
import com.yourong.backend.tc.service.TransactionService;
import com.yourong.common.annotation.LogInfoAnnotation;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.TransactionForOrder;

@Controller
@RequestMapping("transaction")
public class TransactionController extends BaseController {

	@Autowired
	private TransactionService transactionService;
	@Autowired
	private ProjectService projectService;
	

	/**
	 * 生成合同
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "generate/contract")
	public boolean generateContract(HttpServletRequest req, HttpServletResponse resp
			,@RequestParam("transactionId") Long transactionId) {
		return transactionService.generateContract(transactionId);
	}

	/**
	 * 项目放款接口
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "loan")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "交易模块",desc = "执行放款")
	public ResultDO<?> beforeLoanToOriginalCreditor(HttpServletRequest req, HttpServletResponse resp
			,@RequestParam("projectId") Long projectId) {
		
		try {
			return transactionService.beforeLoanToOriginalCreditor(projectId,this.getCurrentLoginUserInfo().getId());
		} catch (Exception e) {
			logger.error("项目满额放款发生异常", e);
		}
		return null;
	}
	
	/**
	 * 直接代付
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/direct/host/pay")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "交易模块",desc = "直接代付")
	@RequiresPermissions("transaction:directHostPay")
	public ResultDO<?> directHostingPayTrade(
			HttpServletRequest req, 
			HttpServletResponse resp,
			@RequestParam("memberId") Long memberId,
			@RequestParam("amount") BigDecimal amount,
			@RequestParam("type") int type,
			@RequestParam("remark") String remark
			) {
		
		try {
			return transactionService.directHostingPayTrade(memberId, amount,type,remark);
		} catch (Exception e) {
			logger.error("直接代付发生异常，memberId="+memberId + ", amount=" + amount, e);
		}
		return null;
	}
	
	/**
	 * 直接代收
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/direct/host/collect")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "交易模块",desc = "直接代收")
	@RequiresPermissions("transaction:directHostCollect")
	public ResultDO<?> directHostingCollectTrade(
			HttpServletRequest req, 
			HttpServletResponse resp,
			@RequestParam("memberId") Long memberId,
			@RequestParam("amount") BigDecimal amount,
			@RequestParam("type") int type,
			@RequestParam("remark") String remark
			) {
		
		try {
			return transactionService.directHostingCollectTrade(memberId, amount, type,remark);
		} catch (Exception e) {
			logger.error("直接代收发生异常，memberId="+memberId + ", amount=" + amount, e);
		}
		return null;
	}
	
	/**
	 * 同步单笔代收交易状态
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/syn/host/collect")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "交易模块",desc = "同步单笔代收交易状态")
	@RequiresPermissions("transaction:synHostingCollectTrade")
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
	 * 保存代付到新浪（处理代付本地保存了，新浪没有保存的业务）目前只支持保存放款的代付，需要后续调整
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/add/sina/host/pay")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "交易模块",desc = "保存代付到新浪")
	@RequiresPermissions("transaction:addSinaHostPay")
	public ResultDO<?> addHostingPayTradeToSina(
			HttpServletRequest req, 
			HttpServletResponse resp,
			@RequestParam("tradeNo") String tradeNo
			) {
		
		try {
			return transactionService.addHostingPayTradeToSina(tradeNo);
		} catch (Exception e) {
			logger.error("保存代付到新浪（处理代付本地保存了，新浪没有保存的业务）发生异常，tradeNo="+tradeNo , e);
		}
		return null;
	}
	/**
	 * 根据代收交易号处理还本付息代付
	 * @param req
	 * @param resp
	 * @return
	 */
	/*@RequestMapping(value = "/process/pay/interest")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "交易模块",desc = "根据代收交易号处理还本付息代付")
	@RequiresPermissions("transaction:processPayInterestAndPrincipal")
	public ResultDO<?> processPayInterestAndPrincipal(
			@RequestParam("tradeNo") String tradeNo
			) {
		ResultDO<?> result = new ResultDO();
		try {
			result = transactionService.processPayInterestAndPrincipal(tradeNo);
		} catch (Exception e) {
			result.setSuccess(false);
			logger.error("根据代收交易号处理还本付息代付发生异常，tradeNo="+tradeNo , e);
		}
		return result;
	}*/

	/**
	 * 交易管理
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "index")
	@RequiresPermissions("transaction:index")
	public String showOrderIndex(HttpServletRequest req, HttpServletResponse resp) {
		return "tc/transaction/index";
	}
	
	/**
	 * 交易管理分页查询
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "ajax")
    @RequiresPermissions("transaction:ajax")
    @ResponseBody
    public Object showOrderPages(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
    	Page<TransactionForOrder> pageRequest = new Page<TransactionForOrder>();
		Map<String, Object> map = getPageInfoFromRequest(req, pageRequest);
        	Page<TransactionForOrder> pager = transactionService.queryTansactionForList(pageRequest, map);	 
         return pager;
    }
	
	/**
	 * 根据交易ID查询付息记录
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletRequestBindingException
	 */
	@RequestMapping(value = "showInterest")
	@RequiresPermissions("transaction:showInterest")
	@ResponseBody
	public Object showInterestRecord(HttpServletRequest req, HttpServletResponse resp) throws ServletRequestBindingException {
		Page<TransactionInterest> pageRequest = new Page<TransactionInterest>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", ServletRequestUtils.getLongParameter(req, "transactionId"));
		Page<TransactionInterest> pager = transactionService.showInterestRecord(pageRequest, map);
		return pager;
	}
	
	@RequestMapping(value="/syn/project/host/collect")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "交易模块",desc = "同步项目代收交易状态")
	@RequiresPermissions("transaction:synProjectHostCollect")
	public Object synProjectHostCollectToSina(
			HttpServletRequest req, 
			HttpServletResponse resp,
			@RequestParam("projectId") Long projectId
			) {
	   transactionService.synProjectHostCollectToSina(projectId);
       return "1";
	}
	
	
	/**
	 * 同步单笔代付交易状态
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/syn/host/pay")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "交易模块",desc = "同步单笔代付交易状态")
	@RequiresPermissions("transaction:synHostingPayTrade")
	public ResultDO<?> synHostingPayTrade(
			HttpServletRequest req, 
			HttpServletResponse resp,
			@RequestParam("tradeNo") String tradeNo
			) {
		try {
			return transactionService.synHostingPayTrade(tradeNo);
		} catch (Exception e) {
			logger.error("同步单笔代付交易状态发生异常，tradeNo="+tradeNo , e);
		}
		return null;
	}
	
	
	/**
	 * 同步单笔退款交易状态
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/syn/host/refund")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "交易模块",desc = "同步单笔退款交易状态")
	@RequiresPermissions("transaction:synHostingRefundTrade")
	public ResultDO<?> synHostingRefundTrade(
			HttpServletRequest req, 
			HttpServletResponse resp,
			@RequestParam("tradeNo") String tradeNo
			) {
		try {
			return transactionService.synHostingRefundTrade(tradeNo);
		} catch (Exception e) {
			logger.error("同步单笔退款状态发生异常，tradeNo="+tradeNo , e);
		}
		return null;
	}
	
	/**
	 * 根据垫资代收号发起垫资批付
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/create/batchPay/overdueRepay")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "交易模块",desc = "根据垫资代收号发起垫资批付")
	@RequiresPermissions("transaction:createBatchPayOverdueRepay")
	public ResultDO<?> createBatchPayOverdueRepay(
			HttpServletRequest req, 
			HttpServletResponse resp,
			@RequestParam("tradeNo") String tradeNo
			) {
		ResultDO<?> resultDO = new ResultDO();
		try {
			resultDO = projectService.createHostPayForOverdueRepayByCollectTradeNo(tradeNo);
			return resultDO;
		} catch (Exception e) {
			resultDO.setSuccess(false);
			logger.error("根据垫资代收号发起垫资批付发生异常，tradeNo="+tradeNo , e);
		}
		return resultDO;
	}
	
	/**
	 * 根据放款代收号发起放款批付
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/create/batchPay/loan")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "交易模块",desc = "根据放款代收号发起放款批付")
	@RequiresPermissions("transaction:createBatchPayLoan")
	public ResultDO<?> createBatchPayLoan(
			HttpServletRequest req, 
			HttpServletResponse resp,
			@RequestParam("tradeNo") String tradeNo
			) {
		ResultDO<?> resultDO = new ResultDO();
		try {
			resultDO = transactionService.createHostPayForLoan(tradeNo);
			return resultDO;
		} catch (Exception e) {
			resultDO.setSuccess(false);
			logger.error("根据放款代收号发起放款批付发生异常，tradeNo="+tradeNo , e);
		}
		return resultDO;
	}
	
	
	/**
	 * 根据批付号查询交易状态
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/syn/batch/host/pay")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "交易模块", desc = "根据批付号查询交易状态")
	@RequiresPermissions("transaction:synBatchHostingPayTrade")
	public ResultDO<?> synBatchHostingPayTrade(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam("batchPayNo") String batchPayNo) {
		ResultDO<?> resultDO = new ResultDO();
		try {
			resultDO = transactionService.synBatchHostingPayTrade(batchPayNo);
		} catch (Exception e) {
			logger.error("根据批付号查询交易状态，batchPayNo=" + batchPayNo, e);
		}
		return resultDO;
	}
	
	/**
	 * 根据项目id创建远程批付
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/create/remote/batch/pay")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "交易模块", desc = "根据批付号创建远程批付")
	@RequiresPermissions("transaction:createRemoteBatchPay")
	public ResultDO<?> createRemoteBatchPay(HttpServletRequest req, HttpServletResponse resp,@RequestParam("projectId") Long projectId) {
		ResultDO<?> resultDO = new ResultDO();
		try {
			resultDO = transactionService.createRemoteBatchPay(projectId);
		} catch (Exception e) {
			logger.error("根据项目id创建远程批付，projectId=" + projectId, e);
		}
		return resultDO;
	}
	/**
	 * 根据批次号创建本地批付
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "/create/host/batch/pay")
	@ResponseBody
	@LogInfoAnnotation(moduleName = "交易模块", desc = "根据批次号创建本地批付")
	@RequiresPermissions("transaction:createHostBatchPay")
	public ResultDO<?> createHostBatchPay(HttpServletRequest req, HttpServletResponse resp,@RequestParam("tradeNo") String tradeNo) {
		ResultDO<?> resultDO = new ResultDO();
		try {
			resultDO = transactionService.createHostBatchPay(tradeNo);
		} catch (Exception e) {
			logger.error("根据批次号创建本地批付发生异常，tradeNo=" + tradeNo, e);
		}
		return resultDO;
	}
}
