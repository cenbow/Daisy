/**
 * 
 */
package com.yourong.core.ic.model.biz;

import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年12月19日上午10:23:45
 */
public class TransactionTransferRecordBiz extends AbstractBaseObject{
	
	private static final long serialVersionUID = 1L;
	
	private List<TransferRecordListBiz> transferRecordListBiz;
	
	//0：其他  1:发起转让  2：继续转让 3：终止转让  4：全部转让 5：已还款
	private Integer flag;
	
	private String remarks;

	/**
	 * @return the transferRecordListBiz
	 */
	public List<TransferRecordListBiz> getTransferRecordListBiz() {
		return transferRecordListBiz;
	}

	/**
	 * @param transferRecordListBiz the transferRecordListBiz to set
	 */
	public void setTransferRecordListBiz(
			List<TransferRecordListBiz> transferRecordListBiz) {
		this.transferRecordListBiz = transferRecordListBiz;
	}

	/**
	 * @return the flag
	 */
	public Integer getFlag() {
		return flag;
	}

	/**
	 * @param flag the flag to set
	 */
	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
