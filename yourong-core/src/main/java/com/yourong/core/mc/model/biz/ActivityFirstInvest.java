package com.yourong.core.mc.model.biz;

import java.io.Serializable;
import java.math.BigDecimal;

import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.StringUtil;

public class ActivityFirstInvest implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4296157459652549655L;

	/**用户ID**/
    private Long memberId;
    

    /**投资金额**/
    private BigDecimal totalInvest;
    

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}


	public BigDecimal getTotalInvest() {
		return totalInvest;
	}

	public void setTotalInvest(BigDecimal totalInvest) {
		this.totalInvest = totalInvest;
	}
	
	public String getTotalInvestFormat() {
		if(totalInvest!=null){
			return FormulaUtil.getFormatPrice(totalInvest);
		}
		return null;
	}

}