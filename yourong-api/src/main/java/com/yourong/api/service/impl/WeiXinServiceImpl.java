package com.yourong.api.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yourong.api.dto.WithdrawLogDto;
import com.yourong.api.service.*;
import com.yourong.common.cache.RedisMemberClient;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.util.*;
import com.yourong.core.uc.manager.MemberTokenManager;
import com.yourong.core.uc.model.Member;
import com.yourong.core.uc.model.MemberToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.weixin.Article;
import com.yourong.common.weixin.NewsMsg;
import com.yourong.common.weixin.TextMsg;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.mc.manager.CouponManager;
import com.yourong.core.mc.manager.WinxinTemplateManager;
import com.yourong.core.mc.model.WinxinTemplate;
import com.yourong.core.tc.manager.TransactionInterestManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.tc.model.TransactionInterest;
import com.yourong.core.tc.model.biz.MemberTransactionCapital;
import com.yourong.core.tc.model.query.TransactionInterestQuery;
import com.yourong.core.tc.model.query.TransactionQuery;

@Service
public class WeiXinServiceImpl implements WeiXinService {
	@Autowired
	private TransactionService transactionService;
	@Autowired
	private BalanceService balanceService;
	@Autowired
	private CouponManager couponManager;
	@Autowired
	private TransactionInterestManager transactionInterestManager;
	@Autowired
	private RechargeLogWithdrawLogService rechargeLogWithdrawLogService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private MemberTokenManager memberTokenManager;
	@Autowired 
	private WinxinTemplateManager winxinManager;
	
	@Autowired
	private TransactionManager transactionManager;

	public static final String ROOT_URL_WEB = PropertiesUtil.getProperties("root_url_web");

	@Override
	public String queryMemberBalance(Long memberId, String fromUserName, String toUserName) {
		Member member = memberService.selectByPrimaryKey(memberId);
		String trueName = StringUtil.isNotBlank(member.getUsername())?member.getUsername():StringUtil.maskMobile(member.getMobile());
		//存钱罐余额
		 Balance balance = balanceService.queryBalance(memberId, TypeEnum.BALANCE_TYPE_PIGGY);
		 MemberTransactionCapital capital = transactionService.getMemberTransactionCapital(memberId);
  	 	 //待收本金
  	 	 BigDecimal receivablePrincipal = capital==null?BigDecimal.ZERO:capital.getReceivablePrincipal();
  	 	 //待收收益
  	     BigDecimal receivableInterest = capital==null?BigDecimal.ZERO:capital.getReceivableInterest();
  	     //资产总计＝存钱罐余额＋待收本金＋待收收益
  	 	 BigDecimal totalAssets = balance.getBalance().add(receivablePrincipal).add(receivableInterest);
  	 	 TextMsg msg = new TextMsg();
  	 	 msg.add("亲爱的").add(trueName).add("，您目前：");
  	 	 msg.addln();
  	 	 msg.add("新浪存钱罐余额：").add(FormulaUtil.getFormatPriceRound(balance.getAvailableBalance()));
  	 	 msg.addln();
  	 	 msg.add("资产总计：").add(FormulaUtil.getFormatPriceRound(totalAssets));
  	 	 msg.addln();
  	 	 msg.setToUserName(fromUserName);
  	 	 msg.setFromUserName(toUserName);
		 return msg.toXml();
	}

	@Override
	public String queryMemberTransaction(Long memberId, String fromUserName, String toUserName) {
		Member member = memberService.selectByPrimaryKey(memberId);
		String trueName = StringUtil.isNotBlank(member.getUsername())?member.getUsername():StringUtil.maskMobile(member.getMobile());
		Integer performance = transactionService.getTransactionsTotalCount(memberId, 1);
		Integer repayment = transactionService.getTransactionsTotalCount(memberId, 2);
		TextMsg msg = new TextMsg();
		msg.add("亲爱的").add(trueName).add("，您目前：");
		msg.addln();
		msg.add("履约中项目：").add(performance.toString()).add("个");
		msg.addln();
		msg.add("已还款项目：").add(repayment.toString()).add("个");
		msg.addln();
		try {
			TransactionInterest interest = transactionInterestManager.queryLastPayTransactionInterest(memberId);
			if(interest != null){
				msg.add("最近即将还款：").add(DateUtils.formatDatetoString(interest.getEndDate(),"yyyy年MM月dd日"));
				msg.addln();
			}
		} catch (Exception e) {
		}
		msg.add("更多详情<a href='"+ROOT_URL_WEB+"/transaction/investment'>请点击这里</a>查看");
		msg.setToUserName(fromUserName);
 	 	msg.setFromUserName(toUserName);
  	 	return msg.toXml();
	}

