package com.yourong.core.fin.model;

import java.math.BigDecimal;
import java.util.Date;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;

/**
 * 人气值流水对象
 * @author Leon Ray
 * 2014年10月28日-下午12:31:35
 */
public class PopularityInOutLog extends AbstractBaseObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/****/
    private Integer id;

    /**用户id**/
    private Long memberId;


    /**余额**/
    private BigDecimal balance;

    /**1-推荐好友；2-平台活动；3-平台派送 4-兑换优惠券 **/
    private Integer type;

    /**收入**/
    private BigDecimal income;

    /**支出**/
    private BigDecimal outlay;

    /**来源id(交易id，会员id等)**/
    private Long sourceId;

    /****/
    private String remark;

    /**发生时间**/
    private Date happenTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }


    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getOutlay() {
        return outlay;
    }

    public void setOutlay(BigDecimal outlay) {
        this.outlay = outlay;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId ;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Date getHappenTime() {
        return happenTime;
    }

    public void setHappenTime(Date happenTime) {
        this.happenTime = happenTime;
    }

	public String getHappenTimeStr() {
		return DateUtils.formatDatetoString(happenTime, DateUtils.TIME_PATTERN);
	}
    
    public String getTypeName(){
    	if(TypeEnum.FIN_POPULARITY_TYPE_RECOMMEND.getType()==type.intValue()){
    		return TypeEnum.FIN_POPULARITY_TYPE_RECOMMEND.getDesc();
    	}
    	if(TypeEnum.FIN_POPULARITY_TYPE_EXCHANGE.getType()==type.intValue()){
    		return TypeEnum.FIN_POPULARITY_TYPE_EXCHANGE.getDesc();
    	}
    	if(TypeEnum.FIN_POPULARITY_TYPE_SEND.getType()==type.intValue()){
    		return TypeEnum.FIN_POPULARITY_TYPE_SEND.getDesc();
    	}
    	if(TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY.getType()==type.intValue()){
    		return TypeEnum.FIN_POPULARITY_TYPE_ACTIVITY.getDesc();
    	}
    	if(TypeEnum.FIN_POPULARITY_TYPE_REISSUE.getType()==type.intValue()){
    		return TypeEnum.FIN_POPULARITY_TYPE_REISSUE.getDesc();
    	}
    	if(TypeEnum.FIN_POPULARITY_TYPE_CHECK.getType()==type.intValue()){
    		return TypeEnum.FIN_POPULARITY_TYPE_CHECK.getDesc();
    	}
    	if(TypeEnum.FIN_POPULARITY_TYPE_WITHDRAW.getType()==type.intValue()){
    		return TypeEnum.FIN_POPULARITY_TYPE_WITHDRAW.getDesc();
    	}
    	if(TypeEnum.FIN_POPULARITY_TYPE_PROFIT_EXCHANGE.getType()==type.intValue()){
    		return TypeEnum.FIN_POPULARITY_TYPE_PROFIT_EXCHANGE.getDesc();
    	}
        if(TypeEnum.FIN_POPULARITY_TYPE_SHOP_CONSUME.getType()==type.intValue()){
            return TypeEnum.FIN_POPULARITY_TYPE_SHOP_CONSUME.getDesc();
        }
        if(TypeEnum.FIN_POPULARITY_TYPE_OVERDUE_CONSUME.getType()==type.intValue()){
            return TypeEnum.FIN_POPULARITY_TYPE_OVERDUE_CONSUME.getDesc();
        }
		return null;
    }
    
    public String getFormatOutlay() {
    	if(outlay!=null && outlay.compareTo(BigDecimal.ZERO)>0){
    		return FormulaUtil.getFormatPriceNoSep(outlay)+"点";
    	}
    	return "-";
    }
    
    public String getFormatIncome() {
    	if(income!=null && income.compareTo(BigDecimal.ZERO)>0){
    		return FormulaUtil.getFormatPriceNoSep(income)+"点";
    	}
    	return "-";
    }
}