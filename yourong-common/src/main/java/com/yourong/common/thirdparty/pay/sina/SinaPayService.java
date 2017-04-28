package com.yourong.common.thirdparty.pay.sina;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yourong.common.enums.SinaPayEnum;
import com.yourong.common.thirdparty.pay.BalancePayResponse;
import com.yourong.common.thirdparty.pay.BlanCadeResponse;
import com.yourong.common.thirdparty.pay.CarListResponse;
import com.yourong.common.thirdparty.pay.PayMentService;
import com.yourong.common.thirdparty.pay.PayResponse;
import com.yourong.common.thirdparty.pay.VerifyEntityResponse;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.Encodes;
import com.yourong.common.util.HttpUtil;
import com.yourong.common.util.SignUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.common.web.BaseService;

@Service
@Deprecated
public class SinaPayService extends BaseService implements PayMentService {

	private Map<String, String> buildBaseMap() {
		Map<String, String> params = Maps.newHashMap();
		params.put(SinaPayEnum.PARAM_BASE_VERSION.getCode(),
				SinaPayConfig.version);
		params.put(SinaPayEnum.PARAM_BASE_PARTNER_ID.getCode(),
				SinaPayConfig.partnerId);
		params.put(SinaPayEnum.PARAM_BASE_INPUT_CHARSET.getCode(),
				SinaPayConfig.inputCharset);
		params.put(SinaPayEnum.PARAM_BASE_SIGN_TYPE.getCode(),
				SinaPayConfig.signType);
		params.put(SinaPayEnum.PARAM_BASE_SIGN_VERSION.getCode(),
				SinaPayConfig.signVersion);
		params.put(SinaPayEnum.PARAM_BASE_REQUEST_TIME.getCode(), DateUtils
				.formatDatetoString(DateUtils.getCurrentDateTime(),
						DateUtils.TIME_PATTERN_SESSION));
		return params;

	}
	
	public PayResponse createActivateMember(String name, String memberType, String clientIp, Map<String, String> map) {
		Map<String, String> coreParams = Maps.newHashMap();
		// 基本参数
		coreParams.put(SinaPayEnum.PARAM_BASE_SERVICE.getCode(),
				SinaPayEnum.SERVICE_CREATE_ACTIVATE_MEMBER.getCode());
		// 用户标识信息
		coreParams.put(SinaPayEnum.PARAM_CORE_IDENTITY_ID.getCode(), name);
		// 会员类型
		coreParams.put(SinaPayEnum.PARAM_CORE_IDENTITY_TYPE.getCode(),
				SinaPayEnum.IDENTITY_UID.getCode());
		// 用户标示类型
		coreParams.put(SinaPayEnum.PARAM_CORE_MEMBER_TYPE.getCode(), memberType);
		// 请求者IP
		coreParams.put("client_ip", clientIp);
		buildExtMap(map, coreParams);
		Object inverkApi = (PayResponse) inverkApi(coreParams,
				PayResponse.class);
		return (PayResponse) inverkApi;
	}