	@Override
	public String queryMemberCoupon(Long memberId, String fromUserName, String toUserName) {
		Member member = memberService.selectByPrimaryKey(memberId);
		String trueName = StringUtil.isNotBlank(member.getUsername())?member.getUsername():StringUtil.maskMobile(member.getMobile());
		Integer coupons=0, income=0;
		try {
			//现金券
			coupons = couponManager.getCouponTotalCount(memberId, 1, 1);
			//收益券
			income = couponManager.getCouponTotalCount(memberId, 2, 1);
		} catch (ManagerException e) {
		}
		Balance balance = balanceService.queryBalance(memberId, TypeEnum.BALANCE_TYPE_MEMBER_POPULARITY);
		TextMsg msg = new TextMsg();
		msg.add("亲爱的").add(trueName).add("，您目前：");
		msg.addln();
		msg.add("可用现金券：").add(coupons.toString()).add("张");
		msg.addln();
		msg.add("可用收益券：").add(income.toString()).add("张");
		msg.addln();
		msg.add("人气值：").add(FormulaUtil.getIntegerDefaultZero(balance.getAvailableBalance())).add("点");
		msg.setToUserName(fromUserName);
 	 	 msg.setFromUserName(toUserName);
  	 	return msg.toXml();
	}

	@Override
	public String queryMemberWithDraw(Long memberId, String fromUserName, String toUserName) {
		int iDisplayStart = 0 ;
		Map<String, Object> map = Maps.newHashMap();
		map.put(Constant.STARTROW, iDisplayStart);
		map.put(Constant.PAGESIZE, 3);
		map.put("memberId", memberId);
		List<WithdrawLogDto> list = rechargeLogWithdrawLogService.selectWithdrawLogForPaginWeb(map);
		TextMsg msg = new TextMsg();
		msg.setToUserName(fromUserName);
		msg.setFromUserName(toUserName);
		if (Collections3.isNotEmpty(list)){
			for (WithdrawLogDto dto:list){
				msg.add(dto.getWithdrawNo());
				msg.addln();
				msg.add("提现时间点:").add(DateUtils.formatDatetoString(dto.getWithdrawTime(),DateUtils.TIME_PATTERN));
				msg.addln();
				msg.add("提现金额:").add(FormulaUtil.getFormatPriceRound(dto.getWithdrawAmount()));
				msg.addln();
				msg.add("提现卡号:").add(dto.getBankCardNo());
				msg.addln();
				msg.add("提现状态:").add(dto.getStatusToS());
				msg.addln();
				msg.addln();
			}
			msg.add("更多详情").add("<a href='").add(ROOT_URL_WEB).add("/memberBalance/rechargeIndex'>").add("请点击这里").add("</a>").add("查看");
		}else {
			msg.add("您暂无提现记录");
		}
		return msg.toXml();
	}

	@Override
	public String registerOrBind(String fromUserName, String toUserName, MemberToken memberToken) {

		TextMsg msg = new TextMsg();
		if (memberToken ==null){
			msg.add(" 抱歉，您尚未绑定微信服务号！ ");
			msg.addln();
			msg.add(" 已有有融网账户，请<a href='"+ROOT_URL_WEB+"/security/mLogin/?openId="+fromUserName+"'>点击这里</a>绑定。");
			msg.addln();
			msg.add(" 新用户请<a href='"+ROOT_URL_WEB+"/security/mRegister/?openId="+fromUserName+"'>点击这里</a>注册并绑定");
		}else {
			Member member = memberService.selectByPrimaryKey(memberToken.getMemberId());
			String name = StringUtil.isNotBlank(member.getUsername())?member.getUsername():StringUtil.maskMobile(member.getMobile());
			msg.add("亲爱的").add(name).add(", 您已经成功绑定微信服务。\n\n点击左下角键盘回复文本【解绑】可解除账户绑定。");
			msg.addln();
		}
		msg.setFromUserName( toUserName);
		msg.setToUserName(fromUserName);
		return msg.toXml();
	}

	@Override
	public String unbundlingWeiXinStep1(String fromUserName, String toUserName, MemberToken memberToken) {
 		RedisMemberClient.setWeiXinUnbundlingDirective(fromUserName, memberToken.getMemberId());
		TextMsg msg = new TextMsg();
		msg.add("您将解除和微信服务号的绑定，回复：");
		msg.addln();
		msg.add("【1】 确认");
		msg.addln();
		msg.add("【2】 取消");
		msg.setFromUserName(toUserName);
		msg.setToUserName(fromUserName);
		return msg.toXml();
	}

