/**
 * 
 */
package com.yourong.core.tc.model.query;

import java.util.List;

import com.yourong.common.domain.BaseQueryParam;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.core.ic.model.DebtInterest;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年2月1日下午2:48:18
 */
public class CollectingProjectQuery extends BaseQueryParam{
	

    /**募集中的项目，显示项目状态为【已满额】【已截止】【募集中】【募集赞同】四个状态，根据交易表状态（募集中：0）筛选**/
    private int[]  status = {StatusEnum.TRANSACTION_INVESTMENTING.getStatus() ,
			};
    
    
    /**p2p项目**/
    private int investType=ProjectEnum.PROJECT_TYPE_DIRECT.getType() ;


    /**签署状态（0-初始化，1-未签署，2-已签署，3-已过期）**/
	private Integer signStatus;
    
	/**
	 * @return the status
	 */
	public int[] getStatus() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(int[] status) {
		this.status = status;
	}


	/**
	 * @return the investType
	 */
	public int getInvestType() {
		return investType;
	}


	/**
	 * @param investType the investType to set
	 */
	public void setInvestType(int investType) {
		this.investType = investType;
	}


	public Integer getSignStatus() {
		return signStatus;
	}


	public void setSignStatus(Integer signStatus) {
		this.signStatus = signStatus;
	}
    
}
