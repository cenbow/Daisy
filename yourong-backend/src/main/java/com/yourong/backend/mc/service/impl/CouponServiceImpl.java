package com.yourong.backend.mc.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import com.yourong.common.util.DateUtils;
import com.yourong.core.mc.manager.CouponSendLogManager;
import com.yourong.core.mc.model.CouponSendLog;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.yourong.backend.mc.service.CouponService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.common.MessageClient;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.tc.manager.OrderManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Order;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;

import javax.annotation.Resource;

@Service
public class CouponServiceImpl implements CouponService {

	private static Logger logger = LoggerFactory
			.getLogger(CouponServiceImpl.class);
	@Autowired
	private CouponManager couponManager;
	
	@Autowired
	private MemberManager memberManager;
	
	@Autowired
	private OrderManager orderManager;
	
	
	@Autowired
	private TransactionManager transactionManager;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	private CouponSendLogManager couponSendLogManager;

	/**
	 * 优惠券分页列表
	 */
	@Override
	public Page<Coupon> findByPage(Page<Coupon> pageRequest,
			Map<String, Object> map) {
		try {
			return couponManager.findByPage(pageRequest, map);
		} catch (Exception e) {
			logger.error("优惠券分页列表获取失败", e);
		}
		return null;
	}

	@Override
	public Coupon selectByPrimaryKey(Long id) {
		try {
			return couponManager.selectByPrimaryKey(id);
		} catch (Exception e) {
			logger.error("优惠券分页列表获取失败", e);
		}
		return null;
	}

	@Override
	public Coupon receiveCoupon(Long userId, Long activityId,Long senderId) {
		try {
			return couponManager.receiveCoupon(userId, activityId, null,senderId);
		} catch (ManagerException e) {
			logger.error("领用优惠券失败，userId=" + userId + "activityId="
					+ activityId, e);
		}
		return null;
	}

	@Override
	public int useCoupon(String couponNo, Long projectId, Long transactionId) {
		int result = 0;
		try {
			result = couponManager.useCoupon(couponNo, projectId, transactionId);
		} catch (ManagerException e) {
			logger.error("使用优惠券失败，couponNo=" + couponNo + "activityId=" + transactionId, e);
		}
		return result;
	}

	/**
	 * 获取会员可用的优惠券
	 */
	@Override
	public List<Coupon> findUsableCouponsByMemberId(Long memberId,
			Integer couponType,String client,BigDecimal amountScope,Integer daysScope) {
		try {
			List<Coupon> coupons = couponManager.getUsableAndLimitedCoupons(memberId,couponType,client,amountScope,daysScope);
			return coupons;
		} catch (ManagerException e) {
			logger.error("获取会员可用优惠券失败,memberId=" + memberId + "couponType="
					+ couponType, e);
		}
		return null;
	}

	/**
	 * 定时过期
	 */
	@Override
	public Integer expireCouponTask() {
		try {
			Integer result = couponManager.expireCouponTask();
			return result;
		} catch (ManagerException e) {
			logger.error("定时过期优惠券失败", e);
		}
		return null;
	}

	/**
	 * 赠送优惠券
	 */
	@Override
	public Map<String, Object> giveCoupon(Long[] mobiles, Long couponTemplateId,Long senderId) {
		Map<String, Object> results= Maps.newHashMap();
		try {
			boolean result = false;
			for (Long mobile : mobiles) {
				Member member = memberManager.selectByMobile(mobile);
				if(member!=null){
					//调用赠送优惠券方法,后台赠送标明优惠券发送渠道和优惠券获取方式
					Coupon coupon = couponManager.receiveCouponSource(member.getId(),null,couponTemplateId,senderId,
							TypeEnum.COUPON_WAY_BACKEND.getType(),TypeEnum.COUPON_ACCESS_SOURCE_BACKEND_COMPENSATE.getType());
					if(coupon!=null){
						result = true;
					}
				}
				results.put(mobile.toString(), result);
			}
			return results;
		} catch (ManagerException e) {
			logger.error("赠送优惠券失败，mobiles="+mobiles+"couponTemplateId="+couponTemplateId, e);
		}
		return results;
	}

