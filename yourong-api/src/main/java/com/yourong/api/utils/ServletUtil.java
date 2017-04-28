package com.yourong.api.utils;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.yourong.api.dto.MemberSessionDto;
import com.yourong.api.service.BalanceService;
import com.yourong.api.service.MemberService;
import com.yourong.common.constant.Constant;
import com.yourong.common.enums.MemberLogEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.SpringContextHolder;
import com.yourong.common.util.StringUtil;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.mc.model.Activity;
import com.yourong.core.uc.manager.MemberCheckManager;
import com.yourong.core.uc.model.Member;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


public class ServletUtil {
	
	private static MemberService memberService = SpringContextHolder.getBean(MemberService.class);
	private static BalanceService balanceService = SpringContextHolder.getBean(BalanceService.class);
	private static MemberCheckManager memberCheckManager = SpringContextHolder.getBean(MemberCheckManager.class);
	/**
	 * 获取当前登陆的用户对象
	 * @return
	 */
	public static MemberSessionDto getUserDO() {
		MemberSessionDto username = null;
		HttpSession session = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest().getSession();
		username = (MemberSessionDto) session.getAttribute(Constant.CURRENT_USER);
		return username;
	}

    /**
     * 绑卡支付显示名，不显示姓
     * @return
     */
    public  static  String  getMemberMaskTrueName(){
        MemberSessionDto member = getUserDO();
        return StringUtil.maskFirstName(member.getTrueName());
    }


    /**
     * 获取手机号或者用户名
     * @return
     */
    public static String getUserNameOrMobile(){
        MemberSessionDto member = getUserDO();
        if(StringUtil.isNotBlank(member.getUsername())) {
            return member.getUsername();
        } else {
            return member.getMobile().toString();
        }
    }
	/**
	 * 是否实名认证
	 * @return
	 * author: pengyong
	 * 下午3:58:01
	 */
	public static boolean isVerifyTrueName(){
		boolean result = false;
		MemberSessionDto member = getUserDO();
		if(member !=null && StringUtil.isNotBlank(member.getTrueName()) && StringUtil.isNotBlank(member.getIdentityNumber())){
			result = true;
		}
		return result;
	}
	
	/**
	 * 获得指定用户的头像
	 * @param id
	 * @return
	 */
	public static String getMemberAvatarById(Long id){
		return memberService.getMemberAvatar(id);
	}
	
	
	/**
	 * 邮箱是否已经认证
	 * @return
	 */
	public static boolean isEmailBinded(String email){
		boolean result = false;
		Member member = memberService.selectByEmail(email);
		if(member !=null ){
			result = true;
		}	
		return result;
	}

	/**
	 * 获得用户昵称
	 * @param id
	 * @return
	 */
	public static String getMemberUserName(Long id){
		return memberService.getFormatMemberUserName(id);
	}
	

	/**判断登陆用户是否是手机端**/
	public static boolean isMobile(HttpServletRequest request) {
	    String userAgentInfo = request.getHeader("User-Agent");
	    if(StringUtil.isBlank(userAgentInfo)){
	    	return false;
	    }
	    String[] agents = {"Android", "iPhone", "SymbianOS", "Windows Phone", "BB10", "PlayBook", "JDQ39", "iPad", "iPod"};
	    boolean flag = false;
	    for (int v = 0; v < agents.length; v++) {
	        if (userAgentInfo.indexOf(agents[v]) > 0) {
	            flag = true;
	            break;
	        }
	    }
	    return flag;
	}

	/**
	 * 获取人气值
	 * @return
	 */
	public int getPopularityVaule() {
		MemberSessionDto member = getUserDO();
		Balance balance = balanceService.queryBalance(member.getId(), TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
		if(balance != null){
			return balance.getAvailableBalance().intValue();
		}else{
			return 0;
		}
	}

	/**
	 * 获取今日签到所获得的人气值
	 * @param memberId
	 * @return
	 * @throws ManagerException
	 */
	public int getPopularityValueForChecked() throws ManagerException {
		return memberCheckManager.getPopularityValueForChecked(getUserDO().getId());
	}
	
	/**
	 * 获得生日专题人气值
	 * @return
	 * @throws ManagerException 
	 */
	public int getBirthdayPopularityVaule() throws ManagerException{
		int v = getPopularityValueForChecked();
		if(v >= 10){ //签到10倍
			return v/10;
		}
		return v;
	}
	
	/**
	 * 生日活动是否开始
	 * @return
	 */
	public boolean isBirthdayActivity(){
		Activity activity = memberService.getBirthdayActivity();
		if(activity != null){
			if(DateUtils.getCurrentDate().after(activity.getStartTime())){
				return true;
			}
		}
		return false;
	}
	/**
	 * 
	 * @Description:获取用户信息
	 * @param memberId
	 * @return
	 * @author: chaisen
	 * @time:2016年6月21日 下午2:56:40
	 */
	public static Member getMemberInfoByMemberId(Long memberId){
		return memberService.selectByPrimaryKey(memberId);
	}
	
	public boolean isBirthday(){
		MemberSessionDto member = getUserDO();
		int status = getBirthdayStatus(member.getBirthday());
		if(status == 1){
			return true;
		}
		return false;
	}
	
	public int getBirthdayStatus(Date birthday){
		if(birthday == null){
			return 0;
		}
		Date date = DateUtils.getCurrentDate();
		int c_month = DateUtils.getMonth(date);
		int c_day = DateUtils.getDate(date);
		int b_month = DateUtils.getMonth(birthday);
		int b_day = DateUtils.getDate(birthday);
		if(c_month < b_month){
			return 0;//生日还未到
		}else if(c_month == b_month && c_day == b_day){
			return 1;//今天生日
		}else{
			return -1;//生日已经过了
		}
	}
	
	/**
	 * 获取用户Id后6位
	 * @throws ManagerException 
	 */
	public String getMemberId() throws ManagerException{
		MemberSessionDto member = getUserDO();
		String memberId = member.getId().toString();
		return memberId.substring(memberId.length()-6,memberId.length());
	}
}
