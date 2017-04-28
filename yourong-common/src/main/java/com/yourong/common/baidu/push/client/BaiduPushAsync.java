package com.yourong.common.baidu.push.client;

import java.util.concurrent.Future;

import com.yourong.common.baidu.push.exception.PushClientException;
import com.yourong.common.baidu.push.exception.PushServerException;
import com.yourong.common.baidu.push.model.PushMsgToSingleDeviceRequest;
import com.yourong.common.baidu.push.model.PushMsgToSingleDeviceResponse;

public interface BaiduPushAsync {

	Future<PushMsgToSingleDeviceResponse> pushMsgToSingleDeviceAsync(
			PushMsgToSingleDeviceRequest request)
			throws PushClientException, PushServerException;

}
