package com.yourong.common.thirdparty.pay.sina;

import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.collect.Lists;
import com.yourong.common.thirdparty.pay.CardItem;
import com.yourong.common.thirdparty.pay.DepositItem;
import com.yourong.common.thirdparty.pay.DetailItem;
import com.yourong.common.thirdparty.pay.TradeItem;
import com.yourong.common.thirdparty.pay.WithdrawItem;
import com.yourong.common.util.StringUtil;

public class SinaPayResponse {
	
	/**
	 * 响应时间(非空)
	 * 发起响应时间，格式yyyyMMddhhmmss	非空	20140101120401
	 */
	private String response_time;
	/**
	 * 合作者身份ID(非空)
	 * 签约合作方的钱包唯一用户号。	非空	2000001159940003 
	 */
	private String partner_id;
	/**
	 * 参数编码字符集(非空)
	 * 商户网站使用的编码格式，如utf-8、gbk、gb2312等。	非空	UTF-8
	 */
	private String _input_charset;
	/**
	 * 签名(非空)
	 */
	private String sign;
	/**
	 * 签名方式(非空)
	 * 签名方式支持RSA、MD5。建议使用MD5
	 */
	private String sign_type;
	/**
	 * 签名版本号(可空)
	 * 签名密钥版本，默认1.0
	 */
	private String sign_version;
	/**
	 * 响应码(非空)
	 * 参见附录
	 */
	private String response_code;
	/**
	 * 响应信息(可空)
	 * 参见附录
	 */
	private String response_message;
	/**
	 * 备注(可空)
	 * 说明信息，原文返回。客户可根据需要存放需要在响应时带回的信息。
	 */
	private String memo;
	/**
	 * 认证内容
	 */
	private String verify_entity;
	/**
	 * 认证时间
	 */
	private String verify_time;
	/**
	 * 扩展信息
	 */
	private String extend_param;
	/**
	 * 钱包系统卡ID
	 */
	private String card_id;
	/**
	 * 是否已认证
	 */
	private String is_verified;
	/**
	 * 卡列表
	 */
	private String card_list;
	/**
	 * 余额
	 */
	private String balance;
	/**
	 * 可用余额
	 */
	private String available_balance;
	/**
	 * 收支明细列表
	 */
	private String detail_list;
	/**
	 * 页号
	 */
	private String page_no;
	/**
	 * 每页大小
	 */
	private String page_size;
	/**
	 * 总计录数
	 */
	private String total_item;
	/**
	 * 总收入
	 */
	private String total_income;
	/**
	 * 总支出
	 */
	private String total_payout;
	/**
	 * 交易订单号
	 */
	private String out_trade_no;
	/**
	 * 交易状态
	 */
	private String trade_status;
	/**
	 * 支付状态
	 */
	private String pay_status;
	/**
	 * 支付订单号
	 */
	private String out_pay_no;
	/**
	 * 交易明细列表
	 */
	private String trade_list;
	/**
	 * 退款状态
	 */
	private String refund_status;
	/**
	 * 充值状态
	 */
	private String deposit_status;
	/**
	 * 充值明细列表
	 */
	private String deposit_list;
	/**
	 * 提现状态
	 */
	private String withdraw_status;
	/**
	 * 提现明细列表
	 */
	private String withdraw_list;
	/**
	 * 卡列表list
	 */
	private List<CardItem> cardItems;
	/**
	 * 收支明细list
	 */
	private List<DetailItem> detailtems;
	/**
	 * 交易明细list
	 */
	private List<TradeItem> tradeItems;
	
	/**
	 * 交易明细list
	 */
	private List<DepositItem> depositItems;
	
