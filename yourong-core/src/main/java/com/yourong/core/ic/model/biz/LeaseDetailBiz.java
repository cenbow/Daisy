package com.yourong.core.ic.model.biz;

import java.math.BigDecimal;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.LeaseBonusDetail;

public class LeaseDetailBiz extends AbstractBaseObject {

	private static final long serialVersionUID = 1L;

	/** 主键 **/
	private Long id;

	/** 用户共分红比例 **/
	private BigDecimal userBonus;

	/** 借款人分红比例 **/
	private BigDecimal borrowerBonus;

	/** 出借人分红比例 **/
	private BigDecimal lenderBonus;

	/** 租赁公司分红比例 **/
	private BigDecimal lessorBonus;

	/** 分红明细 **/
	Page<LeaseBonusDetail> bonusDetail;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getUserBonus() {
		return userBonus;
	}

	public void setUserBonus(BigDecimal userBonus) {
		this.userBonus = userBonus;
	}

	public Page<LeaseBonusDetail> getBonusDetail() {
		return bonusDetail;
	}

	public void setBonusDetail(Page<LeaseBonusDetail> bonusDetail) {
		this.bonusDetail = bonusDetail;
	}

	public BigDecimal getBorrowerBonus() {
		return borrowerBonus;
	}

	public void setBorrowerBonus(BigDecimal borrowerBonus) {
		this.borrowerBonus = borrowerBonus;
	}

	public BigDecimal getLenderBonus() {
		return lenderBonus;
	}

	public void setLenderBonus(BigDecimal lenderBonus) {
		this.lenderBonus = lenderBonus;
	}

	public BigDecimal getLessorBonus() {
		return lessorBonus;
	}

	public void setLessorBonus(BigDecimal lessorBonus) {
		this.lessorBonus = lessorBonus;
	}
}
