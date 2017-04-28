package com.yourong.core.mc.manager.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.CryptCode;
import com.yourong.common.util.HttpUtil;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.fin.manager.RechargeLogManager;
import com.yourong.core.fin.model.RechargeLog;
import com.yourong.core.mc.manager.DouwanManager;
import com.yourong.core.tc.manager.TransactionManager;
import com.yourong.core.tc.model.Transaction;
import com.yourong.core.uc.manager.MemberManager;
import com.yourong.core.uc.model.Member;

/**
 * 都玩主要回调接口
 * Created by Administrator on 2015/2/12.
 */
@Component
public class DouwanManagerImpl implements DouwanManager{


    //都玩安全码 MD5(“yourongwang”)
    private static final String SECRETCODE = "edada51f2ac06ee6cb73002d5bc6cb0e";

    private static  final  String YRT_KEY = PropertiesUtil.getProperties("advertisers.YRT.key");
    private static  final  String YRT_INVERSTKEY = PropertiesUtil.getProperties("advertisers.YRT.inverstkey");

    private static final String CPS = "CPS";

    private static final  String YRT = "YRT";

    private static final  String YRT_URL = PropertiesUtil.getProperties("advertisers.YRT.url");
    private static final  String YRT_INVERT_URL = PropertiesUtil.getProperties("advertisers.YRT.inverst.url");

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MemberManager memberManager;


    @Autowired
    private TransactionManager transactionManager;

    @Autowired
    private RechargeLogManager rechargeLogManager;

    @Autowired
    private TaskExecutor threadPool;