	/**
	 * 构着扩展信息
	 * 
	 * @param map
	 * @param coreParams
	 */
	private void buildExtMap(Map<String, String> map,
			Map<String, String> coreParams) {
		// 扩展信息
		StringBuffer ext = new StringBuffer();
		if (map != null) {
			Iterator<Entry<String, String>> it = map.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> next = it.next();
				ext.append(next.getKey()).append(StringUtil.CARET)
						.append(next.getKey()).append(StringUtil.VERTICAL_BAR);
			}
			ext.substring(ext.length() - 1, ext.length());
			coreParams.put(SinaPayEnum.PARAM_CORE_EXTEND_PARAM.getCode(),
					ext.toString());
		}
	}

	private Object inverkApi(Map<String, String> coreParam, Class clazz) {
		Map<String, String> param = buildBaseMap();
		if (param != null && coreParam != null) {
			coreParam.putAll(param);
			coreParam.put(SinaPayEnum.PARAM_BASE_SIGN.getCode(), SignUtil
					.generateSign(coreParam, SinaPayConfig.md5Key)
					.toLowerCase());
		}
		String result = HttpUtil.doPost(SinaPayConfig.getGate(coreParam
				.get(SinaPayEnum.PARAM_BASE_SERVICE.getCode())), coreParam,
				SinaPayConfig.inputCharset);
		Object parseObject = JSON.parseObject(Encodes.urlDecode(result), clazz);
		return parseObject;
	}

	public Map hostingDepositBaseParam(String outTradeNo, String memberid,
			BigDecimal amount, String payMethod, String blankCode,
			String returnUrl, String notifyUrl) {

		Map<String, String> coreParams = Maps.newHashMap();
		// 基本参数
		coreParams.put(SinaPayEnum.PARAM_BASE_SERVICE.getCode(),
				SinaPayEnum.SERVICE_CREATE_HOSTING_DEPOSIT.getCode());

		// 交易订单号
		coreParams.put(SinaPayEnum.PARAM_CORE_OUT_TRADE_NO.getCode(),
				outTradeNo);

		// 用户标识信息
		coreParams.put(SinaPayEnum.PARAM_CORE_IDENTITY_ID.getCode(), memberid);
		// 会员类型
		coreParams.put(SinaPayEnum.PARAM_CORE_IDENTITY_TYPE.getCode(),
				SinaPayEnum.IDENTITY_UID.getCode());
		// 账号类型
		coreParams.put(SinaPayEnum.PARAM_CORE_ACCOUNT_TYPE.getCode(),
				SinaPayEnum.ACCOUNT_BASIC.getCode());
		// 金额
		coreParams.put(SinaPayEnum.PARAM_CORE_AMOUNT.getCode(),
				amount.toString());
		// 支付方式
		StringBuffer payext = new StringBuffer();
		payext.append(payMethod);
		payext.append(StringUtil.CARET);
		payext.append(amount.toString());
		payext.append(StringUtil.CARET);
		payext.append(blankCode);
		// 只支持借记卡
		payext.append(",DEBIT,C");

		coreParams.put(SinaPayEnum.PARAM_CORE_PAY_METHOD.getCode(),
				payext.toString());
		// 返回页面
		coreParams.put(SinaPayEnum.PARAM_BASE_RETURN_URL.getCode(), returnUrl);
		// 通知页面
		coreParams.put(SinaPayEnum.PARAM_BASE_NOTIFY_URL.getCode(), notifyUrl);

		Map<String, String> param = buildBaseMap();
		if (param != null && coreParams != null) {
			coreParams.putAll(param);
			coreParams.put(SinaPayEnum.PARAM_BASE_SIGN.getCode(), SignUtil
					.generateSign(coreParams, SinaPayConfig.md5Key)
					.toLowerCase());
		}
		return coreParams;
	}

	public CarListResponse queryBankCard(String name) {
		Map<String, String> coreParams = Maps.newHashMap();
		coreParams.put(SinaPayEnum.PARAM_BASE_SERVICE.getCode(),
				SinaPayEnum.SERVICE_QUERY_BANK_CARD.getCode());
		coreParams.put(SinaPayEnum.PARAM_CORE_IDENTITY_ID.getCode(), name);
		coreParams.put(SinaPayEnum.PARAM_CORE_IDENTITY_TYPE.getCode(),
				SinaPayEnum.IDENTITY_UID.getCode());
		CarListResponse inverkApi = (CarListResponse) inverkApi(coreParams,
				CarListResponse.class);
		return inverkApi;
	}

	public PayResponse setRealName(String memberid, String realName,
			String certificate, String certificateNumber) {
		Map<String, String> coreParams = Maps.newHashMap();
		// 基本参数
		coreParams.put(SinaPayEnum.PARAM_BASE_SERVICE.getCode(),
				SinaPayEnum.SERVICE_SET_REAL_NAME.getCode());
		// 用户标识信息
		coreParams.put(SinaPayEnum.PARAM_CORE_IDENTITY_ID.getCode(), memberid);
		// 会员类型
		coreParams.put(SinaPayEnum.PARAM_CORE_IDENTITY_TYPE.getCode(),
				SinaPayEnum.IDENTITY_UID.getCode());
		// 会员真实姓名
		coreParams.put(SinaPayEnum.PARAM_CORE_REAL_NAME.getCode(), realName);
		// 证件类型
		coreParams.put(SinaPayEnum.PARAM_CORE_CERT_TYPE.getCode(), certificate);
		// 证件号码
		coreParams.put(SinaPayEnum.PARAM_CORE_CERT_NO.getCode(),
				certificateNumber);

		coreParams.put(SinaPayEnum.PARAM_CORE_NEED_CONFIRM.getCode(), "Y");

		Object inverkApi = (PayResponse) inverkApi(coreParams,
				PayResponse.class);
		return (PayResponse) inverkApi;
	}

	@Override
	public PayResponse bindingVerify(String memberid, String verifyType,
			String verifyEntity, Map<String, String> map) {

		Map<String, String> coreParams = Maps.newHashMap();
		// 基本参数
		coreParams.put(SinaPayEnum.PARAM_BASE_SERVICE.getCode(),
				SinaPayEnum.SERVICE_BINDING_VERIFY.getCode());
		// 用户标识信息
		coreParams.put(SinaPayEnum.PARAM_CORE_IDENTITY_ID.getCode(), memberid);
		// 会员类型
		coreParams.put(SinaPayEnum.PARAM_CORE_IDENTITY_TYPE.getCode(),
				SinaPayEnum.IDENTITY_UID.getCode());
		// 认证类型
		coreParams
				.put(SinaPayEnum.PARAM_CORE_VERIFY_TYPE.getCode(), verifyType);
		// TODO 认证内容 RSA 签名
		coreParams.put(SinaPayEnum.PARAM_CORE_VERIFY_TYPE.getCode(),verifyEntity);
		// 扩展内容
		buildExtMap(map, coreParams);

		Object inverkApi = (PayResponse) inverkApi(coreParams,
				PayResponse.class);
		return (PayResponse) inverkApi;

	}

	/**
	 * 解除绑定
	 */
	@Override
	public PayResponse unbindingVerify(String memberid, String verifyType) {
		Map<String, String> coreParams = Maps.newHashMap();
		// 基本参数
		coreParams.put(SinaPayEnum.PARAM_BASE_SERVICE.getCode(),
				SinaPayEnum.SERVICE_UNBINDING_VERIFY.getCode());
		// 用户标识信息
		coreParams.put(SinaPayEnum.PARAM_CORE_IDENTITY_ID.getCode(), memberid);
		// 会员类型
		coreParams.put(SinaPayEnum.PARAM_CORE_IDENTITY_TYPE.getCode(),
				SinaPayEnum.IDENTITY_UID.getCode());
		// 认证类型
		coreParams
				.put(SinaPayEnum.PARAM_CORE_VERIFY_TYPE.getCode(), verifyType);

		Object inverkApi = (PayResponse) inverkApi(coreParams,
				PayResponse.class);
		return (PayResponse) inverkApi;
	}

	/**
	 * 查询绑定信息
	 */
	@Override
	public VerifyEntityResponse queryVerify(String memberid, String verifyType,
			String isMask) {
		Map<String, String> coreParams = Maps.newHashMap();
		// 基本参数
		coreParams.put(SinaPayEnum.PARAM_BASE_SERVICE.getCode(),
				SinaPayEnum.SERVICE_QUERY_VERIFY.getCode());
		// 用户标识信息
		coreParams.put(SinaPayEnum.PARAM_CORE_IDENTITY_ID.getCode(), memberid);
		// 会员类型
		coreParams.put(SinaPayEnum.PARAM_CORE_IDENTITY_TYPE.getCode(),
				SinaPayEnum.IDENTITY_UID.getCode());
		// 认证类型
		coreParams
				.put(SinaPayEnum.PARAM_CORE_VERIFY_TYPE.getCode(), verifyType);
		// 是否掩码
		coreParams.put(SinaPayEnum.PARAM_CORE_IS_MASK.getCode(), isMask);

		VerifyEntityResponse inverkApi = (VerifyEntityResponse) inverkApi(
				coreParams, VerifyEntityResponse.class);
		return inverkApi;
	}

	@Override
	public PayResponse createHostingWithdraw(String out_trade_no,
			String identity_id, String account_type, String amount,
			String card_id, String notifyUrl) {
		Map<String, String> coreParams = Maps.newHashMap();
		// 基本参数
		coreParams.put(SinaPayEnum.PARAM_BASE_SERVICE.getCode(),
				SinaPayEnum.SERVICE_CREATE_HOSTING_WITHDRAW.getCode());
		// 交易订单号
		coreParams.put(SinaPayEnum.PARAM_CORE_OUT_TRADE_NO.getCode(),
				out_trade_no);
		// 用户标识信息
		coreParams.put(SinaPayEnum.PARAM_CORE_IDENTITY_ID.getCode(),
				identity_id);
		// 会员类型
		coreParams.put(SinaPayEnum.PARAM_CORE_IDENTITY_TYPE.getCode(),
				SinaPayEnum.IDENTITY_UID.getCode());
		// 账号类型
		coreParams.put(SinaPayEnum.PARAM_CORE_ACCOUNT_TYPE.getCode(),
				SinaPayEnum.ACCOUNT_BASIC.getCode());

		coreParams.put(SinaPayEnum.PARAM_CORE_AMOUNT.getCode(), amount);

		coreParams.put(SinaPayEnum.PARAM_CORE_CARD_ID.getCode(), card_id);
		// 通知页面
		coreParams.put(SinaPayEnum.PARAM_BASE_NOTIFY_URL.getCode(), notifyUrl);

		Object inverkApi = (PayResponse) inverkApi(coreParams,
				PayResponse.class);

		return (PayResponse) inverkApi;
	}

	@Override
	public BalancePayResponse queryBalance(String memberid) {

		Map<String, String> coreParams = Maps.newHashMap();
		// 基本参数
		coreParams.put(SinaPayEnum.PARAM_BASE_SERVICE.getCode(),
				SinaPayEnum.SERVICE_CREATE_HOSTING_WITHDRAW.getCode());
		// 用户标识信息
		coreParams.put(SinaPayEnum.PARAM_CORE_IDENTITY_ID.getCode(), memberid);
		// 会员类型
		coreParams.put(SinaPayEnum.PARAM_CORE_IDENTITY_TYPE.getCode(),
				SinaPayEnum.IDENTITY_UID.getCode());

		BalancePayResponse inverkApi = (BalancePayResponse) inverkApi(
				coreParams, PayResponse.class);

		return inverkApi;

	}

	@Override
	public PayResponse balanceFreeze(String outFreezeNo, String memberid) {

		return null;
	}

	@Override
	public PayResponse createHostingCollectTrade(String outTradeNo,
			String outTradeCode, String summary, String tradeCloseTime,
			String payerId, String payerIdentityType, String payMethod) {
		Map<String, String> coreParams = Maps.newHashMap();
		coreParams.put(SinaPayEnum.PARAM_BASE_SERVICE.getCode(),
				SinaPayEnum.SERVICE_CREATE_HOSTING_COLLECT_TRADE.getCode());
		coreParams.put(SinaPayEnum.PARAM_CORE_OUT_TRADE_NO.getCode(),
				outTradeNo);
		coreParams.put(SinaPayEnum.PARAM_CORE_OUT_TRADE_CODE.getCode(),
				outTradeCode);
		coreParams.put(SinaPayEnum.PARAM_CORE_SUMMARY.getCode(),
				summary);
		coreParams.put(SinaPayEnum.PARAM_CORE_TRADE_CLOSE_TIME.getCode(),
				tradeCloseTime);

		coreParams.put(SinaPayEnum.PARAM_CORE_PAYER_ID.getCode(), payerId);

		coreParams.put(SinaPayEnum.PARAM_CORE_PAYER_IDENTITY_TYPE.getCode(), payerIdentityType);
		coreParams.put(SinaPayEnum.PARAM_CORE_PAY_METHOD.getCode(), payMethod);

		Object inverkApi = (PayResponse) inverkApi(coreParams,
				PayResponse.class);

		return (PayResponse) inverkApi;
	}

	@Override
	public BlanCadeResponse bindingBankCard(String memberid, String bank_code,
			String bank_account_no, String account_name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PayResponse unBindingBankCard(String memberid, String card_id) {
		// TODO Auto-generated method stub
		return null;
	}

}