	/**
	 * 补发人气值
	 */
	@Override
	public boolean givePopularity(Long mobile,
			BigDecimal income, String remarks, Long senderId) {
		boolean result = false;
		try {
			Member member = memberManager.selectByMobile(mobile);
			if(member!=null){
				result = transactionManager.givePopularity(senderId, member.getId(),TypeEnum.FIN_POPULARITY_TYPE_REISSUE, income, remarks);
				MessageClient.sendMsgForGivePopularity(member.getId(), income, remarks);
			}
		} catch (Exception e) {
			logger.error("赠送人气值失败,电话mobile="+mobile+"人气值income="+income+"备注remarks="+remarks);
		}
		return result;
	}
	/**
	 * 解锁优惠券
	 */
	@Override
	public Object unlockedCouponByCouponNo(String couponNo) {
		ResultDO<Integer> rDO = new ResultDO<Integer>();
		int result = 0;
		try {
			List<Order> orderList = orderManager.selectOrderByCoupon(couponNo);
			for (Order order : orderList){
				if(Optional.fromNullable(order).isPresent()){
					if(order.getStatus()==0|| order.getStatus()==1){
						rDO.setResult(order.getStatus()+10);
						rDO.setSuccess(false);
						return rDO;
					}
					if(order.getStatus()==3){
						couponManager.usedCouponByCouponNo(couponNo);
						rDO.setResult(order.getStatus()+10);
						rDO.setSuccess(false);
						return rDO;
					}
				}
			}
			
			result = couponManager.unlockedCouponByCouponNo(couponNo);
			if(result < 1) {
				Coupon c = couponManager.getCouponByCouponNo(couponNo);
				rDO.setResult(c.getStatus());
				rDO.setSuccess(false);
			} else {
				rDO.setResult(result);
				rDO.setSuccess(true);
			}
		} catch (Exception e) {
			logger.error("更新优惠券失败，couponNo={}",couponNo,e);
			rDO.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return rDO;
	}

	/**
	 * 查询用户名下优惠券
	 */
	@Override
	public Page<Coupon> getCouponByMember(Page<Coupon> pageRequest,
			Map<String, Object> map) {
		try {
			return couponManager.selectCouponByMermberId(pageRequest, map);
		} catch (Exception e) {
			logger.error("查询指定用户名下的优惠券失败");
		}
		return null;

	}

	@Override
	public Object sendCoupon(final File file, final Long templateId, final Long successMobile, final Long senderId) {
		ResultDO<Integer> rDO = new ResultDO<Integer>();
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				logger.info("自动导入异步线程开始");
				String sign=templateId+"-"+ DateUtils.getCurrentDateTime();
				XSSFWorkbook hssfWorkbook= null;
				try {
					hssfWorkbook = new XSSFWorkbook(new FileInputStream(file));
				} catch (IOException e) {
					logger.error("解析Excel异常",e);
				}
				XSSFSheet sheet = hssfWorkbook.getSheetAt(0);
				DecimalFormat decimalFormat=new DecimalFormat("0");
				for(int j=0;j<sheet.getLastRowNum()+1;j++){
					XSSFRow row = sheet.getRow(j);
					XSSFCell cell = row.getCell(0);
					cell.setCellType(CellType.NUMERIC);
					Long memberId=null;
					try {
						memberId =Long.parseLong(decimalFormat.format(cell.getNumericCellValue()));
					} catch (NumberFormatException e) {
						logger.error("memberid转换异常{}",cell.getNumericCellValue(),e);
						continue;
					}
					if (memberId==null){
						logger.error("memberid不存在{}",cell.getNumericCellValue());
						continue;
					}
					CouponSendLog couponSendLog=new CouponSendLog();
					couponSendLog.setMemberId(memberId);
					couponSendLog.setTemplateId(templateId);
					couponSendLog.setSign(sign);
					couponSendLog.setCreateTime(new Date());
					couponSendLog.setUpdateTime(new Date());
					couponSendLog.setSign(sign);

					Coupon coupon = null;
					try {
						//发放优惠券
						coupon = couponManager.receiveCouponSource(memberId,null,templateId,senderId,
                                TypeEnum.COUPON_WAY_BACKEND.getType(),TypeEnum.COUPON_ACCESS_SOURCE_BACKEND_COMPENSATE.getType());
					} catch (ManagerException e) {
						logger.error("发放优惠券异常 templateId={} memberid{}",templateId,cell.getNumericCellValue(),e);
						couponSendLog.setSendStatus(0);
						couponSendLogManager.insertCouponSendLog(couponSendLog);
						continue;
					}
					if(coupon!=null){
						couponSendLog.setSendStatus(1);
						couponSendLogManager.insertCouponSendLog(couponSendLog);
					}else {
						couponSendLog.setSendStatus(0);
						couponSendLogManager.insertCouponSendLog(couponSendLog);
					}
				}
				//补偿机制，查询未成功发送成功的记录重新发送,只补发一次
				int nocount=couponSendLogManager.queryCountNoSendList(templateId,sign);
				int pagecount=(int) Math.ceil((double) nocount / (double) 100);
				for (int i=0;i<pagecount;i++){
					List<CouponSendLog> logList= couponSendLogManager.queryPageNoSendList(templateId,sign,100*i,100);
					for (CouponSendLog log:logList) {
						try {
							//发放优惠券
							Coupon coup = couponManager.receiveCouponSource(log.getMemberId(),null,log.getTemplateId(),senderId,
									TypeEnum.COUPON_WAY_BACKEND.getType(),TypeEnum.COUPON_ACCESS_SOURCE_BACKEND_COMPENSATE.getType());
							if (coup!=null){
								couponSendLogManager.updateSendStatusById(log.getId(),1,new Date());
							}
						} catch (ManagerException e) {
							logger.error("发放优惠券异常 templateId={} memberid{}",templateId,log.getMemberId(),e);
							continue;
						}
					}
				}
				int sendcount = couponSendLogManager.querySendCount(templateId,sign);
				//发放完毕发送短信通知
				MessageClient.sendShortMessageByMobile(successMobile,"有融网后台优惠券发放成功，总共发放人数："+sendcount+"。回复TD退订");
				logger.info("手机号:"+successMobile+"【有融网后台】优惠券发放成功，总共发放人数："+sendcount+"。");
			}
		});
		return rDO;
	}
}