	/**
	 * 交易明细list
	 */
	private List<WithdrawItem> withdrawItems;
	
	
	public String getResponse_time() {
		return response_time;
	}
	public void setResponse_time(String response_time) {
		this.response_time = response_time;
	}
	public String getPartner_id() {
		return partner_id;
	}
	public void setPartner_id(String partner_id) {
		this.partner_id = partner_id;
	}
	public String get_input_charset() {
		return _input_charset;
	}
	public void set_input_charset(String _input_charset) {
		this._input_charset = _input_charset;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getSign_type() {
		return sign_type;
	}
	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}
	public String getSign_version() {
		return sign_version;
	}
	public void setSign_version(String sign_version) {
		this.sign_version = sign_version;
	}
	public String getResponse_code() {
		return response_code;
	}
	public void setResponse_code(String response_code) {
		this.response_code = response_code;
	}
	public String getResponse_message() {
		return response_message;
	}
	public void setResponse_message(String response_message) {
		this.response_message = response_message;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	
	public String getVerify_entity() {
		return verify_entity;
	}
	public void setVerify_entity(String verify_entity) {
		this.verify_entity = verify_entity;
	}
	public String getVerify_time() {
		return verify_time;
	}
	public void setVerify_time(String verify_time) {
		this.verify_time = verify_time;
	}
	public String getExtend_param() {
		return extend_param;
	}
	public void setExtend_param(String extend_param) {
		this.extend_param = extend_param;
	}
	public String getCard_id() {
		return card_id;
	}
	public void setCard_id(String card_id) {
		this.card_id = card_id;
	}
	public String getIs_verified() {
		return is_verified;
	}
	public void setIs_verified(String is_verified) {
		this.is_verified = is_verified;
	}
	public String getCard_list() {
		return card_list;
	}
	public void setCard_list(String card_list) {
		this.card_list = card_list;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getAvailable_balance() {
		return available_balance;
	}
	public void setAvailable_balance(String available_balance) {
		this.available_balance = available_balance;
	}
	public String getDetail_list() {
		return detail_list;
	}
	public void setDetail_list(String detail_list) {
		this.detail_list = detail_list;
	}
	public String getPage_no() {
		return page_no;
	}
	public void setPage_no(String page_no) {
		this.page_no = page_no;
	}
	public String getPage_size() {
		return page_size;
	}
	public void setPage_size(String page_size) {
		this.page_size = page_size;
	}
	public String getTotal_item() {
		return total_item;
	}
	public void setTotal_item(String total_item) {
		this.total_item = total_item;
	}
	public String getTotal_income() {
		return total_income;
	}
	public void setTotal_income(String total_income) {
		this.total_income = total_income;
	}
	public String getTotal_payout() {
		return total_payout;
	}
	public void setTotal_payout(String total_payout) {
		this.total_payout = total_payout;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getTrade_status() {
		return trade_status;
	}
	public void setTrade_status(String trade_status) {
		this.trade_status = trade_status;
	}
	public String getPay_status() {
		return pay_status;
	}
	public void setPay_status(String pay_status) {
		this.pay_status = pay_status;
	}
	public String getOut_pay_no() {
		return out_pay_no;
	}
	public void setOut_pay_no(String out_pay_no) {
		this.out_pay_no = out_pay_no;
	}
	public String getTrade_list() {
		return trade_list;
	}
	public void setTrade_list(String trade_list) {
		this.trade_list = trade_list;
	}
	public String getRefund_status() {
		return refund_status;
	}
	public void setRefund_status(String refund_status) {
		this.refund_status = refund_status;
	}
	public String getDeposit_status() {
		return deposit_status;
	}
	public void setDeposit_status(String deposit_status) {
		this.deposit_status = deposit_status;
	}
	public String getDeposit_list() {
		return deposit_list;
	}
	public void setDeposit_list(String deposit_list) {
		this.deposit_list = deposit_list;
	}
	public String getWithdraw_status() {
		return withdraw_status;
	}
	public void setWithdraw_status(String withdraw_status) {
		this.withdraw_status = withdraw_status;
	}
	public String getWithdraw_list() {
		return withdraw_list;
	}
	public void setWithdraw_list(String withdraw_list) {
		this.withdraw_list = withdraw_list;
	}
	public List<CardItem> getCardItems() {
		//101^ICBC^4003*****001^**三^ DEBIT ^ C
		List<CardItem> cardItemList = Lists.newArrayList();
		if(StringUtil.isNotBlank(card_list)) {
			String[] cardItemArr = card_list.split(StringUtil.ESCAPE + StringUtil.VERTICAL_BAR);
			for (int i = 0; i < cardItemArr.length; i++) {
				String[] cardArr = cardItemArr[i].split(StringUtil.ESCAPE + StringUtil.CARET);
				CardItem cardItem = new CardItem();
				cardItem.setCard_id(cardArr[0]);
				cardItem.setBank_code(cardArr[1]);
				cardItem.setBank_account_no(cardArr[2]);
				cardItem.setAccount_name(cardArr[3]);
				cardItem.setCard_type(cardArr[4]);
				cardItem.setCard_attribute(cardArr[5]);
				cardItem.setNeed_confirm(cardArr[6]);
				cardItem.setCreate_time(cardArr[7]);
				cardItemList.add(cardItem);
			}
		}
		return cardItemList;
	}
	public void setCardItems(List<CardItem> cardItems) {
		this.cardItems = cardItems;
	}
	public List<DetailItem> getDetailtems() {
		//还款1^20131117020101^-^30^100|还款2^20131117020102^-^40^60
		List<DetailItem> detailItemList = Lists.newArrayList();
		if(StringUtil.isNotBlank(detail_list)) {
			String[] detailItemArr = detail_list.split(StringUtil.ESCAPE + StringUtil.VERTICAL_BAR);
			for (int i = 0; i < detailItemArr.length; i++) {
				String[] detailArr = detailItemArr[i].split(StringUtil.ESCAPE + StringUtil.CARET);
				DetailItem detailItem = new DetailItem();
				detailItem.setSummary(detailArr[0]);
				detailItem.setCreate_time(detailArr[1]);
				detailItem.setDetail_type(detailArr[2]);
				detailItem.setAmount(detailArr[3]);
				detailItem.setBalance(detailArr[4]);
				detailItemList.add(detailItem);
			}
		}
		return detailItemList;
	}
	public void setDetailtems(List<DetailItem> detailtems) {
		this.detailtems = detailtems;
	}
	public List<TradeItem> getTradeItems() {
		//20131117020101^还款^30^WAIT_PAY^20131117020101^20131117020101
		List<TradeItem> tradeItemList = Lists.newArrayList();
		if(StringUtil.isNotBlank(trade_list)) {
			String[] tradeItemArr = trade_list.split(StringUtil.ESCAPE + StringUtil.VERTICAL_BAR);
			for (int i = 0; i < tradeItemArr.length; i++) {
				String[] tradeArr = tradeItemArr[i].split(StringUtil.ESCAPE + StringUtil.CARET);
				TradeItem tradeItem = new TradeItem();
				tradeItem.setOut_trade_no(tradeArr[0]);
				tradeItem.setSummary(tradeArr[1]);
				tradeItem.setAmount(tradeArr[2]);
				tradeItem.setTrade_status(tradeArr[3]);
				tradeItem.setCreate_time(tradeArr[4]);
				tradeItem.setLast_update_time(tradeArr[5]);
				tradeItemList.add(tradeItem);
			}
		}
		return tradeItemList;
	}
	public void setTradeItems(List<TradeItem> tradeItems) {
		this.tradeItems = tradeItems;
	}
	public List<DepositItem> getDepositItems() {
		//20131117020101^30^ WAIT_PAY^20131117020101^20131117020101
		List<DepositItem> depositItemList = Lists.newArrayList();
		if(StringUtil.isNotBlank(deposit_list)) {
			String[] depositItemArr = deposit_list.split(StringUtil.ESCAPE + StringUtil.VERTICAL_BAR);
			for (int i = 0; i < depositItemArr.length; i++) {
				String[] depositArr = depositItemArr[i].split(StringUtil.ESCAPE + StringUtil.CARET);
				DepositItem depositItem = new DepositItem();
				depositItem.setOut_trade_no(depositArr[0]);
				depositItem.setAmount(depositArr[1]);
				depositItem.setDeposit_status(depositArr[2]);
				depositItem.setCreate_time(depositArr[3]);
				depositItem.setLast_update_time(depositArr[4]);
				depositItemList.add(depositItem);
			}
		}
		return depositItemList;
	}
	public void setDepositItems(List<DepositItem> depositItems) {
		this.depositItems = depositItems;
	}
	public List<WithdrawItem> getWithdrawItems() {
		List<WithdrawItem> withdrawItemList = Lists.newArrayList();
		if(StringUtil.isNotBlank(withdraw_list)) {
			String[] withdrawItemArr = withdraw_list.split(StringUtil.ESCAPE + StringUtil.VERTICAL_BAR);
			for (int i = 0; i < withdrawItemArr.length; i++) {
				String[] withdrawArr = withdrawItemArr[i].split(StringUtil.ESCAPE + StringUtil.CARET);
				WithdrawItem dwithdrawItem = new WithdrawItem();
				dwithdrawItem.setOut_trade_no(withdrawArr[0]);
				dwithdrawItem.setAmount(withdrawArr[1]);
				dwithdrawItem.setWithdraw_status(withdrawArr[2]);
				dwithdrawItem.setCreate_time(withdrawArr[3]);
				dwithdrawItem.setLast_update_time(withdrawArr[4]);
				withdrawItemList.add(dwithdrawItem);
			}
		}
		return withdrawItemList;
	}
	public void setWithdrawItems(List<WithdrawItem> withdrawItems) {
		this.withdrawItems = withdrawItems;
	}
	@Override
	public int hashCode() {	
	    return HashCodeBuilder.reflectionHashCode(this);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SinaPayResponse other = (SinaPayResponse) obj;
		if (_input_charset == null) {
			if (other._input_charset != null)
				return false;
		} else if (!_input_charset.equals(other._input_charset))
			return false;
		if (available_balance == null) {
			if (other.available_balance != null)
				return false;
		} else if (!available_balance.equals(other.available_balance))
			return false;
		if (balance == null) {
			if (other.balance != null)
				return false;
		} else if (!balance.equals(other.balance))
			return false;
		if (cardItems == null) {
			if (other.cardItems != null)
				return false;
		} else if (!cardItems.equals(other.cardItems))
			return false;
		if (card_id == null) {
			if (other.card_id != null)
				return false;
		} else if (!card_id.equals(other.card_id))
			return false;
		if (card_list == null) {
			if (other.card_list != null)
				return false;
		} else if (!card_list.equals(other.card_list))
			return false;
		if (depositItems == null) {
			if (other.depositItems != null)
				return false;
		} else if (!depositItems.equals(other.depositItems))
			return false;
		if (deposit_list == null) {
			if (other.deposit_list != null)
				return false;
		} else if (!deposit_list.equals(other.deposit_list))
			return false;
		if (deposit_status == null) {
			if (other.deposit_status != null)
				return false;
		} else if (!deposit_status.equals(other.deposit_status))
			return false;
		if (detail_list == null) {
			if (other.detail_list != null)
				return false;
		} else if (!detail_list.equals(other.detail_list))
			return false;
		if (detailtems == null) {
			if (other.detailtems != null)
				return false;
		} else if (!detailtems.equals(other.detailtems))
			return false;
		if (extend_param == null) {
			if (other.extend_param != null)
				return false;
		} else if (!extend_param.equals(other.extend_param))
			return false;
		if (is_verified == null) {
			if (other.is_verified != null)
				return false;
		} else if (!is_verified.equals(other.is_verified))
			return false;
		if (memo == null) {
			if (other.memo != null)
				return false;
		} else if (!memo.equals(other.memo))
			return false;
		if (out_pay_no == null) {
			if (other.out_pay_no != null)
				return false;
		} else if (!out_pay_no.equals(other.out_pay_no))
			return false;
		if (out_trade_no == null) {
			if (other.out_trade_no != null)
				return false;
		} else if (!out_trade_no.equals(other.out_trade_no))
			return false;
		if (page_no == null) {
			if (other.page_no != null)
				return false;
		} else if (!page_no.equals(other.page_no))
			return false;
		if (page_size == null) {
			if (other.page_size != null)
				return false;
		} else if (!page_size.equals(other.page_size))
			return false;
		if (partner_id == null) {
			if (other.partner_id != null)
				return false;
		} else if (!partner_id.equals(other.partner_id))
			return false;
		if (pay_status == null) {
			if (other.pay_status != null)
				return false;
		} else if (!pay_status.equals(other.pay_status))
			return false;
		if (refund_status == null) {
			if (other.refund_status != null)
				return false;
		} else if (!refund_status.equals(other.refund_status))
			return false;
		if (response_code == null) {
			if (other.response_code != null)
				return false;
		} else if (!response_code.equals(other.response_code))
			return false;
		if (response_message == null) {
			if (other.response_message != null)
				return false;
		} else if (!response_message.equals(other.response_message))
			return false;
		if (response_time == null) {
			if (other.response_time != null)
				return false;
		} else if (!response_time.equals(other.response_time))
			return false;
		if (sign == null) {
			if (other.sign != null)
				return false;
		} else if (!sign.equals(other.sign))
			return false;
		if (sign_type == null) {
			if (other.sign_type != null)
				return false;
		} else if (!sign_type.equals(other.sign_type))
			return false;
		if (sign_version == null) {
			if (other.sign_version != null)
				return false;
		} else if (!sign_version.equals(other.sign_version))
			return false;
		if (total_income == null) {
			if (other.total_income != null)
				return false;
		} else if (!total_income.equals(other.total_income))
			return false;
		if (total_item == null) {
			if (other.total_item != null)
				return false;
		} else if (!total_item.equals(other.total_item))
			return false;
		if (total_payout == null) {
			if (other.total_payout != null)
				return false;
		} else if (!total_payout.equals(other.total_payout))
			return false;
		if (tradeItems == null) {
			if (other.tradeItems != null)
				return false;
		} else if (!tradeItems.equals(other.tradeItems))
			return false;
		if (trade_list == null) {
			if (other.trade_list != null)
				return false;
		} else if (!trade_list.equals(other.trade_list))
			return false;
		if (trade_status == null) {
			if (other.trade_status != null)
				return false;
		} else if (!trade_status.equals(other.trade_status))
			return false;
		if (verify_entity == null) {
			if (other.verify_entity != null)
				return false;
		} else if (!verify_entity.equals(other.verify_entity))
			return false;
		if (verify_time == null) {
			if (other.verify_time != null)
				return false;
		} else if (!verify_time.equals(other.verify_time))
			return false;
		if (withdrawItems == null) {
			if (other.withdrawItems != null)
				return false;
		} else if (!withdrawItems.equals(other.withdrawItems))
			return false;
		if (withdraw_list == null) {
			if (other.withdraw_list != null)
				return false;
		} else if (!withdraw_list.equals(other.withdraw_list))
			return false;
		if (withdraw_status == null) {
			if (other.withdraw_status != null)
				return false;
		} else if (!withdraw_status.equals(other.withdraw_status))
			return false;
		return true;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SinaPayResponse [response_time=");
		builder.append(response_time);
		builder.append(", partner_id=");
		builder.append(partner_id);
		builder.append(", _input_charset=");
		builder.append(_input_charset);
		builder.append(", sign=");
		builder.append(sign);
		builder.append(", sign_type=");
		builder.append(sign_type);
		builder.append(", sign_version=");
		builder.append(sign_version);
		builder.append(", response_code=");
		builder.append(response_code);
		builder.append(", response_message=");
		builder.append(response_message);
		builder.append(", memo=");
		builder.append(memo);
		builder.append(", verify_entity=");
		builder.append(verify_entity);
		builder.append(", verify_time=");
		builder.append(verify_time);
		builder.append(", extend_param=");
		builder.append(extend_param);
		builder.append(", card_id=");
		builder.append(card_id);
		builder.append(", is_verified=");
		builder.append(is_verified);
		builder.append(", card_list=");
		builder.append(card_list);
		builder.append(", balance=");
		builder.append(balance);
		builder.append(", available_balance=");
		builder.append(available_balance);
		builder.append(", detail_list=");
		builder.append(detail_list);
		builder.append(", page_no=");
		builder.append(page_no);
		builder.append(", page_size=");
		builder.append(page_size);
		builder.append(", total_item=");
		builder.append(total_item);
		builder.append(", total_income=");
		builder.append(total_income);
		builder.append(", total_payout=");
		builder.append(total_payout);
		builder.append(", out_trade_no=");
		builder.append(out_trade_no);
		builder.append(", trade_status=");
		builder.append(trade_status);
		builder.append(", pay_status=");
		builder.append(pay_status);
		builder.append(", out_pay_no=");
		builder.append(out_pay_no);
		builder.append(", trade_list=");
		builder.append(trade_list);
		builder.append(", refund_status=");
		builder.append(refund_status);
		builder.append(", deposit_status=");
		builder.append(deposit_status);
		builder.append(", deposit_list=");
		builder.append(deposit_list);
		builder.append(", withdraw_status=");
		builder.append(withdraw_status);
		builder.append(", withdraw_list=");
		builder.append(withdraw_list);
		builder.append(", cardItems=");
		builder.append(cardItems);
		builder.append(", detailtems=");
		builder.append(detailtems);
		builder.append(", tradeItems=");
		builder.append(tradeItems);
		builder.append("]");
		return builder.toString();
	}
	
}
