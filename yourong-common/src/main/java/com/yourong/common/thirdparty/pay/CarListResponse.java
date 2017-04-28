package com.yourong.common.thirdparty.pay;

import java.util.List;

import com.google.common.collect.Lists;
import com.yourong.common.util.StringUtil;

public class CarListResponse extends PayResponse {

    /**
     * 
     */
    private static final long serialVersionUID = -6259517360824445375L;
    
    
    /**
     * 卡列表
     */
    private String card_list;


    public String getCard_list() {
        return card_list;
    }


    public void setCard_list(String card_list) {
        this.card_list = card_list;
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
    

}
