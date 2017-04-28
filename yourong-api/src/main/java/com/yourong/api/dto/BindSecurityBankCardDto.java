package com.yourong.api.dto;

import java.util.List;

import com.google.common.collect.Lists;


public class BindSecurityBankCardDto {
	
//	private boolean isSecurity = false;
	
	private List<MemberNonSecurityBankCardDto> bankCardList = Lists.newArrayList();
	
//	public boolean getIsSecurity() {
//		return isSecurity;
//	}
//	public void setSecurity(boolean isSecurity) {
//		this.isSecurity = isSecurity;
//	}
	public List<MemberNonSecurityBankCardDto> getBankCardList() {
		return bankCardList;
	}
	public void setBankCardList(List<MemberNonSecurityBankCardDto> bankCardList) {
		this.bankCardList = bankCardList;
	} 
	
	
}