	@Override
	public String unbundlingWeiXinStep2(String fromUserName, String toUserName,
			String directive, MemberToken memberToken) {
		if(directive.equals("1") || directive.equals("确认")){
			Long memberId = RedisMemberClient.getWeiXinUnbundlingDirective(memberToken.getMemberId());
			if(memberId.longValue() == memberToken.getMemberId().longValue()){
				try {
					memberTokenManager.unbundlingWeiXin(memberId);
					RedisMemberClient.removeWeiXinUnbundlingDirective(memberToken.getMemberId());
				} catch (ManagerException e) {
				}
				TextMsg msg = new TextMsg();
				msg.add("解除绑定成功！好伤心，555...");
				msg.setFromUserName(toUserName);
				msg.setToUserName(fromUserName);
				return msg.toXml();
			}
		}else if(directive.equals("2")  || directive.equals("取消")){
			Long memberId = RedisMemberClient.getWeiXinUnbundlingDirective(memberToken.getMemberId());
			if(memberId.longValue() == memberToken.getMemberId().longValue()){
				RedisMemberClient.removeWeiXinUnbundlingDirective(memberToken.getMemberId());
				TextMsg msg = new TextMsg();
				msg.add("谢谢您手下留情，我会继续努力的！");
				msg.setFromUserName(toUserName);
				msg.setToUserName(fromUserName);
				return msg.toXml();
			}
		}
		return "";
	}
	
	public String responseTransferCustomerService(String fromUserName, String toUserName){
		TextMsg msg = new TextMsg();
		msg.setFromUserName(toUserName);
		msg.setToUserName(fromUserName);
		return msg.toTransferCustomerService();
	}
	public String keyWords(String content)  {
		List<WinxinTemplate> list = Lists.newArrayList();
		String name="";
				try {
					list = winxinManager.getWeixininfo();
				} catch (ManagerException e) {
				}
				if(list!=null&&list.size()>0){
					for(WinxinTemplate bean:list){
						
						String [] temp={bean.getKeyword1(),bean.getKeyword2(),bean.getKeyword3(),bean.getKeyword4(),bean.getKeyword5()};
						boolean b = Arrays.asList(temp).contains(content);
						String keyWord=bean.getKeyword1()+bean.getKeyword2()+bean.getKeyword3()+bean.getKeyword4()+bean.getKeyword5();
						if(b){
							name=keyWord;
							break;
						}
					}
			}
			return name;
	}

