package com.yourong.web.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yourong.common.domain.ResultDO;
import com.yourong.common.enums.ResultCode;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.uc.model.MemberCheck;
import com.yourong.web.dto.MemberSessionDto;
import com.yourong.web.service.CouponService;
import com.yourong.web.service.MemberService;
import com.yourong.web.utils.ServletUtil;

@Controller
@RequestMapping("birthday")
public class BirthdayController extends BaseController{
	
	@Autowired
	private CouponService couponService;
	
	@Autowired
    private MemberService memberService;

	/**
	 * 领取50元生日优惠券
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "receiveBirthday50Coupon", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO receiveBirthday50Coupon(HttpServletRequest req, HttpServletResponse resp){
		ResultDO result = new ResultDO();
		MemberSessionDto userName = ServletUtil.getUserDO();
		Long memberID = userName.getId();
		result = preVerification(result, userName.getBirthday(), 2);
		if(result.isSuccess()){
			return couponService.receiveBirthday50Coupon(memberID, userName.getBirthday());
		}
		return result;
	}
	
	/**
	 * 领取1%元生日优惠券
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "receiveBirthday001Coupon", method = RequestMethod.POST)
	@ResponseBody
	public ResultDO receiveBirthday001Coupon(HttpServletRequest req, HttpServletResponse resp){
		ResultDO result = new ResultDO();
		MemberSessionDto userName = ServletUtil.getUserDO();
		Long memberID = userName.getId();
		result = preVerification(result, userName.getBirthday(), 3);
		if(result.isSuccess()){
			return couponService.receiveBirthday001Coupon(memberID, userName.getBirthday());
		}
		return result;
	}
	
	/**
	 * 生日签到
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value = "goSignIn", method = RequestMethod.POST)
    @ResponseBody
    public ResultDO<MemberCheck> memberSignIn(HttpServletRequest req, HttpServletResponse resp){
		ResultDO<MemberCheck> result = new ResultDO<MemberCheck>();
		MemberSessionDto userName = ServletUtil.getUserDO();
		Integer loginSource = userName.getLoginSource();
		if(loginSource==null) {
			loginSource = TypeEnum.MEMBER_LOGIN_SOURCE_PC.getType();
		}
		result = preVerification(result, userName.getBirthday(), 1);
		try {
			if(result.isSuccess()){
				result = memberService.memberCheck(userName.getId(), loginSource);
			}
		} catch (DuplicateKeyException mysqlE) { //重复签到不打印日志
			logger.error("会员重复签到！memberId={}", userName.getId());
		} catch (Exception e) {
			logger.error("用户签到发生异常", e);
			result.setResultCode(ResultCode.ERROR_SYSTEM);
		}
		return result;
    }
	
	/**
	 * 前期验证
	 * @param result
	 * @param birthday
	 * @return
	 */
	private ResultDO preVerification(ResultDO result, Date birthday, int source){
		if(!ServletUtil.isVerifyTrueName()){
			result.setResultCode(ResultCode.MEMBER_UN_TRUE_NAME);
			return result;
		}
		Activity activity = memberService.getBirthdayActivity();
		if(activity != null){
			if(DateUtils.getCurrentDate().before(activity.getStartTime())){
				result.setResultCode(ResultCode.ACTIVITY_BIRTHDAY_IS_NOT_START);
				return result;
			}
		}
		int status = memberService.getBirthdayStatus(birthday);
		if(status != 1){
			if(source == 1){
				result.setResultCode(ResultCode.MEMBER_BIRTHDAY_SIGN_TIPS_ERROR);
			}else if(source == 2){
				result.setResultCode(ResultCode.MEMBER_BIRTHDAY_COUPON_TIPS_ERROR);
			}else{
				result.setResultCode(ResultCode.MEMBER_BIRTHDAY_INCOME_TIPS_ERROR);
			}
			return result;
		}
		return result;
	}
	
}
