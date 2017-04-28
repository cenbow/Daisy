package com.yourong.common.baidu.push.client;

import com.yourong.common.baidu.push.exception.PushClientException;
import com.yourong.common.baidu.push.exception.PushServerException;
import com.yourong.common.baidu.push.model.AddDevicesToTagRequest;
import com.yourong.common.baidu.push.model.AddDevicesToTagResponse;
import com.yourong.common.baidu.push.model.CreateTagRequest;
import com.yourong.common.baidu.push.model.CreateTagResponse;
import com.yourong.common.baidu.push.model.DeleteDevicesFromTagRequest;
import com.yourong.common.baidu.push.model.DeleteDevicesFromTagResponse;
import com.yourong.common.baidu.push.model.DeleteTagRequest;
import com.yourong.common.baidu.push.model.DeleteTagResponse;
import com.yourong.common.baidu.push.model.PushBatchUniMsgRequest;
import com.yourong.common.baidu.push.model.PushBatchUniMsgResponse;
import com.yourong.common.baidu.push.model.PushMsgToAllRequest;
import com.yourong.common.baidu.push.model.PushMsgToAllResponse;
import com.yourong.common.baidu.push.model.PushMsgToSingleDeviceRequest;
import com.yourong.common.baidu.push.model.PushMsgToSingleDeviceResponse;
import com.yourong.common.baidu.push.model.PushMsgToSmartTagRequest;
import com.yourong.common.baidu.push.model.PushMsgToSmartTagResponse;
import com.yourong.common.baidu.push.model.PushMsgToTagRequest;
import com.yourong.common.baidu.push.model.PushMsgToTagResponse;
import com.yourong.common.baidu.push.model.QueryDeviceNumInTagRequest;
import com.yourong.common.baidu.push.model.QueryDeviceNumInTagResponse;
import com.yourong.common.baidu.push.model.QueryMsgStatusRequest;
import com.yourong.common.baidu.push.model.QueryMsgStatusResponse;
import com.yourong.common.baidu.push.model.QueryStatisticDeviceRequest;
import com.yourong.common.baidu.push.model.QueryStatisticDeviceResponse;
import com.yourong.common.baidu.push.model.QueryStatisticMsgRequest;
import com.yourong.common.baidu.push.model.QueryStatisticMsgResponse;
import com.yourong.common.baidu.push.model.QueryStatisticTopicRequest;
import com.yourong.common.baidu.push.model.QueryStatisticTopicResponse;
import com.yourong.common.baidu.push.model.QueryTagsRequest;
import com.yourong.common.baidu.push.model.QueryTagsResponse;
import com.yourong.common.baidu.push.model.QueryTimerListRequest;
import com.yourong.common.baidu.push.model.QueryTimerListResponse;
import com.yourong.common.baidu.push.model.QueryTimerRecordsRequest;
import com.yourong.common.baidu.push.model.QueryTimerRecordsResponse;
import com.yourong.common.baidu.push.model.QueryTopicListRequest;
import com.yourong.common.baidu.push.model.QueryTopicListResponse;
import com.yourong.common.baidu.push.model.QueryTopicRecordsRequest;
import com.yourong.common.baidu.push.model.QueryTopicRecordsResponse;

public interface BaiduPush {

	public PushMsgToSingleDeviceResponse pushMsgToSingleDevice(
			PushMsgToSingleDeviceRequest request) throws PushClientException,
		    PushServerException;
	
	public PushMsgToAllResponse pushMsgToAll(
			PushMsgToAllRequest request) throws PushClientException,
		    PushServerException;
	
	public PushMsgToTagResponse pushMsgToTag(
			PushMsgToTagRequest request) throws PushClientException, 
	PushServerException;
	
	public PushMsgToSmartTagResponse pushMsgToSmartTag(
			PushMsgToSmartTagRequest request) throws PushClientException, 
	PushServerException;
	
	public PushBatchUniMsgResponse pushBatchUniMsg(
			PushBatchUniMsgRequest request) throws PushClientException, 
	PushServerException;
	
	public QueryMsgStatusResponse queryMsgStatus(
			QueryMsgStatusRequest request) throws PushClientException, 
	PushServerException;
	
	public QueryTimerRecordsResponse queryTimerRecords(
			QueryTimerRecordsRequest request) throws PushClientException, 
	PushServerException;
	
	public QueryTopicRecordsResponse queryTopicRecords(
			QueryTopicRecordsRequest request) throws PushClientException, 
	PushServerException;
	
	public QueryTimerListResponse queryTimerList(
			QueryTimerListRequest request) throws PushClientException, 
	PushServerException;
	
	public QueryTopicListResponse queryTopicList(
			QueryTopicListRequest request) throws PushClientException, 
	PushServerException;
	
	public QueryTagsResponse queryTags(
			QueryTagsRequest request) throws PushClientException, 
	PushServerException;
	
	public CreateTagResponse createTag(
			CreateTagRequest request) throws PushClientException, 
	PushServerException;
	
	public DeleteTagResponse deleteTag(
			DeleteTagRequest request) throws PushClientException, 
	PushServerException;
	
	public AddDevicesToTagResponse addDevicesToTag(
			AddDevicesToTagRequest request) throws PushClientException, 
	PushServerException;
	
	public DeleteDevicesFromTagResponse deleteDevicesFromTag(
			DeleteDevicesFromTagRequest request) throws PushClientException, 
	PushServerException;
	
	public QueryDeviceNumInTagResponse queryDeviceNumInTag(
			QueryDeviceNumInTagRequest request) throws PushClientException, 
	PushServerException;
	
	public QueryStatisticMsgResponse queryStatisticMsg(
			QueryStatisticMsgRequest request) throws PushClientException, 
	PushServerException;
	
	public QueryStatisticTopicResponse queryStatisticTopic(
			QueryStatisticTopicRequest request) throws PushClientException, 
	PushServerException;
	
	public QueryStatisticDeviceResponse queryStatisticDevice(
			QueryStatisticDeviceRequest request) throws 
	PushClientException, PushServerException;
	
}