	@Override
	public String unbundlingWeiXinStep3(String fromUserName, String toUserName,
			String content){
			try {
			Long id=1l;
			List<Article> artList = Lists.newArrayList();
			List<WinxinTemplate> list = winxinManager.getWeixininfo();
			if(list!=null&&list.size()>0){
				for(WinxinTemplate bean:list){
					//String keyWord=bean.getKeyword1()+bean.getKeyword2()+bean.getKeyword3()+bean.getKeyword4()+bean.getKeyword5();
					String [] temp={bean.getKeyword1(),bean.getKeyword2(),bean.getKeyword3(),bean.getKeyword4(),bean.getKeyword5()};
					boolean b = Arrays.asList(temp).contains(content);
					if(b){
						id=bean.getId();
						break;
					}
				}
			}
			WinxinTemplate winxinMenu = winxinManager.queryInfobyId(id);
			if(winxinMenu.getTemplateType().equals("1")){
				TextMsg msgText = new TextMsg();
				msgText.setContent(winxinMenu.getTextDescribe());
				msgText.setFromUserName( toUserName);
				msgText.setToUserName(fromUserName);
		    	return msgText.toXml();
			}else{
				NewsMsg msg=new NewsMsg();
				if (winxinMenu != null && Collections3.isNotEmpty(winxinMenu.getBscAttachments())) {
					//List<BscAttachment> lenderAttachments = Lists.newArrayList();
					for (BscAttachment bscAttachment : winxinMenu.getBscAttachments()) {
						Article art=new Article();
						art.setDescription(winxinMenu.getTextDescribe());
						art.setTitle(winxinMenu.getTitle());
						art.setUrl(winxinMenu.getUrl());
						art.setPicUrl(Config.ossPicUrl+bscAttachment.getFileUrl());
						artList.add(art);
						/*if (DebtEnum.ATTACHMENT_MODULE_DEBT_LENDER.getCode().equals(bscAttachment.getModule())) {
							lenderAttachments.add(bscAttachment);
							continue;
						}*/
					}
				}
					msg.setArticles(artList);
					msg.setFromUserName(toUserName);
					msg.setToUserName(fromUserName);
					return msg.toXml();
			}
			
			} catch (ManagerException e) {
				e.printStackTrace();
			}
			return "";
		
	}
	@Override
	public String firstConcern(String fromUserName, String toUserName) {
		try {
			List<WinxinTemplate> list = winxinManager.queryWeixinAtten();
			if(list!=null&&list.size()>0){
				Long id=list.get(0).getId();
				WinxinTemplate winxin = winxinManager.queryInfobyId(id);
				//文字
				if(winxin.getTemplateType().equals("1")){
					TextMsg msg = new TextMsg();
					msg.setContent(winxin.getTextDescribe());
			    	msg.setFromUserName( toUserName);
			        msg.setToUserName(fromUserName);
			    	return msg.toXml();
			    //图文
				}else if(winxin.getTemplateType().equals("2")){
					List<Article> artList = Lists.newArrayList();
					NewsMsg msg=new NewsMsg();
					if (winxin != null && Collections3.isNotEmpty(winxin.getBscAttachments())) {
						for (BscAttachment bscAttachment : winxin.getBscAttachments()) {
							Article art=new Article();
							art.setDescription(winxin.getTextDescribe());
							art.setUrl(winxin.getUrl());
							art.setTitle(winxin.getTitle());
							art.setPicUrl(Config.ossPicUrl+bscAttachment.getFileUrl());
							artList.add(art);
						}
					}
						msg.setArticles(artList);
						msg.setFromUserName(toUserName);
						msg.setToUserName(fromUserName);
						return msg.toXml();
				//视频	
				}else{
					
				}
			}
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		return "";
	}
	@Override
	public String queryMemberInvest(Long memberId, String fromUserName, String toUserName,Date date,String dateString) {
		//Member member = memberService.selectByPrimaryKey(memberId);
		//String trueName = StringUtil.isNotBlank(member.getUsername())?member.getUsername():StringUtil.maskMobile(member.getMobile());
		TransactionQuery transactionQuery=new TransactionQuery();
		//transactionQuery.setMemberId(memberId);
		transactionQuery.setTransactionStartTime(DateUtils.zerolizedTime(date));
		transactionQuery.setTransactionEndTime(DateUtils.getEndTime(date));
		TextMsg msg = new TextMsg();
		date=DateUtils.getDateFromString(DateUtils.getDateStrFromDate(date), DateUtils.DATE_FMT_3);
		int count=memberService.countRegisterNumberByDate(date);
		try {
			BigDecimal totalAssets=BigDecimal.ZERO;
			BigDecimal returnAssets=BigDecimal.ZERO;
			BigDecimal hadAssets=BigDecimal.ZERO;
		TransactionInterest transactionInterest=transactionManager.selectWaitPayByEndDate(0,date);
		if(transactionInterest!=null){
			 BigDecimal principal = transactionInterest.getPayablePrincipal()==null?BigDecimal.ZERO:transactionInterest.getPayablePrincipal();
			 BigDecimal interest = transactionInterest.getPayableInterest()==null?BigDecimal.ZERO:transactionInterest.getPayableInterest();
			 totalAssets =principal.add(interest);
		}
		
		TransactionInterest returnInterest=transactionManager.selectWaitPayByEndDate(4,date);
		if(returnInterest!=null){
			 BigDecimal principal = returnInterest.getPayablePrincipal()==null?BigDecimal.ZERO:returnInterest.getPayablePrincipal();
			 BigDecimal interest = returnInterest.getPayableInterest()==null?BigDecimal.ZERO:returnInterest.getPayableInterest();
			 returnAssets =principal.add(interest);
		}
		
		TransactionInterest hadInterest=transactionManager.selectRealPayByEndDate(date);
		if(hadInterest!=null){
			 BigDecimal principal = hadInterest.getRealPayPrincipal()==null?BigDecimal.ZERO:hadInterest.getRealPayPrincipal();
			 BigDecimal interest = hadInterest.getRealPayInterest()==null?BigDecimal.ZERO:hadInterest.getRealPayInterest();
			 hadAssets =principal.add(interest);
		}
		
			BigDecimal investAmount = transactionManager.getMemberTotalInvestByStatus(transactionQuery);
			int waitCount=transactionManager.getCountInterestByMemberId(memberId,0,date);
			int hadCount=transactionManager.getCountInterestByMemberId(memberId,1,date);
			int countIng=transactionManager.getCountInterestByMemberId(memberId,4,date);
			
			BigDecimal totalInvestAmount = transactionManager.getTotalInvestAmount(date);
	  	 	// msg.add("亲爱的").add(trueName).add("，平台").add(dateString).add(":");
	  	 	// msg.addln();
	  	 	 msg.add("平台注册量：").add(Integer.toString(count));
	  	 	 msg.addln();
	  	 	 msg.add("平台投资额为：").add(FormulaUtil.getFormatPriceRound(investAmount));
	  	 	 msg.addln();
	  	 	 msg.add("募集中金额：").add(FormulaUtil.getFormatPriceRound(totalInvestAmount));
	  	 	 msg.addln();
	  	 	 msg.add("待回款笔数：").add(Integer.toString(waitCount)).add("总额：").add(FormulaUtil.getFormatPriceRound(totalAssets));
	  	 	 msg.addln();
	  	 	 msg.add("回款中笔数：").add(Integer.toString(countIng)).add("总额：").add(FormulaUtil.getFormatPriceRound(returnAssets));
	  	 	 msg.addln();
	  	 	 msg.add("已回款笔数：").add(Integer.toString(hadCount)).add("总额：").add(FormulaUtil.getFormatPriceRound(hadAssets));
	  	 	 msg.addln();
	  	 	 msg.setToUserName(fromUserName);
	  	 	 msg.setFromUserName(toUserName);
			
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		 return msg.toXml();
		
	}
	
	@Override
	public String queryInvestAmountList(String fromUserName, String toUserName,int pageSize) {
		TextMsg msg = new TextMsg();
		try {
			List<Transaction> transactions=transactionManager.getTotalInvestAmountList(pageSize);
			if(Collections3.isNotEmpty(transactions)){
				for(Transaction bean:transactions){
					BigDecimal totalPrincipal =getPrincipalAmount(bean.getTransactionTime());
					msg.add(DateUtils.getStrFromDate(bean.getTransactionTime(), DateUtils.DATE_FMT_0)).add(" 回款额：").add(FormulaUtil.getFormatPriceRound(totalPrincipal)).add(" 投资额：").add(FormulaUtil.getFormatPriceRound(bean.getInvestAmount()));
					msg.addln();
				}
			}
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		 msg.setToUserName(fromUserName);
 	 	 msg.setFromUserName(toUserName);
 	 	 return msg.toXml();
	}
	
	public BigDecimal getPrincipalAmount(Date date) throws ManagerException{
		TransactionInterestQuery transactionInterestQuery = new TransactionInterestQuery();
		transactionInterestQuery.setTransactionTime(date);
		List<TransactionInterest> transactionInterests = transactionInterestManager
				.selectTransactionInterestsByQueryParams(transactionInterestQuery);
		BigDecimal totalPrincipal = BigDecimal.ZERO;
		BigDecimal totalInterest = BigDecimal.ZERO;
		if (Collections3.isNotEmpty(transactionInterests)) {
			BigDecimal receivableInterest = BigDecimal.ZERO;
			BigDecimal receivablePrincipal = BigDecimal.ZERO;
			BigDecimal receivedInterest = BigDecimal.ZERO;
			BigDecimal receivedPrincipal = BigDecimal.ZERO;
			for (TransactionInterest transactionInterest : transactionInterests) {
				// 未付利息和本金
				if (StatusEnum.TRANSACTION_INTEREST_WAIT_PAY.getStatus() == transactionInterest.getStatus()
						|| StatusEnum.TRANSACTION_INTEREST_PAYING.getStatus() == transactionInterest.getStatus()) {
					receivableInterest = receivableInterest.add(transactionInterest.getPayableInterest());
					receivablePrincipal = receivablePrincipal.add(transactionInterest.getPayablePrincipal());
				}
				// 已付利息和本金
				if (StatusEnum.TRANSACTION_INTEREST_ALL_PAYED.getStatus() == transactionInterest.getStatus()
						|| StatusEnum.TRANSACTION_INTEREST_PART_PAYED.getStatus() == transactionInterest
								.getStatus()) {
					receivedInterest = receivedInterest.add(transactionInterest.getRealPayInterest());
					receivedPrincipal = receivedPrincipal.add(transactionInterest.getRealPayPrincipal());
				}
			}
			totalPrincipal=receivablePrincipal.add(receivedPrincipal);
			totalInterest=receivableInterest.add(receivedInterest);
					
			}
		return totalPrincipal.add(totalInterest);
	}

}
