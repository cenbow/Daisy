package com.yourong.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rop.thirdparty.com.google.common.collect.Lists;

import com.yourong.api.dto.PaymentPlatformDto;
import com.yourong.api.dto.ResultDTO;
import com.yourong.api.service.PaymentPlatformService;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.core.bsc.manager.PaymentPlatformManager;
import com.yourong.core.bsc.model.PaymentPlatform;

@Service
public class PaymentPlatformServiceImpl implements PaymentPlatformService {

	@Autowired
	PaymentPlatformManager paymentPlatformManager;
	
	@Override
	public ResultDTO selectAllPaymentPlatform() {
		ResultDTO result = new ResultDTO();
		try {
			List<PaymentPlatform> pList = paymentPlatformManager.selectAllPaymentPlatform();
			if(Collections3.isNotEmpty(pList)){
				List<PaymentPlatformDto> pp = BeanCopyUtil.mapList(pList, PaymentPlatformDto.class);
				result.setResult(pp);
				return result;
			}
		} catch (ManagerException e) {
			e.printStackTrace();
		}
		result.setResult(Lists.newArrayList());
		return result;
	}

}