    //回调接口
    public void douwanRegisteredCallBack(final Long id) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String tid = "";
                    Member member = memberManager.selectByPrimaryKey(id);
                    String registerTraceNo = member.getRegisterTraceNo();
                    String registerTraceSource = member.getRegisterTraceSource();
                    callBackAdvertisersDOU(tid, registerTraceNo, registerTraceSource, id);
                    callBackAdvertisersYRT(tid, registerTraceNo, registerTraceSource, id);
                   // callBackAdvertisersYYW(tid, registerTraceNo, registerTraceSource, id);
                   // callBackAdvertisersKLZ(tid, registerTraceNo, registerTraceSource, id);
                    callBackAdvertisersBBW(tid, registerTraceNo, registerTraceSource, id);
                } catch (Exception e) {
                    logger.error("广告商接口异常", e);
                }
            }
        });
    }

    /**
     * 调用都玩接口
     * @param tid
     * @param registerTraceNo
     * @param registerTraceSource
     * @param id
     */
    private void callBackAdvertisersDOU(String tid, String registerTraceNo, String registerTraceSource, Long id) {
        if (StringUtil.equalsIgnoreCases(CPS, registerTraceSource, true) && StringUtil.isNotBlank(registerTraceNo)) {
            String[] strings = registerTraceNo.split("_");
            if (strings != null && strings.length > 1)
                tid = strings[strings.length - 1];
            if (StringUtil.isNotBlank(tid)) {
                String url = "http://mall.366dw.com/interface/reflection/tid/%s/step1/%s";
                String urldata = String.format(url, tid, id.toString());
                String s = HttpUtil.doGet(urldata, null);
                logger.info("都玩回调接口，url={},result={}",urldata, s);
            }
        }
    }

    /**
     * 调用易瑞特接口
     * @param tid
     * @param registerTraceNo
     * @param registerTraceSource
     * @param id
     */
    private void callBackAdvertisersYRT(String tid, String registerTraceNo, String registerTraceSource, Long id) {
        if (StringUtil.equalsIgnoreCases(YRT, registerTraceSource, true) && StringUtil.isNotBlank(registerTraceNo)) {
            String[] strings = registerTraceNo.split("_");
            if (strings != null && strings.length > 1)
                tid = strings[strings.length - 1];
            if (StringUtil.isNotBlank(tid)) {
                String sign = generateSign(tid,String.valueOf(id), YRT_KEY);
                String url = YRT_URL+"?tid=%s&uid=%s&sign=%s";
                String urldata = String.format(url, tid, id.toString(), sign);
                String s = HttpUtil.doGet(urldata, null);
                logger.info("易瑞特回调接口，url={},result={}",urldata, s);
            }
        }
    }
    /**
     * 调用易瑞特接口
     * @param tid
     * @param registerTraceNo
     * @param registerTraceSource
     * @param id
     */
    private void firstInvestCallYRT(BigDecimal invest, String registerTraceNo, String registerTraceSource, String tid, Long id) {
        if (StringUtil.equalsIgnoreCases(YRT, registerTraceSource, true) && StringUtil.isNotBlank(registerTraceNo)) {
            String[] strings = registerTraceNo.split("_");
            if (strings != null && strings.length > 1)
                tid = strings[strings.length - 1];
            if (StringUtil.isNotBlank(tid) && invest != null) {
                String sign = generateSign(tid,String.valueOf(id), YRT_INVERSTKEY);
                String urldata = String.format(YRT_INVERT_URL, tid, id.toString(), invest.toPlainString(),sign);
                String s = HttpUtil.doGet(urldata, null);
                logger.info("首次易瑞特交易回调接口，url={},result={}",urldata, s);
            }
        }
    }




    /**
     * 调用优易网接口
     * @param tid
     * @param registerTraceNo
     * @param registerTraceSource
     * @param id
     */
    private  static  final  String YYW_tokenID = "34";
    private  static  final  String YYW_tokenKey = "25833826de4c04b45c9bd947730ab6fe";
    private static   final  String YYW_url="http://www.yuu1.com/index.php?c=adverts_api&m=reg_adverts&recordID=%s&tokenID=34&guid=%s&guname=%s&accesskey=%s";
    private void callBackAdvertisersYYW(String tid, String registerTraceNo, String registerTraceSource, Long id) {
        if (StringUtil.equalsIgnoreCases("YYW", registerTraceSource, true) && StringUtil.isNotBlank(registerTraceNo)) {
            String[] strings = registerTraceNo.split("_");
            if (strings != null && strings.length > 1)
                tid = strings[strings.length - 1];
            if (StringUtil.isNotBlank(tid)) {
                String sign = generateSignYYW(tid, String.valueOf(id), YYW_tokenKey);
                String urldata = String.format(YYW_url, tid, id.toString(), id.toString(), sign);
                String s = HttpUtil.doGet(urldata, null);
                logger.info("优易网接口，url={},result={}",urldata, s);
            }
        }
    }
    private String generateSignYYW(String tid,String uid,String key){
        String sign = CryptCode.encryptToMD5(YYW_tokenID+tid+uid+key);
        String toLowerCase = sign.toLowerCase();
        return toLowerCase;
    }

    /**
     *  快乐赚
     * @param tid
     * @param registerTraceNo
     * @param registerTraceSource
     * @param id
     */
    private static final String KLZ_ulr = "http://www.lezhuan.com/reannal.php?adID=%s&leID=%s&idCode=%s&idName=%s&sign=%s";
    private  static final  String klz_key =  PropertiesUtil.getProperties("tuiguang_klz_key");
    private static final String klz_adID= PropertiesUtil.getProperties("tuiguang_klz_adID");
    private void callBackAdvertisersKLZ(String tid, String registerTraceNo, String registerTraceSource, Long id) {
        if (StringUtil.equalsIgnoreCases("KLZ", registerTraceSource, true) && StringUtil.isNotBlank(registerTraceNo)) {
            String[] strings = registerTraceNo.split("_");
            if (strings != null && strings.length > 1)
                tid = strings[strings.length - 1];
            if (StringUtil.isNotBlank(tid)) {
                String sign = generateSignKLZ(tid, String.valueOf(id), klz_key);
                String urldata = String.format(KLZ_ulr, klz_adID,tid, id.toString(), id.toString(), sign);
                String s = HttpUtil.doGet(urldata, null);
                logger.info(" 快乐赚，url={},result={}",urldata, s);
            }
        }
    }
    private String generateSignKLZ(String tid,String uid,String key){
        String sign = CryptCode.encryptToMD5(key+klz_adID+tid+uid+uid+key);
        String toLowerCase = sign.toLowerCase();
        return toLowerCase;
    }


    /**
     *  蹦蹦网推广链接
     * @param tid
     * @param registerTraceNo
     * @param registerTraceSource
     * @param id
     */
    private static final String BBW_ulr = "http://www.bengbeng.com/reannal.php?adID=%s&annalID=%s&idCode=%s&doukey=%s&idName=%s";
    private  static final  String BBW_key =  PropertiesUtil.getProperties("BBW_key");
    private static final String BBW_adID= PropertiesUtil.getProperties("BBW_adID");
    private void callBackAdvertisersBBW(String tid, String registerTraceNo, String registerTraceSource, Long id) {
        if (StringUtil.equalsIgnoreCases("BBW", registerTraceSource, true) && StringUtil.isNotBlank(registerTraceNo)) {
            String[] strings = registerTraceNo.split("_");
            if (strings != null && strings.length > 1)
                tid = strings[strings.length - 1];
            if (StringUtil.isNotBlank(tid)) {
                String sign = generateSignBBW(tid, String.valueOf(id));
                String urldata = String.format(BBW_ulr, BBW_adID,tid, id.toString(),sign, id.toString() );
                String s = HttpUtil.doGet(urldata, null);
                logger.info("蹦蹦网，url={},result={}",urldata, s);
            }
        }
    }
    private String generateSignBBW(String tid,String uid){
        String sign = CryptCode.encryptToMD5(BBW_adID+tid+uid+BBW_key);
        String toLowerCase = sign.toLowerCase();
        return toLowerCase;
    }



    private String getMemberRegisterTraceSource( final  Long id) {
        String tid = null;
        try {
            Member member = memberManager.selectByPrimaryKey(id);
            String registerTraceNo = member.getRegisterTraceNo();
            String registerTraceSource = member.getRegisterTraceSource();
            if (StringUtil.equalsIgnoreCases(CPS, registerTraceSource, true) && StringUtil.isNotBlank(registerTraceNo)) {
                String[] strings = registerTraceNo.split("_");
                if (strings != null && strings.length > 1)
                    tid = strings[strings.length - 1];
            }
        } catch (Exception e) {
            logger.error("广告商回调异常,获取会员异常", e);
        }
        return tid;
    }



    public void douwanEmailBingCallBack(final  Long id) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String tid = null;
                        Member member = memberManager.selectByPrimaryKey(id);
                        String registerTraceNo = member.getRegisterTraceNo();
                        String registerTraceSource = member.getRegisterTraceSource();
                        emailCallDouwan(tid, registerTraceNo, registerTraceSource, id);
                } catch (Exception e) {
                    logger.error("广告商接口异常", e);
                }
            }
        });
    }
    //都玩email回调接口
    private void emailCallDouwan(String tid, String registerTraceNo, String registerTraceSource, Long id) {
        if (StringUtil.equalsIgnoreCases(CPS, registerTraceSource, true) && StringUtil.isNotBlank(registerTraceNo)) {
            String[] strings = registerTraceNo.split("_");
            if (strings != null && strings.length > 1)
                tid = strings[strings.length - 1];
            if (StringUtil.isNotBlank(tid)) {
                String url = "http://mall.366dw.com/interface/reflection/tid/%s/step2/%s";
                String urldata = String.format(url, tid, id.toString());
                String s = HttpUtil.doGet(urldata, null);
                logger.info("都玩绑定邮箱回调接口，url={},result={}", urldata,s);
            }
        }
    }

    private String generateSign(String tid,String uid,String key){
        String sign = CryptCode.encryptToMD5(tid+uid+key);
        String toLowerCase = sign.toLowerCase();
        return toLowerCase;
    }

    public void douwanFirstTransaction(final Long id,final  int totalDays) {
		logger.info("外部渠道首投接口回调开始 id={}", id);
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					// 首次交易的用户
					BigDecimal invest = null;
					Transaction transaction = isFirsTransaction(id);
					if (transaction != null) {
						invest = transaction.getUsedCapital();
						//totalDays = transaction.getTotalDays();
					}
					Member member = memberManager.selectByPrimaryKey(id);
					String registerTraceNo = member.getRegisterTraceNo();
					String registerTraceSource = member
							.getRegisterTraceSource();
					String tid = "";
					logger.info("都玩首次投资回调接口，memberId={},registerTraceNo={},registerTraceSource={},totalDays={}", id,registerTraceNo,registerTraceSource,totalDays);
					firstInvestCallDouwan(invest, registerTraceNo,
							registerTraceSource, tid, id, totalDays);
					firstInvestCallYRT(invest, registerTraceNo,
							registerTraceSource, tid, id);

				} catch (Exception e) {
					logger.error("都玩接口异常", e);
				}
			}
		});
	}

	private void firstInvestCallDouwan(BigDecimal invest, String registerTraceNo, String registerTraceSource, String tid, Long id,
			Integer totalDays) {
		if (StringUtil.equalsIgnoreCases(CPS, registerTraceSource, true) && StringUtil.isNotBlank(registerTraceNo)) {
			String[] strings = registerTraceNo.split("_");
			if (strings != null && strings.length > 1)
				tid = strings[strings.length - 1];
			if (StringUtil.isNotBlank(tid) && invest != null && totalDays != null) {
				String url = "http://mall.366dw.com/script/yourong_pay/uid/%s/amount/%s/days/%s";
				String urldata = String.format(url, id.toString(), invest.toPlainString(), totalDays);
				String s = HttpUtil.doGet(urldata, null);
				logger.info("都玩首次交易回调接口，url={},result={}", urldata, s);
			}
		}
	}



    /**
     * 获取第一次充值记录的金额
     *
     * @param id
     * @return
     */
    private BigDecimal isFirsRechare(Long id) {
        BigDecimal invest = null;
        Map map = Maps.newHashMap();
        map.put("startRow", 0);
        map.put("pageSize", 2);
        map.put("status", 5);
        map.put("memberId", id);
        try {
            List<RechargeLog> rechargeLogs = rechargeLogManager.selectRechargeByMap(map);
            if (Collections3.isNotEmpty(rechargeLogs) && rechargeLogs.size() == 1) {
                return rechargeLogs.get(0).getAmount();
            }
        } catch (ManagerException e) {
            logger.error("查询充值记录异常", e);
        }
        return invest;
    }

	private Transaction isFirsTransaction(Long memberid) {
		Transaction transaction = null;
		Map map = Maps.newHashMap();
		map.put("startRow", 0);
		map.put("pageSize", 2);
		map.put("memberId", memberid);

		try {
			List<Transaction> transactionByMap = transactionManager.getTransactionByMap(map);
			if (Collections3.isNotEmpty(transactionByMap) && transactionByMap.size() == 1) {
				return transactionByMap.get(0);
			}
		} catch (ManagerException e) {
			logger.error("查询充值记录异常", e);
		}
		return transaction;
	}

}
