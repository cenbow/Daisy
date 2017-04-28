package com.yourong.backend.jobs;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.collect.Maps;
import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.StringUtil;
import com.yourong.core.fin.manager.RechargeLogManager;
import com.yourong.core.fin.manager.WithdrawLogManager;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.uc.manager.MemberInfoManager;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;

/**
 * 定时比对redis指是否正确
 * 
 * @author Administrator
 * 
 */
public class RedisAutoCompareValueTask {
	private static final Logger logger = LoggerFactory.getLogger(RedisAutoCompareValueTask.class);

	@Autowired
	private MemberInfoManager memberInfoManager;

	@Autowired
	private MemberManager memberManager;

	@Autowired
	private RechargeLogManager rechargeLogManager;

	@Autowired
	private WithdrawLogManager withdrawLogManager;

	@Autowired
	private TransactionManager transactionManager;

	@Autowired
	private SysDictManager sysDictManager;

	@Resource
	private MemberManager  memerManager;

	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	//分页大小
	final static  int size = 1000;

	/**
	 * 主方法
	 */
	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				logger.info("定时比对redis指是否正确 start");
				if (!isSynchronize()) {
					logger.info("没有开启，同步redis值 参数");
					return;
				}
				try {
					StopWatch st = new StopWatch();
					logger.info("同步redis值 开始,时间={}", st.getTime());
					st.start();
					// 设置会员相关资金信息
					addRedisValueBalance();
					logger.info("设置会员资金结束,时间={}", st.getTime());
					// 设置会员基础信息
					setRedisMemberInfo();
					st.stop();
					logger.info("同步redis值耗时{}毫秒", st.getTime());
				} catch (Exception e) {
					logger.error("同步redis值出错", e);
				}
				logger.info("定时比对redis指是否正确 end");
			}
		});
	}

	/**
	 * 是否要同步安全卡
	 * @return
	 */
	private boolean isSynchronize(){
		boolean result = false;
		try {
			SysDict sysDict = this.sysDictManager.findByGroupNameAndKey("synchronize_redis_value", "is_redis_syn");
			if (sysDict!=null ){
				result = StringUtil.equalsIgnoreCases(sysDict.getValue(),"Y",true);
			}
		} catch (Exception e) {
			logger.error("获取 同步redis字典项失败",e);
			result = false;
		}
		return result;
	}


	private void addRedisValueBalance() throws Exception {
		int theadcount = 5;
		ExecutorService exec = Executors.newFixedThreadPool(theadcount);
		final Map<String, Object> map = Maps.newHashMap();
		int totalCount = memerManager.selectActiveForPaginTotalCount(map);
		int totalPageCount = (int) Math.ceil((double) totalCount / (double) size);
		for (int i = 1; i <= totalPageCount; i++) {
			int startRow = 0;
			if (i == 1) {
				startRow = 0;
			} else {
				startRow = (i - 1) * size;
			}
			exec.execute(new SyncMemberRedisThead(startRow));
		}
	}

	class SyncMemberRedisThead implements Runnable {
		private int startRow;

		public SyncMemberRedisThead(int startRow) {
			this.startRow = startRow;
		}

		@Override
		public void run() {
			try {
				Map paginmap = Maps.newHashMap();
				paginmap.put(Constant.PAGESIZE, size);
				paginmap.put(Constant.STARTROW, startRow);
				List<Member> list = memerManager.selectActiveForPagin(paginmap);
				addRedisValue(list);
			} catch (Exception e) {
				logger.error("同步redis值， 用户金额相关数据出错", e);
			}
		}
	}

	private void setRedisMemberInfo() throws ManagerException {
		long maxMember = memberManager.getMaxMemberId();
		for (long i = Long.parseLong(Config.firstMemberId); i <= maxMember; i++) {
			Member member = memberManager.selectByPrimaryKey(i);
			if (member== null){
				continue;
			}
			//设置会员头像
			if (StringUtil.isNotBlank(member.getAvatars())){
				RedisMemberClient.setMemberAvatar(i, member.getAvatars());
			}
			//设置会员昵称
			if (StringUtil.isNotBlank(member.getUsername())){
				RedisMemberClient.setMemberUserName(i, member.getUsername());
			}
			//设置邀请码
			RedisMemberClient.setMemberInviteCode(member.getShortUrl(), i);
		}
	}

	private  void  addRedisValue(List<Member> members){
		for (Member member: members){
			try{
				setMemberMoney(member.getId());
			}catch (Exception e){
				logger.error("同步redis值， 用户金额相关数据出错,memberid={}",member.getId(),e);
			}
		}
	}
	private void setMemberMoney(long i) throws ManagerException {
		// 充值总金额
		BigDecimal recharegSucessAmount = rechargeLogManager.totalRecharge(i);
		if (recharegSucessAmount == null){
            recharegSucessAmount = BigDecimal.ZERO;
        }
		RedisMemberClient.setRechargeSuccessAmount(i, recharegSucessAmount);
		//充值总记录
		int count = rechargeLogManager.countRecharge(i);
		RedisMemberClient.setRechargeSuccessCount(i, count);
		// 提现金额
		BigDecimal totalWithDraw = withdrawLogManager.totalWithDraw(i);
		if (totalWithDraw == null){
            totalWithDraw = BigDecimal.ZERO;
        }
		RedisMemberClient.setWithdrawSuccessAmount(i, totalWithDraw);
		//提现次数
		int withdrawCount = withdrawLogManager.countWithDraw(i);
		RedisMemberClient.setWithdrawSuccessCount(i, withdrawCount);
		//累积投资
		BigDecimal countInvestmentAmount = transactionManager.countInvestmentAmount(i);
		if (countInvestmentAmount == null){
            countInvestmentAmount = BigDecimal.ZERO;
        }
		RedisMemberClient.setTotalInvestAmount(i, countInvestmentAmount);
	}
}