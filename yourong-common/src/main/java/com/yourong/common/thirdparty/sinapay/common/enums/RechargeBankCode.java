package com.yourong.common.thirdparty.sinapay.common.enums;

import com.yourong.common.util.StringUtil;

/**
 * 
 * 充值银行支持列表
 *
 */
public enum RechargeBankCode {	
	BOC("中国银行",1),
    ICBC("工商银行",1),
	CMB("招商银行",1),
	CITIC("中信银行",1),
	GDB("广发银行",1),
	HXB("华夏银行",1),
	ABC("农业银行",1),
	PSBC("邮政储蓄银行",1),
	CMBC("民生银行",1),
    CCB("建设银行",1),
	COMM("交通银行",1),
   SPDB("浦发银行",1),
//	CZB("浙商银行",1),
//	NBCB("宁波银行",1),
    SZPAB("平安银行",1),
    CEB("光大银行",1),
//   
	BCCB("北京银行",1),
//	BJRCB("北京农商行",2),
//	 HCCB("杭州银行",2),
//	 GNXS("广州市农信社",2),
//	 GZCB("广州市商业银行",2),
//	 HKBCHINA("汉口银行",2),
	 BOS("上海银行",1),
//	 HSBANK("徽商银行",2),
//	 CBHB("渤海银行",1),
//	 CCQTGB("重庆三峡银行",2),
 //    NJCB("南京银行",1),
//	 SHRCB("上海农业商业银行",2),
//	 SNXS("深圳农业商业银行",2),
//	 SXJS("晋城市商业银行",2),
//	 CSCB("长沙银行",2),
//	 UPOP("银联在线支付",2),
//	 CZCB("浙江稠州商业银行",2),
//	 WZCB("温州市商业银行",2),
     CIB("兴业银行",1),
     SINAPAY("网银",1),
	 //TODO 测试需要
	 TESTBANK("TESTBANK",3);	
  
	private String name;
	
	private int type;

	RechargeBankCode(String name, int type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public static RechargeBankCode getRechargeBankCode(String code) {
		RechargeBankCode[] values = RechargeBankCode.values();
		for (RechargeBankCode temp : values) {
			if (StringUtil.isEquals(code, temp.toString())) {
				return temp;
			}
		}
		return null;
	}
	
}
