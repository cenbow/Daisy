package com.yourong.backend.fin.service;

import com.yourong.common.enums.TypeEnum;
import com.yourong.core.fin.model.Balance;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2014/12/26.
 */
public interface BalanceService {

    /**
     * 充值成功，从第三方同步资金	 写入到我们的资金表 记录流水号
     */
    public void  topUpFromThirdPay(long memberID,BigDecimal income,String sourceId) throws Exception;

    /**
     * 查询余额（主要用于查询平台投资总额和平台收益利息总额）
     * @param type
     * @return
     */
    public BigDecimal getBalanceByType(TypeEnum type);

    /**
     * 查询余额
     * @param
     * @param type
     * @return
     */
    public Balance queryBalance(Long sourceID, TypeEnum type);

    /**
     *  初始化资金表，资金额为0
     * @param sourceID
     * @param type
     */
    public void  initBalance(Long sourceID, TypeEnum type);
    /**
     * 
     * @Description:同步存钱罐
     * @param memberId
     * @param dataTime
     * @return
     * @author: chaisen
     * @time:2016年4月11日 上午11:49:11
     */
	public void synBalance(Date startDate,Date endDate);
}

