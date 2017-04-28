package com.yourong.backend.jobs;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.yourong.common.constant.Constant;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.thirdparty.sinapay.common.util.StringUtil;
import com.yourong.core.ic.manager.BorrowerCreditManager;
import com.yourong.core.ic.model.BorrowerCredit;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.sys.model.SysDict;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;

/**
 * 
 * @Description 减少借款人存续量任务
 * @author luwenshan
 * @time 2016年12月09日 下午4:32:13
 *
 */
public class ReduceBorrowerCreditTask {
	
	@Resource
	private BorrowerCreditManager borrowerCreditManager;
	
	@Resource
	private MemberManager memberManager;
	
	@Resource
	private SysDictManager sysDictManager;
	
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	private static final Logger logger = LoggerFactory.getLogger(ReduceBorrowerCreditTask.class);

	public void work() {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("减少直投项目借款人的存续量定时任务 start");
					// 获取个人/企业用户的授信额信息
					List<BorrowerCredit> borrowerCredits = borrowerCreditManager.getMemberOrEnterpriseBorrowerCredit(null, null, ProjectEnum.PROJECT_TYPE_DIRECT.getType());
					// 获取渠道商用户的授信额信息
					List<BorrowerCredit> channelBorrowerCredits = borrowerCreditManager.getChannelBorrowerCredit(null, ProjectEnum.PROJECT_TYPE_DIRECT.getType());
					if (!channelBorrowerCredits.isEmpty()) {
						borrowerCredits.addAll(channelBorrowerCredits);
					}
					for (BorrowerCredit borrowerCredit : borrowerCredits) {
						try {
							if (borrowerCredit.getBorrowerType() == null) {
								continue;
							}
							BorrowerCredit borrowerCreditInfo = borrowerCreditManager.selectByBorrower
									(borrowerCredit.getBorrowerId(), borrowerCredit.getBorrowerType(), borrowerCredit.getOpenPlatformKey());
							if(borrowerCreditInfo != null) {
								// 如果是个人/企业用户
								if (StringUtil.isBlank(borrowerCredit.getOpenPlatformKey())) { // 如果是个人/企业用户
									Member member = memberManager.selectByPrimaryKey(borrowerCredit.getBorrowerId());
									// 过滤垃圾用户数据
									if (member == null) {
										continue;
									}
									borrowerCreditInfo.setBorrowerTrueName(member.getTrueName());
									borrowerCreditInfo.setBorrowerMobile(member.getMobile());
								}
								borrowerCreditInfo.setInvestType(borrowerCredit.getInvestType());
								borrowerCreditInfo.setPayablePrincipal(borrowerCredit.getPayablePrincipal());
								borrowerCreditManager.updateByBorrower(borrowerCreditInfo);
							} else {
								// 如果是渠道商用户
								if (StringUtil.isNotBlank(borrowerCredit.getOpenPlatformKey())) {
									SysDict sysDict = sysDictManager.findByGroupNameAndKey("channel_key", borrowerCredit.getOpenPlatformKey());
									if(sysDict != null){
										borrowerCredit.setBorrowerTrueName(sysDict.getLabel());
										borrowerCredit.setOpenPlatformKey(sysDict.getKey());
									}
									borrowerCredit.setCreditAmount(Constant.DEFALUT_ZT_BORROWER_CREDIT);
									borrowerCredit.setOnlineFlag(TypeEnum.BORROWER_CREDIT_NORMAL.getType());
								} else { // 如果是个人/企业用户
									// 过滤垃圾数据
									if (borrowerCredit.getBorrowerId() == null) {
										continue;
									}
									Member member = memberManager.selectByPrimaryKey(borrowerCredit.getBorrowerId());
									// 过滤垃圾用户数据
									if (member == null) {
										continue;
									}
									borrowerCredit.setBorrowerTrueName(member.getTrueName());
									borrowerCredit.setBorrowerMobile(member.getMobile());
									borrowerCredit.setCreditAmount(Constant.DEFALUT_ZT_BORROWER_CREDIT);
									borrowerCredit.setOnlineFlag(TypeEnum.BORROWER_CREDIT_NORMAL.getType());
								}
								
								borrowerCreditManager.saveBorrower(borrowerCredit);
							}
						} catch (ManagerException e) {
							logger.error("减少直投项目借款人的存续量出现错误,borrowerId={},borrowerType={},openPlatformKey={}",
									borrowerCredit.getBorrowerId(), borrowerCredit.getBorrowerType(), borrowerCredit.getOpenPlatformKey(),e);
						}
					}
					logger.info("减少直投项目借款人的存续量定时任务 end");
				} catch (Exception e) {
					logger.error("减少直投项目借款人的存续量定时任务异常", e);
				}
			}
		});
		
	}
	
}
