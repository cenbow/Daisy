package com.yourong.backend.jobs;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.collect.Maps;
import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.cache.RedisPlatformClient;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.thirdparty.sinapay.SinaPayClient;
import com.yourong.common.thirdparty.sinapay.common.domain.ResultDto;
import com.yourong.common.thirdparty.sinapay.pay.domain.respone.QueryFinResponse;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.StringUtil;
import com.yourong.core.fin.manager.BalanceManager;
import com.yourong.core.fin.manager.CapitalInOutLogManager;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.uc.manager.MemberBankCardManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberBankCard;

/**
 * 存钱罐 收益 ， 定时同步 ，暂时定 为， 每天一次
 * 
 * @author Administrator
 *
 */
public class PiggyBankEaringsTask {
	private static final Logger logger = LoggerFactory.getLogger(PiggyBankEaringsTask.class);
	@Resource
	private BalanceManager balanceManager;	
	
	@Resource
	private CapitalInOutLogManager capitalInOutLogManager;
	
	@Autowired
	private SinaPayClient sinaPayClient;   
	
	@Resource
	private MemberManager  memerManager;

	@Autowired
	private MemberBankCardManager memberBankCardManager;

	@Autowired
	private SysDictManager sysDictManager;
	
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				StopWatch st = new StopWatch();
				logger.info("同步存钱罐收益开始");
				st.start();
				int size = 3000;
				Map<String, Object> map = Maps.newHashMap();
				try {
					int totalCount = memerManager.selectActiveForPaginTotalCount(map);
					int totalPageCount = (int) Math.ceil((double) totalCount / (double) size);
					for (int i = 1; i <= totalPageCount; i++) {
						if (i == 1) {
							map.put(Constant.STARTROW, 0);
						} else {
							map.put(Constant.STARTROW, (i - 1) * size);
						}
						map.put(Constant.PAGESIZE, size);
						List<Member> list = memerManager.selectActiveForPagin(map);
						getSinaPayIncome(list);
					}
				} catch (Exception e) {
					logger.error("同步存钱罐收益 ，定时任务异常", e);
				}
				// 同步 安全卡
				synchronizeSecurityCard();
				// 同步存七日年化收益
				sysnchronizeFundYield();
				// 同步平台数据
				synchronizePlatformBalance();
				st.stop();
				logger.info("同步存钱罐收益，耗费时间={}毫秒", st.getTime());
			}
		});
	}

	/**
	 * 同步新浪存钱罐收益
	 * @param list
	 */
	private void getSinaPayIncome(List<Member> list) {
		for (Member member : list) {
            //实名认证，才能查询存钱罐余额
            if (isSyncMember(member)) {
                try {
                    balanceManager.synThirdPayEaring(member.getId());
                } catch (Exception e2) {
                    logger.error("同步存钱罐收益 ，定时任务异常,memeberid={}",member.getId(), e2);
                }
                //邀请码放进redis
                RedisMemberClient.setMemberInviteCode(member.getShortUrl(), member.getId());
            }
        }
	}

	/**
	 * 是否要同步
	 * @param member
	 * @return
	 */
	private boolean isSyncMember(Member member) {
		return member!=null && StringUtil.isNotBlank(member.getTrueName()) && StringUtil.isNotBlank(member.getIdentityNumber()) && (member.getStatus()!=0);
	}

	/**
	 * 同步存七日年化收益
 	 */
	private void sysnchronizeFundYield() {
		try{
			//同步七日
			String fincode = Config.finCodeSina;
			ResultDto<?> fundYield = sinaPayClient.queryFundYield(fincode);
			QueryFinResponse module = (QueryFinResponse) fundYield.getModule();
			RedisPlatformClient.setSinapaySevenDaysBonus(module.getYieldList());
		}catch(Exception e){
			logger.error("同步存七日年化收益", e);
		}
	}

	/**
	 * 同步平台数据
	 */
	private void synchronizePlatformBalance() {
		try {
			balanceManager.synchronizedBalance(Long.valueOf(Config.internalMemberId), TypeEnum.BALANCE_TYPE_BASIC);
		} catch (Exception e) {
			logger.error("同步平台余额失败", e);
		}
	}

	/**
	 * 同步安全卡
	 */
	private void  synchronizeSecurityCard(){

		if (!isSynchronizeSecurityCard()){
			logger.info("未同步安全卡");
			return;
		}
		logger.info("同步安全卡开始");
		List<MemberBankCard> memberBankCards = memberBankCardManager.selectAllQuickPayNotSecurity();
		if (Collections3.isEmpty(memberBankCards))
			return;
		for (MemberBankCard card:memberBankCards){
			try {
				memberBankCardManager.setSecurityCardFromThirdPay(card.getId());
			} catch (Exception e) {
				logger.error("同步安全卡数据失败",e);
			}
		}
		logger.info("同步安全卡结束");
	}

	/**
	 * 是否要同步安全卡
	 * @return
	 */
	private boolean isSynchronizeSecurityCard(){
		boolean result = false;
		try {
			SysDict sysDict = this.sysDictManager.findByGroupNameAndKey("synchronize_card", "is_syn");
			if (sysDict!=null ){
				result = StringUtil.equalsIgnoreCases(sysDict.getValue(),"Y",true);
			}
		} catch (Exception e) {
			logger.error("获取 同步安全卡字典项失败",e);
			result = false;
		}
		return result;
	}

}
