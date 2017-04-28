package com.yourong.common.baidu.push.client;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.yourong.common.baidu.push.auth.PushKeyPair;
import com.yourong.common.baidu.push.exception.PushClientException;
import com.yourong.common.baidu.push.exception.PushServerException;
import com.yourong.common.baidu.push.model.PushMsgToSingleDeviceRequest;
import com.yourong.common.baidu.push.model.PushMsgToSingleDeviceResponse;

public class BaiduPushAsyncClient extends BaiduPushClient implements
		BaiduPushAsync {

	private static ExecutorService execurotService = Executors
			.newFixedThreadPool(2);

	public BaiduPushAsyncClient(PushKeyPair pair) {
		super(pair);
	}

	@SuppressWarnings("static-access")
	public BaiduPushAsyncClient(PushKeyPair pair,
			ExecutorService execurotService) {
		super(pair);
		this.execurotService = execurotService;
	}

	@Override
	public Future<PushMsgToSingleDeviceResponse> pushMsgToSingleDeviceAsync(
			final PushMsgToSingleDeviceRequest request)
			throws PushClientException, PushServerException {
		return execurotService
				.submit(new Callable<PushMsgToSingleDeviceResponse>() {
					@Override
					public PushMsgToSingleDeviceResponse call() throws Exception {
						return pushMsgToSingleDevice(request);
					}
				});
	}

}
