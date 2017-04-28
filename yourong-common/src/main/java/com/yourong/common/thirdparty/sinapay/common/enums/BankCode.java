package com.yourong.common.thirdparty.sinapay.common.enums;

import com.yourong.common.util.StringUtil;

/**
 * <p>
 * 银行代码
 * </p>
 * 
 * @author Wallis Wang
 * @version $Id: BankCode.java, v 0.2 2024年5月24日 下午2:49:46 wangqiang Exp $
 */
public enum  BankCode {

	ICBC("中国工商银行",2),
	
	ABC("中国农业银行",2),
	
	CCB("中国建设银行",2),
	
	BOC("中国银行",2),
	
	COMM("交通银行", 2),
	
	CMB("招商银行",2),
	
	CMBC("民生银行",2),
	
	SZPAB("平安银行",2),
	
	GDB("广发银行",2),
	
	CITIC("中信银行",2),
	
	CEB("光大银行",2),

	HXB("华夏银行",2),
	
	CIB("兴业银行",2),

	SPDB("浦发银行",2),
	
	PSBC("中国邮储银行",2),
	
	BCCB("北京银行",1),
	
	//HCCB("杭州银行"),
	
	//HKBCHINA("汉口银行"),
	
	BOS("上海银行", 2);
	
	//NBCB("宁波银行"),
	
	//NJCB("南京银行"),	
	//CSCB("长沙银行"),	
	//CZB("浙商银行", 1);

	private String remarks;

	private  int type;

	BankCode(String remarks,int type) {
		this.remarks = remarks;
		this.type = type;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public static BankCode getBankCode(String code) {
		BankCode[] values = BankCode.values();
		for (BankCode temp : values) {
			if (StringUtil.isEquals(code, temp.toString())) {
				return temp;
			}
		}
		return null;
	}
	
	

}