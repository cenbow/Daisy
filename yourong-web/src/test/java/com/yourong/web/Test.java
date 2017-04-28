package com.yourong.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.yourong.common.thirdparty.sinapay.pay.domain.result.TradeItem;
import com.yourong.common.thirdparty.sinapay.pay.domainservice.converter.ResponseJsonConverter;
import com.yourong.core.mc.model.biz.SingleInvestRuleBiz;

public class Test {

	public static void main(String[] args) {
		String tradeList = "YRPT20160720124548829001285~代收交易:YRCT20160720124528742还本付息~1.67~TRADE_FINISHED~20160720124548~20160720124554$YRPT20160720124548835001285~代收交易:YRCT20160720124528742还本付息~1.72~TRADE_FINISHED~20160720124548~20160720124554$YRPT20160720124548841001285~代收交易:YRCT20160720124528742还本付息~1.67~TRADE_FINISHED~20160720124548~20160720124554";
		List<TradeItem> payItemList = ResponseJsonConverter.convert2TradeItemList2(tradeList);
		//{investAmount: \'100\',baseSendPopularity:\'28\',isExtraCalc:\'false\',investAmount: \'1000\',baseSendPopularity:\'58\',isExtraCalc:\'true\',investAmount: \'2000\',baseSendPopularity:\'118\',isExtraCalc:\'true\',investAmount: \'3000\',baseSendPopularity:\
		List<SingleInvestRuleBiz> list = new ArrayList<>();
		SingleInvestRuleBiz SingleInvestRuleBiz = new SingleInvestRuleBiz();
		SingleInvestRuleBiz.setMinInvestAmount(BigDecimal.valueOf(100));
		SingleInvestRuleBiz.setMaxInvestAmount(BigDecimal.valueOf(1000));
		SingleInvestRuleBiz.setBaseSendPopularity(BigDecimal.valueOf(28));
		SingleInvestRuleBiz.setExtraCalc(false);
		list.add(SingleInvestRuleBiz);
		
		SingleInvestRuleBiz SingleInvestRuleBiz1 = new SingleInvestRuleBiz();
		SingleInvestRuleBiz1.setMinInvestAmount(BigDecimal.valueOf(1000));
		SingleInvestRuleBiz1.setMaxInvestAmount(BigDecimal.valueOf(2000));
		SingleInvestRuleBiz1.setBaseSendPopularity(BigDecimal.valueOf(58));
		SingleInvestRuleBiz1.setExtraCalc(true);
		list.add(SingleInvestRuleBiz1);
		
		SingleInvestRuleBiz SingleInvestRuleBiz2 = new SingleInvestRuleBiz();
		SingleInvestRuleBiz2.setMinInvestAmount(BigDecimal.valueOf(2000));
		SingleInvestRuleBiz2.setMaxInvestAmount(BigDecimal.valueOf(3000));
		SingleInvestRuleBiz2.setBaseSendPopularity(BigDecimal.valueOf(118));
		SingleInvestRuleBiz2.setExtraCalc(true);
		list.add(SingleInvestRuleBiz2);
		
		SingleInvestRuleBiz SingleInvestRuleBiz3 = new SingleInvestRuleBiz();
		SingleInvestRuleBiz3.setMinInvestAmount(BigDecimal.valueOf(3000));
		SingleInvestRuleBiz3.setMaxInvestAmount(BigDecimal.valueOf(4000));
		SingleInvestRuleBiz3.setBaseSendPopularity(BigDecimal.valueOf(178));
		SingleInvestRuleBiz3.setExtraCalc(true);
		list.add(SingleInvestRuleBiz3);
		
		SingleInvestRuleBiz SingleInvestRuleBiz4 = new SingleInvestRuleBiz();
		SingleInvestRuleBiz4.setMinInvestAmount(BigDecimal.valueOf(4000));
		SingleInvestRuleBiz4.setMaxInvestAmount(BigDecimal.valueOf(1000000));
		SingleInvestRuleBiz4.setBaseSendPopularity(BigDecimal.valueOf(298));
		SingleInvestRuleBiz4.setExtraCalc(true);
		list.add(SingleInvestRuleBiz4);
		
		
        String s = new Gson().toJson(list);
        System.out.println(s);
        list = new ArrayList<>();
        list = new Gson().fromJson(s, List.class);
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
	}

}
